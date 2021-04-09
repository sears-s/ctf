package org.bouncycastle.pqc.crypto.qtesla;

import java.security.SecureRandom;

public class QTESLA {
    private static int absolute(int i) {
        int i2 = i >> 31;
        return (i ^ i2) - i2;
    }

    private static long absolute(long j) {
        long j2 = j >> 63;
        return (j ^ j2) - j2;
    }

    private static boolean checkPolynomial(int[] iArr, int i, int i2, int i3) {
        int i4;
        int[] iArr2 = new int[i2];
        for (int i5 = 0; i5 < i2; i5++) {
            iArr2[i5] = absolute(iArr[i5]);
        }
        int i6 = i2;
        int i7 = 0;
        for (int i8 = 0; i8 < i3; i8++) {
            int i9 = 0;
            while (true) {
                i4 = i6 - 1;
                if (i9 >= i4) {
                    break;
                }
                int i10 = i9 + 1;
                int i11 = (iArr2[i10] - iArr2[i9]) >> 31;
                int i12 = iArr2[i10] & i11;
                int i13 = iArr2[i9];
                int i14 = ~i11;
                int i15 = i12 | (i13 & i14);
                iArr2[i10] = (i11 & iArr2[i9]) | (iArr2[i10] & i14);
                iArr2[i9] = i15;
                i9 = i10;
            }
            i7 += iArr2[i4];
            i6--;
        }
        return i7 > i;
    }

    private static boolean checkPolynomial(long[] jArr, int i, int i2, int i3, int i4) {
        int i5;
        short[] sArr = new short[i3];
        for (int i6 = 0; i6 < i3; i6++) {
            sArr[i6] = (short) ((int) absolute(jArr[i + i6]));
        }
        int i7 = 0;
        for (int i8 = 0; i8 < i4; i8++) {
            int i9 = 0;
            while (true) {
                i5 = i3 - 1;
                if (i9 >= i5) {
                    break;
                }
                int i10 = i9 + 1;
                short s = (short) ((sArr[i10] - sArr[i9]) >> 15);
                short s2 = sArr[i10] & s;
                short s3 = sArr[i9];
                int i11 = ~s;
                short s4 = (short) (s2 | (s3 & i11));
                sArr[i10] = (short) ((s & sArr[i9]) | (sArr[i10] & i11));
                sArr[i9] = s4;
                i9 = i10;
            }
            i7 += sArr[i5];
            i3--;
        }
        return i7 > i2;
    }

    private static int generateKeyPair(byte[] bArr, byte[] bArr2, SecureRandom secureRandom, int i, int i2, int i3, int i4, long j, int i5, int i6, int i7, double d, long[] jArr, int i8, int i9) {
        long[] jArr2;
        long[] jArr3;
        long[] jArr4;
        long[] jArr5;
        int i10;
        int i11;
        long[] jArr6;
        byte[] bArr3 = bArr;
        int i12 = i;
        int i13 = i2;
        int i14 = i3;
        int i15 = i4;
        byte[] bArr4 = new byte[32];
        int i16 = (i13 + 3) * 32;
        byte[] bArr5 = new byte[i16];
        long[] jArr7 = new long[i12];
        long[] jArr8 = new long[i12];
        int i17 = i12 * i13;
        long[] jArr9 = new long[i17];
        long[] jArr10 = new long[i17];
        long[] jArr11 = new long[i17];
        secureRandom.nextBytes(bArr4);
        if (i15 == 485978113) {
            i10 = 485978113;
            jArr2 = jArr11;
            jArr5 = jArr10;
            jArr4 = jArr9;
            jArr3 = jArr8;
            HashUtils.secureHashAlgorithmKECCAK128(bArr5, 0, i16, bArr4, 0, 32);
        } else {
            i10 = 485978113;
            jArr2 = jArr11;
            jArr5 = jArr10;
            jArr4 = jArr9;
            jArr3 = jArr8;
        }
        if (i15 == 1129725953) {
            i11 = 1129725953;
            HashUtils.secureHashAlgorithmKECCAK256(bArr5, 0, i16, bArr4, 0, 32);
        } else {
            i11 = 1129725953;
        }
        int i18 = 0;
        int i19 = 0;
        int i20 = 0;
        while (i19 < i13) {
            while (true) {
                if (i15 == i10) {
                    i20++;
                    jArr6 = jArr4;
                    Sample.polynomialGaussSamplerIP(jArr6, i12 * i19, bArr5, i19 * 32, i20);
                } else {
                    jArr6 = jArr4;
                }
                if (i15 == i11) {
                    i20++;
                    Sample.polynomialGaussSamplerIIIP(jArr6, i12 * i19, bArr5, i19 * 32, i20);
                }
                if (!checkPolynomial(jArr6, i12 * i19, i8, i12, i14)) {
                    break;
                }
                jArr4 = jArr6;
            }
            i19++;
            jArr4 = jArr6;
        }
        long[] jArr12 = jArr4;
        while (true) {
            if (i15 == i10) {
                i20++;
                Sample.polynomialGaussSamplerIP(jArr7, i18, bArr5, i13 * 32, i20);
            }
            if (i15 == i11) {
                i20++;
                Sample.polynomialGaussSamplerIIIP(jArr7, i18, bArr5, i13 * 32, i20);
            }
            if (!checkPolynomial(jArr7, i18, i9, i12, i14)) {
                break;
            }
            int i21 = i18;
            int i22 = i10;
            byte[] bArr6 = bArr3;
            long[] jArr13 = jArr7;
            bArr3 = bArr6;
            i10 = i22;
            i18 = i21;
        }
        int i23 = (i13 + 1) * 32;
        int i24 = i10;
        int i25 = i23;
        long[] jArr14 = jArr12;
        int i26 = i18;
        long[] jArr15 = jArr7;
        Polynomial.polynomialUniform(jArr5, bArr5, i23, i, i2, i4, j, i5, i6, i7);
        long[] jArr16 = jArr3;
        Polynomial.polynomialNumberTheoreticTransform(jArr16, jArr15, i12);
        int i27 = i26;
        while (i27 < i13) {
            int i28 = i12 * i27;
            int i29 = i27;
            Polynomial.polynomialMultiplication(jArr2, i28, jArr5, i28, jArr16, 0, i, i4, j);
            Polynomial.polynomialAddition(jArr2, i28, jArr2, i28, jArr14, i28, i);
            for (int i30 = i26; i30 < i12; i30++) {
                long j2 = (long) i15;
                int i31 = i28 + i30;
                long[] jArr17 = jArr2;
                jArr17[i31] = jArr17[i31] - (j2 & ((j2 - jArr17[i31]) >> 63));
            }
            long[] jArr18 = jArr2;
            i27 = i29 + 1;
        }
        long[] jArr19 = jArr2;
        Pack.packPrivateKey(bArr2, jArr15, jArr14, bArr5, i25, i, i2);
        byte[] bArr7 = bArr;
        int i32 = i25;
        if (i15 == 485978113) {
            Pack.encodePublicKeyIP(bArr7, jArr19, bArr5, i32);
        }
        if (i15 == 1129725953) {
            Pack.encodePublicKeyIIIP(bArr7, jArr19, bArr5, i32);
        }
        return i26;
    }

