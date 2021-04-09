package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;

public class SM3 {

    public static class Digest extends BCMessageDigest implements Cloneable {
        public Digest() {
            super(new SM3Digest());
        }

        public Object clone() throws CloneNotSupportedException {
            Digest digest = (Digest) super.clone();
            digest.digest = new SM3Digest((SM3Digest) this.digest);
            return digest;
        }
    }

    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = SM3.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$Digest");
            configurableProvider.addAlgorithm("MessageDigest.SM3", sb.toString());
            String str = "SM3";
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SM3", str);
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.1.2.156.197.1.401", str);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Alg.Alias.MessageDigest.");
            sb2.append(GMObjectIdentifiers.sm3);
            configurableProvider.addAlgorithm(sb2.toString(), str);
        }
    }

    private SM3() {
    }
}
