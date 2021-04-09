package org.jivesoftware.smackx.pubsub;

import java.util.Locale;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;

public enum PubSubElementType {
    CREATE("create", PubSubNamespace.basic),
    DELETE(r3, PubSubNamespace.owner),
    DELETE_EVENT(r3, PubSubNamespace.event),
    CONFIGURE(r3, PubSubNamespace.basic),
    CONFIGURE_OWNER(r3, PubSubNamespace.owner),
    CONFIGURATION("configuration", PubSubNamespace.event),
    OPTIONS("options", PubSubNamespace.basic),
    DEFAULT("default", PubSubNamespace.owner),
    ITEMS(r10, PubSubNamespace.basic),
    ITEMS_EVENT(r10, PubSubNamespace.event),
    ITEM(r10, PubSubNamespace.basic),
    ITEM_EVENT(r10, PubSubNamespace.event),
    PUBLISH("publish", PubSubNamespace.basic),
    PUBLISH_OPTIONS("publish-options", PubSubNamespace.basic),
    PURGE_OWNER("purge", PubSubNamespace.owner),
    PURGE_EVENT("purge", PubSubNamespace.event),
    RETRACT("retract", PubSubNamespace.basic),
    AFFILIATIONS("affiliations", PubSubNamespace.basic),
    AFFILIATIONS_OWNER("affiliations", PubSubNamespace.owner),
    SUBSCRIBE("subscribe", PubSubNamespace.basic),
    SUBSCRIPTION("subscription", PubSubNamespace.basic),
    SUBSCRIPTIONS("subscriptions", PubSubNamespace.basic),
    SUBSCRIPTIONS_OWNER("subscriptions", PubSubNamespace.owner),
    UNSUBSCRIBE("unsubscribe", PubSubNamespace.basic);
    
    private final String eName;
    private final PubSubNamespace nSpace;

    private PubSubElementType(String elemName, PubSubNamespace ns) {
        this.eName = elemName;
        this.nSpace = ns;
    }

    public PubSubNamespace getNamespace() {
        return this.nSpace;
    }

    public String getElementName() {
        return this.eName;
    }

    public static PubSubElementType valueOfFromElemName(String elemName, String namespace) {
        int index = namespace.lastIndexOf(35);
        String fragment = index == -1 ? null : namespace.substring(index + 1);
        if (fragment == null) {
            return valueOf(elemName.toUpperCase(Locale.US).replace('-', '_'));
        }
        StringBuilder sb = new StringBuilder();
        sb.append(elemName);
        sb.append('_');
        sb.append(fragment);
        return valueOf(sb.toString().toUpperCase(Locale.US));
    }
}
