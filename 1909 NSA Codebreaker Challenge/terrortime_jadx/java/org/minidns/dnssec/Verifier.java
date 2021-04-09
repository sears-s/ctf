package org.minidns.dnssec;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.minidns.dnslabel.DnsLabel;
import org.minidns.dnsmessage.Question;
import org.minidns.dnsname.DnsName;
import org.minidns.dnssec.DnssecUnverifiedReason.AlgorithmExceptionThrownReason;
import org.minidns.dnssec.DnssecUnverifiedReason.AlgorithmNotSupportedReason;
import org.minidns.dnssec.DnssecUnverifiedReason.NSECDoesNotMatchReason;
import org.minidns.dnssec.algorithms.AlgorithmMap;
import org.minidns.record.DNSKEY;
import org.minidns.record.Data;
import org.minidns.record.DelegatingDnssecRR;
import org.minidns.record.NSEC;
import org.minidns.record.NSEC3;
import org.minidns.record.RRSIG;
import org.minidns.record.Record;
import org.minidns.util.Base32;

class Verifier {
    private static final AlgorithmMap algorithmMap = AlgorithmMap.INSTANCE;

    Verifier() {
    }

    public static DnssecUnverifiedReason verify(Record<DNSKEY> dnskeyRecord, DelegatingDnssecRR ds) throws DnssecValidationFailedException {
        DNSKEY dnskey = (DNSKEY) dnskeyRecord.payloadData;
        DigestCalculator digestCalculator = algorithmMap.getDsDigestCalculator(ds.digestType);
        if (digestCalculator == null) {
            return new AlgorithmNotSupportedReason(ds.digestTypeByte, ds.getType(), dnskeyRecord);
        }
        byte[] dnskeyData = dnskey.toByteArray();
        byte[] dnskeyOwner = dnskeyRecord.name.getBytes();
        byte[] combined = new byte[(dnskeyOwner.length + dnskeyData.length)];
        System.arraycopy(dnskeyOwner, 0, combined, 0, dnskeyOwner.length);
        System.arraycopy(dnskeyData, 0, combined, dnskeyOwner.length, dnskeyData.length);
        try {
            if (ds.digestEquals(digestCalculator.digest(combined))) {
                return null;
            }
            throw new DnssecValidationFailedException(dnskeyRecord, "SEP is not properly signed by parent DS!");
        } catch (Exception e) {
            return new AlgorithmExceptionThrownReason(ds.digestType, "DS", dnskeyRecord, e);
        }
    }

    public static DnssecUnverifiedReason verify(List<Record<? extends Data>> records, RRSIG rrsig, DNSKEY key) throws IOException {
        SignatureVerifier signatureVerifier = algorithmMap.getSignatureVerifier(rrsig.algorithm);
        if (signatureVerifier == null) {
            return new AlgorithmNotSupportedReason(rrsig.algorithmByte, rrsig.getType(), (Record) records.get(0));
        }
        if (signatureVerifier.verify(combine(rrsig, records), rrsig, key)) {
            return null;
        }
        throw new DnssecValidationFailedException(records, "Signature is invalid.");
    }

    public static DnssecUnverifiedReason verifyNsec(Record<NSEC> nsecRecord, Question q) {
        NSEC nsec = (NSEC) nsecRecord.payloadData;
        if ((!nsecRecord.name.equals(q.name) || nsec.types.contains(q.type)) && !nsecMatches(q.name, nsecRecord.name, nsec.next)) {
            return new NSECDoesNotMatchReason(q, nsecRecord);
        }
        return null;
    }

