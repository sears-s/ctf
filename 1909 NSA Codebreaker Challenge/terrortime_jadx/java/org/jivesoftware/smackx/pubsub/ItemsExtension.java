package org.jivesoftware.smackx.pubsub;

import java.util.List;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;

public class ItemsExtension extends NodeExtension implements EmbeddedPacketExtension {
    protected List<? extends NamedElement> items;
    protected Boolean notify;
    protected ItemsElementType type;

    public enum ItemsElementType {
        items(PubSubElementType.ITEMS, "max_items"),
        retract(PubSubElementType.RETRACT, "notify");
        
        private final String att;
        private final PubSubElementType elem;

        private ItemsElementType(PubSubElementType nodeElement, String attribute) {
            this.elem = nodeElement;
            this.att = attribute;
        }

        public PubSubElementType getNodeElement() {
            return this.elem;
        }

        public String getElementAttribute() {
            return this.att;
        }
    }

    public ItemsExtension(ItemsElementType itemsType, String nodeId, List<? extends NamedElement> items2) {
        super(itemsType.getNodeElement(), nodeId);
        this.type = itemsType;
        this.items = items2;
    }

    public ItemsExtension(String nodeId, List<? extends ExtensionElement> items2, boolean notify2) {
        super(ItemsElementType.retract.getNodeElement(), nodeId);
        this.type = ItemsElementType.retract;
        this.items = items2;
        this.notify = Boolean.valueOf(notify2);
    }

    public ItemsElementType getItemsElementType() {
        return this.type;
    }

    public List<ExtensionElement> getExtensions() {
        return getItems();
    }

    public List<? extends NamedElement> getItems() {
        return this.items;
    }

    public boolean getNotify() {
        return this.notify.booleanValue();
    }

    public CharSequence toXML(String enclosingNamespace) {
        List<? extends NamedElement> list = this.items;
        if (list == null || list.size() == 0) {
            return super.toXML(enclosingNamespace);
        }
        StringBuilder builder = new StringBuilder("<");
        builder.append(getElementName());
        builder.append(" node='");
        builder.append(getNode());
        String str = "'>";
        if (this.notify != null) {
            builder.append("' ");
            builder.append(this.type.getElementAttribute());
            builder.append("='");
            builder.append(this.notify.equals(Boolean.TRUE) ? 1 : 0);
            builder.append(str);
        } else {
            builder.append(str);
            for (NamedElement item : this.items) {
                builder.append(item.toXML(null));
            }
        }
        builder.append("</");
        builder.append(getElementName());
        builder.append('>');
        return builder.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append("Content [");
        sb.append(toXML(null));
        sb.append("]");
        return sb.toString();
    }
}
