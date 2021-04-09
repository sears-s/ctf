package org.bouncycastle.math.ec;

import java.math.BigInteger;
import java.util.Hashtable;
import java.util.Random;
import org.bouncycastle.math.ec.endo.ECEndomorphism;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.FiniteFields;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Integers;

public abstract class ECCurve {
    public static final int COORD_AFFINE = 0;
    public static final int COORD_HOMOGENEOUS = 1;
    public static final int COORD_JACOBIAN = 2;
    public static final int COORD_JACOBIAN_CHUDNOVSKY = 3;
    public static final int COORD_JACOBIAN_MODIFIED = 4;
    public static final int COORD_LAMBDA_AFFINE = 5;
    public static final int COORD_LAMBDA_PROJECTIVE = 6;
    public static final int COORD_SKEWED = 7;
    protected ECFieldElement a;
    protected ECFieldElement b;
    protected BigInteger cofactor;
    protected int coord = 0;
    protected ECEndomorphism endomorphism = null;
    protected FiniteField field;
    protected ECMultiplier multiplier = null;
    protected BigInteger order;

    public static abstract class AbstractF2m extends ECCurve {
        private BigInteger[] si = null;

        protected AbstractF2m(int i, int i2, int i3, int i4) {
            super(buildField(i, i2, i3, i4));
        }

        private static FiniteField buildField(int i, int i2, int i3, int i4) {
            if (i2 == 0) {
                throw new IllegalArgumentException("k1 must be > 0");
            } else if (i3 == 0) {
                if (i4 == 0) {
                    return FiniteFields.getBinaryExtensionField(new int[]{0, i2, i});
                }
                throw new IllegalArgumentException("k3 must be 0 if k2 == 0");
            } else if (i3 <= i2) {
                throw new IllegalArgumentException("k2 must be > k1");
            } else if (i4 > i3) {
                return FiniteFields.getBinaryExtensionField(new int[]{0, i2, i3, i4, i});
            } else {
                throw new IllegalArgumentException("k3 must be > k2");
            }
        }

        public static BigInteger inverse(int i, int[] iArr, BigInteger bigInteger) {
            return new LongArray(bigInteger).modInverse(i, iArr).toBigInteger();
        }

        public ECPoint createPoint(BigInteger bigInteger, BigInteger bigInteger2, boolean z) {
            ECFieldElement fromBigInteger = fromBigInteger(bigInteger);
            ECFieldElement fromBigInteger2 = fromBigInteger(bigInteger2);
            int coordinateSystem = getCoordinateSystem();
            if (coordinateSystem == 5 || coordinateSystem == 6) {
                if (!fromBigInteger.isZero()) {
                    fromBigInteger2 = fromBigInteger2.divide(fromBigInteger).add(fromBigInteger);
                } else if (!fromBigInteger2.square().equals(getB())) {
                    throw new IllegalArgumentException();
                }
            }
            return createRawPoint(fromBigInteger, fromBigInteger2, z);
        }

        /* access modifiers changed from: protected */
        public ECPoint decompressPoint(int i, BigInteger bigInteger) {
            ECFieldElement eCFieldElement;
            ECFieldElement fromBigInteger = fromBigInteger(bigInteger);
            if (fromBigInteger.isZero()) {
                eCFieldElement = getB().sqrt();
            } else {
                ECFieldElement solveQuadraticEquation = solveQuadraticEquation(fromBigInteger.square().invert().multiply(getB()).add(getA()).add(fromBigInteger));
                if (solveQuadraticEquation != null) {
                    if (solveQuadraticEquation.testBitZero() != (i == 1)) {
                        solveQuadraticEquation = solveQuadraticEquation.addOne();
                    }
                    int coordinateSystem = getCoordinateSystem();
                    eCFieldElement = (coordinateSystem == 5 || coordinateSystem == 6) ? solveQuadraticEquation.add(fromBigInteger) : solveQuadraticEquation.multiply(fromBigInteger);
                } else {
                    eCFieldElement = null;
                }
            }
            if (eCFieldElement != null) {
                return createRawPoint(fromBigInteger, eCFieldElement, true);
            }
            throw new IllegalArgumentException("Invalid point compression");
        }

        /* access modifiers changed from: 0000 */
        public synchronized BigInteger[] getSi() {
            if (this.si == null) {
                this.si = Tnaf.getSi(this);
            }
            return this.si;
        }

