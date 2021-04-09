package org.jivesoftware.smackx.xdata;

import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jxmpp.jid.Jid;

public final class XDataManager extends Manager {
    private static final Map<XMPPConnection, XDataManager> INSTANCES = new WeakHashMap();
    public static final String NAMESPACE = "jabber:x:data";

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                XDataManager.getInstanceFor(connection);
            }
        });
    }

    public static synchronized XDataManager getInstanceFor(XMPPConnection connection) {
        XDataManager xDataManager;
        synchronized (XDataManager.class) {
            xDataManager = (XDataManager) INSTANCES.get(connection);
            if (xDataManager == null) {
                xDataManager = new XDataManager(connection);
                INSTANCES.put(connection, xDataManager);
            }
        }
        return xDataManager;
    }

    private XDataManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature("jabber:x:data");
    }

    public boolean isSupported(Jid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(jid, "jabber:x:data");
    }
}
