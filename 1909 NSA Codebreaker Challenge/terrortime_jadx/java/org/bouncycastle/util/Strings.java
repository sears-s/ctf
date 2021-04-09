package org.bouncycastle.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Vector;
import org.bouncycastle.util.encoders.UTF8;

public final class Strings {
    private static String LINE_SEPARATOR;

    private static class StringListImpl extends ArrayList<String> implements StringList {
        private StringListImpl() {
        }

        public void add(int i, String str) {
            super.add(i, str);
        }

        public boolean add(String str) {
            return super.add(str);
        }

        public /* bridge */ /* synthetic */ String get(int i) {
            return (String) super.get(i);
        }

        public String set(int i, String str) {
            return (String) super.set(i, str);
        }

        public String[] toStringArray() {
            String[] strArr = new String[size()];
            for (int i = 0; i != strArr.length; i++) {
                strArr[i] = (String) get(i);
            }
            return strArr;
        }

        public String[] toStringArray(int i, int i2) {
            String[] strArr = new String[(i2 - i)];
            int i3 = i;
            while (i3 != size() && i3 != i2) {
                strArr[i3 - i] = (String) get(i3);
                i3++;
            }
            return strArr;
        }
    }

    static {
        try {
            LINE_SEPARATOR = (String) AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty("line.separator");
                }
            });
        } catch (Exception e) {
            try {
                LINE_SEPARATOR = String.format("%n", new Object[0]);
            } catch (Exception e2) {
                LINE_SEPARATOR = "\n";
            }
        }
    }

    public static char[] asCharArray(byte[] bArr) {
        char[] cArr = new char[bArr.length];
        for (int i = 0; i != cArr.length; i++) {
            cArr[i] = (char) (bArr[i] & 255);
        }
        return cArr;
    }

    public static String fromByteArray(byte[] bArr) {
        return new String(asCharArray(bArr));
    }

    public static String fromUTF8ByteArray(byte[] bArr) {
        char[] cArr = new char[bArr.length];
        int transcodeToUTF16 = UTF8.transcodeToUTF16(bArr, cArr);
        if (transcodeToUTF16 >= 0) {
            return new String(cArr, 0, transcodeToUTF16);
        }
        throw new IllegalArgumentException("Invalid UTF-8 input");
    }

    public static String lineSeparator() {
        return LINE_SEPARATOR;
    }

    public static StringList newList() {
        return new StringListImpl();
    }

    public static String[] split(String str, char c) {
        int i;
        Vector vector = new Vector();
        boolean z = true;
        while (true) {
            if (!z) {
                break;
            }
            int indexOf = str.indexOf(c);
            if (indexOf > 0) {
                vector.addElement(str.substring(0, indexOf));
                str = str.substring(indexOf + 1);
            } else {
                vector.addElement(str);
                z = false;
            }
        }
        String[] strArr = new String[vector.size()];
        for (i = 0; i != strArr.length; i++) {
            strArr[i] = (String) vector.elementAt(i);
        }
        return strArr;
    }

    public static int toByteArray(String str, byte[] bArr, int i) {
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            bArr[i + i2] = (byte) str.charAt(i2);
        }
        return length;
    }

    public static byte[] toByteArray(String str) {
        byte[] bArr = new byte[str.length()];
        for (int i = 0; i != bArr.length; i++) {
            bArr[i] = (byte) str.charAt(i);
        }
        return bArr;
    }

    public static byte[] toByteArray(char[] cArr) {
        byte[] bArr = new byte[cArr.length];
        for (int i = 0; i != bArr.length; i++) {
            bArr[i] = (byte) cArr[i];
        }
        return bArr;
    }

    public static String toLowerCase(String str) {
        char[] charArray = str.toCharArray();
        boolean z = false;
        for (int i = 0; i != charArray.length; i++) {
            char c = charArray[i];
            if ('A' <= c && 'Z' >= c) {
                charArray[i] = (char) ((c - 'A') + 97);
                z = true;
            }
        }
        return z ? new String(charArray) : str;
    }

    /* JADX WARNING: type inference failed for: r1v3 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=char, code=int, for r1v1, types: [char, int] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void toUTF8ByteArray(char[] r6, java.io.OutputStream r7) throws java.io.IOException {
        /*
            r0 = 0
        L_0x0001:
            int r1 = r6.length
            if (r0 >= r1) goto L_0x0073
            char r1 = r6[r0]
            r2 = 128(0x80, float:1.794E-43)
            if (r1 >= r2) goto L_0x000b
            goto L_0x0053
        L_0x000b:
            r3 = 2048(0x800, float:2.87E-42)
            if (r1 >= r3) goto L_0x0017
            int r3 = r1 >> 6
            r3 = r3 | 192(0xc0, float:2.69E-43)
        L_0x0013:
            r7.write(r3)
            goto L_0x0050
        L_0x0017:
            r3 = 55296(0xd800, float:7.7486E-41)
            if (r1 < r3) goto L_0x0063
            r3 = 57343(0xdfff, float:8.0355E-41)
            if (r1 > r3) goto L_0x0063
            int r0 = r0 + 1
            int r3 = r6.length
            java.lang.String r4 = "invalid UTF-16 codepoint"
            if (r0 >= r3) goto L_0x005d
            char r3 = r6[r0]
            r5 = 56319(0xdbff, float:7.892E-41)
            if (r1 > r5) goto L_0x0057
            r1 = r1 & 1023(0x3ff, float:1.434E-42)
            int r1 = r1 << 10
            r3 = r3 & 1023(0x3ff, float:1.434E-42)
            r1 = r1 | r3
            r3 = 65536(0x10000, float:9.18355E-41)
            int r1 = r1 + r3
            int r3 = r1 >> 18
            r3 = r3 | 240(0xf0, float:3.36E-43)
            r7.write(r3)
            int r3 = r1 >> 12
            r3 = r3 & 63
            r3 = r3 | r2
            r7.write(r3)
            int r3 = r1 >> 6
            r3 = r3 & 63
            r3 = r3 | r2
            r7.write(r3)
        L_0x0050:
            r1 = r1 & 63
            r1 = r1 | r2
        L_0x0053:
            r7.write(r1)
            goto L_0x0070
        L_0x0057:
            java.lang.IllegalStateException r6 = new java.lang.IllegalStateException
            r6.<init>(r4)
            throw r6
        L_0x005d:
            java.lang.IllegalStateException r6 = new java.lang.IllegalStateException
            r6.<init>(r4)
            throw r6
        L_0x0063:
            int r3 = r1 >> 12
            r3 = r3 | 224(0xe0, float:3.14E-43)
            r7.write(r3)
            int r3 = r1 >> 6
            r3 = r3 & 63
            r3 = r3 | r2
            goto L_0x0013
        L_0x0070:
            int r0 = r0 + 1
            goto L_0x0001
        L_0x0073:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.util.Strings.toUTF8ByteArray(char[], java.io.OutputStream):void");
    }

    public static byte[] toUTF8ByteArray(String str) {
        return toUTF8ByteArray(str.toCharArray());
    }

    public static byte[] toUTF8ByteArray(char[] cArr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            toUTF8ByteArray(cArr, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("cannot encode string to byte array!");
        }
    }

    public static String toUpperCase(String str) {
        char[] charArray = str.toCharArray();
        boolean z = false;
        for (int i = 0; i != charArray.length; i++) {
            char c = charArray[i];
            if ('a' <= c && 'z' >= c) {
                charArray[i] = (char) ((c - 'a') + 65);
                z = true;
            }
        }
        return z ? new String(charArray) : str;
    }
}
