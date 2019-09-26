package org.jivesoftware.smackx.hints.element;

import org.jivesoftware.smack.packet.Message;

public final class NoStoreHint extends MessageProcessingHint {
    public static final String ELEMENT = "no-store";
    public static final NoStoreHint INSTANCE = new NoStoreHint();

    private NoStoreHint() {
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String toXML(String enclosingNamespace) {
        return "<no-store xmlns='urn:xmpp:hints'/>";
    }

    public MessageProcessingHintType getHintType() {
        return MessageProcessingHintType.no_store;
    }

    public static NoStoreHint from(Message message) {
        return (NoStoreHint) message.getExtension(ELEMENT, MessageProcessingHint.NAMESPACE);
    }

    public static boolean hasHint(Message message) {
        return from(message) != null;
    }

    public static void set(Message message) {
        message.overrideExtension(INSTANCE);
    }
}
