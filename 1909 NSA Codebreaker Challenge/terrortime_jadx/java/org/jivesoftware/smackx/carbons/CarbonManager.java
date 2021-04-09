package org.jivesoftware.smackx.carbons;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.AsyncButOrdered;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.ExceptionCallback;
import org.jivesoftware.smack.util.SuccessCallback;
import org.jivesoftware.smackx.carbons.packet.Carbon.Disable;
import org.jivesoftware.smackx.carbons.packet.Carbon.Enable;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension.Direction;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension.Private;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityFullJid;

public final class CarbonManager extends Manager {
    private static final StanzaFilter CARBON_EXTENSION_FILTER;
    private static Map<XMPPConnection, CarbonManager> INSTANCES = new WeakHashMap();
    /* access modifiers changed from: private */
    public final StanzaListener carbonsListener;
    /* access modifiers changed from: private */
    public final AsyncButOrdered<BareJid> carbonsListenerAsyncButOrdered = new AsyncButOrdered<>();
    /* access modifiers changed from: private */
    public volatile boolean enabled_state = false;
    /* access modifiers changed from: private */
    public final Set<CarbonCopyReceivedListener> listeners = new CopyOnWriteArraySet();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                CarbonManager.getInstanceFor(connection);
            }
        });
        String str = "urn:xmpp:carbons:2";
        CARBON_EXTENSION_FILTER = new AndFilter(new OrFilter(new StanzaExtensionFilter(Direction.sent.name(), str), new StanzaExtensionFilter(Direction.received.name(), str)), StanzaTypeFilter.MESSAGE);
    }

    private CarbonManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature("urn:xmpp:carbons:2");
        this.carbonsListener = new StanzaListener() {
            public void processStanza(Stanza stanza) throws NotConnectedException, InterruptedException {
                final Message wrappingMessage = (Message) stanza;
                CarbonExtension carbonExtension = CarbonExtension.from(wrappingMessage);
                final Direction direction = carbonExtension.getDirection();
                final Message carbonCopy = (Message) carbonExtension.getForwarded().getForwardedStanza();
                CarbonManager.this.carbonsListenerAsyncButOrdered.performAsyncButOrdered(carbonCopy.getFrom().asBareJid(), new Runnable() {
                    public void run() {
                        for (CarbonCopyReceivedListener listener : CarbonManager.this.listeners) {
                            listener.onCarbonCopyReceived(direction, carbonCopy, wrappingMessage);
                        }
                    }
                });
            }
        };
        connection.addConnectionListener(new AbstractConnectionListener() {
            static final /* synthetic */ boolean $assertionsDisabled = false;

            static {
                Class<CarbonManager> cls = CarbonManager.class;
            }

            public void connectionClosed() {
                CarbonManager.this.enabled_state = false;
                boolean removeSyncStanzaListener = CarbonManager.this.connection().removeSyncStanzaListener(CarbonManager.this.carbonsListener);
            }

            public void authenticated(XMPPConnection connection, boolean resumed) {
                if (!resumed) {
                    CarbonManager.this.enabled_state = false;
                }
                CarbonManager.this.addCarbonsListener(connection);
            }
        });
        addCarbonsListener(connection);
    }

    /* access modifiers changed from: private */
    public void addCarbonsListener(XMPPConnection connection) {
        EntityFullJid localAddress = connection.getUser();
        if (localAddress != null) {
            connection.addSyncStanzaListener(this.carbonsListener, new AndFilter(CARBON_EXTENSION_FILTER, FromMatchesFilter.createBare(localAddress)));
        }
    }

    public static synchronized CarbonManager getInstanceFor(XMPPConnection connection) {
        CarbonManager carbonManager;
        synchronized (CarbonManager.class) {
            carbonManager = (CarbonManager) INSTANCES.get(connection);
            if (carbonManager == null) {
                carbonManager = new CarbonManager(connection);
                INSTANCES.put(connection, carbonManager);
            }
        }
        return carbonManager;
    }

    private static IQ carbonsEnabledIQ(boolean new_state) {
        if (new_state) {
            return new Enable();
        }
        return new Disable();
    }

    public boolean addCarbonCopyReceivedListener(CarbonCopyReceivedListener listener) {
        return this.listeners.add(listener);
    }

    public boolean removeCarbonCopyReceivedListener(CarbonCopyReceivedListener listener) {
        return this.listeners.remove(listener);
    }

    public boolean isSupportedByServer() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).serverSupportsFeature("urn:xmpp:carbons:2");
    }

    @Deprecated
    public void sendCarbonsEnabled(boolean new_state) throws NotConnectedException, InterruptedException {
        sendUseCarbons(new_state, null);
    }

    public void enableCarbonsAsync(ExceptionCallback<Exception> exceptionCallback) {
        sendUseCarbons(true, exceptionCallback);
    }

    public void disableCarbonsAsync(ExceptionCallback<Exception> exceptionCallback) {
        sendUseCarbons(false, exceptionCallback);
    }

    private void sendUseCarbons(final boolean use, ExceptionCallback<Exception> exceptionCallback) {
        connection().sendIqRequestAsync(carbonsEnabledIQ(use)).onSuccess(new SuccessCallback<IQ>() {
            public void onSuccess(IQ result) {
                CarbonManager.this.enabled_state = use;
            }
        }).onError(exceptionCallback);
    }

    public synchronized void setCarbonsEnabled(boolean new_state) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (this.enabled_state != new_state) {
            connection().createStanzaCollectorAndSend(carbonsEnabledIQ(new_state)).nextResultOrThrow();
            this.enabled_state = new_state;
        }
    }

    public void enableCarbons() throws XMPPException, SmackException, InterruptedException {
        setCarbonsEnabled(true);
    }

    public void disableCarbons() throws XMPPException, SmackException, InterruptedException {
        setCarbonsEnabled(false);
    }

    public boolean getCarbonsEnabled() {
        return this.enabled_state;
    }

    @Deprecated
    public static void disableCarbons(Message msg) {
        msg.addExtension(Private.INSTANCE);
    }
}
