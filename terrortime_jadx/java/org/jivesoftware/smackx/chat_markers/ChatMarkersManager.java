package org.jivesoftware.smackx.chat_markers;

import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.chat_markers.element.ChatMarkersElements;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;

public final class ChatMarkersManager extends Manager {
    private static final Map<XMPPConnection, ChatMarkersManager> INSTANCES = new WeakHashMap();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                ChatMarkersManager.getInstanceFor(connection);
            }
        });
    }

    public static synchronized ChatMarkersManager getInstanceFor(XMPPConnection connection) {
        ChatMarkersManager chatMarkersManager;
        synchronized (ChatMarkersManager.class) {
            chatMarkersManager = (ChatMarkersManager) INSTANCES.get(connection);
            if (chatMarkersManager == null) {
                chatMarkersManager = new ChatMarkersManager(connection);
                INSTANCES.put(connection, chatMarkersManager);
            }
        }
        return chatMarkersManager;
    }

    private ChatMarkersManager(XMPPConnection connection) {
        super(connection);
    }

    public boolean isSupportedByServer() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).serverSupportsFeature(ChatMarkersElements.NAMESPACE);
    }
}
