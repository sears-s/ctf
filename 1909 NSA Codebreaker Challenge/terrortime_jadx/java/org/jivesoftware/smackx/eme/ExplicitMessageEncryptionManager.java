package org.jivesoftware.smackx.eme;

import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;

public final class ExplicitMessageEncryptionManager {
    private static final Map<XMPPConnection, ExplicitMessageEncryptionManager> INSTANCES = new WeakHashMap();
    public static final String NAMESPACE_V0 = "urn:xmpp:eme:0";

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                ExplicitMessageEncryptionManager.getInstanceFor(connection);
            }
        });
    }

    public static synchronized ExplicitMessageEncryptionManager getInstanceFor(XMPPConnection connection) {
        ExplicitMessageEncryptionManager manager;
        synchronized (ExplicitMessageEncryptionManager.class) {
            manager = (ExplicitMessageEncryptionManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new ExplicitMessageEncryptionManager(connection);
                INSTANCES.put(connection, manager);
            }
        }
        return manager;
    }

    private ExplicitMessageEncryptionManager(XMPPConnection connection) {
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature("urn:xmpp:eme:0");
    }
}
