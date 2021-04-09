package org.jivesoftware.smackx.message_correct.element;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class MessageCorrectExtension implements ExtensionElement {
    public static final String ELEMENT = "replace";
    public static final String ID_TAG = "id";
    public static final String NAMESPACE = "urn:xmpp:message-correct:0";
    private final String idInitialMessage;

    public MessageCorrectExtension(String idInitialMessage2) {
        this.idInitialMessage = (String) StringUtils.requireNotNullOrEmpty(idInitialMessage2, "idInitialMessage must not be null");
    }

    public String getIdInitialMessage() {
        return this.idInitialMessage;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.attribute("id", getIdInitialMessage());
        xml.closeEmptyElement();
        return xml;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public static MessageCorrectExtension from(Message message) {
        return (MessageCorrectExtension) message.getExtension(ELEMENT, NAMESPACE);
    }
}