        public boolean isKoblitz() {
            return this.order != null && this.cofactor != null && this.b.isOne() && (this.a.isZero() || this.a.isOne());
        }

        public boolean isValidFieldElement(BigInteger bigInteger) {
            return bigInteger != null && bigInteger.signum() >= 0 && bigInteger.bitLength() <= getFieldSize();
        }

        /* access modifiers changed from: protected */
        public ECFieldElement solveQuadraticEquation(ECFieldElement eCFieldElement) {
            ECFieldElement eCFieldElement2;
            if (eCFieldElement.isZero()) {
                return eCFieldElement;
            }
            ECFieldElement fromBigInteger = fromBigInteger(ECConstants.ZERO);
            int fieldSize = getFieldSize();
            Random random = new Random();
            do {
                ECFieldElement fromBigInteger2 = fromBigInteger(new BigInteger(fieldSize, random));
                ECFieldElement eCFieldElement3 = eCFieldElement;
                eCFieldElement2 = fromBigInteger;
                for (int i = 1; i < fieldSize; i++) {
                    ECFieldElement square = eCFieldElement3.square();
                    eCFieldElement2 = eCFieldElement2.square().add(square.multiply(fromBigInteger2));
                    eCFieldElement3 = square.add(eCFieldElement);
                }
                if (!eCFieldElement3.isZero()) {
                    return null;
                }
            } while (eCFieldElement2.square().add(eCFieldElement2).isZero());
            return eCFieldElement2;
        }
    }

    public static abstract class AbstractFp extends ECCurve {
        protected AbstractFp(BigInteger bigInteger) {
            super(FiniteFields.getPrimeField(bigInteger));
        }

        /* access modifiers changed from: protected */
        public ECPoint decompressPoint(int i, BigInteger bigInteger) {
            ECFieldElement fromBigInteger = fromBigInteger(bigInteger);
            ECFieldElement sqrt = fromBigInteger.square().add(this.a).multiply(fromBigInteger).add(this.b).sqrt();
            if (sqrt != null) {
                if (sqrt.testBitZero() != (i == 1)) {
                    sqrt = sqrt.negate();
                }
                return createRawPoint(fromBigInteger, sqrt, true);
            }
            throw new IllegalArgumentException("Invalid point compression");
        }

        public boolean isValidFieldElement(BigInteger bigInteger) {
            return bigInteger != null && bigInteger.signum() >= 0 && bigInteger.compareTo(getField().getCharacteristic()) < 0;
        }
    }

    public class Config {
        protected int coord;
        protected ECEndomorphism endomorphism;
        protected ECMultiplier multiplier;

        Config(int i, ECEndomorphism eCEndomorphism, ECMultiplier eCMultiplier) {
            this.coord = i;
            this.endomorphism = eCEndomorphism;
            this.multiplier = eCMultiplier;
        }

        public ECCurve create() {
            if (ECCurve.this.supportsCoordinateSystem(this.coord)) {
                ECCurve cloneCurve = ECCurve.this.cloneCurve();
                if (cloneCurve != ECCurve.this) {
                    synchronized (cloneCurve) {
                        cloneCurve.coord = this.coord;
                        cloneCurve.endomorphism = this.endomorphism;
                        cloneCurve.multiplier = this.multiplier;
                    }
                    return cloneCurve;
                }
                throw new IllegalStateException("implementation returned current curve");
            }
            throw new IllegalStateException("unsupported coordinate system");
        }

        public Config setCoordinateSystem(int i) {
            this.coord = i;
            return this;
        }

        public Config setEndomorphism(ECEndomorphism eCEndomorphism) {
            this.endomorphism = eCEndomorphism;
            return this;
        }

        public Config setMultiplier(ECMultiplier eCMultiplier) {
            this.multiplier = eCMultiplier;
            return this;
        }
    }

    public static class F2m extends AbstractF2m {
        private static final int F2M_DEFAULT_COORDS = 6;
        private org.bouncycastle.math.ec.ECPoint.F2m infinity;
        private int k1;
        private int k2;
        private int k3;
        /* access modifiers changed from: private */
        public int m;

        public F2m(int i, int i2, int i3, int i4, BigInteger bigInteger, BigInteger bigInteger2) {
            this(i, i2, i3, i4, bigInteger, bigInteger2, (BigInteger) null, (BigInteger) null);
        }

