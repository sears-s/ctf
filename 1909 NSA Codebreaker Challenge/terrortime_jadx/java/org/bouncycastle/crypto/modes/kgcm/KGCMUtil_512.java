package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.math.raw.Interleave;

public class KGCMUtil_512 {
    public static final int SIZE = 8;

    public static void add(long[] jArr, long[] jArr2, long[] jArr3) {
        jArr3[0] = jArr[0] ^ jArr2[0];
        jArr3[1] = jArr[1] ^ jArr2[1];
        jArr3[2] = jArr[2] ^ jArr2[2];
        jArr3[3] = jArr[3] ^ jArr2[3];
        jArr3[4] = jArr[4] ^ jArr2[4];
        jArr3[5] = jArr[5] ^ jArr2[5];
        jArr3[6] = jArr[6] ^ jArr2[6];
        jArr3[7] = jArr2[7] ^ jArr[7];
    }

    public static void copy(long[] jArr, long[] jArr2) {
        jArr2[0] = jArr[0];
        jArr2[1] = jArr[1];
        jArr2[2] = jArr[2];
        jArr2[3] = jArr[3];
        jArr2[4] = jArr[4];
        jArr2[5] = jArr[5];
        jArr2[6] = jArr[6];
        jArr2[7] = jArr[7];
    }

    public static boolean equal(long[] jArr, long[] jArr2) {
        return ((jArr2[7] ^ jArr[7]) | ((((((((jArr[0] ^ jArr2[0]) | 0) | (jArr[1] ^ jArr2[1])) | (jArr[2] ^ jArr2[2])) | (jArr[3] ^ jArr2[3])) | (jArr[4] ^ jArr2[4])) | (jArr[5] ^ jArr2[5])) | (jArr[6] ^ jArr2[6]))) == 0;
    }

    public static void multiply(long[] jArr, long[] jArr2, long[] jArr3) {
        int i = 0;
        long j = jArr2[0];
        long j2 = jArr2[1];
        char c = 2;
        long j3 = jArr2[2];
        long j4 = jArr2[3];
        long j5 = jArr2[4];
        long j6 = jArr2[5];
        long j7 = 0;
        long j8 = j;
        long j9 = j2;
        long j10 = j3;
        long j11 = j4;
        long j12 = j5;
        long j13 = j6;
        long j14 = jArr2[6];
        long j15 = jArr2[7];
        long j16 = 0;
        long j17 = 0;
        long j18 = 0;
        long j19 = 0;
        long j20 = 0;
        long j21 = 0;
        long j22 = 0;
        long j23 = 0;
        int i2 = 0;
        while (i2 < 8) {
            long j24 = jArr[i2];
            long j25 = jArr[i2 + 1];
            long j26 = j24;
            long j27 = j21;
            long j28 = j20;
            long j29 = j19;
            long j30 = j18;
            long j31 = j17;
            long j32 = j16;
            int i3 = i;
            while (i3 < 64) {
                long j33 = -(j26 & 1);
                j26 >>>= 1;
                j7 ^= j8 & j33;
                long j34 = -(j25 & 1);
                j25 >>>= 1;
                j31 = (j31 ^ (j9 & j33)) ^ (j8 & j34);
                j30 = (j30 ^ (j10 & j33)) ^ (j9 & j34);
                j29 = (j29 ^ (j11 & j33)) ^ (j10 & j34);
                j28 = (j28 ^ (j12 & j33)) ^ (j11 & j34);
                j27 = (j27 ^ (j13 & j33)) ^ (j12 & j34);
                j22 = (j22 ^ (j14 & j33)) ^ (j13 & j34);
                j23 = (j23 ^ (j15 & j33)) ^ (j14 & j34);
                j32 ^= j15 & j34;
                long j35 = j15 >> 63;
                j15 = (j15 << 1) | (j14 >>> 63);
                j14 = (j14 << 1) | (j13 >>> 63);
                j13 = (j13 << 1) | (j12 >>> 63);
                j12 = (j12 << 1) | (j11 >>> 63);
                j11 = (j11 << 1) | (j10 >>> 63);
                j10 = (j10 << 1) | (j9 >>> 63);
                j9 = (j9 << 1) | (j8 >>> 63);
                j8 = (j8 << 1) ^ (j35 & 293);
                i3++;
                i2 = i2;
            }
            long j36 = j32;
            long j37 = ((j8 ^ (j15 >>> 62)) ^ (j15 >>> 59)) ^ (j15 >>> 56);
            c = 2;
            j8 = ((j15 ^ (j15 << 2)) ^ (j15 << 5)) ^ (j15 << 8);
            j17 = j31;
            j18 = j30;
            j19 = j29;
            j20 = j28;
            j15 = j14;
            j21 = j27;
            j16 = j36;
            j14 = j13;
            j13 = j12;
            j12 = j11;
            j11 = j10;
            j10 = j9;
            j9 = j37;
            i2 += 2;
            i = 0;
        }
        long j38 = j7 ^ ((((j16 << c) ^ j16) ^ (j16 << 5)) ^ (j16 << 8));
        long j39 = (((j16 >>> 62) ^ (j16 >>> 59)) ^ (j16 >>> 56)) ^ j17;
        jArr3[0] = j38;
        jArr3[1] = j39;
        jArr3[2] = j18;
        jArr3[3] = j19;
        jArr3[4] = j20;
        jArr3[5] = j21;
        jArr3[6] = j22;
        jArr3[7] = j23;
    }

