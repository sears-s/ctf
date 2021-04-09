package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Arrays;

public class Blake2xsDigest implements Xof {
    private static final int DIGEST_LENGTH = 32;
    private static final long MAX_NUMBER_BLOCKS = 4294967296L;
    public static final int UNKNOWN_DIGEST_LENGTH = 65535;
    private long blockPos;
    private byte[] buf;
    private int bufPos;
    private int digestLength;
    private int digestPos;
    private byte[] h0;
    private Blake2sDigest hash;
    private long nodeOffset;

    public Blake2xsDigest() {
        this(65535);
    }

    public Blake2xsDigest(int i) {
        this(i, null, null, null);
    }

    public Blake2xsDigest(int i, byte[] bArr) {
        this(i, bArr, null, null);
    }

    public Blake2xsDigest(int i, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        this.h0 = null;
        this.buf = new byte[32];
        this.bufPos = 32;
        this.digestPos = 0;
        this.blockPos = 0;
        if (i < 1 || i > 65535) {
            throw new IllegalArgumentException("BLAKE2xs digest length must be between 1 and 2^16-1");
        }
        this.digestLength = i;
        this.nodeOffset = computeNodeOffset();
        Blake2sDigest blake2sDigest = new Blake2sDigest(32, bArr, bArr2, bArr3, this.nodeOffset);
        this.hash = blake2sDigest;
    }

    public Blake2xsDigest(Blake2xsDigest blake2xsDigest) {
        this.h0 = null;
        this.buf = new byte[32];
        this.bufPos = 32;
        this.digestPos = 0;
        this.blockPos = 0;
        this.digestLength = blake2xsDigest.digestLength;
        this.hash = new Blake2sDigest(blake2xsDigest.hash);
        this.h0 = Arrays.clone(blake2xsDigest.h0);
        this.buf = Arrays.clone(blake2xsDigest.buf);
        this.bufPos = blake2xsDigest.bufPos;
        this.digestPos = blake2xsDigest.digestPos;
        this.blockPos = blake2xsDigest.blockPos;
        this.nodeOffset = blake2xsDigest.nodeOffset;
    }

    private long computeNodeOffset() {
        return ((long) this.digestLength) * MAX_NUMBER_BLOCKS;
    }

    private int computeStepLength() {
        int i = this.digestLength;
        if (i == 65535) {
            return 32;
        }
        return Math.min(32, i - this.digestPos);
    }

    public int doFinal(byte[] bArr, int i) {
        return doFinal(bArr, i, bArr.length);
    }

    public int doFinal(byte[] bArr, int i, int i2) {
        int doOutput = doOutput(bArr, i, i2);
        reset();
        return doOutput;
    }

    public int doOutput(byte[] bArr, int i, int i2) {
        if (this.h0 == null) {
            this.h0 = new byte[this.hash.getDigestSize()];
            this.hash.doFinal(this.h0, 0);
        }
        int i3 = this.digestLength;
        if (i3 != 65535) {
            if (this.digestPos + i2 > i3) {
                throw new IllegalArgumentException("Output length is above the digest length");
            }
        } else if ((this.blockPos << 5) >= getUnknownMaxLength()) {
            throw new IllegalArgumentException("Maximum length is 2^32 blocks of 32 bytes");
        }
        for (int i4 = 0; i4 < i2; i4++) {
            if (this.bufPos >= 32) {
                Blake2sDigest blake2sDigest = new Blake2sDigest(computeStepLength(), 32, this.nodeOffset);
                byte[] bArr2 = this.h0;
                blake2sDigest.update(bArr2, 0, bArr2.length);
                Arrays.fill(this.buf, 0);
                blake2sDigest.doFinal(this.buf, 0);
                this.bufPos = 0;
                this.nodeOffset++;
                this.blockPos++;
            }
            byte[] bArr3 = this.buf;
            int i5 = this.bufPos;
            bArr[i4] = bArr3[i5];
            this.bufPos = i5 + 1;
            this.digestPos++;
        }
        return i2;
    }

    public String getAlgorithmName() {
        return "BLAKE2xs";
    }

    public int getByteLength() {
        return this.hash.getByteLength();
    }

    public int getDigestSize() {
        return this.digestLength;
    }

    public long getUnknownMaxLength() {
        return 137438953472L;
    }

    public void reset() {
        this.hash.reset();
        this.h0 = null;
        this.bufPos = 32;
        this.digestPos = 0;
        this.blockPos = 0;
        this.nodeOffset = computeNodeOffset();
    }

    public void update(byte b) {
        this.hash.update(b);
    }

    public void update(byte[] bArr, int i, int i2) {
        this.hash.update(bArr, i, i2);
    }
}
