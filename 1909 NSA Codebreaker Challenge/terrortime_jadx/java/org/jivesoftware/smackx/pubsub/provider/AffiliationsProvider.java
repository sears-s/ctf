package org.jivesoftware.smackx.pubsub.provider;

import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smackx.pubsub.Affiliation.AffiliationNamespace;
import org.jivesoftware.smackx.pubsub.AffiliationsExtension;

public class AffiliationsProvider extends EmbeddedExtensionProvider<AffiliationsExtension> {
    /* access modifiers changed from: protected */
    public AffiliationsExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> map, List<? extends ExtensionElement> content) {
        return new AffiliationsExtension(AffiliationNamespace.fromXmlns(currentNamespace), content);
    }
}
