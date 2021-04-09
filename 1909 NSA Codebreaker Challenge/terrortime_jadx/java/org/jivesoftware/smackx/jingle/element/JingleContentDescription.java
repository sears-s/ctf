package org.jivesoftware.smackx.jingle.element;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public abstract class JingleContentDescription implements ExtensionElement {
    public static final String ELEMENT = "description";
    private final List<NamedElement> payloads;

    protected JingleContentDescription(List<? extends NamedElement> payloads2) {
        if (payloads2 != null) {
            this.payloads = Collections.unmodifiableList(payloads2);
        } else {
            this.payloads = Collections.emptyList();
        }
    }

    public String getElementName() {
        return ELEMENT;
    }

    public List<NamedElement> getJingleContentDescriptionChildren() {
        return this.payloads;
    }

    /* access modifiers changed from: protected */
    public void addExtraAttributes(XmlStringBuilder xml) {
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        addExtraAttributes(xml);
        xml.rightAngleBracket();
        xml.append((Collection<? extends Element>) this.payloads);
        xml.closeElement((NamedElement) this);
        return xml;
    }
}
