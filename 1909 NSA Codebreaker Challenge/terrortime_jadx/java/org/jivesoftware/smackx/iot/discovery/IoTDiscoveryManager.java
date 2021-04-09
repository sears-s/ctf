package org.jivesoftware.smackx.iot.discovery;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.iot.IoTManager;
import org.jivesoftware.smackx.iot.Thing;
import org.jivesoftware.smackx.iot.control.IoTControlManager;
import org.jivesoftware.smackx.iot.data.IoTDataManager;
import org.jivesoftware.smackx.iot.discovery.element.IoTClaimed;
import org.jivesoftware.smackx.iot.discovery.element.IoTDisown;
import org.jivesoftware.smackx.iot.discovery.element.IoTDisowned;
import org.jivesoftware.smackx.iot.discovery.element.IoTMine;
import org.jivesoftware.smackx.iot.discovery.element.IoTRegister;
import org.jivesoftware.smackx.iot.discovery.element.IoTRemove;
import org.jivesoftware.smackx.iot.discovery.element.IoTRemoved;
import org.jivesoftware.smackx.iot.discovery.element.IoTUnregister;
import org.jivesoftware.smackx.iot.discovery.element.Tag;
import org.jivesoftware.smackx.iot.element.NodeInfo;
import org.jivesoftware.smackx.iot.provisioning.IoTProvisioningManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;

