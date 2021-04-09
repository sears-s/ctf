package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.OtherRevocationInfoFormat;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

class CMSSignedHelper {
    static final CMSSignedHelper INSTANCE = new CMSSignedHelper();
    private static final Map encryptionAlgs = new HashMap();

    static {
        String str = "DSA";
        addEntries(NISTObjectIdentifiers.dsa_with_sha224, str);
        addEntries(NISTObjectIdentifiers.dsa_with_sha256, str);
        addEntries(NISTObjectIdentifiers.dsa_with_sha384, str);
        addEntries(NISTObjectIdentifiers.dsa_with_sha512, str);
        addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_224, str);
        addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_256, str);
        addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_384, str);
        addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_512, str);
        addEntries(OIWObjectIdentifiers.dsaWithSHA1, str);
        String str2 = "RSA";
        addEntries(OIWObjectIdentifiers.md4WithRSA, str2);
        addEntries(OIWObjectIdentifiers.md4WithRSAEncryption, str2);
        addEntries(OIWObjectIdentifiers.md5WithRSA, str2);
        addEntries(OIWObjectIdentifiers.sha1WithRSA, str2);
        addEntries(PKCSObjectIdentifiers.md2WithRSAEncryption, str2);
        addEntries(PKCSObjectIdentifiers.md4WithRSAEncryption, str2);
        addEntries(PKCSObjectIdentifiers.md5WithRSAEncryption, str2);
        addEntries(PKCSObjectIdentifiers.sha1WithRSAEncryption, str2);
        addEntries(PKCSObjectIdentifiers.sha224WithRSAEncryption, str2);
        addEntries(PKCSObjectIdentifiers.sha256WithRSAEncryption, str2);
        addEntries(PKCSObjectIdentifiers.sha384WithRSAEncryption, str2);
        addEntries(PKCSObjectIdentifiers.sha512WithRSAEncryption, str2);
        addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, str2);
        addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, str2);
        addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, str2);
        addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, str2);
        String str3 = "ECDSA";
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA1, str3);
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA224, str3);
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA256, str3);
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA384, str3);
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA512, str3);
        addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_224, str3);
        addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_256, str3);
        addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_384, str3);
        addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_512, str3);
        addEntries(X9ObjectIdentifiers.id_dsa_with_sha1, str);
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, str3);
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, str3);
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, str3);
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, str3);
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, str3);
        addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1, str2);
        addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256, str2);
        String str4 = "RSAandMGF1";
        addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1, str4);
        addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256, str4);
        addEntries(X9ObjectIdentifiers.id_dsa, str);
        addEntries(PKCSObjectIdentifiers.rsaEncryption, str2);
        addEntries(TeleTrusTObjectIdentifiers.teleTrusTRSAsignatureAlgorithm, str2);
        addEntries(X509ObjectIdentifiers.id_ea_rsa, str2);
        addEntries(PKCSObjectIdentifiers.id_RSASSA_PSS, str4);
        String str5 = "GOST3410";
        addEntries(CryptoProObjectIdentifiers.gostR3410_94, str5);
        String str6 = "ECGOST3410";
        addEntries(CryptoProObjectIdentifiers.gostR3410_2001, str6);
        addEntries(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.6.2"), str6);
        addEntries(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.1.5"), str5);
        String str7 = "ECGOST3410-2012-256";
        addEntries(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, str7);
        String str8 = "ECGOST3410-2012-512";
        addEntries(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, str8);
        addEntries(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, str6);
        addEntries(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, str5);
        addEntries(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, str7);
        addEntries(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, str8);
    }

    CMSSignedHelper() {
    }

    private static void addEntries(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        encryptionAlgs.put(aSN1ObjectIdentifier.getId(), str);
    }

    /* access modifiers changed from: 0000 */
    public AlgorithmIdentifier fixAlgID(AlgorithmIdentifier algorithmIdentifier) {
        return algorithmIdentifier.getParameters() == null ? new AlgorithmIdentifier(algorithmIdentifier.getAlgorithm(), DERNull.INSTANCE) : algorithmIdentifier;
    }

    /* access modifiers changed from: 0000 */
    public Store getAttributeCertificates(ASN1Set aSN1Set) {
        if (aSN1Set == null) {
            return new CollectionStore(new ArrayList());
        }
        ArrayList arrayList = new ArrayList(aSN1Set.size());
        Enumeration objects = aSN1Set.getObjects();
        while (objects.hasMoreElements()) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable) objects.nextElement()).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1TaggedObject) {
                arrayList.add(new X509AttributeCertificateHolder(AttributeCertificate.getInstance(((ASN1TaggedObject) aSN1Primitive).getObject())));
            }
        }
        return new CollectionStore(arrayList);
    }

    /* access modifiers changed from: 0000 */
    public Store getCRLs(ASN1Set aSN1Set) {
        if (aSN1Set == null) {
            return new CollectionStore(new ArrayList());
        }
        ArrayList arrayList = new ArrayList(aSN1Set.size());
        Enumeration objects = aSN1Set.getObjects();
        while (objects.hasMoreElements()) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable) objects.nextElement()).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1Sequence) {
                arrayList.add(new X509CRLHolder(CertificateList.getInstance(aSN1Primitive)));
            }
        }
        return new CollectionStore(arrayList);
    }

    /* access modifiers changed from: 0000 */
    public Store getCertificates(ASN1Set aSN1Set) {
        if (aSN1Set == null) {
            return new CollectionStore(new ArrayList());
        }
        ArrayList arrayList = new ArrayList(aSN1Set.size());
        Enumeration objects = aSN1Set.getObjects();
        while (objects.hasMoreElements()) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable) objects.nextElement()).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1Sequence) {
                arrayList.add(new X509CertificateHolder(Certificate.getInstance(aSN1Primitive)));
            }
        }
        return new CollectionStore(arrayList);
    }

    /* access modifiers changed from: 0000 */
    public String getEncryptionAlgName(String str) {
        String str2 = (String) encryptionAlgs.get(str);
        return str2 != null ? str2 : str;
    }

    /* access modifiers changed from: 0000 */
    public Store getOtherRevocationInfo(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Set aSN1Set) {
        if (aSN1Set == null) {
            return new CollectionStore(new ArrayList());
        }
        ArrayList arrayList = new ArrayList(aSN1Set.size());
        Enumeration objects = aSN1Set.getObjects();
        while (objects.hasMoreElements()) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable) objects.nextElement()).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1TaggedObject) {
                ASN1TaggedObject instance = ASN1TaggedObject.getInstance(aSN1Primitive);
                if (instance.getTagNo() == 1) {
                    OtherRevocationInfoFormat instance2 = OtherRevocationInfoFormat.getInstance(instance, false);
                    if (aSN1ObjectIdentifier.equals(instance2.getInfoFormat())) {
                        arrayList.add(instance2.getInfo());
                    }
                }
            }
        }
        return new CollectionStore(arrayList);
    }

    /* access modifiers changed from: 0000 */
    public void setSigningEncryptionAlgorithmMapping(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        addEntries(aSN1ObjectIdentifier, str);
    }
}
