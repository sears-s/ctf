package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class SipHash {

    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("SipHash", 128, new CipherKeyGenerator());
        }
    }

    public static class Mac24 extends BaseMac {
        public Mac24() {
            super(new org.bouncycastle.crypto.macs.SipHash());
        }
    }

    public static class Mac48 extends BaseMac {
        public Mac48() {
            super(new org.bouncycastle.crypto.macs.SipHash(4, 8));
        }
    }

    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = SipHash.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$Mac24");
            configurableProvider.addAlgorithm("Mac.SIPHASH-2-4", sb.toString());
            configurableProvider.addAlgorithm("Alg.Alias.Mac.SIPHASH", "SIPHASH-2-4");
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            sb2.append("$Mac48");
            configurableProvider.addAlgorithm("Mac.SIPHASH-4-8", sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator.SIPHASH", sb3.toString());
            String str = "SIPHASH";
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.SIPHASH-2-4", str);
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.SIPHASH-4-8", str);
        }
    }

    private SipHash() {
    }
}
