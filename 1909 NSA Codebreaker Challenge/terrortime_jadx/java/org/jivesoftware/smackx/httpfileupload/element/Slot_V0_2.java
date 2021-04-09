package org.jivesoftware.smackx.httpfileupload.element;

import java.net.URL;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;

public class Slot_V0_2 extends Slot {
    public static final String NAMESPACE = "urn:xmpp:http:upload";

    public Slot_V0_2(URL putUrl, URL getUrl) {
        super(putUrl, getUrl, null, "urn:xmpp:http:upload");
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.element("put", this.putUrl.toString());
        xml.element("get", this.getUrl.toString());
        return xml;
    }
}
