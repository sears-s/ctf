package org.jivesoftware.smackx.forward.packet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.delay.packet.DelayInformation;

public class Forwarded implements ExtensionElement {
    public static final String ELEMENT = "forwarded";
    public static final String NAMESPACE = "urn:xmpp:forward:0";
    private final DelayInformation delay;
    private final Stanza forwardedPacket;

    public Forwarded(DelayInformation delay2, Stanza fwdPacket) {
        this.delay = delay2;
        this.forwardedPacket = fwdPacket;
    }

    public Forwarded(Stanza fwdPacket) {
        this(null, fwdPacket);
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.rightAngleBracket();
        xml.optElement(getDelayInformation());
        xml.append(this.forwardedPacket.toXML(NAMESPACE));
        xml.closeElement((NamedElement) this);
        return xml;
    }

    @Deprecated
    public Stanza getForwardedPacket() {
        return this.forwardedPacket;
    }

    public Stanza getForwardedStanza() {
        return this.forwardedPacket;
    }

    public DelayInformation getDelayInformation() {
        return this.delay;
    }

    public static Forwarded from(Stanza packet) {
        return (Forwarded) packet.getExtension(ELEMENT, NAMESPACE);
    }

    public static List<Message> extractMessagesFrom(Collection<Forwarded> forwardedCollection) {
        List<Message> res = new ArrayList<>(forwardedCollection.size());
        for (Forwarded forwarded : forwardedCollection) {
            res.add((Message) forwarded.forwardedPacket);
        }
        return res;
    }
}
