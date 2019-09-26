package org.jivesoftware.smackx.xdatalayout;

import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.xdatalayout.packet.DataLayout;

public class XDataLayoutManager {
    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                ServiceDiscoveryManager.getInstanceFor(connection).addFeature(DataLayout.NAMESPACE);
            }
        });
    }
}
