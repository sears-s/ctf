package org.jivesoftware.smackx.muc.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.jingle.element.JingleReason;
import org.jivesoftware.smackx.muc.MUCAffiliation;
import org.jivesoftware.smackx.muc.MUCRole;
import org.jivesoftware.smackx.muc.packet.Destroy;
import org.jivesoftware.smackx.muc.packet.MUCItem;
import org.jivesoftware.smackx.nick.packet.Nick;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MUCParserUtils {
    public static MUCItem parseItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser = parser;
        int initialDepth = parser.getDepth();
        String str = BuildConfig.FLAVOR;
        MUCAffiliation affiliation = MUCAffiliation.fromString(xmlPullParser.getAttributeValue(str, Affiliation.ELEMENT));
        String str2 = Nick.ELEMENT_NAME;
        Resourcepart nick = ParserUtils.getResourcepartAttribute(xmlPullParser, str2);
        MUCRole role = MUCRole.fromString(xmlPullParser.getAttributeValue(str, "role"));
        Jid jid = ParserUtils.getJidAttribute(parser);
        Jid actor = null;
        Resourcepart actorNick = null;
        String reason = null;
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != -934964668) {
                    if (hashCode == 92645877 && name.equals("actor")) {
                        c = 0;
                    }
                } else if (name.equals(JingleReason.ELEMENT)) {
                    c = 1;
                }
                if (c == 0) {
                    Jid actor2 = ParserUtils.getJidAttribute(parser);
                    String actorNickString = xmlPullParser.getAttributeValue(str, str2);
                    if (actorNickString != null) {
                        actor = actor2;
                        actorNick = Resourcepart.from(actorNickString);
                    } else {
                        actor = actor2;
                    }
                } else if (c == 1) {
                    reason = parser.nextText();
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                MUCItem mUCItem = new MUCItem(affiliation, role, actor, reason, jid, nick, actorNick);
                return mUCItem;
            }
        }
    }

    public static Destroy parseDestroy(XmlPullParser parser) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        EntityBareJid jid = ParserUtils.getBareJidAttribute(parser);
        String reason = null;
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                char c = 65535;
                if (name.hashCode() == -934964668 && name.equals(JingleReason.ELEMENT)) {
                    c = 0;
                }
                if (c == 0) {
                    reason = parser.nextText();
                }
            } else if (eventType == 3 && initialDepth == parser.getDepth()) {
                return new Destroy(jid, reason);
            }
        }
    }
}
