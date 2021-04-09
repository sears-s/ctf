package org.jivesoftware.smackx.pubsub.provider;

import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.pubsub.SubscriptionsExtension;
import org.jivesoftware.smackx.pubsub.SubscriptionsExtension.SubscriptionsNamespace;

public class SubscriptionsProvider extends EmbeddedExtensionProvider<SubscriptionsExtension> {
    /* access modifiers changed from: protected */
    public SubscriptionsExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content) {
        return new SubscriptionsExtension(SubscriptionsNamespace.fromXmlns(currentNamespace), (String) attributeMap.get(NodeElement.ELEMENT), content);
    }
}
