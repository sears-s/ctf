package org.jivesoftware.smack;

import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class AsyncButOrdered<K> {
    private final Executor executor;
    private final Map<K, Queue<Runnable>> pendingRunnables;
    /* access modifiers changed from: private */
    public final Map<K, Boolean> threadActiveMap;

    private class Handler implements Runnable {
        private final K key;
        private final Queue<Runnable> keyQueue;

        Handler(Queue<Runnable> keyQueue2, K key2) {
            this.keyQueue = keyQueue2;
            this.key = key2;
        }

        public void run() {
            while (true) {
                while (true) {
                    Runnable runnable = (Runnable) this.keyQueue.poll();
                    Runnable runnable2 = runnable;
                    if (runnable == null) {
                        break;
                    }
                    try {
                        runnable2.run();
                    } catch (Throwable t) {
                        synchronized (AsyncButOrdered.this.threadActiveMap) {
                            AsyncButOrdered.this.threadActiveMap.put(this.key, Boolean.valueOf(false));
                            throw t;
                        }
                    }
                }
                synchronized (AsyncButOrdered.this.threadActiveMap) {
                    if (this.keyQueue.isEmpty()) {
                        AsyncButOrdered.this.threadActiveMap.put(this.key, Boolean.valueOf(false));
                        return;
                    }
                }
            }
        }
    }

    public AsyncButOrdered() {
        this(null);
    }

    public AsyncButOrdered(Executor executor2) {
        this.pendingRunnables = new WeakHashMap();
        this.threadActiveMap = new WeakHashMap();
        this.executor = executor2;
    }

    public boolean performAsyncButOrdered(K key, Runnable runnable) {
        Queue queue;
        boolean newHandler;
        synchronized (this.pendingRunnables) {
            queue = (Queue) this.pendingRunnables.get(key);
            if (queue == null) {
                queue = new ConcurrentLinkedQueue();
                this.pendingRunnables.put(key, queue);
            }
        }
        queue.add(runnable);
        synchronized (this.threadActiveMap) {
            Boolean threadActive = (Boolean) this.threadActiveMap.get(key);
            if (threadActive == null) {
                threadActive = Boolean.valueOf(false);
                this.threadActiveMap.put(key, threadActive);
            }
            newHandler = !threadActive.booleanValue();
            if (newHandler) {
                Handler handler = new Handler<>(queue, key);
                this.threadActiveMap.put(key, Boolean.valueOf(true));
                if (this.executor == null) {
                    AbstractXMPPConnection.asyncGo(handler);
                } else {
                    this.executor.execute(handler);
                }
            }
        }
        return newHandler;
    }

    public Executor asExecutorFor(final K key) {
        return new Executor() {
            public void execute(Runnable runnable) {
                AsyncButOrdered.this.performAsyncButOrdered(key, runnable);
            }
        };
    }
}
