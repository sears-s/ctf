package org.minidns.cache;

import org.minidns.dnsmessage.Question;
import org.minidns.dnsname.DnsName;
import org.minidns.record.Data;
import org.minidns.record.Record;

public class FullLruCache extends ExtendedLruCache {
    public FullLruCache() {
        this(512);
    }

    public FullLruCache(int capacity) {
        super(capacity);
    }

    public FullLruCache(int capacity, long maxTTL) {
        super(capacity, maxTTL);
    }

    /* access modifiers changed from: protected */
    public boolean shouldGather(Record<? extends Data> record, Question question, DnsName authoritativeZone) {
        return true;
    }
}
