package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class Blake2sDigest implements ExtendedDigest {
    private static final int BLOCK_LENGTH_BYTES = 64;
    private static final int ROUNDS = 10;
    private static final int[] blake2s_IV = {1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225};
    private static final byte[][] blake2s_sigma = {new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, new byte[]{14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3}, new byte[]{11, 8, 12, 0, 5, 2, 15, 13, 10, 14, 3, 6, 7, 1, 9, 4}, new byte[]{7, 9, 3, 1, 13, 12, 11, 14, 2, 6, 5, 10, 4, 0, 15, 8}, new byte[]{9, 0, 5, 7, 2, 4, 10, 15, 14, 1, 11, 12, 6, 8, 3, 13}, new byte[]{2, 12, 6, 10, 0, 11, 8, 3, 4, 13, 7, 5, 15, 14, 1, 9}, new byte[]{12, 5, 1, 15, 14, 13, 4, 10, 0, 7, 6, 3, 9, 2, 8, 11}, new byte[]{13, 11, 7, 14, 12, 1, 3, 9, 5, 0, 15, 4, 8, 6, 2, 10}, new byte[]{6, 15, 14, 9, 11, 3, 0, 8, 12, 2, 13, 7, 1, 4, 10, 5}, new byte[]{10, 2, 8, 4, 7, 6, 1, 5, 15, 11, 9, 14, 3, 12, 13, 0}};
    private byte[] buffer;
    private int bufferPos;
    private int[] chainValue;
    private int depth;
    private int digestLength;
    private int f0;
    private int fanout;
    private int innerHashLength;
    private int[] internalState;
    private byte[] key;
    private int keyLength;
    private int leafLength;
    private int nodeDepth;
    private long nodeOffset;
    private byte[] personalization;
    private byte[] salt;
    private int t0;
    private int t1;

    public Blake2sDigest() {
        this(256);
    }

    public Blake2sDigest(int i) {
        this.digestLength = 32;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.fanout = 1;
        this.depth = 1;
        this.leafLength = 0;
        this.nodeOffset = 0;
        this.nodeDepth = 0;
        this.innerHashLength = 0;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        if (i < 8 || i > 256 || i % 8 != 0) {
            throw new IllegalArgumentException("BLAKE2s digest bit length must be a multiple of 8 and not greater than 256");
        }
        this.digestLength = i / 8;
        init(null, null, null);
    }

    Blake2sDigest(int i, int i2, long j) {
        this.digestLength = 32;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.fanout = 1;
        this.depth = 1;
        this.leafLength = 0;
        this.nodeOffset = 0;
        this.nodeDepth = 0;
        this.innerHashLength = 0;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        this.digestLength = i;
        this.nodeOffset = j;
        this.fanout = 0;
        this.depth = 0;
        this.leafLength = i2;
        this.innerHashLength = i2;
        this.nodeDepth = 0;
        init(null, null, null);
    }

    Blake2sDigest(int i, byte[] bArr, byte[] bArr2, byte[] bArr3, long j) {
        this.digestLength = 32;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.fanout = 1;
        this.depth = 1;
        this.leafLength = 0;
        this.nodeOffset = 0;
        this.nodeDepth = 0;
        this.innerHashLength = 0;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        this.digestLength = i;
        this.nodeOffset = j;
        init(bArr2, bArr3, bArr);
    }

    public Blake2sDigest(Blake2sDigest blake2sDigest) {
        this.digestLength = 32;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.fanout = 1;
        this.depth = 1;
        this.leafLength = 0;
        this.nodeOffset = 0;
        this.nodeDepth = 0;
        this.innerHashLength = 0;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        this.bufferPos = blake2sDigest.bufferPos;
        this.buffer = Arrays.clone(blake2sDigest.buffer);
        this.keyLength = blake2sDigest.keyLength;
        this.key = Arrays.clone(blake2sDigest.key);
        this.digestLength = blake2sDigest.digestLength;
        this.internalState = Arrays.clone(this.internalState);
        this.chainValue = Arrays.clone(blake2sDigest.chainValue);
        this.t0 = blake2sDigest.t0;
        this.t1 = blake2sDigest.t1;
        this.f0 = blake2sDigest.f0;
        this.salt = Arrays.clone(blake2sDigest.salt);
        this.personalization = Arrays.clone(blake2sDigest.personalization);
        this.fanout = blake2sDigest.fanout;
        this.depth = blake2sDigest.depth;
        this.leafLength = blake2sDigest.leafLength;
        this.nodeOffset = blake2sDigest.nodeOffset;
        this.nodeDepth = blake2sDigest.nodeDepth;
        this.innerHashLength = blake2sDigest.innerHashLength;
    }

    public Blake2sDigest(byte[] bArr) {
        this.digestLength = 32;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.fanout = 1;
        this.depth = 1;
        this.leafLength = 0;
        this.nodeOffset = 0;
        this.nodeDepth = 0;
        this.innerHashLength = 0;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        init(null, null, bArr);
    }

    public Blake2sDigest(byte[] bArr, int i, byte[] bArr2, byte[] bArr3) {
        this.digestLength = 32;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.fanout = 1;
        this.depth = 1;
        this.leafLength = 0;
        this.nodeOffset = 0;
        this.nodeDepth = 0;
        this.innerHashLength = 0;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        if (i < 1 || i > 32) {
            throw new IllegalArgumentException("Invalid digest length (required: 1 - 32)");
        }
        this.digestLength = i;
        init(bArr2, bArr3, bArr);
    }

    private void G(int i, int i2, int i3, int i4, int i5, int i6) {
        int[] iArr = this.internalState;
        iArr[i3] = iArr[i3] + iArr[i4] + i;
        iArr[i6] = rotr32(iArr[i6] ^ iArr[i3], 16);
        int[] iArr2 = this.internalState;
        iArr2[i5] = iArr2[i5] + iArr2[i6];
        iArr2[i4] = rotr32(iArr2[i4] ^ iArr2[i5], 12);
        int[] iArr3 = this.internalState;
        iArr3[i3] = iArr3[i3] + iArr3[i4] + i2;
        iArr3[i6] = rotr32(iArr3[i6] ^ iArr3[i3], 8);
        int[] iArr4 = this.internalState;
        iArr4[i5] = iArr4[i5] + iArr4[i6];
        iArr4[i4] = rotr32(iArr4[i4] ^ iArr4[i5], 7);
    }

    private void compress(byte[] bArr, int i) {
        initializeInternalState();
        int[] iArr = new int[16];
        int i2 = 0;
        for (int i3 = 0; i3 < 16; i3++) {
            iArr[i3] = Pack.littleEndianToInt(bArr, (i3 * 4) + i);
        }
        for (int i4 = 0; i4 < 10; i4++) {
            byte[][] bArr2 = blake2s_sigma;
            G(iArr[bArr2[i4][0]], iArr[bArr2[i4][1]], 0, 4, 8, 12);
            byte[][] bArr3 = blake2s_sigma;
            G(iArr[bArr3[i4][2]], iArr[bArr3[i4][3]], 1, 5, 9, 13);
            byte[][] bArr4 = blake2s_sigma;
            G(iArr[bArr4[i4][4]], iArr[bArr4[i4][5]], 2, 6, 10, 14);
            byte[][] bArr5 = blake2s_sigma;
            G(iArr[bArr5[i4][6]], iArr[bArr5[i4][7]], 3, 7, 11, 15);
            byte[][] bArr6 = blake2s_sigma;
            G(iArr[bArr6[i4][8]], iArr[bArr6[i4][9]], 0, 5, 10, 15);
            byte[][] bArr7 = blake2s_sigma;
            G(iArr[bArr7[i4][10]], iArr[bArr7[i4][11]], 1, 6, 11, 12);
            byte[][] bArr8 = blake2s_sigma;
            G(iArr[bArr8[i4][12]], iArr[bArr8[i4][13]], 2, 7, 8, 13);
            byte[][] bArr9 = blake2s_sigma;
            G(iArr[bArr9[i4][14]], iArr[bArr9[i4][15]], 3, 4, 9, 14);
        }
        while (true) {
            int[] iArr2 = this.chainValue;
            if (i2 < iArr2.length) {
                int i5 = iArr2[i2];
                int[] iArr3 = this.internalState;
                iArr2[i2] = (i5 ^ iArr3[i2]) ^ iArr3[i2 + 8];
                i2++;
            } else {
                return;
            }
        }
    }

    private void init(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        this.buffer = new byte[64];
        if (bArr3 != null && bArr3.length > 0) {
            if (bArr3.length <= 32) {
                this.key = new byte[bArr3.length];
                System.arraycopy(bArr3, 0, this.key, 0, bArr3.length);
                this.keyLength = bArr3.length;
                System.arraycopy(bArr3, 0, this.buffer, 0, bArr3.length);
                this.bufferPos = 64;
            } else {
                throw new IllegalArgumentException("Keys > 32 bytes are not supported");
            }
        }
        if (this.chainValue == null) {
            this.chainValue = new int[8];
            int[] iArr = this.chainValue;
            int[] iArr2 = blake2s_IV;
            iArr[0] = iArr2[0] ^ ((this.digestLength | (this.keyLength << 8)) | ((this.fanout << 16) | (this.depth << 24)));
            iArr[1] = iArr2[1] ^ this.leafLength;
            long j = this.nodeOffset;
            int i = (int) (j >> 32);
            iArr[2] = ((int) j) ^ iArr2[2];
            iArr[3] = ((i | (this.nodeDepth << 16)) | (this.innerHashLength << 24)) ^ iArr2[3];
            iArr[4] = iArr2[4];
            iArr[5] = iArr2[5];
            if (bArr != null) {
                if (bArr.length == 8) {
                    this.salt = new byte[8];
                    System.arraycopy(bArr, 0, this.salt, 0, bArr.length);
                    int[] iArr3 = this.chainValue;
                    iArr3[4] = iArr3[4] ^ Pack.littleEndianToInt(bArr, 0);
                    int[] iArr4 = this.chainValue;
                    iArr4[5] = Pack.littleEndianToInt(bArr, 4) ^ iArr4[5];
                } else {
                    throw new IllegalArgumentException("Salt length must be exactly 8 bytes");
                }
            }
            int[] iArr5 = this.chainValue;
            int[] iArr6 = blake2s_IV;
            iArr5[6] = iArr6[6];
            iArr5[7] = iArr6[7];
            if (bArr2 == null) {
                return;
            }
            if (bArr2.length == 8) {
                this.personalization = new byte[8];
                System.arraycopy(bArr2, 0, this.personalization, 0, bArr2.length);
                int[] iArr7 = this.chainValue;
                iArr7[6] = iArr7[6] ^ Pack.littleEndianToInt(bArr2, 0);
                int[] iArr8 = this.chainValue;
                iArr8[7] = Pack.littleEndianToInt(bArr2, 4) ^ iArr8[7];
                return;
            }
            throw new IllegalArgumentException("Personalization length must be exactly 8 bytes");
        }
    }

    private void initializeInternalState() {
        int[] iArr = this.chainValue;
        System.arraycopy(iArr, 0, this.internalState, 0, iArr.length);
        System.arraycopy(blake2s_IV, 0, this.internalState, this.chainValue.length, 4);
        int[] iArr2 = this.internalState;
        int i = this.t0;
        int[] iArr3 = blake2s_IV;
        iArr2[12] = i ^ iArr3[4];
        iArr2[13] = this.t1 ^ iArr3[5];
        iArr2[14] = this.f0 ^ iArr3[6];
        iArr2[15] = iArr3[7];
    }

    private int rotr32(int i, int i2) {
        return (i << (32 - i2)) | (i >>> i2);
    }

    public void clearKey() {
        byte[] bArr = this.key;
        if (bArr != null) {
            Arrays.fill(bArr, 0);
            Arrays.fill(this.buffer, 0);
        }
    }

    public void clearSalt() {
        byte[] bArr = this.salt;
        if (bArr != null) {
            Arrays.fill(bArr, 0);
        }
    }

    public int doFinal(byte[] bArr, int i) {
        this.f0 = -1;
        int i2 = this.t0;
        int i3 = this.bufferPos;
        this.t0 = i2 + i3;
        int i4 = this.t0;
        if (i4 < 0 && i3 > (-i4)) {
            this.t1++;
        }
        compress(this.buffer, 0);
        Arrays.fill(this.buffer, 0);
        Arrays.fill(this.internalState, 0);
        int i5 = 0;
        while (true) {
            int[] iArr = this.chainValue;
            if (i5 >= iArr.length) {
                break;
            }
            int i6 = i5 * 4;
            if (i6 >= this.digestLength) {
                break;
            }
            byte[] intToLittleEndian = Pack.intToLittleEndian(iArr[i5]);
            int i7 = this.digestLength;
            if (i6 < i7 - 4) {
                System.arraycopy(intToLittleEndian, 0, bArr, i6 + i, 4);
            } else {
                System.arraycopy(intToLittleEndian, 0, bArr, i + i6, i7 - i6);
            }
            i5++;
        }
        Arrays.fill(this.chainValue, 0);
        reset();
        return this.digestLength;
    }

    public String getAlgorithmName() {
        return "BLAKE2s";
    }

    public int getByteLength() {
        return 64;
    }

    public int getDigestSize() {
        return this.digestLength;
    }

    public void reset() {
        this.bufferPos = 0;
        this.f0 = 0;
        this.t0 = 0;
        this.t1 = 0;
        this.chainValue = null;
        Arrays.fill(this.buffer, 0);
        byte[] bArr = this.key;
        if (bArr != null) {
            System.arraycopy(bArr, 0, this.buffer, 0, bArr.length);
            this.bufferPos = 64;
        }
        init(this.salt, this.personalization, this.key);
    }

    public void update(byte b) {
        int i = this.bufferPos;
        if (64 - i == 0) {
            this.t0 += 64;
            if (this.t0 == 0) {
                this.t1++;
            }
            compress(this.buffer, 0);
            Arrays.fill(this.buffer, 0);
            this.buffer[0] = b;
            this.bufferPos = 1;
            return;
        }
        this.buffer[i] = b;
        this.bufferPos = i + 1;
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0042  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(byte[] r5, int r6, int r7) {
        /*
            r4 = this;
            if (r5 == 0) goto L_0x005f
            if (r7 != 0) goto L_0x0005
            goto L_0x005f
        L_0x0005:
            int r0 = r4.bufferPos
            r1 = 0
            if (r0 == 0) goto L_0x003b
            int r2 = 64 - r0
            if (r2 >= r7) goto L_0x0030
            byte[] r3 = r4.buffer
            java.lang.System.arraycopy(r5, r6, r3, r0, r2)
            int r0 = r4.t0
            int r0 = r0 + 64
            r4.t0 = r0
            int r0 = r4.t0
            if (r0 != 0) goto L_0x0023
            int r0 = r4.t1
            int r0 = r0 + 1
            r4.t1 = r0
        L_0x0023:
            byte[] r0 = r4.buffer
            r4.compress(r0, r1)
            r4.bufferPos = r1
            byte[] r0 = r4.buffer
            org.bouncycastle.util.Arrays.fill(r0, r1)
            goto L_0x003c
        L_0x0030:
            byte[] r1 = r4.buffer
            java.lang.System.arraycopy(r5, r6, r1, r0, r7)
        L_0x0035:
            int r5 = r4.bufferPos
            int r5 = r5 + r7
            r4.bufferPos = r5
            return
        L_0x003b:
            r2 = r1
        L_0x003c:
            int r7 = r7 + r6
            int r0 = r7 + -64
            int r6 = r6 + r2
        L_0x0040:
            if (r6 >= r0) goto L_0x0058
            int r2 = r4.t0
            int r2 = r2 + 64
            r4.t0 = r2
            int r2 = r4.t0
            if (r2 != 0) goto L_0x0052
            int r2 = r4.t1
            int r2 = r2 + 1
            r4.t1 = r2
        L_0x0052:
            r4.compress(r5, r6)
            int r6 = r6 + 64
            goto L_0x0040
        L_0x0058:
            byte[] r0 = r4.buffer
            int r7 = r7 - r6
            java.lang.System.arraycopy(r5, r6, r0, r1, r7)
            goto L_0x0035
        L_0x005f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.crypto.digests.Blake2sDigest.update(byte[], int, int):void");
    }
}
