package org.jivesoftware.smackx.sid.element;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.sid.StableUniqueStanzaIdManager;

public class OriginIdElement extends StableAndUniqueIdElement {
    public static final String ELEMENT = "origin-id";

    public OriginIdElement() {
    }

    public OriginIdElement(String id) {
        super(id);
    }

    public static OriginIdElement addOriginId(Message message) {
        OriginIdElement originId = new OriginIdElement();
        message.addExtension(originId);
        return originId;
    }

    public static boolean hasOriginId(Message message) {
        return getOriginId(message) != null;
    }

    public static OriginIdElement getOriginId(Message message) {
        return (OriginIdElement) message.getExtension(ELEMENT, StableUniqueStanzaIdManager.NAMESPACE);
    }

    public String getNamespace() {
        return StableUniqueStanzaIdManager.NAMESPACE;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public CharSequence toXML(String enclosingNamespace) {
        return new XmlStringBuilder((ExtensionElement) this).attribute("id", getId()).closeEmptyElement();
    }
}
