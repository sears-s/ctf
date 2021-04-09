package org.jivesoftware.smackx.mam.provider;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jivesoftware.smackx.forward.provider.ForwardedProvider;
import org.jivesoftware.smackx.mam.element.MamElements.MamResultExtension;
import org.xmlpull.v1.XmlPullParser;

public class MamResultProvider extends ExtensionElementProvider<MamResultExtension> {
    public MamResultExtension parse(XmlPullParser parser, int initialDepth) throws Exception {
        Forwarded forwarded = null;
        String str = BuildConfig.FLAVOR;
        String queryId = parser.getAttributeValue(str, "queryid");
        String id = parser.getAttributeValue(str, "id");
        while (true) {
            int eventType = parser.next();
            String name = parser.getName();
            if (eventType == 2) {
                char c = 65535;
                if (name.hashCode() == 2097807908 && name.equals(Forwarded.ELEMENT)) {
                    c = 0;
                }
                if (c == 0) {
                    forwarded = (Forwarded) ForwardedProvider.INSTANCE.parse(parser);
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new MamResultExtension(queryId, id, forwarded);
            }
        }
    }
}
