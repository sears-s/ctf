package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.cms.CCMParameters;
import org.bouncycastle.asn1.cms.GCMParameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.AESWrapEngine;
import org.bouncycastle.crypto.engines.AESWrapPadEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.engines.RFC5649WrapEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;

public final class AES {
    /* access modifiers changed from: private */
    public static final Map<String, String> generalAesAttributes = new HashMap();

    public static class AESCCMMAC extends BaseMac {

        private static class CCMMac implements Mac {
            private final CCMBlockCipher ccm;
            private int macLength;

            private CCMMac() {
                this.ccm = new CCMBlockCipher(new AESEngine());
                this.macLength = 8;
            }

            public int doFinal(byte[] bArr, int i) throws DataLengthException, IllegalStateException {
                try {
                    return this.ccm.doFinal(bArr, 0);
                } catch (InvalidCipherTextException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("exception on doFinal(): ");
                    sb.append(e.toString());
                    throw new IllegalStateException(sb.toString());
                }
            }

            public String getAlgorithmName() {
                StringBuilder sb = new StringBuilder();
                sb.append(this.ccm.getAlgorithmName());
                sb.append("Mac");
                return sb.toString();
            }

            public int getMacSize() {
                return this.macLength;
            }

            public void init(CipherParameters cipherParameters) throws IllegalArgumentException {
                this.ccm.init(true, cipherParameters);
                this.macLength = this.ccm.getMac().length;
            }

            public void reset() {
                this.ccm.reset();
            }

            public void update(byte b) throws IllegalStateException {
                this.ccm.processAADByte(b);
            }

            public void update(byte[] bArr, int i, int i2) throws DataLengthException, IllegalStateException {
                this.ccm.processAADBytes(bArr, i, i2);
            }
        }

        public AESCCMMAC() {
            super(new CCMMac());
        }
    }

    public static class AESCMAC extends BaseMac {
        public AESCMAC() {
            super(new CMac(new AESEngine()));
        }
    }

    public static class AESGMAC extends BaseMac {
        public AESGMAC() {
            super(new GMac(new GCMBlockCipher(new AESEngine())));
        }
    }

    public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
        /* access modifiers changed from: protected */
        public AlgorithmParameters engineGenerateParameters() {
            byte[] bArr = new byte[16];
            if (this.random == null) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.random.nextBytes(bArr);
            try {
                AlgorithmParameters createParametersInstance = createParametersInstance("AES");
                createParametersInstance.init(new IvParameterSpec(bArr));
                return createParametersInstance;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        /* access modifiers changed from: protected */
        public void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
        }
    }

    public static class AlgParamGenCCM extends BaseAlgorithmParameterGenerator {
        /* access modifiers changed from: protected */
        public AlgorithmParameters engineGenerateParameters() {
            byte[] bArr = new byte[12];
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            this.random.nextBytes(bArr);
            try {
                AlgorithmParameters createParametersInstance = createParametersInstance("CCM");
                createParametersInstance.init(new CCMParameters(bArr, 12).getEncoded());
                return createParametersInstance;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        /* access modifiers changed from: protected */
        public void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
        }
    }

    public static class AlgParamGenGCM extends BaseAlgorithmParameterGenerator {
        /* access modifiers changed from: protected */
        public AlgorithmParameters engineGenerateParameters() {
            byte[] bArr = new byte[12];
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            this.random.nextBytes(bArr);
            try {
                AlgorithmParameters createParametersInstance = createParametersInstance("GCM");
                createParametersInstance.init(new GCMParameters(bArr, 16).getEncoded());
                return createParametersInstance;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        /* access modifiers changed from: protected */
        public void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
        }
    }

    public static class AlgParams extends IvAlgorithmParameters {
        /* access modifiers changed from: protected */
        public String engineToString() {
            return "AES IV";
        }
    }

    public static class AlgParamsCCM extends BaseAlgorithmParameters {
        private CCMParameters ccmParams;

        /* access modifiers changed from: protected */
        public byte[] engineGetEncoded() throws IOException {
            return this.ccmParams.getEncoded();
        }

        /* access modifiers changed from: protected */
        public byte[] engineGetEncoded(String str) throws IOException {
            if (isASN1FormatString(str)) {
                return this.ccmParams.getEncoded();
            }
            throw new IOException("unknown format specified");
        }

        /* access modifiers changed from: protected */
        public void engineInit(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (GcmSpecUtil.isGcmSpec(algorithmParameterSpec)) {
                this.ccmParams = CCMParameters.getInstance(GcmSpecUtil.extractGcmParameters(algorithmParameterSpec));
            } else if (algorithmParameterSpec instanceof AEADParameterSpec) {
                AEADParameterSpec aEADParameterSpec = (AEADParameterSpec) algorithmParameterSpec;
                this.ccmParams = new CCMParameters(aEADParameterSpec.getNonce(), aEADParameterSpec.getMacSizeInBits() / 8);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("AlgorithmParameterSpec class not recognized: ");
                sb.append(algorithmParameterSpec.getClass().getName());
                throw new InvalidParameterSpecException(sb.toString());
            }
        }

        /* access modifiers changed from: protected */
        public void engineInit(byte[] bArr) throws IOException {
            this.ccmParams = CCMParameters.getInstance(bArr);
        }

        /* access modifiers changed from: protected */
        public void engineInit(byte[] bArr, String str) throws IOException {
            if (isASN1FormatString(str)) {
                this.ccmParams = CCMParameters.getInstance(bArr);
                return;
            }
            throw new IOException("unknown format specified");
        }

