package org.jivesoftware.smackx.jingle_filetransfer.element;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.jingle.element.JingleContent.Creator;

public class Checksum implements ExtensionElement {
    public static final String ATTR_CREATOR = "creator";
    public static final String ATTR_NAME = "name";
    public static final String ELEMENT = "checksum";
    private final Creator creator;
    private final JingleFileTransferChild file;
    private final String name;

    public Checksum(Creator creator2, String name2, JingleFileTransferChild file2) {
        this.creator = creator2;
        this.name = name2;
        this.file = (JingleFileTransferChild) Objects.requireNonNull(file2, "file MUST NOT be null.");
        Objects.requireNonNull(file2.getHash(), "file MUST contain at least one hash element.");
    }

    public String getElementName() {
        return ELEMENT;
    }

    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder sb = new XmlStringBuilder((ExtensionElement) this);
        sb.optAttribute("creator", (Enum<?>) this.creator);
        sb.optAttribute("name", this.name);
        sb.rightAngleBracket();
        sb.element(this.file);
        sb.closeElement((NamedElement) this);
        return sb;
    }

    public String getNamespace() {
        return JingleFileTransfer.NAMESPACE_V5;
    }
}
