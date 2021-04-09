package org.bouncycastle.math.ec;

import java.math.BigInteger;

public abstract class WNafUtil {
    private static final int[] DEFAULT_WINDOW_SIZE_CUTOFFS = {13, 41, 121, 337, 897, 2305};
    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final int[] EMPTY_INTS = new int[0];
    /* access modifiers changed from: private */
    public static final ECPoint[] EMPTY_POINTS = new ECPoint[0];
    public static final String PRECOMP_NAME = "bc_wnaf";

    public static int[] generateCompactNaf(BigInteger bigInteger) {
        if ((bigInteger.bitLength() >>> 16) != 0) {
            throw new IllegalArgumentException("'k' must have bitlength < 2^16");
        } else if (bigInteger.signum() == 0) {
            return EMPTY_INTS;
        } else {
            BigInteger add = bigInteger.shiftLeft(1).add(bigInteger);
            int bitLength = add.bitLength();
            int[] iArr = new int[(bitLength >> 1)];
            BigInteger xor = add.xor(bigInteger);
            int i = bitLength - 1;
            int i2 = 0;
            int i3 = 0;
            int i4 = 1;
            while (i4 < i) {
                if (!xor.testBit(i4)) {
                    i3++;
                } else {
                    int i5 = i2 + 1;
                    iArr[i2] = i3 | ((bigInteger.testBit(i4) ? -1 : 1) << 16);
                    i4++;
                    i3 = 1;
                    i2 = i5;
                }
                i4++;
            }
            int i6 = i2 + 1;
            iArr[i2] = 65536 | i3;
            if (iArr.length > i6) {
                iArr = trim(iArr, i6);
            }
            return iArr;
        }
    }

    public static int[] generateCompactWindowNaf(int i, BigInteger bigInteger) {
        if (i == 2) {
            return generateCompactNaf(bigInteger);
        }
        if (i < 2 || i > 16) {
            throw new IllegalArgumentException("'width' must be in the range [2, 16]");
        } else if ((bigInteger.bitLength() >>> 16) != 0) {
            throw new IllegalArgumentException("'k' must have bitlength < 2^16");
        } else if (bigInteger.signum() == 0) {
            return EMPTY_INTS;
        } else {
            int[] iArr = new int[((bigInteger.bitLength() / i) + 1)];
            int i2 = 1 << i;
            int i3 = i2 - 1;
            int i4 = i2 >>> 1;
            BigInteger bigInteger2 = bigInteger;
            int i5 = 0;
            int i6 = 0;
            boolean z = false;
            while (i5 <= bigInteger2.bitLength()) {
                if (bigInteger2.testBit(i5) == z) {
                    i5++;
                } else {
                    bigInteger2 = bigInteger2.shiftRight(i5);
                    int intValue = bigInteger2.intValue() & i3;
                    if (z) {
                        intValue++;
                    }
                    z = (intValue & i4) != 0;
                    if (z) {
                        intValue -= i2;
                    }
                    if (i6 > 0) {
                        i5--;
                    }
                    int i7 = i6 + 1;
                    iArr[i6] = i5 | (intValue << 16);
                    i5 = i;
                    i6 = i7;
                }
            }
            if (iArr.length > i6) {
                iArr = trim(iArr, i6);
            }
            return iArr;
        }
    }

    public static byte[] generateJSF(BigInteger bigInteger, BigInteger bigInteger2) {
        byte[] bArr = new byte[(Math.max(bigInteger.bitLength(), bigInteger2.bitLength()) + 1)];
        BigInteger bigInteger3 = bigInteger;
        BigInteger bigInteger4 = bigInteger2;
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (true) {
            if ((i | i2) == 0 && bigInteger3.bitLength() <= i3 && bigInteger4.bitLength() <= i3) {
                break;
            }
            int intValue = ((bigInteger3.intValue() >>> i3) + i) & 7;
            int intValue2 = ((bigInteger4.intValue() >>> i3) + i2) & 7;
            int i5 = intValue & 1;
            if (i5 != 0) {
                i5 -= intValue & 2;
                if (intValue + i5 == 4 && (intValue2 & 3) == 2) {
                    i5 = -i5;
                }
            }
            int i6 = intValue2 & 1;
            if (i6 != 0) {
                i6 -= intValue2 & 2;
                if (intValue2 + i6 == 4 && (intValue & 3) == 2) {
                    i6 = -i6;
                }
            }
            if ((i << 1) == i5 + 1) {
                i ^= 1;
            }
            if ((i2 << 1) == i6 + 1) {
                i2 ^= 1;
            }
            i3++;
            if (i3 == 30) {
                bigInteger3 = bigInteger3.shiftRight(30);
                bigInteger4 = bigInteger4.shiftRight(30);
                i3 = 0;
            }
            int i7 = i4 + 1;
            bArr[i4] = (byte) ((i5 << 4) | (i6 & 15));
            i4 = i7;
        }
        return bArr.length > i4 ? trim(bArr, i4) : bArr;
    }

