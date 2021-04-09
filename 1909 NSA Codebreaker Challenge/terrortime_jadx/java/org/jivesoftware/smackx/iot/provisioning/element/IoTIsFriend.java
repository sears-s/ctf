package org.jivesoftware.smackx.iot.provisioning.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jxmpp.jid.Jid;

public class IoTIsFriend extends IQ {
    public static final String ELEMENT = "isFriend";
    public static final String NAMESPACE = "urn:xmpp:iot:provisioning";
    private final Jid jid;

    public IoTIsFriend(Jid jid2) {
        super(ELEMENT, "urn:xmpp:iot:provisioning");
        this.jid = jid2;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("jid", (CharSequence) this.jid);
        xml.setEmptyElement();
        return xml;
    }
}
