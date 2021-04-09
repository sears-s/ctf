package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class Blowfish {

    public static class AlgParams extends IvAlgorithmParameters {
        /* access modifiers changed from: protected */
        public String engineToString() {
            return "Blowfish IV";
        }
    }

    public static class CBC extends BaseBlockCipher {
        public CBC() {
            super((BlockCipher) new CBCBlockCipher(new BlowfishEngine()), 64);
        }
    }

    public static class CMAC extends BaseMac {
        public CMAC() {
            super(new CMac(new BlowfishEngine()));
        }
    }

    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super((BlockCipher) new BlowfishEngine());
        }
    }

    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("Blowfish", 128, new CipherKeyGenerator());
        }
    }

    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = Blowfish.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append("$CMAC");
            configurableProvider.addAlgorithm("Mac.BLOWFISHCMAC", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append(PREFIX);
            sb2.append("$ECB");
            configurableProvider.addAlgorithm("Cipher.BLOWFISH", sb2.toString());
            ASN1ObjectIdentifier aSN1ObjectIdentifier = MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC;
            StringBuilder sb3 = new StringBuilder();
            sb3.append(PREFIX);
            sb3.append("$CBC");
            configurableProvider.addAlgorithm("Cipher", aSN1ObjectIdentifier, sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append(PREFIX);
            sb4.append("$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator.BLOWFISH", sb4.toString());
            String str = "BLOWFISH";
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator", MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC, str);
            StringBuilder sb5 = new StringBuilder();
            sb5.append(PREFIX);
            sb5.append("$AlgParams");
            configurableProvider.addAlgorithm("AlgorithmParameters.BLOWFISH", sb5.toString());
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC, str);
        }
    }

    private Blowfish() {
    }
}
