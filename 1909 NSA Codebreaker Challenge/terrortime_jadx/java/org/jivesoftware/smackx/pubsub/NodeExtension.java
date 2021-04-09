package org.jivesoftware.smackx.pubsub;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;

public class NodeExtension implements ExtensionElement {
    private final PubSubElementType element;
    private final String node;

    public NodeExtension(PubSubElementType elem, String nodeId) {
        this.element = elem;
        this.node = nodeId;
    }

    public NodeExtension(PubSubElementType elem) {
        this(elem, null);
    }

    public String getNode() {
        return this.node;
    }

    public String getElementName() {
        return this.element.getElementName();
    }

    public PubSubNamespace getPubSubNamespace() {
        return this.element.getNamespace();
    }

    public final String getNamespace() {
        return getPubSubNamespace().getXmlns();
    }

    public CharSequence toXML(String enclosingNamespace) {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append('<');
        sb.append(getElementName());
        if (this.node == null) {
            str = BuildConfig.FLAVOR;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(" node='");
            sb2.append(this.node);
            sb2.append('\'');
            str = sb2.toString();
        }
        sb.append(str);
        sb.append("/>");
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(" - content [");
        sb.append(toXML(null));
        sb.append("]");
        return sb.toString();
    }
}
