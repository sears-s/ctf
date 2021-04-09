package org.jivesoftware.smackx.message_markup.element;

import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.message_markup.element.MarkupElement.BlockLevelMarkupElement;
import org.jivesoftware.smackx.message_markup.element.MarkupElement.MarkupChildElement;

public class BlockQuoteElement implements BlockLevelMarkupElement {
    public static final String ELEMENT = "bquote";
    private final int end;
    private final int start;

    public BlockQuoteElement(int start2, int end2) {
        this.start = start2;
        this.end = end2;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder();
        xml.halfOpenElement((NamedElement) this);
        xml.attribute(MarkupChildElement.ATTR_START, getStart());
        xml.attribute("end", getEnd());
        xml.closeEmptyElement();
        return xml;
    }
}
