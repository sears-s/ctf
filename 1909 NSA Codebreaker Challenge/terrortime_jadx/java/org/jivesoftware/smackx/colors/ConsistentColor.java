package org.jivesoftware.smackx.colors;

import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.SHA1;

public class ConsistentColor {
    private static final ConsistentColorSettings DEFAULT_SETTINGS = new ConsistentColorSettings();
    private static final double KB = 0.114d;
    private static final double KG = 0.587d;
    private static final double KR = 0.299d;
    private static final double Y = 0.732d;

    /* renamed from: org.jivesoftware.smackx.colors.ConsistentColor$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smackx$colors$ConsistentColor$Deficiency = new int[Deficiency.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smackx$colors$ConsistentColor$Deficiency[Deficiency.none.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$colors$ConsistentColor$Deficiency[Deficiency.redGreenBlindness.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$colors$ConsistentColor$Deficiency[Deficiency.blueBlindness.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public static class ConsistentColorSettings {
        private final Deficiency deficiency;

        public ConsistentColorSettings() {
            this(Deficiency.none);
        }

        public ConsistentColorSettings(Deficiency deficiency2) {
            this.deficiency = (Deficiency) Objects.requireNonNull(deficiency2, "Deficiency must be given");
        }

        public Deficiency getDeficiency() {
            return this.deficiency;
        }
    }

    public enum Deficiency {
        none,
        redGreenBlindness,
        blueBlindness
    }

    private static double createAngle(CharSequence input) {
        byte[] h = SHA1.bytes(input.toString());
        return 2.0d * (((double) (u(h[0]) + (u(h[1]) * 256))) / 65536.0d) * 3.141592653589793d;
    }

    private static double applyColorDeficiencyCorrection(double angle, Deficiency deficiency) {
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smackx$colors$ConsistentColor$Deficiency[deficiency.ordinal()];
        if (i == 1) {
            return angle;
        }
        if (i == 2) {
            return angle % 3.141592653589793d;
        }
        if (i != 3) {
            return angle;
        }
        return ((angle - 1.5707963267948966d) % 3.141592653589793d) + 1.5707963267948966d;
    }

    private static double[] angleToCbCr(double angle) {
        double factor;
        double cb = Math.cos(angle);
        double cr = Math.sin(angle);
        double acb = Math.abs(cb);
        double acr = Math.abs(cr);
        if (acr > acb) {
            factor = 0.5d / acr;
        } else {
            factor = 0.5d / acb;
        }
        return new double[]{cb * factor, cr * factor};
    }

    private static float[] CbCrToRGB(double[] cbcr, double y) {
        double r = (1.4020000000000001d * cbcr[1]) + y;
        double b = (1.772d * cbcr[0]) + y;
        double g = ((y - (KR * r)) - (KB * b)) / KG;
        return new float[]{(float) clip(r), (float) clip(g), (float) clip(b)};
    }

    private static double clip(double value) {
        double out = value;
        if (value < 0.0d) {
            out = 0.0d;
        }
        if (value > 1.0d) {
            return 1.0d;
        }
        return out;
    }

    private static int u(byte b) {
        return b & 255;
    }

    public static float[] RGBFrom(CharSequence input) {
        return RGBFrom(input, DEFAULT_SETTINGS);
    }

    public static float[] RGBFrom(CharSequence input, ConsistentColorSettings settings) {
        return CbCrToRGB(angleToCbCr(applyColorDeficiencyCorrection(createAngle(input), settings.getDeficiency())), Y);
    }
}
