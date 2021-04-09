package org.bouncycastle.jcajce.provider.asymmetric;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi.ED25519;
import org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi.ED448;
import org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi.X25519;
import org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi.X448;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class EdEC {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.edec.";
    private static final Map<String, String> edxAttributes = new HashMap();

    public static class Mappings extends AsymmetricAlgorithmProvider {
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.XDH", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$XDH");
            configurableProvider.addAlgorithm("KeyFactory.X448", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$X448");
            configurableProvider.addAlgorithm("KeyFactory.X25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$X25519");
            configurableProvider.addAlgorithm("KeyFactory.EDDSA", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$EDDSA");
            configurableProvider.addAlgorithm("KeyFactory.ED448", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$ED448");
            configurableProvider.addAlgorithm("KeyFactory.ED25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$ED25519");
            configurableProvider.addAlgorithm("Signature.EDDSA", "org.bouncycastle.jcajce.provider.asymmetric.edec.SignatureSpi$EdDSA");
            String str = "org.bouncycastle.jcajce.provider.asymmetric.edec.SignatureSpi$Ed448";
            configurableProvider.addAlgorithm("Signature.ED448", str);
            String str2 = "org.bouncycastle.jcajce.provider.asymmetric.edec.SignatureSpi$Ed25519";
            configurableProvider.addAlgorithm("Signature.ED25519", str2);
            String str3 = "Signature";
            configurableProvider.addAlgorithm(str3, EdECObjectIdentifiers.id_Ed448, str);
            configurableProvider.addAlgorithm(str3, EdECObjectIdentifiers.id_Ed25519, str2);
            configurableProvider.addAlgorithm("KeyPairGenerator.EDDSA", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$EdDSA");
            String str4 = "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$Ed448";
            configurableProvider.addAlgorithm("KeyPairGenerator.ED448", str4);
            String str5 = "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$Ed25519";
            configurableProvider.addAlgorithm("KeyPairGenerator.ED25519", str5);
            String str6 = "KeyPairGenerator";
            configurableProvider.addAlgorithm(str6, EdECObjectIdentifiers.id_Ed448, str4);
            configurableProvider.addAlgorithm(str6, EdECObjectIdentifiers.id_Ed25519, str5);
            configurableProvider.addAlgorithm("KeyAgreement.XDH", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$XDH");
            String str7 = "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448";
            configurableProvider.addAlgorithm("KeyAgreement.X448", str7);
            String str8 = "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519";
            configurableProvider.addAlgorithm("KeyAgreement.X25519", str8);
            String str9 = "KeyAgreement";
            configurableProvider.addAlgorithm(str9, EdECObjectIdentifiers.id_X448, str7);
            configurableProvider.addAlgorithm(str9, EdECObjectIdentifiers.id_X25519, str8);
            configurableProvider.addAlgorithm("KeyAgreement.X25519WITHSHA256CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519withSHA256CKDF");
            configurableProvider.addAlgorithm("KeyAgreement.X25519WITHSHA384CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519withSHA384CKDF");
            configurableProvider.addAlgorithm("KeyAgreement.X25519WITHSHA512CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519withSHA512CKDF");
            configurableProvider.addAlgorithm("KeyAgreement.X448WITHSHA256CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448withSHA256CKDF");
            configurableProvider.addAlgorithm("KeyAgreement.X448WITHSHA384CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448withSHA384CKDF");
            configurableProvider.addAlgorithm("KeyAgreement.X448WITHSHA512CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448withSHA512CKDF");
            configurableProvider.addAlgorithm("KeyAgreement.X25519WITHSHA256KDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519withSHA256KDF");
            configurableProvider.addAlgorithm("KeyAgreement.X448WITHSHA512KDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448withSHA512KDF");
            configurableProvider.addAlgorithm("KeyAgreement.X25519UWITHSHA256KDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519UwithSHA256KDF");
            configurableProvider.addAlgorithm("KeyAgreement.X448UWITHSHA512KDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448UwithSHA512KDF");
            configurableProvider.addAlgorithm("KeyPairGenerator.XDH", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$XDH");
            configurableProvider.addAlgorithm("KeyPairGenerator.X448", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$X448");
            configurableProvider.addAlgorithm("KeyPairGenerator.X25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$X25519");
            configurableProvider.addAlgorithm(str6, EdECObjectIdentifiers.id_X448, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpiSpi$X448");
            configurableProvider.addAlgorithm(str6, EdECObjectIdentifiers.id_X25519, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpiSpi$X25519");
            String str10 = "XDH";
            registerOid(configurableProvider, EdECObjectIdentifiers.id_X448, str10, new X448());
            registerOid(configurableProvider, EdECObjectIdentifiers.id_X25519, str10, new X25519());
            String str11 = "EDDSA";
            registerOid(configurableProvider, EdECObjectIdentifiers.id_Ed448, str11, new ED448());
            registerOid(configurableProvider, EdECObjectIdentifiers.id_Ed25519, str11, new ED25519());
        }
    }

    static {
        edxAttributes.put("SupportedKeyClasses", "java.security.interfaces.ECPublicKey|java.security.interfaces.ECPrivateKey");
        edxAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
    }
}
