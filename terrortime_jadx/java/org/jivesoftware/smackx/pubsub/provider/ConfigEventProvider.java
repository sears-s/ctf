package org.jivesoftware.smackx.pubsub.provider;

import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.pubsub.ConfigurationEvent;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.xdata.packet.DataForm;

public class ConfigEventProvider extends EmbeddedExtensionProvider<ConfigurationEvent> {
    /* access modifiers changed from: protected */
    public ConfigurationEvent createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attMap, List<? extends ExtensionElement> content) {
        int size = content.size();
        String str = NodeElement.ELEMENT;
        if (size == 0) {
            return new ConfigurationEvent((String) attMap.get(str));
        }
        return new ConfigurationEvent((String) attMap.get(str), new ConfigureForm((DataForm) content.iterator().next()));
    }
}
