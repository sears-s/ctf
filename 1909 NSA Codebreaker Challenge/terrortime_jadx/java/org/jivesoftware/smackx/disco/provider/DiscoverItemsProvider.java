package org.jivesoftware.smackx.disco.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.disco.packet.DiscoverItems.Item;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jxmpp.jid.Jid;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DiscoverItemsProvider extends IQProvider<DiscoverItems> {
    public DiscoverItems parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
        DiscoverItems discoverItems = new DiscoverItems();
        boolean done = false;
        Jid jid = null;
        String name = BuildConfig.FLAVOR;
        String action = BuildConfig.FLAVOR;
        String node = BuildConfig.FLAVOR;
        String str = NodeElement.ELEMENT;
        String str2 = BuildConfig.FLAVOR;
        discoverItems.setNode(parser.getAttributeValue(str2, str));
        while (!done) {
            int eventType = parser.next();
            String str3 = "item";
            if (eventType == 2 && str3.equals(parser.getName())) {
                jid = ParserUtils.getJidAttribute(parser);
                name = parser.getAttributeValue(str2, "name");
                node = parser.getAttributeValue(str2, str);
                action = parser.getAttributeValue(str2, "action");
            } else if (eventType == 3 && str3.equals(parser.getName())) {
                Item item = new Item(jid);
                item.setName(name);
                item.setNode(node);
                item.setAction(action);
                discoverItems.addItem(item);
            } else if (eventType == 3) {
                if ("query".equals(parser.getName())) {
                    done = true;
                }
            }
        }
        return discoverItems;
    }
}
