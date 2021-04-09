package org.jivesoftware.smackx.hints.element;

import org.jivesoftware.smack.packet.ExtensionElement;

public abstract class MessageProcessingHint implements ExtensionElement {
    public static final String NAMESPACE = "urn:xmpp:hints";

    public abstract MessageProcessingHintType getHintType();

    public final String getNamespace() {
        return NAMESPACE;
    }
}
