package org.jivesoftware.smackx.iot.provisioning.element;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jxmpp.jid.BareJid;

public class Friend implements ExtensionElement {
    public static final String ELEMENT = "friend";
    public static final String NAMESPACE = "urn:xmpp:iot:provisioning";
    private final BareJid friend;

    public Friend(BareJid friend2) {
        this.friend = (BareJid) Objects.requireNonNull(friend2, "Friend must not be null");
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return "urn:xmpp:iot:provisioning";
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.attribute("jid", (CharSequence) this.friend);
        xml.closeEmptyElement();
        return xml;
    }

    public BareJid getFriend() {
        return this.friend;
    }

    public static Friend from(Message message) {
        return (Friend) message.getExtension(ELEMENT, "urn:xmpp:iot:provisioning");
    }
}
