package org.jivesoftware.smackx.time;

import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.time.packet.Time;
import org.jxmpp.jid.Jid;

public final class EntityTimeManager extends Manager {
    private static final Map<XMPPConnection, EntityTimeManager> INSTANCES = new WeakHashMap();
    private static boolean autoEnable = true;
    /* access modifiers changed from: private */
    public boolean enabled = false;

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                EntityTimeManager.getInstanceFor(connection);
            }
        });
    }

    public static void setAutoEnable(boolean autoEnable2) {
        autoEnable = autoEnable2;
    }

    public static synchronized EntityTimeManager getInstanceFor(XMPPConnection connection) {
        EntityTimeManager entityTimeManager;
        synchronized (EntityTimeManager.class) {
            entityTimeManager = (EntityTimeManager) INSTANCES.get(connection);
            if (entityTimeManager == null) {
                entityTimeManager = new EntityTimeManager(connection);
                INSTANCES.put(connection, entityTimeManager);
            }
        }
        return entityTimeManager;
    }

    private EntityTimeManager(XMPPConnection connection) {
        super(connection);
        if (autoEnable) {
            enable();
        }
        AnonymousClass2 r1 = new AbstractIqRequestHandler(Time.ELEMENT, Time.NAMESPACE, Type.get, Mode.async) {
            public IQ handleIQRequest(IQ iqRequest) {
                if (EntityTimeManager.this.enabled) {
                    return Time.createResponse(iqRequest);
                }
                return IQ.createErrorResponse(iqRequest, Condition.not_acceptable);
            }
        };
        connection.registerIQRequestHandler(r1);
    }

    public synchronized void enable() {
        if (!this.enabled) {
            ServiceDiscoveryManager.getInstanceFor(connection()).addFeature(Time.NAMESPACE);
            this.enabled = true;
        }
    }

    public synchronized void disable() {
        if (this.enabled) {
            ServiceDiscoveryManager.getInstanceFor(connection()).removeFeature(Time.NAMESPACE);
            this.enabled = false;
        }
    }

    public boolean isTimeSupported(Jid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(jid, Time.NAMESPACE);
    }

    public Time getTime(Jid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (!isTimeSupported(jid)) {
            return null;
        }
        Time request = new Time();
        request.setTo(jid);
        return (Time) connection().createStanzaCollectorAndSend(request).nextResultOrThrow();
    }
}
