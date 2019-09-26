package org.jivesoftware.smackx.xhtmlim;

import org.jivesoftware.smack.util.XmlStringBuilder;

public class XHTMLText {
    public static final String A = "a";
    public static final String BLOCKQUOTE = "blockquote";
    public static final String BR = "br";
    public static final String CITE = "cite";
    public static final String CODE = "code";
    public static final String EM = "em";
    public static final String H = "h";
    public static final String HREF = "href";
    public static final String IMG = "img";
    public static final String LI = "li";
    public static final String NAMESPACE = "http://www.w3.org/1999/xhtml";
    public static final String OL = "ol";
    public static final String P = "p";
    public static final String Q = "q";
    public static final String SPAN = "span";
    public static final String STRONG = "strong";
    public static final String STYLE = "style";
    public static final String UL = "ul";
    private final XmlStringBuilder text = new XmlStringBuilder();

    public XHTMLText(String style, String lang) {
        appendOpenBodyTag(style, lang);
    }

    public XHTMLText appendOpenAnchorTag(String href, String style) {
        this.text.halfOpenElement("a");
        this.text.optAttribute(HREF, href);
        this.text.optAttribute(STYLE, style);
        this.text.rightAngleBracket();
        return this;
    }

    public XHTMLText appendCloseAnchorTag() {
        this.text.closeElement("a");
        return this;
    }

    public XHTMLText appendOpenBlockQuoteTag(String style) {
        this.text.halfOpenElement(BLOCKQUOTE);
        this.text.optAttribute(STYLE, style);
        this.text.rightAngleBracket();
        return this;
    }

    public XHTMLText appendCloseBlockQuoteTag() {
        this.text.closeElement(BLOCKQUOTE);
        return this;
    }

    private XHTMLText appendOpenBodyTag(String style, String lang) {
        this.text.halfOpenElement("body");
        this.text.xmlnsAttribute(NAMESPACE);
        this.text.optAttribute(STYLE, style);
        this.text.xmllangAttribute(lang);
        this.text.rightAngleBracket();
        return this;
    }

    public XHTMLText appendCloseBodyTag() {
        this.text.closeElement("body");
        return this;
    }

    public XHTMLText appendBrTag() {
        this.text.emptyElement(BR);
        return this;
    }

    public XHTMLText appendOpenCiteTag() {
        this.text.openElement(CITE);
        return this;
    }

    public XHTMLText appendOpenCodeTag() {
        this.text.openElement("code");
        return this;
    }

    public XHTMLText appendCloseCodeTag() {
        this.text.closeElement("code");
        return this;
    }

    public XHTMLText appendOpenEmTag() {
        this.text.openElement(EM);
        return this;
    }

    public XHTMLText appendCloseEmTag() {
        this.text.closeElement(EM);
        return this;
    }

    public XHTMLText appendOpenHeaderTag(int level, String style) {
        if (level > 3 || level < 1) {
            throw new IllegalArgumentException("Level must be between 1 and 3");
        }
        XmlStringBuilder xmlStringBuilder = this.text;
        StringBuilder sb = new StringBuilder();
        sb.append(H);
        sb.append(Integer.toString(level));
        xmlStringBuilder.halfOpenElement(sb.toString());
        this.text.optAttribute(STYLE, style);
        this.text.rightAngleBracket();
        return this;
    }

    public XHTMLText appendCloseHeaderTag(int level) {
        if (level > 3 || level < 1) {
            throw new IllegalArgumentException("Level must be between 1 and 3");
        }
        XmlStringBuilder xmlStringBuilder = this.text;
        StringBuilder sb = new StringBuilder();
        sb.append(H);
        sb.append(Integer.toBinaryString(level));
        xmlStringBuilder.closeElement(sb.toString());
        return this;
    }

    public XHTMLText appendImageTag(String align, String alt, String height, String src, String width) {
        this.text.halfOpenElement(IMG);
        this.text.optAttribute("align", align);
        this.text.optAttribute("alt", alt);
        this.text.optAttribute("height", height);
        this.text.optAttribute("src", src);
        this.text.optAttribute("width", width);
        this.text.rightAngleBracket();
        return this;
    }

    public XHTMLText appendLineItemTag(String style) {
        this.text.halfOpenElement("li");
        this.text.optAttribute(STYLE, style);
        this.text.rightAngleBracket();
        return this;
    }

    public XHTMLText appendCloseLineItemTag() {
        this.text.closeElement("li");
        return this;
    }

    public XHTMLText appendOpenOrderedListTag(String style) {
        this.text.halfOpenElement(OL);
        this.text.optAttribute(STYLE, style);
        this.text.rightAngleBracket();
        return this;
    }

    public XHTMLText appendCloseOrderedListTag() {
        this.text.closeElement(OL);
        return this;
    }

    public XHTMLText appendOpenUnorderedListTag(String style) {
        this.text.halfOpenElement(UL);
        this.text.optAttribute(STYLE, style);
        this.text.rightAngleBracket();
        return this;
    }

    public XHTMLText appendCloseUnorderedListTag() {
        this.text.closeElement(UL);
        return this;
    }

    public XHTMLText appendOpenParagraphTag(String style) {
        this.text.halfOpenElement(P);
        this.text.optAttribute(STYLE, style);
        this.text.rightAngleBracket();
        return this;
    }

    public XHTMLText appendCloseParagraphTag() {
        this.text.closeElement(P);
        return this;
    }

    public XHTMLText appendOpenInlinedQuoteTag(String style) {
        this.text.halfOpenElement(Q);
        this.text.optAttribute(STYLE, style);
        this.text.rightAngleBracket();
        return this;
    }

    public XHTMLText appendCloseInlinedQuoteTag() {
        this.text.closeElement(Q);
        return this;
    }

    public XHTMLText appendOpenSpanTag(String style) {
        this.text.halfOpenElement("span");
        this.text.optAttribute(STYLE, style);
        this.text.rightAngleBracket();
        return this;
    }

    public XHTMLText appendCloseSpanTag() {
        this.text.closeElement("span");
        return this;
    }

    public XHTMLText appendOpenStrongTag() {
        this.text.openElement(STRONG);
        return this;
    }

    public XHTMLText appendCloseStrongTag() {
        this.text.closeElement(STRONG);
        return this;
    }

    public XHTMLText append(String textToAppend) {
        this.text.escape(textToAppend);
        return this;
    }

    public String toString() {
        return this.text.toString();
    }

    public XmlStringBuilder toXML() {
        return this.text;
    }
}
