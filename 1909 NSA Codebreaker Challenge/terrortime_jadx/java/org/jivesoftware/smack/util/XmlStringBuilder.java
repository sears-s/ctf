package org.jivesoftware.smack.util;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jxmpp.util.XmppDateTime;

public class XmlStringBuilder implements Appendable, CharSequence, Element {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final String RIGHT_ANGLE_BRACKET = Character.toString('>');
    private final String enclosingNamespace;
    private final LazyStringBuilder sb;

    private static final class XmlNsAttribute implements CharSequence {
        /* access modifiers changed from: private */
        public final String value;
        private final String xmlFragment;

        private XmlNsAttribute(String value2) {
            this.value = (String) StringUtils.requireNotNullOrEmpty(value2, "Value must not be null");
            StringBuilder sb = new StringBuilder();
            sb.append(" xmlns='");
            sb.append(value2);
            sb.append('\'');
            this.xmlFragment = sb.toString();
        }

        public String toString() {
            return this.xmlFragment;
        }

        public int length() {
            return this.xmlFragment.length();
        }

        public char charAt(int index) {
            return this.xmlFragment.charAt(index);
        }

        public CharSequence subSequence(int start, int end) {
            return this.xmlFragment.subSequence(start, end);
        }
    }

    public XmlStringBuilder() {
        this(BuildConfig.FLAVOR);
    }

    public XmlStringBuilder(String enclosingNamespace2) {
        this.sb = new LazyStringBuilder();
        this.enclosingNamespace = enclosingNamespace2 != null ? enclosingNamespace2 : BuildConfig.FLAVOR;
    }

    public XmlStringBuilder(ExtensionElement pe) {
        this();
        prelude(pe);
    }

    public XmlStringBuilder(NamedElement e) {
        this();
        halfOpenElement(e.getElementName());
    }

    public XmlStringBuilder(ExtensionElement ee, String enclosingNamespace2) {
        this(enclosingNamespace2);
        String namespace = ee.getNamespace();
        prelude(ee);
    }

    public XmlStringBuilder escapedElement(String name, String escapedContent) {
        openElement(name);
        append((CharSequence) escapedContent);
        closeElement(name);
        return this;
    }

    public XmlStringBuilder element(String name, String content) {
        if (content.isEmpty()) {
            return emptyElement(name);
        }
        openElement(name);
        escape(content);
        closeElement(name);
        return this;
    }

    public XmlStringBuilder element(String name, Date content) {
        return element(name, XmppDateTime.formatXEP0082Date(content));
    }

    public XmlStringBuilder element(String name, CharSequence content) {
        return element(name, content.toString());
    }

    public XmlStringBuilder element(String name, Enum<?> content) {
        element(name, content.name());
        return this;
    }

    public XmlStringBuilder element(Element element) {
        return append(element.toXML(null));
    }

    public XmlStringBuilder optElement(String name, String content) {
        if (content != null) {
            element(name, content);
        }
        return this;
    }

    public XmlStringBuilder optElement(String name, Date content) {
        if (content != null) {
            element(name, content);
        }
        return this;
    }

    public XmlStringBuilder optElement(String name, CharSequence content) {
        if (content != null) {
            element(name, content.toString());
        }
        return this;
    }

    public XmlStringBuilder optElement(Element element) {
        if (element != null) {
            append(element.toXML(null));
        }
        return this;
    }

    public XmlStringBuilder optElement(String name, Enum<?> content) {
        if (content != null) {
            element(name, content);
        }
        return this;
    }

    public XmlStringBuilder optElement(String name, Object object) {
        if (object != null) {
            element(name, object.toString());
        }
        return this;
    }

    public XmlStringBuilder optIntElement(String name, int value) {
        if (value >= 0) {
            element(name, String.valueOf(value));
        }
        return this;
    }

    public XmlStringBuilder halfOpenElement(String name) {
        this.sb.append('<').append((CharSequence) name);
        return this;
    }

    public XmlStringBuilder halfOpenElement(NamedElement namedElement) {
        return halfOpenElement(namedElement.getElementName());
    }

