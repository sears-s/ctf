package org.jivesoftware.smackx.muc;

import java.util.Locale;

public enum MUCAffiliation {
    owner,
    admin,
    member,
    outcast,
    none;

    public static MUCAffiliation fromString(String string) {
        if (string == null) {
            return null;
        }
        return valueOf(string.toLowerCase(Locale.US));
    }
}
