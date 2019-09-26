package org.jivesoftware.smack.provider;

import org.jivesoftware.smack.packet.Message.Body;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.util.ParserUtils;
import org.xmlpull.v1.XmlPullParser;

public class BodyElementProvider extends ExtensionElementProvider<Body> {
    public Body parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new Body(ParserUtils.getXmlLang(parser), PacketParserUtils.parseElementText(parser));
    }
}
