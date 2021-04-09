package org.jivesoftware.smackx.pubsub.provider;

import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.pubsub.ItemsExtension;
import org.jivesoftware.smackx.pubsub.ItemsExtension.ItemsElementType;

public class ItemsProvider extends EmbeddedExtensionProvider<ItemsExtension> {
    /* access modifiers changed from: protected */
    public ItemsExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content) {
        return new ItemsExtension(ItemsElementType.items, (String) attributeMap.get(NodeElement.ELEMENT), content);
    }
}
