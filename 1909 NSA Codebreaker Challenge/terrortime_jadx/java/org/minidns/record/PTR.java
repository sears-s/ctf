package org.minidns.record;

import java.io.DataInputStream;
import java.io.IOException;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Record.TYPE;

public class PTR extends RRWithTarget {
    public static PTR parse(DataInputStream dis, byte[] data) throws IOException {
        return new PTR(DnsName.parse(dis, data));
    }

    PTR(String name) {
        this(DnsName.from(name));
    }

    PTR(DnsName name) {
        super(name);
    }

    public TYPE getType() {
        return TYPE.PTR;
    }
}
