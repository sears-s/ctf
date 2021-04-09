package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.asn1.ua.DSTU4145PointEncoder;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class BCDSTU4145PublicKey implements ECPublicKey, org.bouncycastle.jce.interfaces.ECPublicKey, ECPointEncoder {
    static final long serialVersionUID = 7026240464295649314L;
    private String algorithm = "DSTU4145";
    private transient DSTU4145Params dstuParams;
    private transient ECPublicKeyParameters ecPublicKey;
    private transient ECParameterSpec ecSpec;
    private boolean withCompression;

    public BCDSTU4145PublicKey(String str, ECPublicKeyParameters eCPublicKeyParameters) {
        this.algorithm = str;
        this.ecPublicKey = eCPublicKeyParameters;
        this.ecSpec = null;
    }

    public BCDSTU4145PublicKey(String str, ECPublicKeyParameters eCPublicKeyParameters, ECParameterSpec eCParameterSpec) {
        ECDomainParameters parameters = eCPublicKeyParameters.getParameters();
        this.algorithm = str;
        this.ecPublicKey = eCPublicKeyParameters;
        if (eCParameterSpec == null) {
            this.ecSpec = createSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), parameters);
        } else {
            this.ecSpec = eCParameterSpec;
        }
    }

    public BCDSTU4145PublicKey(String str, ECPublicKeyParameters eCPublicKeyParameters, org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpec) {
        ECDomainParameters parameters = eCPublicKeyParameters.getParameters();
        this.algorithm = str;
        this.ecSpec = eCParameterSpec == null ? createSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), parameters) : EC5Util.convertSpec(EC5Util.convertCurve(eCParameterSpec.getCurve(), eCParameterSpec.getSeed()), eCParameterSpec);
        this.ecPublicKey = eCPublicKeyParameters;
    }

    public BCDSTU4145PublicKey(ECPublicKeySpec eCPublicKeySpec) {
        this.ecSpec = eCPublicKeySpec.getParams();
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, eCPublicKeySpec.getW(), false), EC5Util.getDomainParameters(null, this.ecSpec));
    }

    BCDSTU4145PublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        populateFromPubKeyInfo(subjectPublicKeyInfo);
    }

    public BCDSTU4145PublicKey(BCDSTU4145PublicKey bCDSTU4145PublicKey) {
        this.ecPublicKey = bCDSTU4145PublicKey.ecPublicKey;
        this.ecSpec = bCDSTU4145PublicKey.ecSpec;
        this.withCompression = bCDSTU4145PublicKey.withCompression;
        this.dstuParams = bCDSTU4145PublicKey.dstuParams;
    }

    public BCDSTU4145PublicKey(org.bouncycastle.jce.spec.ECPublicKeySpec eCPublicKeySpec, ProviderConfiguration providerConfiguration) {
        if (eCPublicKeySpec.getParams() != null) {
            EllipticCurve convertCurve = EC5Util.convertCurve(eCPublicKeySpec.getParams().getCurve(), eCPublicKeySpec.getParams().getSeed());
            this.ecPublicKey = new ECPublicKeyParameters(eCPublicKeySpec.getQ(), ECUtil.getDomainParameters(providerConfiguration, eCPublicKeySpec.getParams()));
            this.ecSpec = EC5Util.convertSpec(convertCurve, eCPublicKeySpec.getParams());
            return;
        }
        this.ecPublicKey = new ECPublicKeyParameters(providerConfiguration.getEcImplicitlyCa().getCurve().createPoint(eCPublicKeySpec.getQ().getAffineXCoord().toBigInteger(), eCPublicKeySpec.getQ().getAffineYCoord().toBigInteger()), EC5Util.getDomainParameters(providerConfiguration, null));
        this.ecSpec = null;
    }

    private ECParameterSpec createSpec(EllipticCurve ellipticCurve, ECDomainParameters eCDomainParameters) {
        return new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(eCDomainParameters.getG()), eCDomainParameters.getN(), eCDomainParameters.getH().intValue());
    }

    /* JADX WARNING: type inference failed for: r15v3, types: [org.bouncycastle.jce.spec.ECParameterSpec] */
    /* JADX WARNING: type inference failed for: r1v11, types: [java.security.spec.ECParameterSpec] */
    /* JADX WARNING: type inference failed for: r1v12, types: [java.security.spec.ECParameterSpec] */
    /* JADX WARNING: type inference failed for: r5v1, types: [org.bouncycastle.jce.spec.ECNamedCurveSpec] */
    /* JADX WARNING: type inference failed for: r15v11, types: [org.bouncycastle.jce.spec.ECParameterSpec] */
    /* JADX WARNING: type inference failed for: r15v12 */
    /* JADX WARNING: type inference failed for: r4v13, types: [org.bouncycastle.jce.spec.ECNamedCurveParameterSpec] */
    /* JADX WARNING: type inference failed for: r15v15 */
    /* JADX WARNING: type inference failed for: r4v14, types: [org.bouncycastle.jce.spec.ECParameterSpec] */
    /* JADX WARNING: type inference failed for: r13v0 */
    /* JADX WARNING: type inference failed for: r15v17 */
    /* JADX WARNING: type inference failed for: r1v24 */
    /* JADX WARNING: type inference failed for: r5v6, types: [org.bouncycastle.jce.spec.ECNamedCurveSpec] */
    /* JADX WARNING: type inference failed for: r15v18 */
    /* JADX WARNING: type inference failed for: r15v19 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r15v12
  assigns: []
  uses: []
  mth insns count: 111
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
    /* JADX WARNING: Unknown variable types count: 9 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void populateFromPubKeyInfo(org.bouncycastle.asn1.x509.SubjectPublicKeyInfo r15) {
        /*
            r14 = this;
            org.bouncycastle.asn1.DERBitString r0 = r15.getPublicKeyData()
            java.lang.String r1 = "DSTU4145"
            r14.algorithm = r1
            byte[] r0 = r0.getBytes()     // Catch:{ IOException -> 0x016c }
            org.bouncycastle.asn1.ASN1Primitive r0 = org.bouncycastle.asn1.ASN1Primitive.fromByteArray(r0)     // Catch:{ IOException -> 0x016c }
            org.bouncycastle.asn1.ASN1OctetString r0 = (org.bouncycastle.asn1.ASN1OctetString) r0     // Catch:{ IOException -> 0x016c }
            byte[] r0 = r0.getOctets()
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r1 = r15.getAlgorithm()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = r1.getAlgorithm()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = org.bouncycastle.asn1.ua.UAObjectIdentifiers.dstu4145le
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0029
            r14.reverseBytes(r0)
        L_0x0029:
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r1 = r15.getAlgorithm()
            org.bouncycastle.asn1.ASN1Encodable r1 = r1.getParameters()
            org.bouncycastle.asn1.ASN1Sequence r1 = org.bouncycastle.asn1.ASN1Sequence.getInstance(r1)
            r2 = 0
            org.bouncycastle.asn1.ASN1Encodable r2 = r1.getObjectAt(r2)
            boolean r2 = r2 instanceof org.bouncycastle.asn1.ASN1Integer
            r3 = 0
            if (r2 == 0) goto L_0x0062
            org.bouncycastle.asn1.x9.X9ECParameters r15 = org.bouncycastle.asn1.x9.X9ECParameters.getInstance(r1)
            org.bouncycastle.jce.spec.ECParameterSpec r1 = new org.bouncycastle.jce.spec.ECParameterSpec
            org.bouncycastle.math.ec.ECCurve r5 = r15.getCurve()
            org.bouncycastle.math.ec.ECPoint r6 = r15.getG()
            java.math.BigInteger r7 = r15.getN()
            java.math.BigInteger r8 = r15.getH()
            byte[] r9 = r15.getSeed()
            r4 = r1
            r4.<init>(r5, r6, r7, r8, r9)
            r13 = r1
            r1 = r15
            r15 = r13
            goto L_0x0101
        L_0x0062:
            org.bouncycastle.asn1.ua.DSTU4145Params r1 = org.bouncycastle.asn1.ua.DSTU4145Params.getInstance(r1)
            r14.dstuParams = r1
            org.bouncycastle.asn1.ua.DSTU4145Params r1 = r14.dstuParams
            boolean r1 = r1.isNamedCurve()
            if (r1 == 0) goto L_0x009b
            org.bouncycastle.asn1.ua.DSTU4145Params r15 = r14.dstuParams
            org.bouncycastle.asn1.ASN1ObjectIdentifier r15 = r15.getNamedCurve()
            org.bouncycastle.crypto.params.ECDomainParameters r1 = org.bouncycastle.asn1.ua.DSTU4145NamedCurves.getByOID(r15)
            org.bouncycastle.jce.spec.ECNamedCurveParameterSpec r2 = new org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
            java.lang.String r5 = r15.getId()
            org.bouncycastle.math.ec.ECCurve r6 = r1.getCurve()
            org.bouncycastle.math.ec.ECPoint r7 = r1.getG()
            java.math.BigInteger r8 = r1.getN()
            java.math.BigInteger r9 = r1.getH()
            byte[] r10 = r1.getSeed()
            r4 = r2
            r4.<init>(r5, r6, r7, r8, r9, r10)
            r15 = r2
        L_0x0099:
            r1 = r3
            goto L_0x0101
        L_0x009b:
            org.bouncycastle.asn1.ua.DSTU4145Params r1 = r14.dstuParams
            org.bouncycastle.asn1.ua.DSTU4145ECBinary r1 = r1.getECBinary()
            byte[] r2 = r1.getB()
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r4 = r15.getAlgorithm()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = r4.getAlgorithm()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r5 = org.bouncycastle.asn1.ua.UAObjectIdentifiers.dstu4145le
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x00b8
            r14.reverseBytes(r2)
        L_0x00b8:
            org.bouncycastle.asn1.ua.DSTU4145BinaryField r4 = r1.getField()
            org.bouncycastle.math.ec.ECCurve$F2m r12 = new org.bouncycastle.math.ec.ECCurve$F2m
            int r6 = r4.getM()
            int r7 = r4.getK1()
            int r8 = r4.getK2()
            int r9 = r4.getK3()
            java.math.BigInteger r10 = r1.getA()
            java.math.BigInteger r11 = new java.math.BigInteger
            r4 = 1
            r11.<init>(r4, r2)
            r5 = r12
            r5.<init>(r6, r7, r8, r9, r10, r11)
            byte[] r2 = r1.getG()
            org.bouncycastle.asn1.x509.AlgorithmIdentifier r15 = r15.getAlgorithm()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r15 = r15.getAlgorithm()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = org.bouncycastle.asn1.ua.UAObjectIdentifiers.dstu4145le
            boolean r15 = r15.equals(r4)
            if (r15 == 0) goto L_0x00f3
            r14.reverseBytes(r2)
        L_0x00f3:
            org.bouncycastle.jce.spec.ECParameterSpec r15 = new org.bouncycastle.jce.spec.ECParameterSpec
            org.bouncycastle.math.ec.ECPoint r2 = org.bouncycastle.asn1.ua.DSTU4145PointEncoder.decodePoint(r12, r2)
            java.math.BigInteger r1 = r1.getN()
            r15.<init>(r12, r2, r1)
            goto L_0x0099
        L_0x0101:
            org.bouncycastle.math.ec.ECCurve r2 = r15.getCurve()
            byte[] r4 = r15.getSeed()
            java.security.spec.EllipticCurve r7 = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertCurve(r2, r4)
            org.bouncycastle.asn1.ua.DSTU4145Params r4 = r14.dstuParams
            if (r4 == 0) goto L_0x0154
            boolean r1 = r4.isNamedCurve()
            if (r1 == 0) goto L_0x0138
            org.bouncycastle.jce.spec.ECNamedCurveSpec r1 = new org.bouncycastle.jce.spec.ECNamedCurveSpec
            org.bouncycastle.asn1.ua.DSTU4145Params r4 = r14.dstuParams
            org.bouncycastle.asn1.ASN1ObjectIdentifier r4 = r4.getNamedCurve()
            java.lang.String r6 = r4.getId()
            org.bouncycastle.math.ec.ECPoint r4 = r15.getG()
            java.security.spec.ECPoint r8 = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertPoint(r4)
            java.math.BigInteger r9 = r15.getN()
            java.math.BigInteger r10 = r15.getH()
            r5 = r1
            r5.<init>(r6, r7, r8, r9, r10)
            goto L_0x0151
        L_0x0138:
            java.security.spec.ECParameterSpec r1 = new java.security.spec.ECParameterSpec
            org.bouncycastle.math.ec.ECPoint r4 = r15.getG()
            java.security.spec.ECPoint r4 = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertPoint(r4)
            java.math.BigInteger r5 = r15.getN()
            java.math.BigInteger r15 = r15.getH()
            int r15 = r15.intValue()
            r1.<init>(r7, r4, r5, r15)
        L_0x0151:
            r14.ecSpec = r1
            goto L_0x015a
        L_0x0154:
            java.security.spec.ECParameterSpec r15 = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertToSpec(r1)
            r14.ecSpec = r15
        L_0x015a:
            org.bouncycastle.crypto.params.ECPublicKeyParameters r15 = new org.bouncycastle.crypto.params.ECPublicKeyParameters
            org.bouncycastle.math.ec.ECPoint r0 = org.bouncycastle.asn1.ua.DSTU4145PointEncoder.decodePoint(r2, r0)
            java.security.spec.ECParameterSpec r1 = r14.ecSpec
            org.bouncycastle.crypto.params.ECDomainParameters r1 = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.getDomainParameters(r3, r1)
            r15.<init>(r0, r1)
            r14.ecPublicKey = r15
            return
        L_0x016c:
            r15 = move-exception
            java.lang.IllegalArgumentException r15 = new java.lang.IllegalArgumentException
            java.lang.String r0 = "error recovering public key"
            r15.<init>(r0)
            throw r15
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jcajce.provider.asymmetric.dstu.BCDSTU4145PublicKey.populateFromPubKeyInfo(org.bouncycastle.asn1.x509.SubjectPublicKeyInfo):void");
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray((byte[]) objectInputStream.readObject())));
    }

    private void reverseBytes(byte[] bArr) {
        for (int i = 0; i < bArr.length / 2; i++) {
            byte b = bArr[i];
            bArr[i] = bArr[(bArr.length - 1) - i];
            bArr[(bArr.length - 1) - i] = b;
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(getEncoded());
    }

    /* access modifiers changed from: 0000 */
    public ECPublicKeyParameters engineGetKeyParameters() {
        return this.ecPublicKey;
    }

    /* access modifiers changed from: 0000 */
    public org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
        ECParameterSpec eCParameterSpec = this.ecSpec;
        return eCParameterSpec != null ? EC5Util.convertSpec(eCParameterSpec, this.withCompression) : BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof BCDSTU4145PublicKey)) {
            return false;
        }
        BCDSTU4145PublicKey bCDSTU4145PublicKey = (BCDSTU4145PublicKey) obj;
        if (this.ecPublicKey.getQ().equals(bCDSTU4145PublicKey.ecPublicKey.getQ()) && engineGetSpec().equals(bCDSTU4145PublicKey.engineGetSpec())) {
            z = true;
        }
        return z;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public byte[] getEncoded() {
        ASN1Encodable aSN1Encodable;
        ASN1Encodable aSN1Encodable2 = this.dstuParams;
        if (aSN1Encodable2 != null) {
            aSN1Encodable = aSN1Encodable2;
        } else {
            ECParameterSpec eCParameterSpec = this.ecSpec;
            if (eCParameterSpec instanceof ECNamedCurveSpec) {
                aSN1Encodable = new DSTU4145Params(new ASN1ObjectIdentifier(((ECNamedCurveSpec) eCParameterSpec).getName()));
            } else {
                ECCurve convertCurve = EC5Util.convertCurve(eCParameterSpec.getCurve());
                X9ECParameters x9ECParameters = new X9ECParameters(convertCurve, EC5Util.convertPoint(convertCurve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf((long) this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
                aSN1Encodable = new X962Parameters(x9ECParameters);
            }
        }
        try {
            return KeyUtil.getEncodedSubjectPublicKeyInfo(new SubjectPublicKeyInfo(new AlgorithmIdentifier(UAObjectIdentifiers.dstu4145be, aSN1Encodable), (ASN1Encodable) new DEROctetString(DSTU4145PointEncoder.encodePoint(this.ecPublicKey.getQ()))));
        } catch (IOException e) {
            return null;
        }
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
        ECPoint q = this.ecPublicKey.getQ();
        return this.ecSpec == null ? q.getDetachedPoint() : q;
    }

    public byte[] getSbox() {
        DSTU4145Params dSTU4145Params = this.dstuParams;
        return dSTU4145Params != null ? dSTU4145Params.getDKE() : DSTU4145Params.getDefaultDKE();
    }

    public java.security.spec.ECPoint getW() {
        return EC5Util.convertPoint(this.ecPublicKey.getQ());
    }

    public int hashCode() {
        return this.ecPublicKey.getQ().hashCode() ^ engineGetSpec().hashCode();
    }

    public void setPointFormat(String str) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(str);
    }

    public String toString() {
        return ECUtil.publicKeyToString(this.algorithm, this.ecPublicKey.getQ(), engineGetSpec());
    }
}
