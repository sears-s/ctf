package org.jivesoftware.smackx.caps.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.packet.RosterVer;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.hashes.element.HashElement;
import org.jivesoftware.smackx.iot.data.element.NodeElement;

public class CapsExtension implements ExtensionElement {
    public static final String ELEMENT = "c";
    public static final String NAMESPACE = "http://jabber.org/protocol/caps";
    private final String hash;
    private final String node;
    private final String ver;

    public CapsExtension(String node2, String version, String hash2) {
        this.node = node2;
        this.ver = version;
        this.hash = hash2;
    }

    public String getElementName() {
        return "c";
    }

    public String getNamespace() {
        return "http://jabber.org/protocol/caps";
    }

    public String getNode() {
        return this.node;
    }

    public String getVer() {
        return this.ver;
    }

    public String getHash() {
        return this.hash;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.attribute(HashElement.ELEMENT, this.hash).attribute(NodeElement.ELEMENT, this.node).attribute(RosterVer.ELEMENT, this.ver);
        xml.closeEmptyElement();
        return xml;
    }

    public static CapsExtension from(Stanza stanza) {
        return (CapsExtension) stanza.getExtension("c", "http://jabber.org/protocol/caps");
    }
}