    private static int generateKeyPair(byte[] bArr, byte[] bArr2, SecureRandom secureRandom, int i, int i2, int i3, long j, int i4, int i5, int i6, double d, int[] iArr, int i7, int i8) {
        int[] iArr2;
        int i9;
        int i10;
        int[] iArr3;
        int[] iArr4;
        int i11;
        boolean z;
        char c;
        int i12;
        int[] iArr5;
        int i13;
        boolean z2;
        byte[] bArr3 = bArr2;
        int i14 = i;
        int i15 = i2;
        int i16 = i3;
        byte[] bArr4 = new byte[32];
        byte[] bArr5 = new byte[128];
        int[] iArr6 = new int[i14];
        int[] iArr7 = new int[i14];
        int[] iArr8 = new int[i14];
        int[] iArr9 = new int[i14];
        secureRandom.nextBytes(bArr4);
        if (i16 == 4205569) {
            i9 = 4205569;
            iArr2 = iArr9;
            HashUtils.secureHashAlgorithmKECCAK128(bArr5, 0, 128, bArr4, 0, 32);
        } else {
            i9 = 4205569;
            iArr2 = iArr9;
        }
        if (i16 == 4206593 || i16 == 8404993) {
            byte[] bArr6 = bArr4;
            i10 = 4206593;
            HashUtils.secureHashAlgorithmKECCAK256(bArr5, 0, 128, bArr6, 0, 32);
        } else {
            i10 = 4206593;
        }
        int i17 = 0;
        int i18 = 0;
        while (true) {
            if (i16 == i9) {
                i18++;
                Sample.polynomialGaussSamplerI(iArr7, i17, bArr5, i17, i18);
            }
            if (i16 == i10) {
                i11 = i18 + 1;
                iArr4 = iArr8;
                iArr3 = iArr7;
                Sample.polynomialGaussSamplerIII(iArr7, 0, bArr5, 0, i11, i, d, Sample.EXPONENTIAL_DISTRIBUTION_III_SIZE);
            } else {
                iArr4 = iArr8;
                iArr3 = iArr7;
                i11 = i18;
            }
            if (i16 == 8404993) {
                i11++;
                Sample.polynomialGaussSamplerIII(iArr3, 0, bArr5, 0, i11, i, d, Sample.EXPONENTIAL_DISTRIBUTION_III_SPEED);
            }
            iArr7 = iArr3;
            z = true;
            if (!checkPolynomial(iArr7, i7, i14, i15)) {
                break;
            }
            byte[] bArr7 = bArr;
            int i19 = i8;
            i18 = i11;
            iArr8 = iArr4;
            i17 = 0;
            i10 = Parameter.Q_III_SIZE;
        }
        while (true) {
            if (i16 == i9) {
                int i20 = i11 + 1;
                i12 = 0;
                c = ' ';
                Sample.polynomialGaussSamplerI(iArr6, 0, bArr5, 32, i20);
                i11 = i20;
            } else {
                i12 = 0;
                c = ' ';
            }
            if (i16 == 4206593) {
                i11++;
                i13 = i12;
                z2 = z;
                char c2 = c;
                iArr5 = iArr7;
                Sample.polynomialGaussSamplerIII(iArr6, 0, bArr5, 32, i11, i, d, Sample.EXPONENTIAL_DISTRIBUTION_III_SIZE);
            } else {
                i13 = i12;
                z2 = z;
                char c3 = c;
                iArr5 = iArr7;
            }
            if (i16 == 8404993) {
                i11++;
                Sample.polynomialGaussSamplerIII(iArr6, 0, bArr5, 32, i11, i, d, Sample.EXPONENTIAL_DISTRIBUTION_III_SPEED);
            }
            if (checkPolynomial(iArr6, i8, i14, i15) != z2) {
                break;
            }
            byte[] bArr8 = bArr;
            z = z2;
            iArr7 = iArr5;
            i9 = Parameter.Q_I;
        }
        int i21 = i;
        int i22 = i3;
        long j2 = j;
        Polynomial.polynomialUniform(iArr4, bArr5, 64, i21, i22, j2, i4, i5, i6);
        Polynomial.polynomialMultiplication(iArr2, iArr4, iArr6, i21, i22, j2, iArr);
        int[] iArr10 = iArr2;
        int[] iArr11 = iArr5;
        Polynomial.polynomialAdditionCorrection(iArr10, iArr10, iArr11, i14, i16);
        if (i16 == 4205569) {
            Pack.encodePrivateKeyI(bArr3, iArr6, iArr11, bArr5, 64);
            Pack.encodePublicKey(bArr, iArr10, bArr5, 64, 512, 23);
        }
        if (i16 == 4206593) {
            Pack.encodePrivateKeyIIISize(bArr3, iArr6, iArr11, bArr5, 64);
            Pack.encodePublicKey(bArr, iArr10, bArr5, 64, 1024, 23);
        }
        if (i16 == 8404993) {
            Pack.encodePrivateKeyIIISpeed(bArr3, iArr6, iArr11, bArr5, 64);
            Pack.encodePublicKeyIIISpeed(bArr, iArr10, bArr5, 64);
        }
        return i13;
    }

