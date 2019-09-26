package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.GOST3412_2015Engine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.modes.G3413CBCBlockCipher;
import org.bouncycastle.crypto.modes.G3413CFBBlockCipher;
import org.bouncycastle.crypto.modes.G3413CTRBlockCipher;
import org.bouncycastle.crypto.modes.G3413OFBBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public class GOST3412_2015 {

    public static class CBC extends BaseBlockCipher {
        public CBC() {
            super((BlockCipher) new G3413CBCBlockCipher(new GOST3412_2015Engine()), false, 128);
        }
    }

    public static class CTR extends BaseBlockCipher {
        public CTR() {
            super(new BufferedBlockCipher(new G3413CTRBlockCipher(new GOST3412_2015Engine())), 128);
        }
    }

    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super((BlockCipher) new GOST3412_2015Engine());
        }
    }

    public static class GCFB extends BaseBlockCipher {
        public GCFB() {
            super(new BufferedBlockCipher(new G3413CFBBlockCipher(new GOST3412_2015Engine())), false, 128);
        }
    }

    public static class GCFB8 extends BaseBlockCipher {
        public GCFB8() {
            super(new BufferedBlockCipher(new G3413CFBBlockCipher(new GOST3412_2015Engine(), 8)), false, 128);
        }
    }

    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            this(256);
        }

        public KeyGen(int i) {
            super("GOST3412-2015", i, new CipherKeyGenerator());
        }
    }

    public static class Mac extends BaseMac {
        public Mac() {
            super(new CMac(new GOST3412_2015Engine()));
        }
    }

    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = GOST3412_2015.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$ECB");
            configurableProvider.addAlgorithm("Cipher.GOST3412-2015", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            sb2.append("$GCFB");
            configurableProvider.addAlgorithm("Cipher.GOST3412-2015/CFB", sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$GCFB8");
            configurableProvider.addAlgorithm("Cipher.GOST3412-2015/CFB8", sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            sb4.append("$OFB");
            configurableProvider.addAlgorithm("Cipher.GOST3412-2015/OFB", sb4.toString());
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$CBC");
            configurableProvider.addAlgorithm("Cipher.GOST3412-2015/CBC", sb5.toString());
            StringBuilder sb6 = new StringBuilder();
            sb6.append(PREFIX);
            sb6.append("$CTR");
            configurableProvider.addAlgorithm("Cipher.GOST3412-2015/CTR", sb6.toString());
            StringBuilder sb7 = new StringBuilder();
            sb7.append(PREFIX);
            sb7.append("$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator.GOST3412-2015", sb7.toString());
            StringBuilder sb8 = new StringBuilder();
            sb8.append(PREFIX);
            sb8.append("$Mac");
            configurableProvider.addAlgorithm("Mac.GOST3412MAC", sb8.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Mac.GOST3412-2015", "GOST3412MAC");
        }
    }

    public static class OFB extends BaseBlockCipher {
        public OFB() {
            super(new BufferedBlockCipher(new G3413OFBBlockCipher(new GOST3412_2015Engine())), false, 128);
        }
    }
}
