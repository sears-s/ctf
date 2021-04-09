package org.bouncycastle.asn1.bsi;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface BSIObjectIdentifiers {
    public static final ASN1ObjectIdentifier algorithm;
    public static final ASN1ObjectIdentifier bsi_de = new ASN1ObjectIdentifier("0.4.0.127.0.7");
    public static final ASN1ObjectIdentifier ecdsa_plain_RIPEMD160;
    public static final ASN1ObjectIdentifier ecdsa_plain_SHA1;
    public static final ASN1ObjectIdentifier ecdsa_plain_SHA224;
    public static final ASN1ObjectIdentifier ecdsa_plain_SHA256;
    public static final ASN1ObjectIdentifier ecdsa_plain_SHA384;
    public static final ASN1ObjectIdentifier ecdsa_plain_SHA512;
    public static final ASN1ObjectIdentifier ecdsa_plain_signatures = id_ecc.branch("4.1");
    public static final ASN1ObjectIdentifier ecka_eg = id_ecc.branch("5.1");
    public static final ASN1ObjectIdentifier ecka_eg_SessionKDF;
    public static final ASN1ObjectIdentifier ecka_eg_SessionKDF_3DES;
    public static final ASN1ObjectIdentifier ecka_eg_SessionKDF_AES128;
    public static final ASN1ObjectIdentifier ecka_eg_SessionKDF_AES192;
    public static final ASN1ObjectIdentifier ecka_eg_SessionKDF_AES256;
    public static final ASN1ObjectIdentifier ecka_eg_X963kdf;
    public static final ASN1ObjectIdentifier ecka_eg_X963kdf_RIPEMD160;
    public static final ASN1ObjectIdentifier ecka_eg_X963kdf_SHA1;
    public static final ASN1ObjectIdentifier ecka_eg_X963kdf_SHA224;
    public static final ASN1ObjectIdentifier ecka_eg_X963kdf_SHA256;
    public static final ASN1ObjectIdentifier ecka_eg_X963kdf_SHA384;
    public static final ASN1ObjectIdentifier ecka_eg_X963kdf_SHA512;
    public static final ASN1ObjectIdentifier id_ecc = bsi_de.branch("1.1");

    static {
        String str = "1";
        ecdsa_plain_SHA1 = ecdsa_plain_signatures.branch(str);
        String str2 = "2";
        ecdsa_plain_SHA224 = ecdsa_plain_signatures.branch(str2);
        String str3 = "3";
        ecdsa_plain_SHA256 = ecdsa_plain_signatures.branch(str3);
        String str4 = "4";
        ecdsa_plain_SHA384 = ecdsa_plain_signatures.branch(str4);
        String str5 = "5";
        ecdsa_plain_SHA512 = ecdsa_plain_signatures.branch(str5);
        String str6 = "6";
        ecdsa_plain_RIPEMD160 = ecdsa_plain_signatures.branch(str6);
        algorithm = bsi_de.branch(str);
        ecka_eg_X963kdf = ecka_eg.branch(str);
        ecka_eg_X963kdf_SHA1 = ecka_eg_X963kdf.branch(str);
        ecka_eg_X963kdf_SHA224 = ecka_eg_X963kdf.branch(str2);
        ecka_eg_X963kdf_SHA256 = ecka_eg_X963kdf.branch(str3);
        ecka_eg_X963kdf_SHA384 = ecka_eg_X963kdf.branch(str4);
        ecka_eg_X963kdf_SHA512 = ecka_eg_X963kdf.branch(str5);
        ecka_eg_X963kdf_RIPEMD160 = ecka_eg_X963kdf.branch(str6);
        ecka_eg_SessionKDF = ecka_eg.branch(str2);
        ecka_eg_SessionKDF_3DES = ecka_eg_SessionKDF.branch(str);
        ecka_eg_SessionKDF_AES128 = ecka_eg_SessionKDF.branch(str2);
        ecka_eg_SessionKDF_AES192 = ecka_eg_SessionKDF.branch(str3);
        ecka_eg_SessionKDF_AES256 = ecka_eg_SessionKDF.branch(str4);
    }
}
