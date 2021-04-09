package org.minidns.util;

public class Hex {
    public static StringBuilder from(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X ", new Object[]{Byte.valueOf(b)}));
        }
        return sb;
    }
}
