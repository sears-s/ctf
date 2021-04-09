package org.jivesoftware.smackx.pubsub;

import java.util.Collections;
import java.util.List;

public class ItemDeleteEvent extends SubscriptionEvent {
    private List<String> itemIds = Collections.emptyList();

    public ItemDeleteEvent(String nodeId, List<String> deletedItemIds, List<String> subscriptionIds) {
        super(nodeId, subscriptionIds);
        if (deletedItemIds != null) {
            this.itemIds = deletedItemIds;
            return;
        }
        throw new IllegalArgumentException("deletedItemIds cannot be null");
    }

    public List<String> getItemIds() {
        return Collections.unmodifiableList(this.itemIds);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append("  [subscriptions: ");
        sb.append(getSubscriptions());
        sb.append("], [Deleted Items: ");
        sb.append(this.itemIds);
        sb.append(']');
        return sb.toString();
    }
}
