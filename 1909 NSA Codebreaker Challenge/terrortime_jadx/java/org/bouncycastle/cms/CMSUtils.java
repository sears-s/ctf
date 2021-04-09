package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BEROctetStringGenerator;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.OtherRevocationInfoFormat;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.util.io.TeeOutputStream;

class CMSUtils {
    private static final Set<String> des = new HashSet();
    private static final Set ecAlgs = new HashSet();
    private static final Set gostAlgs = new HashSet();
    private static final Set mqvAlgs = new HashSet();

    static {
        des.add("DES");
        des.add("DESEDE");
        des.add(OIWObjectIdentifiers.desCBC.getId());
        des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
        des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
        des.add(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId());
        mqvAlgs.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme);
        ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme);
        ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme);
        gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
    }

    CMSUtils() {
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Collection, code=java.util.Collection<org.bouncycastle.operator.DigestCalculator>, for r2v0, types: [java.util.Collection<org.bouncycastle.operator.DigestCalculator>, java.util.Collection] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.io.InputStream attachDigestsToInputStream(java.util.Collection<org.bouncycastle.operator.DigestCalculator> r2, java.io.InputStream r3) {
        /*
            java.util.Iterator r2 = r2.iterator()
        L_0x0004:
            boolean r0 = r2.hasNext()
            if (r0 == 0) goto L_0x001b
            java.lang.Object r0 = r2.next()
            org.bouncycastle.operator.DigestCalculator r0 = (org.bouncycastle.operator.DigestCalculator) r0
            org.bouncycastle.util.io.TeeInputStream r1 = new org.bouncycastle.util.io.TeeInputStream
            java.io.OutputStream r0 = r0.getOutputStream()
            r1.<init>(r3, r0)
            r3 = r1
            goto L_0x0004
        L_0x001b:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.cms.CMSUtils.attachDigestsToInputStream(java.util.Collection, java.io.InputStream):java.io.InputStream");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Collection, code=java.util.Collection<org.bouncycastle.cms.SignerInfoGenerator>, for r1v0, types: [java.util.Collection<org.bouncycastle.cms.SignerInfoGenerator>, java.util.Collection] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.io.OutputStream attachSignersToOutputStream(java.util.Collection<org.bouncycastle.cms.SignerInfoGenerator> r1, java.io.OutputStream r2) {
        /*
            java.util.Iterator r1 = r1.iterator()
        L_0x0004:
            boolean r0 = r1.hasNext()
            if (r0 == 0) goto L_0x0019
            java.lang.Object r0 = r1.next()
            org.bouncycastle.cms.SignerInfoGenerator r0 = (org.bouncycastle.cms.SignerInfoGenerator) r0
            java.io.OutputStream r0 = r0.getCalculatingOutputStream()
            java.io.OutputStream r2 = getSafeTeeOutputStream(r2, r0)
            goto L_0x0004
        L_0x0019:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.cms.CMSUtils.attachSignersToOutputStream(java.util.Collection, java.io.OutputStream):java.io.OutputStream");
    }

    static OutputStream createBEROctetOutputStream(OutputStream outputStream, int i, boolean z, int i2) throws IOException {
        BEROctetStringGenerator bEROctetStringGenerator = new BEROctetStringGenerator(outputStream, i, z);
        return i2 != 0 ? bEROctetStringGenerator.getOctetOutputStream(new byte[i2]) : bEROctetStringGenerator.getOctetOutputStream();
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.List, code=java.util.List<org.bouncycastle.asn1.ASN1Encodable>, for r2v0, types: [java.util.List<org.bouncycastle.asn1.ASN1Encodable>, java.util.List] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static org.bouncycastle.asn1.ASN1Set createBerSetFromList(java.util.List<org.bouncycastle.asn1.ASN1Encodable> r2) {
        /*
            org.bouncycastle.asn1.ASN1EncodableVector r0 = new org.bouncycastle.asn1.ASN1EncodableVector
            r0.<init>()
            java.util.Iterator r2 = r2.iterator()
        L_0x0009:
            boolean r1 = r2.hasNext()
            if (r1 == 0) goto L_0x0019
            java.lang.Object r1 = r2.next()
            org.bouncycastle.asn1.ASN1Encodable r1 = (org.bouncycastle.asn1.ASN1Encodable) r1
            r0.add(r1)
            goto L_0x0009
        L_0x0019:
            org.bouncycastle.asn1.BERSet r2 = new org.bouncycastle.asn1.BERSet
            r2.<init>(r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.cms.CMSUtils.createBerSetFromList(java.util.List):org.bouncycastle.asn1.ASN1Set");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.List, code=java.util.List<org.bouncycastle.asn1.ASN1Encodable>, for r2v0, types: [java.util.List<org.bouncycastle.asn1.ASN1Encodable>, java.util.List] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static org.bouncycastle.asn1.ASN1Set createDerSetFromList(java.util.List<org.bouncycastle.asn1.ASN1Encodable> r2) {
        /*
            org.bouncycastle.asn1.ASN1EncodableVector r0 = new org.bouncycastle.asn1.ASN1EncodableVector
            r0.<init>()
            java.util.Iterator r2 = r2.iterator()
        L_0x0009:
            boolean r1 = r2.hasNext()
            if (r1 == 0) goto L_0x0019
            java.lang.Object r1 = r2.next()
            org.bouncycastle.asn1.ASN1Encodable r1 = (org.bouncycastle.asn1.ASN1Encodable) r1
            r0.add(r1)
            goto L_0x0009
        L_0x0019:
            org.bouncycastle.asn1.DERSet r2 = new org.bouncycastle.asn1.DERSet
            r2.<init>(r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.cms.CMSUtils.createDerSetFromList(java.util.List):org.bouncycastle.asn1.ASN1Set");
    }

    static List getAttributeCertificatesFromStore(Store store) throws CMSException {
        ArrayList arrayList = new ArrayList();
        try {
            for (X509AttributeCertificateHolder aSN1Structure : store.getMatches(null)) {
                arrayList.add(new DERTaggedObject(false, 2, aSN1Structure.toASN1Structure()));
            }
            return arrayList;
        } catch (ClassCastException e) {
            throw new CMSException("error processing certs", e);
        }
    }

    static List getCRLsFromStore(Store store) throws CMSException {
        ArrayList arrayList = new ArrayList();
        try {
            for (Object next : store.getMatches(null)) {
                if (next instanceof X509CRLHolder) {
                    next = ((X509CRLHolder) next).toASN1Structure();
                } else if (next instanceof OtherRevocationInfoFormat) {
                    OtherRevocationInfoFormat instance = OtherRevocationInfoFormat.getInstance(next);
                    validateInfoFormat(instance);
                    arrayList.add(new DERTaggedObject(false, 1, instance));
                } else if (!(next instanceof ASN1TaggedObject)) {
                }
                arrayList.add(next);
            }
            return arrayList;
        } catch (ClassCastException e) {
            throw new CMSException("error processing certs", e);
        }
    }

    static List getCertificatesFromStore(Store store) throws CMSException {
        ArrayList arrayList = new ArrayList();
        try {
            for (X509CertificateHolder aSN1Structure : store.getMatches(null)) {
                arrayList.add(aSN1Structure.toASN1Structure());
            }
            return arrayList;
        } catch (ClassCastException e) {
            throw new CMSException("error processing certs", e);
        }
    }

    static Collection getOthersFromStore(ASN1ObjectIdentifier aSN1ObjectIdentifier, Store store) {
        ArrayList arrayList = new ArrayList();
        for (ASN1Encodable otherRevocationInfoFormat : store.getMatches(null)) {
            OtherRevocationInfoFormat otherRevocationInfoFormat2 = new OtherRevocationInfoFormat(aSN1ObjectIdentifier, otherRevocationInfoFormat);
            validateInfoFormat(otherRevocationInfoFormat2);
            arrayList.add(new DERTaggedObject(false, 1, otherRevocationInfoFormat2));
        }
        return arrayList;
    }

    static OutputStream getSafeOutputStream(OutputStream outputStream) {
        return outputStream == null ? new NullOutputStream() : outputStream;
    }

    static OutputStream getSafeTeeOutputStream(OutputStream outputStream, OutputStream outputStream2) {
        return outputStream == null ? getSafeOutputStream(outputStream2) : outputStream2 == null ? getSafeOutputStream(outputStream) : new TeeOutputStream(outputStream, outputStream2);
    }

    static boolean isDES(String str) {
        return des.contains(Strings.toUpperCase(str));
    }

    static boolean isEC(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return ecAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isEquivalent(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) {
        boolean z = false;
        if (!(algorithmIdentifier == null || algorithmIdentifier2 == null)) {
            if (!algorithmIdentifier.getAlgorithm().equals(algorithmIdentifier2.getAlgorithm())) {
                return false;
            }
            ASN1Encodable parameters = algorithmIdentifier.getParameters();
            ASN1Encodable parameters2 = algorithmIdentifier2.getParameters();
            if (parameters != null) {
                if (parameters.equals(parameters2) || (parameters.equals(DERNull.INSTANCE) && parameters2 == null)) {
                    z = true;
                }
                return z;
            } else if (parameters2 == null || parameters2.equals(DERNull.INSTANCE)) {
                z = true;
            }
        }
        return z;
    }

    static boolean isGOST(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return gostAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isMQV(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return mqvAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isRFC2631(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_alg_ESDH) || aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_alg_SSDH);
    }

    static ContentInfo readContentInfo(InputStream inputStream) throws CMSException {
        return readContentInfo(new ASN1InputStream(inputStream));
    }

    private static ContentInfo readContentInfo(ASN1InputStream aSN1InputStream) throws CMSException {
        String str = "Malformed content.";
        try {
            ContentInfo instance = ContentInfo.getInstance(aSN1InputStream.readObject());
            if (instance != null) {
                return instance;
            }
            throw new CMSException("No content found.");
        } catch (IOException e) {
            throw new CMSException("IOException reading content.", e);
        } catch (ClassCastException e2) {
            throw new CMSException(str, e2);
        } catch (IllegalArgumentException e3) {
            throw new CMSException(str, e3);
        }
    }

    static ContentInfo readContentInfo(byte[] bArr) throws CMSException {
        return readContentInfo(new ASN1InputStream(bArr));
    }

    public static byte[] streamToByteArray(InputStream inputStream) throws IOException {
        return Streams.readAll(inputStream);
    }

    public static byte[] streamToByteArray(InputStream inputStream, int i) throws IOException {
        return Streams.readAllLimited(inputStream, i);
    }

    private static void validateInfoFormat(OtherRevocationInfoFormat otherRevocationInfoFormat) {
        if (CMSObjectIdentifiers.id_ri_ocsp_response.equals(otherRevocationInfoFormat.getInfoFormat()) && OCSPResponse.getInstance(otherRevocationInfoFormat.getInfo()).getResponseStatus().getValue().intValue() != 0) {
            throw new IllegalArgumentException("cannot add unsuccessful OCSP response to CMS SignedData");
        }
    }
}
