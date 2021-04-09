package org.bouncycastle.jce;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.jivesoftware.smack.util.StringUtils;

public class PKCS10CertificationRequest extends CertificationRequest {
    private static Hashtable algorithms = new Hashtable();
    private static Hashtable keyAlgorithms = new Hashtable();
    private static Set noParams = new HashSet();
    private static Hashtable oids = new Hashtable();
    private static Hashtable params = new Hashtable();

    static {
        String str = "1.2.840.113549.1.1.2";
        algorithms.put("MD2WITHRSAENCRYPTION", new ASN1ObjectIdentifier(str));
        String str2 = "MD2WITHRSA";
        algorithms.put(str2, new ASN1ObjectIdentifier(str));
        String str3 = "1.2.840.113549.1.1.4";
        algorithms.put("MD5WITHRSAENCRYPTION", new ASN1ObjectIdentifier(str3));
        String str4 = "MD5WITHRSA";
        algorithms.put(str4, new ASN1ObjectIdentifier(str3));
        algorithms.put("RSAWITHMD5", new ASN1ObjectIdentifier(str3));
        String str5 = "1.2.840.113549.1.1.5";
        algorithms.put("SHA1WITHRSAENCRYPTION", new ASN1ObjectIdentifier(str5));
        String str6 = "SHA1WITHRSA";
        algorithms.put(str6, new ASN1ObjectIdentifier(str5));
        algorithms.put("SHA224WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha224WithRSAEncryption);
        String str7 = "SHA224WITHRSA";
        algorithms.put(str7, PKCSObjectIdentifiers.sha224WithRSAEncryption);
        algorithms.put("SHA256WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha256WithRSAEncryption);
        String str8 = "SHA256WITHRSA";
        algorithms.put(str8, PKCSObjectIdentifiers.sha256WithRSAEncryption);
        algorithms.put("SHA384WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha384WithRSAEncryption);
        String str9 = "SHA384WITHRSA";
        algorithms.put(str9, PKCSObjectIdentifiers.sha384WithRSAEncryption);
        algorithms.put("SHA512WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha512WithRSAEncryption);
        String str10 = "SHA512WITHRSA";
        algorithms.put(str10, PKCSObjectIdentifiers.sha512WithRSAEncryption);
        String str11 = "SHA1WITHRSAANDMGF1";
        algorithms.put(str11, PKCSObjectIdentifiers.id_RSASSA_PSS);
        String str12 = "SHA224WITHRSAANDMGF1";
        algorithms.put(str12, PKCSObjectIdentifiers.id_RSASSA_PSS);
        String str13 = "SHA256WITHRSAANDMGF1";
        algorithms.put(str13, PKCSObjectIdentifiers.id_RSASSA_PSS);
        algorithms.put("SHA384WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        algorithms.put("SHA512WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        algorithms.put("RSAWITHSHA1", new ASN1ObjectIdentifier(str5));
        algorithms.put("RIPEMD128WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
        algorithms.put("RIPEMD128WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
        algorithms.put("RIPEMD160WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
        algorithms.put("RIPEMD160WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
        algorithms.put("RIPEMD256WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
        algorithms.put("RIPEMD256WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
        String str14 = "1.2.840.10040.4.3";
        String str15 = str13;
        String str16 = "SHA1WITHDSA";
        algorithms.put(str16, new ASN1ObjectIdentifier(str14));
        String str17 = str12;
        algorithms.put("DSAWITHSHA1", new ASN1ObjectIdentifier(str14));
        algorithms.put("SHA224WITHDSA", NISTObjectIdentifiers.dsa_with_sha224);
        algorithms.put("SHA256WITHDSA", NISTObjectIdentifiers.dsa_with_sha256);
        algorithms.put("SHA384WITHDSA", NISTObjectIdentifiers.dsa_with_sha384);
        algorithms.put("SHA512WITHDSA", NISTObjectIdentifiers.dsa_with_sha512);
        algorithms.put("SHA1WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1);
        algorithms.put("SHA224WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA224);
        algorithms.put("SHA256WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA256);
        algorithms.put("SHA384WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA384);
        algorithms.put("SHA512WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA512);
        algorithms.put("ECDSAWITHSHA1", X9ObjectIdentifiers.ecdsa_with_SHA1);
        algorithms.put("GOST3411WITHGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        algorithms.put("GOST3410WITHGOST3411", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        algorithms.put("GOST3411WITHECGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        algorithms.put("GOST3411WITHECGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        algorithms.put("GOST3411WITHGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        oids.put(new ASN1ObjectIdentifier(str5), str6);
        oids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, str7);
        oids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, str8);
        oids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, str9);
        oids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, str10);
        oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3411WITHGOST3410");
        oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "GOST3411WITHECGOST3410");
        oids.put(new ASN1ObjectIdentifier(str3), str4);
        oids.put(new ASN1ObjectIdentifier(str), str2);
        oids.put(new ASN1ObjectIdentifier(str14), str16);
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1WITHECDSA");
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224WITHECDSA");
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256WITHECDSA");
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384WITHECDSA");
        oids.put(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512WITHECDSA");
        oids.put(OIWObjectIdentifiers.sha1WithRSA, str6);
        oids.put(OIWObjectIdentifiers.dsaWithSHA1, str16);
        oids.put(NISTObjectIdentifiers.dsa_with_sha224, "SHA224WITHDSA");
        oids.put(NISTObjectIdentifiers.dsa_with_sha256, "SHA256WITHDSA");
        keyAlgorithms.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        keyAlgorithms.put(X9ObjectIdentifiers.id_dsa, "DSA");
        noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA1);
        noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA224);
        noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA256);
        noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA384);
        noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA512);
        noParams.add(X9ObjectIdentifiers.id_dsa_with_sha1);
        noParams.add(NISTObjectIdentifiers.dsa_with_sha224);
        noParams.add(NISTObjectIdentifiers.dsa_with_sha256);
        noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        params.put(str11, creatPSSParams(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE), 20));
        String str18 = str17;
        params.put(str18, creatPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, DERNull.INSTANCE), 28));
        String str19 = str15;
        params.put(str19, creatPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE), 32));
        String str20 = "SHA384WITHRSAANDMGF1";
        params.put(str20, creatPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, DERNull.INSTANCE), 48));
        String str21 = "SHA512WITHRSAANDMGF1";
        params.put(str21, creatPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, DERNull.INSTANCE), 64));
    }