    public static byte[] generateNaf(BigInteger bigInteger) {
        if (bigInteger.signum() == 0) {
            return EMPTY_BYTES;
        }
        BigInteger add = bigInteger.shiftLeft(1).add(bigInteger);
        int bitLength = add.bitLength() - 1;
        byte[] bArr = new byte[bitLength];
        BigInteger xor = add.xor(bigInteger);
        int i = 1;
        while (i < bitLength) {
            if (xor.testBit(i)) {
                bArr[i - 1] = (byte) (bigInteger.testBit(i) ? -1 : 1);
                i++;
            }
            i++;
        }
        bArr[bitLength - 1] = 1;
        return bArr;
    }

    public static byte[] generateWindowNaf(int i, BigInteger bigInteger) {
        if (i == 2) {
            return generateNaf(bigInteger);
        }
        if (i < 2 || i > 8) {
            throw new IllegalArgumentException("'width' must be in the range [2, 8]");
        } else if (bigInteger.signum() == 0) {
            return EMPTY_BYTES;
        } else {
            byte[] bArr = new byte[(bigInteger.bitLength() + 1)];
            int i2 = 1 << i;
            int i3 = i2 - 1;
            int i4 = i2 >>> 1;
            BigInteger bigInteger2 = bigInteger;
            int i5 = 0;
            int i6 = 0;
            boolean z = false;
            while (i5 <= bigInteger2.bitLength()) {
                if (bigInteger2.testBit(i5) == z) {
                    i5++;
                } else {
                    bigInteger2 = bigInteger2.shiftRight(i5);
                    int intValue = bigInteger2.intValue() & i3;
                    if (z) {
                        intValue++;
                    }
                    z = (intValue & i4) != 0;
                    if (z) {
                        intValue -= i2;
                    }
                    if (i6 > 0) {
                        i5--;
                    }
                    int i7 = i6 + i5;
                    int i8 = i7 + 1;
                    bArr[i7] = (byte) intValue;
                    i6 = i8;
                    i5 = i;
                }
            }
            if (bArr.length > i6) {
                bArr = trim(bArr, i6);
            }
            return bArr;
        }
    }

    public static int getNafWeight(BigInteger bigInteger) {
        if (bigInteger.signum() == 0) {
            return 0;
        }
        return bigInteger.shiftLeft(1).add(bigInteger).xor(bigInteger).bitCount();
    }

    public static WNafPreCompInfo getWNafPreCompInfo(ECPoint eCPoint) {
        return getWNafPreCompInfo(eCPoint.getCurve().getPreCompInfo(eCPoint, PRECOMP_NAME));
    }

    public static WNafPreCompInfo getWNafPreCompInfo(PreCompInfo preCompInfo) {
        if (preCompInfo instanceof WNafPreCompInfo) {
            return (WNafPreCompInfo) preCompInfo;
        }
        return null;
    }

    public static int getWindowSize(int i) {
        return getWindowSize(i, DEFAULT_WINDOW_SIZE_CUTOFFS);
    }

    public static int getWindowSize(int i, int[] iArr) {
        int i2 = 0;
        while (i2 < iArr.length && i >= iArr[i2]) {
            i2++;
        }
        return i2 + 2;
    }

    public static ECPoint mapPointWithPrecomp(ECPoint eCPoint, int i, final boolean z, final ECPointMap eCPointMap) {
        ECCurve curve = eCPoint.getCurve();
        final WNafPreCompInfo precompute = precompute(eCPoint, i, z);
        ECPoint map = eCPointMap.map(eCPoint);
        curve.precompute(map, PRECOMP_NAME, new PreCompCallback() {
            public PreCompInfo precompute(PreCompInfo preCompInfo) {
                WNafPreCompInfo wNafPreCompInfo = new WNafPreCompInfo();
                ECPoint twice = precompute.getTwice();
                if (twice != null) {
                    wNafPreCompInfo.setTwice(eCPointMap.map(twice));
                }
                ECPoint[] preComp = precompute.getPreComp();
                ECPoint[] eCPointArr = new ECPoint[preComp.length];
                for (int i = 0; i < preComp.length; i++) {
                    eCPointArr[i] = eCPointMap.map(preComp[i]);
                }
                wNafPreCompInfo.setPreComp(eCPointArr);
                if (z) {
                    ECPoint[] eCPointArr2 = new ECPoint[eCPointArr.length];
                    for (int i2 = 0; i2 < eCPointArr2.length; i2++) {
                        eCPointArr2[i2] = eCPointArr[i2].negate();
                    }
                    wNafPreCompInfo.setPreCompNeg(eCPointArr2);
                }
                return wNafPreCompInfo;
            }
        });
        return map;
    }

