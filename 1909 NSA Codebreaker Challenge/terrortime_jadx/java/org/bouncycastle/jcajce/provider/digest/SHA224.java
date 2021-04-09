package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;

public class SHA224 {

    public static class Digest extends BCMessageDigest implements Cloneable {
        public Digest() {
            super(new SHA224Digest());
        }

        public Object clone() throws CloneNotSupportedException {
            Digest digest = (Digest) super.clone();
            digest.digest = new SHA224Digest((SHA224Digest) this.digest);
            return digest;
        }
    }

    public static class HashMac extends BaseMac {
        public HashMac() {
            super(new HMac(new SHA224Digest()));
        }
    }

    public static class KeyGenerator extends BaseKeyGenerator {
        public KeyGenerator() {
            super("HMACSHA224", 224, new CipherKeyGenerator());
        }
    }

    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = SHA224.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$Digest");
            configurableProvider.addAlgorithm("MessageDigest.SHA-224", sb.toString());
            String str = McElieceCCA2KeyGenParameterSpec.SHA224;
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA224", str);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Alg.Alias.MessageDigest.");
            sb2.append(NISTObjectIdentifiers.id_sha224);
            configurableProvider.addAlgorithm(sb2.toString(), str);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            String str2 = "$HashMac";
            sb3.append(str2);
            configurableProvider.addAlgorithm("Mac.PBEWITHHMACSHA224", sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            sb4.append(str2);
            String sb5 = sb4.toString();
            StringBuilder sb6 = new StringBuilder();
            sb6.append(PREFIX);
            sb6.append("$KeyGenerator");
            String str3 = "SHA224";
            addHMACAlgorithm(configurableProvider, str3, sb5, sb6.toString());
            addHMACAlias(configurableProvider, str3, PKCSObjectIdentifiers.id_hmacWithSHA224);
        }
    }

    private SHA224() {
    }
}
