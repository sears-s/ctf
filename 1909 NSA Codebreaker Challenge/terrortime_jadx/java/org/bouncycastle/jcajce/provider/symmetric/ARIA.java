package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CCMParameters;
import org.bouncycastle.asn1.cms.GCMParameters;
import org.bouncycastle.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.ARIAEngine;
import org.bouncycastle.crypto.engines.ARIAWrapEngine;
import org.bouncycastle.crypto.engines.ARIAWrapPadEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
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
import org.bouncycastle.jcajce.spec.AEADParameterSpec;

public final class ARIA {

    public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
        /* access modifiers changed from: protected */
        public AlgorithmParameters engineGenerateParameters() {
            byte[] bArr = new byte[16];
            if (this.random == null) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.random.nextBytes(bArr);
            try {
                AlgorithmParameters createParametersInstance = createParametersInstance("ARIA");
                createParametersInstance.init(new IvParameterSpec(bArr));
                return createParametersInstance;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        /* access modifiers changed from: protected */
        public void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for ARIA parameter generation.");
        }
    }

    public static class AlgParams extends IvAlgorithmParameters {
        /* access modifiers changed from: protected */
        public String engineToString() {
            return "ARIA IV";
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
            super((BlockCipher) new CBCBlockCipher(new ARIAEngine()), 128);
        }
    }

