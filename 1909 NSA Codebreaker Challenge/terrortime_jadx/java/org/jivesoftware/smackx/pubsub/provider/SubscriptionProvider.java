package org.jivesoftware.smackx.pubsub.provider;

import java.io.IOException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.pubsub.Subscription.State;
import org.jxmpp.jid.Jid;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SubscriptionProvider extends ExtensionElementProvider<Subscription> {
    public Subscription parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
        Jid jid = ParserUtils.getJidAttribute(parser);
        State state = null;
        String nodeId = parser.getAttributeValue(null, NodeElement.ELEMENT);
        String subId = parser.getAttributeValue(null, "subid");
        String state2 = parser.getAttributeValue(null, "subscription");
        boolean isRequired = false;
        int tag = parser.next();
        if (tag == 2) {
            String str = "subscribe-options";
            if (parser.getName().equals(str)) {
                tag = parser.next();
                if (tag == 2 && parser.getName().equals("required")) {
                    isRequired = true;
                }
                while (tag != 3 && !parser.getName().equals(str)) {
                    tag = parser.next();
                }
            }
        }
        boolean isRequired2 = isRequired;
        int i = tag;
        while (parser.getEventType() != 3) {
            parser.next();
        }
        if (state2 != null) {
            state = State.valueOf(state2);
        }
        Subscription subscription = new Subscription(jid, nodeId, subId, state, isRequired2);
        return subscription;
    }
}
