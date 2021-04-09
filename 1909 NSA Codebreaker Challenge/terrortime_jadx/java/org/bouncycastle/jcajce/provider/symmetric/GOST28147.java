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
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.CryptoProWrapEngine;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.engines.GOST28147WrapEngine;
import org.bouncycastle.crypto.macs.GOST28147Mac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;

public final class GOST28147 {
    /* access modifiers changed from: private */
    public static Map<String, ASN1ObjectIdentifier> nameMappings = new HashMap();
    private static Map<ASN1ObjectIdentifier, String> oidMappings = new HashMap();

    public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
        byte[] iv = new byte[8];
        byte[] sBox = GOST28147Engine.getSBox("E-A");

        /* access modifiers changed from: protected */
        public AlgorithmParameters engineGenerateParameters() {
            if (this.random == null) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.random.nextBytes(this.iv);
            try {
                AlgorithmParameters createParametersInstance = createParametersInstance("GOST28147");
                createParametersInstance.init(new GOST28147ParameterSpec(this.sBox, this.iv));
                return createParametersInstance;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        /* access modifiers changed from: protected */
        public void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            if (algorithmParameterSpec instanceof GOST28147ParameterSpec) {
                this.sBox = ((GOST28147ParameterSpec) algorithmParameterSpec).getSBox();
                return;
            }
            throw new InvalidAlgorithmParameterException("parameter spec not supported");
        }
    }

    public static class AlgParams extends BaseAlgParams {
        private byte[] iv;
        private ASN1ObjectIdentifier sBox = CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet;

        /* access modifiers changed from: protected */
        public void engineInit(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (algorithmParameterSpec instanceof IvParameterSpec) {
                this.iv = ((IvParameterSpec) algorithmParameterSpec).getIV();
            } else if (algorithmParameterSpec instanceof GOST28147ParameterSpec) {
                this.iv = ((GOST28147ParameterSpec) algorithmParameterSpec).getIV();
                try {
                    this.sBox = getSBoxOID(((GOST28147ParameterSpec) algorithmParameterSpec).getSBox());
                } catch (IllegalArgumentException e) {
                    throw new InvalidParameterSpecException(e.getMessage());
                }
            } else {
                throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object");
            }
        }

        /* access modifiers changed from: protected */
        public String engineToString() {
            return "GOST 28147 IV Parameters";
        }

        /* access modifiers changed from: protected */
        public AlgorithmParameterSpec localEngineGetParameterSpec(Class cls) throws InvalidParameterSpecException {
            if (cls == IvParameterSpec.class) {
                return new IvParameterSpec(this.iv);
            }
            if (cls == GOST28147ParameterSpec.class || cls == AlgorithmParameterSpec.class) {
                return new GOST28147ParameterSpec(this.sBox, this.iv);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("AlgorithmParameterSpec not recognized: ");
            sb.append(cls.getName());
            throw new InvalidParameterSpecException(sb.toString());
        }

        /* access modifiers changed from: protected */
        public byte[] localGetEncoded() throws IOException {
            return new GOST28147Parameters(this.iv, this.sBox).getEncoded();
        }

        /* access modifiers changed from: protected */
        public void localInit(byte[] bArr) throws IOException {
            ASN1Primitive fromByteArray = ASN1Primitive.fromByteArray(bArr);
            if (fromByteArray instanceof ASN1OctetString) {
                this.iv = ASN1OctetString.getInstance(fromByteArray).getOctets();
            } else if (fromByteArray instanceof ASN1Sequence) {
                GOST28147Parameters instance = GOST28147Parameters.getInstance(fromByteArray);
                this.sBox = instance.getEncryptionParamSet();
                this.iv = instance.getIV();
            } else {
                throw new IOException("Unable to recognize parameters");
            }
        }
    }

    public static abstract class BaseAlgParams extends BaseAlgorithmParameters {
        private byte[] iv;
        private ASN1ObjectIdentifier sBox = CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet;

