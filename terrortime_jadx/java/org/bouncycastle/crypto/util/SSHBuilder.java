package org.bouncycastle.crypto.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bouncycastle.util.Strings;

class SSHBuilder {
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    SSHBuilder() {
    }

    public byte[] getBytes() {
        return this.bos.toByteArray();
    }

    public void rawArray(byte[] bArr) {
        u32((long) bArr.length);
        try {
            this.bos.write(bArr);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void u32(long j) {
        this.bos.write((int) ((j >>> 24) & 255));
        this.bos.write((int) ((j >>> 16) & 255));
        this.bos.write((int) ((j >>> 8) & 255));
        this.bos.write((int) (j & 255));
    }

    public void write(byte[] bArr) {
        try {
            this.bos.write(bArr);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void writeString(String str) {
        rawArray(Strings.toByteArray(str));
    }
}
