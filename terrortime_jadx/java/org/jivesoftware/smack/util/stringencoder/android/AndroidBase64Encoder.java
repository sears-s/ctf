package org.jivesoftware.smack.util.stringencoder.android;

import android.util.Base64;
import org.jivesoftware.smack.util.stringencoder.Base64.Encoder;

public final class AndroidBase64Encoder implements Encoder {
    private static final int BASE64_ENCODER_FLAGS = 2;
    private static AndroidBase64Encoder instance = new AndroidBase64Encoder();

    private AndroidBase64Encoder() {
    }

    public static AndroidBase64Encoder getInstance() {
        return instance;
    }

    public byte[] decode(String string) {
        return Base64.decode(string, 0);
    }

    public byte[] decode(byte[] input, int offset, int len) {
        return Base64.decode(input, offset, len, 0);
    }

    public String encodeToString(byte[] input, int offset, int len) {
        return Base64.encodeToString(input, offset, len, 2);
    }

    public byte[] encode(byte[] input, int offset, int len) {
        return Base64.encode(input, offset, len, 2);
    }
}
