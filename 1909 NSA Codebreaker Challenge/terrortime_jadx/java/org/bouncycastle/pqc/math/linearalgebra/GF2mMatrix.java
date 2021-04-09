package org.bouncycastle.pqc.math.linearalgebra;

import java.lang.reflect.Array;

public class GF2mMatrix extends Matrix {
    protected GF2mField field;
    protected int[][] matrix;

    public GF2mMatrix(GF2mField gF2mField, byte[] bArr) {
        this.field = gF2mField;
        int i = 8;
        int i2 = 1;
        while (gF2mField.getDegree() > i) {
            i2++;
            i += 8;
        }
        String str = " Error: given array is not encoded matrix over GF(2^m)";
        if (bArr.length >= 5) {
            this.numRows = ((((bArr[3] & 255) << 24) ^ ((bArr[2] & 255) << Tnaf.POW_2_WIDTH)) ^ ((bArr[1] & 255) << 8)) ^ (bArr[0] & 255);
            int i3 = i2 * this.numRows;
            if (this.numRows > 0) {
                int i4 = 4;
                if ((bArr.length - 4) % i3 == 0) {
                    this.numColumns = (bArr.length - 4) / i3;
                    this.matrix = (int[][]) Array.newInstance(int.class, new int[]{this.numRows, this.numColumns});
                    int i5 = 0;
                    while (i5 < this.numRows) {
                        int i6 = i4;
                        int i7 = 0;
                        while (i7 < this.numColumns) {
                            int i8 = i6;
                            int i9 = 0;
                            while (i9 < i) {
                                int[] iArr = this.matrix[i5];
                                int i10 = i8 + 1;
                                iArr[i7] = ((bArr[i8] & 255) << i9) ^ iArr[i7];
                                i9 += 8;
                                i8 = i10;
                            }
                            if (this.field.isElementOfThisField(this.matrix[i5][i7])) {
                                i7++;
                                i6 = i8;
                            } else {
                                throw new IllegalArgumentException(str);
                            }
                        }
                        i5++;
                        i4 = i6;
                    }
                    return;
                }
            }
            throw new IllegalArgumentException(str);
        }
        throw new IllegalArgumentException(str);
    }

    protected GF2mMatrix(GF2mField gF2mField, int[][] iArr) {
        this.field = gF2mField;
        this.matrix = iArr;
        this.numRows = iArr.length;
        this.numColumns = iArr[0].length;
    }

    public GF2mMatrix(GF2mMatrix gF2mMatrix) {
        this.numRows = gF2mMatrix.numRows;
        this.numColumns = gF2mMatrix.numColumns;
        this.field = gF2mMatrix.field;
        this.matrix = new int[this.numRows][];
        for (int i = 0; i < this.numRows; i++) {
            this.matrix[i] = IntUtils.clone(gF2mMatrix.matrix[i]);
        }
    }

    private void addToRow(int[] iArr, int[] iArr2) {
        for (int length = iArr2.length - 1; length >= 0; length--) {
            iArr2[length] = this.field.add(iArr[length], iArr2[length]);
        }
    }

    private int[] multRowWithElement(int[] iArr, int i) {
        int[] iArr2 = new int[iArr.length];
        for (int length = iArr.length - 1; length >= 0; length--) {
            iArr2[length] = this.field.mult(iArr[length], i);
        }
        return iArr2;
    }

    private void multRowWithElementThis(int[] iArr, int i) {
        for (int length = iArr.length - 1; length >= 0; length--) {
            iArr[length] = this.field.mult(iArr[length], i);
        }
    }

    private static void swapColumns(int[][] iArr, int i, int i2) {
        int[] iArr2 = iArr[i];
        iArr[i] = iArr[i2];
        iArr[i2] = iArr2;
    }

