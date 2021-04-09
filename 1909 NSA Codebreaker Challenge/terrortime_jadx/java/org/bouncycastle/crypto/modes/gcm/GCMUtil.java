package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.util.Pack;

public abstract class GCMUtil {
    private static final int E1 = -520093696;
    private static final long E1L = -2233785415175766016L;

    public static void asBytes(int[] iArr, byte[] bArr) {
        Pack.intToBigEndian(iArr, bArr, 0);
    }

    public static void asBytes(long[] jArr, byte[] bArr) {
        Pack.longToBigEndian(jArr, bArr, 0);
    }

    public static byte[] asBytes(int[] iArr) {
        byte[] bArr = new byte[16];
        Pack.intToBigEndian(iArr, bArr, 0);
        return bArr;
    }

    public static byte[] asBytes(long[] jArr) {
        byte[] bArr = new byte[16];
        Pack.longToBigEndian(jArr, bArr, 0);
        return bArr;
    }

    public static void asInts(byte[] bArr, int[] iArr) {
        Pack.bigEndianToInt(bArr, 0, iArr);
    }

    public static int[] asInts(byte[] bArr) {
        int[] iArr = new int[4];
        Pack.bigEndianToInt(bArr, 0, iArr);
        return iArr;
    }

    public static void asLongs(byte[] bArr, long[] jArr) {
        Pack.bigEndianToLong(bArr, 0, jArr);
    }

    public static long[] asLongs(byte[] bArr) {
        long[] jArr = new long[2];
        Pack.bigEndianToLong(bArr, 0, jArr);
        return jArr;
    }

    public static void copy(int[] iArr, int[] iArr2) {
        iArr2[0] = iArr[0];
        iArr2[1] = iArr[1];
        iArr2[2] = iArr[2];
        iArr2[3] = iArr[3];
    }

    public static void copy(long[] jArr, long[] jArr2) {
        jArr2[0] = jArr[0];
        jArr2[1] = jArr[1];
    }

    public static void divideP(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j >> 63;
        jArr2[0] = ((j ^ (E1L & j3)) << 1) | (j2 >>> 63);
        jArr2[1] = (j2 << 1) | (-j3);
    }

    public static void multiply(byte[] bArr, byte[] bArr2) {
        long[] asLongs = asLongs(bArr);
        multiply(asLongs, asLongs(bArr2));
        asBytes(asLongs, bArr);
    }

    public static void multiply(int[] iArr, int[] iArr2) {
        int i = iArr2[0];
        int i2 = iArr2[1];
        int i3 = 0;
        int i4 = i;
        int i5 = i2;
        int i6 = iArr2[2];
        int i7 = iArr2[3];
        int i8 = 0;
        int i9 = 0;
        int i10 = 0;
        int i11 = 0;
        while (i8 < 4) {
            int i12 = iArr[i8];
            int i13 = i7;
            int i14 = i6;
            int i15 = i5;
            int i16 = i4;
            int i17 = i3;
            int i18 = i11;
            int i19 = i10;
            int i20 = i9;
            for (int i21 = 0; i21 < 32; i21++) {
                int i22 = i12 >> 31;
                i12 <<= 1;
                i20 ^= i16 & i22;
                i19 ^= i15 & i22;
                i18 ^= i14 & i22;
                i17 ^= i22 & i13;
                i13 = (i13 >>> 1) | (i14 << 31);
                i14 = (i14 >>> 1) | (i15 << 31);
                i15 = (i15 >>> 1) | (i16 << 31);
                i16 = (i16 >>> 1) ^ (((i13 << 31) >> 8) & E1);
            }
            i8++;
            i9 = i20;
            i10 = i19;
            i11 = i18;
            i3 = i17;
            i4 = i16;
            i5 = i15;
            i6 = i14;
            i7 = i13;
        }
        iArr[0] = i9;
        iArr[1] = i10;
        iArr[2] = i11;
        iArr[3] = i3;
    }

    public static void multiply(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = 0;
        long j4 = j2;
        long j5 = jArr2[0];
        long j6 = jArr2[1];
        long j7 = 0;
        long j8 = 0;
        long j9 = j;
        for (int i = 0; i < 64; i++) {
            long j10 = j9 >> 63;
            j9 <<= 1;
            j3 ^= j5 & j10;
            long j11 = j8 ^ (j6 & j10);
            long j12 = j4 >> 63;
            j4 <<= 1;
            j8 = j11 ^ (j5 & j12);
            j7 ^= j6 & j12;
            j6 = (j6 >>> 1) | (j5 << 63);
            j5 = (j5 >>> 1) ^ (((j6 << 63) >> 8) & E1L);
        }
        long j13 = ((((j7 >>> 1) ^ j7) ^ (j7 >>> 2)) ^ (j7 >>> 7)) ^ j3;
        long j14 = ((j7 << 57) ^ ((j7 << 63) ^ (j7 << 62))) ^ j8;
        jArr[0] = j13;
        jArr[1] = j14;
    }

