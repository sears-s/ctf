package com.badguy.terrortime;

public class IntAppField extends AppField {
    private static Integer DEFAULTVALUE = Integer.valueOf(0);
    private Integer value;

    public IntAppField() {
        super(AppFieldTypes.INT);
        setValueToDefault();
    }

    public IntAppField(Integer i) {
        super(AppFieldTypes.INT);
        setValue(Integer.valueOf(i.intValue()));
    }

    public IntAppField(IntAppField iField) {
        super(AppFieldTypes.INT);
        if (iField != null) {
            this.value = Integer.valueOf(iField.getValue().intValue());
        } else {
            this.value = DEFAULTVALUE;
        }
    }

    public final void setValue(Integer newValue) {
        if (newValue != null) {
            this.value = new Integer(newValue.intValue());
        } else {
            this.value = DEFAULTVALUE;
        }
    }

    public final Integer getValue() {
        if (this.value == null) {
            return null;
        }
        if (isDefaultValue()) {
            return DEFAULTVALUE;
        }
        return this.value;
    }

    public final boolean isValid() {
        if (this.value != null) {
            return true;
        }
        return false;
    }

    public final void setValueToDefault() {
        this.value = DEFAULTVALUE;
    }

    public final String typeAsString() {
        return "INTEGER";
    }

    public final boolean isDefaultValue() {
        if (!isValid() || !this.value.equals(DEFAULTVALUE)) {
            return false;
        }
        return true;
    }

    public final String toString() {
        return getValue().toString();
    }

    public final boolean equals(Object o) {
        Class<IntAppField> cls = IntAppField.class;
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !isValid() || ((IntAppField) cls.cast(o)).isDefaultValue() != isDefaultValue()) {
            return false;
        }
        return this.value.equals(((IntAppField) cls.cast(o)).getValue());
    }
}
