package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Strings;

public class JCEECPublicKey implements ECPublicKey, org.bouncycastle.jce.interfaces.ECPublicKey, ECPointEncoder {
    private String algorithm = "EC";
    private ECParameterSpec ecSpec;
    private GOST3410PublicKeyAlgParameters gostParams;
    private ECPoint q;
    private boolean withCompression;

    public JCEECPublicKey(String str, ECPublicKeySpec eCPublicKeySpec) {
        this.algorithm = str;
        this.ecSpec = eCPublicKeySpec.getParams();
        this.q = EC5Util.convertPoint(this.ecSpec, eCPublicKeySpec.getW(), false);
    }

    public JCEECPublicKey(String str, ECPublicKeyParameters eCPublicKeyParameters) {
        this.algorithm = str;
        this.q = eCPublicKeyParameters.getQ();
        this.ecSpec = null;
    }

    public JCEECPublicKey(String str, ECPublicKeyParameters eCPublicKeyParameters, ECParameterSpec eCParameterSpec) {
        ECDomainParameters parameters = eCPublicKeyParameters.getParameters();
        this.algorithm = str;
        this.q = eCPublicKeyParameters.getQ();
        if (eCParameterSpec == null) {
            this.ecSpec = createSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), parameters);
        } else {
            this.ecSpec = eCParameterSpec;
        }
    }

    public JCEECPublicKey(String str, ECPublicKeyParameters eCPublicKeyParameters, org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpec) {
        ECDomainParameters parameters = eCPublicKeyParameters.getParameters();
        this.algorithm = str;
        this.q = eCPublicKeyParameters.getQ();
        this.ecSpec = eCParameterSpec == null ? createSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), parameters) : EC5Util.convertSpec(EC5Util.convertCurve(eCParameterSpec.getCurve(), eCParameterSpec.getSeed()), eCParameterSpec);
    }

    public JCEECPublicKey(String str, JCEECPublicKey jCEECPublicKey) {
        this.algorithm = str;
        this.q = jCEECPublicKey.q;
        this.ecSpec = jCEECPublicKey.ecSpec;
        this.withCompression = jCEECPublicKey.withCompression;
        this.gostParams = jCEECPublicKey.gostParams;
    }

    public JCEECPublicKey(String str, org.bouncycastle.jce.spec.ECPublicKeySpec eCPublicKeySpec) {
        ECParameterSpec eCParameterSpec;
        this.algorithm = str;
        this.q = eCPublicKeySpec.getQ();
        if (eCPublicKeySpec.getParams() != null) {
            eCParameterSpec = EC5Util.convertSpec(EC5Util.convertCurve(eCPublicKeySpec.getParams().getCurve(), eCPublicKeySpec.getParams().getSeed()), eCPublicKeySpec.getParams());
        } else {
            if (this.q.getCurve() == null) {
                this.q = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve().createPoint(this.q.getAffineXCoord().toBigInteger(), this.q.getAffineYCoord().toBigInteger(), false);
            }
            eCParameterSpec = null;
        }
        this.ecSpec = eCParameterSpec;
    }

    public JCEECPublicKey(ECPublicKey eCPublicKey) {
        this.algorithm = eCPublicKey.getAlgorithm();
        this.ecSpec = eCPublicKey.getParams();
        this.q = EC5Util.convertPoint(this.ecSpec, eCPublicKey.getW(), false);
    }

    JCEECPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        populateFromPubKeyInfo(subjectPublicKeyInfo);
    }

    private ECParameterSpec createSpec(EllipticCurve ellipticCurve, ECDomainParameters eCDomainParameters) {
        return new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(eCDomainParameters.getG()), eCDomainParameters.getN(), eCDomainParameters.getH().intValue());
    }

    private void extractBytes(byte[] bArr, int i, BigInteger bigInteger) {
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length < 32) {
            byte[] bArr2 = new byte[32];
            System.arraycopy(byteArray, 0, bArr2, bArr2.length - byteArray.length, byteArray.length);
            byteArray = bArr2;
        }
        for (int i2 = 0; i2 != 32; i2++) {
            bArr[i + i2] = byteArray[(byteArray.length - 1) - i2];
        }
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [java.security.spec.ECParameterSpec] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void populateFromPubKeyInfo(org.bouncycastle.asn1.x509.SubjectPublicKeyInfo r15) {
        /*
            r14 = this;
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r0 = r15.getAlgorithmId()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r0 = r0.getAlgorithm()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers.gostR3410_2001
            boolean r0 = r0.equals(r1)
            java.lang.String r1 = "error recovering public key"
            r2 = 4
            r3 = 0
            r4 = 1
            if (r0 == 0) goto L_0x00a1
            org.bouncycastle.asn1.DERBitString r0 = r15.getPublicKeyData()
            java.lang.String r5 = "ECGOST3410"
            r14.algorithm = r5
            byte[] r0 = r0.getBytes()     // Catch:{ IOException -> 0x009a }
            org.bouncycastle.asn1.ASN1Primitive r0 = org.bouncycastle.asn1.ASN1Primitive.fromByteArray(r0)     // Catch:{ IOException -> 0x009a }
            org.bouncycastle.asn1.ASN1OctetString r0 = (org.bouncycastle.asn1.ASN1OctetString) r0     // Catch:{ IOException -> 0x009a }
            byte[] r0 = r0.getOctets()
            r1 = 65
            byte[] r1 = new byte[r1]
            r1[r3] = r2
        L_0x0031:
            r2 = 32
            if (r4 > r2) goto L_0x0045
            int r2 = r2 - r4
            byte r2 = r0[r2]
            r1[r4] = r2
            int r2 = r4 + 32
            int r3 = 64 - r4
            byte r3 = r0[r3]
            r1[r2] = r3
            int r4 = r4 + 1
            goto L_0x0031
        L_0x0045:
            org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters r0 = new org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r15 = r15.getAlgorithmId()
            org.bouncycastle.asn1.ASN1Encodable r15 = r15.getParameters()
            org.bouncycastle.asn1.ASN1Sequence r15 = (org.bouncycastle.asn1.ASN1Sequence) r15
            r0.<init>(r15)
            r14.gostParams = r0
            org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters r15 = r14.gostParams
            org.bouncycastle.asn1.ASN1ObjectIdentifier r15 = r15.getPublicKeyParamSet()
            java.lang.String r15 = org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves.getName(r15)
            org.bouncycastle.jce.spec.ECNamedCurveParameterSpec r15 = org.bouncycastle.jce.ECGOST3410NamedCurveTable.getParameterSpec(r15)
            org.bouncycastle.math.ec.ECCurve r0 = r15.getCurve()
            byte[] r2 = r15.getSeed()
            java.security.spec.EllipticCurve r5 = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertCurve(r0, r2)
            org.bouncycastle.math.ec.ECPoint r0 = r0.decodePoint(r1)
            r14.q = r0
            org.bouncycastle.jce.spec.ECNamedCurveSpec r0 = new org.bouncycastle.jce.spec.ECNamedCurveSpec
            org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters r1 = r14.gostParams
            org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = r1.getPublicKeyParamSet()
            java.lang.String r4 = org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves.getName(r1)
            org.bouncycastle.math.ec.ECPoint r1 = r15.getG()
            java.security.spec.ECPoint r6 = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertPoint(r1)
            java.math.BigInteger r7 = r15.getN()
            java.math.BigInteger r8 = r15.getH()
            r3 = r0
            r3.<init>(r4, r5, r6, r7, r8)
            r14.ecSpec = r0
            goto L_0x0173
        L_0x009a:
            r15 = move-exception
            java.lang.IllegalArgumentException r15 = new java.lang.IllegalArgumentException
            r15.<init>(r1)
            throw r15
        L_0x00a1:
            org.bouncycastle.asn1.x9.X962Parameters r0 = new org.bouncycastle.asn1.x9.X962Parameters
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r5 = r15.getAlgorithmId()
            org.bouncycastle.asn1.ASN1Encodable r5 = r5.getParameters()
            org.bouncycastle.asn1.ASN1Primitive r5 = (org.bouncycastle.asn1.ASN1Primitive) r5
            r0.<init>(r5)
            boolean r5 = r0.isNamedCurve()
            if (r5 == 0) goto L_0x00e9
            org.bouncycastle.asn1.ASN1Primitive r0 = r0.getParameters()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r0 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r0
            org.bouncycastle.asn1.x9.X9ECParameters r5 = org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil.getNamedCurveByOid(r0)
            org.bouncycastle.math.ec.ECCurve r6 = r5.getCurve()
            byte[] r7 = r5.getSeed()
            java.security.spec.EllipticCurve r10 = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertCurve(r6, r7)
            org.bouncycastle.jce.spec.ECNamedCurveSpec r7 = new org.bouncycastle.jce.spec.ECNamedCurveSpec
            java.lang.String r9 = org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil.getCurveName(r0)
            org.bouncycastle.math.ec.ECPoint r0 = r5.getG()
            java.security.spec.ECPoint r11 = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertPoint(r0)
            java.math.BigInteger r12 = r5.getN()
            java.math.BigInteger r13 = r5.getH()
            r8 = r7
            r8.<init>(r9, r10, r11, r12, r13)
        L_0x00e6:
            r14.ecSpec = r7
            goto L_0x012b
        L_0x00e9:
            boolean r5 = r0.isImplicitlyCA()
            if (r5 == 0) goto L_0x00fd
            r0 = 0
            r14.ecSpec = r0
            org.bouncycastle.jcajce.provider.config.ProviderConfiguration r0 = org.bouncycastle.jce.provider.BouncyCastleProvider.CONFIGURATION
            org.bouncycastle.jce.spec.ECParameterSpec r0 = r0.getEcImplicitlyCa()
            org.bouncycastle.math.ec.ECCurve r6 = r0.getCurve()
            goto L_0x012b
        L_0x00fd:
            org.bouncycastle.asn1.ASN1Primitive r0 = r0.getParameters()
            org.bouncycastle.asn1.x9.X9ECParameters r0 = org.bouncycastle.asn1.x9.X9ECParameters.getInstance(r0)
            org.bouncycastle.math.ec.ECCurve r6 = r0.getCurve()
            byte[] r5 = r0.getSeed()
            java.security.spec.EllipticCurve r5 = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertCurve(r6, r5)
            java.security.spec.ECParameterSpec r7 = new java.security.spec.ECParameterSpec
            org.bouncycastle.math.ec.ECPoint r8 = r0.getG()
            java.security.spec.ECPoint r8 = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertPoint(r8)
            java.math.BigInteger r9 = r0.getN()
            java.math.BigInteger r0 = r0.getH()
            int r0 = r0.intValue()
            r7.<init>(r5, r8, r9, r0)
            goto L_0x00e6
        L_0x012b:
            org.bouncycastle.asn1.DERBitString r15 = r15.getPublicKeyData()
            byte[] r15 = r15.getBytes()
            org.bouncycastle.asn1.DEROctetString r0 = new org.bouncycastle.asn1.DEROctetString
            r0.<init>(r15)
            byte r3 = r15[r3]
            if (r3 != r2) goto L_0x0168
            byte r2 = r15[r4]
            int r3 = r15.length
            r4 = 2
            int r3 = r3 - r4
            if (r2 != r3) goto L_0x0168
            byte r2 = r15[r4]
            r3 = 3
            if (r2 == r4) goto L_0x014c
            byte r2 = r15[r4]
            if (r2 != r3) goto L_0x0168
        L_0x014c:
            org.bouncycastle.asn1.x9.X9IntegerConverter r2 = new org.bouncycastle.asn1.x9.X9IntegerConverter
            r2.<init>()
            int r2 = r2.getByteLength(r6)
            int r4 = r15.length
            int r4 = r4 - r3
            if (r2 < r4) goto L_0x0168
            org.bouncycastle.asn1.ASN1Primitive r15 = org.bouncycastle.asn1.ASN1Primitive.fromByteArray(r15)     // Catch:{ IOException -> 0x0161 }
            r0 = r15
            org.bouncycastle.asn1.ASN1OctetString r0 = (org.bouncycastle.asn1.ASN1OctetString) r0     // Catch:{ IOException -> 0x0161 }
            goto L_0x0168
        L_0x0161:
            r15 = move-exception
            java.lang.IllegalArgumentException r15 = new java.lang.IllegalArgumentException
            r15.<init>(r1)
            throw r15
        L_0x0168:
            org.bouncycastle.asn1.x9.X9ECPoint r15 = new org.bouncycastle.asn1.x9.X9ECPoint
            r15.<init>(r6, r0)
            org.bouncycastle.math.ec.ECPoint r15 = r15.getPoint()
            r14.q = r15
        L_0x0173:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.provider.JCEECPublicKey.populateFromPubKeyInfo(org.bouncycastle.asn1.x509.SubjectPublicKeyInfo):void");
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray((byte[]) objectInputStream.readObject())));
        this.algorithm = (String) objectInputStream.readObject();
        this.withCompression = objectInputStream.readBoolean();
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(getEncoded());
        objectOutputStream.writeObject(this.algorithm);
        objectOutputStream.writeBoolean(this.withCompression);
    }

    public ECPoint engineGetQ() {
        return this.q;
    }

    /* access modifiers changed from: 0000 */
    public org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
        ECParameterSpec eCParameterSpec = this.ecSpec;
        return eCParameterSpec != null ? EC5Util.convertSpec(eCParameterSpec, this.withCompression) : BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof JCEECPublicKey)) {
            return false;
        }
        JCEECPublicKey jCEECPublicKey = (JCEECPublicKey) obj;
        if (engineGetQ().equals(jCEECPublicKey.engineGetQ()) && engineGetSpec().equals(jCEECPublicKey.engineGetSpec())) {
            z = true;
        }
        return z;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public byte[] getEncoded() {
        SubjectPublicKeyInfo subjectPublicKeyInfo;
        X962Parameters x962Parameters;
        ASN1Encodable aSN1Encodable;
        if (this.algorithm.equals("ECGOST3410")) {
            ASN1Encodable aSN1Encodable2 = this.gostParams;
            if (aSN1Encodable2 != null) {
                aSN1Encodable = aSN1Encodable2;
            } else {
                ECParameterSpec eCParameterSpec = this.ecSpec;
                if (eCParameterSpec instanceof ECNamedCurveSpec) {
                    aSN1Encodable = new GOST3410PublicKeyAlgParameters(ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec) eCParameterSpec).getName()), CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet);
                } else {
                    ECCurve convertCurve = EC5Util.convertCurve(eCParameterSpec.getCurve());
                    X9ECParameters x9ECParameters = new X9ECParameters(convertCurve, EC5Util.convertPoint(convertCurve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf((long) this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
                    aSN1Encodable = new X962Parameters(x9ECParameters);
                }
            }
            BigInteger bigInteger = this.q.getAffineXCoord().toBigInteger();
            BigInteger bigInteger2 = this.q.getAffineYCoord().toBigInteger();
            byte[] bArr = new byte[64];
            extractBytes(bArr, 0, bigInteger);
            extractBytes(bArr, 32, bigInteger2);
            try {
                subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, aSN1Encodable), (ASN1Encodable) new DEROctetString(bArr));
            } catch (IOException e) {
                return null;
            }
        } else {
            ECParameterSpec eCParameterSpec2 = this.ecSpec;
            if (eCParameterSpec2 instanceof ECNamedCurveSpec) {
                ASN1ObjectIdentifier namedCurveOid = ECUtil.getNamedCurveOid(((ECNamedCurveSpec) eCParameterSpec2).getName());
                if (namedCurveOid == null) {
                    namedCurveOid = new ASN1ObjectIdentifier(((ECNamedCurveSpec) this.ecSpec).getName());
                }
                x962Parameters = new X962Parameters(namedCurveOid);
            } else if (eCParameterSpec2 == null) {
                x962Parameters = new X962Parameters((ASN1Null) DERNull.INSTANCE);
            } else {
                ECCurve convertCurve2 = EC5Util.convertCurve(eCParameterSpec2.getCurve());
                X9ECParameters x9ECParameters2 = new X9ECParameters(convertCurve2, EC5Util.convertPoint(convertCurve2, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf((long) this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
                x962Parameters = new X962Parameters(x9ECParameters2);
            }
            subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, x962Parameters), ((ASN1OctetString) new X9ECPoint(engineGetQ().getCurve().createPoint(getQ().getAffineXCoord().toBigInteger(), getQ().getAffineYCoord().toBigInteger(), this.withCompression)).toASN1Primitive()).getOctets());
        }
        return KeyUtil.getEncodedSubjectPublicKeyInfo(subjectPublicKeyInfo);
    }

    public String getFormat() {
        return "X.509";
    }

    public org.bouncycastle.jce.spec.ECParameterSpec getParameters() {
        ECParameterSpec eCParameterSpec = this.ecSpec;
        if (eCParameterSpec == null) {
            return null;
        }
        return EC5Util.convertSpec(eCParameterSpec, this.withCompression);
    }

    public ECParameterSpec getParams() {
        return this.ecSpec;
    }

    public ECPoint getQ() {
        return this.ecSpec == null ? this.q.getDetachedPoint() : this.q;
    }

    public java.security.spec.ECPoint getW() {
        return EC5Util.convertPoint(this.q);
    }

    public int hashCode() {
        return engineGetQ().hashCode() ^ engineGetSpec().hashCode();
    }

    public void setPointFormat(String str) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(str);
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        String lineSeparator = Strings.lineSeparator();
        stringBuffer.append("EC Public Key");
        stringBuffer.append(lineSeparator);
        stringBuffer.append("            X: ");
        stringBuffer.append(this.q.getAffineXCoord().toBigInteger().toString(16));
        stringBuffer.append(lineSeparator);
        stringBuffer.append("            Y: ");
        stringBuffer.append(this.q.getAffineYCoord().toBigInteger().toString(16));
        stringBuffer.append(lineSeparator);
        return stringBuffer.toString();
    }
}
