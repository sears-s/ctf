package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.util.XmlStringBuilder;

public class StartTls implements Nonza {
    public static final String ELEMENT = "starttls";
    public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-tls";
    private final boolean required;

    public StartTls() {
        this(false);
    }

    public StartTls(boolean required2) {
        this.required = required2;
    }

    public boolean required() {
        return this.required;
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
        xml.condEmptyElement(this.required, "required");
        xml.closeElement((NamedElement) this);
        return xml;
    }
}