    public static int generateKeyPairI(byte[] bArr, byte[] bArr2, SecureRandom secureRandom) {
        return generateKeyPair(bArr, bArr2, secureRandom, 512, 30, Parameter.Q_I, Parameter.Q_INVERSE_I, 23, 19, Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_I, 27.0d, PolynomialHeuristic.ZETA_I, 1586, 1586);
    }

    public static int generateKeyPairIIIP(byte[] bArr, byte[] bArr2, SecureRandom secureRandom) {
        return generateKeyPair(bArr, bArr2, secureRandom, 2048, 5, 40, Parameter.Q_III_P, Parameter.Q_INVERSE_III_P, 31, 180, Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_III_P, 10.0d, PolynomialProvablySecure.ZETA_III_P, 901, 901);
    }

    public static int generateKeyPairIIISize(byte[] bArr, byte[] bArr2, SecureRandom secureRandom) {
        return generateKeyPair(bArr, bArr2, secureRandom, 1024, 48, Parameter.Q_III_SIZE, Parameter.Q_INVERSE_III_SIZE, 23, 38, Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_III_SIZE, 9.0d, PolynomialHeuristic.ZETA_III_SIZE, 910, 910);
    }

    public static int generateKeyPairIIISpeed(byte[] bArr, byte[] bArr2, SecureRandom secureRandom) {
        return generateKeyPair(bArr, bArr2, secureRandom, 1024, 48, Parameter.Q_III_SPEED, Parameter.Q_INVERSE_III_SPEED, 24, 38, Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_III_SPEED, 12.0d, PolynomialHeuristic.ZETA_III_SPEED, 1147, 1233);
    }

    public static int generateKeyPairIP(byte[] bArr, byte[] bArr2, SecureRandom secureRandom) {
        return generateKeyPair(bArr, bArr2, secureRandom, 1024, 4, 25, Parameter.Q_I_P, Parameter.Q_INVERSE_I_P, 29, 108, Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_I_P, 10.0d, PolynomialProvablySecure.ZETA_I_P, 554, 554);
    }

    private static void hashFunction(byte[] bArr, int i, int[] iArr, byte[] bArr2, int i2, int i3, int i4, int i5) {
        int i6 = i3 + 64;
        byte[] bArr3 = new byte[i6];
        for (int i7 = 0; i7 < i3; i7++) {
            int i8 = ((i5 / 2) - iArr[i7]) >> 31;
            iArr[i7] = ((~i8) & iArr[i7]) | ((iArr[i7] - i5) & i8);
            int i9 = 1 << i4;
            int i10 = iArr[i7] & (i9 - 1);
            int i11 = ((1 << (i4 - 1)) - i10) >> 31;
            bArr3[i7] = (byte) ((iArr[i7] - ((i10 & (~i11)) | ((i10 - i9) & i11))) >> i4);
        }
        System.arraycopy(bArr2, i2, bArr3, i3, 64);
        if (i5 == 4205569) {
            HashUtils.secureHashAlgorithmKECCAK128(bArr, i, 32, bArr3, 0, i6);
        }
        if (i5 == 4206593 || i5 == 8404993) {
            HashUtils.secureHashAlgorithmKECCAK256(bArr, i, 32, bArr3, 0, i6);
        }
    }

