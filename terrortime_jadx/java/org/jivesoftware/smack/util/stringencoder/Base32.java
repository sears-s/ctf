package org.jivesoftware.smack.util.stringencoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.jivesoftware.smack.util.StringUtils;

public class Base32 {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ2345678";
    private static final StringEncoder base32Stringencoder = new StringEncoder() {
        public String encode(String string) {
            return Base32.encode(string);
        }

        public String decode(String string) {
            return Base32.decode(string);
        }
    };

    public static StringEncoder getStringEncoder() {
        return base32Stringencoder;
    }

    public static String decode(String str) {
        int i;
        char c;
        String str2 = StringUtils.UTF8;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        try {
            byte[] raw = str.getBytes(str2);
            for (byte b : raw) {
                char c2 = (char) b;
                if (!Character.isWhitespace(c2)) {
                    bs.write((byte) Character.toUpperCase(c2));
                }
            }
            while (true) {
                i = 8;
                c = '8';
                if (bs.size() % 8 == 0) {
                    break;
                }
                bs.write(56);
            }
            byte[] in = bs.toByteArray();
            bs.reset();
            DataOutputStream ds = new DataOutputStream(bs);
            int i2 = 0;
            while (i2 < in.length / i) {
                short[] s = new short[i];
                int[] t = new int[5];
                int padlen = 8;
                int j = 0;
                while (j < i && ((char) in[(i2 * 8) + j]) != c) {
                    s[j] = (short) ALPHABET.indexOf(in[(i2 * 8) + j]);
                    if (s[j] < 0) {
                        return null;
                    }
                    padlen--;
                    j++;
                    i = 8;
                    c = '8';
                }
                int blocklen = paddingToLen(padlen);
                if (blocklen < 0) {
                    return null;
                }
                t[0] = (s[0] << 3) | (s[1] >> 2);
                t[1] = ((s[1] & 3) << 6) | (s[2] << 1) | (s[3] >> 4);
                t[2] = ((s[3] & 15) << 4) | ((s[4] >> 1) & 15);
                t[3] = (s[4] << 7) | (s[5] << 2) | (s[6] >> 3);
                t[4] = ((s[6] & 7) << 5) | s[7];
                int j2 = 0;
                while (j2 < blocklen) {
                    try {
                        ds.writeByte((byte) (t[j2] & 255));
                        j2++;
                    } catch (IOException e) {
                    }
                }
                i2++;
                i = 8;
                c = '8';
            }
            try {
                return new String(bs.toByteArray(), str2);
            } catch (UnsupportedEncodingException e2) {
                throw new AssertionError(e2);
            }
        } catch (UnsupportedEncodingException e3) {
            throw new AssertionError(e3);
        }
    }

    public static String encode(String str) {
        String str2 = StringUtils.UTF8;
        try {
            byte[] b = str.getBytes(str2);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (int i = 0; i < (b.length + 4) / 5; i++) {
                short[] s = new short[5];
                int[] t = new int[8];
                int blocklen = 5;
                for (int j = 0; j < 5; j++) {
                    if ((i * 5) + j < b.length) {
                        s[j] = (short) (b[(i * 5) + j] & 255);
                    } else {
                        s[j] = 0;
                        blocklen--;
                    }
                }
                int padlen = lenToPadding(blocklen);
                t[0] = (byte) ((s[0] >> 3) & 31);
                t[1] = (byte) (((s[0] & 7) << 2) | ((s[1] >> 6) & 3));
                t[2] = (byte) ((s[1] >> 1) & 31);
                t[3] = (byte) (((s[1] & 1) << 4) | ((s[2] >> 4) & 15));
                t[4] = (byte) (((s[2] & 15) << 1) | ((s[3] >> 7) & 1));
                t[5] = (byte) ((s[3] >> 2) & 31);
                t[6] = (byte) (((s[4] >> 5) & 7) | ((s[3] & 3) << 3));
                t[7] = (byte) (s[4] & 31);
                for (int j2 = 0; j2 < t.length - padlen; j2++) {
                    os.write(ALPHABET.charAt(t[j2]));
                }
            }
            try {
                return new String(os.toByteArray(), str2);
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError(e);
            }
        } catch (UnsupportedEncodingException e2) {
            throw new AssertionError(e2);
        }
    }

    private static int lenToPadding(int blocklen) {
        if (blocklen == 1) {
            return 6;
        }
        if (blocklen == 2) {
            return 4;
        }
        if (blocklen == 3) {
            return 3;
        }
        if (blocklen == 4) {
            return 1;
        }
        if (blocklen != 5) {
            return -1;
        }
        return 0;
    }

    private static int paddingToLen(int padlen) {
        if (padlen == 0) {
            return 5;
        }
        if (padlen == 1) {
            return 4;
        }
        if (padlen == 3) {
            return 3;
        }
        if (padlen != 4) {
            return padlen != 6 ? -1 : 1;
        }
        return 2;
    }
}
