package org.jivesoftware.smackx.httpfileupload.element;

import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.shim.packet.Header;

public class Slot extends IQ {
    public static final String ELEMENT = "slot";
    public static final String NAMESPACE = "urn:xmpp:http:upload:0";
    protected final URL getUrl;
    private final Map<String, String> headers;
    protected final URL putUrl;

    public Slot(URL putUrl2, URL getUrl2) {
        this(putUrl2, getUrl2, null);
    }

    public Slot(URL putUrl2, URL getUrl2, Map<String, String> headers2) {
        this(putUrl2, getUrl2, headers2, "urn:xmpp:http:upload:0");
    }

    protected Slot(URL putUrl2, URL getUrl2, Map<String, String> headers2, String namespace) {
        super(ELEMENT, namespace);
        setType(Type.result);
        this.putUrl = putUrl2;
        this.getUrl = getUrl2;
        if (headers2 == null) {
            this.headers = Collections.emptyMap();
        } else {
            this.headers = Collections.unmodifiableMap(headers2);
        }
    }

    public URL getPutUrl() {
        return this.putUrl;
    }

    public URL getGetUrl() {
        return this.getUrl;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        String str = "put";
        String str2 = "url";
        xml.halfOpenElement(str).attribute(str2, this.putUrl.toString());
        if (this.headers.isEmpty()) {
            xml.closeEmptyElement();
        } else {
            xml.rightAngleBracket();
            for (Entry<String, String> entry : getHeaders().entrySet()) {
                String str3 = Header.ELEMENT;
                xml.halfOpenElement(str3).attribute("name", (String) entry.getKey()).rightAngleBracket();
                xml.escape((String) entry.getValue());
                xml.closeElement(str3);
            }
            xml.closeElement(str);
        }
        xml.halfOpenElement("get").attribute(str2, this.getUrl.toString()).closeEmptyElement();
        return xml;
    }
}
