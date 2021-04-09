package org.jivesoftware.smack.provider;

import org.jivesoftware.smack.packet.ExtensionElement;

public final class ExtensionProviderInfo extends AbstractProviderInfo {
    public /* bridge */ /* synthetic */ String getElementName() {
        return super.getElementName();
    }

    public /* bridge */ /* synthetic */ String getNamespace() {
        return super.getNamespace();
    }

    public ExtensionProviderInfo(String elementName, String namespace, ExtensionElementProvider<ExtensionElement> extProvider) {
        super(elementName, namespace, extProvider);
    }
}
