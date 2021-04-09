package org.minidns.util;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;
import org.minidns.dnsname.DnsName;

public class InetAddressUtil {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Pattern IPV4_PATTERN = Pattern.compile("\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z");
    private static final Pattern IPV6_PATTERN = Pattern.compile("(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))");

    public static Inet4Address ipv4From(CharSequence cs) {
        try {
            InetAddress inetAddress = InetAddress.getByName(cs.toString());
            if (inetAddress instanceof Inet4Address) {
                return (Inet4Address) inetAddress;
            }
            throw new IllegalArgumentException();
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Inet6Address ipv6From(CharSequence cs) {
        try {
            InetAddress inetAddress = InetAddress.getByName(cs.toString());
            if (inetAddress instanceof Inet6Address) {
                return (Inet6Address) inetAddress;
            }
            throw new IllegalArgumentException();
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean isIpV4Address(CharSequence address) {
        if (address == null) {
            return false;
        }
        return IPV4_PATTERN.matcher(address).matches();
    }

    public static boolean isIpV6Address(CharSequence address) {
        if (address == null) {
            return false;
        }
        return IPV6_PATTERN.matcher(address).matches();
    }

    public static boolean isIpAddress(CharSequence address) {
        return isIpV6Address(address) || isIpV4Address(address);
    }

    public static DnsName reverseIpAddressOf(Inet6Address inet6Address) {
        String[] ipAddressParts = inet6Address.getHostAddress().split(":");
        String[] parts = new String[32];
        int currentPartNum = 0;
        for (int i = ipAddressParts.length - 1; i >= 0; i--) {
            String currentPart = ipAddressParts[i];
            int missingPlaces = 4 - currentPart.length();
            int j = 0;
            while (j < missingPlaces) {
                int currentPartNum2 = currentPartNum + 1;
                parts[currentPartNum] = "0";
                j++;
                currentPartNum = currentPartNum2;
            }
            int j2 = 0;
            while (j2 < currentPart.length()) {
                int currentPartNum3 = currentPartNum + 1;
                parts[currentPartNum] = Character.toString(currentPart.charAt(j2));
                j2++;
                currentPartNum = currentPartNum3;
            }
        }
        return DnsName.from(parts);
    }

    public static DnsName reverseIpAddressOf(Inet4Address inet4Address) {
        return DnsName.from(inet4Address.getHostAddress().split("\\."));
    }
}
