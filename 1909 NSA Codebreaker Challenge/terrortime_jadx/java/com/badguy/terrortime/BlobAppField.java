package com.badguy.terrortime;

import java.util.Arrays;

public class BlobAppField extends AppField {
    private static byte[] DEFAULTVALUE = null;
    private byte[] value = null;

    public BlobAppField() {
        super(AppFieldTypes.BIN);
    }

    public BlobAppField(byte[] blobBytes) {
        super(AppFieldTypes.BIN);
        setValue(blobBytes);
    }

    public BlobAppField(BlobAppField bField) {
        super(AppFieldTypes.BIN);
        setValue(bField.getValue());
    }

    public final void setValue(byte[] newValue) {
        if (newValue == null || newValue == DEFAULTVALUE) {
            this.value = DEFAULTVALUE;
        } else {
            this.value = Arrays.copyOf(newValue, newValue.length);
        }
    }

    public final byte[] getValue() {
        byte[] bArr = this.value;
        if (bArr == null) {
            return null;
        }
        byte[] bArr2 = DEFAULTVALUE;
        if (bArr == bArr2) {
            return bArr2;
        }
        return Arrays.copyOf(bArr, bArr.length);
    }

    public final boolean isValid() {
        return true;
    }

    public final void setValueToDefault() {
        this.value = DEFAULTVALUE;
    }

    public final String typeAsString() {
        return "BLOB";
    }

    public final boolean isDefaultValue() {
        if (this.value == DEFAULTVALUE) {
            return true;
        }
        return false;
    }

    public final String toString() {
        if (this.value == null) {
            return "null";
        }
        return Arrays.toString(getValue());
    }

    public final boolean equals(Object o) {
        Class<BlobAppField> cls = BlobAppField.class;
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !isValid() || isDefaultValue() != ((BlobAppField) cls.cast(o)).isDefaultValue()) {
            return false;
        }
        return Arrays.equals(this.value, ((BlobAppField) cls.cast(o)).getValue());
    }
}
