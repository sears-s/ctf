package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public final class Ed25519PrivateKeyParameters extends AsymmetricKeyParameter {
    public static final int KEY_SIZE = 32;
    public static final int SIGNATURE_SIZE = 64;
    private final byte[] data = new byte[32];

    public Ed25519PrivateKeyParameters(InputStream inputStream) throws IOException {
        super(true);
        if (32 != Streams.readFully(inputStream, this.data)) {
            throw new EOFException("EOF encountered in middle of Ed25519 private key");
        }
    }

    public Ed25519PrivateKeyParameters(SecureRandom secureRandom) {
        super(true);
        Ed25519.generatePrivateKey(secureRandom, this.data);
    }

    public Ed25519PrivateKeyParameters(byte[] bArr, int i) {
        super(true);
        System.arraycopy(bArr, i, this.data, 0, 32);
    }

    public void encode(byte[] bArr, int i) {
        System.arraycopy(this.data, 0, bArr, i, 32);
    }

    public Ed25519PublicKeyParameters generatePublicKey() {
        byte[] bArr = new byte[32];
        Ed25519.generatePublicKey(this.data, 0, bArr, 0);
        return new Ed25519PublicKeyParameters(bArr, 0);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.data);
    }

    public void sign(int i, Ed25519PublicKeyParameters ed25519PublicKeyParameters, byte[] bArr, byte[] bArr2, int i2, int i3, byte[] bArr3, int i4) {
        int i5 = i;
        Ed25519PublicKeyParameters ed25519PublicKeyParameters2 = ed25519PublicKeyParameters;
        byte[] bArr4 = new byte[32];
        if (ed25519PublicKeyParameters2 == null) {
            Ed25519.generatePublicKey(this.data, 0, bArr4, 0);
        } else {
            ed25519PublicKeyParameters2.encode(bArr4, 0);
        }
        if (i5 == 0) {
            int i6 = i3;
            if (bArr == null) {
                Ed25519.sign(this.data, 0, bArr4, 0, bArr2, i2, i3, bArr3, i4);
                return;
            }
            throw new IllegalArgumentException("ctx");
        } else if (i5 == 1) {
            int i7 = i3;
            Ed25519.sign(this.data, 0, bArr4, 0, bArr, bArr2, i2, i3, bArr3, i4);
        } else if (i5 != 2) {
            throw new IllegalArgumentException("algorithm");
        } else if (64 == i3) {
            Ed25519.signPrehash(this.data, 0, bArr4, 0, bArr, bArr2, i2, bArr3, i4);
        } else {
            throw new IllegalArgumentException("msgLen");
        }
    }
}
