package org.minidns.record;

import java.io.DataInputStream;
import java.io.IOException;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Record.TYPE;

public class CNAME extends RRWithTarget {
    public static CNAME parse(DataInputStream dis, byte[] data) throws IOException {
        return new CNAME(DnsName.parse(dis, data));
    }

    public CNAME(String target) {
        this(DnsName.from(target));
    }

    public CNAME(DnsName target) {
        super(target);
    }

    public TYPE getType() {
        return TYPE.CNAME;
    }
}
