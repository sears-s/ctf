package org.jivesoftware.smackx.filetransfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.IllegalStateChangeException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Error;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jxmpp.jid.Jid;

public class OutgoingFileTransfer extends FileTransfer {
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(OutgoingFileTransfer.class.getName());
    private static int RESPONSE_TIMEOUT = 60000;
    private NegotiationProgress callback;
    private Jid initiator;
    /* access modifiers changed from: private */
    public OutputStream outputStream;
    private Thread transferThread;

    /* renamed from: org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer$4 reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$StanzaError$Condition = new int[Condition.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$packet$StanzaError$Condition[Condition.forbidden.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$StanzaError$Condition[Condition.bad_request.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public interface NegotiationProgress {
        void errorEstablishingStream(Exception exc);

        void outputStreamEstablished(OutputStream outputStream);

        void statusUpdated(Status status, Status status2);
    }

    public static int getResponseTimeout() {
        return RESPONSE_TIMEOUT;
    }

    public static void setResponseTimeout(int responseTimeout) {
        RESPONSE_TIMEOUT = responseTimeout;
    }

    protected OutgoingFileTransfer(Jid initiator2, Jid target, String streamID, FileTransferNegotiator transferNegotiator) {
        super(target, streamID, transferNegotiator);
        this.initiator = initiator2;
    }

    /* access modifiers changed from: protected */
    public void setOutputStream(OutputStream stream) {
        if (this.outputStream == null) {
            this.outputStream = stream;
        }
    }

    /* access modifiers changed from: protected */
    public OutputStream getOutputStream() {
        if (getStatus().equals(Status.negotiated)) {
            return this.outputStream;
        }
        return null;
    }

    public synchronized OutputStream sendFile(String fileName, long fileSize, String description) throws XMPPException, SmackException, InterruptedException {
        if (isDone() || this.outputStream != null) {
            throw new IllegalStateException("The negotiation process has already been attempted on this file transfer");
        }
        try {
            setFileInfo(fileName, fileSize);
            this.outputStream = negotiateStream(fileName, fileSize, description);
        } catch (XMPPErrorException e) {
            handleXMPPException(e);
            throw e;
        }
        return this.outputStream;
    }

    public synchronized void sendFile(String fileName, long fileSize, String description, NegotiationProgress progress) {
        if (progress != null) {
            try {
                checkTransferThread();
                if (isDone() || this.outputStream != null) {
                    throw new IllegalStateException("The negotiation process has already been attempted for this file transfer");
                }
                setFileInfo(fileName, fileSize);
                this.callback = progress;
                final String str = fileName;
                final long j = fileSize;
                final String str2 = description;
                final NegotiationProgress negotiationProgress = progress;
                AnonymousClass1 r1 = new Runnable() {
                    public void run() {
                        try {
                            OutgoingFileTransfer.this.outputStream = OutgoingFileTransfer.this.negotiateStream(str, j, str2);
                            negotiationProgress.outputStreamEstablished(OutgoingFileTransfer.this.outputStream);
                        } catch (XMPPErrorException e) {
                            OutgoingFileTransfer.this.handleXMPPException(e);
                        } catch (Exception e2) {
                            OutgoingFileTransfer.this.setException(e2);
                        }
                    }
                };
                StringBuilder sb = new StringBuilder();
                sb.append("File Transfer Negotiation ");
                sb.append(this.streamID);
                this.transferThread = new Thread(r1, sb.toString());
                this.transferThread.start();
            } catch (Throwable th) {
                throw th;
            }
        } else {
            throw new IllegalArgumentException("Callback progress cannot be null.");
        }
    }

    private void checkTransferThread() {
        Thread thread = this.transferThread;
        if ((thread != null && thread.isAlive()) || isDone()) {
            throw new IllegalStateException("File transfer in progress or has already completed.");
        }
    }

