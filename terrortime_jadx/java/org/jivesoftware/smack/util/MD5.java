package org.jivesoftware.smack.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private static MessageDigest MD5_DIGEST;

    static {
        try {
            MD5_DIGEST = MessageDigest.getInstance(StringUtils.MD5);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static synchronized byte[] bytes(byte[] bytes) {
        byte[] digest;
        synchronized (MD5.class) {
            digest = MD5_DIGEST.digest(bytes);
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