    public static void multiplyP(int[] iArr) {
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr[2];
        int i4 = iArr[3];
        iArr[0] = (((i4 << 31) >> 31) & E1) ^ (i >>> 1);
        iArr[1] = (i2 >>> 1) | (i << 31);
        iArr[2] = (i3 >>> 1) | (i2 << 31);
        iArr[3] = (i4 >>> 1) | (i3 << 31);
    }

    public static void multiplyP(int[] iArr, int[] iArr2) {
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr[2];
        int i4 = iArr[3];
        iArr2[0] = (((i4 << 31) >> 31) & E1) ^ (i >>> 1);
        iArr2[1] = (i2 >>> 1) | (i << 31);
        iArr2[2] = (i3 >>> 1) | (i2 << 31);
        iArr2[3] = (i4 >>> 1) | (i3 << 31);
    }

    public static void multiplyP(long[] jArr) {
        long j = jArr[0];
        long j2 = jArr[1];
        jArr[0] = (((j2 << 63) >> 63) & E1L) ^ (j >>> 1);
        jArr[1] = (j << 63) | (j2 >>> 1);
    }

    public static void multiplyP(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        jArr2[0] = (((j2 << 63) >> 63) & E1L) ^ (j >>> 1);
        jArr2[1] = (j << 63) | (j2 >>> 1);
    }

    public static void multiplyP3(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j2 << 61;
        jArr2[0] = (j3 >>> 7) ^ ((((j >>> 3) ^ j3) ^ (j3 >>> 1)) ^ (j3 >>> 2));
        jArr2[1] = (j << 61) | (j2 >>> 3);
    }

    public static void multiplyP4(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j2 << 60;
        jArr2[0] = (j3 >>> 7) ^ ((((j >>> 4) ^ j3) ^ (j3 >>> 1)) ^ (j3 >>> 2));
        jArr2[1] = (j << 60) | (j2 >>> 4);
    }

    public static void multiplyP7(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j2 << 57;
        jArr2[0] = (j3 >>> 7) ^ ((((j >>> 7) ^ j3) ^ (j3 >>> 1)) ^ (j3 >>> 2));
        jArr2[1] = (j << 57) | (j2 >>> 7);
    }

    public static void multiplyP8(int[] iArr) {
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr[2];
        int i4 = iArr[3];
        int i5 = i4 << 24;
        iArr[0] = (i5 >>> 7) ^ ((((i >>> 8) ^ i5) ^ (i5 >>> 1)) ^ (i5 >>> 2));
        iArr[1] = (i2 >>> 8) | (i << 24);
        iArr[2] = (i3 >>> 8) | (i2 << 24);
        iArr[3] = (i4 >>> 8) | (i3 << 24);
    }

    public static void multiplyP8(int[] iArr, int[] iArr2) {
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr[2];
        int i4 = iArr[3];
        int i5 = i4 << 24;
        iArr2[0] = (i5 >>> 7) ^ ((((i >>> 8) ^ i5) ^ (i5 >>> 1)) ^ (i5 >>> 2));
        iArr2[1] = (i2 >>> 8) | (i << 24);
        iArr2[2] = (i3 >>> 8) | (i2 << 24);
        iArr2[3] = (i4 >>> 8) | (i3 << 24);
    }

