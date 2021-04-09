package org.jivesoftware.smackx.pubsub;

import java.util.Arrays;
import java.util.List;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;

public class EventElement implements EmbeddedPacketExtension {
    public static final String ELEMENT = "event";
    public static final String NAMESPACE = PubSubNamespace.event.getXmlns();
    private final NodeExtension ext;
    private final EventElementType type;

    public EventElement(EventElementType eventType, NodeExtension eventExt) {
        this.type = eventType;
        this.ext = eventExt;
    }

    public EventElementType getEventType() {
        return this.type;
    }

    public List<ExtensionElement> getExtensions() {
        return Arrays.asList(new ExtensionElement[]{getEvent()});
    }

    public NodeExtension getEvent() {
        return this.ext;
    }

    public String getElementName() {
        return "event";
    }

    public String getNamespace() {
        return PubSubNamespace.event.getXmlns();
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.rightAngleBracket();
        xml.append(this.ext.toXML(null));
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public static EventElement from(Stanza stanza) {
        return (EventElement) stanza.getExtension("event", NAMESPACE);
    }
}
