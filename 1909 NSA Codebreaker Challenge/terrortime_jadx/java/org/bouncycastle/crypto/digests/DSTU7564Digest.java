package org.bouncycastle.crypto.digests;

import org.bouncycastle.asn1.cmc.BodyPartID;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class DSTU7564Digest implements ExtendedDigest, Memoable {
    private static final int NB_1024 = 16;
    private static final int NB_512 = 8;
    private static final int NR_1024 = 14;
    private static final int NR_512 = 10;
    private static final byte[] S0 = {-88, 67, 95, 6, 107, 117, 108, 89, 113, -33, -121, -107, 23, -16, -40, 9, 109, -13, 29, -53, -55, 77, 44, -81, 121, -32, -105, -3, 111, 75, 69, 57, 62, -35, -93, 79, -76, -74, -102, 14, 31, -65, 21, -31, 73, -46, -109, -58, -110, 114, -98, 97, -47, 99, -6, -18, -12, 25, -43, -83, 88, -92, -69, -95, -36, -14, -125, 55, 66, -28, 122, 50, -100, -52, -85, 74, -113, 110, 4, 39, 46, -25, -30, 90, -106, 22, 35, 43, -62, 101, 102, 15, PSSSigner.TRAILER_IMPLICIT, -87, 71, 65, 52, 72, -4, -73, 106, -120, -91, 83, -122, -7, 91, -37, 56, 123, -61, 30, 34, 51, 36, 40, 54, -57, -78, 59, -114, 119, -70, -11, 20, -97, 8, 85, -101, 76, -2, 96, 92, -38, 24, 70, -51, 125, 33, -80, 63, 27, -119, -1, -21, -124, 105, 58, -99, -41, -45, 112, 103, 64, -75, -34, 93, 48, -111, -79, 120, 17, 1, -27, 0, 104, -104, -96, -59, 2, -90, 116, 45, 11, -94, 118, -77, -66, -50, -67, -82, -23, -118, 49, 28, -20, -15, -103, -108, -86, -10, 38, 47, -17, -24, -116, 53, 3, -44, Byte.MAX_VALUE, -5, 5, -63, 94, -112, 32, 61, -126, -9, -22, 10, 13, 126, -8, 80, 26, -60, 7, 87, -72, 60, 98, -29, -56, -84, 82, 100, Tnaf.POW_2_WIDTH, -48, -39, 19, 12, 18, 41, 81, -71, -49, -42, 115, -115, -127, 84, -64, -19, 78, 68, -89, 42, -123, 37, -26, -54, 124, -117, 86, Byte.MIN_VALUE};
    private static final byte[] S1 = {-50, -69, -21, -110, -22, -53, 19, -63, -23, 58, -42, -78, -46, -112, 23, -8, 66, 21, 86, -76, 101, 28, -120, 67, -59, 92, 54, -70, -11, 87, 103, -115, 49, -10, 100, 88, -98, -12, 34, -86, 117, 15, 2, -79, -33, 109, 115, 77, 124, 38, 46, -9, 8, 93, 68, 62, -97, 20, -56, -82, 84, Tnaf.POW_2_WIDTH, -40, PSSSigner.TRAILER_IMPLICIT, 26, 107, 105, -13, -67, 51, -85, -6, -47, -101, 104, 78, 22, -107, -111, -18, 76, 99, -114, 91, -52, 60, 25, -95, -127, 73, 123, -39, 111, 55, 96, -54, -25, 43, 72, -3, -106, 69, -4, 65, 18, 13, 121, -27, -119, -116, -29, 32, 48, -36, -73, 108, 74, -75, 63, -105, -44, 98, 45, 6, -92, -91, -125, 95, 42, -38, -55, 0, 126, -94, 85, -65, 17, -43, -100, -49, 14, 10, 61, 81, 125, -109, 27, -2, -60, 71, 9, -122, 11, -113, -99, 106, 7, -71, -80, -104, 24, 50, 113, 75, -17, 59, 112, -96, -28, 64, -1, -61, -87, -26, 120, -7, -117, 70, Byte.MIN_VALUE, 30, 56, -31, -72, -88, -32, 12, 35, 118, 29, 37, 36, 5, -15, 110, -108, 40, -102, -124, -24, -93, 79, 119, -45, -123, -30, 82, -14, -126, 80, 122, 47, 116, 83, -77, 97, -81, 57, 53, -34, -51, 31, -103, -84, -83, 114, 44, -35, -48, -121, -66, 94, -90, -20, 4, -58, 3, 52, -5, -37, 89, -74, -62, 1, -16, 90, -19, -89, 102, 33, Byte.MAX_VALUE, -118, 39, -57, -64, 41, -41};
    private static final byte[] S2 = {-109, -39, -102, -75, -104, 34, 69, -4, -70, 106, -33, 2, -97, -36, 81, 89, 74, 23, 43, -62, -108, -12, -69, -93, 98, -28, 113, -44, -51, 112, 22, -31, 73, 60, -64, -40, 92, -101, -83, -123, 83, -95, 122, -56, 45, -32, -47, 114, -90, 44, -60, -29, 118, 120, -73, -76, 9, 59, 14, 65, 76, -34, -78, -112, 37, -91, -41, 3, 17, 0, -61, 46, -110, -17, 78, 18, -99, 125, -53, 53, Tnaf.POW_2_WIDTH, -43, 79, -98, 77, -87, 85, -58, -48, 123, 24, -105, -45, 54, -26, 72, 86, -127, -113, 119, -52, -100, -71, -30, -84, -72, 47, 21, -92, 124, -38, 56, 30, 11, 5, -42, 20, 110, 108, 126, 102, -3, -79, -27, 96, -81, 94, 51, -121, -55, -16, 93, 109, 63, -120, -115, -57, -9, 29, -23, -20, -19, Byte.MIN_VALUE, 41, 39, -49, -103, -88, 80, 15, 55, 36, 40, 48, -107, -46, 62, 91, 64, -125, -77, 105, 87, 31, 7, 28, -118, PSSSigner.TRAILER_IMPLICIT, 32, -21, -50, -114, -85, -18, 49, -94, 115, -7, -54, 58, 26, -5, 13, -63, -2, -6, -14, 111, -67, -106, -35, 67, 82, -74, 8, -13, -82, -66, 25, -119, 50, 38, -80, -22, 75, 100, -124, -126, 107, -11, 121, -65, 1, 95, 117, 99, 27, 35, 61, 104, 42, 101, -24, -111, -10, -1, 19, 88, -15, 71, 10, Byte.MAX_VALUE, -59, -89, -25, 97, 90, 6, 70, 68, 66, 4, -96, -37, 57, -122, 84, -86, -116, 52, 33, -117, -8, 12, 116, 103};
    private static final byte[] S3 = {104, -115, -54, 77, 115, 75, 78, 42, -44, 82, 38, -77, 84, 30, 25, 31, 34, 3, 70, 61, 45, 74, 83, -125, 19, -118, -73, -43, 37, 121, -11, -67, 88, 47, 13, 2, -19, 81, -98, 17, -14, 62, 85, 94, -47, 22, 60, 102, 112, 93, -13, 69, 64, -52, -24, -108, 86, 8, -50, 26, 58, -46, -31, -33, -75, 56, 110, 14, -27, -12, -7, -122, -23, 79, -42, -123, 35, -49, 50, -103, 49, 20, -82, -18, -56, 72, -45, 48, -95, -110, 65, -79, 24, -60, 44, 113, 114, 68, 21, -3, 55, -66, 95, -86, -101, -120, -40, -85, -119, -100, -6, 96, -22, PSSSigner.TRAILER_IMPLICIT, 98, 12, 36, -90, -88, -20, 103, 32, -37, 124, 40, -35, -84, 91, 52, 126, Tnaf.POW_2_WIDTH, -15, 123, -113, 99, -96, 5, -102, 67, 119, 33, -65, 39, 9, -61, -97, -74, -41, 41, -62, -21, -64, -92, -117, -116, 29, -5, -1, -63, -78, -105, 46, -8, 101, -10, 117, 7, 4, 73, 51, -28, -39, -71, -48, 66, -57, 108, -112, 0, -114, 111, 80, 1, -59, -38, 71, 63, -51, 105, -94, -30, 122, -89, -58, -109, 15, 10, 6, -26, 43, -106, -93, 28, -81, 106, 18, -124, 57, -25, -80, -126, -9, -2, -99, -121, 92, -127, 53, -34, -76, -91, -4, Byte.MIN_VALUE, -17, -53, -69, 107, 118, -70, 90, 125, 120, 11, -107, -29, -83, 116, -104, 59, 54, 100, 109, -36, -16, 89, -87, 76, 23, Byte.MAX_VALUE, -111, -72, -55, 87, 27, -32, 97};
    private int blockSize;
    private byte[] buf;
    private int bufOff;
    private int columns;
    private int hashSize;
    private long inputBlocks;
    private int rounds;
    private long[] state;
    private long[] tempState1;
    private long[] tempState2;

    public DSTU7564Digest(int i) {
        int i2;
        if (i == 256 || i == 384 || i == 512) {
            this.hashSize = i >>> 3;
            if (i > 256) {
                this.columns = 16;
                i2 = 14;
            } else {
                this.columns = 8;
                i2 = 10;
            }
            this.rounds = i2;
            int i3 = this.columns;
            this.blockSize = i3 << 3;
            this.state = new long[i3];
            long[] jArr = this.state;
            int i4 = this.blockSize;
            jArr[0] = (long) i4;
            this.tempState1 = new long[i3];
            this.tempState2 = new long[i3];
            this.buf = new byte[i4];
            return;
        }
        throw new IllegalArgumentException("Hash size is not recommended. Use 256/384/512 instead");
    }

    public DSTU7564Digest(DSTU7564Digest dSTU7564Digest) {
        copyIn(dSTU7564Digest);
    }

    private void P(long[] jArr) {
        for (int i = 0; i < this.rounds; i++) {
            long j = (long) i;
            for (int i2 = 0; i2 < this.columns; i2++) {
                jArr[i2] = jArr[i2] ^ j;
                j += 16;
            }
            shiftRows(jArr);
            subBytes(jArr);
            mixColumns(jArr);
        }
    }

    private void Q(long[] jArr) {
        for (int i = 0; i < this.rounds; i++) {
            long j = (((long) (((this.columns - 1) << 4) ^ i)) << 56) | 67818912035696883L;
            for (int i2 = 0; i2 < this.columns; i2++) {
                jArr[i2] = jArr[i2] + j;
                j -= 1152921504606846976L;
            }
            shiftRows(jArr);
            subBytes(jArr);
            mixColumns(jArr);
        }
    }

    private void copyIn(DSTU7564Digest dSTU7564Digest) {
        this.hashSize = dSTU7564Digest.hashSize;
        this.blockSize = dSTU7564Digest.blockSize;
        this.rounds = dSTU7564Digest.rounds;
        int i = this.columns;
        if (i <= 0 || i != dSTU7564Digest.columns) {
            this.columns = dSTU7564Digest.columns;
            this.state = Arrays.clone(dSTU7564Digest.state);
            int i2 = this.columns;
            this.tempState1 = new long[i2];
            this.tempState2 = new long[i2];
            this.buf = Arrays.clone(dSTU7564Digest.buf);
        } else {
            System.arraycopy(dSTU7564Digest.state, 0, this.state, 0, i);
            System.arraycopy(dSTU7564Digest.buf, 0, this.buf, 0, this.blockSize);
        }
        this.inputBlocks = dSTU7564Digest.inputBlocks;
        this.bufOff = dSTU7564Digest.bufOff;
    }

    private static long mixColumn(long j) {
        long j2 = ((9187201950435737471L & j) << 1) ^ (((j & -9187201950435737472L) >>> 7) * 29);
        long rotate = rotate(8, j) ^ j;
        long rotate2 = (rotate ^ rotate(16, rotate)) ^ rotate(48, j);
        long j3 = (j ^ rotate2) ^ j2;
        long j4 = ((-9187201950435737472L & j3) >>> 6) * 29;
        return ((rotate(32, (((j3 & 4629771061636907072L) >>> 6) * 29) ^ (j4 ^ ((4557430888798830399L & j3) << 2))) ^ rotate2) ^ rotate(40, j2)) ^ rotate(48, j2);
    }

    private void mixColumns(long[] jArr) {
        for (int i = 0; i < this.columns; i++) {
            jArr[i] = mixColumn(jArr[i]);
        }
    }

    private void processBlock(byte[] bArr, int i) {
        int i2 = i;
        for (int i3 = 0; i3 < this.columns; i3++) {
            long littleEndianToLong = Pack.littleEndianToLong(bArr, i2);
            i2 += 8;
            this.tempState1[i3] = this.state[i3] ^ littleEndianToLong;
            this.tempState2[i3] = littleEndianToLong;
        }
        P(this.tempState1);
        Q(this.tempState2);
        for (int i4 = 0; i4 < this.columns; i4++) {
            long[] jArr = this.state;
            jArr[i4] = jArr[i4] ^ (this.tempState1[i4] ^ this.tempState2[i4]);
        }
    }

    private static long rotate(int i, long j) {
        return (j << (-i)) | (j >>> i);
    }

    private void shiftRows(long[] jArr) {
        int i = this.columns;
        if (i == 8) {
            long j = jArr[0];
            long j2 = jArr[1];
            long j3 = jArr[2];
            long j4 = jArr[3];
            long j5 = jArr[4];
            long j6 = jArr[5];
            long j7 = jArr[6];
            long j8 = jArr[7];
            long j9 = (j ^ j5) & -4294967296L;
            long j10 = j ^ j9;
            long j11 = j5 ^ j9;
            long j12 = (j2 ^ j6) & 72057594021150720L;
            long j13 = j2 ^ j12;
            long j14 = j6 ^ j12;
            long j15 = (j3 ^ j7) & 281474976645120L;
            long j16 = j3 ^ j15;
            long j17 = j7 ^ j15;
            long j18 = (j4 ^ j8) & 1099511627520L;
            long j19 = j4 ^ j18;
            long j20 = j8 ^ j18;
            long j21 = (j10 ^ j16) & -281470681808896L;
            long j22 = j10 ^ j21;
            long j23 = j16 ^ j21;
            long j24 = (j13 ^ j19) & 72056494543077120L;
            long j25 = j13 ^ j24;
            long j26 = j19 ^ j24;
            long j27 = (j11 ^ j17) & -281470681808896L;
            long j28 = j11 ^ j27;
            long j29 = j17 ^ j27;
            long j30 = (j14 ^ j20) & 72056494543077120L;
            long j31 = j14 ^ j30;
            long j32 = j20 ^ j30;
            long j33 = (j22 ^ j25) & -71777214294589696L;
            long j34 = j22 ^ j33;
            long j35 = j25 ^ j33;
            long j36 = (j23 ^ j26) & -71777214294589696L;
            long j37 = j23 ^ j36;
            long j38 = j26 ^ j36;
            long j39 = (j28 ^ j31) & -71777214294589696L;
            long j40 = j28 ^ j39;
            long j41 = j31 ^ j39;
            long j42 = (j29 ^ j32) & -71777214294589696L;
            long j43 = j29 ^ j42;
            long j44 = j32 ^ j42;
            jArr[0] = j34;
            jArr[1] = j35;
            jArr[2] = j37;
            jArr[3] = j38;
            jArr[4] = j40;
            jArr[5] = j41;
            jArr[6] = j43;
            jArr[7] = j44;
        } else if (i == 16) {
            long j45 = jArr[0];
            long j46 = jArr[1];
            long j47 = jArr[2];
            long j48 = jArr[3];
            long j49 = jArr[4];
            long j50 = jArr[5];
            long j51 = jArr[6];
            long j52 = jArr[7];
            long j53 = jArr[8];
            long j54 = jArr[9];
            long j55 = jArr[10];
            long j56 = jArr[11];
            long j57 = jArr[12];
            long j58 = jArr[13];
            long j59 = jArr[14];
            long j60 = jArr[15];
            long j61 = (j45 ^ j53) & -72057594037927936L;
            long j62 = j45 ^ j61;
            long j63 = j53 ^ j61;
            long j64 = (j46 ^ j54) & -72057594037927936L;
            long j65 = j46 ^ j64;
            long j66 = j54 ^ j64;
            long j67 = (j47 ^ j55) & -281474976710656L;
            long j68 = j47 ^ j67;
            long j69 = j55 ^ j67;
            long j70 = (j48 ^ j56) & -1099511627776L;
            long j71 = j48 ^ j70;
            long j72 = j56 ^ j70;
            long j73 = (j49 ^ j57) & -4294967296L;
            long j74 = j49 ^ j73;
            long j75 = j57 ^ j73;
            long j76 = (j50 ^ j58) & 72057594021150720L;
            long j77 = j50 ^ j76;
            long j78 = j58 ^ j76;
            long j79 = (j51 ^ j59) & 72057594037862400L;
            long j80 = j51 ^ j79;
            long j81 = j59 ^ j79;
            long j82 = (j52 ^ j60) & 72057594037927680L;
            long j83 = j52 ^ j82;
            long j84 = j60 ^ j82;
            long j85 = (j62 ^ j74) & 72057589742960640L;
            long j86 = j62 ^ j85;
            long j87 = j74 ^ j85;
            long j88 = (j65 ^ j77) & -16777216;
            long j89 = j65 ^ j88;
            long j90 = j77 ^ j88;
            long j91 = (j68 ^ j80) & -71776119061282816L;
            long j92 = j68 ^ j91;
            long j93 = j80 ^ j91;
            long j94 = (j71 ^ j83) & -72056494526300416L;
            long j95 = j71 ^ j94;
            long j96 = j83 ^ j94;
            long j97 = (j63 ^ j75) & 72057589742960640L;
            long j98 = j63 ^ j97;
            long j99 = j75 ^ j97;
            long j100 = (j66 ^ j78) & -16777216;
            long j101 = j66 ^ j100;
            long j102 = j78 ^ j100;
            long j103 = (j69 ^ j81) & -71776119061282816L;
            long j104 = j69 ^ j103;
            long j105 = j81 ^ j103;
            long j106 = (j72 ^ j84) & -72056494526300416L;
            long j107 = j72 ^ j106;
            long j108 = j84 ^ j106;
            long j109 = (j86 ^ j92) & -281470681808896L;
            long j110 = j86 ^ j109;
            long j111 = j92 ^ j109;
            long j112 = (j89 ^ j95) & 72056494543077120L;
            long j113 = j89 ^ j112;
            long j114 = j95 ^ j112;
            long j115 = (j87 ^ j93) & -281470681808896L;
            long j116 = j87 ^ j115;
            long j117 = j93 ^ j115;
            long j118 = (j90 ^ j96) & 72056494543077120L;
            long j119 = j90 ^ j118;
            long j120 = j96 ^ j118;
            long j121 = (j98 ^ j104) & -281470681808896L;
            long j122 = j98 ^ j121;
            long j123 = j104 ^ j121;
            long j124 = (j101 ^ j107) & 72056494543077120L;
            long j125 = j101 ^ j124;
            long j126 = j107 ^ j124;
            long j127 = (j99 ^ j105) & -281470681808896L;
            long j128 = j99 ^ j127;
            long j129 = j105 ^ j127;
            long j130 = (j102 ^ j108) & 72056494543077120L;
            long j131 = j102 ^ j130;
            long j132 = j108 ^ j130;
            long j133 = (j110 ^ j113) & -71777214294589696L;
            long j134 = j110 ^ j133;
            long j135 = j113 ^ j133;
            long j136 = (j111 ^ j114) & -71777214294589696L;
            long j137 = j111 ^ j136;
            long j138 = j114 ^ j136;
            long j139 = (j116 ^ j119) & -71777214294589696L;
            long j140 = j116 ^ j139;
            long j141 = j119 ^ j139;
            long j142 = (j117 ^ j120) & -71777214294589696L;
            long j143 = j117 ^ j142;
            long j144 = j120 ^ j142;
            long j145 = (j122 ^ j125) & -71777214294589696L;
            long j146 = j122 ^ j145;
            long j147 = j125 ^ j145;
            long j148 = (j123 ^ j126) & -71777214294589696L;
            long j149 = j123 ^ j148;
            long j150 = j126 ^ j148;
            long j151 = (j128 ^ j131) & -71777214294589696L;
            long j152 = j128 ^ j151;
            long j153 = j131 ^ j151;
            long j154 = (j129 ^ j132) & -71777214294589696L;
            long j155 = j129 ^ j154;
            long j156 = j132 ^ j154;
            jArr[0] = j134;
            jArr[1] = j135;
            jArr[2] = j137;
            jArr[3] = j138;
            jArr[4] = j140;
            jArr[5] = j141;
            jArr[6] = j143;
            jArr[7] = j144;
            jArr[8] = j146;
            jArr[9] = j147;
            jArr[10] = j149;
            jArr[11] = j150;
            jArr[12] = j152;
            jArr[13] = j153;
            jArr[14] = j155;
            jArr[15] = j156;
        } else {
            throw new IllegalStateException("unsupported state size: only 512/1024 are allowed");
        }
    }

    private void subBytes(long[] jArr) {
        for (int i = 0; i < this.columns; i++) {
            long j = jArr[i];
            int i2 = (int) j;
            int i3 = (int) (j >>> 32);
            byte[] bArr = S0;
            byte b = bArr[i2 & 255];
            byte[] bArr2 = S1;
            byte b2 = bArr2[(i2 >>> 8) & 255];
            byte[] bArr3 = S2;
            byte b3 = bArr3[(i2 >>> 16) & 255];
            byte[] bArr4 = S3;
            byte b4 = (bArr4[i2 >>> 24] << 24) | (b & 255) | ((b2 & 255) << 8) | ((b3 & 255) << Tnaf.POW_2_WIDTH);
            byte b5 = bArr[i3 & 255];
            byte b6 = bArr2[(i3 >>> 8) & 255];
            byte b7 = bArr3[(i3 >>> 16) & 255];
            jArr[i] = (((long) b4) & BodyPartID.bodyIdMax) | (((long) ((bArr4[i3 >>> 24] << 24) | (((b5 & 255) | ((b6 & 255) << 8)) | ((b7 & 255) << Tnaf.POW_2_WIDTH)))) << 32);
        }
    }

    public Memoable copy() {
        return new DSTU7564Digest(this);
    }

    public int doFinal(byte[] bArr, int i) {
        int i2;
        int i3;
        int i4 = this.bufOff;
        byte[] bArr2 = this.buf;
        this.bufOff = i4 + 1;
        bArr2[i4] = Byte.MIN_VALUE;
        int i5 = this.blockSize - 12;
        int i6 = 0;
        if (this.bufOff > i5) {
            while (true) {
                int i7 = this.bufOff;
                if (i7 >= this.blockSize) {
                    break;
                }
                byte[] bArr3 = this.buf;
                this.bufOff = i7 + 1;
                bArr3[i7] = 0;
            }
            this.bufOff = 0;
            processBlock(this.buf, 0);
        }
        while (true) {
            i2 = this.bufOff;
            if (i2 >= i5) {
                break;
            }
            byte[] bArr4 = this.buf;
            this.bufOff = i2 + 1;
            bArr4[i2] = 0;
        }
        long j = (((this.inputBlocks & BodyPartID.bodyIdMax) * ((long) this.blockSize)) + ((long) i4)) << 3;
        Pack.intToLittleEndian((int) j, this.buf, i2);
        this.bufOff += 4;
        Pack.longToLittleEndian((j >>> 32) + (((this.inputBlocks >>> 32) * ((long) this.blockSize)) << 3), this.buf, this.bufOff);
        processBlock(this.buf, 0);
        System.arraycopy(this.state, 0, this.tempState1, 0, this.columns);
        P(this.tempState1);
        while (true) {
            i3 = this.columns;
            if (i6 >= i3) {
                break;
            }
            long[] jArr = this.state;
            jArr[i6] = jArr[i6] ^ this.tempState1[i6];
            i6++;
        }
        for (int i8 = i3 - (this.hashSize >>> 3); i8 < this.columns; i8++) {
            Pack.longToLittleEndian(this.state[i8], bArr, i);
            i += 8;
        }
        reset();
        return this.hashSize;
    }

    public String getAlgorithmName() {
        return "DSTU7564";
    }

    public int getByteLength() {
        return this.blockSize;
    }

    public int getDigestSize() {
        return this.hashSize;
    }

    public void reset() {
        Arrays.fill(this.state, 0);
        this.state[0] = (long) this.blockSize;
        this.inputBlocks = 0;
        this.bufOff = 0;
    }

    public void reset(Memoable memoable) {
        copyIn((DSTU7564Digest) memoable);
    }

    public void update(byte b) {
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = b;
        if (this.bufOff == this.blockSize) {
            processBlock(bArr, 0);
            this.bufOff = 0;
            this.inputBlocks++;
        }
    }

    public void update(byte[] bArr, int i, int i2) {
        while (this.bufOff != 0 && i2 > 0) {
            int i3 = i + 1;
            update(bArr[i]);
            i2--;
            i = i3;
        }
        if (i2 > 0) {
            while (i2 >= this.blockSize) {
                processBlock(bArr, i);
                int i4 = this.blockSize;
                i += i4;
                i2 -= i4;
                this.inputBlocks++;
            }
            while (i2 > 0) {
                int i5 = i + 1;
                update(bArr[i]);
                i2--;
                i = i5;
            }
        }
    }
}