    private static void hashFunction(byte[] bArr, int i, long[] jArr, byte[] bArr2, int i2, int i3, int i4, int i5, int i6) {
        int i7 = i3;
        int i8 = i4;
        int i9 = i6;
        int i10 = i7 * i8;
        int i11 = i10 + 64;
        byte[] bArr3 = new byte[i11];
        int i12 = 0;
        while (i12 < i8) {
            int i13 = i7 * i12;
            int i14 = 0;
            while (i14 < i7) {
                long j = jArr[i13];
                long j2 = (((long) (i9 / 2)) - j) >> 63;
                int i15 = i12;
                long j3 = ((j - ((long) i9)) & j2) | (j & (~j2));
                int i16 = 1 << i5;
                long j4 = ((long) (i16 - 1)) & j3;
                long j5 = (((long) (1 << (i5 - 1))) - j4) >> 63;
                long j6 = ((~j5) & j4) | ((j4 - ((long) i16)) & j5);
                int i17 = i13 + 1;
                bArr3[i13] = (byte) ((int) ((j3 - j6) >> i5));
                i14++;
                i7 = i3;
                int i18 = i4;
                i13 = i17;
                i12 = i15;
            }
            i12++;
            i7 = i3;
            i8 = i4;
        }
        System.arraycopy(bArr2, i2, bArr3, i10, 64);
        if (i9 == 485978113) {
            HashUtils.secureHashAlgorithmKECCAK128(bArr, i, 32, bArr3, 0, i11);
        }
        if (i9 == 1129725953) {
            HashUtils.secureHashAlgorithmKECCAK256(bArr, i, 32, bArr3, 0, i11);
        }
    }

    private static int signing(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3, SecureRandom secureRandom, int i3, int i4, int i5, int i6, long j, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16, int i17) {
        long[] jArr;
        long[] jArr2;
        int[] iArr;
        long[] jArr3;
        long[] jArr4;
        short[] sArr;
        boolean z;
        long[] jArr5;
        int i18;
        long[] jArr6;
        byte[] bArr4;
        long[] jArr7;
        long[] jArr8;
        byte[] bArr5 = bArr;
        int i19 = i3;
        int i20 = i4;
        int i21 = i5;
        int i22 = i6;
        byte[] bArr6 = new byte[32];
        byte[] bArr7 = new byte[32];
        byte[] bArr8 = new byte[128];
        byte[] bArr9 = new byte[32];
        int[] iArr2 = new int[i21];
        int i23 = i19 * i20;
        long[] jArr9 = new long[i23];
        long[] jArr10 = new long[i23];
        long[] jArr11 = new long[i19];
        short[] sArr2 = new short[i21];
        long[] jArr12 = new long[i19];
        long[] jArr13 = new long[i19];
        long[] jArr14 = new long[i19];
        long[] jArr15 = new long[i23];
        secureRandom.nextBytes(bArr9);
        long[] jArr16 = jArr15;
        System.arraycopy(bArr9, 0, bArr8, 32, 32);
        long[] jArr17 = jArr14;
        System.arraycopy(bArr3, i15 - 32, bArr8, 0, 32);
        if (i22 == 485978113) {
            jArr = jArr11;
            jArr4 = jArr10;
            jArr3 = jArr9;
            jArr2 = jArr12;
            sArr = sArr2;
            iArr = iArr2;
            HashUtils.secureHashAlgorithmKECCAK128(bArr8, 64, 64, bArr2, 0, i2);
            z = true;
            HashUtils.secureHashAlgorithmKECCAK128(bArr7, 0, 32, bArr8, 0, 128);
        } else {
            jArr = jArr11;
            jArr4 = jArr10;
            jArr3 = jArr9;
            iArr = iArr2;
            jArr2 = jArr12;
            sArr = sArr2;
            z = true;
        }
        if (i22 == 1129725953) {
            boolean z2 = z;
            HashUtils.secureHashAlgorithmKECCAK256(bArr8, 64, 64, bArr2, 0, i2);
            HashUtils.secureHashAlgorithmKECCAK256(bArr7, 0, 32, bArr8, 0, 128);
        } else {
            boolean z3 = z;
        }
        byte[] bArr10 = bArr8;
        byte[] bArr11 = bArr7;
        byte[] bArr12 = bArr6;
        Polynomial.polynomialUniform(jArr3, bArr3, i15 - 64, i3, i4, i6, j, i7, i13, i14);
        int i24 = 0;
        boolean z4 = false;
        while (true) {
            boolean z5 = true;
            int i25 = i24 + 1;
            Sample.sampleY(jArr, bArr11, 0, i25, i3, i6, i8, i9);
            long[] jArr18 = jArr;
            long[] jArr19 = jArr2;
            Polynomial.polynomialNumberTheoreticTransform(jArr19, jArr18, i19);
            int i26 = 0;
            while (i26 < i20) {
                int i27 = i19 * i26;
                int i28 = i26;
                long[] jArr20 = jArr19;
                boolean z6 = z5;
                long[] jArr21 = jArr18;
                Polynomial.polynomialMultiplication(jArr4, i27, jArr3, i27, jArr19, 0, i3, i6, j);
                i26 = i28 + 1;
                z5 = z6;
                jArr19 = jArr20;
                jArr18 = jArr21;
            }
            long[] jArr22 = jArr19;
            boolean z7 = z5;
            long[] jArr23 = jArr18;
            hashFunction(bArr12, 0, jArr4, bArr10, 64, i3, i4, i10, i6);
            Sample.encodeC(iArr, sArr, bArr12, 0, i3, i5);
            int i29 = i3;
            Polynomial.sparsePolynomialMultiplication8(jArr17, 0, bArr3, 0, iArr, sArr, i29, i5);
            Polynomial.polynomialAddition(jArr13, 0, jArr23, 0, jArr17, 0, i29);
            long[] jArr24 = jArr13;
            if (testRejection(jArr24, i19, i8, i11) != z7) {
                int i30 = 0;
                while (true) {
                    if (i30 >= i20) {
                        jArr5 = jArr24;
                        break;
                    }
                    int i31 = i19 * i30;
                    int i32 = i30 + 1;
                    int i33 = i31;
                    int i34 = i3;
                    jArr5 = jArr24;
                    Polynomial.sparsePolynomialMultiplication8(jArr16, i33, bArr3, i19 * i32, iArr, sArr, i34, i5);
                    long[] jArr25 = jArr4;
                    Polynomial.polynomialSubtraction(jArr25, i33, jArr4, i31, jArr16, i31, i34, i6, i16, i17);
                    z4 = testV(jArr25, i33, i3, i10, i6, i12);
                    if (z4 == z7) {
                        break;
                    }
                    int i35 = i8;
                    int i36 = i11;
                    i30 = i32;
                    jArr24 = jArr5;
                }
                if (z4 != z7) {
                    break;
                }
                i24 = i25;
                jArr7 = jArr22;
                jArr8 = jArr23;
                jArr13 = jArr5;
            } else {
                jArr13 = jArr24;
                i24 = i25;
                jArr7 = jArr22;
                jArr8 = jArr23;
            }
        }
        if (i22 == 485978113) {
            bArr4 = bArr12;
            jArr6 = jArr5;
            i18 = 0;
            Pack.encodeSignatureIP(bArr5, 0, bArr4, 0, jArr6);
        } else {
            bArr4 = bArr12;
            jArr6 = jArr5;
            i18 = 0;
        }
        if (i22 == 1129725953) {
            Pack.encodeSignatureIIIP(bArr5, i18, bArr4, i18, jArr6);
        }
        return i18;
    }

