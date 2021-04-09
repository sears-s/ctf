package org.bouncycastle.openssl.jcajce;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.openssl.EncryptionException;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.util.Integers;

class PEMUtilities {
    private static final Map KEYSIZES = new HashMap();
    private static final Set PKCS5_SCHEME_1 = new HashSet();
    private static final Set PKCS5_SCHEME_2 = new HashSet();
    private static final Map PRFS = new HashMap();
    private static final Map PRFS_SALT = new HashMap();

    static {
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndRC2_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC);
        PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.id_PBES2);
        PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.des_EDE3_CBC);
        PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes128_CBC);
        PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes192_CBC);
        PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes256_CBC);
        KEYSIZES.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), Integers.valueOf(192));
        KEYSIZES.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), Integers.valueOf(128));
        KEYSIZES.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), Integers.valueOf(192));
        KEYSIZES.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), Integers.valueOf(256));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4.getId(), Integers.valueOf(128));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, Integers.valueOf(40));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, Integers.valueOf(128));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, Integers.valueOf(192));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, Integers.valueOf(128));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, Integers.valueOf(40));
        PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA1, "PBKDF2withHMACSHA1");
        PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA256, "PBKDF2withHMACSHA256");
        PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA512, "PBKDF2withHMACSHA512");
        PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA224, "PBKDF2withHMACSHA224");
        PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA384, "PBKDF2withHMACSHA384");
        PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, "PBKDF2withHMACSHA3-224");
        PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, "PBKDF2withHMACSHA3-256");
        PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, "PBKDF2withHMACSHA3-384");
        PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, "PBKDF2withHMACSHA3-512");
        PRFS.put(CryptoProObjectIdentifiers.gostR3411Hmac, "PBKDF2withHMACGOST3411");
        PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA1, Integers.valueOf(20));
        PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA256, Integers.valueOf(32));
        PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA512, Integers.valueOf(64));
        PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA224, Integers.valueOf(28));
        PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA384, Integers.valueOf(48));
        PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, Integers.valueOf(28));
        PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, Integers.valueOf(32));
        PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, Integers.valueOf(48));
        PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, Integers.valueOf(64));
        PRFS_SALT.put(CryptoProObjectIdentifiers.gostR3411Hmac, Integers.valueOf(32));
    }

    PEMUtilities() {
    }

    static byte[] crypt(boolean z, JcaJceHelper jcaJceHelper, byte[] bArr, char[] cArr, String str, byte[] bArr2) throws PEMException {
        String str2;
        String str3;
        String str4;
        String str5;
        SecretKey secretKey;
        byte[] bArr3;
        RC2ParameterSpec rC2ParameterSpec;
        JcaJceHelper jcaJceHelper2 = jcaJceHelper;
        char[] cArr2 = cArr;
        String str6 = str;
        byte[] bArr4 = bArr2;
        IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr4);
        String str7 = "NoPadding";
        if (str6.endsWith("-CFB")) {
            str3 = "CFB";
            str2 = str7;
        } else {
            str3 = "CBC";
            str2 = "PKCS5Padding";
        }
        String str8 = "DES-EDE3";
        String str9 = "DES-EDE";
        if (str6.endsWith("-ECB") || str9.equals(str6) || str8.equals(str6)) {
            ivParameterSpec = null;
            str3 = "ECB";
        }
        AlgorithmParameterSpec algorithmParameterSpec = ivParameterSpec;
        if (str6.endsWith("-OFB")) {
            str5 = "OFB";
            str4 = str7;
        } else {
            str4 = str2;
            str5 = str3;
        }
        boolean startsWith = str6.startsWith(str9);
        String str10 = "AES";
        int i = 1;
        if (startsWith) {
            String str11 = "DESede";
            secretKey = getKey(jcaJceHelper, cArr, str11, 24, bArr2, !str6.startsWith(str8));
            str10 = str11;
        } else if (str6.startsWith("DES-")) {
            str10 = "DES";
            secretKey = getKey(jcaJceHelper, cArr2, str10, 8, bArr4);
        } else if (str6.startsWith("BF-")) {
            str10 = "Blowfish";
            secretKey = getKey(jcaJceHelper, cArr2, str10, 16, bArr4);
        } else {
            int i2 = 128;
            if (str6.startsWith("RC2-")) {
                str10 = "RC2";
                if (str6.startsWith("RC2-40-")) {
                    i2 = 40;
                } else if (str6.startsWith("RC2-64-")) {
                    i2 = 64;
                }
                secretKey = getKey(jcaJceHelper, cArr2, str10, i2 / 8, bArr4);
                if (algorithmParameterSpec == null) {
                    rC2ParameterSpec = new RC2ParameterSpec(i2);
                } else {
                    rC2ParameterSpec = new RC2ParameterSpec(i2, bArr4);
                }
                algorithmParameterSpec = rC2ParameterSpec;
            } else if (str6.startsWith("AES-")) {
                if (bArr4.length > 8) {
                    bArr3 = new byte[8];
                    System.arraycopy(bArr4, 0, bArr3, 0, 8);
                } else {
                    bArr3 = bArr4;
                }
                if (!str6.startsWith("AES-128-")) {
                    if (str6.startsWith("AES-192-")) {
                        i2 = 192;
                    } else if (str6.startsWith("AES-256-")) {
                        i2 = 256;
                    } else {
                        throw new EncryptionException("unknown AES encryption with private key");
                    }
                }
                secretKey = getKey(jcaJceHelper, cArr2, str10, i2 / 8, bArr3);
            } else {
                throw new EncryptionException("unknown encryption with private key");
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str10);
        String str12 = "/";
        sb.append(str12);
        sb.append(str5);
        sb.append(str12);
        sb.append(str4);
        try {
            Cipher createCipher = jcaJceHelper.createCipher(sb.toString());
            if (!z) {
                i = 2;
            }
            if (algorithmParameterSpec == null) {
                createCipher.init(i, secretKey);
            } else {
                createCipher.init(i, secretKey, algorithmParameterSpec);
            }
            byte[] bArr5 = bArr;
            return createCipher.doFinal(bArr);
        } catch (Exception e) {
            throw new EncryptionException("exception using cipher - please check password and data.", e);
        }
    }

    public static SecretKey generateSecretKeyForPKCS5Scheme2(JcaJceHelper jcaJceHelper, String str, char[] cArr, byte[] bArr, int i) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        return new SecretKeySpec(jcaJceHelper.createSecretKeyFactory("PBKDF2with8BIT").generateSecret(new PBEKeySpec(cArr, bArr, i, getKeySize(str))).getEncoded(), str);
    }

    public static SecretKey generateSecretKeyForPKCS5Scheme2(JcaJceHelper jcaJceHelper, String str, char[] cArr, byte[] bArr, int i, AlgorithmIdentifier algorithmIdentifier) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        String str2 = (String) PRFS.get(algorithmIdentifier.getAlgorithm());
        if (str2 != null) {
            return new SecretKeySpec(jcaJceHelper.createSecretKeyFactory(str2).generateSecret(new PBEKeySpec(cArr, bArr, i, getKeySize(str))).getEncoded(), str);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unknown PRF in PKCS#2: ");
        sb.append(algorithmIdentifier.getAlgorithm());
        throw new NoSuchAlgorithmException(sb.toString());
    }

    private static SecretKey getKey(JcaJceHelper jcaJceHelper, char[] cArr, String str, int i, byte[] bArr) throws PEMException {
        return getKey(jcaJceHelper, cArr, str, i, bArr, false);
    }

    private static SecretKey getKey(JcaJceHelper jcaJceHelper, char[] cArr, String str, int i, byte[] bArr, boolean z) throws PEMException {
        try {
            byte[] encoded = jcaJceHelper.createSecretKeyFactory("PBKDF-OpenSSL").generateSecret(new PBEKeySpec(cArr, bArr, 1, i * 8)).getEncoded();
            if (z && encoded.length >= 24) {
                System.arraycopy(encoded, 0, encoded, 16, 8);
            }
            return new SecretKeySpec(encoded, str);
        } catch (GeneralSecurityException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to create OpenSSL PBDKF: ");
            sb.append(e.getMessage());
            throw new PEMException(sb.toString(), e);
        }
    }

    static int getKeySize(String str) {
        if (KEYSIZES.containsKey(str)) {
            return ((Integer) KEYSIZES.get(str)).intValue();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("no key size for algorithm: ");
        sb.append(str);
        throw new IllegalStateException(sb.toString());
    }

    static int getSaltSize(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (PRFS_SALT.containsKey(aSN1ObjectIdentifier)) {
            return ((Integer) PRFS_SALT.get(aSN1ObjectIdentifier)).intValue();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("no salt size for algorithm: ");
        sb.append(aSN1ObjectIdentifier);
        throw new IllegalStateException(sb.toString());
    }

    static boolean isHmacSHA1(AlgorithmIdentifier algorithmIdentifier) {
        return algorithmIdentifier == null || algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_hmacWithSHA1);
    }

    public static boolean isPKCS12(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return aSN1ObjectIdentifier.getId().startsWith(PKCSObjectIdentifiers.pkcs_12PbeIds.getId());
    }

    static boolean isPKCS5Scheme1(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return PKCS5_SCHEME_1.contains(aSN1ObjectIdentifier);
    }

    static boolean isPKCS5Scheme2(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return PKCS5_SCHEME_2.contains(aSN1ObjectIdentifier);
    }
}
