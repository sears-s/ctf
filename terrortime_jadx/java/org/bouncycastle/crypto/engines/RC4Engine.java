package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;

public class RC4Engine implements StreamCipher {
    private static final int STATE_LENGTH = 256;
    private byte[] engineState = null;
    private byte[] workingKey = null;
    private int x = 0;
    private int y = 0;

    private void setKey(byte[] bArr) {
        this.workingKey = bArr;
        this.x = 0;
        this.y = 0;
        if (this.engineState == null) {
            this.engineState = new byte[256];
        }
        for (int i = 0; i < 256; i++) {
            this.engineState[i] = (byte) i;
        }
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < 256; i4++) {
            byte b = bArr[i2] & 255;
            byte[] bArr2 = this.engineState;
            i3 = (b + bArr2[i4] + i3) & 255;
            byte b2 = bArr2[i4];
            bArr2[i4] = bArr2[i3];
            bArr2[i3] = b2;
            i2 = (i2 + 1) % bArr.length;
        }
    }

    public String getAlgorithmName() {
        return "RC4";
    }

    public void init(boolean z, CipherParameters cipherParameters) {
        if (cipherParameters instanceof KeyParameter) {
            this.workingKey = ((KeyParameter) cipherParameters).getKey();
            setKey(this.workingKey);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("invalid parameter passed to RC4 init - ");
        sb.append(cipherParameters.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }

    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        } else if (i3 + i2 <= bArr2.length) {
            for (int i4 = 0; i4 < i2; i4++) {
                this.x = (this.x + 1) & 255;
                byte[] bArr3 = this.engineState;
                int i5 = this.x;
                this.y = (bArr3[i5] + this.y) & 255;
                byte b = bArr3[i5];
                int i6 = this.y;
                bArr3[i5] = bArr3[i6];
                bArr3[i6] = b;
                bArr2[i4 + i3] = (byte) (bArr3[(bArr3[i5] + bArr3[i6]) & 255] ^ bArr[i4 + i]);
            }
            return i2;
        } else {
            throw new OutputLengthException("output buffer too short");
        }
    }

    public void reset() {
        setKey(this.workingKey);
    }

    public byte returnByte(byte b) {
        this.x = (this.x + 1) & 255;
        byte[] bArr = this.engineState;
        int i = this.x;
        this.y = (bArr[i] + this.y) & 255;
        byte b2 = bArr[i];
        int i2 = this.y;
        bArr[i] = bArr[i2];
        bArr[i2] = b2;
        return (byte) (b ^ bArr[(bArr[i] + bArr[i2]) & 255]);
    }
}