    public XmlStringBuilder openElement(String name) {
        halfOpenElement(name).rightAngleBracket();
        return this;
    }

    public XmlStringBuilder closeElement(String name) {
        this.sb.append((CharSequence) "</").append((CharSequence) name);
        rightAngleBracket();
        return this;
    }

    public XmlStringBuilder closeElement(NamedElement e) {
        closeElement(e.getElementName());
        return this;
    }

    public XmlStringBuilder closeEmptyElement() {
        this.sb.append((CharSequence) "/>");
        return this;
    }

    public XmlStringBuilder rightAngleBracket() {
        this.sb.append((CharSequence) RIGHT_ANGLE_BRACKET);
        return this;
    }

    @Deprecated
    public XmlStringBuilder rightAngelBracket() {
        return rightAngleBracket();
    }

    public XmlStringBuilder attribute(String name, String value) {
        this.sb.append(' ').append((CharSequence) name).append((CharSequence) "='");
        escapeAttributeValue(value);
        this.sb.append('\'');
        return this;
    }

    public XmlStringBuilder attribute(String name, boolean bool) {
        return attribute(name, Boolean.toString(bool));
    }

    public XmlStringBuilder attribute(String name, Date value) {
        return attribute(name, XmppDateTime.formatXEP0082Date(value));
    }

    public XmlStringBuilder attribute(String name, CharSequence value) {
        return attribute(name, value.toString());
    }

    public XmlStringBuilder attribute(String name, Enum<?> value) {
        attribute(name, value.name());
        return this;
    }

    public XmlStringBuilder attribute(String name, int value) {
        return attribute(name, String.valueOf(value));
    }

    public XmlStringBuilder optAttribute(String name, String value) {
        if (value != null) {
            attribute(name, value);
        }
        return this;
    }

    public XmlStringBuilder optAttribute(String name, Date value) {
        if (value != null) {
            attribute(name, value);
        }
        return this;
    }

    public XmlStringBuilder optAttribute(String name, CharSequence value) {
        if (value != null) {
            attribute(name, value.toString());
        }
        return this;
    }

    public XmlStringBuilder optAttribute(String name, Enum<?> value) {
        if (value != null) {
            attribute(name, value.toString());
        }
        return this;
    }

    public XmlStringBuilder optIntAttribute(String name, int value) {
        if (value >= 0) {
            attribute(name, Integer.toString(value));
        }
        return this;
    }

    public XmlStringBuilder optLongAttribute(String name, Long value) {
        if (value != null && value.longValue() >= 0) {
            attribute(name, Long.toString(value.longValue()));
        }
        return this;
    }

    public XmlStringBuilder optBooleanAttribute(String name, boolean bool) {
        if (bool) {
            this.sb.append(' ').append((CharSequence) name).append((CharSequence) "='true'");
        }
        return this;
    }

    public XmlStringBuilder optBooleanAttributeDefaultTrue(String name, boolean bool) {
        if (!bool) {
            this.sb.append(' ').append((CharSequence) name).append((CharSequence) "='false'");
        }
        return this;
    }

    public XmlStringBuilder xmlnsAttribute(String value) {
        if (value != null && !this.enclosingNamespace.equals(value)) {
            append((CharSequence) new XmlNsAttribute(value));
        }
        return this;
    }

    public XmlStringBuilder xmllangAttribute(String value) {
        optAttribute("xml:lang", value);
        return this;
    }

    public XmlStringBuilder optXmlLangAttribute(String lang) {
        if (!StringUtils.isNullOrEmpty((CharSequence) lang)) {
            xmllangAttribute(lang);
        }
        return this;
    }

    public XmlStringBuilder escape(String text) {
        this.sb.append(StringUtils.escapeForXml(text));
        return this;
    }

    public XmlStringBuilder escapeAttributeValue(String value) {
        this.sb.append(StringUtils.escapeForXmlAttributeApos(value));
        return this;
    }

