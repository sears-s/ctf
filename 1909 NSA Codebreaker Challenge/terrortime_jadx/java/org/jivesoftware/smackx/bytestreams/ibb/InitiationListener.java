package org.jivesoftware.smackx.bytestreams.ibb;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.bytestreams.BytestreamListener;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Open;
import org.jivesoftware.smackx.filetransfer.StreamNegotiator;

class InitiationListener extends AbstractIqRequestHandler {
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(InitiationListener.class.getName());
    private final ExecutorService initiationListenerExecutor = Executors.newCachedThreadPool();
    private final InBandBytestreamManager manager;

    protected InitiationListener(InBandBytestreamManager manager2) {
        super("open", "http://jabber.org/protocol/ibb", Type.set, Mode.async);
        this.manager = manager2;
    }

    public IQ handleIQRequest(final IQ packet) {
        this.initiationListenerExecutor.execute(new Runnable() {
            public void run() {
                try {
                    InitiationListener.this.processRequest(packet);
                } catch (InterruptedException | NotConnectedException e) {
                    InitiationListener.LOGGER.log(Level.WARNING, "proccessRequest", e);
                }
            }
        });
        return null;
    }

    /* access modifiers changed from: private */
    public void processRequest(Stanza packet) throws NotConnectedException, InterruptedException {
        Open ibbRequest = (Open) packet;
        if (ibbRequest.getBlockSize() > this.manager.getMaximumBlockSize()) {
            this.manager.replyResourceConstraintPacket(ibbRequest);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(ibbRequest.getFrom().toString());
        sb.append(9);
        sb.append(ibbRequest.getSessionID());
        StreamNegotiator.signal(sb.toString(), ibbRequest);
        if (!this.manager.getIgnoredBytestreamRequests().remove(ibbRequest.getSessionID())) {
            InBandBytestreamRequest request = new InBandBytestreamRequest(this.manager, ibbRequest);
            BytestreamListener userListener = this.manager.getUserListener(ibbRequest.getFrom());
            if (userListener != null) {
                userListener.incomingBytestreamRequest(request);
            } else if (!this.manager.getAllRequestListeners().isEmpty()) {
                for (BytestreamListener listener : this.manager.getAllRequestListeners()) {
                    listener.incomingBytestreamRequest(request);
                }
            } else {
                this.manager.replyRejectPacket(ibbRequest);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void shutdown() {
        this.initiationListenerExecutor.shutdownNow();
    }
}