    public synchronized void sendFile(final File file, final String description) throws SmackException {
        checkTransferThread();
        if (file == null || !file.exists() || !file.canRead()) {
            throw new IllegalArgumentException("Could not read file");
        }
        setFileInfo(file.getAbsolutePath(), file.getName(), file.length());
        AnonymousClass2 r1 = new Runnable() {
            /* JADX WARNING: Unknown top exception splitter block from list: {B:31:0x0091=Splitter:B:31:0x0091, B:18:0x005d=Splitter:B:18:0x005d, B:41:0x00bf=Splitter:B:41:0x00bf} */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r8 = this;
                    java.lang.String r0 = "Closing input stream"
                    java.lang.String r1 = "Closing output stream"
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r2 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this     // Catch:{ XMPPErrorException -> 0x00f8, Exception -> 0x001e }
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r3 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this     // Catch:{ XMPPErrorException -> 0x00f8, Exception -> 0x001e }
                    java.io.File r4 = r5     // Catch:{ XMPPErrorException -> 0x00f8, Exception -> 0x001e }
                    java.lang.String r4 = r4.getName()     // Catch:{ XMPPErrorException -> 0x00f8, Exception -> 0x001e }
                    java.io.File r5 = r5     // Catch:{ XMPPErrorException -> 0x00f8, Exception -> 0x001e }
                    long r5 = r5.length()     // Catch:{ XMPPErrorException -> 0x00f8, Exception -> 0x001e }
                    java.lang.String r7 = r6     // Catch:{ XMPPErrorException -> 0x00f8, Exception -> 0x001e }
                    java.io.OutputStream r3 = r3.negotiateStream(r4, r5, r7)     // Catch:{ XMPPErrorException -> 0x00f8, Exception -> 0x001e }
                    r2.outputStream = r3     // Catch:{ XMPPErrorException -> 0x00f8, Exception -> 0x001e }
                    goto L_0x0024
                L_0x001e:
                    r2 = move-exception
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r3 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this
                    r3.setException(r2)
                L_0x0024:
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r2 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this
                    java.io.OutputStream r2 = r2.outputStream
                    if (r2 != 0) goto L_0x002d
                    return
                L_0x002d:
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r2 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this
                    org.jivesoftware.smackx.filetransfer.FileTransfer$Status r3 = org.jivesoftware.smackx.filetransfer.FileTransfer.Status.negotiated
                    org.jivesoftware.smackx.filetransfer.FileTransfer$Status r4 = org.jivesoftware.smackx.filetransfer.FileTransfer.Status.in_progress
                    boolean r2 = r2.updateStatus(r3, r4)
                    if (r2 != 0) goto L_0x003a
                    return
                L_0x003a:
                    r2 = 0
                    java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x009b, IOException -> 0x0074 }
                    java.io.File r4 = r5     // Catch:{ FileNotFoundException -> 0x009b, IOException -> 0x0074 }
                    r3.<init>(r4)     // Catch:{ FileNotFoundException -> 0x009b, IOException -> 0x0074 }
                    r2 = r3
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r3 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this     // Catch:{ FileNotFoundException -> 0x009b, IOException -> 0x0074 }
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r4 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this     // Catch:{ FileNotFoundException -> 0x009b, IOException -> 0x0074 }
                    java.io.OutputStream r4 = r4.outputStream     // Catch:{ FileNotFoundException -> 0x009b, IOException -> 0x0074 }
                    r3.writeToStream(r2, r4)     // Catch:{ FileNotFoundException -> 0x009b, IOException -> 0x0074 }
                    r2.close()     // Catch:{ IOException -> 0x0053 }
                    goto L_0x005d
                L_0x0053:
                    r3 = move-exception
                    java.util.logging.Logger r4 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.LOGGER
                    java.util.logging.Level r5 = java.util.logging.Level.WARNING
                    r4.log(r5, r0, r3)
                L_0x005d:
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r0 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this     // Catch:{ IOException -> 0x0067 }
                    java.io.OutputStream r0 = r0.outputStream     // Catch:{ IOException -> 0x0067 }
                    r0.close()     // Catch:{ IOException -> 0x0067 }
                L_0x0066:
                    goto L_0x00c9
                L_0x0067:
                    r0 = move-exception
                    java.util.logging.Logger r3 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.LOGGER
                    java.util.logging.Level r4 = java.util.logging.Level.WARNING
                    r3.log(r4, r1, r0)
                    goto L_0x00c9
                L_0x0072:
                    r3 = move-exception
                    goto L_0x00d3
                L_0x0074:
                    r3 = move-exception
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r4 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this     // Catch:{ all -> 0x0072 }
                    org.jivesoftware.smackx.filetransfer.FileTransfer$Status r5 = org.jivesoftware.smackx.filetransfer.FileTransfer.Status.error     // Catch:{ all -> 0x0072 }
                    r4.setStatus(r5)     // Catch:{ all -> 0x0072 }
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r4 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this     // Catch:{ all -> 0x0072 }
                    r4.setException(r3)     // Catch:{ all -> 0x0072 }
                    if (r2 == 0) goto L_0x0091
                    r2.close()     // Catch:{ IOException -> 0x0087 }
                    goto L_0x0091
                L_0x0087:
                    r3 = move-exception
                    java.util.logging.Logger r4 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.LOGGER
                    java.util.logging.Level r5 = java.util.logging.Level.WARNING
                    r4.log(r5, r0, r3)
                L_0x0091:
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r0 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this     // Catch:{ IOException -> 0x0067 }
                    java.io.OutputStream r0 = r0.outputStream     // Catch:{ IOException -> 0x0067 }
                    r0.close()     // Catch:{ IOException -> 0x0067 }
                    goto L_0x0066
                L_0x009b:
                    r3 = move-exception
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r4 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this     // Catch:{ all -> 0x0072 }
                    org.jivesoftware.smackx.filetransfer.FileTransfer$Status r5 = org.jivesoftware.smackx.filetransfer.FileTransfer.Status.error     // Catch:{ all -> 0x0072 }
                    r4.setStatus(r5)     // Catch:{ all -> 0x0072 }
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r4 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this     // Catch:{ all -> 0x0072 }
                    org.jivesoftware.smackx.filetransfer.FileTransfer$Error r5 = org.jivesoftware.smackx.filetransfer.FileTransfer.Error.bad_file     // Catch:{ all -> 0x0072 }
                    r4.setError(r5)     // Catch:{ all -> 0x0072 }
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r4 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this     // Catch:{ all -> 0x0072 }
                    r4.setException(r3)     // Catch:{ all -> 0x0072 }
                    if (r2 == 0) goto L_0x00bf
                    r2.close()     // Catch:{ IOException -> 0x00b5 }
                    goto L_0x00bf
                L_0x00b5:
                    r3 = move-exception
                    java.util.logging.Logger r4 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.LOGGER
                    java.util.logging.Level r5 = java.util.logging.Level.WARNING
                    r4.log(r5, r0, r3)
                L_0x00bf:
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r0 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this     // Catch:{ IOException -> 0x0067 }
                    java.io.OutputStream r0 = r0.outputStream     // Catch:{ IOException -> 0x0067 }
                    r0.close()     // Catch:{ IOException -> 0x0067 }
                    goto L_0x0066
                L_0x00c9:
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r0 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this
                    org.jivesoftware.smackx.filetransfer.FileTransfer$Status r1 = org.jivesoftware.smackx.filetransfer.FileTransfer.Status.in_progress
                    org.jivesoftware.smackx.filetransfer.FileTransfer$Status r3 = org.jivesoftware.smackx.filetransfer.FileTransfer.Status.complete
                    r0.updateStatus(r1, r3)
                    return
                L_0x00d3:
                    if (r2 == 0) goto L_0x00e3
                    r2.close()     // Catch:{ IOException -> 0x00d9 }
                    goto L_0x00e3
                L_0x00d9:
                    r4 = move-exception
                    java.util.logging.Logger r5 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.LOGGER
                    java.util.logging.Level r6 = java.util.logging.Level.WARNING
                    r5.log(r6, r0, r4)
                L_0x00e3:
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r0 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this     // Catch:{ IOException -> 0x00ed }
                    java.io.OutputStream r0 = r0.outputStream     // Catch:{ IOException -> 0x00ed }
                    r0.close()     // Catch:{ IOException -> 0x00ed }
                    goto L_0x00f7
                L_0x00ed:
                    r0 = move-exception
                    java.util.logging.Logger r4 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.LOGGER
                    java.util.logging.Level r5 = java.util.logging.Level.WARNING
                    r4.log(r5, r1, r0)
                L_0x00f7:
                    throw r3
                L_0x00f8:
                    r0 = move-exception
                    org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer r1 = org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.this
                    r1.handleXMPPException(r0)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.AnonymousClass2.run():void");
            }
        };
        StringBuilder sb = new StringBuilder();
        sb.append("File Transfer ");
        sb.append(this.streamID);
        this.transferThread = new Thread(r1, sb.toString());
        this.transferThread.start();
    }

    public synchronized void sendStream(InputStream in, String fileName, long fileSize, String description) {
        checkTransferThread();
        setFileInfo(fileName, fileSize);
        final String str = fileName;
        final long j = fileSize;
        final String str2 = description;
        final InputStream inputStream = in;
        AnonymousClass3 r1 = new Runnable() {
            public void run() {
                try {
                    OutgoingFileTransfer.this.outputStream = OutgoingFileTransfer.this.negotiateStream(str, j, str2);
                } catch (XMPPErrorException e) {
                    OutgoingFileTransfer.this.handleXMPPException(e);
                    return;
                } catch (Exception e2) {
                    OutgoingFileTransfer.this.setException(e2);
                }
                if (OutgoingFileTransfer.this.outputStream != null && OutgoingFileTransfer.this.updateStatus(Status.negotiated, Status.in_progress)) {
                    try {
                        OutgoingFileTransfer.this.writeToStream(inputStream, OutgoingFileTransfer.this.outputStream);
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            OutgoingFileTransfer.this.outputStream.flush();
                            OutgoingFileTransfer.this.outputStream.close();
                        } catch (IOException e3) {
                        }
                    } catch (IOException e4) {
                        OutgoingFileTransfer.this.setStatus(Status.error);
                        OutgoingFileTransfer.this.setException(e4);
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        OutgoingFileTransfer.this.outputStream.flush();
                        OutgoingFileTransfer.this.outputStream.close();
                    } catch (Throwable th) {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            OutgoingFileTransfer.this.outputStream.flush();
                            OutgoingFileTransfer.this.outputStream.close();
                        } catch (IOException e5) {
                        }
                        throw th;
                    }
                    OutgoingFileTransfer.this.updateStatus(Status.in_progress, Status.complete);
                }
            }
        };
        StringBuilder sb = new StringBuilder();
        sb.append("File Transfer ");
        sb.append(this.streamID);
        this.transferThread = new Thread(r1, sb.toString());
        this.transferThread.start();
    }

    /* access modifiers changed from: private */
    public void handleXMPPException(XMPPErrorException e) {
        StanzaError error = e.getStanzaError();
        if (error != null) {
            int i = AnonymousClass4.$SwitchMap$org$jivesoftware$smack$packet$StanzaError$Condition[error.getCondition().ordinal()];
            if (i == 1) {
                setStatus(Status.refused);
                return;
            } else if (i != 2) {
                setStatus(Status.error);
            } else {
                setStatus(Status.error);
                setError(Error.not_acceptable);
            }
        }
        setException(e);
    }

    public long getBytesSent() {
        return this.amountWritten;
    }

    /* access modifiers changed from: private */
    public OutputStream negotiateStream(String fileName, long fileSize, String description) throws SmackException, XMPPException, InterruptedException {
        if (updateStatus(Status.initial, Status.negotiating_transfer)) {
            StreamNegotiator streamNegotiator = this.negotiator.negotiateOutgoingTransfer(getPeer(), this.streamID, fileName, fileSize, description, RESPONSE_TIMEOUT);
            if (updateStatus(Status.negotiating_transfer, Status.negotiating_stream)) {
                this.outputStream = streamNegotiator.createOutgoingStream(this.streamID, this.initiator, getPeer());
                if (updateStatus(Status.negotiating_stream, Status.negotiated)) {
                    return this.outputStream;
                }
                throw new IllegalStateChangeException();
            }
            throw new IllegalStateChangeException();
        }
        throw new IllegalStateChangeException();
    }

    public void cancel() {
        setStatus(Status.cancelled);
    }

    /* access modifiers changed from: protected */
    public boolean updateStatus(Status oldStatus, Status newStatus) {
        boolean isUpdated = super.updateStatus(oldStatus, newStatus);
        NegotiationProgress negotiationProgress = this.callback;
        if (negotiationProgress != null && isUpdated) {
            negotiationProgress.statusUpdated(oldStatus, newStatus);
        }
        return isUpdated;
    }

    /* access modifiers changed from: protected */
    public void setStatus(Status status) {
        Status oldStatus = getStatus();
        super.setStatus(status);
        NegotiationProgress negotiationProgress = this.callback;
        if (negotiationProgress != null) {
            negotiationProgress.statusUpdated(oldStatus, status);
        }
    }

    /* access modifiers changed from: protected */
    public void setException(Exception exception) {
        super.setException(exception);
        NegotiationProgress negotiationProgress = this.callback;
        if (negotiationProgress != null) {
            negotiationProgress.errorEstablishingStream(exception);
        }
    }
}
