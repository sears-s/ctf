package org.jivesoftware.smackx.pubsub;

import java.util.Collections;
import java.util.List;

public class SubscriptionsExtension extends NodeExtension {
    protected List<Subscription> items;

    public enum SubscriptionsNamespace {
        basic(PubSubElementType.SUBSCRIPTIONS),
        owner(PubSubElementType.SUBSCRIPTIONS_OWNER);
        
        public final PubSubElementType type;

        private SubscriptionsNamespace(PubSubElementType type2) {
            this.type = type2;
        }

        public static SubscriptionsNamespace fromXmlns(String xmlns) {
            SubscriptionsNamespace[] values;
            for (SubscriptionsNamespace subscriptionsNamespace : values()) {
                if (subscriptionsNamespace.type.getNamespace().getXmlns().equals(xmlns)) {
                    return subscriptionsNamespace;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid Subscription namespace: ");
            sb.append(xmlns);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public SubscriptionsExtension(List<Subscription> subList) {
        this(SubscriptionsNamespace.basic, null, subList);
    }

    public SubscriptionsExtension(String nodeId, List<Subscription> subList) {
        this(SubscriptionsNamespace.basic, nodeId, subList);
    }

    public SubscriptionsExtension(SubscriptionsNamespace subscriptionsNamespace, String nodeId, List<Subscription> subList) {
        super(subscriptionsNamespace.type, nodeId);
        this.items = Collections.emptyList();
        if (subList != null) {
            this.items = subList;
        }
    }

    public List<Subscription> getSubscriptions() {
        return this.items;
    }

    public CharSequence toXML(String enclosingNamespace) {
        List<Subscription> list = this.items;
        if (list == null || list.size() == 0) {
            return super.toXML(enclosingNamespace);
        }
        StringBuilder builder = new StringBuilder("<");
        builder.append(getElementName());
        if (getNode() != null) {
            builder.append(" node='");
            builder.append(getNode());
            builder.append('\'');
        }
        builder.append('>');
        for (Subscription item : this.items) {
            builder.append(item.toXML((String) null));
        }
        builder.append("</");
        builder.append(getElementName());
        builder.append('>');
        return builder.toString();
    }
}
