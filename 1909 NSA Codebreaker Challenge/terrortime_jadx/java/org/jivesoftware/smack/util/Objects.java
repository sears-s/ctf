package org.jivesoftware.smack.util;

public class Objects {
    public static <T> T requireNonNull(T obj, String message) {
        if (obj != null) {
            return obj;
        }
        throw new NullPointerException(message);
    }

    public static <T> T requireNonNull(T obj) {
        return requireNonNull(obj, null);
    }

    public static boolean equals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }
}
