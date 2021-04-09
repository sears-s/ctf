package org.jivesoftware.smackx.pep;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jivesoftware.smack.AsyncButOrdered;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.jidtype.AbstractJidTypeFilter.JidType;
import org.jivesoftware.smack.filter.jidtype.FromJidTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubException.NotAPubSubNodeException;
import org.jivesoftware.smackx.pubsub.PubSubFeature;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.filter.EventExtensionFilter;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;

public final class PEPManager extends Manager {
    private static final StanzaFilter FROM_BARE_JID_WITH_EVENT_EXTENSION_FILTER = new AndFilter(new FromJidTypeFilter(JidType.BareJid), EventExtensionFilter.INSTANCE);
    private static final Map<XMPPConnection, PEPManager> INSTANCES = new WeakHashMap();
    private static final PubSubFeature[] REQUIRED_FEATURES = {PubSubFeature.auto_create, PubSubFeature.auto_subscribe, PubSubFeature.filtered_notifications};
    /* access modifiers changed from: private */
    public final AsyncButOrdered<EntityBareJid> asyncButOrdered = new AsyncButOrdered<>();
    /* access modifiers changed from: private */
    public final Set<PEPListener> pepListeners = new CopyOnWriteArraySet();

    public static synchronized PEPManager getInstanceFor(XMPPConnection connection) {
        PEPManager pepManager;
        synchronized (PEPManager.class) {
            pepManager = (PEPManager) INSTANCES.get(connection);
            if (pepManager == null) {
                pepManager = new PEPManager(connection);
                INSTANCES.put(connection, pepManager);
            }
        }
        return pepManager;
    }

    private PEPManager(XMPPConnection connection) {
        super(connection);
        connection.addSyncStanzaListener(new StanzaListener() {
            static final /* synthetic */ boolean $assertionsDisabled = false;

            static {
                Class<PEPManager> cls = PEPManager.class;
            }

            public void processStanza(Stanza stanza) {
                final Message message = (Message) stanza;
                final EventElement event = EventElement.from(stanza);
                final EntityBareJid from = message.getFrom().asEntityBareJidIfPossible();
                PEPManager.this.asyncButOrdered.performAsyncButOrdered(from, new Runnable() {
                    public void run() {
                        for (PEPListener listener : PEPManager.this.pepListeners) {
                            listener.eventReceived(from, event, message);
                        }
                    }
                });
            }
        }, FROM_BARE_JID_WITH_EVENT_EXTENSION_FILTER);
    }

    public boolean addPEPListener(PEPListener pepListener) {
        return this.pepListeners.add(pepListener);
    }

    public boolean removePEPListener(PEPListener pepListener) {
        return this.pepListeners.remove(pepListener);
    }

    public void publish(Item item, String node) throws NotConnectedException, InterruptedException, NoResponseException, XMPPErrorException, NotAPubSubNodeException {
        XMPPConnection connection = connection();
        ((LeafNode) PubSubManager.getInstance(connection, connection.getUser().asEntityBareJid()).getNode(node)).publish(item);
    }

    public boolean isSupported() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        XMPPConnection connection = connection();
        return ServiceDiscoveryManager.getInstanceFor(connection).supportsFeatures((Jid) connection.getUser().asBareJid(), (CharSequence[]) REQUIRED_FEATURES);
    }
}
