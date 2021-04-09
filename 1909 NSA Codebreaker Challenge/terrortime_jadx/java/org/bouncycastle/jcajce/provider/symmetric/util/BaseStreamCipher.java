package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util;

public class BaseStreamCipher extends BaseWrapCipher implements PBE {
    private Class[] availableSpecs;
    private StreamCipher cipher;
    private int digest;
    private int ivLength;
    private ParametersWithIV ivParam;
    private int keySizeInBits;
    private String pbeAlgorithm;
    private PBEParameterSpec pbeSpec;

    protected BaseStreamCipher(StreamCipher streamCipher, int i) {
        this(streamCipher, i, -1, -1);
    }

    protected BaseStreamCipher(StreamCipher streamCipher, int i, int i2, int i3) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.ivLength = 0;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.cipher = streamCipher;
        this.ivLength = i;
        this.keySizeInBits = i2;
        this.digest = i3;
    }

    /* access modifiers changed from: protected */
    public int engineDoFinal(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws ShortBufferException {
        if (i3 + i2 <= bArr2.length) {
            if (i2 != 0) {
                this.cipher.processBytes(bArr, i, i2, bArr2, i3);
            }
            this.cipher.reset();
            return i2;
        }
        throw new ShortBufferException("output buffer too short for input.");
    }

    /* access modifiers changed from: protected */
    public byte[] engineDoFinal(byte[] bArr, int i, int i2) {
        if (i2 != 0) {
            byte[] engineUpdate = engineUpdate(bArr, i, i2);
            this.cipher.reset();
            return engineUpdate;
        }
        this.cipher.reset();
        return new byte[0];
    }

    /* access modifiers changed from: protected */
    public int engineGetBlockSize() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public byte[] engineGetIV() {
        ParametersWithIV parametersWithIV = this.ivParam;
        if (parametersWithIV != null) {
            return parametersWithIV.getIV();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public int engineGetKeySize(Key key) {
        return key.getEncoded().length * 8;
    }

    /* access modifiers changed from: protected */
    public int engineGetOutputSize(int i) {
        return i;
    }

    /* access modifiers changed from: protected */
    public AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null) {
            if (this.pbeSpec != null) {
                try {
                    AlgorithmParameters createParametersInstance = createParametersInstance(this.pbeAlgorithm);
                    createParametersInstance.init(this.pbeSpec);
                    return createParametersInstance;
                } catch (Exception e) {
                    return null;
                }
            } else if (this.ivParam != null) {
                String algorithmName = this.cipher.getAlgorithmName();
                if (algorithmName.indexOf(47) >= 0) {
                    algorithmName = algorithmName.substring(0, algorithmName.indexOf(47));
                }
                String str = "ChaCha7539";
                if (algorithmName.startsWith(str)) {
                    algorithmName = str;
                } else if (algorithmName.startsWith("Grain")) {
                    algorithmName = "Grainv1";
                } else if (algorithmName.startsWith("HC")) {
                    int indexOf = algorithmName.indexOf(45);
                    StringBuilder sb = new StringBuilder();
                    sb.append(algorithmName.substring(0, indexOf));
                    sb.append(algorithmName.substring(indexOf + 1));
                    algorithmName = sb.toString();
                }
                try {
                    this.engineParams = createParametersInstance(algorithmName);
                    this.engineParams.init(new IvParameterSpec(this.ivParam.getIV()));
                } catch (Exception e2) {
                    throw new RuntimeException(e2.toString());
                }
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
        engineInit(i, key, algorithmParameterSpec, secureRandom);
        this.engineParams = algorithmParameters;
    }

    /* access modifiers changed from: protected */
    public void engineInit(int i, Key key, SecureRandom secureRandom) throws InvalidKeyException {
        try {
            engineInit(i, key, (AlgorithmParameterSpec) null, secureRandom);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidKeyException(e.getMessage());
        }
    }

    /* access modifiers changed from: protected */
    public void engineInit(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters cipherParameters;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.engineParams = null;
        if (key instanceof SecretKey) {
            if (key instanceof PKCS12Key) {
                PKCS12Key pKCS12Key = (PKCS12Key) key;
                this.pbeSpec = (PBEParameterSpec) algorithmParameterSpec;
                if ((pKCS12Key instanceof PKCS12KeyWithParameters) && this.pbeSpec == null) {
                    PKCS12KeyWithParameters pKCS12KeyWithParameters = (PKCS12KeyWithParameters) pKCS12Key;
                    this.pbeSpec = new PBEParameterSpec(pKCS12KeyWithParameters.getSalt(), pKCS12KeyWithParameters.getIterationCount());
                }
                cipherParameters = Util.makePBEParameters(pKCS12Key.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
            } else if (key instanceof BCPBEKey) {
                BCPBEKey bCPBEKey = (BCPBEKey) key;
                this.pbeAlgorithm = bCPBEKey.getOID() != null ? bCPBEKey.getOID().getId() : bCPBEKey.getAlgorithm();
                if (bCPBEKey.getParam() != null) {
                    cipherParameters = bCPBEKey.getParam();
                    this.pbeSpec = new PBEParameterSpec(bCPBEKey.getSalt(), bCPBEKey.getIterationCount());
                } else if (algorithmParameterSpec instanceof PBEParameterSpec) {
                    CipherParameters makePBEParameters = Util.makePBEParameters(bCPBEKey, algorithmParameterSpec, this.cipher.getAlgorithmName());
                    this.pbeSpec = (PBEParameterSpec) algorithmParameterSpec;
                    cipherParameters = makePBEParameters;
                } else {
                    throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
                }
                if (bCPBEKey.getIvSize() != 0) {
                    this.ivParam = (ParametersWithIV) cipherParameters;
                }
            } else if (algorithmParameterSpec == null) {
                if (this.digest <= 0) {
                    cipherParameters = new KeyParameter(key.getEncoded());
                } else {
                    throw new InvalidKeyException("Algorithm requires a PBE key");
                }
            } else if (algorithmParameterSpec instanceof IvParameterSpec) {
                CipherParameters parametersWithIV = new ParametersWithIV(new KeyParameter(key.getEncoded()), ((IvParameterSpec) algorithmParameterSpec).getIV());
                this.ivParam = (ParametersWithIV) parametersWithIV;
                cipherParameters = parametersWithIV;
            } else {
                throw new InvalidAlgorithmParameterException("unknown parameter type.");
            }
            if (this.ivLength != 0 && !(cipherParameters instanceof ParametersWithIV)) {
                if (secureRandom == null) {
                    secureRandom = CryptoServicesRegistrar.getSecureRandom();
                }
                if (i == 1 || i == 3) {
                    byte[] bArr = new byte[this.ivLength];
                    secureRandom.nextBytes(bArr);
                    ParametersWithIV parametersWithIV2 = new ParametersWithIV(cipherParameters, bArr);
                    this.ivParam = parametersWithIV2;
                    cipherParameters = parametersWithIV2;
                } else {
                    throw new InvalidAlgorithmParameterException("no IV set when one expected");
                }
            }
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            try {
                                StringBuilder sb = new StringBuilder();
                                sb.append("unknown opmode ");
                                sb.append(i);
                                sb.append(" passed");
                                throw new InvalidParameterException(sb.toString());
                            } catch (Exception e) {
                                throw new InvalidKeyException(e.getMessage());
                            }
                        }
                    }
                }
                this.cipher.init(false, cipherParameters);
                return;
            }
            this.cipher.init(true, cipherParameters);
            return;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Key for algorithm ");
        sb2.append(key.getAlgorithm());
        sb2.append(" not suitable for symmetric enryption.");
        throw new InvalidKeyException(sb2.toString());
    }

    /* access modifiers changed from: protected */
    public void engineSetMode(String str) throws NoSuchAlgorithmException {
        if (!str.equalsIgnoreCase("ECB") && !str.equals("NONE")) {
            StringBuilder sb = new StringBuilder();
            sb.append("can't support mode ");
            sb.append(str);
            throw new NoSuchAlgorithmException(sb.toString());
        }
    }

    /* access modifiers changed from: protected */
    public void engineSetPadding(String str) throws NoSuchPaddingException {
        if (!str.equalsIgnoreCase("NoPadding")) {
            StringBuilder sb = new StringBuilder();
            sb.append("Padding ");
            sb.append(str);
            sb.append(" unknown.");
            throw new NoSuchPaddingException(sb.toString());
        }
    }

    /* access modifiers changed from: protected */
    public int engineUpdate(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws ShortBufferException {
        if (i3 + i2 <= bArr2.length) {
            try {
                this.cipher.processBytes(bArr, i, i2, bArr2, i3);
                return i2;
            } catch (DataLengthException e) {
                throw new IllegalStateException(e.getMessage());
            }
        } else {
            throw new ShortBufferException("output buffer too short for input.");
        }
    }

    /* access modifiers changed from: protected */
    public byte[] engineUpdate(byte[] bArr, int i, int i2) {
        byte[] bArr2 = new byte[i2];
        this.cipher.processBytes(bArr, i, i2, bArr2, 0);
        return bArr2;
    }
}
