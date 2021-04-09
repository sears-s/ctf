package org.minidns;

import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsname.DnsName;
import org.minidns.dnsqueryresult.CachedDnsQueryResult;
import org.minidns.dnsqueryresult.DnsQueryResult;

public abstract class DnsCache {
    public static final int DEFAULT_CACHE_SIZE = 512;

    /* access modifiers changed from: protected */
    public abstract CachedDnsQueryResult getNormalized(DnsMessage dnsMessage);

    public abstract void offer(DnsMessage dnsMessage, DnsQueryResult dnsQueryResult, DnsName dnsName);

    /* access modifiers changed from: protected */
    public abstract void putNormalized(DnsMessage dnsMessage, DnsQueryResult dnsQueryResult);

    public final void put(DnsMessage query, DnsQueryResult result) {
        putNormalized(query.asNormalizedVersion(), result);
    }

    public final CachedDnsQueryResult get(DnsMessage query) {
        return getNormalized(query.asNormalizedVersion());
    }
}
