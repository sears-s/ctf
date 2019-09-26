package org.bouncycastle.crypto.signers;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.util.Arrays;

public class Ed25519Signer implements Signer {
    private final Buffer buffer = new Buffer();
    private boolean forSigning;
    private Ed25519PrivateKeyParameters privateKey;
    private Ed25519PublicKeyParameters publicKey;

    private static class Buffer extends ByteArrayOutputStream {
        private Buffer() {
        }

        /* access modifiers changed from: 0000 */
        public synchronized byte[] generateSignature(Ed25519PrivateKeyParameters ed25519PrivateKeyParameters, Ed25519PublicKeyParameters ed25519PublicKeyParameters) {
            byte[] bArr;
            bArr = new byte[64];
            ed25519PrivateKeyParameters.sign(0, ed25519PublicKeyParameters, null, this.buf, 0, this.count, bArr, 0);
            reset();
            return bArr;
        }

        public synchronized void reset() {
            Arrays.fill(this.buf, 0, this.count, 0);
            this.count = 0;
        }

        /* access modifiers changed from: 0000 */
        public synchronized boolean verifySignature(Ed25519PublicKeyParameters ed25519PublicKeyParameters, byte[] bArr) {
            if (64 != bArr.length) {
                return false;
            }
            boolean verify = Ed25519.verify(bArr, 0, ed25519PublicKeyParameters.getEncoded(), 0, this.buf, 0, this.count);
            reset();
            return verify;
        }
    }

    public byte[] generateSignature() {
        if (this.forSigning) {
            Ed25519PrivateKeyParameters ed25519PrivateKeyParameters = this.privateKey;
            if (ed25519PrivateKeyParameters != null) {
                return this.buffer.generateSignature(ed25519PrivateKeyParameters, this.publicKey);
            }
        }
        throw new IllegalStateException("Ed25519Signer not initialised for signature generation.");
    }

    public void init(boolean z, CipherParameters cipherParameters) {
        this.forSigning = z;
        if (z) {
            this.privateKey = (Ed25519PrivateKeyParameters) cipherParameters;
            this.publicKey = this.privateKey.generatePublicKey();
        } else {
            this.privateKey = null;
            this.publicKey = (Ed25519PublicKeyParameters) cipherParameters;
        }
        reset();
    }

    public void reset() {
        this.buffer.reset();
    }

    public void update(byte b) {
        this.buffer.write(b);
    }

    public void update(byte[] bArr, int i, int i2) {
        this.buffer.write(bArr, i, i2);
    }

    public boolean verifySignature(byte[] bArr) {
        if (!this.forSigning) {
            Ed25519PublicKeyParameters ed25519PublicKeyParameters = this.publicKey;
            if (ed25519PublicKeyParameters != null) {
                return this.buffer.verifySignature(ed25519PublicKeyParameters, bArr);
            }
        }
        throw new IllegalStateException("Ed25519Signer not initialised for verification");
    }
}
