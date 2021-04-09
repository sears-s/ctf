package org.jivesoftware.smackx.bytestreams.ibb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jivesoftware.smackx.bytestreams.BytestreamSession;
import org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager.StanzaType;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Close;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Data;
import org.jivesoftware.smackx.bytestreams.ibb.packet.DataPacketExtension;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Open;
import org.jxmpp.jid.Jid;

public class InBandBytestreamSession implements BytestreamSession {
    /* access modifiers changed from: private */
    public final Open byteStreamRequest;
    private boolean closeBothStreamsEnabled = false;
    /* access modifiers changed from: private */
    public final XMPPConnection connection;
    private IBBInputStream inputStream;
    private boolean isClosed = false;
    private IBBOutputStream outputStream;
    /* access modifiers changed from: private */
    public Jid remoteJID;

    /* renamed from: org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamSession$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smackx$bytestreams$ibb$InBandBytestreamManager$StanzaType = new int[StanzaType.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smackx$bytestreams$ibb$InBandBytestreamManager$StanzaType[StanzaType.IQ.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smackx$bytestreams$ibb$InBandBytestreamManager$StanzaType[StanzaType.MESSAGE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private class IBBDataPacketFilter implements StanzaFilter {
        private IBBDataPacketFilter() {
        }

        /* synthetic */ IBBDataPacketFilter(InBandBytestreamSession x0, AnonymousClass1 x1) {
            this();
        }

