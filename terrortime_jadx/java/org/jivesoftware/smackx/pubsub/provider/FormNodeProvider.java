package org.jivesoftware.smackx.pubsub.provider;

import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.pubsub.FormNode;
import org.jivesoftware.smackx.pubsub.FormNodeType;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

public class FormNodeProvider extends EmbeddedExtensionProvider<FormNode> {
    /* access modifiers changed from: protected */
    public FormNode createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content) {
        return new FormNode(FormNodeType.valueOfFromElementName(currentElement, currentNamespace), (String) attributeMap.get(NodeElement.ELEMENT), new Form((DataForm) content.iterator().next()));
    }
}
