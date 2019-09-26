package org.minidns.constants;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.bouncycastle.crypto.tls.CipherSuite;

public class DnsRootServer {
    protected static final Inet4Address[] IPV4_ROOT_SERVERS = {rootServerInet4Address('a', 198, 41, 0, 4), rootServerInet4Address('b', 192, 228, 79, 201), rootServerInet4Address('c', 192, 33, 4, 12), rootServerInet4Address('d', 199, 7, 91, 13), rootServerInet4Address('e', 192, 203, 230, 10), rootServerInet4Address('f', 192, 5, 5, 241), rootServerInet4Address('g', 192, 112, 36, 4), rootServerInet4Address('h', 198, 97, CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA256, 53), rootServerInet4Address('i', 192, 36, CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA, 17), rootServerInet4Address('j', 192, 58, 128, 30), rootServerInet4Address('k', CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA256, 0, 14, 129), rootServerInet4Address('l', 199, 7, 83, 42), rootServerInet4Address('m', 202, 12, 27, 33)};
    private static final Map<Character, Inet4Address> IPV4_ROOT_SERVER_MAP = new HashMap();
    protected static final Inet6Address[] IPV6_ROOT_SERVERS = {rootServerInet6Address('a', 8193, 1283, 47678, 0, 0, 0, 2, 48), rootServerInet6Address('b', 8193, 1280, CipherSuite.TLS_RSA_WITH_CAMELLIA_256_CBC_SHA, 0, 0, 0, 0, 11), rootServerInet6Address('c', 8193, 1280, 2, 0, 0, 0, 0, 12), rootServerInet6Address('d', 8193, 1280, 45, 0, 0, 0, 0, 13), rootServerInet6Address('f', 8193, 1280, 47, 0, 0, 0, 0, 15), rootServerInet6Address('h', 8193, 1280, 1, 0, 0, 0, 0, 83), rootServerInet6Address('i', 8193, 2046, 0, 0, 0, 0, 0, 83), rootServerInet6Address('j', 8193, 1283, 3111, 0, 0, 0, 2, 48), rootServerInet6Address('l', 8193, 1280, 3, 0, 0, 0, 0, 66), rootServerInet6Address('m', 8193, 3523, 0, 0, 0, 0, 0, 53)};
    private static final Map<Character, Inet6Address> IPV6_ROOT_SERVER_MAP = new HashMap();

    private static Inet4Address rootServerInet4Address(char rootServerId, int addr0, int addr1, int addr2, int addr3) {
        StringBuilder sb = new StringBuilder();
        sb.append(rootServerId);
        sb.append(".root-servers.net");
        try {
            Inet4Address inetAddress = (Inet4Address) InetAddress.getByAddress(sb.toString(), new byte[]{(byte) addr0, (byte) addr1, (byte) addr2, (byte) addr3});
            IPV4_ROOT_SERVER_MAP.put(Character.valueOf(rootServerId), inetAddress);
            return inetAddress;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static Inet6Address rootServerInet6Address(char rootServerId, int addr0, int addr1, int addr2, int addr3, int addr4, int addr5, int addr6, int addr7) {
        StringBuilder sb = new StringBuilder();
        sb.append(rootServerId);
        sb.append(".root-servers.net");
        try {
            Inet6Address inetAddress = (Inet6Address) InetAddress.getByAddress(sb.toString(), new byte[]{(byte) (addr0 >> 8), (byte) addr0, (byte) (addr1 >> 8), (byte) addr1, (byte) (addr2 >> 8), (byte) addr2, (byte) (addr3 >> 8), (byte) addr3, (byte) (addr4 >> 8), (byte) addr4, (byte) (addr5 >> 8), (byte) addr5, (byte) (addr6 >> 8), (byte) addr6, (byte) (addr7 >> 8), (byte) addr7});
            IPV6_ROOT_SERVER_MAP.put(Character.valueOf(rootServerId), inetAddress);
            return inetAddress;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static Inet4Address getRandomIpv4RootServer(Random random) {
        Inet4Address[] inet4AddressArr = IPV4_ROOT_SERVERS;
        return inet4AddressArr[random.nextInt(inet4AddressArr.length)];
    }

    public static Inet6Address getRandomIpv6RootServer(Random random) {
        Inet6Address[] inet6AddressArr = IPV6_ROOT_SERVERS;
        return inet6AddressArr[random.nextInt(inet6AddressArr.length)];
    }

    public static Inet4Address getIpv4RootServerById(char id) {
        return (Inet4Address) IPV4_ROOT_SERVER_MAP.get(Character.valueOf(id));
    }

    public static Inet6Address getIpv6RootServerById(char id) {
        return (Inet6Address) IPV6_ROOT_SERVER_MAP.get(Character.valueOf(id));
    }
}
