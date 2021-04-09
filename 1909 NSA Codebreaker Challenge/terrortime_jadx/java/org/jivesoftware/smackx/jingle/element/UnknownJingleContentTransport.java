package org.jivesoftware.smackx.jingle.element;

import java.util.List;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public final class UnknownJingleContentTransport extends JingleContentTransport {
    private final StandardExtensionElement standardExtensionElement;

    public UnknownJingleContentTransport(StandardExtensionElement standardExtensionElement2) {
        super(null, null);
        this.standardExtensionElement = standardExtensionElement2;
    }

    public String getElementName() {
        return this.standardExtensionElement.getElementName();
    }

    public String getNamespace() {
        return this.standardExtensionElement.getNamespace();
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        return this.standardExtensionElement.toXML((String) null);
    }

    public List<JingleContentTransportCandidate> getCandidates() {
        throw new UnsupportedOperationException();
    }

    public JingleContentTransportInfo getInfo() {
        throw new UnsupportedOperationException();
    }

    public StandardExtensionElement getStandardExtensionElement() {
        return this.standardExtensionElement;
    }
}
