package org.bouncycastle.cms;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.jivesoftware.smack.util.StringUtils;

public class DefaultCMSSignatureAlgorithmNameGenerator implements CMSSignatureAlgorithmNameGenerator {
    private final Map digestAlgs = new HashMap();
    private final Map encryptionAlgs = new HashMap();

    public DefaultCMSSignatureAlgorithmNameGenerator() {
        String str = "SHA224";
        String str2 = "DSA";
        addEntries(NISTObjectIdentifiers.dsa_with_sha224, str, str2);
        String str3 = "SHA256";
        addEntries(NISTObjectIdentifiers.dsa_with_sha256, str3, str2);
        String str4 = "SHA384";
        addEntries(NISTObjectIdentifiers.dsa_with_sha384, str4, str2);
        String str5 = "SHA512";
        addEntries(NISTObjectIdentifiers.dsa_with_sha512, str5, str2);
        String str6 = "SHA3-224";
        addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_224, str6, str2);
        String str7 = "SHA3-256";
        addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_256, str7, str2);
        String str8 = "SHA3-384";
        addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_384, str8, str2);
        String str9 = "SHA3-512";
        addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_512, str9, str2);
        String str10 = "RSA";
        addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, str6, str10);
        addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, str7, str10);
        addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, str8, str10);
        addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, str9, str10);
        String str11 = "ECDSA";
        addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_224, str6, str11);
        addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_256, str7, str11);
        addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_384, str8, str11);
        addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_512, str9, str11);
        String str12 = "SHA1";
        addEntries(OIWObjectIdentifiers.dsaWithSHA1, str12, str2);
        String str13 = "MD4";
        addEntries(OIWObjectIdentifiers.md4WithRSA, str13, str10);
        addEntries(OIWObjectIdentifiers.md4WithRSAEncryption, str13, str10);
        ASN1ObjectIdentifier aSN1ObjectIdentifier = OIWObjectIdentifiers.md5WithRSA;
        String str14 = StringUtils.MD5;
        addEntries(aSN1ObjectIdentifier, str14, str10);
        addEntries(OIWObjectIdentifiers.sha1WithRSA, str12, str10);
        String str15 = str9;
        addEntries(PKCSObjectIdentifiers.md2WithRSAEncryption, "MD2", str10);
        addEntries(PKCSObjectIdentifiers.md4WithRSAEncryption, str13, str10);
        addEntries(PKCSObjectIdentifiers.md5WithRSAEncryption, str14, str10);
        addEntries(PKCSObjectIdentifiers.sha1WithRSAEncryption, str12, str10);
        addEntries(PKCSObjectIdentifiers.sha224WithRSAEncryption, str, str10);
        addEntries(PKCSObjectIdentifiers.sha256WithRSAEncryption, str3, str10);
        addEntries(PKCSObjectIdentifiers.sha384WithRSAEncryption, str4, str10);
        addEntries(PKCSObjectIdentifiers.sha512WithRSAEncryption, str5, str10);
        addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128, "RIPEMD128", str10);
        addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160, "RIPEMD160", str10);
        addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256, "RIPEMD256", str10);
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA1, str12, str11);
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA224, str, str11);
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA256, str3, str11);
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA384, str4, str11);
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA512, str5, str11);
        addEntries(X9ObjectIdentifiers.id_dsa_with_sha1, str12, str2);
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, str12, str11);
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, str, str11);
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, str3, str11);
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, str4, str11);
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, str5, str11);
        addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1, str12, str10);
        addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256, str3, str10);
        addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1, str12, "RSAandMGF1");
        addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256, str3, "RSAandMGF1");
        String str16 = "PLAIN-ECDSA";
        addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA1, str12, str16);
        addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA224, str, str16);
        addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA256, str3, str16);
        addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA384, str4, str16);
        addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA512, str5, str16);
        addEntries(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, "RIPEMD160", str16);
        addEntries(GMObjectIdentifiers.sm2sign_with_sha256, str3, "SM2");
        addEntries(GMObjectIdentifiers.sm2sign_with_sm3, "SM3", "SM2");
        this.encryptionAlgs.put(X9ObjectIdentifiers.id_dsa, str2);
        this.encryptionAlgs.put(PKCSObjectIdentifiers.rsaEncryption, str10);
        this.encryptionAlgs.put(TeleTrusTObjectIdentifiers.teleTrusTRSAsignatureAlgorithm, str10);
        this.encryptionAlgs.put(X509ObjectIdentifiers.id_ea_rsa, str10);
        this.encryptionAlgs.put(PKCSObjectIdentifiers.id_RSASSA_PSS, "RSAandMGF1");
        this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_94, "GOST3410");
        this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
        this.encryptionAlgs.put(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.6.2"), "ECGOST3410");
        this.encryptionAlgs.put(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.1.5"), "GOST3410");
        this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012-256");
        this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012-512");
        this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "ECGOST3410");
        this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3410");
        this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "ECGOST3410-2012-256");
        this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "ECGOST3410-2012-512");
        this.digestAlgs.put(PKCSObjectIdentifiers.md2, "MD2");
        this.digestAlgs.put(PKCSObjectIdentifiers.md4, str13);
        this.digestAlgs.put(PKCSObjectIdentifiers.md5, str14);
        this.digestAlgs.put(OIWObjectIdentifiers.idSHA1, str12);
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha224, str);
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha256, str3);
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha384, str4);
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha512, str5);
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha3_224, str6);
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha3_256, str7);
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha3_384, str8);
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha3_512, str15);
        this.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
        this.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
        this.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
        this.digestAlgs.put(CryptoProObjectIdentifiers.gostR3411, "GOST3411");
        this.digestAlgs.put(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.2.1"), "GOST3411");
        this.digestAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, "GOST3411-2012-256");
        this.digestAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, "GOST3411-2012-512");
        this.digestAlgs.put(GMObjectIdentifiers.sm3, "SM3");
    }

    private void addEntries(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str, String str2) {
        this.digestAlgs.put(aSN1ObjectIdentifier, str);
        this.encryptionAlgs.put(aSN1ObjectIdentifier, str2);
    }

    private String getDigestAlgName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String str = (String) this.digestAlgs.get(aSN1ObjectIdentifier);
        return str != null ? str : aSN1ObjectIdentifier.getId();
    }

    private String getEncryptionAlgName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String str = (String) this.encryptionAlgs.get(aSN1ObjectIdentifier);
        return str != null ? str : aSN1ObjectIdentifier.getId();
    }

    public String getSignatureName(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) {
        String digestAlgName = getDigestAlgName(algorithmIdentifier2.getAlgorithm());
        String str = "with";
        if (!digestAlgName.equals(algorithmIdentifier2.getAlgorithm().getId())) {
            StringBuilder sb = new StringBuilder();
            sb.append(digestAlgName);
            sb.append(str);
            sb.append(getEncryptionAlgName(algorithmIdentifier2.getAlgorithm()));
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(getDigestAlgName(algorithmIdentifier.getAlgorithm()));
        sb2.append(str);
        sb2.append(getEncryptionAlgName(algorithmIdentifier2.getAlgorithm()));
        return sb2.toString();
    }

    /* access modifiers changed from: protected */
    public void setSigningDigestAlgorithmMapping(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        this.digestAlgs.put(aSN1ObjectIdentifier, str);
    }

    /* access modifiers changed from: protected */
    public void setSigningEncryptionAlgorithmMapping(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        this.encryptionAlgs.put(aSN1ObjectIdentifier, str);
    }
}
