package org.jivesoftware.smackx.sid.element;

import java.util.UUID;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.StringUtils;

public abstract class StableAndUniqueIdElement implements ExtensionElement {
    public static final String ATTR_ID = "id";
    private final String id;

    public StableAndUniqueIdElement() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return this.id;
    }

    public StableAndUniqueIdElement(String id2) {
        if (!StringUtils.isNullOrEmpty((CharSequence) id2)) {
            this.id = id2;
            return;
        }
        throw new IllegalArgumentException("Argument 'id' cannot be null or empty.");
    }
}
