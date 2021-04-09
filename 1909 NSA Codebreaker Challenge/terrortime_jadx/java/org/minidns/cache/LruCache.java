package org.minidns.cache;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.minidns.DnsCache;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsname.DnsName;
import org.minidns.dnsqueryresult.CachedDnsQueryResult;
import org.minidns.dnsqueryresult.DirectCachedDnsQueryResult;
import org.minidns.dnsqueryresult.DnsQueryResult;

public class LruCache extends DnsCache {
    protected LinkedHashMap<DnsMessage, CachedDnsQueryResult> backend;
    protected int capacity;
    protected long expireCount;
    protected long hitCount;
    protected long maxTTL;
    protected long missCount;

    public LruCache(int capacity2, long maxTTL2) {
        this.missCount = 0;
        this.expireCount = 0;
        this.hitCount = 0;
        this.capacity = capacity2;
        this.maxTTL = maxTTL2;
        final int i = capacity2;
        AnonymousClass1 r2 = new LinkedHashMap<DnsMessage, CachedDnsQueryResult>(Math.min(((capacity2 + 3) / 4) + capacity2 + 2, 11), 0.75f, true) {
            /* access modifiers changed from: protected */
            public boolean removeEldestEntry(Entry<DnsMessage, CachedDnsQueryResult> entry) {
                return size() > i;
            }
        };
        this.backend = r2;
    }

    public LruCache(int capacity2) {
        this(capacity2, Long.MAX_VALUE);
    }

    public LruCache() {
        this(512);
    }

    /* access modifiers changed from: protected */
    public synchronized void putNormalized(DnsMessage q, DnsQueryResult result) {
        if (result.response.receiveTimestamp > 0) {
            this.backend.put(q, new DirectCachedDnsQueryResult(q, result));
        }
    }

    /* access modifiers changed from: protected */
    public synchronized CachedDnsQueryResult getNormalized(DnsMessage q) {
        DnsMessage dnsMessage = q;
        synchronized (this) {
            CachedDnsQueryResult result = (CachedDnsQueryResult) this.backend.get(dnsMessage);
            if (result == null) {
                this.missCount++;
                return null;
            }
            DnsMessage message = result.response;
            if (message.receiveTimestamp + (1000 * Math.min(message.getAnswersMinTtl(), this.maxTTL)) < System.currentTimeMillis()) {
                this.missCount++;
                this.expireCount++;
                this.backend.remove(dnsMessage);
                return null;
            }
            this.hitCount++;
            return result;
        }
    }

    public synchronized void clear() {
        this.backend.clear();
        this.missCount = 0;
        this.hitCount = 0;
        this.expireCount = 0;
    }

    public long getMissCount() {
        return this.missCount;
    }

    public long getExpireCount() {
        return this.expireCount;
    }

    public long getHitCount() {
        return this.hitCount;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LRUCache{usage=");
        sb.append(this.backend.size());
        sb.append("/");
        sb.append(this.capacity);
        sb.append(", hits=");
        sb.append(this.hitCount);
        sb.append(", misses=");
        sb.append(this.missCount);
        sb.append(", expires=");
        sb.append(this.expireCount);
        sb.append("}");
        return sb.toString();
    }

    public void offer(DnsMessage query, DnsQueryResult result, DnsName knownAuthoritativeZone) {
    }
}
