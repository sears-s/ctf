package org.minidns.util;

import org.minidns.dnsname.DnsName;

public final class NameUtil {
    public static boolean idnEquals(String name1, String name2) {
        boolean z = true;
        if (name1 == name2) {
            return true;
        }
        if (name1 == null || name2 == null) {
            return false;
        }
        if (name1.equals(name2)) {
            return true;
        }
        if (DnsName.from(name1).compareTo(DnsName.from(name2)) != 0) {
            z = false;
        }
        return z;
    }
}
