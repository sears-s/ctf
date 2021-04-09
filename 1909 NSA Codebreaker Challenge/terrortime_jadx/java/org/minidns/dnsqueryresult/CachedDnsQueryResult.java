package org.minidns.dnsqueryresult;

import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsqueryresult.DnsQueryResult.QueryMethod;

public abstract class CachedDnsQueryResult extends DnsQueryResult {
    protected final DnsQueryResult cachedDnsQueryResult;

    protected CachedDnsQueryResult(DnsMessage query, DnsQueryResult cachedDnsQueryResult2) {
        super(QueryMethod.cachedDirect, query, cachedDnsQueryResult2.response);
        this.cachedDnsQueryResult = cachedDnsQueryResult2;
    }

    protected CachedDnsQueryResult(DnsMessage query, DnsMessage response, DnsQueryResult synthesynthesizationSource) {
        super(QueryMethod.cachedSynthesized, query, response);
        this.cachedDnsQueryResult = synthesynthesizationSource;
    }
}
