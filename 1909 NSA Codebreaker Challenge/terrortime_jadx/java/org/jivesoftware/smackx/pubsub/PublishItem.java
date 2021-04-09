package org.jivesoftware.smackx.pubsub;

import java.util.ArrayList;
import java.util.Collection;
import org.jivesoftware.smackx.pubsub.Item;

public class PublishItem<T extends Item> extends NodeExtension {
    protected Collection<T> items;

    public PublishItem(String nodeId, T toPublish) {
        super(PubSubElementType.PUBLISH, nodeId);
        this.items = new ArrayList(1);
        this.items.add(toPublish);
    }

    public PublishItem(String nodeId, Collection<T> toPublish) {
        super(PubSubElementType.PUBLISH, nodeId);
        this.items = toPublish;
    }

    public String toXML(String enclosingNamespace) {
        StringBuilder builder = new StringBuilder("<");
        builder.append(getElementName());
        builder.append(" node='");
        builder.append(getNode());
        builder.append("'>");
        for (T item : this.items) {
            builder.append(item.toXML((String) null));
        }
        builder.append("</publish>");
        return builder.toString();
    }
}
