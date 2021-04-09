package org.jivesoftware.smackx.muclight.provider;

import java.util.HashMap;
import org.jivesoftware.smack.packet.Message.Subject;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.muclight.element.MUCLightElements.ConfigurationsChangeExtension;
import org.xmlpull.v1.XmlPullParser;

public class MUCLightConfigurationsChangeProvider extends ExtensionElementProvider<ConfigurationsChangeExtension> {
    public ConfigurationsChangeExtension parse(XmlPullParser parser, int initialDepth) throws Exception {
        String prevVersion = null;
        String version = null;
        String roomName = null;
        String subject = null;
        HashMap hashMap = null;
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getName().equals("prev-version")) {
                    prevVersion = parser.nextText();
                } else if (parser.getName().equals("version")) {
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
                ConfigurationsChangeExtension configurationsChangeExtension = new ConfigurationsChangeExtension(prevVersion, version, roomName, subject, hashMap);
                return configurationsChangeExtension;
            }
        }
    }
}
