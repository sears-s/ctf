package org.jivesoftware.smackx.iqlast;

import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jxmpp.jid.Jid;

public final class LastActivityManager extends Manager {
    private static boolean enabledPerDefault = true;
    private static final Map<XMPPConnection, LastActivityManager> instances = new WeakHashMap();
    /* access modifiers changed from: private */
    public boolean enabled = false;
    private volatile long lastMessageSent;

    /* renamed from: org.jivesoftware.smackx.iqlast.LastActivityManager$5 reason: invalid class name */
    static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$Presence$Mode = new int[Mode.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$packet$Presence$Mode[Mode.available.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$Presence$Mode[Mode.chat.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                LastActivityManager.getInstanceFor(connection);
            }
        });
    }

    public static void setEnabledPerDefault(boolean enabledPerDefault2) {
        enabledPerDefault = enabledPerDefault2;
    }

    public static synchronized LastActivityManager getInstanceFor(XMPPConnection connection) {
        LastActivityManager lastActivityManager;
        synchronized (LastActivityManager.class) {
            lastActivityManager = (LastActivityManager) instances.get(connection);
            if (lastActivityManager == null) {
                lastActivityManager = new LastActivityManager(connection);
            }
        }
        return lastActivityManager;
    }

    private LastActivityManager(XMPPConnection connection) {
        super(connection);
        connection.addStanzaSendingListener(new StanzaListener() {
            public void processStanza(Stanza packet) {
                Mode mode = ((Presence) packet).getMode();
                if (mode != null) {
                    int i = AnonymousClass5.$SwitchMap$org$jivesoftware$smack$packet$Presence$Mode[mode.ordinal()];
                    if (i == 1 || i == 2) {
                        LastActivityManager.this.resetIdleTime();
                    }
                }
            }
        }, StanzaTypeFilter.PRESENCE);
        connection.addStanzaSendingListener(new StanzaListener() {
            public void processStanza(Stanza packet) {
                if (((Message) packet).getType() != Type.error) {
                    LastActivityManager.this.resetIdleTime();
                }
            }
        }, StanzaTypeFilter.MESSAGE);
        AnonymousClass4 r2 = new AbstractIqRequestHandler("query", LastActivity.NAMESPACE, IQ.Type.get, IQRequestHandler.Mode.async) {
            public IQ handleIQRequest(IQ iqRequest) {
                if (!LastActivityManager.this.enabled) {
                    return IQ.createErrorResponse(iqRequest, Condition.not_acceptable);
                }
                LastActivity message = new LastActivity();
                message.setType(IQ.Type.result);
                message.setTo(iqRequest.getFrom());
                message.setFrom(iqRequest.getTo());
                message.setStanzaId(iqRequest.getStanzaId());
                message.setLastActivity(LastActivityManager.this.getIdleTime());
                return message;
            }
        };
        connection.registerIQRequestHandler(r2);
        if (enabledPerDefault) {
            enable();
        }
        resetIdleTime();
        instances.put(connection, this);
    }

    public synchronized void enable() {
        ServiceDiscoveryManager.getInstanceFor(connection()).addFeature(LastActivity.NAMESPACE);
        this.enabled = true;
    }

    public synchronized void disable() {
        ServiceDiscoveryManager.getInstanceFor(connection()).removeFeature(LastActivity.NAMESPACE);
        this.enabled = false;
    }

    /* access modifiers changed from: private */
    public void resetIdleTime() {
        this.lastMessageSent = System.currentTimeMillis();
    }

    /* access modifiers changed from: private */
    public long getIdleTime() {
        return (System.currentTimeMillis() - this.lastMessageSent) / 1000;
    }

    public LastActivity getLastActivity(Jid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return (LastActivity) connection().createStanzaCollectorAndSend(new LastActivity(jid)).nextResultOrThrow();
    }

    public boolean isLastActivitySupported(Jid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(jid, LastActivity.NAMESPACE);
    }
}
