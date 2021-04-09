package org.jivesoftware.smackx.jingle.transports.jingle_ibb;

import java.util.WeakHashMap;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.provider.JingleContentProviderManager;
import org.jivesoftware.smackx.jingle.transports.JingleTransportManager;
import org.jivesoftware.smackx.jingle.transports.JingleTransportSession;
import org.jivesoftware.smackx.jingle.transports.jingle_ibb.element.JingleIBBTransport;
import org.jivesoftware.smackx.jingle.transports.jingle_ibb.provider.JingleIBBTransportProvider;

public final class JingleIBBTransportManager extends JingleTransportManager<JingleIBBTransport> {
    private static final WeakHashMap<XMPPConnection, JingleIBBTransportManager> INSTANCES = new WeakHashMap<>();

    private JingleIBBTransportManager(XMPPConnection connection) {
        super(connection);
        JingleContentProviderManager.addJingleContentTransportProvider(getNamespace(), new JingleIBBTransportProvider());
    }

    public static synchronized JingleIBBTransportManager getInstanceFor(XMPPConnection connection) {
        JingleIBBTransportManager manager;
        synchronized (JingleIBBTransportManager.class) {
            manager = (JingleIBBTransportManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new JingleIBBTransportManager(connection);
                INSTANCES.put(connection, manager);
            }
        }
        return manager;
    }

    public String getNamespace() {
        return JingleIBBTransport.NAMESPACE_V1;
    }

    public JingleTransportSession<JingleIBBTransport> transportSession(JingleSession jingleSession) {
        return new JingleIBBTransportSession(jingleSession);
    }

    public void authenticated(XMPPConnection connection, boolean resumed) {
    }
}
