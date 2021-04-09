package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

public class Keccak {

    public static class Digest224 extends DigestKeccak {
        public Digest224() {
            super(224);
        }
    }

    public static class Digest256 extends DigestKeccak {
        public Digest256() {
            super(256);
        }
    }

    public static class Digest288 extends DigestKeccak {
        public Digest288() {
            super(288);
        }
    }

    public static class Digest384 extends DigestKeccak {
        public Digest384() {
            super(384);
        }
    }

    public static class Digest512 extends DigestKeccak {
        public Digest512() {
            super(512);
        }
    }

    public static class DigestKeccak extends BCMessageDigest implements Cloneable {
        public DigestKeccak(int i) {
            super(new KeccakDigest(i));
        }

        public Object clone() throws CloneNotSupportedException {
            BCMessageDigest bCMessageDigest = (BCMessageDigest) super.clone();
            bCMessageDigest.digest = new KeccakDigest((KeccakDigest) this.digest);
            return bCMessageDigest;
        }
    }

    public static class HashMac224 extends BaseMac {
        public HashMac224() {
            super(new HMac(new KeccakDigest(224)));
        }
    }

    public static class HashMac256 extends BaseMac {
        public HashMac256() {
            super(new HMac(new KeccakDigest(256)));
        }
    }

    public static class HashMac288 extends BaseMac {
        public HashMac288() {
            super(new HMac(new KeccakDigest(288)));
        }
    }

    public static class HashMac384 extends BaseMac {
        public HashMac384() {
            super(new HMac(new KeccakDigest(384)));
        }
    }

    public static class HashMac512 extends BaseMac {
        public HashMac512() {
            super(new HMac(new KeccakDigest(512)));
        }
    }

    public static class KeyGenerator224 extends BaseKeyGenerator {
        public KeyGenerator224() {
            super("HMACKECCAK224", 224, new CipherKeyGenerator());
        }
    }

    public static class KeyGenerator256 extends BaseKeyGenerator {
        public KeyGenerator256() {
            super("HMACKECCAK256", 256, new CipherKeyGenerator());
        }
    }

    public static class KeyGenerator288 extends BaseKeyGenerator {
        public KeyGenerator288() {
            super("HMACKECCAK288", 288, new CipherKeyGenerator());
        }
    }

    public static class KeyGenerator384 extends BaseKeyGenerator {
        public KeyGenerator384() {
            super("HMACKECCAK384", 384, new CipherKeyGenerator());
        }
    }

    public static class KeyGenerator512 extends BaseKeyGenerator {
        public KeyGenerator512() {
            super("HMACKECCAK512", 512, new CipherKeyGenerator());
        }
    }

    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = Keccak.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$Digest224");
            configurableProvider.addAlgorithm("MessageDigest.KECCAK-224", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            sb2.append("$Digest288");
            configurableProvider.addAlgorithm("MessageDigest.KECCAK-288", sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$Digest256");
            configurableProvider.addAlgorithm("MessageDigest.KECCAK-256", sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            sb4.append("$Digest384");
            configurableProvider.addAlgorithm("MessageDigest.KECCAK-384", sb4.toString());
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$Digest512");
            configurableProvider.addAlgorithm("MessageDigest.KECCAK-512", sb5.toString());
            StringBuilder sb6 = new StringBuilder();
            sb6.append(PREFIX);
            sb6.append("$HashMac224");
            String sb7 = sb6.toString();
            StringBuilder sb8 = new StringBuilder();
            sb8.append(PREFIX);
            sb8.append("$KeyGenerator224");
            addHMACAlgorithm(configurableProvider, "KECCAK224", sb7, sb8.toString());
            StringBuilder sb9 = new StringBuilder();
            sb9.append(PREFIX);
            sb9.append("$HashMac256");
            String sb10 = sb9.toString();
            StringBuilder sb11 = new StringBuilder();
            sb11.append(PREFIX);
            sb11.append("$KeyGenerator256");
            addHMACAlgorithm(configurableProvider, "KECCAK256", sb10, sb11.toString());
            StringBuilder sb12 = new StringBuilder();
            sb12.append(PREFIX);
            sb12.append("$HashMac288");
            String sb13 = sb12.toString();
            StringBuilder sb14 = new StringBuilder();
            sb14.append(PREFIX);
            sb14.append("$KeyGenerator288");
            addHMACAlgorithm(configurableProvider, "KECCAK288", sb13, sb14.toString());
            StringBuilder sb15 = new StringBuilder();
            sb15.append(PREFIX);
            sb15.append("$HashMac384");
            String sb16 = sb15.toString();
            StringBuilder sb17 = new StringBuilder();
            sb17.append(PREFIX);
            sb17.append("$KeyGenerator384");
            addHMACAlgorithm(configurableProvider, "KECCAK384", sb16, sb17.toString());
            StringBuilder sb18 = new StringBuilder();
            sb18.append(PREFIX);
            sb18.append("$HashMac512");
            String sb19 = sb18.toString();
            StringBuilder sb20 = new StringBuilder();
            sb20.append(PREFIX);
            sb20.append("$KeyGenerator512");
            addHMACAlgorithm(configurableProvider, "KECCAK512", sb19, sb20.toString());
        }
    }

    private Keccak() {
    }
}
