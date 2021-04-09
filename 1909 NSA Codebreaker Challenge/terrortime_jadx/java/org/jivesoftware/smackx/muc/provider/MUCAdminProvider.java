package org.jivesoftware.smackx.muc.provider;

import java.io.IOException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.muc.packet.MUCAdmin;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MUCAdminProvider extends IQProvider<MUCAdmin> {
    public MUCAdmin parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
        MUCAdmin mucAdmin = new MUCAdmin();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getName().equals("item")) {
                    mucAdmin.addItem(MUCParserUtils.parseItem(parser));
                }
            } else if (eventType == 3 && parser.getName().equals("query")) {
                done = true;
            }
        }
        return mucAdmin;
    }
}
