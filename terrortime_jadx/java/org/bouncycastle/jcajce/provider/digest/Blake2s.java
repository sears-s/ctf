package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.crypto.digests.Blake2sDigest;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;

public class Blake2s {

    public static class Blake2s128 extends BCMessageDigest implements Cloneable {
        public Blake2s128() {
            super(new Blake2sDigest(128));
        }

        public Object clone() throws CloneNotSupportedException {
            Blake2s128 blake2s128 = (Blake2s128) super.clone();
            blake2s128.digest = new Blake2sDigest((Blake2sDigest) this.digest);
            return blake2s128;
        }
    }

    public static class Blake2s160 extends BCMessageDigest implements Cloneable {
        public Blake2s160() {
            super(new Blake2sDigest((int) CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256));
        }

        public Object clone() throws CloneNotSupportedException {
            Blake2s160 blake2s160 = (Blake2s160) super.clone();
            blake2s160.digest = new Blake2sDigest((Blake2sDigest) this.digest);
            return blake2s160;
        }
    }

    public static class Blake2s224 extends BCMessageDigest implements Cloneable {
        public Blake2s224() {
            super(new Blake2sDigest(224));
        }

        public Object clone() throws CloneNotSupportedException {
            Blake2s224 blake2s224 = (Blake2s224) super.clone();
            blake2s224.digest = new Blake2sDigest((Blake2sDigest) this.digest);
            return blake2s224;
        }
    }

    public static class Blake2s256 extends BCMessageDigest implements Cloneable {
        public Blake2s256() {
            super(new Blake2sDigest(256));
        }

        public Object clone() throws CloneNotSupportedException {
            Blake2s256 blake2s256 = (Blake2s256) super.clone();
            blake2s256.digest = new Blake2sDigest((Blake2sDigest) this.digest);
            return blake2s256;
        }
    }

    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = Blake2s.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$Blake2s256");
            configurableProvider.addAlgorithm("MessageDigest.BLAKE2S-256", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            String str = "Alg.Alias.MessageDigest.";
            sb2.append(str);
            sb2.append(MiscObjectIdentifiers.id_blake2s256);
            configurableProvider.addAlgorithm(sb2.toString(), "BLAKE2S-256");
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$Blake2s224");
            configurableProvider.addAlgorithm("MessageDigest.BLAKE2S-224", sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str);
            sb4.append(MiscObjectIdentifiers.id_blake2s224);
            configurableProvider.addAlgorithm(sb4.toString(), "BLAKE2S-224");
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$Blake2s160");
            configurableProvider.addAlgorithm("MessageDigest.BLAKE2S-160", sb5.toString());
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str);
            sb6.append(MiscObjectIdentifiers.id_blake2s160);
            configurableProvider.addAlgorithm(sb6.toString(), "BLAKE2S-160");
            StringBuilder sb7 = new StringBuilder();
            sb7.append(PREFIX);
            sb7.append("$Blake2s128");
            configurableProvider.addAlgorithm("MessageDigest.BLAKE2S-128", sb7.toString());
            StringBuilder sb8 = new StringBuilder();
            sb8.append(str);
            sb8.append(MiscObjectIdentifiers.id_blake2s128);
            configurableProvider.addAlgorithm(sb8.toString(), "BLAKE2S-128");
        }
    }

    private Blake2s() {
    }
}
