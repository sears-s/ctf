package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.dstu.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class DSTU4145 {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.dstu.";

    public static class Mappings extends AsymmetricAlgorithmProvider {
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.DSTU4145", "org.bouncycastle.jcajce.provider.asymmetric.dstu.KeyFactorySpi");
            String str = "DSTU4145";
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.DSTU-4145-2002", str);
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.DSTU4145-3410", str);
            registerOid(configurableProvider, UAObjectIdentifiers.dstu4145le, str, new KeyFactorySpi());
            registerOidAlgorithmParameters(configurableProvider, UAObjectIdentifiers.dstu4145le, str);
            registerOid(configurableProvider, UAObjectIdentifiers.dstu4145be, str, new KeyFactorySpi());
            registerOidAlgorithmParameters(configurableProvider, UAObjectIdentifiers.dstu4145be, str);
            configurableProvider.addAlgorithm("KeyPairGenerator.DSTU4145", "org.bouncycastle.jcajce.provider.asymmetric.dstu.KeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.DSTU-4145", str);
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.DSTU-4145-2002", str);
            configurableProvider.addAlgorithm("Signature.DSTU4145", "org.bouncycastle.jcajce.provider.asymmetric.dstu.SignatureSpi");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.DSTU-4145", str);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.DSTU-4145-2002", str);
            ConfigurableProvider configurableProvider2 = configurableProvider;
            addSignatureAlgorithm(configurableProvider2, "GOST3411", "DSTU4145LE", "org.bouncycastle.jcajce.provider.asymmetric.dstu.SignatureSpiLe", UAObjectIdentifiers.dstu4145le);
            ConfigurableProvider configurableProvider3 = configurableProvider;
            addSignatureAlgorithm(configurableProvider3, "GOST3411", "DSTU4145", "org.bouncycastle.jcajce.provider.asymmetric.dstu.SignatureSpi", UAObjectIdentifiers.dstu4145be);
        }
    }
}
