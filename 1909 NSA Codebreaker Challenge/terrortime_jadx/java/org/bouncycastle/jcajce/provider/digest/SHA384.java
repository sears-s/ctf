package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.macs.OldHMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;

public class SHA384 {

    public static class Digest extends BCMessageDigest implements Cloneable {
        public Digest() {
            super(new SHA384Digest());
        }

        public Object clone() throws CloneNotSupportedException {
            Digest digest = (Digest) super.clone();
            digest.digest = new SHA384Digest((SHA384Digest) this.digest);
            return digest;
        }
    }

    public static class HashMac extends BaseMac {
        public HashMac() {
            super(new HMac(new SHA384Digest()));
        }
    }

    public static class KeyGenerator extends BaseKeyGenerator {
        public KeyGenerator() {
            super("HMACSHA384", 384, new CipherKeyGenerator());
        }
    }

    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = SHA384.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$Digest");
            configurableProvider.addAlgorithm("MessageDigest.SHA-384", sb.toString());
            String str = McElieceCCA2KeyGenParameterSpec.SHA384;
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA384", str);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Alg.Alias.MessageDigest.");
            sb2.append(NISTObjectIdentifiers.id_sha384);
            configurableProvider.addAlgorithm(sb2.toString(), str);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$OldSHA384");
            configurableProvider.addAlgorithm("Mac.OLDHMACSHA384", sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            String str2 = "$HashMac";
            sb4.append(str2);
            configurableProvider.addAlgorithm("Mac.PBEWITHHMACSHA384", sb4.toString());
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append(str2);
            String sb6 = sb5.toString();
            StringBuilder sb7 = new StringBuilder();
            sb7.append(PREFIX);
            sb7.append("$KeyGenerator");
            String str3 = "SHA384";
            addHMACAlgorithm(configurableProvider, str3, sb6, sb7.toString());
            addHMACAlias(configurableProvider, str3, PKCSObjectIdentifiers.id_hmacWithSHA384);
        }
    }

    public static class OldSHA384 extends BaseMac {
        public OldSHA384() {
            super(new OldHMac(new SHA384Digest()));
        }
    }

    private SHA384() {
    }
}
