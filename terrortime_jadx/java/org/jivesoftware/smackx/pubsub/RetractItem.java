package org.jivesoftware.smackx.pubsub;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;

public class RetractItem implements ExtensionElement {
    private final String id;

    public RetractItem(String itemId) {
        if (itemId != null) {
            this.id = itemId;
            return;
        }
        throw new IllegalArgumentException("itemId must not be 'null'");
    }

    public String getId() {
        return this.id;
    }

    public String getElementName() {
        return "retract";
    }

    public String getNamespace() {
        return PubSubNamespace.event.getXmlns();
    }

    public String toXML(String enclosingNamespace) {
        StringBuilder sb = new StringBuilder();
        sb.append("<retract id='");
        sb.append(this.id);
        sb.append("'/>");
        return sb.toString();
    }
}
