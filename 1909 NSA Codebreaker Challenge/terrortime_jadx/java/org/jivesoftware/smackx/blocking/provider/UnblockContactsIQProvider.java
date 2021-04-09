package org.jivesoftware.smackx.blocking.provider;

import com.badguy.terrortime.BuildConfig;
import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.blocking.element.UnblockContactsIQ;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.xmlpull.v1.XmlPullParser;

public class UnblockContactsIQProvider extends IQProvider<UnblockContactsIQ> {
    public UnblockContactsIQ parse(XmlPullParser parser, int initialDepth) throws Exception {
        List<Jid> jids = null;
        while (true) {
            int eventType = parser.next();
            if (eventType != 2) {
                if (eventType == 3 && parser.getDepth() == initialDepth) {
                    return new UnblockContactsIQ(jids);
                }
            } else if (parser.getName().equals("item")) {
                if (jids == null) {
                    jids = new ArrayList<>();
                }
                jids.add(JidCreate.from(parser.getAttributeValue(BuildConfig.FLAVOR, "jid")));
            }
        }
    }
}