    public Matrix computeInverse() {
        Class<int> cls = int.class;
        String str = "Matrix is not invertible.";
        if (this.numRows == this.numColumns) {
            int[][] iArr = (int[][]) Array.newInstance(cls, new int[]{this.numRows, this.numRows});
            for (int i = this.numRows - 1; i >= 0; i--) {
                iArr[i] = IntUtils.clone(this.matrix[i]);
            }
            int[][] iArr2 = (int[][]) Array.newInstance(cls, new int[]{this.numRows, this.numRows});
            for (int i2 = this.numRows - 1; i2 >= 0; i2--) {
                iArr2[i2][i2] = 1;
            }
            for (int i3 = 0; i3 < this.numRows; i3++) {
                if (iArr[i3][i3] == 0) {
                    int i4 = i3 + 1;
                    boolean z = false;
                    while (i4 < this.numRows) {
                        if (iArr[i4][i3] != 0) {
                            swapColumns(iArr, i3, i4);
                            swapColumns(iArr2, i3, i4);
                            i4 = this.numRows;
                            z = true;
                        }
                        i4++;
                    }
                    if (!z) {
                        throw new ArithmeticException(str);
                    }
                }
                int inverse = this.field.inverse(iArr[i3][i3]);
                multRowWithElementThis(iArr[i3], inverse);
                multRowWithElementThis(iArr2[i3], inverse);
                for (int i5 = 0; i5 < this.numRows; i5++) {
                    if (i5 != i3) {
                        int i6 = iArr[i5][i3];
                        if (i6 != 0) {
                            int[] multRowWithElement = multRowWithElement(iArr[i3], i6);
                            int[] multRowWithElement2 = multRowWithElement(iArr2[i3], i6);
                            addToRow(multRowWithElement, iArr[i5]);
                            addToRow(multRowWithElement2, iArr2[i5]);
                        }
                    }
                }
            }
            return new GF2mMatrix(this.field, iArr2);
        }
        throw new ArithmeticException(str);
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof GF2mMatrix)) {
            GF2mMatrix gF2mMatrix = (GF2mMatrix) obj;
            if (this.field.equals(gF2mMatrix.field) && gF2mMatrix.numRows == this.numColumns && gF2mMatrix.numColumns == this.numColumns) {
                for (int i = 0; i < this.numRows; i++) {
                    for (int i2 = 0; i2 < this.numColumns; i2++) {
                        if (this.matrix[i][i2] != gF2mMatrix.matrix[i][i2]) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public byte[] getEncoded() {
        int i = 8;
        int i2 = 1;
        while (this.field.getDegree() > i) {
            i2++;
            i += 8;
        }
        int i3 = this.numRows * this.numColumns * i2;
        int i4 = 4;
        byte[] bArr = new byte[(i3 + 4)];
        bArr[0] = (byte) (this.numRows & 255);
        bArr[1] = (byte) ((this.numRows >>> 8) & 255);
        bArr[2] = (byte) ((this.numRows >>> 16) & 255);
        bArr[3] = (byte) ((this.numRows >>> 24) & 255);
        for (int i5 = 0; i5 < this.numRows; i5++) {
            int i6 = 0;
            while (i6 < this.numColumns) {
                int i7 = i4;
                int i8 = 0;
                while (i8 < i) {
                    int i9 = i7 + 1;
                    bArr[i7] = (byte) (this.matrix[i5][i6] >>> i8);
                    i8 += 8;
                    i7 = i9;
                }
                i6++;
                i4 = i7;
            }
        }
        return bArr;
    }

    public int hashCode() {
        int hashCode = (((this.field.hashCode() * 31) + this.numRows) * 31) + this.numColumns;
        int i = 0;
        while (i < this.numRows) {
            int i2 = hashCode;
            for (int i3 = 0; i3 < this.numColumns; i3++) {
                i2 = (i2 * 31) + this.matrix[i][i3];
            }
            i++;
            hashCode = i2;
        }
        return hashCode;
    }

    public boolean isZero() {
        for (int i = 0; i < this.numRows; i++) {
            for (int i2 = 0; i2 < this.numColumns; i2++) {
                if (this.matrix[i][i2] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public Vector leftMultiply(Vector vector) {
        throw new RuntimeException("Not implemented.");
    }

    public Matrix rightMultiply(Matrix matrix2) {
        throw new RuntimeException("Not implemented.");
    }

    public Matrix rightMultiply(Permutation permutation) {
        throw new RuntimeException("Not implemented.");
    }

    public Vector rightMultiply(Vector vector) {
        throw new RuntimeException("Not implemented.");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.numRows);
        sb.append(" x ");
        sb.append(this.numColumns);
        sb.append(" Matrix over ");
        sb.append(this.field.toString());
        sb.append(": \n");
        String sb2 = sb.toString();
        for (int i = 0; i < this.numRows; i++) {
            String str = sb2;
            for (int i2 = 0; i2 < this.numColumns; i2++) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(str);
                sb3.append(this.field.elementToStr(this.matrix[i][i2]));
                sb3.append(" : ");
                str = sb3.toString();
            }
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str);
            sb4.append("\n");
            sb2 = sb4.toString();
        }
        return sb2;
    }
}
