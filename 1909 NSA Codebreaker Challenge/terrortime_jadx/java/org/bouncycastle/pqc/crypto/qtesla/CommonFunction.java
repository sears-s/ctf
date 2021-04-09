package org.bouncycastle.pqc.crypto.qtesla;

class CommonFunction {
    CommonFunction() {
    }

    public static short load16(byte[] bArr, int i) {
        short s;
        int i2 = 0;
        if (bArr.length - i >= 2) {
            s = 0;
            while (i2 < 2) {
                s = (short) (s ^ (((short) (bArr[i + i2] & 255)) << (i2 * 8)));
                i2++;
            }
        } else {
            short s2 = 0;
            while (i2 < bArr.length - i) {
                s2 = (short) (s ^ (((short) (bArr[i + i2] & 255)) << (i2 * 8)));
                i2++;
            }
        }
        return s;
    }

    public static int load32(byte[] bArr, int i) {
        int i2;
        int i3 = 0;
        if (bArr.length - i >= 4) {
            i2 = 0;
            while (i3 < 4) {
                i2 ^= (bArr[i + i3] & 255) << (i3 * 8);
                i3++;
            }
        } else {
            int i4 = 0;
            while (i3 < bArr.length - i) {
                i4 = i2 ^ ((bArr[i + i3] & 255) << (i3 * 8));
                i3++;
            }
        }
        return i2;
    }

    public static long load64(byte[] bArr, int i) {
        int i2 = 0;
        long j = 0;
        if (bArr.length - i >= 8) {
            while (i2 < 8) {
                j ^= ((long) (bArr[i + i2] & 255)) << (i2 * 8);
                i2++;
            }
        } else {
            while (i2 < bArr.length - i) {
                j ^= ((long) (bArr[i + i2] & 255)) << (i2 * 8);
                i2++;
            }
        }
        return j;
    }

    public static boolean memoryEqual(byte[] bArr, int i, byte[] bArr2, int i2, int i3) {
        if (i + i3 > bArr.length || i2 + i3 > bArr2.length) {
            return false;
        }
        for (int i4 = 0; i4 < i3; i4++) {
            if (bArr[i + i4] != bArr2[i2 + i4]) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=short, code=int, for r6v0, types: [short, int] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void store16(byte[] r4, int r5, int r6) {
        /*
            int r0 = r4.length
            int r0 = r0 - r5
            r1 = 0
            r2 = 2
            if (r0 < r2) goto L_0x0016
        L_0x0006:
            if (r1 >= r2) goto L_0x0028
            int r0 = r5 + r1
            int r3 = r1 * 8
            int r3 = r6 >> r3
            r3 = r3 & 255(0xff, float:3.57E-43)
            byte r3 = (byte) r3
            r4[r0] = r3
            int r1 = r1 + 1
            goto L_0x0006
        L_0x0016:
            int r0 = r4.length
            int r0 = r0 - r5
            if (r1 >= r0) goto L_0x0028
            int r0 = r5 + r1
            int r2 = r1 * 8
            int r2 = r6 >> r2
            r2 = r2 & 255(0xff, float:3.57E-43)
            byte r2 = (byte) r2
            r4[r0] = r2
            int r1 = r1 + 1
            goto L_0x0016
        L_0x0028:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.pqc.crypto.qtesla.CommonFunction.store16(byte[], int, short):void");
    }

    public static void store32(byte[] bArr, int i, int i2) {
        int i3 = 0;
        if (bArr.length - i >= 4) {
            while (i3 < 4) {
                bArr[i + i3] = (byte) ((i2 >> (i3 * 8)) & 255);
                i3++;
            }
            return;
        }
        while (i3 < bArr.length - i) {
            bArr[i + i3] = (byte) ((i2 >> (i3 * 8)) & 255);
            i3++;
        }
    }

    public static void store64(byte[] bArr, int i, long j) {
        int i2 = 0;
        if (bArr.length - i >= 8) {
            while (i2 < 8) {
                bArr[i + i2] = (byte) ((int) ((j >> (i2 * 8)) & 255));
                i2++;
            }
            return;
        }
        while (i2 < bArr.length - i) {
            bArr[i + i2] = (byte) ((int) ((j >> (i2 * 8)) & 255));
            i2++;
        }
    }
}
