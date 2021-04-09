package org.jivesoftware.smackx.pubsub;

import android.support.v4.app.NotificationCompat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.EmptyResultIQ;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.PubSubException.NotALeafNodeException;
import org.jivesoftware.smackx.pubsub.PubSubException.NotAPubSubNodeException;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.smackx.pubsub.util.NodeUtils;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

public final class PubSubManager extends Manager {
    public static final String AUTO_CREATE_FEATURE = "http://jabber.org/protocol/pubsub#auto-create";
    private static final Map<XMPPConnection, Map<BareJid, PubSubManager>> INSTANCES = new WeakHashMap();
    private static final Logger LOGGER = Logger.getLogger(PubSubManager.class.getName());
    private final Map<String, Node> nodeMap = new ConcurrentHashMap();
    private final BareJid pubSubService;

    public static PubSubManager getInstance(XMPPConnection connection) {
        DomainBareJid pubSubService2 = null;
        if (connection.isAuthenticated()) {
            try {
                pubSubService2 = getPubSubService(connection);
            } catch (NoResponseException | NotConnectedException | XMPPErrorException e) {
                LOGGER.log(Level.WARNING, "Could not determine PubSub service", e);
            } catch (InterruptedException e2) {
                LOGGER.log(Level.FINE, "Interrupted while trying to determine PubSub service", e2);
            }
        }
        if (pubSubService2 == null) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("pubsub.");
                sb.append(connection.getXMPPServiceDomain());
                pubSubService2 = JidCreate.domainBareFrom(sb.toString());
            } catch (XmppStringprepException e3) {
                throw new RuntimeException(e3);
            }
        }
        return getInstance(connection, pubSubService2);
    }

    public static synchronized PubSubManager getInstance(XMPPConnection connection, BareJid pubSubService2) {
        PubSubManager pubSubManager;
        synchronized (PubSubManager.class) {
            Map map = (Map) INSTANCES.get(connection);
            if (map == null) {
                map = new HashMap();
                INSTANCES.put(connection, map);
            }
            pubSubManager = (PubSubManager) map.get(pubSubService2);
            if (pubSubManager == null) {
                pubSubManager = new PubSubManager(connection, pubSubService2);
                map.put(pubSubService2, pubSubManager);
            }
        }
        return pubSubManager;
    }

    PubSubManager(XMPPConnection connection, BareJid toAddress) {
        super(connection);
        this.pubSubService = toAddress;
    }

    public LeafNode createNode() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        LeafNode newNode = new LeafNode(this, ((NodeExtension) sendPubsubPacket(Type.set, new NodeExtension(PubSubElementType.CREATE), null).getExtension("create", PubSubNamespace.basic.getXmlns())).getNode());
        this.nodeMap.put(newNode.getId(), newNode);
        return newNode;
    }

    public LeafNode createNode(String nodeId) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return (LeafNode) createNode(nodeId, null);
    }

    public Node createNode(String nodeId, Form config) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        PubSub request = PubSub.createPubsubPacket(this.pubSubService, Type.set, new NodeExtension(PubSubElementType.CREATE, nodeId));
        boolean isLeafNode = true;
        if (config != null) {
            request.addExtension(new FormNode(FormNodeType.CONFIGURE, config));
            FormField nodeTypeField = config.getField(ConfigureNodeFields.node_type.getFieldName());
            if (nodeTypeField != null) {
                isLeafNode = ((CharSequence) nodeTypeField.getValues().get(0)).equals(NodeType.leaf.toString());
            }
        }
        sendPubsubPacket(request);
        Node newNode = isLeafNode ? new LeafNode(this, nodeId) : new CollectionNode(this, nodeId);
        this.nodeMap.put(newNode.getId(), newNode);
        return newNode;
    }

    public <T extends Node> T getNode(String id) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotAPubSubNodeException {
        Node node = (Node) this.nodeMap.get(id);
        if (node == null) {
            DiscoverInfo info = new DiscoverInfo();
            info.setTo((Jid) this.pubSubService);
            info.setNode(id);
            DiscoverInfo infoReply = (DiscoverInfo) connection().createStanzaCollectorAndSend(info).nextResultOrThrow();
            String str = "pubsub";
            if (infoReply.hasIdentity(str, "leaf")) {
                node = new LeafNode(this, id);
            } else if (infoReply.hasIdentity(str, "collection")) {
                node = new CollectionNode(this, id);
            } else {
                throw new NotAPubSubNodeException(id, infoReply);
            }
            this.nodeMap.put(id, node);
        }
        return node;
    }

    public LeafNode getOrCreateLeafNode(String id) throws NoResponseException, NotConnectedException, InterruptedException, XMPPErrorException, NotALeafNodeException {
        try {
            return (LeafNode) getNode(id);
        } catch (NotAPubSubNodeException e) {
            return createNode(id);
        } catch (XMPPErrorException e1) {
            if (e1.getStanzaError().getCondition() == Condition.item_not_found) {
                try {
                    return createNode(id);
                } catch (XMPPErrorException e2) {
                    if (e2.getStanzaError().getCondition() == Condition.conflict) {
                        try {
                            return (LeafNode) getNode(id);
                        } catch (NotAPubSubNodeException e3) {
                            throw new IllegalStateException(e3);
                        }
                    } else {
                        throw e2;
                    }
                }
            } else if (e1.getStanzaError().getCondition() == Condition.service_unavailable) {
                Logger logger = LOGGER;
                StringBuilder sb = new StringBuilder();
                sb.append("The PubSub service ");
                sb.append(this.pubSubService);
                sb.append(" threw an DiscoInfoNodeAssertionError, trying workaround for Prosody bug #805 (https://prosody.im/issues/issue/805)");
                logger.warning(sb.toString());
                return getOrCreateLeafNodeProsodyWorkaround(id);
            } else {
                throw e1;
            }
        }
    }

    public LeafNode getLeafNode(String id) throws NotALeafNodeException, NoResponseException, NotConnectedException, InterruptedException, XMPPErrorException, NotAPubSubNodeException {
        try {
            Node node = getNode(id);
            if (node instanceof LeafNode) {
                return (LeafNode) node;
            }
            throw new NotALeafNodeException(id, this.pubSubService);
        } catch (XMPPErrorException e) {
            if (e.getStanzaError().getCondition() == Condition.service_unavailable) {
                return getLeafNodeProsodyWorkaround(id);
            }
            throw e;
        }
    }

    private LeafNode getLeafNodeProsodyWorkaround(String id) throws NoResponseException, NotConnectedException, InterruptedException, NotALeafNodeException, XMPPErrorException {
        LeafNode leafNode = new LeafNode(this, id);
        try {
            leafNode.getItems(1);
            this.nodeMap.put(id, leafNode);
            return leafNode;
        } catch (XMPPErrorException e) {
            if (e.getStanzaError().getCondition() == Condition.feature_not_implemented) {
                throw new NotALeafNodeException(id, this.pubSubService);
            }
            throw e;
        }
    }

    private LeafNode getOrCreateLeafNodeProsodyWorkaround(String id) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException, NotALeafNodeException {
        try {
            return createNode(id);
        } catch (XMPPErrorException e1) {
            if (e1.getStanzaError().getCondition() == Condition.conflict) {
                return getLeafNodeProsodyWorkaround(id);
            }
            throw e1;
        }
    }

    public <I extends Item> LeafNode tryToPublishAndPossibleAutoCreate(String id, I item) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        LeafNode leafNode = new LeafNode(this, id);
        leafNode.publish(item);
        this.nodeMap.put(id, leafNode);
        return leafNode;
    }

    public DiscoverItems discoverNodes(String nodeId) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        DiscoverItems items = new DiscoverItems();
        if (nodeId != null) {
            items.setNode(nodeId);
        }
        items.setTo((Jid) this.pubSubService);
        return (DiscoverItems) connection().createStanzaCollectorAndSend(items).nextResultOrThrow();
    }

    public List<Subscription> getSubscriptions() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ((SubscriptionsExtension) sendPubsubPacket(Type.get, new NodeExtension(PubSubElementType.SUBSCRIPTIONS), null).getExtension(PubSubElementType.SUBSCRIPTIONS.getElementName(), PubSubElementType.SUBSCRIPTIONS.getNamespace().getXmlns())).getSubscriptions();
    }

    public List<Affiliation> getAffiliations() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ((AffiliationsExtension) sendPubsubPacket(Type.get, new NodeExtension(PubSubElementType.AFFILIATIONS), null).getExtension(PubSubElementType.AFFILIATIONS)).getAffiliations();
    }

    public void deleteNode(String nodeId) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        sendPubsubPacket(Type.set, new NodeExtension(PubSubElementType.DELETE, nodeId), PubSubElementType.DELETE.getNamespace());
        this.nodeMap.remove(nodeId);
    }

    public ConfigureForm getDefaultConfiguration() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return NodeUtils.getFormFromPacket(sendPubsubPacket(Type.get, new NodeExtension(PubSubElementType.DEFAULT), PubSubElementType.DEFAULT.getNamespace()), PubSubElementType.DEFAULT);
    }

    public BareJid getServiceJid() {
        return this.pubSubService;
    }

    public DiscoverInfo getSupportedFeatures() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).discoverInfo(this.pubSubService);
    }

    public boolean supportsAutomaticNodeCreation() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(this.pubSubService, AUTO_CREATE_FEATURE);
    }

    public boolean canCreateNodesAndPublishItems() throws NoResponseException, NotConnectedException, InterruptedException, XMPPErrorException {
        LeafNode leafNode = null;
        try {
            LeafNode leafNode2 = createNode();
            if (leafNode2 != null) {
                deleteNode(leafNode2.getId());
            }
            return true;
        } catch (XMPPErrorException e) {
            if (e.getStanzaError().getCondition() == Condition.forbidden) {
                if (leafNode != null) {
                    deleteNode(leafNode.getId());
                }
                return false;
            }
            throw e;
        } catch (Throwable th) {
            if (leafNode != null) {
                deleteNode(leafNode.getId());
            }
            throw th;
        }
    }

    private PubSub sendPubsubPacket(Type type, ExtensionElement ext, PubSubNamespace ns) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return sendPubsubPacket(this.pubSubService, type, Collections.singletonList(ext), ns);
    }

    /* access modifiers changed from: 0000 */
    public XMPPConnection getConnection() {
        return connection();
    }

    /* access modifiers changed from: 0000 */
    public PubSub sendPubsubPacket(Jid to, Type type, List<ExtensionElement> extList, PubSubNamespace ns) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        PubSub pubSub = new PubSub(to, type, ns);
        for (ExtensionElement pe : extList) {
            pubSub.addExtension(pe);
        }
        return sendPubsubPacket(pubSub);
    }

    /* access modifiers changed from: 0000 */
    public PubSub sendPubsubPacket(PubSub packet) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        IQ resultIQ = (IQ) connection().createStanzaCollectorAndSend(packet).nextResultOrThrow();
        if (resultIQ instanceof EmptyResultIQ) {
            return null;
        }
        return (PubSub) resultIQ;
    }

    public static DomainBareJid getPubSubService(XMPPConnection connection) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection).findService("http://jabber.org/protocol/pubsub", true, "pubsub", NotificationCompat.CATEGORY_SERVICE);
    }
}
