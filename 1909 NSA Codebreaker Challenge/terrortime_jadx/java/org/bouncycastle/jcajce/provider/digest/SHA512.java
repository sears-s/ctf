package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.macs.OldHMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSKeyParameters;

public class SHA512 {

    public static class Digest extends BCMessageDigest implements Cloneable {
        public Digest() {
            super(new SHA512Digest());
        }

        public Object clone() throws CloneNotSupportedException {
            Digest digest = (Digest) super.clone();
            digest.digest = new SHA512Digest((SHA512Digest) this.digest);
            return digest;
        }
    }

    public static class DigestT extends BCMessageDigest implements Cloneable {
        public DigestT(int i) {
            super(new SHA512tDigest(i));
        }

        public Object clone() throws CloneNotSupportedException {
            DigestT digestT = (DigestT) super.clone();
            digestT.digest = new SHA512tDigest((SHA512tDigest) this.digest);
            return digestT;
        }
    }

    public static class DigestT224 extends DigestT {
        public DigestT224() {
            super(224);
        }
    }

    public static class DigestT256 extends DigestT {
        public DigestT256() {
            super(256);
        }
    }

    public static class HashMac extends BaseMac {
        public HashMac() {
            super(new HMac(new SHA512Digest()));
        }
    }

    public static class HashMacT224 extends BaseMac {
        public HashMacT224() {
            super(new HMac(new SHA512tDigest(224)));
        }
    }

    public static class HashMacT256 extends BaseMac {
        public HashMacT256() {
            super(new HMac(new SHA512tDigest(256)));
        }
    }

    public static class KeyGenerator extends BaseKeyGenerator {
        public KeyGenerator() {
            super("HMACSHA512", 512, new CipherKeyGenerator());
        }
    }

    public static class KeyGeneratorT224 extends BaseKeyGenerator {
        public KeyGeneratorT224() {
            super("HMACSHA512/224", 224, new CipherKeyGenerator());
        }
    }

    public static class KeyGeneratorT256 extends BaseKeyGenerator {
        public KeyGeneratorT256() {
            super("HMACSHA512/256", 256, new CipherKeyGenerator());
        }
    }

    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = SHA512.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$Digest");
            configurableProvider.addAlgorithm("MessageDigest.SHA-512", sb.toString());
            String str = "SHA-512";
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512", str);
            StringBuilder sb2 = new StringBuilder();
            String str2 = "Alg.Alias.MessageDigest.";
            sb2.append(str2);
            sb2.append(NISTObjectIdentifiers.id_sha512);
            configurableProvider.addAlgorithm(sb2.toString(), str);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$DigestT224");
            configurableProvider.addAlgorithm("MessageDigest.SHA-512/224", sb3.toString());
            String str3 = "SHA-512/224";
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512/224", str3);
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str2);
            sb4.append(NISTObjectIdentifiers.id_sha512_224);
            configurableProvider.addAlgorithm(sb4.toString(), str3);
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$DigestT256");
            configurableProvider.addAlgorithm("MessageDigest.SHA-512/256", sb5.toString());
            String str4 = SPHINCSKeyParameters.SHA512_256;
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512256", str4);
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str2);
            sb6.append(NISTObjectIdentifiers.id_sha512_256);
            configurableProvider.addAlgorithm(sb6.toString(), str4);
            StringBuilder sb7 = new StringBuilder();
            sb7.append(PREFIX);
            sb7.append("$OldSHA512");
            configurableProvider.addAlgorithm("Mac.OLDHMACSHA512", sb7.toString());
            StringBuilder sb8 = new StringBuilder();
            sb8.append(PREFIX);
            String str5 = "$HashMac";
            sb8.append(str5);
            configurableProvider.addAlgorithm("Mac.PBEWITHHMACSHA512", sb8.toString());
            StringBuilder sb9 = new StringBuilder();
            sb9.append(PREFIX);
            sb9.append(str5);
            String sb10 = sb9.toString();
            StringBuilder sb11 = new StringBuilder();
            sb11.append(PREFIX);
            sb11.append("$KeyGenerator");
            String str6 = "SHA512";
            addHMACAlgorithm(configurableProvider, str6, sb10, sb11.toString());
            addHMACAlias(configurableProvider, str6, PKCSObjectIdentifiers.id_hmacWithSHA512);
            StringBuilder sb12 = new StringBuilder();
            sb12.append(PREFIX);
            sb12.append("$HashMacT224");
            String sb13 = sb12.toString();
            StringBuilder sb14 = new StringBuilder();
            sb14.append(PREFIX);
            sb14.append("$KeyGeneratorT224");
            addHMACAlgorithm(configurableProvider, "SHA512/224", sb13, sb14.toString());
            StringBuilder sb15 = new StringBuilder();
            sb15.append(PREFIX);
            sb15.append("$HashMacT256");
            String sb16 = sb15.toString();
            StringBuilder sb17 = new StringBuilder();
            sb17.append(PREFIX);
            sb17.append("$KeyGeneratorT256");
            addHMACAlgorithm(configurableProvider, "SHA512/256", sb16, sb17.toString());
        }
    }

    public static class OldSHA512 extends BaseMac {
        public OldSHA512() {
            super(new OldHMac(new SHA512Digest()));
        }
    }

    private SHA512() {
    }
}
