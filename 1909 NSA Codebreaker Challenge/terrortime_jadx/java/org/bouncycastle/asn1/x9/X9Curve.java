package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Arrays;

public class X9Curve extends ASN1Object implements X9ObjectIdentifiers {
    private ECCurve curve;
    private ASN1ObjectIdentifier fieldIdentifier;
    private byte[] seed;

    /* JADX WARNING: type inference failed for: r7v2, types: [org.bouncycastle.math.ec.ECCurve$F2m] */
    /* JADX WARNING: type inference failed for: r2v27, types: [org.bouncycastle.math.ec.ECCurve] */
    /* JADX WARNING: type inference failed for: r6v11, types: [org.bouncycastle.math.ec.ECCurve$Fp] */
    /* JADX WARNING: type inference failed for: r7v9, types: [org.bouncycastle.math.ec.ECCurve$F2m] */
    /* JADX WARNING: type inference failed for: r6v12, types: [org.bouncycastle.math.ec.ECCurve$Fp] */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r7v9, types: [org.bouncycastle.math.ec.ECCurve$F2m]
  assigns: [org.bouncycastle.math.ec.ECCurve$F2m, org.bouncycastle.math.ec.ECCurve$Fp]
  uses: [org.bouncycastle.math.ec.ECCurve$F2m, org.bouncycastle.math.ec.ECCurve, org.bouncycastle.math.ec.ECCurve$Fp]
  mth insns count: 94
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
    /* JADX WARNING: Unknown variable types count: 3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public X9Curve(org.bouncycastle.asn1.x9.X9FieldID r17, java.math.BigInteger r18, java.math.BigInteger r19, org.bouncycastle.asn1.ASN1Sequence r20) {
        /*
            r16 = this;
            r0 = r16
            r1 = r20
            r16.<init>()
            r2 = 0
            r0.fieldIdentifier = r2
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = r17.getIdentifier()
            r0.fieldIdentifier = r2
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = r0.fieldIdentifier
            org.bouncycastle.asn1.ASN1ObjectIdentifier r3 = prime_field
            boolean r2 = r2.equals(r3)
            r3 = 2
            r4 = 0
            r5 = 1
            if (r2 == 0) goto L_0x0057
            org.bouncycastle.asn1.ASN1Primitive r2 = r17.getParameters()
            org.bouncycastle.asn1.ASN1Integer r2 = (org.bouncycastle.asn1.ASN1Integer) r2
            java.math.BigInteger r7 = r2.getValue()
            java.math.BigInteger r8 = new java.math.BigInteger
            org.bouncycastle.asn1.ASN1Encodable r2 = r1.getObjectAt(r4)
            org.bouncycastle.asn1.ASN1OctetString r2 = org.bouncycastle.asn1.ASN1OctetString.getInstance(r2)
            byte[] r2 = r2.getOctets()
            r8.<init>(r5, r2)
            java.math.BigInteger r9 = new java.math.BigInteger
            org.bouncycastle.asn1.ASN1Encodable r2 = r1.getObjectAt(r5)
            org.bouncycastle.asn1.ASN1OctetString r2 = org.bouncycastle.asn1.ASN1OctetString.getInstance(r2)
            byte[] r2 = r2.getOctets()
            r9.<init>(r5, r2)
            org.bouncycastle.math.ec.ECCurve$Fp r2 = new org.bouncycastle.math.ec.ECCurve$Fp
            r6 = r2
            r10 = r18
            r11 = r19
            r6.<init>(r7, r8, r9, r10, r11)
        L_0x0053:
            r0.curve = r2
            goto L_0x010a
        L_0x0057:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = r0.fieldIdentifier
            org.bouncycastle.asn1.ASN1ObjectIdentifier r6 = characteristic_two_field
            boolean r2 = r2.equals(r6)
            if (r2 == 0) goto L_0x012a
            org.bouncycastle.asn1.ASN1Primitive r2 = r17.getParameters()
            org.bouncycastle.asn1.ASN1Sequence r2 = org.bouncycastle.asn1.ASN1Sequence.getInstance(r2)
            org.bouncycastle.asn1.ASN1Encodable r6 = r2.getObjectAt(r4)
            org.bouncycastle.asn1.ASN1Integer r6 = (org.bouncycastle.asn1.ASN1Integer) r6
            java.math.BigInteger r6 = r6.getValue()
            int r8 = r6.intValue()
            org.bouncycastle.asn1.ASN1Encodable r6 = r2.getObjectAt(r5)
            org.bouncycastle.asn1.ASN1ObjectIdentifier r6 = (org.bouncycastle.asn1.ASN1ObjectIdentifier) r6
            org.bouncycastle.asn1.ASN1ObjectIdentifier r7 = tpBasis
            boolean r7 = r6.equals(r7)
            if (r7 == 0) goto L_0x0099
            org.bouncycastle.asn1.ASN1Encodable r2 = r2.getObjectAt(r3)
            org.bouncycastle.asn1.ASN1Integer r2 = org.bouncycastle.asn1.ASN1Integer.getInstance(r2)
            java.math.BigInteger r2 = r2.getValue()
            int r2 = r2.intValue()
            r9 = r2
            r10 = r4
            r11 = r10
            goto L_0x00dc
        L_0x0099:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r7 = ppBasis
            boolean r6 = r6.equals(r7)
            if (r6 == 0) goto L_0x0122
            org.bouncycastle.asn1.ASN1Encodable r2 = r2.getObjectAt(r3)
            org.bouncycastle.asn1.ASN1Sequence r2 = org.bouncycastle.asn1.ASN1Sequence.getInstance(r2)
            org.bouncycastle.asn1.ASN1Encodable r6 = r2.getObjectAt(r4)
            org.bouncycastle.asn1.ASN1Integer r6 = org.bouncycastle.asn1.ASN1Integer.getInstance(r6)
            java.math.BigInteger r6 = r6.getValue()
            int r6 = r6.intValue()
            org.bouncycastle.asn1.ASN1Encodable r7 = r2.getObjectAt(r5)
            org.bouncycastle.asn1.ASN1Integer r7 = org.bouncycastle.asn1.ASN1Integer.getInstance(r7)
            java.math.BigInteger r7 = r7.getValue()
            int r7 = r7.intValue()
            org.bouncycastle.asn1.ASN1Encodable r2 = r2.getObjectAt(r3)
            org.bouncycastle.asn1.ASN1Integer r2 = org.bouncycastle.asn1.ASN1Integer.getInstance(r2)
            java.math.BigInteger r2 = r2.getValue()
            int r2 = r2.intValue()
            r11 = r2
            r9 = r6
            r10 = r7
        L_0x00dc:
            java.math.BigInteger r12 = new java.math.BigInteger
            org.bouncycastle.asn1.ASN1Encodable r2 = r1.getObjectAt(r4)
            org.bouncycastle.asn1.ASN1OctetString r2 = org.bouncycastle.asn1.ASN1OctetString.getInstance(r2)
            byte[] r2 = r2.getOctets()
            r12.<init>(r5, r2)
            java.math.BigInteger r13 = new java.math.BigInteger
            org.bouncycastle.asn1.ASN1Encodable r2 = r1.getObjectAt(r5)
            org.bouncycastle.asn1.ASN1OctetString r2 = org.bouncycastle.asn1.ASN1OctetString.getInstance(r2)
            byte[] r2 = r2.getOctets()
            r13.<init>(r5, r2)
            org.bouncycastle.math.ec.ECCurve$F2m r2 = new org.bouncycastle.math.ec.ECCurve$F2m
            r7 = r2
            r14 = r18
            r15 = r19
            r7.<init>(r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x0053
        L_0x010a:
            int r2 = r20.size()
            r4 = 3
            if (r2 != r4) goto L_0x0121
            org.bouncycastle.asn1.ASN1Encodable r1 = r1.getObjectAt(r3)
            org.bouncycastle.asn1.DERBitString r1 = (org.bouncycastle.asn1.DERBitString) r1
            byte[] r1 = r1.getBytes()
            byte[] r1 = org.bouncycastle.util.Arrays.clone(r1)
            r0.seed = r1
        L_0x0121:
            return
        L_0x0122:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.String r2 = "This type of EC basis is not implemented"
            r1.<init>(r2)
            throw r1
        L_0x012a:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.String r2 = "This type of ECCurve is not implemented"
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x9.X9Curve.<init>(org.bouncycastle.asn1.x9.X9FieldID, java.math.BigInteger, java.math.BigInteger, org.bouncycastle.asn1.ASN1Sequence):void");
    }

    public X9Curve(ECCurve eCCurve) {
        this(eCCurve, null);
    }

    public X9Curve(ECCurve eCCurve, byte[] bArr) {
        this.fieldIdentifier = null;
        this.curve = eCCurve;
        this.seed = Arrays.clone(bArr);
        setFieldIdentifier();
    }

    private void setFieldIdentifier() {
        ASN1ObjectIdentifier aSN1ObjectIdentifier;
        if (ECAlgorithms.isFpCurve(this.curve)) {
            aSN1ObjectIdentifier = prime_field;
        } else if (ECAlgorithms.isF2mCurve(this.curve)) {
            aSN1ObjectIdentifier = characteristic_two_field;
        } else {
            throw new IllegalArgumentException("This type of ECCurve is not implemented");
        }
        this.fieldIdentifier = aSN1ObjectIdentifier;
    }

    public ECCurve getCurve() {
        return this.curve;
    }

    public byte[] getSeed() {
        return Arrays.clone(this.seed);
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:0x0060  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.bouncycastle.asn1.ASN1Primitive toASN1Primitive() {
        /*
            r3 = this;
            org.bouncycastle.asn1.ASN1EncodableVector r0 = new org.bouncycastle.asn1.ASN1EncodableVector
            r0.<init>()
            org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = r3.fieldIdentifier
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = prime_field
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0034
            org.bouncycastle.asn1.x9.X9FieldElement r1 = new org.bouncycastle.asn1.x9.X9FieldElement
            org.bouncycastle.math.ec.ECCurve r2 = r3.curve
            org.bouncycastle.math.ec.ECFieldElement r2 = r2.getA()
            r1.<init>(r2)
            org.bouncycastle.asn1.ASN1Primitive r1 = r1.toASN1Primitive()
            r0.add(r1)
            org.bouncycastle.asn1.x9.X9FieldElement r1 = new org.bouncycastle.asn1.x9.X9FieldElement
            org.bouncycastle.math.ec.ECCurve r2 = r3.curve
            org.bouncycastle.math.ec.ECFieldElement r2 = r2.getB()
            r1.<init>(r2)
        L_0x002c:
            org.bouncycastle.asn1.ASN1Primitive r1 = r1.toASN1Primitive()
            r0.add(r1)
            goto L_0x005c
        L_0x0034:
            org.bouncycastle.asn1.ASN1ObjectIdentifier r1 = r3.fieldIdentifier
            org.bouncycastle.asn1.ASN1ObjectIdentifier r2 = characteristic_two_field
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x005c
            org.bouncycastle.asn1.x9.X9FieldElement r1 = new org.bouncycastle.asn1.x9.X9FieldElement
            org.bouncycastle.math.ec.ECCurve r2 = r3.curve
            org.bouncycastle.math.ec.ECFieldElement r2 = r2.getA()
            r1.<init>(r2)
            org.bouncycastle.asn1.ASN1Primitive r1 = r1.toASN1Primitive()
            r0.add(r1)
            org.bouncycastle.asn1.x9.X9FieldElement r1 = new org.bouncycastle.asn1.x9.X9FieldElement
            org.bouncycastle.math.ec.ECCurve r2 = r3.curve
            org.bouncycastle.math.ec.ECFieldElement r2 = r2.getB()
            r1.<init>(r2)
            goto L_0x002c
        L_0x005c:
            byte[] r1 = r3.seed
            if (r1 == 0) goto L_0x0068
            org.bouncycastle.asn1.DERBitString r2 = new org.bouncycastle.asn1.DERBitString
            r2.<init>(r1)
            r0.add(r2)
        L_0x0068:
            org.bouncycastle.asn1.DERSequence r1 = new org.bouncycastle.asn1.DERSequence
            r1.<init>(r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.x9.X9Curve.toASN1Primitive():org.bouncycastle.asn1.ASN1Primitive");
    }
}
