package org.jivesoftware.smackx.push_notifications;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.push_notifications.element.DisablePushNotificationsIQ;
import org.jivesoftware.smackx.push_notifications.element.EnablePushNotificationsIQ;
import org.jxmpp.jid.Jid;

public final class PushNotificationsManager extends Manager {
    private static final Map<XMPPConnection, PushNotificationsManager> INSTANCES = new WeakHashMap();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                PushNotificationsManager.getInstanceFor(connection);
            }
        });
    }

    public static synchronized PushNotificationsManager getInstanceFor(XMPPConnection connection) {
        PushNotificationsManager pushNotificationsManager;
        synchronized (PushNotificationsManager.class) {
            pushNotificationsManager = (PushNotificationsManager) INSTANCES.get(connection);
            if (pushNotificationsManager == null) {
                pushNotificationsManager = new PushNotificationsManager(connection);
                INSTANCES.put(connection, pushNotificationsManager);
            }
        }
        return pushNotificationsManager;
    }

    private PushNotificationsManager(XMPPConnection connection) {
        super(connection);
    }

    public boolean isSupported() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).accountSupportsFeatures("urn:xmpp:push:0");
    }

    public boolean enable(Jid pushJid, String node) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return enable(pushJid, node, null);
    }

    public boolean enable(Jid pushJid, String node, HashMap<String, String> publishOptions) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return changePushNotificationsStatus(new EnablePushNotificationsIQ(pushJid, node, publishOptions));
    }

    public boolean disableAll(Jid pushJid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return disable(pushJid, null);
    }

    public boolean disable(Jid pushJid, String node) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return changePushNotificationsStatus(new DisablePushNotificationsIQ(pushJid, node));
    }

    private boolean changePushNotificationsStatus(IQ iq) throws NotConnectedException, InterruptedException, NoResponseException, XMPPErrorException {
        return ((IQ) connection().createStanzaCollectorAndSend(iq).nextResultOrThrow()).getType() != Type.error;
    }
}
