package org.jivesoftware.smackx.pubsub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.FlexibleStanzaTypeFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.delay.DelayInformationManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.pubsub.Affiliation.AffiliationNamespace;
import org.jivesoftware.smackx.pubsub.SubscriptionsExtension.SubscriptionsNamespace;
import org.jivesoftware.smackx.pubsub.listener.ItemDeleteListener;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jivesoftware.smackx.pubsub.listener.NodeConfigListener;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.smackx.pubsub.util.NodeUtils;
import org.jivesoftware.smackx.shim.packet.Header;
import org.jivesoftware.smackx.shim.packet.HeadersExtension;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.Jid;

public abstract class Node {
    protected ConcurrentHashMap<NodeConfigListener, StanzaListener> configEventToListenerMap = new ConcurrentHashMap<>();
    protected final String id;
    protected ConcurrentHashMap<ItemDeleteListener, StanzaListener> itemDeleteToListenerMap = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<ItemEventListener<Item>, StanzaListener> itemEventToListenerMap = new ConcurrentHashMap<>();
    protected final PubSubManager pubSubManager;

    class EventContentFilter extends FlexibleStanzaTypeFilter<Message> {
        private final boolean allowEmpty;
        private final String firstElement;
        private final String secondElement;

        EventContentFilter(Node this$02, String elementName) {
            this(elementName, null);
        }

        EventContentFilter(String firstLevelElement, String secondLevelElement) {
            this.firstElement = firstLevelElement;
            this.secondElement = secondLevelElement;
            this.allowEmpty = this.firstElement.equals(EventElementType.items.toString()) && "item".equals(secondLevelElement);
        }

        public boolean acceptSpecific(Message message) {
            EventElement event = EventElement.from(message);
            if (event == null) {
                return false;
            }
            NodeExtension embedEvent = event.getEvent();
            if (embedEvent == null || !embedEvent.getElementName().equals(this.firstElement) || !embedEvent.getNode().equals(Node.this.getId())) {
                return false;
            }
            if (this.secondElement == null) {
                return true;
            }
            if (embedEvent instanceof EmbeddedPacketExtension) {
                List<ExtensionElement> secondLevelList = ((EmbeddedPacketExtension) embedEvent).getExtensions();
                if (this.allowEmpty && secondLevelList.isEmpty()) {
                    return true;
                }
                if (secondLevelList.size() <= 0 || !((ExtensionElement) secondLevelList.get(0)).getElementName().equals(this.secondElement)) {
                    return false;
                }
                return true;
            }
            return false;
        }
    }

    public static class ItemDeleteTranslator implements StanzaListener {
        private final ItemDeleteListener listener;

        public ItemDeleteTranslator(ItemDeleteListener eventListener) {
            this.listener = eventListener;
        }

        public void processStanza(Stanza packet) {
            EventElement event = (EventElement) packet.getExtension("event", PubSubNamespace.event.getXmlns());
            if (((ExtensionElement) event.getExtensions().get(0)).getElementName().equals(PubSubElementType.PURGE_EVENT.getElementName())) {
                this.listener.handlePurge();
                return;
            }
            ItemsExtension itemsElem = (ItemsExtension) event.getEvent();
            Collection<RetractItem> pubItems = itemsElem.getItems();
            List<String> items = new ArrayList<>(pubItems.size());
            for (RetractItem item : pubItems) {
                items.add(item.getId());
            }
            this.listener.handleDeletedItems(new ItemDeleteEvent(itemsElem.getNode(), items, Node.getSubscriptionIds(packet)));
        }
    }

    public static class ItemEventTranslator implements StanzaListener {
        private final ItemEventListener listener;

        public ItemEventTranslator(ItemEventListener eventListener) {
            this.listener = eventListener;
        }

        public void processStanza(Stanza packet) {
            ItemsExtension itemsElem = (ItemsExtension) ((EventElement) packet.getExtension("event", PubSubNamespace.event.getXmlns())).getEvent();
            this.listener.handlePublishedItems(new ItemPublishEvent(itemsElem.getNode(), itemsElem.getItems(), Node.getSubscriptionIds(packet), DelayInformationManager.getDelayTimestamp(packet)));
        }
    }

    public static class NodeConfigTranslator implements StanzaListener {
        private final NodeConfigListener listener;

        public NodeConfigTranslator(NodeConfigListener eventListener) {
            this.listener = eventListener;
        }

        public void processStanza(Stanza packet) {
            this.listener.handleNodeConfiguration((ConfigurationEvent) ((EventElement) packet.getExtension("event", PubSubNamespace.event.getXmlns())).getEvent());
        }
    }

