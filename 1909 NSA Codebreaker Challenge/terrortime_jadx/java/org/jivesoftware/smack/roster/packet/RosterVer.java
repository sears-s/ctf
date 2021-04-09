package org.jivesoftware.smack.roster.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public final class RosterVer implements ExtensionElement {
    public static final String ELEMENT = "ver";
    public static final RosterVer INSTANCE = new RosterVer();
    public static final String NAMESPACE = "urn:xmpp:features:rosterver";

    private RosterVer() {
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.closeEmptyElement();
        return xml;
    }
}
