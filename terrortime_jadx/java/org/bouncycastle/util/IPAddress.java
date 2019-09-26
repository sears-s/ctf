package org.bouncycastle.util;

public class IPAddress {
    private static boolean isMaskValue(String str, int i) {
        boolean z = false;
        try {
            int parseInt = Integer.parseInt(str);
            if (parseInt >= 0 && parseInt <= i) {
                z = true;
            }
            return z;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValid(String str) {
        return isValidIPv4(str) || isValidIPv6(str);
    }

    public static boolean isValidIPv4(String str) {
        boolean z = false;
        if (str.length() == 0) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(".");
        String sb2 = sb.toString();
        int i = 0;
        int i2 = 0;
        while (i < sb2.length()) {
            int indexOf = sb2.indexOf(46, i);
            if (indexOf <= i) {
                break;
            } else if (i2 == 4) {
                return false;
            } else {
                try {
                    int parseInt = Integer.parseInt(sb2.substring(i, indexOf));
                    if (parseInt < 0 || parseInt > 255) {
                        return false;
                    }
                    i = indexOf + 1;
                    i2++;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        if (i2 == 4) {
            z = true;
        }
        return z;
    }

    public static boolean isValidIPv4WithNetmask(String str) {
        int indexOf = str.indexOf("/");
        String substring = str.substring(indexOf + 1);
        if (indexOf <= 0 || !isValidIPv4(str.substring(0, indexOf))) {
            return false;
        }
        return isValidIPv4(substring) || isMaskValue(substring, 32);
    }

    public static boolean isValidIPv6(String str) {
        boolean z = false;
        if (str.length() == 0) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(":");
        String sb2 = sb.toString();
        int i = 0;
        int i2 = 0;
        boolean z2 = false;
        while (i < sb2.length()) {
            int indexOf = sb2.indexOf(58, i);
            if (indexOf < i) {
                break;
            } else if (i2 == 8) {
                return false;
            } else {
                if (i != indexOf) {
                    String substring = sb2.substring(i, indexOf);
                    if (indexOf != sb2.length() - 1 || substring.indexOf(46) <= 0) {
                        try {
                            int parseInt = Integer.parseInt(sb2.substring(i, indexOf), 16);
                            if (parseInt < 0 || parseInt > 65535) {
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    } else if (!isValidIPv4(substring)) {
                        return false;
                    } else {
                        i2++;
                    }
                } else if (indexOf != 1 && indexOf != sb2.length() - 1 && z2) {
                    return false;
                } else {
                    z2 = true;
                }
                i = indexOf + 1;
                i2++;
            }
        }
        if (i2 == 8 || z2) {
            z = true;
        }
        return z;
    }

    public static boolean isValidIPv6WithNetmask(String str) {
        int indexOf = str.indexOf("/");
        String substring = str.substring(indexOf + 1);
        if (indexOf <= 0 || !isValidIPv6(str.substring(0, indexOf))) {
            return false;
        }
        return isValidIPv6(substring) || isMaskValue(substring, 128);
    }

    public static boolean isValidWithNetMask(String str) {
        return isValidIPv4WithNetmask(str) || isValidIPv6WithNetmask(str);
    }
}
