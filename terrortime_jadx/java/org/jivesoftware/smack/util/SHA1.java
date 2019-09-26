package org.jivesoftware.smack.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1 {
    private static MessageDigest SHA1_DIGEST;

    static {
        try {
            SHA1_DIGEST = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static synchronized byte[] bytes(byte[] bytes) {
        byte[] digest;
        synchronized (SHA1.class) {
            SHA1_DIGEST.update(bytes);
            digest = SHA1_DIGEST.digest();
        }
        return digest;
    }

    public static byte[] bytes(String string) {
        return bytes(StringUtils.toUtf8Bytes(string));
    }

    public static String hex(byte[] bytes) {
        return StringUtils.encodeHex(bytes(bytes));
    }

    public static String hex(String string) {
        return hex(StringUtils.toUtf8Bytes(string));
    }
}
