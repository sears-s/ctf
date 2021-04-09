package org.jivesoftware.smackx.iot.discovery.provider;

import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.iot.discovery.element.IoTClaimed;
import org.jivesoftware.smackx.iot.parser.NodeInfoParser;
import org.xmlpull.v1.XmlPullParser;

public class IoTClaimedProvider extends IQProvider<IoTClaimed> {
    public IoTClaimed parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new IoTClaimed(ParserUtils.getJidAttribute(parser), NodeInfoParser.parse(parser));
    }
}
