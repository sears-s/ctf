package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.rfc8032.Ed448;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public final class Ed448PrivateKeyParameters extends AsymmetricKeyParameter {
    public static final int KEY_SIZE = 57;
    public static final int SIGNATURE_SIZE = 114;
    private final byte[] data = new byte[57];

    public Ed448PrivateKeyParameters(InputStream inputStream) throws IOException {
        super(true);
        if (57 != Streams.readFully(inputStream, this.data)) {
            throw new EOFException("EOF encountered in middle of Ed448 private key");
        }
    }

    public Ed448PrivateKeyParameters(SecureRandom secureRandom) {
        super(true);
        Ed448.generatePrivateKey(secureRandom, this.data);
    }

    public Ed448PrivateKeyParameters(byte[] bArr, int i) {
        super(true);
        System.arraycopy(bArr, i, this.data, 0, 57);
    }

    public void encode(byte[] bArr, int i) {
        System.arraycopy(this.data, 0, bArr, i, 57);
    }

    public Ed448PublicKeyParameters generatePublicKey() {
        byte[] bArr = new byte[57];
        Ed448.generatePublicKey(this.data, 0, bArr, 0);
        return new Ed448PublicKeyParameters(bArr, 0);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.data);
    }

    public void sign(int i, Ed448PublicKeyParameters ed448PublicKeyParameters, byte[] bArr, byte[] bArr2, int i2, int i3, byte[] bArr3, int i4) {
        int i5 = i;
        Ed448PublicKeyParameters ed448PublicKeyParameters2 = ed448PublicKeyParameters;
        byte[] bArr4 = new byte[57];
        if (ed448PublicKeyParameters2 == null) {
            Ed448.generatePublicKey(this.data, 0, bArr4, 0);
        } else {
            ed448PublicKeyParameters2.encode(bArr4, 0);
        }
        if (i5 == 0) {
            int i6 = i3;
            Ed448.sign(this.data, 0, bArr4, 0, bArr, bArr2, i2, i3, bArr3, i4);
        } else if (i5 != 1) {
            throw new IllegalArgumentException("algorithm");
        } else if (64 == i3) {
            Ed448.signPrehash(this.data, 0, bArr4, 0, bArr, bArr2, i2, bArr3, i4);
        } else {
            throw new IllegalArgumentException("msgLen");
        }
    }
}