    private static int signing(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3, SecureRandom secureRandom, int i3, int i4, int i5, long j, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int[] iArr) {
        int[] iArr2;
        int[] iArr3;
        short[] sArr;
        int[] iArr4;
        short[] sArr2;
        short[] sArr3;
        int[] iArr5;
        int[] iArr6;
        int[] iArr7;
        int[] iArr8;
        int[] iArr9;
        byte[] bArr4 = bArr3;
        int i16 = i3;
        int i17 = i4;
        int i18 = i5;
        byte[] bArr5 = new byte[32];
        byte[] bArr6 = new byte[32];
        byte[] bArr7 = new byte[128];
        byte[] bArr8 = new byte[64];
        byte[] bArr9 = new byte[32];
        int[] iArr10 = new int[i17];
        short[] sArr4 = new short[i16];
        short[] sArr5 = new short[i16];
        int[] iArr11 = new int[i16];
        int[] iArr12 = new int[i16];
        short[] sArr6 = new short[i17];
        int[] iArr13 = new int[i16];
        int[] iArr14 = new int[i16];
        int[] iArr15 = new int[i16];
        int[] iArr16 = new int[i16];
        if (i18 == 4205569) {
            Pack.decodePrivateKeyI(bArr8, sArr4, sArr5, bArr4);
        }
        int[] iArr17 = iArr11;
        if (i18 == 4206593) {
            Pack.decodePrivateKeyIIISize(bArr8, sArr4, sArr5, bArr4);
        }
        if (i18 == 8404993) {
            Pack.decodePrivateKeyIIISpeed(bArr8, sArr4, sArr5, bArr4);
        }
        secureRandom.nextBytes(bArr9);
        System.arraycopy(bArr9, 0, bArr7, 32, 32);
        System.arraycopy(bArr8, 32, bArr7, 0, 32);
        if (i18 == 4205569) {
            iArr2 = iArr12;
            sArr2 = sArr5;
            sArr = sArr4;
            iArr6 = iArr13;
            int[] iArr18 = iArr14;
            int[] iArr19 = iArr15;
            sArr3 = sArr6;
            iArr3 = iArr19;
            iArr4 = iArr10;
            HashUtils.secureHashAlgorithmKECCAK128(bArr7, 64, 64, bArr2, 0, i2);
            iArr5 = iArr18;
            HashUtils.secureHashAlgorithmKECCAK128(bArr6, 0, 32, bArr7, 0, 128);
        } else {
            iArr6 = iArr13;
            iArr2 = iArr12;
            sArr2 = sArr5;
            sArr = sArr4;
            iArr4 = iArr10;
            iArr5 = iArr14;
            int[] iArr20 = iArr15;
            sArr3 = sArr6;
            iArr3 = iArr20;
        }
        if (i18 == 4206593 || i18 == 8404993) {
            iArr7 = iArr5;
            HashUtils.secureHashAlgorithmKECCAK256(bArr7, 64, 64, bArr2, 0, i2);
            HashUtils.secureHashAlgorithmKECCAK256(bArr6, 0, 32, bArr7, 0, 128);
        } else {
            iArr7 = iArr5;
        }
        int[] iArr21 = iArr6;
        Polynomial.polynomialUniform(iArr17, bArr8, 0, i3, i5, j, i6, i12, i13);
        int i19 = 0;
        while (true) {
            int i20 = i19 + 1;
            Sample.sampleY(iArr21, bArr6, 0, i20, i3, i5, i7, i8);
            Polynomial.polynomialMultiplication(iArr2, iArr17, iArr21, i3, i5, j, iArr);
            hashFunction(bArr5, 0, iArr2, bArr7, 64, i3, i9, i5);
            int i21 = i3;
            int i22 = i4;
            Sample.encodeC(iArr4, sArr3, bArr5, 0, i21, i22);
            Polynomial.sparsePolynomialMultiplication16(iArr3, sArr, iArr4, sArr3, i21, i22);
            int[] iArr22 = iArr3;
            int[] iArr23 = iArr21;
            Polynomial.polynomialAddition(iArr7, iArr23, iArr22, i16);
            if (!testRejection(iArr7, i16, i7, i10)) {
                Polynomial.sparsePolynomialMultiplication16(iArr16, sArr2, iArr4, sArr3, i3, i4);
                iArr9 = iArr2;
                iArr8 = iArr16;
                Polynomial.polynomialSubtractionCorrection(iArr9, iArr9, iArr8, i16, i18);
                if (!testV(iArr9, i16, i9, i18, i11)) {
                    break;
                }
            } else {
                iArr9 = iArr2;
                int i23 = i9;
                int i24 = i11;
                iArr8 = iArr16;
            }
            iArr2 = iArr9;
            iArr16 = iArr8;
            iArr21 = iArr23;
            iArr3 = iArr22;
            i19 = i20;
        }
        if (i18 == 4205569) {
            Pack.encodeSignature(bArr, 0, bArr5, 0, iArr7, i3, i9);
        }
        if (i18 == 4206593) {
            Pack.encodeSignature(bArr, 0, bArr5, 0, iArr7, i3, i9);
        }
        if (i18 == 8404993) {
            Pack.encodeSignatureIIISpeed(bArr, 0, bArr5, 0, iArr7);
        }
        return 0;
    }

