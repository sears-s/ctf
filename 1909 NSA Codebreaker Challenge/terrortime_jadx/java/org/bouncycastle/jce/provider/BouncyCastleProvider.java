package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.newhope.NHKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.qtesla.QTESLAKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi;
import org.jivesoftware.smack.util.StringUtils;

public final class BouncyCastleProvider extends Provider implements ConfigurableProvider {
    private static final String[] ASYMMETRIC_CIPHERS = {"DSA", "DH", "EC", "RSA", "GOST", "ECGOST", "ElGamal", "DSTU4145", "GM", "EdEC"};
    private static final String[] ASYMMETRIC_GENERIC = {"X509", "IES"};
    private static final String ASYMMETRIC_PACKAGE = "org.bouncycastle.jcajce.provider.asymmetric.";
    public static final ProviderConfiguration CONFIGURATION = new BouncyCastleProviderConfiguration();
    private static final String[] DIGESTS = {"GOST3411", "Keccak", "MD2", "MD4", StringUtils.MD5, "SHA1", "RIPEMD128", "RIPEMD160", "RIPEMD256", "RIPEMD320", "SHA224", "SHA256", "SHA384", "SHA512", "SHA3", "Skein", "SM3", "Tiger", "Whirlpool", "Blake2b", "Blake2s", "DSTU7564"};
    private static final String DIGEST_PACKAGE = "org.bouncycastle.jcajce.provider.digest.";
    private static final String[] KEYSTORES = {"BC", "BCFKS", "PKCS12"};
    private static final String KEYSTORE_PACKAGE = "org.bouncycastle.jcajce.provider.keystore.";
    public static final String PROVIDER_NAME = "BC";
    private static final String[] SECURE_RANDOMS = {"DRBG"};
    private static final String SECURE_RANDOM_PACKAGE = "org.bouncycastle.jcajce.provider.drbg.";
    private static final String[] SYMMETRIC_CIPHERS = {"AES", "ARC4", "ARIA", "Blowfish", "Camellia", "CAST5", "CAST6", "ChaCha", "DES", "DESede", "GOST28147", "Grainv1", "Grain128", "HC128", "HC256", "IDEA", "Noekeon", "RC2", "RC5", "RC6", "Rijndael", "Salsa20", "SEED", "Serpent", "Shacal2", "Skipjack", "SM4", "TEA", "Twofish", "Threefish", "VMPC", "VMPCKSA3", "XTEA", "XSalsa20", "OpenSSLPBKDF", "DSTU7624", "GOST3412_2015"};
    private static final String[] SYMMETRIC_GENERIC = {"PBEPBKDF1", "PBEPBKDF2", "PBEPKCS12", "TLSKDF", "SCRYPT"};
    private static final String[] SYMMETRIC_MACS = {"SipHash", "Poly1305"};
    private static final String SYMMETRIC_PACKAGE = "org.bouncycastle.jcajce.provider.symmetric.";
    private static String info = "BouncyCastle Security Provider v1.62";
    private static final Map keyInfoConverters = new HashMap();

