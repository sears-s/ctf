package org.jivesoftware.smackx.iot.control;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.iot.IoTManager;
import org.jivesoftware.smackx.iot.Thing;
import org.jivesoftware.smackx.iot.control.element.IoTSetRequest;
import org.jivesoftware.smackx.iot.control.element.IoTSetResponse;
import org.jivesoftware.smackx.iot.control.element.SetData;
import org.jivesoftware.smackx.iot.element.NodeInfo;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;

public final class IoTControlManager extends IoTManager {
    private static final Map<XMPPConnection, IoTControlManager> INSTANCES = new WeakHashMap();
    /* access modifiers changed from: private */
    public final Map<NodeInfo, Thing> things = new ConcurrentHashMap();

    public static synchronized IoTControlManager getInstanceFor(XMPPConnection connection) {
        IoTControlManager manager;
        synchronized (IoTControlManager.class) {
            manager = (IoTControlManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new IoTControlManager(connection);
                INSTANCES.put(connection, manager);
            }
        }
        return manager;
    }

    private IoTControlManager(XMPPConnection connection) {
        super(connection);
        AnonymousClass1 r1 = new IoTIqRequestHandler("set", "urn:xmpp:iot:control", Type.set, Mode.async) {
            public IQ handleIoTIqRequest(IQ iqRequest) {
                IoTSetRequest iotSetRequest = (IoTSetRequest) iqRequest;
                Thing thing = (Thing) IoTControlManager.this.things.get(NodeInfo.EMPTY);
                if (thing == null) {
                    return null;
                }
                ThingControlRequest controlRequest = thing.getControlRequestHandler();
                if (controlRequest == null) {
                    return null;
                }
                try {
                    controlRequest.processRequest(iotSetRequest.getFrom(), iotSetRequest.getSetData());
                    return new IoTSetResponse(iotSetRequest);
                } catch (XMPPErrorException e) {
                    return IQ.createErrorResponse((IQ) iotSetRequest, e.getStanzaError());
                }
            }
        };
        connection.registerIQRequestHandler(r1);
    }

    public IoTSetResponse setUsingIq(FullJid jid, SetData data) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return setUsingIq(jid, (Collection<? extends SetData>) Collections.singleton(data));
    }

    public IoTSetResponse setUsingIq(FullJid jid, Collection<? extends SetData> data) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        IoTSetRequest request = new IoTSetRequest(data);
        request.setTo((Jid) jid);
        return (IoTSetResponse) connection().createStanzaCollectorAndSend(request).nextResultOrThrow();
    }

    public void installThing(Thing thing) {
        this.things.put(thing.getNodeInfo(), thing);
    }

    public Thing uninstallThing(Thing thing) {
        return uninstallThing(thing.getNodeInfo());
    }

    public Thing uninstallThing(NodeInfo nodeInfo) {
        return (Thing) this.things.remove(nodeInfo);
    }
}
