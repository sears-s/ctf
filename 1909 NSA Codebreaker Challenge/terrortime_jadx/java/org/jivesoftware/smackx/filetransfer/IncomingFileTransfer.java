package org.jivesoftware.smackx.filetransfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Error;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

public class IncomingFileTransfer extends FileTransfer {
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(IncomingFileTransfer.class.getName());
    /* access modifiers changed from: private */
    public InputStream inputStream;
    /* access modifiers changed from: private */
    public FileTransferRequest receiveRequest;

    protected IncomingFileTransfer(FileTransferRequest request, FileTransferNegotiator transferNegotiator) {
        super(request.getRequestor(), request.getStreamID(), transferNegotiator);
        this.receiveRequest = request;
    }

    public InputStream receiveFile() throws SmackException, XMPPErrorException, InterruptedException {
        if (this.inputStream == null) {
            try {
                this.inputStream = negotiateStream();
                return this.inputStream;
            } catch (XMPPErrorException e) {
                setException(e);
                throw e;
            }
        } else {
            throw new IllegalStateException("Transfer already negotiated!");
        }
    }

    public void receiveFile(final File file) throws SmackException, IOException {
        if (file != null) {
            if (!file.exists()) {
                file.createNewFile();
            }
            if (file.canWrite()) {
                AnonymousClass1 r1 = new Runnable() {
                    public void run() {
                        try {
                            IncomingFileTransfer.this.inputStream = IncomingFileTransfer.this.negotiateStream();
                            OutputStream outputStream = null;
                            try {
                                outputStream = new FileOutputStream(file);
                                IncomingFileTransfer.this.setStatus(Status.in_progress);
                                IncomingFileTransfer.this.writeToStream(IncomingFileTransfer.this.inputStream, outputStream);
                            } catch (FileNotFoundException e) {
                                IncomingFileTransfer.this.setStatus(Status.error);
                                IncomingFileTransfer.this.setError(Error.bad_file);
                                IncomingFileTransfer.this.setException(e);
                            } catch (IOException e2) {
                                IncomingFileTransfer.this.setStatus(Status.error);
                                IncomingFileTransfer.this.setError(Error.stream);
                                IncomingFileTransfer.this.setException(e2);
                            }
                            if (IncomingFileTransfer.this.getStatus().equals(Status.in_progress)) {
                                IncomingFileTransfer.this.setStatus(Status.complete);
                            }
                            if (IncomingFileTransfer.this.inputStream != null) {
                                try {
                                    IncomingFileTransfer.this.inputStream.close();
                                } catch (IOException e3) {
                                    IncomingFileTransfer.LOGGER.log(Level.WARNING, "Closing input stream", e3);
                                }
                            }
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e4) {
                                    IncomingFileTransfer.LOGGER.log(Level.WARNING, "Closing output stream", e4);
                                }
                            }
                        } catch (Exception e5) {
                            IncomingFileTransfer.this.setStatus(Status.error);
                            IncomingFileTransfer.this.setException(e5);
                        }
                    }
                };
                StringBuilder sb = new StringBuilder();
                sb.append("File Transfer ");
                sb.append(this.streamID);
                new Thread(r1, sb.toString()).start();
                return;
            }
            throw new IllegalArgumentException("Cannot write to provided file");
        }
        throw new IllegalArgumentException("File cannot be null");
    }

    /* access modifiers changed from: private */
    public InputStream negotiateStream() throws SmackException, XMPPErrorException, InterruptedException {
        setStatus(Status.negotiating_transfer);
        final StreamNegotiator streamNegotiator = this.negotiator.selectStreamNegotiator(this.receiveRequest);
        setStatus(Status.negotiating_stream);
        FutureTask<InputStream> streamNegotiatorTask = new FutureTask<>(new Callable<InputStream>() {
            public InputStream call() throws Exception {
                return streamNegotiator.createIncomingStream(IncomingFileTransfer.this.receiveRequest.getStreamInitiation());
            }
        });
        streamNegotiatorTask.run();
        try {
            InputStream inputStream2 = (InputStream) streamNegotiatorTask.get(15, TimeUnit.SECONDS);
            streamNegotiatorTask.cancel(true);
            setStatus(Status.negotiated);
            return inputStream2;
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof XMPPErrorException) {
                throw ((XMPPErrorException) cause);
            } else if (cause instanceof InterruptedException) {
                throw ((InterruptedException) cause);
            } else if (cause instanceof NoResponseException) {
                throw ((NoResponseException) cause);
            } else if (cause instanceof SmackException) {
                throw ((SmackException) cause);
            } else {
                throw new SmackException("Error in execution", e);
            }
        } catch (TimeoutException e2) {
            throw new SmackException("Request timed out", e2);
        } catch (Throwable th) {
            streamNegotiatorTask.cancel(true);
            throw th;
        }
    }

    public void cancel() {
        setStatus(Status.cancelled);
    }
}
