package org.bouncycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;

public class BcSignerOutputStream extends OutputStream {
    private Signer sig;

    BcSignerOutputStream(Signer signer) {
        this.sig = signer;
    }

    /* access modifiers changed from: 0000 */
    public byte[] getSignature() throws CryptoException {
        return this.sig.generateSignature();
    }

    /* access modifiers changed from: 0000 */
    public boolean verify(byte[] bArr) {
        return this.sig.verifySignature(bArr);
    }

    public void write(int i) throws IOException {
        this.sig.update((byte) i);
    }

    public void write(byte[] bArr) throws IOException {
        this.sig.update(bArr, 0, bArr.length);
    }

    public void write(byte[] bArr, int i, int i2) throws IOException {
        this.sig.update(bArr, i, i2);
    }
}
