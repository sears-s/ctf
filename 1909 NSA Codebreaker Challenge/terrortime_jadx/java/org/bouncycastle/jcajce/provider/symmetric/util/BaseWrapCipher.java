package org.bouncycastle.jcajce.provider.symmetric.util;

import com.badguy.terrortime.BuildConfig;
import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

public abstract class BaseWrapCipher extends CipherSpi implements PBE {
    private Class[] availableSpecs;
    protected AlgorithmParameters engineParams;
    private boolean forWrapping;
    private final JcaJceHelper helper;
    private byte[] iv;
    private int ivSize;
    protected int pbeHash;
    protected int pbeIvSize;
    protected int pbeKeySize;
    protected int pbeType;
    protected Wrapper wrapEngine;
    private ErasableOutputStream wrapStream;

    protected static final class ErasableOutputStream extends ByteArrayOutputStream {
        public void erase() {
            Arrays.fill(this.buf, 0);
            reset();
        }

        public byte[] getBuf() {
            return this.buf;
        }
    }

    protected static class InvalidKeyOrParametersException extends InvalidKeyException {
        private final Throwable cause;

        InvalidKeyOrParametersException(String str, Throwable th) {
            super(str);
            this.cause = th;
        }

        public Throwable getCause() {
            return this.cause;
        }
    }

    protected BaseWrapCipher() {
        this.availableSpecs = new Class[]{GOST28147WrapParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class, IvParameterSpec.class};
        this.pbeType = 2;
        this.pbeHash = 1;
        this.engineParams = null;
        this.wrapEngine = null;
        this.wrapStream = null;
        this.helper = new BCJcaJceHelper();
    }

    protected BaseWrapCipher(Wrapper wrapper) {
        this(wrapper, 0);
    }

    protected BaseWrapCipher(Wrapper wrapper, int i) {
        this.availableSpecs = new Class[]{GOST28147WrapParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class, IvParameterSpec.class};
        this.pbeType = 2;
        this.pbeHash = 1;
        this.engineParams = null;
        this.wrapEngine = null;
        this.wrapStream = null;
        this.helper = new BCJcaJceHelper();
        this.wrapEngine = wrapper;
        this.ivSize = i;
    }

    /* access modifiers changed from: protected */
    public final AlgorithmParameters createParametersInstance(String str) throws NoSuchAlgorithmException, NoSuchProviderException {
        return this.helper.createAlgorithmParameters(str);
    }

    /* access modifiers changed from: protected */
    public int engineDoFinal(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
        ErasableOutputStream erasableOutputStream = this.wrapStream;
        if (erasableOutputStream != null) {
            erasableOutputStream.write(bArr, i, i2);
            try {
                byte[] wrap = this.forWrapping ? this.wrapEngine.wrap(this.wrapStream.getBuf(), 0, this.wrapStream.size()) : this.wrapEngine.unwrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
                if (wrap.length + i3 <= bArr2.length) {
                    System.arraycopy(wrap, 0, bArr2, i3, wrap.length);
                    int length = wrap.length;
                    this.wrapStream.erase();
                    return length;
                }
                throw new ShortBufferException("output buffer too short for input.");
            } catch (InvalidCipherTextException e) {
                throw new BadPaddingException(e.getMessage());
            } catch (Exception e2) {
                throw new IllegalBlockSizeException(e2.getMessage());
            } catch (Throwable th) {
                this.wrapStream.erase();
                throw th;
            }
        } else {
            throw new IllegalStateException("not supported in a wrapping mode");
        }
    }

    /* access modifiers changed from: protected */
    public byte[] engineDoFinal(byte[] bArr, int i, int i2) throws IllegalBlockSizeException, BadPaddingException {
        ErasableOutputStream erasableOutputStream = this.wrapStream;
        if (erasableOutputStream != null) {
            erasableOutputStream.write(bArr, i, i2);
            try {
                byte[] unwrap = this.forWrapping ? this.wrapEngine.wrap(this.wrapStream.getBuf(), 0, this.wrapStream.size()) : this.wrapEngine.unwrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
                this.wrapStream.erase();
                return unwrap;
            } catch (InvalidCipherTextException e) {
                throw new BadPaddingException(e.getMessage());
            } catch (Exception e2) {
                throw new IllegalBlockSizeException(e2.getMessage());
            } catch (Throwable th) {
                this.wrapStream.erase();
                throw th;
            }
        } else {
            throw new IllegalStateException("not supported in a wrapping mode");
        }
    }

