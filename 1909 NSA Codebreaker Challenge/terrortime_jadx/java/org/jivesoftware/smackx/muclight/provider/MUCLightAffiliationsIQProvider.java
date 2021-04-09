package org.jivesoftware.smackx.muclight.provider;

import com.badguy.terrortime.BuildConfig;
import java.util.HashMap;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.muclight.MUCLightAffiliation;
import org.jivesoftware.smackx.muclight.element.MUCLightAffiliationsIQ;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.xmlpull.v1.XmlPullParser;

public class MUCLightAffiliationsIQProvider extends IQProvider<MUCLightAffiliationsIQ> {
    public MUCLightAffiliationsIQ parse(XmlPullParser parser, int initialDepth) throws Exception {
        String version = null;
        HashMap<Jid, MUCLightAffiliation> occupants = new HashMap<>();
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                if (parser.getName().equals("version")) {
                    version = parser.nextText();
                }
                if (parser.getName().equals("user")) {
                    occupants.put(JidCreate.from(parser.nextText()), MUCLightAffiliation.fromString(parser.getAttributeValue(BuildConfig.FLAVOR, Affiliation.ELEMENT)));
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new MUCLightAffiliationsIQ(version, occupants);
            }
        }
    }
}
