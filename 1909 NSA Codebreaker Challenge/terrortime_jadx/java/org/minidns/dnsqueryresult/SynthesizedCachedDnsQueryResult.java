package org.minidns.dnsqueryresult;

import org.minidns.dnsmessage.DnsMessage;

public class SynthesizedCachedDnsQueryResult extends CachedDnsQueryResult {
    public SynthesizedCachedDnsQueryResult(DnsMessage query, DnsMessage response, DnsQueryResult synthesynthesizationSource) {
        super(query, response, synthesynthesizationSource);
    }
}
