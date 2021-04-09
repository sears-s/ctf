package org.jivesoftware.smackx.message_markup.element;

import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.message_markup.element.MarkupElement.MarkupChildElement;

public class ListElement implements MarkupChildElement {
    public static final String ELEMENT = "list";
    public static final String ELEM_LI = "li";
    private final int end;
    private final List<ListEntryElement> entries;
    private final int start;

    public static class ListEntryElement implements NamedElement {
        private final int start;

        public ListEntryElement(int start2) {
            this.start = start2;
        }

        public int getStart() {
            return this.start;
        }

        public String getElementName() {
            return "li";
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement((NamedElement) this);
            xml.attribute(MarkupChildElement.ATTR_START, getStart());
            xml.closeEmptyElement();
            return xml;
        }
    }

    public ListElement(int start2, int end2, List<ListEntryElement> entries2) {
        this.start = start2;
        this.end = end2;
        this.entries = Collections.unmodifiableList(entries2);
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public List<ListEntryElement> getEntries() {
        return this.entries;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder();
        xml.halfOpenElement((NamedElement) this);
        xml.attribute(MarkupChildElement.ATTR_START, getStart());
        xml.attribute("end", getEnd());
        xml.rightAngleBracket();
        for (ListEntryElement li : getEntries()) {
            xml.append(li.toXML((String) null));
        }
        xml.closeElement((NamedElement) this);
        return xml;
    }
}
