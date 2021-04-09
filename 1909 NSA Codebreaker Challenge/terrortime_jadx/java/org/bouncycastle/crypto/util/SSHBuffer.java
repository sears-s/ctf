package org.bouncycastle.crypto.util;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;

class SSHBuffer {
    private final byte[] buffer;
    private int pos = 0;

    public SSHBuffer(byte[] bArr) {
        this.buffer = bArr;
    }

    public SSHBuffer(byte[] bArr, byte[] bArr2) {
        int i = 0;
        this.buffer = bArr2;
        while (i != bArr.length) {
            if (bArr[i] == bArr2[i]) {
                i++;
            } else {
                throw new IllegalArgumentException("magic-number incorrect");
            }
        }
        this.pos += bArr.length;
    }

    public byte[] getBuffer() {
        return Arrays.clone(this.buffer);
    }

    public boolean hasRemaining() {
        return this.pos < this.buffer.length;
    }

    public BigInteger positiveBigNum() {
        int readU32 = readU32();
        int i = this.pos;
        int i2 = i + readU32;
        byte[] bArr = this.buffer;
        if (i2 <= bArr.length) {
            byte[] bArr2 = new byte[readU32];
            System.arraycopy(bArr, i, bArr2, 0, bArr2.length);
            this.pos += readU32;
            return new BigInteger(1, bArr2);
        }
        throw new IllegalArgumentException("not enough data for big num");
    }

    public byte[] readPaddedString() {
        int readU32 = readU32();
        if (readU32 == 0) {
            return new byte[0];
        }
        int i = this.pos;
        int i2 = i + readU32;
        byte[] bArr = this.buffer;
        if (i2 <= bArr.length) {
            int i3 = (readU32 - (bArr[(i + readU32) - 1] & 255)) + i;
            this.pos = i3;
            return Arrays.copyOfRange(bArr, i, i3);
        }
        throw new IllegalArgumentException("not enough data for string");
    }

    public byte[] readString() {
        int readU32 = readU32();
        if (readU32 == 0) {
            return new byte[0];
        }
        int i = this.pos;
        int i2 = i + readU32;
        byte[] bArr = this.buffer;
        if (i2 <= bArr.length) {
            int i3 = readU32 + i;
            this.pos = i3;
            return Arrays.copyOfRange(bArr, i, i3);
        }
        throw new IllegalArgumentException("not enough data for string");
    }

    public int readU32() {
        int i = this.pos;
        int i2 = i + 4;
        byte[] bArr = this.buffer;
        if (i2 <= bArr.length) {
            this.pos = i + 1;
            int i3 = (bArr[i] & 255) << 24;
            int i4 = this.pos;
            this.pos = i4 + 1;
            byte b = i3 | ((bArr[i4] & 255) << Tnaf.POW_2_WIDTH);
            int i5 = this.pos;
            this.pos = i5 + 1;
            byte b2 = b | ((bArr[i5] & 255) << 8);
            int i6 = this.pos;
            this.pos = i6 + 1;
            return b2 | (bArr[i6] & 255);
        }
        throw new IllegalArgumentException("4 bytes for U32 exceeds buffer.");
    }
}
