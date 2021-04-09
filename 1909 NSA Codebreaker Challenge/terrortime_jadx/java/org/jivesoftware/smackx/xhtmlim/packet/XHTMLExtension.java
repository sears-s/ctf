package org.jivesoftware.smackx.xhtmlim.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class XHTMLExtension implements ExtensionElement {
    public static final String ELEMENT = "html";
    public static final String NAMESPACE = "http://jabber.org/protocol/xhtml-im";
    private final List<CharSequence> bodies = new ArrayList();

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.rightAngleBracket();
        for (CharSequence body : getBodies()) {
            xml.append(body);
        }
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public List<CharSequence> getBodies() {
        List<CharSequence> unmodifiableList;
        synchronized (this.bodies) {
            unmodifiableList = Collections.unmodifiableList(new ArrayList(this.bodies));
        }
        return unmodifiableList;
    }

    public void addBody(CharSequence body) {
        synchronized (this.bodies) {
            this.bodies.add(body);
        }
    }

    public int getBodiesCount() {
        int size;
        synchronized (this.bodies) {
            size = this.bodies.size();
        }
        return size;
    }

    public static XHTMLExtension from(Message message) {
        return (XHTMLExtension) message.getExtension(ELEMENT, NAMESPACE);
    }
}
