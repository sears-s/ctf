package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class ECGOST {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.ecgost.";
    private static final String PREFIX_GOST_2012 = "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.";

    public static class Mappings extends AsymmetricAlgorithmProvider {
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.KeyFactorySpi");
            String str = "ECGOST3410";
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410-2001", str);
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.ECGOST-3410", str);
            registerOid(configurableProvider, CryptoProObjectIdentifiers.gostR3410_2001, str, new KeyFactorySpi());
            registerOid(configurableProvider, CryptoProObjectIdentifiers.gostR3410_2001DH, str, new KeyFactorySpi());
            registerOidAlgorithmParameters(configurableProvider, CryptoProObjectIdentifiers.gostR3410_2001, str);
            configurableProvider.addAlgorithm("KeyPairGenerator.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.KeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.ECGOST-3410", str);
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410-2001", str);
            configurableProvider.addAlgorithm("Signature.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.SignatureSpi");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.ECGOST-3410", str);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.GOST-3410-2001", str);
            configurableProvider.addAlgorithm("KeyAgreement.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.KeyAgreementSpi$ECVKO");
            StringBuilder sb = new StringBuilder();
            String str2 = "Alg.Alias.KeyAgreement.";
            sb.append(str2);
            sb.append(CryptoProObjectIdentifiers.gostR3410_2001);
            configurableProvider.addAlgorithm(sb.toString(), str);
            configurableProvider.addAlgorithm("Alg.Alias.KeyAgreement.GOST-3410-2001", str);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str2);
            sb2.append(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
            configurableProvider.addAlgorithm(sb2.toString(), str);
            configurableProvider.addAlgorithm("AlgorithmParameters.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.AlgorithmParametersSpi");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.GOST-3410-2001", str);
            ConfigurableProvider configurableProvider2 = configurableProvider;
            addSignatureAlgorithm(configurableProvider2, "GOST3411", "ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.SignatureSpi", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
            configurableProvider.addAlgorithm("KeyFactory.ECGOST3410-2012", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi");
            String str3 = "ECGOST3410-2012";
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410-2012", str3);
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.ECGOST-3410-2012", str3);
            registerOid(configurableProvider, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, str3, new org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi());
            registerOid(configurableProvider, RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256, str3, new org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi());
            registerOidAlgorithmParameters(configurableProvider, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, str3);
            registerOid(configurableProvider, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, str3, new org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi());
            registerOid(configurableProvider, RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512, str3, new org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi());
            registerOidAlgorithmParameters(configurableProvider, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, str3);
            configurableProvider.addAlgorithm("KeyPairGenerator.ECGOST3410-2012", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.ECGOST3410-2012", str3);
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410-2012", str3);
            configurableProvider.addAlgorithm("Signature.ECGOST3410-2012-256", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi256");
            String str4 = "ECGOST3410-2012-256";
            configurableProvider.addAlgorithm("Alg.Alias.Signature.ECGOST3410-2012-256", str4);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.GOST-3410-2012-256", str4);
            addSignatureAlgorithm(configurableProvider2, "GOST3411-2012-256", "ECGOST3410-2012-256", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi256", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256);
            configurableProvider.addAlgorithm("Signature.ECGOST3410-2012-512", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi512");
            String str5 = "ECGOST3410-2012-512";
            configurableProvider.addAlgorithm("Alg.Alias.Signature.ECGOST3410-2012-512", str5);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.GOST-3410-2012-512", str5);
            ConfigurableProvider configurableProvider3 = configurableProvider;
            addSignatureAlgorithm(configurableProvider3, "GOST3411-2012-512", "ECGOST3410-2012-512", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi512", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512);
            configurableProvider.addAlgorithm("KeyAgreement.ECGOST3410-2012-256", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyAgreementSpi$ECVKO256");
            configurableProvider.addAlgorithm("KeyAgreement.ECGOST3410-2012-512", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyAgreementSpi$ECVKO512");
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str2);
            sb3.append(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
            configurableProvider.addAlgorithm(sb3.toString(), str4);
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str2);
            sb4.append(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
            configurableProvider.addAlgorithm(sb4.toString(), str5);
            StringBuilder sb5 = new StringBuilder();
            sb5.append(str2);
            sb5.append(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256);
            configurableProvider.addAlgorithm(sb5.toString(), str4);
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str2);
            sb6.append(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512);
            configurableProvider.addAlgorithm(sb6.toString(), str5);
        }
    }
}
