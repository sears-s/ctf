package org.jivesoftware.smackx.jingle.element;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public abstract class JingleContentTransport implements ExtensionElement {
    public static final String ELEMENT = "transport";
    protected final List<JingleContentTransportCandidate> candidates;
    protected final JingleContentTransportInfo info;

    protected JingleContentTransport(List<JingleContentTransportCandidate> candidates2) {
        this(candidates2, null);
    }

    protected JingleContentTransport(List<JingleContentTransportCandidate> candidates2, JingleContentTransportInfo info2) {
        if (candidates2 != null) {
            this.candidates = Collections.unmodifiableList(candidates2);
        } else {
            this.candidates = Collections.emptyList();
        }
        this.info = info2;
    }

    public List<JingleContentTransportCandidate> getCandidates() {
        return this.candidates;
    }

    public JingleContentTransportInfo getInfo() {
        return this.info;
    }

    public String getElementName() {
        return "transport";
    }

    /* access modifiers changed from: protected */
    public void addExtraAttributes(XmlStringBuilder xml) {
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        addExtraAttributes(xml);
        if (!this.candidates.isEmpty() || this.info != null) {
            xml.rightAngleBracket();
            xml.append((Collection<? extends Element>) this.candidates);
            xml.optElement(this.info);
            xml.closeElement((NamedElement) this);
        } else {
            xml.closeEmptyElement();
        }
        return xml;
    }
}
