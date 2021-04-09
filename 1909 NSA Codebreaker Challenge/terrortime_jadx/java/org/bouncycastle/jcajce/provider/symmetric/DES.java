package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.generators.DESKeyGenerator;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.ISO9797Alg3Mac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.PBKDF1Key;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class DES {

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
            super((BlockCipher) new CBCBlockCipher(new DESEngine()), 64);
        }
    }

    public static class CBCMAC extends BaseMac {
        public CBCMAC() {
            super(new CBCBlockCipherMac(new DESEngine()));
        }
    }

    public static class CMAC extends BaseMac {
        public CMAC() {
            super(new CMac(new DESEngine()));
        }
    }

    public static class DES64 extends BaseMac {
        public DES64() {
            super(new CBCBlockCipherMac((BlockCipher) new DESEngine(), 64));
        }
    }

    public static class DES64with7816d4 extends BaseMac {
        public DES64with7816d4() {
            super(new CBCBlockCipherMac(new DESEngine(), 64, new ISO7816d4Padding()));
        }
    }

    public static class DES9797Alg3 extends BaseMac {
        public DES9797Alg3() {
            super(new ISO9797Alg3Mac(new DESEngine()));
        }
    }

    public static class DES9797Alg3with7816d4 extends BaseMac {
        public DES9797Alg3with7816d4() {
            super(new ISO9797Alg3Mac((BlockCipher) new DESEngine(), (BlockCipherPadding) new ISO7816d4Padding()));
        }
    }

    public static class DESCFB8 extends BaseMac {
        public DESCFB8() {
            super(new CFBBlockCipherMac(new DESEngine()));
        }
    }

    public static class DESPBEKeyFactory extends BaseSecretKeyFactory {
        private int digest;
        private boolean forCipher;
        private int ivSize;
        private int keySize;
        private int scheme;

        public DESPBEKeyFactory(String str, ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z, int i, int i2, int i3, int i4) {
            super(str, aSN1ObjectIdentifier);
            this.forCipher = z;
            this.scheme = i;
            this.digest = i2;
            this.keySize = i3;
            this.ivSize = i4;
        }

        /* access modifiers changed from: protected */
        public SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
            if (keySpec instanceof PBEKeySpec) {
                PBEKeySpec pBEKeySpec = (PBEKeySpec) keySpec;
                if (pBEKeySpec.getSalt() == null) {
                    int i = this.scheme;
                    if (i == 0 || i == 4) {
                        return new PBKDF1Key(pBEKeySpec.getPassword(), this.scheme == 0 ? PasswordConverter.ASCII : PasswordConverter.UTF8);
                    }
                    BCPBEKey bCPBEKey = new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, pBEKeySpec, null);
                    return bCPBEKey;
                }
                CipherParameters makePBEParameters = this.forCipher ? Util.makePBEParameters(pBEKeySpec, this.scheme, this.digest, this.keySize, this.ivSize) : Util.makePBEMacParameters(pBEKeySpec, this.scheme, this.digest, this.keySize);
                DESParameters.setOddParity((makePBEParameters instanceof ParametersWithIV ? (KeyParameter) ((ParametersWithIV) makePBEParameters).getParameters() : (KeyParameter) makePBEParameters).getKey());
                BCPBEKey bCPBEKey2 = new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, pBEKeySpec, makePBEParameters);
                return bCPBEKey2;
            }
            throw new InvalidKeySpecException("Invalid KeySpec");
        }
    }

    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super((BlockCipher) new DESEngine());
        }
    }

    public static class KeyFactory extends BaseSecretKeyFactory {
        public KeyFactory() {
            super("DES", null);
        }

        /* access modifiers changed from: protected */
        public SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
            return keySpec instanceof DESKeySpec ? new SecretKeySpec(((DESKeySpec) keySpec).getKey(), "DES") : super.engineGenerateSecret(keySpec);
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
                if (DESKeySpec.class.isAssignableFrom(cls)) {
                    try {
                        return new DESKeySpec(secretKey.getEncoded());
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
        public KeyGenerator() {
            super("DES", 64, new DESKeyGenerator());
        }

        /* access modifiers changed from: protected */
        public SecretKey engineGenerateKey() {
            if (this.uninitialised) {
                this.engine.init(new KeyGenerationParameters(CryptoServicesRegistrar.getSecureRandom(), this.defaultKeySize));
                this.uninitialised = false;
            }
            return new SecretKeySpec(this.engine.generateKey(), this.algName);
        }

        /* access modifiers changed from: protected */
        public void engineInit(int i, SecureRandom secureRandom) {
            super.engineInit(i, secureRandom);
        }
    }

    public static class Mappings extends AlgorithmProvider {
        private static final String PACKAGE = "org.bouncycastle.jcajce.provider.symmetric";
        private static final String PREFIX = DES.class.getName();

        private void addAlias(ConfigurableProvider configurableProvider, ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
            StringBuilder sb = new StringBuilder();
            sb.append("Alg.Alias.KeyGenerator.");
            sb.append(aSN1ObjectIdentifier.getId());
            configurableProvider.addAlgorithm(sb.toString(), str);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Alg.Alias.KeyFactory.");
            sb2.append(aSN1ObjectIdentifier.getId());
            configurableProvider.addAlgorithm(sb2.toString(), str);
        }

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$ECB");
            configurableProvider.addAlgorithm("Cipher.DES", sb.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier = OIWObjectIdentifiers.desCBC;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            sb2.append("$CBC");
            configurableProvider.addAlgorithm("Cipher", aSN1ObjectIdentifier, sb2.toString());
            String str = "DES";
            addAlias(configurableProvider, OIWObjectIdentifiers.desCBC, str);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$RFC3211");
            configurableProvider.addAlgorithm("Cipher.DESRFC3211WRAP", sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            sb4.append("$KeyGenerator");
            configurableProvider.addAlgorithm("KeyGenerator.DES", sb4.toString());
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.DES", sb5.toString());
            StringBuilder sb6 = new StringBuilder();
            sb6.append(PREFIX);
            sb6.append("$CMAC");
            configurableProvider.addAlgorithm("Mac.DESCMAC", sb6.toString());
            StringBuilder sb7 = new StringBuilder();
            sb7.append(PREFIX);
            sb7.append("$CBCMAC");
            configurableProvider.addAlgorithm("Mac.DESMAC", sb7.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DES", "DESMAC");
            StringBuilder sb8 = new StringBuilder();
            sb8.append(PREFIX);
            sb8.append("$DESCFB8");
            configurableProvider.addAlgorithm("Mac.DESMAC/CFB8", sb8.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DES/CFB8", "DESMAC/CFB8");
            StringBuilder sb9 = new StringBuilder();
            sb9.append(PREFIX);
            sb9.append("$DES64");
            configurableProvider.addAlgorithm("Mac.DESMAC64", sb9.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DES64", "DESMAC64");
            StringBuilder sb10 = new StringBuilder();
            sb10.append(PREFIX);
            sb10.append("$DES64with7816d4");
            configurableProvider.addAlgorithm("Mac.DESMAC64WITHISO7816-4PADDING", sb10.toString());
            String str2 = "DESMAC64WITHISO7816-4PADDING";
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DES64WITHISO7816-4PADDING", str2);
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESISO9797ALG1MACWITHISO7816-4PADDING", str2);
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESISO9797ALG1WITHISO7816-4PADDING", str2);
            StringBuilder sb11 = new StringBuilder();
            sb11.append(PREFIX);
            String str3 = "$DES9797Alg3";
            sb11.append(str3);
            configurableProvider.addAlgorithm("Mac.DESWITHISO9797", sb11.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESISO9797MAC", "DESWITHISO9797");
            StringBuilder sb12 = new StringBuilder();
            sb12.append(PREFIX);
            sb12.append(str3);
            configurableProvider.addAlgorithm("Mac.ISO9797ALG3MAC", sb12.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Mac.ISO9797ALG3", "ISO9797ALG3MAC");
            StringBuilder sb13 = new StringBuilder();
            sb13.append(PREFIX);
            sb13.append("$DES9797Alg3with7816d4");
            configurableProvider.addAlgorithm("Mac.ISO9797ALG3WITHISO7816-4PADDING", sb13.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Mac.ISO9797ALG3MACWITHISO7816-4PADDING", "ISO9797ALG3WITHISO7816-4PADDING");
            configurableProvider.addAlgorithm("AlgorithmParameters.DES", "org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", OIWObjectIdentifiers.desCBC, str);
            StringBuilder sb14 = new StringBuilder();
            sb14.append(PREFIX);
            sb14.append("$AlgParamGen");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.DES", sb14.toString());
            StringBuilder sb15 = new StringBuilder();
            sb15.append("Alg.Alias.AlgorithmParameterGenerator.");
            sb15.append(OIWObjectIdentifiers.desCBC);
            configurableProvider.addAlgorithm(sb15.toString(), str);
            StringBuilder sb16 = new StringBuilder();
            sb16.append(PREFIX);
            sb16.append("$PBEWithMD2");
            configurableProvider.addAlgorithm("Cipher.PBEWITHMD2ANDDES", sb16.toString());
            StringBuilder sb17 = new StringBuilder();
            sb17.append(PREFIX);
            sb17.append("$PBEWithMD5");
            configurableProvider.addAlgorithm("Cipher.PBEWITHMD5ANDDES", sb17.toString());
            StringBuilder sb18 = new StringBuilder();
            sb18.append(PREFIX);
            sb18.append("$PBEWithSHA1");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHA1ANDDES", sb18.toString());
            String str4 = "Alg.Alias.Cipher";
            String str5 = "PBEWITHMD2ANDDES";
            configurableProvider.addAlgorithm(str4, PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, str5);
            String str6 = "PBEWITHMD5ANDDES";
            configurableProvider.addAlgorithm(str4, PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, str6);
            String str7 = "PBEWITHSHA1ANDDES";
            configurableProvider.addAlgorithm(str4, PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, str7);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHMD2ANDDES-CBC", str5);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHMD5ANDDES-CBC", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1ANDDES-CBC", str7);
            StringBuilder sb19 = new StringBuilder();
            sb19.append(PREFIX);
            sb19.append("$PBEWithMD2KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD2ANDDES", sb19.toString());
            StringBuilder sb20 = new StringBuilder();
            sb20.append(PREFIX);
            sb20.append("$PBEWithMD5KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD5ANDDES", sb20.toString());
            StringBuilder sb21 = new StringBuilder();
            sb21.append(PREFIX);
            sb21.append("$PBEWithSHA1KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHA1ANDDES", sb21.toString());
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHMD2ANDDES-CBC", str5);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHMD5ANDDES-CBC", str6);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1ANDDES-CBC", str7);
            StringBuilder sb22 = new StringBuilder();
            String str8 = "Alg.Alias.SecretKeyFactory.";
            sb22.append(str8);
            sb22.append(PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC);
            configurableProvider.addAlgorithm(sb22.toString(), str5);
            StringBuilder sb23 = new StringBuilder();
            sb23.append(str8);
            sb23.append(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC);
            configurableProvider.addAlgorithm(sb23.toString(), str6);
            StringBuilder sb24 = new StringBuilder();
            sb24.append(str8);
            sb24.append(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC);
            configurableProvider.addAlgorithm(sb24.toString(), str7);
        }
    }

    public static class PBEWithMD2 extends BaseBlockCipher {
        public PBEWithMD2() {
            CBCBlockCipher cBCBlockCipher = new CBCBlockCipher(new DESEngine());
            super(cBCBlockCipher, 0, 5, 64, 8);
        }
    }

    public static class PBEWithMD2KeyFactory extends DESPBEKeyFactory {
        public PBEWithMD2KeyFactory() {
            super("PBEwithMD2andDES", PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, true, 0, 5, 64, 64);
        }
    }

    public static class PBEWithMD5 extends BaseBlockCipher {
        public PBEWithMD5() {
            CBCBlockCipher cBCBlockCipher = new CBCBlockCipher(new DESEngine());
            super(cBCBlockCipher, 0, 0, 64, 8);
        }
    }

    public static class PBEWithMD5KeyFactory extends DESPBEKeyFactory {
        public PBEWithMD5KeyFactory() {
            super("PBEwithMD5andDES", PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, true, 0, 0, 64, 64);
        }
    }

    public static class PBEWithSHA1 extends BaseBlockCipher {
        public PBEWithSHA1() {
            CBCBlockCipher cBCBlockCipher = new CBCBlockCipher(new DESEngine());
            super(cBCBlockCipher, 0, 1, 64, 8);
        }
    }

    public static class PBEWithSHA1KeyFactory extends DESPBEKeyFactory {
        public PBEWithSHA1KeyFactory() {
            super("PBEwithSHA1andDES", PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, true, 0, 1, 64, 64);
        }
    }

    public static class RFC3211 extends BaseWrapCipher {
        public RFC3211() {
            super(new RFC3211WrapEngine(new DESEngine()), 8);
        }
    }

    private DES() {
    }
}
