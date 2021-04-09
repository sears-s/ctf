package org.jivesoftware.smackx.message_markup.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.message_markup.element.ListElement.ListEntryElement;
import org.jivesoftware.smackx.message_markup.element.SpanElement.SpanStyle;

public class MarkupElement implements ExtensionElement {
    public static final String ELEMENT = "markup";
    public static final String NAMESPACE = "urn:xmpp:markup:0";
    private final List<MarkupChildElement> childElements;

    public interface BlockLevelMarkupElement extends MarkupChildElement {
    }

    public static final class Builder {
        private final List<CodeBlockElement> codes;
        /* access modifiers changed from: private */
        public final List<ListElement> lists;
        private final List<BlockQuoteElement> quotes;
        private final List<SpanElement> spans;

        public static final class ListBuilder {
            private int end;
            private final ArrayList<ListEntryElement> entries;
            private final Builder markup;

            private ListBuilder(Builder markup2) {
                this.entries = new ArrayList<>();
                this.end = -1;
                this.markup = markup2;
            }

            public ListBuilder addEntry(int start, int end2) {
                ListEntryElement last;
                Builder.verifyStartEnd(start, end2);
                if (this.entries.size() == 0) {
                    last = null;
                } else {
                    ArrayList<ListEntryElement> arrayList = this.entries;
                    last = (ListEntryElement) arrayList.get(arrayList.size() - 1);
                }
                if (last == null || start == this.end) {
                    this.entries.add(new ListEntryElement(start));
                    this.end = end2;
                    return this;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Next entries start must be equal to last entries end (");
                sb.append(this.end);
                sb.append(").");
                throw new IllegalArgumentException(sb.toString());
            }

            public Builder endList() {
                if (this.entries.size() > 0) {
                    this.markup.lists.add(new ListElement(((ListEntryElement) this.entries.get(0)).getStart(), this.end, this.entries));
                }
                return this.markup;
            }
        }

        private Builder() {
            this.spans = new ArrayList();
            this.quotes = new ArrayList();
            this.codes = new ArrayList();
            this.lists = new ArrayList();
        }

        public Builder setDeleted(int start, int end) {
            return addSpan(start, end, Collections.singleton(SpanStyle.deleted));
        }

        public Builder setEmphasis(int start, int end) {
            return addSpan(start, end, Collections.singleton(SpanStyle.emphasis));
        }

        public Builder setCode(int start, int end) {
            return addSpan(start, end, Collections.singleton(SpanStyle.code));
        }

        public Builder addSpan(int start, int end, Set<SpanStyle> styles) {
            verifyStartEnd(start, end);
            for (SpanElement other : this.spans) {
                if ((start >= other.getStart() && start <= other.getEnd()) || (end >= other.getStart() && end <= other.getEnd())) {
                    throw new IllegalArgumentException("Spans MUST NOT overlap each other.");
                }
            }
            this.spans.add(new SpanElement(start, end, styles));
            return this;
        }

        public Builder setBlockQuote(int start, int end) {
            verifyStartEnd(start, end);
            for (BlockQuoteElement other : this.quotes) {
                Integer s = Integer.valueOf(start);
                Integer e = Integer.valueOf(end);
                if (s.compareTo(Integer.valueOf(other.getStart())) * s.compareTo(Integer.valueOf(other.getEnd())) * e.compareTo(Integer.valueOf(other.getStart())) * e.compareTo(Integer.valueOf(other.getEnd())) < 1) {
                    throw new IllegalArgumentException("BlockQuotes MUST NOT overlap each others boundaries");
                }
            }
            this.quotes.add(new BlockQuoteElement(start, end));
            return this;
        }

        public Builder setCodeBlock(int start, int end) {
            verifyStartEnd(start, end);
            this.codes.add(new CodeBlockElement(start, end));
            return this;
        }

        public ListBuilder beginList() {
            return new ListBuilder();
        }

        public MarkupElement build() {
            List<MarkupChildElement> children = new ArrayList<>();
            children.addAll(this.spans);
            children.addAll(this.quotes);
            children.addAll(this.codes);
            children.addAll(this.lists);
            return new MarkupElement(children);
        }

        /* access modifiers changed from: private */
        public static void verifyStartEnd(int start, int end) {
            if (start >= end || start < 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("Start value (");
                sb.append(start);
                sb.append(") MUST be greater equal than 0 and MUST be smaller than end value (");
                sb.append(end);
                sb.append(").");
                throw new IllegalArgumentException(sb.toString());
            }
        }
    }

    public interface MarkupChildElement extends NamedElement {
        public static final String ATTR_END = "end";
        public static final String ATTR_START = "start";

        int getEnd();

        int getStart();
    }

    public MarkupElement(List<MarkupChildElement> childElements2) {
        this.childElements = Collections.unmodifiableList(childElements2);
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public List<MarkupChildElement> getChildElements() {
        return this.childElements;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this).rightAngleBracket();
        for (MarkupChildElement child : getChildElements()) {
            xml.append(child.toXML(null));
        }
        xml.closeElement((NamedElement) this);
        return xml;
    }
}