public final class IoTDiscoveryManager extends Manager {
    private static final Map<XMPPConnection, IoTDiscoveryManager> INSTANCES = new WeakHashMap();
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(IoTDiscoveryManager.class.getName());
    private Jid preconfiguredRegistry;
    private final Map<NodeInfo, ThingState> things = new HashMap();
    private final Set<Jid> usedRegistries = new HashSet();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                if (IoTManager.isAutoEnableActive()) {
                    IoTDiscoveryManager.getInstanceFor(connection);
                }
            }
        });
    }

    public static synchronized IoTDiscoveryManager getInstanceFor(XMPPConnection connection) {
        IoTDiscoveryManager manager;
        synchronized (IoTDiscoveryManager.class) {
            manager = (IoTDiscoveryManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new IoTDiscoveryManager(connection);
                INSTANCES.put(connection, manager);
            }
        }
        return manager;
    }

    private IoTDiscoveryManager(XMPPConnection connection) {
        super(connection);
        AnonymousClass2 r1 = new AbstractIqRequestHandler(IoTClaimed.ELEMENT, "urn:xmpp:iot:discovery", Type.set, Mode.sync) {
            public IQ handleIQRequest(IQ iqRequest) {
                if (!IoTDiscoveryManager.this.isRegistry(iqRequest.getFrom())) {
                    Logger access$000 = IoTDiscoveryManager.LOGGER;
                    Level level = Level.SEVERE;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Received control stanza from non-registry entity: ");
                    sb.append(iqRequest);
                    access$000.log(level, sb.toString());
                    return null;
                }
                IoTClaimed iotClaimed = (IoTClaimed) iqRequest;
                Jid owner = iotClaimed.getJid();
                IoTDiscoveryManager.this.getStateFor(iotClaimed.getNodeInfo()).setOwner(owner.asBareJid());
                Logger access$0002 = IoTDiscoveryManager.LOGGER;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Our thing got claimed by ");
                sb2.append(owner);
                sb2.append(". ");
                sb2.append(iotClaimed);
                access$0002.info(sb2.toString());
                try {
                    IoTProvisioningManager.getInstanceFor(IoTDiscoveryManager.this.connection()).sendFriendshipRequest(owner.asBareJid());
                } catch (InterruptedException | NotConnectedException e) {
                    IoTDiscoveryManager.LOGGER.log(Level.WARNING, "Could not friendship owner", e);
                }
                return IQ.createResultIQ(iqRequest);
            }
        };
        connection.registerIQRequestHandler(r1);
        AnonymousClass3 r7 = new AbstractIqRequestHandler("disown", "urn:xmpp:iot:discovery", Type.set, Mode.sync) {
            public IQ handleIQRequest(IQ iqRequest) {
                if (!IoTDiscoveryManager.this.isRegistry(iqRequest.getFrom())) {
                    Logger access$000 = IoTDiscoveryManager.LOGGER;
                    Level level = Level.SEVERE;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Received control stanza from non-registry entity: ");
                    sb.append(iqRequest);
                    access$000.log(level, sb.toString());
                    return null;
                }
                IoTDisowned iotDisowned = (IoTDisowned) iqRequest;
                Jid from = iqRequest.getFrom();
                NodeInfo nodeInfo = iotDisowned.getNodeInfo();
                ThingState state = IoTDiscoveryManager.this.getStateFor(nodeInfo);
                String str = "Received <disowned/> for ";
                if (!from.equals((CharSequence) state.getRegistry())) {
                    Logger access$0002 = IoTDiscoveryManager.LOGGER;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append(nodeInfo);
                    sb2.append(" from ");
                    sb2.append(from);
                    sb2.append(" but this is not the registry ");
                    sb2.append(state.getRegistry());
                    sb2.append(" of the thing.");
                    access$0002.severe(sb2.toString());
                    return null;
                }
                if (state.isOwned()) {
                    state.setUnowned();
                } else {
                    Logger access$0003 = IoTDiscoveryManager.LOGGER;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(str);
                    sb3.append(nodeInfo);
                    sb3.append(" but thing was not owned.");
                    access$0003.fine(sb3.toString());
                }
                return IQ.createResultIQ(iqRequest);
            }
        };
        connection.registerIQRequestHandler(r7);
        AnonymousClass4 r12 = new AbstractIqRequestHandler(IoTRemoved.ELEMENT, "urn:xmpp:iot:discovery", Type.set, Mode.async) {
            public IQ handleIQRequest(IQ iqRequest) {
                if (!IoTDiscoveryManager.this.isRegistry(iqRequest.getFrom())) {
                    Logger access$000 = IoTDiscoveryManager.LOGGER;
                    Level level = Level.SEVERE;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Received control stanza from non-registry entity: ");
                    sb.append(iqRequest);
                    access$000.log(level, sb.toString());
                    return null;
                }
                IoTRemoved iotRemoved = (IoTRemoved) iqRequest;
                IoTDiscoveryManager.this.getStateFor(iotRemoved.getNodeInfo()).setRemoved();
                try {
                    IoTProvisioningManager.getInstanceFor(IoTDiscoveryManager.this.connection()).unfriend(iotRemoved.getFrom());
                } catch (InterruptedException | NotConnectedException e) {
                    IoTDiscoveryManager.LOGGER.log(Level.SEVERE, "Could not unfriend registry after <removed/>", e);
                }
                return IQ.createResultIQ(iqRequest);
            }
        };
        connection.registerIQRequestHandler(r12);
    }

    public Jid findRegistry() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Jid jid = this.preconfiguredRegistry;
        if (jid != null) {
            return jid;
        }
        List<DiscoverInfo> discoverInfos = ServiceDiscoveryManager.getInstanceFor(connection()).findServicesDiscoverInfo("urn:xmpp:iot:discovery", true, true);
        if (!discoverInfos.isEmpty()) {
            return ((DiscoverInfo) discoverInfos.get(0)).getFrom();
        }
        return null;
    }

    public ThingState registerThing(Thing thing) throws NotConnectedException, InterruptedException, NoResponseException, XMPPErrorException, IoTClaimedException {
        return registerThing(findRegistry(), thing);
    }

    public ThingState registerThing(Jid registry, Thing thing) throws NotConnectedException, InterruptedException, NoResponseException, XMPPErrorException, IoTClaimedException {
        XMPPConnection connection = connection();
        IoTRegister iotRegister = new IoTRegister(thing.getMetaTags(), thing.getNodeInfo(), thing.isSelfOwened());
        iotRegister.setTo(registry);
        IQ result = (IQ) connection.createStanzaCollectorAndSend(iotRegister).nextResultOrThrow();
        if (!(result instanceof IoTClaimed)) {
            ThingState state = getStateFor(thing.getNodeInfo());
            state.setRegistry(registry.asBareJid());
            interactWithRegistry(registry);
            IoTDataManager.getInstanceFor(connection).installThing(thing);
            IoTControlManager.getInstanceFor(connection).installThing(thing);
            return state;
        }
        throw new IoTClaimedException((IoTClaimed) result);
    }

    public IoTClaimed claimThing(Collection<Tag> metaTags) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return claimThing(metaTags, true);
    }

    public IoTClaimed claimThing(Collection<Tag> metaTags, boolean publicThing) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return claimThing(findRegistry(), metaTags, publicThing);
    }

    public IoTClaimed claimThing(Jid registry, Collection<Tag> metaTags, boolean publicThing) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        interactWithRegistry(registry);
        IoTMine iotMine = new IoTMine(metaTags, publicThing);
        iotMine.setTo(registry);
        IoTClaimed iotClaimed = (IoTClaimed) connection().createStanzaCollectorAndSend(iotMine).nextResultOrThrow();
        IoTProvisioningManager.getInstanceFor(connection()).sendFriendshipRequest(iotClaimed.getJid().asBareJid());
        return iotClaimed;
    }

    public void removeThing(BareJid thing) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        removeThing(thing, NodeInfo.EMPTY);
    }

    public void removeThing(BareJid thing, NodeInfo nodeInfo) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        removeThing(findRegistry(), thing, nodeInfo);
    }

    public void removeThing(Jid registry, BareJid thing, NodeInfo nodeInfo) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        interactWithRegistry(registry);
        IoTRemove iotRemove = new IoTRemove(thing, nodeInfo);
        iotRemove.setTo(registry);
        connection().createStanzaCollectorAndSend(iotRemove).nextResultOrThrow();
    }

    public void unregister() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        unregister(NodeInfo.EMPTY);
    }

    public void unregister(NodeInfo nodeInfo) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        unregister(findRegistry(), nodeInfo);
    }

    public void unregister(Jid registry, NodeInfo nodeInfo) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        interactWithRegistry(registry);
        IoTUnregister iotUnregister = new IoTUnregister(nodeInfo);
        iotUnregister.setTo(registry);
        connection().createStanzaCollectorAndSend(iotUnregister).nextResultOrThrow();
        getStateFor(nodeInfo).setUnregistered();
        XMPPConnection connection = connection();
        IoTDataManager.getInstanceFor(connection).uninstallThing(nodeInfo);
        IoTControlManager.getInstanceFor(connection).uninstallThing(nodeInfo);
    }

    public void disownThing(Jid thing) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        disownThing(thing, NodeInfo.EMPTY);
    }

    public void disownThing(Jid thing, NodeInfo nodeInfo) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        disownThing(findRegistry(), thing, nodeInfo);
    }

    public void disownThing(Jid registry, Jid thing, NodeInfo nodeInfo) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        interactWithRegistry(registry);
        IoTDisown iotDisown = new IoTDisown(thing, nodeInfo);
        iotDisown.setTo(registry);
        connection().createStanzaCollectorAndSend(iotDisown).nextResultOrThrow();
    }

    public boolean isRegistry(BareJid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Objects.requireNonNull(jid, "JID argument must not be null");
        if (!jid.equals((CharSequence) findRegistry()) && !this.usedRegistries.contains(jid)) {
            return false;
        }
        return true;
    }

    public boolean isRegistry(Jid jid) {
        try {
            return isRegistry(jid.asBareJid());
        } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e) {
            Logger logger = LOGGER;
            Level level = Level.WARNING;
            StringBuilder sb = new StringBuilder();
            sb.append("Could not determine if ");
            sb.append(jid);
            sb.append(" is a registry");
            logger.log(level, sb.toString(), e);
            return false;
        }
    }

    private void interactWithRegistry(Jid registry) throws NotConnectedException, InterruptedException {
        if (this.usedRegistries.add(registry)) {
            IoTProvisioningManager.getInstanceFor(connection()).sendFriendshipRequestIfRequired(registry.asBareJid());
        }
    }

    public ThingState getStateFor(Thing thing) {
        return (ThingState) this.things.get(thing.getNodeInfo());
    }

    /* access modifiers changed from: private */
    public ThingState getStateFor(NodeInfo nodeInfo) {
        ThingState state = (ThingState) this.things.get(nodeInfo);
        if (state != null) {
            return state;
        }
        ThingState state2 = new ThingState(nodeInfo);
        this.things.put(nodeInfo, state2);
        return state2;
    }
}
