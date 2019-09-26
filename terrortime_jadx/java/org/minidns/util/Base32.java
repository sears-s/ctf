package org.minidns.util;

public final class Base32 {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUV";
    private static final String PADDING = "======";

    private Base32() {
    }

    public static String encodeToString(byte[] bytes) {
        int paddingCount = ((int) (8.0d - (((double) (bytes.length % 5)) * 1.6d))) % 8;
        byte[] padded = new byte[(bytes.length + paddingCount)];
        System.arraycopy(bytes, 0, padded, 0, bytes.length);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i += 5) {
            long j = (((long) (padded[i] & 255)) << 32) + (((long) (padded[i + 1] & 255)) << 24) + ((long) ((padded[i + 2] & 255) << Tnaf.POW_2_WIDTH)) + ((long) ((padded[i + 3] & 255) << 8)) + ((long) (padded[i + 4] & 255));
            int i2 = (int) ((j >> 35) & 31);
            String str = ALPHABET;
            sb.append(str.charAt(i2));
            sb.append(str.charAt((int) ((j >> 30) & 31)));
            sb.append(str.charAt((int) ((j >> 25) & 31)));
            sb.append(str.charAt((int) ((j >> 20) & 31)));
            sb.append(str.charAt((int) ((j >> 15) & 31)));
            sb.append(str.charAt((int) ((j >> 10) & 31)));
            sb.append(str.charAt((int) ((j >> 5) & 31)));
            sb.append(str.charAt((int) (31 & j)));
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(sb.substring(0, sb.length() - paddingCount));
        sb2.append(PADDING.substring(0, paddingCount));
        return sb2.toString();
    }
}
