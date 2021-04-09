package org.bouncycastle.cert.dane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

public class DANEEntryStore implements Store {
    private final Map entries;

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.List, code=java.util.List<org.bouncycastle.cert.dane.DANEEntry>, for r4v0, types: [java.util.List, java.util.List<org.bouncycastle.cert.dane.DANEEntry>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    DANEEntryStore(java.util.List<org.bouncycastle.cert.dane.DANEEntry> r4) {
        /*
            r3 = this;
            r3.<init>()
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            java.util.Iterator r4 = r4.iterator()
        L_0x000c:
            boolean r1 = r4.hasNext()
            if (r1 == 0) goto L_0x0020
            java.lang.Object r1 = r4.next()
            org.bouncycastle.cert.dane.DANEEntry r1 = (org.bouncycastle.cert.dane.DANEEntry) r1
            java.lang.String r2 = r1.getDomainName()
            r0.put(r2, r1)
            goto L_0x000c
        L_0x0020:
            java.util.Map r4 = java.util.Collections.unmodifiableMap(r0)
            r3.entries = r4
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.cert.dane.DANEEntryStore.<init>(java.util.List):void");
    }

    public Collection getMatches(Selector selector) throws StoreException {
        if (selector == null) {
            return this.entries.values();
        }
        ArrayList arrayList = new ArrayList();
        for (Object next : this.entries.values()) {
            if (selector.match(next)) {
                arrayList.add(next);
            }
        }
        return Collections.unmodifiableList(arrayList);
    }

    public Store toCertificateStore() {
        Collection<DANEEntry> matches = getMatches(null);
        ArrayList arrayList = new ArrayList(matches.size());
        for (DANEEntry certificate : matches) {
            arrayList.add(certificate.getCertificate());
        }
        return new CollectionStore(arrayList);
    }
}
