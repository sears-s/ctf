package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class RFC5649WrapEngine implements Wrapper {
    private BlockCipher engine;
    private byte[] extractedAIV = null;
    private boolean forWrapping;
    private byte[] highOrderIV = {-90, 89, 89, -90};
    private KeyParameter param;
    private byte[] preIV = this.highOrderIV;

    public RFC5649WrapEngine(BlockCipher blockCipher) {
        this.engine = blockCipher;
    }

    private byte[] padPlaintext(byte[] bArr) {
        int length = bArr.length;
        int i = (8 - (length % 8)) % 8;
        byte[] bArr2 = new byte[(length + i)];
        System.arraycopy(bArr, 0, bArr2, 0, length);
        if (i != 0) {
            System.arraycopy(new byte[i], 0, bArr2, length, i);
        }
        return bArr2;
    }

    private byte[] rfc3394UnwrapNoIvCheck(byte[] bArr, int i, int i2) {
        byte[] bArr2 = bArr;
        int i3 = i;
        byte[] bArr3 = new byte[8];
        byte[] bArr4 = new byte[(i2 - bArr3.length)];
        byte[] bArr5 = new byte[bArr3.length];
        byte[] bArr6 = new byte[(bArr3.length + 8)];
        System.arraycopy(bArr2, i3, bArr5, 0, bArr3.length);
        System.arraycopy(bArr2, i3 + bArr3.length, bArr4, 0, i2 - bArr3.length);
        this.engine.init(false, this.param);
        int i4 = (i2 / 8) - 1;
        for (int i5 = 5; i5 >= 0; i5--) {
            for (int i6 = i4; i6 >= 1; i6--) {
                System.arraycopy(bArr5, 0, bArr6, 0, bArr3.length);
                int i7 = (i6 - 1) * 8;
                System.arraycopy(bArr4, i7, bArr6, bArr3.length, 8);
                int i8 = (i4 * i5) + i6;
                int i9 = 1;
                while (i8 != 0) {
                    int length = bArr3.length - i9;
                    bArr6[length] = (byte) (bArr6[length] ^ ((byte) i8));
                    i8 >>>= 8;
                    i9++;
                }
                this.engine.processBlock(bArr6, 0, bArr6, 0);
                System.arraycopy(bArr6, 0, bArr5, 0, 8);
                System.arraycopy(bArr6, 8, bArr4, i7, 8);
            }
        }
        this.extractedAIV = bArr5;
        return bArr4;
    }

    public String getAlgorithmName() {
        return this.engine.getAlgorithmName();
    }

    public void init(boolean z, CipherParameters cipherParameters) {
        this.forWrapping = z;
        if (cipherParameters instanceof ParametersWithRandom) {
            cipherParameters = ((ParametersWithRandom) cipherParameters).getParameters();
        }
        if (cipherParameters instanceof KeyParameter) {
            this.param = (KeyParameter) cipherParameters;
            this.preIV = this.highOrderIV;
        } else if (cipherParameters instanceof ParametersWithIV) {
            ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
            this.preIV = parametersWithIV.getIV();
            this.param = (KeyParameter) parametersWithIV.getParameters();
            if (this.preIV.length != 4) {
                throw new IllegalArgumentException("IV length not equal to 4");
            }
        }
    }

    public byte[] unwrap(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        byte[] bArr2;
        if (!this.forWrapping) {
            int i3 = i2 / 8;
            if (i3 * 8 != i2) {
                throw new InvalidCipherTextException("unwrap data must be a multiple of 8 bytes");
            } else if (i3 != 1) {
                byte[] bArr3 = new byte[i2];
                System.arraycopy(bArr, i, bArr3, 0, i2);
                byte[] bArr4 = new byte[i2];
                if (i3 == 2) {
                    this.engine.init(false, this.param);
                    int i4 = 0;
                    while (i4 < bArr3.length) {
                        this.engine.processBlock(bArr3, i4, bArr4, i4);
                        i4 += this.engine.getBlockSize();
                    }
                    this.extractedAIV = new byte[8];
                    byte[] bArr5 = this.extractedAIV;
                    System.arraycopy(bArr4, 0, bArr5, 0, bArr5.length);
                    int length = bArr4.length;
                    byte[] bArr6 = this.extractedAIV;
                    bArr2 = new byte[(length - bArr6.length)];
                    System.arraycopy(bArr4, bArr6.length, bArr2, 0, bArr2.length);
                } else {
                    bArr2 = rfc3394UnwrapNoIvCheck(bArr, i, i2);
                }
                byte[] bArr7 = new byte[4];
                byte[] bArr8 = new byte[4];
                System.arraycopy(this.extractedAIV, 0, bArr7, 0, bArr7.length);
                System.arraycopy(this.extractedAIV, bArr7.length, bArr8, 0, bArr8.length);
                int bigEndianToInt = Pack.bigEndianToInt(bArr8, 0);
                boolean constantTimeAreEqual = Arrays.constantTimeAreEqual(bArr7, this.preIV);
                int length2 = bArr2.length;
                if (bigEndianToInt <= length2 - 8) {
                    constantTimeAreEqual = false;
                }
                if (bigEndianToInt > length2) {
                    constantTimeAreEqual = false;
                }
                int i5 = length2 - bigEndianToInt;
                if (i5 >= bArr2.length) {
                    i5 = bArr2.length;
                    constantTimeAreEqual = false;
                }
                byte[] bArr9 = new byte[i5];
                byte[] bArr10 = new byte[i5];
                System.arraycopy(bArr2, bArr2.length - i5, bArr10, 0, i5);
                if (!Arrays.constantTimeAreEqual(bArr10, bArr9)) {
                    constantTimeAreEqual = false;
                }
                if (constantTimeAreEqual) {
                    byte[] bArr11 = new byte[bigEndianToInt];
                    System.arraycopy(bArr2, 0, bArr11, 0, bArr11.length);
                    return bArr11;
                }
                throw new InvalidCipherTextException("checksum failed");
            } else {
                throw new InvalidCipherTextException("unwrap data must be at least 16 bytes");
            }
        } else {
            throw new IllegalStateException("not set for unwrapping");
        }
    }

    public byte[] wrap(byte[] bArr, int i, int i2) {
        if (this.forWrapping) {
            byte[] bArr2 = new byte[8];
            byte[] intToBigEndian = Pack.intToBigEndian(i2);
            byte[] bArr3 = this.preIV;
            int i3 = 0;
            System.arraycopy(bArr3, 0, bArr2, 0, bArr3.length);
            System.arraycopy(intToBigEndian, 0, bArr2, this.preIV.length, intToBigEndian.length);
            byte[] bArr4 = new byte[i2];
            System.arraycopy(bArr, i, bArr4, 0, i2);
            byte[] padPlaintext = padPlaintext(bArr4);
            if (padPlaintext.length == 8) {
                byte[] bArr5 = new byte[(padPlaintext.length + bArr2.length)];
                System.arraycopy(bArr2, 0, bArr5, 0, bArr2.length);
                System.arraycopy(padPlaintext, 0, bArr5, bArr2.length, padPlaintext.length);
                this.engine.init(true, this.param);
                while (i3 < bArr5.length) {
                    this.engine.processBlock(bArr5, i3, bArr5, i3);
                    i3 += this.engine.getBlockSize();
                }
                return bArr5;
            }
            RFC3394WrapEngine rFC3394WrapEngine = new RFC3394WrapEngine(this.engine);
            rFC3394WrapEngine.init(true, new ParametersWithIV(this.param, bArr2));
            return rFC3394WrapEngine.wrap(padPlaintext, 0, padPlaintext.length);
        }
        throw new IllegalStateException("not set for wrapping");
    }
}
