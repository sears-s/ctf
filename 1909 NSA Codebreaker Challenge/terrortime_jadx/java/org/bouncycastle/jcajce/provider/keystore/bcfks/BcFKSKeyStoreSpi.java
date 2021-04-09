package org.bouncycastle.jcajce.provider.keystore.bcfks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore.CallbackHandlerProtection;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAKey;
import java.security.interfaces.RSAKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.bc.EncryptedObjectStoreData;
import org.bouncycastle.asn1.bc.EncryptedPrivateKeyData;
import org.bouncycastle.asn1.bc.EncryptedSecretKeyData;
import org.bouncycastle.asn1.bc.ObjectData;
import org.bouncycastle.asn1.bc.ObjectDataSequence;
import org.bouncycastle.asn1.bc.ObjectStore;
import org.bouncycastle.asn1.bc.ObjectStoreData;
import org.bouncycastle.asn1.bc.ObjectStoreIntegrityCheck;
import org.bouncycastle.asn1.bc.PbkdMacIntegrityCheck;
import org.bouncycastle.asn1.bc.SecretKeyData;
import org.bouncycastle.asn1.bc.SignatureCheck;
import org.bouncycastle.asn1.cms.CCMParameters;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.ScryptParams;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.PBKDF2Config;
import org.bouncycastle.crypto.util.PBKDFConfig;
import org.bouncycastle.crypto.util.ScryptConfig;
import org.bouncycastle.jcajce.BCFKSLoadStoreParameter;
import org.bouncycastle.jcajce.BCFKSLoadStoreParameter.CertChainValidator;
import org.bouncycastle.jcajce.BCFKSLoadStoreParameter.EncryptionAlgorithm;
import org.bouncycastle.jcajce.BCFKSLoadStoreParameter.MacAlgorithm;
import org.bouncycastle.jcajce.BCFKSLoadStoreParameter.SignatureAlgorithm;
import org.bouncycastle.jcajce.BCFKSStoreParameter;
import org.bouncycastle.jcajce.BCLoadStoreParameter;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

class BcFKSKeyStoreSpi extends KeyStoreSpi {
    private static final BigInteger CERTIFICATE = BigInteger.valueOf(0);
    private static final BigInteger PRIVATE_KEY = BigInteger.valueOf(1);
    private static final BigInteger PROTECTED_PRIVATE_KEY = BigInteger.valueOf(3);
    private static final BigInteger PROTECTED_SECRET_KEY = BigInteger.valueOf(4);
    private static final BigInteger SECRET_KEY = BigInteger.valueOf(2);
    private static final Map<String, ASN1ObjectIdentifier> oidMap = new HashMap();
    private static final Map<ASN1ObjectIdentifier, String> publicAlgMap = new HashMap();
    private Date creationDate;
    private final Map<String, ObjectData> entries = new HashMap();
    private final JcaJceHelper helper;
    private AlgorithmIdentifier hmacAlgorithm;
    private KeyDerivationFunc hmacPkbdAlgorithm;
    private Date lastModifiedDate;
    private final Map<String, PrivateKey> privateKeyCache = new HashMap();
    private AlgorithmIdentifier signatureAlgorithm;
    private ASN1ObjectIdentifier storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_CCM;
    private CertChainValidator validator;
    private PublicKey verificationKey;

    public static class Def extends BcFKSKeyStoreSpi {
        public Def() {
            super(new DefaultJcaJceHelper());
        }

        public /* bridge */ /* synthetic */ Enumeration engineAliases() {
            return BcFKSKeyStoreSpi.super.engineAliases();
        }

        public /* bridge */ /* synthetic */ boolean engineContainsAlias(String str) {
            return BcFKSKeyStoreSpi.super.engineContainsAlias(str);
        }

        public /* bridge */ /* synthetic */ void engineDeleteEntry(String str) throws KeyStoreException {
            BcFKSKeyStoreSpi.super.engineDeleteEntry(str);
        }

        public /* bridge */ /* synthetic */ Certificate engineGetCertificate(String str) {
            return BcFKSKeyStoreSpi.super.engineGetCertificate(str);
        }

        public /* bridge */ /* synthetic */ String engineGetCertificateAlias(Certificate certificate) {
            return BcFKSKeyStoreSpi.super.engineGetCertificateAlias(certificate);
        }

        public /* bridge */ /* synthetic */ Certificate[] engineGetCertificateChain(String str) {
            return BcFKSKeyStoreSpi.super.engineGetCertificateChain(str);
        }

        public /* bridge */ /* synthetic */ Date engineGetCreationDate(String str) {
            return BcFKSKeyStoreSpi.super.engineGetCreationDate(str);
        }

        public /* bridge */ /* synthetic */ Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            return BcFKSKeyStoreSpi.super.engineGetKey(str, cArr);
        }

        public /* bridge */ /* synthetic */ boolean engineIsCertificateEntry(String str) {
            return BcFKSKeyStoreSpi.super.engineIsCertificateEntry(str);
        }

        public /* bridge */ /* synthetic */ boolean engineIsKeyEntry(String str) {
            return BcFKSKeyStoreSpi.super.engineIsKeyEntry(str);
        }

