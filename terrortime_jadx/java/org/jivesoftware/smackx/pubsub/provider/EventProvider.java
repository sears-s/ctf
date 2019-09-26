package org.jivesoftware.smackx.pubsub.provider;

import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jivesoftware.smackx.pubsub.EventElementType;
import org.jivesoftware.smackx.pubsub.NodeExtension;

public class EventProvider extends EmbeddedExtensionProvider<EventElement> {
    /* access modifiers changed from: protected */
    public EventElement createReturnExtension(String currentElement, String currentNamespace, Map<String, String> map, List<? extends ExtensionElement> content) {
        return new EventElement(EventElementType.valueOf(((ExtensionElement) content.get(0)).getElementName()), (NodeExtension) content.get(0));
    }
}
