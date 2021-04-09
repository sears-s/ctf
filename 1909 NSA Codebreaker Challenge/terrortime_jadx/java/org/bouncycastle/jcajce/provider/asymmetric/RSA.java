package org.bouncycastle.jcajce.provider.asymmetric;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.jivesoftware.smack.util.StringUtils;

public class RSA {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.rsa.";
    /* access modifiers changed from: private */
    public static final Map<String, String> generalRsaAttributes = new HashMap();

    public static class Mappings extends AsymmetricAlgorithmProvider {
        private void addDigestSignature(ConfigurableProvider configurableProvider, String str, String str2, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("WITHRSA");
            String sb2 = sb.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append("withRSA");
            String sb4 = sb3.toString();
            StringBuilder sb5 = new StringBuilder();
            sb5.append(str);
            sb5.append("WithRSA");
            String sb6 = sb5.toString();
            StringBuilder sb7 = new StringBuilder();
            sb7.append(str);
            sb7.append("/RSA");
            String sb8 = sb7.toString();
            StringBuilder sb9 = new StringBuilder();
            sb9.append(str);
            sb9.append("WITHRSAENCRYPTION");
            String sb10 = sb9.toString();
            StringBuilder sb11 = new StringBuilder();
            sb11.append(str);
            sb11.append("withRSAEncryption");
            String sb12 = sb11.toString();
            StringBuilder sb13 = new StringBuilder();
            sb13.append(str);
            sb13.append("WithRSAEncryption");
            String sb14 = sb13.toString();
            StringBuilder sb15 = new StringBuilder();
            sb15.append("Signature.");
            sb15.append(sb2);
            configurableProvider.addAlgorithm(sb15.toString(), str2);
            StringBuilder sb16 = new StringBuilder();
            String str3 = "Alg.Alias.Signature.";
            sb16.append(str3);
            sb16.append(sb4);
            configurableProvider.addAlgorithm(sb16.toString(), sb2);
            StringBuilder sb17 = new StringBuilder();
            sb17.append(str3);
            sb17.append(sb6);
            configurableProvider.addAlgorithm(sb17.toString(), sb2);
            StringBuilder sb18 = new StringBuilder();
            sb18.append(str3);
            sb18.append(sb10);
            configurableProvider.addAlgorithm(sb18.toString(), sb2);
            StringBuilder sb19 = new StringBuilder();
            sb19.append(str3);
            sb19.append(sb12);
            configurableProvider.addAlgorithm(sb19.toString(), sb2);
            StringBuilder sb20 = new StringBuilder();
            sb20.append(str3);
            sb20.append(sb14);
            configurableProvider.addAlgorithm(sb20.toString(), sb2);
            StringBuilder sb21 = new StringBuilder();
            sb21.append(str3);
            sb21.append(sb8);
            configurableProvider.addAlgorithm(sb21.toString(), sb2);
            if (aSN1ObjectIdentifier != null) {
                StringBuilder sb22 = new StringBuilder();
                sb22.append(str3);
                sb22.append(aSN1ObjectIdentifier);
                configurableProvider.addAlgorithm(sb22.toString(), sb2);
                StringBuilder sb23 = new StringBuilder();
                sb23.append("Alg.Alias.Signature.OID.");
                sb23.append(aSN1ObjectIdentifier);
                configurableProvider.addAlgorithm(sb23.toString(), sb2);
            }
        }

        private void addISO9796Signature(ConfigurableProvider configurableProvider, String str, String str2) {
            StringBuilder sb = new StringBuilder();
            String str3 = "Alg.Alias.Signature.";
            sb.append(str3);
            sb.append(str);
            sb.append("withRSA/ISO9796-2");
            String sb2 = sb.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            String str4 = "WITHRSA/ISO9796-2";
            sb3.append(str4);
            configurableProvider.addAlgorithm(sb2, sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str3);
            sb4.append(str);
            sb4.append("WithRSA/ISO9796-2");
            String sb5 = sb4.toString();
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str);
            sb6.append(str4);
            configurableProvider.addAlgorithm(sb5, sb6.toString());
            StringBuilder sb7 = new StringBuilder();
            sb7.append("Signature.");
            sb7.append(str);
            sb7.append(str4);
            configurableProvider.addAlgorithm(sb7.toString(), str2);
        }