    static int signingI(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3, SecureRandom secureRandom) {
        return signing(bArr, bArr2, i, i2, bArr3, secureRandom, 512, 30, Parameter.Q_I, Parameter.Q_INVERSE_I, 23, 1048575, 20, 21, 1586, 1586, 19, Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_I, 1021, 32, PolynomialHeuristic.ZETA_I);
    }

    public static int signingIIIP(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3, SecureRandom secureRandom) {
        return signing(bArr, bArr2, i, i2, bArr3, secureRandom, 2048, 5, 40, Parameter.Q_III_P, Parameter.Q_INVERSE_III_P, 31, Parameter.B_III_P, 23, 24, 901, 901, 180, Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_III_P, Polynomial.PRIVATE_KEY_III_P, 15, 34);
    }

    static int signingIIISize(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3, SecureRandom secureRandom) {
        return signing(bArr, bArr2, i, i2, bArr3, secureRandom, 1024, 48, Parameter.Q_III_SIZE, Parameter.Q_INVERSE_III_SIZE, 23, 1048575, 20, 21, 910, 910, 38, Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_III_SIZE, 1021, 32, PolynomialHeuristic.ZETA_III_SIZE);
    }

    static int signingIIISpeed(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3, SecureRandom secureRandom) {
        return signing(bArr, bArr2, i, i2, bArr3, secureRandom, 1024, 48, Parameter.Q_III_SPEED, Parameter.Q_INVERSE_III_SPEED, 24, 2097151, 21, 22, 1233, 1147, 38, Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_III_SPEED, 511, 32, PolynomialHeuristic.ZETA_III_SPEED);
    }

    public static int signingIP(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3, SecureRandom secureRandom) {
        return signing(bArr, bArr2, i, i2, bArr3, secureRandom, 1024, 4, 25, Parameter.Q_I_P, Parameter.Q_INVERSE_I_P, 29, 2097151, 21, 22, 554, 554, 108, Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_I_P, Polynomial.PRIVATE_KEY_I_P, 1, 29);
    }

    private static boolean testRejection(int[] iArr, int i, int i2, int i3) {
        for (int i4 = 0; i4 < i; i4++) {
            if (absolute(iArr[i4]) > i2 - i3) {
                return true;
            }
        }
        return false;
    }

    private static boolean testRejection(long[] jArr, int i, int i2, int i3) {
        for (int i4 = 0; i4 < i; i4++) {
            if (absolute(jArr[i4]) > ((long) (i2 - i3))) {
                return true;
            }
        }
        return false;
    }

    private static boolean testV(int[] iArr, int i, int i2, int i3, int i4) {
        for (int i5 = 0; i5 < i; i5++) {
            int i6 = i3 / 2;
            int i7 = (i6 - iArr[i5]) >> 31;
            int i8 = ((~i7) & iArr[i5]) | ((iArr[i5] - i3) & i7);
            int i9 = 1 << (i2 - 1);
            if ((((~(absolute(i8) - (i6 - i4))) >>> 31) | ((~(absolute(i8 - ((((i8 + i9) - 1) >> i2) << i2)) - (i9 - i4))) >>> 31)) == 1) {
                return true;
            }
        }
        return false;
    }

