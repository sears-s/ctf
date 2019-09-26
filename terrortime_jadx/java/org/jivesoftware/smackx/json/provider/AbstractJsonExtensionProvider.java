package org.jivesoftware.smackx.json.provider;

import java.io.IOException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.json.packet.AbstractJsonPacketExtension;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class AbstractJsonExtensionProvider extends ExtensionElementProvider<AbstractJsonPacketExtension> {
    public abstract AbstractJsonPacketExtension from(String str);

    public AbstractJsonPacketExtension parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        return from(PacketParserUtils.parseElementText(parser));
    }
}
