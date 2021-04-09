package org.minidns.record;

import java.io.DataInputStream;
import java.io.IOException;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Record.TYPE;

public class NS extends RRWithTarget {
    public static NS parse(DataInputStream dis, byte[] data) throws IOException {
        return new NS(DnsName.parse(dis, data));
    }

    public NS(DnsName name) {
        super(name);
    }

    public TYPE getType() {
        return TYPE.NS;
    }
}
