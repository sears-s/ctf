package org.jivesoftware.smackx.pubsub.provider;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.xmlpull.v1.XmlPullParser;

public class PubSubProvider extends IQProvider<PubSub> {
    public PubSub parse(XmlPullParser parser, int initialDepth) throws Exception {
        PubSub pubsub = new PubSub(PubSubNamespace.valueOfFromXmlns(parser.getNamespace()));
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                PacketParserUtils.addExtensionElement((Stanza) pubsub, parser);
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return pubsub;
            }
        }
    }
}
