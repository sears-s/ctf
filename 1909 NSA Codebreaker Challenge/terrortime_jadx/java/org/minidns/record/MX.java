package org.minidns.record;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Record.TYPE;

public class MX extends Data {
    @Deprecated
    public final DnsName name;
    public final int priority;
    public final DnsName target;

    public static MX parse(DataInputStream dis, byte[] data) throws IOException {
        return new MX(dis.readUnsignedShort(), DnsName.parse(dis, data));
    }

    public MX(int priority2, String name2) {
        this(priority2, DnsName.from(name2));
    }

    public MX(int priority2, DnsName name2) {
        this.priority = priority2;
        this.target = name2;
        this.name = this.target;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeShort(this.priority);
        this.target.writeToStream(dos);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.priority);
        sb.append(" ");
        sb.append(this.target);
        sb.append('.');
        return sb.toString();
    }

    public TYPE getType() {
        return TYPE.MX;
    }
}
