package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.qtesla.QTESLAKeyFactorySpi;

public class QTESLA {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.qtesla.";

    public static class Mappings extends AsymmetricAlgorithmProvider {
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.QTESLA", "org.bouncycastle.pqc.jcajce.provider.qtesla.QTESLAKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.QTESLA", "org.bouncycastle.pqc.jcajce.provider.qtesla.KeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("Signature.QTESLA", "org.bouncycastle.pqc.jcajce.provider.qtesla.SignatureSpi$qTESLA");
            String str = "QTESLA-I";
            addSignatureAlgorithm(configurableProvider, str, "org.bouncycastle.pqc.jcajce.provider.qtesla.SignatureSpi$HeuristicI", PQCObjectIdentifiers.qTESLA_I);
            String str2 = "QTESLA-III-SIZE";
            addSignatureAlgorithm(configurableProvider, str2, "org.bouncycastle.pqc.jcajce.provider.qtesla.SignatureSpi$HeuristicIIISize", PQCObjectIdentifiers.qTESLA_III_size);
            String str3 = "QTESLA-III-SPEED";
            addSignatureAlgorithm(configurableProvider, str3, "org.bouncycastle.pqc.jcajce.provider.qtesla.SignatureSpi$HeuristicIIISpeed", PQCObjectIdentifiers.qTESLA_III_speed);
            String str4 = "QTESLA-P-I";
            addSignatureAlgorithm(configurableProvider, str4, "org.bouncycastle.pqc.jcajce.provider.qtesla.SignatureSpi$ProvablySecureI", PQCObjectIdentifiers.qTESLA_p_I);
            String str5 = "QTESLA-P-III";
            addSignatureAlgorithm(configurableProvider, str5, "org.bouncycastle.pqc.jcajce.provider.qtesla.SignatureSpi$ProvablySecureIII", PQCObjectIdentifiers.qTESLA_p_III);
            QTESLAKeyFactorySpi qTESLAKeyFactorySpi = new QTESLAKeyFactorySpi();
            registerOid(configurableProvider, PQCObjectIdentifiers.qTESLA_I, str, qTESLAKeyFactorySpi);
            registerOid(configurableProvider, PQCObjectIdentifiers.qTESLA_III_size, str2, qTESLAKeyFactorySpi);
            registerOid(configurableProvider, PQCObjectIdentifiers.qTESLA_III_speed, str3, qTESLAKeyFactorySpi);
            registerOid(configurableProvider, PQCObjectIdentifiers.qTESLA_p_I, str4, qTESLAKeyFactorySpi);
            registerOid(configurableProvider, PQCObjectIdentifiers.qTESLA_p_III, str5, qTESLAKeyFactorySpi);
        }
    }
}
