package org.bouncycastle.pqc.crypto.gmss.util;

import org.bouncycastle.crypto.Digest;

public class WinternitzOTSVerify {
    private Digest messDigestOTS;
    private int w;

    public WinternitzOTSVerify(Digest digest, int i) {
        this.w = i;
        this.messDigestOTS = digest;
    }

    public byte[] Verify(byte[] bArr, byte[] bArr2) {
        int i;
        int i2;
        byte[] bArr3 = bArr;
        byte[] bArr4 = bArr2;
        int digestSize = this.messDigestOTS.getDigestSize();
        byte[] bArr5 = new byte[digestSize];
        int i3 = 0;
        this.messDigestOTS.update(bArr3, 0, bArr3.length);
        byte[] bArr6 = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(bArr6, 0);
        int i4 = digestSize << 3;
        int i5 = this.w;
        int i6 = ((i5 - 1) + i4) / i5;
        int log = getLog((i6 << i5) + 1);
        int i7 = this.w;
        int i8 = ((((log + i7) - 1) / i7) + i6) * digestSize;
        if (i8 != bArr4.length) {
            return null;
        }
        byte[] bArr7 = new byte[i8];
        int i9 = 8;
        if (8 % i7 == 0) {
            int i10 = 8 / i7;
            int i11 = (1 << i7) - 1;
            int i12 = 0;
            int i13 = 0;
            byte[] bArr8 = new byte[digestSize];
            int i14 = 0;
            while (i14 < bArr6.length) {
                byte[] bArr9 = bArr8;
                int i15 = i13;
                int i16 = i12;
                int i17 = 0;
                while (i17 < i10) {
                    int i18 = bArr6[i14] & i11;
                    i16 += i18;
                    int i19 = i10;
                    int i20 = i15 * digestSize;
                    System.arraycopy(bArr4, i20, bArr9, 0, digestSize);
                    while (true) {
                        int i21 = i16;
                        if (i18 >= i11) {
                            break;
                        }
                        this.messDigestOTS.update(bArr9, 0, bArr9.length);
                        bArr9 = new byte[this.messDigestOTS.getDigestSize()];
                        this.messDigestOTS.doFinal(bArr9, 0);
                        i18++;
                        byte[] bArr10 = bArr2;
                        i16 = i21;
                    }
                    System.arraycopy(bArr9, 0, bArr7, i20, digestSize);
                    bArr6[i14] = (byte) (bArr6[i14] >>> this.w);
                    i15++;
                    i17++;
                    i10 = i19;
                    bArr4 = bArr2;
                }
                int i22 = i10;
                i14++;
                bArr4 = bArr2;
                i12 = i16;
                i13 = i15;
                bArr8 = bArr9;
            }
            int i23 = (i6 << this.w) - i12;
            int i24 = 0;
            while (i24 < log) {
                int i25 = i13 * digestSize;
                System.arraycopy(bArr2, i25, bArr8, 0, digestSize);
                for (int i26 = i23 & i11; i26 < i11; i26++) {
                    this.messDigestOTS.update(bArr8, 0, bArr8.length);
                    bArr8 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(bArr8, 0);
                }
                System.arraycopy(bArr8, 0, bArr7, i25, digestSize);
                int i27 = this.w;
                i23 >>>= i27;
                i13++;
                i24 += i27;
            }
        } else {
            byte[] bArr11 = bArr4;
            if (i7 < 8) {
                int i28 = digestSize / i7;
                int i29 = (1 << i7) - 1;
                int i30 = 0;
                int i31 = 0;
                int i32 = 0;
                byte[] bArr12 = new byte[digestSize];
                int i33 = 0;
                while (i33 < i28) {
                    int i34 = i30;
                    long j = 0;
                    for (int i35 = 0; i35 < this.w; i35++) {
                        j ^= (long) ((bArr6[i34] & 255) << (i35 << 3));
                        i34++;
                    }
                    int i36 = 0;
                    byte[] bArr13 = bArr12;
                    while (true) {
                        i2 = i33;
                        if (i36 >= i9) {
                            break;
                        }
                        int i37 = (int) (j & ((long) i29));
                        i31 += i37;
                        int i38 = i32 * digestSize;
                        System.arraycopy(bArr11, i38, bArr13, 0, digestSize);
                        while (i37 < i29) {
                            this.messDigestOTS.update(bArr13, 0, bArr13.length);
                            bArr13 = new byte[this.messDigestOTS.getDigestSize()];
                            this.messDigestOTS.doFinal(bArr13, 0);
                            i37++;
                        }
                        System.arraycopy(bArr13, 0, bArr7, i38, digestSize);
                        j >>>= this.w;
                        i32++;
                        i36++;
                        i33 = i2;
                        i9 = 8;
                    }
                    i33 = i2 + 1;
                    bArr12 = bArr13;
                    i30 = i34;
                    i9 = 8;
                }
                int i39 = digestSize % this.w;
                long j2 = 0;
                for (int i40 = 0; i40 < i39; i40++) {
                    j2 ^= (long) ((bArr6[i30] & 255) << (i40 << 3));
                    i30++;
                }
                int i41 = i39 << 3;
                int i42 = 0;
                byte[] bArr14 = bArr12;
                while (i42 < i41) {
                    int i43 = (int) (j2 & ((long) i29));
                    i31 += i43;
                    int i44 = i32 * digestSize;
                    System.arraycopy(bArr11, i44, bArr14, 0, digestSize);
                    while (i43 < i29) {
                        this.messDigestOTS.update(bArr14, 0, bArr14.length);
                        bArr14 = new byte[this.messDigestOTS.getDigestSize()];
                        this.messDigestOTS.doFinal(bArr14, 0);
                        i43++;
                    }
                    System.arraycopy(bArr14, 0, bArr7, i44, digestSize);
                    int i45 = this.w;
                    j2 >>>= i45;
                    i32++;
                    i42 += i45;
                }
                int i46 = (i6 << this.w) - i31;
                int i47 = 0;
                while (i47 < log) {
                    int i48 = i32 * digestSize;
                    System.arraycopy(bArr11, i48, bArr14, 0, digestSize);
                    for (int i49 = i46 & i29; i49 < i29; i49++) {
                        this.messDigestOTS.update(bArr14, 0, bArr14.length);
                        bArr14 = new byte[this.messDigestOTS.getDigestSize()];
                        this.messDigestOTS.doFinal(bArr14, 0);
                    }
                    System.arraycopy(bArr14, 0, bArr7, i48, digestSize);
                    int i50 = this.w;
                    i46 >>>= i50;
                    i32++;
                    i47 += i50;
                }
            } else if (i7 < 57) {
                int i51 = i4 - i7;
                int i52 = (1 << i7) - 1;
                byte[] bArr15 = new byte[digestSize];
                int i53 = 0;
                int i54 = 0;
                int i55 = 0;
                while (i53 <= i51) {
                    int i56 = i53 >>> 3;
                    int i57 = i53 % 8;
                    int i58 = i53 + this.w;
                    int i59 = i3;
                    long j3 = 0;
                    while (i56 < ((i58 + 7) >>> 3)) {
                        j3 ^= (long) ((bArr6[i56] & 255) << (i59 << 3));
                        i59++;
                        i56++;
                        log = log;
                        i51 = i51;
                    }
                    int i60 = i51;
                    int i61 = log;
                    int i62 = i6;
                    long j4 = (long) i52;
                    long j5 = (j3 >>> i57) & j4;
                    int i63 = i52;
                    i54 = (int) (((long) i54) + j5);
                    int i64 = i55 * digestSize;
                    int i65 = i58;
                    System.arraycopy(bArr11, i64, bArr15, 0, digestSize);
                    while (j5 < j4) {
                        long j6 = j4;
                        this.messDigestOTS.update(bArr15, 0, bArr15.length);
                        bArr15 = new byte[this.messDigestOTS.getDigestSize()];
                        this.messDigestOTS.doFinal(bArr15, 0);
                        j5++;
                        j4 = j6;
                    }
                    System.arraycopy(bArr15, 0, bArr7, i64, digestSize);
                    i55++;
                    i6 = i62;
                    i52 = i63;
                    i53 = i65;
                    log = i61;
                    i51 = i60;
                    i3 = 0;
                }
                int i66 = log;
                int i67 = i6;
                int i68 = i52;
                int i69 = i53 >>> 3;
                if (i69 < digestSize) {
                    int i70 = i53 % 8;
                    int i71 = 0;
                    long j7 = 0;
                    while (i69 < digestSize) {
                        j7 ^= (long) ((bArr6[i69] & 255) << (i71 << 3));
                        i71++;
                        i69++;
                    }
                    i = i68;
                    long j8 = (long) i;
                    long j9 = (j7 >>> i70) & j8;
                    i54 = (int) (((long) i54) + j9);
                    int i72 = i55 * digestSize;
                    System.arraycopy(bArr11, i72, bArr15, 0, digestSize);
                    while (j9 < j8) {
                        long j10 = j8;
                        this.messDigestOTS.update(bArr15, 0, bArr15.length);
                        bArr15 = new byte[this.messDigestOTS.getDigestSize()];
                        this.messDigestOTS.doFinal(bArr15, 0);
                        j9++;
                        j8 = j10;
                    }
                    System.arraycopy(bArr15, 0, bArr7, i72, digestSize);
                    i55++;
                } else {
                    i = i68;
                }
                int i73 = (i67 << this.w) - i54;
                int i74 = i66;
                int i75 = 0;
                while (i75 < i74) {
                    int i76 = i55 * digestSize;
                    System.arraycopy(bArr11, i76, bArr15, 0, digestSize);
                    byte[] bArr16 = bArr7;
                    for (long j11 = (long) (i73 & i); j11 < ((long) i); j11++) {
                        this.messDigestOTS.update(bArr15, 0, bArr15.length);
                        bArr15 = new byte[this.messDigestOTS.getDigestSize()];
                        this.messDigestOTS.doFinal(bArr15, 0);
                    }
                    byte[] bArr17 = bArr16;
                    System.arraycopy(bArr15, 0, bArr17, i76, digestSize);
                    int i77 = this.w;
                    i73 >>>= i77;
                    i55++;
                    i75 += i77;
                    bArr7 = bArr17;
                }
            }
        }
        byte[] bArr18 = bArr7;
        byte[] bArr19 = new byte[digestSize];
        this.messDigestOTS.update(bArr18, 0, bArr18.length);
        byte[] bArr20 = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(bArr20, 0);
        return bArr20;
    }

    public int getLog(int i) {
        int i2 = 1;
        int i3 = 2;
        while (i3 < i) {
            i3 <<= 1;
            i2++;
        }
        return i2;
    }

    public int getSignatureLength() {
        int digestSize = this.messDigestOTS.getDigestSize();
        int i = digestSize << 3;
        int i2 = this.w;
        int i3 = (i + (i2 - 1)) / i2;
        int log = getLog((i3 << i2) + 1);
        int i4 = this.w;
        return digestSize * (i3 + (((log + i4) - 1) / i4));
    }
}
