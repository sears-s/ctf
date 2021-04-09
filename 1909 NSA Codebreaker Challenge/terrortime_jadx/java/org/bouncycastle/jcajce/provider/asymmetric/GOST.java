package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.gost.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class GOST {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.gost.";

    public static class Mappings extends AsymmetricAlgorithmProvider {
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyPairGenerator.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.KeyPairGeneratorSpi");
            String str = "GOST3410";
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410", str);
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410-94", str);
            configurableProvider.addAlgorithm("KeyFactory.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.KeyFactorySpi");
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410", str);
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410-94", str);
            configurableProvider.addAlgorithm("AlgorithmParameters.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.AlgorithmParametersSpi");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.AlgorithmParameterGeneratorSpi");
            registerOid(configurableProvider, CryptoProObjectIdentifiers.gostR3410_94, str, new KeyFactorySpi());
            registerOidAlgorithmParameterGenerator(configurableProvider, CryptoProObjectIdentifiers.gostR3410_94, str);
            configurableProvider.addAlgorithm("Signature.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.SignatureSpi");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.GOST-3410", str);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.GOST-3410-94", str);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.GOST3411withGOST3410", str);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.GOST3411WITHGOST3410", str);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.GOST3411WithGOST3410", str);
            StringBuilder sb = new StringBuilder();
            sb.append("Alg.Alias.Signature.");
            sb.append(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
            configurableProvider.addAlgorithm(sb.toString(), str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.GOST-3410", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.GOST-3410", str);
        }
    }
}
