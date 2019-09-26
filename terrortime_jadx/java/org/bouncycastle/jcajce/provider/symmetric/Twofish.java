package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;

public final class Twofish {

    public static class AlgParams extends IvAlgorithmParameters {
        /* access modifiers changed from: protected */
        public String engineToString() {
            return "Twofish IV";
        }
    }

    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super((BlockCipherProvider) new BlockCipherProvider() {
                public BlockCipher get() {
                    return new TwofishEngine();
                }
            });
        }
    }

    public static class GMAC extends BaseMac {
        public GMAC() {
            super(new GMac(new GCMBlockCipher(new TwofishEngine())));
        }
    }

    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("Twofish", 256, new CipherKeyGenerator());
        }
    }

    public static class Mappings extends SymmetricAlgorithmProvider {
        private static final String PREFIX = Twofish.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$ECB");
            configurableProvider.addAlgorithm("Cipher.Twofish", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            String str = "$KeyGen";
            sb2.append(str);
            configurableProvider.addAlgorithm("KeyGenerator.Twofish", sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$AlgParams");
            configurableProvider.addAlgorithm("AlgorithmParameters.Twofish", sb3.toString());
            String str2 = "PKCS12PBE";
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDTWOFISH", str2);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDTWOFISH-CBC", str2);
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            sb4.append("$PBEWithSHA");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHAANDTWOFISH-CBC", sb4.toString());
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$PBEWithSHAKeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAANDTWOFISH-CBC", sb5.toString());
            StringBuilder sb6 = new StringBuilder();
            sb6.append(PREFIX);
            sb6.append("$GMAC");
            String sb7 = sb6.toString();
            StringBuilder sb8 = new StringBuilder();
            sb8.append(PREFIX);
            sb8.append(str);
            String sb9 = sb8.toString();
            String str3 = "Twofish";
            addGMacAlgorithm(configurableProvider, str3, sb7, sb9);
            StringBuilder sb10 = new StringBuilder();
            sb10.append(PREFIX);
            sb10.append("$Poly1305");
            String sb11 = sb10.toString();
            StringBuilder sb12 = new StringBuilder();
            sb12.append(PREFIX);
            sb12.append("$Poly1305KeyGen");
            addPoly1305Algorithm(configurableProvider, str3, sb11, sb12.toString());
        }
    }

    public static class PBEWithSHA extends BaseBlockCipher {
        public PBEWithSHA() {
            CBCBlockCipher cBCBlockCipher = new CBCBlockCipher(new TwofishEngine());
            super(cBCBlockCipher, 2, 1, 256, 16);
        }
    }

    public static class PBEWithSHAKeyFactory extends PBESecretKeyFactory {
        public PBEWithSHAKeyFactory() {
            super("PBEwithSHAandTwofish-CBC", null, true, 2, 1, 256, 128);
        }
    }

    public static class Poly1305 extends BaseMac {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(new TwofishEngine()));
        }
    }

    public static class Poly1305KeyGen extends BaseKeyGenerator {
        public Poly1305KeyGen() {
            super("Poly1305-Twofish", 256, new Poly1305KeyGenerator());
        }
    }

    private Twofish() {
    }
}
