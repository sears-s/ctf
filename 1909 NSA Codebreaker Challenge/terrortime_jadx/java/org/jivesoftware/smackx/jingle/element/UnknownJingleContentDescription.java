package org.jivesoftware.smackx.jingle.element;

import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public final class UnknownJingleContentDescription extends JingleContentDescription {
    private final StandardExtensionElement standardExtensionElement;

    public UnknownJingleContentDescription(StandardExtensionElement standardExtensionElement2) {
        super(standardExtensionElement2.getElements());
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

    public StandardExtensionElement getStandardExtensionElement() {
        return this.standardExtensionElement;
    }
}
