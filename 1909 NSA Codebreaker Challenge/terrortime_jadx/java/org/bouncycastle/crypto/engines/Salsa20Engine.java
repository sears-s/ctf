package org.bouncycastle.crypto.engines;

import org.bouncycastle.asn1.cmc.BodyPartID;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.MaxBytesExceededException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.SkippingStreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

public class Salsa20Engine implements SkippingStreamCipher {
    public static final int DEFAULT_ROUNDS = 20;
    private static final int STATE_SIZE = 16;
    private static final int[] TAU_SIGMA = Pack.littleEndianToInt(Strings.toByteArray("expand 16-byte kexpand 32-byte k"), 0, 8);
    protected static final byte[] sigma = Strings.toByteArray("expand 32-byte k");
    protected static final byte[] tau = Strings.toByteArray("expand 16-byte k");
    private int cW0;
    private int cW1;
    private int cW2;
    protected int[] engineState;
    private int index;
    private boolean initialised;
    private byte[] keyStream;
    protected int rounds;
    protected int[] x;

    public Salsa20Engine() {
        this(20);
    }

    public Salsa20Engine(int i) {
        this.index = 0;
        this.engineState = new int[16];
        this.x = new int[16];
        this.keyStream = new byte[64];
        this.initialised = false;
        if (i <= 0 || (i & 1) != 0) {
            throw new IllegalArgumentException("'rounds' must be a positive, even number");
        }
        this.rounds = i;
    }