    private static boolean testV(long[] jArr, int i, int i2, int i3, int i4, int i5) {
        int i6 = i4;
        int i7 = i2;
        for (int i8 = 0; i8 < i7; i8++) {
            int i9 = i6 / 2;
            int i10 = i + i8;
            long j = (((long) i9) - jArr[i10]) >> 63;
            long j2 = ((~j) & jArr[i10]) | ((jArr[i10] - ((long) i6)) & j);
            long j3 = (~(absolute(j2) - ((long) (i9 - i5)))) >>> 63;
            int i11 = 1 << (i3 - 1);
            if ((((~(absolute(j2 - (((long) ((int) (((((long) i11) + j2) - 1) >> i3))) << i3)) - ((long) (i11 - i5)))) >>> 63) | j3) == 1) {
                return true;
            }
        }
        return false;
    }

    private static boolean testZ(int[] iArr, int i, int i2, int i3) {
        for (int i4 = 0; i4 < i; i4++) {
            int i5 = i2 - i3;
            if (iArr[i4] < (-i5) || iArr[i4] > i5) {
                return true;
            }
        }
        return false;
    }

    private static boolean testZ(long[] jArr, int i, int i2, int i3) {
        for (int i4 = 0; i4 < i; i4++) {
            int i5 = i2 - i3;
            if (jArr[i4] < ((long) (-i5)) || jArr[i4] > ((long) i5)) {
                return true;
            }
        }
        return false;
    }

    private static int verifying(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3, int i3, int i4, int i5, int i6, long j, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, long[] jArr) {
        byte[] bArr4;
        byte[] bArr5 = bArr;
        byte[] bArr6 = bArr2;
        int i16 = i;
        byte[] bArr7 = bArr3;
        int i17 = i3;
        int i18 = i5;
        int i19 = i6;
        byte[] bArr8 = new byte[32];
        byte[] bArr9 = new byte[32];
        byte[] bArr10 = new byte[32];
        byte[] bArr11 = new byte[64];
        int i20 = i17 * i4;
        int[] iArr = new int[i20];
        int[] iArr2 = new int[i18];
        short[] sArr = new short[i18];
        long[] jArr2 = new long[i20];
        long[] jArr3 = new long[i17];
        long[] jArr4 = new long[i17];
        long[] jArr5 = new long[i20];
        long[] jArr6 = new long[i20];
        int[] iArr3 = iArr2;
        if (i2 < i11) {
            return -1;
        }
        if (i19 == 485978113) {
            Pack.decodeSignatureIP(bArr8, jArr3, bArr6, i16);
        }
        if (i19 == 1129725953) {
            Pack.decodeSignatureIIIP(bArr8, jArr3, bArr6, i16);
        }
        if (testZ(jArr3, i17, i8, i10)) {
            return -2;
        }
        if (i19 == 485978113) {
            Pack.decodePublicKeyIP(iArr, bArr10, 0, bArr7);
        }
        if (i19 == 1129725953) {
            Pack.decodePublicKeyIIIP(iArr, bArr10, 0, bArr7);
        }
        long[] jArr7 = jArr6;
        byte[] bArr12 = bArr11;
        int[] iArr4 = iArr;
        byte[] bArr13 = bArr9;
        byte[] bArr14 = bArr8;
        Polynomial.polynomialUniform(jArr6, bArr10, 0, i3, i4, i6, j, i7, i12, i13);
        Sample.encodeC(iArr3, sArr, bArr14, 0, i3, i5);
        Polynomial.polynomialNumberTheoreticTransform(jArr4, jArr3, i17);
        int i21 = i4;
        int i22 = 0;
        while (i22 < i21) {
            int i23 = i17 * i22;
            int i24 = i23;
            int i25 = i23;
            int i26 = i3;
            Polynomial.polynomialMultiplication(jArr2, i24, jArr7, i25, jArr4, 0, i26, i6, j);
            int i27 = i22;
            Polynomial.sparsePolynomialMultiplication32(jArr5, i24, iArr4, i25, iArr3, sArr, i26, i5, i6, i14, i15);
            Polynomial.polynomialSubtraction(jArr2, i24, jArr2, i25, jArr5, i23, i26, i6, i14, i15);
            i22 = i27 + 1;
        }
        int i28 = i6;
        if (i28 == 485978113) {
            bArr4 = bArr;
            HashUtils.secureHashAlgorithmKECCAK128(bArr12, 0, 64, bArr, 0, bArr4.length);
        } else {
            bArr4 = bArr;
        }
        if (i28 == 1129725953) {
            HashUtils.secureHashAlgorithmKECCAK256(bArr12, 0, 64, bArr, 0, bArr4.length);
        }
        hashFunction(bArr13, 0, jArr2, bArr12, 0, i3, i4, i9, i6);
        return !CommonFunction.memoryEqual(bArr14, 0, bArr13, 0, 32) ? -3 : 0;
    }