        public F2m(int i, int i2, int i3, int i4, BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4) {
            super(i, i2, i3, i4);
            this.m = i;
            this.k1 = i2;
            this.k2 = i3;
            this.k3 = i4;
            this.order = bigInteger3;
            this.cofactor = bigInteger4;
            this.infinity = new org.bouncycastle.math.ec.ECPoint.F2m(this, null, null, false);
            this.a = fromBigInteger(bigInteger);
            this.b = fromBigInteger(bigInteger2);
            this.coord = 6;
        }

        protected F2m(int i, int i2, int i3, int i4, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, BigInteger bigInteger, BigInteger bigInteger2) {
            super(i, i2, i3, i4);
            this.m = i;
            this.k1 = i2;
            this.k2 = i3;
            this.k3 = i4;
            this.order = bigInteger;
            this.cofactor = bigInteger2;
            this.infinity = new org.bouncycastle.math.ec.ECPoint.F2m(this, null, null, false);
            this.a = eCFieldElement;
            this.b = eCFieldElement2;
            this.coord = 6;
        }

        public F2m(int i, int i2, BigInteger bigInteger, BigInteger bigInteger2) {
            this(i, i2, 0, 0, bigInteger, bigInteger2, (BigInteger) null, (BigInteger) null);
        }

        public F2m(int i, int i2, BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4) {
            this(i, i2, 0, 0, bigInteger, bigInteger2, bigInteger3, bigInteger4);
        }

        /* access modifiers changed from: protected */
        public ECCurve cloneCurve() {
            F2m f2m = new F2m(this.m, this.k1, this.k2, this.k3, this.a, this.b, this.order, this.cofactor);
            return f2m;
        }

        public ECLookupTable createCacheSafeLookupTable(ECPoint[] eCPointArr, int i, int i2) {
            final int i3 = (this.m + 63) >>> 6;
            final int[] iArr = isTrinomial() ? new int[]{this.k1} : new int[]{this.k1, this.k2, this.k3};
            final long[] jArr = new long[(i2 * i3 * 2)];
            int i4 = 0;
            for (int i5 = 0; i5 < i2; i5++) {
                ECPoint eCPoint = eCPointArr[i + i5];
                ((org.bouncycastle.math.ec.ECFieldElement.F2m) eCPoint.getRawXCoord()).x.copyTo(jArr, i4);
                int i6 = i4 + i3;
                ((org.bouncycastle.math.ec.ECFieldElement.F2m) eCPoint.getRawYCoord()).x.copyTo(jArr, i6);
                i4 = i6 + i3;
            }
            final int i7 = i2;
            AnonymousClass1 r1 = new ECLookupTable() {
                public int getSize() {
                    return i7;
                }

                public ECPoint lookup(int i) {
                    int i2;
                    long[] create64 = Nat.create64(i3);
                    long[] create642 = Nat.create64(i3);
                    int i3 = 0;
                    for (int i4 = 0; i4 < i7; i4++) {
                        long j = (long) (((i4 ^ i) - 1) >> 31);
                        int i5 = 0;
                        while (true) {
                            i2 = i3;
                            if (i5 >= i2) {
                                break;
                            }
                            long j2 = create64[i5];
                            long[] jArr = jArr;
                            create64[i5] = j2 ^ (jArr[i3 + i5] & j);
                            create642[i5] = create642[i5] ^ (jArr[(i2 + i3) + i5] & j);
                            i5++;
                        }
                        i3 += i2 * 2;
                    }
                    F2m f2m = F2m.this;
                    return f2m.createRawPoint(new org.bouncycastle.math.ec.ECFieldElement.F2m(f2m.m, iArr, new LongArray(create64)), new org.bouncycastle.math.ec.ECFieldElement.F2m(F2m.this.m, iArr, new LongArray(create642)), false);
                }
            };
            return r1;
        }

        /* access modifiers changed from: protected */
        public ECMultiplier createDefaultMultiplier() {
            return isKoblitz() ? new WTauNafMultiplier() : super.createDefaultMultiplier();
        }

        /* access modifiers changed from: protected */
        public ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, boolean z) {
            return new org.bouncycastle.math.ec.ECPoint.F2m(this, eCFieldElement, eCFieldElement2, z);
        }

