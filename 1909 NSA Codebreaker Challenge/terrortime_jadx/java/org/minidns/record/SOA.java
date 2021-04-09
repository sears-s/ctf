package org.minidns.record;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bouncycastle.asn1.cmc.BodyPartID;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Record.TYPE;

public class SOA extends Data {
    public final int expire;
    public final long minimum;
    public final DnsName mname;
    public final int refresh;
    public final int retry;
    public final DnsName rname;
    public final long serial;

    public static SOA parse(DataInputStream dis, byte[] data) throws IOException {
        SOA soa = new SOA(DnsName.parse(dis, data), DnsName.parse(dis, data), ((long) dis.readInt()) & BodyPartID.bodyIdMax, dis.readInt(), dis.readInt(), dis.readInt(), ((long) dis.readInt()) & BodyPartID.bodyIdMax);
        return soa;
    }

    public SOA(String mname2, String rname2, long serial2, int refresh2, int retry2, int expire2, long minimum2) {
        this(DnsName.from(mname2), DnsName.from(rname2), serial2, refresh2, retry2, expire2, minimum2);
    }

    public SOA(DnsName mname2, DnsName rname2, long serial2, int refresh2, int retry2, int expire2, long minimum2) {
        this.mname = mname2;
        this.rname = rname2;
        this.serial = serial2;
        this.refresh = refresh2;
        this.retry = retry2;
        this.expire = expire2;
        this.minimum = minimum2;
    }

    public TYPE getType() {
        return TYPE.SOA;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        this.mname.writeToStream(dos);
        this.rname.writeToStream(dos);
        dos.writeInt((int) this.serial);
        dos.writeInt(this.refresh);
        dos.writeInt(this.retry);
        dos.writeInt(this.expire);
        dos.writeInt((int) this.minimum);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.mname);
        String str = ". ";
        sb.append(str);
        sb.append(this.rname);
        sb.append(str);
        sb.append(this.serial);
        sb.append(' ');
        sb.append(this.refresh);
        sb.append(' ');
        sb.append(this.retry);
        sb.append(' ');
        sb.append(this.expire);
        sb.append(' ');
        return sb.append(this.minimum).toString();
    }
}
