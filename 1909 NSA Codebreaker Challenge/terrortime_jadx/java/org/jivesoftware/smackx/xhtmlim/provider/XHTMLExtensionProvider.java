package org.jivesoftware.smackx.xhtmlim.provider;

import java.io.IOException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XHTMLExtensionProvider extends ExtensionElementProvider<XHTMLExtension> {
    public XHTMLExtension parse(XmlPullParser parser, int initialDepth) throws IOException, XmlPullParserException {
        XHTMLExtension xhtmlExtension = new XHTMLExtension();
        while (true) {
            int eventType = parser.getEventType();
            String name = parser.getName();
            if (eventType == 2) {
                if (name.equals("body")) {
                    xhtmlExtension.addBody(PacketParserUtils.parseElement(parser));
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return xhtmlExtension;
            }
            parser.next();
        }
    }
}
