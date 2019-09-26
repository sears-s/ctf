package org.jivesoftware.smackx.muc;

import java.util.Locale;

public enum MUCRole {
    moderator,
    none,
    participant,
    visitor;

    public static MUCRole fromString(String string) {
        if (string == null) {
            return null;
        }
        return valueOf(string.toLowerCase(Locale.US));
    }
}
