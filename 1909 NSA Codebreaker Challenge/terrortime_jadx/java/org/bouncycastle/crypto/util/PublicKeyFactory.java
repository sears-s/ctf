package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.ua.DSTU4145BinaryField;
import org.bouncycastle.asn1.ua.DSTU4145ECBinary;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.asn1.ua.DSTU4145PointEncoder;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.DHPublicKey;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.bouncycastle.asn1.x9.ValidationParams;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.DHValidationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.math.ec.ECCurve.F2m;

public class PublicKeyFactory {
    private static Map converters = new HashMap();

    private static class DHAgreementConverter extends SubjectPublicKeyInfoConverter {
        private DHAgreementConverter() {
            super();
        }

        /* access modifiers changed from: 0000 */
        public AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) throws IOException {
            DHParameter instance = DHParameter.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            ASN1Integer aSN1Integer = (ASN1Integer) subjectPublicKeyInfo.parsePublicKey();
            BigInteger l = instance.getL();
            return new DHPublicKeyParameters(aSN1Integer.getValue(), new DHParameters(instance.getP(), instance.getG(), null, l == null ? 0 : l.intValue()));
        }
    }

    private static class DHPublicNumberConverter extends SubjectPublicKeyInfoConverter {
        private DHPublicNumberConverter() {
            super();
        }

        /* access modifiers changed from: 0000 */
        public AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) throws IOException {
            BigInteger y = DHPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey()).getY();
            DomainParameters instance = DomainParameters.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            BigInteger p = instance.getP();
            BigInteger g = instance.getG();
            BigInteger q = instance.getQ();
            DHValidationParameters dHValidationParameters = null;
            BigInteger j = instance.getJ() != null ? instance.getJ() : null;
            ValidationParams validationParams = instance.getValidationParams();
            if (validationParams != null) {
                dHValidationParameters = new DHValidationParameters(validationParams.getSeed(), validationParams.getPgenCounter().intValue());
            }
            DHParameters dHParameters = new DHParameters(p, g, q, j, dHValidationParameters);
            return new DHPublicKeyParameters(y, dHParameters);
        }
    }

    private static class DSAConverter extends SubjectPublicKeyInfoConverter {
        private DSAConverter() {
            super();
        }

        /* access modifiers changed from: 0000 */
        public AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) throws IOException {
            DSAParameters dSAParameters;
            ASN1Integer aSN1Integer = (ASN1Integer) subjectPublicKeyInfo.parsePublicKey();
            ASN1Encodable parameters = subjectPublicKeyInfo.getAlgorithm().getParameters();
            if (parameters != null) {
                DSAParameter instance = DSAParameter.getInstance(parameters.toASN1Primitive());
                dSAParameters = new DSAParameters(instance.getP(), instance.getQ(), instance.getG());
            } else {
                dSAParameters = null;
            }
            return new DSAPublicKeyParameters(aSN1Integer.getValue(), dSAParameters);
        }
    }

    private static class DSTUConverter extends SubjectPublicKeyInfoConverter {
        private DSTUConverter() {
            super();
        }

        private void reverseBytes(byte[] bArr) {
            for (int i = 0; i < bArr.length / 2; i++) {
                byte b = bArr[i];
                bArr[i] = bArr[(bArr.length - 1) - i];
                bArr[(bArr.length - 1) - i] = b;
            }
        }

        /* access modifiers changed from: 0000 */
        public AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) throws IOException {
            ECDomainParameters eCDomainParameters;
            try {
                byte[] octets = ((ASN1OctetString) ASN1Primitive.fromByteArray(subjectPublicKeyInfo.getPublicKeyData().getBytes())).getOctets();
                if (subjectPublicKeyInfo.getAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le)) {
                    reverseBytes(octets);
                }
                DSTU4145Params instance = DSTU4145Params.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
                if (instance.isNamedCurve()) {
                    eCDomainParameters = DSTU4145NamedCurves.getByOID(instance.getNamedCurve());
                } else {
                    DSTU4145ECBinary eCBinary = instance.getECBinary();
                    byte[] b = eCBinary.getB();
                    if (subjectPublicKeyInfo.getAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le)) {
                        reverseBytes(b);
                    }
                    DSTU4145BinaryField field = eCBinary.getField();
                    F2m f2m = new F2m(field.getM(), field.getK1(), field.getK2(), field.getK3(), eCBinary.getA(), new BigInteger(1, b));
                    byte[] g = eCBinary.getG();
                    if (subjectPublicKeyInfo.getAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le)) {
                        reverseBytes(g);
                    }
                    eCDomainParameters = new ECDomainParameters(f2m, DSTU4145PointEncoder.decodePoint(f2m, g), eCBinary.getN());
                }
                return new ECPublicKeyParameters(DSTU4145PointEncoder.decodePoint(eCDomainParameters.getCurve(), octets), eCDomainParameters);
            } catch (IOException e) {
                throw new IllegalArgumentException("error recovering public key");
            }
        }
    }

    private static class ECConverter extends SubjectPublicKeyInfoConverter {
        private ECConverter() {
            super();
        }

        /* JADX WARNING: type inference failed for: r7v3, types: [org.bouncycastle.crypto.params.ECDomainParameters] */
        /* access modifiers changed from: 0000 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.bouncycastle.crypto.params.AsymmetricKeyParameter getPublicKeyParameters(org.bouncycastle.asn1.x509.SubjectPublicKeyInfo r9, java.lang.Object r10) {
            /*
                r8 = this;
                org.bouncycastle.asn1.x509.AlgorithmIdentifier r0 = r9.getAlgorithm()
                org.bouncycastle.asn1.ASN1Encodable r0 = r0.getParameters()
                org.bouncycastle.asn1.x9.X962Parameters r0 = org.bouncycastle.asn1.x9.X962Parameters.getInstance(r0)
                boolean r1 = r0.isNamedCurve()
                if (r1 == 0) goto L_0x003e
                org.bouncycastle.asn1.ASN1Primitive r10 = r0.getParameters()
                r1 = r10
                org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r1
                org.bouncycastle.asn1.x9.X9ECParameters r10 = org.bouncycastle.crypto.ec.CustomNamedCurves.getByOID(r1)
                if (r10 != 0) goto L_0x0023
                org.bouncycastle.asn1.x9.X9ECParameters r10 = org.bouncycastle.asn1.x9.ECNamedCurveTable.getByOID(r1)
            L_0x0023:
                org.bouncycastle.crypto.params.ECNamedDomainParameters r7 = new org.bouncycastle.crypto.params.ECNamedDomainParameters
                org.bouncycastle.math.ec.ECCurve r2 = r10.getCurve()
                org.bouncycastle.math.ec.ECPoint r3 = r10.getG()
                java.math.BigInteger r4 = r10.getN()
                java.math.BigInteger r5 = r10.getH()
                byte[] r6 = r10.getSeed()
                r0 = r7
                r0.<init>(r1, r2, r3, r4, r5, r6)
                goto L_0x006b
            L_0x003e:
                boolean r1 = r0.isImplicitlyCA()
                if (r1 == 0) goto L_0x0048
                r7 = r10
                org.bouncycastle.crypto.params.ECDomainParameters r7 = (org.bouncycastle.crypto.params.ECDomainParameters) r7
                goto L_0x006b
            L_0x0048:
                org.bouncycastle.asn1.ASN1Primitive r10 = r0.getParameters()
                org.bouncycastle.asn1.x9.X9ECParameters r10 = org.bouncycastle.asn1.x9.X9ECParameters.getInstance(r10)
                org.bouncycastle.crypto.params.ECDomainParameters r6 = new org.bouncycastle.crypto.params.ECDomainParameters
                org.bouncycastle.math.ec.ECCurve r1 = r10.getCurve()
                org.bouncycastle.math.ec.ECPoint r2 = r10.getG()
                java.math.BigInteger r3 = r10.getN()
                java.math.BigInteger r4 = r10.getH()
                byte[] r5 = r10.getSeed()
                r0 = r6
                r0.<init>(r1, r2, r3, r4, r5)
                r7 = r6
            L_0x006b:
                org.bouncycastle.asn1.DERBitString r9 = r9.getPublicKeyData()
                byte[] r9 = r9.getBytes()
                org.bouncycastle.asn1.DEROctetString r10 = new org.bouncycastle.asn1.DEROctetString
                r10.<init>(r9)
                r0 = 0
                byte r0 = r9[r0]
                r1 = 4
                if (r0 != r1) goto L_0x00b1
                r0 = 1
                byte r0 = r9[r0]
                int r1 = r9.length
                r2 = 2
                int r1 = r1 - r2
                if (r0 != r1) goto L_0x00b1
                byte r0 = r9[r2]
                r1 = 3
                if (r0 == r2) goto L_0x008f
                byte r0 = r9[r2]
                if (r0 != r1) goto L_0x00b1
            L_0x008f:
                org.bouncycastle.asn1.x9.X9IntegerConverter r0 = new org.bouncycastle.asn1.x9.X9IntegerConverter
                r0.<init>()
                org.bouncycastle.math.ec.ECCurve r2 = r7.getCurve()
                int r0 = r0.getByteLength(r2)
                int r2 = r9.length
                int r2 = r2 - r1
                if (r0 < r2) goto L_0x00b1
                org.bouncycastle.asn1.ASN1Primitive r9 = org.bouncycastle.asn1.ASN1Primitive.fromByteArray(r9)     // Catch:{ IOException -> 0x00a8 }
                r10 = r9
                org.bouncycastle.asn1.ASN1OctetString r10 = (org.bouncycastle.asn1.ASN1OctetString) r10     // Catch:{ IOException -> 0x00a8 }
                goto L_0x00b1
            L_0x00a8:
                r9 = move-exception
                java.lang.IllegalArgumentException r9 = new java.lang.IllegalArgumentException
                java.lang.String r10 = "error recovering public key"
                r9.<init>(r10)
                throw r9
            L_0x00b1:
                org.bouncycastle.asn1.x9.X9ECPoint r9 = new org.bouncycastle.asn1.x9.X9ECPoint
                org.bouncycastle.math.ec.ECCurve r0 = r7.getCurve()
                r9.<init>(r0, r10)
                org.bouncycastle.crypto.params.ECPublicKeyParameters r10 = new org.bouncycastle.crypto.params.ECPublicKeyParameters
                org.bouncycastle.math.ec.ECPoint r9 = r9.getPoint()
                r10.<init>(r9, r7)
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.crypto.util.PublicKeyFactory.ECConverter.getPublicKeyParameters(org.bouncycastle.asn1.x509.SubjectPublicKeyInfo, java.lang.Object):org.bouncycastle.crypto.params.AsymmetricKeyParameter");
        }
    }

    private static class Ed25519Converter extends SubjectPublicKeyInfoConverter {
        private Ed25519Converter() {
            super();
        }

        /* access modifiers changed from: 0000 */
        public AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) {
            return new Ed25519PublicKeyParameters(PublicKeyFactory.getRawKey(subjectPublicKeyInfo, obj, 32), 0);
        }
    }

    private static class Ed448Converter extends SubjectPublicKeyInfoConverter {
        private Ed448Converter() {
            super();
        }

        /* access modifiers changed from: 0000 */
        public AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) {
            return new Ed448PublicKeyParameters(PublicKeyFactory.getRawKey(subjectPublicKeyInfo, obj, 57), 0);
        }
    }

    private static class ElGamalConverter extends SubjectPublicKeyInfoConverter {
        private ElGamalConverter() {
            super();
        }

        /* access modifiers changed from: 0000 */
        public AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) throws IOException {
            ElGamalParameter instance = ElGamalParameter.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            return new ElGamalPublicKeyParameters(((ASN1Integer) subjectPublicKeyInfo.parsePublicKey()).getValue(), new ElGamalParameters(instance.getP(), instance.getG()));
        }
    }

    private static class GOST3410_2001Converter extends SubjectPublicKeyInfoConverter {
        private GOST3410_2001Converter() {
            super();
        }

        /* access modifiers changed from: 0000 */
        public AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) {
            try {
                byte[] octets = ((ASN1OctetString) ASN1Primitive.fromByteArray(subjectPublicKeyInfo.getPublicKeyData().getBytes())).getOctets();
                byte[] bArr = new byte[65];
                bArr[0] = 4;
                for (int i = 1; i <= 32; i++) {
                    bArr[i] = octets[32 - i];
                    bArr[i + 32] = octets[64 - i];
                }
                GOST3410PublicKeyAlgParameters instance = GOST3410PublicKeyAlgParameters.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
                ECGOST3410Parameters eCGOST3410Parameters = new ECGOST3410Parameters(new ECNamedDomainParameters(instance.getPublicKeyParamSet(), ECGOST3410NamedCurves.getByOID(instance.getPublicKeyParamSet())), instance.getPublicKeyParamSet(), instance.getDigestParamSet(), instance.getEncryptionParamSet());
                return new ECPublicKeyParameters(eCGOST3410Parameters.getCurve().decodePoint(bArr), eCGOST3410Parameters);
            } catch (IOException e) {
                throw new IllegalArgumentException("error recovering public key");
            }
        }
    }

    private static class GOST3410_2012Converter extends SubjectPublicKeyInfoConverter {
        private GOST3410_2012Converter() {
            super();
        }

        /* access modifiers changed from: 0000 */
        public AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) {
            ASN1ObjectIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
            try {
                byte[] octets = ((ASN1OctetString) ASN1Primitive.fromByteArray(subjectPublicKeyInfo.getPublicKeyData().getBytes())).getOctets();
                int i = 32;
                if (algorithm.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512)) {
                    i = 64;
                }
                int i2 = i * 2;
                byte[] bArr = new byte[(i2 + 1)];
                bArr[0] = 4;
                for (int i3 = 1; i3 <= i; i3++) {
                    bArr[i3] = octets[i - i3];
                    bArr[i3 + i] = octets[i2 - i3];
                }
                GOST3410PublicKeyAlgParameters instance = GOST3410PublicKeyAlgParameters.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
                ECGOST3410Parameters eCGOST3410Parameters = new ECGOST3410Parameters(new ECNamedDomainParameters(instance.getPublicKeyParamSet(), ECGOST3410NamedCurves.getByOID(instance.getPublicKeyParamSet())), instance.getPublicKeyParamSet(), instance.getDigestParamSet(), instance.getEncryptionParamSet());
                return new ECPublicKeyParameters(eCGOST3410Parameters.getCurve().decodePoint(bArr), eCGOST3410Parameters);
            } catch (IOException e) {
                throw new IllegalArgumentException("error recovering public key");
            }
        }
    }

    private static class RSAConverter extends SubjectPublicKeyInfoConverter {
        private RSAConverter() {
            super();
        }

        /* access modifiers changed from: 0000 */
        public AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) throws IOException {
            RSAPublicKey instance = RSAPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
            return new RSAKeyParameters(false, instance.getModulus(), instance.getPublicExponent());
        }
    }

    private static abstract class SubjectPublicKeyInfoConverter {
        private SubjectPublicKeyInfoConverter() {
        }

        /* access modifiers changed from: 0000 */
        public abstract AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) throws IOException;
    }

    private static class X25519Converter extends SubjectPublicKeyInfoConverter {
        private X25519Converter() {
            super();
        }

        /* access modifiers changed from: 0000 */
        public AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) {
            return new X25519PublicKeyParameters(PublicKeyFactory.getRawKey(subjectPublicKeyInfo, obj, 32), 0);
        }
    }

    private static class X448Converter extends SubjectPublicKeyInfoConverter {
        private X448Converter() {
            super();
        }

        /* access modifiers changed from: 0000 */
        public AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) {
            return new X448PublicKeyParameters(PublicKeyFactory.getRawKey(subjectPublicKeyInfo, obj, 56), 0);
        }
    }

    static {
        converters.put(PKCSObjectIdentifiers.rsaEncryption, new RSAConverter());
        converters.put(PKCSObjectIdentifiers.id_RSASSA_PSS, new RSAConverter());
        converters.put(X509ObjectIdentifiers.id_ea_rsa, new RSAConverter());
        converters.put(X9ObjectIdentifiers.dhpublicnumber, new DHPublicNumberConverter());
        converters.put(PKCSObjectIdentifiers.dhKeyAgreement, new DHAgreementConverter());
        converters.put(X9ObjectIdentifiers.id_dsa, new DSAConverter());
        converters.put(OIWObjectIdentifiers.dsaWithSHA1, new DSAConverter());
        converters.put(OIWObjectIdentifiers.elGamalAlgorithm, new ElGamalConverter());
        converters.put(X9ObjectIdentifiers.id_ecPublicKey, new ECConverter());
        converters.put(CryptoProObjectIdentifiers.gostR3410_2001, new GOST3410_2001Converter());
        converters.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, new GOST3410_2012Converter());
        converters.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, new GOST3410_2012Converter());
        converters.put(UAObjectIdentifiers.dstu4145be, new DSTUConverter());
        converters.put(UAObjectIdentifiers.dstu4145le, new DSTUConverter());
        converters.put(EdECObjectIdentifiers.id_X25519, new X25519Converter());
        converters.put(EdECObjectIdentifiers.id_X448, new X448Converter());
        converters.put(EdECObjectIdentifiers.id_Ed25519, new Ed25519Converter());
        converters.put(EdECObjectIdentifiers.id_Ed448, new Ed448Converter());
    }

    public static AsymmetricKeyParameter createKey(InputStream inputStream) throws IOException {
        return createKey(SubjectPublicKeyInfo.getInstance(new ASN1InputStream(inputStream).readObject()));
    }

    public static AsymmetricKeyParameter createKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        return createKey(subjectPublicKeyInfo, null);
    }

    public static AsymmetricKeyParameter createKey(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) throws IOException {
        AlgorithmIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm();
        SubjectPublicKeyInfoConverter subjectPublicKeyInfoConverter = (SubjectPublicKeyInfoConverter) converters.get(algorithm.getAlgorithm());
        if (subjectPublicKeyInfoConverter != null) {
            return subjectPublicKeyInfoConverter.getPublicKeyParameters(subjectPublicKeyInfo, obj);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("algorithm identifier in public key not recognised: ");
        sb.append(algorithm.getAlgorithm());
        throw new IOException(sb.toString());
    }

    public static AsymmetricKeyParameter createKey(byte[] bArr) throws IOException {
        return createKey(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(bArr)));
    }

    /* access modifiers changed from: private */
    public static byte[] getRawKey(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj, int i) {
        byte[] octets = subjectPublicKeyInfo.getPublicKeyData().getOctets();
        if (i == octets.length) {
            return octets;
        }
        throw new RuntimeException("public key encoding has incorrect length");
    }
}
