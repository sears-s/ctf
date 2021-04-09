package org.jivesoftware.smackx.filetransfer;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smackx.si.packet.StreamInitiation;
import org.jxmpp.jid.EntityFullJid;

public final class FileTransferManager extends Manager {
    private static final Map<XMPPConnection, FileTransferManager> INSTANCES = new WeakHashMap();
    private final FileTransferNegotiator fileTransferNegotiator;
    /* access modifiers changed from: private */
    public final List<FileTransferListener> listeners = new CopyOnWriteArrayList();

    public static synchronized FileTransferManager getInstanceFor(XMPPConnection connection) {
        FileTransferManager fileTransferManager;
        synchronized (FileTransferManager.class) {
            fileTransferManager = (FileTransferManager) INSTANCES.get(connection);
            if (fileTransferManager == null) {
                fileTransferManager = new FileTransferManager(connection);
                INSTANCES.put(connection, fileTransferManager);
            }
        }
        return fileTransferManager;
    }

    private FileTransferManager(XMPPConnection connection) {
        super(connection);
        this.fileTransferNegotiator = FileTransferNegotiator.getInstanceFor(connection);
        AnonymousClass1 r1 = new AbstractIqRequestHandler(StreamInitiation.ELEMENT, "http://jabber.org/protocol/si", Type.set, Mode.async) {
            public IQ handleIQRequest(IQ packet) {
                FileTransferRequest request = new FileTransferRequest(FileTransferManager.this, (StreamInitiation) packet);
                for (FileTransferListener listener : FileTransferManager.this.listeners) {
                    listener.fileTransferRequest(request);
                }
                return null;
            }
        };
        connection.registerIQRequestHandler(r1);
    }

    public void addFileTransferListener(FileTransferListener li) {
        this.listeners.add(li);
    }

    public void removeFileTransferListener(FileTransferListener li) {
        this.listeners.remove(li);
    }

    public OutgoingFileTransfer createOutgoingFileTransfer(EntityFullJid userID) {
        if (userID != null) {
            return new OutgoingFileTransfer(connection().getUser(), userID, FileTransferNegotiator.getNextStreamID(), this.fileTransferNegotiator);
        }
        throw new IllegalArgumentException("userID was null");
    }

    /* access modifiers changed from: protected */
    public IncomingFileTransfer createIncomingFileTransfer(FileTransferRequest request) {
        if (request != null) {
            IncomingFileTransfer transfer = new IncomingFileTransfer(request, this.fileTransferNegotiator);
            transfer.setFileInfo(request.getFileName(), request.getFileSize());
            return transfer;
        }
        throw new NullPointerException("ReceiveRequest cannot be null");
    }

    /* access modifiers changed from: protected */
    public void rejectIncomingFileTransfer(FileTransferRequest request) throws NotConnectedException, InterruptedException {
        connection().sendStanza(IQ.createErrorResponse((IQ) request.getStreamInitiation(), StanzaError.getBuilder(Condition.forbidden)));
    }
}
