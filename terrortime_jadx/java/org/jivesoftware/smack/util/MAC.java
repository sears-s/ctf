package org.jivesoftware.smack.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MAC {
    public static final String HMACSHA1 = "HmacSHA1";
    private static Mac HMAC_SHA1;

    static {
        try {
            HMAC_SHA1 = Mac.getInstance(HMACSHA1);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static synchronized byte[] hmacsha1(SecretKeySpec key, byte[] input) throws InvalidKeyException {
        byte[] doFinal;
        synchronized (MAC.class) {
            HMAC_SHA1.init(key);
            doFinal = HMAC_SHA1.doFinal(input);
        }
        return doFinal;
    }

    public static byte[] hmacsha1(byte[] keyBytes, byte[] input) throws InvalidKeyException {
        return hmacsha1(new SecretKeySpec(keyBytes, HMACSHA1), input);
    }
}
