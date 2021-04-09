package org.jivesoftware.smackx.geoloc;

import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.geoloc.packet.GeoLocation;
import org.jivesoftware.smackx.geoloc.packet.GeoLocation.Builder;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubException.NotALeafNodeException;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jxmpp.jid.Jid;

public final class GeoLocationManager extends Manager {
    private static final Map<XMPPConnection, GeoLocationManager> INSTANCES = new WeakHashMap();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                GeoLocationManager.getInstanceFor(connection);
            }
        });
    }

    public GeoLocationManager(XMPPConnection connection) {
        super(connection);
    }

    public static synchronized GeoLocationManager getInstanceFor(XMPPConnection connection) {
        GeoLocationManager geoLocationManager;
        synchronized (GeoLocationManager.class) {
            geoLocationManager = (GeoLocationManager) INSTANCES.get(connection);
            if (geoLocationManager == null) {
                geoLocationManager = new GeoLocationManager(connection);
                INSTANCES.put(connection, geoLocationManager);
            }
        }
        return geoLocationManager;
    }

    public void sendGeoLocationToJid(GeoLocation geoLocation, Jid jid) throws InterruptedException, NotConnectedException {
        XMPPConnection connection = connection();
        Message geoLocationMessage = new Message(jid);
        geoLocationMessage.addExtension(geoLocation);
        connection.sendStanza(geoLocationMessage);
    }

    public static boolean isGeoLocationMessage(Message message) {
        return GeoLocation.from(message) != null;
    }

    public void sendGeolocation(GeoLocation geoLocation) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotALeafNodeException {
        getNode().publish(new PayloadItem(geoLocation));
    }

    public void stopPublishingGeolocation() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotALeafNodeException {
        getNode().publish(new PayloadItem(new Builder().build()));
    }

    private LeafNode getNode() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotALeafNodeException {
        return PubSubManager.getInstance(connection()).getOrCreateLeafNode(GeoLocation.NAMESPACE);
    }
}
