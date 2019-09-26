package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.crypto.KeyAgreementSpi;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.gnu.GNUObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.agreement.kdf.DHKDFParameters;
import org.bouncycastle.crypto.agreement.kdf.DHKEKGenerator;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;
import org.jivesoftware.smack.util.MAC;

public abstract class BaseAgreementSpi extends KeyAgreementSpi {
    private static final Map<String, ASN1ObjectIdentifier> defaultOids = new HashMap();
    private static final Hashtable des = new Hashtable();
    private static final Map<String, Integer> keySizes = new HashMap();
    private static final Map<String, String> nameTable = new HashMap();
    private static final Hashtable oids = new Hashtable();
    protected final String kaAlgorithm;
    protected final DerivationFunction kdf;
    protected byte[] ukmParameters;

    static {
        Integer valueOf = Integers.valueOf(64);
        Integer valueOf2 = Integers.valueOf(128);
        Integer valueOf3 = Integers.valueOf(192);
        Integer valueOf4 = Integers.valueOf(256);
        String str = "DES";
        keySizes.put(str, valueOf);
        String str2 = "DESEDE";
        keySizes.put(str2, valueOf3);
        keySizes.put("BLOWFISH", valueOf2);
        String str3 = "AES";
        keySizes.put(str3, valueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_ECB.getId(), valueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_ECB.getId(), valueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_ECB.getId(), valueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), valueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), valueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), valueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_CFB.getId(), valueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_CFB.getId(), valueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_CFB.getId(), valueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_OFB.getId(), valueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_OFB.getId(), valueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_OFB.getId(), valueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_wrap.getId(), valueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_wrap.getId(), valueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_wrap.getId(), valueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), valueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_CCM.getId(), valueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_CCM.getId(), valueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_GCM.getId(), valueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_GCM.getId(), valueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_GCM.getId(), valueOf4);
        keySizes.put(NTTObjectIdentifiers.id_camellia128_wrap.getId(), valueOf2);
        keySizes.put(NTTObjectIdentifiers.id_camellia192_wrap.getId(), valueOf3);
        keySizes.put(NTTObjectIdentifiers.id_camellia256_wrap.getId(), valueOf4);
        keySizes.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap.getId(), valueOf2);
        keySizes.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), valueOf3);
        keySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), valueOf3);
        keySizes.put(OIWObjectIdentifiers.desCBC.getId(), valueOf);
        keySizes.put(CryptoProObjectIdentifiers.gostR28147_gcfb.getId(), valueOf4);
        keySizes.put(CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap.getId(), valueOf4);
        keySizes.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap.getId(), valueOf4);
        keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA1.getId(), Integers.valueOf(CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256));
        keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA256.getId(), valueOf4);
        keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA384.getId(), Integers.valueOf(384));
        keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA512.getId(), Integers.valueOf(512));
        defaultOids.put(str2, PKCSObjectIdentifiers.des_EDE3_CBC);
        defaultOids.put(str3, NISTObjectIdentifiers.id_aes256_CBC);
        defaultOids.put("CAMELLIA", NTTObjectIdentifiers.id_camellia256_cbc);
        String str4 = "SEED";
        defaultOids.put(str4, KISAObjectIdentifiers.id_seedCBC);
        defaultOids.put(str, OIWObjectIdentifiers.desCBC);
        nameTable.put(MiscObjectIdentifiers.cast5CBC.getId(), "CAST5");
        nameTable.put(MiscObjectIdentifiers.as_sys_sec_alg_ideaCBC.getId(), "IDEA");
        String str5 = "Blowfish";
        nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_ECB.getId(), str5);
        nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC.getId(), str5);
        nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CFB.getId(), str5);
        nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_OFB.getId(), str5);
        nameTable.put(OIWObjectIdentifiers.desECB.getId(), str);
        nameTable.put(OIWObjectIdentifiers.desCBC.getId(), str);
        nameTable.put(OIWObjectIdentifiers.desCFB.getId(), str);
        nameTable.put(OIWObjectIdentifiers.desOFB.getId(), str);
        String str6 = "DESede";
        nameTable.put(OIWObjectIdentifiers.desEDE.getId(), str6);
        nameTable.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), str6);
        nameTable.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), str6);
        nameTable.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap.getId(), "RC2");
        nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA1.getId(), MAC.HMACSHA1);
        nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA224.getId(), "HmacSHA224");
        nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA256.getId(), "HmacSHA256");
        nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA384.getId(), "HmacSHA384");
        nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA512.getId(), "HmacSHA512");
        String str7 = "Camellia";
        nameTable.put(NTTObjectIdentifiers.id_camellia128_cbc.getId(), str7);
        nameTable.put(NTTObjectIdentifiers.id_camellia192_cbc.getId(), str7);
        nameTable.put(NTTObjectIdentifiers.id_camellia256_cbc.getId(), str7);
        nameTable.put(NTTObjectIdentifiers.id_camellia128_wrap.getId(), str7);
        nameTable.put(NTTObjectIdentifiers.id_camellia192_wrap.getId(), str7);
        nameTable.put(NTTObjectIdentifiers.id_camellia256_wrap.getId(), str7);
        nameTable.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap.getId(), str4);
        nameTable.put(KISAObjectIdentifiers.id_seedCBC.getId(), str4);
        nameTable.put(KISAObjectIdentifiers.id_seedMAC.getId(), str4);
        nameTable.put(CryptoProObjectIdentifiers.gostR28147_gcfb.getId(), "GOST28147");
        nameTable.put(NISTObjectIdentifiers.id_aes128_wrap.getId(), str3);
        nameTable.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), str3);
        nameTable.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), str3);
        oids.put(str2, PKCSObjectIdentifiers.des_EDE3_CBC);
        oids.put(str3, NISTObjectIdentifiers.id_aes256_CBC);
        oids.put(str, OIWObjectIdentifiers.desCBC);
        des.put(str, str);
        des.put(str2, str);
        des.put(OIWObjectIdentifiers.desCBC.getId(), str);
        des.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), str);
        des.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), str);
    }

    public BaseAgreementSpi(String str, DerivationFunction derivationFunction) {
        this.kaAlgorithm = str;
        this.kdf = derivationFunction;
    }

    protected static String getAlgorithm(String str) {
        if (str.indexOf(91) > 0) {
            return str.substring(0, str.indexOf(91));
        }
        if (str.startsWith(NISTObjectIdentifiers.aes.getId())) {
            return "AES";
        }
        if (str.startsWith(GNUObjectIdentifiers.Serpent.getId())) {
            return "Serpent";
        }
        String str2 = (String) nameTable.get(Strings.toUpperCase(str));
        return str2 != null ? str2 : str;
    }

    protected static int getKeySize(String str) {
        if (str.indexOf(91) > 0) {
            return Integer.parseInt(str.substring(str.indexOf(91) + 1, str.indexOf(93)));
        }
        String upperCase = Strings.toUpperCase(str);
        if (!keySizes.containsKey(upperCase)) {
            return -1;
        }
        return ((Integer) keySizes.get(upperCase)).intValue();
    }

    private byte[] getSharedSecretBytes(byte[] bArr, String str, int i) throws NoSuchAlgorithmException {
        DerivationParameters derivationParameters;
        DerivationFunction derivationFunction = this.kdf;
        if (derivationFunction != null) {
            if (i >= 0) {
                byte[] bArr2 = new byte[(i / 8)];
                if (!(derivationFunction instanceof DHKEKGenerator)) {
                    derivationParameters = new KDFParameters(bArr, this.ukmParameters);
                } else if (str != null) {
                    try {
                        derivationParameters = new DHKDFParameters(new ASN1ObjectIdentifier(str), i, bArr, this.ukmParameters);
                    } catch (IllegalArgumentException e) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("no OID for algorithm: ");
                        sb.append(str);
                        throw new NoSuchAlgorithmException(sb.toString());
                    }
                } else {
                    throw new NoSuchAlgorithmException("algorithm OID is null");
                }
                this.kdf.init(derivationParameters);
                this.kdf.generateBytes(bArr2, 0, bArr2.length);
                Arrays.clear(bArr);
                return bArr2;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("unknown algorithm encountered: ");
            sb2.append(str);
            throw new NoSuchAlgorithmException(sb2.toString());
        } else if (i <= 0) {
            return bArr;
        } else {
            byte[] bArr3 = new byte[(i / 8)];
            System.arraycopy(bArr, 0, bArr3, 0, bArr3.length);
            Arrays.clear(bArr);
            return bArr3;
        }
    }

    protected static byte[] trimZeroes(byte[] bArr) {
        if (bArr[0] != 0) {
            return bArr;
        }
        int i = 0;
        while (i < bArr.length && bArr[i] == 0) {
            i++;
        }
        byte[] bArr2 = new byte[(bArr.length - i)];
        System.arraycopy(bArr, i, bArr2, 0, bArr2.length);
        return bArr2;
    }

    /* access modifiers changed from: protected */
    public abstract byte[] calcSecret();

    /* access modifiers changed from: protected */
    public int engineGenerateSecret(byte[] bArr, int i) throws IllegalStateException, ShortBufferException {
        byte[] engineGenerateSecret = engineGenerateSecret();
        if (bArr.length - i >= engineGenerateSecret.length) {
            System.arraycopy(engineGenerateSecret, 0, bArr, i, engineGenerateSecret.length);
            return engineGenerateSecret.length;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.kaAlgorithm);
        sb.append(" key agreement: need ");
        sb.append(engineGenerateSecret.length);
        sb.append(" bytes");
        throw new ShortBufferException(sb.toString());
    }

    /* access modifiers changed from: protected */
    public SecretKey engineGenerateSecret(String str) throws NoSuchAlgorithmException {
        String upperCase = Strings.toUpperCase(str);
        String id = oids.containsKey(upperCase) ? ((ASN1ObjectIdentifier) oids.get(upperCase)).getId() : str;
        byte[] sharedSecretBytes = getSharedSecretBytes(calcSecret(), id, getKeySize(id));
        String algorithm = getAlgorithm(str);
        if (des.containsKey(algorithm)) {
            DESParameters.setOddParity(sharedSecretBytes);
        }
        return new SecretKeySpec(sharedSecretBytes, algorithm);
    }

    /* access modifiers changed from: protected */
    public byte[] engineGenerateSecret() throws IllegalStateException {
        if (this.kdf == null) {
            return calcSecret();
        }
        byte[] calcSecret = calcSecret();
        try {
            return getSharedSecretBytes(calcSecret, null, calcSecret.length * 8);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
