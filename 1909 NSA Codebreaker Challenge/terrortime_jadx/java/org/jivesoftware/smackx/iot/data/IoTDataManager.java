package org.jivesoftware.smackx.iot.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.iot.IoTManager;
import org.jivesoftware.smackx.iot.Thing;
import org.jivesoftware.smackx.iot.data.element.IoTDataField;
import org.jivesoftware.smackx.iot.data.element.IoTDataReadOutAccepted;
import org.jivesoftware.smackx.iot.data.element.IoTDataRequest;
import org.jivesoftware.smackx.iot.data.element.IoTFieldsExtension;
import org.jivesoftware.smackx.iot.data.filter.IoTFieldsExtensionFilter;
import org.jivesoftware.smackx.iot.element.NodeInfo;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;

public final class IoTDataManager extends IoTManager {
    private static final Map<XMPPConnection, IoTDataManager> INSTANCES = new WeakHashMap();
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(IoTDataManager.class.getName());
    private final AtomicInteger nextSeqNr = new AtomicInteger();
    /* access modifiers changed from: private */
    public final Map<NodeInfo, Thing> things = new ConcurrentHashMap();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                if (IoTManager.isAutoEnableActive()) {
                    IoTDataManager.getInstanceFor(connection);
                }
            }
        });
    }

    public static synchronized IoTDataManager getInstanceFor(XMPPConnection connection) {
        IoTDataManager manager;
        synchronized (IoTDataManager.class) {
            manager = (IoTDataManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new IoTDataManager(connection);
                INSTANCES.put(connection, manager);
            }
        }
        return manager;
    }

    private IoTDataManager(XMPPConnection connection) {
        super(connection);
        AnonymousClass2 r1 = new IoTIqRequestHandler("req", "urn:xmpp:iot:sensordata", Type.get, Mode.async) {
            public IQ handleIoTIqRequest(IQ iqRequest) {
                final IoTDataRequest dataRequest = (IoTDataRequest) iqRequest;
                if (!dataRequest.isMomentary()) {
                    return null;
                }
                final Thing thing = (Thing) IoTDataManager.this.things.get(NodeInfo.EMPTY);
                if (thing == null) {
                    return null;
                }
                ThingMomentaryReadOutRequest readOutRequest = thing.getMomentaryReadOutRequestHandler();
                if (readOutRequest == null) {
                    return null;
                }
                readOutRequest.momentaryReadOutRequest(new ThingMomentaryReadOutResult() {
                    public void momentaryReadOut(List<? extends IoTDataField> results) {
                        IoTFieldsExtension iotFieldsExtension = IoTFieldsExtension.buildFor(dataRequest.getSequenceNr(), true, thing.getNodeInfo(), results);
                        Message message = new Message(dataRequest.getFrom());
                        message.addExtension(iotFieldsExtension);
                        try {
                            IoTDataManager.this.connection().sendStanza(message);
                        } catch (InterruptedException | NotConnectedException e) {
                            Logger access$200 = IoTDataManager.LOGGER;
                            Level level = Level.SEVERE;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Could not send read-out response ");
                            sb.append(message);
                            access$200.log(level, sb.toString(), e);
                        }
                    }
                });
                return new IoTDataReadOutAccepted(dataRequest);
            }
        };
        connection.registerIQRequestHandler(r1);
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

    /* JADX INFO: finally extract failed */
    public List<IoTFieldsExtension> requestMomentaryValuesReadOut(EntityFullJid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        XMPPConnection connection = connection();
        int seqNr = this.nextSeqNr.incrementAndGet();
        IoTDataRequest iotDataRequest = new IoTDataRequest(seqNr, true);
        iotDataRequest.setTo((Jid) jid);
        StanzaFilter doneFilter = new IoTFieldsExtensionFilter(seqNr, true);
        StanzaFilter dataFilter = new IoTFieldsExtensionFilter(seqNr, false);
        StanzaCollector doneCollector = connection.createStanzaCollector(doneFilter);
        StanzaCollector dataCollector = connection.createStanzaCollector(StanzaCollector.newConfiguration().setStanzaFilter(dataFilter).setCollectorToReset(doneCollector));
        try {
            connection.createStanzaCollectorAndSend(iotDataRequest).nextResultOrThrow();
            doneCollector.nextResult();
            dataCollector.cancel();
            doneCollector.cancel();
            int collectedCount = dataCollector.getCollectedCount();
            List<IoTFieldsExtension> res = new ArrayList<>(collectedCount);
            for (int i = 0; i < collectedCount; i++) {
                res.add(IoTFieldsExtension.from((Message) dataCollector.pollResult()));
            }
            return res;
        } catch (Throwable th) {
            dataCollector.cancel();
            doneCollector.cancel();
            throw th;
        }
    }
}
