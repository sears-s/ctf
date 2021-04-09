package org.jivesoftware.smackx.filetransfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.jxmpp.jid.Jid;

public abstract class FileTransfer {
    private static final int BUFFER_SIZE = 8192;
    protected long amountWritten = -1;
    private Error error;
    private Exception exception;
    private String fileName;
    private String filePath;
    private long fileSize;
    protected FileTransferNegotiator negotiator;
    private Jid peer;
    private Status status = Status.initial;
    private final Object statusMonitor = new Object();
    protected String streamID;

    public enum Error {
        none("No error"),
        not_acceptable("The peer did not find any of the provided stream mechanisms acceptable."),
        bad_file("The provided file to transfer does not exist or could not be read."),
        no_response("The remote user did not respond or the connection timed out."),
        connection("An error occurred over the socket connected to send the file."),
        stream("An error occurred while sending or receiving the file.");
        
        private final String msg;

        private Error(String msg2) {
            this.msg = msg2;
        }

        public String getMessage() {
            return this.msg;
        }

        public String toString() {
            return this.msg;
        }
    }

    public enum Status {
        error("Error"),
        initial("Initial"),
        negotiating_transfer("Negotiating Transfer"),
        refused("Refused"),
        negotiating_stream("Negotiating Stream"),
        negotiated("Negotiated"),
        in_progress("In Progress"),
        complete("Complete"),
        cancelled("Cancelled");
        
        private final String status;

        private Status(String status2) {
            this.status = status2;
        }

        public String toString() {
            return this.status;
        }
    }

    public abstract void cancel();

    protected FileTransfer(Jid peer2, String streamID2, FileTransferNegotiator negotiator2) {
        this.peer = peer2;
        this.streamID = streamID2;
        this.negotiator = negotiator2;
    }

    /* access modifiers changed from: protected */
    public void setFileInfo(String fileName2, long fileSize2) {
        this.fileName = fileName2;
        this.fileSize = fileSize2;
    }

    /* access modifiers changed from: protected */
    public void setFileInfo(String path, String fileName2, long fileSize2) {
        this.filePath = path;
        this.fileName = fileName2;
        this.fileSize = fileSize2;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public Jid getPeer() {
        return this.peer;
    }

    public double getProgress() {
        long j = this.amountWritten;
        if (j > 0) {
            long j2 = this.fileSize;
            if (j2 > 0) {
                return ((double) j) / ((double) j2);
            }
        }
        return 0.0d;
    }

    public boolean isDone() {
        return this.status == Status.cancelled || this.status == Status.error || this.status == Status.complete || this.status == Status.refused;
    }

    public Status getStatus() {
        return this.status;
    }

    /* access modifiers changed from: protected */
    public void setError(Error type) {
        this.error = type;
    }

    public Error getError() {
        return this.error;
    }

    public Exception getException() {
        return this.exception;
    }

    public String getStreamID() {
        return this.streamID;
    }

    /* access modifiers changed from: protected */
    public void setException(Exception exception2) {
        this.exception = exception2;
    }

    /* access modifiers changed from: protected */
    public void setStatus(Status status2) {
        synchronized (this.statusMonitor) {
            this.status = status2;
        }
    }

    /* access modifiers changed from: protected */
    public boolean updateStatus(Status oldStatus, Status newStatus) {
        synchronized (this.statusMonitor) {
            if (oldStatus != this.status) {
                return false;
            }
            this.status = newStatus;
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void writeToStream(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[8192];
        this.amountWritten = 0;
        while (true) {
            int read = in.read(b);
            int count = read;
            if (read > 0 && !getStatus().equals(Status.cancelled)) {
                out.write(b, 0, count);
                this.amountWritten += (long) count;
            }
        }
        if (!getStatus().equals(Status.cancelled) && getError() == Error.none && this.amountWritten != this.fileSize) {
            setStatus(Status.error);
            this.error = Error.connection;
        }
    }

    public long getAmountWritten() {
        return this.amountWritten;
    }
}
