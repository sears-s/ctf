package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.DSTU7564Digest;
import org.bouncycastle.crypto.macs.DSTU7564Mac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

public class DSTU7564 {

    public static class Digest256 extends DigestDSTU7564 {
        public Digest256() {
            super(256);
        }
    }

    public static class Digest384 extends DigestDSTU7564 {
        public Digest384() {
            super(384);
        }
    }

    public static class Digest512 extends DigestDSTU7564 {
        public Digest512() {
            super(512);
        }
    }

    public static class DigestDSTU7564 extends BCMessageDigest implements Cloneable {
        public DigestDSTU7564(int i) {
            super(new DSTU7564Digest(i));
        }

        public Object clone() throws CloneNotSupportedException {
            BCMessageDigest bCMessageDigest = (BCMessageDigest) super.clone();
            bCMessageDigest.digest = new DSTU7564Digest((DSTU7564Digest) this.digest);
            return bCMessageDigest;
        }
    }

    public static class HashMac256 extends BaseMac {
        public HashMac256() {
            super(new DSTU7564Mac(256));
        }
    }

    public static class HashMac384 extends BaseMac {
        public HashMac384() {
            super(new DSTU7564Mac(384));
        }
    }

    public static class HashMac512 extends BaseMac {
        public HashMac512() {
            super(new DSTU7564Mac(512));
        }
    }

    public static class KeyGenerator256 extends BaseKeyGenerator {
        public KeyGenerator256() {
            super("HMACDSTU7564-256", 256, new CipherKeyGenerator());
        }
    }

    public static class KeyGenerator384 extends BaseKeyGenerator {
        public KeyGenerator384() {
            super("HMACDSTU7564-384", 384, new CipherKeyGenerator());
        }
    }

    public static class KeyGenerator512 extends BaseKeyGenerator {
        public KeyGenerator512() {
            super("HMACDSTU7564-512", 512, new CipherKeyGenerator());
        }
    }

    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = DSTU7564.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            String str = "$Digest256";
            sb.append(str);
            configurableProvider.addAlgorithm("MessageDigest.DSTU7564-256", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            String str2 = "$Digest384";
            sb2.append(str2);
            configurableProvider.addAlgorithm("MessageDigest.DSTU7564-384", sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            String str3 = "$Digest512";
            sb3.append(str3);
            configurableProvider.addAlgorithm("MessageDigest.DSTU7564-512", sb3.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier = UAObjectIdentifiers.dstu7564digest_256;
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            sb4.append(str);
            String sb5 = sb4.toString();
            String str4 = "MessageDigest";
            configurableProvider.addAlgorithm(str4, aSN1ObjectIdentifier, sb5);
            ASN1ObjectIdentifier aSN1ObjectIdentifier2 = UAObjectIdentifiers.dstu7564digest_384;
            StringBuilder sb6 = new StringBuilder();
            sb6.append(PREFIX);
            sb6.append(str2);
            configurableProvider.addAlgorithm(str4, aSN1ObjectIdentifier2, sb6.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier3 = UAObjectIdentifiers.dstu7564digest_512;
            StringBuilder sb7 = new StringBuilder();
            sb7.append(PREFIX);
            sb7.append(str3);
            configurableProvider.addAlgorithm(str4, aSN1ObjectIdentifier3, sb7.toString());
            StringBuilder sb8 = new StringBuilder();
            sb8.append(PREFIX);
            sb8.append("$HashMac256");
            String sb9 = sb8.toString();
            StringBuilder sb10 = new StringBuilder();
            sb10.append(PREFIX);
            sb10.append("$KeyGenerator256");
            String str5 = "DSTU7564-256";
            addHMACAlgorithm(configurableProvider, str5, sb9, sb10.toString());
            StringBuilder sb11 = new StringBuilder();
            sb11.append(PREFIX);
            sb11.append("$HashMac384");
            String sb12 = sb11.toString();
            StringBuilder sb13 = new StringBuilder();
            sb13.append(PREFIX);
            sb13.append("$KeyGenerator384");
            String str6 = "DSTU7564-384";
            addHMACAlgorithm(configurableProvider, str6, sb12, sb13.toString());
            StringBuilder sb14 = new StringBuilder();
            sb14.append(PREFIX);
            sb14.append("$HashMac512");
            String sb15 = sb14.toString();
            StringBuilder sb16 = new StringBuilder();
            sb16.append(PREFIX);
            sb16.append("$KeyGenerator512");
            String str7 = "DSTU7564-512";
            addHMACAlgorithm(configurableProvider, str7, sb15, sb16.toString());
            addHMACAlias(configurableProvider, str5, UAObjectIdentifiers.dstu7564mac_256);
            addHMACAlias(configurableProvider, str6, UAObjectIdentifiers.dstu7564mac_384);
            addHMACAlias(configurableProvider, str7, UAObjectIdentifiers.dstu7564mac_512);
        }
    }

    private DSTU7564() {
    }
}
