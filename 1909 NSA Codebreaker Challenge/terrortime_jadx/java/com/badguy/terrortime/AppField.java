package com.badguy.terrortime;

abstract class AppField {
    private AppFieldTypes type;

    public abstract boolean equals(Object obj);

    public abstract Object getValue();

    public abstract boolean isDefaultValue();

    public abstract boolean isValid();

    public abstract void setValueToDefault();

    public abstract String toString();

    public abstract String typeAsString();

    public AppField(AppFieldTypes aType) {
        this.type = aType;
    }
}
