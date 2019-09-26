package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class IES {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.ies.";

    public static class Mappings extends AsymmetricAlgorithmProvider {
        public void configure(ConfigurableProvider configurableProvider) {
            String str = "org.bouncycastle.jcajce.provider.asymmetric.ies.AlgorithmParametersSpi";
            configurableProvider.addAlgorithm("AlgorithmParameters.IES", str);
            configurableProvider.addAlgorithm("AlgorithmParameters.ECIES", str);
        }
    }
}
