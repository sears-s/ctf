package org.jivesoftware.smack.util.stringencoder;

import java.io.UnsupportedEncodingException;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;

public class Base64 {
    private static Encoder base64encoder;

    public interface Encoder {
        byte[] decode(String str);

        byte[] decode(byte[] bArr, int i, int i2);

        byte[] encode(byte[] bArr, int i, int i2);

        String encodeToString(byte[] bArr, int i, int i2);
    }

    public static void setEncoder(Encoder encoder) {
        Objects.requireNonNull(encoder, "encoder must no be null");
        base64encoder = encoder;
    }

    public static final String encode(String string) {
        try {
            return encodeToString(string.getBytes(StringUtils.UTF8));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported", e);
        }
    }

    public static final String encodeToString(byte[] input) {
        try {
            return new String(encode(input), StringUtils.USASCII);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static final String encodeToString(byte[] input, int offset, int len) {
        try {
            return new String(encode(input, offset, len), StringUtils.USASCII);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static final byte[] encode(byte[] input) {
        return encode(input, 0, input.length);
    }

    public static final byte[] encode(byte[] input, int offset, int len) {
        return base64encoder.encode(input, offset, len);
    }

    public static final String decodeToString(String string) {
        try {
            return new String(decode(string), StringUtils.UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported", e);
        }
    }

    public static final String decodeToString(byte[] input, int offset, int len) {
        try {
            return new String(decode(input, offset, len), StringUtils.UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported", e);
        }
    }

    public static final byte[] decode(String string) {
        return base64encoder.decode(string);
    }

    public static final byte[] decode(byte[] input) {
        return base64encoder.decode(input, 0, input.length);
    }

    public static final byte[] decode(byte[] input, int offset, int len) {
        return base64encoder.decode(input, offset, len);
    }
}
