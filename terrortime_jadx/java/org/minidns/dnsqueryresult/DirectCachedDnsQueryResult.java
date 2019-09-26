package org.minidns.dnsqueryresult;

import org.minidns.dnsmessage.DnsMessage;

public class DirectCachedDnsQueryResult extends CachedDnsQueryResult {
    public DirectCachedDnsQueryResult(DnsMessage query, DnsQueryResult cachedDnsQueryResult) {
        super(query, cachedDnsQueryResult);
    }
}
