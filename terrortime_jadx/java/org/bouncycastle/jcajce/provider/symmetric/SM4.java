package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;

public final class SM4 {

    public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
        /* access modifiers changed from: protected */
        public AlgorithmParameters engineGenerateParameters() {
            byte[] bArr = new byte[16];
            if (this.random == null) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.random.nextBytes(bArr);
            try {
                AlgorithmParameters createParametersInstance = createParametersInstance("SM4");
                createParametersInstance.init(new IvParameterSpec(bArr));
                return createParametersInstance;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        /* access modifiers changed from: protected */
        public void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for SM4 parameter generation.");
        }
    }

    public static class AlgParams extends IvAlgorithmParameters {
        /* access modifiers changed from: protected */
        public String engineToString() {
            return "SM4 IV";
        }
    }

    public static class CMAC extends BaseMac {
        public CMAC() {
            super(new CMac(new SM4Engine()));
        }
    }

    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super((BlockCipherProvider) new BlockCipherProvider() {
                public BlockCipher get() {
                    return new SM4Engine();
                }
            });
        }
    }

    public static class GMAC extends BaseMac {
        public GMAC() {
            super(new GMac(new GCMBlockCipher(new SM4Engine())));
        }
    }

    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("SM4", 128, new CipherKeyGenerator());
        }
    }

    public static class Mappings extends SymmetricAlgorithmProvider {
        private static final String PREFIX = SM4.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$AlgParams");
            configurableProvider.addAlgorithm("AlgorithmParameters.SM4", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            sb2.append("$AlgParamGen");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.SM4", sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$ECB");
            configurableProvider.addAlgorithm("Cipher.SM4", sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            String str = "$KeyGen";
            sb4.append(str);
            configurableProvider.addAlgorithm("KeyGenerator.SM4", sb4.toString());
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$CMAC");
            String sb6 = sb5.toString();
            StringBuilder sb7 = new StringBuilder();
            sb7.append(PREFIX);
            sb7.append(str);
            String str2 = "SM4";
            addCMacAlgorithm(configurableProvider, str2, sb6, sb7.toString());
            StringBuilder sb8 = new StringBuilder();
            sb8.append(PREFIX);
            sb8.append("$GMAC");
            String sb9 = sb8.toString();
            StringBuilder sb10 = new StringBuilder();
            sb10.append(PREFIX);
            sb10.append(str);
            addGMacAlgorithm(configurableProvider, str2, sb9, sb10.toString());
            StringBuilder sb11 = new StringBuilder();
            sb11.append(PREFIX);
            sb11.append("$Poly1305");
            String sb12 = sb11.toString();
            StringBuilder sb13 = new StringBuilder();
            sb13.append(PREFIX);
            sb13.append("$Poly1305KeyGen");
            addPoly1305Algorithm(configurableProvider, str2, sb12, sb13.toString());
        }
    }

    public static class Poly1305 extends BaseMac {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(new SM4Engine()));
        }
    }

    public static class Poly1305KeyGen extends BaseKeyGenerator {
        public Poly1305KeyGen() {
            super("Poly1305-SM4", 256, new Poly1305KeyGenerator());
        }
    }

    private SM4() {
    }
}
