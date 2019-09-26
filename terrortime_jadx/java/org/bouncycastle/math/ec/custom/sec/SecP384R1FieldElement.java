package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECFieldElement.AbstractFp;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Arrays;

public class SecP384R1FieldElement extends AbstractFp {
    public static final BigInteger Q = SecP384R1Curve.q;
    protected int[] x;

    public SecP384R1FieldElement() {
        this.x = Nat.create(12);
    }

    public SecP384R1FieldElement(BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP384R1FieldElement");
        }
        this.x = SecP384R1Field.fromBigInteger(bigInteger);
    }

    protected SecP384R1FieldElement(int[] iArr) {
        this.x = iArr;
    }

    public ECFieldElement add(ECFieldElement eCFieldElement) {
        int[] create = Nat.create(12);
        SecP384R1Field.add(this.x, ((SecP384R1FieldElement) eCFieldElement).x, create);
        return new SecP384R1FieldElement(create);
    }

    public ECFieldElement addOne() {
        int[] create = Nat.create(12);
        SecP384R1Field.addOne(this.x, create);
        return new SecP384R1FieldElement(create);
    }

    public ECFieldElement divide(ECFieldElement eCFieldElement) {
        int[] create = Nat.create(12);
        Mod.invert(SecP384R1Field.P, ((SecP384R1FieldElement) eCFieldElement).x, create);
        SecP384R1Field.multiply(create, this.x, create);
        return new SecP384R1FieldElement(create);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SecP384R1FieldElement)) {
            return false;
        }
        return Nat.eq(12, this.x, ((SecP384R1FieldElement) obj).x);
    }

    public String getFieldName() {
        return "SecP384R1Field";
    }

    public int getFieldSize() {
        return Q.bitLength();
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 12);
    }

    public ECFieldElement invert() {
        int[] create = Nat.create(12);
        Mod.invert(SecP384R1Field.P, this.x, create);
        return new SecP384R1FieldElement(create);
    }

    public boolean isOne() {
        return Nat.isOne(12, this.x);
    }

    public boolean isZero() {
        return Nat.isZero(12, this.x);
    }

    public ECFieldElement multiply(ECFieldElement eCFieldElement) {
        int[] create = Nat.create(12);
        SecP384R1Field.multiply(this.x, ((SecP384R1FieldElement) eCFieldElement).x, create);
        return new SecP384R1FieldElement(create);
    }

    public ECFieldElement negate() {
        int[] create = Nat.create(12);
        SecP384R1Field.negate(this.x, create);
        return new SecP384R1FieldElement(create);
    }

    public ECFieldElement sqrt() {
        int[] iArr = this.x;
        if (Nat.isZero(12, iArr) || Nat.isOne(12, iArr)) {
            return this;
        }
        int[] create = Nat.create(12);
        int[] create2 = Nat.create(12);
        int[] create3 = Nat.create(12);
        int[] create4 = Nat.create(12);
        SecP384R1Field.square(iArr, create);
        SecP384R1Field.multiply(create, iArr, create);
        SecP384R1Field.squareN(create, 2, create2);
        SecP384R1Field.multiply(create2, create, create2);
        SecP384R1Field.square(create2, create2);
        SecP384R1Field.multiply(create2, iArr, create2);
        SecP384R1Field.squareN(create2, 5, create3);
        SecP384R1Field.multiply(create3, create2, create3);
        SecP384R1Field.squareN(create3, 5, create4);
        SecP384R1Field.multiply(create4, create2, create4);
        SecP384R1Field.squareN(create4, 15, create2);
        SecP384R1Field.multiply(create2, create4, create2);
        SecP384R1Field.squareN(create2, 2, create3);
        SecP384R1Field.multiply(create, create3, create);
        SecP384R1Field.squareN(create3, 28, create3);
        SecP384R1Field.multiply(create2, create3, create2);
        SecP384R1Field.squareN(create2, 60, create3);
        SecP384R1Field.multiply(create3, create2, create3);
        SecP384R1Field.squareN(create3, 120, create2);
        SecP384R1Field.multiply(create2, create3, create2);
        SecP384R1Field.squareN(create2, 15, create2);
        SecP384R1Field.multiply(create2, create4, create2);
        SecP384R1Field.squareN(create2, 33, create2);
        SecP384R1Field.multiply(create2, create, create2);
        SecP384R1Field.squareN(create2, 64, create2);
        SecP384R1Field.multiply(create2, iArr, create2);
        SecP384R1Field.squareN(create2, 30, create);
        SecP384R1Field.square(create, create2);
        return Nat.eq(12, iArr, create2) ? new SecP384R1FieldElement(create) : null;
    }

    public ECFieldElement square() {
        int[] create = Nat.create(12);
        SecP384R1Field.square(this.x, create);
        return new SecP384R1FieldElement(create);
    }

    public ECFieldElement subtract(ECFieldElement eCFieldElement) {
        int[] create = Nat.create(12);
        SecP384R1Field.subtract(this.x, ((SecP384R1FieldElement) eCFieldElement).x, create);
        return new SecP384R1FieldElement(create);
    }

    public boolean testBitZero() {
        return Nat.getBit(this.x, 0) == 1;
    }

    public BigInteger toBigInteger() {
        return Nat.toBigInteger(12, this.x);
    }
}
