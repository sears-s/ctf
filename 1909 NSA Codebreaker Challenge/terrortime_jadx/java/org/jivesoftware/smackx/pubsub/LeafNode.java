package org.jivesoftware.smackx.pubsub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.ItemsExtension.ItemsElementType;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jxmpp.jid.Jid;

public class LeafNode extends Node {
    LeafNode(PubSubManager pubSubManager, String nodeId) {
        super(pubSubManager, nodeId);
    }

    public DiscoverItems discoverItems() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        DiscoverItems items = new DiscoverItems();
        items.setTo((Jid) this.pubSubManager.getServiceJid());
        items.setNode(getId());
        return (DiscoverItems) this.pubSubManager.getConnection().createStanzaCollectorAndSend(items).nextResultOrThrow();
    }

    public <T extends Item> List<T> getItems() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getItems(null, null);
    }

    public <T extends Item> List<T> getItems(String subscriptionId) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getItems(createPubsubPacket(Type.get, new GetItemsRequest(getId(), subscriptionId)));
    }

    public <T extends Item> List<T> getItems(Collection<String> ids) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        List<Item> itemList = new ArrayList<>(ids.size());
        for (String id : ids) {
            itemList.add(new Item(id));
        }
        return getItems(createPubsubPacket(Type.get, new ItemsExtension(ItemsElementType.items, getId(), itemList)));
    }

    public <T extends Item> List<T> getItems(int maxItems) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getItems(createPubsubPacket(Type.get, new GetItemsRequest(getId(), maxItems)));
    }

    public <T extends Item> List<T> getItems(int maxItems, String subscriptionId) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getItems(createPubsubPacket(Type.get, new GetItemsRequest(getId(), subscriptionId, maxItems)));
    }

    public <T extends Item> List<T> getItems(List<ExtensionElement> additionalExtensions, List<ExtensionElement> returnedExtensions) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        PubSub request = createPubsubPacket(Type.get, new GetItemsRequest(getId()));
        request.addExtensions(additionalExtensions);
        return getItems(request, returnedExtensions);
    }

    private <T extends Item> List<T> getItems(PubSub request) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getItems(request, null);
    }

    private <T extends Item> List<T> getItems(PubSub request, List<ExtensionElement> returnedExtensions) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        PubSub result = (PubSub) this.pubSubManager.getConnection().createStanzaCollectorAndSend(request).nextResultOrThrow();
        ItemsExtension itemsElem = (ItemsExtension) result.getExtension(PubSubElementType.ITEMS);
        if (returnedExtensions != null) {
            returnedExtensions.addAll(result.getExtensions());
        }
        return itemsElem.getItems();
    }

    @Deprecated
    public void send() throws NotConnectedException, InterruptedException, NoResponseException, XMPPErrorException {
        publish();
    }

    @Deprecated
    public <T extends Item> void send(T item) throws NotConnectedException, InterruptedException, NoResponseException, XMPPErrorException {
        publish(item);
    }

    @Deprecated
    public <T extends Item> void send(Collection<T> items) throws NotConnectedException, InterruptedException, NoResponseException, XMPPErrorException {
        publish(items);
    }

    public void publish() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        this.pubSubManager.getConnection().createStanzaCollectorAndSend(createPubsubPacket(Type.set, new NodeExtension(PubSubElementType.PUBLISH, getId()))).nextResultOrThrow();
    }

    public <T extends Item> void publish(T item) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Collection<T> items = new ArrayList<>(1);
        items.add(item == null ? new Item() : item);
        publish(items);
    }

    public <T extends Item> void publish(Collection<T> items) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        this.pubSubManager.getConnection().createStanzaCollectorAndSend(createPubsubPacket(Type.set, new PublishItem(getId(), items))).nextResultOrThrow();
    }

    public void deleteAllItems() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        this.pubSubManager.getConnection().createStanzaCollectorAndSend(createPubsubPacket(Type.set, new NodeExtension(PubSubElementType.PURGE_OWNER, getId()))).nextResultOrThrow();
    }

    public void deleteItem(String itemId) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Collection<String> items = new ArrayList<>(1);
        items.add(itemId);
        deleteItem(items);
    }

    public void deleteItem(Collection<String> itemIds) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        List<Item> items = new ArrayList<>(itemIds.size());
        for (String id : itemIds) {
            items.add(new Item(id));
        }
        this.pubSubManager.getConnection().createStanzaCollectorAndSend(createPubsubPacket(Type.set, new ItemsExtension(ItemsElementType.retract, getId(), items))).nextResultOrThrow();
    }
}
