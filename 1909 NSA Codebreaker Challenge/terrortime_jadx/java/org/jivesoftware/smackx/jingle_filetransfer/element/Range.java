package org.jivesoftware.smackx.jingle_filetransfer.element;

import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.hashes.element.HashElement;

public class Range implements NamedElement {
    public static final String ATTR_LENGTH = "length";
    public static final String ATTR_OFFSET = "offset";
    public static final String ELEMENT = "range";
    private final HashElement hash;
    private final int length;
    private final int offset;

    public Range() {
        this(0, -1, null);
    }

    public Range(int length2) {
        this(0, length2, null);
    }

    public Range(int offset2, int length2) {
        this(offset2, length2, null);
    }

    public Range(int offset2, int length2, HashElement hash2) {
        this.offset = offset2;
        this.length = length2;
        this.hash = hash2;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLength() {
        return this.length;
    }

    public HashElement getHash() {
        return this.hash;
    }

    public String getElementName() {
        return "range";
    }

    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder sb = new XmlStringBuilder((NamedElement) this);
        int i = this.offset;
        if (i > 0) {
            sb.attribute(ATTR_OFFSET, i);
        }
        int i2 = this.length;
        if (i2 > 0) {
            sb.attribute(ATTR_LENGTH, i2);
        }
        if (this.hash != null) {
            sb.rightAngleBracket();
            sb.element(this.hash);
            sb.closeElement((NamedElement) this);
        } else {
            sb.closeEmptyElement();
        }
        return sb;
    }

    public boolean equals(Object other) {
        boolean z = false;
        if (other == null || !(other instanceof Range)) {
            return false;
        }
        if (hashCode() == other.hashCode()) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return toXML(null).toString().hashCode();
    }
}
