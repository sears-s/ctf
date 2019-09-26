package org.bouncycastle.jcajce.provider.keystore.pkcs12;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
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
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.cms.CMSEnvelopedGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.PKCS12StoreParameter;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.BCKeyStore;
import org.bouncycastle.jce.provider.JDKPKCS12StoreParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;

public class PKCS12KeyStoreSpi extends KeyStoreSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers, BCKeyStore {
    static final int CERTIFICATE = 1;
    static final int KEY = 2;
    static final int KEY_PRIVATE = 0;
    static final int KEY_PUBLIC = 1;
    static final int KEY_SECRET = 2;
    private static final int MIN_ITERATIONS = 51200;
    static final int NULL = 0;
    static final String PKCS12_MAX_IT_COUNT_PROPERTY = "org.bouncycastle.pkcs12.max_it_count";
    private static final int SALT_SIZE = 20;
    static final int SEALED = 4;
    static final int SECRET = 3;
    private static final DefaultSecretKeyProvider keySizeProvider = new DefaultSecretKeyProvider();
    private ASN1ObjectIdentifier certAlgorithm;
    private CertificateFactory certFact;
    private IgnoresCaseHashtable certs = new IgnoresCaseHashtable();
    private Hashtable chainCerts = new Hashtable();
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private int itCount = 102400;
    private ASN1ObjectIdentifier keyAlgorithm;
    private Hashtable keyCerts = new Hashtable();
    private IgnoresCaseHashtable keys = new IgnoresCaseHashtable();
    private Hashtable localIds = new Hashtable();
    private AlgorithmIdentifier macAlgorithm = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
    protected SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    private int saltLength = 20;

    public static class BCPKCS12KeyStore extends PKCS12KeyStoreSpi {
        public BCPKCS12KeyStore() {
            super(new BCJcaJceHelper(), pbeWithSHAAnd3_KeyTripleDES_CBC, pbeWithSHAAnd40BitRC2_CBC);
        }
    }

    public static class BCPKCS12KeyStore3DES extends PKCS12KeyStoreSpi {
        public BCPKCS12KeyStore3DES() {
            super(new BCJcaJceHelper(), pbeWithSHAAnd3_KeyTripleDES_CBC, pbeWithSHAAnd3_KeyTripleDES_CBC);
        }
    }

    private class CertId {
        byte[] id;

        CertId(PublicKey publicKey) {
            this.id = PKCS12KeyStoreSpi.this.createSubjectKeyId(publicKey).getKeyIdentifier();
        }

