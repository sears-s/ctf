package org.jivesoftware.smackx.message_markup.element;

import java.util.Collections;
import java.util.Set;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.message_markup.element.MarkupElement.MarkupChildElement;

public class SpanElement implements MarkupChildElement {
    public static final String ELEMENT = "span";
    public static final String code = "code";
    public static final String deleted = "deleted";
    public static final String emphasis = "emphasis";
    private final int end;
    private final int start;
    private final Set<SpanStyle> styles;

    public enum SpanStyle {
        emphasis,
        code,
        deleted
    }

    public SpanElement(int start2, int end2, Set<SpanStyle> styles2) {
        this.start = start2;
        this.end = end2;
        this.styles = Collections.unmodifiableSet(styles2);
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public Set<SpanStyle> getStyles() {
        return this.styles;
    }

    public String getElementName() {
        return "span";
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder();
        xml.halfOpenElement((NamedElement) this);
        xml.attribute(MarkupChildElement.ATTR_START, getStart());
        xml.attribute("end", getEnd());
        xml.rightAngleBracket();
        for (SpanStyle style : getStyles()) {
            xml.halfOpenElement(style.toString()).closeEmptyElement();
        }
        xml.closeElement((NamedElement) this);
        return xml;
    }
}
