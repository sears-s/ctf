package org.bouncycastle.jcajce.provider.asymmetric;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class GM {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.ec.";
    private static final Map<String, String> generalSm2Attributes = new HashMap();

    public static class Mappings extends AsymmetricAlgorithmProvider {
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Signature.SHA256WITHSM2", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMSignatureSpi$sha256WithSM2");
            StringBuilder sb = new StringBuilder();
            String str = "Alg.Alias.Signature.";
            sb.append(str);
            sb.append(GMObjectIdentifiers.sm2sign_with_sha256);
            configurableProvider.addAlgorithm(sb.toString(), "SHA256WITHSM2");
            configurableProvider.addAlgorithm("Signature.SM3WITHSM2", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMSignatureSpi$sm3WithSM2");
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(GMObjectIdentifiers.sm2sign_with_sm3);
            configurableProvider.addAlgorithm(sb2.toString(), "SM3WITHSM2");
            configurableProvider.addAlgorithm("Cipher.SM2", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2");
            String str2 = "SM2";
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.SM2WITHSM3", str2);
            StringBuilder sb3 = new StringBuilder();
            String str3 = "Alg.Alias.Cipher.";
            sb3.append(str3);
            sb3.append(GMObjectIdentifiers.sm2encrypt_with_sm3);
            configurableProvider.addAlgorithm(sb3.toString(), str2);
            configurableProvider.addAlgorithm("Cipher.SM2WITHBLAKE2B", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withBlake2b");
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str3);
            sb4.append(GMObjectIdentifiers.sm2encrypt_with_blake2b512);
            configurableProvider.addAlgorithm(sb4.toString(), "SM2WITHBLAKE2B");
            configurableProvider.addAlgorithm("Cipher.SM2WITHBLAKE2S", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withBlake2s");
            StringBuilder sb5 = new StringBuilder();
            sb5.append(str3);
            sb5.append(GMObjectIdentifiers.sm2encrypt_with_blake2s256);
            configurableProvider.addAlgorithm(sb5.toString(), "SM2WITHBLAKE2S");
            configurableProvider.addAlgorithm("Cipher.SM2WITHWHIRLPOOL", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withWhirlpool");
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str3);
            sb6.append(GMObjectIdentifiers.sm2encrypt_with_whirlpool);
            configurableProvider.addAlgorithm(sb6.toString(), "SM2WITHWHIRLPOOL");
            configurableProvider.addAlgorithm("Cipher.SM2WITHMD5", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withMD5");
            StringBuilder sb7 = new StringBuilder();
            sb7.append(str3);
            sb7.append(GMObjectIdentifiers.sm2encrypt_with_md5);
            configurableProvider.addAlgorithm(sb7.toString(), "SM2WITHMD5");
            configurableProvider.addAlgorithm("Cipher.SM2WITHRIPEMD160", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withRMD");
            StringBuilder sb8 = new StringBuilder();
            sb8.append(str3);
            sb8.append(GMObjectIdentifiers.sm2encrypt_with_rmd160);
            configurableProvider.addAlgorithm(sb8.toString(), "SM2WITHRIPEMD160");
            configurableProvider.addAlgorithm("Cipher.SM2WITHSHA1", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withSha1");
            StringBuilder sb9 = new StringBuilder();
            sb9.append(str3);
            sb9.append(GMObjectIdentifiers.sm2encrypt_with_sha1);
            configurableProvider.addAlgorithm(sb9.toString(), "SM2WITHSHA1");
            configurableProvider.addAlgorithm("Cipher.SM2WITHSHA224", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withSha224");
            StringBuilder sb10 = new StringBuilder();
            sb10.append(str3);
            sb10.append(GMObjectIdentifiers.sm2encrypt_with_sha224);
            configurableProvider.addAlgorithm(sb10.toString(), "SM2WITHSHA224");
            configurableProvider.addAlgorithm("Cipher.SM2WITHSHA256", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withSha256");
            StringBuilder sb11 = new StringBuilder();
            sb11.append(str3);
            sb11.append(GMObjectIdentifiers.sm2encrypt_with_sha256);
            configurableProvider.addAlgorithm(sb11.toString(), "SM2WITHSHA256");
            configurableProvider.addAlgorithm("Cipher.SM2WITHSHA384", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withSha384");
            StringBuilder sb12 = new StringBuilder();
            sb12.append(str3);
            sb12.append(GMObjectIdentifiers.sm2encrypt_with_sha384);
            configurableProvider.addAlgorithm(sb12.toString(), "SM2WITHSHA384");
            configurableProvider.addAlgorithm("Cipher.SM2WITHSHA512", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withSha512");
            StringBuilder sb13 = new StringBuilder();
            sb13.append(str3);
            sb13.append(GMObjectIdentifiers.sm2encrypt_with_sha512);
            configurableProvider.addAlgorithm(sb13.toString(), "SM2WITHSHA512");
        }
    }

    static {
        generalSm2Attributes.put("SupportedKeyClasses", "java.security.interfaces.ECPublicKey|java.security.interfaces.ECPrivateKey");
        generalSm2Attributes.put("SupportedKeyFormats", "PKCS#8|X.509");
    }
}
