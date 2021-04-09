package org.jivesoftware.smack.util;

import java.lang.Exception;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventManger<K, R, E extends Exception> {
    private final Map<K, Reference<R>> events = new ConcurrentHashMap();

    public interface Callback<E extends Exception> {
        void action() throws Exception;
    }

    private static class Reference<V> {
        volatile V eventResult;

        private Reference() {
        }
    }

    public R performActionAndWaitForEvent(K eventKey, long timeout, Callback<E> action) throws InterruptedException, Exception {
        Reference<R> reference = new Reference<>();
        this.events.put(eventKey, reference);
        try {
            synchronized (reference) {
                action.action();
                reference.wait(timeout);
            }
            Object obj = reference.eventResult;
            this.events.remove(eventKey);
            return obj;
        } catch (Throwable th) {
            this.events.remove(eventKey);
            throw th;
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [R, V] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=R, code=null, for r4v0, types: [R, V] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean signalEvent(K r3, R r4) {
        /*
            r2 = this;
            java.util.Map<K, org.jivesoftware.smack.util.EventManger$Reference<R>> r0 = r2.events
            java.lang.Object r0 = r0.get(r3)
            org.jivesoftware.smack.util.EventManger$Reference r0 = (org.jivesoftware.smack.util.EventManger.Reference) r0
            if (r0 != 0) goto L_0x000c
            r1 = 0
            return r1
        L_0x000c:
            r0.eventResult = r4
            monitor-enter(r0)
            r0.notifyAll()     // Catch:{ all -> 0x0015 }
            monitor-exit(r0)     // Catch:{ all -> 0x0015 }
            r1 = 1
            return r1
        L_0x0015:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0015 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.util.EventManger.signalEvent(java.lang.Object, java.lang.Object):boolean");
    }
}
