package org.jivesoftware.smackx.bob.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smackx.bob.BoBData;
import org.jivesoftware.smackx.bob.BoBHash;

public class BoBIQ extends IQ {
    public static final String ELEMENT = "data";
    public static final String NAMESPACE = "urn:xmpp:bob";
    private final BoBData bobData;
    private final BoBHash bobHash;

    public BoBIQ(BoBHash bobHash2, BoBData bobData2) {
        super("data", "urn:xmpp:bob");
        this.bobHash = bobHash2;
        this.bobData = bobData2;
    }

    public BoBIQ(BoBHash bobHash2) {
        this(bobHash2, null);
    }

    public BoBHash getBoBHash() {
        return this.bobHash;
    }

    public BoBData getBoBData() {
        return this.bobData;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("cid", this.bobHash.getCid());
        BoBData boBData = this.bobData;
        if (boBData != null) {
            xml.optIntAttribute("max_age", boBData.getMaxAge());
            xml.attribute("type", this.bobData.getType());
            xml.rightAngleBracket();
            xml.escape(this.bobData.getContentBase64Encoded());
        } else {
            xml.setEmptyElement();
        }
        return xml;
    }
}
