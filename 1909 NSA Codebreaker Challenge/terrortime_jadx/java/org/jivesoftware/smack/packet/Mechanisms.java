package org.jivesoftware.smack.packet;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class Mechanisms implements ExtensionElement {
    public static final String ELEMENT = "mechanisms";
    public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-sasl";
    public final List<String> mechanisms = new LinkedList();

    public Mechanisms(String mechanism) {
        this.mechanisms.add(mechanism);
    }

    public Mechanisms(Collection<String> mechanisms2) {
        this.mechanisms.addAll(mechanisms2);
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return "urn:ietf:params:xml:ns:xmpp-sasl";
    }

    public List<String> getMechanisms() {
        return Collections.unmodifiableList(this.mechanisms);
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.rightAngleBracket();
        for (String mechanism : this.mechanisms) {
            xml.element("mechanism", mechanism);
        }
        xml.closeElement((NamedElement) this);
        return xml;
    }
}
