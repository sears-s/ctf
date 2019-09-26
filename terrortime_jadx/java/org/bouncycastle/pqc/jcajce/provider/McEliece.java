package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;

public class McEliece {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.mceliece.";

    public static class Mappings extends AsymmetricAlgorithmProvider {
        public void configure(ConfigurableProvider configurableProvider) {
            String str = "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyPairGeneratorSpi";
            configurableProvider.addAlgorithm("KeyPairGenerator.McElieceKobaraImai", str);
            configurableProvider.addAlgorithm("KeyPairGenerator.McEliecePointcheval", str);
            configurableProvider.addAlgorithm("KeyPairGenerator.McElieceFujisaki", str);
            configurableProvider.addAlgorithm("KeyPairGenerator.McEliece", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.McEliece-CCA2", str);
            String str2 = "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi";
            configurableProvider.addAlgorithm("KeyFactory.McElieceKobaraImai", str2);
            configurableProvider.addAlgorithm("KeyFactory.McEliecePointcheval", str2);
            configurableProvider.addAlgorithm("KeyFactory.McElieceFujisaki", str2);
            String str3 = "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceKeyFactorySpi";
            configurableProvider.addAlgorithm("KeyFactory.McEliece", str3);
            configurableProvider.addAlgorithm("KeyFactory.McEliece-CCA2", str2);
            StringBuilder sb = new StringBuilder();
            String str4 = "KeyFactory.";
            sb.append(str4);
            sb.append(PQCObjectIdentifiers.mcElieceCca2);
            configurableProvider.addAlgorithm(sb.toString(), str2);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str4);
            sb2.append(PQCObjectIdentifiers.mcEliece);
            configurableProvider.addAlgorithm(sb2.toString(), str3);
            configurableProvider.addAlgorithm("Cipher.McEliece", "org.bouncycastle.pqc.jcajce.provider.mceliece.McEliecePKCSCipherSpi$McEliecePKCS");
            configurableProvider.addAlgorithm("Cipher.McEliecePointcheval", "org.bouncycastle.pqc.jcajce.provider.mceliece.McEliecePointchevalCipherSpi$McEliecePointcheval");
            configurableProvider.addAlgorithm("Cipher.McElieceKobaraImai", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceKobaraImaiCipherSpi$McElieceKobaraImai");
            configurableProvider.addAlgorithm("Cipher.McElieceFujisaki", "org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceFujisakiCipherSpi$McElieceFujisaki");
        }
    }
}
