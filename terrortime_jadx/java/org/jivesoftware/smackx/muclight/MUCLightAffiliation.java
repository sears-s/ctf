package org.jivesoftware.smackx.muclight;

import java.util.Locale;

public enum MUCLightAffiliation {
    owner,
    member,
    none;

    public static MUCLightAffiliation fromString(String string) {
        if (string == null) {
            return null;
        }
        return valueOf(string.toLowerCase(Locale.US));
    }
}
