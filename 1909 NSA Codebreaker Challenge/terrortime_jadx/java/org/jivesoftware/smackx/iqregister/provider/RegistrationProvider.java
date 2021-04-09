package org.jivesoftware.smackx.iqregister.provider;

import com.badguy.terrortime.BuildConfig;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.xmlpull.v1.XmlPullParser;

public class RegistrationProvider extends IQProvider<Registration> {
    public Registration parse(XmlPullParser parser, int initialDepth) throws Exception {
        String instruction = null;
        Map<String, String> fields = new HashMap<>();
        List<ExtensionElement> packetExtensions = new LinkedList<>();
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getNamespace().equals(Registration.NAMESPACE)) {
                    String name = parser.getName();
                    String value = BuildConfig.FLAVOR;
                    if (parser.next() == 4) {
                        value = parser.getText();
                    }
                    if (!name.equals("instructions")) {
                        fields.put(name, value);
                    } else {
                        instruction = value;
                    }
                } else {
                    PacketParserUtils.addExtensionElement((Collection<ExtensionElement>) packetExtensions, parser);
                }
            } else if (eventType == 3 && parser.getName().equals("query")) {
                Registration registration = new Registration(instruction, fields);
                registration.addExtensions(packetExtensions);
                return registration;
            }
        }
    }
}
