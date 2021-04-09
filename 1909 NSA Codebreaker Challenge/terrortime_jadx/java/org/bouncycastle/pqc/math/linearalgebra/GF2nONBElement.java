package org.bouncycastle.pqc.math.linearalgebra;

import com.badguy.terrortime.BuildConfig;
import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.asn1.cmc.BodyPartID;
import org.bouncycastle.util.Arrays;

public class GF2nONBElement extends GF2nElement {
    private static final int MAXLONG = 64;
    private static final long[] mBitmask = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824, 2147483648L, 4294967296L, 8589934592L, 17179869184L, 34359738368L, 68719476736L, 137438953472L, 274877906944L, 549755813888L, 1099511627776L, 2199023255552L, 4398046511104L, 8796093022208L, 17592186044416L, 35184372088832L, 70368744177664L, 140737488355328L, 281474976710656L, 562949953421312L, 1125899906842624L, 2251799813685248L, 4503599627370496L, 9007199254740992L, 18014398509481984L, 36028797018963968L, 72057594037927936L, 144115188075855872L, 288230376151711744L, 576460752303423488L, 1152921504606846976L, 2305843009213693952L, 4611686018427387904L, Long.MIN_VALUE};
    private static final int[] mIBY64 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5};
    private static final long[] mMaxmask = {1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727, 268435455, 536870911, 1073741823, 2147483647L, BodyPartID.bodyIdMax, 8589934591L, 17179869183L, 34359738367L, 68719476735L, 137438953471L, 274877906943L, 549755813887L, 1099511627775L, 2199023255551L, 4398046511103L, 8796093022207L, 17592186044415L, 35184372088831L, 70368744177663L, 140737488355327L, 281474976710655L, 562949953421311L, 1125899906842623L, 2251799813685247L, 4503599627370495L, 9007199254740991L, 18014398509481983L, 36028797018963967L, 72057594037927935L, 144115188075855871L, 288230376151711743L, 576460752303423487L, 1152921504606846975L, 2305843009213693951L, 4611686018427387903L, Long.MAX_VALUE, -1};
    private int mBit;
    private int mLength;
    private long[] mPol;

    public GF2nONBElement(GF2nONBElement gF2nONBElement) {
        this.mField = gF2nONBElement.mField;
        this.mDegree = this.mField.getDegree();
        this.mLength = ((GF2nONBField) this.mField).getONBLength();
        this.mBit = ((GF2nONBField) this.mField).getONBBit();
        this.mPol = new long[this.mLength];
        assign(gF2nONBElement.getElement());
    }

    public GF2nONBElement(GF2nONBField gF2nONBField, BigInteger bigInteger) {
        this.mField = gF2nONBField;
        this.mDegree = this.mField.getDegree();
        this.mLength = gF2nONBField.getONBLength();
        this.mBit = gF2nONBField.getONBBit();
        this.mPol = new long[this.mLength];
        assign(bigInteger);
    }

    public GF2nONBElement(GF2nONBField gF2nONBField, SecureRandom secureRandom) {
        this.mField = gF2nONBField;
        this.mDegree = this.mField.getDegree();
        this.mLength = gF2nONBField.getONBLength();
        this.mBit = gF2nONBField.getONBBit();
        int i = this.mLength;
        this.mPol = new long[i];
        if (i > 1) {
            for (int i2 = 0; i2 < this.mLength - 1; i2++) {
                this.mPol[i2] = secureRandom.nextLong();
            }
            this.mPol[this.mLength - 1] = secureRandom.nextLong() >>> (64 - this.mBit);
            return;
        }
        this.mPol[0] = secureRandom.nextLong();
        long[] jArr = this.mPol;
        jArr[0] = jArr[0] >>> (64 - this.mBit);
    }

    public GF2nONBElement(GF2nONBField gF2nONBField, byte[] bArr) {
        this.mField = gF2nONBField;
        this.mDegree = this.mField.getDegree();
        this.mLength = gF2nONBField.getONBLength();
        this.mBit = gF2nONBField.getONBBit();
        this.mPol = new long[this.mLength];
        assign(bArr);
    }

    private GF2nONBElement(GF2nONBField gF2nONBField, long[] jArr) {
        this.mField = gF2nONBField;
        this.mDegree = this.mField.getDegree();
        this.mLength = gF2nONBField.getONBLength();
        this.mBit = gF2nONBField.getONBBit();
        this.mPol = jArr;
    }

    public static GF2nONBElement ONE(GF2nONBField gF2nONBField) {
        int oNBLength = gF2nONBField.getONBLength();
        long[] jArr = new long[oNBLength];
        int i = 0;
        while (true) {
            int i2 = oNBLength - 1;
            if (i < i2) {
                jArr[i] = -1;
                i++;
            } else {
                jArr[i2] = mMaxmask[gF2nONBField.getONBBit() - 1];
                return new GF2nONBElement(gF2nONBField, jArr);
            }
        }
    }

    public static GF2nONBElement ZERO(GF2nONBField gF2nONBField) {
        return new GF2nONBElement(gF2nONBField, new long[gF2nONBField.getONBLength()]);
    }

    private void assign(BigInteger bigInteger) {
        assign(bigInteger.toByteArray());
    }

    private void assign(byte[] bArr) {
        this.mPol = new long[this.mLength];
        for (int i = 0; i < bArr.length; i++) {
            long[] jArr = this.mPol;
            int i2 = i >>> 3;
            jArr[i2] = jArr[i2] | ((((long) bArr[(bArr.length - 1) - i]) & 255) << ((i & 7) << 3));
        }
    }

    private void assign(long[] jArr) {
        System.arraycopy(jArr, 0, this.mPol, 0, this.mLength);
    }

    private long[] getElement() {
        long[] jArr = this.mPol;
        long[] jArr2 = new long[jArr.length];
        System.arraycopy(jArr, 0, jArr2, 0, jArr.length);
        return jArr2;
    }

    private long[] getElementReverseOrder() {
        long[] jArr = new long[this.mPol.length];
        for (int i = 0; i < this.mDegree; i++) {
            if (testBit((this.mDegree - i) - 1)) {
                int i2 = i >>> 6;
                jArr[i2] = jArr[i2] | mBitmask[i & 63];
            }
        }
        return jArr;
    }

    public GFElement add(GFElement gFElement) throws RuntimeException {
        GF2nONBElement gF2nONBElement = new GF2nONBElement(this);
        gF2nONBElement.addToThis(gFElement);
        return gF2nONBElement;
    }

    public void addToThis(GFElement gFElement) throws RuntimeException {
        if (gFElement instanceof GF2nONBElement) {
            GF2nONBElement gF2nONBElement = (GF2nONBElement) gFElement;
            if (this.mField.equals(gF2nONBElement.mField)) {
                for (int i = 0; i < this.mLength; i++) {
                    long[] jArr = this.mPol;
                    jArr[i] = jArr[i] ^ gF2nONBElement.mPol[i];
                }
                return;
            }
            throw new RuntimeException();
        }
        throw new RuntimeException();
    }

    /* access modifiers changed from: 0000 */
    public void assignOne() {
        int i = 0;
        while (true) {
            int i2 = this.mLength;
            if (i < i2 - 1) {
                this.mPol[i] = -1;
                i++;
            } else {
                this.mPol[i2 - 1] = mMaxmask[this.mBit - 1];
                return;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void assignZero() {
        this.mPol = new long[this.mLength];
    }

    public Object clone() {
        return new GF2nONBElement(this);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof GF2nONBElement)) {
            return false;
        }
        GF2nONBElement gF2nONBElement = (GF2nONBElement) obj;
        for (int i = 0; i < this.mLength; i++) {
            if (this.mPol[i] != gF2nONBElement.mPol[i]) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return Arrays.hashCode(this.mPol);
    }

    public GF2nElement increase() {
        GF2nONBElement gF2nONBElement = new GF2nONBElement(this);
        gF2nONBElement.increaseThis();
        return gF2nONBElement;
    }

    public void increaseThis() {
        addToThis(ONE((GF2nONBField) this.mField));
    }

    public GFElement invert() throws ArithmeticException {
        GF2nONBElement gF2nONBElement = new GF2nONBElement(this);
        gF2nONBElement.invertThis();
        return gF2nONBElement;
    }

    public void invertThis() throws ArithmeticException {
        if (!isZero()) {
            int i = 31;
            boolean z = false;
            while (!z && i >= 0) {
                if ((((long) (this.mDegree - 1)) & mBitmask[i]) != 0) {
                    z = true;
                }
                i--;
            }
            int i2 = i + 1;
            ZERO((GF2nONBField) this.mField);
            GF2nONBElement gF2nONBElement = new GF2nONBElement(this);
            int i3 = 1;
            for (int i4 = i2 - 1; i4 >= 0; i4--) {
                GF2nElement gF2nElement = (GF2nElement) gF2nONBElement.clone();
                for (int i5 = 1; i5 <= i3; i5++) {
                    gF2nElement.squareThis();
                }
                gF2nONBElement.multiplyThisBy(gF2nElement);
                i3 <<= 1;
                if ((((long) (this.mDegree - 1)) & mBitmask[i4]) != 0) {
                    gF2nONBElement.squareThis();
                    gF2nONBElement.multiplyThisBy(this);
                    i3++;
                }
            }
            gF2nONBElement.squareThis();
            return;
        }
        throw new ArithmeticException();
    }

    public boolean isOne() {
        boolean z = true;
        for (int i = 0; i < this.mLength - 1 && z; i++) {
            z = z && (this.mPol[i] & -1) == -1;
        }
        if (!z) {
            return z;
        }
        if (z) {
            long j = this.mPol[this.mLength - 1];
            long[] jArr = mMaxmask;
            int i2 = this.mBit;
            if ((j & jArr[i2 - 1]) == jArr[i2 - 1]) {
                return true;
            }
        }
        return false;
    }

    public boolean isZero() {
        boolean z = true;
        for (int i = 0; i < this.mLength && z; i++) {
            z = z && (this.mPol[i] & -1) == 0;
        }
        return z;
    }

    public GFElement multiply(GFElement gFElement) throws RuntimeException {
        GF2nONBElement gF2nONBElement = new GF2nONBElement(this);
        gF2nONBElement.multiplyThisBy(gFElement);
        return gF2nONBElement;
    }

    public void multiplyThisBy(GFElement gFElement) throws RuntimeException {
        boolean z;
        GFElement gFElement2 = gFElement;
        if (gFElement2 instanceof GF2nONBElement) {
            GF2nONBElement gF2nONBElement = (GF2nONBElement) gFElement2;
            if (!this.mField.equals(gF2nONBElement.mField)) {
                throw new RuntimeException();
            } else if (equals(gFElement)) {
                squareThis();
            } else {
                long[] jArr = this.mPol;
                long[] jArr2 = gF2nONBElement.mPol;
                long[] jArr3 = new long[this.mLength];
                int[][] iArr = ((GF2nONBField) this.mField).mMult;
                int i = this.mLength - 1;
                int i2 = this.mBit - 1;
                long[] jArr4 = mBitmask;
                long j = jArr4[63];
                long j2 = jArr4[i2];
                int i3 = 0;
                int i4 = 0;
                while (i4 < this.mDegree) {
                    int i5 = i3;
                    int i6 = i5;
                    while (i5 < this.mDegree) {
                        int[] iArr2 = mIBY64;
                        int i7 = iArr2[i5];
                        int i8 = i5 & 63;
                        int i9 = iArr2[iArr[i5][i3]];
                        int i10 = iArr[i5][i3] & 63;
                        long j3 = jArr[i7];
                        long[] jArr5 = mBitmask;
                        if ((j3 & jArr5[i8]) != 0) {
                            if ((jArr2[i9] & jArr5[i10]) != 0) {
                                i6 ^= 1;
                            }
                            if (iArr[i5][1] != -1) {
                                if ((jArr2[mIBY64[iArr[i5][1]]] & mBitmask[iArr[i5][1] & 63]) != 0) {
                                    i6 ^= 1;
                                }
                            }
                        }
                        i5++;
                        i3 = 0;
                    }
                    int i11 = mIBY64[i4];
                    int i12 = i4 & 63;
                    if (i6 != 0) {
                        jArr3[i11] = jArr3[i11] ^ mBitmask[i12];
                    }
                    if (this.mLength > 1) {
                        int i13 = i - 1;
                        boolean z2 = (jArr[i] & 1) == 1;
                        int i14 = i13;
                        while (i14 >= 0) {
                            boolean z3 = (jArr[i14] & 1) != 0;
                            jArr[i14] = jArr[i14] >>> 1;
                            if (z2) {
                                jArr[i14] = jArr[i14] ^ j;
                            }
                            i14--;
                            z2 = z3;
                        }
                        jArr[i] = jArr[i] >>> 1;
                        if (z2) {
                            jArr[i] = jArr[i] ^ j2;
                        }
                        boolean z4 = (jArr2[i] & 1) == 1;
                        while (i13 >= 0) {
                            boolean z5 = (jArr2[i13] & 1) != 0;
                            jArr2[i13] = jArr2[i13] >>> 1;
                            if (z4) {
                                jArr2[i13] = jArr2[i13] ^ j;
                            }
                            i13--;
                            z4 = z5;
                        }
                        jArr2[i] = jArr2[i] >>> 1;
                        if (z4) {
                            jArr2[i] = jArr2[i] ^ j2;
                        }
                        i3 = 0;
                        z = true;
                    } else {
                        i3 = 0;
                        boolean z6 = (jArr[0] & 1) == 1;
                        jArr[0] = jArr[0] >>> 1;
                        if (z6) {
                            jArr[0] = jArr[0] ^ j2;
                        }
                        boolean z7 = (jArr2[0] & 1) == 1;
                        z = true;
                        jArr2[0] = jArr2[0] >>> 1;
                        if (z7) {
                            jArr2[0] = jArr2[0] ^ j2;
                        }
                    }
                    i4++;
                    boolean z8 = z;
                }
                assign(jArr3);
            }
        } else {
            throw new RuntimeException("The elements have different representation: not yet implemented");
        }
    }

    /* access modifiers changed from: 0000 */
    public void reverseOrder() {
        this.mPol = getElementReverseOrder();
    }

    public GF2nElement solveQuadraticEquation() throws RuntimeException {
        if (trace() != 1) {
            long j = mBitmask[63];
            long[] jArr = new long[this.mLength];
            int i = 0;
            long j2 = 0;
            for (int i2 = 1; i < this.mLength - i2; i2 = 1) {
                long j3 = j2;
                for (int i3 = i2; i3 < 64; i3++) {
                    long[] jArr2 = mBitmask;
                    if ((jArr2[i3] & this.mPol[i]) == 0 || (j3 & jArr2[i3 - 1]) == 0) {
                        long j4 = this.mPol[i];
                        long[] jArr3 = mBitmask;
                        if ((j4 & jArr3[i3]) != 0 || (jArr3[i3 - 1] & j3) != 0) {
                            j3 ^= mBitmask[i3];
                        }
                    }
                }
                jArr[i] = j3;
                int i4 = ((j & j3) > 0 ? 1 : ((j & j3) == 0 ? 0 : -1));
                j2 = ((i4 == 0 || (1 & this.mPol[i + 1]) != 1) && !(i4 == 0 && (this.mPol[i + 1] & 1) == 0)) ? 1 : 0;
                i++;
            }
            int i5 = this.mDegree & 63;
            long j5 = this.mPol[this.mLength - 1];
            for (int i6 = 1; i6 < i5; i6++) {
                long[] jArr4 = mBitmask;
                if ((jArr4[i6] & j5) == 0 || (jArr4[i6 - 1] & j2) == 0) {
                    long[] jArr5 = mBitmask;
                    if ((jArr5[i6] & j5) != 0 || (jArr5[i6 - 1] & j2) != 0) {
                        j2 ^= mBitmask[i6];
                    }
                }
            }
            jArr[this.mLength - 1] = j2;
            return new GF2nONBElement((GF2nONBField) this.mField, jArr);
        }
        throw new RuntimeException();
    }

    public GF2nElement square() {
        GF2nONBElement gF2nONBElement = new GF2nONBElement(this);
        gF2nONBElement.squareThis();
        return gF2nONBElement;
    }

    public GF2nElement squareRoot() {
        GF2nONBElement gF2nONBElement = new GF2nONBElement(this);
        gF2nONBElement.squareRootThis();
        return gF2nONBElement;
    }

    public void squareRootThis() {
        long[] element = getElement();
        int i = this.mLength - 1;
        int i2 = this.mBit - 1;
        long j = mBitmask[63];
        boolean z = (element[0] & 1) != 0;
        int i3 = i;
        while (i3 >= 0) {
            boolean z2 = (element[i3] & 1) != 0;
            element[i3] = element[i3] >>> 1;
            if (z) {
                if (i3 == i) {
                    element[i3] = element[i3] ^ mBitmask[i2];
                } else {
                    element[i3] = element[i3] ^ j;
                }
            }
            i3--;
            z = z2;
        }
        assign(element);
    }

    public void squareThis() {
        long[] element = getElement();
        int i = this.mLength - 1;
        int i2 = this.mBit - 1;
        long[] jArr = mBitmask;
        long j = jArr[63];
        boolean z = false;
        boolean z2 = (element[i] & jArr[i2]) != 0;
        int i3 = 0;
        while (i3 < i) {
            boolean z3 = (element[i3] & j) != 0;
            element[i3] = element[i3] << 1;
            if (z2) {
                element[i3] = 1 ^ element[i3];
            }
            i3++;
            z2 = z3;
        }
        if ((element[i] & mBitmask[i2]) != 0) {
            z = true;
        }
        element[i] = element[i] << 1;
        if (z2) {
            element[i] = element[i] ^ 1;
        }
        if (z) {
            element[i] = mBitmask[i2 + 1] ^ element[i];
        }
        assign(element);
    }

    /* access modifiers changed from: 0000 */
    public boolean testBit(int i) {
        return i >= 0 && i <= this.mDegree && (this.mPol[i >>> 6] & mBitmask[i & 63]) != 0;
    }

    public boolean testRightmostBit() {
        return (this.mPol[this.mLength - 1] & mBitmask[this.mBit - 1]) != 0;
    }

    public byte[] toByteArray() {
        int i = ((this.mDegree - 1) >> 3) + 1;
        byte[] bArr = new byte[i];
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = (i2 & 7) << 3;
            bArr[(i - i2) - 1] = (byte) ((int) ((this.mPol[i2 >>> 3] & (255 << i3)) >>> i3));
        }
        return bArr;
    }

    public BigInteger toFlexiBigInt() {
        return new BigInteger(1, toByteArray());
    }

    public String toString() {
        return toString(16);
    }

    public String toString(int i) {
        String str;
        String str2;
        StringBuilder sb;
        String str3;
        long[] element = getElement();
        int i2 = this.mBit;
        String str4 = BuildConfig.FLAVOR;
        if (i == 2) {
            while (true) {
                i2--;
                str = "0";
                str2 = "1";
                if (i2 < 0) {
                    break;
                }
                if ((element[element.length - 1] & (1 << i2)) == 0) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str4);
                    sb2.append(str);
                    str3 = sb2.toString();
                } else {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(str4);
                    sb3.append(str2);
                    str3 = sb3.toString();
                }
                str4 = str3;
            }
            for (int length = element.length - 2; length >= 0; length--) {
                for (int i3 = 63; i3 >= 0; i3--) {
                    if ((element[length] & mBitmask[i3]) == 0) {
                        sb = new StringBuilder();
                        sb.append(str4);
                        sb.append(str);
                    } else {
                        sb = new StringBuilder();
                        sb.append(str4);
                        sb.append(str2);
                    }
                    str4 = sb.toString();
                }
            }
        } else if (i == 16) {
            char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            for (int length2 = element.length - 1; length2 >= 0; length2--) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(str4);
                sb4.append(cArr[((int) (element[length2] >>> 60)) & 15]);
                String sb5 = sb4.toString();
                StringBuilder sb6 = new StringBuilder();
                sb6.append(sb5);
                sb6.append(cArr[((int) (element[length2] >>> 56)) & 15]);
                String sb7 = sb6.toString();
                StringBuilder sb8 = new StringBuilder();
                sb8.append(sb7);
                sb8.append(cArr[((int) (element[length2] >>> 52)) & 15]);
                String sb9 = sb8.toString();
                StringBuilder sb10 = new StringBuilder();
                sb10.append(sb9);
                sb10.append(cArr[((int) (element[length2] >>> 48)) & 15]);
                String sb11 = sb10.toString();
                StringBuilder sb12 = new StringBuilder();
                sb12.append(sb11);
                sb12.append(cArr[((int) (element[length2] >>> 44)) & 15]);
                String sb13 = sb12.toString();
                StringBuilder sb14 = new StringBuilder();
                sb14.append(sb13);
                sb14.append(cArr[((int) (element[length2] >>> 40)) & 15]);
                String sb15 = sb14.toString();
                StringBuilder sb16 = new StringBuilder();
                sb16.append(sb15);
                sb16.append(cArr[((int) (element[length2] >>> 36)) & 15]);
                String sb17 = sb16.toString();
                StringBuilder sb18 = new StringBuilder();
                sb18.append(sb17);
                sb18.append(cArr[((int) (element[length2] >>> 32)) & 15]);
                String sb19 = sb18.toString();
                StringBuilder sb20 = new StringBuilder();
                sb20.append(sb19);
                sb20.append(cArr[((int) (element[length2] >>> 28)) & 15]);
                String sb21 = sb20.toString();
                StringBuilder sb22 = new StringBuilder();
                sb22.append(sb21);
                sb22.append(cArr[((int) (element[length2] >>> 24)) & 15]);
                String sb23 = sb22.toString();
                StringBuilder sb24 = new StringBuilder();
                sb24.append(sb23);
                sb24.append(cArr[((int) (element[length2] >>> 20)) & 15]);
                String sb25 = sb24.toString();
                StringBuilder sb26 = new StringBuilder();
                sb26.append(sb25);
                sb26.append(cArr[((int) (element[length2] >>> 16)) & 15]);
                String sb27 = sb26.toString();
                StringBuilder sb28 = new StringBuilder();
                sb28.append(sb27);
                sb28.append(cArr[((int) (element[length2] >>> 12)) & 15]);
                String sb29 = sb28.toString();
                StringBuilder sb30 = new StringBuilder();
                sb30.append(sb29);
                sb30.append(cArr[((int) (element[length2] >>> 8)) & 15]);
                String sb31 = sb30.toString();
                StringBuilder sb32 = new StringBuilder();
                sb32.append(sb31);
                sb32.append(cArr[((int) (element[length2] >>> 4)) & 15]);
                String sb33 = sb32.toString();
                StringBuilder sb34 = new StringBuilder();
                sb34.append(sb33);
                sb34.append(cArr[((int) element[length2]) & 15]);
                String sb35 = sb34.toString();
                StringBuilder sb36 = new StringBuilder();
                sb36.append(sb35);
                sb36.append(" ");
                str4 = sb36.toString();
            }
        }
        return str4;
    }

    public int trace() {
        int i = this.mLength - 1;
        int i2 = 0;
        int i3 = 0;
        while (i2 < i) {
            int i4 = i3;
            for (int i5 = 0; i5 < 64; i5++) {
                if ((this.mPol[i2] & mBitmask[i5]) != 0) {
                    i4 ^= 1;
                }
            }
            i2++;
            i3 = i4;
        }
        int i6 = this.mBit;
        for (int i7 = 0; i7 < i6; i7++) {
            if ((this.mPol[i] & mBitmask[i7]) != 0) {
                i3 ^= 1;
            }
        }
        return i3;
    }
}
