package org.jivesoftware.smackx.pubsub;

import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iot.data.element.NodeElement;

public class GetItemsRequest extends NodeExtension {
    protected final int maxItems;
    protected final String subId;

    public GetItemsRequest(String nodeId) {
        this(nodeId, null, -1);
    }

    public GetItemsRequest(String nodeId, String subscriptionId) {
        this(nodeId, subscriptionId, -1);
    }

    public GetItemsRequest(String nodeId, int maxItemsToReturn) {
        this(nodeId, null, maxItemsToReturn);
    }

    public GetItemsRequest(String nodeId, String subscriptionId, int maxItemsToReturn) {
        super(PubSubElementType.ITEMS, nodeId);
        this.maxItems = maxItemsToReturn;
        this.subId = subscriptionId;
    }

    public String getSubscriptionId() {
        return this.subId;
    }

    public int getMaxItems() {
        return this.maxItems;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder();
        xml.halfOpenElement(getElementName());
        xml.attribute(NodeElement.ELEMENT, getNode());
        xml.optAttribute("subid", getSubscriptionId());
        xml.optIntAttribute("max_items", getMaxItems());
        xml.closeEmptyElement();
        return xml;
    }
}
