package org.jivesoftware.smack.compress.packet;

import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.Nonza;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class Compress implements Nonza {
    public static final String ELEMENT = "compress";
    public static final String NAMESPACE = "http://jabber.org/protocol/compress";
    public final String method;

    public static class Feature implements ExtensionElement {
        public static final String ELEMENT = "compression";
        public final List<String> methods;

        public Feature(List<String> methods2) {
            this.methods = methods2;
        }

        public List<String> getMethods() {
            return Collections.unmodifiableList(this.methods);
        }

        public String getNamespace() {
            return "http://jabber.org/protocol/compress";
        }

        public String getElementName() {
            return ELEMENT;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            xml.rightAngleBracket();
            for (String method : this.methods) {
                xml.element("method", method);
            }
            xml.closeElement((NamedElement) this);
            return xml;
        }
    }

    public Compress(String method2) {
        this.method = method2;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return "http://jabber.org/protocol/compress";
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.rightAngleBracket();
        xml.element("method", this.method);
        xml.closeElement((NamedElement) this);
        return xml;
    }
}
