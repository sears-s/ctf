package org.minidns.idna;

public class MiniDnsIdna {
    private static IdnaTransformator idnaTransformator = new DefaultIdnaTransformator();

    public static String toASCII(String string) {
        return idnaTransformator.toASCII(string);
    }

    public static String toUnicode(String string) {
        return idnaTransformator.toUnicode(string);
    }

    public static void setActiveTransformator(IdnaTransformator idnaTransformator2) {
        if (idnaTransformator2 != null) {
            idnaTransformator = idnaTransformator2;
            return;
        }
        throw new IllegalArgumentException();
    }
}
