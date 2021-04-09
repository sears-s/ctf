package org.jivesoftware.smackx.iot.discovery.provider;

import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.iot.discovery.element.IoTDisown;
import org.jivesoftware.smackx.iot.parser.NodeInfoParser;
import org.xmlpull.v1.XmlPullParser;

public class IoTDisownProvider extends IQProvider<IoTDisown> {
    public IoTDisown parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new IoTDisown(ParserUtils.getJidAttribute(parser), NodeInfoParser.parse(parser));
    }
}
