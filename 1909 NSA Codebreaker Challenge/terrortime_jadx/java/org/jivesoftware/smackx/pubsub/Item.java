package org.jivesoftware.smackx.pubsub;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iot.data.element.NodeElement;

public class Item extends NodeExtension {
    private final String itemId;

    public enum ItemNamespace {
        pubsub(PubSubElementType.ITEM),
        event(PubSubElementType.ITEM_EVENT);
        
        /* access modifiers changed from: private */
        public final PubSubElementType type;

        private ItemNamespace(PubSubElementType type2) {
            this.type = type2;
        }

        public static ItemNamespace fromXmlns(String xmlns) {
            ItemNamespace[] values;
            for (ItemNamespace itemNamespace : values()) {
                if (itemNamespace.type.getNamespace().getXmlns().equals(xmlns)) {
                    return itemNamespace;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid item namespace: ");
            sb.append(xmlns);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public Item() {
        this(ItemNamespace.pubsub, null, null);
    }

    public Item(String itemId2) {
        this(ItemNamespace.pubsub, itemId2, null);
    }

    public Item(ItemNamespace itemNamespace, String itemId2) {
        this(itemNamespace, itemId2, null);
    }

    public Item(String itemId2, String nodeId) {
        this(ItemNamespace.pubsub, itemId2, nodeId);
    }

    public Item(ItemNamespace itemNamespace, String itemId2, String nodeId) {
        super(itemNamespace.type, nodeId);
        this.itemId = itemId2;
    }

    public String getId() {
        return this.itemId;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = getCommonXml();
        xml.closeEmptyElement();
        return xml;
    }

    /* access modifiers changed from: protected */
    public final XmlStringBuilder getCommonXml() {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.optAttribute("id", getId());
        xml.optAttribute(NodeElement.ELEMENT, getNode());
        return xml;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(" | Content [");
        sb.append(toXML((String) null));
        sb.append("]");
        return sb.toString();
    }
}