    public static void multiplyP8(long[] jArr) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j2 << 56;
        jArr[0] = (j3 >>> 7) ^ ((((j >>> 8) ^ j3) ^ (j3 >>> 1)) ^ (j3 >>> 2));
        jArr[1] = (j << 56) | (j2 >>> 8);
    }

    public static void multiplyP8(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = j2 << 56;
        jArr2[0] = (j3 >>> 7) ^ ((((j >>> 8) ^ j3) ^ (j3 >>> 1)) ^ (j3 >>> 2));
        jArr2[1] = (j << 56) | (j2 >>> 8);
    }

    public static byte[] oneAsBytes() {
        byte[] bArr = new byte[16];
        bArr[0] = Byte.MIN_VALUE;
        return bArr;
    }

    public static int[] oneAsInts() {
        int[] iArr = new int[4];
        iArr[0] = Integer.MIN_VALUE;
        return iArr;
    }

    public static long[] oneAsLongs() {
        long[] jArr = new long[2];
        jArr[0] = Long.MIN_VALUE;
        return jArr;
    }

    public static long[] pAsLongs() {
        long[] jArr = new long[2];
        jArr[0] = 4611686018427387904L;
        return jArr;
    }

    public static void square(long[] jArr, long[] jArr2) {
        long[] jArr3 = new long[4];
        Interleave.expand64To128Rev(jArr[0], jArr3, 0);
        Interleave.expand64To128Rev(jArr[1], jArr3, 2);
        long j = jArr3[0];
        long j2 = jArr3[1];
        long j3 = jArr3[2];
        long j4 = jArr3[3];
        long j5 = j3 ^ ((j4 << 57) ^ ((j4 << 63) ^ (j4 << 62)));
        long j6 = j ^ ((((j5 >>> 1) ^ j5) ^ (j5 >>> 2)) ^ (j5 >>> 7));
        long j7 = (j2 ^ ((((j4 >>> 1) ^ j4) ^ (j4 >>> 2)) ^ (j4 >>> 7))) ^ ((j5 << 57) ^ ((j5 << 63) ^ (j5 << 62)));
        jArr2[0] = j6;
        jArr2[1] = j7;
    }

    public static void xor(byte[] bArr, int i, byte[] bArr2, int i2, int i3) {
        while (true) {
            i3--;
            if (i3 >= 0) {
                int i4 = i + i3;
                bArr[i4] = (byte) (bArr[i4] ^ bArr2[i2 + i3]);
            } else {
                return;
            }
        }
    }

    public static void xor(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, int i3) {
        int i4 = 0;
        do {
            bArr3[i3 + i4] = (byte) (bArr[i + i4] ^ bArr2[i2 + i4]);
            int i5 = i4 + 1;
            bArr3[i3 + i5] = (byte) (bArr[i + i5] ^ bArr2[i2 + i5]);
            int i6 = i5 + 1;
            bArr3[i3 + i6] = (byte) (bArr[i + i6] ^ bArr2[i2 + i6]);
            int i7 = i6 + 1;
            bArr3[i3 + i7] = (byte) (bArr[i + i7] ^ bArr2[i2 + i7]);
            i4 = i7 + 1;
        } while (i4 < 16);
    }

    public static void xor(byte[] bArr, byte[] bArr2) {
        int i = 0;
        do {
            bArr[i] = (byte) (bArr[i] ^ bArr2[i]);
            int i2 = i + 1;
            bArr[i2] = (byte) (bArr[i2] ^ bArr2[i2]);
            int i3 = i2 + 1;
            bArr[i3] = (byte) (bArr[i3] ^ bArr2[i3]);
            int i4 = i3 + 1;
            bArr[i4] = (byte) (bArr[i4] ^ bArr2[i4]);
            i = i4 + 1;
        } while (i < 16);
    }

    public static void xor(byte[] bArr, byte[] bArr2, int i) {
        int i2 = 0;
        do {
            bArr[i2] = (byte) (bArr[i2] ^ bArr2[i + i2]);
            int i3 = i2 + 1;
            bArr[i3] = (byte) (bArr[i3] ^ bArr2[i + i3]);
            int i4 = i3 + 1;
            bArr[i4] = (byte) (bArr[i4] ^ bArr2[i + i4]);
            int i5 = i4 + 1;
            bArr[i5] = (byte) (bArr[i5] ^ bArr2[i + i5]);
            i2 = i5 + 1;
        } while (i2 < 16);
    }

    public static void xor(byte[] bArr, byte[] bArr2, int i, int i2) {
        while (true) {
            i2--;
            if (i2 >= 0) {
                bArr[i2] = (byte) (bArr[i2] ^ bArr2[i + i2]);
            } else {
                return;
            }
        }
    }

    public static void xor(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        int i = 0;
        do {
            bArr3[i] = (byte) (bArr[i] ^ bArr2[i]);
            int i2 = i + 1;
            bArr3[i2] = (byte) (bArr[i2] ^ bArr2[i2]);
            int i3 = i2 + 1;
            bArr3[i3] = (byte) (bArr[i3] ^ bArr2[i3]);
            int i4 = i3 + 1;
            bArr3[i4] = (byte) (bArr[i4] ^ bArr2[i4]);
            i = i4 + 1;
        } while (i < 16);
    }

    public static void xor(int[] iArr, int[] iArr2) {
        iArr[0] = iArr[0] ^ iArr2[0];
        iArr[1] = iArr[1] ^ iArr2[1];
        iArr[2] = iArr[2] ^ iArr2[2];
        iArr[3] = iArr2[3] ^ iArr[3];
    }

    public static void xor(int[] iArr, int[] iArr2, int[] iArr3) {
        iArr3[0] = iArr[0] ^ iArr2[0];
        iArr3[1] = iArr[1] ^ iArr2[1];
        iArr3[2] = iArr[2] ^ iArr2[2];
        iArr3[3] = iArr[3] ^ iArr2[3];
    }

    public static void xor(long[] jArr, long[] jArr2) {
        jArr[0] = jArr[0] ^ jArr2[0];
        jArr[1] = jArr[1] ^ jArr2[1];
    }

    public static void xor(long[] jArr, long[] jArr2, long[] jArr3) {
        jArr3[0] = jArr[0] ^ jArr2[0];
        jArr3[1] = jArr2[1] ^ jArr[1];
    }
}
