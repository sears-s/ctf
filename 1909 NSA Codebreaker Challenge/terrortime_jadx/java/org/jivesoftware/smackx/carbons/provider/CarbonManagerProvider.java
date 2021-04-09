package org.jivesoftware.smackx.carbons.provider;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension.Direction;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jivesoftware.smackx.forward.provider.ForwardedProvider;
import org.xmlpull.v1.XmlPullParser;

public class CarbonManagerProvider extends ExtensionElementProvider<CarbonExtension> {
    private static final ForwardedProvider FORWARDED_PROVIDER = new ForwardedProvider();

    public CarbonExtension parse(XmlPullParser parser, int initialDepth) throws Exception {
        Direction dir = Direction.valueOf(parser.getName());
        Forwarded fwd = null;
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == 2 && parser.getName().equals(Forwarded.ELEMENT)) {
                fwd = (Forwarded) FORWARDED_PROVIDER.parse(parser);
            } else if (eventType == 3 && dir == Direction.valueOf(parser.getName())) {
                done = true;
            }
        }
        if (fwd != null) {
            return new CarbonExtension(dir, fwd);
        }
        throw new SmackException("sent/received must contain exactly one <forwarded> tag");
    }
}