        /* access modifiers changed from: protected */
        public String engineToString() {
            return "CCM";
        }

        /* access modifiers changed from: protected */
        public AlgorithmParameterSpec localEngineGetParameterSpec(Class cls) throws InvalidParameterSpecException {
            if (cls == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(cls)) {
                return GcmSpecUtil.gcmSpecExists() ? GcmSpecUtil.extractGcmSpec(this.ccmParams.toASN1Primitive()) : new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8);
            }
            if (cls == AEADParameterSpec.class) {
                return new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8);
            }
            if (cls == IvParameterSpec.class) {
                return new IvParameterSpec(this.ccmParams.getNonce());
            }
            StringBuilder sb = new StringBuilder();
            sb.append("AlgorithmParameterSpec not recognized: ");
            sb.append(cls.getName());
            throw new InvalidParameterSpecException(sb.toString());
        }
    }

    public static class AlgParamsGCM extends BaseAlgorithmParameters {
        private GCMParameters gcmParams;

        /* access modifiers changed from: protected */
        public byte[] engineGetEncoded() throws IOException {
            return this.gcmParams.getEncoded();
        }

        /* access modifiers changed from: protected */
        public byte[] engineGetEncoded(String str) throws IOException {
            if (isASN1FormatString(str)) {
                return this.gcmParams.getEncoded();
            }
            throw new IOException("unknown format specified");
        }

        /* access modifiers changed from: protected */
        public void engineInit(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (GcmSpecUtil.isGcmSpec(algorithmParameterSpec)) {
                this.gcmParams = GcmSpecUtil.extractGcmParameters(algorithmParameterSpec);
            } else if (algorithmParameterSpec instanceof AEADParameterSpec) {
                AEADParameterSpec aEADParameterSpec = (AEADParameterSpec) algorithmParameterSpec;
                this.gcmParams = new GCMParameters(aEADParameterSpec.getNonce(), aEADParameterSpec.getMacSizeInBits() / 8);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("AlgorithmParameterSpec class not recognized: ");
                sb.append(algorithmParameterSpec.getClass().getName());
                throw new InvalidParameterSpecException(sb.toString());
            }
        }

        /* access modifiers changed from: protected */
        public void engineInit(byte[] bArr) throws IOException {
            this.gcmParams = GCMParameters.getInstance(bArr);
        }

        /* access modifiers changed from: protected */
        public void engineInit(byte[] bArr, String str) throws IOException {
            if (isASN1FormatString(str)) {
                this.gcmParams = GCMParameters.getInstance(bArr);
                return;
            }
            throw new IOException("unknown format specified");
        }

        /* access modifiers changed from: protected */
        public String engineToString() {
            return "GCM";
        }

