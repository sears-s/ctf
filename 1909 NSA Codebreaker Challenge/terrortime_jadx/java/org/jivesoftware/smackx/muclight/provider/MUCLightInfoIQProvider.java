package org.jivesoftware.smackx.muclight.provider;

import com.badguy.terrortime.BuildConfig;
import java.util.HashMap;
import org.jivesoftware.smack.packet.Message.Subject;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.muclight.MUCLightAffiliation;
import org.jivesoftware.smackx.muclight.MUCLightRoomConfiguration;
import org.jivesoftware.smackx.muclight.element.MUCLightInfoIQ;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.xmlpull.v1.XmlPullParser;

public class MUCLightInfoIQProvider extends IQProvider<MUCLightInfoIQ> {
    public MUCLightInfoIQ parse(XmlPullParser parser, int initialDepth) throws Exception {
        String version = null;
        String roomName = null;
        String subject = null;
        HashMap hashMap = null;
        HashMap<Jid, MUCLightAffiliation> occupants = new HashMap<>();
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getName().equals("version")) {
                    version = parser.nextText();
                }
                if (parser.getName().equals("configuration")) {
                    int depth = parser.getDepth();
                    while (true) {
                        int eventType2 = parser.next();
                        if (eventType2 != 2) {
                            if (eventType2 == 3 && parser.getDepth() == depth) {
                                break;
                            }
                        } else if (parser.getName().equals("roomname")) {
                            roomName = parser.nextText();
                        } else if (parser.getName().equals(Subject.ELEMENT)) {
                            subject = parser.nextText();
                        } else {
                            if (hashMap == null) {
                                hashMap = new HashMap();
                            }
                            hashMap.put(parser.getName(), parser.nextText());
                        }
                    }
                }
                if (parser.getName().equals("occupants")) {
                    occupants = iterateOccupants(parser);
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new MUCLightInfoIQ(version, new MUCLightRoomConfiguration(roomName, subject, hashMap), occupants);
            }
        }
    }

    private static HashMap<Jid, MUCLightAffiliation> iterateOccupants(XmlPullParser parser) throws Exception {
        HashMap<Jid, MUCLightAffiliation> occupants = new HashMap<>();
        int depth = parser.getDepth();
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getName().equals("user")) {
                    occupants.put(JidCreate.from(parser.nextText()), MUCLightAffiliation.fromString(parser.getAttributeValue(BuildConfig.FLAVOR, Affiliation.ELEMENT)));
                }
            } else if (eventType == 3 && parser.getDepth() == depth) {
                return occupants;
            }
        }
    }
}