        protected static ASN1ObjectIdentifier getSBoxOID(String str) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) GOST28147.nameMappings.get(str);
            if (aSN1ObjectIdentifier != null) {
                return aSN1ObjectIdentifier;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Unknown SBOX name: ");
            sb.append(str);
            throw new IllegalArgumentException(sb.toString());
        }

        protected static ASN1ObjectIdentifier getSBoxOID(byte[] bArr) {
            return getSBoxOID(GOST28147Engine.getSBoxName(bArr));
        }

        /* access modifiers changed from: protected */
        public final byte[] engineGetEncoded() throws IOException {
            return engineGetEncoded("ASN.1");
        }

        /* access modifiers changed from: protected */
        public final byte[] engineGetEncoded(String str) throws IOException {
            if (isASN1FormatString(str)) {
                return localGetEncoded();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Unknown parameter format: ");
            sb.append(str);
            throw new IOException(sb.toString());
        }

        /* access modifiers changed from: protected */
        public void engineInit(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (algorithmParameterSpec instanceof IvParameterSpec) {
                this.iv = ((IvParameterSpec) algorithmParameterSpec).getIV();
            } else if (algorithmParameterSpec instanceof GOST28147ParameterSpec) {
                this.iv = ((GOST28147ParameterSpec) algorithmParameterSpec).getIV();
                try {
                    this.sBox = getSBoxOID(((GOST28147ParameterSpec) algorithmParameterSpec).getSBox());
                } catch (IllegalArgumentException e) {
                    throw new InvalidParameterSpecException(e.getMessage());
                }
            } else {
                throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object");
            }
        }

        /* access modifiers changed from: protected */
        public final void engineInit(byte[] bArr) throws IOException {
            engineInit(bArr, "ASN.1");
        }

        /* access modifiers changed from: protected */
        public final void engineInit(byte[] bArr, String str) throws IOException {
            if (bArr == null) {
                throw new NullPointerException("Encoded parameters cannot be null");
            } else if (isASN1FormatString(str)) {
                try {
                    localInit(bArr);
                } catch (IOException e) {
                    throw e;
                } catch (Exception e2) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Parameter parsing failed: ");
                    sb.append(e2.getMessage());
                    throw new IOException(sb.toString());
                }
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Unknown parameter format: ");
                sb2.append(str);
                throw new IOException(sb2.toString());
            }
        }

        /* access modifiers changed from: protected */
        public AlgorithmParameterSpec localEngineGetParameterSpec(Class cls) throws InvalidParameterSpecException {
            if (cls == IvParameterSpec.class) {
                return new IvParameterSpec(this.iv);
            }
            if (cls == GOST28147ParameterSpec.class || cls == AlgorithmParameterSpec.class) {
                return new GOST28147ParameterSpec(this.sBox, this.iv);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("AlgorithmParameterSpec not recognized: ");
            sb.append(cls.getName());
            throw new InvalidParameterSpecException(sb.toString());
        }

        /* access modifiers changed from: protected */
        public byte[] localGetEncoded() throws IOException {
            return new GOST28147Parameters(this.iv, this.sBox).getEncoded();
        }

        /* access modifiers changed from: 0000 */
        public abstract void localInit(byte[] bArr) throws IOException;
    }

    public static class CBC extends BaseBlockCipher {
        public CBC() {
            super((BlockCipher) new CBCBlockCipher(new GOST28147Engine()), 64);
        }
    }

