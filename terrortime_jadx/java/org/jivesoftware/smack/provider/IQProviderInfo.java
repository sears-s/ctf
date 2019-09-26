package org.jivesoftware.smack.provider;

import org.jivesoftware.smack.packet.IQ;

public final class IQProviderInfo extends AbstractProviderInfo {
    public /* bridge */ /* synthetic */ String getElementName() {
        return super.getElementName();
    }

    public /* bridge */ /* synthetic */ String getNamespace() {
        return super.getNamespace();
    }

    public IQProviderInfo(String elementName, String namespace, IQProvider<IQ> iqProvider) {
        super(elementName, namespace, iqProvider);
    }
}
