package org.jivesoftware.smackx.bytestreams.socks5;

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
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.filetransfer.StreamNegotiator;

final class InitiationListener extends AbstractIqRequestHandler {
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(InitiationListener.class.getName());
    private final ExecutorService initiationListenerExecutor = Executors.newCachedThreadPool();
    private final Socks5BytestreamManager manager;

    protected InitiationListener(Socks5BytestreamManager manager2) {
        super("query", Bytestream.NAMESPACE, Type.set, Mode.async);
        this.manager = manager2;
    }

    public IQ handleIQRequest(final IQ packet) {
        this.initiationListenerExecutor.execute(new Runnable() {
            public void run() {
                try {
                    InitiationListener.this.processRequest(packet);
                } catch (InterruptedException | NotConnectedException e) {
                    InitiationListener.LOGGER.log(Level.WARNING, "process request", e);
                }
            }
        });
        return null;
    }

    /* access modifiers changed from: private */
    public void processRequest(Stanza packet) throws NotConnectedException, InterruptedException {
        Bytestream byteStreamRequest = (Bytestream) packet;
        StringBuilder sb = new StringBuilder();
        sb.append(byteStreamRequest.getFrom().toString());
        sb.append(9);
        sb.append(byteStreamRequest.getSessionID());
        StreamNegotiator.signal(sb.toString(), byteStreamRequest);
        if (!this.manager.getIgnoredBytestreamRequests().remove(byteStreamRequest.getSessionID())) {
            Socks5BytestreamRequest request = new Socks5BytestreamRequest(this.manager, byteStreamRequest);
            BytestreamListener userListener = this.manager.getUserListener(byteStreamRequest.getFrom());
            if (userListener != null) {
                userListener.incomingBytestreamRequest(request);
            } else if (!this.manager.getAllRequestListeners().isEmpty()) {
                for (BytestreamListener listener : this.manager.getAllRequestListeners()) {
                    listener.incomingBytestreamRequest(request);
                }
            } else {
                this.manager.replyRejectPacket(byteStreamRequest);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void shutdown() {
        this.initiationListenerExecutor.shutdownNow();
    }
}
