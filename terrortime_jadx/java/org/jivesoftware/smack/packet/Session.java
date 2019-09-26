package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class Session extends SimpleIQ {
    public static final String ELEMENT = "session";
    public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-session";

    public static class Feature implements ExtensionElement {
        public static final String OPTIONAL_ELEMENT = "optional";
        private final boolean optional;

        public Feature(boolean optional2) {
            this.optional = optional2;
        }

        public boolean isOptional() {
            return this.optional;
        }

        public String getElementName() {
            return Session.ELEMENT;
        }

        public String getNamespace() {
            return Session.NAMESPACE;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            if (this.optional) {
                xml.rightAngleBracket();
                xml.emptyElement(OPTIONAL_ELEMENT);
                xml.closeElement((NamedElement) this);
            } else {
                xml.closeEmptyElement();
            }
            return xml;
        }
    }

    public Session() {
        super(ELEMENT, NAMESPACE);
        setType(Type.set);
    }
}
