package org.jivesoftware.smackx.pubsub.provider;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.Item.ItemNamespace;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.xmlpull.v1.XmlPullParser;

public class ItemProvider extends ExtensionElementProvider<Item> {
    public Item parse(XmlPullParser parser, int initialDepth) throws Exception {
        String id = parser.getAttributeValue(null, "id");
        String node = parser.getAttributeValue(null, NodeElement.ELEMENT);
        ItemNamespace itemNamespace = ItemNamespace.fromXmlns(parser.getNamespace());
        if (parser.next() == 3) {
            return new Item(itemNamespace, id, node);
        }
        ExtensionElementProvider<ExtensionElement> extensionProvider = ProviderManager.getExtensionProvider(parser.getName(), parser.getNamespace());
        if (extensionProvider == null) {
            return new PayloadItem(itemNamespace, id, node, new SimplePayload(PacketParserUtils.parseElement(parser, true).toString()));
        }
        return new PayloadItem(itemNamespace, id, node, (ExtensionElement) extensionProvider.parse(parser));
    }
}
