package org.jivesoftware.smackx.pubsub.provider;

import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.PubSubElementType;

public class SimpleNodeProvider extends EmbeddedExtensionProvider<NodeExtension> {
    /* access modifiers changed from: protected */
    public NodeExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> list) {
        return new NodeExtension(PubSubElementType.valueOfFromElemName(currentElement, currentNamespace), (String) attributeMap.get(NodeElement.ELEMENT));
    }
}