    /* access modifiers changed from: protected */
    public int engineGetBlockSize() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public byte[] engineGetIV() {
        return Arrays.clone(this.iv);
    }

    /* access modifiers changed from: protected */
    public int engineGetKeySize(Key key) {
        return key.getEncoded().length * 8;
    }

    /* access modifiers changed from: protected */
    public int engineGetOutputSize(int i) {
        return -1;
    }

    /* access modifiers changed from: protected */
    public AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.iv != null) {
            String algorithmName = this.wrapEngine.getAlgorithmName();
            if (algorithmName.indexOf(47) >= 0) {
                algorithmName = algorithmName.substring(0, algorithmName.indexOf(47));
            }
            try {
                this.engineParams = createParametersInstance(algorithmName);
                this.engineParams.init(new IvParameterSpec(this.iv));
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
        }
        return this.engineParams;
    }

    /* access modifiers changed from: protected */
    public void engineInit(int i, Key key, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec algorithmParameterSpec = null;
        if (algorithmParameters != null) {
            int i2 = 0;
            while (true) {
                Class[] clsArr = this.availableSpecs;
                if (i2 == clsArr.length) {
                    break;
                }
                try {
                    algorithmParameterSpec = algorithmParameters.getParameterSpec(clsArr[i2]);
                    break;
                } catch (Exception e) {
                    i2++;
                }
            }
            if (algorithmParameterSpec == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("can't handle parameter ");
                sb.append(algorithmParameters.toString());
                throw new InvalidAlgorithmParameterException(sb.toString());
            }
        }
        this.engineParams = algorithmParameters;
        engineInit(i, key, algorithmParameterSpec, secureRandom);
    }

    /* access modifiers changed from: protected */
    public void engineInit(int i, Key key, SecureRandom secureRandom) throws InvalidKeyException {
        try {
            engineInit(i, key, (AlgorithmParameterSpec) null, secureRandom);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidKeyOrParametersException(e.getMessage(), e);
        }
    }

    /* access modifiers changed from: protected */
    public void engineInit(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters cipherParameters;
        if (key instanceof BCPBEKey) {
            BCPBEKey bCPBEKey = (BCPBEKey) key;
            if (algorithmParameterSpec instanceof PBEParameterSpec) {
                cipherParameters = Util.makePBEParameters(bCPBEKey, algorithmParameterSpec, this.wrapEngine.getAlgorithmName());
            } else if (bCPBEKey.getParam() != null) {
                cipherParameters = bCPBEKey.getParam();
            } else {
                throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
            }
        } else {
            cipherParameters = new KeyParameter(key.getEncoded());
        }
        if (algorithmParameterSpec instanceof IvParameterSpec) {
            this.iv = ((IvParameterSpec) algorithmParameterSpec).getIV();
            cipherParameters = new ParametersWithIV(cipherParameters, this.iv);
        }
        if (algorithmParameterSpec instanceof GOST28147WrapParameterSpec) {
            GOST28147WrapParameterSpec gOST28147WrapParameterSpec = (GOST28147WrapParameterSpec) algorithmParameterSpec;
            byte[] sBox = gOST28147WrapParameterSpec.getSBox();
            if (sBox != null) {
                cipherParameters = new ParametersWithSBox(cipherParameters, sBox);
            }
            cipherParameters = new ParametersWithUKM(cipherParameters, gOST28147WrapParameterSpec.getUKM());
        }
        if ((cipherParameters instanceof KeyParameter) && this.ivSize != 0 && (i == 3 || i == 1)) {
            this.iv = new byte[this.ivSize];
            secureRandom.nextBytes(this.iv);
            cipherParameters = new ParametersWithIV(cipherParameters, this.iv);
        }
        if (secureRandom != null) {
            cipherParameters = new ParametersWithRandom(cipherParameters, secureRandom);
        }
        if (i != 1) {
            if (i == 2) {
                this.wrapEngine.init(false, cipherParameters);
                this.wrapStream = new ErasableOutputStream();
            } else if (i == 3) {
                this.wrapEngine.init(true, cipherParameters);
                this.wrapStream = null;
            } else if (i == 4) {
                try {
                    this.wrapEngine.init(false, cipherParameters);
                    this.wrapStream = null;
                } catch (Exception e) {
                    throw new InvalidKeyOrParametersException(e.getMessage(), e);
                }
            } else {
                throw new InvalidParameterException("Unknown mode parameter passed to init.");
            }
            this.forWrapping = false;
            return;
        }
        this.wrapEngine.init(true, cipherParameters);
        this.wrapStream = new ErasableOutputStream();
        this.forWrapping = true;
    }

    /* access modifiers changed from: protected */
    public void engineSetMode(String str) throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();
        sb.append("can't support mode ");
        sb.append(str);
        throw new NoSuchAlgorithmException(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void engineSetPadding(String str) throws NoSuchPaddingException {
        StringBuilder sb = new StringBuilder();
        sb.append("Padding ");
        sb.append(str);
        sb.append(" unknown.");
        throw new NoSuchPaddingException(sb.toString());
    }

    /* access modifiers changed from: protected */
    public Key engineUnwrap(byte[] bArr, String str, int i) throws InvalidKeyException, NoSuchAlgorithmException {
        String str2 = "Unknown key type ";
        try {
            byte[] engineDoFinal = this.wrapEngine == null ? engineDoFinal(bArr, 0, bArr.length) : this.wrapEngine.unwrap(bArr, 0, bArr.length);
            if (i == 3) {
                return new SecretKeySpec(engineDoFinal, str);
            }
            if (!str.equals(BuildConfig.FLAVOR) || i != 2) {
                try {
                    KeyFactory createKeyFactory = this.helper.createKeyFactory(str);
                    if (i == 1) {
                        return createKeyFactory.generatePublic(new X509EncodedKeySpec(engineDoFinal));
                    }
                    if (i == 2) {
                        return createKeyFactory.generatePrivate(new PKCS8EncodedKeySpec(engineDoFinal));
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(str2);
                    sb.append(i);
                    throw new InvalidKeyException(sb.toString());
                } catch (NoSuchProviderException e) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str2);
                    sb2.append(e.getMessage());
                    throw new InvalidKeyException(sb2.toString());
                } catch (InvalidKeySpecException e2) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(str2);
                    sb3.append(e2.getMessage());
                    throw new InvalidKeyException(sb3.toString());
                }
            } else {
                try {
                    PrivateKeyInfo instance = PrivateKeyInfo.getInstance(engineDoFinal);
                    PrivateKey privateKey = BouncyCastleProvider.getPrivateKey(instance);
                    if (privateKey != null) {
                        return privateKey;
                    }
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("algorithm ");
                    sb4.append(instance.getPrivateKeyAlgorithm().getAlgorithm());
                    sb4.append(" not supported");
                    throw new InvalidKeyException(sb4.toString());
                } catch (Exception e3) {
                    throw new InvalidKeyException("Invalid key encoding.");
                }
            }
        } catch (InvalidCipherTextException e4) {
            throw new InvalidKeyException(e4.getMessage());
        } catch (BadPaddingException e5) {
            throw new InvalidKeyException(e5.getMessage());
        } catch (IllegalBlockSizeException e6) {
            throw new InvalidKeyException(e6.getMessage());
        }
    }

    /* access modifiers changed from: protected */
    public int engineUpdate(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws ShortBufferException {
        ErasableOutputStream erasableOutputStream = this.wrapStream;
        if (erasableOutputStream != null) {
            erasableOutputStream.write(bArr, i, i2);
            return 0;
        }
        throw new IllegalStateException("not supported in a wrapping mode");
    }

    /* access modifiers changed from: protected */
    public byte[] engineUpdate(byte[] bArr, int i, int i2) {
        ErasableOutputStream erasableOutputStream = this.wrapStream;
        if (erasableOutputStream != null) {
            erasableOutputStream.write(bArr, i, i2);
            return null;
        }
        throw new IllegalStateException("not supported in a wrapping mode");
    }

    /* access modifiers changed from: protected */
    public byte[] engineWrap(Key key) throws IllegalBlockSizeException, InvalidKeyException {
        byte[] encoded = key.getEncoded();
        if (encoded != null) {
            try {
                return this.wrapEngine == null ? engineDoFinal(encoded, 0, encoded.length) : this.wrapEngine.wrap(encoded, 0, encoded.length);
            } catch (BadPaddingException e) {
                throw new IllegalBlockSizeException(e.getMessage());
            }
        } else {
            throw new InvalidKeyException("Cannot wrap key, null encoding.");
        }
    }
}
