package org.minidns;

import java.lang.Exception;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.minidns.util.CallbackRecipient;
import org.minidns.util.ExceptionCallback;
import org.minidns.util.SuccessCallback;

public abstract class MiniDnsFuture<V, E extends Exception> implements Future<V>, CallbackRecipient<V, E> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final ExecutorService EXECUTOR_SERVICE;
    private boolean cancelled;
    protected E exception;
    /* access modifiers changed from: private */
    public ExceptionCallback<E> exceptionCallback;
    protected V result;
    /* access modifiers changed from: private */
    public SuccessCallback<V> successCallback;

    public static class InternalMiniDnsFuture<V, E extends Exception> extends MiniDnsFuture<V, E> {
        public final synchronized void setResult(V result) {
            this.result = result;
            notifyAll();
            maybeInvokeCallbacks();
        }

        public final synchronized void setException(E exception) {
            this.exception = exception;
            notifyAll();
            maybeInvokeCallbacks();
        }
    }

    static {
        ThreadFactory threadFactory = new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("MiniDnsFuture Thread");
                return thread;
            }
        };
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(128);
        RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                r.run();
            }
        };
        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, cores <= 4 ? 2 : cores, 60, TimeUnit.SECONDS, blockingQueue, threadFactory, rejectedExecutionHandler);
        EXECUTOR_SERVICE = threadPoolExecutor;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0013, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean cancel(boolean r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            boolean r0 = r1.isDone()     // Catch:{ all -> 0x0014 }
            if (r0 == 0) goto L_0x000a
            r0 = 0
            monitor-exit(r1)
            return r0
        L_0x000a:
            r0 = 1
            r1.cancelled = r0     // Catch:{ all -> 0x0014 }
            if (r2 == 0) goto L_0x0012
            r1.notifyAll()     // Catch:{ all -> 0x0014 }
        L_0x0012:
            monitor-exit(r1)
            return r0
        L_0x0014:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.minidns.MiniDnsFuture.cancel(boolean):boolean");
    }

    public final synchronized boolean isCancelled() {
        return this.cancelled;
    }

    public final synchronized boolean isDone() {
        return this.result != null;
    }

    public CallbackRecipient<V, E> onSuccess(SuccessCallback<V> successCallback2) {
        this.successCallback = successCallback2;
        maybeInvokeCallbacks();
        return this;
    }

    public CallbackRecipient<V, E> onError(ExceptionCallback<E> exceptionCallback2) {
        this.exceptionCallback = exceptionCallback2;
        maybeInvokeCallbacks();
        return this;
    }

    private final V getOrThrowExecutionException() throws ExecutionException {
        V v = this.result;
        if (v != null) {
            return v;
        }
        E e = this.exception;
        if (e == null) {
            throw new CancellationException();
        }
        throw new ExecutionException(e);
    }

    public final synchronized V get() throws InterruptedException, ExecutionException {
        while (this.result == null && this.exception == null && !this.cancelled) {
            wait();
        }
        return getOrThrowExecutionException();
    }

    public final synchronized V getOrThrow() throws Exception {
        while (this.result == null && this.exception == null && !this.cancelled) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (this.exception != null) {
            throw this.exception;
        } else if (!this.cancelled) {
        } else {
            throw new CancellationException();
        }
        return this.result;
    }

    public final synchronized V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
        while (this.result != null && this.exception != null && !this.cancelled) {
            long waitTimeRemaining = deadline - System.currentTimeMillis();
            if (waitTimeRemaining > 0) {
                wait(waitTimeRemaining);
            }
        }
        if (this.cancelled) {
            throw new CancellationException();
        } else if (this.result == null || this.exception == null) {
            throw new TimeoutException();
        }
        return getOrThrowExecutionException();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized void maybeInvokeCallbacks() {
        /*
            r2 = this;
            monitor-enter(r2)
            boolean r0 = r2.cancelled     // Catch:{ all -> 0x002e }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r2)
            return
        L_0x0007:
            V r0 = r2.result     // Catch:{ all -> 0x002e }
            if (r0 == 0) goto L_0x001a
            org.minidns.util.SuccessCallback<V> r0 = r2.successCallback     // Catch:{ all -> 0x002e }
            if (r0 == 0) goto L_0x001a
            java.util.concurrent.ExecutorService r0 = EXECUTOR_SERVICE     // Catch:{ all -> 0x002e }
            org.minidns.MiniDnsFuture$3 r1 = new org.minidns.MiniDnsFuture$3     // Catch:{ all -> 0x002e }
            r1.<init>()     // Catch:{ all -> 0x002e }
            r0.submit(r1)     // Catch:{ all -> 0x002e }
            goto L_0x002c
        L_0x001a:
            E r0 = r2.exception     // Catch:{ all -> 0x002e }
            if (r0 == 0) goto L_0x002c
            org.minidns.util.ExceptionCallback<E> r0 = r2.exceptionCallback     // Catch:{ all -> 0x002e }
            if (r0 == 0) goto L_0x002c
            java.util.concurrent.ExecutorService r0 = EXECUTOR_SERVICE     // Catch:{ all -> 0x002e }
            org.minidns.MiniDnsFuture$4 r1 = new org.minidns.MiniDnsFuture$4     // Catch:{ all -> 0x002e }
            r1.<init>()     // Catch:{ all -> 0x002e }
            r0.submit(r1)     // Catch:{ all -> 0x002e }
        L_0x002c:
            monitor-exit(r2)
            return
        L_0x002e:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.minidns.MiniDnsFuture.maybeInvokeCallbacks():void");
    }

    public static <V, E extends Exception> MiniDnsFuture<V, E> from(V result2) {
        InternalMiniDnsFuture<V, E> future = new InternalMiniDnsFuture<>();
        future.setResult(result2);
        return future;
    }
}
