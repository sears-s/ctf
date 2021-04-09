package org.jivesoftware.smack.util.stringencoder;

import org.jivesoftware.smack.util.Objects;

public class Base64UrlSafeEncoder {
    private static StringEncoder base64UrlSafeEncoder;

    public static void setEncoder(StringEncoder encoder) {
        Objects.requireNonNull(encoder, "encoder must no be null");
        base64UrlSafeEncoder = encoder;
    }

    public static StringEncoder getStringEncoder() {
        return base64UrlSafeEncoder;
    }

    public static final String encode(String string) {
        return base64UrlSafeEncoder.encode(string);
    }

    public static final String decode(String string) {
        return base64UrlSafeEncoder.decode(string);
    }
}