        /* access modifiers changed from: protected */
        public AlgorithmParameterSpec localEngineGetParameterSpec(Class cls) throws InvalidParameterSpecException {
            if (cls == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(cls)) {
                return GcmSpecUtil.gcmSpecExists() ? GcmSpecUtil.extractGcmSpec(this.gcmParams.toASN1Primitive()) : new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8);
            }
            if (cls == AEADParameterSpec.class) {
                return new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8);
            }
            if (cls == IvParameterSpec.class) {
                return new IvParameterSpec(this.gcmParams.getNonce());
            }
            StringBuilder sb = new StringBuilder();
            sb.append("AlgorithmParameterSpec not recognized: ");
            sb.append(cls.getName());
            throw new InvalidParameterSpecException(sb.toString());
        }
    }

    public static class CBC extends BaseBlockCipher {
        public CBC() {
            super((BlockCipher) new CBCBlockCipher(new AESEngine()), 128);
        }
    }

    public static class CCM extends BaseBlockCipher {
        public CCM() {
            super((AEADBlockCipher) new CCMBlockCipher(new AESEngine()), false, 16);
        }
    }

    public static class CFB extends BaseBlockCipher {
        public CFB() {
            super(new BufferedBlockCipher(new CFBBlockCipher(new AESEngine(), 128)), 128);
        }
    }

    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super((BlockCipherProvider) new BlockCipherProvider() {
                public BlockCipher get() {
                    return new AESEngine();
                }
            });
        }
    }

    public static class GCM extends BaseBlockCipher {
        public GCM() {
            super((AEADBlockCipher) new GCMBlockCipher(new AESEngine()));
        }
    }

    public static class KeyFactory extends BaseSecretKeyFactory {
        public KeyFactory() {
            super("AES", null);
        }
    }

    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            this(192);
        }

        public KeyGen(int i) {
            super("AES", i, new CipherKeyGenerator());
        }
    }

    public static class KeyGen128 extends KeyGen {
        public KeyGen128() {
            super(128);
        }
    }

    public static class KeyGen192 extends KeyGen {
        public KeyGen192() {
            super(192);
        }
    }

    public static class KeyGen256 extends KeyGen {
        public KeyGen256() {
            super(256);
        }
    }

    public static class Mappings extends SymmetricAlgorithmProvider {
        private static final String PREFIX = AES.class.getName();
        private static final String wrongAES128 = "2.16.840.1.101.3.4.2";
        private static final String wrongAES192 = "2.16.840.1.101.3.4.22";
        private static final String wrongAES256 = "2.16.840.1.101.3.4.42";

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$AlgParams");
            configurableProvider.addAlgorithm("AlgorithmParameters.AES", sb.toString());
            String str = "AES";
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.2", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.22", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.42", str);
            StringBuilder sb2 = new StringBuilder();
            String str2 = "Alg.Alias.AlgorithmParameters.";
            sb2.append(str2);
            sb2.append(NISTObjectIdentifiers.id_aes128_CBC);
            configurableProvider.addAlgorithm(sb2.toString(), str);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str2);
            sb3.append(NISTObjectIdentifiers.id_aes192_CBC);
            configurableProvider.addAlgorithm(sb3.toString(), str);
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str2);
            sb4.append(NISTObjectIdentifiers.id_aes256_CBC);
            configurableProvider.addAlgorithm(sb4.toString(), str);
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$AlgParamsGCM");
            configurableProvider.addAlgorithm("AlgorithmParameters.GCM", sb5.toString());
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str2);
            sb6.append(NISTObjectIdentifiers.id_aes128_GCM);
            String str3 = "GCM";
            configurableProvider.addAlgorithm(sb6.toString(), str3);
            StringBuilder sb7 = new StringBuilder();
            sb7.append(str2);
            sb7.append(NISTObjectIdentifiers.id_aes192_GCM);
            configurableProvider.addAlgorithm(sb7.toString(), str3);
            StringBuilder sb8 = new StringBuilder();
            sb8.append(str2);
            sb8.append(NISTObjectIdentifiers.id_aes256_GCM);
            configurableProvider.addAlgorithm(sb8.toString(), str3);
            StringBuilder sb9 = new StringBuilder();
            sb9.append(PREFIX);
            sb9.append("$AlgParamsCCM");
            configurableProvider.addAlgorithm("AlgorithmParameters.CCM", sb9.toString());
            StringBuilder sb10 = new StringBuilder();
            sb10.append(str2);
            sb10.append(NISTObjectIdentifiers.id_aes128_CCM);
            String str4 = "CCM";
            configurableProvider.addAlgorithm(sb10.toString(), str4);
            StringBuilder sb11 = new StringBuilder();
            sb11.append(str2);
            sb11.append(NISTObjectIdentifiers.id_aes192_CCM);
            configurableProvider.addAlgorithm(sb11.toString(), str4);
            StringBuilder sb12 = new StringBuilder();
            sb12.append(str2);
            sb12.append(NISTObjectIdentifiers.id_aes256_CCM);
            configurableProvider.addAlgorithm(sb12.toString(), str4);
            StringBuilder sb13 = new StringBuilder();
            sb13.append(PREFIX);
            sb13.append("$AlgParamGen");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.AES", sb13.toString());
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.2", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.22", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.42", str);
            StringBuilder sb14 = new StringBuilder();
            String str5 = "Alg.Alias.AlgorithmParameterGenerator.";
            sb14.append(str5);
            sb14.append(NISTObjectIdentifiers.id_aes128_CBC);
            configurableProvider.addAlgorithm(sb14.toString(), str);
            StringBuilder sb15 = new StringBuilder();
            sb15.append(str5);
            sb15.append(NISTObjectIdentifiers.id_aes192_CBC);
            configurableProvider.addAlgorithm(sb15.toString(), str);
            StringBuilder sb16 = new StringBuilder();
            sb16.append(str5);
            sb16.append(NISTObjectIdentifiers.id_aes256_CBC);
            configurableProvider.addAlgorithm(sb16.toString(), str);
            configurableProvider.addAttributes("Cipher.AES", AES.generalAesAttributes);
            StringBuilder sb17 = new StringBuilder();
            sb17.append(PREFIX);
            sb17.append("$ECB");
            configurableProvider.addAlgorithm("Cipher.AES", sb17.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.2", str);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.22", str);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.42", str);
            ASN1ObjectIdentifier aSN1ObjectIdentifier = NISTObjectIdentifiers.id_aes128_ECB;
            StringBuilder sb18 = new StringBuilder();
            sb18.append(PREFIX);
            sb18.append("$ECB");
            String str6 = "Cipher";
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier, sb18.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier2 = NISTObjectIdentifiers.id_aes192_ECB;
            StringBuilder sb19 = new StringBuilder();
            sb19.append(PREFIX);
            sb19.append("$ECB");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier2, sb19.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier3 = NISTObjectIdentifiers.id_aes256_ECB;
            StringBuilder sb20 = new StringBuilder();
            sb20.append(PREFIX);
            sb20.append("$ECB");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier3, sb20.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier4 = NISTObjectIdentifiers.id_aes128_CBC;
            StringBuilder sb21 = new StringBuilder();
            sb21.append(PREFIX);
            sb21.append("$CBC");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier4, sb21.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier5 = NISTObjectIdentifiers.id_aes192_CBC;
            StringBuilder sb22 = new StringBuilder();
            sb22.append(PREFIX);
            sb22.append("$CBC");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier5, sb22.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier6 = NISTObjectIdentifiers.id_aes256_CBC;
            StringBuilder sb23 = new StringBuilder();
            sb23.append(PREFIX);
            sb23.append("$CBC");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier6, sb23.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier7 = NISTObjectIdentifiers.id_aes128_OFB;
            StringBuilder sb24 = new StringBuilder();
            sb24.append(PREFIX);
            sb24.append("$OFB");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier7, sb24.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier8 = NISTObjectIdentifiers.id_aes192_OFB;
            StringBuilder sb25 = new StringBuilder();
            sb25.append(PREFIX);
            sb25.append("$OFB");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier8, sb25.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier9 = NISTObjectIdentifiers.id_aes256_OFB;
            StringBuilder sb26 = new StringBuilder();
            sb26.append(PREFIX);
            sb26.append("$OFB");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier9, sb26.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier10 = NISTObjectIdentifiers.id_aes128_CFB;
            StringBuilder sb27 = new StringBuilder();
            sb27.append(PREFIX);
            sb27.append("$CFB");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier10, sb27.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier11 = NISTObjectIdentifiers.id_aes192_CFB;
            StringBuilder sb28 = new StringBuilder();
            sb28.append(PREFIX);
            sb28.append("$CFB");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier11, sb28.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier12 = NISTObjectIdentifiers.id_aes256_CFB;
            StringBuilder sb29 = new StringBuilder();
            sb29.append(PREFIX);
            sb29.append("$CFB");
            configurableProvider.addAlgorithm(str6, aSN1ObjectIdentifier12, sb29.toString());
            configurableProvider.addAttributes("Cipher.AESWRAP", AES.generalAesAttributes);
            StringBuilder sb30 = new StringBuilder();
            sb30.append(PREFIX);
            sb30.append("$Wrap");
            configurableProvider.addAlgorithm("Cipher.AESWRAP", sb30.toString());
            String str7 = "Alg.Alias.Cipher";
            configurableProvider.addAlgorithm(str7, NISTObjectIdentifiers.id_aes128_wrap, "AESWRAP");
            configurableProvider.addAlgorithm(str7, NISTObjectIdentifiers.id_aes192_wrap, "AESWRAP");
            configurableProvider.addAlgorithm(str7, NISTObjectIdentifiers.id_aes256_wrap, "AESWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.AESKW", "AESWRAP");
            configurableProvider.addAttributes("Cipher.AESWRAPPAD", AES.generalAesAttributes);
            StringBuilder sb31 = new StringBuilder();
            sb31.append(PREFIX);
            sb31.append("$WrapPad");
            configurableProvider.addAlgorithm("Cipher.AESWRAPPAD", sb31.toString());
            configurableProvider.addAlgorithm(str7, NISTObjectIdentifiers.id_aes128_wrap_pad, "AESWRAPPAD");
            configurableProvider.addAlgorithm(str7, NISTObjectIdentifiers.id_aes192_wrap_pad, "AESWRAPPAD");
            configurableProvider.addAlgorithm(str7, NISTObjectIdentifiers.id_aes256_wrap_pad, "AESWRAPPAD");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.AESKWP", "AESWRAPPAD");
            StringBuilder sb32 = new StringBuilder();
            sb32.append(PREFIX);
            sb32.append("$RFC3211Wrap");
            configurableProvider.addAlgorithm("Cipher.AESRFC3211WRAP", sb32.toString());
            StringBuilder sb33 = new StringBuilder();
            sb33.append(PREFIX);
            sb33.append("$RFC5649Wrap");
            configurableProvider.addAlgorithm("Cipher.AESRFC5649WRAP", sb33.toString());
            StringBuilder sb34 = new StringBuilder();
            sb34.append(PREFIX);
            sb34.append("$AlgParamGenCCM");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.CCM", sb34.toString());
            StringBuilder sb35 = new StringBuilder();
            sb35.append(str5);
            sb35.append(NISTObjectIdentifiers.id_aes128_CCM);
            configurableProvider.addAlgorithm(sb35.toString(), str4);
            StringBuilder sb36 = new StringBuilder();
            sb36.append(str5);
            sb36.append(NISTObjectIdentifiers.id_aes192_CCM);
            configurableProvider.addAlgorithm(sb36.toString(), str4);
            StringBuilder sb37 = new StringBuilder();
            sb37.append(str5);
            sb37.append(NISTObjectIdentifiers.id_aes256_CCM);
            configurableProvider.addAlgorithm(sb37.toString(), str4);
            configurableProvider.addAttributes("Cipher.CCM", AES.generalAesAttributes);
            StringBuilder sb38 = new StringBuilder();
            sb38.append(PREFIX);
            sb38.append("$CCM");
            configurableProvider.addAlgorithm("Cipher.CCM", sb38.toString());
            configurableProvider.addAlgorithm(str7, NISTObjectIdentifiers.id_aes128_CCM, str4);
            configurableProvider.addAlgorithm(str7, NISTObjectIdentifiers.id_aes192_CCM, str4);
            configurableProvider.addAlgorithm(str7, NISTObjectIdentifiers.id_aes256_CCM, str4);
            StringBuilder sb39 = new StringBuilder();
            sb39.append(PREFIX);
            sb39.append("$AlgParamGenGCM");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.GCM", sb39.toString());
            StringBuilder sb40 = new StringBuilder();
            sb40.append(str5);
            sb40.append(NISTObjectIdentifiers.id_aes128_GCM);
            configurableProvider.addAlgorithm(sb40.toString(), str3);
            StringBuilder sb41 = new StringBuilder();
            sb41.append(str5);
            sb41.append(NISTObjectIdentifiers.id_aes192_GCM);
            configurableProvider.addAlgorithm(sb41.toString(), str3);
            StringBuilder sb42 = new StringBuilder();
            sb42.append(str5);
            sb42.append(NISTObjectIdentifiers.id_aes256_GCM);
            configurableProvider.addAlgorithm(sb42.toString(), str3);
            configurableProvider.addAttributes("Cipher.GCM", AES.generalAesAttributes);
            StringBuilder sb43 = new StringBuilder();
            sb43.append(PREFIX);
            sb43.append("$GCM");
            configurableProvider.addAlgorithm("Cipher.GCM", sb43.toString());
            configurableProvider.addAlgorithm(str7, NISTObjectIdentifiers.id_aes128_GCM, str3);
            configurableProvider.addAlgorithm(str7, NISTObjectIdentifiers.id_aes192_GCM, str3);
            configurableProvider.addAlgorithm(str7, NISTObjectIdentifiers.id_aes256_GCM, str3);
            StringBuilder sb44 = new StringBuilder();
            sb44.append(PREFIX);
            sb44.append("$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator.AES", sb44.toString());
            StringBuilder sb45 = new StringBuilder();
            sb45.append(PREFIX);
            String str8 = "$KeyGen128";
            sb45.append(str8);
            configurableProvider.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.2", sb45.toString());
            StringBuilder sb46 = new StringBuilder();
            sb46.append(PREFIX);
            String str9 = "$KeyGen192";
            sb46.append(str9);
            configurableProvider.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.22", sb46.toString());
            StringBuilder sb47 = new StringBuilder();
            sb47.append(PREFIX);
            String str10 = "$KeyGen256";
            sb47.append(str10);
            configurableProvider.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.42", sb47.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier13 = NISTObjectIdentifiers.id_aes128_ECB;
            StringBuilder sb48 = new StringBuilder();
            sb48.append(PREFIX);
            sb48.append(str8);
            String str11 = "KeyGenerator";
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier13, sb48.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier14 = NISTObjectIdentifiers.id_aes128_CBC;
            StringBuilder sb49 = new StringBuilder();
            sb49.append(PREFIX);
            sb49.append(str8);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier14, sb49.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier15 = NISTObjectIdentifiers.id_aes128_OFB;
            StringBuilder sb50 = new StringBuilder();
            sb50.append(PREFIX);
            sb50.append(str8);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier15, sb50.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier16 = NISTObjectIdentifiers.id_aes128_CFB;
            StringBuilder sb51 = new StringBuilder();
            sb51.append(PREFIX);
            sb51.append(str8);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier16, sb51.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier17 = NISTObjectIdentifiers.id_aes192_ECB;
            StringBuilder sb52 = new StringBuilder();
            sb52.append(PREFIX);
            sb52.append(str9);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier17, sb52.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier18 = NISTObjectIdentifiers.id_aes192_CBC;
            StringBuilder sb53 = new StringBuilder();
            sb53.append(PREFIX);
            sb53.append(str9);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier18, sb53.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier19 = NISTObjectIdentifiers.id_aes192_OFB;
            StringBuilder sb54 = new StringBuilder();
            sb54.append(PREFIX);
            sb54.append(str9);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier19, sb54.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier20 = NISTObjectIdentifiers.id_aes192_CFB;
            StringBuilder sb55 = new StringBuilder();
            sb55.append(PREFIX);
            sb55.append(str9);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier20, sb55.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier21 = NISTObjectIdentifiers.id_aes256_ECB;
            StringBuilder sb56 = new StringBuilder();
            sb56.append(PREFIX);
            sb56.append(str10);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier21, sb56.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier22 = NISTObjectIdentifiers.id_aes256_CBC;
            StringBuilder sb57 = new StringBuilder();
            sb57.append(PREFIX);
            sb57.append(str10);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier22, sb57.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier23 = NISTObjectIdentifiers.id_aes256_OFB;
            StringBuilder sb58 = new StringBuilder();
            sb58.append(PREFIX);
            sb58.append(str10);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier23, sb58.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier24 = NISTObjectIdentifiers.id_aes256_CFB;
            StringBuilder sb59 = new StringBuilder();
            sb59.append(PREFIX);
            sb59.append(str10);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier24, sb59.toString());
            StringBuilder sb60 = new StringBuilder();
            sb60.append(PREFIX);
            sb60.append("$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator.AESWRAP", sb60.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier25 = NISTObjectIdentifiers.id_aes128_wrap;
            StringBuilder sb61 = new StringBuilder();
            sb61.append(PREFIX);
            sb61.append(str8);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier25, sb61.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier26 = NISTObjectIdentifiers.id_aes192_wrap;
            StringBuilder sb62 = new StringBuilder();
            sb62.append(PREFIX);
            sb62.append(str9);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier26, sb62.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier27 = NISTObjectIdentifiers.id_aes256_wrap;
            StringBuilder sb63 = new StringBuilder();
            sb63.append(PREFIX);
            sb63.append(str10);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier27, sb63.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier28 = NISTObjectIdentifiers.id_aes128_GCM;
            StringBuilder sb64 = new StringBuilder();
            sb64.append(PREFIX);
            sb64.append(str8);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier28, sb64.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier29 = NISTObjectIdentifiers.id_aes192_GCM;
            StringBuilder sb65 = new StringBuilder();
            sb65.append(PREFIX);
            sb65.append(str9);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier29, sb65.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier30 = NISTObjectIdentifiers.id_aes256_GCM;
            StringBuilder sb66 = new StringBuilder();
            sb66.append(PREFIX);
            sb66.append(str10);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier30, sb66.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier31 = NISTObjectIdentifiers.id_aes128_CCM;
            StringBuilder sb67 = new StringBuilder();
            sb67.append(PREFIX);
            sb67.append(str8);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier31, sb67.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier32 = NISTObjectIdentifiers.id_aes192_CCM;
            StringBuilder sb68 = new StringBuilder();
            sb68.append(PREFIX);
            sb68.append(str9);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier32, sb68.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier33 = NISTObjectIdentifiers.id_aes256_CCM;
            StringBuilder sb69 = new StringBuilder();
            sb69.append(PREFIX);
            sb69.append(str10);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier33, sb69.toString());
            StringBuilder sb70 = new StringBuilder();
            sb70.append(PREFIX);
            sb70.append("$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator.AESWRAPPAD", sb70.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier34 = NISTObjectIdentifiers.id_aes128_wrap_pad;
            StringBuilder sb71 = new StringBuilder();
            sb71.append(PREFIX);
            sb71.append(str8);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier34, sb71.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier35 = NISTObjectIdentifiers.id_aes192_wrap_pad;
            StringBuilder sb72 = new StringBuilder();
            sb72.append(PREFIX);
            sb72.append(str9);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier35, sb72.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier36 = NISTObjectIdentifiers.id_aes256_wrap_pad;
            StringBuilder sb73 = new StringBuilder();
            sb73.append(PREFIX);
            sb73.append(str10);
            configurableProvider.addAlgorithm(str11, aSN1ObjectIdentifier36, sb73.toString());
            StringBuilder sb74 = new StringBuilder();
            sb74.append(PREFIX);
            sb74.append("$AESCMAC");
            configurableProvider.addAlgorithm("Mac.AESCMAC", sb74.toString());
            StringBuilder sb75 = new StringBuilder();
            sb75.append(PREFIX);
            sb75.append("$AESCCMMAC");
            configurableProvider.addAlgorithm("Mac.AESCCMMAC", sb75.toString());
            StringBuilder sb76 = new StringBuilder();
            sb76.append("Alg.Alias.Mac.");
            sb76.append(NISTObjectIdentifiers.id_aes128_CCM.getId());
            configurableProvider.addAlgorithm(sb76.toString(), "AESCCMMAC");
            StringBuilder sb77 = new StringBuilder();
            sb77.append("Alg.Alias.Mac.");
            sb77.append(NISTObjectIdentifiers.id_aes192_CCM.getId());
            configurableProvider.addAlgorithm(sb77.toString(), "AESCCMMAC");
            StringBuilder sb78 = new StringBuilder();
            sb78.append("Alg.Alias.Mac.");
            sb78.append(NISTObjectIdentifiers.id_aes256_CCM.getId());
            configurableProvider.addAlgorithm(sb78.toString(), "AESCCMMAC");
            String str12 = "PBEWITHSHAAND128BITAES-CBC-BC";
            configurableProvider.addAlgorithm(str7, BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc, str12);
            String str13 = "PBEWITHSHAAND192BITAES-CBC-BC";
            configurableProvider.addAlgorithm(str7, BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc, str13);
            String str14 = "PBEWITHSHAAND256BITAES-CBC-BC";
            configurableProvider.addAlgorithm(str7, BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc, str14);
            configurableProvider.addAlgorithm(str7, BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc, "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm(str7, BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc, "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm(str7, BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc, "PBEWITHSHA256AND256BITAES-CBC-BC");
            StringBuilder sb79 = new StringBuilder();
            sb79.append(PREFIX);
            sb79.append("$PBEWithSHA1AESCBC128");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND128BITAES-CBC-BC", sb79.toString());
            StringBuilder sb80 = new StringBuilder();
            sb80.append(PREFIX);
            sb80.append("$PBEWithSHA1AESCBC192");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND192BITAES-CBC-BC", sb80.toString());
            StringBuilder sb81 = new StringBuilder();
            sb81.append(PREFIX);
            sb81.append("$PBEWithSHA1AESCBC256");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND256BITAES-CBC-BC", sb81.toString());
            StringBuilder sb82 = new StringBuilder();
            sb82.append(PREFIX);
            sb82.append("$PBEWithSHA256AESCBC128");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHA256AND128BITAES-CBC-BC", sb82.toString());
            StringBuilder sb83 = new StringBuilder();
            sb83.append(PREFIX);
            sb83.append("$PBEWithSHA256AESCBC192");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHA256AND192BITAES-CBC-BC", sb83.toString());
            StringBuilder sb84 = new StringBuilder();
            sb84.append(PREFIX);
            sb84.append("$PBEWithSHA256AESCBC256");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHA256AND256BITAES-CBC-BC", sb84.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITAES-CBC-BC", str12);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND192BITAES-CBC-BC", str13);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND256BITAES-CBC-BC", str14);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND128BITAES-CBC-BC", str12);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND192BITAES-CBC-BC", str13);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND256BITAES-CBC-BC", str14);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND128BITAES-BC", str12);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND192BITAES-BC", str13);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND256BITAES-BC", str14);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITAES-BC", str12);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND192BITAES-BC", str13);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND256BITAES-BC", str14);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND128BITAES-BC", str12);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND192BITAES-BC", str13);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND256BITAES-BC", str14);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND128BITAES-CBC-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND192BITAES-CBC-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND256BITAES-CBC-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            StringBuilder sb85 = new StringBuilder();
            sb85.append(PREFIX);
            sb85.append("$PBEWithAESCBC");
            configurableProvider.addAlgorithm("Cipher.PBEWITHMD5AND128BITAES-CBC-OPENSSL", sb85.toString());
            StringBuilder sb86 = new StringBuilder();
            sb86.append(PREFIX);
            sb86.append("$PBEWithAESCBC");
            configurableProvider.addAlgorithm("Cipher.PBEWITHMD5AND192BITAES-CBC-OPENSSL", sb86.toString());
            StringBuilder sb87 = new StringBuilder();
            sb87.append(PREFIX);
            sb87.append("$PBEWithAESCBC");
            configurableProvider.addAlgorithm("Cipher.PBEWITHMD5AND256BITAES-CBC-OPENSSL", sb87.toString());
            StringBuilder sb88 = new StringBuilder();
            sb88.append(PREFIX);
            sb88.append("$KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.AES", sb88.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier37 = NISTObjectIdentifiers.aes;
            StringBuilder sb89 = new StringBuilder();
            sb89.append(PREFIX);
            sb89.append("$KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory", aSN1ObjectIdentifier37, sb89.toString());
            StringBuilder sb90 = new StringBuilder();
            sb90.append(PREFIX);
            sb90.append("$PBEWithMD5And128BitAESCBCOpenSSL");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND128BITAES-CBC-OPENSSL", sb90.toString());
            StringBuilder sb91 = new StringBuilder();
            sb91.append(PREFIX);
            sb91.append("$PBEWithMD5And192BitAESCBCOpenSSL");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND192BITAES-CBC-OPENSSL", sb91.toString());
            StringBuilder sb92 = new StringBuilder();
            sb92.append(PREFIX);
            sb92.append("$PBEWithMD5And256BitAESCBCOpenSSL");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND256BITAES-CBC-OPENSSL", sb92.toString());
            StringBuilder sb93 = new StringBuilder();
            sb93.append(PREFIX);
            sb93.append("$PBEWithSHAAnd128BitAESBC");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND128BITAES-CBC-BC", sb93.toString());
            StringBuilder sb94 = new StringBuilder();
            sb94.append(PREFIX);
            sb94.append("$PBEWithSHAAnd192BitAESBC");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND192BITAES-CBC-BC", sb94.toString());
            StringBuilder sb95 = new StringBuilder();
            sb95.append(PREFIX);
            sb95.append("$PBEWithSHAAnd256BitAESBC");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND256BITAES-CBC-BC", sb95.toString());
            StringBuilder sb96 = new StringBuilder();
            sb96.append(PREFIX);
            sb96.append("$PBEWithSHA256And128BitAESBC");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND128BITAES-CBC-BC", sb96.toString());
            StringBuilder sb97 = new StringBuilder();
            sb97.append(PREFIX);
            sb97.append("$PBEWithSHA256And192BitAESBC");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND192BITAES-CBC-BC", sb97.toString());
            StringBuilder sb98 = new StringBuilder();
            sb98.append(PREFIX);
            sb98.append("$PBEWithSHA256And256BitAESBC");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND256BITAES-CBC-BC", sb98.toString());
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND128BITAES-CBC-BC", str12);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND192BITAES-CBC-BC", str13);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND256BITAES-CBC-BC", str14);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND128BITAES-CBC-BC", str12);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND192BITAES-CBC-BC", str13);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND256BITAES-CBC-BC", str14);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND128BITAES-CBC-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND192BITAES-CBC-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND256BITAES-CBC-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc, str12);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc, str13);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc, str14);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc, "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc, "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc, "PBEWITHSHA256AND256BITAES-CBC-BC");
            String str15 = "PKCS12PBE";
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND128BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND192BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND256BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND128BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND192BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND256BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND128BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND192BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND256BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND128BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND192BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND256BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND128BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND192BITAES-CBC-BC", str15);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND256BITAES-CBC-BC", str15);
            StringBuilder sb99 = new StringBuilder();
            sb99.append(str2);
            sb99.append(BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc.getId());
            configurableProvider.addAlgorithm(sb99.toString(), str15);
            StringBuilder sb100 = new StringBuilder();
            sb100.append(str2);
            sb100.append(BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc.getId());
            configurableProvider.addAlgorithm(sb100.toString(), str15);
            StringBuilder sb101 = new StringBuilder();
            sb101.append(str2);
            sb101.append(BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc.getId());
            configurableProvider.addAlgorithm(sb101.toString(), str15);
            StringBuilder sb102 = new StringBuilder();
            sb102.append(str2);
            sb102.append(BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc.getId());
            configurableProvider.addAlgorithm(sb102.toString(), str15);
            StringBuilder sb103 = new StringBuilder();
            sb103.append(str2);
            sb103.append(BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc.getId());
            configurableProvider.addAlgorithm(sb103.toString(), str15);
            StringBuilder sb104 = new StringBuilder();
            sb104.append(str2);
            sb104.append(BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc.getId());
            configurableProvider.addAlgorithm(sb104.toString(), str15);
            StringBuilder sb105 = new StringBuilder();
            sb105.append(PREFIX);
            sb105.append("$AESGMAC");
            String sb106 = sb105.toString();
            StringBuilder sb107 = new StringBuilder();
            sb107.append(PREFIX);
            sb107.append(str8);
            addGMacAlgorithm(configurableProvider, str, sb106, sb107.toString());
            StringBuilder sb108 = new StringBuilder();
            sb108.append(PREFIX);
            sb108.append("$Poly1305");
            String sb109 = sb108.toString();
            StringBuilder sb110 = new StringBuilder();
            sb110.append(PREFIX);
            sb110.append("$Poly1305KeyGen");
            addPoly1305Algorithm(configurableProvider, str, sb109, sb110.toString());
        }
    }

    public static class OFB extends BaseBlockCipher {
        public OFB() {
            super(new BufferedBlockCipher(new OFBBlockCipher(new AESEngine(), 128)), 128);
        }
    }

    public static class PBEWithAESCBC extends BaseBlockCipher {
        public PBEWithAESCBC() {
            super((BlockCipher) new CBCBlockCipher(new AESEngine()));
        }
    }

    public static class PBEWithMD5And128BitAESCBCOpenSSL extends PBESecretKeyFactory {
        public PBEWithMD5And128BitAESCBCOpenSSL() {
            super("PBEWithMD5And128BitAES-CBC-OpenSSL", null, true, 3, 0, 128, 128);
        }
    }

    public static class PBEWithMD5And192BitAESCBCOpenSSL extends PBESecretKeyFactory {
        public PBEWithMD5And192BitAESCBCOpenSSL() {
            super("PBEWithMD5And192BitAES-CBC-OpenSSL", null, true, 3, 0, 192, 128);
        }
    }

    public static class PBEWithMD5And256BitAESCBCOpenSSL extends PBESecretKeyFactory {
        public PBEWithMD5And256BitAESCBCOpenSSL() {
            super("PBEWithMD5And256BitAES-CBC-OpenSSL", null, true, 3, 0, 256, 128);
        }
    }

    public static class PBEWithSHA1AESCBC128 extends BaseBlockCipher {
        public PBEWithSHA1AESCBC128() {
            CBCBlockCipher cBCBlockCipher = new CBCBlockCipher(new AESEngine());
            super(cBCBlockCipher, 2, 1, 128, 16);
        }
    }

    public static class PBEWithSHA1AESCBC192 extends BaseBlockCipher {
        public PBEWithSHA1AESCBC192() {
            CBCBlockCipher cBCBlockCipher = new CBCBlockCipher(new AESEngine());
            super(cBCBlockCipher, 2, 1, 192, 16);
        }
    }

    public static class PBEWithSHA1AESCBC256 extends BaseBlockCipher {
        public PBEWithSHA1AESCBC256() {
            CBCBlockCipher cBCBlockCipher = new CBCBlockCipher(new AESEngine());
            super(cBCBlockCipher, 2, 1, 256, 16);
        }
    }

    public static class PBEWithSHA256AESCBC128 extends BaseBlockCipher {
        public PBEWithSHA256AESCBC128() {
            CBCBlockCipher cBCBlockCipher = new CBCBlockCipher(new AESEngine());
            super(cBCBlockCipher, 2, 4, 128, 16);
        }
    }

    public static class PBEWithSHA256AESCBC192 extends BaseBlockCipher {
        public PBEWithSHA256AESCBC192() {
            CBCBlockCipher cBCBlockCipher = new CBCBlockCipher(new AESEngine());
            super(cBCBlockCipher, 2, 4, 192, 16);
        }
    }

    public static class PBEWithSHA256AESCBC256 extends BaseBlockCipher {
        public PBEWithSHA256AESCBC256() {
            CBCBlockCipher cBCBlockCipher = new CBCBlockCipher(new AESEngine());
            super(cBCBlockCipher, 2, 4, 256, 16);
        }
    }

    public static class PBEWithSHA256And128BitAESBC extends PBESecretKeyFactory {
        public PBEWithSHA256And128BitAESBC() {
            super("PBEWithSHA256And128BitAES-CBC-BC", null, true, 2, 4, 128, 128);
        }
    }

    public static class PBEWithSHA256And192BitAESBC extends PBESecretKeyFactory {
        public PBEWithSHA256And192BitAESBC() {
            super("PBEWithSHA256And192BitAES-CBC-BC", null, true, 2, 4, 192, 128);
        }
    }

    public static class PBEWithSHA256And256BitAESBC extends PBESecretKeyFactory {
        public PBEWithSHA256And256BitAESBC() {
            super("PBEWithSHA256And256BitAES-CBC-BC", null, true, 2, 4, 256, 128);
        }
    }

    public static class PBEWithSHAAnd128BitAESBC extends PBESecretKeyFactory {
        public PBEWithSHAAnd128BitAESBC() {
            super("PBEWithSHA1And128BitAES-CBC-BC", null, true, 2, 1, 128, 128);
        }
    }

    public static class PBEWithSHAAnd192BitAESBC extends PBESecretKeyFactory {
        public PBEWithSHAAnd192BitAESBC() {
            super("PBEWithSHA1And192BitAES-CBC-BC", null, true, 2, 1, 192, 128);
        }
    }

    public static class PBEWithSHAAnd256BitAESBC extends PBESecretKeyFactory {
        public PBEWithSHAAnd256BitAESBC() {
            super("PBEWithSHA1And256BitAES-CBC-BC", null, true, 2, 1, 256, 128);
        }
    }

    public static class Poly1305 extends BaseMac {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(new AESEngine()));
        }
    }

    public static class Poly1305KeyGen extends BaseKeyGenerator {
        public Poly1305KeyGen() {
            super("Poly1305-AES", 256, new Poly1305KeyGenerator());
        }
    }

    public static class RFC3211Wrap extends BaseWrapCipher {
        public RFC3211Wrap() {
            super(new RFC3211WrapEngine(new AESEngine()), 16);
        }
    }

    public static class RFC5649Wrap extends BaseWrapCipher {
        public RFC5649Wrap() {
            super(new RFC5649WrapEngine(new AESEngine()));
        }
    }

    public static class Wrap extends BaseWrapCipher {
        public Wrap() {
            super(new AESWrapEngine());
        }
    }

    public static class WrapPad extends BaseWrapCipher {
        public WrapPad() {
            super(new AESWrapPadEngine());
        }
    }

    static {
        generalAesAttributes.put("SupportedKeyClasses", "javax.crypto.SecretKey");
        generalAesAttributes.put("SupportedKeyFormats", "RAW");
    }

    private AES() {
    }
}
