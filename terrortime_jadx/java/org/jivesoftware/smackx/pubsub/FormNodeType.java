package org.jivesoftware.smackx.pubsub;

import java.util.Locale;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;

public enum FormNodeType {
    CONFIGURE_OWNER,
    CONFIGURE,
    OPTIONS,
    DEFAULT;

    public PubSubElementType getNodeElement() {
        return PubSubElementType.valueOf(toString());
    }

    public static FormNodeType valueOfFromElementName(String elem, String configNamespace) {
        if (!"configure".equals(elem) || !PubSubNamespace.owner.getXmlns().equals(configNamespace)) {
            return valueOf(elem.toUpperCase(Locale.US));
        }
        return CONFIGURE_OWNER;
    }
}
