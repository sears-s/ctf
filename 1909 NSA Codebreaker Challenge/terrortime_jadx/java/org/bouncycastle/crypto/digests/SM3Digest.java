package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SM3Digest extends GeneralDigest {
    private static final int BLOCK_SIZE = 16;
    private static final int DIGEST_LENGTH = 32;
    private static final int[] T = new int[64];
    private int[] V = new int[8];
    private int[] W = new int[68];
    private int[] inwords = new int[16];
    private int xOff;

    static {
        int i;
        int i2 = 0;
        while (true) {
            if (i2 >= 16) {
                break;
            }
            T[i2] = (2043430169 >>> (32 - i2)) | (2043430169 << i2);
            i2++;
        }
        for (i = 16; i < 64; i++) {
            int i3 = i % 32;
            T[i] = (2055708042 >>> (32 - i3)) | (2055708042 << i3);
        }
    }

    public SM3Digest() {
        reset();
    }

    public SM3Digest(SM3Digest sM3Digest) {
        super((GeneralDigest) sM3Digest);
        copyIn(sM3Digest);
    }

    private int FF0(int i, int i2, int i3) {
        return (i ^ i2) ^ i3;
    }

    private int FF1(int i, int i2, int i3) {
        return (i & i3) | (i & i2) | (i2 & i3);
    }

    private int GG0(int i, int i2, int i3) {
        return (i ^ i2) ^ i3;
    }

    private int GG1(int i, int i2, int i3) {
        return ((~i) & i3) | (i2 & i);
    }

    private int P0(int i) {
        return (i ^ ((i << 9) | (i >>> 23))) ^ ((i << 17) | (i >>> 15));
    }

    private int P1(int i) {
        return (i ^ ((i << 15) | (i >>> 17))) ^ ((i << 23) | (i >>> 9));
    }

    private void copyIn(SM3Digest sM3Digest) {
        int[] iArr = sM3Digest.V;
        int[] iArr2 = this.V;
        System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
        int[] iArr3 = sM3Digest.inwords;
        int[] iArr4 = this.inwords;
        System.arraycopy(iArr3, 0, iArr4, 0, iArr4.length);
        this.xOff = sM3Digest.xOff;
    }

    public Memoable copy() {
        return new SM3Digest(this);
    }

    public int doFinal(byte[] bArr, int i) {
        finish();
        Pack.intToBigEndian(this.V, bArr, i);
        reset();
        return 32;
    }

    public String getAlgorithmName() {
        return "SM3";
    }

    public int getDigestSize() {
        return 32;
    }

    /* access modifiers changed from: protected */
    public void processBlock() {
        int i;
        int i2 = 0;
        while (true) {
            if (i2 >= 16) {
                break;
            }
            this.W[i2] = this.inwords[i2];
            i2++;
        }
        for (int i3 = 16; i3 < 68; i3++) {
            int[] iArr = this.W;
            int i4 = iArr[i3 - 3];
            int i5 = (i4 >>> 17) | (i4 << 15);
            int i6 = iArr[i3 - 13];
            iArr[i3] = (P1(i5 ^ (iArr[i3 - 16] ^ iArr[i3 - 9])) ^ ((i6 >>> 25) | (i6 << 7))) ^ this.W[i3 - 6];
        }
        int[] iArr2 = this.V;
        int i7 = iArr2[0];
        int i8 = iArr2[1];
        int i9 = iArr2[2];
        int i10 = iArr2[3];
        int i11 = iArr2[4];
        int i12 = iArr2[5];
        int i13 = iArr2[6];
        int i14 = iArr2[7];
        int i15 = i13;
        int i16 = 0;
        int i17 = i8;
        int i18 = i7;
        int i19 = i17;
        int i20 = i10;
        int i21 = i9;
        int i22 = i20;
        int i23 = i12;
        int i24 = i11;
        int i25 = i23;
        for (i = 16; i16 < i; i = 16) {
            int i26 = (i18 << 12) | (i18 >>> 20);
            int i27 = i26 + i24 + T[i16];
            int i28 = (i27 << 7) | (i27 >>> 25);
            int i29 = i28 ^ i26;
            int[] iArr3 = this.W;
            int i30 = iArr3[i16];
            int FF0 = FF0(i18, i19, i21) + i22 + i29 + (i30 ^ iArr3[i16 + 4]);
            int i31 = (i19 << 9) | (i19 >>> 23);
            i16++;
            i14 = i15;
            i15 = (i25 << 19) | (i25 >>> 13);
            i25 = i24;
            i24 = P0(GG0(i24, i25, i15) + i14 + i28 + i30);
            i22 = i21;
            i21 = i31;
            i19 = i18;
            i18 = FF0;
        }
        int i32 = i19;
        int i33 = i18;
        int i34 = i22;
        int i35 = i21;
        int i36 = i25;
        int i37 = i24;
        int i38 = 16;
        while (i38 < 64) {
            int i39 = (i33 << 12) | (i33 >>> 20);
            int i40 = i39 + i37 + T[i38];
            int i41 = (i40 << 7) | (i40 >>> 25);
            int i42 = i41 ^ i39;
            int[] iArr4 = this.W;
            int i43 = iArr4[i38];
            int FF1 = FF1(i33, i32, i35) + i34 + i42 + (i43 ^ iArr4[i38 + 4]);
            int i44 = (i32 >>> 23) | (i32 << 9);
            i38++;
            i14 = i15;
            i15 = (i36 >>> 13) | (i36 << 19);
            i36 = i37;
            i37 = P0(GG1(i37, i36, i15) + i14 + i41 + i43);
            int i45 = i35;
            i35 = i44;
            i32 = i33;
            i33 = FF1;
            i34 = i45;
        }
        int[] iArr5 = this.V;
        iArr5[0] = i33 ^ iArr5[0];
        iArr5[1] = i32 ^ iArr5[1];
        iArr5[2] = iArr5[2] ^ i35;
        iArr5[3] = iArr5[3] ^ i34;
        iArr5[4] = iArr5[4] ^ i37;
        iArr5[5] = iArr5[5] ^ i36;
        iArr5[6] = iArr5[6] ^ i15;
        iArr5[7] = iArr5[7] ^ i14;
        this.xOff = 0;
    }

    /* access modifiers changed from: protected */
    public void processLength(long j) {
        int i = this.xOff;
        if (i > 14) {
            this.inwords[i] = 0;
            this.xOff = i + 1;
            processBlock();
        }
        while (true) {
            int i2 = this.xOff;
            if (i2 < 14) {
                this.inwords[i2] = 0;
                this.xOff = i2 + 1;
            } else {
                int[] iArr = this.inwords;
                this.xOff = i2 + 1;
                iArr[i2] = (int) (j >>> 32);
                int i3 = this.xOff;
                this.xOff = i3 + 1;
                iArr[i3] = (int) j;
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void processWord(byte[] bArr, int i) {
        int i2 = i + 1;
        int i3 = i2 + 1;
        int i4 = (bArr[i3 + 1] & 255) | ((bArr[i] & 255) << 24) | ((bArr[i2] & 255) << Tnaf.POW_2_WIDTH) | ((bArr[i3] & 255) << 8);
        int[] iArr = this.inwords;
        int i5 = this.xOff;
        iArr[i5] = i4;
        this.xOff = i5 + 1;
        if (this.xOff >= 16) {
            processBlock();
        }
    }

    public void reset() {
        super.reset();
        int[] iArr = this.V;
        iArr[0] = 1937774191;
        iArr[1] = 1226093241;
        iArr[2] = 388252375;
        iArr[3] = -628488704;
        iArr[4] = -1452330820;
        iArr[5] = 372324522;
        iArr[6] = -477237683;
        iArr[7] = -1325724082;
        this.xOff = 0;
    }

    public void reset(Memoable memoable) {
        SM3Digest sM3Digest = (SM3Digest) memoable;
        super.copyIn(sM3Digest);
        copyIn(sM3Digest);
    }
}
