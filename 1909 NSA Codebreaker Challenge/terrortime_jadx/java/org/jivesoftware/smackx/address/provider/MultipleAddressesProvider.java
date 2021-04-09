package org.jivesoftware.smackx.address.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.address.packet.MultipleAddresses;
import org.jivesoftware.smackx.address.packet.MultipleAddresses.Address;
import org.jivesoftware.smackx.address.packet.MultipleAddresses.Type;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild;
import org.jivesoftware.smackx.reference.element.ReferenceElement;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MultipleAddressesProvider extends ExtensionElementProvider<MultipleAddresses> {
    public MultipleAddresses parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser = parser;
        MultipleAddresses multipleAddresses = new MultipleAddresses();
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                int i = initialDepth;
                String name = parser.getName();
                char c = 65535;
                if (name.hashCode() == -1147692044 && name.equals(Address.ELEMENT)) {
                    c = 0;
                }
                if (c == 0) {
                    String str = BuildConfig.FLAVOR;
                    multipleAddresses.addAddress(Type.valueOf(xmlPullParser.getAttributeValue(str, "type")), ParserUtils.getJidAttribute(xmlPullParser, "jid"), xmlPullParser.getAttributeValue(str, NodeElement.ELEMENT), xmlPullParser.getAttributeValue(str, JingleFileTransferChild.ELEM_DESC), "true".equals(xmlPullParser.getAttributeValue(str, "delivered")), xmlPullParser.getAttributeValue(str, ReferenceElement.ATTR_URI));
                }
            } else if (eventType != 3) {
                int i2 = initialDepth;
            } else if (parser.getDepth() == initialDepth) {
                return multipleAddresses;
            }
        }
    }
}
