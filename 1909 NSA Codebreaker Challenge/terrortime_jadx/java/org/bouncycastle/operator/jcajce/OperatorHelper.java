package org.bouncycastle.operator.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Integers;

class OperatorHelper {
    private static final Map asymmetricWrapperAlgNames = new HashMap();
    private static final Map oids = new HashMap();
    private static final Map symmetricKeyAlgNames = new HashMap();
    private static final Map symmetricWrapperAlgNames = new HashMap();
    private static final Map symmetricWrapperKeySizes = new HashMap();
    private JcaJceHelper helper;

    private static class OpCertificateException extends CertificateException {
        private Throwable cause;

        public OpCertificateException(String str, Throwable th) {
            super(str);
            this.cause = th;
        }

        public Throwable getCause() {
            return this.cause;
        }
    }

    static {
        String str = "SHA1WITHRSA";
        oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"), str);
        oids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224WITHRSA");
        oids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256WITHRSA");
        oids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384WITHRSA");
        oids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512WITHRSA");
        oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3411WITHGOST3410");
        oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "GOST3411WITHECGOST3410");
        oids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "GOST3411-2012-256WITHECGOST3410-2012-256");
        oids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "GOST3411-2012-512WITHECGOST3410-2012-512");
        oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA1, "SHA1WITHPLAIN-ECDSA");
        oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA224, "SHA224WITHPLAIN-ECDSA");
        oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA256, "SHA256WITHPLAIN-ECDSA");
        oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA384, "SHA384WITHPLAIN-ECDSA");
        oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA512, "SHA512WITHPLAIN-ECDSA");
        oids.put(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, "RIPEMD160WITHPLAIN-ECDSA");
        oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1WITHCVC-ECDSA");
        oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224WITHCVC-ECDSA");
        oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256WITHCVC-ECDSA");
        oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384WITHCVC-ECDSA");
        oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512WITHCVC-ECDSA");
        oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"), "MD5WITHRSA");
        oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"), "MD2WITHRSA");
        String str2 = "SHA1WITHDSA";
        oids.put(new ASN1ObjectIdentifier("1.2.840.10040.4.3"), str2);
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1WITHECDSA");
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224WITHECDSA");
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256WITHECDSA");
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384WITHECDSA");
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512WITHECDSA");
        oids.put(OIWObjectIdentifiers.sha1WithRSA, str);
        oids.put(OIWObjectIdentifiers.dsaWithSHA1, str2);
        oids.put(NISTObjectIdentifiers.dsa_with_sha224, "SHA224WITHDSA");
        oids.put(NISTObjectIdentifiers.dsa_with_sha256, "SHA256WITHDSA");
        oids.put(OIWObjectIdentifiers.idSHA1, "SHA1");
        oids.put(NISTObjectIdentifiers.id_sha224, "SHA224");
        oids.put(NISTObjectIdentifiers.id_sha256, "SHA256");
        oids.put(NISTObjectIdentifiers.id_sha384, "SHA384");
        oids.put(NISTObjectIdentifiers.id_sha512, "SHA512");
        oids.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
        oids.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
        oids.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
        asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
        asymmetricWrapperAlgNames.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
        symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, "DESEDEWrap");
        symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap, "RC2Wrap");
        String str3 = "AESWrap";
        symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes128_wrap, str3);
        symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes192_wrap, str3);
        symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes256_wrap, str3);
        String str4 = "CamelliaWrap";
        symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia128_wrap, str4);
        symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia192_wrap, str4);
        symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia256_wrap, str4);
        symmetricWrapperAlgNames.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, "SEEDWrap");
        String str5 = "DESede";
        symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, str5);
        symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, Integers.valueOf(192));
        symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes128_wrap, Integers.valueOf(128));
        symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes192_wrap, Integers.valueOf(192));
        symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes256_wrap, Integers.valueOf(256));
        symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia128_wrap, Integers.valueOf(128));
        symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia192_wrap, Integers.valueOf(192));
        symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia256_wrap, Integers.valueOf(256));
        symmetricWrapperKeySizes.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, Integers.valueOf(128));
        symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
        String str6 = "AES";
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.aes, str6);
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes128_CBC, str6);
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes192_CBC, str6);
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes256_CBC, str6);
        symmetricKeyAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, str5);
        symmetricKeyAlgNames.put(PKCSObjectIdentifiers.RC2_CBC, "RC2");
    }

    OperatorHelper(JcaJceHelper jcaJceHelper) {
        this.helper = jcaJceHelper;
    }

    private static String getDigestName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String digestName = MessageDigestUtils.getDigestName(aSN1ObjectIdentifier);
        int indexOf = digestName.indexOf(45);
        if (indexOf <= 0 || digestName.startsWith("SHA3")) {
            return digestName;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(digestName.substring(0, indexOf));
        sb.append(digestName.substring(indexOf + 1));
        return sb.toString();
    }

    private static String getSignatureName(AlgorithmIdentifier algorithmIdentifier) {
        ASN1Encodable parameters = algorithmIdentifier.getParameters();
        if (parameters == null || DERNull.INSTANCE.equals(parameters) || !algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            return oids.containsKey(algorithmIdentifier.getAlgorithm()) ? (String) oids.get(algorithmIdentifier.getAlgorithm()) : algorithmIdentifier.getAlgorithm().getId();
        }
        RSASSAPSSparams instance = RSASSAPSSparams.getInstance(parameters);
        StringBuilder sb = new StringBuilder();
        sb.append(getDigestName(instance.getHashAlgorithm().getAlgorithm()));
        sb.append("WITHRSAANDMGF1");
        return sb.toString();
    }

    private boolean notDefaultPSSParams(ASN1Sequence aSN1Sequence) throws GeneralSecurityException {
        boolean z = false;
        if (!(aSN1Sequence == null || aSN1Sequence.size() == 0)) {
            RSASSAPSSparams instance = RSASSAPSSparams.getInstance(aSN1Sequence);
            if (!instance.getMaskGenAlgorithm().getAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1) || !instance.getHashAlgorithm().equals(AlgorithmIdentifier.getInstance(instance.getMaskGenAlgorithm().getParameters()))) {
                return true;
            }
            if (instance.getSaltLength().intValue() != createDigest(instance.getHashAlgorithm()).getDigestLength()) {
                z = true;
            }
        }
        return z;
    }

    public X509Certificate convertCertificate(X509CertificateHolder x509CertificateHolder) throws CertificateException {
        try {
            return (X509Certificate) this.helper.createCertificateFactory("X.509").generateCertificate(new ByteArrayInputStream(x509CertificateHolder.getEncoded()));
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("cannot get encoded form of certificate: ");
            sb.append(e.getMessage());
            throw new OpCertificateException(sb.toString(), e);
        } catch (NoSuchProviderException e2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("cannot find factory provider: ");
            sb2.append(e2.getMessage());
            throw new OpCertificateException(sb2.toString(), e2);
        }
    }

    public PublicKey convertPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws OperatorCreationException {
        String str = "cannot create key factory: ";
        try {
            return this.helper.createKeyFactory(subjectPublicKeyInfo.getAlgorithm().getAlgorithm().getId()).generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("cannot get encoded form of key: ");
            sb.append(e.getMessage());
            throw new OperatorCreationException(sb.toString(), e);
        } catch (NoSuchAlgorithmException e2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(e2.getMessage());
            throw new OperatorCreationException(sb2.toString(), e2);
        } catch (NoSuchProviderException e3) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("cannot find factory provider: ");
            sb3.append(e3.getMessage());
            throw new OperatorCreationException(sb3.toString(), e3);
        } catch (InvalidKeySpecException e4) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str);
            sb4.append(e4.getMessage());
            throw new OperatorCreationException(sb4.toString(), e4);
        }
    }

    /* access modifiers changed from: 0000 */
    public AlgorithmParameters createAlgorithmParameters(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        if (algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.rsaEncryption)) {
            return null;
        }
        try {
            AlgorithmParameters createAlgorithmParameters = this.helper.createAlgorithmParameters(algorithmIdentifier.getAlgorithm().getId());
            try {
                createAlgorithmParameters.init(algorithmIdentifier.getParameters().toASN1Primitive().getEncoded());
                return createAlgorithmParameters;
            } catch (IOException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("cannot initialise algorithm parameters: ");
                sb.append(e.getMessage());
                throw new OperatorCreationException(sb.toString(), e);
            }
        } catch (NoSuchAlgorithmException e2) {
            return null;
        } catch (NoSuchProviderException e3) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("cannot create algorithm parameters: ");
            sb2.append(e3.getMessage());
            throw new OperatorCreationException(sb2.toString(), e3);
        }
    }

    /* access modifiers changed from: 0000 */
    public Cipher createAsymmetricWrapper(ASN1ObjectIdentifier aSN1ObjectIdentifier, Map map) throws OperatorCreationException {
        String str = null;
        try {
            if (!map.isEmpty()) {
                str = (String) map.get(aSN1ObjectIdentifier);
            }
            if (str == null) {
                str = (String) asymmetricWrapperAlgNames.get(aSN1ObjectIdentifier);
            }
            if (str != null) {
                try {
                    return this.helper.createCipher(str);
                } catch (NoSuchAlgorithmException e) {
                    if (str.equals("RSA/ECB/PKCS1Padding")) {
                        try {
                            return this.helper.createCipher("RSA/NONE/PKCS1Padding");
                        } catch (NoSuchAlgorithmException e2) {
                        }
                    }
                }
            }
            return this.helper.createCipher(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e3) {
            StringBuilder sb = new StringBuilder();
            sb.append("cannot create cipher: ");
            sb.append(e3.getMessage());
            throw new OperatorCreationException(sb.toString(), e3);
        }
    }

    /* access modifiers changed from: 0000 */
    public Cipher createCipher(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws OperatorCreationException {
        try {
            return this.helper.createCipher(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("cannot create cipher: ");
            sb.append(e.getMessage());
            throw new OperatorCreationException(sb.toString(), e);
        }
    }

    /* access modifiers changed from: 0000 */
    public MessageDigest createDigest(AlgorithmIdentifier algorithmIdentifier) throws GeneralSecurityException {
        try {
            return this.helper.createDigest(MessageDigestUtils.getDigestName(algorithmIdentifier.getAlgorithm()));
        } catch (NoSuchAlgorithmException e) {
            if (oids.get(algorithmIdentifier.getAlgorithm()) != null) {
                return this.helper.createDigest((String) oids.get(algorithmIdentifier.getAlgorithm()));
            }
            throw e;
        }
    }

    /* access modifiers changed from: 0000 */
    public KeyAgreement createKeyAgreement(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws OperatorCreationException {
        try {
            return this.helper.createKeyAgreement(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("cannot create key agreement: ");
            sb.append(e.getMessage());
            throw new OperatorCreationException(sb.toString(), e);
        }
    }

    /* access modifiers changed from: 0000 */
    public KeyPairGenerator createKeyPairGenerator(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        try {
            return this.helper.createKeyPairGenerator(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("cannot create key agreement: ");
            sb.append(e.getMessage());
            throw new CMSException(sb.toString(), e);
        }
    }

    public Signature createRawSignature(AlgorithmIdentifier algorithmIdentifier) {
        try {
            String signatureName = getSignatureName(algorithmIdentifier);
            StringBuilder sb = new StringBuilder();
            sb.append("NONE");
            sb.append(signatureName.substring(signatureName.indexOf("WITH")));
            String sb2 = sb.toString();
            Signature createSignature = this.helper.createSignature(sb2);
            if (algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
                AlgorithmParameters createAlgorithmParameters = this.helper.createAlgorithmParameters(sb2);
                AlgorithmParametersUtils.loadParameters(createAlgorithmParameters, algorithmIdentifier.getParameters());
                createSignature.setParameter((PSSParameterSpec) createAlgorithmParameters.getParameterSpec(PSSParameterSpec.class));
            }
            return createSignature;
        } catch (Exception e) {
            return null;
        }
    }

    /* access modifiers changed from: 0000 */
    public Signature createSignature(AlgorithmIdentifier algorithmIdentifier) throws GeneralSecurityException {
        Signature signature;
        try {
            signature = this.helper.createSignature(getSignatureName(algorithmIdentifier));
        } catch (NoSuchAlgorithmException e) {
            if (oids.get(algorithmIdentifier.getAlgorithm()) != null) {
                signature = this.helper.createSignature((String) oids.get(algorithmIdentifier.getAlgorithm()));
            } else {
                throw e;
            }
        }
        if (algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            ASN1Sequence instance = ASN1Sequence.getInstance(algorithmIdentifier.getParameters());
            if (notDefaultPSSParams(instance)) {
                try {
                    AlgorithmParameters createAlgorithmParameters = this.helper.createAlgorithmParameters("PSS");
                    createAlgorithmParameters.init(instance.getEncoded());
                    signature.setParameter(createAlgorithmParameters.getParameterSpec(PSSParameterSpec.class));
                } catch (IOException e2) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("unable to process PSS parameters: ");
                    sb.append(e2.getMessage());
                    throw new GeneralSecurityException(sb.toString());
                }
            }
        }
        return signature;
    }

    /* access modifiers changed from: 0000 */
    public Cipher createSymmetricWrapper(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws OperatorCreationException {
        try {
            String str = (String) symmetricWrapperAlgNames.get(aSN1ObjectIdentifier);
            if (str != null) {
                try {
                    return this.helper.createCipher(str);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createCipher(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e2) {
            StringBuilder sb = new StringBuilder();
            sb.append("cannot create cipher: ");
            sb.append(e2.getMessage());
            throw new OperatorCreationException(sb.toString(), e2);
        }
    }

    /* access modifiers changed from: 0000 */
    public String getKeyAlgorithmName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String str = (String) symmetricKeyAlgNames.get(aSN1ObjectIdentifier);
        return str != null ? str : aSN1ObjectIdentifier.getId();
    }

    /* access modifiers changed from: 0000 */
    public int getKeySizeInBits(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return ((Integer) symmetricWrapperKeySizes.get(aSN1ObjectIdentifier)).intValue();
    }

    /* access modifiers changed from: 0000 */
    public String getWrappingAlgorithmName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return (String) symmetricWrapperAlgNames.get(aSN1ObjectIdentifier);
    }
}
