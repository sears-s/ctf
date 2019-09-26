package org.bouncycastle.crypto.engines;

import android.support.v4.view.InputDeviceCompat;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.crypto.tls.CipherSuite;

public final class TwofishEngine implements BlockCipher {
    private static final int BLOCK_SIZE = 16;
    private static final int GF256_FDBK = 361;
    private static final int GF256_FDBK_2 = 180;
    private static final int GF256_FDBK_4 = 90;
    private static final int INPUT_WHITEN = 0;
    private static final int MAX_KEY_BITS = 256;
    private static final int MAX_ROUNDS = 16;
    private static final int OUTPUT_WHITEN = 4;
    private static final byte[][] P = {new byte[]{-87, 103, -77, -24, 4, -3, -93, 118, -102, -110, Byte.MIN_VALUE, 120, -28, -35, -47, 56, 13, -58, 53, -104, 24, -9, -20, 108, 67, 117, 55, 38, -6, 19, -108, 72, -14, -48, -117, 48, -124, 84, -33, 35, 25, 91, 61, 89, -13, -82, -94, -126, 99, 1, -125, 46, -39, 81, -101, 124, -90, -21, -91, -66, 22, 12, -29, 97, -64, -116, 58, -11, 115, 44, 37, 11, -69, 78, -119, 107, 83, 106, -76, -15, -31, -26, -67, 69, -30, -12, -74, 102, -52, -107, 3, 86, -44, 28, 30, -41, -5, -61, -114, -75, -23, -49, -65, -70, -22, 119, 57, -81, 51, -55, 98, 113, -127, 121, 9, -83, 36, -51, -7, -40, -27, -59, -71, 77, 68, 8, -122, -25, -95, 29, -86, -19, 6, 112, -78, -46, 65, 123, -96, 17, 49, -62, 39, -112, 32, -10, 96, -1, -106, 92, -79, -85, -98, -100, 82, 27, 95, -109, 10, -17, -111, -123, 73, -18, 45, 79, -113, 59, 71, -121, 109, 70, -42, 62, 105, 100, 42, -50, -53, 47, -4, -105, 5, 122, -84, Byte.MAX_VALUE, -43, 26, 75, 14, -89, 90, 40, 20, 63, 41, -120, 60, 76, 2, -72, -38, -80, 23, 85, 31, -118, 125, 87, -57, -115, 116, -73, -60, -97, 114, 126, 21, 34, 18, 88, 7, -103, 52, 110, 80, -34, 104, 101, PSSSigner.TRAILER_IMPLICIT, -37, -8, -56, -88, 43, 64, -36, -2, 50, -92, -54, Tnaf.POW_2_WIDTH, 33, -16, -45, 93, 15, 0, 111, -99, 54, 66, 74, 94, -63, -32}, new byte[]{117, -13, -58, -12, -37, 123, -5, -56, 74, -45, -26, 107, 69, 125, -24, 75, -42, 50, -40, -3, 55, 113, -15, -31, 48, 15, -8, 27, -121, -6, 6, 63, 94, -70, -82, 91, -118, 0, PSSSigner.TRAILER_IMPLICIT, -99, 109, -63, -79, 14, Byte.MIN_VALUE, 93, -46, -43, -96, -124, 7, 20, -75, -112, 44, -93, -78, 115, 76, 84, -110, 116, 54, 81, 56, -80, -67, 90, -4, 96, 98, -106, 108, 66, -9, Tnaf.POW_2_WIDTH, 124, 40, 39, -116, 19, -107, -100, -57, 36, 70, 59, 112, -54, -29, -123, -53, 17, -48, -109, -72, -90, -125, 32, -1, -97, 119, -61, -52, 3, 111, 8, -65, 64, -25, 43, -30, 121, 12, -86, -126, 65, 58, -22, -71, -28, -102, -92, -105, 126, -38, 122, 23, 102, -108, -95, 29, 61, -16, -34, -77, 11, 114, -89, 28, -17, -47, 83, 62, -113, 51, 38, 95, -20, 118, 42, 73, -127, -120, -18, 33, -60, 26, -21, -39, -59, 57, -103, -51, -83, 49, -117, 1, 24, 35, -35, 31, 78, 45, -7, 72, 79, -14, 101, -114, 120, 92, 88, 25, -115, -27, -104, 87, 103, Byte.MAX_VALUE, 5, 100, -81, 99, -74, -2, -11, -73, 60, -91, -50, -23, 104, 68, -32, 77, 67, 105, 41, 46, -84, 21, 89, -88, 10, -98, 110, 71, -33, 52, 53, 106, -49, -36, 34, -55, -64, -101, -119, -44, -19, -85, 18, -94, 13, 82, -69, 2, 47, -87, -41, 97, 30, -76, 80, 4, -10, -62, 22, 37, -122, 86, 85, 9, -66, -111}};
    private static final int P_00 = 1;
    private static final int P_01 = 0;
    private static final int P_02 = 0;
    private static final int P_03 = 1;
    private static final int P_04 = 1;
    private static final int P_10 = 0;
    private static final int P_11 = 0;
    private static final int P_12 = 1;
    private static final int P_13 = 1;
    private static final int P_14 = 0;
    private static final int P_20 = 1;
    private static final int P_21 = 1;
    private static final int P_22 = 0;
    private static final int P_23 = 0;
    private static final int P_24 = 0;
    private static final int P_30 = 0;
    private static final int P_31 = 1;
    private static final int P_32 = 1;
    private static final int P_33 = 0;
    private static final int P_34 = 1;
    private static final int ROUNDS = 16;
    private static final int ROUND_SUBKEYS = 8;
    private static final int RS_GF_FDBK = 333;
    private static final int SK_BUMP = 16843009;
    private static final int SK_ROTL = 9;
    private static final int SK_STEP = 33686018;
    private static final int TOTAL_SUBKEYS = 40;
    private boolean encrypting = false;
    private int[] gMDS0 = new int[256];
    private int[] gMDS1 = new int[256];
    private int[] gMDS2 = new int[256];
    private int[] gMDS3 = new int[256];
    private int[] gSBox;
    private int[] gSubKeys;
    private int k64Cnt = 0;
    private byte[] workingKey = null;

