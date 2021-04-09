package org.jivesoftware.smackx.sid.element;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.sid.StableUniqueStanzaIdManager;

public class StanzaIdElement extends StableAndUniqueIdElement {
    public static final String ATTR_BY = "by";
    public static final String ELEMENT = "stanza-id";
    private final String by;

    public StanzaIdElement(String by2) {
        this.by = by2;
    }

    public StanzaIdElement(String id, String by2) {
        super(id);
        this.by = by2;
    }

    public static boolean hasStanzaId(Message message) {
        return getStanzaId(message) != null;
    }

    public static StanzaIdElement getStanzaId(Message message) {
        return (StanzaIdElement) message.getExtension(ELEMENT, StableUniqueStanzaIdManager.NAMESPACE);
    }

    public String getBy() {
        return this.by;
    }

    public String getNamespace() {
        return StableUniqueStanzaIdManager.NAMESPACE;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        return new XmlStringBuilder((ExtensionElement) this).attribute("id", getId()).attribute(ATTR_BY, getBy()).closeEmptyElement();
    }
}
