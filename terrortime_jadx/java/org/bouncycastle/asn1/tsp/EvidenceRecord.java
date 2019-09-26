package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.BigIntegers;

public class EvidenceRecord extends ASN1Object {
    private static final ASN1ObjectIdentifier OID = new ASN1ObjectIdentifier("1.3.6.1.5.5.11.0.2.1");
    private ArchiveTimeStampSequence archiveTimeStampSequence;
    private CryptoInfos cryptoInfos;
    private ASN1Sequence digestAlgorithms;
    private EncryptionInfo encryptionInfo;
    private ASN1Integer version = new ASN1Integer(1);

    private EvidenceRecord(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() >= 3 || aSN1Sequence.size() <= 5) {
            ASN1Integer instance = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
            if (instance.getValue().equals(BigIntegers.ONE)) {
                this.version = instance;
                this.digestAlgorithms = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(1));
                int i = 2;
                while (i != aSN1Sequence.size() - 1) {
                    ASN1Encodable objectAt = aSN1Sequence.getObjectAt(i);
                    if (objectAt instanceof ASN1TaggedObject) {
                        ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) objectAt;
                        int tagNo = aSN1TaggedObject.getTagNo();
                        if (tagNo == 0) {
                            this.cryptoInfos = CryptoInfos.getInstance(aSN1TaggedObject, false);
                        } else if (tagNo == 1) {
                            this.encryptionInfo = EncryptionInfo.getInstance(aSN1TaggedObject, false);
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append("unknown tag in getInstance: ");
                            sb.append(aSN1TaggedObject.getTagNo());
                            throw new IllegalArgumentException(sb.toString());
                        }
                        i++;
                    } else {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("unknown object in getInstance: ");
                        sb2.append(objectAt.getClass().getName());
                        throw new IllegalArgumentException(sb2.toString());
                    }
                }
                this.archiveTimeStampSequence = ArchiveTimeStampSequence.getInstance(aSN1Sequence.getObjectAt(aSN1Sequence.size() - 1));
                return;
            }
            throw new IllegalArgumentException("incompatible version");
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("wrong sequence size in constructor: ");
        sb3.append(aSN1Sequence.size());
        throw new IllegalArgumentException(sb3.toString());
    }

    private EvidenceRecord(EvidenceRecord evidenceRecord, ArchiveTimeStampSequence archiveTimeStampSequence2, ArchiveTimeStamp archiveTimeStamp) {
        ASN1Sequence aSN1Sequence;
        this.version = evidenceRecord.version;
        if (archiveTimeStamp != null) {
            AlgorithmIdentifier digestAlgorithmIdentifier = archiveTimeStamp.getDigestAlgorithmIdentifier();
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            Enumeration objects = evidenceRecord.digestAlgorithms.getObjects();
            boolean z = false;
            while (true) {
                if (!objects.hasMoreElements()) {
                    break;
                }
                AlgorithmIdentifier instance = AlgorithmIdentifier.getInstance(objects.nextElement());
                aSN1EncodableVector.add(instance);
                if (instance.equals(digestAlgorithmIdentifier)) {
                    z = true;
                    break;
                }
            }
            if (!z) {
                aSN1EncodableVector.add(digestAlgorithmIdentifier);
                aSN1Sequence = new DERSequence(aSN1EncodableVector);
                this.digestAlgorithms = aSN1Sequence;
                this.cryptoInfos = evidenceRecord.cryptoInfos;
                this.encryptionInfo = evidenceRecord.encryptionInfo;
                this.archiveTimeStampSequence = archiveTimeStampSequence2;
            }
        }
        aSN1Sequence = evidenceRecord.digestAlgorithms;
        this.digestAlgorithms = aSN1Sequence;
        this.cryptoInfos = evidenceRecord.cryptoInfos;
        this.encryptionInfo = evidenceRecord.encryptionInfo;
        this.archiveTimeStampSequence = archiveTimeStampSequence2;
    }

    public EvidenceRecord(AlgorithmIdentifier[] algorithmIdentifierArr, CryptoInfos cryptoInfos2, EncryptionInfo encryptionInfo2, ArchiveTimeStampSequence archiveTimeStampSequence2) {
        this.digestAlgorithms = new DERSequence((ASN1Encodable[]) algorithmIdentifierArr);
        this.cryptoInfos = cryptoInfos2;
        this.encryptionInfo = encryptionInfo2;
        this.archiveTimeStampSequence = archiveTimeStampSequence2;
    }

    public static EvidenceRecord getInstance(Object obj) {
        if (obj instanceof EvidenceRecord) {
            return (EvidenceRecord) obj;
        }
        if (obj != null) {
            return new EvidenceRecord(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public EvidenceRecord addArchiveTimeStamp(ArchiveTimeStamp archiveTimeStamp, boolean z) {
        if (z) {
            return new EvidenceRecord(this, this.archiveTimeStampSequence.append(new ArchiveTimeStampChain(archiveTimeStamp)), archiveTimeStamp);
        }
        ArchiveTimeStampChain[] archiveTimeStampChains = this.archiveTimeStampSequence.getArchiveTimeStampChains();
        archiveTimeStampChains[archiveTimeStampChains.length - 1] = archiveTimeStampChains[archiveTimeStampChains.length - 1].append(archiveTimeStamp);
        return new EvidenceRecord(this, new ArchiveTimeStampSequence(archiveTimeStampChains), null);
    }

    public ArchiveTimeStampSequence getArchiveTimeStampSequence() {
        return this.archiveTimeStampSequence;
    }

    public AlgorithmIdentifier[] getDigestAlgorithms() {
        AlgorithmIdentifier[] algorithmIdentifierArr = new AlgorithmIdentifier[this.digestAlgorithms.size()];
        for (int i = 0; i != algorithmIdentifierArr.length; i++) {
            algorithmIdentifierArr[i] = AlgorithmIdentifier.getInstance(this.digestAlgorithms.getObjectAt(i));
        }
        return algorithmIdentifierArr;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(this.version);
        aSN1EncodableVector.add(this.digestAlgorithms);
        CryptoInfos cryptoInfos2 = this.cryptoInfos;
        if (cryptoInfos2 != null) {
            aSN1EncodableVector.add(cryptoInfos2);
        }
        EncryptionInfo encryptionInfo2 = this.encryptionInfo;
        if (encryptionInfo2 != null) {
            aSN1EncodableVector.add(encryptionInfo2);
        }
        aSN1EncodableVector.add(this.archiveTimeStampSequence);
        return new DERSequence(aSN1EncodableVector);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EvidenceRecord: Oid(");
        sb.append(OID);
        sb.append(")");
        return sb.toString();
    }
}
