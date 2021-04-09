package org.jivesoftware.smackx.sid;

import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.ToTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.sid.element.OriginIdElement;

public final class StableUniqueStanzaIdManager extends Manager {
    private static final StanzaListener ADD_ORIGIN_ID_INTERCEPTOR = new StanzaListener() {
        public void processStanza(Stanza stanza) {
            OriginIdElement.addOriginId((Message) stanza);
        }
    };
    private static final Map<XMPPConnection, StableUniqueStanzaIdManager> INSTANCES = new WeakHashMap();
    public static final String NAMESPACE = "urn:xmpp:sid:0";
    private static final StanzaFilter OUTGOING_FILTER = new AndFilter(MessageTypeFilter.NORMAL_OR_CHAT_OR_HEADLINE, ToTypeFilter.ENTITY_FULL_OR_BARE_JID);

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                StableUniqueStanzaIdManager.getInstanceFor(connection);
            }
        });
    }

    private StableUniqueStanzaIdManager(XMPPConnection connection) {
        super(connection);
        enable();
    }

    public static synchronized StableUniqueStanzaIdManager getInstanceFor(XMPPConnection connection) {
        StableUniqueStanzaIdManager manager;
        synchronized (StableUniqueStanzaIdManager.class) {
            manager = (StableUniqueStanzaIdManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new StableUniqueStanzaIdManager(connection);
                INSTANCES.put(connection, manager);
            }
        }
        return manager;
    }

    public synchronized void enable() {
        ServiceDiscoveryManager.getInstanceFor(connection()).addFeature(NAMESPACE);
        connection().addStanzaInterceptor(ADD_ORIGIN_ID_INTERCEPTOR, new AndFilter(OUTGOING_FILTER, new NotFilter(OUTGOING_FILTER)));
    }

    public synchronized void disable() {
        ServiceDiscoveryManager.getInstanceFor(connection()).removeFeature(NAMESPACE);
        connection().removeStanzaInterceptor(ADD_ORIGIN_ID_INTERCEPTOR);
    }

    public synchronized boolean isEnabled() {
        return ServiceDiscoveryManager.getInstanceFor(connection()).includesFeature(NAMESPACE);
    }
}
