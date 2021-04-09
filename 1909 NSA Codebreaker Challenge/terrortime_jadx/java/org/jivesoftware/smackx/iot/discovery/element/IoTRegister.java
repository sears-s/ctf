package org.jivesoftware.smackx.iot.discovery.element;

import java.util.Collection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smackx.iot.element.NodeInfo;

public class IoTRegister extends IQ {
    public static final String ELEMENT = "register";
    public static final String NAMESPACE = "urn:xmpp:iot:discovery";
    private final NodeInfo nodeInfo;
    private final boolean selfOwned;
    private final Collection<Tag> tags;

    public IoTRegister(Collection<Tag> tags2, NodeInfo nodeInfo2, boolean selfOwned2) {
        super("register", "urn:xmpp:iot:discovery");
        if (!tags2.isEmpty()) {
            this.tags = tags2;
            this.nodeInfo = nodeInfo2;
            this.selfOwned = selfOwned2;
            return;
        }
        throw new IllegalArgumentException();
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        this.nodeInfo.appendTo(xml);
        xml.optBooleanAttribute("selfOwned", this.selfOwned);
        xml.rightAngleBracket();
        xml.append(this.tags);
        return xml;
    }
}
