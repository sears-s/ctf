package org.minidns.record;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Inet6Address;
import org.minidns.record.Record.TYPE;
import org.minidns.util.InetAddressUtil;

public class AAAA extends InternetAddressRR {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    public TYPE getType() {
        return TYPE.AAAA;
    }

    public AAAA(Inet6Address inet6address) {
        super(inet6address.getAddress());
    }

    public AAAA(byte[] ip) {
        super(ip);
        if (ip.length != 16) {
            throw new IllegalArgumentException("IPv6 address in AAAA record is always 16 byte");
        }
    }

    public AAAA(CharSequence ipv6CharSequence) {
        this(InetAddressUtil.ipv6From(ipv6CharSequence));
    }

    public static AAAA parse(DataInputStream dis) throws IOException {
        byte[] ip = new byte[16];
        dis.readFully(ip);
        return new AAAA(ip);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.ip.length; i += 2) {
            if (i != 0) {
                sb.append(':');
            }
            sb.append(Integer.toHexString(((this.ip[i] & 255) << 8) + (this.ip[i + 1] & 255)));
        }
        return sb.toString();
    }
}
