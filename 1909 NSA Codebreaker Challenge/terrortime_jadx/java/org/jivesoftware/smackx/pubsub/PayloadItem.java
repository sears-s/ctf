package org.jivesoftware.smackx.pubsub;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.pubsub.Item.ItemNamespace;

public class PayloadItem<E extends ExtensionElement> extends Item {
    private final E payload;

    public PayloadItem(E payloadExt) {
        if (payloadExt != null) {
            this.payload = payloadExt;
            return;
        }
        throw new IllegalArgumentException("payload cannot be 'null'");
    }

    public PayloadItem(String itemId, E payloadExt) {
        super(itemId);
        if (payloadExt != null) {
            this.payload = payloadExt;
            return;
        }
        throw new IllegalArgumentException("payload cannot be 'null'");
    }

    public PayloadItem(String itemId, String nodeId, E payloadExt) {
        this(ItemNamespace.pubsub, itemId, nodeId, payloadExt);
    }

    public PayloadItem(ItemNamespace itemNamespace, String itemId, String nodeId, E payloadExt) {
        super(itemNamespace, itemId, nodeId);
        if (payloadExt != null) {
            this.payload = payloadExt;
            return;
        }
        throw new IllegalArgumentException("payload cannot be 'null'");
    }

    public E getPayload() {
        return this.payload;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = getCommonXml();
        xml.rightAngleBracket();
        xml.append(this.payload.toXML(null));
        xml.closeElement((NamedElement) this);
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
