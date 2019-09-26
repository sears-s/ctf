package org.jivesoftware.smack.util.stringencoder.android;

import android.util.Base64;
import java.io.UnsupportedEncodingException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.stringencoder.StringEncoder;

public final class AndroidBase64UrlSafeEncoder implements StringEncoder {
    private static final int BASE64_ENCODER_FLAGS = 10;
    private static AndroidBase64UrlSafeEncoder instance = new AndroidBase64UrlSafeEncoder();

    private AndroidBase64UrlSafeEncoder() {
    }

    public static AndroidBase64UrlSafeEncoder getInstance() {
        return instance;
    }

    public String encode(String string) {
        try {
            return Base64.encodeToString(string.getBytes(StringUtils.UTF8), 10);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported", e);
        }
    }

    public String decode(String string) {
        try {
            return new String(Base64.decode(string, 10), StringUtils.UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported", e);
        }
    }
}
