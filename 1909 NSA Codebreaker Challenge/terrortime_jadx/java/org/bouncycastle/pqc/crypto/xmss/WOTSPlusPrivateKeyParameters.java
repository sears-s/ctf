package org.bouncycastle.pqc.crypto.xmss;

final class WOTSPlusPrivateKeyParameters {
    private final byte[][] privateKey;

    protected WOTSPlusPrivateKeyParameters(WOTSPlusParameters wOTSPlusParameters, byte[][] bArr) {
        if (wOTSPlusParameters == null) {
            throw new NullPointerException("params == null");
        } else if (bArr == null) {
            throw new NullPointerException("privateKey == null");
        } else if (!XMSSUtil.hasNullPointer(bArr)) {
            String str = "wrong privateKey format";
            if (bArr.length == wOTSPlusParameters.getLen()) {
                int i = 0;
                while (i < bArr.length) {
                    if (bArr[i].length == wOTSPlusParameters.getDigestSize()) {
                        i++;
                    } else {
                        throw new IllegalArgumentException(str);
                    }
                }
                this.privateKey = XMSSUtil.cloneArray(bArr);
                return;
            }
            throw new IllegalArgumentException(str);
        } else {
            throw new NullPointerException("privateKey byte array == null");
        }
    }

    /* access modifiers changed from: protected */
    public byte[][] toByteArray() {
        return XMSSUtil.cloneArray(this.privateKey);
    }
}
