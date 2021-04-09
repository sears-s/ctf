package org.jivesoftware.smackx.commands;

public class AdHocCommandNote {
    private final Type type;
    private final String value;

    public enum Type {
        info,
        warn,
        error
    }

    public AdHocCommandNote(Type type2, String value2) {
        this.type = type2;
        this.value = value2;
    }

    public String getValue() {
        return this.value;
    }

    public Type getType() {
        return this.type;
    }
}