    public BouncyCastleProvider() {
        super("BC", 1.62d, info);
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                BouncyCastleProvider.this.setup();
                return null;
            }
        });
    }

    private static AsymmetricKeyInfoConverter getAsymmetricKeyInfoConverter(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        AsymmetricKeyInfoConverter asymmetricKeyInfoConverter;
        synchronized (keyInfoConverters) {
            asymmetricKeyInfoConverter = (AsymmetricKeyInfoConverter) keyInfoConverters.get(aSN1ObjectIdentifier);
        }
        return asymmetricKeyInfoConverter;
    }

    public static PrivateKey getPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        AsymmetricKeyInfoConverter asymmetricKeyInfoConverter = getAsymmetricKeyInfoConverter(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm());
        if (asymmetricKeyInfoConverter == null) {
            return null;
        }
        return asymmetricKeyInfoConverter.generatePrivate(privateKeyInfo);
    }

    public static PublicKey getPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        AsymmetricKeyInfoConverter asymmetricKeyInfoConverter = getAsymmetricKeyInfoConverter(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
        if (asymmetricKeyInfoConverter == null) {
            return null;
        }
        return asymmetricKeyInfoConverter.generatePublic(subjectPublicKeyInfo);
    }

    private void loadAlgorithms(String str, String[] strArr) {
        for (int i = 0; i != strArr.length; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(strArr[i]);
            sb.append("$Mappings");
            Class loadClass = ClassUtil.loadClass(BouncyCastleProvider.class, sb.toString());
            if (loadClass != null) {
                try {
                    ((AlgorithmProvider) loadClass.newInstance()).configure(this);
                } catch (Exception e) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("cannot create instance of ");
                    sb2.append(str);
                    sb2.append(strArr[i]);
                    sb2.append("$Mappings : ");
                    sb2.append(e);
                    throw new InternalError(sb2.toString());
                }
            }
        }
    }

    private void loadPQCKeys() {
        addKeyInfoConverter(PQCObjectIdentifiers.sphincs256, new Sphincs256KeyFactorySpi());
        addKeyInfoConverter(PQCObjectIdentifiers.newHope, new NHKeyFactorySpi());
        addKeyInfoConverter(PQCObjectIdentifiers.xmss, new XMSSKeyFactorySpi());
        addKeyInfoConverter(PQCObjectIdentifiers.xmss_mt, new XMSSMTKeyFactorySpi());
        addKeyInfoConverter(PQCObjectIdentifiers.mcEliece, new McElieceKeyFactorySpi());
        addKeyInfoConverter(PQCObjectIdentifiers.mcElieceCca2, new McElieceCCA2KeyFactorySpi());
        addKeyInfoConverter(PQCObjectIdentifiers.rainbow, new RainbowKeyFactorySpi());
        addKeyInfoConverter(PQCObjectIdentifiers.qTESLA_I, new QTESLAKeyFactorySpi());
        addKeyInfoConverter(PQCObjectIdentifiers.qTESLA_III_size, new QTESLAKeyFactorySpi());
        addKeyInfoConverter(PQCObjectIdentifiers.qTESLA_III_speed, new QTESLAKeyFactorySpi());
        addKeyInfoConverter(PQCObjectIdentifiers.qTESLA_p_I, new QTESLAKeyFactorySpi());
        addKeyInfoConverter(PQCObjectIdentifiers.qTESLA_p_III, new QTESLAKeyFactorySpi());
    }

    /* access modifiers changed from: private */
    public void setup() {
        loadAlgorithms(DIGEST_PACKAGE, DIGESTS);
        String[] strArr = SYMMETRIC_GENERIC;
        String str = SYMMETRIC_PACKAGE;
        loadAlgorithms(str, strArr);
        loadAlgorithms(str, SYMMETRIC_MACS);
        loadAlgorithms(str, SYMMETRIC_CIPHERS);
        String[] strArr2 = ASYMMETRIC_GENERIC;
        String str2 = ASYMMETRIC_PACKAGE;
        loadAlgorithms(str2, strArr2);
        loadAlgorithms(str2, ASYMMETRIC_CIPHERS);
        loadAlgorithms(KEYSTORE_PACKAGE, KEYSTORES);
        loadAlgorithms(SECURE_RANDOM_PACKAGE, SECURE_RANDOMS);
        loadPQCKeys();
        put("X509Store.CERTIFICATE/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCertCollection");
        put("X509Store.ATTRIBUTECERTIFICATE/COLLECTION", "org.bouncycastle.jce.provider.X509StoreAttrCertCollection");
        put("X509Store.CRL/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCRLCollection");
        put("X509Store.CERTIFICATEPAIR/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCertPairCollection");
        put("X509Store.CERTIFICATE/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCerts");
        put("X509Store.CRL/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCRLs");
        put("X509Store.ATTRIBUTECERTIFICATE/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPAttrCerts");
        put("X509Store.CERTIFICATEPAIR/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCertPairs");
        put("X509StreamParser.CERTIFICATE", "org.bouncycastle.jce.provider.X509CertParser");
        put("X509StreamParser.ATTRIBUTECERTIFICATE", "org.bouncycastle.jce.provider.X509AttrCertParser");
        put("X509StreamParser.CRL", "org.bouncycastle.jce.provider.X509CRLParser");
        put("X509StreamParser.CERTIFICATEPAIR", "org.bouncycastle.jce.provider.X509CertPairParser");
        put("Cipher.BROKENPBEWITHMD5ANDDES", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithMD5AndDES");
        put("Cipher.BROKENPBEWITHSHA1ANDDES", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithSHA1AndDES");
        put("Cipher.OLDPBEWITHSHAANDTWOFISH-CBC", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$OldPBEWithSHAAndTwofish");
        put("CertPathValidator.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathValidatorSpi");
        put("CertPathBuilder.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathBuilderSpi");
        String str3 = "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi";
        put("CertPathValidator.RFC3280", str3);
        String str4 = "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi";
        put("CertPathBuilder.RFC3280", str4);
        put("CertPathValidator.PKIX", str3);
        put("CertPathBuilder.PKIX", str4);
        put("CertStore.Collection", "org.bouncycastle.jce.provider.CertStoreCollectionSpi");
        put("CertStore.LDAP", "org.bouncycastle.jce.provider.X509LDAPCertStoreSpi");
        put("CertStore.Multi", "org.bouncycastle.jce.provider.MultiCertStoreSpi");
        put("Alg.Alias.CertStore.X509LDAP", "LDAP");
    }

    public void addAlgorithm(String str, String str2) {
        if (!containsKey(str)) {
            put(str, str2);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("duplicate provider key (");
        sb.append(str);
        sb.append(") found");
        throw new IllegalStateException(sb.toString());
    }

    public void addAlgorithm(String str, ASN1ObjectIdentifier aSN1ObjectIdentifier, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(".");
        sb.append(aSN1ObjectIdentifier);
        addAlgorithm(sb.toString(), str2);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append(".OID.");
        sb2.append(aSN1ObjectIdentifier);
        addAlgorithm(sb2.toString(), str2);
    }

    public void addAttributes(String str, Map<String, String> map) {
        for (String str2 : map.keySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(" ");
            sb.append(str2);
            String sb2 = sb.toString();
            if (!containsKey(sb2)) {
                put(sb2, map.get(str2));
            } else {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("duplicate provider attribute key (");
                sb3.append(sb2);
                sb3.append(") found");
                throw new IllegalStateException(sb3.toString());
            }
        }
    }

    public void addKeyInfoConverter(ASN1ObjectIdentifier aSN1ObjectIdentifier, AsymmetricKeyInfoConverter asymmetricKeyInfoConverter) {
        synchronized (keyInfoConverters) {
            keyInfoConverters.put(aSN1ObjectIdentifier, asymmetricKeyInfoConverter);
        }
    }

    public boolean hasAlgorithm(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        String str3 = ".";
        sb.append(str3);
        sb.append(str2);
        if (!containsKey(sb.toString())) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Alg.Alias.");
            sb2.append(str);
            sb2.append(str3);
            sb2.append(str2);
            if (!containsKey(sb2.toString())) {
                return false;
            }
        }
        return true;
    }

    public void setParameter(String str, Object obj) {
        synchronized (CONFIGURATION) {
            ((BouncyCastleProviderConfiguration) CONFIGURATION).setParameter(str, obj);
        }
    }
}
