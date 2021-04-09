package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;

public class SHA1 {

    public static class Digest extends BCMessageDigest implements Cloneable {
        public Digest() {
            super(new SHA1Digest());
        }

        public Object clone() throws CloneNotSupportedException {
            Digest digest = (Digest) super.clone();
            digest.digest = new SHA1Digest((SHA1Digest) this.digest);
            return digest;
        }
    }

    public static class HashMac extends BaseMac {
        public HashMac() {
            super(new HMac(new SHA1Digest()));
        }
    }

    public static class KeyGenerator extends BaseKeyGenerator {
        public KeyGenerator() {
            super("HMACSHA1", CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256, new CipherKeyGenerator());
        }
    }

    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = SHA1.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$Digest");
            configurableProvider.addAlgorithm("MessageDigest.SHA-1", sb.toString());
            String str = "SHA-1";
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA1", str);
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA", str);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Alg.Alias.MessageDigest.");
            sb2.append(OIWObjectIdentifiers.idSHA1);
            configurableProvider.addAlgorithm(sb2.toString(), str);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$HashMac");
            String sb4 = sb3.toString();
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$KeyGenerator");
            String str2 = "SHA1";
            addHMACAlgorithm(configurableProvider, str2, sb4, sb5.toString());
            addHMACAlias(configurableProvider, str2, PKCSObjectIdentifiers.id_hmacWithSHA1);
            addHMACAlias(configurableProvider, str2, IANAObjectIdentifiers.hmacSHA1);
            StringBuilder sb6 = new StringBuilder();
            sb6.append(PREFIX);
            String str3 = "$SHA1Mac";
            sb6.append(str3);
            configurableProvider.addAlgorithm("Mac.PBEWITHHMACSHA", sb6.toString());
            StringBuilder sb7 = new StringBuilder();
            sb7.append(PREFIX);
            sb7.append(str3);
            configurableProvider.addAlgorithm("Mac.PBEWITHHMACSHA1", sb7.toString());
            String str4 = "PBEWITHHMACSHA1";
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHHMACSHA", str4);
            StringBuilder sb8 = new StringBuilder();
            sb8.append("Alg.Alias.SecretKeyFactory.");
            sb8.append(OIWObjectIdentifiers.idSHA1);
            configurableProvider.addAlgorithm(sb8.toString(), str4);
            StringBuilder sb9 = new StringBuilder();
            sb9.append("Alg.Alias.Mac.");
            sb9.append(OIWObjectIdentifiers.idSHA1);
            configurableProvider.addAlgorithm(sb9.toString(), "PBEWITHHMACSHA");
            StringBuilder sb10 = new StringBuilder();
            sb10.append(PREFIX);
            sb10.append("$PBEWithMacKeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHHMACSHA1", sb10.toString());
        }
    }

    public static class PBEWithMacKeyFactory extends PBESecretKeyFactory {
        public PBEWithMacKeyFactory() {
            super("PBEwithHmacSHA", null, false, 2, 1, CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256, 0);
        }
    }

    public static class SHA1Mac extends BaseMac {
        public SHA1Mac() {
            super(new HMac(new SHA1Digest()));
        }
    }

    private SHA1() {
    }
}
