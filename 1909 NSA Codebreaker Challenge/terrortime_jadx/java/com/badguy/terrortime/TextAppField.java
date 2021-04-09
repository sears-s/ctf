package com.badguy.terrortime;

public class TextAppField extends AppField {
    private static String DEFAULTVALUE = BuildConfig.FLAVOR;
    private String value;

    public TextAppField() {
        super(AppFieldTypes.TEXT);
        setValueToDefault();
    }

    public TextAppField(String newValue) {
        super(AppFieldTypes.TEXT);
        if (newValue != null) {
            this.value = newValue;
        } else {
            this.value = DEFAULTVALUE;
        }
    }

    public final void setValue(String newValue) {
        if (newValue != null) {
            this.value = newValue;
        } else {
            this.value = DEFAULTVALUE;
        }
    }

    public final String getValue() {
        if (this.value == null) {
            return null;
        }
        if (isDefaultValue()) {
            return DEFAULTVALUE;
        }
        return String.valueOf(this.value);
    }

    public final boolean isValid() {
        if (this.value != null) {
            return true;
        }
        return false;
    }

    public final void setValueToDefault() {
        this.value = BuildConfig.FLAVOR;
    }

    public final String typeAsString() {
        return "TEXT";
    }

    public final boolean isDefaultValue() {
        if (!isValid() || !this.value.equals(DEFAULTVALUE)) {
            return false;
        }
        return true;
    }

    public final String toString() {
        return getValue();
    }

    public final boolean equals(Object o) {
        Class<TextAppField> cls = TextAppField.class;
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !isValid() || ((TextAppField) cls.cast(o)).isDefaultValue() != isDefaultValue()) {
            return false;
        }
        return this.value.equals(((TextAppField) cls.cast(o)).getValue());
    }
}
