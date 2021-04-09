package org.jivesoftware.smackx.iot.discovery.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smackx.iot.element.NodeInfo;
import org.jxmpp.jid.Jid;

public class IoTClaimed extends IQ {
    public static final String ELEMENT = "claimed";
    public static final String NAMESPACE = "urn:xmpp:iot:discovery";
    private final Jid jid;
    private final NodeInfo nodeInfo;

    public IoTClaimed(Jid jid2) {
        this(jid2, NodeInfo.EMPTY);
    }

    public IoTClaimed(Jid jid2, NodeInfo nodeInfo2) {
        super(ELEMENT, "urn:xmpp:iot:discovery");
        this.jid = jid2;
        this.nodeInfo = nodeInfo2;
    }

    public Jid getJid() {
        return this.jid;
    }

    public String getNodeId() {
        return this.nodeInfo.getNodeId();
    }

    public String getSourceId() {
        return this.nodeInfo.getSourceId();
    }

    public NodeInfo getNodeInfo() {
        return this.nodeInfo;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("jid", (CharSequence) this.jid);
        this.nodeInfo.appendTo(xml);
        xml.setEmptyElement();
        return xml;
    }
}
