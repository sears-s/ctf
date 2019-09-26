package org.bouncycastle.jcajce.provider.keystore;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class PKCS12 {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.keystore.pkcs12.";

    public static class Mappings extends AsymmetricAlgorithmProvider {
        public void configure(ConfigurableProvider configurableProvider) {
            String str = "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$BCPKCS12KeyStore";
            configurableProvider.addAlgorithm("KeyStore.PKCS12", str);
            configurableProvider.addAlgorithm("KeyStore.BCPKCS12", str);
            String str2 = "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$DefPKCS12KeyStore";
            configurableProvider.addAlgorithm("KeyStore.PKCS12-DEF", str2);
            configurableProvider.addAlgorithm("KeyStore.PKCS12-3DES-40RC2", str);
            configurableProvider.addAlgorithm("KeyStore.PKCS12-3DES-3DES", "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$BCPKCS12KeyStore3DES");
            configurableProvider.addAlgorithm("KeyStore.PKCS12-DEF-3DES-40RC2", str2);
            configurableProvider.addAlgorithm("KeyStore.PKCS12-DEF-3DES-3DES", "org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$DefPKCS12KeyStore3DES");
        }
    }
}
