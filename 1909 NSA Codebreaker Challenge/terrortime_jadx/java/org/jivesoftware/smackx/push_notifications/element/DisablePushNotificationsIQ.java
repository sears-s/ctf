package org.jivesoftware.smackx.push_notifications.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jxmpp.jid.Jid;

public class DisablePushNotificationsIQ extends IQ {
    public static final String ELEMENT = "disable";
    public static final String NAMESPACE = "urn:xmpp:push:0";
    private final Jid jid;
    private final String node;

    public DisablePushNotificationsIQ(Jid jid2, String node2) {
        super("disable", "urn:xmpp:push:0");
        this.jid = jid2;
        this.node = node2;
        setType(Type.set);
    }

    public DisablePushNotificationsIQ(Jid jid2) {
        this(jid2, null);
    }

    public Jid getJid() {
        return this.jid;
    }

    public String getNode() {
        return this.node;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("jid", (CharSequence) this.jid);
        xml.optAttribute(NodeElement.ELEMENT, this.node);
        xml.rightAngleBracket();
        return xml;
    }
}
