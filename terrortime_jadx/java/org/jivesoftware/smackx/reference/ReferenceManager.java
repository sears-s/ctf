package org.jivesoftware.smackx.reference;

import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;

public final class ReferenceManager extends Manager {
    private static final Map<XMPPConnection, ReferenceManager> INSTANCES = new WeakHashMap();
    public static final String NAMESPACE = "urn:xmpp:reference:0";

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                ReferenceManager.getInstanceFor(connection);
            }
        });
    }

    private ReferenceManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature(NAMESPACE);
    }

    public static synchronized ReferenceManager getInstanceFor(XMPPConnection connection) {
        ReferenceManager manager;
        synchronized (ReferenceManager.class) {
            manager = (ReferenceManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new ReferenceManager(connection);
                INSTANCES.put(connection, manager);
            }
        }
        return manager;
    }
}