    private boolean limitExceeded() {
        boolean z = true;
        int i = this.cW0 + 1;
        this.cW0 = i;
        if (i == 0) {
            int i2 = this.cW1 + 1;
            this.cW1 = i2;
            if (i2 == 0) {
                int i3 = this.cW2 + 1;
                this.cW2 = i3;
                if ((i3 & 32) == 0) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    private boolean limitExceeded(int i) {
        this.cW0 += i;
        int i2 = this.cW0;
        if (i2 < i && i2 >= 0) {
            boolean z = true;
            int i3 = this.cW1 + 1;
            this.cW1 = i3;
            if (i3 == 0) {
                int i4 = this.cW2 + 1;
                this.cW2 = i4;
                if ((i4 & 32) == 0) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    private void resetLimitCounter() {
        this.cW0 = 0;
        this.cW1 = 0;
        this.cW2 = 0;
    }

    protected static int rotl(int i, int i2) {
        return (i >>> (-i2)) | (i << i2);
    }

    public static void salsaCore(int i, int[] iArr, int[] iArr2) {
        int[] iArr3 = iArr;
        int[] iArr4 = iArr2;
        if (iArr3.length != 16) {
            throw new IllegalArgumentException();
        } else if (iArr4.length != 16) {
            throw new IllegalArgumentException();
        } else if (i % 2 == 0) {
            char c = 0;
            int i2 = iArr3[0];
            int i3 = iArr3[1];
            int i4 = iArr3[2];
            int i5 = iArr3[3];
            int i6 = iArr3[4];
            int i7 = iArr3[5];
            int i8 = iArr3[6];
            int i9 = 7;
            int i10 = iArr3[7];
            int i11 = iArr3[8];
            int i12 = 9;
            int i13 = iArr3[9];
            int i14 = iArr3[10];
            int i15 = iArr3[11];
            int i16 = iArr3[12];
            int i17 = 13;
            int i18 = iArr3[13];
            int i19 = iArr3[14];
            int i20 = iArr3[15];
            int i21 = i19;
            int i22 = i18;
            int i23 = i16;
            int i24 = i15;
            int i25 = i14;
            int i26 = i13;
            int i27 = i11;
            int i28 = i10;
            int i29 = i8;
            int i30 = i7;
            int i31 = i6;
            int i32 = i5;
            int i33 = i4;
            int i34 = i3;
            int i35 = i2;
            int i36 = i;
            while (i36 > 0) {
                int rotl = rotl(i35 + i23, i9) ^ i31;
                int rotl2 = i27 ^ rotl(rotl + i35, i12);
                int rotl3 = i23 ^ rotl(rotl2 + rotl, i17);
                int rotl4 = rotl(rotl3 + rotl2, 18) ^ i35;
                int rotl5 = i26 ^ rotl(i30 + i34, i9);
                int rotl6 = i22 ^ rotl(rotl5 + i30, i12);
                int rotl7 = i34 ^ rotl(rotl6 + rotl5, i17);
                int rotl8 = rotl(rotl7 + rotl6, 18) ^ i30;
                int rotl9 = i21 ^ rotl(i25 + i29, 7);
                int rotl10 = i33 ^ rotl(rotl9 + i25, 9);
                int rotl11 = i29 ^ rotl(rotl10 + rotl9, 13);
                int rotl12 = i25 ^ rotl(rotl11 + rotl10, 18);
                int rotl13 = i32 ^ rotl(i20 + i24, 7);
                int rotl14 = i28 ^ rotl(rotl13 + i20, 9);
                int i37 = i36;
                int rotl15 = i24 ^ rotl(rotl14 + rotl13, 13);
                int i38 = rotl6;
                int rotl16 = i20 ^ rotl(rotl15 + rotl14, 18);
                int i39 = rotl3;
                i34 = rotl7 ^ rotl(rotl4 + rotl13, 7);
                i33 = rotl10 ^ rotl(i34 + rotl4, 9);
                int rotl17 = rotl13 ^ rotl(i33 + i34, 13);
                i29 = rotl11 ^ rotl(rotl8 + rotl, 7);
                i28 = rotl14 ^ rotl(i29 + rotl8, 9);
                int rotl18 = rotl(i28 + i29, 13) ^ rotl;
                i30 = rotl8 ^ rotl(rotl18 + i28, 18);
                i24 = rotl15 ^ rotl(rotl12 + rotl5, 7);
                int rotl19 = rotl(i24 + rotl12, 9) ^ rotl2;
                i26 = rotl5 ^ rotl(rotl19 + i24, 13);
                i25 = rotl12 ^ rotl(i26 + rotl19, 18);
                i23 = i39 ^ rotl(rotl16 + rotl9, 7);
                i22 = i38 ^ rotl(i23 + rotl16, 9);
                i21 = rotl9 ^ rotl(i22 + i23, 13);
                i20 = rotl16 ^ rotl(i21 + i22, 18);
                i32 = rotl17;
                i27 = rotl19;
                i35 = rotl4 ^ rotl(rotl17 + i33, 18);
                i31 = rotl18;
                c = 0;
                i17 = 13;
                i12 = 9;
                i9 = 7;
                int[] iArr5 = iArr;
                i36 = i37 - 2;
                int[] iArr6 = iArr2;
            }
            char c2 = c;
            iArr2[c2] = i35 + iArr[c2];
            iArr2[1] = i34 + iArr[1];
            iArr2[2] = i33 + iArr[2];
            iArr2[3] = i32 + iArr[3];
            iArr2[4] = i31 + iArr[4];
            iArr2[5] = i30 + iArr[5];
            iArr2[6] = i29 + iArr[6];
            iArr2[7] = i28 + iArr[7];
            iArr2[8] = i27 + iArr[8];
            iArr2[9] = i26 + iArr[9];
            iArr2[10] = i25 + iArr[10];
            iArr2[11] = i24 + iArr[11];
            iArr2[12] = i23 + iArr[12];
            iArr2[13] = i22 + iArr[13];
            iArr2[14] = i21 + iArr[14];
            iArr2[15] = i20 + iArr[15];
        } else {
            throw new IllegalArgumentException("Number of rounds must be even");
        }
    }

    /* access modifiers changed from: protected */
    public void advanceCounter() {
        int[] iArr = this.engineState;
        int i = iArr[8] + 1;
        iArr[8] = i;
        if (i == 0) {
            iArr[9] = iArr[9] + 1;
        }
    }

    /* access modifiers changed from: protected */
    public void advanceCounter(long j) {
        int i = (int) (j >>> 32);
        int i2 = (int) j;
        if (i > 0) {
            int[] iArr = this.engineState;
            iArr[9] = iArr[9] + i;
        }
        int[] iArr2 = this.engineState;
        int i3 = iArr2[8];
        iArr2[8] = iArr2[8] + i2;
        if (i3 != 0 && iArr2[8] < i3) {
            iArr2[9] = iArr2[9] + 1;
        }
    }

    /* access modifiers changed from: protected */
    public void generateKeyStream(byte[] bArr) {
        salsaCore(this.rounds, this.engineState, this.x);
        Pack.intToLittleEndian(this.x, bArr, 0);
    }

    public String getAlgorithmName() {
        String str = "Salsa20";
        if (this.rounds == 20) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("/");
        sb.append(this.rounds);
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public long getCounter() {
        int[] iArr = this.engineState;
        return (((long) iArr[9]) << 32) | (((long) iArr[8]) & BodyPartID.bodyIdMax);
    }

    /* access modifiers changed from: protected */
    public int getNonceSize() {
        return 8;
    }

    public long getPosition() {
        return (getCounter() * 64) + ((long) this.index);
    }

    public void init(boolean z, CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithIV) {
            ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
            byte[] iv = parametersWithIV.getIV();
            if (iv == null || iv.length != getNonceSize()) {
                StringBuilder sb = new StringBuilder();
                sb.append(getAlgorithmName());
                sb.append(" requires exactly ");
                sb.append(getNonceSize());
                sb.append(" bytes of IV");
                throw new IllegalArgumentException(sb.toString());
            }
            CipherParameters parameters = parametersWithIV.getParameters();
            if (parameters == null) {
                if (this.initialised) {
                    setKey(null, iv);
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(getAlgorithmName());
                    sb2.append(" KeyParameter can not be null for first initialisation");
                    throw new IllegalStateException(sb2.toString());
                }
            } else if (parameters instanceof KeyParameter) {
                setKey(((KeyParameter) parameters).getKey(), iv);
            } else {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(getAlgorithmName());
                sb3.append(" Init parameters must contain a KeyParameter (or null for re-init)");
                throw new IllegalArgumentException(sb3.toString());
            }
            reset();
            this.initialised = true;
            return;
        }
        StringBuilder sb4 = new StringBuilder();
        sb4.append(getAlgorithmName());
        sb4.append(" Init parameters must include an IV");
        throw new IllegalArgumentException(sb4.toString());
    }

    /* access modifiers changed from: protected */
    public void packTauOrSigma(int i, int[] iArr, int i2) {
        int i3 = (i - 16) / 4;
        int[] iArr2 = TAU_SIGMA;
        iArr[i2] = iArr2[i3];
        iArr[i2 + 1] = iArr2[i3 + 1];
        iArr[i2 + 2] = iArr2[i3 + 2];
        iArr[i2 + 3] = iArr2[i3 + 3];
    }

    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        if (!this.initialised) {
            StringBuilder sb = new StringBuilder();
            sb.append(getAlgorithmName());
            sb.append(" not initialised");
            throw new IllegalStateException(sb.toString());
        } else if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        } else if (i3 + i2 > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        } else if (!limitExceeded(i2)) {
            for (int i4 = 0; i4 < i2; i4++) {
                int i5 = i4 + i3;
                byte[] bArr3 = this.keyStream;
                int i6 = this.index;
                bArr2[i5] = (byte) (bArr3[i6] ^ bArr[i4 + i]);
                this.index = (i6 + 1) & 63;
                if (this.index == 0) {
                    advanceCounter();
                    generateKeyStream(this.keyStream);
                }
            }
            return i2;
        } else {
            throw new MaxBytesExceededException("2^70 byte limit per IV would be exceeded; Change IV");
        }
    }

    public void reset() {
        this.index = 0;
        resetLimitCounter();
        resetCounter();
        generateKeyStream(this.keyStream);
    }

    /* access modifiers changed from: protected */
    public void resetCounter() {
        int[] iArr = this.engineState;
        iArr[9] = 0;
        iArr[8] = 0;
    }

    /* access modifiers changed from: protected */
    public void retreatCounter() {
        int[] iArr = this.engineState;
        if (iArr[8] == 0 && iArr[9] == 0) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
        int[] iArr2 = this.engineState;
        int i = iArr2[8] - 1;
        iArr2[8] = i;
        if (i == -1) {
            iArr2[9] = iArr2[9] - 1;
        }
    }

    /* access modifiers changed from: protected */
    public void retreatCounter(long j) {
        int i = (int) (j >>> 32);
        int i2 = (int) j;
        String str = "attempt to reduce counter past zero.";
        if (i != 0) {
            int[] iArr = this.engineState;
            if ((((long) iArr[9]) & BodyPartID.bodyIdMax) >= (((long) i) & BodyPartID.bodyIdMax)) {
                iArr[9] = iArr[9] - i;
            } else {
                throw new IllegalStateException(str);
            }
        }
        int[] iArr2 = this.engineState;
        if ((((long) iArr2[8]) & BodyPartID.bodyIdMax) >= (BodyPartID.bodyIdMax & ((long) i2))) {
            iArr2[8] = iArr2[8] - i2;
        } else if (iArr2[9] != 0) {
            iArr2[9] = iArr2[9] - 1;
            iArr2[8] = iArr2[8] - i2;
        } else {
            throw new IllegalStateException(str);
        }
    }

    public byte returnByte(byte b) {
        if (!limitExceeded()) {
            byte[] bArr = this.keyStream;
            int i = this.index;
            byte b2 = (byte) (b ^ bArr[i]);
            this.index = (i + 1) & 63;
            if (this.index == 0) {
                advanceCounter();
                generateKeyStream(this.keyStream);
            }
            return b2;
        }
        throw new MaxBytesExceededException("2^70 byte limit per IV; Change IV");
    }

    public long seekTo(long j) {
        reset();
        return skip(j);
    }

    /* access modifiers changed from: protected */
    public void setKey(byte[] bArr, byte[] bArr2) {
        if (bArr != null) {
            if (bArr.length == 16 || bArr.length == 32) {
                int length = (bArr.length - 16) / 4;
                int[] iArr = this.engineState;
                int[] iArr2 = TAU_SIGMA;
                iArr[0] = iArr2[length];
                iArr[5] = iArr2[length + 1];
                iArr[10] = iArr2[length + 2];
                iArr[15] = iArr2[length + 3];
                Pack.littleEndianToInt(bArr, 0, iArr, 1, 4);
                Pack.littleEndianToInt(bArr, bArr.length - 16, this.engineState, 11, 4);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(getAlgorithmName());
                sb.append(" requires 128 bit or 256 bit key");
                throw new IllegalArgumentException(sb.toString());
            }
        }
        Pack.littleEndianToInt(bArr2, 0, this.engineState, 6, 2);
    }

    public long skip(long j) {
        long j2;
        if (j >= 0) {
            if (j >= 64) {
                long j3 = j / 64;
                advanceCounter(j3);
                j2 = j - (j3 * 64);
            } else {
                j2 = j;
            }
            int i = this.index;
            this.index = (((int) j2) + i) & 63;
            if (this.index < i) {
                advanceCounter();
            }
        } else {
            long j4 = -j;
            if (j4 >= 64) {
                long j5 = j4 / 64;
                retreatCounter(j5);
                j4 -= j5 * 64;
            }
            for (long j6 = 0; j6 < j4; j6++) {
                if (this.index == 0) {
                    retreatCounter();
                }
                this.index = (this.index - 1) & 63;
            }
        }
        generateKeyStream(this.keyStream);
        return j;
    }
}
