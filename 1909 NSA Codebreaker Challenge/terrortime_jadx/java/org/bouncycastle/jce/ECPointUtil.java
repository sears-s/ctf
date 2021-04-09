package org.bouncycastle.jce;

public class ECPointUtil {
    /* JADX WARNING: type inference failed for: r1v0, types: [org.bouncycastle.math.ec.ECCurve] */
    /* JADX WARNING: type inference failed for: r0v6, types: [org.bouncycastle.math.ec.ECCurve$Fp] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.security.spec.ECPoint decodePoint(java.security.spec.EllipticCurve r11, byte[] r12) {
        /*
            java.security.spec.ECField r0 = r11.getField()
            boolean r0 = r0 instanceof java.security.spec.ECFieldFp
            if (r0 == 0) goto L_0x0021
            org.bouncycastle.math.ec.ECCurve$Fp r0 = new org.bouncycastle.math.ec.ECCurve$Fp
            java.security.spec.ECField r1 = r11.getField()
            java.security.spec.ECFieldFp r1 = (java.security.spec.ECFieldFp) r1
            java.math.BigInteger r1 = r1.getP()
            java.math.BigInteger r2 = r11.getA()
            java.math.BigInteger r11 = r11.getB()
            r0.<init>(r1, r2, r11)
            r1 = r0
            goto L_0x006a
        L_0x0021:
            java.security.spec.ECField r0 = r11.getField()
            java.security.spec.ECFieldF2m r0 = (java.security.spec.ECFieldF2m) r0
            int[] r0 = r0.getMidTermsOfReductionPolynomial()
            int r1 = r0.length
            r2 = 3
            r3 = 0
            if (r1 != r2) goto L_0x0051
            org.bouncycastle.math.ec.ECCurve$F2m r1 = new org.bouncycastle.math.ec.ECCurve$F2m
            java.security.spec.ECField r2 = r11.getField()
            java.security.spec.ECFieldF2m r2 = (java.security.spec.ECFieldF2m) r2
            int r5 = r2.getM()
            r2 = 2
            r6 = r0[r2]
            r2 = 1
            r7 = r0[r2]
            r8 = r0[r3]
            java.math.BigInteger r9 = r11.getA()
            java.math.BigInteger r10 = r11.getB()
            r4 = r1
            r4.<init>(r5, r6, r7, r8, r9, r10)
            goto L_0x006a
        L_0x0051:
            org.bouncycastle.math.ec.ECCurve$F2m r1 = new org.bouncycastle.math.ec.ECCurve$F2m
            java.security.spec.ECField r2 = r11.getField()
            java.security.spec.ECFieldF2m r2 = (java.security.spec.ECFieldF2m) r2
            int r2 = r2.getM()
            r0 = r0[r3]
            java.math.BigInteger r3 = r11.getA()
            java.math.BigInteger r11 = r11.getB()
            r1.<init>(r2, r0, r3, r11)
        L_0x006a:
            org.bouncycastle.math.ec.ECPoint r11 = r1.decodePoint(r12)
            java.security.spec.ECPoint r11 = org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertPoint(r11)
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jce.ECPointUtil.decodePoint(java.security.spec.EllipticCurve, byte[]):java.security.spec.ECPoint");
    }
}
