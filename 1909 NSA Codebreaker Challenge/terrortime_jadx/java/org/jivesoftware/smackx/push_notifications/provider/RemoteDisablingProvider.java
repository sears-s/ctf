package org.jivesoftware.smackx.push_notifications.provider;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.push_notifications.element.PushNotificationsElements.RemoteDisablingExtension;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.xmlpull.v1.XmlPullParser;

public class RemoteDisablingProvider extends ExtensionElementProvider<RemoteDisablingExtension> {
    public RemoteDisablingExtension parse(XmlPullParser parser, int initialDepth) throws Exception {
        Jid userJid = null;
        String str = BuildConfig.FLAVOR;
        String node = parser.getAttributeValue(str, NodeElement.ELEMENT);
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                String str2 = Affiliation.ELEMENT;
                if (name.equals(str2)) {
                    userJid = JidCreate.from(parser.getAttributeValue(str, "jid"));
                    String affiliation = parser.getAttributeValue(str, str2);
                    if (affiliation == null || !affiliation.equals(PrivacyItem.SUBSCRIPTION_NONE)) {
                    }
                } else {
                    continue;
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new RemoteDisablingExtension(node, userJid);
            }
        }
        return null;
    }
}
