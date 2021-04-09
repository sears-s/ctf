package android.support.v4.text;

import android.os.Build.VERSION;
import android.text.TextUtils;
import com.badguy.terrortime.BuildConfig;
import java.util.Locale;
import org.jivesoftware.smack.util.StringUtils;

public final class TextUtilsCompat {
    private static final String ARAB_SCRIPT_SUBTAG = "Arab";
    private static final String HEBR_SCRIPT_SUBTAG = "Hebr";
    private static final Locale ROOT;

    static {
        String str = BuildConfig.FLAVOR;
        ROOT = new Locale(str, str);
    }

    public static String htmlEncode(String s) {
        if (VERSION.SDK_INT >= 17) {
            return TextUtils.htmlEncode(s);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\"') {
                sb.append(StringUtils.QUOTE_ENCODE);
            } else if (c == '<') {
                sb.append(StringUtils.LT_ENCODE);
            } else if (c == '>') {
                sb.append(StringUtils.GT_ENCODE);
            } else if (c == '&') {
                sb.append(StringUtils.AMP_ENCODE);
            } else if (c != '\'') {
                sb.append(c);
            } else {
                sb.append("&#39;");
            }
        }
        return sb.toString();
    }

    public static int getLayoutDirectionFromLocale(Locale locale) {
        if (VERSION.SDK_INT >= 17) {
            return TextUtils.getLayoutDirectionFromLocale(locale);
        }
        if (locale != null && !locale.equals(ROOT)) {
            String scriptSubtag = ICUCompat.maximizeAndGetScript(locale);
            if (scriptSubtag == null) {
                return getLayoutDirectionFromFirstChar(locale);
            }
            if (scriptSubtag.equalsIgnoreCase(ARAB_SCRIPT_SUBTAG) || scriptSubtag.equalsIgnoreCase(HEBR_SCRIPT_SUBTAG)) {
                return 1;
            }
        }
        return 0;
    }

    private static int getLayoutDirectionFromFirstChar(Locale locale) {
        byte directionality = Character.getDirectionality(locale.getDisplayName(locale).charAt(0));
        return (directionality == 1 || directionality == 2) ? 1 : 0;
    }

    private TextUtilsCompat() {
    }
}
