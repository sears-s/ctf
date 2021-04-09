package org.jivesoftware.smackx.bob.element;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.bob.BoBHash;
import org.jivesoftware.smackx.xhtmlim.XHTMLText;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;

public class BoBExtension extends XHTMLExtension {
    private final String alt;
    private final BoBHash bobHash;
    private final String paragraph;

    public BoBExtension(BoBHash bobHash2, String alt2, String paragraph2) {
        this.bobHash = bobHash2;
        this.alt = alt2;
        this.paragraph = paragraph2;
    }

    public BoBHash getBoBHash() {
        return this.bobHash;
    }

    public String getAlt() {
        return this.alt;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.rightAngleBracket();
        String str = "body";
        xml.halfOpenElement(str);
        xml.xmlnsAttribute(XHTMLText.NAMESPACE);
        xml.rightAngleBracket();
        String str2 = XHTMLText.P;
        xml.openElement(str2);
        xml.optEscape(this.paragraph);
        xml.halfOpenElement(XHTMLText.IMG);
        xml.optAttribute("alt", this.alt);
        xml.attribute("src", this.bobHash.toSrc());
        xml.closeEmptyElement();
        xml.closeElement(str2);
        xml.closeElement(str);
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public static BoBExtension from(Message message) {
        return (BoBExtension) message.getExtension(XHTMLExtension.ELEMENT, XHTMLExtension.NAMESPACE);
    }
}
