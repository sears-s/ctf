package org.minidns.util;

public class PlatformDetection {

    /* renamed from: android reason: collision with root package name */
    private static Boolean f0android;

    public static boolean isAndroid() {
        if (f0android == null) {
            try {
                Class.forName("android.Manifest");
                f0android = Boolean.valueOf(true);
            } catch (Exception e) {
                f0android = Boolean.valueOf(false);
            }
        }
        return f0android.booleanValue();
    }
}
