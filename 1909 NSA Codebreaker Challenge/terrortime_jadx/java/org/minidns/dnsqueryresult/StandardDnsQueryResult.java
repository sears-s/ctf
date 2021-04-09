package org.minidns.dnsqueryresult;

import java.net.InetAddress;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsqueryresult.DnsQueryResult.QueryMethod;

public class StandardDnsQueryResult extends DnsQueryResult {
    public final int port;
    public final InetAddress serverAddress;

    public StandardDnsQueryResult(InetAddress serverAddress2, int port2, QueryMethod queryMethod, DnsMessage query, DnsMessage responseDnsMessage) {
        super(queryMethod, query, responseDnsMessage);
        this.serverAddress = serverAddress2;
        this.port = port2;
    }
}
