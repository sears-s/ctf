package org.jivesoftware.smackx.caps.provider;

import java.io.IOException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.roster.packet.RosterVer;
import org.jivesoftware.smackx.caps.packet.CapsExtension;
import org.jivesoftware.smackx.hashes.element.HashElement;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class CapsExtensionProvider extends ExtensionElementProvider<CapsExtension> {
    public CapsExtension parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        if (parser.getEventType() == 2) {
            String str = "c";
            if (parser.getName().equalsIgnoreCase(str)) {
                String hash = parser.getAttributeValue(null, HashElement.ELEMENT);
                String version = parser.getAttributeValue(null, RosterVer.ELEMENT);
                String node = parser.getAttributeValue(null, NodeElement.ELEMENT);
                parser.next();
                if (parser.getEventType() != 3 || !parser.getName().equalsIgnoreCase(str)) {
                    throw new SmackException("Malformed nested Caps element");
                } else if (hash != null && version != null && node != null) {
                    return new CapsExtension(node, version, hash);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Caps element with missing attributes. Attributes: hash=");
                    sb.append(hash);
                    sb.append(" version=");
                    sb.append(version);
                    sb.append(" node=");
                    sb.append(node);
                    throw new SmackException(sb.toString());
                }
            }
        }
        throw new SmackException("Malformed Caps element");
    }
}
