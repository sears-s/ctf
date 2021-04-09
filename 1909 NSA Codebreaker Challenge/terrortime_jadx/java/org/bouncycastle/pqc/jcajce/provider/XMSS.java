package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi;

public class XMSS {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.xmss.";

    public static class Mappings extends AsymmetricAlgorithmProvider {
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyPairGeneratorSpi");
            addSignatureAlgorithm(configurableProvider, "XMSS-SHA256", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withSha256", BCObjectIdentifiers.xmss_SHA256);
            addSignatureAlgorithm(configurableProvider, "XMSS-SHAKE128", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake128", BCObjectIdentifiers.xmss_SHAKE128);
            addSignatureAlgorithm(configurableProvider, "XMSS-SHA512", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withSha512", BCObjectIdentifiers.xmss_SHA512);
            addSignatureAlgorithm(configurableProvider, "XMSS-SHAKE256", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake256", BCObjectIdentifiers.xmss_SHAKE256);
            ConfigurableProvider configurableProvider2 = configurableProvider;
            addSignatureAlgorithm(configurableProvider2, "SHA256", "XMSS-SHA256", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withSha256andPrehash", BCObjectIdentifiers.xmss_SHA256ph);
            addSignatureAlgorithm(configurableProvider2, "SHAKE128", "XMSS-SHAKE128", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake128andPrehash", BCObjectIdentifiers.xmss_SHAKE128ph);
            addSignatureAlgorithm(configurableProvider2, "SHA512", "XMSS-SHA512", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withSha512andPrehash", BCObjectIdentifiers.xmss_SHA512ph);
            addSignatureAlgorithm(configurableProvider2, "SHAKE256", "XMSS-SHAKE256", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake256andPrehash", BCObjectIdentifiers.xmss_SHAKE256ph);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHA256WITHXMSS", "SHA256WITHXMSS-SHA256");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHAKE128WITHXMSS", "SHAKE128WITHXMSS-SHAKE128");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHA512WITHXMSS", "SHA512WITHXMSS-SHA512");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHAKE256WITHXMSS", "SHAKE256WITHXMSS-SHAKE256");
            configurableProvider.addAlgorithm("KeyFactory.XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyPairGeneratorSpi");
            addSignatureAlgorithm(configurableProvider, "XMSSMT-SHA256", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withSha256", BCObjectIdentifiers.xmss_mt_SHA256);
            addSignatureAlgorithm(configurableProvider, "XMSSMT-SHAKE128", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake128", BCObjectIdentifiers.xmss_mt_SHAKE128);
            addSignatureAlgorithm(configurableProvider, "XMSSMT-SHA512", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withSha512", BCObjectIdentifiers.xmss_mt_SHA512);
            addSignatureAlgorithm(configurableProvider, "XMSSMT-SHAKE256", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake256", BCObjectIdentifiers.xmss_mt_SHAKE256);
            ConfigurableProvider configurableProvider3 = configurableProvider;
            addSignatureAlgorithm(configurableProvider3, "SHA256", "XMSSMT-SHA256", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withSha256andPrehash", BCObjectIdentifiers.xmss_mt_SHA256ph);
            addSignatureAlgorithm(configurableProvider3, "SHAKE128", "XMSSMT-SHAKE128", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake128andPrehash", BCObjectIdentifiers.xmss_mt_SHAKE128ph);
            addSignatureAlgorithm(configurableProvider3, "SHA512", "XMSSMT-SHA512", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withSha512andPrehash", BCObjectIdentifiers.xmss_mt_SHA512ph);
            addSignatureAlgorithm(configurableProvider3, "SHAKE256", "XMSSMT-SHAKE256", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake256andPrehash", BCObjectIdentifiers.xmss_mt_SHAKE256ph);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHA256WITHXMSSMT", "SHA256WITHXMSSMT-SHA256");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHAKE128WITHXMSSMT", "SHAKE128WITHXMSSMT-SHAKE128");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHA512WITHXMSSMT", "SHA512WITHXMSSMT-SHA512");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHAKE256WITHXMSSMT", "SHAKE256WITHXMSSMT-SHAKE256");
            registerOid(configurableProvider, PQCObjectIdentifiers.xmss, "XMSS", new XMSSKeyFactorySpi());
            registerOid(configurableProvider, PQCObjectIdentifiers.xmss_mt, "XMSSMT", new XMSSMTKeyFactorySpi());
        }
    }
}