        CertId(byte[] bArr) {
            this.id = bArr;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CertId)) {
                return false;
            }
            return Arrays.areEqual(this.id, ((CertId) obj).id);
        }

        public int hashCode() {
            return Arrays.hashCode(this.id);
        }
    }

    public static class DefPKCS12KeyStore extends PKCS12KeyStoreSpi {
        public DefPKCS12KeyStore() {
            super(new DefaultJcaJceHelper(), pbeWithSHAAnd3_KeyTripleDES_CBC, pbeWithSHAAnd40BitRC2_CBC);
        }
    }

    public static class DefPKCS12KeyStore3DES extends PKCS12KeyStoreSpi {
        public DefPKCS12KeyStore3DES() {
            super(new DefaultJcaJceHelper(), pbeWithSHAAnd3_KeyTripleDES_CBC, pbeWithSHAAnd3_KeyTripleDES_CBC);
        }
    }

    private static class DefaultSecretKeyProvider {
        private final Map KEY_SIZES;

        DefaultSecretKeyProvider() {
            HashMap hashMap = new HashMap();
            hashMap.put(new ASN1ObjectIdentifier(CMSEnvelopedGenerator.CAST5_CBC), Integers.valueOf(128));
            hashMap.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
            hashMap.put(NISTObjectIdentifiers.id_aes128_CBC, Integers.valueOf(128));
            hashMap.put(NISTObjectIdentifiers.id_aes192_CBC, Integers.valueOf(192));
            hashMap.put(NISTObjectIdentifiers.id_aes256_CBC, Integers.valueOf(256));
            hashMap.put(NTTObjectIdentifiers.id_camellia128_cbc, Integers.valueOf(128));
            hashMap.put(NTTObjectIdentifiers.id_camellia192_cbc, Integers.valueOf(192));
            hashMap.put(NTTObjectIdentifiers.id_camellia256_cbc, Integers.valueOf(256));
            hashMap.put(CryptoProObjectIdentifiers.gostR28147_gcfb, Integers.valueOf(256));
            this.KEY_SIZES = Collections.unmodifiableMap(hashMap);
        }

        public int getKeySize(AlgorithmIdentifier algorithmIdentifier) {
            Integer num = (Integer) this.KEY_SIZES.get(algorithmIdentifier.getAlgorithm());
            if (num != null) {
                return num.intValue();
            }
            return -1;
        }
    }

    private static class IgnoresCaseHashtable {
        private Hashtable keys;
        private Hashtable orig;

        private IgnoresCaseHashtable() {
            this.orig = new Hashtable();
            this.keys = new Hashtable();
        }

        public Enumeration elements() {
            return this.orig.elements();
        }

        public Object get(String str) {
            String str2 = (String) this.keys.get(str == null ? null : Strings.toLowerCase(str));
            if (str2 == null) {
                return null;
            }
            return this.orig.get(str2);
        }

        public Enumeration keys() {
            return this.orig.keys();
        }

        public void put(String str, Object obj) {
            String lowerCase = str == null ? null : Strings.toLowerCase(str);
            String str2 = (String) this.keys.get(lowerCase);
            if (str2 != null) {
                this.orig.remove(str2);
            }
            this.keys.put(lowerCase, str);
            this.orig.put(str, obj);
        }

        public Object remove(String str) {
            String str2 = (String) this.keys.remove(str == null ? null : Strings.toLowerCase(str));
            if (str2 == null) {
                return null;
            }
            return this.orig.remove(str2);
        }
    }

    public PKCS12KeyStoreSpi(JcaJceHelper jcaJceHelper, ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2) {
        this.keyAlgorithm = aSN1ObjectIdentifier;
        this.certAlgorithm = aSN1ObjectIdentifier2;
        try {
            this.certFact = jcaJceHelper.createCertificateFactory("X.509");
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("can't create cert factory - ");
            sb.append(e.toString());
            throw new IllegalArgumentException(sb.toString());
        }
    }

    private byte[] calculatePbeMac(ASN1ObjectIdentifier aSN1ObjectIdentifier, byte[] bArr, int i, char[] cArr, boolean z, byte[] bArr2) throws Exception {
        PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(bArr, i);
        Mac createMac = this.helper.createMac(aSN1ObjectIdentifier.getId());
        createMac.init(new PKCS12Key(cArr, z), pBEParameterSpec);
        createMac.update(bArr2);
        return createMac.doFinal();
    }

    private Cipher createCipher(int i, char[] cArr, AlgorithmIdentifier algorithmIdentifier) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException {
        SecretKey secretKey;
        AlgorithmParameterSpec algorithmParameterSpec;
        PBES2Parameters instance = PBES2Parameters.getInstance(algorithmIdentifier.getParameters());
        PBKDF2Params instance2 = PBKDF2Params.getInstance(instance.getKeyDerivationFunc().getParameters());
        AlgorithmIdentifier instance3 = AlgorithmIdentifier.getInstance(instance.getEncryptionScheme());
        SecretKeyFactory createSecretKeyFactory = this.helper.createSecretKeyFactory(instance.getKeyDerivationFunc().getAlgorithm().getId());
        if (instance2.isDefaultPrf()) {
            secretKey = createSecretKeyFactory.generateSecret(new PBEKeySpec(cArr, instance2.getSalt(), validateIterationCount(instance2.getIterationCount()), keySizeProvider.getKeySize(instance3)));
        } else {
            PBKDF2KeySpec pBKDF2KeySpec = new PBKDF2KeySpec(cArr, instance2.getSalt(), validateIterationCount(instance2.getIterationCount()), keySizeProvider.getKeySize(instance3), instance2.getPrf());
            secretKey = createSecretKeyFactory.generateSecret(pBKDF2KeySpec);
        }
        Cipher instance4 = Cipher.getInstance(instance.getEncryptionScheme().getAlgorithm().getId());
        ASN1Encodable parameters = instance.getEncryptionScheme().getParameters();
        if (parameters instanceof ASN1OctetString) {
            algorithmParameterSpec = new IvParameterSpec(ASN1OctetString.getInstance(parameters).getOctets());
        } else {
            GOST28147Parameters instance5 = GOST28147Parameters.getInstance(parameters);
            algorithmParameterSpec = new GOST28147ParameterSpec(instance5.getEncryptionParamSet(), instance5.getIV());
        }
        instance4.init(i, secretKey, algorithmParameterSpec);
        return instance4;
    }

    /* access modifiers changed from: private */
    public SubjectKeyIdentifier createSubjectKeyId(PublicKey publicKey) {
        try {
            return new SubjectKeyIdentifier(getDigest(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded())));
        } catch (Exception e) {
            throw new RuntimeException("error creating key");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x01b1 A[Catch:{ CertificateEncodingException -> 0x0255 }] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x01c9 A[Catch:{ CertificateEncodingException -> 0x0255 }, LOOP:3: B:40:0x01c3->B:42:0x01c9, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void doStore(java.io.OutputStream r19, char[] r20, boolean r21) throws java.io.IOException {
        /*
            r18 = this;
            r8 = r18
            r0 = r19
            r7 = r20
            if (r7 == 0) goto L_0x04eb
            org.bouncycastle.asn1.ASN1EncodableVector r1 = new org.bouncycastle.asn1.ASN1EncodableVector
            r1.<init>()
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r2 = r8.keys
            java.util.Enumeration r2 = r2.keys()
        L_0x0013:
            boolean r3 = r2.hasMoreElements()
            r4 = 51200(0xc800, float:7.1746E-41)
            r5 = 20
            if (r3 == 0) goto L_0x0120
            byte[] r3 = new byte[r5]
            java.security.SecureRandom r5 = r8.random
            r5.nextBytes(r3)
            java.lang.Object r5 = r2.nextElement()
            java.lang.String r5 = (java.lang.String) r5
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r6 = r8.keys
            java.lang.Object r6 = r6.get(r5)
            java.security.PrivateKey r6 = (java.security.PrivateKey) r6
            org.bouncycastle.asn1.pkcs.PKCS12PBEParams r11 = new org.bouncycastle.asn1.pkcs.PKCS12PBEParams
            r11.<init>(r3, r4)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r3 = r8.keyAlgorithm
            java.lang.String r3 = r3.getId()
            byte[] r3 = r8.wrapKey(r3, r6, r11, r7)
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r4 = new org.bouncycastle.asn1.x509.AlgorithmIdentifier
            org.bouncycastle.asn1.ASN1ObjectIdentifier r12 = r8.keyAlgorithm
            org.bouncycastle.asn1.ASN1Primitive r11 = r11.toASN1Primitive()
            r4.<init>(r12, r11)
            org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo r11 = new org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo
            r11.<init>(r4, r3)
            org.bouncycastle.asn1.ASN1EncodableVector r3 = new org.bouncycastle.asn1.ASN1EncodableVector
            r3.<init>()
            boolean r4 = r6 instanceof org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier
            if (r4 == 0) goto L_0x00c3
            org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier r6 = (org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier) r6
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = pkcs_9_at_friendlyName
            org.bouncycastle.asn1.ASN1Encodable r4 = r6.getBagAttribute(r4)
            org.bouncycastle.asn1.DERBMPString r4 = (org.bouncycastle.asn1.DERBMPString) r4
            if (r4 == 0) goto L_0x0071
            java.lang.String r4 = r4.getString()
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto L_0x007b
        L_0x0071:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = pkcs_9_at_friendlyName
            org.bouncycastle.asn1.DERBMPString r12 = new org.bouncycastle.asn1.DERBMPString
            r12.<init>(r5)
            r6.setBagAttribute(r4, r12)
        L_0x007b:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = pkcs_9_at_localKeyId
            org.bouncycastle.asn1.ASN1Encodable r4 = r6.getBagAttribute(r4)
            if (r4 != 0) goto L_0x0094
            java.security.cert.Certificate r4 = r8.engineGetCertificate(r5)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r12 = pkcs_9_at_localKeyId
            java.security.PublicKey r4 = r4.getPublicKey()
            org.bouncycastle.asn1.x509.SubjectKeyIdentifier r4 = r8.createSubjectKeyId(r4)
            r6.setBagAttribute(r12, r4)
        L_0x0094:
            java.util.Enumeration r4 = r6.getBagAttributeKeys()
            r10 = 0
        L_0x0099:
            boolean r12 = r4.hasMoreElements()
            if (r12 == 0) goto L_0x00c4
            java.lang.Object r10 = r4.nextElement()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r10 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r10
            org.bouncycastle.asn1.ASN1EncodableVector r12 = new org.bouncycastle.asn1.ASN1EncodableVector
            r12.<init>()
            r12.add(r10)
            org.bouncycastle.asn1.DERSet r13 = new org.bouncycastle.asn1.DERSet
            org.bouncycastle.asn1.ASN1Encodable r10 = r6.getBagAttribute(r10)
            r13.<init>(r10)
            r12.add(r13)
            org.bouncycastle.asn1.DERSequence r10 = new org.bouncycastle.asn1.DERSequence
            r10.<init>(r12)
            r3.add(r10)
            r10 = 1
            goto L_0x0099
        L_0x00c3:
            r10 = 0
        L_0x00c4:
            if (r10 != 0) goto L_0x010b
            org.bouncycastle.asn1.ASN1EncodableVector r4 = new org.bouncycastle.asn1.ASN1EncodableVector
            r4.<init>()
            java.security.cert.Certificate r6 = r8.engineGetCertificate(r5)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r9 = pkcs_9_at_localKeyId
            r4.add(r9)
            org.bouncycastle.asn1.DERSet r9 = new org.bouncycastle.asn1.DERSet
            java.security.PublicKey r6 = r6.getPublicKey()
            org.bouncycastle.asn1.x509.SubjectKeyIdentifier r6 = r8.createSubjectKeyId(r6)
            r9.<init>(r6)
            r4.add(r9)
            org.bouncycastle.asn1.DERSequence r6 = new org.bouncycastle.asn1.DERSequence
            r6.<init>(r4)
            r3.add(r6)
            org.bouncycastle.asn1.ASN1EncodableVector r4 = new org.bouncycastle.asn1.ASN1EncodableVector
            r4.<init>()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r6 = pkcs_9_at_friendlyName
            r4.add(r6)
            org.bouncycastle.asn1.DERSet r6 = new org.bouncycastle.asn1.DERSet
            org.bouncycastle.asn1.DERBMPString r9 = new org.bouncycastle.asn1.DERBMPString
            r9.<init>(r5)
            r6.<init>(r9)
            r4.add(r6)
            org.bouncycastle.asn1.DERSequence r5 = new org.bouncycastle.asn1.DERSequence
            r5.<init>(r4)
            r3.add(r5)
        L_0x010b:
            org.bouncycastle.asn1.pkcs.SafeBag r4 = new org.bouncycastle.asn1.pkcs.SafeBag
            org.bouncycastle.asn1.ASN1ObjectIdentifier r5 = pkcs8ShroudedKeyBag
            org.bouncycastle.asn1.ASN1Primitive r6 = r11.toASN1Primitive()
            org.bouncycastle.asn1.DERSet r9 = new org.bouncycastle.asn1.DERSet
            r9.<init>(r3)
            r4.<init>(r5, r6, r9)
            r1.add(r4)
            goto L_0x0013
        L_0x0120:
            org.bouncycastle.asn1.DERSequence r2 = new org.bouncycastle.asn1.DERSequence
            r2.<init>(r1)
            java.lang.String r1 = "DER"
            byte[] r2 = r2.getEncoded(r1)
            org.bouncycastle.asn1.BEROctetString r11 = new org.bouncycastle.asn1.BEROctetString
            r11.<init>(r2)
            byte[] r2 = new byte[r5]
            java.security.SecureRandom r3 = r8.random
            r3.nextBytes(r2)
            org.bouncycastle.asn1.ASN1EncodableVector r3 = new org.bouncycastle.asn1.ASN1EncodableVector
            r3.<init>()
            org.bouncycastle.asn1.pkcs.PKCS12PBEParams r5 = new org.bouncycastle.asn1.pkcs.PKCS12PBEParams
            r5.<init>(r2, r4)
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r12 = new org.bouncycastle.asn1.x509.AlgorithmIdentifier
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = r8.certAlgorithm
            org.bouncycastle.asn1.ASN1Primitive r4 = r5.toASN1Primitive()
            r12.<init>(r2, r4)
            java.util.Hashtable r2 = new java.util.Hashtable
            r2.<init>()
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r4 = r8.keys
            java.util.Enumeration r4 = r4.keys()
        L_0x0157:
            boolean r5 = r4.hasMoreElements()
            java.lang.String r6 = "Error encoding certificate: "
            if (r5 == 0) goto L_0x026f
            java.lang.Object r5 = r4.nextElement()     // Catch:{ CertificateEncodingException -> 0x0255 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ CertificateEncodingException -> 0x0255 }
            java.security.cert.Certificate r13 = r8.engineGetCertificate(r5)     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.pkcs.CertBag r14 = new org.bouncycastle.asn1.pkcs.CertBag     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r15 = x509Certificate     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.DEROctetString r9 = new org.bouncycastle.asn1.DEROctetString     // Catch:{ CertificateEncodingException -> 0x0255 }
            byte[] r10 = r13.getEncoded()     // Catch:{ CertificateEncodingException -> 0x0255 }
            r9.<init>(r10)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r14.<init>(r15, r9)     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.ASN1EncodableVector r9 = new org.bouncycastle.asn1.ASN1EncodableVector     // Catch:{ CertificateEncodingException -> 0x0255 }
            r9.<init>()     // Catch:{ CertificateEncodingException -> 0x0255 }
            boolean r10 = r13 instanceof org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier     // Catch:{ CertificateEncodingException -> 0x0255 }
            if (r10 == 0) goto L_0x01f3
            r10 = r13
            org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier r10 = (org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier) r10     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r15 = pkcs_9_at_friendlyName     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.ASN1Encodable r15 = r10.getBagAttribute(r15)     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.DERBMPString r15 = (org.bouncycastle.asn1.DERBMPString) r15     // Catch:{ CertificateEncodingException -> 0x0255 }
            if (r15 == 0) goto L_0x019d
            java.lang.String r15 = r15.getString()     // Catch:{ CertificateEncodingException -> 0x0255 }
            boolean r15 = r15.equals(r5)     // Catch:{ CertificateEncodingException -> 0x0255 }
            if (r15 != 0) goto L_0x019a
            goto L_0x019d
        L_0x019a:
            r16 = r4
            goto L_0x01a9
        L_0x019d:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r15 = pkcs_9_at_friendlyName     // Catch:{ CertificateEncodingException -> 0x0255 }
            r16 = r4
            org.bouncycastle.asn1.DERBMPString r4 = new org.bouncycastle.asn1.DERBMPString     // Catch:{ CertificateEncodingException -> 0x0255 }
            r4.<init>(r5)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r10.setBagAttribute(r15, r4)     // Catch:{ CertificateEncodingException -> 0x0255 }
        L_0x01a9:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = pkcs_9_at_localKeyId     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.ASN1Encodable r4 = r10.getBagAttribute(r4)     // Catch:{ CertificateEncodingException -> 0x0255 }
            if (r4 != 0) goto L_0x01be
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = pkcs_9_at_localKeyId     // Catch:{ CertificateEncodingException -> 0x0255 }
            java.security.PublicKey r15 = r13.getPublicKey()     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.x509.SubjectKeyIdentifier r15 = r8.createSubjectKeyId(r15)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r10.setBagAttribute(r4, r15)     // Catch:{ CertificateEncodingException -> 0x0255 }
        L_0x01be:
            java.util.Enumeration r4 = r10.getBagAttributeKeys()     // Catch:{ CertificateEncodingException -> 0x0255 }
            r15 = 0
        L_0x01c3:
            boolean r17 = r4.hasMoreElements()     // Catch:{ CertificateEncodingException -> 0x0255 }
            if (r17 == 0) goto L_0x01f6
            java.lang.Object r15 = r4.nextElement()     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r15 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r15     // Catch:{ CertificateEncodingException -> 0x0255 }
            r17 = r4
            org.bouncycastle.asn1.ASN1EncodableVector r4 = new org.bouncycastle.asn1.ASN1EncodableVector     // Catch:{ CertificateEncodingException -> 0x0255 }
            r4.<init>()     // Catch:{ CertificateEncodingException -> 0x0255 }
            r4.add(r15)     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.DERSet r7 = new org.bouncycastle.asn1.DERSet     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.ASN1Encodable r15 = r10.getBagAttribute(r15)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r7.<init>(r15)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r4.add(r7)     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.DERSequence r7 = new org.bouncycastle.asn1.DERSequence     // Catch:{ CertificateEncodingException -> 0x0255 }
            r7.<init>(r4)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r9.add(r7)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r7 = r20
            r4 = r17
            r15 = 1
            goto L_0x01c3
        L_0x01f3:
            r16 = r4
            r15 = 0
        L_0x01f6:
            if (r15 != 0) goto L_0x0239
            org.bouncycastle.asn1.ASN1EncodableVector r4 = new org.bouncycastle.asn1.ASN1EncodableVector     // Catch:{ CertificateEncodingException -> 0x0255 }
            r4.<init>()     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r7 = pkcs_9_at_localKeyId     // Catch:{ CertificateEncodingException -> 0x0255 }
            r4.add(r7)     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.DERSet r7 = new org.bouncycastle.asn1.DERSet     // Catch:{ CertificateEncodingException -> 0x0255 }
            java.security.PublicKey r10 = r13.getPublicKey()     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.x509.SubjectKeyIdentifier r10 = r8.createSubjectKeyId(r10)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r7.<init>(r10)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r4.add(r7)     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.DERSequence r7 = new org.bouncycastle.asn1.DERSequence     // Catch:{ CertificateEncodingException -> 0x0255 }
            r7.<init>(r4)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r9.add(r7)     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.ASN1EncodableVector r4 = new org.bouncycastle.asn1.ASN1EncodableVector     // Catch:{ CertificateEncodingException -> 0x0255 }
            r4.<init>()     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r7 = pkcs_9_at_friendlyName     // Catch:{ CertificateEncodingException -> 0x0255 }
            r4.add(r7)     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.DERSet r7 = new org.bouncycastle.asn1.DERSet     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.DERBMPString r10 = new org.bouncycastle.asn1.DERBMPString     // Catch:{ CertificateEncodingException -> 0x0255 }
            r10.<init>(r5)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r7.<init>(r10)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r4.add(r7)     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.DERSequence r5 = new org.bouncycastle.asn1.DERSequence     // Catch:{ CertificateEncodingException -> 0x0255 }
            r5.<init>(r4)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r9.add(r5)     // Catch:{ CertificateEncodingException -> 0x0255 }
        L_0x0239:
            org.bouncycastle.asn1.pkcs.SafeBag r4 = new org.bouncycastle.asn1.pkcs.SafeBag     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r5 = certBag     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.ASN1Primitive r7 = r14.toASN1Primitive()     // Catch:{ CertificateEncodingException -> 0x0255 }
            org.bouncycastle.asn1.DERSet r10 = new org.bouncycastle.asn1.DERSet     // Catch:{ CertificateEncodingException -> 0x0255 }
            r10.<init>(r9)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r4.<init>(r5, r7, r10)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r3.add(r4)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r2.put(r13, r13)     // Catch:{ CertificateEncodingException -> 0x0255 }
            r7 = r20
            r4 = r16
            goto L_0x0157
        L_0x0255:
            r0 = move-exception
            java.io.IOException r1 = new java.io.IOException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r6)
            java.lang.String r0 = r0.toString()
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1.<init>(r0)
            throw r1
        L_0x026f:
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r4 = r8.certs
            java.util.Enumeration r4 = r4.keys()
        L_0x0275:
            boolean r5 = r4.hasMoreElements()
            if (r5 == 0) goto L_0x036d
            java.lang.Object r5 = r4.nextElement()     // Catch:{ CertificateEncodingException -> 0x0353 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r7 = r8.certs     // Catch:{ CertificateEncodingException -> 0x0353 }
            java.lang.Object r7 = r7.get(r5)     // Catch:{ CertificateEncodingException -> 0x0353 }
            java.security.cert.Certificate r7 = (java.security.cert.Certificate) r7     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r9 = r8.keys     // Catch:{ CertificateEncodingException -> 0x0353 }
            java.lang.Object r9 = r9.get(r5)     // Catch:{ CertificateEncodingException -> 0x0353 }
            if (r9 == 0) goto L_0x0292
            goto L_0x0275
        L_0x0292:
            org.bouncycastle.asn1.pkcs.CertBag r9 = new org.bouncycastle.asn1.pkcs.CertBag     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r10 = x509Certificate     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.DEROctetString r13 = new org.bouncycastle.asn1.DEROctetString     // Catch:{ CertificateEncodingException -> 0x0353 }
            byte[] r14 = r7.getEncoded()     // Catch:{ CertificateEncodingException -> 0x0353 }
            r13.<init>(r14)     // Catch:{ CertificateEncodingException -> 0x0353 }
            r9.<init>(r10, r13)     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.ASN1EncodableVector r10 = new org.bouncycastle.asn1.ASN1EncodableVector     // Catch:{ CertificateEncodingException -> 0x0353 }
            r10.<init>()     // Catch:{ CertificateEncodingException -> 0x0353 }
            boolean r13 = r7 instanceof org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier     // Catch:{ CertificateEncodingException -> 0x0353 }
            if (r13 == 0) goto L_0x0315
            r13 = r7
            org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier r13 = (org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier) r13     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r14 = pkcs_9_at_friendlyName     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.ASN1Encodable r14 = r13.getBagAttribute(r14)     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.DERBMPString r14 = (org.bouncycastle.asn1.DERBMPString) r14     // Catch:{ CertificateEncodingException -> 0x0353 }
            if (r14 == 0) goto L_0x02c2
            java.lang.String r14 = r14.getString()     // Catch:{ CertificateEncodingException -> 0x0353 }
            boolean r14 = r14.equals(r5)     // Catch:{ CertificateEncodingException -> 0x0353 }
            if (r14 != 0) goto L_0x02cc
        L_0x02c2:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r14 = pkcs_9_at_friendlyName     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.DERBMPString r15 = new org.bouncycastle.asn1.DERBMPString     // Catch:{ CertificateEncodingException -> 0x0353 }
            r15.<init>(r5)     // Catch:{ CertificateEncodingException -> 0x0353 }
            r13.setBagAttribute(r14, r15)     // Catch:{ CertificateEncodingException -> 0x0353 }
        L_0x02cc:
            java.util.Enumeration r14 = r13.getBagAttributeKeys()     // Catch:{ CertificateEncodingException -> 0x0353 }
            r15 = 0
        L_0x02d1:
            boolean r16 = r14.hasMoreElements()     // Catch:{ CertificateEncodingException -> 0x0353 }
            if (r16 == 0) goto L_0x0312
            java.lang.Object r16 = r14.nextElement()     // Catch:{ CertificateEncodingException -> 0x0353 }
            r17 = r4
            r4 = r16
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r4     // Catch:{ CertificateEncodingException -> 0x0353 }
            r16 = r14
            org.bouncycastle.asn1.ASN1ObjectIdentifier r14 = org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers.pkcs_9_at_localKeyId     // Catch:{ CertificateEncodingException -> 0x0353 }
            boolean r14 = r4.equals(r14)     // Catch:{ CertificateEncodingException -> 0x0353 }
            if (r14 == 0) goto L_0x02f0
            r14 = r16
            r4 = r17
            goto L_0x02d1
        L_0x02f0:
            org.bouncycastle.asn1.ASN1EncodableVector r14 = new org.bouncycastle.asn1.ASN1EncodableVector     // Catch:{ CertificateEncodingException -> 0x0353 }
            r14.<init>()     // Catch:{ CertificateEncodingException -> 0x0353 }
            r14.add(r4)     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.DERSet r15 = new org.bouncycastle.asn1.DERSet     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.ASN1Encodable r4 = r13.getBagAttribute(r4)     // Catch:{ CertificateEncodingException -> 0x0353 }
            r15.<init>(r4)     // Catch:{ CertificateEncodingException -> 0x0353 }
            r14.add(r15)     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.DERSequence r4 = new org.bouncycastle.asn1.DERSequence     // Catch:{ CertificateEncodingException -> 0x0353 }
            r4.<init>(r14)     // Catch:{ CertificateEncodingException -> 0x0353 }
            r10.add(r4)     // Catch:{ CertificateEncodingException -> 0x0353 }
            r14 = r16
            r4 = r17
            r15 = 1
            goto L_0x02d1
        L_0x0312:
            r17 = r4
            goto L_0x0318
        L_0x0315:
            r17 = r4
            r15 = 0
        L_0x0318:
            if (r15 != 0) goto L_0x0339
            org.bouncycastle.asn1.ASN1EncodableVector r4 = new org.bouncycastle.asn1.ASN1EncodableVector     // Catch:{ CertificateEncodingException -> 0x0353 }
            r4.<init>()     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r13 = pkcs_9_at_friendlyName     // Catch:{ CertificateEncodingException -> 0x0353 }
            r4.add(r13)     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.DERSet r13 = new org.bouncycastle.asn1.DERSet     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.DERBMPString r14 = new org.bouncycastle.asn1.DERBMPString     // Catch:{ CertificateEncodingException -> 0x0353 }
            r14.<init>(r5)     // Catch:{ CertificateEncodingException -> 0x0353 }
            r13.<init>(r14)     // Catch:{ CertificateEncodingException -> 0x0353 }
            r4.add(r13)     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.DERSequence r5 = new org.bouncycastle.asn1.DERSequence     // Catch:{ CertificateEncodingException -> 0x0353 }
            r5.<init>(r4)     // Catch:{ CertificateEncodingException -> 0x0353 }
            r10.add(r5)     // Catch:{ CertificateEncodingException -> 0x0353 }
        L_0x0339:
            org.bouncycastle.asn1.pkcs.SafeBag r4 = new org.bouncycastle.asn1.pkcs.SafeBag     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r5 = certBag     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.ASN1Primitive r9 = r9.toASN1Primitive()     // Catch:{ CertificateEncodingException -> 0x0353 }
            org.bouncycastle.asn1.DERSet r13 = new org.bouncycastle.asn1.DERSet     // Catch:{ CertificateEncodingException -> 0x0353 }
            r13.<init>(r10)     // Catch:{ CertificateEncodingException -> 0x0353 }
            r4.<init>(r5, r9, r13)     // Catch:{ CertificateEncodingException -> 0x0353 }
            r3.add(r4)     // Catch:{ CertificateEncodingException -> 0x0353 }
            r2.put(r7, r7)     // Catch:{ CertificateEncodingException -> 0x0353 }
            r4 = r17
            goto L_0x0275
        L_0x0353:
            r0 = move-exception
            java.io.IOException r1 = new java.io.IOException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r6)
            java.lang.String r0 = r0.toString()
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1.<init>(r0)
            throw r1
        L_0x036d:
            java.util.Set r4 = r18.getUsedCertificateSet()
            java.util.Hashtable r5 = r8.chainCerts
            java.util.Enumeration r5 = r5.keys()
        L_0x0377:
            boolean r7 = r5.hasMoreElements()
            if (r7 == 0) goto L_0x0421
            java.lang.Object r7 = r5.nextElement()     // Catch:{ CertificateEncodingException -> 0x0407 }
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$CertId r7 = (org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi.CertId) r7     // Catch:{ CertificateEncodingException -> 0x0407 }
            java.util.Hashtable r9 = r8.chainCerts     // Catch:{ CertificateEncodingException -> 0x0407 }
            java.lang.Object r7 = r9.get(r7)     // Catch:{ CertificateEncodingException -> 0x0407 }
            java.security.cert.Certificate r7 = (java.security.cert.Certificate) r7     // Catch:{ CertificateEncodingException -> 0x0407 }
            boolean r9 = r4.contains(r7)     // Catch:{ CertificateEncodingException -> 0x0407 }
            if (r9 != 0) goto L_0x0392
            goto L_0x0377
        L_0x0392:
            java.lang.Object r9 = r2.get(r7)     // Catch:{ CertificateEncodingException -> 0x0407 }
            if (r9 == 0) goto L_0x0399
            goto L_0x0377
        L_0x0399:
            org.bouncycastle.asn1.pkcs.CertBag r9 = new org.bouncycastle.asn1.pkcs.CertBag     // Catch:{ CertificateEncodingException -> 0x0407 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r10 = x509Certificate     // Catch:{ CertificateEncodingException -> 0x0407 }
            org.bouncycastle.asn1.DEROctetString r13 = new org.bouncycastle.asn1.DEROctetString     // Catch:{ CertificateEncodingException -> 0x0407 }
            byte[] r14 = r7.getEncoded()     // Catch:{ CertificateEncodingException -> 0x0407 }
            r13.<init>(r14)     // Catch:{ CertificateEncodingException -> 0x0407 }
            r9.<init>(r10, r13)     // Catch:{ CertificateEncodingException -> 0x0407 }
            org.bouncycastle.asn1.ASN1EncodableVector r10 = new org.bouncycastle.asn1.ASN1EncodableVector     // Catch:{ CertificateEncodingException -> 0x0407 }
            r10.<init>()     // Catch:{ CertificateEncodingException -> 0x0407 }
            boolean r13 = r7 instanceof org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier     // Catch:{ CertificateEncodingException -> 0x0407 }
            if (r13 == 0) goto L_0x03ee
            org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier r7 = (org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier) r7     // Catch:{ CertificateEncodingException -> 0x0407 }
            java.util.Enumeration r13 = r7.getBagAttributeKeys()     // Catch:{ CertificateEncodingException -> 0x0407 }
        L_0x03b8:
            boolean r14 = r13.hasMoreElements()     // Catch:{ CertificateEncodingException -> 0x0407 }
            if (r14 == 0) goto L_0x03ee
            java.lang.Object r14 = r13.nextElement()     // Catch:{ CertificateEncodingException -> 0x0407 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r14 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r14     // Catch:{ CertificateEncodingException -> 0x0407 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r15 = org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers.pkcs_9_at_localKeyId     // Catch:{ CertificateEncodingException -> 0x0407 }
            boolean r15 = r14.equals(r15)     // Catch:{ CertificateEncodingException -> 0x0407 }
            if (r15 == 0) goto L_0x03cd
            goto L_0x03b8
        L_0x03cd:
            org.bouncycastle.asn1.ASN1EncodableVector r15 = new org.bouncycastle.asn1.ASN1EncodableVector     // Catch:{ CertificateEncodingException -> 0x0407 }
            r15.<init>()     // Catch:{ CertificateEncodingException -> 0x0407 }
            r15.add(r14)     // Catch:{ CertificateEncodingException -> 0x0407 }
            r16 = r2
            org.bouncycastle.asn1.DERSet r2 = new org.bouncycastle.asn1.DERSet     // Catch:{ CertificateEncodingException -> 0x0407 }
            org.bouncycastle.asn1.ASN1Encodable r14 = r7.getBagAttribute(r14)     // Catch:{ CertificateEncodingException -> 0x0407 }
            r2.<init>(r14)     // Catch:{ CertificateEncodingException -> 0x0407 }
            r15.add(r2)     // Catch:{ CertificateEncodingException -> 0x0407 }
            org.bouncycastle.asn1.DERSequence r2 = new org.bouncycastle.asn1.DERSequence     // Catch:{ CertificateEncodingException -> 0x0407 }
            r2.<init>(r15)     // Catch:{ CertificateEncodingException -> 0x0407 }
            r10.add(r2)     // Catch:{ CertificateEncodingException -> 0x0407 }
            r2 = r16
            goto L_0x03b8
        L_0x03ee:
            r16 = r2
            org.bouncycastle.asn1.pkcs.SafeBag r2 = new org.bouncycastle.asn1.pkcs.SafeBag     // Catch:{ CertificateEncodingException -> 0x0407 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r7 = certBag     // Catch:{ CertificateEncodingException -> 0x0407 }
            org.bouncycastle.asn1.ASN1Primitive r9 = r9.toASN1Primitive()     // Catch:{ CertificateEncodingException -> 0x0407 }
            org.bouncycastle.asn1.DERSet r13 = new org.bouncycastle.asn1.DERSet     // Catch:{ CertificateEncodingException -> 0x0407 }
            r13.<init>(r10)     // Catch:{ CertificateEncodingException -> 0x0407 }
            r2.<init>(r7, r9, r13)     // Catch:{ CertificateEncodingException -> 0x0407 }
            r3.add(r2)     // Catch:{ CertificateEncodingException -> 0x0407 }
            r2 = r16
            goto L_0x0377
        L_0x0407:
            r0 = move-exception
            java.io.IOException r1 = new java.io.IOException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r6)
            java.lang.String r0 = r0.toString()
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1.<init>(r0)
            throw r1
        L_0x0421:
            org.bouncycastle.asn1.DERSequence r2 = new org.bouncycastle.asn1.DERSequence
            r2.<init>(r3)
            byte[] r6 = r2.getEncoded(r1)
            r2 = 1
            r5 = 0
            r1 = r18
            r3 = r12
            r4 = r20
            byte[] r1 = r1.cryptData(r2, r3, r4, r5, r6)
            org.bouncycastle.asn1.pkcs.EncryptedData r2 = new org.bouncycastle.asn1.pkcs.EncryptedData
            org.bouncycastle.asn1.ASN1ObjectIdentifier r3 = data
            org.bouncycastle.asn1.BEROctetString r4 = new org.bouncycastle.asn1.BEROctetString
            r4.<init>(r1)
            r2.<init>(r3, r12, r4)
            r1 = 2
            org.bouncycastle.asn1.pkcs.ContentInfo[] r1 = new org.bouncycastle.asn1.pkcs.ContentInfo[r1]
            org.bouncycastle.asn1.pkcs.ContentInfo r3 = new org.bouncycastle.asn1.pkcs.ContentInfo
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = data
            r3.<init>(r4, r11)
            r4 = 0
            r1[r4] = r3
            org.bouncycastle.asn1.pkcs.ContentInfo r3 = new org.bouncycastle.asn1.pkcs.ContentInfo
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = encryptedData
            org.bouncycastle.asn1.ASN1Primitive r2 = r2.toASN1Primitive()
            r3.<init>(r4, r2)
            r2 = 1
            r1[r2] = r3
            org.bouncycastle.asn1.pkcs.AuthenticatedSafe r2 = new org.bouncycastle.asn1.pkcs.AuthenticatedSafe
            r2.<init>(r1)
            java.io.ByteArrayOutputStream r1 = new java.io.ByteArrayOutputStream
            r1.<init>()
            if (r21 == 0) goto L_0x046e
            org.bouncycastle.asn1.DEROutputStream r3 = new org.bouncycastle.asn1.DEROutputStream
            r3.<init>(r1)
            goto L_0x0473
        L_0x046e:
            org.bouncycastle.asn1.BEROutputStream r3 = new org.bouncycastle.asn1.BEROutputStream
            r3.<init>(r1)
        L_0x0473:
            r3.writeObject(r2)
            byte[] r1 = r1.toByteArray()
            org.bouncycastle.asn1.pkcs.ContentInfo r9 = new org.bouncycastle.asn1.pkcs.ContentInfo
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = data
            org.bouncycastle.asn1.BEROctetString r3 = new org.bouncycastle.asn1.BEROctetString
            r3.<init>(r1)
            r9.<init>(r2, r3)
            int r1 = r8.saltLength
            byte[] r10 = new byte[r1]
            java.security.SecureRandom r1 = r8.random
            r1.nextBytes(r10)
            org.bouncycastle.asn1.ASN1Encodable r1 = r9.getContent()
            org.bouncycastle.asn1.ASN1OctetString r1 = (org.bouncycastle.asn1.ASN1OctetString) r1
            byte[] r7 = r1.getOctets()
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r1 = r8.macAlgorithm     // Catch:{ Exception -> 0x04cf }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = r1.getAlgorithm()     // Catch:{ Exception -> 0x04cf }
            int r4 = r8.itCount     // Catch:{ Exception -> 0x04cf }
            r6 = 0
            r1 = r18
            r3 = r10
            r5 = r20
            byte[] r1 = r1.calculatePbeMac(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x04cf }
            org.bouncycastle.asn1.x509.DigestInfo r2 = new org.bouncycastle.asn1.x509.DigestInfo     // Catch:{ Exception -> 0x04cf }
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r3 = r8.macAlgorithm     // Catch:{ Exception -> 0x04cf }
            r2.<init>(r3, r1)     // Catch:{ Exception -> 0x04cf }
            org.bouncycastle.asn1.pkcs.MacData r1 = new org.bouncycastle.asn1.pkcs.MacData     // Catch:{ Exception -> 0x04cf }
            int r3 = r8.itCount     // Catch:{ Exception -> 0x04cf }
            r1.<init>(r2, r10, r3)     // Catch:{ Exception -> 0x04cf }
            org.bouncycastle.asn1.pkcs.Pfx r2 = new org.bouncycastle.asn1.pkcs.Pfx
            r2.<init>(r9, r1)
            if (r21 == 0) goto L_0x04c6
            org.bouncycastle.asn1.DEROutputStream r1 = new org.bouncycastle.asn1.DEROutputStream
            r1.<init>(r0)
            goto L_0x04cb
        L_0x04c6:
            org.bouncycastle.asn1.BEROutputStream r1 = new org.bouncycastle.asn1.BEROutputStream
            r1.<init>(r0)
        L_0x04cb:
            r1.writeObject(r2)
            return
        L_0x04cf:
            r0 = move-exception
            java.io.IOException r1 = new java.io.IOException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "error constructing MAC: "
            r2.append(r3)
            java.lang.String r0 = r0.toString()
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1.<init>(r0)
            throw r1
        L_0x04eb:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            java.lang.String r1 = "No password supplied for PKCS#12 KeyStore."
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi.doStore(java.io.OutputStream, char[], boolean):void");
    }

    private static byte[] getDigest(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        Digest createSHA1 = DigestFactory.createSHA1();
        byte[] bArr = new byte[createSHA1.getDigestSize()];
        byte[] bytes = subjectPublicKeyInfo.getPublicKeyData().getBytes();
        createSHA1.update(bytes, 0, bytes.length);
        createSHA1.doFinal(bArr, 0);
        return bArr;
    }

    private Set getUsedCertificateSet() {
        HashSet hashSet = new HashSet();
        Enumeration keys2 = this.keys.keys();
        while (keys2.hasMoreElements()) {
            Certificate[] engineGetCertificateChain = engineGetCertificateChain((String) keys2.nextElement());
            for (int i = 0; i != engineGetCertificateChain.length; i++) {
                hashSet.add(engineGetCertificateChain[i]);
            }
        }
        Enumeration keys3 = this.certs.keys();
        while (keys3.hasMoreElements()) {
            hashSet.add(engineGetCertificate((String) keys3.nextElement()));
        }
        return hashSet;
    }

    private int validateIterationCount(BigInteger bigInteger) {
        int intValue = bigInteger.intValue();
        if (intValue >= 0) {
            BigInteger asBigInteger = Properties.asBigInteger(PKCS12_MAX_IT_COUNT_PROPERTY);
            if (asBigInteger == null || asBigInteger.intValue() >= intValue) {
                return intValue;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("iteration count ");
            sb.append(intValue);
            sb.append(" greater than ");
            sb.append(asBigInteger.intValue());
            throw new IllegalStateException(sb.toString());
        }
        throw new IllegalStateException("negative iteration count found");
    }

    /* access modifiers changed from: protected */
    public byte[] cryptData(boolean z, AlgorithmIdentifier algorithmIdentifier, char[] cArr, boolean z2, byte[] bArr) throws IOException {
        ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
        int i = z ? 1 : 2;
        String str = "exception decrypting data - ";
        if (algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
            PKCS12PBEParams instance = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
            try {
                PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(instance.getIV(), instance.getIterations().intValue());
                PKCS12Key pKCS12Key = new PKCS12Key(cArr, z2);
                Cipher createCipher = this.helper.createCipher(algorithm.getId());
                createCipher.init(i, pKCS12Key, pBEParameterSpec);
                return createCipher.doFinal(bArr);
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(e.toString());
                throw new IOException(sb.toString());
            }
        } else if (algorithm.equals(PKCSObjectIdentifiers.id_PBES2)) {
            try {
                return createCipher(i, cArr, algorithmIdentifier).doFinal(bArr);
            } catch (Exception e2) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append(e2.toString());
                throw new IOException(sb2.toString());
            }
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("unknown PBE algorithm: ");
            sb3.append(algorithm);
            throw new IOException(sb3.toString());
        }
    }

    public Enumeration engineAliases() {
        Hashtable hashtable = new Hashtable();
        Enumeration keys2 = this.certs.keys();
        while (keys2.hasMoreElements()) {
            hashtable.put(keys2.nextElement(), "cert");
        }
        Enumeration keys3 = this.keys.keys();
        while (keys3.hasMoreElements()) {
            String str = (String) keys3.nextElement();
            if (hashtable.get(str) == null) {
                hashtable.put(str, "key");
            }
        }
        return hashtable.keys();
    }

    public boolean engineContainsAlias(String str) {
        return (this.certs.get(str) == null && this.keys.get(str) == null) ? false : true;
    }

    public void engineDeleteEntry(String str) throws KeyStoreException {
        Key key = (Key) this.keys.remove(str);
        Certificate certificate = (Certificate) this.certs.remove(str);
        if (certificate != null) {
            this.chainCerts.remove(new CertId(certificate.getPublicKey()));
        }
        if (key != null) {
            String str2 = (String) this.localIds.remove(str);
            if (str2 != null) {
                certificate = (Certificate) this.keyCerts.remove(str2);
            }
            if (certificate != null) {
                this.chainCerts.remove(new CertId(certificate.getPublicKey()));
            }
        }
    }

    public Certificate engineGetCertificate(String str) {
        if (str != null) {
            Certificate certificate = (Certificate) this.certs.get(str);
            if (certificate != null) {
                return certificate;
            }
            String str2 = (String) this.localIds.get(str);
            return (Certificate) (str2 != null ? this.keyCerts.get(str2) : this.keyCerts.get(str));
        }
        throw new IllegalArgumentException("null alias passed to getCertificate.");
    }

    public String engineGetCertificateAlias(Certificate certificate) {
        Enumeration elements = this.certs.elements();
        Enumeration keys2 = this.certs.keys();
        while (elements.hasMoreElements()) {
            String str = (String) keys2.nextElement();
            if (((Certificate) elements.nextElement()).equals(certificate)) {
                return str;
            }
        }
        Enumeration elements2 = this.keyCerts.elements();
        Enumeration keys3 = this.keyCerts.keys();
        while (elements2.hasMoreElements()) {
            String str2 = (String) keys3.nextElement();
            if (((Certificate) elements2.nextElement()).equals(certificate)) {
                return str2;
            }
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ac  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.security.cert.Certificate[] engineGetCertificateChain(java.lang.String r9) {
        /*
            r8 = this;
            if (r9 == 0) goto L_0x00cb
            boolean r0 = r8.engineIsKeyEntry(r9)
            r1 = 0
            if (r0 != 0) goto L_0x000a
            return r1
        L_0x000a:
            java.security.cert.Certificate r9 = r8.engineGetCertificate(r9)
            if (r9 == 0) goto L_0x00ca
            java.util.Vector r0 = new java.util.Vector
            r0.<init>()
        L_0x0015:
            if (r9 == 0) goto L_0x00b4
            r2 = r9
            java.security.cert.X509Certificate r2 = (java.security.cert.X509Certificate) r2
            org.bouncycastle.asn1.ASN1ObjectIdentifier r3 = org.bouncycastle.asn1.x509.Extension.authorityKeyIdentifier
            java.lang.String r3 = r3.getId()
            byte[] r3 = r2.getExtensionValue(r3)
            if (r3 == 0) goto L_0x0065
            org.bouncycastle.asn1.ASN1InputStream r4 = new org.bouncycastle.asn1.ASN1InputStream     // Catch:{ IOException -> 0x005a }
            r4.<init>(r3)     // Catch:{ IOException -> 0x005a }
            org.bouncycastle.asn1.ASN1Primitive r3 = r4.readObject()     // Catch:{ IOException -> 0x005a }
            org.bouncycastle.asn1.ASN1OctetString r3 = (org.bouncycastle.asn1.ASN1OctetString) r3     // Catch:{ IOException -> 0x005a }
            byte[] r3 = r3.getOctets()     // Catch:{ IOException -> 0x005a }
            org.bouncycastle.asn1.ASN1InputStream r4 = new org.bouncycastle.asn1.ASN1InputStream     // Catch:{ IOException -> 0x005a }
            r4.<init>(r3)     // Catch:{ IOException -> 0x005a }
            org.bouncycastle.asn1.ASN1Primitive r3 = r4.readObject()     // Catch:{ IOException -> 0x005a }
            org.bouncycastle.asn1.x509.AuthorityKeyIdentifier r3 = org.bouncycastle.asn1.x509.AuthorityKeyIdentifier.getInstance(r3)     // Catch:{ IOException -> 0x005a }
            byte[] r4 = r3.getKeyIdentifier()     // Catch:{ IOException -> 0x005a }
            if (r4 == 0) goto L_0x0065
            java.util.Hashtable r4 = r8.chainCerts     // Catch:{ IOException -> 0x005a }
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$CertId r5 = new org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$CertId     // Catch:{ IOException -> 0x005a }
            byte[] r3 = r3.getKeyIdentifier()     // Catch:{ IOException -> 0x005a }
            r5.<init>(r3)     // Catch:{ IOException -> 0x005a }
            java.lang.Object r3 = r4.get(r5)     // Catch:{ IOException -> 0x005a }
            java.security.cert.Certificate r3 = (java.security.cert.Certificate) r3     // Catch:{ IOException -> 0x005a }
            goto L_0x0066
        L_0x005a:
            r9 = move-exception
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r9 = r9.toString()
            r0.<init>(r9)
            throw r0
        L_0x0065:
            r3 = r1
        L_0x0066:
            if (r3 != 0) goto L_0x00a3
            java.security.Principal r4 = r2.getIssuerDN()
            java.security.Principal r5 = r2.getSubjectDN()
            boolean r5 = r4.equals(r5)
            if (r5 != 0) goto L_0x00a3
            java.util.Hashtable r5 = r8.chainCerts
            java.util.Enumeration r5 = r5.keys()
        L_0x007c:
            boolean r6 = r5.hasMoreElements()
            if (r6 == 0) goto L_0x00a3
            java.util.Hashtable r6 = r8.chainCerts
            java.lang.Object r7 = r5.nextElement()
            java.lang.Object r6 = r6.get(r7)
            java.security.cert.X509Certificate r6 = (java.security.cert.X509Certificate) r6
            java.security.Principal r7 = r6.getSubjectDN()
            boolean r7 = r7.equals(r4)
            if (r7 == 0) goto L_0x007c
            java.security.PublicKey r7 = r6.getPublicKey()     // Catch:{ Exception -> 0x00a1 }
            r2.verify(r7)     // Catch:{ Exception -> 0x00a1 }
            r3 = r6
            goto L_0x00a3
        L_0x00a1:
            r6 = move-exception
            goto L_0x007c
        L_0x00a3:
            boolean r2 = r0.contains(r9)
            if (r2 == 0) goto L_0x00ac
        L_0x00a9:
            r9 = r1
            goto L_0x0015
        L_0x00ac:
            r0.addElement(r9)
            if (r3 == r9) goto L_0x00a9
            r9 = r3
            goto L_0x0015
        L_0x00b4:
            int r9 = r0.size()
            java.security.cert.Certificate[] r9 = new java.security.cert.Certificate[r9]
            r1 = 0
        L_0x00bb:
            int r2 = r9.length
            if (r1 == r2) goto L_0x00c9
            java.lang.Object r2 = r0.elementAt(r1)
            java.security.cert.Certificate r2 = (java.security.cert.Certificate) r2
            r9[r1] = r2
            int r1 = r1 + 1
            goto L_0x00bb
        L_0x00c9:
            return r9
        L_0x00ca:
            return r1
        L_0x00cb:
            java.lang.IllegalArgumentException r9 = new java.lang.IllegalArgumentException
            java.lang.String r0 = "null alias passed to getCertificateChain."
            r9.<init>(r0)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi.engineGetCertificateChain(java.lang.String):java.security.cert.Certificate[]");
    }

    public Date engineGetCreationDate(String str) {
        if (str == null) {
            throw new NullPointerException("alias == null");
        } else if (this.keys.get(str) == null && this.certs.get(str) == null) {
            return null;
        } else {
            return new Date();
        }
    }

    public Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        if (str != null) {
            return (Key) this.keys.get(str);
        }
        throw new IllegalArgumentException("null alias passed to getKey.");
    }

    public boolean engineIsCertificateEntry(String str) {
        return this.certs.get(str) != null && this.keys.get(str) == null;
    }

    public boolean engineIsKeyEntry(String str) {
        return this.keys.get(str) != null;
    }

    /* JADX WARNING: type inference failed for: r7v0 */
    /* JADX WARNING: type inference failed for: r7v7 */
    /* JADX WARNING: type inference failed for: r7v8 */
    /* JADX WARNING: type inference failed for: r7v25 */
    /* JADX WARNING: type inference failed for: r7v26 */
    /* JADX WARNING: type inference failed for: r17v9, types: [org.bouncycastle.asn1.ASN1OctetString] */
    /* JADX WARNING: type inference failed for: r5v26, types: [java.lang.Object] */
    /* JADX WARNING: type inference failed for: r5v28 */
    /* JADX WARNING: type inference failed for: r17v10 */
    /* JADX WARNING: type inference failed for: r5v29 */
    /* JADX WARNING: type inference failed for: r17v11 */
    /* JADX WARNING: type inference failed for: r17v12 */
    /* JADX WARNING: type inference failed for: r5v30 */
    /* JADX WARNING: type inference failed for: r17v13 */
    /* JADX WARNING: type inference failed for: r5v31 */
    /* JADX WARNING: type inference failed for: r17v15, types: [org.bouncycastle.asn1.ASN1OctetString] */
    /* JADX WARNING: type inference failed for: r5v32, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r7v45 */
    /* JADX WARNING: type inference failed for: r7v46 */
    /* JADX WARNING: type inference failed for: r7v47 */
    /* JADX WARNING: type inference failed for: r7v48 */
    /* JADX WARNING: type inference failed for: r5v35 */
    /* JADX WARNING: type inference failed for: r17v16 */
    /* JADX WARNING: type inference failed for: r5v36 */
    /* JADX WARNING: type inference failed for: r17v17 */
    /* JADX WARNING: type inference failed for: r17v18 */
    /* JADX WARNING: type inference failed for: r17v19 */
    /* JADX WARNING: type inference failed for: r5v37 */
    /* JADX WARNING: type inference failed for: r5v38 */
    /* JADX WARNING: type inference failed for: r5v39 */
    /* JADX WARNING: type inference failed for: r17v20 */
    /* JADX WARNING: type inference failed for: r5v40 */
    /* JADX WARNING: type inference failed for: r17v21 */
    /* JADX WARNING: type inference failed for: r5v41 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r7v7
  assigns: []
  uses: []
  mth insns count: 536
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0487  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x04a6  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00f0  */
    /* JADX WARNING: Unknown variable types count: 16 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void engineLoad(java.io.InputStream r23, char[] r24) throws java.io.IOException {
        /*
            r22 = this;
            r8 = r22
            r0 = r23
            r9 = r24
            if (r0 != 0) goto L_0x0009
            return
        L_0x0009:
            if (r9 == 0) goto L_0x05e9
            java.io.BufferedInputStream r1 = new java.io.BufferedInputStream
            r1.<init>(r0)
            r0 = 10
            r1.mark(r0)
            int r0 = r1.read()
            r2 = 48
            if (r0 != r2) goto L_0x05e1
            r1.reset()
            org.bouncycastle.asn1.ASN1InputStream r0 = new org.bouncycastle.asn1.ASN1InputStream
            r0.<init>(r1)
            org.bouncycastle.asn1.ASN1Primitive r0 = r0.readObject()     // Catch:{ Exception -> 0x05d6 }
            org.bouncycastle.asn1.pkcs.Pfx r0 = org.bouncycastle.asn1.pkcs.Pfx.getInstance(r0)     // Catch:{ Exception -> 0x05d6 }
            org.bouncycastle.asn1.pkcs.ContentInfo r10 = r0.getAuthSafe()
            java.util.Vector r11 = new java.util.Vector
            r11.<init>()
            org.bouncycastle.asn1.pkcs.MacData r1 = r0.getMacData()
            r12 = 1
            r13 = 0
            if (r1 == 0) goto L_0x00d0
            org.bouncycastle.asn1.pkcs.MacData r0 = r0.getMacData()
            org.bouncycastle.asn1.x509.DigestInfo r14 = r0.getMac()
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r1 = r14.getAlgorithmId()
            r8.macAlgorithm = r1
            byte[] r15 = r0.getSalt()
            java.math.BigInteger r0 = r0.getIterationCount()
            int r0 = r8.validateIterationCount(r0)
            r8.itCount = r0
            int r0 = r15.length
            r8.saltLength = r0
            org.bouncycastle.asn1.ASN1Encodable r0 = r10.getContent()
            org.bouncycastle.asn1.ASN1OctetString r0 = (org.bouncycastle.asn1.ASN1OctetString) r0
            byte[] r0 = r0.getOctets()
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r1 = r8.macAlgorithm     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = r1.getAlgorithm()     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            int r4 = r8.itCount     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            r6 = 0
            r1 = r22
            r3 = r15
            r5 = r24
            r7 = r0
            byte[] r1 = r1.calculatePbeMac(r2, r3, r4, r5, r6, r7)     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            byte[] r14 = r14.getDigest()     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            boolean r1 = org.bouncycastle.util.Arrays.constantTimeAreEqual(r1, r14)     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            if (r1 != 0) goto L_0x00d0
            int r1 = r9.length     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            java.lang.String r7 = "PKCS12 key store mac invalid - wrong password or corrupted file."
            if (r1 > 0) goto L_0x00ab
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r1 = r8.macAlgorithm     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = r1.getAlgorithm()     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            int r4 = r8.itCount     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            r6 = 1
            r1 = r22
            r3 = r15
            r5 = r24
            r15 = r7
            r7 = r0
            byte[] r0 = r1.calculatePbeMac(r2, r3, r4, r5, r6, r7)     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            boolean r0 = org.bouncycastle.util.Arrays.constantTimeAreEqual(r0, r14)     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            if (r0 == 0) goto L_0x00a5
            r0 = r12
            goto L_0x00d1
        L_0x00a5:
            java.io.IOException r0 = new java.io.IOException     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            r0.<init>(r15)     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            throw r0     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
        L_0x00ab:
            r15 = r7
            java.io.IOException r0 = new java.io.IOException     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            r0.<init>(r15)     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
            throw r0     // Catch:{ IOException -> 0x00ce, Exception -> 0x00b2 }
        L_0x00b2:
            r0 = move-exception
            java.io.IOException r1 = new java.io.IOException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "error constructing MAC: "
            r2.append(r3)
            java.lang.String r0 = r0.toString()
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1.<init>(r0)
            throw r1
        L_0x00ce:
            r0 = move-exception
            throw r0
        L_0x00d0:
            r0 = r13
        L_0x00d1:
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r1 = new org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable
            r7 = 0
            r1.<init>()
            r8.keys = r1
            java.util.Hashtable r1 = new java.util.Hashtable
            r1.<init>()
            r8.localIds = r1
            org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = r10.getContentType()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = data
            boolean r1 = r1.equals(r2)
            java.lang.String r14 = "unmarked"
            java.lang.String r15 = "attempt to add existing attribute with different value"
            if (r1 == 0) goto L_0x0487
            org.bouncycastle.asn1.ASN1InputStream r1 = new org.bouncycastle.asn1.ASN1InputStream
            org.bouncycastle.asn1.ASN1Encodable r2 = r10.getContent()
            org.bouncycastle.asn1.ASN1OctetString r2 = (org.bouncycastle.asn1.ASN1OctetString) r2
            byte[] r2 = r2.getOctets()
            r1.<init>(r2)
            org.bouncycastle.asn1.ASN1Primitive r1 = r1.readObject()
            org.bouncycastle.asn1.pkcs.AuthenticatedSafe r1 = org.bouncycastle.asn1.pkcs.AuthenticatedSafe.getInstance(r1)
            org.bouncycastle.asn1.pkcs.ContentInfo[] r10 = r1.getContentInfo()
            r6 = r13
            r16 = r6
        L_0x010e:
            int r1 = r10.length
            if (r6 == r1) goto L_0x0489
            r1 = r10[r6]
            org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = r1.getContentType()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = data
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x024d
            org.bouncycastle.asn1.ASN1InputStream r1 = new org.bouncycastle.asn1.ASN1InputStream
            r2 = r10[r6]
            org.bouncycastle.asn1.ASN1Encodable r2 = r2.getContent()
            org.bouncycastle.asn1.ASN1OctetString r2 = (org.bouncycastle.asn1.ASN1OctetString) r2
            byte[] r2 = r2.getOctets()
            r1.<init>(r2)
            org.bouncycastle.asn1.ASN1Primitive r1 = r1.readObject()
            org.bouncycastle.asn1.ASN1Sequence r1 = (org.bouncycastle.asn1.ASN1Sequence) r1
            r2 = r13
        L_0x0137:
            int r3 = r1.size()
            if (r2 == r3) goto L_0x0248
            org.bouncycastle.asn1.ASN1Encodable r3 = r1.getObjectAt(r2)
            org.bouncycastle.asn1.pkcs.SafeBag r3 = org.bouncycastle.asn1.pkcs.SafeBag.getInstance(r3)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = r3.getBagId()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r5 = pkcs8ShroudedKeyBag
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x020e
            org.bouncycastle.asn1.ASN1Encodable r4 = r3.getBagValue()
            org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo r4 = org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo.getInstance(r4)
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r5 = r4.getEncryptionAlgorithm()
            byte[] r4 = r4.getEncryptedData()
            java.security.PrivateKey r4 = r8.unwrapKey(r5, r4, r9, r0)
            org.bouncycastle.asn1.ASN1Set r5 = r3.getBagAttributes()
            if (r5 == 0) goto L_0x01e6
            org.bouncycastle.asn1.ASN1Set r3 = r3.getBagAttributes()
            java.util.Enumeration r3 = r3.getObjects()
            r5 = r7
            r17 = r5
        L_0x0176:
            boolean r18 = r3.hasMoreElements()
            if (r18 == 0) goto L_0x01e9
            java.lang.Object r18 = r3.nextElement()
            r7 = r18
            org.bouncycastle.asn1.ASN1Sequence r7 = (org.bouncycastle.asn1.ASN1Sequence) r7
            org.bouncycastle.asn1.ASN1Encodable r18 = r7.getObjectAt(r13)
            r13 = r18
            org.bouncycastle.asn1.ASN1ObjectIdentifier r13 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r13
            org.bouncycastle.asn1.ASN1Encodable r7 = r7.getObjectAt(r12)
            org.bouncycastle.asn1.ASN1Set r7 = (org.bouncycastle.asn1.ASN1Set) r7
            int r18 = r7.size()
            if (r18 <= 0) goto L_0x01c1
            r12 = 0
            org.bouncycastle.asn1.ASN1Encodable r7 = r7.getObjectAt(r12)
            org.bouncycastle.asn1.ASN1Primitive r7 = (org.bouncycastle.asn1.ASN1Primitive) r7
            boolean r12 = r4 instanceof org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier
            if (r12 == 0) goto L_0x01c2
            r12 = r4
            org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier r12 = (org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier) r12
            org.bouncycastle.asn1.ASN1Encodable r20 = r12.getBagAttribute(r13)
            if (r20 == 0) goto L_0x01bd
            org.bouncycastle.asn1.ASN1Primitive r12 = r20.toASN1Primitive()
            boolean r12 = r12.equals(r7)
            if (r12 == 0) goto L_0x01b7
            goto L_0x01c2
        L_0x01b7:
            java.io.IOException r0 = new java.io.IOException
            r0.<init>(r15)
            throw r0
        L_0x01bd:
            r12.setBagAttribute(r13, r7)
            goto L_0x01c2
        L_0x01c1:
            r7 = 0
        L_0x01c2:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r12 = pkcs_9_at_friendlyName
            boolean r12 = r13.equals(r12)
            if (r12 == 0) goto L_0x01d6
            org.bouncycastle.asn1.DERBMPString r7 = (org.bouncycastle.asn1.DERBMPString) r7
            java.lang.String r5 = r7.getString()
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r7 = r8.keys
            r7.put(r5, r4)
            goto L_0x01e2
        L_0x01d6:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r12 = pkcs_9_at_localKeyId
            boolean r12 = r13.equals(r12)
            if (r12 == 0) goto L_0x01e2
            r17 = r7
            org.bouncycastle.asn1.ASN1OctetString r17 = (org.bouncycastle.asn1.ASN1OctetString) r17
        L_0x01e2:
            r7 = 0
            r12 = 1
            r13 = 0
            goto L_0x0176
        L_0x01e6:
            r5 = 0
            r17 = 0
        L_0x01e9:
            if (r17 == 0) goto L_0x0206
            java.lang.String r3 = new java.lang.String
            byte[] r7 = r17.getOctets()
            byte[] r7 = org.bouncycastle.util.encoders.Hex.encode(r7)
            r3.<init>(r7)
            if (r5 != 0) goto L_0x0200
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r5 = r8.keys
            r5.put(r3, r4)
            goto L_0x0241
        L_0x0200:
            java.util.Hashtable r4 = r8.localIds
            r4.put(r5, r3)
            goto L_0x0241
        L_0x0206:
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r3 = r8.keys
            r3.put(r14, r4)
            r16 = 1
            goto L_0x0241
        L_0x020e:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = r3.getBagId()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r5 = certBag
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x021e
            r11.addElement(r3)
            goto L_0x0241
        L_0x021e:
            java.io.PrintStream r4 = java.lang.System.out
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "extra in data "
            r5.append(r7)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r7 = r3.getBagId()
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            r4.println(r5)
            java.io.PrintStream r4 = java.lang.System.out
            java.lang.String r3 = org.bouncycastle.asn1.util.ASN1Dump.dumpAsString(r3)
            r4.println(r3)
        L_0x0241:
            int r2 = r2 + 1
            r7 = 0
            r12 = 1
            r13 = 0
            goto L_0x0137
        L_0x0248:
            r17 = r0
            r13 = r6
            goto L_0x047e
        L_0x024d:
            r1 = r10[r6]
            org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = r1.getContentType()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = encryptedData
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x043d
            r1 = r10[r6]
            org.bouncycastle.asn1.ASN1Encodable r1 = r1.getContent()
            org.bouncycastle.asn1.pkcs.EncryptedData r1 = org.bouncycastle.asn1.pkcs.EncryptedData.getInstance(r1)
            r2 = 0
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r3 = r1.getEncryptionAlgorithm()
            org.bouncycastle.asn1.ASN1OctetString r1 = r1.getContent()
            byte[] r7 = r1.getOctets()
            r1 = r22
            r4 = r24
            r5 = r0
            r13 = r6
            r6 = r7
            byte[] r1 = r1.cryptData(r2, r3, r4, r5, r6)
            org.bouncycastle.asn1.ASN1Primitive r1 = org.bouncycastle.asn1.ASN1Primitive.fromByteArray(r1)
            org.bouncycastle.asn1.ASN1Sequence r1 = (org.bouncycastle.asn1.ASN1Sequence) r1
            r2 = 0
        L_0x0284:
            int r3 = r1.size()
            if (r2 == r3) goto L_0x043a
            org.bouncycastle.asn1.ASN1Encodable r3 = r1.getObjectAt(r2)
            org.bouncycastle.asn1.pkcs.SafeBag r3 = org.bouncycastle.asn1.pkcs.SafeBag.getInstance(r3)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = r3.getBagId()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r5 = certBag
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x02a7
            r11.addElement(r3)
            r17 = r0
            r21 = r1
            goto L_0x0432
        L_0x02a7:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = r3.getBagId()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r5 = pkcs8ShroudedKeyBag
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0364
            org.bouncycastle.asn1.ASN1Encodable r4 = r3.getBagValue()
            org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo r4 = org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo.getInstance(r4)
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r5 = r4.getEncryptionAlgorithm()
            byte[] r4 = r4.getEncryptedData()
            java.security.PrivateKey r4 = r8.unwrapKey(r5, r4, r9, r0)
            r5 = r4
            org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier r5 = (org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier) r5
            org.bouncycastle.asn1.ASN1Set r3 = r3.getBagAttributes()
            java.util.Enumeration r3 = r3.getObjects()
            r6 = 0
            r7 = 0
        L_0x02d4:
            boolean r12 = r3.hasMoreElements()
            if (r12 == 0) goto L_0x0343
            java.lang.Object r12 = r3.nextElement()
            org.bouncycastle.asn1.ASN1Sequence r12 = (org.bouncycastle.asn1.ASN1Sequence) r12
            r17 = r0
            r0 = 0
            org.bouncycastle.asn1.ASN1Encodable r19 = r12.getObjectAt(r0)
            r0 = r19
            org.bouncycastle.asn1.ASN1ObjectIdentifier r0 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r0
            r21 = r1
            r1 = 1
            org.bouncycastle.asn1.ASN1Encodable r12 = r12.getObjectAt(r1)
            org.bouncycastle.asn1.ASN1Set r12 = (org.bouncycastle.asn1.ASN1Set) r12
            int r1 = r12.size()
            if (r1 <= 0) goto L_0x031d
            r1 = 0
            org.bouncycastle.asn1.ASN1Encodable r12 = r12.getObjectAt(r1)
            r1 = r12
            org.bouncycastle.asn1.ASN1Primitive r1 = (org.bouncycastle.asn1.ASN1Primitive) r1
            org.bouncycastle.asn1.ASN1Encodable r12 = r5.getBagAttribute(r0)
            if (r12 == 0) goto L_0x0319
            org.bouncycastle.asn1.ASN1Primitive r12 = r12.toASN1Primitive()
            boolean r12 = r12.equals(r1)
            if (r12 == 0) goto L_0x0313
            goto L_0x031e
        L_0x0313:
            java.io.IOException r0 = new java.io.IOException
            r0.<init>(r15)
            throw r0
        L_0x0319:
            r5.setBagAttribute(r0, r1)
            goto L_0x031e
        L_0x031d:
            r1 = 0
        L_0x031e:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r12 = pkcs_9_at_friendlyName
            boolean r12 = r0.equals(r12)
            if (r12 == 0) goto L_0x0333
            org.bouncycastle.asn1.DERBMPString r1 = (org.bouncycastle.asn1.DERBMPString) r1
            java.lang.String r0 = r1.getString()
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r1 = r8.keys
            r1.put(r0, r4)
            r7 = r0
            goto L_0x033e
        L_0x0333:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r12 = pkcs_9_at_localKeyId
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x033e
            org.bouncycastle.asn1.ASN1OctetString r1 = (org.bouncycastle.asn1.ASN1OctetString) r1
            r6 = r1
        L_0x033e:
            r0 = r17
            r1 = r21
            goto L_0x02d4
        L_0x0343:
            r17 = r0
            r21 = r1
            java.lang.String r0 = new java.lang.String
            byte[] r1 = r6.getOctets()
            byte[] r1 = org.bouncycastle.util.encoders.Hex.encode(r1)
            r0.<init>(r1)
            if (r7 != 0) goto L_0x035d
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r1 = r8.keys
            r1.put(r0, r4)
            goto L_0x0432
        L_0x035d:
            java.util.Hashtable r1 = r8.localIds
            r1.put(r7, r0)
            goto L_0x0432
        L_0x0364:
            r17 = r0
            r21 = r1
            org.bouncycastle.asn1.ASN1ObjectIdentifier r0 = r3.getBagId()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = keyBag
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x040f
            org.bouncycastle.asn1.ASN1Encodable r0 = r3.getBagValue()
            org.bouncycastle.asn1.pkcs.PrivateKeyInfo r0 = org.bouncycastle.asn1.pkcs.PrivateKeyInfo.getInstance(r0)
            java.security.PrivateKey r0 = org.bouncycastle.jce.provider.BouncyCastleProvider.getPrivateKey(r0)
            r1 = r0
            org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier r1 = (org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier) r1
            org.bouncycastle.asn1.ASN1Set r3 = r3.getBagAttributes()
            java.util.Enumeration r3 = r3.getObjects()
            r4 = 0
            r5 = 0
        L_0x038d:
            boolean r6 = r3.hasMoreElements()
            if (r6 == 0) goto L_0x03f4
            java.lang.Object r6 = r3.nextElement()
            org.bouncycastle.asn1.ASN1Sequence r6 = org.bouncycastle.asn1.ASN1Sequence.getInstance(r6)
            r7 = 0
            org.bouncycastle.asn1.ASN1Encodable r12 = r6.getObjectAt(r7)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r12 = org.bouncycastle.asn1.ASN1ObjectIdentifier.getInstance(r12)
            r7 = 1
            org.bouncycastle.asn1.ASN1Encodable r6 = r6.getObjectAt(r7)
            org.bouncycastle.asn1.ASN1Set r6 = org.bouncycastle.asn1.ASN1Set.getInstance(r6)
            int r7 = r6.size()
            if (r7 <= 0) goto L_0x038d
            r7 = 0
            org.bouncycastle.asn1.ASN1Encodable r6 = r6.getObjectAt(r7)
            org.bouncycastle.asn1.ASN1Primitive r6 = (org.bouncycastle.asn1.ASN1Primitive) r6
            org.bouncycastle.asn1.ASN1Encodable r7 = r1.getBagAttribute(r12)
            if (r7 == 0) goto L_0x03d1
            org.bouncycastle.asn1.ASN1Primitive r7 = r7.toASN1Primitive()
            boolean r7 = r7.equals(r6)
            if (r7 == 0) goto L_0x03cb
            goto L_0x03d4
        L_0x03cb:
            java.io.IOException r0 = new java.io.IOException
            r0.<init>(r15)
            throw r0
        L_0x03d1:
            r1.setBagAttribute(r12, r6)
        L_0x03d4:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r7 = pkcs_9_at_friendlyName
            boolean r7 = r12.equals(r7)
            if (r7 == 0) goto L_0x03e8
            org.bouncycastle.asn1.DERBMPString r6 = (org.bouncycastle.asn1.DERBMPString) r6
            java.lang.String r5 = r6.getString()
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r6 = r8.keys
            r6.put(r5, r0)
            goto L_0x038d
        L_0x03e8:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r7 = pkcs_9_at_localKeyId
            boolean r7 = r12.equals(r7)
            if (r7 == 0) goto L_0x038d
            org.bouncycastle.asn1.ASN1OctetString r6 = (org.bouncycastle.asn1.ASN1OctetString) r6
            r4 = r6
            goto L_0x038d
        L_0x03f4:
            java.lang.String r1 = new java.lang.String
            byte[] r3 = r4.getOctets()
            byte[] r3 = org.bouncycastle.util.encoders.Hex.encode(r3)
            r1.<init>(r3)
            if (r5 != 0) goto L_0x0409
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r3 = r8.keys
            r3.put(r1, r0)
            goto L_0x0432
        L_0x0409:
            java.util.Hashtable r0 = r8.localIds
            r0.put(r5, r1)
            goto L_0x0432
        L_0x040f:
            java.io.PrintStream r0 = java.lang.System.out
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "extra in encryptedData "
            r1.append(r4)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = r3.getBagId()
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            r0.println(r1)
            java.io.PrintStream r0 = java.lang.System.out
            java.lang.String r1 = org.bouncycastle.asn1.util.ASN1Dump.dumpAsString(r3)
            r0.println(r1)
        L_0x0432:
            int r2 = r2 + 1
            r0 = r17
            r1 = r21
            goto L_0x0284
        L_0x043a:
            r17 = r0
            goto L_0x047e
        L_0x043d:
            r17 = r0
            r13 = r6
            java.io.PrintStream r0 = java.lang.System.out
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "extra "
            r1.append(r2)
            r3 = r10[r13]
            org.bouncycastle.asn1.ASN1ObjectIdentifier r3 = r3.getContentType()
            java.lang.String r3 = r3.getId()
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            r0.println(r1)
            java.io.PrintStream r0 = java.lang.System.out
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r2)
            r2 = r10[r13]
            org.bouncycastle.asn1.ASN1Encodable r2 = r2.getContent()
            java.lang.String r2 = org.bouncycastle.asn1.util.ASN1Dump.dumpAsString(r2)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.println(r1)
        L_0x047e:
            int r6 = r13 + 1
            r0 = r17
            r7 = 0
            r12 = 1
            r13 = 0
            goto L_0x010e
        L_0x0487:
            r16 = 0
        L_0x0489:
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r0 = new org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable
            r1 = 0
            r0.<init>()
            r8.certs = r0
            java.util.Hashtable r0 = new java.util.Hashtable
            r0.<init>()
            r8.chainCerts = r0
            java.util.Hashtable r0 = new java.util.Hashtable
            r0.<init>()
            r8.keyCerts = r0
            r0 = 0
        L_0x04a0:
            int r2 = r11.size()
            if (r0 == r2) goto L_0x05d5
            java.lang.Object r2 = r11.elementAt(r0)
            org.bouncycastle.asn1.pkcs.SafeBag r2 = (org.bouncycastle.asn1.pkcs.SafeBag) r2
            org.bouncycastle.asn1.ASN1Encodable r3 = r2.getBagValue()
            org.bouncycastle.asn1.pkcs.CertBag r3 = org.bouncycastle.asn1.pkcs.CertBag.getInstance(r3)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = r3.getCertId()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r5 = x509Certificate
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x05ba
            java.io.ByteArrayInputStream r4 = new java.io.ByteArrayInputStream     // Catch:{ Exception -> 0x05af }
            org.bouncycastle.asn1.ASN1Encodable r3 = r3.getCertValue()     // Catch:{ Exception -> 0x05af }
            org.bouncycastle.asn1.ASN1OctetString r3 = (org.bouncycastle.asn1.ASN1OctetString) r3     // Catch:{ Exception -> 0x05af }
            byte[] r3 = r3.getOctets()     // Catch:{ Exception -> 0x05af }
            r4.<init>(r3)     // Catch:{ Exception -> 0x05af }
            java.security.cert.CertificateFactory r3 = r8.certFact     // Catch:{ Exception -> 0x05af }
            java.security.cert.Certificate r3 = r3.generateCertificate(r4)     // Catch:{ Exception -> 0x05af }
            org.bouncycastle.asn1.ASN1Set r4 = r2.getBagAttributes()
            if (r4 == 0) goto L_0x0550
            org.bouncycastle.asn1.ASN1Set r2 = r2.getBagAttributes()
            java.util.Enumeration r2 = r2.getObjects()
            r4 = r1
            r7 = r4
        L_0x04e5:
            boolean r5 = r2.hasMoreElements()
            if (r5 == 0) goto L_0x054d
            java.lang.Object r5 = r2.nextElement()
            org.bouncycastle.asn1.ASN1Sequence r5 = org.bouncycastle.asn1.ASN1Sequence.getInstance(r5)
            r6 = 0
            org.bouncycastle.asn1.ASN1Encodable r9 = r5.getObjectAt(r6)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r9 = org.bouncycastle.asn1.ASN1ObjectIdentifier.getInstance(r9)
            r10 = 1
            org.bouncycastle.asn1.ASN1Encodable r5 = r5.getObjectAt(r10)
            org.bouncycastle.asn1.ASN1Set r5 = org.bouncycastle.asn1.ASN1Set.getInstance(r5)
            int r12 = r5.size()
            if (r12 <= 0) goto L_0x04e5
            org.bouncycastle.asn1.ASN1Encodable r5 = r5.getObjectAt(r6)
            org.bouncycastle.asn1.ASN1Primitive r5 = (org.bouncycastle.asn1.ASN1Primitive) r5
            boolean r12 = r3 instanceof org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier
            if (r12 == 0) goto L_0x0532
            r12 = r3
            org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier r12 = (org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier) r12
            org.bouncycastle.asn1.ASN1Encodable r13 = r12.getBagAttribute(r9)
            if (r13 == 0) goto L_0x052f
            org.bouncycastle.asn1.ASN1Primitive r12 = r13.toASN1Primitive()
            boolean r12 = r12.equals(r5)
            if (r12 == 0) goto L_0x0529
            goto L_0x0532
        L_0x0529:
            java.io.IOException r0 = new java.io.IOException
            r0.<init>(r15)
            throw r0
        L_0x052f:
            r12.setBagAttribute(r9, r5)
        L_0x0532:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r12 = pkcs_9_at_friendlyName
            boolean r12 = r9.equals(r12)
            if (r12 == 0) goto L_0x0541
            org.bouncycastle.asn1.DERBMPString r5 = (org.bouncycastle.asn1.DERBMPString) r5
            java.lang.String r7 = r5.getString()
            goto L_0x04e5
        L_0x0541:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r12 = pkcs_9_at_localKeyId
            boolean r9 = r9.equals(r12)
            if (r9 == 0) goto L_0x04e5
            r4 = r5
            org.bouncycastle.asn1.ASN1OctetString r4 = (org.bouncycastle.asn1.ASN1OctetString) r4
            goto L_0x04e5
        L_0x054d:
            r6 = 0
            r10 = 1
            goto L_0x0554
        L_0x0550:
            r6 = 0
            r10 = 1
            r4 = r1
            r7 = r4
        L_0x0554:
            java.util.Hashtable r2 = r8.chainCerts
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$CertId r5 = new org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$CertId
            java.security.PublicKey r9 = r3.getPublicKey()
            r5.<init>(r9)
            r2.put(r5, r3)
            if (r16 == 0) goto L_0x0590
            java.util.Hashtable r2 = r8.keyCerts
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x05ab
            java.lang.String r2 = new java.lang.String
            java.security.PublicKey r4 = r3.getPublicKey()
            org.bouncycastle.asn1.x509.SubjectKeyIdentifier r4 = r8.createSubjectKeyId(r4)
            byte[] r4 = r4.getKeyIdentifier()
            byte[] r4 = org.bouncycastle.util.encoders.Hex.encode(r4)
            r2.<init>(r4)
            java.util.Hashtable r4 = r8.keyCerts
            r4.put(r2, r3)
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r3 = r8.keys
            java.lang.Object r4 = r3.remove(r14)
            r3.put(r2, r4)
            goto L_0x05ab
        L_0x0590:
            if (r4 == 0) goto L_0x05a4
            java.lang.String r2 = new java.lang.String
            byte[] r4 = r4.getOctets()
            byte[] r4 = org.bouncycastle.util.encoders.Hex.encode(r4)
            r2.<init>(r4)
            java.util.Hashtable r4 = r8.keyCerts
            r4.put(r2, r3)
        L_0x05a4:
            if (r7 == 0) goto L_0x05ab
            org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$IgnoresCaseHashtable r2 = r8.certs
            r2.put(r7, r3)
        L_0x05ab:
            int r0 = r0 + 1
            goto L_0x04a0
        L_0x05af:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            java.lang.String r0 = r0.toString()
            r1.<init>(r0)
            throw r1
        L_0x05ba:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unsupported certificate type: "
            r1.append(r2)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = r3.getCertId()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x05d5:
            return
        L_0x05d6:
            r0 = move-exception
            java.io.IOException r1 = new java.io.IOException
            java.lang.String r0 = r0.getMessage()
            r1.<init>(r0)
            throw r1
        L_0x05e1:
            java.io.IOException r0 = new java.io.IOException
            java.lang.String r1 = "stream does not represent a PKCS12 key store"
            r0.<init>(r1)
            throw r0
        L_0x05e9:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            java.lang.String r1 = "No password supplied for PKCS#12 KeyStore."
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi.engineLoad(java.io.InputStream, char[]):void");
    }

    public void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
        if (this.keys.get(str) == null) {
            this.certs.put(str, certificate);
            this.chainCerts.put(new CertId(certificate.getPublicKey()), certificate);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("There is a key entry with the name ");
        sb.append(str);
        sb.append(".");
        throw new KeyStoreException(sb.toString());
    }

    public void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
        boolean z = key instanceof PrivateKey;
        if (!z) {
            throw new KeyStoreException("PKCS12 does not support non-PrivateKeys");
        } else if (!z || certificateArr != null) {
            if (this.keys.get(str) != null) {
                engineDeleteEntry(str);
            }
            this.keys.put(str, key);
            if (certificateArr != null) {
                this.certs.put(str, certificateArr[0]);
                for (int i = 0; i != certificateArr.length; i++) {
                    this.chainCerts.put(new CertId(certificateArr[i].getPublicKey()), certificateArr[i]);
                }
            }
        } else {
            throw new KeyStoreException("no certificate chain for private key");
        }
    }

    public void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
        throw new RuntimeException("operation not supported");
    }

    public int engineSize() {
        Hashtable hashtable = new Hashtable();
        Enumeration keys2 = this.certs.keys();
        while (keys2.hasMoreElements()) {
            hashtable.put(keys2.nextElement(), "cert");
        }
        Enumeration keys3 = this.keys.keys();
        while (keys3.hasMoreElements()) {
            String str = (String) keys3.nextElement();
            if (hashtable.get(str) == null) {
                hashtable.put(str, "key");
            }
        }
        return hashtable.size();
    }

    public void engineStore(OutputStream outputStream, char[] cArr) throws IOException {
        doStore(outputStream, cArr, false);
    }

    public void engineStore(LoadStoreParameter loadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
        PKCS12StoreParameter pKCS12StoreParameter;
        char[] cArr;
        if (loadStoreParameter != null) {
            boolean z = loadStoreParameter instanceof PKCS12StoreParameter;
            if (z || (loadStoreParameter instanceof JDKPKCS12StoreParameter)) {
                if (z) {
                    pKCS12StoreParameter = (PKCS12StoreParameter) loadStoreParameter;
                } else {
                    JDKPKCS12StoreParameter jDKPKCS12StoreParameter = (JDKPKCS12StoreParameter) loadStoreParameter;
                    pKCS12StoreParameter = new PKCS12StoreParameter(jDKPKCS12StoreParameter.getOutputStream(), loadStoreParameter.getProtectionParameter(), jDKPKCS12StoreParameter.isUseDEREncoding());
                }
                ProtectionParameter protectionParameter = loadStoreParameter.getProtectionParameter();
                if (protectionParameter == null) {
                    cArr = null;
                } else if (protectionParameter instanceof PasswordProtection) {
                    cArr = ((PasswordProtection) protectionParameter).getPassword();
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("No support for protection parameter of type ");
                    sb.append(protectionParameter.getClass().getName());
                    throw new IllegalArgumentException(sb.toString());
                }
                doStore(pKCS12StoreParameter.getOutputStream(), cArr, pKCS12StoreParameter.isForDEREncoding());
                return;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("No support for 'param' of type ");
            sb2.append(loadStoreParameter.getClass().getName());
            throw new IllegalArgumentException(sb2.toString());
        }
        throw new IllegalArgumentException("'param' arg cannot be null");
    }

    public void setRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
    }

    /* access modifiers changed from: protected */
    public PrivateKey unwrapKey(AlgorithmIdentifier algorithmIdentifier, byte[] bArr, char[] cArr, boolean z) throws IOException {
        ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
        try {
            boolean on = algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds);
            String str = BuildConfig.FLAVOR;
            if (on) {
                PKCS12PBEParams instance = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
                PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(instance.getIV(), validateIterationCount(instance.getIterations()));
                Cipher createCipher = this.helper.createCipher(algorithm.getId());
                createCipher.init(4, new PKCS12Key(cArr, z), pBEParameterSpec);
                return (PrivateKey) createCipher.unwrap(bArr, str, 2);
            } else if (algorithm.equals(PKCSObjectIdentifiers.id_PBES2)) {
                return (PrivateKey) createCipher(4, cArr, algorithmIdentifier).unwrap(bArr, str, 2);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("exception unwrapping private key - cannot recognise: ");
                sb.append(algorithm);
                throw new IOException(sb.toString());
            }
        } catch (Exception e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("exception unwrapping private key - ");
            sb2.append(e.toString());
            throw new IOException(sb2.toString());
        }
    }

    /* access modifiers changed from: protected */
    public byte[] wrapKey(String str, Key key, PKCS12PBEParams pKCS12PBEParams, char[] cArr) throws IOException {
        PBEKeySpec pBEKeySpec = new PBEKeySpec(cArr);
        try {
            SecretKeyFactory createSecretKeyFactory = this.helper.createSecretKeyFactory(str);
            PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(pKCS12PBEParams.getIV(), pKCS12PBEParams.getIterations().intValue());
            Cipher createCipher = this.helper.createCipher(str);
            createCipher.init(3, createSecretKeyFactory.generateSecret(pBEKeySpec), pBEParameterSpec);
            return createCipher.wrap(key);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("exception encrypting data - ");
            sb.append(e.toString());
            throw new IOException(sb.toString());
        }
    }
}
