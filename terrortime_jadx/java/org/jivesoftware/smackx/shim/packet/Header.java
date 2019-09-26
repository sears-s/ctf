package org.jivesoftware.smackx.shim.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class Header implements ExtensionElement {
    public static final String ELEMENT = "header";
    private final String name;
    private final String value;

    public Header(String name2, String value2) {
        this.name = name2;
        this.value = value2;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return HeadersExtension.NAMESPACE;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
        xml.attribute("name", this.name);
        xml.rightAngleBracket();
        xml.escape(this.value);
        xml.closeElement((NamedElement) this);
        return xml;
    }
}
