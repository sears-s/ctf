package org.jivesoftware.smackx.iot.discovery.provider;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.iot.discovery.element.IoTRegister;
import org.jivesoftware.smackx.iot.discovery.element.Tag;
import org.jivesoftware.smackx.iot.discovery.element.Tag.Type;
import org.jivesoftware.smackx.iot.element.NodeInfo;
import org.jivesoftware.smackx.iot.parser.NodeInfoParser;
import org.xmlpull.v1.XmlPullParser;

public class IoTRegisterProvider extends IQProvider<IoTRegister> {
    public IoTRegister parse(XmlPullParser parser, int initialDepth) throws Exception {
        boolean selfOwned = ParserUtils.getBooleanAttribute(parser, "selfOwned", false);
        NodeInfo nodeInfo = NodeInfoParser.parse(parser);
        List<Tag> tags = new ArrayList<>();
        while (parser.getDepth() != initialDepth) {
            if (parser.next() == 2) {
                String element = parser.getName();
                Type type = null;
                char c = 65535;
                int hashCode = element.hashCode();
                if (hashCode != 109446) {
                    if (hashCode == 114225 && element.equals("str")) {
                        c = 0;
                    }
                } else if (element.equals("num")) {
                    c = 1;
                }
                if (c == 0) {
                    type = Type.str;
                } else if (c == 1) {
                    type = Type.num;
                }
                if (type != null) {
                    tags.add(new Tag(parser.getAttributeValue(null, "name"), type, parser.getAttributeValue(null, "value")));
                }
            }
        }
        return new IoTRegister(tags, nodeInfo, selfOwned);
    }
}
