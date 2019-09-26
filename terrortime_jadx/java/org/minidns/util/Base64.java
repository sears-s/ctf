package org.minidns.util;

public final class Base64 {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final String PADDING = "==";

    private Base64() {
    }

    public static String encodeToString(byte[] bytes) {
        int paddingCount = (3 - (bytes.length % 3)) % 3;
        byte[] padded = new byte[(bytes.length + paddingCount)];
        System.arraycopy(bytes, 0, padded, 0, bytes.length);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i += 3) {
            int j = ((padded[i] & 255) << Tnaf.POW_2_WIDTH) + ((padded[i + 1] & 255) << 8) + (padded[i + 2] & 255);
            int i2 = (j >> 18) & 63;
            String str = ALPHABET;
            sb.append(str.charAt(i2));
            sb.append(str.charAt((j >> 12) & 63));
            sb.append(str.charAt((j >> 6) & 63));
            sb.append(str.charAt(j & 63));
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(sb.substring(0, sb.length() - paddingCount));
        sb2.append(PADDING.substring(0, paddingCount));
        return sb2.toString();
    }
}
