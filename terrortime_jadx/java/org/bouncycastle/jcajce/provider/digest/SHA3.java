package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

public class SHA3 {

    public static class Digest224 extends DigestSHA3 {
        public Digest224() {
            super(224);
        }
    }

    public static class Digest256 extends DigestSHA3 {
        public Digest256() {
            super(256);
        }
    }

    public static class Digest384 extends DigestSHA3 {
        public Digest384() {
            super(384);
        }
    }

    public static class Digest512 extends DigestSHA3 {
        public Digest512() {
            super(512);
        }
    }

    public static class DigestSHA3 extends BCMessageDigest implements Cloneable {
        public DigestSHA3(int i) {
            super(new SHA3Digest(i));
        }

        public Object clone() throws CloneNotSupportedException {
            BCMessageDigest bCMessageDigest = (BCMessageDigest) super.clone();
            bCMessageDigest.digest = new SHA3Digest((SHA3Digest) this.digest);
            return bCMessageDigest;
        }
    }

    public static class HashMac224 extends HashMacSHA3 {
        public HashMac224() {
            super(224);
        }
    }

    public static class HashMac256 extends HashMacSHA3 {
        public HashMac256() {
            super(256);
        }
    }

    public static class HashMac384 extends HashMacSHA3 {
        public HashMac384() {
            super(384);
        }
    }

    public static class HashMac512 extends HashMacSHA3 {
        public HashMac512() {
            super(512);
        }
    }

    public static class HashMacSHA3 extends BaseMac {
        public HashMacSHA3(int i) {
            super(new HMac(new SHA3Digest(i)));
        }
    }

    public static class KeyGenerator224 extends KeyGeneratorSHA3 {
        public KeyGenerator224() {
            super(224);
        }
    }

    public static class KeyGenerator256 extends KeyGeneratorSHA3 {
        public KeyGenerator256() {
            super(256);
        }
    }

    public static class KeyGenerator384 extends KeyGeneratorSHA3 {
        public KeyGenerator384() {
            super(384);
        }
    }

    public static class KeyGenerator512 extends KeyGeneratorSHA3 {
        public KeyGenerator512() {
            super(512);
        }
    }

    public static class KeyGeneratorSHA3 extends BaseKeyGenerator {
        public KeyGeneratorSHA3(int i) {
            StringBuilder sb = new StringBuilder();
            sb.append("HMACSHA3-");
            sb.append(i);
            super(sb.toString(), i, new CipherKeyGenerator());
        }
    }

    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = SHA3.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            String str = "$Digest224";
            sb.append(str);
            configurableProvider.addAlgorithm("MessageDigest.SHA3-224", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            String str2 = "$Digest256";
            sb2.append(str2);
            configurableProvider.addAlgorithm("MessageDigest.SHA3-256", sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            String str3 = "$Digest384";
            sb3.append(str3);
            configurableProvider.addAlgorithm("MessageDigest.SHA3-384", sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            String str4 = "$Digest512";
            sb4.append(str4);
            configurableProvider.addAlgorithm("MessageDigest.SHA3-512", sb4.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier = NISTObjectIdentifiers.id_sha3_224;
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append(str);
            String sb6 = sb5.toString();
            String str5 = "MessageDigest";
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier, sb6);
            ASN1ObjectIdentifier aSN1ObjectIdentifier2 = NISTObjectIdentifiers.id_sha3_256;
            StringBuilder sb7 = new StringBuilder();
            sb7.append(PREFIX);
            sb7.append(str2);
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier2, sb7.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier3 = NISTObjectIdentifiers.id_sha3_384;
            StringBuilder sb8 = new StringBuilder();
            sb8.append(PREFIX);
            sb8.append(str3);
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier3, sb8.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier4 = NISTObjectIdentifiers.id_sha3_512;
            StringBuilder sb9 = new StringBuilder();
            sb9.append(PREFIX);
            sb9.append(str4);
            configurableProvider.addAlgorithm(str5, aSN1ObjectIdentifier4, sb9.toString());
            StringBuilder sb10 = new StringBuilder();
            sb10.append(PREFIX);
            sb10.append("$HashMac224");
            String sb11 = sb10.toString();
            StringBuilder sb12 = new StringBuilder();
            sb12.append(PREFIX);
            sb12.append("$KeyGenerator224");
            String str6 = "SHA3-224";
            addHMACAlgorithm(configurableProvider, str6, sb11, sb12.toString());
            addHMACAlias(configurableProvider, str6, NISTObjectIdentifiers.id_hmacWithSHA3_224);
            StringBuilder sb13 = new StringBuilder();
            sb13.append(PREFIX);
            sb13.append("$HashMac256");
            String sb14 = sb13.toString();
            StringBuilder sb15 = new StringBuilder();
            sb15.append(PREFIX);
            sb15.append("$KeyGenerator256");
            String str7 = "SHA3-256";
            addHMACAlgorithm(configurableProvider, str7, sb14, sb15.toString());
            addHMACAlias(configurableProvider, str7, NISTObjectIdentifiers.id_hmacWithSHA3_256);
            StringBuilder sb16 = new StringBuilder();
            sb16.append(PREFIX);
            sb16.append("$HashMac384");
            String sb17 = sb16.toString();
            StringBuilder sb18 = new StringBuilder();
            sb18.append(PREFIX);
            sb18.append("$KeyGenerator384");
            String str8 = "SHA3-384";
            addHMACAlgorithm(configurableProvider, str8, sb17, sb18.toString());
            addHMACAlias(configurableProvider, str8, NISTObjectIdentifiers.id_hmacWithSHA3_384);
            StringBuilder sb19 = new StringBuilder();
            sb19.append(PREFIX);
            sb19.append("$HashMac512");
            String sb20 = sb19.toString();
            StringBuilder sb21 = new StringBuilder();
            sb21.append(PREFIX);
            sb21.append("$KeyGenerator512");
            String str9 = "SHA3-512";
            addHMACAlgorithm(configurableProvider, str9, sb20, sb21.toString());
            addHMACAlias(configurableProvider, str9, NISTObjectIdentifiers.id_hmacWithSHA3_512);
        }
    }

    private SHA3() {
    }
}