    public static DnssecUnverifiedReason verifyNsec3(DnsName zone, Record<NSEC3> nsec3record, Question q) {
        NSEC3 nsec3 = (NSEC3) nsec3record.payloadData;
        DigestCalculator digestCalculator = algorithmMap.getNsecDigestCalculator(nsec3.hashAlgorithm);
        if (digestCalculator == null) {
            return new AlgorithmNotSupportedReason(nsec3.hashAlgorithmByte, nsec3.getType(), nsec3record);
        }
        String s = Base32.encodeToString(nsec3hash(digestCalculator, nsec3, q.name, nsec3.iterations));
        StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append(".");
        sb.append(zone);
        if (nsec3record.name.equals(DnsName.from(sb.toString()))) {
            if (nsec3.types.contains(q.type)) {
                return new NSECDoesNotMatchReason(q, nsec3record);
            }
            return null;
        } else if (nsecMatches(s, nsec3record.name.getHostpart(), Base32.encodeToString(nsec3.getNextHashed()))) {
            return null;
        } else {
            return new NSECDoesNotMatchReason(q, nsec3record);
        }
    }

    static byte[] combine(RRSIG rrsig, List<Record<? extends Data>> records) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            rrsig.writePartialSignature(dos);
            DnsName sigName = ((Record) records.get(0)).name;
            if (!sigName.isRootLabel()) {
                if (sigName.getLabelCount() < rrsig.labels) {
                    throw new DnssecValidationFailedException("Invalid RRsig record");
                } else if (sigName.getLabelCount() > rrsig.labels) {
                    sigName = DnsName.from(DnsLabel.WILDCARD_LABEL, sigName.stripToLabels(rrsig.labels));
                }
            }
            List<byte[]> recordBytes = new ArrayList<>(records.size());
            for (Record record : records) {
                Record record2 = new Record(sigName, record.type, record.clazzValue, rrsig.originalTtl, record.payloadData);
                recordBytes.add(record2.toByteArray());
            }
            final int offset = sigName.size() + 10;
            Collections.sort(recordBytes, new Comparator<byte[]>() {
                public int compare(byte[] b1, byte[] b2) {
                    int i = offset;
                    while (i < b1.length && i < b2.length) {
                        if (b1[i] != b2[i]) {
                            return (b1[i] & 255) - (b2[i] & 255);
                        }
                        i++;
                    }
                    return b1.length - b2.length;
                }
            });
            for (byte[] recordByte : recordBytes) {
                dos.write(recordByte);
            }
            dos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean nsecMatches(String test, String lowerBound, String upperBound) {
        return nsecMatches(DnsName.from(test), DnsName.from(lowerBound), DnsName.from(upperBound));
    }

    static boolean nsecMatches(DnsName test, DnsName lowerBound, DnsName upperBound) {
        int lowerParts = lowerBound.getLabelCount();
        int upperParts = upperBound.getLabelCount();
        int testParts = test.getLabelCount();
        if (testParts > lowerParts && !test.isChildOf(lowerBound) && test.stripToLabels(lowerParts).compareTo(lowerBound) < 0) {
            return false;
        }
        if (testParts <= lowerParts && test.compareTo(lowerBound.stripToLabels(testParts)) < 0) {
            return false;
        }
        if (testParts > upperParts && !test.isChildOf(upperBound) && test.stripToLabels(upperParts).compareTo(upperBound) > 0) {
            return false;
        }
        if (testParts > upperParts || test.compareTo(upperBound.stripToLabels(testParts)) < 0) {
            return true;
        }
        return false;
    }

    static byte[] nsec3hash(DigestCalculator digestCalculator, NSEC3 nsec3, DnsName ownerName, int iterations) {
        return nsec3hash(digestCalculator, nsec3.getSalt(), ownerName.getBytes(), iterations);
    }

    static byte[] nsec3hash(DigestCalculator digestCalculator, byte[] salt, byte[] data, int iterations) {
        while (true) {
            int iterations2 = iterations - 1;
            if (iterations < 0) {
                return data;
            }
            byte[] combined = new byte[(data.length + salt.length)];
            System.arraycopy(data, 0, combined, 0, data.length);
            System.arraycopy(salt, 0, combined, data.length, salt.length);
            data = digestCalculator.digest(combined);
            iterations = iterations2;
        }
    }
}
