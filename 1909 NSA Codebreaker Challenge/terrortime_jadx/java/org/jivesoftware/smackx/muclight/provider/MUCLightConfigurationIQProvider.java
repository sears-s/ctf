package org.jivesoftware.smackx.muclight.provider;

import java.util.HashMap;
import org.jivesoftware.smack.packet.Message.Subject;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.muclight.MUCLightRoomConfiguration;
import org.jivesoftware.smackx.muclight.element.MUCLightConfigurationIQ;
import org.xmlpull.v1.XmlPullParser;

public class MUCLightConfigurationIQProvider extends IQProvider<MUCLightConfigurationIQ> {
    public MUCLightConfigurationIQ parse(XmlPullParser parser, int initialDepth) throws Exception {
        String version = null;
        String roomName = null;
        String subject = null;
        HashMap hashMap = null;
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getName().equals("version")) {
                    version = parser.nextText();
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
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new MUCLightConfigurationIQ(version, new MUCLightRoomConfiguration(roomName, subject, hashMap));
            }
        }
    }
}