        private void addPSSSignature(ConfigurableProvider configurableProvider, String str, String str2) {
            StringBuilder sb = new StringBuilder();
            String str3 = "Alg.Alias.Signature.";
            sb.append(str3);
            sb.append(str);
            sb.append("withRSA/PSS");
            String sb2 = sb.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            String str4 = "WITHRSAANDMGF1";
            sb3.append(str4);
            configurableProvider.addAlgorithm(sb2, sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str3);
            sb4.append(str);
            sb4.append("WithRSA/PSS");
            String sb5 = sb4.toString();
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str);
            sb6.append(str4);
            configurableProvider.addAlgorithm(sb5, sb6.toString());
            StringBuilder sb7 = new StringBuilder();
            sb7.append(str3);
            sb7.append(str);
            sb7.append("withRSAandMGF1");
            String sb8 = sb7.toString();
            StringBuilder sb9 = new StringBuilder();
            sb9.append(str);
            sb9.append(str4);
            configurableProvider.addAlgorithm(sb8, sb9.toString());
            StringBuilder sb10 = new StringBuilder();
            sb10.append(str3);
            sb10.append(str);
            sb10.append("WithRSAAndMGF1");
            String sb11 = sb10.toString();
            StringBuilder sb12 = new StringBuilder();
            sb12.append(str);
            sb12.append(str4);
            configurableProvider.addAlgorithm(sb11, sb12.toString());
            StringBuilder sb13 = new StringBuilder();
            sb13.append("Signature.");
            sb13.append(str);
            sb13.append(str4);
            configurableProvider.addAlgorithm(sb13.toString(), str2);
        }

        private void addX931Signature(ConfigurableProvider configurableProvider, String str, String str2) {
            StringBuilder sb = new StringBuilder();
            String str3 = "Alg.Alias.Signature.";
            sb.append(str3);
            sb.append(str);
            sb.append("withRSA/X9.31");
            String sb2 = sb.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            String str4 = "WITHRSA/X9.31";
            sb3.append(str4);
            configurableProvider.addAlgorithm(sb2, sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str3);
            sb4.append(str);
            sb4.append("WithRSA/X9.31");
            String sb5 = sb4.toString();
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str);
            sb6.append(str4);
            configurableProvider.addAlgorithm(sb5, sb6.toString());
            StringBuilder sb7 = new StringBuilder();
            sb7.append("Signature.");
            sb7.append(str);
            sb7.append(str4);
            configurableProvider.addAlgorithm(sb7.toString(), str2);
        }

        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameters.OAEP", "org.bouncycastle.jcajce.provider.asymmetric.rsa.AlgorithmParametersSpi$OAEP");
            configurableProvider.addAlgorithm("AlgorithmParameters.PSS", "org.bouncycastle.jcajce.provider.asymmetric.rsa.AlgorithmParametersSpi$PSS");
            String str = "PSS";
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.RSAPSS", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.RSASSA-PSS", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA224withRSA/PSS", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA256withRSA/PSS", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA384withRSA/PSS", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA512withRSA/PSS", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA224WITHRSAANDMGF1", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA256WITHRSAANDMGF1", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA384WITHRSAANDMGF1", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA512WITHRSAANDMGF1", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-224WITHRSAANDMGF1", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-256WITHRSAANDMGF1", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-384WITHRSAANDMGF1", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-512WITHRSAANDMGF1", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.RAWRSAPSS", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.NONEWITHRSAPSS", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.NONEWITHRSASSA-PSS", str);
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.NONEWITHRSAANDMGF1", str);
            configurableProvider.addAttributes("Cipher.RSA", RSA.generalRsaAttributes);
            configurableProvider.addAlgorithm("Cipher.RSA", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$NoPadding");
            configurableProvider.addAlgorithm("Cipher.RSA/RAW", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$NoPadding");
            String str2 = "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding";
            configurableProvider.addAlgorithm("Cipher.RSA/PKCS1", str2);
            String str3 = "Cipher";
            configurableProvider.addAlgorithm(str3, PKCSObjectIdentifiers.rsaEncryption, str2);
            configurableProvider.addAlgorithm(str3, X509ObjectIdentifiers.id_ea_rsa, str2);
            configurableProvider.addAlgorithm("Cipher.RSA/1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding_PrivateOnly");
            configurableProvider.addAlgorithm("Cipher.RSA/2", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding_PublicOnly");
            configurableProvider.addAlgorithm("Cipher.RSA/OAEP", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$OAEPPadding");
            configurableProvider.addAlgorithm(str3, PKCSObjectIdentifiers.id_RSAES_OAEP, "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$OAEPPadding");
            configurableProvider.addAlgorithm("Cipher.RSA/ISO9796-1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$ISO9796d1Padding");
            String str4 = "RSA";
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.RSA//RAW", str4);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.RSA//NOPADDING", str4);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.RSA//PKCS1PADDING", "RSA/PKCS1");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.RSA//OAEPPADDING", "RSA/OAEP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.RSA//ISO9796-1PADDING", "RSA/ISO9796-1");
            configurableProvider.addAlgorithm("KeyFactory.RSA", "org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.RSA", "org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyPairGeneratorSpi");
            KeyFactorySpi keyFactorySpi = new KeyFactorySpi();
            registerOid(configurableProvider, PKCSObjectIdentifiers.rsaEncryption, str4, keyFactorySpi);
            registerOid(configurableProvider, X509ObjectIdentifiers.id_ea_rsa, str4, keyFactorySpi);
            registerOid(configurableProvider, PKCSObjectIdentifiers.id_RSAES_OAEP, str4, keyFactorySpi);
            registerOid(configurableProvider, PKCSObjectIdentifiers.id_RSASSA_PSS, str4, keyFactorySpi);
            registerOidAlgorithmParameters(configurableProvider, PKCSObjectIdentifiers.rsaEncryption, str4);
            registerOidAlgorithmParameters(configurableProvider, X509ObjectIdentifiers.id_ea_rsa, str4);
            registerOidAlgorithmParameters(configurableProvider, PKCSObjectIdentifiers.id_RSAES_OAEP, "OAEP");
            registerOidAlgorithmParameters(configurableProvider, PKCSObjectIdentifiers.id_RSASSA_PSS, str);
            String str5 = "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$PSSwithRSA";
            configurableProvider.addAlgorithm("Signature.RSASSA-PSS", str5);
            StringBuilder sb = new StringBuilder();
            sb.append("Signature.");
            sb.append(PKCSObjectIdentifiers.id_RSASSA_PSS);
            configurableProvider.addAlgorithm(sb.toString(), str5);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Signature.OID.");
            sb2.append(PKCSObjectIdentifiers.id_RSASSA_PSS);
            configurableProvider.addAlgorithm(sb2.toString(), str5);
            configurableProvider.addAlgorithm("Signature.RSA", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$noneRSA");
            configurableProvider.addAlgorithm("Signature.RAWRSASSA-PSS", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$nonePSS");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.RAWRSA", str4);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.NONEWITHRSA", str4);
            String str6 = "RAWRSASSA-PSS";
            configurableProvider.addAlgorithm("Alg.Alias.Signature.RAWRSAPSS", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.NONEWITHRSAPSS", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.NONEWITHRSASSA-PSS", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.NONEWITHRSAANDMGF1", str6);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.RSAPSS", "RSASSA-PSS");
            String str7 = "SHA224";
            addPSSSignature(configurableProvider, str7, "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA224withRSA");
            String str8 = "SHA256";
            addPSSSignature(configurableProvider, str8, "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA256withRSA");
            String str9 = "SHA384";
            addPSSSignature(configurableProvider, str9, "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA384withRSA");
            String str10 = "SHA512";
            addPSSSignature(configurableProvider, str10, "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512withRSA");
            String str11 = "SHA512(224)";
            addPSSSignature(configurableProvider, str11, "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512_224withRSA");
            String str12 = "SHA512(256)";
            addPSSSignature(configurableProvider, str12, "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512_256withRSA");
            addPSSSignature(configurableProvider, "SHA3-224", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_224withRSA");
            addPSSSignature(configurableProvider, "SHA3-256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_256withRSA");
            addPSSSignature(configurableProvider, "SHA3-384", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_384withRSA");
            addPSSSignature(configurableProvider, "SHA3-512", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_512withRSA");
            String str13 = "MessageDigest";
            if (configurableProvider.hasAlgorithm(str13, "MD2")) {
                addDigestSignature(configurableProvider, "MD2", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$MD2", PKCSObjectIdentifiers.md2WithRSAEncryption);
            }
            if (configurableProvider.hasAlgorithm(str13, "MD4")) {
                addDigestSignature(configurableProvider, "MD4", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$MD4", PKCSObjectIdentifiers.md4WithRSAEncryption);
            }
            String str14 = StringUtils.MD5;
            if (configurableProvider.hasAlgorithm(str13, str14)) {
                addDigestSignature(configurableProvider, str14, "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$MD5", PKCSObjectIdentifiers.md5WithRSAEncryption);
                addISO9796Signature(configurableProvider, str14, "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$MD5WithRSAEncryption");
            }
            String str15 = "SHA1";
            if (configurableProvider.hasAlgorithm(str13, str15)) {
                configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA1withRSA/PSS", str);
                configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA1WITHRSAANDMGF1", str);
                addPSSSignature(configurableProvider, str15, "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA1withRSA");
                addDigestSignature(configurableProvider, str15, "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA1", PKCSObjectIdentifiers.sha1WithRSAEncryption);
                addISO9796Signature(configurableProvider, str15, "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA1WithRSAEncryption");
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Alg.Alias.Signature.");
                sb3.append(OIWObjectIdentifiers.sha1WithRSA);
                configurableProvider.addAlgorithm(sb3.toString(), "SHA1WITHRSA");
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Alg.Alias.Signature.OID.");
                sb4.append(OIWObjectIdentifiers.sha1WithRSA);
                configurableProvider.addAlgorithm(sb4.toString(), "SHA1WITHRSA");
                addX931Signature(configurableProvider, str15, "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA1WithRSAEncryption");
            }
            addDigestSignature(configurableProvider, str7, "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA224", PKCSObjectIdentifiers.sha224WithRSAEncryption);
            addDigestSignature(configurableProvider, str8, "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA256", PKCSObjectIdentifiers.sha256WithRSAEncryption);
            addDigestSignature(configurableProvider, str9, "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA384", PKCSObjectIdentifiers.sha384WithRSAEncryption);
            addDigestSignature(configurableProvider, str10, "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA512", PKCSObjectIdentifiers.sha512WithRSAEncryption);
            addDigestSignature(configurableProvider, str11, "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA512_224", PKCSObjectIdentifiers.sha512_224WithRSAEncryption);
            addDigestSignature(configurableProvider, str12, "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA512_256", PKCSObjectIdentifiers.sha512_256WithRSAEncryption);
            addDigestSignature(configurableProvider, "SHA3-224", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_224", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224);
            addDigestSignature(configurableProvider, "SHA3-256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_256", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256);
            addDigestSignature(configurableProvider, "SHA3-384", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_384", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384);
            addDigestSignature(configurableProvider, "SHA3-512", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_512", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512);
            addISO9796Signature(configurableProvider, str7, "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA224WithRSAEncryption");
            addISO9796Signature(configurableProvider, str8, "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA256WithRSAEncryption");
            addISO9796Signature(configurableProvider, str9, "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA384WithRSAEncryption");
            addISO9796Signature(configurableProvider, str10, "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA512WithRSAEncryption");
            addISO9796Signature(configurableProvider, str11, "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA512_224WithRSAEncryption");
            addISO9796Signature(configurableProvider, str12, "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA512_256WithRSAEncryption");
            addX931Signature(configurableProvider, str7, "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA224WithRSAEncryption");
            addX931Signature(configurableProvider, str8, "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA256WithRSAEncryption");
            addX931Signature(configurableProvider, str9, "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA384WithRSAEncryption");
            addX931Signature(configurableProvider, str10, "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA512WithRSAEncryption");
            addX931Signature(configurableProvider, str11, "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA512_224WithRSAEncryption");
            addX931Signature(configurableProvider, str12, "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA512_256WithRSAEncryption");
            if (configurableProvider.hasAlgorithm(str13, "RIPEMD128")) {
                addDigestSignature(configurableProvider, "RIPEMD128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD128", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
                addDigestSignature(configurableProvider, "RMD128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD128", null);
                addX931Signature(configurableProvider, "RMD128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD128WithRSAEncryption");
                addX931Signature(configurableProvider, "RIPEMD128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD128WithRSAEncryption");
            }
            if (configurableProvider.hasAlgorithm(str13, "RIPEMD160")) {
                addDigestSignature(configurableProvider, "RIPEMD160", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD160", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
                addDigestSignature(configurableProvider, "RMD160", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD160", null);
                configurableProvider.addAlgorithm("Alg.Alias.Signature.RIPEMD160WithRSA/ISO9796-2", "RIPEMD160withRSA/ISO9796-2");
                configurableProvider.addAlgorithm("Signature.RIPEMD160withRSA/ISO9796-2", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$RIPEMD160WithRSAEncryption");
                addX931Signature(configurableProvider, "RMD160", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD160WithRSAEncryption");
                addX931Signature(configurableProvider, "RIPEMD160", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD160WithRSAEncryption");
            }
            if (configurableProvider.hasAlgorithm(str13, "RIPEMD256")) {
                addDigestSignature(configurableProvider, "RIPEMD256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD256", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
                addDigestSignature(configurableProvider, "RMD256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD256", null);
            }
            if (configurableProvider.hasAlgorithm(str13, "WHIRLPOOL")) {
                addISO9796Signature(configurableProvider, "Whirlpool", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$WhirlpoolWithRSAEncryption");
                addISO9796Signature(configurableProvider, "WHIRLPOOL", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$WhirlpoolWithRSAEncryption");
                addX931Signature(configurableProvider, "Whirlpool", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$WhirlpoolWithRSAEncryption");
                addX931Signature(configurableProvider, "WHIRLPOOL", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$WhirlpoolWithRSAEncryption");
            }
        }
    }

    static {
        generalRsaAttributes.put("SupportedKeyClasses", "javax.crypto.interfaces.RSAPublicKey|javax.crypto.interfaces.RSAPrivateKey");
        generalRsaAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
    }
}
