package org.jivesoftware.smackx.iot.provisioning.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smackx.mam.element.MamElements.MamResultExtension;
import org.jxmpp.jid.BareJid;

public class IoTIsFriendResponse extends IQ {
    public static final String ELEMENT = "isFriendResponse";
    public static final String NAMESPACE = "urn:xmpp:iot:provisioning";
    private final BareJid jid;
    private final boolean result;

    public IoTIsFriendResponse(BareJid jid2, boolean result2) {
        super(ELEMENT, "urn:xmpp:iot:provisioning");
        this.jid = jid2;
        this.result = result2;
    }

    public BareJid getJid() {
        return this.jid;
    }

    public boolean getIsFriendResult() {
        return this.result;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("jid", (CharSequence) this.jid);
        xml.attribute(MamResultExtension.ELEMENT, this.result);
        xml.setEmptyElement();
        return xml;
    }
}
