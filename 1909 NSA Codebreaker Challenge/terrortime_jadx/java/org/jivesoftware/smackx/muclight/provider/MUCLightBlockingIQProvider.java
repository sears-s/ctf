package org.jivesoftware.smackx.muclight.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.util.HashMap;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.muclight.element.MUCLightBlockingIQ;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MUCLightBlockingIQProvider extends IQProvider<MUCLightBlockingIQ> {
    public MUCLightBlockingIQ parse(XmlPullParser parser, int initialDepth) throws Exception {
        HashMap<Jid, Boolean> rooms = null;
        HashMap<Jid, Boolean> users = null;
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getName().equals("room")) {
                    rooms = parseBlocking(parser, rooms);
                }
                if (parser.getName().equals("user")) {
                    users = parseBlocking(parser, users);
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                MUCLightBlockingIQ mucLightBlockingIQ = new MUCLightBlockingIQ(rooms, users);
                mucLightBlockingIQ.setType(Type.result);
                return mucLightBlockingIQ;
            }
        }
    }

    private static HashMap<Jid, Boolean> parseBlocking(XmlPullParser parser, HashMap<Jid, Boolean> map) throws XmppStringprepException, XmlPullParserException, IOException {
        if (map == null) {
            map = new HashMap<>();
        }
        String action = parser.getAttributeValue(BuildConfig.FLAVOR, "action");
        if (action.equals("deny")) {
            map.put(JidCreate.from(parser.nextText()), Boolean.valueOf(false));
        } else if (action.equals("allow")) {
            map.put(JidCreate.from(parser.nextText()), Boolean.valueOf(true));
        }
        return map;
    }
}
