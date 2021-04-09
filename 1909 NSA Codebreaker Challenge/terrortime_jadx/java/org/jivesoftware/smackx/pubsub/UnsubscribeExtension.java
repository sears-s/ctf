package org.jivesoftware.smackx.pubsub;

import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iot.data.element.NodeElement;

public class UnsubscribeExtension extends NodeExtension {
    protected String id;
    protected String jid;

    public UnsubscribeExtension(String subscriptionJid) {
        this(subscriptionJid, null, null);
    }

    public UnsubscribeExtension(String subscriptionJid, String nodeId) {
        this(subscriptionJid, nodeId, null);
    }

    public UnsubscribeExtension(String jid2, String nodeId, String subscriptionId) {
        super(PubSubElementType.UNSUBSCRIBE, nodeId);
        this.jid = jid2;
        this.id = subscriptionId;
    }

    public String getJid() {
        return this.jid;
    }

    public String getId() {
        return this.id;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder();
        xml.halfOpenElement(getElementName());
        xml.attribute("jid", this.jid);
        xml.optAttribute(NodeElement.ELEMENT, getNode());
        xml.optAttribute("subid", this.id);
        xml.closeEmptyElement();
        return xml;
    }
}