    public static class CFB extends BaseBlockCipher {
        public CFB() {
            super(new BufferedBlockCipher(new CFBBlockCipher(new ARIAEngine(), 128)), 128);
        }
    }

    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super((BlockCipherProvider) new BlockCipherProvider() {
                public BlockCipher get() {
                    return new ARIAEngine();
                }
            });
        }
    }

    public static class GMAC extends BaseMac {
        public GMAC() {
            super(new GMac(new GCMBlockCipher(new ARIAEngine())));
        }
    }

    public static class KeyFactory extends BaseSecretKeyFactory {
        public KeyFactory() {
            super("ARIA", null);
        }
    }

    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            this(256);
        }

        public KeyGen(int i) {
            super("ARIA", i, new CipherKeyGenerator());
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
        private static final String PREFIX = ARIA.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$AlgParams");
            configurableProvider.addAlgorithm("AlgorithmParameters.ARIA", sb.toString());
            String str = "Alg.Alias.AlgorithmParameters";
            String str2 = "ARIA";
            configurableProvider.addAlgorithm(str, NSRIObjectIdentifiers.id_aria128_cbc, str2);
            configurableProvider.addAlgorithm(str, NSRIObjectIdentifiers.id_aria192_cbc, str2);
            configurableProvider.addAlgorithm(str, NSRIObjectIdentifiers.id_aria256_cbc, str2);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            sb2.append("$AlgParamGen");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.ARIA", sb2.toString());
            String str3 = "Alg.Alias.AlgorithmParameterGenerator";
            configurableProvider.addAlgorithm(str3, NSRIObjectIdentifiers.id_aria128_cbc, str2);
            configurableProvider.addAlgorithm(str3, NSRIObjectIdentifiers.id_aria192_cbc, str2);
            configurableProvider.addAlgorithm(str3, NSRIObjectIdentifiers.id_aria256_cbc, str2);
            configurableProvider.addAlgorithm(str3, NSRIObjectIdentifiers.id_aria128_ofb, str2);
            configurableProvider.addAlgorithm(str3, NSRIObjectIdentifiers.id_aria192_ofb, str2);
            configurableProvider.addAlgorithm(str3, NSRIObjectIdentifiers.id_aria256_ofb, str2);
            configurableProvider.addAlgorithm(str3, NSRIObjectIdentifiers.id_aria128_cfb, str2);
            configurableProvider.addAlgorithm(str3, NSRIObjectIdentifiers.id_aria192_cfb, str2);
            configurableProvider.addAlgorithm(str3, NSRIObjectIdentifiers.id_aria256_cfb, str2);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            String str4 = "$ECB";
            sb3.append(str4);
            configurableProvider.addAlgorithm("Cipher.ARIA", sb3.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier = NSRIObjectIdentifiers.id_aria128_ecb;
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            sb4.append(str4);
            String str5 = "Cipher";
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier, sb4.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier2 = NSRIObjectIdentifiers.id_aria192_ecb;
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append(str4);
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier2, sb5.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier3 = NSRIObjectIdentifiers.id_aria256_ecb;
            StringBuilder sb6 = new StringBuilder();
            sb6.append(PREFIX);
            sb6.append(str4);
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier3, sb6.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier4 = NSRIObjectIdentifiers.id_aria128_cbc;
            StringBuilder sb7 = new StringBuilder();
            sb7.append(PREFIX);
            sb7.append("$CBC");
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier4, sb7.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier5 = NSRIObjectIdentifiers.id_aria192_cbc;
            StringBuilder sb8 = new StringBuilder();
            sb8.append(PREFIX);
            sb8.append("$CBC");
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier5, sb8.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier6 = NSRIObjectIdentifiers.id_aria256_cbc;
            StringBuilder sb9 = new StringBuilder();
            sb9.append(PREFIX);
            sb9.append("$CBC");
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier6, sb9.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier7 = NSRIObjectIdentifiers.id_aria128_cfb;
            StringBuilder sb10 = new StringBuilder();
            sb10.append(PREFIX);
            sb10.append("$CFB");
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier7, sb10.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier8 = NSRIObjectIdentifiers.id_aria192_cfb;
            StringBuilder sb11 = new StringBuilder();
            sb11.append(PREFIX);
            sb11.append("$CFB");
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier8, sb11.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier9 = NSRIObjectIdentifiers.id_aria256_cfb;
            StringBuilder sb12 = new StringBuilder();
            sb12.append(PREFIX);
            sb12.append("$CFB");
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier9, sb12.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier10 = NSRIObjectIdentifiers.id_aria128_ofb;
            StringBuilder sb13 = new StringBuilder();
            sb13.append(PREFIX);
            sb13.append("$OFB");
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier10, sb13.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier11 = NSRIObjectIdentifiers.id_aria192_ofb;
            StringBuilder sb14 = new StringBuilder();
            sb14.append(PREFIX);
            sb14.append("$OFB");
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier11, sb14.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier12 = NSRIObjectIdentifiers.id_aria256_ofb;
            StringBuilder sb15 = new StringBuilder();
            sb15.append(PREFIX);
            sb15.append("$OFB");
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier12, sb15.toString());
            StringBuilder sb16 = new StringBuilder();
            sb16.append(PREFIX);
            sb16.append("$RFC3211Wrap");
            configurableProvider.addAlgorithm("Cipher.ARIARFC3211WRAP", sb16.toString());
            StringBuilder sb17 = new StringBuilder();
            sb17.append(PREFIX);
            sb17.append("$Wrap");
            configurableProvider.addAlgorithm("Cipher.ARIAWRAP", sb17.toString());
            String str6 = "ARIAWRAP";
            String str7 = "Alg.Alias.Cipher";
            configurableProvider.addAlgorithm(str7, NSRIObjectIdentifiers.id_aria128_kw, str6);
            configurableProvider.addAlgorithm(str7, NSRIObjectIdentifiers.id_aria192_kw, str6);
            configurableProvider.addAlgorithm(str7, NSRIObjectIdentifiers.id_aria256_kw, str6);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.ARIAKW", str6);
            StringBuilder sb18 = new StringBuilder();
            sb18.append(PREFIX);
            sb18.append("$WrapPad");
            configurableProvider.addAlgorithm("Cipher.ARIAWRAPPAD", sb18.toString());
            String str8 = "ARIAWRAPPAD";
            configurableProvider.addAlgorithm(str7, NSRIObjectIdentifiers.id_aria128_kwp, str8);
            configurableProvider.addAlgorithm(str7, NSRIObjectIdentifiers.id_aria192_kwp, str8);
            configurableProvider.addAlgorithm(str7, NSRIObjectIdentifiers.id_aria256_kwp, str8);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.ARIAKWP", str8);
            StringBuilder sb19 = new StringBuilder();
            sb19.append(PREFIX);
            sb19.append("$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator.ARIA", sb19.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier13 = NSRIObjectIdentifiers.id_aria128_kw;
            StringBuilder sb20 = new StringBuilder();
            sb20.append(PREFIX);
            String str9 = "$KeyGen128";
            sb20.append(str9);
            String str10 = "KeyGenerator";
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier13, sb20.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier14 = NSRIObjectIdentifiers.id_aria192_kw;
            StringBuilder sb21 = new StringBuilder();
            sb21.append(PREFIX);
            String str11 = "$KeyGen192";
            sb21.append(str11);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier14, sb21.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier15 = NSRIObjectIdentifiers.id_aria256_kw;
            StringBuilder sb22 = new StringBuilder();
            sb22.append(PREFIX);
            String str12 = "$KeyGen256";
            sb22.append(str12);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier15, sb22.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier16 = NSRIObjectIdentifiers.id_aria128_kwp;
            StringBuilder sb23 = new StringBuilder();
            sb23.append(PREFIX);
            sb23.append(str9);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier16, sb23.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier17 = NSRIObjectIdentifiers.id_aria192_kwp;
            StringBuilder sb24 = new StringBuilder();
            sb24.append(PREFIX);
            sb24.append(str11);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier17, sb24.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier18 = NSRIObjectIdentifiers.id_aria256_kwp;
            StringBuilder sb25 = new StringBuilder();
            sb25.append(PREFIX);
            sb25.append(str12);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier18, sb25.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier19 = NSRIObjectIdentifiers.id_aria128_ecb;
            StringBuilder sb26 = new StringBuilder();
            sb26.append(PREFIX);
            sb26.append(str9);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier19, sb26.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier20 = NSRIObjectIdentifiers.id_aria192_ecb;
            StringBuilder sb27 = new StringBuilder();
            sb27.append(PREFIX);
            sb27.append(str11);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier20, sb27.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier21 = NSRIObjectIdentifiers.id_aria256_ecb;
            StringBuilder sb28 = new StringBuilder();
            sb28.append(PREFIX);
            sb28.append(str12);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier21, sb28.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier22 = NSRIObjectIdentifiers.id_aria128_cbc;
            StringBuilder sb29 = new StringBuilder();
            sb29.append(PREFIX);
            sb29.append(str9);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier22, sb29.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier23 = NSRIObjectIdentifiers.id_aria192_cbc;
            StringBuilder sb30 = new StringBuilder();
            sb30.append(PREFIX);
            sb30.append(str11);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier23, sb30.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier24 = NSRIObjectIdentifiers.id_aria256_cbc;
            StringBuilder sb31 = new StringBuilder();
            sb31.append(PREFIX);
            sb31.append(str12);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier24, sb31.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier25 = NSRIObjectIdentifiers.id_aria128_cfb;
            StringBuilder sb32 = new StringBuilder();
            sb32.append(PREFIX);
            sb32.append(str9);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier25, sb32.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier26 = NSRIObjectIdentifiers.id_aria192_cfb;
            StringBuilder sb33 = new StringBuilder();
            sb33.append(PREFIX);
            sb33.append(str11);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier26, sb33.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier27 = NSRIObjectIdentifiers.id_aria256_cfb;
            StringBuilder sb34 = new StringBuilder();
            sb34.append(PREFIX);
            sb34.append(str12);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier27, sb34.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier28 = NSRIObjectIdentifiers.id_aria128_ofb;
            StringBuilder sb35 = new StringBuilder();
            sb35.append(PREFIX);
            sb35.append(str9);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier28, sb35.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier29 = NSRIObjectIdentifiers.id_aria192_ofb;
            StringBuilder sb36 = new StringBuilder();
            sb36.append(PREFIX);
            sb36.append(str11);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier29, sb36.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier30 = NSRIObjectIdentifiers.id_aria256_ofb;
            StringBuilder sb37 = new StringBuilder();
            sb37.append(PREFIX);
            sb37.append(str12);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier30, sb37.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier31 = NSRIObjectIdentifiers.id_aria128_ccm;
            StringBuilder sb38 = new StringBuilder();
            sb38.append(PREFIX);
            sb38.append(str9);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier31, sb38.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier32 = NSRIObjectIdentifiers.id_aria192_ccm;
            StringBuilder sb39 = new StringBuilder();
            sb39.append(PREFIX);
            sb39.append(str11);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier32, sb39.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier33 = NSRIObjectIdentifiers.id_aria256_ccm;
            StringBuilder sb40 = new StringBuilder();
            sb40.append(PREFIX);
            sb40.append(str12);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier33, sb40.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier34 = NSRIObjectIdentifiers.id_aria128_gcm;
            StringBuilder sb41 = new StringBuilder();
            sb41.append(PREFIX);
            sb41.append(str9);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier34, sb41.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier35 = NSRIObjectIdentifiers.id_aria192_gcm;
            StringBuilder sb42 = new StringBuilder();
            sb42.append(PREFIX);
            sb42.append(str11);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier35, sb42.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier36 = NSRIObjectIdentifiers.id_aria256_gcm;
            StringBuilder sb43 = new StringBuilder();
            sb43.append(PREFIX);
            sb43.append(str12);
            configurableProvider.addAlgorithm(str10, aSN1ObjectIdentifier36, sb43.toString());
            StringBuilder sb44 = new StringBuilder();
            sb44.append(PREFIX);
            sb44.append("$KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.ARIA", sb44.toString());
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", NSRIObjectIdentifiers.id_aria128_cbc, str2);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", NSRIObjectIdentifiers.id_aria192_cbc, str2);
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", NSRIObjectIdentifiers.id_aria256_cbc, str2);
            StringBuilder sb45 = new StringBuilder();
            sb45.append(PREFIX);
            sb45.append("$AlgParamGenCCM");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.ARIACCM", sb45.toString());
            StringBuilder sb46 = new StringBuilder();
            String str13 = "Alg.Alias.AlgorithmParameterGenerator.";
            sb46.append(str13);
            sb46.append(NSRIObjectIdentifiers.id_aria128_ccm);
            String str14 = "CCM";
            configurableProvider.addAlgorithm(sb46.toString(), str14);
            StringBuilder sb47 = new StringBuilder();
            sb47.append(str13);
            sb47.append(NSRIObjectIdentifiers.id_aria192_ccm);
            configurableProvider.addAlgorithm(sb47.toString(), str14);
            StringBuilder sb48 = new StringBuilder();
            sb48.append(str13);
            sb48.append(NSRIObjectIdentifiers.id_aria256_ccm);
            configurableProvider.addAlgorithm(sb48.toString(), str14);
            configurableProvider.addAlgorithm(str7, NSRIObjectIdentifiers.id_aria128_ccm, str14);
            configurableProvider.addAlgorithm(str7, NSRIObjectIdentifiers.id_aria192_ccm, str14);
            configurableProvider.addAlgorithm(str7, NSRIObjectIdentifiers.id_aria256_ccm, str14);
            StringBuilder sb49 = new StringBuilder();
            sb49.append(PREFIX);
            sb49.append("$AlgParamGenGCM");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.ARIAGCM", sb49.toString());
            StringBuilder sb50 = new StringBuilder();
            sb50.append(str13);
            sb50.append(NSRIObjectIdentifiers.id_aria128_gcm);
            String str15 = "GCM";
            configurableProvider.addAlgorithm(sb50.toString(), str15);
            StringBuilder sb51 = new StringBuilder();
            sb51.append(str13);
            sb51.append(NSRIObjectIdentifiers.id_aria192_gcm);
            configurableProvider.addAlgorithm(sb51.toString(), str15);
            StringBuilder sb52 = new StringBuilder();
            sb52.append(str13);
            sb52.append(NSRIObjectIdentifiers.id_aria256_gcm);
            configurableProvider.addAlgorithm(sb52.toString(), str15);
            configurableProvider.addAlgorithm(str7, NSRIObjectIdentifiers.id_aria128_gcm, str15);
            configurableProvider.addAlgorithm(str7, NSRIObjectIdentifiers.id_aria192_gcm, str15);
            configurableProvider.addAlgorithm(str7, NSRIObjectIdentifiers.id_aria256_gcm, str15);
            StringBuilder sb53 = new StringBuilder();
            sb53.append(PREFIX);
            sb53.append("$GMAC");
            String sb54 = sb53.toString();
            StringBuilder sb55 = new StringBuilder();
            sb55.append(PREFIX);
            sb55.append("$KeyGen");
            addGMacAlgorithm(configurableProvider, str2, sb54, sb55.toString());
            StringBuilder sb56 = new StringBuilder();
            sb56.append(PREFIX);
            sb56.append("$Poly1305");
            String sb57 = sb56.toString();
            StringBuilder sb58 = new StringBuilder();
            sb58.append(PREFIX);
            sb58.append("$Poly1305KeyGen");
            addPoly1305Algorithm(configurableProvider, str2, sb57, sb58.toString());
        }
    }

    public static class OFB extends BaseBlockCipher {
        public OFB() {
            super(new BufferedBlockCipher(new OFBBlockCipher(new ARIAEngine(), 128)), 128);
        }
    }

    public static class Poly1305 extends BaseMac {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(new ARIAEngine()));
        }
    }

    public static class Poly1305KeyGen extends BaseKeyGenerator {
        public Poly1305KeyGen() {
            super("Poly1305-ARIA", 256, new Poly1305KeyGenerator());
        }
    }

    public static class RFC3211Wrap extends BaseWrapCipher {
        public RFC3211Wrap() {
            super(new RFC3211WrapEngine(new ARIAEngine()), 16);
        }
    }

    public static class Wrap extends BaseWrapCipher {
        public Wrap() {
            super(new ARIAWrapEngine());
        }
    }

    public static class WrapPad extends BaseWrapCipher {
        public WrapPad() {
            super(new ARIAWrapPadEngine());
        }
    }

    private ARIA() {
    }
}
