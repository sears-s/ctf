package org.jivesoftware.smackx.httpfileupload.element;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class FileTooLargeError implements ExtensionElement {
    public static final String ELEMENT = "file-too-large";
    public static final String NAMESPACE = "urn:xmpp:http:upload:0";
    private final long maxFileSize;
    private final String namespace;

    public FileTooLargeError(long maxFileSize2) {
        this(maxFileSize2, "urn:xmpp:http:upload:0");
    }

    protected FileTooLargeError(long maxFileSize2, String namespace2) {
        this.maxFileSize = maxFileSize2;
        this.namespace = namespace2;
    }

    public long getMaxFileSize() {
        return this.maxFileSize;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.rightAngleBracket();
        xml.element("max-file-size", String.valueOf(this.maxFileSize));
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public static FileTooLargeError from(IQ iq) {
        StanzaError error = iq.getError();
        if (error == null) {
            return null;
        }
        return (FileTooLargeError) error.getExtension(ELEMENT, "urn:xmpp:http:upload:0");
    }
}
