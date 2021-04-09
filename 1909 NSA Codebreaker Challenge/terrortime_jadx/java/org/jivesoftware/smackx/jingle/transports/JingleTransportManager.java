package org.jivesoftware.smackx.jingle.transports;

import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.element.JingleContentTransport;

public abstract class JingleTransportManager<D extends JingleContentTransport> extends AbstractConnectionListener {
    private final XMPPConnection connection;

    public abstract String getNamespace();

    public abstract JingleTransportSession<D> transportSession(JingleSession jingleSession);

    public JingleTransportManager(XMPPConnection connection2) {
        this.connection = connection2;
        connection2.addConnectionListener(this);
    }

    public XMPPConnection getConnection() {
        return this.connection;
    }

    public void connected(XMPPConnection connection2) {
    }

    public void connectionClosed() {
    }

    public void connectionClosedOnError(Exception e) {
    }
}
