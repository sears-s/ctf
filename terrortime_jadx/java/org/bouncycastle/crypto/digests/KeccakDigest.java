package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class KeccakDigest implements ExtendedDigest {
    private static long[] KeccakRoundConstants = {1, 32898, -9223372036854742902L, -9223372034707259392L, 32907, 2147483649L, -9223372034707259263L, -9223372036854743031L, 138, 136, 2147516425L, 2147483658L, 2147516555L, -9223372036854775669L, -9223372036854742903L, -9223372036854743037L, -9223372036854743038L, -9223372036854775680L, 32778, -9223372034707292150L, -9223372034707259263L, -9223372036854742912L, 2147483649L, -9223372034707259384L};
    protected int bitsInQueue;
    protected byte[] dataQueue;
    protected int fixedOutputLength;
    protected int rate;
    protected boolean squeezing;
    protected long[] state;

    public KeccakDigest() {
        this(288);
    }

    public KeccakDigest(int i) {
        this.state = new long[25];
        this.dataQueue = new byte[192];
        init(i);
    }

    public KeccakDigest(KeccakDigest keccakDigest) {
        this.state = new long[25];
        this.dataQueue = new byte[192];
        long[] jArr = keccakDigest.state;
        System.arraycopy(jArr, 0, this.state, 0, jArr.length);
        byte[] bArr = keccakDigest.dataQueue;
        System.arraycopy(bArr, 0, this.dataQueue, 0, bArr.length);
        this.rate = keccakDigest.rate;
        this.bitsInQueue = keccakDigest.bitsInQueue;
        this.fixedOutputLength = keccakDigest.fixedOutputLength;
        this.squeezing = keccakDigest.squeezing;
    }

    private void KeccakAbsorb(byte[] bArr, int i) {
        int i2 = this.rate >> 6;
        for (int i3 = 0; i3 < i2; i3++) {
            long[] jArr = this.state;
            jArr[i3] = jArr[i3] ^ Pack.littleEndianToLong(bArr, i);
            i += 8;
        }
        KeccakPermutation();
    }

    private void KeccakExtract() {
        Pack.longToLittleEndian(this.state, 0, this.rate >> 6, this.dataQueue, 0);
    }

    private void KeccakPermutation() {
        long[] jArr = this.state;
        char c = 0;
        long j = jArr[0];
        boolean z = true;
        long j2 = jArr[1];
        long j3 = jArr[2];
        char c2 = 3;
        long j4 = jArr[3];
        long j5 = jArr[4];
        long j6 = jArr[5];
        long j7 = jArr[6];
        long j8 = jArr[7];
        long j9 = jArr[8];
        long j10 = jArr[9];
        long j11 = jArr[10];
        long j12 = jArr[11];
        long j13 = jArr[12];
        long j14 = jArr[13];
        long j15 = jArr[14];
        long j16 = jArr[15];
        long j17 = jArr[16];
        long j18 = jArr[17];
        long j19 = jArr[18];
        long j20 = jArr[19];
        long j21 = jArr[20];
        long j22 = jArr[21];
        long j23 = jArr[22];
        long j24 = jArr[23];
        long j25 = jArr[24];
        long j26 = j24;
        long j27 = j23;
        long j28 = j22;
        long j29 = j21;
        long j30 = j20;
        long j31 = j19;
        long j32 = j18;
        long j33 = j17;
        long j34 = j16;
        long j35 = j15;
        long j36 = j14;
        long j37 = j13;
        long j38 = j12;
        long j39 = j11;
        long j40 = j10;
        long j41 = j9;
        long j42 = j8;
        long j43 = j7;
        long j44 = j6;
        long j45 = j5;
        long j46 = j4;
        long j47 = j3;
        long j48 = j2;
        long j49 = j;
        int i = 0;
        while (i < 24) {
            long j50 = (((j49 ^ j44) ^ j39) ^ j34) ^ j29;
            long j51 = (((j48 ^ j43) ^ j38) ^ j33) ^ j28;
            long j52 = (((j47 ^ j42) ^ j37) ^ j32) ^ j27;
            long j53 = (((j46 ^ j41) ^ j36) ^ j31) ^ j26;
            long j54 = (((j45 ^ j40) ^ j35) ^ j30) ^ j25;
            long j55 = ((j51 << (z ? 1 : 0)) | (j51 >>> -1)) ^ j54;
            long j56 = ((j52 << z) | (j52 >>> -1)) ^ j50;
            long j57 = ((j53 << z) | (j53 >>> -1)) ^ j51;
            long j58 = ((j54 << z) | (j54 >>> -1)) ^ j52;
            long j59 = ((j50 << z) | (j50 >>> -1)) ^ j53;
            long j60 = j49 ^ j55;
            long j61 = j44 ^ j55;
            long j62 = j39 ^ j55;
            long j63 = j34 ^ j55;
            long j64 = j29 ^ j55;
            long j65 = j48 ^ j56;
            long j66 = j43 ^ j56;
            long j67 = j38 ^ j56;
            long j68 = j33 ^ j56;
            long j69 = j28 ^ j56;
            long j70 = j47 ^ j57;
            long j71 = j42 ^ j57;
            long j72 = j37 ^ j57;
            long j73 = j32 ^ j57;
            long j74 = j27 ^ j57;
            long j75 = j46 ^ j58;
            long j76 = j41 ^ j58;
            long j77 = j36 ^ j58;
            long j78 = j31 ^ j58;
            long j79 = j26 ^ j58;
            long j80 = j45 ^ j59;
            long j81 = j40 ^ j59;
            long j82 = j35 ^ j59;
            long j83 = j30 ^ j59;
            long j84 = j25 ^ j59;
            int i2 = i;
            long j85 = (j66 << 44) | (j66 >>> 20);
            long j86 = (j81 << 20) | (j81 >>> 44);
            long j87 = j74 << 61;
            long j88 = j74 >>> c2;
            long j89 = (j65 << z) | (j65 >>> 63);
            long j90 = j87 | j88;
            long j91 = j70;
            long j92 = (j82 << 39) | (j82 >>> 25);
            long j93 = (j72 << 43) | (j72 >>> 21);
            long j94 = (j91 << 62) | (j91 >>> 2);
            long j95 = (j77 << 25) | (j77 >>> 39);
            long j96 = (j64 << 18) | (j64 >>> 46);
            long j97 = (j83 << 8) | (j83 >>> 56);
            long j98 = j63 << 41;
            long j99 = j63 >>> 23;
            long j100 = (j79 << 56) | (j79 >>> 8);
            long j101 = j98 | j99;
            long j102 = (j80 << 27) | (j80 >>> 37);
            long j103 = (j84 << 14) | (j84 >>> 50);
            long j104 = j69 << 2;
            long j105 = j69 >>> 62;
            long j106 = j95;
            long j107 = j104 | j105;
            long j108 = j76 << 55;
            long j109 = j76 >>> 9;
            long j110 = j107;
            long j111 = j108 | j109;
            long j112 = j68 << 45;
            long j113 = j68 >>> 19;
            long j114 = j111;
            long j115 = j112 | j113;
            long j116 = j90;
            long j117 = (j61 << 36) | (j61 >>> 28);
            long j118 = j78 << 21;
            long j119 = j78 >>> 43;
            long j120 = j117;
            long j121 = j118 | j119;
            long j122 = j73 << 15;
            long j123 = j73 >>> 49;
            long j124 = j115;
            long j125 = j122 | j123;
            long j126 = j67 << 10;
            long j127 = j67 >>> 54;
            long j128 = j125;
            long j129 = j126 | j127;
            long j130 = j71 << 6;
            long j131 = j71 >>> 58;
            long j132 = j129;
            long j133 = j130 | j131;
            long j134 = j62 << 3;
            long j135 = j62 >>> 61;
            long j136 = j133;
            long j137 = j134 | j135;
            long j138 = (j75 << 28) | (j75 >>> 36);
            long j139 = ((~j85) & j93) ^ j60;
            long j140 = ((~j93) & j121) ^ j85;
            j47 = j93 ^ ((~j121) & j103);
            j46 = ((~j103) & j60) ^ j121;
            long j141 = j103 ^ (j85 & (~j60));
            long j142 = j138 ^ ((~j86) & j137);
            long j143 = ((~j137) & j124) ^ j86;
            long j144 = j124;
            long j145 = j141;
            long j146 = ((~j144) & j116) ^ j137;
            long j147 = j116;
            long j148 = j146;
            long j149 = ((~j147) & j138) ^ j144;
            long j150 = (j86 & (~j138)) ^ j147;
            long j151 = j136;
            j39 = j89 ^ ((~j151) & j106);
            long j152 = j149;
            long j153 = j106;
            long j154 = ((~j153) & j97) ^ j151;
            long j155 = j97;
            long j156 = j142;
            long j157 = ((~j155) & j96) ^ j153;
            long j158 = j96;
            long j159 = j157;
            long j160 = j155 ^ ((~j158) & j89);
            long j161 = ((~j89) & j151) ^ j158;
            long j162 = j120;
            long j163 = j102 ^ ((~j162) & j132);
            long j164 = j160;
            long j165 = j132;
            long j166 = j161;
            long j167 = ((~j165) & j128) ^ j162;
            long j168 = j128;
            long j169 = j150;
            long j170 = j100;
            long j171 = j165 ^ ((~j168) & j100);
            long j172 = ((~j170) & j102) ^ j168;
            long j173 = ((~j102) & j162) ^ j170;
            long j174 = j114;
            j29 = j94 ^ ((~j174) & j92);
            long j175 = j92;
            long j176 = j172;
            long j177 = ((~j175) & j101) ^ j174;
            long j178 = j101;
            long j179 = j173;
            long j180 = ((~j178) & j110) ^ j175;
            long j181 = j110;
            long j182 = j180;
            long j183 = j178 ^ ((~j181) & j94);
            j25 = j181 ^ ((~j94) & j174);
            long j184 = j139 ^ KeccakRoundConstants[i2];
            z = true;
            j37 = j159;
            j36 = j164;
            j44 = j156;
            j30 = j179;
            j40 = j169;
            j31 = j176;
            j38 = j154;
            j35 = j166;
            j28 = j177;
            j33 = j167;
            j42 = j148;
            j27 = j182;
            j43 = j143;
            j41 = j152;
            j45 = j145;
            j49 = j184;
            i = i2 + 1;
            j32 = j171;
            c = 0;
            j34 = j163;
            j48 = j140;
            c2 = 3;
            j26 = j183;
            jArr = jArr;
        }
        long[] jArr2 = jArr;
        jArr2[c] = j49;
        jArr2[1] = j48;
        jArr2[2] = j47;
        jArr2[3] = j46;
        jArr2[4] = j45;
        jArr2[5] = j44;
        jArr2[6] = j43;
        jArr2[7] = j42;
        jArr2[8] = j41;
        jArr2[9] = j40;
        jArr2[10] = j39;
        jArr2[11] = j38;
        jArr2[12] = j37;
        jArr2[13] = j36;
        jArr2[14] = j35;
        jArr2[15] = j34;
        jArr2[16] = j33;
        jArr2[17] = j32;
        jArr2[18] = j31;
        jArr2[19] = j30;
        jArr2[20] = j29;
        jArr2[21] = j28;
        jArr2[22] = j27;
        jArr2[23] = j26;
        jArr2[24] = j25;
    }

    private void init(int i) {
        if (i == 128 || i == 224 || i == 256 || i == 288 || i == 384 || i == 512) {
            initSponge(1600 - (i << 1));
            return;
        }
        throw new IllegalArgumentException("bitLength must be one of 128, 224, 256, 288, 384, or 512.");
    }

    private void initSponge(int i) {
        if (i <= 0 || i >= 1600 || i % 64 != 0) {
            throw new IllegalStateException("invalid rate value");
        }
        this.rate = i;
        int i2 = 0;
        while (true) {
            long[] jArr = this.state;
            if (i2 < jArr.length) {
                jArr[i2] = 0;
                i2++;
            } else {
                Arrays.fill(this.dataQueue, 0);
                this.bitsInQueue = 0;
                this.squeezing = false;
                this.fixedOutputLength = (1600 - i) / 2;
                return;
            }
        }
    }

    private void padAndSwitchToSqueezingPhase() {
        byte[] bArr = this.dataQueue;
        int i = this.bitsInQueue;
        int i2 = i >> 3;
        bArr[i2] = (byte) (bArr[i2] | ((byte) ((int) (1 << (i & 7)))));
        int i3 = i + 1;
        this.bitsInQueue = i3;
        if (i3 == this.rate) {
            KeccakAbsorb(bArr, 0);
            this.bitsInQueue = 0;
        }
        int i4 = this.bitsInQueue;
        int i5 = i4 >> 6;
        int i6 = i4 & 63;
        int i7 = 0;
        for (int i8 = 0; i8 < i5; i8++) {
            long[] jArr = this.state;
            jArr[i8] = jArr[i8] ^ Pack.littleEndianToLong(this.dataQueue, i7);
            i7 += 8;
        }
        if (i6 > 0) {
            long j = (1 << i6) - 1;
            long[] jArr2 = this.state;
            jArr2[i5] = jArr2[i5] ^ (Pack.littleEndianToLong(this.dataQueue, i7) & j);
        }
        long[] jArr3 = this.state;
        int i9 = (this.rate - 1) >> 6;
        jArr3[i9] = jArr3[i9] ^ Long.MIN_VALUE;
        KeccakPermutation();
        KeccakExtract();
        this.bitsInQueue = this.rate;
        this.squeezing = true;
    }

    /* access modifiers changed from: protected */
    public void absorb(byte[] bArr, int i, int i2) {
        int i3 = this.bitsInQueue;
        if (i3 % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        } else if (!this.squeezing) {
            int i4 = this.rate >> 3;
            int i5 = i3 >> 3;
            int i6 = 0;
            while (i6 < i2) {
                if (i5 == 0) {
                    int i7 = i2 - i4;
                    if (i6 <= i7) {
                        do {
                            KeccakAbsorb(bArr, i + i6);
                            i6 += i4;
                        } while (i6 <= i7);
                    }
                }
                int min = Math.min(i4 - i5, i2 - i6);
                System.arraycopy(bArr, i + i6, this.dataQueue, i5, min);
                i5 += min;
                i6 += min;
                if (i5 == i4) {
                    KeccakAbsorb(this.dataQueue, 0);
                    i5 = 0;
                }
            }
            this.bitsInQueue = i5 << 3;
        } else {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }
    }

    /* access modifiers changed from: protected */
    public void absorbBits(int i, int i2) {
        if (i2 < 1 || i2 > 7) {
            throw new IllegalArgumentException("'bits' must be in the range 1 to 7");
        }
        int i3 = this.bitsInQueue;
        if (i3 % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        } else if (!this.squeezing) {
            this.dataQueue[i3 >> 3] = (byte) (i & ((1 << i2) - 1));
            this.bitsInQueue = i3 + i2;
        } else {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }
    }

    public int doFinal(byte[] bArr, int i) {
        squeeze(bArr, i, (long) this.fixedOutputLength);
        reset();
        return getDigestSize();
    }

    /* access modifiers changed from: protected */
    public int doFinal(byte[] bArr, int i, byte b, int i2) {
        if (i2 > 0) {
            absorbBits(b, i2);
        }
        squeeze(bArr, i, (long) this.fixedOutputLength);
        reset();
        return getDigestSize();
    }

    public String getAlgorithmName() {
        StringBuilder sb = new StringBuilder();
        sb.append("Keccak-");
        sb.append(this.fixedOutputLength);
        return sb.toString();
    }

    public int getByteLength() {
        return this.rate / 8;
    }

    public int getDigestSize() {
        return this.fixedOutputLength / 8;
    }

    public void reset() {
        init(this.fixedOutputLength);
    }

    /* access modifiers changed from: protected */
    public void squeeze(byte[] bArr, int i, long j) {
        if (!this.squeezing) {
            padAndSwitchToSqueezingPhase();
        }
        long j2 = 0;
        if (j % 8 == 0) {
            while (j2 < j) {
                if (this.bitsInQueue == 0) {
                    KeccakPermutation();
                    KeccakExtract();
                    this.bitsInQueue = this.rate;
                }
                int min = (int) Math.min((long) this.bitsInQueue, j - j2);
                System.arraycopy(this.dataQueue, (this.rate - this.bitsInQueue) / 8, bArr, ((int) (j2 / 8)) + i, min / 8);
                this.bitsInQueue -= min;
                j2 += (long) min;
            }
            return;
        }
        throw new IllegalStateException("outputLength not a multiple of 8");
    }

    public void update(byte b) {
        absorb(new byte[]{b}, 0, 1);
    }

    public void update(byte[] bArr, int i, int i2) {
        absorb(bArr, i, i2);
    }
}
