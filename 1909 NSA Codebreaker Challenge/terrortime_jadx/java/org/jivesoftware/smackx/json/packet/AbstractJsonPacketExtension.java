package org.jivesoftware.smackx.json.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public abstract class AbstractJsonPacketExtension implements ExtensionElement {
    private final String json;

    protected AbstractJsonPacketExtension(String json2) {
        this.json = json2;
    }

    public final String getJson() {
        return this.json;
    }

    public final XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.rightAngleBracket();
        xml.append((CharSequence) this.json);
        xml.closeElement((NamedElement) this);
        return xml;
    }
}