        /* access modifiers changed from: protected */
        public ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr, boolean z) {
            org.bouncycastle.math.ec.ECPoint.F2m f2m = new org.bouncycastle.math.ec.ECPoint.F2m(this, eCFieldElement, eCFieldElement2, eCFieldElementArr, z);
            return f2m;
        }

        public ECFieldElement fromBigInteger(BigInteger bigInteger) {
            org.bouncycastle.math.ec.ECFieldElement.F2m f2m = new org.bouncycastle.math.ec.ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, bigInteger);
            return f2m;
        }

        public int getFieldSize() {
            return this.m;
        }

        public ECPoint getInfinity() {
            return this.infinity;
        }

        public int getK1() {
            return this.k1;
        }

        public int getK2() {
            return this.k2;
        }

        public int getK3() {
            return this.k3;
        }

        public int getM() {
            return this.m;
        }

        public boolean isTrinomial() {
            return this.k2 == 0 && this.k3 == 0;
        }

        public boolean supportsCoordinateSystem(int i) {
            return i == 0 || i == 1 || i == 6;
        }
    }

    public static class Fp extends AbstractFp {
        private static final int FP_DEFAULT_COORDS = 4;
        org.bouncycastle.math.ec.ECPoint.Fp infinity;
        BigInteger q;
        BigInteger r;

        public Fp(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3) {
            this(bigInteger, bigInteger2, bigInteger3, null, null);
        }

        public Fp(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4, BigInteger bigInteger5) {
            super(bigInteger);
            this.q = bigInteger;
            this.r = org.bouncycastle.math.ec.ECFieldElement.Fp.calculateResidue(bigInteger);
            this.infinity = new org.bouncycastle.math.ec.ECPoint.Fp(this, null, null, false);
            this.a = fromBigInteger(bigInteger2);
            this.b = fromBigInteger(bigInteger3);
            this.order = bigInteger4;
            this.cofactor = bigInteger5;
            this.coord = 4;
        }

        protected Fp(BigInteger bigInteger, BigInteger bigInteger2, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
            this(bigInteger, bigInteger2, eCFieldElement, eCFieldElement2, null, null);
        }

        protected Fp(BigInteger bigInteger, BigInteger bigInteger2, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, BigInteger bigInteger3, BigInteger bigInteger4) {
            super(bigInteger);
            this.q = bigInteger;
            this.r = bigInteger2;
            this.infinity = new org.bouncycastle.math.ec.ECPoint.Fp(this, null, null, false);
            this.a = eCFieldElement;
            this.b = eCFieldElement2;
            this.order = bigInteger3;
            this.cofactor = bigInteger4;
            this.coord = 4;
        }

        /* access modifiers changed from: protected */
        public ECCurve cloneCurve() {
            Fp fp = new Fp(this.q, this.r, this.a, this.b, this.order, this.cofactor);
            return fp;
        }

        /* access modifiers changed from: protected */
        public ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, boolean z) {
            return new org.bouncycastle.math.ec.ECPoint.Fp(this, eCFieldElement, eCFieldElement2, z);
        }

        /* access modifiers changed from: protected */
        public ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr, boolean z) {
            org.bouncycastle.math.ec.ECPoint.Fp fp = new org.bouncycastle.math.ec.ECPoint.Fp(this, eCFieldElement, eCFieldElement2, eCFieldElementArr, z);
            return fp;
        }

        public ECFieldElement fromBigInteger(BigInteger bigInteger) {
            return new org.bouncycastle.math.ec.ECFieldElement.Fp(this.q, this.r, bigInteger);
        }

        public int getFieldSize() {
            return this.q.bitLength();
        }

        public ECPoint getInfinity() {
            return this.infinity;
        }

        public BigInteger getQ() {
            return this.q;
        }

        public ECPoint importPoint(ECPoint eCPoint) {
            if (this != eCPoint.getCurve() && getCoordinateSystem() == 2 && !eCPoint.isInfinity()) {
                int coordinateSystem = eCPoint.getCurve().getCoordinateSystem();
                if (coordinateSystem == 2 || coordinateSystem == 3 || coordinateSystem == 4) {
                    org.bouncycastle.math.ec.ECPoint.Fp fp = new org.bouncycastle.math.ec.ECPoint.Fp(this, fromBigInteger(eCPoint.x.toBigInteger()), fromBigInteger(eCPoint.y.toBigInteger()), new ECFieldElement[]{fromBigInteger(eCPoint.zs[0].toBigInteger())}, eCPoint.withCompression);
                    return fp;
                }
            }
            return super.importPoint(eCPoint);
        }

        public boolean supportsCoordinateSystem(int i) {
            return i == 0 || i == 1 || i == 2 || i == 4;
        }
    }

    protected ECCurve(FiniteField finiteField) {
        this.field = finiteField;
    }

    public static int[] getAllCoordinateSystems() {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    }

    /* access modifiers changed from: protected */
    public void checkPoint(ECPoint eCPoint) {
        if (eCPoint == null || this != eCPoint.getCurve()) {
            throw new IllegalArgumentException("'point' must be non-null and on this curve");
        }
    }

    /* access modifiers changed from: protected */
    public void checkPoints(ECPoint[] eCPointArr) {
        checkPoints(eCPointArr, 0, eCPointArr.length);
    }

    /* access modifiers changed from: protected */
    public void checkPoints(ECPoint[] eCPointArr, int i, int i2) {
        if (eCPointArr == null) {
            throw new IllegalArgumentException("'points' cannot be null");
        } else if (i < 0 || i2 < 0 || i > eCPointArr.length - i2) {
            throw new IllegalArgumentException("invalid range specified for 'points'");
        } else {
            int i3 = 0;
            while (i3 < i2) {
                ECPoint eCPoint = eCPointArr[i + i3];
                if (eCPoint == null || this == eCPoint.getCurve()) {
                    i3++;
                } else {
                    throw new IllegalArgumentException("'points' entries must be null or on this curve");
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public abstract ECCurve cloneCurve();

    public synchronized Config configure() {
        return new Config(this.coord, this.endomorphism, this.multiplier);
    }

    public ECLookupTable createCacheSafeLookupTable(ECPoint[] eCPointArr, int i, final int i2) {
        final int fieldSize = (getFieldSize() + 7) >>> 3;
        final byte[] bArr = new byte[(i2 * fieldSize * 2)];
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            ECPoint eCPoint = eCPointArr[i + i4];
            byte[] byteArray = eCPoint.getRawXCoord().toBigInteger().toByteArray();
            byte[] byteArray2 = eCPoint.getRawYCoord().toBigInteger().toByteArray();
            int i5 = 1;
            int i6 = byteArray.length > fieldSize ? 1 : 0;
            int length = byteArray.length - i6;
            if (byteArray2.length <= fieldSize) {
                i5 = 0;
            }
            int length2 = byteArray2.length - i5;
            int i7 = i3 + fieldSize;
            System.arraycopy(byteArray, i6, bArr, i7 - length, length);
            i3 = i7 + fieldSize;
            System.arraycopy(byteArray2, i5, bArr, i3 - length2, length2);
        }
        return new ECLookupTable() {
            public int getSize() {
                return i2;
            }

            public ECPoint lookup(int i) {
                int i2;
                int i3 = fieldSize;
                byte[] bArr = new byte[i3];
                byte[] bArr2 = new byte[i3];
                int i4 = 0;
                for (int i5 = 0; i5 < i2; i5++) {
                    int i6 = ((i5 ^ i) - 1) >> 31;
                    int i7 = 0;
                    while (true) {
                        i2 = fieldSize;
                        if (i7 >= i2) {
                            break;
                        }
                        byte b = bArr[i7];
                        byte[] bArr3 = bArr;
                        bArr[i7] = (byte) (b ^ (bArr3[i4 + i7] & i6));
                        bArr2[i7] = (byte) ((bArr3[(i2 + i4) + i7] & i6) ^ bArr2[i7]);
                        i7++;
                    }
                    i4 += i2 * 2;
                }
                ECCurve eCCurve = ECCurve.this;
                return eCCurve.createRawPoint(eCCurve.fromBigInteger(new BigInteger(1, bArr)), ECCurve.this.fromBigInteger(new BigInteger(1, bArr2)), false);
            }
        };
    }

    /* access modifiers changed from: protected */
    public ECMultiplier createDefaultMultiplier() {
        ECEndomorphism eCEndomorphism = this.endomorphism;
        return eCEndomorphism instanceof GLVEndomorphism ? new GLVMultiplier(this, (GLVEndomorphism) eCEndomorphism) : new WNafL2RMultiplier();
    }

    public ECPoint createPoint(BigInteger bigInteger, BigInteger bigInteger2) {
        return createPoint(bigInteger, bigInteger2, false);
    }

    public ECPoint createPoint(BigInteger bigInteger, BigInteger bigInteger2, boolean z) {
        return createRawPoint(fromBigInteger(bigInteger), fromBigInteger(bigInteger2), z);
    }

    /* access modifiers changed from: protected */
    public abstract ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, boolean z);

    /* access modifiers changed from: protected */
    public abstract ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr, boolean z);

    public ECPoint decodePoint(byte[] bArr) {
        ECPoint eCPoint;
        int fieldSize = (getFieldSize() + 7) / 8;
        boolean z = false;
        byte b2 = bArr[0];
        if (b2 != 0) {
            if (b2 == 2 || b2 == 3) {
                if (bArr.length == fieldSize + 1) {
                    eCPoint = decompressPoint(b2 & 1, BigIntegers.fromUnsignedByteArray(bArr, 1, fieldSize));
                    if (!eCPoint.implIsValid(true, true)) {
                        throw new IllegalArgumentException("Invalid point");
                    }
                } else {
                    throw new IllegalArgumentException("Incorrect length for compressed encoding");
                }
            } else if (b2 != 4) {
                if (b2 != 6 && b2 != 7) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invalid point encoding 0x");
                    sb.append(Integer.toString(b2, 16));
                    throw new IllegalArgumentException(sb.toString());
                } else if (bArr.length == (fieldSize * 2) + 1) {
                    BigInteger fromUnsignedByteArray = BigIntegers.fromUnsignedByteArray(bArr, 1, fieldSize);
                    BigInteger fromUnsignedByteArray2 = BigIntegers.fromUnsignedByteArray(bArr, fieldSize + 1, fieldSize);
                    boolean testBit = fromUnsignedByteArray2.testBit(0);
                    if (b2 == 7) {
                        z = true;
                    }
                    if (testBit == z) {
                        eCPoint = validatePoint(fromUnsignedByteArray, fromUnsignedByteArray2);
                    } else {
                        throw new IllegalArgumentException("Inconsistent Y coordinate in hybrid encoding");
                    }
                } else {
                    throw new IllegalArgumentException("Incorrect length for hybrid encoding");
                }
            } else if (bArr.length == (fieldSize * 2) + 1) {
                eCPoint = validatePoint(BigIntegers.fromUnsignedByteArray(bArr, 1, fieldSize), BigIntegers.fromUnsignedByteArray(bArr, fieldSize + 1, fieldSize));
            } else {
                throw new IllegalArgumentException("Incorrect length for uncompressed encoding");
            }
        } else if (bArr.length == 1) {
            eCPoint = getInfinity();
        } else {
            throw new IllegalArgumentException("Incorrect length for infinity encoding");
        }
        if (b2 == 0 || !eCPoint.isInfinity()) {
            return eCPoint;
        }
        throw new IllegalArgumentException("Invalid infinity encoding");
    }

    /* access modifiers changed from: protected */
    public abstract ECPoint decompressPoint(int i, BigInteger bigInteger);

    public boolean equals(Object obj) {
        return this == obj || ((obj instanceof ECCurve) && equals((ECCurve) obj));
    }

    public boolean equals(ECCurve eCCurve) {
        return this == eCCurve || (eCCurve != null && getField().equals(eCCurve.getField()) && getA().toBigInteger().equals(eCCurve.getA().toBigInteger()) && getB().toBigInteger().equals(eCCurve.getB().toBigInteger()));
    }

    public abstract ECFieldElement fromBigInteger(BigInteger bigInteger);

    public ECFieldElement getA() {
        return this.a;
    }

    public ECFieldElement getB() {
        return this.b;
    }

    public BigInteger getCofactor() {
        return this.cofactor;
    }

    public int getCoordinateSystem() {
        return this.coord;
    }

    public ECEndomorphism getEndomorphism() {
        return this.endomorphism;
    }

    public FiniteField getField() {
        return this.field;
    }

    public abstract int getFieldSize();

    public abstract ECPoint getInfinity();

    public synchronized ECMultiplier getMultiplier() {
        if (this.multiplier == null) {
            this.multiplier = createDefaultMultiplier();
        }
        return this.multiplier;
    }

    public BigInteger getOrder() {
        return this.order;
    }

    public PreCompInfo getPreCompInfo(ECPoint eCPoint, String str) {
        Hashtable hashtable;
        PreCompInfo preCompInfo;
        checkPoint(eCPoint);
        synchronized (eCPoint) {
            hashtable = eCPoint.preCompTable;
        }
        if (hashtable == null) {
            return null;
        }
        synchronized (hashtable) {
            preCompInfo = (PreCompInfo) hashtable.get(str);
        }
        return preCompInfo;
    }

    public int hashCode() {
        return (getField().hashCode() ^ Integers.rotateLeft(getA().toBigInteger().hashCode(), 8)) ^ Integers.rotateLeft(getB().toBigInteger().hashCode(), 16);
    }

    public ECPoint importPoint(ECPoint eCPoint) {
        if (this == eCPoint.getCurve()) {
            return eCPoint;
        }
        if (eCPoint.isInfinity()) {
            return getInfinity();
        }
        ECPoint normalize = eCPoint.normalize();
        return createPoint(normalize.getXCoord().toBigInteger(), normalize.getYCoord().toBigInteger(), normalize.withCompression);
    }

    public abstract boolean isValidFieldElement(BigInteger bigInteger);

    public void normalizeAll(ECPoint[] eCPointArr) {
        normalizeAll(eCPointArr, 0, eCPointArr.length, null);
    }

    public void normalizeAll(ECPoint[] eCPointArr, int i, int i2, ECFieldElement eCFieldElement) {
        checkPoints(eCPointArr, i, i2);
        int coordinateSystem = getCoordinateSystem();
        if (coordinateSystem != 0 && coordinateSystem != 5) {
            ECFieldElement[] eCFieldElementArr = new ECFieldElement[i2];
            int[] iArr = new int[i2];
            int i3 = 0;
            for (int i4 = 0; i4 < i2; i4++) {
                int i5 = i + i4;
                ECPoint eCPoint = eCPointArr[i5];
                if (eCPoint != null && (eCFieldElement != null || !eCPoint.isNormalized())) {
                    eCFieldElementArr[i3] = eCPoint.getZCoord(0);
                    int i6 = i3 + 1;
                    iArr[i3] = i5;
                    i3 = i6;
                }
            }
            if (i3 != 0) {
                ECAlgorithms.montgomeryTrick(eCFieldElementArr, 0, i3, eCFieldElement);
                for (int i7 = 0; i7 < i3; i7++) {
                    int i8 = iArr[i7];
                    eCPointArr[i8] = eCPointArr[i8].normalize(eCFieldElementArr[i7]);
                }
            }
        } else if (eCFieldElement != null) {
            throw new IllegalArgumentException("'iso' not valid for affine coordinates");
        }
    }

    public PreCompInfo precompute(ECPoint eCPoint, String str, PreCompCallback preCompCallback) {
        Hashtable hashtable;
        PreCompInfo precompute;
        checkPoint(eCPoint);
        synchronized (eCPoint) {
            hashtable = eCPoint.preCompTable;
            if (hashtable == null) {
                hashtable = new Hashtable(4);
                eCPoint.preCompTable = hashtable;
            }
        }
        synchronized (hashtable) {
            PreCompInfo preCompInfo = (PreCompInfo) hashtable.get(str);
            precompute = preCompCallback.precompute(preCompInfo);
            if (precompute != preCompInfo) {
                hashtable.put(str, precompute);
            }
        }
        return precompute;
    }

    public boolean supportsCoordinateSystem(int i) {
        return i == 0;
    }

    public ECPoint validatePoint(BigInteger bigInteger, BigInteger bigInteger2) {
        ECPoint createPoint = createPoint(bigInteger, bigInteger2);
        if (createPoint.isValid()) {
            return createPoint;
        }
        throw new IllegalArgumentException("Invalid point coordinates");
    }

    public ECPoint validatePoint(BigInteger bigInteger, BigInteger bigInteger2, boolean z) {
        ECPoint createPoint = createPoint(bigInteger, bigInteger2, z);
        if (createPoint.isValid()) {
            return createPoint;
        }
        throw new IllegalArgumentException("Invalid point coordinates");
    }
}
