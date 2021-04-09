package org.jivesoftware.smackx.pubsub;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.xmlpull.v1.XmlPullParserException;

public class SimplePayload implements ExtensionElement {
    private final String elemName;
    private final String ns;
    private final String payload;

    public SimplePayload(String xmlPayload) {
        try {
            QName qname = ParserUtils.getQName(PacketParserUtils.getParserFor(xmlPayload));
            this.payload = xmlPayload;
            this.elemName = (String) StringUtils.requireNotNullOrEmpty(qname.getLocalPart(), "Could not determine element name from XML payload");
            this.ns = (String) StringUtils.requireNotNullOrEmpty(qname.getNamespaceURI(), "Could not determine namespace from XML payload");
        } catch (IOException | XmlPullParserException e) {
            throw new AssertionError(e);
        }
    }

    @Deprecated
    public SimplePayload(String elementName, String namespace, CharSequence xmlPayload) {
        this(xmlPayload.toString());
        if (!elementName.equals(this.elemName)) {
            throw new IllegalArgumentException();
        } else if (!namespace.equals(this.ns)) {
            throw new IllegalArgumentException();
        }
    }

    public String getElementName() {
        return this.elemName;
    }

    public String getNamespace() {
        return this.ns;
    }

    public String toXML(String enclosingNamespace) {
        return this.payload;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append("payload [");
        sb.append(toXML((String) null));
        sb.append("]");
        return sb.toString();
    }
}