    public TwofishEngine() {
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        int[] iArr3 = new int[2];
        for (int i = 0; i < 256; i++) {
            int i2 = P[0][i] & 255;
            iArr[0] = i2;
            iArr2[0] = Mx_X(i2) & 255;
            iArr3[0] = Mx_Y(i2) & 255;
            int i3 = P[1][i] & 255;
            iArr[1] = i3;
            iArr2[1] = Mx_X(i3) & 255;
            iArr3[1] = Mx_Y(i3) & 255;
            this.gMDS0[i] = iArr[1] | (iArr2[1] << 8) | (iArr3[1] << 16) | (iArr3[1] << 24);
            this.gMDS1[i] = iArr3[0] | (iArr3[0] << 8) | (iArr2[0] << 16) | (iArr[0] << 24);
            this.gMDS2[i] = (iArr3[1] << 24) | iArr2[1] | (iArr3[1] << 8) | (iArr[1] << 16);
            this.gMDS3[i] = iArr2[0] | (iArr[0] << 8) | (iArr3[0] << 16) | (iArr2[0] << 24);
        }
    }

    private void Bits32ToBytes(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) i;
        bArr[i2 + 1] = (byte) (i >> 8);
        bArr[i2 + 2] = (byte) (i >> 16);
        bArr[i2 + 3] = (byte) (i >> 24);
    }

    private int BytesTo32Bits(byte[] bArr, int i) {
        return ((bArr[i + 3] & 255) << 24) | (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << Tnaf.POW_2_WIDTH);
    }

    private int F32(int i, int[] iArr) {
        int i2;
        int i3;
        int b0 = b0(i);
        int b1 = b1(i);
        int b2 = b2(i);
        int b3 = b3(i);
        int i4 = iArr[0];
        int i5 = iArr[1];
        int i6 = iArr[2];
        int i7 = iArr[3];
        int i8 = this.k64Cnt & 3;
        if (i8 == 0) {
            b0 = (P[1][b0] & 255) ^ b0(i7);
            b1 = (P[0][b1] & 255) ^ b1(i7);
            b2 = (P[0][b2] & 255) ^ b2(i7);
            b3 = (P[1][b3] & 255) ^ b3(i7);
        } else if (i8 != 1) {
            if (i8 != 2) {
                if (i8 != 3) {
                    return 0;
                }
            }
            int[] iArr2 = this.gMDS0;
            byte[][] bArr = P;
            int i9 = iArr2[(bArr[0][(bArr[0][b0] & 255) ^ b0(i5)] & 255) ^ b0(i4)];
            int[] iArr3 = this.gMDS1;
            byte[][] bArr2 = P;
            int i10 = i9 ^ iArr3[(bArr2[0][(bArr2[1][b1] & 255) ^ b1(i5)] & 255) ^ b1(i4)];
            int[] iArr4 = this.gMDS2;
            byte[][] bArr3 = P;
            i2 = i10 ^ iArr4[(bArr3[1][(bArr3[0][b2] & 255) ^ b2(i5)] & 255) ^ b2(i4)];
            int[] iArr5 = this.gMDS3;
            byte[][] bArr4 = P;
            i3 = iArr5[(bArr4[1][(bArr4[1][b3] & 255) ^ b3(i5)] & 255) ^ b3(i4)];
            return i2 ^ i3;
        } else {
            i2 = (this.gMDS0[(P[0][b0] & 255) ^ b0(i4)] ^ this.gMDS1[(P[0][b1] & 255) ^ b1(i4)]) ^ this.gMDS2[(P[1][b2] & 255) ^ b2(i4)];
            i3 = this.gMDS3[(P[1][b3] & 255) ^ b3(i4)];
            return i2 ^ i3;
        }
        b0 = b0(i6) ^ (P[1][b0] & 255);
        b1 = b1(i6) ^ (P[1][b1] & 255);
        b2 = b2(i6) ^ (P[0][b2] & 255);
        b3 = (P[0][b3] & 255) ^ b3(i6);
        int[] iArr22 = this.gMDS0;
        byte[][] bArr5 = P;
        int i92 = iArr22[(bArr5[0][(bArr5[0][b0] & 255) ^ b0(i5)] & 255) ^ b0(i4)];
        int[] iArr32 = this.gMDS1;
        byte[][] bArr22 = P;
        int i102 = i92 ^ iArr32[(bArr22[0][(bArr22[1][b1] & 255) ^ b1(i5)] & 255) ^ b1(i4)];
        int[] iArr42 = this.gMDS2;
        byte[][] bArr32 = P;
        i2 = i102 ^ iArr42[(bArr32[1][(bArr32[0][b2] & 255) ^ b2(i5)] & 255) ^ b2(i4)];
        int[] iArr52 = this.gMDS3;
        byte[][] bArr42 = P;
        i3 = iArr52[(bArr42[1][(bArr42[1][b3] & 255) ^ b3(i5)] & 255) ^ b3(i4)];
        return i2 ^ i3;
    }

    private int Fe32_0(int i) {
        int[] iArr = this.gSBox;
        return iArr[(((i >>> 24) & 255) * 2) + InputDeviceCompat.SOURCE_DPAD] ^ ((iArr[((i & 255) * 2) + 0] ^ iArr[(((i >>> 8) & 255) * 2) + 1]) ^ iArr[(((i >>> 16) & 255) * 2) + 512]);
    }

    private int Fe32_3(int i) {
        int[] iArr = this.gSBox;
        return iArr[(((i >>> 16) & 255) * 2) + InputDeviceCompat.SOURCE_DPAD] ^ ((iArr[(((i >>> 24) & 255) * 2) + 0] ^ iArr[((i & 255) * 2) + 1]) ^ iArr[(((i >>> 8) & 255) * 2) + 512]);
    }

    private int LFSR1(int i) {
        return ((i & 1) != 0 ? 180 : 0) ^ (i >> 1);
    }

    private int LFSR2(int i) {
        int i2 = 0;
        int i3 = (i >> 2) ^ ((i & 2) != 0 ? 180 : 0);
        if ((i & 1) != 0) {
            i2 = 90;
        }
        return i3 ^ i2;
    }

    private int Mx_X(int i) {
        return i ^ LFSR2(i);
    }

    private int Mx_Y(int i) {
        return LFSR2(i) ^ (LFSR1(i) ^ i);
    }

    private int RS_MDS_Encode(int i, int i2) {
        int i3 = i2;
        for (int i4 = 0; i4 < 4; i4++) {
            i3 = RS_rem(i3);
        }
        int i5 = i ^ i3;
        for (int i6 = 0; i6 < 4; i6++) {
            i5 = RS_rem(i5);
        }
        return i5;
    }

    private int RS_rem(int i) {
        int i2 = (i >>> 24) & 255;
        int i3 = 0;
        int i4 = ((i2 << 1) ^ ((i2 & 128) != 0 ? RS_GF_FDBK : 0)) & 255;
        int i5 = i2 >>> 1;
        if ((i2 & 1) != 0) {
            i3 = CipherSuite.TLS_DH_anon_WITH_AES_128_GCM_SHA256;
        }
        int i6 = (i5 ^ i3) ^ i4;
        return ((((i << 8) ^ (i6 << 24)) ^ (i4 << 16)) ^ (i6 << 8)) ^ i2;
    }

    private int b0(int i) {
        return i & 255;
    }

    private int b1(int i) {
        return (i >>> 8) & 255;
    }

    private int b2(int i) {
        return (i >>> 16) & 255;
    }

    private int b3(int i) {
        return (i >>> 24) & 255;
    }

    private void decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int BytesTo32Bits = BytesTo32Bits(bArr, i + 8) ^ this.gSubKeys[6];
        int i3 = 39;
        int BytesTo32Bits2 = BytesTo32Bits(bArr, i + 4) ^ this.gSubKeys[5];
        int BytesTo32Bits3 = BytesTo32Bits(bArr, i) ^ this.gSubKeys[4];
        int BytesTo32Bits4 = BytesTo32Bits(bArr, i + 12) ^ this.gSubKeys[7];
        for (int i4 = 0; i4 < 16; i4 += 2) {
            int Fe32_0 = Fe32_0(BytesTo32Bits3);
            int Fe32_3 = Fe32_3(BytesTo32Bits2);
            int i5 = (Fe32_3 * 2) + Fe32_0;
            int[] iArr = this.gSubKeys;
            int i6 = i3 - 1;
            int i7 = BytesTo32Bits4 ^ (i5 + iArr[i3]);
            int i8 = (BytesTo32Bits >>> 31) | (BytesTo32Bits << 1);
            int i9 = i6 - 1;
            BytesTo32Bits = i8 ^ ((Fe32_0 + Fe32_3) + iArr[i6]);
            BytesTo32Bits4 = (i7 << 31) | (i7 >>> 1);
            int Fe32_02 = Fe32_0(BytesTo32Bits);
            int Fe32_32 = Fe32_3(BytesTo32Bits4);
            int i10 = (Fe32_32 * 2) + Fe32_02;
            int[] iArr2 = this.gSubKeys;
            int i11 = i9 - 1;
            int i12 = BytesTo32Bits2 ^ (i10 + iArr2[i9]);
            int i13 = (BytesTo32Bits3 >>> 31) | (BytesTo32Bits3 << 1);
            i3 = i11 - 1;
            BytesTo32Bits3 = i13 ^ ((Fe32_02 + Fe32_32) + iArr2[i11]);
            BytesTo32Bits2 = (i12 << 31) | (i12 >>> 1);
        }
        Bits32ToBytes(this.gSubKeys[0] ^ BytesTo32Bits, bArr2, i2);
        Bits32ToBytes(this.gSubKeys[1] ^ BytesTo32Bits4, bArr2, i2 + 4);
        Bits32ToBytes(this.gSubKeys[2] ^ BytesTo32Bits3, bArr2, i2 + 8);
        Bits32ToBytes(this.gSubKeys[3] ^ BytesTo32Bits2, bArr2, i2 + 12);
    }

    private void encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int i3 = 0;
        int BytesTo32Bits = BytesTo32Bits(bArr, i) ^ this.gSubKeys[0];
        int BytesTo32Bits2 = BytesTo32Bits(bArr, i + 4) ^ this.gSubKeys[1];
        int BytesTo32Bits3 = BytesTo32Bits(bArr, i + 8) ^ this.gSubKeys[2];
        int BytesTo32Bits4 = BytesTo32Bits(bArr, i + 12) ^ this.gSubKeys[3];
        int i4 = 8;
        while (i3 < 16) {
            int Fe32_0 = Fe32_0(BytesTo32Bits);
            int Fe32_3 = Fe32_3(BytesTo32Bits2);
            int i5 = Fe32_0 + Fe32_3;
            int[] iArr = this.gSubKeys;
            int i6 = i4 + 1;
            int i7 = BytesTo32Bits3 ^ (i5 + iArr[i4]);
            BytesTo32Bits3 = (i7 >>> 1) | (i7 << 31);
            int i8 = (BytesTo32Bits4 >>> 31) | (BytesTo32Bits4 << 1);
            int i9 = i6 + 1;
            BytesTo32Bits4 = i8 ^ ((Fe32_0 + (Fe32_3 * 2)) + iArr[i6]);
            int Fe32_02 = Fe32_0(BytesTo32Bits3);
            int Fe32_32 = Fe32_3(BytesTo32Bits4);
            int i10 = Fe32_02 + Fe32_32;
            int[] iArr2 = this.gSubKeys;
            int i11 = i9 + 1;
            int i12 = BytesTo32Bits ^ (i10 + iArr2[i9]);
            BytesTo32Bits = (i12 >>> 1) | (i12 << 31);
            int i13 = (BytesTo32Bits2 << 1) | (BytesTo32Bits2 >>> 31);
            i3 += 2;
            int i14 = i11 + 1;
            BytesTo32Bits2 = i13 ^ ((Fe32_02 + (Fe32_32 * 2)) + iArr2[i11]);
            i4 = i14;
        }
        Bits32ToBytes(this.gSubKeys[4] ^ BytesTo32Bits3, bArr2, i2);
        Bits32ToBytes(BytesTo32Bits4 ^ this.gSubKeys[5], bArr2, i2 + 4);
        Bits32ToBytes(this.gSubKeys[6] ^ BytesTo32Bits, bArr2, i2 + 8);
        Bits32ToBytes(this.gSubKeys[7] ^ BytesTo32Bits2, bArr2, i2 + 12);
    }

    /* JADX WARNING: type inference failed for: r13v0 */
    /* JADX WARNING: type inference failed for: r12v0 */
    /* JADX WARNING: type inference failed for: r11v0 */
    /* JADX WARNING: type inference failed for: r10v3 */
    /* JADX WARNING: type inference failed for: r13v2 */
    /* JADX WARNING: type inference failed for: r12v9 */
    /* JADX WARNING: type inference failed for: r11v18 */
    /* JADX WARNING: type inference failed for: r10v14 */
    /* JADX WARNING: type inference failed for: r10v27 */
    /* JADX WARNING: type inference failed for: r11v30 */
    /* JADX WARNING: type inference failed for: r12v25 */
    /* JADX WARNING: type inference failed for: r13v26 */
    /* JADX WARNING: type inference failed for: r11v31 */
    /* JADX WARNING: type inference failed for: r12v26 */
    /* JADX WARNING: type inference failed for: r13v27 */
    /* JADX WARNING: type inference failed for: r10v34 */
    /* JADX WARNING: type inference failed for: r11v33 */
    /* JADX WARNING: type inference failed for: r12v27 */
    /* JADX WARNING: type inference failed for: r11v34 */
    /* JADX WARNING: type inference failed for: r12v28 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setKey(byte[] r19) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = 4
            int[] r3 = new int[r2]
            int[] r4 = new int[r2]
            int[] r5 = new int[r2]
            r6 = 40
            int[] r6 = new int[r6]
            r0.gSubKeys = r6
            int r6 = r0.k64Cnt
            r7 = 1
            if (r6 < r7) goto L_0x01f9
            if (r6 > r2) goto L_0x01f1
            r6 = 0
            r8 = r6
        L_0x001a:
            int r9 = r0.k64Cnt
            if (r8 >= r9) goto L_0x003e
            int r9 = r8 * 8
            int r10 = r0.BytesTo32Bits(r1, r9)
            r3[r8] = r10
            int r9 = r9 + r2
            int r9 = r0.BytesTo32Bits(r1, r9)
            r4[r8] = r9
            int r9 = r0.k64Cnt
            int r9 = r9 - r7
            int r9 = r9 - r8
            r10 = r3[r8]
            r11 = r4[r8]
            int r10 = r0.RS_MDS_Encode(r10, r11)
            r5[r9] = r10
            int r8 = r8 + 1
            goto L_0x001a
        L_0x003e:
            r1 = r6
        L_0x003f:
            r2 = 20
            if (r1 >= r2) goto L_0x006b
            r2 = 33686018(0x2020202, float:9.551468E-38)
            int r2 = r2 * r1
            int r8 = r0.F32(r2, r3)
            r9 = 16843009(0x1010101, float:2.3694278E-38)
            int r2 = r2 + r9
            int r2 = r0.F32(r2, r4)
            int r9 = r2 << 8
            int r2 = r2 >>> 24
            r2 = r2 | r9
            int r8 = r8 + r2
            int[] r9 = r0.gSubKeys
            int r10 = r1 * 2
            r9[r10] = r8
            int r8 = r8 + r2
            int r10 = r10 + r7
            int r2 = r8 << 9
            int r8 = r8 >>> 23
            r2 = r2 | r8
            r9[r10] = r2
            int r1 = r1 + 1
            goto L_0x003f
        L_0x006b:
            r1 = r5[r6]
            r2 = r5[r7]
            r3 = 2
            r4 = r5[r3]
            r8 = 3
            r5 = r5[r8]
            r9 = 1024(0x400, float:1.435E-42)
            int[] r9 = new int[r9]
            r0.gSBox = r9
            r9 = r6
        L_0x007c:
            r10 = 256(0x100, float:3.59E-43)
            if (r9 >= r10) goto L_0x01f0
            int r10 = r0.k64Cnt
            r10 = r10 & r8
            if (r10 == 0) goto L_0x00f7
            if (r10 == r7) goto L_0x0099
            if (r10 == r3) goto L_0x0093
            if (r10 == r8) goto L_0x008d
            goto L_0x01eb
        L_0x008d:
            r10 = r9
            r11 = r10
            r12 = r11
            r13 = r12
            goto L_0x012b
        L_0x0093:
            r10 = r9
            r11 = r10
            r12 = r11
            r13 = r12
            goto L_0x015f
        L_0x0099:
            int[] r10 = r0.gSBox
            int r11 = r9 * 2
            int[] r12 = r0.gMDS0
            byte[][] r13 = P
            r13 = r13[r6]
            byte r13 = r13[r9]
            r13 = r13 & 255(0xff, float:3.57E-43)
            int r14 = r0.b0(r1)
            r13 = r13 ^ r14
            r12 = r12[r13]
            r10[r11] = r12
            int[] r10 = r0.gSBox
            int r12 = r11 + 1
            int[] r13 = r0.gMDS1
            byte[][] r14 = P
            r14 = r14[r6]
            byte r14 = r14[r9]
            r14 = r14 & 255(0xff, float:3.57E-43)
            int r15 = r0.b1(r1)
            r14 = r14 ^ r15
            r13 = r13[r14]
            r10[r12] = r13
            int[] r10 = r0.gSBox
            int r12 = r11 + 512
            int[] r13 = r0.gMDS2
            byte[][] r14 = P
            r14 = r14[r7]
            byte r14 = r14[r9]
            r14 = r14 & 255(0xff, float:3.57E-43)
            int r15 = r0.b2(r1)
            r14 = r14 ^ r15
            r13 = r13[r14]
            r10[r12] = r13
            int[] r10 = r0.gSBox
            int r11 = r11 + 513
            int[] r12 = r0.gMDS3
            byte[][] r13 = P
            r13 = r13[r7]
            byte r13 = r13[r9]
            r13 = r13 & 255(0xff, float:3.57E-43)
            int r14 = r0.b3(r1)
            r13 = r13 ^ r14
            r12 = r12[r13]
            r10[r11] = r12
            goto L_0x01eb
        L_0x00f7:
            byte[][] r10 = P
            r10 = r10[r7]
            byte r10 = r10[r9]
            r10 = r10 & 255(0xff, float:3.57E-43)
            int r11 = r0.b0(r5)
            r10 = r10 ^ r11
            byte[][] r11 = P
            r11 = r11[r6]
            byte r11 = r11[r9]
            r11 = r11 & 255(0xff, float:3.57E-43)
            int r12 = r0.b1(r5)
            r11 = r11 ^ r12
            byte[][] r12 = P
            r12 = r12[r6]
            byte r12 = r12[r9]
            r12 = r12 & 255(0xff, float:3.57E-43)
            int r13 = r0.b2(r5)
            r12 = r12 ^ r13
            byte[][] r13 = P
            r13 = r13[r7]
            byte r13 = r13[r9]
            r13 = r13 & 255(0xff, float:3.57E-43)
            int r14 = r0.b3(r5)
            r13 = r13 ^ r14
        L_0x012b:
            byte[][] r14 = P
            r14 = r14[r7]
            byte r10 = r14[r10]
            r10 = r10 & 255(0xff, float:3.57E-43)
            int r14 = r0.b0(r4)
            r10 = r10 ^ r14
            byte[][] r14 = P
            r14 = r14[r7]
            byte r11 = r14[r11]
            r11 = r11 & 255(0xff, float:3.57E-43)
            int r14 = r0.b1(r4)
            r11 = r11 ^ r14
            byte[][] r14 = P
            r14 = r14[r6]
            byte r12 = r14[r12]
            r12 = r12 & 255(0xff, float:3.57E-43)
            int r14 = r0.b2(r4)
            r12 = r12 ^ r14
            byte[][] r14 = P
            r14 = r14[r6]
            byte r13 = r14[r13]
            r13 = r13 & 255(0xff, float:3.57E-43)
            int r14 = r0.b3(r4)
            r13 = r13 ^ r14
        L_0x015f:
            int[] r14 = r0.gSBox
            int r15 = r9 * 2
            int[] r3 = r0.gMDS0
            byte[][] r16 = P
            r17 = r16[r6]
            r16 = r16[r6]
            byte r10 = r16[r10]
            r10 = r10 & 255(0xff, float:3.57E-43)
            int r16 = r0.b0(r2)
            r10 = r10 ^ r16
            byte r10 = r17[r10]
            r10 = r10 & 255(0xff, float:3.57E-43)
            int r16 = r0.b0(r1)
            r10 = r10 ^ r16
            r3 = r3[r10]
            r14[r15] = r3
            int[] r3 = r0.gSBox
            int r10 = r15 + 1
            int[] r14 = r0.gMDS1
            byte[][] r16 = P
            r17 = r16[r6]
            r16 = r16[r7]
            byte r11 = r16[r11]
            r11 = r11 & 255(0xff, float:3.57E-43)
            int r16 = r0.b1(r2)
            r11 = r11 ^ r16
            byte r11 = r17[r11]
            r11 = r11 & 255(0xff, float:3.57E-43)
            int r16 = r0.b1(r1)
            r11 = r11 ^ r16
            r11 = r14[r11]
            r3[r10] = r11
            int[] r3 = r0.gSBox
            int r10 = r15 + 512
            int[] r11 = r0.gMDS2
            byte[][] r14 = P
            r16 = r14[r7]
            r14 = r14[r6]
            byte r12 = r14[r12]
            r12 = r12 & 255(0xff, float:3.57E-43)
            int r14 = r0.b2(r2)
            r12 = r12 ^ r14
            byte r12 = r16[r12]
            r12 = r12 & 255(0xff, float:3.57E-43)
            int r14 = r0.b2(r1)
            r12 = r12 ^ r14
            r11 = r11[r12]
            r3[r10] = r11
            int[] r3 = r0.gSBox
            int r15 = r15 + 513
            int[] r10 = r0.gMDS3
            byte[][] r11 = P
            r12 = r11[r7]
            r11 = r11[r7]
            byte r11 = r11[r13]
            r11 = r11 & 255(0xff, float:3.57E-43)
            int r13 = r0.b3(r2)
            r11 = r11 ^ r13
            byte r11 = r12[r11]
            r11 = r11 & 255(0xff, float:3.57E-43)
            int r12 = r0.b3(r1)
            r11 = r11 ^ r12
            r10 = r10[r11]
            r3[r15] = r10
        L_0x01eb:
            int r9 = r9 + 1
            r3 = 2
            goto L_0x007c
        L_0x01f0:
            return
        L_0x01f1:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.String r2 = "Key size larger than 256 bits"
            r1.<init>(r2)
            throw r1
        L_0x01f9:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.String r2 = "Key size less than 64 bits"
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.crypto.engines.TwofishEngine.setKey(byte[]):void");
    }

    public String getAlgorithmName() {
        return "Twofish";
    }

    public int getBlockSize() {
        return 16;
    }

    public void init(boolean z, CipherParameters cipherParameters) {
        if (cipherParameters instanceof KeyParameter) {
            this.encrypting = z;
            this.workingKey = ((KeyParameter) cipherParameters).getKey();
            byte[] bArr = this.workingKey;
            this.k64Cnt = bArr.length / 8;
            setKey(bArr);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("invalid parameter passed to Twofish init - ");
        sb.append(cipherParameters.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }

    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (this.workingKey == null) {
            throw new IllegalStateException("Twofish not initialised");
        } else if (i + 16 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        } else if (i2 + 16 <= bArr2.length) {
            if (this.encrypting) {
                encryptBlock(bArr, i, bArr2, i2);
            } else {
                decryptBlock(bArr, i, bArr2, i2);
            }
            return 16;
        } else {
            throw new OutputLengthException("output buffer too short");
        }
    }

    public void reset() {
        byte[] bArr = this.workingKey;
        if (bArr != null) {
            setKey(bArr);
        }
    }
}
