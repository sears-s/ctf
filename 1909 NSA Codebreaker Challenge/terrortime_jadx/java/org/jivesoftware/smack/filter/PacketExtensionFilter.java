package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.StringUtils;

@Deprecated
public class PacketExtensionFilter implements StanzaFilter {
    private final String elementName;
    private final String namespace;

    public PacketExtensionFilter(String elementName2, String namespace2) {
        StringUtils.requireNotNullOrEmpty(namespace2, "namespace must not be null or empty");
        this.elementName = elementName2;
        this.namespace = namespace2;
    }

    public PacketExtensionFilter(String namespace2) {
        this(null, namespace2);
    }

    public PacketExtensionFilter(ExtensionElement packetExtension) {
        this(packetExtension.getElementName(), packetExtension.getNamespace());
    }

    public boolean accept(Stanza packet) {
        return packet.hasExtension(this.elementName, this.namespace);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": element=");
        sb.append(this.elementName);
        sb.append(" namespace=");
        sb.append(this.namespace);
        return sb.toString();
    }
}
