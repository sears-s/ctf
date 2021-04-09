package org.jivesoftware.smack.util.dns;

import java.net.InetAddress;
import java.util.List;
import org.jivesoftware.smack.util.StringUtils;
import org.minidns.dnsname.DnsName;

public class SRVRecord extends HostAddress implements Comparable<SRVRecord> {
    private int priority;
    private int weight;

    public SRVRecord(DnsName fqdn, int port, int priority2, int weight2, List<InetAddress> inetAddresses) {
        super(fqdn, port, inetAddresses);
        StringUtils.requireNotNullOrEmpty(fqdn, "The FQDN must not be null");
        if (weight2 < 0 || weight2 > 65535) {
            StringBuilder sb = new StringBuilder();
            sb.append("DNS SRV records weight must be a 16-bit unsigned integer (i.e. between 0-65535. Weight was: ");
            sb.append(weight2);
            throw new IllegalArgumentException(sb.toString());
        } else if (priority2 < 0 || priority2 > 65535) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("DNS SRV records priority must be a 16-bit unsigned integer (i.e. between 0-65535. Priority was: ");
            sb2.append(priority2);
            throw new IllegalArgumentException(sb2.toString());
        } else {
            this.priority = priority2;
            this.weight = weight2;
        }
    }

    public int getPriority() {
        return this.priority;
    }

    public int getWeight() {
        return this.weight;
    }

    public int compareTo(SRVRecord other) {
        int res = other.priority - this.priority;
        if (res == 0) {
            return this.weight - other.weight;
        }
        return res;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" prio:");
        sb.append(this.priority);
        sb.append(":w:");
        sb.append(this.weight);
        return sb.toString();
    }
}
