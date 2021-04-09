package org.jivesoftware.smackx.iot.discovery.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smackx.iot.element.NodeInfo;

public class IoTDisowned extends IQ {
    public static final String ELEMENT = "disown";
    public static final String NAMESPACE = "urn:xmpp:iot:discovery";
    private final NodeInfo nodeInfo;

    public IoTDisowned(NodeInfo nodeInfo2) {
        super("disown", "urn:xmpp:iot:discovery");
        this.nodeInfo = nodeInfo2;
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
        this.nodeInfo.appendTo(xml);
        xml.setEmptyElement();
        return xml;
    }
}