        public boolean accept(Stanza packet) {
            DataPacketExtension data;
            if (!packet.getFrom().equals((CharSequence) InBandBytestreamSession.this.remoteJID)) {
                return false;
            }
            if (packet instanceof Data) {
                data = ((Data) packet).getDataPacketExtension();
            } else {
                data = (DataPacketExtension) packet.getExtension("data", "http://jabber.org/protocol/ibb");
                if (data == null) {
                    return false;
                }
            }
            if (!data.getSessionID().equals(InBandBytestreamSession.this.byteStreamRequest.getSessionID())) {
                return false;
            }
            return true;
        }
    }

    private abstract class IBBInputStream extends InputStream {
        private byte[] buffer;
        private int bufferPointer = -1;
        private boolean closeInvoked = false;
        /* access modifiers changed from: private */
        public final StanzaListener dataPacketListener = getDataPacketListener();
        protected final BlockingQueue<DataPacketExtension> dataQueue = new LinkedBlockingQueue();
        /* access modifiers changed from: private */
        public boolean isClosed = false;
        /* access modifiers changed from: private */
        public int readTimeout = 0;
        private long seq = -1;

        /* access modifiers changed from: protected */
        public abstract StanzaFilter getDataPacketFilter();

        /* access modifiers changed from: protected */
        public abstract StanzaListener getDataPacketListener();

        protected IBBInputStream() {
            InBandBytestreamSession.this.connection.addSyncStanzaListener(this.dataPacketListener, getDataPacketFilter());
        }

        public synchronized int read() throws IOException {
            checkClosed();
            if ((this.bufferPointer == -1 || this.bufferPointer >= this.buffer.length) && !loadBuffer()) {
                return -1;
            }
            byte[] bArr = this.buffer;
            int i = this.bufferPointer;
            this.bufferPointer = i + 1;
            return bArr[i] & 255;
        }

        /* JADX WARNING: Unknown top exception splitter block from list: {B:31:0x0046=Splitter:B:31:0x0046, B:24:0x002f=Splitter:B:24:0x002f} */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized int read(byte[] r4, int r5, int r6) throws java.io.IOException {
            /*
                r3 = this;
                monitor-enter(r3)
                if (r4 == 0) goto L_0x004c
                if (r5 < 0) goto L_0x0046
                int r0 = r4.length     // Catch:{ all -> 0x0052 }
                if (r5 > r0) goto L_0x0046
                if (r6 < 0) goto L_0x0046
                int r0 = r5 + r6
                int r1 = r4.length     // Catch:{ all -> 0x0052 }
                if (r0 > r1) goto L_0x0046
                int r0 = r5 + r6
                if (r0 < 0) goto L_0x0046
                if (r6 != 0) goto L_0x0018
                r0 = 0
                monitor-exit(r3)
                return r0
            L_0x0018:
                r3.checkClosed()     // Catch:{ all -> 0x0052 }
                int r0 = r3.bufferPointer     // Catch:{ all -> 0x0052 }
                r1 = -1
                if (r0 == r1) goto L_0x0027
                int r0 = r3.bufferPointer     // Catch:{ all -> 0x0052 }
                byte[] r2 = r3.buffer     // Catch:{ all -> 0x0052 }
                int r2 = r2.length     // Catch:{ all -> 0x0052 }
                if (r0 < r2) goto L_0x002f
            L_0x0027:
                boolean r0 = r3.loadBuffer()     // Catch:{ all -> 0x0052 }
                if (r0 != 0) goto L_0x002f
                monitor-exit(r3)
                return r1
            L_0x002f:
                byte[] r0 = r3.buffer     // Catch:{ all -> 0x0052 }
                int r0 = r0.length     // Catch:{ all -> 0x0052 }
                int r1 = r3.bufferPointer     // Catch:{ all -> 0x0052 }
                int r0 = r0 - r1
                if (r6 <= r0) goto L_0x0038
                r6 = r0
            L_0x0038:
                byte[] r1 = r3.buffer     // Catch:{ all -> 0x0052 }
                int r2 = r3.bufferPointer     // Catch:{ all -> 0x0052 }
                java.lang.System.arraycopy(r1, r2, r4, r5, r6)     // Catch:{ all -> 0x0052 }
                int r1 = r3.bufferPointer     // Catch:{ all -> 0x0052 }
                int r1 = r1 + r6
                r3.bufferPointer = r1     // Catch:{ all -> 0x0052 }
                monitor-exit(r3)
                return r6
            L_0x0046:
                java.lang.IndexOutOfBoundsException r0 = new java.lang.IndexOutOfBoundsException     // Catch:{ all -> 0x0052 }
                r0.<init>()     // Catch:{ all -> 0x0052 }
                throw r0     // Catch:{ all -> 0x0052 }
            L_0x004c:
                java.lang.NullPointerException r0 = new java.lang.NullPointerException     // Catch:{ all -> 0x0052 }
                r0.<init>()     // Catch:{ all -> 0x0052 }
                throw r0     // Catch:{ all -> 0x0052 }
            L_0x0052:
                r4 = move-exception
                monitor-exit(r3)
                throw r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamSession.IBBInputStream.read(byte[], int, int):int");
        }

        public synchronized int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        private synchronized boolean loadBuffer() throws IOException {
            DataPacketExtension data = null;
            try {
                if (this.readTimeout == 0) {
                    while (data == null) {
                        if (this.isClosed && this.dataQueue.isEmpty()) {
                            return false;
                        }
                        data = (DataPacketExtension) this.dataQueue.poll(1000, TimeUnit.MILLISECONDS);
                    }
                } else {
                    data = (DataPacketExtension) this.dataQueue.poll((long) this.readTimeout, TimeUnit.MILLISECONDS);
                    if (data == null) {
                        throw new SocketTimeoutException();
                    }
                }
                if (this.seq == 65535) {
                    this.seq = -1;
                }
                long seq2 = data.getSeq();
                if (seq2 - 1 == this.seq) {
                    this.seq = seq2;
                    this.buffer = data.getDecodedData();
                    this.bufferPointer = 0;
                    return true;
                }
                InBandBytestreamSession.this.close();
                throw new IOException("Packets out of sequence");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        private void checkClosed() throws IOException {
            if (this.closeInvoked) {
                this.dataQueue.clear();
                throw new IOException("Stream is closed");
            }
        }

        public boolean markSupported() {
            return false;
        }

        public void close() throws IOException {
            if (!this.closeInvoked) {
                this.closeInvoked = true;
                InBandBytestreamSession.this.closeByLocal(true);
            }
        }

        /* access modifiers changed from: private */
        public void closeInternal() {
            if (!this.isClosed) {
                this.isClosed = true;
            }
        }

        /* access modifiers changed from: private */
        public void cleanup() {
            InBandBytestreamSession.this.connection.removeSyncStanzaListener(this.dataPacketListener);
        }
    }

    private abstract class IBBOutputStream extends OutputStream {
        protected final byte[] buffer;
        protected int bufferPointer;
        protected boolean isClosed;
        protected long seq;

        /* access modifiers changed from: protected */
        public abstract void writeToXML(DataPacketExtension dataPacketExtension) throws IOException, NotConnectedException, InterruptedException;

        /* synthetic */ IBBOutputStream(InBandBytestreamSession x0, AnonymousClass1 x1) {
            this();
        }

        private IBBOutputStream() {
            this.bufferPointer = 0;
            this.seq = 0;
            this.isClosed = false;
            this.buffer = new byte[InBandBytestreamSession.this.byteStreamRequest.getBlockSize()];
        }

        public synchronized void write(int b) throws IOException {
            if (!this.isClosed) {
                if (this.bufferPointer >= this.buffer.length) {
                    flushBuffer();
                }
                byte[] bArr = this.buffer;
                int i = this.bufferPointer;
                this.bufferPointer = i + 1;
                bArr[i] = (byte) b;
            } else {
                throw new IOException("Stream is closed");
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0037, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized void write(byte[] r3, int r4, int r5) throws java.io.IOException {
            /*
                r2 = this;
                monitor-enter(r2)
                if (r3 == 0) goto L_0x0046
                if (r4 < 0) goto L_0x0040
                int r0 = r3.length     // Catch:{ all -> 0x004c }
                if (r4 > r0) goto L_0x0040
                if (r5 < 0) goto L_0x0040
                int r0 = r4 + r5
                int r1 = r3.length     // Catch:{ all -> 0x004c }
                if (r0 > r1) goto L_0x0040
                int r0 = r4 + r5
                if (r0 < 0) goto L_0x0040
                if (r5 != 0) goto L_0x0017
                monitor-exit(r2)
                return
            L_0x0017:
                boolean r0 = r2.isClosed     // Catch:{ all -> 0x004c }
                if (r0 != 0) goto L_0x0038
                byte[] r0 = r2.buffer     // Catch:{ all -> 0x004c }
                int r0 = r0.length     // Catch:{ all -> 0x004c }
                if (r5 < r0) goto L_0x0033
                byte[] r0 = r2.buffer     // Catch:{ all -> 0x004c }
                int r0 = r0.length     // Catch:{ all -> 0x004c }
                r2.writeOut(r3, r4, r0)     // Catch:{ all -> 0x004c }
                byte[] r0 = r2.buffer     // Catch:{ all -> 0x004c }
                int r0 = r0.length     // Catch:{ all -> 0x004c }
                int r0 = r0 + r4
                byte[] r1 = r2.buffer     // Catch:{ all -> 0x004c }
                int r1 = r1.length     // Catch:{ all -> 0x004c }
                int r1 = r5 - r1
                r2.write(r3, r0, r1)     // Catch:{ all -> 0x004c }
                goto L_0x0036
            L_0x0033:
                r2.writeOut(r3, r4, r5)     // Catch:{ all -> 0x004c }
            L_0x0036:
                monitor-exit(r2)
                return
            L_0x0038:
                java.io.IOException r0 = new java.io.IOException     // Catch:{ all -> 0x004c }
                java.lang.String r1 = "Stream is closed"
                r0.<init>(r1)     // Catch:{ all -> 0x004c }
                throw r0     // Catch:{ all -> 0x004c }
            L_0x0040:
                java.lang.IndexOutOfBoundsException r0 = new java.lang.IndexOutOfBoundsException     // Catch:{ all -> 0x004c }
                r0.<init>()     // Catch:{ all -> 0x004c }
                throw r0     // Catch:{ all -> 0x004c }
            L_0x0046:
                java.lang.NullPointerException r0 = new java.lang.NullPointerException     // Catch:{ all -> 0x004c }
                r0.<init>()     // Catch:{ all -> 0x004c }
                throw r0     // Catch:{ all -> 0x004c }
            L_0x004c:
                r3 = move-exception
                monitor-exit(r2)
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamSession.IBBOutputStream.write(byte[], int, int):void");
        }

        public synchronized void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        private synchronized void writeOut(byte[] b, int off, int len) throws IOException {
            if (!this.isClosed) {
                int available = 0;
                if (len > this.buffer.length - this.bufferPointer) {
                    available = this.buffer.length - this.bufferPointer;
                    System.arraycopy(b, off, this.buffer, this.bufferPointer, available);
                    this.bufferPointer += available;
                    flushBuffer();
                }
                System.arraycopy(b, off + available, this.buffer, this.bufferPointer, len - available);
                this.bufferPointer += len - available;
            } else {
                throw new IOException("Stream is closed");
            }
        }

        public synchronized void flush() throws IOException {
            if (!this.isClosed) {
                flushBuffer();
            } else {
                throw new IOException("Stream is closed");
            }
        }

        private synchronized void flushBuffer() throws IOException {
            if (this.bufferPointer != 0) {
                try {
                    writeToXML(new DataPacketExtension(InBandBytestreamSession.this.byteStreamRequest.getSessionID(), this.seq, Base64.encodeToString(this.buffer, 0, this.bufferPointer)));
                    this.bufferPointer = 0;
                    this.seq = this.seq + 1 == 65535 ? 0 : this.seq + 1;
                } catch (InterruptedException | NotConnectedException e) {
                    IOException ioException = new IOException();
                    ioException.initCause(e);
                    throw ioException;
                }
            }
        }

        public void close() throws IOException {
            if (!this.isClosed) {
                InBandBytestreamSession.this.closeByLocal(false);
            }
        }

        /* access modifiers changed from: protected */
        public void closeInternal(boolean flush) {
            if (!this.isClosed) {
                this.isClosed = true;
                if (flush) {
                    try {
                        flushBuffer();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    private class IQIBBInputStream extends IBBInputStream {
        private IQIBBInputStream() {
            super();
        }

        /* synthetic */ IQIBBInputStream(InBandBytestreamSession x0, AnonymousClass1 x1) {
            this();
        }

        /* access modifiers changed from: protected */
        public StanzaListener getDataPacketListener() {
            return new StanzaListener() {
                private long lastSequence = -1;

                public void processStanza(Stanza packet) throws NotConnectedException, InterruptedException {
                    DataPacketExtension data = ((Data) packet).getDataPacketExtension();
                    if (data.getSeq() <= this.lastSequence) {
                        InBandBytestreamSession.this.connection.sendStanza(IQ.createErrorResponse((IQ) packet, Condition.unexpected_request));
                    } else if (data.getDecodedData() == null) {
                        InBandBytestreamSession.this.connection.sendStanza(IQ.createErrorResponse((IQ) packet, Condition.bad_request));
                    } else {
                        IQIBBInputStream.this.dataQueue.offer(data);
                        InBandBytestreamSession.this.connection.sendStanza(IQ.createResultIQ((IQ) packet));
                        this.lastSequence = data.getSeq();
                        if (this.lastSequence == 65535) {
                            this.lastSequence = -1;
                        }
                    }
                }
            };
        }

        /* access modifiers changed from: protected */
        public StanzaFilter getDataPacketFilter() {
            return new AndFilter(new StanzaTypeFilter(Data.class), new IBBDataPacketFilter(InBandBytestreamSession.this, null));
        }
    }

    private class IQIBBOutputStream extends IBBOutputStream {
        private IQIBBOutputStream() {
            super(InBandBytestreamSession.this, null);
        }

        /* synthetic */ IQIBBOutputStream(InBandBytestreamSession x0, AnonymousClass1 x1) {
            this();
        }

        /* access modifiers changed from: protected */
        public synchronized void writeToXML(DataPacketExtension data) throws IOException {
            IQ iq = new Data(data);
            iq.setTo(InBandBytestreamSession.this.remoteJID);
            try {
                InBandBytestreamSession.this.connection.createStanzaCollectorAndSend(iq).nextResultOrThrow();
            } catch (Exception e) {
                if (!this.isClosed) {
                    InBandBytestreamSession.this.close();
                    IOException ioException = new IOException();
                    ioException.initCause(e);
                    throw ioException;
                }
            }
        }
    }

    private class MessageIBBInputStream extends IBBInputStream {
        private MessageIBBInputStream() {
            super();
        }

        /* synthetic */ MessageIBBInputStream(InBandBytestreamSession x0, AnonymousClass1 x1) {
            this();
        }

        /* access modifiers changed from: protected */
        public StanzaListener getDataPacketListener() {
            return new StanzaListener() {
                public void processStanza(Stanza packet) {
                    DataPacketExtension data = (DataPacketExtension) packet.getExtension("data", "http://jabber.org/protocol/ibb");
                    if (data.getDecodedData() != null) {
                        MessageIBBInputStream.this.dataQueue.offer(data);
                    }
                }
            };
        }

        /* access modifiers changed from: protected */
        public StanzaFilter getDataPacketFilter() {
            return new AndFilter(new StanzaTypeFilter(Message.class), new IBBDataPacketFilter(InBandBytestreamSession.this, null));
        }
    }

    private class MessageIBBOutputStream extends IBBOutputStream {
        private MessageIBBOutputStream() {
            super(InBandBytestreamSession.this, null);
        }

        /* synthetic */ MessageIBBOutputStream(InBandBytestreamSession x0, AnonymousClass1 x1) {
            this();
        }

        /* access modifiers changed from: protected */
        public synchronized void writeToXML(DataPacketExtension data) throws NotConnectedException, InterruptedException {
            Message message = new Message(InBandBytestreamSession.this.remoteJID);
            message.addExtension(data);
            InBandBytestreamSession.this.connection.sendStanza(message);
        }
    }

    protected InBandBytestreamSession(XMPPConnection connection2, Open byteStreamRequest2, Jid remoteJID2) {
        this.connection = connection2;
        this.byteStreamRequest = byteStreamRequest2;
        this.remoteJID = remoteJID2;
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smackx$bytestreams$ibb$InBandBytestreamManager$StanzaType[byteStreamRequest2.getStanza().ordinal()];
        if (i == 1) {
            this.inputStream = new IQIBBInputStream(this, null);
            this.outputStream = new IQIBBOutputStream(this, null);
        } else if (i == 2) {
            this.inputStream = new MessageIBBInputStream(this, null);
            this.outputStream = new MessageIBBOutputStream(this, null);
        }
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public OutputStream getOutputStream() {
        return this.outputStream;
    }

    public int getReadTimeout() {
        return this.inputStream.readTimeout;
    }

    public void setReadTimeout(int timeout) {
        if (timeout >= 0) {
            this.inputStream.readTimeout = timeout;
            return;
        }
        throw new IllegalArgumentException("Timeout must be >= 0");
    }

    public boolean isCloseBothStreamsEnabled() {
        return this.closeBothStreamsEnabled;
    }

    public void setCloseBothStreamsEnabled(boolean closeBothStreamsEnabled2) {
        this.closeBothStreamsEnabled = closeBothStreamsEnabled2;
    }

    public void close() throws IOException {
        closeByLocal(true);
        closeByLocal(false);
    }

    /* access modifiers changed from: protected */
    public void closeByPeer(Close closeRequest) throws NotConnectedException, InterruptedException {
        this.inputStream.closeInternal();
        this.inputStream.cleanup();
        this.outputStream.closeInternal(false);
        this.connection.sendStanza(IQ.createResultIQ(closeRequest));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0072, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void closeByLocal(boolean r4) throws java.io.IOException {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = r3.isClosed     // Catch:{ all -> 0x0073 }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r3)
            return
        L_0x0007:
            boolean r0 = r3.closeBothStreamsEnabled     // Catch:{ all -> 0x0073 }
            r1 = 1
            if (r0 == 0) goto L_0x0017
            org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamSession$IBBInputStream r0 = r3.inputStream     // Catch:{ all -> 0x0073 }
            r0.closeInternal()     // Catch:{ all -> 0x0073 }
            org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamSession$IBBOutputStream r0 = r3.outputStream     // Catch:{ all -> 0x0073 }
            r0.closeInternal(r1)     // Catch:{ all -> 0x0073 }
            goto L_0x0024
        L_0x0017:
            if (r4 == 0) goto L_0x001f
            org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamSession$IBBInputStream r0 = r3.inputStream     // Catch:{ all -> 0x0073 }
            r0.closeInternal()     // Catch:{ all -> 0x0073 }
            goto L_0x0024
        L_0x001f:
            org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamSession$IBBOutputStream r0 = r3.outputStream     // Catch:{ all -> 0x0073 }
            r0.closeInternal(r1)     // Catch:{ all -> 0x0073 }
        L_0x0024:
            org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamSession$IBBInputStream r0 = r3.inputStream     // Catch:{ all -> 0x0073 }
            boolean r0 = r0.isClosed     // Catch:{ all -> 0x0073 }
            if (r0 == 0) goto L_0x0071
            org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamSession$IBBOutputStream r0 = r3.outputStream     // Catch:{ all -> 0x0073 }
            boolean r0 = r0.isClosed     // Catch:{ all -> 0x0073 }
            if (r0 == 0) goto L_0x0071
            r3.isClosed = r1     // Catch:{ all -> 0x0073 }
            org.jivesoftware.smackx.bytestreams.ibb.packet.Close r0 = new org.jivesoftware.smackx.bytestreams.ibb.packet.Close     // Catch:{ all -> 0x0073 }
            org.jivesoftware.smackx.bytestreams.ibb.packet.Open r1 = r3.byteStreamRequest     // Catch:{ all -> 0x0073 }
            java.lang.String r1 = r1.getSessionID()     // Catch:{ all -> 0x0073 }
            r0.<init>(r1)     // Catch:{ all -> 0x0073 }
            org.jxmpp.jid.Jid r1 = r3.remoteJID     // Catch:{ all -> 0x0073 }
            r0.setTo(r1)     // Catch:{ all -> 0x0073 }
            org.jivesoftware.smack.XMPPConnection r1 = r3.connection     // Catch:{ Exception -> 0x0067 }
            org.jivesoftware.smack.StanzaCollector r1 = r1.createStanzaCollectorAndSend(r0)     // Catch:{ Exception -> 0x0067 }
            r1.nextResultOrThrow()     // Catch:{ Exception -> 0x0067 }
            org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamSession$IBBInputStream r1 = r3.inputStream     // Catch:{ all -> 0x0073 }
            r1.cleanup()     // Catch:{ all -> 0x0073 }
            org.jivesoftware.smack.XMPPConnection r1 = r3.connection     // Catch:{ all -> 0x0073 }
            org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager r1 = org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager.getByteStreamManager(r1)     // Catch:{ all -> 0x0073 }
            java.util.Map r1 = r1.getSessions()     // Catch:{ all -> 0x0073 }
            org.jivesoftware.smackx.bytestreams.ibb.packet.Open r2 = r3.byteStreamRequest     // Catch:{ all -> 0x0073 }
            java.lang.String r2 = r2.getSessionID()     // Catch:{ all -> 0x0073 }
            r1.remove(r2)     // Catch:{ all -> 0x0073 }
            goto L_0x0071
        L_0x0067:
            r1 = move-exception
            java.io.IOException r2 = new java.io.IOException     // Catch:{ all -> 0x0073 }
            r2.<init>()     // Catch:{ all -> 0x0073 }
            r2.initCause(r1)     // Catch:{ all -> 0x0073 }
            throw r2     // Catch:{ all -> 0x0073 }
        L_0x0071:
            monitor-exit(r3)
            return
        L_0x0073:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamSession.closeByLocal(boolean):void");
    }

    public void processIQPacket(Data data) throws NotConnectedException, InterruptedException, NotLoggedInException {
        this.inputStream.dataPacketListener.processStanza(data);
    }
}
