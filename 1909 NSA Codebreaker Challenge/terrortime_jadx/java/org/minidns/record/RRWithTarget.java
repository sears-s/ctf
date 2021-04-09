package org.minidns.record;

import java.io.DataOutputStream;
import java.io.IOException;
import org.minidns.dnsname.DnsName;

public abstract class RRWithTarget extends Data {
    @Deprecated
    public final DnsName name;
    public final DnsName target;

    public void serialize(DataOutputStream dos) throws IOException {
        this.target.writeToStream(dos);
    }

    protected RRWithTarget(DnsName target2) {
        this.target = target2;
        this.name = target2;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.target);
        sb.append(".");
        return sb.toString();
    }

    public final DnsName getTarget() {
        return this.target;
    }
}
