package org.jivesoftware.smackx.shim.packet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class HeadersExtension implements ExtensionElement {
    public static final String ELEMENT = "headers";
    public static final String NAMESPACE = "http://jabber.org/protocol/shim";
    private final List<Header> headers;

    public HeadersExtension(List<Header> headerList) {
        if (headerList != null) {
            this.headers = Collections.unmodifiableList(headerList);
        } else {
            this.headers = Collections.emptyList();
        }
    }

    public List<Header> getHeaders() {
        return this.headers;
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
        xml.append((Collection<? extends Element>) this.headers);
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public static HeadersExtension from(Stanza packet) {
        return (HeadersExtension) packet.getExtension(ELEMENT, NAMESPACE);
    }
}
