package org.minidns.record;

import java.io.DataInputStream;
import java.io.IOException;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Record.TYPE;

public class DNAME extends RRWithTarget {
    public static DNAME parse(DataInputStream dis, byte[] data) throws IOException {
        return new DNAME(DnsName.parse(dis, data));
    }

    public DNAME(String target) {
        this(DnsName.from(target));
    }

    public DNAME(DnsName target) {
        super(target);
    }

    public TYPE getType() {
        return TYPE.DNAME;
    }
}