    public static WNafPreCompInfo precompute(final ECPoint eCPoint, final int i, final boolean z) {
        final ECCurve curve = eCPoint.getCurve();
        return (WNafPreCompInfo) curve.precompute(eCPoint, PRECOMP_NAME, new PreCompCallback() {
            private boolean checkExisting(WNafPreCompInfo wNafPreCompInfo, int i, boolean z) {
                return wNafPreCompInfo != null && checkTable(wNafPreCompInfo.getPreComp(), i) && (!z || checkTable(wNafPreCompInfo.getPreCompNeg(), i));
            }

            private boolean checkTable(ECPoint[] eCPointArr, int i) {
                return eCPointArr != null && eCPointArr.length >= i;
            }

            /* JADX WARNING: Removed duplicated region for block: B:41:0x00c5 A[LOOP:0: B:40:0x00c3->B:41:0x00c5, LOOP_END] */
            /* JADX WARNING: Removed duplicated region for block: B:52:0x00ea A[LOOP:1: B:51:0x00e8->B:52:0x00ea, LOOP_END] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public org.bouncycastle.math.ec.PreCompInfo precompute(org.bouncycastle.math.ec.PreCompInfo r12) {
                /*
                    r11 = this;
                    boolean r0 = r12 instanceof org.bouncycastle.math.ec.WNafPreCompInfo
                    r1 = 0
                    if (r0 == 0) goto L_0x0008
                    org.bouncycastle.math.ec.WNafPreCompInfo r12 = (org.bouncycastle.math.ec.WNafPreCompInfo) r12
                    goto L_0x0009
                L_0x0008:
                    r12 = r1
                L_0x0009:
                    int r0 = r3
                    r2 = 2
                    int r0 = r0 - r2
                    r3 = 0
                    int r0 = java.lang.Math.max(r3, r0)
                    r4 = 1
                    int r0 = r4 << r0
                    boolean r5 = r4
                    boolean r5 = r11.checkExisting(r12, r0, r5)
                    if (r5 == 0) goto L_0x001e
                    return r12
                L_0x001e:
                    if (r12 == 0) goto L_0x002d
                    org.bouncycastle.math.ec.ECPoint[] r5 = r12.getPreComp()
                    org.bouncycastle.math.ec.ECPoint[] r6 = r12.getPreCompNeg()
                    org.bouncycastle.math.ec.ECPoint r12 = r12.getTwice()
                    goto L_0x0030
                L_0x002d:
                    r12 = r1
                    r5 = r12
                    r6 = r5
                L_0x0030:
                    if (r5 != 0) goto L_0x0038
                    org.bouncycastle.math.ec.ECPoint[] r5 = org.bouncycastle.math.ec.WNafUtil.EMPTY_POINTS
                    r7 = r3
                    goto L_0x0039
                L_0x0038:
                    int r7 = r5.length
                L_0x0039:
                    if (r7 >= r0) goto L_0x00d6
                    org.bouncycastle.math.ec.ECPoint[] r5 = org.bouncycastle.math.ec.WNafUtil.resizeTable(r5, r0)
                    if (r0 != r4) goto L_0x004b
                    org.bouncycastle.math.ec.ECPoint r1 = r2
                    org.bouncycastle.math.ec.ECPoint r1 = r1.normalize()
                    r5[r3] = r1
                    goto L_0x00d6
                L_0x004b:
                    if (r7 != 0) goto L_0x0053
                    org.bouncycastle.math.ec.ECPoint r8 = r2
                    r5[r3] = r8
                    r8 = r4
                    goto L_0x0054
                L_0x0053:
                    r8 = r7
                L_0x0054:
                    if (r0 != r2) goto L_0x0060
                    org.bouncycastle.math.ec.ECPoint r2 = r2
                    org.bouncycastle.math.ec.ECPoint r2 = r2.threeTimes()
                    r5[r4] = r2
                    goto L_0x00cf
                L_0x0060:
                    int r4 = r8 + -1
                    r4 = r5[r4]
                    if (r12 != 0) goto L_0x00c2
                    r12 = r5[r3]
                    org.bouncycastle.math.ec.ECPoint r12 = r12.twice()
                    boolean r9 = r12.isInfinity()
                    if (r9 != 0) goto L_0x00c2
                    org.bouncycastle.math.ec.ECCurve r9 = r0
                    boolean r9 = org.bouncycastle.math.ec.ECAlgorithms.isFpCurve(r9)
                    if (r9 == 0) goto L_0x00c2
                    org.bouncycastle.math.ec.ECCurve r9 = r0
                    int r9 = r9.getFieldSize()
                    r10 = 64
                    if (r9 < r10) goto L_0x00c2
                    org.bouncycastle.math.ec.ECCurve r9 = r0
                    int r9 = r9.getCoordinateSystem()
                    if (r9 == r2) goto L_0x0093
                    r2 = 3
                    if (r9 == r2) goto L_0x0093
                    r2 = 4
                    if (r9 == r2) goto L_0x0093
                    goto L_0x00c2
                L_0x0093:
                    org.bouncycastle.math.ec.ECFieldElement r1 = r12.getZCoord(r3)
                    org.bouncycastle.math.ec.ECCurve r2 = r0
                    org.bouncycastle.math.ec.ECFieldElement r9 = r12.getXCoord()
                    java.math.BigInteger r9 = r9.toBigInteger()
                    org.bouncycastle.math.ec.ECFieldElement r10 = r12.getYCoord()
                    java.math.BigInteger r10 = r10.toBigInteger()
                    org.bouncycastle.math.ec.ECPoint r2 = r2.createPoint(r9, r10)
                    org.bouncycastle.math.ec.ECFieldElement r9 = r1.square()
                    org.bouncycastle.math.ec.ECFieldElement r10 = r9.multiply(r1)
                    org.bouncycastle.math.ec.ECPoint r4 = r4.scaleX(r9)
                    org.bouncycastle.math.ec.ECPoint r4 = r4.scaleY(r10)
                    if (r7 != 0) goto L_0x00c3
                    r5[r3] = r4
                    goto L_0x00c3
                L_0x00c2:
                    r2 = r12
                L_0x00c3:
                    if (r8 >= r0) goto L_0x00cf
                    int r9 = r8 + 1
                    org.bouncycastle.math.ec.ECPoint r4 = r4.add(r2)
                    r5[r8] = r4
                    r8 = r9
                    goto L_0x00c3
                L_0x00cf:
                    org.bouncycastle.math.ec.ECCurve r2 = r0
                    int r4 = r0 - r7
                    r2.normalizeAll(r5, r7, r4, r1)
                L_0x00d6:
                    boolean r1 = r4
                    if (r1 == 0) goto L_0x00f5
                    if (r6 != 0) goto L_0x00e0
                    org.bouncycastle.math.ec.ECPoint[] r1 = new org.bouncycastle.math.ec.ECPoint[r0]
                L_0x00de:
                    r6 = r1
                    goto L_0x00e8
                L_0x00e0:
                    int r3 = r6.length
                    if (r3 >= r0) goto L_0x00e8
                    org.bouncycastle.math.ec.ECPoint[] r1 = org.bouncycastle.math.ec.WNafUtil.resizeTable(r6, r0)
                    goto L_0x00de
                L_0x00e8:
                    if (r3 >= r0) goto L_0x00f5
                    r1 = r5[r3]
                    org.bouncycastle.math.ec.ECPoint r1 = r1.negate()
                    r6[r3] = r1
                    int r3 = r3 + 1
                    goto L_0x00e8
                L_0x00f5:
                    org.bouncycastle.math.ec.WNafPreCompInfo r0 = new org.bouncycastle.math.ec.WNafPreCompInfo
                    r0.<init>()
                    r0.setPreComp(r5)
                    r0.setPreCompNeg(r6)
                    r0.setTwice(r12)
                    return r0
                */
                throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.math.ec.WNafUtil.AnonymousClass2.precompute(org.bouncycastle.math.ec.PreCompInfo):org.bouncycastle.math.ec.PreCompInfo");
            }
        });
    }

    /* access modifiers changed from: private */
    public static ECPoint[] resizeTable(ECPoint[] eCPointArr, int i) {
        ECPoint[] eCPointArr2 = new ECPoint[i];
        System.arraycopy(eCPointArr, 0, eCPointArr2, 0, eCPointArr.length);
        return eCPointArr2;
    }

    private static byte[] trim(byte[] bArr, int i) {
        byte[] bArr2 = new byte[i];
        System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
        return bArr2;
    }

    private static int[] trim(int[] iArr, int i) {
        int[] iArr2 = new int[i];
        System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
        return iArr2;
    }
}
