package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Pack;

public class XSalsa20Engine extends Salsa20Engine {
    public String getAlgorithmName() {
        return "XSalsa20";
    }

    /* access modifiers changed from: protected */
    public int getNonceSize() {
        return 24;
    }

    /* access modifiers changed from: protected */
    public void setKey(byte[] bArr, byte[] bArr2) {
        if (bArr == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(getAlgorithmName());
            sb.append(" doesn't support re-init with null key");
            throw new IllegalArgumentException(sb.toString());
        } else if (bArr.length == 32) {
            super.setKey(bArr, bArr2);
            Pack.littleEndianToInt(bArr2, 8, this.engineState, 8, 2);
            int[] iArr = new int[this.engineState.length];
            salsaCore(20, this.engineState, iArr);
            this.engineState[1] = iArr[0] - this.engineState[0];
            this.engineState[2] = iArr[5] - this.engineState[5];
            this.engineState[3] = iArr[10] - this.engineState[10];
            this.engineState[4] = iArr[15] - this.engineState[15];
            this.engineState[11] = iArr[6] - this.engineState[6];
            this.engineState[12] = iArr[7] - this.engineState[7];
            this.engineState[13] = iArr[8] - this.engineState[8];
            this.engineState[14] = iArr[9] - this.engineState[9];
            Pack.littleEndianToInt(bArr2, 16, this.engineState, 6, 2);
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(getAlgorithmName());
            sb2.append(" requires a 256 bit key");
            throw new IllegalArgumentException(sb2.toString());
        }
    }
}
