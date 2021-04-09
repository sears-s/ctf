package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.elgamal.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class ElGamal {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.elgamal.";

    public static class Mappings extends AsymmetricAlgorithmProvider {
        public void configure(ConfigurableProvider configurableProvider) {
            String str = "org.bouncycastle.jcajce.provider.asymmetric.elgamal.AlgorithmParameterGeneratorSpi";
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.ELGAMAL", str);
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.ElGamal", str);
            String str2 = "org.bouncycastle.jcajce.provider.asymmetric.elgamal.AlgorithmParametersSpi";
            configurableProvider.addAlgorithm("AlgorithmParameters.ELGAMAL", str2);
            configurableProvider.addAlgorithm("AlgorithmParameters.ElGamal", str2);
            String str3 = "org.bouncycastle.jcajce.provider.asymmetric.elgamal.CipherSpi$NoPadding";
            configurableProvider.addAlgorithm("Cipher.ELGAMAL", str3);
            configurableProvider.addAlgorithm("Cipher.ElGamal", str3);
            String str4 = "ELGAMAL/PKCS1";
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.ELGAMAL/ECB/PKCS1PADDING", str4);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.ELGAMAL/NONE/PKCS1PADDING", str4);
            String str5 = "ELGAMAL";
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.ELGAMAL/NONE/NOPADDING", str5);
            configurableProvider.addAlgorithm("Cipher.ELGAMAL/PKCS1", "org.bouncycastle.jcajce.provider.asymmetric.elgamal.CipherSpi$PKCS1v1_5Padding");
            String str6 = "org.bouncycastle.jcajce.provider.asymmetric.elgamal.KeyFactorySpi";
            configurableProvider.addAlgorithm("KeyFactory.ELGAMAL", str6);
            configurableProvider.addAlgorithm("KeyFactory.ElGamal", str6);
            String str7 = "org.bouncycastle.jcajce.provider.asymmetric.elgamal.KeyPairGeneratorSpi";
            configurableProvider.addAlgorithm("KeyPairGenerator.ELGAMAL", str7);
            configurableProvider.addAlgorithm("KeyPairGenerator.ElGamal", str7);
            registerOid(configurableProvider, OIWObjectIdentifiers.elGamalAlgorithm, str5, new KeyFactorySpi());
            registerOidAlgorithmParameterGenerator(configurableProvider, OIWObjectIdentifiers.elGamalAlgorithm, str5);
        }
    }
}
