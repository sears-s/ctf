package org.jivesoftware.smack;

import java.io.IOException;
import java.lang.Exception;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.CallbackRecipient;
import org.jivesoftware.smack.util.ExceptionCallback;
import org.jivesoftware.smack.util.SuccessCallback;

public abstract class SmackFuture<V, E extends Exception> implements Future<V>, CallbackRecipient<V, E> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(SmackFuture.class.getName());
    private boolean cancelled;
    protected E exception;
    /* access modifiers changed from: private */
    public ExceptionCallback<E> exceptionCallback;
    protected V result;
    /* access modifiers changed from: private */
    public SuccessCallback<V> successCallback;

    public static abstract class InternalProcessStanzaSmackFuture<V, E extends Exception> extends InternalSmackFuture<V, E> implements StanzaListener, ExceptionCallback<E> {
        /* access modifiers changed from: protected */
        public abstract void handleStanza(Stanza stanza);

        /* access modifiers changed from: protected */
        public abstract boolean isNonFatalException(E e);

        public final synchronized void processException(E exception) {
            if (!isNonFatalException(exception)) {
                this.exception = exception;
                notifyAll();
                maybeInvokeCallbacks();
            }
        }

        public final synchronized void processStanza(Stanza stanza) {
            handleStanza(stanza);
        }
    }

    public static class InternalSmackFuture<V, E extends Exception> extends SmackFuture<V, E> {
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

    public static abstract class SimpleInternalProcessStanzaSmackFuture<V, E extends Exception> extends InternalProcessStanzaSmackFuture<V, E> {
        /* access modifiers changed from: protected */
        public boolean isNonFatalException(E e) {
            return false;
        }
    }

    public static class SocketFuture extends InternalSmackFuture<Socket, IOException> {
        /* access modifiers changed from: private */
        public final Socket socket;
        /* access modifiers changed from: private */
        public boolean wasInterrupted;
        /* access modifiers changed from: private */
        public final Object wasInterruptedLock = new Object();

        public SocketFuture(SocketFactory socketFactory) throws IOException {
            this.socket = socketFactory.createSocket();
        }

        /* access modifiers changed from: protected */
        public void futureWait(long timeout) throws InterruptedException {
            try {
                super.futureWait(timeout);
            } catch (InterruptedException interruptedException) {
                synchronized (this.wasInterruptedLock) {
                    this.wasInterrupted = true;
                    if (!this.socket.isClosed()) {
                        closeSocket();
                    }
                    throw interruptedException;
                }
            }
        }

        public void connectAsync(final SocketAddress socketAddress, final int timeout) {
            AbstractXMPPConnection.asyncGo(new Runnable() {
                public void run() {
                    try {
                        SocketFuture.this.socket.connect(socketAddress, timeout);
                        synchronized (SocketFuture.this.wasInterruptedLock) {
                            if (SocketFuture.this.wasInterrupted) {
                                SocketFuture.this.closeSocket();
                                return;
                            }
                            SocketFuture socketFuture = SocketFuture.this;
                            socketFuture.setResult(socketFuture.socket);
                        }
                    } catch (IOException e) {
                        SocketFuture.this.setException(e);
                    }
                }
            });
        }

        /* access modifiers changed from: private */
        public void closeSocket() {
            try {
                this.socket.close();
            } catch (IOException ioException) {
                SmackFuture.LOGGER.log(Level.WARNING, "Could not close socket", ioException);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0013, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized boolean cancel(boolean r2) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.SmackFuture.cancel(boolean):boolean");
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

    private V getOrThrowExecutionException() throws ExecutionException {
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
            futureWait();
        }
        return getOrThrowExecutionException();
    }

    public final synchronized V getOrThrow() throws Exception, InterruptedException {
        while (this.result == null && this.exception == null && !this.cancelled) {
            futureWait();
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
        while (this.result != null && this.exception != null) {
            long waitTimeRemaining = deadline - System.currentTimeMillis();
            if (waitTimeRemaining > 0) {
                futureWait(waitTimeRemaining);
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
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0029, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized void maybeInvokeCallbacks() {
        /*
            r1 = this;
            monitor-enter(r1)
            boolean r0 = r1.cancelled     // Catch:{ all -> 0x002a }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r1)
            return
        L_0x0007:
            V r0 = r1.result     // Catch:{ all -> 0x002a }
            if (r0 == 0) goto L_0x0018
            org.jivesoftware.smack.util.SuccessCallback<V> r0 = r1.successCallback     // Catch:{ all -> 0x002a }
            if (r0 == 0) goto L_0x0018
            org.jivesoftware.smack.SmackFuture$1 r0 = new org.jivesoftware.smack.SmackFuture$1     // Catch:{ all -> 0x002a }
            r0.<init>()     // Catch:{ all -> 0x002a }
            org.jivesoftware.smack.AbstractXMPPConnection.asyncGo(r0)     // Catch:{ all -> 0x002a }
            goto L_0x0028
        L_0x0018:
            E r0 = r1.exception     // Catch:{ all -> 0x002a }
            if (r0 == 0) goto L_0x0028
            org.jivesoftware.smack.util.ExceptionCallback<E> r0 = r1.exceptionCallback     // Catch:{ all -> 0x002a }
            if (r0 == 0) goto L_0x0028
            org.jivesoftware.smack.SmackFuture$2 r0 = new org.jivesoftware.smack.SmackFuture$2     // Catch:{ all -> 0x002a }
            r0.<init>()     // Catch:{ all -> 0x002a }
            org.jivesoftware.smack.AbstractXMPPConnection.asyncGo(r0)     // Catch:{ all -> 0x002a }
        L_0x0028:
            monitor-exit(r1)
            return
        L_0x002a:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.SmackFuture.maybeInvokeCallbacks():void");
    }

    /* access modifiers changed from: protected */
    public final void futureWait() throws InterruptedException {
        futureWait(0);
    }

    /* access modifiers changed from: protected */
    public void futureWait(long timeout) throws InterruptedException {
        wait(timeout);
    }

    public static <V, E extends Exception> SmackFuture<V, E> from(V result2) {
        InternalSmackFuture<V, E> future = new InternalSmackFuture<>();
        future.setResult(result2);
        return future;
    }
}
