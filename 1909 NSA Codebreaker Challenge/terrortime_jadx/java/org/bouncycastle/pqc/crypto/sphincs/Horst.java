package org.bouncycastle.pqc.crypto.sphincs;

class Horst {
    static final int HORST_K = 32;
    static final int HORST_LOGT = 16;
    static final int HORST_SIGBYTES = 13312;
    static final int HORST_SKBYTES = 32;
    static final int HORST_T = 65536;
    static final int N_MASKS = 32;

    Horst() {
    }

    static void expand_seed(byte[] bArr, byte[] bArr2) {
        Seed.prg(bArr, 0, 2097152, bArr2, 0);
    }

    static int horst_sign(HashFunctions hashFunctions, byte[] bArr, int i, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5) {
        byte[] bArr6 = new byte[2097152];
        byte[] bArr7 = new byte[4194272];
        expand_seed(bArr6, bArr3);
        for (int i2 = 0; i2 < 65536; i2++) {
            hashFunctions.hash_n_n(bArr7, (65535 + i2) * 32, bArr6, i2 * 32);
        }
        HashFunctions hashFunctions2 = hashFunctions;
        for (int i3 = 0; i3 < 16; i3++) {
            int i4 = 16 - i3;
            long j = (long) ((1 << i4) - 1);
            int i5 = 1 << (i4 - 1);
            long j2 = (long) (i5 - 1);
            int i6 = 0;
            while (i6 < i5) {
                int i7 = i6;
                long j3 = j2;
                int i8 = i5;
                hashFunctions.hash_2n_n_mask(bArr7, (int) ((((long) i6) + j2) * 32), bArr7, (int) ((((long) (i6 * 2)) + j) * 32), bArr4, i3 * 2 * 32);
                i6 = i7 + 1;
                i5 = i8;
                j2 = j3;
            }
        }
        int i9 = 2016;
        int i10 = i;
        while (i9 < 4064) {
            int i11 = i10 + 1;
            bArr[i10] = bArr7[i9];
            i9++;
            i10 = i11;
        }
        int i12 = 0;
        while (i12 < 32) {
            int i13 = i12 * 2;
            int i14 = (bArr5[i13] & 255) + ((bArr5[i13 + 1] & 255) << 8);
            int i15 = i10;
            int i16 = 0;
            while (i16 < 32) {
                int i17 = i15 + 1;
                bArr[i15] = bArr6[(i14 * 32) + i16];
                i16++;
                i15 = i17;
            }
            int i18 = i14 + 65535;
            int i19 = 0;
            while (i19 < 10) {
                int i20 = (i18 & 1) != 0 ? i18 + 1 : i18 - 1;
                int i21 = i15;
                int i22 = 0;
                while (i22 < 32) {
                    int i23 = i21 + 1;
                    bArr[i21] = bArr7[(i20 * 32) + i22];
                    i22++;
                    i21 = i23;
                }
                i18 = (i20 - 1) / 2;
                i19++;
                i15 = i21;
            }
            i12++;
            i10 = i15;
        }
        for (int i24 = 0; i24 < 32; i24++) {
            bArr2[i24] = bArr7[i24];
        }
        return HORST_SIGBYTES;
    }

    static int horst_verify(HashFunctions hashFunctions, byte[] bArr, byte[] bArr2, int i, byte[] bArr3, byte[] bArr4) {
        HashFunctions hashFunctions2 = hashFunctions;
        byte[] bArr5 = bArr2;
        int i2 = i;
        byte[] bArr6 = new byte[1024];
        int i3 = i2 + 2048;
        int i4 = 0;
        while (i4 < 32) {
            int i5 = i4 * 2;
            int i6 = (bArr4[i5] & 255) + ((bArr4[i5 + 1] & 255) << 8);
            if ((i6 & 1) == 0) {
                hashFunctions2.hash_n_n(bArr6, 0, bArr5, i3);
                for (int i7 = 0; i7 < 32; i7++) {
                    bArr6[i7 + 32] = bArr5[i3 + 32 + i7];
                }
            } else {
                hashFunctions2.hash_n_n(bArr6, 32, bArr5, i3);
                for (int i8 = 0; i8 < 32; i8++) {
                    bArr6[i8] = bArr5[i3 + 32 + i8];
                }
            }
            int i9 = i3 + 64;
            int i10 = 1;
            while (i10 < 10) {
                int i11 = i6 >>> 1;
                if ((i11 & 1) == 0) {
                    hashFunctions.hash_2n_n_mask(bArr6, 0, bArr6, 0, bArr3, (i10 - 1) * 2 * 32);
                    for (int i12 = 0; i12 < 32; i12++) {
                        bArr6[i12 + 32] = bArr5[i9 + i12];
                    }
                } else {
                    hashFunctions.hash_2n_n_mask(bArr6, 32, bArr6, 0, bArr3, (i10 - 1) * 2 * 32);
                    for (int i13 = 0; i13 < 32; i13++) {
                        bArr6[i13] = bArr5[i9 + i13];
                    }
                }
                i9 += 32;
                i10++;
                i6 = i11;
            }
            int i14 = i6 >>> 1;
            hashFunctions.hash_2n_n_mask(bArr6, 0, bArr6, 0, bArr3, 576);
            for (int i15 = 0; i15 < 32; i15++) {
                if (bArr5[(i14 * 32) + i2 + i15] != bArr6[i15]) {
                    for (int i16 = 0; i16 < 32; i16++) {
                        bArr[i16] = 0;
                    }
                    return -1;
                }
            }
            i4++;
            i3 = i9;
        }
        for (int i17 = 0; i17 < 32; i17++) {
            HashFunctions hashFunctions3 = hashFunctions;
            byte[] bArr7 = bArr6;
            hashFunctions3.hash_2n_n_mask(bArr7, i17 * 32, bArr2, i2 + (i17 * 2 * 32), bArr3, 640);
        }
        for (int i18 = 0; i18 < 16; i18++) {
            HashFunctions hashFunctions4 = hashFunctions;
            byte[] bArr8 = bArr6;
            hashFunctions4.hash_2n_n_mask(bArr8, i18 * 32, bArr6, i18 * 2 * 32, bArr3, 704);
        }
        for (int i19 = 0; i19 < 8; i19++) {
            HashFunctions hashFunctions5 = hashFunctions;
            byte[] bArr9 = bArr6;
            hashFunctions5.hash_2n_n_mask(bArr9, i19 * 32, bArr6, i19 * 2 * 32, bArr3, 768);
        }
        for (int i20 = 0; i20 < 4; i20++) {
            HashFunctions hashFunctions6 = hashFunctions;
            byte[] bArr10 = bArr6;
            hashFunctions6.hash_2n_n_mask(bArr10, i20 * 32, bArr6, i20 * 2 * 32, bArr3, 832);
        }
        for (int i21 = 0; i21 < 2; i21++) {
            HashFunctions hashFunctions7 = hashFunctions;
            byte[] bArr11 = bArr6;
            hashFunctions7.hash_2n_n_mask(bArr11, i21 * 32, bArr6, i21 * 2 * 32, bArr3, 896);
        }
        hashFunctions.hash_2n_n_mask(bArr, 0, bArr6, 0, bArr3, 960);
        return 0;
    }
}
