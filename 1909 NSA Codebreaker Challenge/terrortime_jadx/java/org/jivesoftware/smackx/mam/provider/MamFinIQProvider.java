package org.jivesoftware.smackx.mam.provider;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.mam.element.MamFinIQ;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.jivesoftware.smackx.rsm.provider.RSMSetProvider;
import org.xmlpull.v1.XmlPullParser;

public class MamFinIQProvider extends IQProvider<MamFinIQ> {
    public MamFinIQ parse(XmlPullParser parser, int initialDepth) throws Exception {
        String queryId = parser.getAttributeValue(BuildConfig.FLAVOR, "queryid");
        boolean complete = ParserUtils.getBooleanAttribute(parser, "complete", false);
        boolean stable = ParserUtils.getBooleanAttribute(parser, "stable", true);
        RSMSet rsmSet = null;
        while (true) {
            int eventType = parser.next();
            if (eventType != 2) {
                if (eventType == 3 && parser.getDepth() == initialDepth) {
                    return new MamFinIQ(queryId, rsmSet, complete, stable);
                }
            } else if (parser.getName().equals("set")) {
                rsmSet = (RSMSet) RSMSetProvider.INSTANCE.parse(parser);
            }
        }
    }
}
