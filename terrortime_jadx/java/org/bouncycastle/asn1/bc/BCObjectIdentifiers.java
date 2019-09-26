package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface BCObjectIdentifiers {
    public static final ASN1ObjectIdentifier bc = new ASN1ObjectIdentifier("1.3.6.1.4.1.22554");
    public static final ASN1ObjectIdentifier bc_exch;
    public static final ASN1ObjectIdentifier bc_ext;
    public static final ASN1ObjectIdentifier bc_pbe;
    public static final ASN1ObjectIdentifier bc_pbe_sha1;
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs12;
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs12_aes128_cbc;
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs12_aes192_cbc;
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs12_aes256_cbc;
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs5;
    public static final ASN1ObjectIdentifier bc_pbe_sha224 = bc_pbe.branch("2.4");
    public static final ASN1ObjectIdentifier bc_pbe_sha256 = bc_pbe.branch("2.1");
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs12;
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs12_aes128_cbc;
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs12_aes192_cbc;
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs12_aes256_cbc;
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs5;
    public static final ASN1ObjectIdentifier bc_pbe_sha384 = bc_pbe.branch("2.2");
    public static final ASN1ObjectIdentifier bc_pbe_sha512 = bc_pbe.branch("2.3");
    public static final ASN1ObjectIdentifier bc_sig;
    public static final ASN1ObjectIdentifier linkedCertificate;
    public static final ASN1ObjectIdentifier newHope;
    public static final ASN1ObjectIdentifier qTESLA;
    public static final ASN1ObjectIdentifier qTESLA_I;
    public static final ASN1ObjectIdentifier qTESLA_III_size;
    public static final ASN1ObjectIdentifier qTESLA_III_speed;
    public static final ASN1ObjectIdentifier qTESLA_p_I;
    public static final ASN1ObjectIdentifier qTESLA_p_III;
    public static final ASN1ObjectIdentifier sphincs256;
    public static final ASN1ObjectIdentifier sphincs256_with_BLAKE512;
    public static final ASN1ObjectIdentifier sphincs256_with_SHA3_512;
    public static final ASN1ObjectIdentifier sphincs256_with_SHA512;
    public static final ASN1ObjectIdentifier xmss;
    public static final ASN1ObjectIdentifier xmss_SHA256;
    public static final ASN1ObjectIdentifier xmss_SHA256ph;
    public static final ASN1ObjectIdentifier xmss_SHA512;
    public static final ASN1ObjectIdentifier xmss_SHA512ph;
    public static final ASN1ObjectIdentifier xmss_SHAKE128;
    public static final ASN1ObjectIdentifier xmss_SHAKE128ph;
    public static final ASN1ObjectIdentifier xmss_SHAKE256;
    public static final ASN1ObjectIdentifier xmss_SHAKE256ph;
    public static final ASN1ObjectIdentifier xmss_mt;
    public static final ASN1ObjectIdentifier xmss_mt_SHA256;
    public static final ASN1ObjectIdentifier xmss_mt_SHA256ph;
    public static final ASN1ObjectIdentifier xmss_mt_SHA512;
    public static final ASN1ObjectIdentifier xmss_mt_SHA512ph;
    public static final ASN1ObjectIdentifier xmss_mt_SHAKE128;
    public static final ASN1ObjectIdentifier xmss_mt_SHAKE128ph;
    public static final ASN1ObjectIdentifier xmss_mt_SHAKE256;
    public static final ASN1ObjectIdentifier xmss_mt_SHAKE256ph;
    public static final ASN1ObjectIdentifier xmss_mt_with_SHA256 = xmss_mt_SHA256ph;
    public static final ASN1ObjectIdentifier xmss_mt_with_SHA512 = xmss_mt_SHA512ph;
    public static final ASN1ObjectIdentifier xmss_mt_with_SHAKE128 = xmss_mt_SHAKE128;
    public static final ASN1ObjectIdentifier xmss_mt_with_SHAKE256 = xmss_mt_SHAKE256;
    public static final ASN1ObjectIdentifier xmss_with_SHA256 = xmss_SHA256ph;
    public static final ASN1ObjectIdentifier xmss_with_SHA512 = xmss_SHA512ph;
    public static final ASN1ObjectIdentifier xmss_with_SHAKE128 = xmss_SHAKE128ph;
    public static final ASN1ObjectIdentifier xmss_with_SHAKE256 = xmss_SHAKE256ph;

    static {
        String str = "1";
        bc_pbe = bc.branch(str);
        bc_pbe_sha1 = bc_pbe.branch(str);
        bc_pbe_sha1_pkcs5 = bc_pbe_sha1.branch(str);
        String str2 = "2";
        bc_pbe_sha1_pkcs12 = bc_pbe_sha1.branch(str2);
        bc_pbe_sha256_pkcs5 = bc_pbe_sha256.branch(str);
        bc_pbe_sha256_pkcs12 = bc_pbe_sha256.branch(str2);
        String str3 = "1.2";
        bc_pbe_sha1_pkcs12_aes128_cbc = bc_pbe_sha1_pkcs12.branch(str3);
        String str4 = "1.22";
        bc_pbe_sha1_pkcs12_aes192_cbc = bc_pbe_sha1_pkcs12.branch(str4);
        String str5 = "1.42";
        bc_pbe_sha1_pkcs12_aes256_cbc = bc_pbe_sha1_pkcs12.branch(str5);
        bc_pbe_sha256_pkcs12_aes128_cbc = bc_pbe_sha256_pkcs12.branch(str3);
        bc_pbe_sha256_pkcs12_aes192_cbc = bc_pbe_sha256_pkcs12.branch(str4);
        bc_pbe_sha256_pkcs12_aes256_cbc = bc_pbe_sha256_pkcs12.branch(str5);
        bc_sig = bc.branch(str2);
        sphincs256 = bc_sig.branch(str);
        sphincs256_with_BLAKE512 = sphincs256.branch(str);
        sphincs256_with_SHA512 = sphincs256.branch(str2);
        String str6 = "3";
        sphincs256_with_SHA3_512 = sphincs256.branch(str6);
        xmss = bc_sig.branch(str2);
        xmss_SHA256ph = xmss.branch(str);
        xmss_SHA512ph = xmss.branch(str2);
        xmss_SHAKE128ph = xmss.branch(str6);
        String str7 = "4";
        xmss_SHAKE256ph = xmss.branch(str7);
        String str8 = "5";
        xmss_SHA256 = xmss.branch(str8);
        String str9 = "6";
        xmss_SHA512 = xmss.branch(str9);
        String str10 = "7";
        xmss_SHAKE128 = xmss.branch(str10);
        String str11 = "8";
        xmss_SHAKE256 = xmss.branch(str11);
        xmss_mt = bc_sig.branch(str6);
        xmss_mt_SHA256ph = xmss_mt.branch(str);
        xmss_mt_SHA512ph = xmss_mt.branch(str2);
        xmss_mt_SHAKE128ph = xmss_mt.branch(str6);
        xmss_mt_SHAKE256ph = xmss_mt.branch(str7);
        xmss_mt_SHA256 = xmss_mt.branch(str8);
        xmss_mt_SHA512 = xmss_mt.branch(str9);
        xmss_mt_SHAKE128 = xmss_mt.branch(str10);
        xmss_mt_SHAKE256 = xmss_mt.branch(str11);
        qTESLA = bc_sig.branch(str7);
        qTESLA_I = qTESLA.branch(str);
        qTESLA_III_size = qTESLA.branch(str2);
        qTESLA_III_speed = qTESLA.branch(str6);
        qTESLA_p_I = qTESLA.branch(str7);
        qTESLA_p_III = qTESLA.branch(str8);
        bc_exch = bc.branch(str6);
        newHope = bc_exch.branch(str);
        bc_ext = bc.branch(str7);
        linkedCertificate = bc_ext.branch(str);
    }
}
