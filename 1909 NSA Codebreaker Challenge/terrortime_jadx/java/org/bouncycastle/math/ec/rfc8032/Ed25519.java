package org.bouncycastle.math.ec.rfc8032;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.math.ec.rfc7748.X25519.Friend;
import org.bouncycastle.math.ec.rfc7748.X25519Field;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class Ed25519 {
    private static final int[] B_x = {52811034, 25909283, 8072341, 50637101, 13785486, 30858332, 20483199, 20966410, 43936626, 4379245};
    private static final int[] B_y = {40265304, 26843545, 6710886, 53687091, 13421772, 40265318, 26843545, 6710886, 53687091, 13421772};
    private static final int[] C_d = {56195235, 47411844, 25868126, 40503822, 57364, 58321048, 30416477, 31930572, 57760639, 10749657};
    private static final int[] C_d2 = {45281625, 27714825, 18181821, 13898781, 114729, 49533232, 60832955, 30306712, 48412415, 4722099};
    private static final int[] C_d4 = {23454386, 55429651, 2809210, 27797563, 229458, 31957600, 54557047, 27058993, 29715967, 9444199};
    private static final byte[] DOM2_PREFIX = Strings.toByteArray("SigEd25519 no Ed25519 collisions");
    private static final int[] L = {1559614445, 1477600026, -1560830762, 350157278, 0, 0, 0, 268435456};
    private static final int L0 = -50998291;
    private static final int L1 = 19280294;
    private static final int L2 = 127719000;
    private static final int L3 = -6428113;
    private static final int L4 = 5343;
    private static final long M28L = 268435455;
    private static final long M32L = 4294967295L;
    private static final int[] P = {-19, -1, -1, -1, -1, -1, -1, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED};
    private static final int POINT_BYTES = 32;
    private static final int PRECOMP_BLOCKS = 8;
    private static final int PRECOMP_MASK = 7;
    private static final int PRECOMP_POINTS = 8;
    private static final int PRECOMP_SPACING = 8;
    private static final int PRECOMP_TEETH = 4;
    public static final int PREHASH_SIZE = 64;
    public static final int PUBLIC_KEY_SIZE = 32;
    private static final int SCALAR_BYTES = 32;
    private static final int SCALAR_INTS = 8;
    public static final int SECRET_KEY_SIZE = 32;
    public static final int SIGNATURE_SIZE = 64;
    private static final int WNAF_WIDTH_BASE = 7;
    private static int[] precompBase = null;
    private static PointExt[] precompBaseTable = null;
    private static Object precompLock = new Object();

    public static final class Algorithm {
        public static final int Ed25519 = 0;
        public static final int Ed25519ctx = 1;
        public static final int Ed25519ph = 2;
    }

    private static class PointAccum {
        int[] u;
        int[] v;
        int[] x;
        int[] y;
        int[] z;

        private PointAccum() {
            this.x = X25519Field.create();
            this.y = X25519Field.create();
            this.z = X25519Field.create();
            this.u = X25519Field.create();
            this.v = X25519Field.create();
        }
    }

    private static class PointExt {
        int[] t;
        int[] x;
        int[] y;
        int[] z;

        private PointExt() {
            this.x = X25519Field.create();
            this.y = X25519Field.create();
            this.z = X25519Field.create();
            this.t = X25519Field.create();
        }
    }

    private static class PointPrecomp {
        int[] xyd;
        int[] ymx_h;
        int[] ypx_h;

        private PointPrecomp() {
            this.ypx_h = X25519Field.create();
            this.ymx_h = X25519Field.create();
            this.xyd = X25519Field.create();
        }
    }

    private static byte[] calculateS(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        int[] iArr = new int[16];
        decodeScalar(bArr, 0, iArr);
        int[] iArr2 = new int[8];
        decodeScalar(bArr2, 0, iArr2);
        int[] iArr3 = new int[8];
        decodeScalar(bArr3, 0, iArr3);
        Nat256.mulAddTo(iArr2, iArr3, iArr);
        byte[] bArr4 = new byte[64];
        for (int i = 0; i < iArr.length; i++) {
            encode32(iArr[i], bArr4, i * 4);
        }
        return reduceScalar(bArr4);
    }

    private static boolean checkContextVar(byte[] bArr, byte b) {
        return (bArr == null && b == 0) || (bArr != null && bArr.length < 256);
    }

    private static boolean checkPointVar(byte[] bArr) {
        int[] iArr = new int[8];
        decode32(bArr, 0, iArr, 0, 8);
        iArr[7] = iArr[7] & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        return !Nat256.gte(iArr, P);
    }

    private static boolean checkScalarVar(byte[] bArr) {
        int[] iArr = new int[8];
        decodeScalar(bArr, 0, iArr);
        return !Nat256.gte(iArr, L);
    }

    private static Digest createDigest() {
        return new SHA512Digest();
    }

    public static Digest createPrehash() {
        return createDigest();
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
        byte[] copyOfRange = Arrays.copyOfRange(bArr, i, i + 32);
        boolean z2 = false;
        if (!checkPointVar(copyOfRange)) {
            return false;
        }
        int i2 = (copyOfRange[31] & 128) >>> 7;
        copyOfRange[31] = (byte) (copyOfRange[31] & Byte.MAX_VALUE);
        X25519Field.decode(copyOfRange, 0, pointExt.y);
        int[] create = X25519Field.create();
        int[] create2 = X25519Field.create();
        X25519Field.sqr(pointExt.y, create);
        X25519Field.mul(C_d, create, create2);
        X25519Field.subOne(create);
        X25519Field.addOne(create2);
        if (!X25519Field.sqrtRatioVar(create, create2, pointExt.x)) {
            return false;
        }
        X25519Field.normalize(pointExt.x);
        if (i2 == 1 && X25519Field.isZeroVar(pointExt.x)) {
            return false;
        }
        if (i2 != (pointExt.x[0] & 1)) {
            z2 = true;
        }
        if (z ^ z2) {
            X25519Field.negate(pointExt.x, pointExt.x);
        }
        pointExtendXY(pointExt);
        return true;
    }

    private static void decodeScalar(byte[] bArr, int i, int[] iArr) {
        decode32(bArr, i, iArr, 0, 8);
    }

    private static void dom2(Digest digest, byte b, byte[] bArr) {
        if (bArr != null) {
            byte[] bArr2 = DOM2_PREFIX;
            digest.update(bArr2, 0, bArr2.length);
            digest.update(b);
            digest.update((byte) bArr.length);
            digest.update(bArr, 0, bArr.length);
        }
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

    private static void encodePoint(PointAccum pointAccum, byte[] bArr, int i) {
        int[] create = X25519Field.create();
        int[] create2 = X25519Field.create();
        X25519Field.inv(pointAccum.z, create2);
        X25519Field.mul(pointAccum.x, create2, create);
        X25519Field.mul(pointAccum.y, create2, create2);
        X25519Field.normalize(create);
        X25519Field.normalize(create2);
        X25519Field.encode(create2, bArr, i);
        int i2 = (i + 32) - 1;
        bArr[i2] = (byte) (bArr[i2] | ((create[0] & 1) << 7));
    }

    public static void generatePrivateKey(SecureRandom secureRandom, byte[] bArr) {
        secureRandom.nextBytes(bArr);
    }

    public static void generatePublicKey(byte[] bArr, int i, byte[] bArr2, int i2) {
        Digest createDigest = createDigest();
        byte[] bArr3 = new byte[createDigest.getDigestSize()];
        createDigest.update(bArr, i, 32);
        createDigest.doFinal(bArr3, 0);
        byte[] bArr4 = new byte[32];
        pruneScalar(bArr3, 0, bArr4);
        scalarMultBaseEncoded(bArr4, bArr2, i2);
    }

    private static byte[] getWNAF(int[] iArr, int i) {
        int i2;
        int[] iArr2 = new int[16];
        int length = iArr2.length;
        int i3 = 0;
        int i4 = 8;
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
        byte[] bArr = new byte[256];
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

    private static void implSign(Digest digest, byte[] bArr, byte[] bArr2, byte[] bArr3, int i, byte[] bArr4, byte b, byte[] bArr5, int i2, int i3, byte[] bArr6, int i4) {
        dom2(digest, b, bArr4);
        digest.update(bArr, 32, 32);
        digest.update(bArr5, i2, i3);
        digest.doFinal(bArr, 0);
        byte[] reduceScalar = reduceScalar(bArr);
        byte[] bArr7 = new byte[32];
        scalarMultBaseEncoded(reduceScalar, bArr7, 0);
        dom2(digest, b, bArr4);
        digest.update(bArr7, 0, 32);
        digest.update(bArr3, i, 32);
        digest.update(bArr5, i2, i3);
        digest.doFinal(bArr, 0);
        byte[] calculateS = calculateS(reduceScalar, reduceScalar(bArr), bArr2);
        System.arraycopy(bArr7, 0, bArr6, i4, 32);
        System.arraycopy(calculateS, 0, bArr6, i4 + 32, 32);
    }

    private static void implSign(byte[] bArr, int i, byte[] bArr2, byte b, byte[] bArr3, int i2, int i3, byte[] bArr4, int i4) {
        if (checkContextVar(bArr2, b)) {
            Digest createDigest = createDigest();
            byte[] bArr5 = new byte[createDigest.getDigestSize()];
            byte[] bArr6 = bArr;
            int i5 = i;
            createDigest.update(bArr, i, 32);
            createDigest.doFinal(bArr5, 0);
            byte[] bArr7 = new byte[32];
            pruneScalar(bArr5, 0, bArr7);
            byte[] bArr8 = new byte[32];
            scalarMultBaseEncoded(bArr7, bArr8, 0);
            implSign(createDigest, bArr5, bArr7, bArr8, 0, bArr2, b, bArr3, i2, i3, bArr4, i4);
            return;
        }
        throw new IllegalArgumentException("ctx");
    }

    private static void implSign(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte b, byte[] bArr4, int i3, int i4, byte[] bArr5, int i5) {
        if (checkContextVar(bArr3, b)) {
            Digest createDigest = createDigest();
            byte[] bArr6 = new byte[createDigest.getDigestSize()];
            byte[] bArr7 = bArr;
            int i6 = i;
            createDigest.update(bArr, i, 32);
            createDigest.doFinal(bArr6, 0);
            byte[] bArr8 = new byte[32];
            pruneScalar(bArr6, 0, bArr8);
            implSign(createDigest, bArr6, bArr8, bArr2, i2, bArr3, b, bArr4, i3, i4, bArr5, i5);
            return;
        }
        throw new IllegalArgumentException("ctx");
    }

    private static boolean implVerify(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte b, byte[] bArr4, int i3, int i4) {
        if (checkContextVar(bArr3, b)) {
            int i5 = i + 32;
            byte[] copyOfRange = Arrays.copyOfRange(bArr, i, i5);
            byte[] copyOfRange2 = Arrays.copyOfRange(bArr, i5, i + 64);
            if (!checkPointVar(copyOfRange) || !checkScalarVar(copyOfRange2)) {
                return false;
            }
            PointExt pointExt = new PointExt();
            if (!decodePointVar(bArr2, i2, true, pointExt)) {
                return false;
            }
            Digest createDigest = createDigest();
            byte[] bArr5 = new byte[createDigest.getDigestSize()];
            dom2(createDigest, b, bArr3);
            createDigest.update(copyOfRange, 0, 32);
            createDigest.update(bArr2, i2, 32);
            createDigest.update(bArr4, i3, i4);
            createDigest.doFinal(bArr5, 0);
            byte[] reduceScalar = reduceScalar(bArr5);
            int[] iArr = new int[8];
            decodeScalar(copyOfRange2, 0, iArr);
            int[] iArr2 = new int[8];
            decodeScalar(reduceScalar, 0, iArr2);
            PointAccum pointAccum = new PointAccum();
            scalarMultStraussVar(iArr, iArr2, pointExt, pointAccum);
            byte[] bArr6 = new byte[32];
            encodePoint(pointAccum, bArr6, 0);
            return Arrays.areEqual(bArr6, copyOfRange);
        }
        throw new IllegalArgumentException("ctx");
    }

    private static void pointAddPrecomp(PointPrecomp pointPrecomp, PointAccum pointAccum) {
        int[] create = X25519Field.create();
        int[] create2 = X25519Field.create();
        int[] create3 = X25519Field.create();
        int[] iArr = pointAccum.u;
        int[] create4 = X25519Field.create();
        int[] create5 = X25519Field.create();
        int[] iArr2 = pointAccum.v;
        X25519Field.apm(pointAccum.y, pointAccum.x, create2, create);
        X25519Field.mul(create, pointPrecomp.ymx_h, create);
        X25519Field.mul(create2, pointPrecomp.ypx_h, create2);
        X25519Field.mul(pointAccum.u, pointAccum.v, create3);
        X25519Field.mul(create3, pointPrecomp.xyd, create3);
        X25519Field.apm(create2, create, iArr2, iArr);
        X25519Field.apm(pointAccum.z, create3, create5, create4);
        X25519Field.carry(create5);
        X25519Field.mul(iArr, create4, pointAccum.x);
        X25519Field.mul(create5, iArr2, pointAccum.y);
        X25519Field.mul(create4, create5, pointAccum.z);
    }

    private static void pointAddVar(boolean z, PointExt pointExt, PointAccum pointAccum) {
        int[] iArr;
        int[] iArr2;
        int[] iArr3;
        int[] iArr4;
        int[] create = X25519Field.create();
        int[] create2 = X25519Field.create();
        int[] create3 = X25519Field.create();
        int[] create4 = X25519Field.create();
        int[] iArr5 = pointAccum.u;
        int[] create5 = X25519Field.create();
        int[] create6 = X25519Field.create();
        int[] iArr6 = pointAccum.v;
        if (z) {
            iArr = create3;
            iArr4 = create4;
            iArr3 = create5;
            iArr2 = create6;
        } else {
            iArr4 = create3;
            iArr = create4;
            iArr2 = create5;
            iArr3 = create6;
        }
        X25519Field.apm(pointAccum.y, pointAccum.x, create2, create);
        X25519Field.apm(pointExt.y, pointExt.x, iArr, iArr4);
        X25519Field.mul(create, create3, create);
        X25519Field.mul(create2, create4, create2);
        X25519Field.mul(pointAccum.u, pointAccum.v, create3);
        X25519Field.mul(create3, pointExt.t, create3);
        X25519Field.mul(create3, C_d2, create3);
        X25519Field.mul(pointAccum.z, pointExt.z, create4);
        X25519Field.add(create4, create4, create4);
        X25519Field.apm(create2, create, iArr6, iArr5);
        X25519Field.apm(create4, create3, iArr3, iArr2);
        X25519Field.carry(iArr3);
        X25519Field.mul(iArr5, create5, pointAccum.x);
        X25519Field.mul(create6, iArr6, pointAccum.y);
        X25519Field.mul(create5, create6, pointAccum.z);
    }

    private static void pointAddVar(boolean z, PointExt pointExt, PointExt pointExt2, PointExt pointExt3) {
        int[] iArr;
        int[] iArr2;
        int[] iArr3;
        int[] iArr4;
        PointExt pointExt4 = pointExt;
        PointExt pointExt5 = pointExt2;
        PointExt pointExt6 = pointExt3;
        int[] create = X25519Field.create();
        int[] create2 = X25519Field.create();
        int[] create3 = X25519Field.create();
        int[] create4 = X25519Field.create();
        int[] create5 = X25519Field.create();
        int[] create6 = X25519Field.create();
        int[] create7 = X25519Field.create();
        int[] create8 = X25519Field.create();
        if (z) {
            iArr4 = create3;
            iArr3 = create4;
            iArr2 = create6;
            iArr = create7;
        } else {
            iArr3 = create3;
            iArr4 = create4;
            iArr = create6;
            iArr2 = create7;
        }
        int[] iArr5 = create7;
        X25519Field.apm(pointExt4.y, pointExt4.x, create2, create);
        X25519Field.apm(pointExt5.y, pointExt5.x, iArr4, iArr3);
        X25519Field.mul(create, create3, create);
        X25519Field.mul(create2, create4, create2);
        X25519Field.mul(pointExt4.t, pointExt5.t, create3);
        X25519Field.mul(create3, C_d2, create3);
        X25519Field.mul(pointExt4.z, pointExt5.z, create4);
        X25519Field.add(create4, create4, create4);
        X25519Field.apm(create2, create, create8, create5);
        X25519Field.apm(create4, create3, iArr2, iArr);
        X25519Field.carry(iArr2);
        X25519Field.mul(create5, create6, pointExt6.x);
        int[] iArr6 = iArr5;
        X25519Field.mul(iArr6, create8, pointExt6.y);
        X25519Field.mul(create6, iArr6, pointExt6.z);
        X25519Field.mul(create5, create8, pointExt6.t);
    }

    private static PointExt pointCopy(PointAccum pointAccum) {
        PointExt pointExt = new PointExt();
        X25519Field.copy(pointAccum.x, 0, pointExt.x, 0);
        X25519Field.copy(pointAccum.y, 0, pointExt.y, 0);
        X25519Field.copy(pointAccum.z, 0, pointExt.z, 0);
        X25519Field.mul(pointAccum.u, pointAccum.v, pointExt.t);
        return pointExt;
    }

    private static PointExt pointCopy(PointExt pointExt) {
        PointExt pointExt2 = new PointExt();
        X25519Field.copy(pointExt.x, 0, pointExt2.x, 0);
        X25519Field.copy(pointExt.y, 0, pointExt2.y, 0);
        X25519Field.copy(pointExt.z, 0, pointExt2.z, 0);
        X25519Field.copy(pointExt.t, 0, pointExt2.t, 0);
        return pointExt2;
    }

    private static void pointDouble(PointAccum pointAccum) {
        int[] create = X25519Field.create();
        int[] create2 = X25519Field.create();
        int[] create3 = X25519Field.create();
        int[] iArr = pointAccum.u;
        int[] create4 = X25519Field.create();
        int[] create5 = X25519Field.create();
        int[] iArr2 = pointAccum.v;
        X25519Field.sqr(pointAccum.x, create);
        X25519Field.sqr(pointAccum.y, create2);
        X25519Field.sqr(pointAccum.z, create3);
        X25519Field.add(create3, create3, create3);
        X25519Field.apm(create, create2, iArr2, create5);
        X25519Field.add(pointAccum.x, pointAccum.y, iArr);
        X25519Field.sqr(iArr, iArr);
        X25519Field.sub(iArr2, iArr, iArr);
        X25519Field.add(create3, create5, create4);
        X25519Field.carry(create4);
        X25519Field.mul(iArr, create4, pointAccum.x);
        X25519Field.mul(create5, iArr2, pointAccum.y);
        X25519Field.mul(create4, create5, pointAccum.z);
    }

    private static void pointExtendXY(PointAccum pointAccum) {
        X25519Field.one(pointAccum.z);
        X25519Field.copy(pointAccum.x, 0, pointAccum.u, 0);
        X25519Field.copy(pointAccum.y, 0, pointAccum.v, 0);
    }

    private static void pointExtendXY(PointExt pointExt) {
        X25519Field.one(pointExt.z);
        X25519Field.mul(pointExt.x, pointExt.y, pointExt.t);
    }

    private static void pointLookup(int i, int i2, PointPrecomp pointPrecomp) {
        int i3 = i * 8 * 3 * 10;
        for (int i4 = 0; i4 < 8; i4++) {
            int i5 = ((i4 ^ i2) - 1) >> 31;
            Nat.cmov(10, i5, precompBase, i3, pointPrecomp.ypx_h, 0);
            int i6 = i3 + 10;
            int i7 = i5;
            Nat.cmov(10, i7, precompBase, i6, pointPrecomp.ymx_h, 0);
            int i8 = i6 + 10;
            Nat.cmov(10, i7, precompBase, i8, pointPrecomp.xyd, 0);
            i3 = i8 + 10;
        }
    }

    private static PointExt[] pointPrecompVar(PointExt pointExt, int i) {
        PointExt pointExt2 = new PointExt();
        pointAddVar(false, pointExt, pointExt, pointExt2);
        PointExt[] pointExtArr = new PointExt[i];
        pointExtArr[0] = pointCopy(pointExt);
        for (int i2 = 1; i2 < i; i2++) {
            PointExt pointExt3 = pointExtArr[i2 - 1];
            PointExt pointExt4 = new PointExt();
            pointExtArr[i2] = pointExt4;
            pointAddVar(false, pointExt3, pointExt2, pointExt4);
        }
        return pointExtArr;
    }

    private static void pointSetNeutral(PointAccum pointAccum) {
        X25519Field.zero(pointAccum.x);
        X25519Field.one(pointAccum.y);
        X25519Field.one(pointAccum.z);
        X25519Field.zero(pointAccum.u);
        X25519Field.one(pointAccum.v);
    }

    private static void pointSetNeutral(PointExt pointExt) {
        X25519Field.zero(pointExt.x);
        X25519Field.one(pointExt.y);
        X25519Field.one(pointExt.z);
        X25519Field.zero(pointExt.t);
    }

    public static void precompute() {
        int i;
        synchronized (precompLock) {
            if (precompBase == null) {
                PointExt pointExt = new PointExt();
                X25519Field.copy(B_x, 0, pointExt.x, 0);
                X25519Field.copy(B_y, 0, pointExt.y, 0);
                pointExtendXY(pointExt);
                precompBaseTable = pointPrecompVar(pointExt, 32);
                PointAccum pointAccum = new PointAccum();
                X25519Field.copy(B_x, 0, pointAccum.x, 0);
                X25519Field.copy(B_y, 0, pointAccum.y, 0);
                pointExtendXY(pointAccum);
                precompBase = new int[1920];
                int i2 = 0;
                int i3 = 0;
                while (i2 < 8) {
                    PointExt[] pointExtArr = new PointExt[4];
                    PointExt pointExt2 = new PointExt();
                    pointSetNeutral(pointExt2);
                    int i4 = 0;
                    while (true) {
                        i = 1;
                        if (i4 >= 4) {
                            break;
                        }
                        pointAddVar(true, pointExt2, pointCopy(pointAccum), pointExt2);
                        pointDouble(pointAccum);
                        pointExtArr[i4] = pointCopy(pointAccum);
                        if (i2 + i4 != 10) {
                            while (i < 8) {
                                pointDouble(pointAccum);
                                i++;
                            }
                        }
                        i4++;
                    }
                    PointExt[] pointExtArr2 = new PointExt[8];
                    pointExtArr2[0] = pointExt2;
                    int i5 = 0;
                    int i6 = 1;
                    while (i5 < 3) {
                        int i7 = i << i5;
                        int i8 = i6;
                        int i9 = 0;
                        while (i9 < i7) {
                            PointExt pointExt3 = pointExtArr2[i8 - i7];
                            PointExt pointExt4 = pointExtArr[i5];
                            PointExt pointExt5 = new PointExt();
                            pointExtArr2[i8] = pointExt5;
                            pointAddVar(false, pointExt3, pointExt4, pointExt5);
                            i9++;
                            i8++;
                        }
                        i5++;
                        i6 = i8;
                        i = 1;
                    }
                    int i10 = i3;
                    for (int i11 = 0; i11 < 8; i11++) {
                        PointExt pointExt6 = pointExtArr2[i11];
                        int[] create = X25519Field.create();
                        int[] create2 = X25519Field.create();
                        X25519Field.add(pointExt6.z, pointExt6.z, create);
                        X25519Field.inv(create, create2);
                        X25519Field.mul(pointExt6.x, create2, create);
                        X25519Field.mul(pointExt6.y, create2, create2);
                        PointPrecomp pointPrecomp = new PointPrecomp();
                        X25519Field.apm(create2, create, pointPrecomp.ypx_h, pointPrecomp.ymx_h);
                        X25519Field.mul(create, create2, pointPrecomp.xyd);
                        X25519Field.mul(pointPrecomp.xyd, C_d4, pointPrecomp.xyd);
                        X25519Field.normalize(pointPrecomp.ypx_h);
                        X25519Field.normalize(pointPrecomp.ymx_h);
                        X25519Field.copy(pointPrecomp.ypx_h, 0, precompBase, i10);
                        int i12 = i10 + 10;
                        X25519Field.copy(pointPrecomp.ymx_h, 0, precompBase, i12);
                        int i13 = i12 + 10;
                        X25519Field.copy(pointPrecomp.xyd, 0, precompBase, i13);
                        i10 = i13 + 10;
                    }
                    i2++;
                    i3 = i10;
                }
            }
        }
    }

    private static void pruneScalar(byte[] bArr, int i, byte[] bArr2) {
        System.arraycopy(bArr, i, bArr2, 0, 32);
        bArr2[0] = (byte) (bArr2[0] & 248);
        bArr2[31] = (byte) (bArr2[31] & Byte.MAX_VALUE);
        bArr2[31] = (byte) (bArr2[31] | 64);
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
        long decode325 = ((long) decode32(bArr2, 49)) & 4294967295L;
        long decode245 = ((long) (decode24(bArr2, 53) << 4)) & 4294967295L;
        long decode326 = ((long) decode32(bArr2, 56)) & 4294967295L;
        long j = ((long) bArr2[63]) & 255;
        long decode246 = (((long) (decode24(bArr2, 60) << 4)) & 4294967295L) + (decode326 >> 28);
        long j2 = decode326 & M28L;
        long j3 = decode324 - (decode246 * -50998291);
        long decode247 = ((((long) (decode24(bArr2, 32) << 4)) & 4294967295L) - (j * -50998291)) - (decode246 * 19280294);
        long decode327 = ((((long) decode32(bArr2, 35)) & 4294967295L) - (j * 19280294)) - (decode246 * 127719000);
        long decode328 = ((((long) decode32(bArr2, 42)) & 4294967295L) - (j * -6428113)) - (decode246 * 5343);
        long j4 = decode244 - (j2 * -50998291);
        long decode248 = (((((long) (decode24(bArr2, 39) << 4)) & 4294967295L) - (j * 127719000)) - (decode246 * -6428113)) - (j2 * 5343);
        long j5 = decode245 + (decode325 >> 28);
        long j6 = decode325 & M28L;
        long j7 = (decode327 - (j2 * -6428113)) - (j5 * 5343);
        long j8 = ((decode247 - (j2 * 127719000)) - (j5 * -6428113)) - (j6 * 5343);
        long decode249 = ((((long) (decode24(bArr2, 46) << 4)) & 4294967295L) - (j * 5343)) + (decode328 >> 28);
        long j9 = (decode328 & M28L) + (decode248 >> 28);
        long j10 = decode242 - (j9 * -50998291);
        long j11 = (decode322 - (decode249 * -50998291)) - (j9 * 19280294);
        long j12 = ((decode243 - (j6 * -50998291)) - (decode249 * 19280294)) - (j9 * 127719000);
        long j13 = (((j4 - (j5 * 19280294)) - (j6 * 127719000)) - (decode249 * -6428113)) - (j9 * 5343);
        long j14 = (decode248 & M28L) + (j7 >> 28);
        long j15 = j7 & M28L;
        long j16 = decode32 - (j14 * -50998291);
        long j17 = j10 - (j14 * 19280294);
        long j18 = j11 - (j14 * 127719000);
        long j19 = ((((decode323 - (j5 * -50998291)) - (j6 * 19280294)) - (decode249 * 127719000)) - (j9 * -6428113)) - (j14 * 5343);
        long j20 = j15 + (j8 >> 28);
        long j21 = j16 - (j20 * 19280294);
        long j22 = j17 - (j20 * 127719000);
        long j23 = j18 - (j20 * -6428113);
        long j24 = (j12 - (j14 * -6428113)) - (j20 * 5343);
        long j25 = ((((j3 - (j2 * 19280294)) - (j5 * 127719000)) - (j6 * -6428113)) - (decode249 * 5343)) + (j13 >> 28);
        long j26 = j25 & M28L;
        long j27 = j26 >>> 27;
        long j28 = (j8 & M28L) + (j25 >> 28) + j27;
        long decode329 = (((long) decode32(bArr2, 0)) & 4294967295L) - (j28 * -50998291);
        long j29 = ((decode24 - (j20 * -50998291)) - (j28 * 19280294)) + (decode329 >> 28);
        long j30 = decode329 & M28L;
        long j31 = (j21 - (j28 * 127719000)) + (j29 >> 28);
        long j32 = (j22 - (j28 * -6428113)) + (j31 >> 28);
        long j33 = (j23 - (j28 * 5343)) + (j32 >> 28);
        long j34 = j24 + (j33 >> 28);
        long j35 = j33 & M28L;
        long j36 = j19 + (j34 >> 28);
        long j37 = (j13 & M28L) + (j36 >> 28);
        long j38 = j26 + (j37 >> 28);
        long j39 = (j38 >> 28) - j27;
        long j40 = j30 + (j39 & -50998291);
        long j41 = (j29 & M28L) + (j39 & 19280294) + (j40 >> 28);
        long j42 = (j31 & M28L) + (j39 & 127719000) + (j41 >> 28);
        long j43 = (j32 & M28L) + (j39 & -6428113) + (j42 >> 28);
        long j44 = j42 & M28L;
        long j45 = j35 + (j39 & 5343) + (j43 >> 28);
        long j46 = j43 & M28L;
        long j47 = (j34 & M28L) + (j45 >> 28);
        long j48 = j45 & M28L;
        long j49 = (j36 & M28L) + (j47 >> 28);
        long j50 = j47 & M28L;
        long j51 = (j37 & M28L) + (j49 >> 28);
        long j52 = j49 & M28L;
        long j53 = (j38 & M28L) + (j51 >> 28);
        long j54 = j51 & M28L;
        byte[] bArr3 = new byte[32];
        encode56((j40 & M28L) | ((j41 & M28L) << 28), bArr3, 0);
        encode56((j46 << 28) | j44, bArr3, 7);
        encode56(j48 | (j50 << 28), bArr3, 14);
        encode56(j52 | (j54 << 28), bArr3, 21);
        encode32((int) j53, bArr3, 28);
        return bArr3;
    }

    private static void scalarMultBase(byte[] bArr, PointAccum pointAccum) {
        precompute();
        pointSetNeutral(pointAccum);
        int[] iArr = new int[8];
        decodeScalar(bArr, 0, iArr);
        Nat.cadd(8, (~iArr[0]) & 1, iArr, L, iArr);
        Nat.shiftDownBit(8, iArr, 1);
        for (int i = 0; i < 8; i++) {
            iArr[i] = Interleave.shuffle2(iArr[i]);
        }
        PointPrecomp pointPrecomp = new PointPrecomp();
        int i2 = 28;
        while (true) {
            for (int i3 = 0; i3 < 8; i3++) {
                int i4 = iArr[i3] >>> i2;
                int i5 = (i4 >>> 3) & 1;
                pointLookup(i3, (i4 ^ (-i5)) & 7, pointPrecomp);
                X25519Field.cswap(i5, pointPrecomp.ypx_h, pointPrecomp.ymx_h);
                X25519Field.cnegate(i5, pointPrecomp.xyd);
                pointAddPrecomp(pointPrecomp, pointAccum);
            }
            i2 -= 4;
            if (i2 >= 0) {
                pointDouble(pointAccum);
            } else {
                return;
            }
        }
    }

    private static void scalarMultBaseEncoded(byte[] bArr, byte[] bArr2, int i) {
        PointAccum pointAccum = new PointAccum();
        scalarMultBase(bArr, pointAccum);
        encodePoint(pointAccum, bArr2, i);
    }

    public static void scalarMultBaseYZ(Friend friend, byte[] bArr, int i, int[] iArr, int[] iArr2) {
        if (friend != null) {
            byte[] bArr2 = new byte[32];
            pruneScalar(bArr, i, bArr2);
            PointAccum pointAccum = new PointAccum();
            scalarMultBase(bArr2, pointAccum);
            X25519Field.copy(pointAccum.y, 0, iArr, 0);
            X25519Field.copy(pointAccum.z, 0, iArr2, 0);
            return;
        }
        throw new NullPointerException("This method is only for use by X25519");
    }

    private static void scalarMultStraussVar(int[] iArr, int[] iArr2, PointExt pointExt, PointAccum pointAccum) {
        precompute();
        byte[] wnaf = getWNAF(iArr, 7);
        byte[] wnaf2 = getWNAF(iArr2, 5);
        PointExt[] pointPrecompVar = pointPrecompVar(pointExt, 8);
        pointSetNeutral(pointAccum);
        int i = 255;
        while (i > 0 && (wnaf[i] | wnaf2[i]) == 0) {
            i--;
        }
        while (true) {
            byte b = wnaf[i];
            boolean z = false;
            if (b != 0) {
                int i2 = b >> 31;
                pointAddVar(i2 != 0, precompBaseTable[(b ^ i2) >>> 1], pointAccum);
            }
            byte b2 = wnaf2[i];
            if (b2 != 0) {
                int i3 = b2 >> 31;
                int i4 = (b2 ^ i3) >>> 1;
                if (i3 != 0) {
                    z = true;
                }
                pointAddVar(z, pointPrecompVar[i4], pointAccum);
            }
            i--;
            if (i >= 0) {
                pointDouble(pointAccum);
            } else {
                return;
            }
        }
    }

    public static void sign(byte[] bArr, int i, byte[] bArr2, int i2, int i3, byte[] bArr3, int i4) {
        implSign(bArr, i, null, 0, bArr2, i2, i3, bArr3, i4);
    }

    public static void sign(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, int i3, int i4, byte[] bArr4, int i5) {
        implSign(bArr, i, bArr2, i2, null, 0, bArr3, i3, i4, bArr4, i5);
    }

    public static void sign(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4, int i3, int i4, byte[] bArr5, int i5) {
        implSign(bArr, i, bArr2, i2, bArr3, 0, bArr4, i3, i4, bArr5, i5);
    }

    public static void sign(byte[] bArr, int i, byte[] bArr2, byte[] bArr3, int i2, int i3, byte[] bArr4, int i4) {
        implSign(bArr, i, bArr2, 0, bArr3, i2, i3, bArr4, i4);
    }

    public static void signPrehash(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, Digest digest, byte[] bArr4, int i3) {
        byte[] bArr5 = new byte[64];
        if (64 == digest.doFinal(bArr5, 0)) {
            implSign(bArr, i, bArr2, i2, bArr3, 1, bArr5, 0, bArr5.length, bArr4, i3);
            return;
        }
        throw new IllegalArgumentException("ph");
    }

    public static void signPrehash(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4, int i3, byte[] bArr5, int i4) {
        implSign(bArr, i, bArr2, i2, bArr3, 1, bArr4, i3, 64, bArr5, i4);
    }

    public static void signPrehash(byte[] bArr, int i, byte[] bArr2, Digest digest, byte[] bArr3, int i2) {
        byte[] bArr4 = new byte[64];
        if (64 == digest.doFinal(bArr4, 0)) {
            implSign(bArr, i, bArr2, 1, bArr4, 0, bArr4.length, bArr3, i2);
            return;
        }
        throw new IllegalArgumentException("ph");
    }

    public static void signPrehash(byte[] bArr, int i, byte[] bArr2, byte[] bArr3, int i2, byte[] bArr4, int i3) {
        implSign(bArr, i, bArr2, 1, bArr3, i2, 64, bArr4, i3);
    }

    public static boolean verify(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, int i3, int i4) {
        return implVerify(bArr, i, bArr2, i2, null, 0, bArr3, i3, i4);
    }

    public static boolean verify(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4, int i3, int i4) {
        return implVerify(bArr, i, bArr2, i2, bArr3, 0, bArr4, i3, i4);
    }

    public static boolean verifyPrehash(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, Digest digest) {
        byte[] bArr4 = new byte[64];
        if (64 == digest.doFinal(bArr4, 0)) {
            return implVerify(bArr, i, bArr2, i2, bArr3, 1, bArr4, 0, bArr4.length);
        }
        throw new IllegalArgumentException("ph");
    }

    public static boolean verifyPrehash(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4, int i3) {
        return implVerify(bArr, i, bArr2, i2, bArr3, 1, bArr4, i3, 64);
    }
}
