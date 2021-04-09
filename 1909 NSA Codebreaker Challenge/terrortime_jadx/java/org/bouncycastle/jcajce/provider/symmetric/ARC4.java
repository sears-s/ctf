package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class ARC4 {

    public static class Base extends BaseStreamCipher {
        public Base() {
            super(new RC4Engine(), 0);
        }
    }

    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("RC4", 128, new CipherKeyGenerator());
        }
    }

    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = ARC4.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$Base");
            configurableProvider.addAlgorithm("Cipher.ARC4", sb.toString());
            String str = "Alg.Alias.Cipher";
            String str2 = "ARC4";
            configurableProvider.addAlgorithm(str, PKCSObjectIdentifiers.rc4, str2);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.ARCFOUR", str2);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.RC4", str2);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            sb2.append("$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator.ARC4", sb2.toString());
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.RC4", str2);
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.1.2.840.113549.3.4", str2);
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$PBEWithSHAAnd128BitKeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND128BITRC4", sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            sb4.append("$PBEWithSHAAnd40BitKeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND40BITRC4", sb4.toString());
            StringBuilder sb5 = new StringBuilder();
            String str3 = "Alg.Alias.AlgorithmParameters.";
            sb5.append(str3);
            sb5.append(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4);
            String str4 = "PKCS12PBE";
            configurableProvider.addAlgorithm(sb5.toString(), str4);
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str3);
            sb6.append(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4);
            configurableProvider.addAlgorithm(sb6.toString(), str4);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND40BITRC4", str4);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND128BITRC4", str4);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDRC4", str4);
            StringBuilder sb7 = new StringBuilder();
            sb7.append(PREFIX);
            sb7.append("$PBEWithSHAAnd128Bit");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND128BITRC4", sb7.toString());
            StringBuilder sb8 = new StringBuilder();
            sb8.append(PREFIX);
            sb8.append("$PBEWithSHAAnd40Bit");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND40BITRC4", sb8.toString());
            String str5 = "Alg.Alias.SecretKeyFactory";
            String str6 = "PBEWITHSHAAND128BITRC4";
            configurableProvider.addAlgorithm(str5, PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, str6);
            String str7 = "PBEWITHSHAAND40BITRC4";
            configurableProvider.addAlgorithm(str5, PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, str7);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITRC4", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND40BITRC4", str7);
            configurableProvider.addAlgorithm(str, PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, str6);
            configurableProvider.addAlgorithm(str, PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, str7);
        }
    }

    public static class PBEWithSHAAnd128Bit extends BaseStreamCipher {
        public PBEWithSHAAnd128Bit() {
            super(new RC4Engine(), 0, 128, 1);
        }
    }

    public static class PBEWithSHAAnd128BitKeyFactory extends PBESecretKeyFactory {
        public PBEWithSHAAnd128BitKeyFactory() {
            super("PBEWithSHAAnd128BitRC4", PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, true, 2, 1, 128, 0);
        }
    }

    public static class PBEWithSHAAnd40Bit extends BaseStreamCipher {
        public PBEWithSHAAnd40Bit() {
            super(new RC4Engine(), 0, 40, 1);
        }
    }

    public static class PBEWithSHAAnd40BitKeyFactory extends PBESecretKeyFactory {
        public PBEWithSHAAnd40BitKeyFactory() {
            super("PBEWithSHAAnd128BitRC4", PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, true, 2, 1, 40, 0);
        }
    }

    private ARC4() {
    }
}
