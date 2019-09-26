package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

public class Whirlpool {

    public static class Digest extends BCMessageDigest implements Cloneable {
        public Digest() {
            super(new WhirlpoolDigest());
        }

        public Object clone() throws CloneNotSupportedException {
            Digest digest = (Digest) super.clone();
            digest.digest = new WhirlpoolDigest((WhirlpoolDigest) this.digest);
            return digest;
        }
    }

    public static class HashMac extends BaseMac {
        public HashMac() {
            super(new HMac(new WhirlpoolDigest()));
        }
    }

    public static class KeyGenerator extends BaseKeyGenerator {
        public KeyGenerator() {
            super("HMACWHIRLPOOL", 512, new CipherKeyGenerator());
        }
    }

    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = Whirlpool.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            String str = "$Digest";
            sb.append(str);
            configurableProvider.addAlgorithm("MessageDigest.WHIRLPOOL", sb.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier = ISOIECObjectIdentifiers.whirlpool;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            sb2.append(str);
            configurableProvider.addAlgorithm("MessageDigest", aSN1ObjectIdentifier, sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$HashMac");
            String sb4 = sb3.toString();
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$KeyGenerator");
            addHMACAlgorithm(configurableProvider, "WHIRLPOOL", sb4, sb5.toString());
        }
    }

    private Whirlpool() {
    }
}
