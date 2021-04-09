package org.minidns.cache;

import org.minidns.DnsCache;

public interface MiniDnsCacheFactory {
    DnsCache newCache();
}
