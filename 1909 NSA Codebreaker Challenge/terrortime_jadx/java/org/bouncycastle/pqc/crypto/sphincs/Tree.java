package org.bouncycastle.pqc.crypto.sphincs;

class Tree {

    static class leafaddr {
        int level;
        long subleaf;
        long subtree;

        public leafaddr() {
        }

        public leafaddr(leafaddr leafaddr) {
            this.level = leafaddr.level;
            this.subtree = leafaddr.subtree;
            this.subleaf = leafaddr.subleaf;
        }
    }

    Tree() {
    }

    static void gen_leaf_wots(HashFunctions hashFunctions, byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, leafaddr leafaddr2) {
        byte[] bArr4 = new byte[32];
        byte[] bArr5 = new byte[2144];
        Wots wots = new Wots();
        HashFunctions hashFunctions2 = hashFunctions;
        Seed.get_seed(hashFunctions, bArr4, 0, bArr3, leafaddr2);
        wots.wots_pkgen(hashFunctions, bArr5, 0, bArr4, 0, bArr2, i2);
        l_tree(hashFunctions, bArr, i, bArr5, 0, bArr2, i2);
    }

    static void l_tree(HashFunctions hashFunctions, byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, int i3) {
        int i4;
        byte[] bArr4 = bArr2;
        int i5 = i2;
        int i6 = 67;
        for (int i7 = 0; i7 < 7; i7++) {
            int i8 = 0;
            while (true) {
                i4 = i6 >>> 1;
                if (i8 >= i4) {
                    break;
                }
                hashFunctions.hash_2n_n_mask(bArr2, i5 + (i8 * 32), bArr2, i5 + (i8 * 2 * 32), bArr3, i3 + (i7 * 2 * 32));
                i8++;
            }
            if ((i6 & 1) != 0) {
                System.arraycopy(bArr4, i5 + ((i6 - 1) * 32), bArr4, (i4 * 32) + i5, 32);
                i4++;
            }
            i6 = i4;
        }
        byte[] bArr5 = bArr;
        System.arraycopy(bArr4, i5, bArr, i, 32);
    }

    static void treehash(HashFunctions hashFunctions, byte[] bArr, int i, int i2, byte[] bArr2, leafaddr leafaddr2, byte[] bArr3, int i3) {
        leafaddr leafaddr3 = new leafaddr(leafaddr2);
        int i4 = i2 + 1;
        byte[] bArr4 = new byte[(i4 * 32)];
        int[] iArr = new int[i4];
        int i5 = 1;
        int i6 = (int) (leafaddr3.subleaf + ((long) (1 << i2)));
        int i7 = 0;
        while (true) {
            int i8 = 32;
            if (leafaddr3.subleaf >= ((long) i6)) {
                break;
            }
            gen_leaf_wots(hashFunctions, bArr4, i7 * 32, bArr3, i3, bArr2, leafaddr3);
            iArr[i7] = 0;
            int i9 = i7 + i5;
            while (i9 > i5) {
                int i10 = i9 - 1;
                int i11 = i9 - 2;
                if (iArr[i10] != iArr[i11]) {
                    break;
                }
                int i12 = i11 * 32;
                int i13 = i8;
                int i14 = i6;
                int i15 = i12;
                int i16 = i5;
                int[] iArr2 = iArr;
                hashFunctions.hash_2n_n_mask(bArr4, i12, bArr4, i15, bArr3, i3 + ((iArr[i10] + 7) * 2 * i8));
                iArr2[i11] = iArr2[i11] + i16;
                i9--;
                i5 = i16;
                i8 = i13;
                i6 = i14;
                iArr = iArr2;
            }
            int i17 = i6;
            int i18 = i5;
            int[] iArr3 = iArr;
            leafaddr3.subleaf++;
            i7 = i9;
            i5 = i18;
            i6 = i17;
            iArr = iArr3;
        }
        for (int i19 = 0; i19 < 32; i19++) {
            bArr[i + i19] = bArr4[i19];
        }
    }
}
