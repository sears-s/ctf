package org.jivesoftware.smackx.chat_markers.element;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class ChatMarkersElements {
    public static final String NAMESPACE = "urn:xmpp:chat-markers:0";

    public static class AcknowledgedExtension implements ExtensionElement {
        public static final String ELEMENT = "acknowledged";
        private final String id;

        public AcknowledgedExtension(String id2) {
            this.id = id2;
        }

        public String getId() {
            return this.id;
        }

        public String getElementName() {
            return ELEMENT;
        }

        public String getNamespace() {
            return ChatMarkersElements.NAMESPACE;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            xml.attribute("id", this.id);
            xml.closeEmptyElement();
            return xml;
        }

        public static AcknowledgedExtension from(Message message) {
            return (AcknowledgedExtension) message.getExtension(ELEMENT, ChatMarkersElements.NAMESPACE);
        }
    }

    public static class DisplayedExtension implements ExtensionElement {
        public static final String ELEMENT = "displayed";
        private final String id;

        public DisplayedExtension(String id2) {
            this.id = id2;
        }

        public String getId() {
            return this.id;
        }

        public String getElementName() {
            return ELEMENT;
        }

        public String getNamespace() {
            return ChatMarkersElements.NAMESPACE;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            xml.attribute("id", this.id);
            xml.closeEmptyElement();
            return xml;
        }

        public static DisplayedExtension from(Message message) {
            return (DisplayedExtension) message.getExtension(ELEMENT, ChatMarkersElements.NAMESPACE);
        }
    }

    public static class MarkableExtension implements ExtensionElement {
        public static final String ELEMENT = "markable";

        public String getElementName() {
            return ELEMENT;
        }

        public String getNamespace() {
            return ChatMarkersElements.NAMESPACE;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            xml.closeEmptyElement();
            return xml;
        }

        public static MarkableExtension from(Message message) {
            return (MarkableExtension) message.getExtension(ELEMENT, ChatMarkersElements.NAMESPACE);
        }
    }

    public static class ReceivedExtension implements ExtensionElement {
        public static final String ELEMENT = "received";
        private final String id;

        public ReceivedExtension(String id2) {
            this.id = id2;
        }

        public String getId() {
            return this.id;
        }

        public String getElementName() {
            return "received";
        }

        public String getNamespace() {
            return ChatMarkersElements.NAMESPACE;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            xml.attribute("id", this.id);
            xml.closeEmptyElement();
            return xml;
        }

        public static ReceivedExtension from(Message message) {
            return (ReceivedExtension) message.getExtension("received", ChatMarkersElements.NAMESPACE);
        }
    }
}