    private static int verifying(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3, int i3, int i4, int i5, long j, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int[] iArr) {
        int[] iArr2;
        short[] sArr;
        int[] iArr3;
        int[] iArr4;
        int[] iArr5;
        int[] iArr6;
        int i16;
        byte[] bArr4 = bArr;
        int i17 = i3;
        int i18 = i4;
        int i19 = i5;
        byte[] bArr5 = new byte[32];
        byte[] bArr6 = new byte[32];
        byte[] bArr7 = new byte[32];
        byte[] bArr8 = new byte[64];
        int[] iArr7 = new int[i17];
        int[] iArr8 = new int[i18];
        short[] sArr2 = new short[i18];
        int[] iArr9 = new int[i17];
        int[] iArr10 = new int[i17];
        int[] iArr11 = new int[i17];
        int[] iArr12 = new int[i17];
        if (i2 < i11) {
            return -1;
        }
        if (i19 == 4205569 || i19 == 4206593) {
            iArr5 = iArr12;
            iArr4 = iArr11;
            iArr6 = iArr10;
            iArr3 = iArr9;
            sArr = sArr2;
            iArr2 = iArr8;
            Pack.decodeSignature(bArr5, iArr10, bArr2, i, i3, i8);
        } else {
            iArr5 = iArr12;
            iArr4 = iArr11;
            iArr6 = iArr10;
            iArr3 = iArr9;
            sArr = sArr2;
            iArr2 = iArr8;
        }
        if (i19 == 8404993) {
            Pack.decodeSignatureIIISpeed(bArr5, iArr6, bArr2, i);
        }
        if (testZ(iArr6, i17, i7, i9)) {
            return -2;
        }
        if (i19 == 4205569 || i19 == 4206593) {
            i16 = 8404993;
            Pack.decodePublicKey(iArr7, bArr7, 0, bArr3, i3, i6);
        } else {
            i16 = 8404993;
        }
        if (i19 == i16) {
            Pack.decodePublicKeyIIISpeed(iArr7, bArr7, 0, bArr3);
        }
        int[] iArr13 = iArr7;
        byte[] bArr9 = bArr8;
        byte[] bArr10 = bArr6;
        Polynomial.polynomialUniform(iArr5, bArr7, 0, i3, i5, j, i6, i12, i13);
        int i20 = i3;
        int i21 = i4;
        Sample.encodeC(iArr2, sArr, bArr5, 0, i20, i21);
        Polynomial.sparsePolynomialMultiplication32(iArr4, iArr13, iArr2, sArr, i20, i21);
        int[] iArr14 = iArr3;
        int i22 = i3;
        int i23 = i5;
        long j2 = j;
        Polynomial.polynomialMultiplication(iArr14, iArr5, iArr6, i22, i23, j2, iArr);
        Polynomial.polynomialSubtractionMontgomery(iArr14, iArr3, iArr4, i22, i23, j2, i10);
        if (i19 == 4205569) {
            HashUtils.secureHashAlgorithmKECCAK128(bArr9, 0, 64, bArr, 0, bArr4.length);
        }
        if (i19 == 4206593 || i19 == i16) {
            HashUtils.secureHashAlgorithmKECCAK256(bArr9, 0, 64, bArr, 0, bArr4.length);
        }
        hashFunction(bArr10, 0, iArr3, bArr9, 0, i3, i8, i5);
        return !CommonFunction.memoryEqual(bArr5, 0, bArr10, 0, 32) ? -3 : 0;
    }

    static int verifyingI(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3) {
        return verifying(bArr, bArr2, i, i2, bArr3, 512, 30, (int) Parameter.Q_I, (long) Parameter.Q_INVERSE_I, 23, 1048575, 21, 1586, (int) Parameter.R_I, (int) Polynomial.SIGNATURE_I, 19, (int) Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_I, 1021, 32, PolynomialHeuristic.ZETA_I);
    }

    static int verifyingIIISize(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3) {
        return verifying(bArr, bArr2, i, i2, bArr3, 1024, 48, (int) Parameter.Q_III_SIZE, (long) Parameter.Q_INVERSE_III_SIZE, 23, 1048575, 21, 910, (int) Parameter.R_III_SIZE, (int) Polynomial.SIGNATURE_III_SIZE, 38, (int) Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_III_SIZE, 1021, 32, PolynomialHeuristic.ZETA_III_SIZE);
    }

    static int verifyingIIISpeed(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3) {
        return verifying(bArr, bArr2, i, i2, bArr3, 1024, 48, (int) Parameter.Q_III_SPEED, (long) Parameter.Q_INVERSE_III_SPEED, 24, 2097151, 22, 1233, (int) Parameter.R_III_SPEED, 2848, 38, (int) Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_III_SPEED, 511, 32, PolynomialHeuristic.ZETA_III_SPEED);
    }

    static int verifyingPI(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3) {
        return verifying(bArr, bArr2, i, i2, bArr3, 1024, 4, 25, (int) Parameter.Q_I_P, (long) Parameter.Q_INVERSE_I_P, 29, 2097151, 22, 554, 2848, 108, (int) Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_I_P, 1, 29, PolynomialProvablySecure.ZETA_I_P);
    }

    static int verifyingPIII(byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3) {
        return verifying(bArr, bArr2, i, i2, bArr3, 2048, 5, 40, (int) Parameter.Q_III_P, (long) Parameter.Q_INVERSE_III_P, 31, (int) Parameter.B_III_P, 24, 901, (int) Polynomial.SIGNATURE_III_P, 180, (int) Parameter.INVERSE_NUMBER_THEORETIC_TRANSFORM_III_P, 15, 34, PolynomialProvablySecure.ZETA_III_P);
    }
}
