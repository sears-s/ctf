package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface EACObjectIdentifiers {
    public static final ASN1ObjectIdentifier bsi_de = new ASN1ObjectIdentifier("0.4.0.127.0.7");
    public static final ASN1ObjectIdentifier id_CA = bsi_de.branch("2.2.3");
    public static final ASN1ObjectIdentifier id_CA_DH;
    public static final ASN1ObjectIdentifier id_CA_DH_3DES_CBC_CBC;
    public static final ASN1ObjectIdentifier id_CA_ECDH;
    public static final ASN1ObjectIdentifier id_CA_ECDH_3DES_CBC_CBC;
    public static final ASN1ObjectIdentifier id_EAC_ePassport = bsi_de.branch("3.1.2.1");
    public static final ASN1ObjectIdentifier id_PK = bsi_de.branch("2.2.1");
    public static final ASN1ObjectIdentifier id_PK_DH;
    public static final ASN1ObjectIdentifier id_PK_ECDH;
    public static final ASN1ObjectIdentifier id_TA = bsi_de.branch("2.2.2");
    public static final ASN1ObjectIdentifier id_TA_ECDSA;
    public static final ASN1ObjectIdentifier id_TA_ECDSA_SHA_1;
    public static final ASN1ObjectIdentifier id_TA_ECDSA_SHA_224;
    public static final ASN1ObjectIdentifier id_TA_ECDSA_SHA_256;
    public static final ASN1ObjectIdentifier id_TA_ECDSA_SHA_384;
    public static final ASN1ObjectIdentifier id_TA_ECDSA_SHA_512;
    public static final ASN1ObjectIdentifier id_TA_RSA;
    public static final ASN1ObjectIdentifier id_TA_RSA_PSS_SHA_1;
    public static final ASN1ObjectIdentifier id_TA_RSA_PSS_SHA_256;
    public static final ASN1ObjectIdentifier id_TA_RSA_PSS_SHA_512 = id_TA_RSA.branch("6");
    public static final ASN1ObjectIdentifier id_TA_RSA_v1_5_SHA_1;
    public static final ASN1ObjectIdentifier id_TA_RSA_v1_5_SHA_256;
    public static final ASN1ObjectIdentifier id_TA_RSA_v1_5_SHA_512;

    static {
        String str = "1";
        id_PK_DH = id_PK.branch(str);
        String str2 = "2";
        id_PK_ECDH = id_PK.branch(str2);
        id_CA_DH = id_CA.branch(str);
        id_CA_DH_3DES_CBC_CBC = id_CA_DH.branch(str);
        id_CA_ECDH = id_CA.branch(str2);
        id_CA_ECDH_3DES_CBC_CBC = id_CA_ECDH.branch(str);
        id_TA_RSA = id_TA.branch(str);
        id_TA_RSA_v1_5_SHA_1 = id_TA_RSA.branch(str);
        id_TA_RSA_v1_5_SHA_256 = id_TA_RSA.branch(str2);
        String str3 = "3";
        id_TA_RSA_PSS_SHA_1 = id_TA_RSA.branch(str3);
        String str4 = "4";
        id_TA_RSA_PSS_SHA_256 = id_TA_RSA.branch(str4);
        String str5 = "5";
        id_TA_RSA_v1_5_SHA_512 = id_TA_RSA.branch(str5);
        id_TA_ECDSA = id_TA.branch(str2);
        id_TA_ECDSA_SHA_1 = id_TA_ECDSA.branch(str);
        id_TA_ECDSA_SHA_224 = id_TA_ECDSA.branch(str2);
        id_TA_ECDSA_SHA_256 = id_TA_ECDSA.branch(str3);
        id_TA_ECDSA_SHA_384 = id_TA_ECDSA.branch(str4);
        id_TA_ECDSA_SHA_512 = id_TA_ECDSA.branch(str5);
    }
}
