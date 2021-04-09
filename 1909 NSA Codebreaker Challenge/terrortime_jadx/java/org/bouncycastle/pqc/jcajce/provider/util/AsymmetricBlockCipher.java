package org.bouncycastle.pqc.jcajce.provider.util;

import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;

public abstract class AsymmetricBlockCipher extends CipherSpiExt {
    protected ByteArrayOutputStream buf = new ByteArrayOutputStream();
    protected int cipherTextSize;
    protected int maxPlainTextSize;
    protected AlgorithmParameterSpec paramSpec;

    /* access modifiers changed from: protected */
    public void checkLength(int i) throws IllegalBlockSizeException {
        int size = i + this.buf.size();
        String str = " bytes).";
        if (this.opMode == 1) {
            if (size > this.maxPlainTextSize) {
                StringBuilder sb = new StringBuilder();
                sb.append("The length of the plaintext (");
                sb.append(size);
                sb.append(" bytes) is not supported by the cipher (max. ");
                sb.append(this.maxPlainTextSize);
                sb.append(str);
                throw new IllegalBlockSizeException(sb.toString());
            }
        } else if (this.opMode == 2 && size != this.cipherTextSize) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Illegal ciphertext length (expected ");
            sb2.append(this.cipherTextSize);
            sb2.append(" bytes, was ");
            sb2.append(size);
            sb2.append(str);
            throw new IllegalBlockSizeException(sb2.toString());
        }
    }

    public final int doFinal(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        if (bArr2.length >= getOutputSize(i2)) {
            byte[] doFinal = doFinal(bArr, i, i2);
            System.arraycopy(doFinal, 0, bArr2, i3, doFinal.length);
            return doFinal.length;
        }
        throw new ShortBufferException("Output buffer too short.");
    }

    public final byte[] doFinal(byte[] bArr, int i, int i2) throws IllegalBlockSizeException, BadPaddingException {
        checkLength(i2);
        update(bArr, i, i2);
        byte[] byteArray = this.buf.toByteArray();
        this.buf.reset();
        int i3 = this.opMode;
        if (i3 == 1) {
            return messageEncrypt(byteArray);
        }
        if (i3 != 2) {
            return null;
        }
        return messageDecrypt(byteArray);
    }

    public final int getBlockSize() {
        return this.opMode == 1 ? this.maxPlainTextSize : this.cipherTextSize;
    }

    public final byte[] getIV() {
        return null;
    }

    public final int getOutputSize(int i) {
        if (i + this.buf.size() > getBlockSize()) {
            return 0;
        }
        return this.opMode == 1 ? this.cipherTextSize : this.maxPlainTextSize;
    }

    public final AlgorithmParameterSpec getParameters() {
        return this.paramSpec;
    }

    /* access modifiers changed from: protected */
    public abstract void initCipherDecrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException;

    /* access modifiers changed from: protected */
    public abstract void initCipherEncrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException;

    public final void initDecrypt(Key key) throws InvalidKeyException {
        try {
            initDecrypt(key, null);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
        }
    }

    public final void initDecrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.opMode = 2;
        initCipherDecrypt(key, algorithmParameterSpec);
    }

    public final void initEncrypt(Key key) throws InvalidKeyException {
        try {
            initEncrypt(key, null, CryptoServicesRegistrar.getSecureRandom());
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
        }
    }

    public final void initEncrypt(Key key, SecureRandom secureRandom) throws InvalidKeyException {
        try {
            initEncrypt(key, null, secureRandom);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
        }
    }

    public final void initEncrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        initEncrypt(key, algorithmParameterSpec, CryptoServicesRegistrar.getSecureRandom());
    }

    public final void initEncrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.opMode = 1;
        initCipherEncrypt(key, algorithmParameterSpec, secureRandom);
    }

    /* access modifiers changed from: protected */
    public abstract byte[] messageDecrypt(byte[] bArr) throws IllegalBlockSizeException, BadPaddingException;

    /* access modifiers changed from: protected */
    public abstract byte[] messageEncrypt(byte[] bArr) throws IllegalBlockSizeException, BadPaddingException;

    /* access modifiers changed from: protected */
    public final void setMode(String str) {
    }

    /* access modifiers changed from: protected */
    public final void setPadding(String str) {
    }

    public final int update(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        update(bArr, i, i2);
        return 0;
    }

    public final byte[] update(byte[] bArr, int i, int i2) {
        if (i2 != 0) {
            this.buf.write(bArr, i, i2);
        }
        return new byte[0];
    }
}
