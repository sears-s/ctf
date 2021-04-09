package org.bouncycastle.jcajce.provider.symmetric.util;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import org.bouncycastle.asn1.cms.GCMParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.CTSBlockCipher;
import org.bouncycastle.crypto.modes.EAXBlockCipher;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.GOFBBlockCipher;
import org.bouncycastle.crypto.modes.KCCMBlockCipher;
import org.bouncycastle.crypto.modes.KCTRBlockCipher;
import org.bouncycastle.crypto.modes.KGCMBlockCipher;
import org.bouncycastle.crypto.modes.OCBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.OpenPGPCFBBlockCipher;
import org.bouncycastle.crypto.modes.PGPCFBBlockCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.ISO10126d2Padding;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.TBCPadding;
import org.bouncycastle.crypto.paddings.X923Padding;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class BaseBlockCipher extends BaseWrapCipher implements PBE {
    private static final int BUF_SIZE = 512;
    private static final Class gcmSpecClass = ClassUtil.loadClass(BaseBlockCipher.class, "javax.crypto.spec.GCMParameterSpec");
    private AEADParameters aeadParams;
    private Class[] availableSpecs;
    private BlockCipher baseEngine;
    private GenericBlockCipher cipher;
    private int digest;
    private BlockCipherProvider engineProvider;
    private boolean fixedIv;
    private int ivLength;
    private ParametersWithIV ivParam;
    private int keySizeInBits;
    private String modeName;
    private boolean padded;
    private String pbeAlgorithm;
    private PBEParameterSpec pbeSpec;
    private int scheme;

    private static class AEADGenericBlockCipher implements GenericBlockCipher {
        private static final Constructor aeadBadTagConstructor;
        /* access modifiers changed from: private */
        public AEADBlockCipher cipher;

        static {
            Class loadClass = ClassUtil.loadClass(BaseBlockCipher.class, "javax.crypto.AEADBadTagException");
            aeadBadTagConstructor = loadClass != null ? findExceptionConstructor(loadClass) : null;
        }

        AEADGenericBlockCipher(AEADBlockCipher aEADBlockCipher) {
            this.cipher = aEADBlockCipher;
        }

        private static Constructor findExceptionConstructor(Class cls) {
            try {
                return cls.getConstructor(new Class[]{String.class});
            } catch (Exception e) {
                return null;
            }
        }

        public int doFinal(byte[] bArr, int i) throws IllegalStateException, BadPaddingException {
            Throwable th;
            try {
                return this.cipher.doFinal(bArr, i);
            } catch (InvalidCipherTextException e) {
                Constructor constructor = aeadBadTagConstructor;
                if (constructor != null) {
                    try {
                        th = (BadPaddingException) constructor.newInstance(new Object[]{e.getMessage()});
                    } catch (Exception e2) {
                        th = null;
                    }
                    if (th != null) {
                        throw th;
                    }
                }
                throw new BadPaddingException(e.getMessage());
            }
        }

        public String getAlgorithmName() {
            return this.cipher.getUnderlyingCipher().getAlgorithmName();
        }

        public int getOutputSize(int i) {
            return this.cipher.getOutputSize(i);
        }

        public BlockCipher getUnderlyingCipher() {
            return this.cipher.getUnderlyingCipher();
        }

        public int getUpdateOutputSize(int i) {
            return this.cipher.getUpdateOutputSize(i);
        }

        public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
            this.cipher.init(z, cipherParameters);
        }

        public int processByte(byte b, byte[] bArr, int i) throws DataLengthException {
            return this.cipher.processByte(b, bArr, i);
        }

        public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
            return this.cipher.processBytes(bArr, i, i2, bArr2, i3);
        }

        public void updateAAD(byte[] bArr, int i, int i2) {
            this.cipher.processAADBytes(bArr, i, i2);
        }

        public boolean wrapOnNoPadding() {
            return false;
        }
    }

    private static class BufferedGenericBlockCipher implements GenericBlockCipher {
        private BufferedBlockCipher cipher;

        BufferedGenericBlockCipher(BlockCipher blockCipher) {
            this.cipher = new PaddedBufferedBlockCipher(blockCipher);
        }

        BufferedGenericBlockCipher(BlockCipher blockCipher, BlockCipherPadding blockCipherPadding) {
            this.cipher = new PaddedBufferedBlockCipher(blockCipher, blockCipherPadding);
        }

        BufferedGenericBlockCipher(BufferedBlockCipher bufferedBlockCipher) {
            this.cipher = bufferedBlockCipher;
        }

        public int doFinal(byte[] bArr, int i) throws IllegalStateException, BadPaddingException {
            try {
                return this.cipher.doFinal(bArr, i);
            } catch (InvalidCipherTextException e) {
                throw new BadPaddingException(e.getMessage());
            }
        }

        public String getAlgorithmName() {
            return this.cipher.getUnderlyingCipher().getAlgorithmName();
        }

        public int getOutputSize(int i) {
            return this.cipher.getOutputSize(i);
        }

        public BlockCipher getUnderlyingCipher() {
            return this.cipher.getUnderlyingCipher();
        }

        public int getUpdateOutputSize(int i) {
            return this.cipher.getUpdateOutputSize(i);
        }

        public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
            this.cipher.init(z, cipherParameters);
        }

        public int processByte(byte b, byte[] bArr, int i) throws DataLengthException {
            return this.cipher.processByte(b, bArr, i);
        }

        public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
            return this.cipher.processBytes(bArr, i, i2, bArr2, i3);
        }

        public void updateAAD(byte[] bArr, int i, int i2) {
            throw new UnsupportedOperationException("AAD is not supported in the current mode.");
        }

        public boolean wrapOnNoPadding() {
            return !(this.cipher instanceof CTSBlockCipher);
        }
    }

    private interface GenericBlockCipher {
        int doFinal(byte[] bArr, int i) throws IllegalStateException, BadPaddingException;

        String getAlgorithmName();

        int getOutputSize(int i);

        BlockCipher getUnderlyingCipher();

        int getUpdateOutputSize(int i);

        void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException;

        int processByte(byte b, byte[] bArr, int i) throws DataLengthException;

        int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException;

        void updateAAD(byte[] bArr, int i, int i2);

        boolean wrapOnNoPadding();
    }

    protected BaseBlockCipher(BlockCipher blockCipher) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = blockCipher;
        this.cipher = new BufferedGenericBlockCipher(blockCipher);
    }

    protected BaseBlockCipher(BlockCipher blockCipher, int i) {
        this(blockCipher, true, i);
    }

    protected BaseBlockCipher(BlockCipher blockCipher, int i, int i2, int i3, int i4) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = blockCipher;
        this.scheme = i;
        this.digest = i2;
        this.keySizeInBits = i3;
        this.ivLength = i4;
        this.cipher = new BufferedGenericBlockCipher(blockCipher);
    }

    protected BaseBlockCipher(BlockCipher blockCipher, boolean z, int i) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = blockCipher;
        this.fixedIv = z;
        this.cipher = new BufferedGenericBlockCipher(blockCipher);
        this.ivLength = i / 8;
    }

    protected BaseBlockCipher(BufferedBlockCipher bufferedBlockCipher, int i) {
        this(bufferedBlockCipher, true, i);
    }

    protected BaseBlockCipher(BufferedBlockCipher bufferedBlockCipher, boolean z, int i) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = bufferedBlockCipher.getUnderlyingCipher();
        this.cipher = new BufferedGenericBlockCipher(bufferedBlockCipher);
        this.fixedIv = z;
        this.ivLength = i / 8;
    }

    protected BaseBlockCipher(AEADBlockCipher aEADBlockCipher) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = aEADBlockCipher.getUnderlyingCipher();
        this.ivLength = this.baseEngine.getBlockSize();
        this.cipher = new AEADGenericBlockCipher(aEADBlockCipher);
    }

    protected BaseBlockCipher(AEADBlockCipher aEADBlockCipher, boolean z, int i) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = aEADBlockCipher.getUnderlyingCipher();
        this.fixedIv = z;
        this.ivLength = i;
        this.cipher = new AEADGenericBlockCipher(aEADBlockCipher);
    }

    protected BaseBlockCipher(BlockCipherProvider blockCipherProvider) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = blockCipherProvider.get();
        this.engineProvider = blockCipherProvider;
        this.cipher = new BufferedGenericBlockCipher(blockCipherProvider.get());
    }

    private CipherParameters adjustParameters(AlgorithmParameterSpec algorithmParameterSpec, CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithIV) {
            CipherParameters parameters = ((ParametersWithIV) cipherParameters).getParameters();
            if (algorithmParameterSpec instanceof IvParameterSpec) {
                this.ivParam = new ParametersWithIV(parameters, ((IvParameterSpec) algorithmParameterSpec).getIV());
            } else if (!(algorithmParameterSpec instanceof GOST28147ParameterSpec)) {
                return cipherParameters;
            } else {
                GOST28147ParameterSpec gOST28147ParameterSpec = (GOST28147ParameterSpec) algorithmParameterSpec;
                ParametersWithSBox parametersWithSBox = new ParametersWithSBox(cipherParameters, gOST28147ParameterSpec.getSbox());
                if (gOST28147ParameterSpec.getIV() == null || this.ivLength == 0) {
                    return parametersWithSBox;
                }
                this.ivParam = new ParametersWithIV(parameters, gOST28147ParameterSpec.getIV());
                return this.ivParam;
            }
        } else if (algorithmParameterSpec instanceof IvParameterSpec) {
            this.ivParam = new ParametersWithIV(cipherParameters, ((IvParameterSpec) algorithmParameterSpec).getIV());
        } else if (!(algorithmParameterSpec instanceof GOST28147ParameterSpec)) {
            return cipherParameters;
        } else {
            GOST28147ParameterSpec gOST28147ParameterSpec2 = (GOST28147ParameterSpec) algorithmParameterSpec;
            ParametersWithSBox parametersWithSBox2 = new ParametersWithSBox(cipherParameters, gOST28147ParameterSpec2.getSbox());
            return (gOST28147ParameterSpec2.getIV() == null || this.ivLength == 0) ? parametersWithSBox2 : new ParametersWithIV(parametersWithSBox2, gOST28147ParameterSpec2.getIV());
        }
        return this.ivParam;
    }

    private boolean isAEADModeName(String str) {
        return "CCM".equals(str) || "EAX".equals(str) || "GCM".equals(str) || "OCB".equals(str);
    }

    /* access modifiers changed from: protected */
    public int engineDoFinal(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
        int i4;
        if (engineGetOutputSize(i2) + i3 <= bArr2.length) {
            if (i2 != 0) {
                try {
                    i4 = this.cipher.processBytes(bArr, i, i2, bArr2, i3);
                } catch (OutputLengthException e) {
                    throw new IllegalBlockSizeException(e.getMessage());
                } catch (DataLengthException e2) {
                    throw new IllegalBlockSizeException(e2.getMessage());
                }
            } else {
                i4 = 0;
            }
            return i4 + this.cipher.doFinal(bArr2, i3 + i4);
        }
        throw new ShortBufferException("output buffer too short for input.");
    }

    /* access modifiers changed from: protected */
    public byte[] engineDoFinal(byte[] bArr, int i, int i2) throws IllegalBlockSizeException, BadPaddingException {
        byte[] bArr2 = new byte[engineGetOutputSize(i2)];
        int processBytes = i2 != 0 ? this.cipher.processBytes(bArr, i, i2, bArr2, 0) : 0;
        try {
            int doFinal = processBytes + this.cipher.doFinal(bArr2, processBytes);
            if (doFinal == bArr2.length) {
                return bArr2;
            }
            byte[] bArr3 = new byte[doFinal];
            System.arraycopy(bArr2, 0, bArr3, 0, doFinal);
            return bArr3;
        } catch (DataLengthException e) {
            throw new IllegalBlockSizeException(e.getMessage());
        }
    }

    /* access modifiers changed from: protected */
    public int engineGetBlockSize() {
        return this.baseEngine.getBlockSize();
    }

    /* access modifiers changed from: protected */
    public byte[] engineGetIV() {
        AEADParameters aEADParameters = this.aeadParams;
        if (aEADParameters != null) {
            return aEADParameters.getNonce();
        }
        ParametersWithIV parametersWithIV = this.ivParam;
        return parametersWithIV != null ? parametersWithIV.getIV() : null;
    }

    /* access modifiers changed from: protected */
    public int engineGetKeySize(Key key) {
        return key.getEncoded().length * 8;
    }

    /* access modifiers changed from: protected */
    public int engineGetOutputSize(int i) {
        return this.cipher.getOutputSize(i);
    }

    /* access modifiers changed from: protected */
    public AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null) {
            if (this.pbeSpec != null) {
                try {
                    this.engineParams = createParametersInstance(this.pbeAlgorithm);
                    this.engineParams.init(this.pbeSpec);
                } catch (Exception e) {
                    return null;
                }
            } else if (this.aeadParams != null) {
                try {
                    this.engineParams = createParametersInstance("GCM");
                    this.engineParams.init(new GCMParameters(this.aeadParams.getNonce(), this.aeadParams.getMacSize() / 8).getEncoded());
                } catch (Exception e2) {
                    throw new RuntimeException(e2.toString());
                }
            } else if (this.ivParam != null) {
                String algorithmName = this.cipher.getUnderlyingCipher().getAlgorithmName();
                if (algorithmName.indexOf(47) >= 0) {
                    algorithmName = algorithmName.substring(0, algorithmName.indexOf(47));
                }
                try {
                    this.engineParams = createParametersInstance(algorithmName);
                    this.engineParams.init(new IvParameterSpec(this.ivParam.getIV()));
                } catch (Exception e3) {
                    throw new RuntimeException(e3.toString());
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
                if (clsArr[i2] != null) {
                    try {
                        algorithmParameterSpec = algorithmParameters.getParameterSpec(clsArr[i2]);
                        break;
                    } catch (Exception e) {
                    }
                }
                i2++;
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

    /* JADX WARNING: type inference failed for: r5v0 */
    /* JADX WARNING: type inference failed for: r5v1, types: [org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r5v2, types: [org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r2v2, types: [org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r2v3, types: [org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r5v4, types: [org.bouncycastle.crypto.params.ParametersWithRandom] */
    /* JADX WARNING: type inference failed for: r2v7 */
    /* JADX WARNING: type inference failed for: r2v8 */
    /* JADX WARNING: type inference failed for: r5v7 */
    /* JADX WARNING: type inference failed for: r7v9, types: [org.bouncycastle.crypto.params.AEADParameters] */
    /* JADX WARNING: type inference failed for: r5v9 */
    /* JADX WARNING: type inference failed for: r5v11, types: [org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r5v12, types: [org.bouncycastle.crypto.params.RC5Parameters, org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r5v15, types: [org.bouncycastle.crypto.params.RC2Parameters, org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r5v18 */
    /* JADX WARNING: type inference failed for: r5v19, types: [org.bouncycastle.crypto.params.ParametersWithSBox, org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r3v27 */
    /* JADX WARNING: type inference failed for: r5v22 */
    /* JADX WARNING: type inference failed for: r2v77 */
    /* JADX WARNING: type inference failed for: r3v28, types: [org.bouncycastle.crypto.params.ParametersWithIV] */
    /* JADX WARNING: type inference failed for: r3v29, types: [org.bouncycastle.crypto.params.ParametersWithIV] */
    /* JADX WARNING: type inference failed for: r5v25, types: [org.bouncycastle.crypto.params.AEADParameters] */
    /* JADX WARNING: type inference failed for: r3v38 */
    /* JADX WARNING: type inference failed for: r5v29 */
    /* JADX WARNING: type inference failed for: r5v32, types: [org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r7v22, types: [org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r7v23 */
    /* JADX WARNING: type inference failed for: r5v33 */
    /* JADX WARNING: type inference failed for: r7v25, types: [org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r5v36 */
    /* JADX WARNING: type inference failed for: r7v28 */
    /* JADX WARNING: type inference failed for: r5v38, types: [org.bouncycastle.crypto.params.KeyParameter] */
    /* JADX WARNING: type inference failed for: r5v42, types: [org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r5v45 */
    /* JADX WARNING: type inference failed for: r5v46, types: [org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r5v48, types: [org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r5v52, types: [org.bouncycastle.crypto.CipherParameters] */
    /* JADX WARNING: type inference failed for: r5v53, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r5v54, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r5v55 */
    /* JADX WARNING: type inference failed for: r5v56 */
    /* JADX WARNING: type inference failed for: r3v43 */
    /* JADX WARNING: type inference failed for: r3v44 */
    /* JADX WARNING: type inference failed for: r5v57 */
    /* JADX WARNING: type inference failed for: r5v58 */
    /* JADX WARNING: type inference failed for: r7v64 */
    /* JADX WARNING: type inference failed for: r5v59 */
    /* JADX WARNING: type inference failed for: r5v60 */
    /* JADX WARNING: type inference failed for: r5v61 */
    /* JADX WARNING: type inference failed for: r5v62 */
    /* JADX WARNING: type inference failed for: r5v63 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00aa, code lost:
        if ((r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV) != false) goto L_0x00ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00f7, code lost:
        if ((r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV) != false) goto L_0x00ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0145, code lost:
        if ((r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV) != false) goto L_0x00ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x01fd, code lost:
        if ((r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV) != false) goto L_0x00ac;
     */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r5v0
  assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], ?[OBJECT, ARRAY], org.bouncycastle.crypto.params.RC5Parameters, org.bouncycastle.crypto.params.RC2Parameters, org.bouncycastle.crypto.params.ParametersWithSBox, org.bouncycastle.crypto.params.AEADParameters, org.bouncycastle.crypto.CipherParameters, java.lang.String, org.bouncycastle.crypto.params.KeyParameter]
  uses: [?[OBJECT, ARRAY], org.bouncycastle.crypto.CipherParameters, org.bouncycastle.crypto.params.AEADParameters, java.lang.String]
  mth insns count: 510
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0245  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x0483  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0488  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x04ca  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0500 A[Catch:{ Exception -> 0x04ed }] */
    /* JADX WARNING: Removed duplicated region for block: B:247:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0206  */
    /* JADX WARNING: Unknown variable types count: 19 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void engineInit(int r21, java.security.Key r22, java.security.spec.AlgorithmParameterSpec r23, java.security.SecureRandom r24) throws java.security.InvalidKeyException, java.security.InvalidAlgorithmParameterException {
        /*
            r20 = this;
            r1 = r20
            r0 = r21
            r2 = r22
            r3 = r23
            r4 = r24
            r5 = 0
            r1.pbeSpec = r5
            r1.pbeAlgorithm = r5
            r1.engineParams = r5
            r1.aeadParams = r5
            boolean r6 = r2 instanceof javax.crypto.SecretKey
            if (r6 != 0) goto L_0x0039
            java.security.InvalidKeyException r0 = new java.security.InvalidKeyException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Key for algorithm "
            r3.append(r4)
            if (r2 == 0) goto L_0x0029
            java.lang.String r5 = r22.getAlgorithm()
        L_0x0029:
            r3.append(r5)
            java.lang.String r2 = " not suitable for symmetric enryption."
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            r0.<init>(r2)
            throw r0
        L_0x0039:
            java.lang.String r6 = "RC5-64"
            if (r3 != 0) goto L_0x0052
            org.bouncycastle.crypto.BlockCipher r7 = r1.baseEngine
            java.lang.String r7 = r7.getAlgorithmName()
            boolean r7 = r7.startsWith(r6)
            if (r7 != 0) goto L_0x004a
            goto L_0x0052
        L_0x004a:
            java.security.InvalidAlgorithmParameterException r0 = new java.security.InvalidAlgorithmParameterException
            java.lang.String r2 = "RC5 requires an RC5ParametersSpec to be passed in."
            r0.<init>(r2)
            throw r0
        L_0x0052:
            int r7 = r1.scheme
            r8 = 4
            java.lang.String r9 = "Algorithm requires a PBE key"
            r10 = 2
            r11 = 1
            if (r7 == r10) goto L_0x0167
            boolean r12 = r2 instanceof org.bouncycastle.jcajce.PKCS12Key
            if (r12 == 0) goto L_0x0061
            goto L_0x0167
        L_0x0061:
            boolean r12 = r2 instanceof org.bouncycastle.jcajce.PBKDF1Key
            if (r12 == 0) goto L_0x00b3
            r5 = r2
            org.bouncycastle.jcajce.PBKDF1Key r5 = (org.bouncycastle.jcajce.PBKDF1Key) r5
            boolean r7 = r3 instanceof javax.crypto.spec.PBEParameterSpec
            if (r7 == 0) goto L_0x0071
            r7 = r3
            javax.crypto.spec.PBEParameterSpec r7 = (javax.crypto.spec.PBEParameterSpec) r7
            r1.pbeSpec = r7
        L_0x0071:
            boolean r7 = r5 instanceof org.bouncycastle.jcajce.PBKDF1KeyWithParameters
            if (r7 == 0) goto L_0x008b
            javax.crypto.spec.PBEParameterSpec r7 = r1.pbeSpec
            if (r7 != 0) goto L_0x008b
            javax.crypto.spec.PBEParameterSpec r7 = new javax.crypto.spec.PBEParameterSpec
            r9 = r5
            org.bouncycastle.jcajce.PBKDF1KeyWithParameters r9 = (org.bouncycastle.jcajce.PBKDF1KeyWithParameters) r9
            byte[] r12 = r9.getSalt()
            int r9 = r9.getIterationCount()
            r7.<init>(r12, r9)
            r1.pbeSpec = r7
        L_0x008b:
            byte[] r13 = r5.getEncoded()
            r14 = 0
            int r15 = r1.digest
            int r5 = r1.keySizeInBits
            int r7 = r1.ivLength
            int r17 = r7 * 8
            javax.crypto.spec.PBEParameterSpec r7 = r1.pbeSpec
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$GenericBlockCipher r9 = r1.cipher
            java.lang.String r19 = r9.getAlgorithmName()
            r16 = r5
            r18 = r7
            org.bouncycastle.crypto.CipherParameters r5 = org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEParameters(r13, r14, r15, r16, r17, r18, r19)
            boolean r7 = r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV
            if (r7 == 0) goto L_0x0201
        L_0x00ac:
            r7 = r5
            org.bouncycastle.crypto.params.ParametersWithIV r7 = (org.bouncycastle.crypto.params.ParametersWithIV) r7
            r1.ivParam = r7
            goto L_0x0201
        L_0x00b3:
            boolean r12 = r2 instanceof org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey
            if (r12 == 0) goto L_0x0102
            r5 = r2
            org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey r5 = (org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey) r5
            org.bouncycastle.asn1.ASN1ObjectIdentifier r7 = r5.getOID()
            if (r7 == 0) goto L_0x00c9
            org.bouncycastle.asn1.ASN1ObjectIdentifier r7 = r5.getOID()
            java.lang.String r7 = r7.getId()
            goto L_0x00cd
        L_0x00c9:
            java.lang.String r7 = r5.getAlgorithm()
        L_0x00cd:
            r1.pbeAlgorithm = r7
            org.bouncycastle.crypto.CipherParameters r7 = r5.getParam()
            if (r7 == 0) goto L_0x00de
            org.bouncycastle.crypto.CipherParameters r5 = r5.getParam()
            org.bouncycastle.crypto.CipherParameters r5 = r1.adjustParameters(r3, r5)
            goto L_0x00f5
        L_0x00de:
            boolean r7 = r3 instanceof javax.crypto.spec.PBEParameterSpec
            if (r7 == 0) goto L_0x00fa
            r7 = r3
            javax.crypto.spec.PBEParameterSpec r7 = (javax.crypto.spec.PBEParameterSpec) r7
            r1.pbeSpec = r7
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$GenericBlockCipher r7 = r1.cipher
            org.bouncycastle.crypto.BlockCipher r7 = r7.getUnderlyingCipher()
            java.lang.String r7 = r7.getAlgorithmName()
            org.bouncycastle.crypto.CipherParameters r5 = org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEParameters(r5, r3, r7)
        L_0x00f5:
            boolean r7 = r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV
            if (r7 == 0) goto L_0x0201
            goto L_0x00ac
        L_0x00fa:
            java.security.InvalidAlgorithmParameterException r0 = new java.security.InvalidAlgorithmParameterException
            java.lang.String r2 = "PBE requires PBE parameters to be set."
            r0.<init>(r2)
            throw r0
        L_0x0102:
            boolean r12 = r2 instanceof javax.crypto.interfaces.PBEKey
            if (r12 == 0) goto L_0x0149
            r5 = r2
            javax.crypto.interfaces.PBEKey r5 = (javax.crypto.interfaces.PBEKey) r5
            r7 = r3
            javax.crypto.spec.PBEParameterSpec r7 = (javax.crypto.spec.PBEParameterSpec) r7
            r1.pbeSpec = r7
            boolean r7 = r5 instanceof org.bouncycastle.jcajce.PKCS12KeyWithParameters
            if (r7 == 0) goto L_0x0125
            javax.crypto.spec.PBEParameterSpec r7 = r1.pbeSpec
            if (r7 != 0) goto L_0x0125
            javax.crypto.spec.PBEParameterSpec r7 = new javax.crypto.spec.PBEParameterSpec
            byte[] r9 = r5.getSalt()
            int r12 = r5.getIterationCount()
            r7.<init>(r9, r12)
            r1.pbeSpec = r7
        L_0x0125:
            byte[] r13 = r5.getEncoded()
            int r14 = r1.scheme
            int r15 = r1.digest
            int r5 = r1.keySizeInBits
            int r7 = r1.ivLength
            int r17 = r7 * 8
            javax.crypto.spec.PBEParameterSpec r7 = r1.pbeSpec
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$GenericBlockCipher r9 = r1.cipher
            java.lang.String r19 = r9.getAlgorithmName()
            r16 = r5
            r18 = r7
            org.bouncycastle.crypto.CipherParameters r5 = org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEParameters(r13, r14, r15, r16, r17, r18, r19)
            boolean r7 = r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV
            if (r7 == 0) goto L_0x0201
            goto L_0x00ac
        L_0x0149:
            boolean r12 = r2 instanceof org.bouncycastle.jcajce.spec.RepeatedSecretKeySpec
            if (r12 != 0) goto L_0x0201
            if (r7 == 0) goto L_0x0161
            if (r7 == r8) goto L_0x0161
            if (r7 == r11) goto L_0x0161
            r5 = 5
            if (r7 == r5) goto L_0x0161
            org.bouncycastle.crypto.params.KeyParameter r5 = new org.bouncycastle.crypto.params.KeyParameter
            byte[] r7 = r22.getEncoded()
            r5.<init>(r7)
            goto L_0x0201
        L_0x0161:
            java.security.InvalidKeyException r0 = new java.security.InvalidKeyException
            r0.<init>(r9)
            throw r0
        L_0x0167:
            r5 = r2
            javax.crypto.SecretKey r5 = (javax.crypto.SecretKey) r5     // Catch:{ Exception -> 0x0533 }
            boolean r7 = r3 instanceof javax.crypto.spec.PBEParameterSpec
            if (r7 == 0) goto L_0x0173
            r7 = r3
            javax.crypto.spec.PBEParameterSpec r7 = (javax.crypto.spec.PBEParameterSpec) r7
            r1.pbeSpec = r7
        L_0x0173:
            boolean r7 = r5 instanceof javax.crypto.interfaces.PBEKey
            if (r7 == 0) goto L_0x019c
            javax.crypto.spec.PBEParameterSpec r12 = r1.pbeSpec
            if (r12 != 0) goto L_0x019c
            r12 = r5
            javax.crypto.interfaces.PBEKey r12 = (javax.crypto.interfaces.PBEKey) r12
            byte[] r13 = r12.getSalt()
            if (r13 == 0) goto L_0x0194
            javax.crypto.spec.PBEParameterSpec r13 = new javax.crypto.spec.PBEParameterSpec
            byte[] r14 = r12.getSalt()
            int r12 = r12.getIterationCount()
            r13.<init>(r14, r12)
            r1.pbeSpec = r13
            goto L_0x019c
        L_0x0194:
            java.security.InvalidAlgorithmParameterException r0 = new java.security.InvalidAlgorithmParameterException
            java.lang.String r2 = "PBEKey requires parameters to specify salt"
            r0.<init>(r2)
            throw r0
        L_0x019c:
            javax.crypto.spec.PBEParameterSpec r12 = r1.pbeSpec
            if (r12 != 0) goto L_0x01a9
            if (r7 == 0) goto L_0x01a3
            goto L_0x01a9
        L_0x01a3:
            java.security.InvalidKeyException r0 = new java.security.InvalidKeyException
            r0.<init>(r9)
            throw r0
        L_0x01a9:
            boolean r7 = r2 instanceof org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey
            if (r7 == 0) goto L_0x01e0
            r7 = r2
            org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey r7 = (org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey) r7
            org.bouncycastle.crypto.CipherParameters r7 = r7.getParam()
            boolean r9 = r7 instanceof org.bouncycastle.crypto.params.ParametersWithIV
            if (r9 == 0) goto L_0x01b9
            goto L_0x01d6
        L_0x01b9:
            if (r7 != 0) goto L_0x01d8
            byte[] r12 = r5.getEncoded()
            r13 = 2
            int r14 = r1.digest
            int r15 = r1.keySizeInBits
            int r5 = r1.ivLength
            int r16 = r5 * 8
            javax.crypto.spec.PBEParameterSpec r5 = r1.pbeSpec
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$GenericBlockCipher r7 = r1.cipher
            java.lang.String r18 = r7.getAlgorithmName()
            r17 = r5
            org.bouncycastle.crypto.CipherParameters r7 = org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEParameters(r12, r13, r14, r15, r16, r17, r18)
        L_0x01d6:
            r5 = r7
            goto L_0x01fb
        L_0x01d8:
            java.security.InvalidKeyException r0 = new java.security.InvalidKeyException
            java.lang.String r2 = "Algorithm requires a PBE key suitable for PKCS12"
            r0.<init>(r2)
            throw r0
        L_0x01e0:
            byte[] r12 = r5.getEncoded()
            r13 = 2
            int r14 = r1.digest
            int r15 = r1.keySizeInBits
            int r5 = r1.ivLength
            int r16 = r5 * 8
            javax.crypto.spec.PBEParameterSpec r5 = r1.pbeSpec
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$GenericBlockCipher r7 = r1.cipher
            java.lang.String r18 = r7.getAlgorithmName()
            r17 = r5
            org.bouncycastle.crypto.CipherParameters r5 = org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEParameters(r12, r13, r14, r15, r16, r17, r18)
        L_0x01fb:
            boolean r7 = r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV
            if (r7 == 0) goto L_0x0201
            goto L_0x00ac
        L_0x0201:
            boolean r7 = r3 instanceof org.bouncycastle.jcajce.spec.AEADParameterSpec
            r9 = 0
            if (r7 == 0) goto L_0x0245
            java.lang.String r2 = r1.modeName
            boolean r2 = r1.isAEADModeName(r2)
            if (r2 != 0) goto L_0x021d
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$GenericBlockCipher r2 = r1.cipher
            boolean r2 = r2 instanceof org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.AEADGenericBlockCipher
            if (r2 == 0) goto L_0x0215
            goto L_0x021d
        L_0x0215:
            java.security.InvalidAlgorithmParameterException r0 = new java.security.InvalidAlgorithmParameterException
            java.lang.String r2 = "AEADParameterSpec can only be used with AEAD modes."
            r0.<init>(r2)
            throw r0
        L_0x021d:
            r2 = r3
            org.bouncycastle.jcajce.spec.AEADParameterSpec r2 = (org.bouncycastle.jcajce.spec.AEADParameterSpec) r2
            boolean r3 = r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV
            if (r3 == 0) goto L_0x022d
            org.bouncycastle.crypto.params.ParametersWithIV r5 = (org.bouncycastle.crypto.params.ParametersWithIV) r5
            org.bouncycastle.crypto.CipherParameters r3 = r5.getParameters()
            org.bouncycastle.crypto.params.KeyParameter r3 = (org.bouncycastle.crypto.params.KeyParameter) r3
            goto L_0x0230
        L_0x022d:
            r3 = r5
            org.bouncycastle.crypto.params.KeyParameter r3 = (org.bouncycastle.crypto.params.KeyParameter) r3
        L_0x0230:
            org.bouncycastle.crypto.params.AEADParameters r5 = new org.bouncycastle.crypto.params.AEADParameters
            int r6 = r2.getMacSizeInBits()
            byte[] r7 = r2.getNonce()
            byte[] r2 = r2.getAssociatedData()
            r5.<init>(r3, r6, r7, r2)
            r1.aeadParams = r5
            goto L_0x0474
        L_0x0245:
            boolean r7 = r3 instanceof javax.crypto.spec.IvParameterSpec
            if (r7 == 0) goto L_0x02bd
            int r2 = r1.ivLength
            if (r2 == 0) goto L_0x02a7
            r2 = r3
            javax.crypto.spec.IvParameterSpec r2 = (javax.crypto.spec.IvParameterSpec) r2
            byte[] r3 = r2.getIV()
            int r3 = r3.length
            int r6 = r1.ivLength
            if (r3 == r6) goto L_0x0282
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$GenericBlockCipher r3 = r1.cipher
            boolean r3 = r3 instanceof org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.AEADGenericBlockCipher
            if (r3 != 0) goto L_0x0282
            boolean r3 = r1.fixedIv
            if (r3 != 0) goto L_0x0264
            goto L_0x0282
        L_0x0264:
            java.security.InvalidAlgorithmParameterException r0 = new java.security.InvalidAlgorithmParameterException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "IV must be "
            r2.append(r3)
            int r3 = r1.ivLength
            r2.append(r3)
            java.lang.String r3 = " bytes long."
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r0.<init>(r2)
            throw r0
        L_0x0282:
            boolean r3 = r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV
            if (r3 == 0) goto L_0x0296
            org.bouncycastle.crypto.params.ParametersWithIV r3 = new org.bouncycastle.crypto.params.ParametersWithIV
            org.bouncycastle.crypto.params.ParametersWithIV r5 = (org.bouncycastle.crypto.params.ParametersWithIV) r5
            org.bouncycastle.crypto.CipherParameters r5 = r5.getParameters()
            byte[] r2 = r2.getIV()
            r3.<init>(r5, r2)
            goto L_0x029f
        L_0x0296:
            org.bouncycastle.crypto.params.ParametersWithIV r3 = new org.bouncycastle.crypto.params.ParametersWithIV
            byte[] r2 = r2.getIV()
            r3.<init>(r5, r2)
        L_0x029f:
            r5 = r3
            r2 = r5
            org.bouncycastle.crypto.params.ParametersWithIV r2 = (org.bouncycastle.crypto.params.ParametersWithIV) r2
            r1.ivParam = r2
            goto L_0x0474
        L_0x02a7:
            java.lang.String r2 = r1.modeName
            if (r2 == 0) goto L_0x0474
            java.lang.String r3 = "ECB"
            boolean r2 = r2.equals(r3)
            if (r2 != 0) goto L_0x02b5
            goto L_0x0474
        L_0x02b5:
            java.security.InvalidAlgorithmParameterException r0 = new java.security.InvalidAlgorithmParameterException
            java.lang.String r2 = "ECB mode does not use an IV"
            r0.<init>(r2)
            throw r0
        L_0x02bd:
            boolean r7 = r3 instanceof org.bouncycastle.jcajce.spec.GOST28147ParameterSpec
            if (r7 == 0) goto L_0x0304
            org.bouncycastle.jcajce.spec.GOST28147ParameterSpec r3 = (org.bouncycastle.jcajce.spec.GOST28147ParameterSpec) r3
            org.bouncycastle.crypto.params.ParametersWithSBox r5 = new org.bouncycastle.crypto.params.ParametersWithSBox
            org.bouncycastle.crypto.params.KeyParameter r6 = new org.bouncycastle.crypto.params.KeyParameter
            byte[] r2 = r22.getEncoded()
            r6.<init>(r2)
            byte[] r2 = r3.getSbox()
            r5.<init>(r6, r2)
            byte[] r2 = r3.getIV()
            if (r2 == 0) goto L_0x0474
            int r2 = r1.ivLength
            if (r2 == 0) goto L_0x0474
            boolean r2 = r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV
            if (r2 == 0) goto L_0x02f3
            org.bouncycastle.crypto.params.ParametersWithIV r2 = new org.bouncycastle.crypto.params.ParametersWithIV
            org.bouncycastle.crypto.params.ParametersWithIV r5 = (org.bouncycastle.crypto.params.ParametersWithIV) r5
            org.bouncycastle.crypto.CipherParameters r5 = r5.getParameters()
            byte[] r3 = r3.getIV()
            r2.<init>(r5, r3)
            goto L_0x02fc
        L_0x02f3:
            org.bouncycastle.crypto.params.ParametersWithIV r2 = new org.bouncycastle.crypto.params.ParametersWithIV
            byte[] r3 = r3.getIV()
            r2.<init>(r5, r3)
        L_0x02fc:
            r3 = r2
            org.bouncycastle.crypto.params.ParametersWithIV r3 = (org.bouncycastle.crypto.params.ParametersWithIV) r3
            r1.ivParam = r3
            r5 = r2
            goto L_0x0474
        L_0x0304:
            boolean r7 = r3 instanceof javax.crypto.spec.RC2ParameterSpec
            if (r7 == 0) goto L_0x033f
            javax.crypto.spec.RC2ParameterSpec r3 = (javax.crypto.spec.RC2ParameterSpec) r3
            org.bouncycastle.crypto.params.RC2Parameters r5 = new org.bouncycastle.crypto.params.RC2Parameters
            byte[] r2 = r22.getEncoded()
            int r6 = r3.getEffectiveKeyBits()
            r5.<init>(r2, r6)
            byte[] r2 = r3.getIV()
            if (r2 == 0) goto L_0x0474
            int r2 = r1.ivLength
            if (r2 == 0) goto L_0x0474
            boolean r2 = r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV
            if (r2 == 0) goto L_0x0335
            org.bouncycastle.crypto.params.ParametersWithIV r2 = new org.bouncycastle.crypto.params.ParametersWithIV
            org.bouncycastle.crypto.params.ParametersWithIV r5 = (org.bouncycastle.crypto.params.ParametersWithIV) r5
            org.bouncycastle.crypto.CipherParameters r5 = r5.getParameters()
            byte[] r3 = r3.getIV()
            r2.<init>(r5, r3)
            goto L_0x02fc
        L_0x0335:
            org.bouncycastle.crypto.params.ParametersWithIV r2 = new org.bouncycastle.crypto.params.ParametersWithIV
            byte[] r3 = r3.getIV()
            r2.<init>(r5, r3)
            goto L_0x02fc
        L_0x033f:
            boolean r7 = r3 instanceof javax.crypto.spec.RC5ParameterSpec
            if (r7 == 0) goto L_0x03fc
            javax.crypto.spec.RC5ParameterSpec r3 = (javax.crypto.spec.RC5ParameterSpec) r3
            org.bouncycastle.crypto.params.RC5Parameters r5 = new org.bouncycastle.crypto.params.RC5Parameters
            byte[] r2 = r22.getEncoded()
            int r7 = r3.getRounds()
            r5.<init>(r2, r7)
            org.bouncycastle.crypto.BlockCipher r2 = r1.baseEngine
            java.lang.String r2 = r2.getAlgorithmName()
            java.lang.String r7 = "RC5"
            boolean r2 = r2.startsWith(r7)
            if (r2 == 0) goto L_0x03f4
            org.bouncycastle.crypto.BlockCipher r2 = r1.baseEngine
            java.lang.String r2 = r2.getAlgorithmName()
            java.lang.String r7 = "RC5-32"
            boolean r2 = r2.equals(r7)
            java.lang.String r7 = "."
            if (r2 == 0) goto L_0x0397
            int r2 = r3.getWordSize()
            r6 = 32
            if (r2 != r6) goto L_0x0379
            goto L_0x03ca
        L_0x0379:
            java.security.InvalidAlgorithmParameterException r0 = new java.security.InvalidAlgorithmParameterException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "RC5 already set up for a word size of 32 not "
            r2.append(r4)
            int r3 = r3.getWordSize()
            r2.append(r3)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r0.<init>(r2)
            throw r0
        L_0x0397:
            org.bouncycastle.crypto.BlockCipher r2 = r1.baseEngine
            java.lang.String r2 = r2.getAlgorithmName()
            boolean r2 = r2.equals(r6)
            if (r2 == 0) goto L_0x03ca
            int r2 = r3.getWordSize()
            r6 = 64
            if (r2 != r6) goto L_0x03ac
            goto L_0x03ca
        L_0x03ac:
            java.security.InvalidAlgorithmParameterException r0 = new java.security.InvalidAlgorithmParameterException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "RC5 already set up for a word size of 64 not "
            r2.append(r4)
            int r3 = r3.getWordSize()
            r2.append(r3)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r0.<init>(r2)
            throw r0
        L_0x03ca:
            byte[] r2 = r3.getIV()
            if (r2 == 0) goto L_0x0474
            int r2 = r1.ivLength
            if (r2 == 0) goto L_0x0474
            boolean r2 = r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV
            if (r2 == 0) goto L_0x03e9
            org.bouncycastle.crypto.params.ParametersWithIV r2 = new org.bouncycastle.crypto.params.ParametersWithIV
            org.bouncycastle.crypto.params.ParametersWithIV r5 = (org.bouncycastle.crypto.params.ParametersWithIV) r5
            org.bouncycastle.crypto.CipherParameters r5 = r5.getParameters()
            byte[] r3 = r3.getIV()
            r2.<init>(r5, r3)
            goto L_0x02fc
        L_0x03e9:
            org.bouncycastle.crypto.params.ParametersWithIV r2 = new org.bouncycastle.crypto.params.ParametersWithIV
            byte[] r3 = r3.getIV()
            r2.<init>(r5, r3)
            goto L_0x02fc
        L_0x03f4:
            java.security.InvalidAlgorithmParameterException r0 = new java.security.InvalidAlgorithmParameterException
            java.lang.String r2 = "RC5 parameters passed to a cipher that is not RC5."
            r0.<init>(r2)
            throw r0
        L_0x03fc:
            java.lang.Class r2 = gcmSpecClass
            if (r2 == 0) goto L_0x0465
            boolean r2 = r2.isInstance(r3)
            if (r2 == 0) goto L_0x0465
            java.lang.String r2 = r1.modeName
            boolean r2 = r1.isAEADModeName(r2)
            if (r2 != 0) goto L_0x041d
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$GenericBlockCipher r2 = r1.cipher
            boolean r2 = r2 instanceof org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.AEADGenericBlockCipher
            if (r2 == 0) goto L_0x0415
            goto L_0x041d
        L_0x0415:
            java.security.InvalidAlgorithmParameterException r0 = new java.security.InvalidAlgorithmParameterException
            java.lang.String r2 = "GCMParameterSpec can only be used with AEAD modes."
            r0.<init>(r2)
            throw r0
        L_0x041d:
            java.lang.Class r2 = gcmSpecClass     // Catch:{ Exception -> 0x045c }
            java.lang.String r6 = "getTLen"
            java.lang.Class[] r7 = new java.lang.Class[r9]     // Catch:{ Exception -> 0x045c }
            java.lang.reflect.Method r2 = r2.getDeclaredMethod(r6, r7)     // Catch:{ Exception -> 0x045c }
            java.lang.Class r6 = gcmSpecClass     // Catch:{ Exception -> 0x045c }
            java.lang.String r7 = "getIV"
            java.lang.Class[] r12 = new java.lang.Class[r9]     // Catch:{ Exception -> 0x045c }
            java.lang.reflect.Method r6 = r6.getDeclaredMethod(r7, r12)     // Catch:{ Exception -> 0x045c }
            boolean r7 = r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV     // Catch:{ Exception -> 0x045c }
            if (r7 == 0) goto L_0x043b
            org.bouncycastle.crypto.params.ParametersWithIV r5 = (org.bouncycastle.crypto.params.ParametersWithIV) r5     // Catch:{ Exception -> 0x045c }
            org.bouncycastle.crypto.CipherParameters r5 = r5.getParameters()     // Catch:{ Exception -> 0x045c }
        L_0x043b:
            org.bouncycastle.crypto.params.KeyParameter r5 = (org.bouncycastle.crypto.params.KeyParameter) r5     // Catch:{ Exception -> 0x045c }
            org.bouncycastle.crypto.params.AEADParameters r7 = new org.bouncycastle.crypto.params.AEADParameters     // Catch:{ Exception -> 0x045c }
            java.lang.Object[] r12 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x045c }
            java.lang.Object r2 = r2.invoke(r3, r12)     // Catch:{ Exception -> 0x045c }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ Exception -> 0x045c }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x045c }
            java.lang.Object[] r12 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x045c }
            java.lang.Object r3 = r6.invoke(r3, r12)     // Catch:{ Exception -> 0x045c }
            byte[] r3 = (byte[]) r3     // Catch:{ Exception -> 0x045c }
            byte[] r3 = (byte[]) r3     // Catch:{ Exception -> 0x045c }
            r7.<init>(r5, r2, r3)     // Catch:{ Exception -> 0x045c }
            r1.aeadParams = r7     // Catch:{ Exception -> 0x045c }
            r5 = r7
            goto L_0x0474
        L_0x045c:
            r0 = move-exception
            java.security.InvalidAlgorithmParameterException r0 = new java.security.InvalidAlgorithmParameterException
            java.lang.String r2 = "Cannot process GCMParameterSpec."
            r0.<init>(r2)
            throw r0
        L_0x0465:
            if (r3 == 0) goto L_0x0474
            boolean r2 = r3 instanceof javax.crypto.spec.PBEParameterSpec
            if (r2 == 0) goto L_0x046c
            goto L_0x0474
        L_0x046c:
            java.security.InvalidAlgorithmParameterException r0 = new java.security.InvalidAlgorithmParameterException
            java.lang.String r2 = "unknown parameter type."
            r0.<init>(r2)
            throw r0
        L_0x0474:
            int r2 = r1.ivLength
            r3 = 3
            if (r2 == 0) goto L_0x04bb
            boolean r2 = r5 instanceof org.bouncycastle.crypto.params.ParametersWithIV
            if (r2 != 0) goto L_0x04bb
            boolean r2 = r5 instanceof org.bouncycastle.crypto.params.AEADParameters
            if (r2 != 0) goto L_0x04bb
            if (r4 != 0) goto L_0x0488
            java.security.SecureRandom r2 = org.bouncycastle.crypto.CryptoServicesRegistrar.getSecureRandom()
            goto L_0x0489
        L_0x0488:
            r2 = r4
        L_0x0489:
            if (r0 == r11) goto L_0x04a9
            if (r0 != r3) goto L_0x048e
            goto L_0x04a9
        L_0x048e:
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$GenericBlockCipher r2 = r1.cipher
            org.bouncycastle.crypto.BlockCipher r2 = r2.getUnderlyingCipher()
            java.lang.String r2 = r2.getAlgorithmName()
            java.lang.String r6 = "PGPCFB"
            int r2 = r2.indexOf(r6)
            if (r2 < 0) goto L_0x04a1
            goto L_0x04bb
        L_0x04a1:
            java.security.InvalidAlgorithmParameterException r0 = new java.security.InvalidAlgorithmParameterException
            java.lang.String r2 = "no IV set when one expected"
            r0.<init>(r2)
            throw r0
        L_0x04a9:
            int r6 = r1.ivLength
            byte[] r6 = new byte[r6]
            r2.nextBytes(r6)
            org.bouncycastle.crypto.params.ParametersWithIV r2 = new org.bouncycastle.crypto.params.ParametersWithIV
            r2.<init>(r5, r6)
            r5 = r2
            org.bouncycastle.crypto.params.ParametersWithIV r5 = (org.bouncycastle.crypto.params.ParametersWithIV) r5
            r1.ivParam = r5
            goto L_0x04bc
        L_0x04bb:
            r2 = r5
        L_0x04bc:
            if (r4 == 0) goto L_0x04c8
            boolean r5 = r1.padded
            if (r5 == 0) goto L_0x04c8
            org.bouncycastle.crypto.params.ParametersWithRandom r5 = new org.bouncycastle.crypto.params.ParametersWithRandom
            r5.<init>(r2, r4)
            r2 = r5
        L_0x04c8:
            if (r0 == r11) goto L_0x04f5
            if (r0 == r10) goto L_0x04ef
            if (r0 == r3) goto L_0x04f5
            if (r0 != r8) goto L_0x04d1
            goto L_0x04ef
        L_0x04d1:
            java.security.InvalidParameterException r2 = new java.security.InvalidParameterException     // Catch:{ Exception -> 0x04ed }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ed }
            r3.<init>()     // Catch:{ Exception -> 0x04ed }
            java.lang.String r4 = "unknown opmode "
            r3.append(r4)     // Catch:{ Exception -> 0x04ed }
            r3.append(r0)     // Catch:{ Exception -> 0x04ed }
            java.lang.String r0 = " passed"
            r3.append(r0)     // Catch:{ Exception -> 0x04ed }
            java.lang.String r0 = r3.toString()     // Catch:{ Exception -> 0x04ed }
            r2.<init>(r0)     // Catch:{ Exception -> 0x04ed }
            throw r2     // Catch:{ Exception -> 0x04ed }
        L_0x04ed:
            r0 = move-exception
            goto L_0x0529
        L_0x04ef:
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$GenericBlockCipher r0 = r1.cipher     // Catch:{ Exception -> 0x04ed }
            r0.init(r9, r2)     // Catch:{ Exception -> 0x04ed }
            goto L_0x04fa
        L_0x04f5:
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$GenericBlockCipher r0 = r1.cipher     // Catch:{ Exception -> 0x04ed }
            r0.init(r11, r2)     // Catch:{ Exception -> 0x04ed }
        L_0x04fa:
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$GenericBlockCipher r0 = r1.cipher     // Catch:{ Exception -> 0x04ed }
            boolean r0 = r0 instanceof org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.AEADGenericBlockCipher     // Catch:{ Exception -> 0x04ed }
            if (r0 == 0) goto L_0x0528
            org.bouncycastle.crypto.params.AEADParameters r0 = r1.aeadParams     // Catch:{ Exception -> 0x04ed }
            if (r0 != 0) goto L_0x0528
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$GenericBlockCipher r0 = r1.cipher     // Catch:{ Exception -> 0x04ed }
            org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher$AEADGenericBlockCipher r0 = (org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.AEADGenericBlockCipher) r0     // Catch:{ Exception -> 0x04ed }
            org.bouncycastle.crypto.modes.AEADBlockCipher r0 = r0.cipher     // Catch:{ Exception -> 0x04ed }
            org.bouncycastle.crypto.params.AEADParameters r2 = new org.bouncycastle.crypto.params.AEADParameters     // Catch:{ Exception -> 0x04ed }
            org.bouncycastle.crypto.params.ParametersWithIV r3 = r1.ivParam     // Catch:{ Exception -> 0x04ed }
            org.bouncycastle.crypto.CipherParameters r3 = r3.getParameters()     // Catch:{ Exception -> 0x04ed }
            org.bouncycastle.crypto.params.KeyParameter r3 = (org.bouncycastle.crypto.params.KeyParameter) r3     // Catch:{ Exception -> 0x04ed }
            byte[] r0 = r0.getMac()     // Catch:{ Exception -> 0x04ed }
            int r0 = r0.length     // Catch:{ Exception -> 0x04ed }
            int r0 = r0 * 8
            org.bouncycastle.crypto.params.ParametersWithIV r4 = r1.ivParam     // Catch:{ Exception -> 0x04ed }
            byte[] r4 = r4.getIV()     // Catch:{ Exception -> 0x04ed }
            r2.<init>(r3, r0, r4)     // Catch:{ Exception -> 0x04ed }
            r1.aeadParams = r2     // Catch:{ Exception -> 0x04ed }
        L_0x0528:
            return
        L_0x0529:
            org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher$InvalidKeyOrParametersException r2 = new org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher$InvalidKeyOrParametersException
            java.lang.String r3 = r0.getMessage()
            r2.<init>(r3, r0)
            throw r2
        L_0x0533:
            r0 = move-exception
            java.security.InvalidKeyException r0 = new java.security.InvalidKeyException
            java.lang.String r2 = "PKCS12 requires a SecretKey/PBEKey"
            r0.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.engineInit(int, java.security.Key, java.security.spec.AlgorithmParameterSpec, java.security.SecureRandom):void");
    }

    /* access modifiers changed from: protected */
    public void engineSetMode(String str) throws NoSuchAlgorithmException {
        GenericBlockCipher aEADGenericBlockCipher;
        GenericBlockCipher aEADGenericBlockCipher2;
        this.modeName = Strings.toUpperCase(str);
        if (this.modeName.equals("ECB")) {
            this.ivLength = 0;
            aEADGenericBlockCipher2 = new BufferedGenericBlockCipher(this.baseEngine);
        } else if (this.modeName.equals("CBC")) {
            this.ivLength = this.baseEngine.getBlockSize();
            aEADGenericBlockCipher2 = new BufferedGenericBlockCipher((BlockCipher) new CBCBlockCipher(this.baseEngine));
        } else {
            if (this.modeName.startsWith("OFB")) {
                this.ivLength = this.baseEngine.getBlockSize();
                if (this.modeName.length() != 3) {
                    aEADGenericBlockCipher = new BufferedGenericBlockCipher((BlockCipher) new OFBBlockCipher(this.baseEngine, Integer.parseInt(this.modeName.substring(3))));
                } else {
                    BlockCipher blockCipher = this.baseEngine;
                    aEADGenericBlockCipher2 = new BufferedGenericBlockCipher((BlockCipher) new OFBBlockCipher(blockCipher, blockCipher.getBlockSize() * 8));
                }
            } else if (this.modeName.startsWith("CFB")) {
                this.ivLength = this.baseEngine.getBlockSize();
                if (this.modeName.length() != 3) {
                    aEADGenericBlockCipher = new BufferedGenericBlockCipher((BlockCipher) new CFBBlockCipher(this.baseEngine, Integer.parseInt(this.modeName.substring(3))));
                } else {
                    BlockCipher blockCipher2 = this.baseEngine;
                    aEADGenericBlockCipher2 = new BufferedGenericBlockCipher((BlockCipher) new CFBBlockCipher(blockCipher2, blockCipher2.getBlockSize() * 8));
                }
            } else if (this.modeName.startsWith("PGP")) {
                boolean equalsIgnoreCase = this.modeName.equalsIgnoreCase("PGPCFBwithIV");
                this.ivLength = this.baseEngine.getBlockSize();
                aEADGenericBlockCipher = new BufferedGenericBlockCipher((BlockCipher) new PGPCFBBlockCipher(this.baseEngine, equalsIgnoreCase));
            } else if (this.modeName.equalsIgnoreCase("OpenPGPCFB")) {
                this.ivLength = 0;
                aEADGenericBlockCipher2 = new BufferedGenericBlockCipher((BlockCipher) new OpenPGPCFBBlockCipher(this.baseEngine));
            } else if (this.modeName.startsWith("SIC")) {
                this.ivLength = this.baseEngine.getBlockSize();
                if (this.ivLength >= 16) {
                    this.fixedIv = false;
                    aEADGenericBlockCipher2 = new BufferedGenericBlockCipher(new BufferedBlockCipher(new SICBlockCipher(this.baseEngine)));
                } else {
                    throw new IllegalArgumentException("Warning: SIC-Mode can become a twotime-pad if the blocksize of the cipher is too small. Use a cipher with a block size of at least 128 bits (e.g. AES)");
                }
            } else if (this.modeName.startsWith("CTR")) {
                this.ivLength = this.baseEngine.getBlockSize();
                this.fixedIv = false;
                BlockCipher blockCipher3 = this.baseEngine;
                aEADGenericBlockCipher = blockCipher3 instanceof DSTU7624Engine ? new BufferedGenericBlockCipher(new BufferedBlockCipher(new KCTRBlockCipher(blockCipher3))) : new BufferedGenericBlockCipher(new BufferedBlockCipher(new SICBlockCipher(blockCipher3)));
            } else if (this.modeName.startsWith("GOFB")) {
                this.ivLength = this.baseEngine.getBlockSize();
                aEADGenericBlockCipher2 = new BufferedGenericBlockCipher(new BufferedBlockCipher(new GOFBBlockCipher(this.baseEngine)));
            } else if (this.modeName.startsWith("GCFB")) {
                this.ivLength = this.baseEngine.getBlockSize();
                aEADGenericBlockCipher2 = new BufferedGenericBlockCipher(new BufferedBlockCipher(new GCFBBlockCipher(this.baseEngine)));
            } else if (this.modeName.startsWith("CTS")) {
                this.ivLength = this.baseEngine.getBlockSize();
                aEADGenericBlockCipher2 = new BufferedGenericBlockCipher((BufferedBlockCipher) new CTSBlockCipher(new CBCBlockCipher(this.baseEngine)));
            } else if (this.modeName.startsWith("CCM")) {
                this.ivLength = 12;
                BlockCipher blockCipher4 = this.baseEngine;
                aEADGenericBlockCipher = blockCipher4 instanceof DSTU7624Engine ? new AEADGenericBlockCipher(new KCCMBlockCipher(blockCipher4)) : new AEADGenericBlockCipher(new CCMBlockCipher(blockCipher4));
            } else {
                String str2 = "can't support mode ";
                if (this.modeName.startsWith("OCB")) {
                    BlockCipherProvider blockCipherProvider = this.engineProvider;
                    if (blockCipherProvider != null) {
                        this.ivLength = 15;
                        aEADGenericBlockCipher2 = new AEADGenericBlockCipher(new OCBBlockCipher(this.baseEngine, blockCipherProvider.get()));
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append(str2);
                        sb.append(str);
                        throw new NoSuchAlgorithmException(sb.toString());
                    }
                } else if (this.modeName.startsWith("EAX")) {
                    this.ivLength = this.baseEngine.getBlockSize();
                    aEADGenericBlockCipher2 = new AEADGenericBlockCipher(new EAXBlockCipher(this.baseEngine));
                } else if (this.modeName.startsWith("GCM")) {
                    this.ivLength = this.baseEngine.getBlockSize();
                    BlockCipher blockCipher5 = this.baseEngine;
                    aEADGenericBlockCipher = blockCipher5 instanceof DSTU7624Engine ? new AEADGenericBlockCipher(new KGCMBlockCipher(blockCipher5)) : new AEADGenericBlockCipher(new GCMBlockCipher(blockCipher5));
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str2);
                    sb2.append(str);
                    throw new NoSuchAlgorithmException(sb2.toString());
                }
            }
            this.cipher = aEADGenericBlockCipher;
            return;
        }
        this.cipher = aEADGenericBlockCipher2;
    }

    /* access modifiers changed from: protected */
    public void engineSetPadding(String str) throws NoSuchPaddingException {
        BufferedGenericBlockCipher bufferedGenericBlockCipher;
        String upperCase = Strings.toUpperCase(str);
        if (upperCase.equals("NOPADDING")) {
            if (this.cipher.wrapOnNoPadding()) {
                bufferedGenericBlockCipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(this.cipher.getUnderlyingCipher()));
            } else {
                return;
            }
        } else if (upperCase.equals("WITHCTS") || upperCase.equals("CTSPADDING") || upperCase.equals("CS3PADDING")) {
            bufferedGenericBlockCipher = new BufferedGenericBlockCipher((BufferedBlockCipher) new CTSBlockCipher(this.cipher.getUnderlyingCipher()));
        } else {
            this.padded = true;
            if (isAEADModeName(this.modeName)) {
                throw new NoSuchPaddingException("Only NoPadding can be used with AEAD modes.");
            } else if (upperCase.equals("PKCS5PADDING") || upperCase.equals("PKCS7PADDING")) {
                bufferedGenericBlockCipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher());
            } else if (upperCase.equals("ZEROBYTEPADDING")) {
                bufferedGenericBlockCipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ZeroBytePadding());
            } else if (upperCase.equals("ISO10126PADDING") || upperCase.equals("ISO10126-2PADDING")) {
                bufferedGenericBlockCipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ISO10126d2Padding());
            } else if (upperCase.equals("X9.23PADDING") || upperCase.equals("X923PADDING")) {
                bufferedGenericBlockCipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new X923Padding());
            } else if (upperCase.equals("ISO7816-4PADDING") || upperCase.equals("ISO9797-1PADDING")) {
                bufferedGenericBlockCipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ISO7816d4Padding());
            } else if (upperCase.equals("TBCPADDING")) {
                bufferedGenericBlockCipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new TBCPadding());
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Padding ");
                sb.append(str);
                sb.append(" unknown.");
                throw new NoSuchPaddingException(sb.toString());
            }
        }
        this.cipher = bufferedGenericBlockCipher;
    }

    /* access modifiers changed from: protected */
    public int engineUpdate(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws ShortBufferException {
        if (this.cipher.getUpdateOutputSize(i2) + i3 <= bArr2.length) {
            try {
                return this.cipher.processBytes(bArr, i, i2, bArr2, i3);
            } catch (DataLengthException e) {
                throw new IllegalStateException(e.toString());
            }
        } else {
            throw new ShortBufferException("output buffer too short for input.");
        }
    }

    /* access modifiers changed from: protected */
    public byte[] engineUpdate(byte[] bArr, int i, int i2) {
        int updateOutputSize = this.cipher.getUpdateOutputSize(i2);
        if (updateOutputSize > 0) {
            byte[] bArr2 = new byte[updateOutputSize];
            int processBytes = this.cipher.processBytes(bArr, i, i2, bArr2, 0);
            if (processBytes == 0) {
                return null;
            }
            if (processBytes == bArr2.length) {
                return bArr2;
            }
            byte[] bArr3 = new byte[processBytes];
            System.arraycopy(bArr2, 0, bArr3, 0, processBytes);
            return bArr3;
        }
        this.cipher.processBytes(bArr, i, i2, null, 0);
        return null;
    }

    /* access modifiers changed from: protected */
    public void engineUpdateAAD(ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();
        if (remaining >= 1) {
            if (byteBuffer.hasArray()) {
                engineUpdateAAD(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), remaining);
                byteBuffer.position(byteBuffer.limit());
            } else if (remaining <= 512) {
                byte[] bArr = new byte[remaining];
                byteBuffer.get(bArr);
                engineUpdateAAD(bArr, 0, bArr.length);
                Arrays.fill(bArr, 0);
            } else {
                byte[] bArr2 = new byte[512];
                do {
                    int min = Math.min(bArr2.length, remaining);
                    byteBuffer.get(bArr2, 0, min);
                    engineUpdateAAD(bArr2, 0, min);
                    remaining -= min;
                } while (remaining > 0);
                Arrays.fill(bArr2, 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void engineUpdateAAD(byte[] bArr, int i, int i2) {
        this.cipher.updateAAD(bArr, i, i2);
    }
}
