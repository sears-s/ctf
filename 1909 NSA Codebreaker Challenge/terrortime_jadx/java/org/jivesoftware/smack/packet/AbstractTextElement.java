package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;

public abstract class AbstractTextElement implements ExtensionElement {
    public static final String ELEMENT = "text";
    private final String lang;
    private final String text;

    protected AbstractTextElement(String text2, String lang2) {
        this.text = (String) StringUtils.requireNotNullOrEmpty(text2, "Text must not be null or empty");
        this.lang = lang2;
    }

    public String getElementName() {
        return "text";
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.optXmlLangAttribute(this.lang);
        xml.rightAngleBracket();
        xml.escape(this.text);
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public final String getText() {
        return this.text;
    }

    public final String getLang() {
        return this.lang;
    }
}
