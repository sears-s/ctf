package org.jivesoftware.smack.util;

public class ByteUtils {
    @Deprecated
    public static byte[] concact(byte[] arrayOne, byte[] arrayTwo) {
        return concat(arrayOne, arrayTwo);
    }

    public static byte[] concat(byte[] arrayOne, byte[] arrayTwo) {
        byte[] res = new byte[(arrayOne.length + arrayTwo.length)];
        System.arraycopy(arrayOne, 0, res, 0, arrayOne.length);
        System.arraycopy(arrayTwo, 0, res, arrayOne.length, arrayTwo.length);
        return res;
    }
}
