package org.jivesoftware.smack.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MultiMap<K, V> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final int DEFAULT_MAP_SIZE = 6;
    private static final int ENTRY_LIST_SIZE = 3;
    private final Map<K, List<V>> map;

    private static final class SimpleMapEntry<K, V> implements Entry<K, V> {
        private final K key;
        private V value;

        private SimpleMapEntry(K key2, V value2) {
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
            V tmp = this.value;
            this.value = value2;
            return tmp;
        }
    }

    public MultiMap() {
        this(6);
    }

    public MultiMap(int size) {
        this.map = new LinkedHashMap(size);
    }

    public int size() {
        int size = 0;
        for (List<V> list : this.map.values()) {
            size += list.size();
        }
        return size;
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        for (List<V> list : this.map.values()) {
            if (list.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public V getFirst(Object key) {
        List<V> res = getAll(key);
        if (res.isEmpty()) {
            return null;
        }
        return res.iterator().next();
    }

    public List<V> getAll(Object key) {
        List<V> res = (List) this.map.get(key);
        if (res == null) {
            return Collections.emptyList();
        }
        return res;
    }

    public boolean put(K key, V value) {
        List<V> list = (List) this.map.get(key);
        if (list == null) {
            List<V> list2 = new ArrayList<>(3);
            list2.add(value);
            this.map.put(key, list2);
            return false;
        }
        list.add(value);
        return true;
    }

    public V remove(Object key) {
        List<V> res = (List) this.map.remove(key);
        if (res == null) {
            return null;
        }
        return res.iterator().next();
    }

    public boolean removeOne(Object key, V value) {
        List<V> list = (List) this.map.get(key);
        if (list == null) {
            return false;
        }
        boolean res = list.remove(value);
        if (list.isEmpty()) {
            this.map.remove(key);
        }
        return res;
    }

    public void putAll(Map<? extends K, ? extends V> map2) {
        for (Entry<? extends K, ? extends V> entry : map2.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        this.map.clear();
    }

    public Set<K> keySet() {
        return this.map.keySet();
    }

    public List<V> values() {
        List<V> values = new ArrayList<>(size());
        for (List<V> list : this.map.values()) {
            values.addAll(list);
        }
        return values;
    }

    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entrySet = new LinkedHashSet<>(size());
        for (Entry<K, List<V>> entries : this.map.entrySet()) {
            K key = entries.getKey();
            for (V value : (List) entries.getValue()) {
                entrySet.add(new SimpleMapEntry(key, value));
            }
        }
        return entrySet;
    }
}