    public PKCS10CertificationRequest(String str, X500Principal x500Principal, PublicKey publicKey, ASN1Set aSN1Set, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        this(str, convertName(x500Principal), publicKey, aSN1Set, privateKey, "BC");
    }

    public PKCS10CertificationRequest(String str, X500Principal x500Principal, PublicKey publicKey, ASN1Set aSN1Set, PrivateKey privateKey, String str2) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        this(str, convertName(x500Principal), publicKey, aSN1Set, privateKey, str2);
    }

    public PKCS10CertificationRequest(String str, X509Name x509Name, PublicKey publicKey, ASN1Set aSN1Set, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        this(str, x509Name, publicKey, aSN1Set, privateKey, "BC");
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x006b  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0070  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public PKCS10CertificationRequest(java.lang.String r5, org.bouncycastle.asn1.x509.X509Name r6, java.security.PublicKey r7, org.bouncycastle.asn1.ASN1Set r8, java.security.PrivateKey r9, java.lang.String r10) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException, java.security.InvalidKeyException, java.security.SignatureException {
        /*
            r4 = this;
            r4.<init>()
            java.lang.String r0 = org.bouncycastle.util.Strings.toUpperCase(r5)
            java.util.Hashtable r1 = algorithms
            java.lang.Object r1 = r1.get(r0)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r1
            if (r1 != 0) goto L_0x0020
            org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = new org.bouncycastle.asn1.ASN1ObjectIdentifier     // Catch:{ Exception -> 0x0017 }
            r1.<init>(r0)     // Catch:{ Exception -> 0x0017 }
            goto L_0x0020
        L_0x0017:
            r5 = move-exception
            java.lang.IllegalArgumentException r5 = new java.lang.IllegalArgumentException
            java.lang.String r6 = "Unknown signature type requested"
            r5.<init>(r6)
            throw r5
        L_0x0020:
            if (r6 == 0) goto L_0x00b7
            if (r7 == 0) goto L_0x00af
            java.util.Set r2 = noParams
            boolean r2 = r2.contains(r1)
            if (r2 == 0) goto L_0x0034
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r0 = new org.bouncycastle.asn1.x509.AlgorithmIdentifier
            r0.<init>(r1)
        L_0x0031:
            r4.sigAlgId = r0
            goto L_0x0054
        L_0x0034:
            java.util.Hashtable r2 = params
            boolean r2 = r2.containsKey(r0)
            if (r2 == 0) goto L_0x004c
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r2 = new org.bouncycastle.asn1.x509.AlgorithmIdentifier
            java.util.Hashtable r3 = params
            java.lang.Object r0 = r3.get(r0)
            org.bouncycastle.asn1.ASN1Encodable r0 = (org.bouncycastle.asn1.ASN1Encodable) r0
            r2.<init>(r1, r0)
            r4.sigAlgId = r2
            goto L_0x0054
        L_0x004c:
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r0 = new org.bouncycastle.asn1.x509.AlgorithmIdentifier
            org.bouncycastle.asn1.DERNull r2 = org.bouncycastle.asn1.DERNull.INSTANCE
            r0.<init>(r1, r2)
            goto L_0x0031
        L_0x0054:
            byte[] r7 = r7.getEncoded()     // Catch:{ IOException -> 0x00a6 }
            org.bouncycastle.asn1.ASN1Primitive r7 = org.bouncycastle.asn1.ASN1Primitive.fromByteArray(r7)     // Catch:{ IOException -> 0x00a6 }
            org.bouncycastle.asn1.ASN1Sequence r7 = (org.bouncycastle.asn1.ASN1Sequence) r7     // Catch:{ IOException -> 0x00a6 }
            org.bouncycastle.asn1.pkcs.CertificationRequestInfo r0 = new org.bouncycastle.asn1.pkcs.CertificationRequestInfo     // Catch:{ IOException -> 0x00a6 }
            org.bouncycastle.asn1.x509.SubjectPublicKeyInfo r7 = org.bouncycastle.asn1.x509.SubjectPublicKeyInfo.getInstance(r7)     // Catch:{ IOException -> 0x00a6 }
            r0.<init>(r6, r7, r8)     // Catch:{ IOException -> 0x00a6 }
            r4.reqInfo = r0     // Catch:{ IOException -> 0x00a6 }
            if (r10 != 0) goto L_0x0070
            java.security.Signature r5 = java.security.Signature.getInstance(r5)
            goto L_0x0074
        L_0x0070:
            java.security.Signature r5 = java.security.Signature.getInstance(r5, r10)
        L_0x0074:
            r5.initSign(r9)
            org.bouncycastle.asn1.pkcs.CertificationRequestInfo r6 = r4.reqInfo     // Catch:{ Exception -> 0x008e }
            java.lang.String r7 = "DER"
            byte[] r6 = r6.getEncoded(r7)     // Catch:{ Exception -> 0x008e }
            r5.update(r6)     // Catch:{ Exception -> 0x008e }
            org.bouncycastle.asn1.DERBitString r6 = new org.bouncycastle.asn1.DERBitString
            byte[] r5 = r5.sign()
            r6.<init>(r5)
            r4.sigBits = r6
            return
        L_0x008e:
            r5 = move-exception
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "exception encoding TBS cert request - "
            r7.append(r8)
            r7.append(r5)
            java.lang.String r5 = r7.toString()
            r6.<init>(r5)
            throw r6
        L_0x00a6:
            r5 = move-exception
            java.lang.IllegalArgumentException r5 = new java.lang.IllegalArgumentException
            java.lang.String r6 = "can't encode public key"
            r5.<init>(r6)
            throw r5
        L_0x00af:
            java.lang.IllegalArgumentException r5 = new java.lang.IllegalArgumentException
            java.lang.String r6 = "public key must not be null"
            r5.<init>(r6)
            throw r5
        L_0x00b7:
            java.lang.IllegalArgumentException r5 = new java.lang.IllegalArgumentException
            java.lang.String r6 = "subject must not be null"
            r5.<init>(r6)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.PKCS10CertificationRequest.<init>(java.lang.String, org.bouncycastle.asn1.x509.X509Name, java.security.PublicKey, org.bouncycastle.asn1.ASN1Set, java.security.PrivateKey, java.lang.String):void");
    }

    public PKCS10CertificationRequest(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public PKCS10CertificationRequest(byte[] bArr) {
        super(toDERSequence(bArr));
    }

    private static X509Name convertName(X500Principal x500Principal) {
        try {
            return new X509Principal(x500Principal.getEncoded());
        } catch (IOException e) {
            throw new IllegalArgumentException("can't convert name");
        }
    }

    private static RSASSAPSSparams creatPSSParams(AlgorithmIdentifier algorithmIdentifier, int i) {
        return new RSASSAPSSparams(algorithmIdentifier, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, algorithmIdentifier), new ASN1Integer((long) i), new ASN1Integer(1));
    }

    private static String getDigestAlgName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return PKCSObjectIdentifiers.md5.equals(aSN1ObjectIdentifier) ? StringUtils.MD5 : OIWObjectIdentifiers.idSHA1.equals(aSN1ObjectIdentifier) ? "SHA1" : NISTObjectIdentifiers.id_sha224.equals(aSN1ObjectIdentifier) ? "SHA224" : NISTObjectIdentifiers.id_sha256.equals(aSN1ObjectIdentifier) ? "SHA256" : NISTObjectIdentifiers.id_sha384.equals(aSN1ObjectIdentifier) ? "SHA384" : NISTObjectIdentifiers.id_sha512.equals(aSN1ObjectIdentifier) ? "SHA512" : TeleTrusTObjectIdentifiers.ripemd128.equals(aSN1ObjectIdentifier) ? "RIPEMD128" : TeleTrusTObjectIdentifiers.ripemd160.equals(aSN1ObjectIdentifier) ? "RIPEMD160" : TeleTrusTObjectIdentifiers.ripemd256.equals(aSN1ObjectIdentifier) ? "RIPEMD256" : CryptoProObjectIdentifiers.gostR3411.equals(aSN1ObjectIdentifier) ? "GOST3411" : aSN1ObjectIdentifier.getId();
    }

    static String getSignatureName(AlgorithmIdentifier algorithmIdentifier) {
        ASN1Encodable parameters = algorithmIdentifier.getParameters();
        if (parameters == null || DERNull.INSTANCE.equals(parameters) || !algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            return algorithmIdentifier.getAlgorithm().getId();
        }
        RSASSAPSSparams instance = RSASSAPSSparams.getInstance(parameters);
        StringBuilder sb = new StringBuilder();
        sb.append(getDigestAlgName(instance.getHashAlgorithm().getAlgorithm()));
        sb.append("withRSAandMGF1");
        return sb.toString();
    }

    private void setSignatureParameters(Signature signature, ASN1Encodable aSN1Encodable) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (aSN1Encodable != null && !DERNull.INSTANCE.equals(aSN1Encodable)) {
            AlgorithmParameters instance = AlgorithmParameters.getInstance(signature.getAlgorithm(), signature.getProvider());
            try {
                instance.init(aSN1Encodable.toASN1Primitive().getEncoded(ASN1Encoding.DER));
                if (signature.getAlgorithm().endsWith("MGF1")) {
                    try {
                        signature.setParameter(instance.getParameterSpec(PSSParameterSpec.class));
                    } catch (GeneralSecurityException e) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Exception extracting parameters: ");
                        sb.append(e.getMessage());
                        throw new SignatureException(sb.toString());
                    }
                }
            } catch (IOException e2) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("IOException decoding parameters: ");
                sb2.append(e2.getMessage());
                throw new SignatureException(sb2.toString());
            }
        }
    }

    private static ASN1Sequence toDERSequence(byte[] bArr) {
        try {
            return (ASN1Sequence) new ASN1InputStream(bArr).readObject();
        } catch (Exception e) {
            throw new IllegalArgumentException("badly encoded request");
        }
    }

    public byte[] getEncoded() {
        try {
            return getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public PublicKey getPublicKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        return getPublicKey("BC");
    }

    public PublicKey getPublicKey(String str) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        X509EncodedKeySpec x509EncodedKeySpec;
        AlgorithmIdentifier algorithm;
        String str2 = "error decoding public key";
        SubjectPublicKeyInfo subjectPublicKeyInfo = this.reqInfo.getSubjectPublicKeyInfo();
        try {
            x509EncodedKeySpec = new X509EncodedKeySpec(new DERBitString((ASN1Encodable) subjectPublicKeyInfo).getOctets());
            algorithm = subjectPublicKeyInfo.getAlgorithm();
            return str == null ? KeyFactory.getInstance(algorithm.getAlgorithm().getId()).generatePublic(x509EncodedKeySpec) : KeyFactory.getInstance(algorithm.getAlgorithm().getId(), str).generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException e) {
            if (keyAlgorithms.get(algorithm.getAlgorithm()) != null) {
                String str3 = (String) keyAlgorithms.get(algorithm.getAlgorithm());
                return str == null ? KeyFactory.getInstance(str3).generatePublic(x509EncodedKeySpec) : KeyFactory.getInstance(str3, str).generatePublic(x509EncodedKeySpec);
            }
            throw e;
        } catch (InvalidKeySpecException e2) {
            throw new InvalidKeyException(str2);
        } catch (IOException e3) {
            throw new InvalidKeyException(str2);
        }
    }

    public boolean verify() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        return verify("BC");
    }

    public boolean verify(String str) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        return verify(getPublicKey(str), str);
    }

    public boolean verify(PublicKey publicKey, String str) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Signature signature;
        if (str == null) {
            try {
                signature = Signature.getInstance(getSignatureName(this.sigAlgId));
            } catch (NoSuchAlgorithmException e) {
                if (oids.get(this.sigAlgId.getAlgorithm()) != null) {
                    String str2 = (String) oids.get(this.sigAlgId.getAlgorithm());
                    signature = str == null ? Signature.getInstance(str2) : Signature.getInstance(str2, str);
                } else {
                    throw e;
                }
            }
        } else {
            signature = Signature.getInstance(getSignatureName(this.sigAlgId), str);
        }
        setSignatureParameters(signature, this.sigAlgId.getParameters());
        signature.initVerify(publicKey);
        try {
            signature.update(this.reqInfo.getEncoded(ASN1Encoding.DER));
            return signature.verify(this.sigBits.getOctets());
        } catch (Exception e2) {
            StringBuilder sb = new StringBuilder();
            sb.append("exception encoding TBS cert request - ");
            sb.append(e2);
            throw new SignatureException(sb.toString());
        }
    }
}
