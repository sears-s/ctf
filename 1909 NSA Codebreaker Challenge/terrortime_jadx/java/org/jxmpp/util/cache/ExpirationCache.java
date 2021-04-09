package org.jxmpp.util.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ExpirationCache<K, V> implements Cache<K, V>, Map<K, V> {
    private final LruCache<K, ExpireElement<V>> cache;
    private long defaultExpirationTime;

    private static class EntryImpl<K, V> implements Entry<K, V> {
        private final K key;
        private V value;

        EntryImpl(K key2, V value2) {
            this.key = key2;
            this.value = value2;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(V value2) {
            V oldValue = this.value;
            this.value = value2;
            return oldValue;
        }
    }

    private static class ExpireElement<V> {
        /* access modifiers changed from: private */
        public final V element;
        private final long expirationTimestamp;

        private ExpireElement(V element2, long expirationTime) {
            this.element = element2;
            this.expirationTimestamp = System.currentTimeMillis() + expirationTime;
        }

        /* access modifiers changed from: private */
        public boolean isExpired() {
            return System.currentTimeMillis() > this.expirationTimestamp;
        }

        public int hashCode() {
            return this.element.hashCode();
        }

        public boolean equals(Object other) {
            if (!(other instanceof ExpireElement)) {
                return false;
            }
            return this.element.equals(((ExpireElement) other).element);
        }
    }

    public ExpirationCache(int maxSize, long defaultExpirationTime2) {
        this.cache = new LruCache<>(maxSize);
        setDefaultExpirationTime(defaultExpirationTime2);
    }

    public void setDefaultExpirationTime(long defaultExpirationTime2) {
        if (defaultExpirationTime2 > 0) {
            this.defaultExpirationTime = defaultExpirationTime2;
            return;
        }
        throw new IllegalArgumentException();
    }

    public V put(K key, V value) {
        return put(key, value, this.defaultExpirationTime);
    }

    public V put(K key, V value, long expirationTime) {
        ExpireElement<V> eOld = (ExpireElement) this.cache.put(key, new ExpireElement(value, expirationTime));
        if (eOld == null) {
            return null;
        }
        return eOld.element;
    }

    public V lookup(K key) {
        return get(key);
    }

    @Deprecated
    public V get(Object key) {
        ExpireElement<V> v = (ExpireElement) this.cache.get(key);
        if (v == null) {
            return null;
        }
        if (!v.isExpired()) {
            return v.element;
        }
        remove(key);
        return null;
    }

    public V remove(Object key) {
        ExpireElement<V> e = (ExpireElement) this.cache.remove(key);
        if (e == null) {
            return null;
        }
        return e.element;
    }

    public int getMaxCacheSize() {
        return this.cache.getMaxCacheSize();
    }

    public void setMaxCacheSize(int maxCacheSize) {
        this.cache.setMaxCacheSize(maxCacheSize);
    }

    public int size() {
        return this.cache.size();
    }

    public boolean isEmpty() {
        return this.cache.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.cache.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.cache.containsValue(value);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        this.cache.clear();
    }

    public Set<K> keySet() {
        return this.cache.keySet();
    }

    public Collection<V> values() {
        Set<V> res = new HashSet<>();
        for (ExpireElement<V> value : this.cache.values()) {
            res.add(value.element);
        }
        return res;
    }

    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> res = new HashSet<>();
        for (Entry<K, ExpireElement<V>> entry : this.cache.entrySet()) {
            res.add(new EntryImpl(entry.getKey(), ((ExpireElement) entry.getValue()).element));
        }
        return res;
    }
}