    public static class CryptoProWrap extends BaseWrapCipher {
        public CryptoProWrap() {
            super(new CryptoProWrapEngine());
        }
    }

    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super((BlockCipher) new GOST28147Engine());
        }
    }

    public static class GCFB extends BaseBlockCipher {
        public GCFB() {
            super(new BufferedBlockCipher(new GCFBBlockCipher(new GOST28147Engine())), 64);
        }
    }

    public static class GostWrap extends BaseWrapCipher {
        public GostWrap() {
            super(new GOST28147WrapEngine());
        }
    }

    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            this(256);
        }

        public KeyGen(int i) {
            super("GOST28147", i, new CipherKeyGenerator());
        }
    }

    public static class Mac extends BaseMac {
        public Mac() {
            super(new GOST28147Mac());
        }
    }

    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = GOST28147.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$ECB");
            configurableProvider.addAlgorithm("Cipher.GOST28147", sb.toString());
            String str = "GOST28147";
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.GOST", str);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.GOST-28147", str);
            StringBuilder sb2 = new StringBuilder();
            String str2 = "Cipher.";
            sb2.append(str2);
            sb2.append(CryptoProObjectIdentifiers.gostR28147_gcfb);
            String sb3 = sb2.toString();
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            sb4.append("$GCFB");
            configurableProvider.addAlgorithm(sb3, sb4.toString());
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator.GOST28147", sb5.toString());
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.GOST", str);
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.GOST-28147", str);
            StringBuilder sb6 = new StringBuilder();
            sb6.append("Alg.Alias.KeyGenerator.");
            sb6.append(CryptoProObjectIdentifiers.gostR28147_gcfb);
            configurableProvider.addAlgorithm(sb6.toString(), str);
            StringBuilder sb7 = new StringBuilder();
            sb7.append(PREFIX);
            sb7.append("$AlgParams");
            configurableProvider.addAlgorithm("AlgorithmParameters.GOST28147", sb7.toString());
            StringBuilder sb8 = new StringBuilder();
            sb8.append(PREFIX);
            sb8.append("$AlgParamGen");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.GOST28147", sb8.toString());
            StringBuilder sb9 = new StringBuilder();
            sb9.append("Alg.Alias.AlgorithmParameters.");
            sb9.append(CryptoProObjectIdentifiers.gostR28147_gcfb);
            configurableProvider.addAlgorithm(sb9.toString(), str);
            StringBuilder sb10 = new StringBuilder();
            sb10.append("Alg.Alias.AlgorithmParameterGenerator.");
            sb10.append(CryptoProObjectIdentifiers.gostR28147_gcfb);
            configurableProvider.addAlgorithm(sb10.toString(), str);
            StringBuilder sb11 = new StringBuilder();
            sb11.append(str2);
            sb11.append(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap);
            String sb12 = sb11.toString();
            StringBuilder sb13 = new StringBuilder();
            sb13.append(PREFIX);
            sb13.append("$CryptoProWrap");
            configurableProvider.addAlgorithm(sb12, sb13.toString());
            StringBuilder sb14 = new StringBuilder();
            sb14.append(str2);
            sb14.append(CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap);
            String sb15 = sb14.toString();
            StringBuilder sb16 = new StringBuilder();
            sb16.append(PREFIX);
            sb16.append("$GostWrap");
            configurableProvider.addAlgorithm(sb15, sb16.toString());
            StringBuilder sb17 = new StringBuilder();
            sb17.append(PREFIX);
            sb17.append("$Mac");
            configurableProvider.addAlgorithm("Mac.GOST28147MAC", sb17.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Mac.GOST28147", "GOST28147MAC");
        }
    }

    static {
        oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_TestParamSet, "E-TEST");
        String str = "E-A";
        oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, str);
        String str2 = "E-B";
        oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_B_ParamSet, str2);
        String str3 = "E-C";
        oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_C_ParamSet, str3);
        String str4 = "E-D";
        oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_D_ParamSet, str4);
        String str5 = "Param-Z";
        oidMappings.put(RosstandartObjectIdentifiers.id_tc26_gost_28147_param_Z, str5);
        nameMappings.put(str, CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet);
        nameMappings.put(str2, CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_B_ParamSet);
        nameMappings.put(str3, CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_C_ParamSet);
        nameMappings.put(str4, CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_D_ParamSet);
        nameMappings.put(str5, RosstandartObjectIdentifiers.id_tc26_gost_28147_param_Z);
    }

    private GOST28147() {
    }
}
