package org.jivesoftware.smackx.shim.provider;

import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smackx.shim.packet.HeadersExtension;

public class HeadersProvider extends EmbeddedExtensionProvider<HeadersExtension> {
    public static final HeadersProvider INSTANCE = new HeadersProvider();

    /* access modifiers changed from: protected */
    public HeadersExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> map, List<? extends ExtensionElement> content) {
        return new HeadersExtension(content);
    }
}
