package org.jivesoftware.smack.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class CleaningWeakReferenceMap<K, V> extends HashMap<K, WeakReference<V>> {
    private static final long serialVersionUID = 0;
    private final int cleanInterval;
    private int numberOfInsertsSinceLastClean;

    public CleaningWeakReferenceMap() {
        this(50);
    }

    public CleaningWeakReferenceMap(int cleanInterval2) {
        this.numberOfInsertsSinceLastClean = 0;
        this.cleanInterval = cleanInterval2;
    }

    public WeakReference<V> put(K key, WeakReference<V> value) {
        WeakReference<V> ret = (WeakReference) super.put(key, value);
        int i = this.numberOfInsertsSinceLastClean;
        this.numberOfInsertsSinceLastClean = i + 1;
        if (i > this.cleanInterval) {
            this.numberOfInsertsSinceLastClean = 0;
            clean();
        }
        return ret;
    }

    private void clean() {
        Iterator<Entry<K, WeakReference<V>>> iter = entrySet().iterator();
        while (iter.hasNext()) {
            Entry<K, WeakReference<V>> e = (Entry) iter.next();
            if (!(e == null || e.getValue() == null || ((WeakReference) e.getValue()).get() != null)) {
                iter.remove();
            }
        }
    }
}