    Node(PubSubManager pubSubManager2, String nodeId) {
        this.pubSubManager = pubSubManager2;
        this.id = nodeId;
    }

    public String getId() {
        return this.id;
    }

    public ConfigureForm getNodeConfiguration() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return NodeUtils.getFormFromPacket(sendPubsubPacket(createPubsubPacket(Type.get, new NodeExtension(PubSubElementType.CONFIGURE_OWNER, getId()))), PubSubElementType.CONFIGURE_OWNER);
    }

    public void sendConfigurationForm(Form submitForm) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        this.pubSubManager.getConnection().createStanzaCollectorAndSend(createPubsubPacket(Type.set, new FormNode(FormNodeType.CONFIGURE_OWNER, getId(), submitForm))).nextResultOrThrow();
    }

    public DiscoverInfo discoverInfo() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        DiscoverInfo info = new DiscoverInfo();
        info.setTo((Jid) this.pubSubManager.getServiceJid());
        info.setNode(getId());
        return (DiscoverInfo) this.pubSubManager.getConnection().createStanzaCollectorAndSend(info).nextResultOrThrow();
    }

    public List<Subscription> getSubscriptions() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getSubscriptions(null, null);
    }

    public List<Subscription> getSubscriptions(List<ExtensionElement> additionalExtensions, Collection<ExtensionElement> returnedExtensions) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getSubscriptions(SubscriptionsNamespace.basic, additionalExtensions, returnedExtensions);
    }

    public List<Subscription> getSubscriptionsAsOwner() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getSubscriptionsAsOwner(null, null);
    }

    public List<Subscription> getSubscriptionsAsOwner(List<ExtensionElement> additionalExtensions, Collection<ExtensionElement> returnedExtensions) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getSubscriptions(SubscriptionsNamespace.owner, additionalExtensions, returnedExtensions);
    }

    private List<Subscription> getSubscriptions(SubscriptionsNamespace subscriptionsNamespace, List<ExtensionElement> additionalExtensions, Collection<ExtensionElement> returnedExtensions) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        PubSubElementType pubSubElementType = subscriptionsNamespace.type;
        PubSub pubSub = createPubsubPacket(Type.get, new NodeExtension(pubSubElementType, getId()));
        if (additionalExtensions != null) {
            for (ExtensionElement pe : additionalExtensions) {
                pubSub.addExtension(pe);
            }
        }
        PubSub reply = sendPubsubPacket(pubSub);
        if (returnedExtensions != null) {
            returnedExtensions.addAll(reply.getExtensions());
        }
        return ((SubscriptionsExtension) reply.getExtension(pubSubElementType)).getSubscriptions();
    }

    public PubSub modifySubscriptionsAsOwner(List<Subscription> changedSubs) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return sendPubsubPacket(createPubsubPacket(Type.set, new SubscriptionsExtension(SubscriptionsNamespace.owner, getId(), changedSubs)));
    }

    public List<Affiliation> getAffiliations() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getAffiliations(null, null);
    }

    public List<Affiliation> getAffiliations(List<ExtensionElement> additionalExtensions, Collection<ExtensionElement> returnedExtensions) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getAffiliations(AffiliationNamespace.basic, additionalExtensions, returnedExtensions);
    }

    public List<Affiliation> getAffiliationsAsOwner() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getAffiliationsAsOwner(null, null);
    }

    public List<Affiliation> getAffiliationsAsOwner(List<ExtensionElement> additionalExtensions, Collection<ExtensionElement> returnedExtensions) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getAffiliations(AffiliationNamespace.owner, additionalExtensions, returnedExtensions);
    }

    private List<Affiliation> getAffiliations(AffiliationNamespace affiliationsNamespace, List<ExtensionElement> additionalExtensions, Collection<ExtensionElement> returnedExtensions) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        PubSubElementType pubSubElementType = affiliationsNamespace.type;
        PubSub pubSub = createPubsubPacket(Type.get, new NodeExtension(pubSubElementType, getId()));
        if (additionalExtensions != null) {
            for (ExtensionElement pe : additionalExtensions) {
                pubSub.addExtension(pe);
            }
        }
        PubSub reply = sendPubsubPacket(pubSub);
        if (returnedExtensions != null) {
            returnedExtensions.addAll(reply.getExtensions());
        }
        return ((AffiliationsExtension) reply.getExtension(pubSubElementType)).getAffiliations();
    }

    public PubSub modifyAffiliationAsOwner(List<Affiliation> affiliations) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        for (Affiliation affiliation : affiliations) {
            if (affiliation.getPubSubNamespace() != PubSubNamespace.owner) {
                throw new IllegalArgumentException("Must use Affiliation(BareJid, Type) affiliations");
            }
        }
        return sendPubsubPacket(createPubsubPacket(Type.set, new AffiliationsExtension(AffiliationNamespace.owner, affiliations, getId())));
    }

    public Subscription subscribe(String jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return (Subscription) sendPubsubPacket(createPubsubPacket(Type.set, new SubscribeExtension(jid, getId()))).getExtension(PubSubElementType.SUBSCRIPTION);
    }

    public Subscription subscribe(String jid, SubscribeForm subForm) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        PubSub request = createPubsubPacket(Type.set, new SubscribeExtension(jid, getId()));
        request.addExtension(new FormNode(FormNodeType.OPTIONS, subForm));
        return (Subscription) sendPubsubPacket(request).getExtension(PubSubElementType.SUBSCRIPTION);
    }

    public void unsubscribe(String jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        unsubscribe(jid, null);
    }

    public void unsubscribe(String jid, String subscriptionId) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        sendPubsubPacket(createPubsubPacket(Type.set, new UnsubscribeExtension(jid, getId(), subscriptionId)));
    }

    public SubscribeForm getSubscriptionOptions(String jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getSubscriptionOptions(jid, null);
    }

    public SubscribeForm getSubscriptionOptions(String jid, String subscriptionId) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return new SubscribeForm(((FormNode) sendPubsubPacket(createPubsubPacket(Type.get, new OptionsExtension(jid, getId(), subscriptionId))).getExtension(PubSubElementType.OPTIONS)).getForm());
    }

    public void addItemEventListener(ItemEventListener listener) {
        StanzaListener conListener = new ItemEventTranslator(listener);
        this.itemEventToListenerMap.put(listener, conListener);
        this.pubSubManager.getConnection().addSyncStanzaListener(conListener, new EventContentFilter(EventElementType.items.toString(), "item"));
    }

    public void removeItemEventListener(ItemEventListener listener) {
        StanzaListener conListener = (StanzaListener) this.itemEventToListenerMap.remove(listener);
        if (conListener != null) {
            this.pubSubManager.getConnection().removeSyncStanzaListener(conListener);
        }
    }

    public void addConfigurationListener(NodeConfigListener listener) {
        StanzaListener conListener = new NodeConfigTranslator(listener);
        this.configEventToListenerMap.put(listener, conListener);
        this.pubSubManager.getConnection().addSyncStanzaListener(conListener, new EventContentFilter(this, EventElementType.configuration.toString()));
    }

    public void removeConfigurationListener(NodeConfigListener listener) {
        StanzaListener conListener = (StanzaListener) this.configEventToListenerMap.remove(listener);
        if (conListener != null) {
            this.pubSubManager.getConnection().removeSyncStanzaListener(conListener);
        }
    }

    public void addItemDeleteListener(ItemDeleteListener listener) {
        StanzaListener delListener = new ItemDeleteTranslator(listener);
        this.itemDeleteToListenerMap.put(listener, delListener);
        EventContentFilter deleteItem = new EventContentFilter(EventElementType.items.toString(), "retract");
        EventContentFilter purge = new EventContentFilter(this, EventElementType.purge.toString());
        this.pubSubManager.getConnection().addSyncStanzaListener(delListener, new OrFilter(deleteItem, purge));
    }

    public void removeItemDeleteListener(ItemDeleteListener listener) {
        StanzaListener conListener = (StanzaListener) this.itemDeleteToListenerMap.remove(listener);
        if (conListener != null) {
            this.pubSubManager.getConnection().removeSyncStanzaListener(conListener);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" ");
        sb.append(getClass().getName());
        sb.append(" id: ");
        sb.append(this.id);
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public PubSub createPubsubPacket(Type type, NodeExtension ext) {
        return PubSub.createPubsubPacket(this.pubSubManager.getServiceJid(), type, ext);
    }

    /* access modifiers changed from: protected */
    public PubSub sendPubsubPacket(PubSub packet) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return this.pubSubManager.sendPubsubPacket(packet);
    }

    /* access modifiers changed from: private */
    public static List<String> getSubscriptionIds(Stanza packet) {
        HeadersExtension headers = (HeadersExtension) packet.getExtension(HeadersExtension.ELEMENT, HeadersExtension.NAMESPACE);
        List<String> values = null;
        if (headers != null) {
            values = new ArrayList<>(headers.getHeaders().size());
            for (Header header : headers.getHeaders()) {
                values.add(header.getValue());
            }
        }
        return values;
    }
}