    public XmlStringBuilder optEscape(CharSequence text) {
        if (text == null) {
            return this;
        }
        return escape(text);
    }

    public XmlStringBuilder escape(CharSequence text) {
        return escape(text.toString());
    }

    public XmlStringBuilder prelude(ExtensionElement pe) {
        return prelude(pe.getElementName(), pe.getNamespace());
    }

    public XmlStringBuilder prelude(String elementName, String namespace) {
        halfOpenElement(elementName);
        xmlnsAttribute(namespace);
        return this;
    }

    public XmlStringBuilder optAppend(CharSequence csq) {
        if (csq != null) {
            append(csq);
        }
        return this;
    }

    public XmlStringBuilder optAppend(Element element) {
        if (element != null) {
            append(element.toXML(this.enclosingNamespace));
        }
        return this;
    }

    public XmlStringBuilder append(XmlStringBuilder xsb) {
        this.sb.append(xsb.sb);
        return this;
    }

    public XmlStringBuilder append(Collection<? extends Element> elements) {
        return append(elements, null);
    }

    public XmlStringBuilder append(Collection<? extends Element> elements, String enclosingNamespace2) {
        for (Element element : elements) {
            append(element.toXML(enclosingNamespace2));
        }
        return this;
    }

    public XmlStringBuilder emptyElement(Enum<?> element) {
        return emptyElement(element.name());
    }

    public XmlStringBuilder emptyElement(String element) {
        halfOpenElement(element);
        return closeEmptyElement();
    }

    public XmlStringBuilder condEmptyElement(boolean condition, String element) {
        if (condition) {
            emptyElement(element);
        }
        return this;
    }

    public XmlStringBuilder condAttribute(boolean condition, String name, String value) {
        if (condition) {
            attribute(name, value);
        }
        return this;
    }

    public XmlStringBuilder append(CharSequence csq) {
        this.sb.append(csq);
        return this;
    }

    public XmlStringBuilder append(CharSequence csq, int start, int end) {
        this.sb.append(csq, start, end);
        return this;
    }

    public XmlStringBuilder append(char c) {
        this.sb.append(c);
        return this;
    }

    public int length() {
        return this.sb.length();
    }

    public char charAt(int index) {
        return this.sb.charAt(index);
    }

    public CharSequence subSequence(int start, int end) {
        return this.sb.subSequence(start, end);
    }

    public String toString() {
        return this.sb.toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof CharSequence)) {
            return false;
        }
        return toString().equals(((CharSequence) other).toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public void write(Writer writer, String enclosingNamespace2) throws IOException {
        for (CharSequence csq : this.sb.getAsList()) {
            if (csq instanceof XmlStringBuilder) {
                ((XmlStringBuilder) csq).write(writer, enclosingNamespace2);
            } else if (csq instanceof XmlNsAttribute) {
                XmlNsAttribute xmlNsAttribute = (XmlNsAttribute) csq;
                if (!xmlNsAttribute.value.equals(enclosingNamespace2)) {
                    writer.write(xmlNsAttribute.toString());
                    enclosingNamespace2 = xmlNsAttribute.value;
                }
            } else {
                writer.write(csq.toString());
            }
        }
    }

    public CharSequence toXML(String enclosingNamespace2) {
        StringBuilder res = new StringBuilder();
        appendXmlTo(res, enclosingNamespace2);
        return res;
    }

    private void appendXmlTo(StringBuilder res, String enclosingNamespace2) {
        for (CharSequence csq : this.sb.getAsList()) {
            if (csq instanceof XmlStringBuilder) {
                ((XmlStringBuilder) csq).appendXmlTo(res, enclosingNamespace2);
            } else if (csq instanceof XmlNsAttribute) {
                XmlNsAttribute xmlNsAttribute = (XmlNsAttribute) csq;
                if (!xmlNsAttribute.value.equals(enclosingNamespace2)) {
                    this.sb.append((CharSequence) xmlNsAttribute);
                    enclosingNamespace2 = xmlNsAttribute.value;
                }
            } else {
                res.append(csq);
            }
        }
    }
}