        public /* bridge */ /* synthetic */ void engineLoad(InputStream inputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException {
            BcFKSKeyStoreSpi.super.engineLoad(inputStream, cArr);
        }

        public /* bridge */ /* synthetic */ void engineLoad(LoadStoreParameter loadStoreParameter) throws CertificateException, NoSuchAlgorithmException, IOException {
            BcFKSKeyStoreSpi.super.engineLoad(loadStoreParameter);
        }

        public /* bridge */ /* synthetic */ void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
            BcFKSKeyStoreSpi.super.engineSetCertificateEntry(str, certificate);
        }

        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
            BcFKSKeyStoreSpi.super.engineSetKeyEntry(str, key, cArr, certificateArr);
        }

        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
            BcFKSKeyStoreSpi.super.engineSetKeyEntry(str, bArr, certificateArr);
        }

        public /* bridge */ /* synthetic */ int engineSize() {
            return BcFKSKeyStoreSpi.super.engineSize();
        }

        public /* bridge */ /* synthetic */ void engineStore(OutputStream outputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException {
            BcFKSKeyStoreSpi.super.engineStore(outputStream, cArr);
        }

        public /* bridge */ /* synthetic */ void engineStore(LoadStoreParameter loadStoreParameter) throws CertificateException, NoSuchAlgorithmException, IOException {
            BcFKSKeyStoreSpi.super.engineStore(loadStoreParameter);
        }
    }

    public static class DefShared extends SharedKeyStoreSpi {
        public DefShared() {
            super(new DefaultJcaJceHelper());
        }

        public /* bridge */ /* synthetic */ Enumeration engineAliases() {
            return super.engineAliases();
        }

        public /* bridge */ /* synthetic */ boolean engineContainsAlias(String str) {
            return super.engineContainsAlias(str);
        }

        public /* bridge */ /* synthetic */ void engineDeleteEntry(String str) throws KeyStoreException {
            super.engineDeleteEntry(str);
        }

        public /* bridge */ /* synthetic */ Certificate engineGetCertificate(String str) {
            return super.engineGetCertificate(str);
        }

        public /* bridge */ /* synthetic */ String engineGetCertificateAlias(Certificate certificate) {
            return super.engineGetCertificateAlias(certificate);
        }

        public /* bridge */ /* synthetic */ Certificate[] engineGetCertificateChain(String str) {
            return super.engineGetCertificateChain(str);
        }

        public /* bridge */ /* synthetic */ Date engineGetCreationDate(String str) {
            return super.engineGetCreationDate(str);
        }

        public /* bridge */ /* synthetic */ Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            return super.engineGetKey(str, cArr);
        }

        public /* bridge */ /* synthetic */ boolean engineIsCertificateEntry(String str) {
            return super.engineIsCertificateEntry(str);
        }

        public /* bridge */ /* synthetic */ boolean engineIsKeyEntry(String str) {
            return super.engineIsKeyEntry(str);
        }

        public /* bridge */ /* synthetic */ void engineLoad(InputStream inputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException {
            super.engineLoad(inputStream, cArr);
        }

        public /* bridge */ /* synthetic */ void engineLoad(LoadStoreParameter loadStoreParameter) throws CertificateException, NoSuchAlgorithmException, IOException {
            super.engineLoad(loadStoreParameter);
        }

        public /* bridge */ /* synthetic */ void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
            super.engineSetCertificateEntry(str, certificate);
        }

        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, key, cArr, certificateArr);
        }

        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, bArr, certificateArr);
        }

        public /* bridge */ /* synthetic */ int engineSize() {
            return super.engineSize();
        }

        public /* bridge */ /* synthetic */ void engineStore(OutputStream outputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException {
            super.engineStore(outputStream, cArr);
        }

        public /* bridge */ /* synthetic */ void engineStore(LoadStoreParameter loadStoreParameter) throws CertificateException, NoSuchAlgorithmException, IOException {
            super.engineStore(loadStoreParameter);
        }
    }

    private static class ExtKeyStoreException extends KeyStoreException {
        private final Throwable cause;

        ExtKeyStoreException(String str, Throwable th) {
            super(str);
            this.cause = th;
        }

        public Throwable getCause() {
            return this.cause;
        }
    }

    private static class SharedKeyStoreSpi extends BcFKSKeyStoreSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
        private final Map<String, byte[]> cache;
        private final byte[] seedKey;

        public SharedKeyStoreSpi(JcaJceHelper jcaJceHelper) {
            super(jcaJceHelper);
            try {
                this.seedKey = new byte[32];
                jcaJceHelper.createSecureRandom("DEFAULT").nextBytes(this.seedKey);
                this.cache = new HashMap();
            } catch (GeneralSecurityException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("can't create random - ");
                sb.append(e.toString());
                throw new IllegalArgumentException(sb.toString());
            }
        }

        private byte[] calculateMac(String str, char[] cArr) throws NoSuchAlgorithmException, InvalidKeyException {
            return SCrypt.generate(cArr != null ? Arrays.concatenate(Strings.toUTF8ByteArray(cArr), Strings.toUTF8ByteArray(str)) : Arrays.concatenate(this.seedKey, Strings.toUTF8ByteArray(str)), this.seedKey, 16384, 8, 1, 32);
        }

        public void engineDeleteEntry(String str) throws KeyStoreException {
            throw new KeyStoreException("delete operation not supported in shared mode");
        }

        public Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            String str2 = "unable to recover key (";
            try {
                byte[] calculateMac = calculateMac(str, cArr);
                if (!this.cache.containsKey(str) || Arrays.constantTimeAreEqual((byte[]) this.cache.get(str), calculateMac)) {
                    Key engineGetKey = BcFKSKeyStoreSpi.super.engineGetKey(str, cArr);
                    if (engineGetKey != null && !this.cache.containsKey(str)) {
                        this.cache.put(str, calculateMac);
                    }
                    return engineGetKey;
                }
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append(str);
                sb.append(")");
                throw new UnrecoverableKeyException(sb.toString());
            } catch (InvalidKeyException e) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str2);
                sb2.append(str);
                sb2.append("): ");
                sb2.append(e.getMessage());
                throw new UnrecoverableKeyException(sb2.toString());
            }
        }

        public void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
            throw new KeyStoreException("set operation not supported in shared mode");
        }

        public void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
            throw new KeyStoreException("set operation not supported in shared mode");
        }

        public void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
            throw new KeyStoreException("set operation not supported in shared mode");
        }
    }

    public static class Std extends BcFKSKeyStoreSpi {
        public Std() {
            super(new BCJcaJceHelper());
        }

        public /* bridge */ /* synthetic */ Enumeration engineAliases() {
            return BcFKSKeyStoreSpi.super.engineAliases();
        }

        public /* bridge */ /* synthetic */ boolean engineContainsAlias(String str) {
            return BcFKSKeyStoreSpi.super.engineContainsAlias(str);
        }

        public /* bridge */ /* synthetic */ void engineDeleteEntry(String str) throws KeyStoreException {
            BcFKSKeyStoreSpi.super.engineDeleteEntry(str);
        }

        public /* bridge */ /* synthetic */ Certificate engineGetCertificate(String str) {
            return BcFKSKeyStoreSpi.super.engineGetCertificate(str);
        }

        public /* bridge */ /* synthetic */ String engineGetCertificateAlias(Certificate certificate) {
            return BcFKSKeyStoreSpi.super.engineGetCertificateAlias(certificate);
        }

        public /* bridge */ /* synthetic */ Certificate[] engineGetCertificateChain(String str) {
            return BcFKSKeyStoreSpi.super.engineGetCertificateChain(str);
        }

        public /* bridge */ /* synthetic */ Date engineGetCreationDate(String str) {
            return BcFKSKeyStoreSpi.super.engineGetCreationDate(str);
        }

        public /* bridge */ /* synthetic */ Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            return BcFKSKeyStoreSpi.super.engineGetKey(str, cArr);
        }

        public /* bridge */ /* synthetic */ boolean engineIsCertificateEntry(String str) {
            return BcFKSKeyStoreSpi.super.engineIsCertificateEntry(str);
        }

        public /* bridge */ /* synthetic */ boolean engineIsKeyEntry(String str) {
            return BcFKSKeyStoreSpi.super.engineIsKeyEntry(str);
        }

        public /* bridge */ /* synthetic */ void engineLoad(InputStream inputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException {
            BcFKSKeyStoreSpi.super.engineLoad(inputStream, cArr);
        }

        public /* bridge */ /* synthetic */ void engineLoad(LoadStoreParameter loadStoreParameter) throws CertificateException, NoSuchAlgorithmException, IOException {
            BcFKSKeyStoreSpi.super.engineLoad(loadStoreParameter);
        }

        public /* bridge */ /* synthetic */ void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
            BcFKSKeyStoreSpi.super.engineSetCertificateEntry(str, certificate);
        }

        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
            BcFKSKeyStoreSpi.super.engineSetKeyEntry(str, key, cArr, certificateArr);
        }

        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
            BcFKSKeyStoreSpi.super.engineSetKeyEntry(str, bArr, certificateArr);
        }

        public /* bridge */ /* synthetic */ int engineSize() {
            return BcFKSKeyStoreSpi.super.engineSize();
        }

        public /* bridge */ /* synthetic */ void engineStore(OutputStream outputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException {
            BcFKSKeyStoreSpi.super.engineStore(outputStream, cArr);
        }

        public /* bridge */ /* synthetic */ void engineStore(LoadStoreParameter loadStoreParameter) throws CertificateException, NoSuchAlgorithmException, IOException {
            BcFKSKeyStoreSpi.super.engineStore(loadStoreParameter);
        }
    }

    public static class StdShared extends SharedKeyStoreSpi {
        public StdShared() {
            super(new BCJcaJceHelper());
        }

        public /* bridge */ /* synthetic */ Enumeration engineAliases() {
            return super.engineAliases();
        }

        public /* bridge */ /* synthetic */ boolean engineContainsAlias(String str) {
            return super.engineContainsAlias(str);
        }

        public /* bridge */ /* synthetic */ void engineDeleteEntry(String str) throws KeyStoreException {
            super.engineDeleteEntry(str);
        }

        public /* bridge */ /* synthetic */ Certificate engineGetCertificate(String str) {
            return super.engineGetCertificate(str);
        }

        public /* bridge */ /* synthetic */ String engineGetCertificateAlias(Certificate certificate) {
            return super.engineGetCertificateAlias(certificate);
        }

        public /* bridge */ /* synthetic */ Certificate[] engineGetCertificateChain(String str) {
            return super.engineGetCertificateChain(str);
        }

        public /* bridge */ /* synthetic */ Date engineGetCreationDate(String str) {
            return super.engineGetCreationDate(str);
        }

        public /* bridge */ /* synthetic */ Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            return super.engineGetKey(str, cArr);
        }

        public /* bridge */ /* synthetic */ boolean engineIsCertificateEntry(String str) {
            return super.engineIsCertificateEntry(str);
        }

        public /* bridge */ /* synthetic */ boolean engineIsKeyEntry(String str) {
            return super.engineIsKeyEntry(str);
        }

        public /* bridge */ /* synthetic */ void engineLoad(InputStream inputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException {
            super.engineLoad(inputStream, cArr);
        }

        public /* bridge */ /* synthetic */ void engineLoad(LoadStoreParameter loadStoreParameter) throws CertificateException, NoSuchAlgorithmException, IOException {
            super.engineLoad(loadStoreParameter);
        }

        public /* bridge */ /* synthetic */ void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
            super.engineSetCertificateEntry(str, certificate);
        }

        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, key, cArr, certificateArr);
        }

        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, bArr, certificateArr);
        }

        public /* bridge */ /* synthetic */ int engineSize() {
            return super.engineSize();
        }

        public /* bridge */ /* synthetic */ void engineStore(OutputStream outputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException {
            super.engineStore(outputStream, cArr);
        }

        public /* bridge */ /* synthetic */ void engineStore(LoadStoreParameter loadStoreParameter) throws CertificateException, NoSuchAlgorithmException, IOException {
            super.engineStore(loadStoreParameter);
        }
    }

    static {
        oidMap.put("DESEDE", OIWObjectIdentifiers.desEDE);
        oidMap.put("TRIPLEDES", OIWObjectIdentifiers.desEDE);
        oidMap.put("TDEA", OIWObjectIdentifiers.desEDE);
        oidMap.put("HMACSHA1", PKCSObjectIdentifiers.id_hmacWithSHA1);
        oidMap.put("HMACSHA224", PKCSObjectIdentifiers.id_hmacWithSHA224);
        oidMap.put("HMACSHA256", PKCSObjectIdentifiers.id_hmacWithSHA256);
        oidMap.put("HMACSHA384", PKCSObjectIdentifiers.id_hmacWithSHA384);
        oidMap.put("HMACSHA512", PKCSObjectIdentifiers.id_hmacWithSHA512);
        oidMap.put("SEED", KISAObjectIdentifiers.id_seedCBC);
        oidMap.put("CAMELLIA.128", NTTObjectIdentifiers.id_camellia128_cbc);
        oidMap.put("CAMELLIA.192", NTTObjectIdentifiers.id_camellia192_cbc);
        oidMap.put("CAMELLIA.256", NTTObjectIdentifiers.id_camellia256_cbc);
        oidMap.put("ARIA.128", NSRIObjectIdentifiers.id_aria128_cbc);
        oidMap.put("ARIA.192", NSRIObjectIdentifiers.id_aria192_cbc);
        oidMap.put("ARIA.256", NSRIObjectIdentifiers.id_aria256_cbc);
        publicAlgMap.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        publicAlgMap.put(X9ObjectIdentifiers.id_ecPublicKey, "EC");
        String str = "DH";
        publicAlgMap.put(OIWObjectIdentifiers.elGamalAlgorithm, str);
        publicAlgMap.put(PKCSObjectIdentifiers.dhKeyAgreement, str);
        publicAlgMap.put(X9ObjectIdentifiers.id_dsa, "DSA");
    }

    BcFKSKeyStoreSpi(JcaJceHelper jcaJceHelper) {
        this.helper = jcaJceHelper;
    }

    private byte[] calculateMac(byte[] bArr, AlgorithmIdentifier algorithmIdentifier, KeyDerivationFunc keyDerivationFunc, char[] cArr) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        String id = algorithmIdentifier.getAlgorithm().getId();
        Mac createMac = this.helper.createMac(id);
        try {
            String str = "INTEGRITY_CHECK";
            if (cArr == null) {
                cArr = new char[0];
            }
            createMac.init(new SecretKeySpec(generateKey(keyDerivationFunc, str, cArr, -1), id));
            return createMac.doFinal(bArr);
        } catch (InvalidKeyException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot set up MAC calculation: ");
            sb.append(e.getMessage());
            throw new IOException(sb.toString());
        }
    }

    private Cipher createCipher(String str, byte[] bArr) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {
        Cipher createCipher = this.helper.createCipher(str);
        createCipher.init(1, new SecretKeySpec(bArr, "AES"));
        return createCipher;
    }

    private EncryptedPrivateKeyData createPrivateKeySequence(EncryptedPrivateKeyInfo encryptedPrivateKeyInfo, Certificate[] certificateArr) throws CertificateEncodingException {
        org.bouncycastle.asn1.x509.Certificate[] certificateArr2 = new org.bouncycastle.asn1.x509.Certificate[certificateArr.length];
        for (int i = 0; i != certificateArr.length; i++) {
            certificateArr2[i] = org.bouncycastle.asn1.x509.Certificate.getInstance(certificateArr[i].getEncoded());
        }
        return new EncryptedPrivateKeyData(encryptedPrivateKeyInfo, certificateArr2);
    }

    private Certificate decodeCertificate(Object obj) {
        JcaJceHelper jcaJceHelper = this.helper;
        String str = "X.509";
        if (jcaJceHelper != null) {
            try {
                return jcaJceHelper.createCertificateFactory(str).generateCertificate(new ByteArrayInputStream(org.bouncycastle.asn1.x509.Certificate.getInstance(obj).getEncoded()));
            } catch (Exception e) {
                return null;
            }
        } else {
            try {
                return CertificateFactory.getInstance(str).generateCertificate(new ByteArrayInputStream(org.bouncycastle.asn1.x509.Certificate.getInstance(obj).getEncoded()));
            } catch (Exception e2) {
                return null;
            }
        }
    }

    private byte[] decryptData(String str, AlgorithmIdentifier algorithmIdentifier, char[] cArr, byte[] bArr) throws IOException {
        AlgorithmParameters algorithmParameters;
        Cipher cipher;
        if (algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_PBES2)) {
            PBES2Parameters instance = PBES2Parameters.getInstance(algorithmIdentifier.getParameters());
            EncryptionScheme encryptionScheme = instance.getEncryptionScheme();
            try {
                if (encryptionScheme.getAlgorithm().equals(NISTObjectIdentifiers.id_aes256_CCM)) {
                    cipher = this.helper.createCipher("AES/CCM/NoPadding");
                    algorithmParameters = this.helper.createAlgorithmParameters("CCM");
                    algorithmParameters.init(CCMParameters.getInstance(encryptionScheme.getParameters()).getEncoded());
                } else if (encryptionScheme.getAlgorithm().equals(NISTObjectIdentifiers.id_aes256_wrap_pad)) {
                    cipher = this.helper.createCipher("AESKWP");
                    algorithmParameters = null;
                } else {
                    throw new IOException("BCFKS KeyStore cannot recognize protection encryption algorithm.");
                }
                KeyDerivationFunc keyDerivationFunc = instance.getKeyDerivationFunc();
                if (cArr == null) {
                    cArr = new char[0];
                }
                cipher.init(2, new SecretKeySpec(generateKey(keyDerivationFunc, str, cArr, 32), "AES"), algorithmParameters);
                return cipher.doFinal(bArr);
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new IOException(e2.toString());
            }
        } else {
            throw new IOException("BCFKS KeyStore cannot recognize protection algorithm.");
        }
    }

    private Date extractCreationDate(ObjectData objectData, Date date) {
        try {
            return objectData.getCreationDate().getDate();
        } catch (ParseException e) {
            return date;
        }
    }

    private char[] extractPassword(LoadStoreParameter loadStoreParameter) throws IOException {
        ProtectionParameter protectionParameter = loadStoreParameter.getProtectionParameter();
        if (protectionParameter == null) {
            return null;
        }
        if (protectionParameter instanceof PasswordProtection) {
            return ((PasswordProtection) protectionParameter).getPassword();
        }
        if (protectionParameter instanceof CallbackHandlerProtection) {
            CallbackHandler callbackHandler = ((CallbackHandlerProtection) protectionParameter).getCallbackHandler();
            PasswordCallback passwordCallback = new PasswordCallback("password: ", false);
            try {
                callbackHandler.handle(new Callback[]{passwordCallback});
                return passwordCallback.getPassword();
            } catch (UnsupportedCallbackException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("PasswordCallback not recognised: ");
                sb.append(e.getMessage());
                throw new IllegalArgumentException(sb.toString(), e);
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("no support for protection parameter of type ");
            sb2.append(protectionParameter.getClass().getName());
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    private byte[] generateKey(KeyDerivationFunc keyDerivationFunc, String str, char[] cArr, int i) throws IOException {
        byte[] PKCS12PasswordToBytes = PBEParametersGenerator.PKCS12PasswordToBytes(cArr);
        byte[] PKCS12PasswordToBytes2 = PBEParametersGenerator.PKCS12PasswordToBytes(str.toCharArray());
        if (MiscObjectIdentifiers.id_scrypt.equals(keyDerivationFunc.getAlgorithm())) {
            ScryptParams instance = ScryptParams.getInstance(keyDerivationFunc.getParameters());
            if (instance.getKeyLength() != null) {
                i = instance.getKeyLength().intValue();
            } else if (i == -1) {
                throw new IOException("no keyLength found in ScryptParams");
            }
            return SCrypt.generate(Arrays.concatenate(PKCS12PasswordToBytes, PKCS12PasswordToBytes2), instance.getSalt(), instance.getCostParameter().intValue(), instance.getBlockSize().intValue(), instance.getBlockSize().intValue(), i);
        } else if (keyDerivationFunc.getAlgorithm().equals(PKCSObjectIdentifiers.id_PBKDF2)) {
            PBKDF2Params instance2 = PBKDF2Params.getInstance(keyDerivationFunc.getParameters());
            if (instance2.getKeyLength() != null) {
                i = instance2.getKeyLength().intValue();
            } else if (i == -1) {
                throw new IOException("no keyLength found in PBKDF2Params");
            }
            if (instance2.getPrf().getAlgorithm().equals(PKCSObjectIdentifiers.id_hmacWithSHA512)) {
                PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator = new PKCS5S2ParametersGenerator(new SHA512Digest());
                pKCS5S2ParametersGenerator.init(Arrays.concatenate(PKCS12PasswordToBytes, PKCS12PasswordToBytes2), instance2.getSalt(), instance2.getIterationCount().intValue());
                return ((KeyParameter) pKCS5S2ParametersGenerator.generateDerivedParameters(i * 8)).getKey();
            } else if (instance2.getPrf().getAlgorithm().equals(NISTObjectIdentifiers.id_hmacWithSHA3_512)) {
                PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator2 = new PKCS5S2ParametersGenerator(new SHA3Digest(512));
                pKCS5S2ParametersGenerator2.init(Arrays.concatenate(PKCS12PasswordToBytes, PKCS12PasswordToBytes2), instance2.getSalt(), instance2.getIterationCount().intValue());
                return ((KeyParameter) pKCS5S2ParametersGenerator2.generateDerivedParameters(i * 8)).getKey();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("BCFKS KeyStore: unrecognized MAC PBKD PRF: ");
                sb.append(instance2.getPrf().getAlgorithm());
                throw new IOException(sb.toString());
            }
        } else {
            throw new IOException("BCFKS KeyStore: unrecognized MAC PBKD.");
        }
    }

    private KeyDerivationFunc generatePkbdAlgorithmIdentifier(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i) {
        byte[] bArr = new byte[64];
        getDefaultSecureRandom().nextBytes(bArr);
        if (PKCSObjectIdentifiers.id_PBKDF2.equals(aSN1ObjectIdentifier)) {
            return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(bArr, 51200, i, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE)));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unknown derivation algorithm: ");
        sb.append(aSN1ObjectIdentifier);
        throw new IllegalStateException(sb.toString());
    }

    private KeyDerivationFunc generatePkbdAlgorithmIdentifier(KeyDerivationFunc keyDerivationFunc, int i) {
        boolean equals = MiscObjectIdentifiers.id_scrypt.equals(keyDerivationFunc.getAlgorithm());
        ASN1Encodable parameters = keyDerivationFunc.getParameters();
        if (equals) {
            ScryptParams instance = ScryptParams.getInstance(parameters);
            byte[] bArr = new byte[instance.getSalt().length];
            getDefaultSecureRandom().nextBytes(bArr);
            ScryptParams scryptParams = new ScryptParams(bArr, instance.getCostParameter(), instance.getBlockSize(), instance.getParallelizationParameter(), BigInteger.valueOf((long) i));
            return new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, scryptParams);
        }
        PBKDF2Params instance2 = PBKDF2Params.getInstance(parameters);
        byte[] bArr2 = new byte[instance2.getSalt().length];
        getDefaultSecureRandom().nextBytes(bArr2);
        return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(bArr2, instance2.getIterationCount().intValue(), i, instance2.getPrf()));
    }

    private KeyDerivationFunc generatePkbdAlgorithmIdentifier(PBKDFConfig pBKDFConfig, int i) {
        if (MiscObjectIdentifiers.id_scrypt.equals(pBKDFConfig.getAlgorithm())) {
            ScryptConfig scryptConfig = (ScryptConfig) pBKDFConfig;
            byte[] bArr = new byte[scryptConfig.getSaltLength()];
            getDefaultSecureRandom().nextBytes(bArr);
            ScryptParams scryptParams = new ScryptParams(bArr, scryptConfig.getCostParameter(), scryptConfig.getBlockSize(), scryptConfig.getParallelizationParameter(), i);
            return new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, scryptParams);
        }
        PBKDF2Config pBKDF2Config = (PBKDF2Config) pBKDFConfig;
        byte[] bArr2 = new byte[pBKDF2Config.getSaltLength()];
        getDefaultSecureRandom().nextBytes(bArr2);
        return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(bArr2, pBKDF2Config.getIterationCount(), i, pBKDF2Config.getPRF()));
    }

    private AlgorithmIdentifier generateSignatureAlgId(Key key, SignatureAlgorithm signatureAlgorithm2) throws IOException {
        if (key == null) {
            return null;
        }
        if (key instanceof ECKey) {
            if (signatureAlgorithm2 == SignatureAlgorithm.SHA512withECDSA) {
                return new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA512);
            }
            if (signatureAlgorithm2 == SignatureAlgorithm.SHA3_512withECDSA) {
                return new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_512);
            }
        }
        if (key instanceof DSAKey) {
            if (signatureAlgorithm2 == SignatureAlgorithm.SHA512withDSA) {
                return new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha512);
            }
            if (signatureAlgorithm2 == SignatureAlgorithm.SHA3_512withDSA) {
                return new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_512);
            }
        }
        if (key instanceof RSAKey) {
            if (signatureAlgorithm2 == SignatureAlgorithm.SHA512withRSA) {
                return new AlgorithmIdentifier(PKCSObjectIdentifiers.sha512WithRSAEncryption, DERNull.INSTANCE);
            }
            if (signatureAlgorithm2 == SignatureAlgorithm.SHA3_512withRSA) {
                return new AlgorithmIdentifier(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, DERNull.INSTANCE);
            }
        }
        throw new IOException("unknown signature algorithm");
    }

    private SecureRandom getDefaultSecureRandom() {
        return CryptoServicesRegistrar.getSecureRandom();
    }

    private EncryptedObjectStoreData getEncryptedObjectStoreData(AlgorithmIdentifier algorithmIdentifier, char[] cArr) throws IOException, NoSuchAlgorithmException {
        ObjectData[] objectDataArr = (ObjectData[]) this.entries.values().toArray(new ObjectData[this.entries.size()]);
        KeyDerivationFunc generatePkbdAlgorithmIdentifier = generatePkbdAlgorithmIdentifier(this.hmacPkbdAlgorithm, 32);
        if (cArr == null) {
            cArr = new char[0];
        }
        byte[] generateKey = generateKey(generatePkbdAlgorithmIdentifier, "STORE_ENCRYPTION", cArr, 32);
        ObjectStoreData objectStoreData = new ObjectStoreData(algorithmIdentifier, this.creationDate, this.lastModifiedDate, new ObjectDataSequence(objectDataArr), null);
        try {
            if (this.storeEncryptionAlgorithm.equals(NISTObjectIdentifiers.id_aes256_CCM)) {
                Cipher createCipher = createCipher("AES/CCM/NoPadding", generateKey);
                return new EncryptedObjectStoreData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(generatePkbdAlgorithmIdentifier, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(createCipher.getParameters().getEncoded())))), createCipher.doFinal(objectStoreData.getEncoded()));
            }
            return new EncryptedObjectStoreData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(generatePkbdAlgorithmIdentifier, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_wrap_pad))), createCipher("AESKWP", generateKey).doFinal(objectStoreData.getEncoded()));
        } catch (NoSuchPaddingException e) {
            throw new NoSuchAlgorithmException(e.toString());
        } catch (BadPaddingException e2) {
            throw new IOException(e2.toString());
        } catch (IllegalBlockSizeException e3) {
            throw new IOException(e3.toString());
        } catch (InvalidKeyException e4) {
            throw new IOException(e4.toString());
        } catch (NoSuchProviderException e5) {
            throw new IOException(e5.toString());
        }
    }

    private static String getPublicKeyAlg(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String str = (String) publicAlgMap.get(aSN1ObjectIdentifier);
        return str != null ? str : aSN1ObjectIdentifier.getId();
    }

    private boolean isSimilarHmacPbkd(PBKDFConfig pBKDFConfig, KeyDerivationFunc keyDerivationFunc) {
        if (!pBKDFConfig.getAlgorithm().equals(keyDerivationFunc.getAlgorithm())) {
            return false;
        }
        if (MiscObjectIdentifiers.id_scrypt.equals(keyDerivationFunc.getAlgorithm())) {
            if (!(pBKDFConfig instanceof ScryptConfig)) {
                return false;
            }
            ScryptConfig scryptConfig = (ScryptConfig) pBKDFConfig;
            ScryptParams instance = ScryptParams.getInstance(keyDerivationFunc.getParameters());
            if (!(scryptConfig.getSaltLength() == instance.getSalt().length && scryptConfig.getBlockSize() == instance.getBlockSize().intValue() && scryptConfig.getCostParameter() == instance.getCostParameter().intValue() && scryptConfig.getParallelizationParameter() == instance.getParallelizationParameter().intValue())) {
                return false;
            }
        } else if (!(pBKDFConfig instanceof PBKDF2Config)) {
            return false;
        } else {
            PBKDF2Config pBKDF2Config = (PBKDF2Config) pBKDFConfig;
            PBKDF2Params instance2 = PBKDF2Params.getInstance(keyDerivationFunc.getParameters());
            if (!(pBKDF2Config.getSaltLength() == instance2.getSalt().length && pBKDF2Config.getIterationCount() == instance2.getIterationCount().intValue())) {
                return false;
            }
        }
        return true;
    }

    private void verifyMac(byte[] bArr, PbkdMacIntegrityCheck pbkdMacIntegrityCheck, char[] cArr) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        if (!Arrays.constantTimeAreEqual(calculateMac(bArr, pbkdMacIntegrityCheck.getMacAlgorithm(), pbkdMacIntegrityCheck.getPbkdAlgorithm(), cArr), pbkdMacIntegrityCheck.getMac())) {
            throw new IOException("BCFKS KeyStore corrupted: MAC calculation failed");
        }
    }

    private void verifySig(ASN1Encodable aSN1Encodable, SignatureCheck signatureCheck, PublicKey publicKey) throws GeneralSecurityException, IOException {
        Signature createSignature = this.helper.createSignature(signatureCheck.getSignatureAlgorithm().getAlgorithm().getId());
        createSignature.initVerify(publicKey);
        createSignature.update(aSN1Encodable.toASN1Primitive().getEncoded(ASN1Encoding.DER));
        if (!createSignature.verify(signatureCheck.getSignature().getOctets())) {
            throw new IOException("BCFKS KeyStore corrupted: signature calculation failed");
        }
    }

    public Enumeration<String> engineAliases() {
        final Iterator it = new HashSet(this.entries.keySet()).iterator();
        return new Enumeration() {
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            public Object nextElement() {
                return it.next();
            }
        };
    }

    public boolean engineContainsAlias(String str) {
        if (str != null) {
            return this.entries.containsKey(str);
        }
        throw new NullPointerException("alias value is null");
    }

    public void engineDeleteEntry(String str) throws KeyStoreException {
        if (((ObjectData) this.entries.get(str)) != null) {
            this.privateKeyCache.remove(str);
            this.entries.remove(str);
            this.lastModifiedDate = new Date();
        }
    }

    public Certificate engineGetCertificate(String str) {
        ObjectData objectData = (ObjectData) this.entries.get(str);
        if (objectData != null) {
            if (objectData.getType().equals(PRIVATE_KEY) || objectData.getType().equals(PROTECTED_PRIVATE_KEY)) {
                return decodeCertificate(EncryptedPrivateKeyData.getInstance(objectData.getData()).getCertificateChain()[0]);
            }
            if (objectData.getType().equals(CERTIFICATE)) {
                return decodeCertificate(objectData.getData());
            }
        }
        return null;
    }

    public String engineGetCertificateAlias(Certificate certificate) {
        if (certificate == null) {
            return null;
        }
        try {
            byte[] encoded = certificate.getEncoded();
            for (String str : this.entries.keySet()) {
                ObjectData objectData = (ObjectData) this.entries.get(str);
                if (objectData.getType().equals(CERTIFICATE)) {
                    if (Arrays.areEqual(objectData.getData(), encoded)) {
                        return str;
                    }
                } else if (objectData.getType().equals(PRIVATE_KEY) || objectData.getType().equals(PROTECTED_PRIVATE_KEY)) {
                    try {
                        if (Arrays.areEqual(EncryptedPrivateKeyData.getInstance(objectData.getData()).getCertificateChain()[0].toASN1Primitive().getEncoded(), encoded)) {
                            return str;
                        }
                    } catch (IOException e) {
                    }
                }
            }
            return null;
        } catch (CertificateEncodingException e2) {
            return null;
        }
    }

    public Certificate[] engineGetCertificateChain(String str) {
        ObjectData objectData = (ObjectData) this.entries.get(str);
        if (objectData == null || (!objectData.getType().equals(PRIVATE_KEY) && !objectData.getType().equals(PROTECTED_PRIVATE_KEY))) {
            return null;
        }
        org.bouncycastle.asn1.x509.Certificate[] certificateChain = EncryptedPrivateKeyData.getInstance(objectData.getData()).getCertificateChain();
        Certificate[] certificateArr = new X509Certificate[certificateChain.length];
        for (int i = 0; i != certificateArr.length; i++) {
            certificateArr[i] = decodeCertificate(certificateChain[i]);
        }
        return certificateArr;
    }

    public Date engineGetCreationDate(String str) {
        ObjectData objectData = (ObjectData) this.entries.get(str);
        if (objectData == null) {
            return null;
        }
        try {
            return objectData.getLastModifiedDate().getDate();
        } catch (ParseException e) {
            return new Date();
        }
    }

    public Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        ObjectData objectData = (ObjectData) this.entries.get(str);
        if (objectData == null) {
            return null;
        }
        String str2 = "): ";
        if (objectData.getType().equals(PRIVATE_KEY) || objectData.getType().equals(PROTECTED_PRIVATE_KEY)) {
            PrivateKey privateKey = (PrivateKey) this.privateKeyCache.get(str);
            if (privateKey != null) {
                return privateKey;
            }
            EncryptedPrivateKeyInfo instance = EncryptedPrivateKeyInfo.getInstance(EncryptedPrivateKeyData.getInstance(objectData.getData()).getEncryptedPrivateKeyInfo());
            try {
                PrivateKeyInfo instance2 = PrivateKeyInfo.getInstance(decryptData("PRIVATE_KEY_ENCRYPTION", instance.getEncryptionAlgorithm(), cArr, instance.getEncryptedData()));
                PrivateKey generatePrivate = this.helper.createKeyFactory(getPublicKeyAlg(instance2.getPrivateKeyAlgorithm().getAlgorithm())).generatePrivate(new PKCS8EncodedKeySpec(instance2.getEncoded()));
                this.privateKeyCache.put(str, generatePrivate);
                return generatePrivate;
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("BCFKS KeyStore unable to recover private key (");
                sb.append(str);
                sb.append(str2);
                sb.append(e.getMessage());
                throw new UnrecoverableKeyException(sb.toString());
            }
        } else {
            String str3 = "BCFKS KeyStore unable to recover secret key (";
            if (objectData.getType().equals(SECRET_KEY) || objectData.getType().equals(PROTECTED_SECRET_KEY)) {
                EncryptedSecretKeyData instance3 = EncryptedSecretKeyData.getInstance(objectData.getData());
                try {
                    SecretKeyData instance4 = SecretKeyData.getInstance(decryptData("SECRET_KEY_ENCRYPTION", instance3.getKeyEncryptionAlgorithm(), cArr, instance3.getEncryptedKeyData()));
                    return this.helper.createSecretKeyFactory(instance4.getKeyAlgorithm().getId()).generateSecret(new SecretKeySpec(instance4.getKeyBytes(), instance4.getKeyAlgorithm().getId()));
                } catch (Exception e2) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str3);
                    sb2.append(str);
                    sb2.append(str2);
                    sb2.append(e2.getMessage());
                    throw new UnrecoverableKeyException(sb2.toString());
                }
            } else {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(str3);
                sb3.append(str);
                sb3.append("): type not recognized");
                throw new UnrecoverableKeyException(sb3.toString());
            }
        }
    }

    public boolean engineIsCertificateEntry(String str) {
        ObjectData objectData = (ObjectData) this.entries.get(str);
        if (objectData != null) {
            return objectData.getType().equals(CERTIFICATE);
        }
        return false;
    }

    public boolean engineIsKeyEntry(String str) {
        ObjectData objectData = (ObjectData) this.entries.get(str);
        if (objectData == null) {
            return false;
        }
        BigInteger type = objectData.getType();
        return type.equals(PRIVATE_KEY) || type.equals(SECRET_KEY) || type.equals(PROTECTED_PRIVATE_KEY) || type.equals(PROTECTED_SECRET_KEY);
    }

    public void engineLoad(InputStream inputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException {
        AlgorithmIdentifier algorithmIdentifier;
        ObjectStoreData objectStoreData;
        ASN1Encodable storeData;
        PublicKey publicKey;
        this.entries.clear();
        this.privateKeyCache.clear();
        this.creationDate = null;
        this.lastModifiedDate = null;
        this.hmacAlgorithm = null;
        if (inputStream == null) {
            Date date = new Date();
            this.creationDate = date;
            this.lastModifiedDate = date;
            this.verificationKey = null;
            this.validator = null;
            this.hmacAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
            this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 64);
            return;
        }
        try {
            ObjectStore instance = ObjectStore.getInstance(new ASN1InputStream(inputStream).readObject());
            ObjectStoreIntegrityCheck integrityCheck = instance.getIntegrityCheck();
            if (integrityCheck.getType() == 0) {
                PbkdMacIntegrityCheck instance2 = PbkdMacIntegrityCheck.getInstance(integrityCheck.getIntegrityCheck());
                this.hmacAlgorithm = instance2.getMacAlgorithm();
                this.hmacPkbdAlgorithm = instance2.getPbkdAlgorithm();
                algorithmIdentifier = this.hmacAlgorithm;
                try {
                    verifyMac(instance.getStoreData().toASN1Primitive().getEncoded(), instance2, cArr);
                } catch (NoSuchProviderException e) {
                    throw new IOException(e.getMessage());
                }
            } else if (integrityCheck.getType() == 1) {
                SignatureCheck instance3 = SignatureCheck.getInstance(integrityCheck.getIntegrityCheck());
                algorithmIdentifier = instance3.getSignatureAlgorithm();
                try {
                    org.bouncycastle.asn1.x509.Certificate[] certificates = instance3.getCertificates();
                    if (this.validator == null) {
                        storeData = instance.getStoreData();
                        publicKey = this.verificationKey;
                    } else if (certificates != null) {
                        CertificateFactory createCertificateFactory = this.helper.createCertificateFactory("X.509");
                        X509Certificate[] x509CertificateArr = new X509Certificate[certificates.length];
                        for (int i = 0; i != x509CertificateArr.length; i++) {
                            x509CertificateArr[i] = (X509Certificate) createCertificateFactory.generateCertificate(new ByteArrayInputStream(certificates[i].getEncoded()));
                        }
                        if (this.validator.isValid(x509CertificateArr)) {
                            storeData = instance.getStoreData();
                            publicKey = x509CertificateArr[0].getPublicKey();
                        } else {
                            throw new IOException("certificate chain in key store signature not valid");
                        }
                    } else {
                        throw new IOException("validator specified but no certifcates in store");
                    }
                    verifySig(storeData, instance3, publicKey);
                } catch (GeneralSecurityException e2) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("error verifying signature: ");
                    sb.append(e2.getMessage());
                    throw new IOException(sb.toString(), e2);
                }
            } else {
                throw new IOException("BCFKS KeyStore unable to recognize integrity check.");
            }
            ASN1Encodable storeData2 = instance.getStoreData();
            if (storeData2 instanceof EncryptedObjectStoreData) {
                EncryptedObjectStoreData encryptedObjectStoreData = (EncryptedObjectStoreData) storeData2;
                objectStoreData = ObjectStoreData.getInstance(decryptData("STORE_ENCRYPTION", encryptedObjectStoreData.getEncryptionAlgorithm(), cArr, encryptedObjectStoreData.getEncryptedContent().getOctets()));
            } else {
                objectStoreData = ObjectStoreData.getInstance(storeData2);
            }
            try {
                this.creationDate = objectStoreData.getCreationDate().getDate();
                this.lastModifiedDate = objectStoreData.getLastModifiedDate().getDate();
                if (objectStoreData.getIntegrityAlgorithm().equals(algorithmIdentifier)) {
                    Iterator it = objectStoreData.getObjectDataSequence().iterator();
                    while (it.hasNext()) {
                        ObjectData instance4 = ObjectData.getInstance(it.next());
                        this.entries.put(instance4.getIdentifier(), instance4);
                    }
                    return;
                }
                throw new IOException("BCFKS KeyStore storeData integrity algorithm does not match store integrity algorithm.");
            } catch (ParseException e3) {
                throw new IOException("BCFKS KeyStore unable to parse store data information.");
            }
        } catch (Exception e4) {
            throw new IOException(e4.getMessage());
        }
    }

    public void engineLoad(LoadStoreParameter loadStoreParameter) throws CertificateException, NoSuchAlgorithmException, IOException {
        if (loadStoreParameter == null) {
            throw new IllegalArgumentException("'parameter' arg cannot be null");
        } else if (loadStoreParameter instanceof BCFKSLoadStoreParameter) {
            BCFKSLoadStoreParameter bCFKSLoadStoreParameter = (BCFKSLoadStoreParameter) loadStoreParameter;
            char[] extractPassword = extractPassword(bCFKSLoadStoreParameter);
            this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(bCFKSLoadStoreParameter.getStorePBKDFConfig(), 64);
            this.storeEncryptionAlgorithm = bCFKSLoadStoreParameter.getStoreEncryptionAlgorithm() == EncryptionAlgorithm.AES256_CCM ? NISTObjectIdentifiers.id_aes256_CCM : NISTObjectIdentifiers.id_aes256_wrap_pad;
            this.hmacAlgorithm = bCFKSLoadStoreParameter.getStoreMacAlgorithm() == MacAlgorithm.HmacSHA512 ? new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE) : new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE);
            this.verificationKey = (PublicKey) bCFKSLoadStoreParameter.getStoreSignatureKey();
            this.validator = bCFKSLoadStoreParameter.getCertChainValidator();
            this.signatureAlgorithm = generateSignatureAlgId(this.verificationKey, bCFKSLoadStoreParameter.getStoreSignatureAlgorithm());
            AlgorithmIdentifier algorithmIdentifier = this.hmacAlgorithm;
            ASN1ObjectIdentifier aSN1ObjectIdentifier = this.storeEncryptionAlgorithm;
            InputStream inputStream = bCFKSLoadStoreParameter.getInputStream();
            engineLoad(inputStream, extractPassword);
            if (inputStream == null) {
                return;
            }
            if (!isSimilarHmacPbkd(bCFKSLoadStoreParameter.getStorePBKDFConfig(), this.hmacPkbdAlgorithm) || !aSN1ObjectIdentifier.equals(this.storeEncryptionAlgorithm)) {
                throw new IOException("configuration parameters do not match existing store");
            }
        } else if (loadStoreParameter instanceof BCLoadStoreParameter) {
            engineLoad(((BCLoadStoreParameter) loadStoreParameter).getInputStream(), extractPassword(loadStoreParameter));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("no support for 'parameter' of type ");
            sb.append(loadStoreParameter.getClass().getName());
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
        Date date;
        ObjectData objectData = (ObjectData) this.entries.get(str);
        Date date2 = new Date();
        if (objectData == null) {
            date = date2;
        } else if (objectData.getType().equals(CERTIFICATE)) {
            date = extractCreationDate(objectData, date2);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("BCFKS KeyStore already has a key entry with alias ");
            sb.append(str);
            throw new KeyStoreException(sb.toString());
        }
        try {
            Map<String, ObjectData> map = this.entries;
            ObjectData objectData2 = new ObjectData(CERTIFICATE, str, date, date2, certificate.getEncoded(), null);
            map.put(str, objectData2);
            this.lastModifiedDate = date2;
        } catch (CertificateEncodingException e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("BCFKS KeyStore unable to handle certificate: ");
            sb2.append(e.getMessage());
            throw new ExtKeyStoreException(sb2.toString(), e);
        }
    }

    public void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
        SecretKeyData secretKeyData;
        EncryptedSecretKeyData encryptedSecretKeyData;
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;
        Date date = new Date();
        ObjectData objectData = (ObjectData) this.entries.get(str);
        Date extractCreationDate = objectData != null ? extractCreationDate(objectData, date) : date;
        this.privateKeyCache.remove(str);
        String str2 = "AES/CCM/NoPadding";
        String str3 = "AESKWP";
        String str4 = "BCFKS KeyStore exception storing private key: ";
        if (key instanceof PrivateKey) {
            if (certificateArr != null) {
                try {
                    byte[] encoded = key.getEncoded();
                    KeyDerivationFunc generatePkbdAlgorithmIdentifier = generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 32);
                    String str5 = "PRIVATE_KEY_ENCRYPTION";
                    if (cArr == null) {
                        cArr = new char[0];
                    }
                    byte[] generateKey = generateKey(generatePkbdAlgorithmIdentifier, str5, cArr, 32);
                    if (this.storeEncryptionAlgorithm.equals(NISTObjectIdentifiers.id_aes256_CCM)) {
                        Cipher createCipher = createCipher(str2, generateKey);
                        encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(generatePkbdAlgorithmIdentifier, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(createCipher.getParameters().getEncoded())))), createCipher.doFinal(encoded));
                    } else {
                        encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(generatePkbdAlgorithmIdentifier, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_wrap_pad))), createCipher(str3, generateKey).doFinal(encoded));
                    }
                    EncryptedPrivateKeyData createPrivateKeySequence = createPrivateKeySequence(encryptedPrivateKeyInfo, certificateArr);
                    Map<String, ObjectData> map = this.entries;
                    ObjectData objectData2 = new ObjectData(PRIVATE_KEY, str, extractCreationDate, date, createPrivateKeySequence.getEncoded(), null);
                    map.put(str, objectData2);
                } catch (Exception e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str4);
                    sb.append(e.toString());
                    throw new ExtKeyStoreException(sb.toString(), e);
                }
            } else {
                throw new KeyStoreException("BCFKS KeyStore requires a certificate chain for private key storage.");
            }
        } else if (!(key instanceof SecretKey)) {
            throw new KeyStoreException("BCFKS KeyStore unable to recognize key.");
        } else if (certificateArr == null) {
            try {
                byte[] encoded2 = key.getEncoded();
                KeyDerivationFunc generatePkbdAlgorithmIdentifier2 = generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 32);
                String str6 = "SECRET_KEY_ENCRYPTION";
                if (cArr == null) {
                    cArr = new char[0];
                }
                byte[] generateKey2 = generateKey(generatePkbdAlgorithmIdentifier2, str6, cArr, 32);
                String upperCase = Strings.toUpperCase(key.getAlgorithm());
                if (upperCase.indexOf("AES") > -1) {
                    secretKeyData = new SecretKeyData(NISTObjectIdentifiers.aes, encoded2);
                } else {
                    ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) oidMap.get(upperCase);
                    if (aSN1ObjectIdentifier != null) {
                        secretKeyData = new SecretKeyData(aSN1ObjectIdentifier, encoded2);
                    } else {
                        Map<String, ASN1ObjectIdentifier> map2 = oidMap;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(upperCase);
                        sb2.append(".");
                        sb2.append(encoded2.length * 8);
                        ASN1ObjectIdentifier aSN1ObjectIdentifier2 = (ASN1ObjectIdentifier) map2.get(sb2.toString());
                        if (aSN1ObjectIdentifier2 != null) {
                            secretKeyData = new SecretKeyData(aSN1ObjectIdentifier2, encoded2);
                        } else {
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("BCFKS KeyStore cannot recognize secret key (");
                            sb3.append(upperCase);
                            sb3.append(") for storage.");
                            throw new KeyStoreException(sb3.toString());
                        }
                    }
                }
                if (this.storeEncryptionAlgorithm.equals(NISTObjectIdentifiers.id_aes256_CCM)) {
                    Cipher createCipher2 = createCipher(str2, generateKey2);
                    encryptedSecretKeyData = new EncryptedSecretKeyData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(generatePkbdAlgorithmIdentifier2, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(createCipher2.getParameters().getEncoded())))), createCipher2.doFinal(secretKeyData.getEncoded()));
                } else {
                    encryptedSecretKeyData = new EncryptedSecretKeyData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(generatePkbdAlgorithmIdentifier2, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_wrap_pad))), createCipher(str3, generateKey2).doFinal(secretKeyData.getEncoded()));
                }
                Map<String, ObjectData> map3 = this.entries;
                ObjectData objectData3 = new ObjectData(SECRET_KEY, str, extractCreationDate, date, encryptedSecretKeyData.getEncoded(), null);
                map3.put(str, objectData3);
            } catch (Exception e2) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(str4);
                sb4.append(e2.toString());
                throw new ExtKeyStoreException(sb4.toString(), e2);
            }
        } else {
            throw new KeyStoreException("BCFKS KeyStore cannot store certificate chain with secret key.");
        }
        this.lastModifiedDate = date;
    }

    public void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
        Date date = new Date();
        ObjectData objectData = (ObjectData) this.entries.get(str);
        Date extractCreationDate = objectData != null ? extractCreationDate(objectData, date) : date;
        String str2 = "BCFKS KeyStore exception storing protected private key: ";
        if (certificateArr != null) {
            try {
                EncryptedPrivateKeyInfo instance = EncryptedPrivateKeyInfo.getInstance(bArr);
                try {
                    this.privateKeyCache.remove(str);
                    Map<String, ObjectData> map = this.entries;
                    ObjectData objectData2 = new ObjectData(PROTECTED_PRIVATE_KEY, str, extractCreationDate, date, createPrivateKeySequence(instance, certificateArr).getEncoded(), null);
                    map.put(str, objectData2);
                } catch (Exception e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str2);
                    sb.append(e.toString());
                    throw new ExtKeyStoreException(sb.toString(), e);
                }
            } catch (Exception e2) {
                throw new ExtKeyStoreException("BCFKS KeyStore private key encoding must be an EncryptedPrivateKeyInfo.", e2);
            }
        } else {
            try {
                Map<String, ObjectData> map2 = this.entries;
                ObjectData objectData3 = new ObjectData(PROTECTED_SECRET_KEY, str, extractCreationDate, date, bArr, null);
                map2.put(str, objectData3);
            } catch (Exception e3) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str2);
                sb2.append(e3.toString());
                throw new ExtKeyStoreException(sb2.toString(), e3);
            }
        }
        this.lastModifiedDate = date;
    }

    public int engineSize() {
        return this.entries.size();
    }

    public void engineStore(OutputStream outputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException {
        KeyDerivationFunc keyDerivationFunc;
        BigInteger bigInteger;
        if (this.creationDate != null) {
            EncryptedObjectStoreData encryptedObjectStoreData = getEncryptedObjectStoreData(this.hmacAlgorithm, cArr);
            if (MiscObjectIdentifiers.id_scrypt.equals(this.hmacPkbdAlgorithm.getAlgorithm())) {
                ScryptParams instance = ScryptParams.getInstance(this.hmacPkbdAlgorithm.getParameters());
                keyDerivationFunc = this.hmacPkbdAlgorithm;
                bigInteger = instance.getKeyLength();
            } else {
                PBKDF2Params instance2 = PBKDF2Params.getInstance(this.hmacPkbdAlgorithm.getParameters());
                keyDerivationFunc = this.hmacPkbdAlgorithm;
                bigInteger = instance2.getKeyLength();
            }
            this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(keyDerivationFunc, bigInteger.intValue());
            try {
                outputStream.write(new ObjectStore(encryptedObjectStoreData, new ObjectStoreIntegrityCheck(new PbkdMacIntegrityCheck(this.hmacAlgorithm, this.hmacPkbdAlgorithm, calculateMac(encryptedObjectStoreData.getEncoded(), this.hmacAlgorithm, this.hmacPkbdAlgorithm, cArr)))).getEncoded());
                outputStream.flush();
            } catch (NoSuchProviderException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("cannot calculate mac: ");
                sb.append(e.getMessage());
                throw new IOException(sb.toString());
            }
        } else {
            throw new IOException("KeyStore not initialized");
        }
    }

    public void engineStore(LoadStoreParameter loadStoreParameter) throws CertificateException, NoSuchAlgorithmException, IOException {
        SignatureCheck signatureCheck;
        if (loadStoreParameter == null) {
            throw new IllegalArgumentException("'parameter' arg cannot be null");
        } else if (loadStoreParameter instanceof BCFKSStoreParameter) {
            BCFKSStoreParameter bCFKSStoreParameter = (BCFKSStoreParameter) loadStoreParameter;
            char[] extractPassword = extractPassword(loadStoreParameter);
            this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(bCFKSStoreParameter.getStorePBKDFConfig(), 64);
            engineStore(bCFKSStoreParameter.getOutputStream(), extractPassword);
        } else if (loadStoreParameter instanceof BCFKSLoadStoreParameter) {
            BCFKSLoadStoreParameter bCFKSLoadStoreParameter = (BCFKSLoadStoreParameter) loadStoreParameter;
            if (bCFKSLoadStoreParameter.getStoreSignatureKey() != null) {
                this.signatureAlgorithm = generateSignatureAlgId(bCFKSLoadStoreParameter.getStoreSignatureKey(), bCFKSLoadStoreParameter.getStoreSignatureAlgorithm());
                this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(bCFKSLoadStoreParameter.getStorePBKDFConfig(), 64);
                this.storeEncryptionAlgorithm = bCFKSLoadStoreParameter.getStoreEncryptionAlgorithm() == EncryptionAlgorithm.AES256_CCM ? NISTObjectIdentifiers.id_aes256_CCM : NISTObjectIdentifiers.id_aes256_wrap_pad;
                this.hmacAlgorithm = bCFKSLoadStoreParameter.getStoreMacAlgorithm() == MacAlgorithm.HmacSHA512 ? new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE) : new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE);
                EncryptedObjectStoreData encryptedObjectStoreData = getEncryptedObjectStoreData(this.signatureAlgorithm, extractPassword(bCFKSLoadStoreParameter));
                try {
                    Signature createSignature = this.helper.createSignature(this.signatureAlgorithm.getAlgorithm().getId());
                    createSignature.initSign((PrivateKey) bCFKSLoadStoreParameter.getStoreSignatureKey());
                    createSignature.update(encryptedObjectStoreData.getEncoded());
                    X509Certificate[] storeCertificates = bCFKSLoadStoreParameter.getStoreCertificates();
                    if (storeCertificates != null) {
                        org.bouncycastle.asn1.x509.Certificate[] certificateArr = new org.bouncycastle.asn1.x509.Certificate[storeCertificates.length];
                        for (int i = 0; i != certificateArr.length; i++) {
                            certificateArr[i] = org.bouncycastle.asn1.x509.Certificate.getInstance(storeCertificates[i].getEncoded());
                        }
                        signatureCheck = new SignatureCheck(this.signatureAlgorithm, certificateArr, createSignature.sign());
                    } else {
                        signatureCheck = new SignatureCheck(this.signatureAlgorithm, createSignature.sign());
                    }
                    bCFKSLoadStoreParameter.getOutputStream().write(new ObjectStore(encryptedObjectStoreData, new ObjectStoreIntegrityCheck(signatureCheck)).getEncoded());
                    bCFKSLoadStoreParameter.getOutputStream().flush();
                } catch (GeneralSecurityException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("error creating signature: ");
                    sb.append(e.getMessage());
                    throw new IOException(sb.toString(), e);
                }
            } else {
                char[] extractPassword2 = extractPassword(bCFKSLoadStoreParameter);
                this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(bCFKSLoadStoreParameter.getStorePBKDFConfig(), 64);
                this.storeEncryptionAlgorithm = bCFKSLoadStoreParameter.getStoreEncryptionAlgorithm() == EncryptionAlgorithm.AES256_CCM ? NISTObjectIdentifiers.id_aes256_CCM : NISTObjectIdentifiers.id_aes256_wrap_pad;
                this.hmacAlgorithm = bCFKSLoadStoreParameter.getStoreMacAlgorithm() == MacAlgorithm.HmacSHA512 ? new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE) : new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE);
                engineStore(bCFKSLoadStoreParameter.getOutputStream(), extractPassword2);
            }
        } else if (loadStoreParameter instanceof BCLoadStoreParameter) {
            engineStore(((BCLoadStoreParameter) loadStoreParameter).getOutputStream(), extractPassword(loadStoreParameter));
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("no support for 'parameter' of type ");
            sb2.append(loadStoreParameter.getClass().getName());
            throw new IllegalArgumentException(sb2.toString());
        }
    }
}
