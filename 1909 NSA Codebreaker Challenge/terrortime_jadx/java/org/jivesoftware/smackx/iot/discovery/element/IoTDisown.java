package org.jivesoftware.smackx.iot.discovery.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smackx.iot.element.NodeInfo;
import org.jxmpp.jid.Jid;

public class IoTDisown extends IQ {
    public static final String ELEMENT = "disown";
    public static final String NAMESPACE = "urn:xmpp:iot:discovery";
    private final Jid jid;
    private final NodeInfo nodeInfo;

    public IoTDisown(Jid jid2) {
        this(jid2, NodeInfo.EMPTY);
    }

    public IoTDisown(Jid jid2, NodeInfo nodeInfo2) {
        super("disown", "urn:xmpp:iot:discovery");
        this.jid = jid2;
        this.nodeInfo = nodeInfo2;
    }

    public Jid getJid() {
        return this.jid;
    }

    public String getNodeId() {
        NodeInfo nodeInfo2 = this.nodeInfo;
        if (nodeInfo2 != null) {
            return nodeInfo2.getNodeId();
        }
        return null;
    }

    public String getSourceId() {
        NodeInfo nodeInfo2 = this.nodeInfo;
        if (nodeInfo2 != null) {
            return nodeInfo2.getSourceId();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("jid", (CharSequence) this.jid);
        this.nodeInfo.appendTo(xml);
        xml.rightAngleBracket();
        return xml;
    }
}
