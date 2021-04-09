package org.jxmpp.util.cache;

public interface Cache<K, V> {
    @Deprecated
    V get(Object obj);

    int getMaxCacheSize();

    V lookup(K k);

    V put(K k, V v);

    void setMaxCacheSize(int i);
}