    public static void multiplyX(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr[2];
        long j4 = jArr[3];
        long j5 = jArr[4];
        long j6 = jArr[5];
        long j7 = jArr[6];
        long j8 = jArr[7];
        jArr2[0] = (j << 1) ^ ((j8 >> 63) & 293);
        jArr2[1] = (j2 << 1) | (j >>> 63);
        jArr2[2] = (j3 << 1) | (j2 >>> 63);
        jArr2[3] = (j4 << 1) | (j3 >>> 63);
        jArr2[4] = (j5 << 1) | (j4 >>> 63);
        jArr2[5] = (j6 << 1) | (j5 >>> 63);
        jArr2[6] = (j7 << 1) | (j6 >>> 63);
        jArr2[7] = (j8 << 1) | (j7 >>> 63);
    }

    public static void multiplyX8(long[] jArr, long[] jArr2) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr[2];
        long j4 = jArr[3];
        long j5 = jArr[4];
        long j6 = jArr[5];
        long j7 = jArr[6];
        long j8 = jArr[7];
        long j9 = j8 >>> 56;
        jArr2[0] = ((((j << 8) ^ j9) ^ (j9 << 2)) ^ (j9 << 5)) ^ (j9 << 8);
        jArr2[1] = (j2 << 8) | (j >>> 56);
        jArr2[2] = (j3 << 8) | (j2 >>> 56);
        jArr2[3] = (j4 << 8) | (j3 >>> 56);
        jArr2[4] = (j5 << 8) | (j4 >>> 56);
        jArr2[5] = (j6 << 8) | (j5 >>> 56);
        jArr2[6] = (j7 << 8) | (j6 >>> 56);
        jArr2[7] = (j8 << 8) | (j7 >>> 56);
    }

    public static void one(long[] jArr) {
        jArr[0] = 1;
        jArr[1] = 0;
        jArr[2] = 0;
        jArr[3] = 0;
        jArr[4] = 0;
        jArr[5] = 0;
        jArr[6] = 0;
        jArr[7] = 0;
    }

    public static void square(long[] jArr, long[] jArr2) {
        int i = 16;
        long[] jArr3 = new long[16];
        for (int i2 = 0; i2 < 8; i2++) {
            Interleave.expand64To128(jArr[i2], jArr3, i2 << 1);
        }
        while (true) {
            i--;
            if (i >= 8) {
                long j = jArr3[i];
                int i3 = i - 8;
                jArr3[i3] = jArr3[i3] ^ ((((j << 2) ^ j) ^ (j << 5)) ^ (j << 8));
                int i4 = i3 + 1;
                jArr3[i4] = ((j >>> 56) ^ ((j >>> 62) ^ (j >>> 59))) ^ jArr3[i4];
            } else {
                copy(jArr3, jArr2);
                return;
            }
        }
    }

    public static void x(long[] jArr) {
        jArr[0] = 2;
        jArr[1] = 0;
        jArr[2] = 0;
        jArr[3] = 0;
        jArr[4] = 0;
        jArr[5] = 0;
        jArr[6] = 0;
        jArr[7] = 0;
    }

    public static void zero(long[] jArr) {
        jArr[0] = 0;
        jArr[1] = 0;
        jArr[2] = 0;
        jArr[3] = 0;
        jArr[4] = 0;
        jArr[5] = 0;
        jArr[6] = 0;
        jArr[7] = 0;
    }
}
