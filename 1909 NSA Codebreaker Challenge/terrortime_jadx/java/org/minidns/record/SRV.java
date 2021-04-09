package org.minidns.record;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Record.TYPE;

public class SRV extends Data implements Comparable<SRV> {
    @Deprecated
    public final DnsName name;
    public final int port;
    public final int priority;
    public final DnsName target;
    public final int weight;

    public static SRV parse(DataInputStream dis, byte[] data) throws IOException {
        return new SRV(dis.readUnsignedShort(), dis.readUnsignedShort(), dis.readUnsignedShort(), DnsName.parse(dis, data));
    }

    public SRV(int priority2, int weight2, int port2, String name2) {
        this(priority2, weight2, port2, DnsName.from(name2));
    }

    public SRV(int priority2, int weight2, int port2, DnsName name2) {
        this.priority = priority2;
        this.weight = weight2;
        this.port = port2;
        this.target = name2;
        this.name = this.target;
    }

    public boolean isServiceAvailable() {
        return !this.target.isRootLabel();
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeShort(this.priority);
        dos.writeShort(this.weight);
        dos.writeShort(this.port);
        this.target.writeToStream(dos);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.priority);
        String str = " ";
        sb.append(str);
        sb.append(this.weight);
        sb.append(str);
        sb.append(this.port);
        sb.append(str);
        sb.append(this.target);
        sb.append(".");
        return sb.toString();
    }

    public TYPE getType() {
        return TYPE.SRV;
    }

    public int compareTo(SRV other) {
        int res = other.priority - this.priority;
        if (res == 0) {
            return this.weight - other.weight;
        }
        return res;
    }
}
