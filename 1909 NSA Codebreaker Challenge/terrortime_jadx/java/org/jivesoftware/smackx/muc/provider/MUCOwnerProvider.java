package org.jivesoftware.smackx.muc.provider;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.muc.packet.Destroy;
import org.jivesoftware.smackx.muc.packet.MUCOwner;
import org.xmlpull.v1.XmlPullParser;

public class MUCOwnerProvider extends IQProvider<MUCOwner> {
    public MUCOwner parse(XmlPullParser parser, int initialDepth) throws Exception {
        MUCOwner mucOwner = new MUCOwner();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getName().equals("item")) {
                    mucOwner.addItem(MUCParserUtils.parseItem(parser));
                } else if (parser.getName().equals(Destroy.ELEMENT)) {
                    mucOwner.setDestroy(MUCParserUtils.parseDestroy(parser));
                } else {
                    PacketParserUtils.addExtensionElement((Stanza) mucOwner, parser);
                }
            } else if (eventType == 3 && parser.getName().equals("query")) {
                done = true;
            }
        }
        return mucOwner;
    }
}
