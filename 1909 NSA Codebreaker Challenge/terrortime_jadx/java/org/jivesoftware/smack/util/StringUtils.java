package org.jivesoftware.smack.util;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class StringUtils {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final String AMP_ENCODE = "&amp;";
    public static final String APOS_ENCODE = "&apos;";
    public static final String GT_ENCODE = "&gt;";
    public static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    public static final String LT_ENCODE = "&lt;";
    public static final String MD5 = "MD5";
    public static final String QUOTE_ENCODE = "&quot;";
    private static final ThreadLocal<SecureRandom> SECURE_RANDOM = new ThreadLocal<SecureRandom>() {
        /* access modifiers changed from: protected */
        public SecureRandom initialValue() {
            return new SecureRandom();
        }
    };
    public static final String SHA1 = "SHA-1";
    public static final String USASCII = "US-ASCII";
    public static final String UTF8 = "UTF-8";
    private static final char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final ThreadLocal<Random> randGen = new ThreadLocal<Random>() {
        /* access modifiers changed from: protected */
        public Random initialValue() {
            return new Random();
        }
    };

    /* renamed from: org.jivesoftware.smack.util.StringUtils$3 reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$util$StringUtils$XmlEscapeMode = new int[XmlEscapeMode.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$util$StringUtils$XmlEscapeMode[XmlEscapeMode.safe.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$util$StringUtils$XmlEscapeMode[XmlEscapeMode.forAttribute.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$util$StringUtils$XmlEscapeMode[XmlEscapeMode.forAttributeApos.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$util$StringUtils$XmlEscapeMode[XmlEscapeMode.forText.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private enum XmlEscapeMode {
        safe,
        forAttribute,
        forAttributeApos,
        forText
    }

    public static CharSequence escapeForXml(CharSequence input) {
        return escapeForXml(input, XmlEscapeMode.safe);
    }

    public static CharSequence escapeForXmlAttribute(CharSequence input) {
        return escapeForXml(input, XmlEscapeMode.forAttribute);
    }

    public static CharSequence escapeForXmlAttributeApos(CharSequence input) {
        return escapeForXml(input, XmlEscapeMode.forAttributeApos);
    }

    public static CharSequence escapeForXmlText(CharSequence input) {
        return escapeForXml(input, XmlEscapeMode.forText);
    }

    private static CharSequence escapeForXml(CharSequence input, XmlEscapeMode xmlEscapeMode) {
        if (input == null) {
            return null;
        }
        int len = input.length();
        StringBuilder out = new StringBuilder((int) (((double) len) * 1.3d));
        int last = 0;
        int i = 0;
        while (i < len) {
            CharSequence toAppend = null;
            char ch = input.charAt(i);
            int i2 = AnonymousClass3.$SwitchMap$org$jivesoftware$smack$util$StringUtils$XmlEscapeMode[xmlEscapeMode.ordinal()];
            if (i2 != 1) {
                if (i2 != 2) {
                    if (i2 != 3) {
                        if (i2 == 4) {
                            if (ch == '&') {
                                toAppend = AMP_ENCODE;
                            } else if (ch == '<') {
                                toAppend = LT_ENCODE;
                            }
                        }
                    } else if (ch == '&') {
                        toAppend = AMP_ENCODE;
                    } else if (ch == '\'') {
                        toAppend = APOS_ENCODE;
                    } else if (ch == '<') {
                        toAppend = LT_ENCODE;
                    }
                } else if (ch == '\"') {
                    toAppend = QUOTE_ENCODE;
                } else if (ch == '<') {
                    toAppend = LT_ENCODE;
                } else if (ch == '&') {
                    toAppend = AMP_ENCODE;
                } else if (ch == '\'') {
                    toAppend = APOS_ENCODE;
                }
            } else if (ch == '\"') {
                toAppend = QUOTE_ENCODE;
            } else if (ch == '<') {
                toAppend = LT_ENCODE;
            } else if (ch == '>') {
                toAppend = GT_ENCODE;
            } else if (ch == '&') {
                toAppend = AMP_ENCODE;
            } else if (ch == '\'') {
                toAppend = APOS_ENCODE;
            }
            if (toAppend != null) {
                if (i > last) {
                    out.append(input, last, i);
                }
                out.append(toAppend);
                i++;
                last = i;
            } else {
                i++;
            }
        }
        if (last == 0) {
            return input;
        }
        if (i > last) {
            out.append(input, last, i);
        }
        return out;
    }

    @Deprecated
    public static synchronized String hash(String data) {
        String hex;
        synchronized (StringUtils.class) {
            hex = SHA1.hex(data);
        }
        return hex;
    }

    public static String encodeHex(byte[] bytes) {
        char[] hexChars = new char[(bytes.length * 2)];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 255;
            int i = j * 2;
            char[] cArr = HEX_CHARS;
            hexChars[i] = cArr[v >>> 4];
            hexChars[(j * 2) + 1] = cArr[v & 15];
        }
        return new String(hexChars);
    }

    public static byte[] toUtf8Bytes(String string) {
        try {
            return string.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not supported by platform", e);
        }
    }

    public static String insecureRandomString(int length) {
        return randomString(length, (Random) randGen.get());
    }

    public static String randomString(int length) {
        return randomString(length, (Random) SECURE_RANDOM.get());
    }

    private static String randomString(int length, Random random) {
        if (length < 1) {
            return null;
        }
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        char[] randomChars = new char[length];
        for (int i = 0; i < length; i++) {
            randomChars[i] = getPrintableChar(randomBytes[i]);
        }
        return new String(randomChars);
    }

    private static char getPrintableChar(byte indexByte) {
        int index = indexByte & 255;
        char[] cArr = numbersAndLetters;
        return cArr[index % cArr.length];
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isNullOrEmpty(cs);
    }

    public static boolean isNullOrEmpty(CharSequence cs) {
        return cs == null || isEmpty(cs);
    }

    public static boolean isNotEmpty(CharSequence... css) {
        for (CharSequence cs : css) {
            if (isNullOrEmpty(cs)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNullOrEmpty(CharSequence... css) {
        for (CharSequence cs : css) {
            if (isNotEmpty(cs)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs.length() == 0;
    }

    public static String collectionToString(Collection<? extends Object> collection) {
        return toStringBuilder(collection, " ").toString();
    }

    public static StringBuilder toStringBuilder(Collection<? extends Object> collection, String delimiter) {
        StringBuilder sb = new StringBuilder(collection.size() * 20);
        Iterator<? extends Object> it = collection.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(delimiter);
            }
        }
        return sb;
    }

    public static String returnIfNotEmptyTrimmed(String string) {
        if (string == null) {
            return null;
        }
        String trimmedString = string.trim();
        if (trimmedString.length() > 0) {
            return trimmedString;
        }
        return null;
    }

    public static boolean nullSafeCharSequenceEquals(CharSequence csOne, CharSequence csTwo) {
        return nullSafeCharSequenceComparator(csOne, csTwo) == 0;
    }

    public static int nullSafeCharSequenceComparator(CharSequence csOne, CharSequence csTwo) {
        int i = 1;
        if ((csOne == null) ^ (csTwo == null)) {
            if (csOne == null) {
                i = -1;
            }
            return i;
        } else if (csOne == null && csTwo == null) {
            return 0;
        } else {
            return csOne.toString().compareTo(csTwo.toString());
        }
    }

    public static <CS extends CharSequence> CS requireNotNullOrEmpty(CS cs, String message) {
        if (!isNullOrEmpty((CharSequence) cs)) {
            return cs;
        }
        throw new IllegalArgumentException(message);
    }

    public static <CS extends CharSequence> CS requireNullOrNotEmpty(CS cs, String message) {
        if (cs == null) {
            return null;
        }
        if (!cs.toString().isEmpty()) {
            return cs;
        }
        throw new IllegalArgumentException(message);
    }

    public static String maybeToString(CharSequence cs) {
        if (cs == null) {
            return null;
        }
        return cs.toString();
    }
}
