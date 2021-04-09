package org.jivesoftware.smackx.hints.element;

import org.jivesoftware.smack.packet.Message;

public final class StoreHint extends MessageProcessingHint {
    public static final String ELEMENT = "store";
    public static final StoreHint INSTANCE = new StoreHint();

    private StoreHint() {
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String toXML(String enclosingNamespace) {
        return "<store xmlns='urn:xmpp:hints'/>";
    }

    public MessageProcessingHintType getHintType() {
        return MessageProcessingHintType.store;
    }

    public static StoreHint from(Message message) {
        return (StoreHint) message.getExtension(ELEMENT, MessageProcessingHint.NAMESPACE);
    }

    public static boolean hasHint(Message message) {
        return from(message) != null;
    }

    public static void set(Message message) {
        message.overrideExtension(INSTANCE);
    }
}
