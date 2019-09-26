package okhttp3.internal.http2;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import okio.AsyncTimeout;
import okio.Buffer;
import okio.BufferedSource;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public final class Http2Stream {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    long bytesLeftInWriteWindow;
    final Http2Connection connection;
    ErrorCode errorCode = null;
    private boolean hasResponseHeaders;
    final int id;
    final StreamTimeout readTimeout = new StreamTimeout();
    private final List<Header> requestHeaders;
    private List<Header> responseHeaders;
    final FramingSink sink;
    private final FramingSource source;
    long unacknowledgedBytesRead = 0;
    final StreamTimeout writeTimeout = new StreamTimeout();

    final class FramingSink implements Sink {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private static final long EMIT_BUFFER_SIZE = 16384;
        boolean closed;
        boolean finished;
        private final Buffer sendBuffer = new Buffer();

        static {
            Class<Http2Stream> cls = Http2Stream.class;
        }

        FramingSink() {
        }

        public void write(Buffer source, long byteCount) throws IOException {
            this.sendBuffer.write(source, byteCount);
            while (this.sendBuffer.size() >= EMIT_BUFFER_SIZE) {
                emitFrame(false);
            }
        }

        /* JADX INFO: finally extract failed */
        private void emitFrame(boolean outFinished) throws IOException {
            long toWrite;
            synchronized (Http2Stream.this) {
                Http2Stream.this.writeTimeout.enter();
                while (Http2Stream.this.bytesLeftInWriteWindow <= 0 && !this.finished && !this.closed && Http2Stream.this.errorCode == null) {
                    try {
                        Http2Stream.this.waitForIo();
                    } catch (Throwable th) {
                        Http2Stream.this.writeTimeout.exitAndThrowIfTimedOut();
                        throw th;
                    }
                }
                Http2Stream.this.writeTimeout.exitAndThrowIfTimedOut();
                Http2Stream.this.checkOutNotClosed();
                toWrite = Math.min(Http2Stream.this.bytesLeftInWriteWindow, this.sendBuffer.size());
                Http2Stream.this.bytesLeftInWriteWindow -= toWrite;
            }
            Http2Stream.this.writeTimeout.enter();
            try {
                Http2Stream.this.connection.writeData(Http2Stream.this.id, outFinished && toWrite == this.sendBuffer.size(), this.sendBuffer, toWrite);
            } finally {
                Http2Stream.this.writeTimeout.exitAndThrowIfTimedOut();
            }
        }

        public void flush() throws IOException {
            synchronized (Http2Stream.this) {
                Http2Stream.this.checkOutNotClosed();
            }
            while (this.sendBuffer.size() > 0) {
                emitFrame(false);
                Http2Stream.this.connection.flush();
            }
        }

        public Timeout timeout() {
            return Http2Stream.this.writeTimeout;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x001e, code lost:
            if (r8.sendBuffer.size() <= 0) goto L_0x002e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0028, code lost:
            if (r8.sendBuffer.size() <= 0) goto L_0x003d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x002a, code lost:
            emitFrame(true);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x002e, code lost:
            r8.this$0.connection.writeData(r8.this$0.id, true, null, 0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x003d, code lost:
            r2 = r8.this$0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x003f, code lost:
            monitor-enter(r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            r8.closed = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0042, code lost:
            monitor-exit(r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0043, code lost:
            r8.this$0.connection.flush();
            r8.this$0.cancelStreamIfNecessary();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x004f, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
            if (r8.this$0.sink.finished != false) goto L_0x003d;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void close() throws java.io.IOException {
            /*
                r8 = this;
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                monitor-enter(r0)
                boolean r1 = r8.closed     // Catch:{ all -> 0x0053 }
                if (r1 == 0) goto L_0x000a
                monitor-exit(r0)     // Catch:{ all -> 0x0053 }
                return
            L_0x000a:
                monitor-exit(r0)     // Catch:{ all -> 0x0053 }
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Stream$FramingSink r0 = r0.sink
                boolean r0 = r0.finished
                r1 = 1
                if (r0 != 0) goto L_0x003d
                okio.Buffer r0 = r8.sendBuffer
                long r2 = r0.size()
                r4 = 0
                int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r0 <= 0) goto L_0x002e
            L_0x0020:
                okio.Buffer r0 = r8.sendBuffer
                long r2 = r0.size()
                int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r0 <= 0) goto L_0x003d
                r8.emitFrame(r1)
                goto L_0x0020
            L_0x002e:
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Connection r2 = r0.connection
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                int r3 = r0.id
                r4 = 1
                r5 = 0
                r6 = 0
                r2.writeData(r3, r4, r5, r6)
            L_0x003d:
                okhttp3.internal.http2.Http2Stream r2 = okhttp3.internal.http2.Http2Stream.this
                monitor-enter(r2)
                r8.closed = r1     // Catch:{ all -> 0x0050 }
                monitor-exit(r2)     // Catch:{ all -> 0x0050 }
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Connection r0 = r0.connection
                r0.flush()
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                r0.cancelStreamIfNecessary()
                return
            L_0x0050:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0050 }
                throw r0
            L_0x0053:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0053 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.FramingSink.close():void");
        }
    }

    private final class FramingSource implements Source {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        boolean closed;
        boolean finished;
        private final long maxByteCount;
        private final Buffer readBuffer = new Buffer();
        private final Buffer receiveBuffer = new Buffer();

        static {
            Class<Http2Stream> cls = Http2Stream.class;
        }

        FramingSource(long maxByteCount2) {
            this.maxByteCount = maxByteCount2;
        }

        public long read(Buffer sink, long byteCount) throws IOException {
            ErrorCode errorCode;
            if (byteCount >= 0) {
                long read = -1;
                synchronized (Http2Stream.this) {
                    waitUntilReadable();
                    if (!this.closed) {
                        errorCode = Http2Stream.this.errorCode;
                        if (this.readBuffer.size() > 0) {
                            read = this.readBuffer.read(sink, Math.min(byteCount, this.readBuffer.size()));
                            Http2Stream.this.unacknowledgedBytesRead += read;
                        }
                        if (errorCode == null && Http2Stream.this.unacknowledgedBytesRead >= ((long) (Http2Stream.this.connection.okHttpSettings.getInitialWindowSize() / 2))) {
                            Http2Stream.this.connection.writeWindowUpdateLater(Http2Stream.this.id, Http2Stream.this.unacknowledgedBytesRead);
                            Http2Stream.this.unacknowledgedBytesRead = 0;
                        }
                    } else {
                        throw new IOException("stream closed");
                    }
                }
                if (read != -1) {
                    updateConnectionFlowControl(read);
                    return read;
                } else if (errorCode == null) {
                    return -1;
                } else {
                    throw new StreamResetException(errorCode);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("byteCount < 0: ");
                sb.append(byteCount);
                throw new IllegalArgumentException(sb.toString());
            }
        }

        private void updateConnectionFlowControl(long read) {
            Http2Stream.this.connection.updateConnectionFlowControl(read);
        }

        private void waitUntilReadable() throws IOException {
            Http2Stream.this.readTimeout.enter();
            while (this.readBuffer.size() == 0 && !this.finished && !this.closed && Http2Stream.this.errorCode == null) {
                try {
                    Http2Stream.this.waitForIo();
                } finally {
                    Http2Stream.this.readTimeout.exitAndThrowIfTimedOut();
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void receive(BufferedSource in, long byteCount) throws IOException {
            boolean finished2;
            boolean z;
            boolean flowControlError;
            while (byteCount > 0) {
                synchronized (Http2Stream.this) {
                    finished2 = this.finished;
                    z = true;
                    flowControlError = this.readBuffer.size() + byteCount > this.maxByteCount;
                }
                if (flowControlError) {
                    in.skip(byteCount);
                    Http2Stream.this.closeLater(ErrorCode.FLOW_CONTROL_ERROR);
                    return;
                } else if (finished2) {
                    in.skip(byteCount);
                    return;
                } else {
                    long read = in.read(this.receiveBuffer, byteCount);
                    if (read != -1) {
                        long byteCount2 = byteCount - read;
                        synchronized (Http2Stream.this) {
                            if (this.readBuffer.size() != 0) {
                                z = false;
                            }
                            boolean wasEmpty = z;
                            this.readBuffer.writeAll(this.receiveBuffer);
                            if (wasEmpty) {
                                Http2Stream.this.notifyAll();
                            }
                        }
                        byteCount = byteCount2;
                    } else {
                        throw new EOFException();
                    }
                }
            }
        }

        public Timeout timeout() {
            return Http2Stream.this.readTimeout;
        }

        public void close() throws IOException {
            long bytesDiscarded;
            synchronized (Http2Stream.this) {
                this.closed = true;
                bytesDiscarded = this.readBuffer.size();
                this.readBuffer.clear();
                Http2Stream.this.notifyAll();
            }
            if (bytesDiscarded > 0) {
                updateConnectionFlowControl(bytesDiscarded);
            }
            Http2Stream.this.cancelStreamIfNecessary();
        }
    }

    class StreamTimeout extends AsyncTimeout {
        StreamTimeout() {
        }

        /* access modifiers changed from: protected */
        public void timedOut() {
            Http2Stream.this.closeLater(ErrorCode.CANCEL);
        }

        /* access modifiers changed from: protected */
        public IOException newTimeoutException(IOException cause) {
            SocketTimeoutException socketTimeoutException = new SocketTimeoutException("timeout");
            if (cause != null) {
                socketTimeoutException.initCause(cause);
            }
            return socketTimeoutException;
        }

        public void exitAndThrowIfTimedOut() throws IOException {
            if (exit()) {
                throw newTimeoutException(null);
            }
        }
    }

    Http2Stream(int id2, Http2Connection connection2, boolean outFinished, boolean inFinished, List<Header> requestHeaders2) {
        if (connection2 == null) {
            throw new NullPointerException("connection == null");
        } else if (requestHeaders2 != null) {
            this.id = id2;
            this.connection = connection2;
            this.bytesLeftInWriteWindow = (long) connection2.peerSettings.getInitialWindowSize();
            this.source = new FramingSource((long) connection2.okHttpSettings.getInitialWindowSize());
            this.sink = new FramingSink();
            this.source.finished = inFinished;
            this.sink.finished = outFinished;
            this.requestHeaders = requestHeaders2;
        } else {
            throw new NullPointerException("requestHeaders == null");
        }
    }

    public int getId() {
        return this.id;
    }

    public synchronized boolean isOpen() {
        if (this.errorCode != null) {
            return false;
        }
        if ((this.source.finished || this.source.closed) && ((this.sink.finished || this.sink.closed) && this.hasResponseHeaders)) {
            return false;
        }
        return true;
    }

    public boolean isLocallyInitiated() {
        if (this.connection.client == ((this.id & 1) == 1)) {
            return true;
        }
        return false;
    }

    public Http2Connection getConnection() {
        return this.connection;
    }

    public List<Header> getRequestHeaders() {
        return this.requestHeaders;
    }

    public synchronized List<Header> takeResponseHeaders() throws IOException {
        List<Header> result;
        if (isLocallyInitiated()) {
            this.readTimeout.enter();
            while (this.responseHeaders == null) {
                try {
                    try {
                        if (this.errorCode == null) {
                            waitForIo();
                        }
                    } catch (Throwable th) {
                        th = th;
                        this.readTimeout.exitAndThrowIfTimedOut();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    this.readTimeout.exitAndThrowIfTimedOut();
                    throw th;
                }
            }
            this.readTimeout.exitAndThrowIfTimedOut();
            result = this.responseHeaders;
            if (result != null) {
                this.responseHeaders = null;
            } else {
                throw new StreamResetException(this.errorCode);
            }
        } else {
            throw new IllegalStateException("servers cannot read response headers");
        }
        return result;
    }

    public synchronized ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public void sendResponseHeaders(List<Header> responseHeaders2, boolean out) throws IOException {
        boolean z;
        if (responseHeaders2 != null) {
            boolean outFinished = false;
            boolean flushHeaders = false;
            synchronized (this) {
                z = true;
                this.hasResponseHeaders = true;
                if (!out) {
                    this.sink.finished = true;
                    flushHeaders = true;
                    outFinished = true;
                }
            }
            if (!flushHeaders) {
                synchronized (this.connection) {
                    if (this.connection.bytesLeftInWriteWindow != 0) {
                        z = false;
                    }
                    flushHeaders = z;
                }
            }
            this.connection.writeSynReply(this.id, outFinished, responseHeaders2);
            if (flushHeaders) {
                this.connection.flush();
                return;
            }
            return;
        }
        throw new NullPointerException("responseHeaders == null");
    }

    public Timeout readTimeout() {
        return this.readTimeout;
    }

    public Timeout writeTimeout() {
        return this.writeTimeout;
    }

    public Source getSource() {
        return this.source;
    }

    public Sink getSink() {
        synchronized (this) {
            if (!this.hasResponseHeaders) {
                if (!isLocallyInitiated()) {
                    throw new IllegalStateException("reply before requesting the sink");
                }
            }
        }
        return this.sink;
    }

    public void close(ErrorCode rstStatusCode) throws IOException {
        if (closeInternal(rstStatusCode)) {
            this.connection.writeSynReset(this.id, rstStatusCode);
        }
    }

    public void closeLater(ErrorCode errorCode2) {
        if (closeInternal(errorCode2)) {
            this.connection.writeSynResetLater(this.id, errorCode2);
        }
    }

    private boolean closeInternal(ErrorCode errorCode2) {
        synchronized (this) {
            if (this.errorCode != null) {
                return false;
            }
            if (this.source.finished && this.sink.finished) {
                return false;
            }
            this.errorCode = errorCode2;
            notifyAll();
            this.connection.removeStream(this.id);
            return true;
        }
    }

    /* access modifiers changed from: 0000 */
    public void receiveHeaders(List<Header> headers) {
        boolean open = true;
        synchronized (this) {
            this.hasResponseHeaders = true;
            if (this.responseHeaders == null) {
                this.responseHeaders = headers;
                open = isOpen();
                notifyAll();
            } else {
                List<Header> newHeaders = new ArrayList<>();
                newHeaders.addAll(this.responseHeaders);
                newHeaders.add(null);
                newHeaders.addAll(headers);
                this.responseHeaders = newHeaders;
            }
        }
        if (!open) {
            this.connection.removeStream(this.id);
        }
    }

    /* access modifiers changed from: 0000 */
    public void receiveData(BufferedSource in, int length) throws IOException {
        this.source.receive(in, (long) length);
    }

    /* access modifiers changed from: 0000 */
    public void receiveFin() {
        boolean open;
        synchronized (this) {
            this.source.finished = true;
            open = isOpen();
            notifyAll();
        }
        if (!open) {
            this.connection.removeStream(this.id);
        }
    }

    /* access modifiers changed from: 0000 */
    public synchronized void receiveRstStream(ErrorCode errorCode2) {
        if (this.errorCode == null) {
            this.errorCode = errorCode2;
            notifyAll();
        }
    }

    /* access modifiers changed from: 0000 */
    public void cancelStreamIfNecessary() throws IOException {
        boolean cancel;
        boolean open;
        synchronized (this) {
            cancel = !this.source.finished && this.source.closed && (this.sink.finished || this.sink.closed);
            open = isOpen();
        }
        if (cancel) {
            close(ErrorCode.CANCEL);
        } else if (!open) {
            this.connection.removeStream(this.id);
        }
    }

    /* access modifiers changed from: 0000 */
    public void addBytesToWriteWindow(long delta) {
        this.bytesLeftInWriteWindow += delta;
        if (delta > 0) {
            notifyAll();
        }
    }

    /* access modifiers changed from: 0000 */
    public void checkOutNotClosed() throws IOException {
        if (this.sink.closed) {
            throw new IOException("stream closed");
        } else if (!this.sink.finished) {
            ErrorCode errorCode2 = this.errorCode;
            if (errorCode2 != null) {
                throw new StreamResetException(errorCode2);
            }
        } else {
            throw new IOException("stream finished");
        }
    }

    /* access modifiers changed from: 0000 */
    public void waitForIo() throws InterruptedIOException {
        try {
            wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException();
        }
    }
}
