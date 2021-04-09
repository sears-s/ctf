package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.DESedeWrapEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.DES.DESPBEKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class DESede {

    public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
        /* access modifiers changed from: protected */
        public AlgorithmParameters engineGenerateParameters() {
            byte[] bArr = new byte[8];
            if (this.random == null) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.random.nextBytes(bArr);
            try {
                AlgorithmParameters createParametersInstance = createParametersInstance("DES");
                createParametersInstance.init(new IvParameterSpec(bArr));
                return createParametersInstance;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        /* access modifiers changed from: protected */
        public void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for DES parameter generation.");
        }
    }

    public static class CBC extends BaseBlockCipher {
        public CBC() {
            super((BlockCipher) new CBCBlockCipher(new DESedeEngine()), 64);
        }
    }

    public static class CBCMAC extends BaseMac {
        public CBCMAC() {
            super(new CBCBlockCipherMac(new DESedeEngine()));
        }
    }

    public static class CMAC extends BaseMac {
        public CMAC() {
            super(new CMac(new DESedeEngine()));
        }
    }

    public static class DESede64 extends BaseMac {
        public DESede64() {
            super(new CBCBlockCipherMac((BlockCipher) new DESedeEngine(), 64));
        }
    }

    public static class DESede64with7816d4 extends BaseMac {
        public DESede64with7816d4() {
            super(new CBCBlockCipherMac(new DESedeEngine(), 64, new ISO7816d4Padding()));
        }
    }

    public static class DESedeCFB8 extends BaseMac {
        public DESedeCFB8() {
            super(new CFBBlockCipherMac(new DESedeEngine()));
        }
    }

    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super((BlockCipher) new DESedeEngine());
        }
    }

    public static class KeyFactory extends BaseSecretKeyFactory {
        public KeyFactory() {
            super("DESede", null);
        }

        /* access modifiers changed from: protected */
        public SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
            return keySpec instanceof DESedeKeySpec ? new SecretKeySpec(((DESedeKeySpec) keySpec).getKey(), "DESede") : super.engineGenerateSecret(keySpec);
        }

        /* access modifiers changed from: protected */
        public KeySpec engineGetKeySpec(SecretKey secretKey, Class cls) throws InvalidKeySpecException {
            if (cls == null) {
                throw new InvalidKeySpecException("keySpec parameter is null");
            } else if (secretKey == null) {
                throw new InvalidKeySpecException("key parameter is null");
            } else if (SecretKeySpec.class.isAssignableFrom(cls)) {
                return new SecretKeySpec(secretKey.getEncoded(), this.algName);
            } else {
                if (DESedeKeySpec.class.isAssignableFrom(cls)) {
                    byte[] encoded = secretKey.getEncoded();
                    try {
                        if (encoded.length != 16) {
                            return new DESedeKeySpec(encoded);
                        }
                        byte[] bArr = new byte[24];
                        System.arraycopy(encoded, 0, bArr, 0, 16);
                        System.arraycopy(encoded, 0, bArr, 16, 8);
                        return new DESedeKeySpec(bArr);
                    } catch (Exception e) {
                        throw new InvalidKeySpecException(e.toString());
                    }
                } else {
                    throw new InvalidKeySpecException("Invalid KeySpec");
                }
            }
        }
    }

    public static class KeyGenerator extends BaseKeyGenerator {
        private boolean keySizeSet = false;

        public KeyGenerator() {
            super("DESede", 192, new DESedeKeyGenerator());
        }

        /* access modifiers changed from: protected */
        public SecretKey engineGenerateKey() {
            if (this.uninitialised) {
                this.engine.init(new KeyGenerationParameters(CryptoServicesRegistrar.getSecureRandom(), this.defaultKeySize));
                this.uninitialised = false;
            }
            if (this.keySizeSet) {
                return new SecretKeySpec(this.engine.generateKey(), this.algName);
            }
            byte[] generateKey = this.engine.generateKey();
            System.arraycopy(generateKey, 0, generateKey, 16, 8);
            return new SecretKeySpec(generateKey, this.algName);
        }

        /* access modifiers changed from: protected */
        public void engineInit(int i, SecureRandom secureRandom) {
            super.engineInit(i, secureRandom);
            this.keySizeSet = true;
        }
    }

    public static class KeyGenerator3 extends BaseKeyGenerator {
        public KeyGenerator3() {
            super("DESede3", 192, new DESedeKeyGenerator());
        }
    }

    public static class Mappings extends AlgorithmProvider {
        private static final String PACKAGE = "org.bouncycastle.jcajce.provider.symmetric";
        private static final String PREFIX = DESede.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$ECB");
            configurableProvider.addAlgorithm("Cipher.DESEDE", sb.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier = PKCSObjectIdentifiers.des_EDE3_CBC;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            sb2.append("$CBC");
            String str = "Cipher";
            configurableProvider.addAlgorithm(str, aSN1ObjectIdentifier, sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            String str2 = "$Wrap";
            sb3.append(str2);
            configurableProvider.addAlgorithm("Cipher.DESEDEWRAP", sb3.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier2 = PKCSObjectIdentifiers.id_alg_CMS3DESwrap;
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            sb4.append(str2);
            configurableProvider.addAlgorithm(str, aSN1ObjectIdentifier2, sb4.toString());
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$RFC3211");
            configurableProvider.addAlgorithm("Cipher.DESEDERFC3211WRAP", sb5.toString());
            String str3 = "DESEDEWRAP";
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.DESEDERFC3217WRAP", str3);
            String str4 = "DESEDE";
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.TDEA", str4);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.TDEAWRAP", str3);
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.TDEA", str4);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.TDEA", str4);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.TDEA", str4);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.TDEA", str4);
            String str5 = "PBEWITHSHAAND2-KEYTRIPLEDES-CBC";
            String str6 = "PBEWITHSHAAND3-KEYTRIPLEDES-CBC";
            if (configurableProvider.hasAlgorithm("MessageDigest", "SHA-1")) {
                StringBuilder sb6 = new StringBuilder();
                sb6.append(PREFIX);
                sb6.append("$PBEWithSHAAndDES3Key");
                configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", sb6.toString());
                StringBuilder sb7 = new StringBuilder();
                sb7.append(PREFIX);
                sb7.append("$BrokePBEWithSHAAndDES3Key");
                configurableProvider.addAlgorithm("Cipher.BROKENPBEWITHSHAAND3-KEYTRIPLEDES-CBC", sb7.toString());
                StringBuilder sb8 = new StringBuilder();
                sb8.append(PREFIX);
                sb8.append("$OldPBEWithSHAAndDES3Key");
                configurableProvider.addAlgorithm("Cipher.OLDPBEWITHSHAAND3-KEYTRIPLEDES-CBC", sb8.toString());
                StringBuilder sb9 = new StringBuilder();
                sb9.append(PREFIX);
                sb9.append("$PBEWithSHAAndDES2Key");
                configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", sb9.toString());
                StringBuilder sb10 = new StringBuilder();
                sb10.append(PREFIX);
                sb10.append("$BrokePBEWithSHAAndDES2Key");
                configurableProvider.addAlgorithm("Cipher.BROKENPBEWITHSHAAND2-KEYTRIPLEDES-CBC", sb10.toString());
                String str7 = "Alg.Alias.Cipher";
                configurableProvider.addAlgorithm(str7, PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, str6);
                configurableProvider.addAlgorithm(str7, PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, str5);
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1ANDDESEDE", str6);
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND3-KEYTRIPLEDES-CBC", str6);
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND2-KEYTRIPLEDES-CBC", str5);
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND3-KEYDESEDE-CBC", str6);
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND2-KEYDESEDE-CBC", str5);
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND3-KEYDESEDE-CBC", str6);
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND2-KEYDESEDE-CBC", str5);
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1ANDDESEDE-CBC", str6);
            }
            StringBuilder sb11 = new StringBuilder();
            sb11.append(PREFIX);
            String str8 = "$KeyGenerator";
            sb11.append(str8);
            configurableProvider.addAlgorithm("KeyGenerator.DESEDE", sb11.toString());
            StringBuilder sb12 = new StringBuilder();
            sb12.append("KeyGenerator.");
            sb12.append(PKCSObjectIdentifiers.des_EDE3_CBC);
            String sb13 = sb12.toString();
            StringBuilder sb14 = new StringBuilder();
            sb14.append(PREFIX);
            sb14.append("$KeyGenerator3");
            configurableProvider.addAlgorithm(sb13, sb14.toString());
            StringBuilder sb15 = new StringBuilder();
            sb15.append(PREFIX);
            sb15.append(str8);
            configurableProvider.addAlgorithm("KeyGenerator.DESEDEWRAP", sb15.toString());
            StringBuilder sb16 = new StringBuilder();
            sb16.append(PREFIX);
            String str9 = "$KeyFactory";
            sb16.append(str9);
            configurableProvider.addAlgorithm("SecretKeyFactory.DESEDE", sb16.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier3 = OIWObjectIdentifiers.desEDE;
            StringBuilder sb17 = new StringBuilder();
            sb17.append(PREFIX);
            sb17.append(str9);
            configurableProvider.addAlgorithm("SecretKeyFactory", aSN1ObjectIdentifier3, sb17.toString());
            StringBuilder sb18 = new StringBuilder();
            sb18.append(PREFIX);
            sb18.append("$CMAC");
            configurableProvider.addAlgorithm("Mac.DESEDECMAC", sb18.toString());
            StringBuilder sb19 = new StringBuilder();
            sb19.append(PREFIX);
            sb19.append("$CBCMAC");
            configurableProvider.addAlgorithm("Mac.DESEDEMAC", sb19.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDE", "DESEDEMAC");
            StringBuilder sb20 = new StringBuilder();
            sb20.append(PREFIX);
            sb20.append("$DESedeCFB8");
            configurableProvider.addAlgorithm("Mac.DESEDEMAC/CFB8", sb20.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDE/CFB8", "DESEDEMAC/CFB8");
            StringBuilder sb21 = new StringBuilder();
            sb21.append(PREFIX);
            sb21.append("$DESede64");
            configurableProvider.addAlgorithm("Mac.DESEDEMAC64", sb21.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDE64", "DESEDEMAC64");
            StringBuilder sb22 = new StringBuilder();
            sb22.append(PREFIX);
            sb22.append("$DESede64with7816d4");
            configurableProvider.addAlgorithm("Mac.DESEDEMAC64WITHISO7816-4PADDING", sb22.toString());
            String str10 = "DESEDEMAC64WITHISO7816-4PADDING";
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDE64WITHISO7816-4PADDING", str10);
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDEISO9797ALG1MACWITHISO7816-4PADDING", str10);
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDEISO9797ALG1WITHISO7816-4PADDING", str10);
            configurableProvider.addAlgorithm("AlgorithmParameters.DESEDE", "org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters");
            StringBuilder sb23 = new StringBuilder();
            sb23.append("Alg.Alias.AlgorithmParameters.");
            sb23.append(PKCSObjectIdentifiers.des_EDE3_CBC);
            configurableProvider.addAlgorithm(sb23.toString(), str4);
            StringBuilder sb24 = new StringBuilder();
            sb24.append(PREFIX);
            sb24.append("$AlgParamGen");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.DESEDE", sb24.toString());
            StringBuilder sb25 = new StringBuilder();
            sb25.append("Alg.Alias.AlgorithmParameterGenerator.");
            sb25.append(PKCSObjectIdentifiers.des_EDE3_CBC);
            configurableProvider.addAlgorithm(sb25.toString(), str4);
            StringBuilder sb26 = new StringBuilder();
            sb26.append(PREFIX);
            sb26.append("$PBEWithSHAAndDES3KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", sb26.toString());
            StringBuilder sb27 = new StringBuilder();
            sb27.append(PREFIX);
            sb27.append("$PBEWithSHAAndDES2KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", sb27.toString());
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1ANDDESEDE", str6);
            String str11 = "PKCS12PBE";
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND3-KEYTRIPLEDES", str11);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND2-KEYTRIPLEDES", str11);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", str11);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", str11);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDDES3KEY-CBC", str11);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDDES2KEY-CBC", str11);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBE", str6);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.3", str6);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.4", str5);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWithSHAAnd3KeyTripleDES", str6);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.3", str11);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.4", str11);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWithSHAAnd3KeyTripleDES", str6);
        }
    }

    public static class PBEWithSHAAndDES2Key extends BaseBlockCipher {
        public PBEWithSHAAndDES2Key() {
            CBCBlockCipher cBCBlockCipher = new CBCBlockCipher(new DESedeEngine());
            super(cBCBlockCipher, 2, 1, 128, 8);
        }
    }

    public static class PBEWithSHAAndDES2KeyFactory extends DESPBEKeyFactory {
        public PBEWithSHAAndDES2KeyFactory() {
            super("PBEwithSHAandDES2Key-CBC", PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, true, 2, 1, 128, 64);
        }
    }

    public static class PBEWithSHAAndDES3Key extends BaseBlockCipher {
        public PBEWithSHAAndDES3Key() {
            CBCBlockCipher cBCBlockCipher = new CBCBlockCipher(new DESedeEngine());
            super(cBCBlockCipher, 2, 1, 192, 8);
        }
    }

    public static class PBEWithSHAAndDES3KeyFactory extends DESPBEKeyFactory {
        public PBEWithSHAAndDES3KeyFactory() {
            super("PBEwithSHAandDES3Key-CBC", PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, true, 2, 1, 192, 64);
        }
    }

    public static class RFC3211 extends BaseWrapCipher {
        public RFC3211() {
            super(new RFC3211WrapEngine(new DESedeEngine()), 8);
        }
    }

    public static class Wrap extends BaseWrapCipher {
        public Wrap() {
            super(new DESedeWrapEngine());
        }
    }

    private DESede() {
    }
}
