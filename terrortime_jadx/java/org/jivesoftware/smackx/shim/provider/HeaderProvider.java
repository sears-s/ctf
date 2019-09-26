package org.jivesoftware.smackx.shim.provider;

import java.io.IOException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.shim.packet.Header;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class HeaderProvider extends ExtensionElementProvider<Header> {
    public Header parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
        String name = parser.getAttributeValue(null, "name");
        String value = null;
        parser.next();
        if (parser.getEventType() == 4) {
            value = parser.getText();
        }
        while (parser.getEventType() != 3) {
            parser.next();
        }
        return new Header(name, value);
    }
}
