package org.jivesoftware.smackx.hoxt.packet;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.shim.packet.HeadersExtension;

public abstract class AbstractHttpOverXmpp extends IQ {
    public static final String NAMESPACE = "urn:xmpp:http";
    private final Data data;
    private final HeadersExtension headers;
    private final String version;

    public static class Base64 implements NamedElement {
        public static final String ELEMENT = "base64";
        private final String text;

        public Base64(String text2) {
            this.text = text2;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.rightAngleBracket();
            xml.optAppend((CharSequence) this.text);
            xml.closeElement((NamedElement) this);
            return xml;
        }

        public String getText() {
            return this.text;
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static abstract class Builder<B extends Builder<B, C>, C extends AbstractHttpOverXmpp> {
        /* access modifiers changed from: private */
        public Data data;
        /* access modifiers changed from: private */
        public HeadersExtension headers;
        /* access modifiers changed from: private */
        public String version = "1.1";

        public abstract C build();

        /* access modifiers changed from: protected */
        public abstract B getThis();

        public B setData(Data data2) {
            this.data = data2;
            return getThis();
        }

        public B setHeaders(HeadersExtension headers2) {
            this.headers = headers2;
            return getThis();
        }

        public B setVersion(String version2) {
            this.version = version2;
            return getThis();
        }
    }

    public static class ChunkedBase64 implements NamedElement {
        public static final String ELEMENT = "chunkedBase64";
        private final String streamId;

        public ChunkedBase64(String streamId2) {
            this.streamId = streamId2;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.attribute(Base64BinaryChunk.ATTRIBUTE_STREAM_ID, this.streamId);
            xml.closeEmptyElement();
            return xml;
        }

        public String getStreamId() {
            return this.streamId;
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class Data implements NamedElement {
        public static final String ELEMENT = "data";
        private final NamedElement child;

        public Data(NamedElement child2) {
            this.child = child2;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.rightAngleBracket();
            xml.element(this.child);
            xml.closeElement((NamedElement) this);
            return xml;
        }

        public NamedElement getChild() {
            return this.child;
        }

        public String getElementName() {
            return "data";
        }
    }

    public static class Ibb implements NamedElement {
        public static final String ELEMENT = "ibb";
        private final String sid;

        public Ibb(String sid2) {
            this.sid = sid2;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.attribute("sid", this.sid);
            xml.closeEmptyElement();
            return xml;
        }

        public String getSid() {
            return this.sid;
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class Text implements NamedElement {
        public static final String ELEMENT = "text";
        private final String text;

        public Text(String text2) {
            this.text = text2;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.rightAngleBracket();
            xml.optAppend((CharSequence) this.text);
            xml.closeElement((NamedElement) this);
            return xml;
        }

        public String getText() {
            return this.text;
        }

        public String getElementName() {
            return "text";
        }
    }

    public static class Xml implements NamedElement {
        public static final String ELEMENT = "xml";
        private final String text;

        public Xml(String text2) {
            this.text = text2;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.rightAngleBracket();
            xml.optAppend((CharSequence) this.text);
            xml.closeElement((NamedElement) this);
            return xml;
        }

        public String getText() {
            return this.text;
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    /* access modifiers changed from: protected */
    public abstract IQChildElementXmlStringBuilder getIQHoxtChildElementBuilder(IQChildElementXmlStringBuilder iQChildElementXmlStringBuilder);

    protected AbstractHttpOverXmpp(String element, Builder<?, ?> builder) {
        super(element, "urn:xmpp:http");
        this.headers = builder.headers;
        this.data = builder.data;
        this.version = (String) Objects.requireNonNull(builder.version, "version must not be null");
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        IQChildElementXmlStringBuilder builder = getIQHoxtChildElementBuilder(xml);
        builder.optAppend((Element) this.headers);
        builder.optAppend((Element) this.data);
        return builder;
    }

    public String getVersion() {
        return this.version;
    }

    public HeadersExtension getHeaders() {
        return this.headers;
    }

    public Data getData() {
        return this.data;
    }
}
