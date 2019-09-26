package org.bouncycastle.math.ec.rfc8032;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.math.ec.rfc7748.X448.Friend;
import org.bouncycastle.math.ec.rfc7748.X448Field;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class Ed448 {
    private static final int[] B_x = {118276190, 40534716, 9670182, 135141552, 85017403, 259173222, 68333082, 171784774, 174973732, 15824510, 73756743, 57518561, 94773951, 248652241, 107736333, 82941708};
    private static final int[] B_y = {36764180, 8885695, 130592152, 20104429, 163904957, 30304195, 121295871, 5901357, 125344798, 171541512, 175338348, 209069246, 3626697, 38307682, 24032956, 110359655};
    private static final int C_d = -39081;
    private static final byte[] DOM4_PREFIX = Strings.toByteArray("SigEd448");
    private static final int[] L = {-1420278541, 595116690, -1916432555, 560775794, -1361693040, -1001465015, 2093622249, -1, -1, -1, -1, -1, -1, 1073741823};
    private static final int L4_0 = 43969588;
    private static final int L4_1 = 30366549;
    private static final int L4_2 = 163752818;
    private static final int L4_3 = 258169998;
    private static final int L4_4 = 96434764;
    private static final int L4_5 = 227822194;
    private static final int L4_6 = 149865618;
    private static final int L4_7 = 550336261;
    private static final int L_0 = 78101261;
    private static final int L_1 = 141809365;
    private static final int L_2 = 175155932;
    private static final int L_3 = 64542499;
    private static final int L_4 = 158326419;
    private static final int L_5 = 191173276;
    private static final int L_6 = 104575268;
    private static final int L_7 = 137584065;
    private static final long M26L = 67108863;
    private static final long M28L = 268435455;
    private static final long M32L = 4294967295L;
    private static final int[] P = {-1, -1, -1, -1, -1, -1, -1, -2, -1, -1, -1, -1, -1, -1};
    private static final int POINT_BYTES = 57;
    private static final int PRECOMP_BLOCKS = 5;
    private static final int PRECOMP_MASK = 15;
    private static final int PRECOMP_POINTS = 16;
    private static final int PRECOMP_SPACING = 18;
    private static final int PRECOMP_TEETH = 5;
    public static final int PREHASH_SIZE = 64;
    public static final int PUBLIC_KEY_SIZE = 57;
    private static final int SCALAR_BYTES = 57;
    private static final int SCALAR_INTS = 14;
    public static final int SECRET_KEY_SIZE = 57;
    public static final int SIGNATURE_SIZE = 114;
    private static final int WNAF_WIDTH_BASE = 7;
    private static int[] precompBase = null;
    private static PointExt[] precompBaseTable = null;
    private static Object precompLock = new Object();

    public static final class Algorithm {
        public static final int Ed448 = 0;
        public static final int Ed448ph = 1;
    }

    private static class PointExt {
        int[] x;
        int[] y;
        int[] z;

        private PointExt() {
            this.x = X448Field.create();
            this.y = X448Field.create();
            this.z = X448Field.create();
        }
    }

    private static class PointPrecomp {
        int[] x;
        int[] y;

        private PointPrecomp() {
            this.x = X448Field.create();
            this.y = X448Field.create();
        }
    }

    private static byte[] calculateS(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        int[] iArr = new int[28];
        decodeScalar(bArr, 0, iArr);
        int[] iArr2 = new int[14];
        decodeScalar(bArr2, 0, iArr2);
        int[] iArr3 = new int[14];
        decodeScalar(bArr3, 0, iArr3);
        Nat.mulAddTo(14, iArr2, iArr3, iArr);
        byte[] bArr4 = new byte[114];
        for (int i = 0; i < iArr.length; i++) {
            encode32(iArr[i], bArr4, i * 4);
        }
        return reduceScalar(bArr4);
    }

    private static boolean checkContextVar(byte[] bArr) {
        return bArr != null && bArr.length < 256;
    }

    private static boolean checkPointVar(byte[] bArr) {
        if ((bArr[56] & Byte.MAX_VALUE) != 0) {
            return false;
        }
        int[] iArr = new int[14];
        decode32(bArr, 0, iArr, 0, 14);
        return !Nat.gte(14, iArr, P);
    }

    private static boolean checkScalarVar(byte[] bArr) {
        if (bArr[56] != 0) {
            return false;
        }
        int[] iArr = new int[14];
        decodeScalar(bArr, 0, iArr);
        return !Nat.gte(14, iArr, L);
    }

    public static Xof createPrehash() {
        return createXof();
    }

    private static Xof createXof() {
        return new SHAKEDigest(256);
    }

    private static int decode16(byte[] bArr, int i) {
        return ((bArr[i + 1] & 255) << 8) | (bArr[i] & 255);
    }

    private static int decode24(byte[] bArr, int i) {
        int i2 = i + 1;
        return ((bArr[i2 + 1] & 255) << Tnaf.POW_2_WIDTH) | (bArr[i] & 255) | ((bArr[i2] & 255) << 8);
    }

    private static int decode32(byte[] bArr, int i) {
        int i2 = i + 1;
        int i3 = i2 + 1;
        return (bArr[i3 + 1] << 24) | (bArr[i] & 255) | ((bArr[i2] & 255) << 8) | ((bArr[i3] & 255) << Tnaf.POW_2_WIDTH);
    }

    private static void decode32(byte[] bArr, int i, int[] iArr, int i2, int i3) {
        for (int i4 = 0; i4 < i3; i4++) {
            iArr[i2 + i4] = decode32(bArr, (i4 * 4) + i);
        }
    }

    private static boolean decodePointVar(byte[] bArr, int i, boolean z, PointExt pointExt) {
        byte[] copyOfRange = Arrays.copyOfRange(bArr, i, i + 57);
        boolean z2 = false;
        if (!checkPointVar(copyOfRange)) {
            return false;
        }
        int i2 = (copyOfRange[56] & 128) >>> 7;
        copyOfRange[56] = (byte) (copyOfRange[56] & Byte.MAX_VALUE);
        X448Field.decode(copyOfRange, 0, pointExt.y);
        int[] create = X448Field.create();
        int[] create2 = X448Field.create();
        X448Field.sqr(pointExt.y, create);
        X448Field.mul(create, 39081, create2);
        X448Field.negate(create, create);
        X448Field.addOne(create);
        X448Field.addOne(create2);
        if (!X448Field.sqrtRatioVar(create, create2, pointExt.x)) {
            return false;
        }
        X448Field.normalize(pointExt.x);
        if (i2 == 1 && X448Field.isZeroVar(pointExt.x)) {
            return false;
        }
        if (i2 != (pointExt.x[0] & 1)) {
            z2 = true;
        }
        if (z ^ z2) {
            X448Field.negate(pointExt.x, pointExt.x);
        }
        pointExtendXY(pointExt);
        return true;
    }

    private static void decodeScalar(byte[] bArr, int i, int[] iArr) {
        decode32(bArr, i, iArr, 0, 14);
    }

    private static void dom4(Xof xof, byte b, byte[] bArr) {
        byte[] bArr2 = DOM4_PREFIX;
        xof.update(bArr2, 0, bArr2.length);
        xof.update(b);
        xof.update((byte) bArr.length);
        xof.update(bArr, 0, bArr.length);
    }

    private static void encode24(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) i;
        int i3 = i2 + 1;
        bArr[i3] = (byte) (i >>> 8);
        bArr[i3 + 1] = (byte) (i >>> 16);
    }

    private static void encode32(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) i;
        int i3 = i2 + 1;
        bArr[i3] = (byte) (i >>> 8);
        int i4 = i3 + 1;
        bArr[i4] = (byte) (i >>> 16);
        bArr[i4 + 1] = (byte) (i >>> 24);
    }

    private static void encode56(long j, byte[] bArr, int i) {
        encode32((int) j, bArr, i);
        encode24((int) (j >>> 32), bArr, i + 4);
    }

    private static void encodePoint(PointExt pointExt, byte[] bArr, int i) {
        int[] create = X448Field.create();
        int[] create2 = X448Field.create();
        X448Field.inv(pointExt.z, create2);
        X448Field.mul(pointExt.x, create2, create);
        X448Field.mul(pointExt.y, create2, create2);
        X448Field.normalize(create);
        X448Field.normalize(create2);
        X448Field.encode(create2, bArr, i);
        bArr[(i + 57) - 1] = (byte) ((create[0] & 1) << 7);
    }

    public static void generatePrivateKey(SecureRandom secureRandom, byte[] bArr) {
        secureRandom.nextBytes(bArr);
    }

    public static void generatePublicKey(byte[] bArr, int i, byte[] bArr2, int i2) {
        Xof createXof = createXof();
        byte[] bArr3 = new byte[114];
        createXof.update(bArr, i, 57);
        createXof.doFinal(bArr3, 0, bArr3.length);
        byte[] bArr4 = new byte[57];
        pruneScalar(bArr3, 0, bArr4);
        scalarMultBaseEncoded(bArr4, bArr2, i2);
    }

    private static byte[] getWNAF(int[] iArr, int i) {
        int i2;
        int[] iArr2 = new int[28];
        int length = iArr2.length;
        int i3 = 0;
        int i4 = 14;
        int i5 = 0;
        while (true) {
            i4--;
            if (i4 < 0) {
                break;
            }
            int i6 = iArr[i4];
            int i7 = length - 1;
            iArr2[i7] = (i5 << 16) | (i6 >>> 16);
            length = i7 - 1;
            iArr2[length] = i6;
            i5 = i6;
        }
        byte[] bArr = new byte[448];
        int i8 = 1 << i;
        int i9 = i8 - 1;
        int i10 = i8 >>> 1;
        int i11 = 0;
        int i12 = 0;
        while (i3 < iArr2.length) {
            int i13 = iArr2[i3];
            while (i2 < 16) {
                int i14 = i13 >>> i2;
                if ((i14 & 1) == i12) {
                    i2++;
                } else {
                    int i15 = (i14 & i9) + i12;
                    int i16 = i15 & i10;
                    int i17 = i15 - (i16 << 1);
                    i12 = i16 >>> (i - 1);
                    bArr[(i3 << 4) + i2] = (byte) i17;
                    i2 += i;
                }
            }
            i3++;
            i11 = i2 - 16;
        }
        return bArr;
    }

    private static void implSign(Xof xof, byte[] bArr, byte[] bArr2, byte[] bArr3, int i, byte[] bArr4, byte b, byte[] bArr5, int i2, int i3, byte[] bArr6, int i4) {
        dom4(xof, b, bArr4);
        xof.update(bArr, 57, 57);
        xof.update(bArr5, i2, i3);
        xof.doFinal(bArr, 0, bArr.length);
        byte[] reduceScalar = reduceScalar(bArr);
        byte[] bArr7 = new byte[57];
        scalarMultBaseEncoded(reduceScalar, bArr7, 0);
        dom4(xof, b, bArr4);
        xof.update(bArr7, 0, 57);
        xof.update(bArr3, i, 57);
        xof.update(bArr5, i2, i3);
        xof.doFinal(bArr, 0, bArr.length);
        byte[] calculateS = calculateS(reduceScalar, reduceScalar(bArr), bArr2);
        System.arraycopy(bArr7, 0, bArr6, i4, 57);
        System.arraycopy(calculateS, 0, bArr6, i4 + 57, 57);
    }

    private static void implSign(byte[] bArr, int i, byte[] bArr2, byte b, byte[] bArr3, int i2, int i3, byte[] bArr4, int i4) {
        if (checkContextVar(bArr2)) {
            Xof createXof = createXof();
            byte[] bArr5 = new byte[114];
            byte[] bArr6 = bArr;
            int i5 = i;
            createXof.update(bArr, i, 57);
            createXof.doFinal(bArr5, 0, bArr5.length);
            byte[] bArr7 = new byte[57];
            pruneScalar(bArr5, 0, bArr7);
            byte[] bArr8 = new byte[57];
            scalarMultBaseEncoded(bArr7, bArr8, 0);
            implSign(createXof, bArr5, bArr7, bArr8, 0, bArr2, b, bArr3, i2, i3, bArr4, i4);
            return;
        }
        throw new IllegalArgumentException("ctx");
    }

    private static void implSign(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte b, byte[] bArr4, int i3, int i4, byte[] bArr5, int i5) {
        if (checkContextVar(bArr3)) {
            Xof createXof = createXof();
            byte[] bArr6 = new byte[114];
            byte[] bArr7 = bArr;
            int i6 = i;
            createXof.update(bArr, i, 57);
            createXof.doFinal(bArr6, 0, bArr6.length);
            byte[] bArr8 = new byte[57];
            pruneScalar(bArr6, 0, bArr8);
            implSign(createXof, bArr6, bArr8, bArr2, i2, bArr3, b, bArr4, i3, i4, bArr5, i5);
            return;
        }
        throw new IllegalArgumentException("ctx");
    }

    private static boolean implVerify(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte b, byte[] bArr4, int i3, int i4) {
        if (checkContextVar(bArr3)) {
            int i5 = i + 57;
            byte[] copyOfRange = Arrays.copyOfRange(bArr, i, i5);
            byte[] copyOfRange2 = Arrays.copyOfRange(bArr, i5, i + 114);
            if (!checkPointVar(copyOfRange) || !checkScalarVar(copyOfRange2)) {
                return false;
            }
            PointExt pointExt = new PointExt();
            if (!decodePointVar(bArr2, i2, true, pointExt)) {
                return false;
            }
            Xof createXof = createXof();
            byte[] bArr5 = new byte[114];
            dom4(createXof, b, bArr3);
            createXof.update(copyOfRange, 0, 57);
            createXof.update(bArr2, i2, 57);
            createXof.update(bArr4, i3, i4);
            createXof.doFinal(bArr5, 0, bArr5.length);
            byte[] reduceScalar = reduceScalar(bArr5);
            int[] iArr = new int[14];
            decodeScalar(copyOfRange2, 0, iArr);
            int[] iArr2 = new int[14];
            decodeScalar(reduceScalar, 0, iArr2);
            PointExt pointExt2 = new PointExt();
            scalarMultStraussVar(iArr, iArr2, pointExt, pointExt2);
            byte[] bArr6 = new byte[57];
            encodePoint(pointExt2, bArr6, 0);
            return Arrays.areEqual(bArr6, copyOfRange);
        }
        throw new IllegalArgumentException("ctx");
    }

    private static void pointAddPrecomp(PointPrecomp pointPrecomp, PointExt pointExt) {
        int[] create = X448Field.create();
        int[] create2 = X448Field.create();
        int[] create3 = X448Field.create();
        int[] create4 = X448Field.create();
        int[] create5 = X448Field.create();
        int[] create6 = X448Field.create();
        int[] create7 = X448Field.create();
        X448Field.sqr(pointExt.z, create);
        X448Field.mul(pointPrecomp.x, pointExt.x, create2);
        X448Field.mul(pointPrecomp.y, pointExt.y, create3);
        X448Field.mul(create2, create3, create4);
        X448Field.mul(create4, 39081, create4);
        X448Field.add(create, create4, create5);
        X448Field.sub(create, create4, create6);
        X448Field.add(pointPrecomp.x, pointPrecomp.y, create);
        X448Field.add(pointExt.x, pointExt.y, create4);
        X448Field.mul(create, create4, create7);
        X448Field.add(create3, create2, create);
        X448Field.sub(create3, create2, create4);
        X448Field.carry(create);
        X448Field.sub(create7, create, create7);
        X448Field.mul(create7, pointExt.z, create7);
        X448Field.mul(create4, pointExt.z, create4);
        X448Field.mul(create5, create7, pointExt.x);
        X448Field.mul(create4, create6, pointExt.y);
        X448Field.mul(create5, create6, pointExt.z);
    }

    private static void pointAddVar(boolean z, PointExt pointExt, PointExt pointExt2) {
        int[] iArr;
        int[] iArr2;
        int[] iArr3;
        int[] iArr4;
        int[] create = X448Field.create();
        int[] create2 = X448Field.create();
        int[] create3 = X448Field.create();
        int[] create4 = X448Field.create();
        int[] create5 = X448Field.create();
        int[] create6 = X448Field.create();
        int[] create7 = X448Field.create();
        int[] create8 = X448Field.create();
        if (z) {
            X448Field.sub(pointExt.y, pointExt.x, create8);
            iArr2 = create2;
            iArr3 = create5;
            iArr4 = create6;
            iArr = create7;
        } else {
            X448Field.add(pointExt.y, pointExt.x, create8);
            iArr3 = create2;
            iArr2 = create5;
            iArr = create6;
            iArr4 = create7;
        }
        X448Field.mul(pointExt.z, pointExt2.z, create);
        X448Field.sqr(create, create2);
        X448Field.mul(pointExt.x, pointExt2.x, create3);
        X448Field.mul(pointExt.y, pointExt2.y, create4);
        X448Field.mul(create3, create4, create5);
        X448Field.mul(create5, 39081, create5);
        X448Field.add(create2, create5, iArr);
        X448Field.sub(create2, create5, iArr4);
        X448Field.add(pointExt2.x, pointExt2.y, create5);
        X448Field.mul(create8, create5, create8);
        X448Field.add(create4, create3, iArr3);
        X448Field.sub(create4, create3, iArr2);
        X448Field.carry(iArr3);
        X448Field.sub(create8, create2, create8);
        X448Field.mul(create8, create, create8);
        X448Field.mul(create5, create, create5);
        X448Field.mul(create6, create8, pointExt2.x);
        X448Field.mul(create5, create7, pointExt2.y);
        X448Field.mul(create6, create7, pointExt2.z);
    }

    private static PointExt pointCopy(PointExt pointExt) {
        PointExt pointExt2 = new PointExt();
        X448Field.copy(pointExt.x, 0, pointExt2.x, 0);
        X448Field.copy(pointExt.y, 0, pointExt2.y, 0);
        X448Field.copy(pointExt.z, 0, pointExt2.z, 0);
        return pointExt2;
    }

    private static void pointDouble(PointExt pointExt) {
        int[] create = X448Field.create();
        int[] create2 = X448Field.create();
        int[] create3 = X448Field.create();
        int[] create4 = X448Field.create();
        int[] create5 = X448Field.create();
        int[] create6 = X448Field.create();
        X448Field.add(pointExt.x, pointExt.y, create);
        X448Field.sqr(create, create);
        X448Field.sqr(pointExt.x, create2);
        X448Field.sqr(pointExt.y, create3);
        X448Field.add(create2, create3, create4);
        X448Field.carry(create4);
        X448Field.sqr(pointExt.z, create5);
        X448Field.add(create5, create5, create5);
        X448Field.carry(create5);
        X448Field.sub(create4, create5, create6);
        X448Field.sub(create, create4, create);
        X448Field.sub(create2, create3, create2);
        X448Field.mul(create, create6, pointExt.x);
        X448Field.mul(create4, create2, pointExt.y);
        X448Field.mul(create4, create6, pointExt.z);
    }

    private static void pointExtendXY(PointExt pointExt) {
        X448Field.one(pointExt.z);
    }

    private static void pointLookup(int i, int i2, PointPrecomp pointPrecomp) {
        int i3 = i * 16 * 2 * 16;
        for (int i4 = 0; i4 < 16; i4++) {
            int i5 = ((i4 ^ i2) - 1) >> 31;
            Nat.cmov(16, i5, precompBase, i3, pointPrecomp.x, 0);
            int i6 = i3 + 16;
            Nat.cmov(16, i5, precompBase, i6, pointPrecomp.y, 0);
            i3 = i6 + 16;
        }
    }

    private static PointExt[] pointPrecompVar(PointExt pointExt, int i) {
        PointExt pointCopy = pointCopy(pointExt);
        pointDouble(pointCopy);
        PointExt[] pointExtArr = new PointExt[i];
        pointExtArr[0] = pointCopy(pointExt);
        for (int i2 = 1; i2 < i; i2++) {
            pointExtArr[i2] = pointCopy(pointExtArr[i2 - 1]);
            pointAddVar(false, pointCopy, pointExtArr[i2]);
        }
        return pointExtArr;
    }

    private static void pointSetNeutral(PointExt pointExt) {
        X448Field.zero(pointExt.x);
        X448Field.one(pointExt.y);
        X448Field.one(pointExt.z);
    }

    public static void precompute() {
        synchronized (precompLock) {
            if (precompBase == null) {
                PointExt pointExt = new PointExt();
                X448Field.copy(B_x, 0, pointExt.x, 0);
                X448Field.copy(B_y, 0, pointExt.y, 0);
                pointExtendXY(pointExt);
                precompBaseTable = pointPrecompVar(pointExt, 32);
                precompBase = new int[2560];
                int i = 0;
                int i2 = 0;
                while (i < 5) {
                    PointExt[] pointExtArr = new PointExt[5];
                    PointExt pointExt2 = new PointExt();
                    pointSetNeutral(pointExt2);
                    int i3 = 0;
                    while (true) {
                        if (i3 >= 5) {
                            break;
                        }
                        pointAddVar(true, pointExt, pointExt2);
                        pointDouble(pointExt);
                        pointExtArr[i3] = pointCopy(pointExt);
                        if (i + i3 != 8) {
                            for (int i4 = 1; i4 < 18; i4++) {
                                pointDouble(pointExt);
                            }
                        }
                        i3++;
                    }
                    PointExt[] pointExtArr2 = new PointExt[16];
                    pointExtArr2[0] = pointExt2;
                    int i5 = 0;
                    int i6 = 1;
                    while (i5 < 4) {
                        int i7 = 1 << i5;
                        int i8 = i6;
                        int i9 = 0;
                        while (i9 < i7) {
                            pointExtArr2[i8] = pointCopy(pointExtArr2[i8 - i7]);
                            pointAddVar(false, pointExtArr[i5], pointExtArr2[i8]);
                            i9++;
                            i8++;
                        }
                        i5++;
                        i6 = i8;
                    }
                    int i10 = i2;
                    for (int i11 = 0; i11 < 16; i11++) {
                        PointExt pointExt3 = pointExtArr2[i11];
                        X448Field.inv(pointExt3.z, pointExt3.z);
                        X448Field.mul(pointExt3.x, pointExt3.z, pointExt3.x);
                        X448Field.mul(pointExt3.y, pointExt3.z, pointExt3.y);
                        X448Field.copy(pointExt3.x, 0, precompBase, i10);
                        int i12 = i10 + 16;
                        X448Field.copy(pointExt3.y, 0, precompBase, i12);
                        i10 = i12 + 16;
                    }
                    i++;
                    i2 = i10;
                }
            }
        }
    }

    private static void pruneScalar(byte[] bArr, int i, byte[] bArr2) {
        System.arraycopy(bArr, i, bArr2, 0, 56);
        bArr2[0] = (byte) (bArr2[0] & 252);
        bArr2[55] = (byte) (bArr2[55] | 128);
        bArr2[56] = 0;
    }

    private static byte[] reduceScalar(byte[] bArr) {
        byte[] bArr2 = bArr;
        long decode24 = ((long) (decode24(bArr2, 4) << 4)) & 4294967295L;
        long decode32 = ((long) decode32(bArr2, 7)) & 4294967295L;
        long decode242 = ((long) (decode24(bArr2, 11) << 4)) & 4294967295L;
        long decode322 = ((long) decode32(bArr2, 14)) & 4294967295L;
        long decode243 = ((long) (decode24(bArr2, 18) << 4)) & 4294967295L;
        long decode323 = ((long) decode32(bArr2, 21)) & 4294967295L;
        long decode244 = ((long) (decode24(bArr2, 25) << 4)) & 4294967295L;
        long decode324 = ((long) decode32(bArr2, 28)) & 4294967295L;
        long decode245 = ((long) (decode24(bArr2, 32) << 4)) & 4294967295L;
        long decode325 = ((long) decode32(bArr2, 35)) & 4294967295L;
        long decode246 = ((long) (decode24(bArr2, 39) << 4)) & 4294967295L;
        long decode326 = ((long) decode32(bArr2, 42)) & 4294967295L;
        long decode247 = ((long) (decode24(bArr2, 46) << 4)) & 4294967295L;
        long decode327 = ((long) decode32(bArr2, 49)) & 4294967295L;
        long decode248 = ((long) (decode24(bArr2, 53) << 4)) & 4294967295L;
        long decode249 = ((long) (decode24(bArr2, 74) << 4)) & 4294967295L;
        long decode328 = ((long) decode32(bArr2, 77)) & 4294967295L;
        long decode2410 = ((long) (decode24(bArr2, 81) << 4)) & 4294967295L;
        long decode329 = ((long) decode32(bArr2, 84)) & 4294967295L;
        long decode2411 = ((long) (decode24(bArr2, 88) << 4)) & 4294967295L;
        long decode3210 = ((long) decode32(bArr2, 91)) & 4294967295L;
        long decode2412 = ((long) (decode24(bArr2, 95) << 4)) & 4294967295L;
        long decode3211 = ((long) decode32(bArr2, 98)) & 4294967295L;
        long decode2413 = ((long) (decode24(bArr2, 102) << 4)) & 4294967295L;
        long decode3212 = ((long) decode32(bArr2, 105)) & 4294967295L;
        long decode16 = ((long) decode16(bArr2, 112)) & 4294967295L;
        long j = decode2410 + (decode16 * 550336261);
        long decode2414 = (((long) (decode24(bArr2, 109) << 4)) & 4294967295L) + (decode3212 >>> 28);
        long j2 = decode3212 & M28L;
        long j3 = decode328 + (decode16 * 149865618) + (decode2414 * 550336261);
        long j4 = decode2413 + (decode3211 >>> 28);
        long j5 = decode3211 & M28L;
        long decode3213 = (((long) decode32(bArr2, 70)) & 4294967295L) + (decode16 * 96434764) + (decode2414 * 227822194) + (j2 * 149865618) + (j4 * 550336261);
        long j6 = decode2412 + (decode3210 >>> 28);
        long j7 = decode3210 & M28L;
        long decode3214 = (((long) decode32(bArr2, 63)) & 4294967295L) + (decode16 * 163752818) + (decode2414 * 258169998) + (j2 * 96434764) + (j4 * 227822194) + (j5 * 149865618) + (j6 * 550336261);
        long decode2415 = (((long) (decode24(bArr2, 60) << 4)) & 4294967295L) + (decode16 * 30366549) + (decode2414 * 163752818) + (j2 * 258169998) + (j4 * 96434764) + (j5 * 227822194) + (j6 * 149865618) + (j7 * 550336261);
        long j8 = decode2411 + (decode329 >>> 28);
        long j9 = decode249 + (decode16 * 227822194) + (decode2414 * 149865618) + (j2 * 550336261) + (decode3213 >>> 28);
        long j10 = j3 + (j9 >>> 28);
        long j11 = j + (j10 >>> 28);
        long j12 = j10 & M28L;
        long j13 = (decode329 & M28L) + (j11 >>> 28);
        long j14 = j11 & M28L;
        long j15 = decode244 + (j14 * 43969588);
        long j16 = decode324 + (j13 * 43969588) + (j14 * 30366549);
        long j17 = decode245 + (j8 * 43969588) + (j13 * 30366549) + (j14 * 163752818);
        long j18 = decode325 + (j7 * 43969588) + (j8 * 30366549) + (j13 * 163752818) + (j14 * 258169998);
        long j19 = decode246 + (j6 * 43969588) + (j7 * 30366549) + (j8 * 163752818) + (j13 * 258169998) + (j14 * 96434764);
        long j20 = decode326 + (j5 * 43969588) + (j6 * 30366549) + (j7 * 163752818) + (j8 * 258169998) + (j13 * 96434764) + (j14 * 227822194);
        long j21 = decode327 + (j2 * 43969588) + (j4 * 30366549) + (j5 * 163752818) + (j6 * 258169998) + (j7 * 96434764) + (j8 * 227822194) + (j13 * 149865618) + (j14 * 550336261);
        long j22 = decode3214 + (decode2415 >>> 28);
        long decode2416 = (((long) (decode24(bArr2, 67) << 4)) & 4294967295L) + (decode16 * 258169998) + (decode2414 * 96434764) + (j2 * 227822194) + (j4 * 149865618) + (j5 * 550336261) + (j22 >>> 28);
        long j23 = (decode3213 & M28L) + (decode2416 >>> 28);
        long j24 = decode2416 & M28L;
        long j25 = (j9 & M28L) + (j23 >>> 28);
        long j26 = j23 & M28L;
        long j27 = decode322 + (j26 * 43969588);
        long j28 = decode243 + (j25 * 43969588) + (j26 * 30366549);
        long j29 = decode323 + (j12 * 43969588) + (j25 * 30366549) + (j26 * 163752818);
        long j30 = j15 + (j12 * 30366549) + (j25 * 163752818) + (j26 * 258169998);
        long j31 = j16 + (j12 * 163752818) + (j25 * 258169998) + (j26 * 96434764);
        long j32 = j17 + (j12 * 258169998) + (j25 * 96434764) + (j26 * 227822194);
        long j33 = j19 + (j12 * 227822194) + (j25 * 149865618) + (j26 * 550336261);
        long j34 = decode242 + (j24 * 43969588);
        long j35 = j18 + (j12 * 96434764) + (j25 * 227822194) + (j26 * 149865618) + (j24 * 550336261);
        long j36 = decode248 + (decode2414 * 43969588) + (j2 * 30366549) + (j4 * 163752818) + (j5 * 258169998) + (j6 * 96434764) + (j7 * 227822194) + (j8 * 149865618) + (j13 * 550336261) + (j21 >>> 28);
        long decode3215 = (((long) decode32(bArr2, 56)) & 4294967295L) + (decode16 * 43969588) + (decode2414 * 30366549) + (j2 * 163752818) + (j4 * 258169998) + (j5 * 96434764) + (j6 * 227822194) + (j7 * 149865618) + (j8 * 550336261) + (j36 >>> 28);
        long j37 = j36 & M28L;
        long j38 = (decode2415 & M28L) + (decode3215 >>> 28);
        long j39 = (j22 & M28L) + (j38 >>> 28);
        long j40 = j38 & M28L;
        long j41 = decode32 + (j39 * 43969588);
        long j42 = j34 + (j39 * 30366549);
        long j43 = j27 + (j24 * 30366549) + (j39 * 163752818);
        long j44 = j28 + (j24 * 163752818) + (j39 * 258169998);
        long j45 = j29 + (j24 * 258169998) + (j39 * 96434764);
        long j46 = j30 + (j24 * 96434764) + (j39 * 227822194);
        long j47 = j32 + (j24 * 149865618) + (j39 * 550336261);
        long j48 = j31 + (j24 * 227822194) + (j39 * 149865618) + (j40 * 550336261);
        long j49 = j37 & M26L;
        long j50 = ((decode3215 & M28L) * 4) + (j37 >>> 26) + 1;
        long decode3216 = (((long) decode32(bArr2, 0)) & 4294967295L) + (78101261 * j50);
        long j51 = decode24 + (43969588 * j40) + (141809365 * j50) + (decode3216 >>> 28);
        long j52 = j41 + (30366549 * j40) + (175155932 * j50) + (j51 >>> 28);
        long j53 = j42 + (163752818 * j40) + (64542499 * j50) + (j52 >>> 28);
        long j54 = j52 & M28L;
        long j55 = j43 + (258169998 * j40) + (158326419 * j50) + (j53 >>> 28);
        long j56 = j44 + (96434764 * j40) + (191173276 * j50) + (j55 >>> 28);
        long j57 = j55 & M28L;
        long j58 = j45 + (227822194 * j40) + (104575268 * j50) + (j56 >>> 28);
        long j59 = j56 & M28L;
        long j60 = j46 + (149865618 * j40) + (j50 * 137584065) + (j58 >>> 28);
        long j61 = j48 + (j60 >>> 28);
        long j62 = j47 + (j61 >>> 28);
        long j63 = j35 + (j62 >>> 28);
        long j64 = j62 & M28L;
        long j65 = j33 + (j63 >>> 28);
        long j66 = j20 + (j12 * 149865618) + (j25 * 550336261) + (j65 >>> 28);
        long j67 = decode247 + (j4 * 43969588) + (j5 * 30366549) + (j6 * 163752818) + (j7 * 258169998) + (j8 * 96434764) + (j13 * 227822194) + (j14 * 149865618) + (j12 * 550336261) + (j66 >>> 28);
        long j68 = (j21 & M28L) + (j67 >>> 28);
        long j69 = j49 + (j68 >>> 28);
        long j70 = (j69 >>> 26) - 1;
        long j71 = (decode3216 & M28L) - (j70 & 78101261);
        long j72 = ((j51 & M28L) - (j70 & 141809365)) + (j71 >> 28);
        long j73 = (j54 - (j70 & 175155932)) + (j72 >> 28);
        long j74 = ((j53 & M28L) - (j70 & 64542499)) + (j73 >> 28);
        long j75 = j73 & M28L;
        long j76 = (j57 - (j70 & 158326419)) + (j74 >> 28);
        long j77 = j74 & M28L;
        long j78 = (j59 - (j70 & 191173276)) + (j76 >> 28);
        long j79 = j76 & M28L;
        long j80 = ((j58 & M28L) - (j70 & 104575268)) + (j78 >> 28);
        long j81 = j78 & M28L;
        long j82 = ((j60 & M28L) - (j70 & 137584065)) + (j80 >> 28);
        long j83 = j80 & M28L;
        long j84 = (j61 & M28L) + (j82 >> 28);
        long j85 = j82 & M28L;
        long j86 = j64 + (j84 >> 28);
        long j87 = j84 & M28L;
        long j88 = (j63 & M28L) + (j86 >> 28);
        long j89 = j86 & M28L;
        long j90 = (j65 & M28L) + (j88 >> 28);
        long j91 = j88 & M28L;
        long j92 = (j66 & M28L) + (j90 >> 28);
        long j93 = j90 & M28L;
        long j94 = (j67 & M28L) + (j92 >> 28);
        long j95 = j92 & M28L;
        long j96 = (j68 & M28L) + (j94 >> 28);
        long j97 = j94 & M28L;
        long j98 = (j69 & M26L) + (j96 >> 28);
        long j99 = j96 & M28L;
        byte[] bArr3 = new byte[57];
        encode56(((j72 & M28L) << 28) | (j71 & M28L), bArr3, 0);
        encode56((j77 << 28) | j75, bArr3, 7);
        encode56(j79 | (j81 << 28), bArr3, 14);
        encode56(j83 | (j85 << 28), bArr3, 21);
        encode56(j87 | (j89 << 28), bArr3, 28);
        encode56(j91 | (j93 << 28), bArr3, 35);
        encode56(j95 | (j97 << 28), bArr3, 42);
        encode56((j98 << 28) | j99, bArr3, 49);
        return bArr3;
    }

    private static void scalarMultBase(byte[] bArr, PointExt pointExt) {
        precompute();
        pointSetNeutral(pointExt);
        int[] iArr = new int[15];
        decodeScalar(bArr, 0, iArr);
        iArr[14] = Nat.cadd(14, (~iArr[0]) & 1, iArr, L, iArr) + 4;
        Nat.shiftDownBit(iArr.length, iArr, 0);
        PointPrecomp pointPrecomp = new PointPrecomp();
        int i = 17;
        while (true) {
            int i2 = 0;
            int i3 = i;
            while (i2 < 5) {
                int i4 = 0;
                int i5 = i3;
                for (int i6 = 0; i6 < 5; i6++) {
                    i4 = (i4 & (~(1 << i6))) ^ ((iArr[i5 >>> 5] >>> (i5 & 31)) << i6);
                    i5 += 18;
                }
                int i7 = (i4 >>> 4) & 1;
                pointLookup(i2, ((-i7) ^ i4) & 15, pointPrecomp);
                X448Field.cnegate(i7, pointPrecomp.x);
                pointAddPrecomp(pointPrecomp, pointExt);
                i2++;
                i3 = i5;
            }
            i--;
            if (i >= 0) {
                pointDouble(pointExt);
            } else {
                return;
            }
        }
    }

    private static void scalarMultBaseEncoded(byte[] bArr, byte[] bArr2, int i) {
        PointExt pointExt = new PointExt();
        scalarMultBase(bArr, pointExt);
        encodePoint(pointExt, bArr2, i);
    }

    public static void scalarMultBaseXY(Friend friend, byte[] bArr, int i, int[] iArr, int[] iArr2) {
        if (friend != null) {
            byte[] bArr2 = new byte[57];
            pruneScalar(bArr, i, bArr2);
            PointExt pointExt = new PointExt();
            scalarMultBase(bArr2, pointExt);
            X448Field.copy(pointExt.x, 0, iArr, 0);
            X448Field.copy(pointExt.y, 0, iArr2, 0);
            return;
        }
        throw new NullPointerException("This method is only for use by X448");
    }

    private static void scalarMultStraussVar(int[] iArr, int[] iArr2, PointExt pointExt, PointExt pointExt2) {
        precompute();
        byte[] wnaf = getWNAF(iArr, 7);
        byte[] wnaf2 = getWNAF(iArr2, 5);
        PointExt[] pointPrecompVar = pointPrecompVar(pointExt, 8);
        pointSetNeutral(pointExt2);
        int i = 447;
        while (i > 0 && (wnaf[i] | wnaf2[i]) == 0) {
            i--;
        }
        while (true) {
            byte b = wnaf[i];
            boolean z = false;
            if (b != 0) {
                int i2 = b >> 31;
                pointAddVar(i2 != 0, precompBaseTable[(b ^ i2) >>> 1], pointExt2);
            }
            byte b2 = wnaf2[i];
            if (b2 != 0) {
                int i3 = b2 >> 31;
                int i4 = (b2 ^ i3) >>> 1;
                if (i3 != 0) {
                    z = true;
                }
                pointAddVar(z, pointPrecompVar[i4], pointExt2);
            }
            i--;
            if (i >= 0) {
                pointDouble(pointExt2);
            } else {
                return;
            }
        }
    }

    public static void sign(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4, int i3, int i4, byte[] bArr5, int i5) {
        implSign(bArr, i, bArr2, i2, bArr3, 0, bArr4, i3, i4, bArr5, i5);
    }

    public static void sign(byte[] bArr, int i, byte[] bArr2, byte[] bArr3, int i2, int i3, byte[] bArr4, int i4) {
        implSign(bArr, i, bArr2, 0, bArr3, i2, i3, bArr4, i4);
    }

    public static void signPrehash(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, Xof xof, byte[] bArr4, int i3) {
        byte[] bArr5 = new byte[64];
        if (64 == xof.doFinal(bArr5, 0, 64)) {
            implSign(bArr, i, bArr2, i2, bArr3, 1, bArr5, 0, bArr5.length, bArr4, i3);
            return;
        }
        throw new IllegalArgumentException("ph");
    }

    public static void signPrehash(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4, int i3, byte[] bArr5, int i4) {
        implSign(bArr, i, bArr2, i2, bArr3, 1, bArr4, i3, 64, bArr5, i4);
    }

    public static void signPrehash(byte[] bArr, int i, byte[] bArr2, Xof xof, byte[] bArr3, int i2) {
        byte[] bArr4 = new byte[64];
        if (64 == xof.doFinal(bArr4, 0, 64)) {
            implSign(bArr, i, bArr2, 1, bArr4, 0, bArr4.length, bArr3, i2);
            return;
        }
        throw new IllegalArgumentException("ph");
    }

    public static void signPrehash(byte[] bArr, int i, byte[] bArr2, byte[] bArr3, int i2, byte[] bArr4, int i3) {
        implSign(bArr, i, bArr2, 1, bArr3, i2, 64, bArr4, i3);
    }

    public static boolean verify(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4, int i3, int i4) {
        return implVerify(bArr, i, bArr2, i2, bArr3, 0, bArr4, i3, i4);
    }

    public static boolean verifyPrehash(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, Xof xof) {
        byte[] bArr4 = new byte[64];
        if (64 == xof.doFinal(bArr4, 0, 64)) {
            return implVerify(bArr, i, bArr2, i2, bArr3, 1, bArr4, 0, bArr4.length);
        }
        throw new IllegalArgumentException("ph");
    }

    public static boolean verifyPrehash(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4, int i3) {
        return implVerify(bArr, i, bArr2, i2, bArr3, 1, bArr4, i3, 64);
    }
}
