package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSAUtil;
import org.bouncycastle.jcajce.provider.asymmetric.dsa.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class DSA {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.dsa.";

    public static class Mappings extends AsymmetricAlgorithmProvider {
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameters.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.AlgorithmParametersSpi");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.AlgorithmParameterGeneratorSpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.KeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("KeyFactory.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.KeyFactorySpi");
            configurableProvider.addAlgorithm("Signature.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$stdDSA");
            configurableProvider.addAlgorithm("Signature.NONEWITHDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$noneDSA");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.RAWDSA", "NONEWITHDSA");
            String str = "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA";
            configurableProvider.addAlgorithm("Signature.DETDSA", str);
            configurableProvider.addAlgorithm("Signature.SHA1WITHDETDSA", str);
            String str2 = "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA224";
            configurableProvider.addAlgorithm("Signature.SHA224WITHDETDSA", str2);
            String str3 = "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA256";
            configurableProvider.addAlgorithm("Signature.SHA256WITHDETDSA", str3);
            String str4 = "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA384";
            configurableProvider.addAlgorithm("Signature.SHA384WITHDETDSA", str4);
            String str5 = "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA512";
            configurableProvider.addAlgorithm("Signature.SHA512WITHDETDSA", str5);
            configurableProvider.addAlgorithm("Signature.DDSA", str);
            configurableProvider.addAlgorithm("Signature.SHA1WITHDDSA", str);
            configurableProvider.addAlgorithm("Signature.SHA224WITHDDSA", str2);
            configurableProvider.addAlgorithm("Signature.SHA256WITHDDSA", str3);
            configurableProvider.addAlgorithm("Signature.SHA384WITHDDSA", str4);
            configurableProvider.addAlgorithm("Signature.SHA512WITHDDSA", str5);
            configurableProvider.addAlgorithm("Signature.SHA3-224WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_224");
            configurableProvider.addAlgorithm("Signature.SHA3-256WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_256");
            configurableProvider.addAlgorithm("Signature.SHA3-384WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_384");
            configurableProvider.addAlgorithm("Signature.SHA3-512WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_512");
            ConfigurableProvider configurableProvider2 = configurableProvider;
            addSignatureAlgorithm(configurableProvider2, "SHA224", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa224", NISTObjectIdentifiers.dsa_with_sha224);
            ConfigurableProvider configurableProvider3 = configurableProvider;
            addSignatureAlgorithm(configurableProvider3, "SHA256", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa256", NISTObjectIdentifiers.dsa_with_sha256);
            ConfigurableProvider configurableProvider4 = configurableProvider;
            addSignatureAlgorithm(configurableProvider4, "SHA384", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa384", NISTObjectIdentifiers.dsa_with_sha384);
            ConfigurableProvider configurableProvider5 = configurableProvider;
            addSignatureAlgorithm(configurableProvider5, "SHA512", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa512", NISTObjectIdentifiers.dsa_with_sha512);
            addSignatureAlgorithm(configurableProvider4, "SHA3-224", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_224", NISTObjectIdentifiers.id_dsa_with_sha3_224);
            addSignatureAlgorithm(configurableProvider5, "SHA3-256", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_256", NISTObjectIdentifiers.id_dsa_with_sha3_256);
            addSignatureAlgorithm(configurableProvider4, "SHA3-384", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_384", NISTObjectIdentifiers.id_dsa_with_sha3_384);
            addSignatureAlgorithm(configurableProvider5, "SHA3-512", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_512", NISTObjectIdentifiers.id_dsa_with_sha3_512);
            String str6 = "DSA";
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHA/DSA", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHA1withDSA", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHA1WITHDSA", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.1.3.14.3.2.26with1.2.840.10040.4.1", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.1.3.14.3.2.26with1.2.840.10040.4.3", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.DSAwithSHA1", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.DSAWITHSHA1", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHA1WithDSA", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.DSAWithSHA1", str6);
            KeyFactorySpi keyFactorySpi = new KeyFactorySpi();
            for (int i = 0; i != DSAUtil.dsaOids.length; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append("Alg.Alias.Signature.");
                sb.append(DSAUtil.dsaOids[i]);
                configurableProvider.addAlgorithm(sb.toString(), str6);
                registerOid(configurableProvider, DSAUtil.dsaOids[i], str6, keyFactorySpi);
                registerOidAlgorithmParameterGenerator(configurableProvider, DSAUtil.dsaOids[i], str6);
            }
        }
    }
}
