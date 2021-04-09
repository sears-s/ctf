package org.jivesoftware.smackx.hints;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.hints.element.MessageProcessingHintType;
import org.jivesoftware.smackx.hints.element.NoCopyHint;
import org.jivesoftware.smackx.hints.element.NoPermanentStoreHint;
import org.jivesoftware.smackx.hints.element.NoStoreHint;
import org.jivesoftware.smackx.hints.element.StoreHint;

public class MessageProcessingHintsManager {
    public static Set<MessageProcessingHintType> getHintsFrom(Message message) {
        Set<MessageProcessingHintType> hints = null;
        if (NoCopyHint.hasHint(message)) {
            hints = new HashSet<>(MessageProcessingHintType.values().length);
            hints.add(MessageProcessingHintType.no_copy);
        }
        if (NoPermanentStoreHint.hasHint(message)) {
            if (hints == null) {
                hints = new HashSet<>(MessageProcessingHintType.values().length);
            }
            hints.add(MessageProcessingHintType.no_permanent_store);
        }
        if (NoStoreHint.hasHint(message)) {
            if (hints == null) {
                hints = new HashSet<>(MessageProcessingHintType.values().length);
            }
            hints.add(MessageProcessingHintType.no_store);
        }
        if (StoreHint.hasHint(message)) {
            if (hints == null) {
                hints = new HashSet<>(MessageProcessingHintType.values().length);
            }
            hints.add(MessageProcessingHintType.store);
        }
        if (hints == null) {
            return Collections.emptySet();
        }
        return hints;
    }
}
