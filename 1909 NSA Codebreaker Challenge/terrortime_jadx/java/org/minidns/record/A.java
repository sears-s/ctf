package org.minidns.record;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import org.minidns.record.Record.TYPE;
import org.minidns.util.InetAddressUtil;

public class A extends InternetAddressRR {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    public TYPE getType() {
        return TYPE.A;
    }

    public A(Inet4Address inet4Address) {
        super(inet4Address.getAddress());
    }

    public A(int q1, int q2, int q3, int q4) {
        super(new byte[]{(byte) q1, (byte) q2, (byte) q3, (byte) q4});
        if (q1 < 0 || q1 > 255 || q2 < 0 || q2 > 255 || q3 < 0 || q3 > 255 || q4 < 0 || q4 > 255) {
            throw new IllegalArgumentException();
        }
    }

    public A(byte[] ip) {
        super(ip);
        if (ip.length != 4) {
            throw new IllegalArgumentException("IPv4 address in A record is always 4 byte");
        }
    }

    public A(CharSequence ipv4CharSequence) {
        this(InetAddressUtil.ipv4From(ipv4CharSequence));
    }

    public static A parse(DataInputStream dis) throws IOException {
        byte[] ip = new byte[4];
        dis.readFully(ip);
        return new A(ip);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(this.ip[0] & 255));
        String str = ".";
        sb.append(str);
        sb.append(Integer.toString(this.ip[1] & 255));
        sb.append(str);
        sb.append(Integer.toString(this.ip[2] & 255));
        sb.append(str);
        sb.append(Integer.toString(this.ip[3] & 255));
        return sb.toString();
    }
}
