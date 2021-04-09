package org.jivesoftware.smackx.spoiler;

import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;

public final class SpoilerManager extends Manager {
    private static final Map<XMPPConnection, SpoilerManager> INSTANCES = new WeakHashMap();
    public static final String NAMESPACE_0 = "urn:xmpp:spoiler:0";
    private final ServiceDiscoveryManager serviceDiscoveryManager;

    private SpoilerManager(XMPPConnection connection) {
        super(connection);
        this.serviceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(connection);
    }

    public void startAnnounceSupport() {
        this.serviceDiscoveryManager.addFeature("urn:xmpp:spoiler:0");
    }

    public void stopAnnounceSupport() {
        this.serviceDiscoveryManager.removeFeature("urn:xmpp:spoiler:0");
    }

    public static synchronized SpoilerManager getInstanceFor(XMPPConnection connection) {
        SpoilerManager manager;
        synchronized (SpoilerManager.class) {
            manager = (SpoilerManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new SpoilerManager(connection);
                INSTANCES.put(connection, manager);
            }
        }
        return manager;
    }
}
