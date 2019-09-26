package org.jivesoftware.smackx.iot.provisioning.element;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jxmpp.jid.BareJid;

public class Unfriend implements ExtensionElement {
    public static final String ELEMENT = "UNFRIEND";
    public static final String NAMESPACE = "urn:xmpp:iot:provisioning";
    private final BareJid jid;

    public Unfriend(BareJid jid2) {
        this.jid = jid2;
    }

    public BareJid getJid() {
        return this.jid;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return "urn:xmpp:iot:provisioning";
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.attribute("jid", (CharSequence) this.jid);
        xml.closeEmptyElement();
        return xml;
    }

    public static Unfriend from(Message message) {
        return (Unfriend) message.getExtension(ELEMENT, "urn:xmpp:iot:provisioning");
    }
}
