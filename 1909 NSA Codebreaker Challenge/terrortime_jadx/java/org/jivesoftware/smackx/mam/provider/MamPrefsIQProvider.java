package org.jivesoftware.smackx.mam.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.mam.element.MamElements.MamResultExtension;
import org.jivesoftware.smackx.mam.element.MamPrefsIQ;
import org.jivesoftware.smackx.mam.element.MamPrefsIQ.DefaultBehavior;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MamPrefsIQProvider extends IQProvider<MamPrefsIQ> {
    public MamPrefsIQ parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
        String str = BuildConfig.FLAVOR;
        String iqType = parser.getAttributeValue(str, "type");
        String defaultBehaviorString = parser.getAttributeValue(str, "default");
        DefaultBehavior defaultBehavior = null;
        if (defaultBehaviorString != null) {
            defaultBehavior = DefaultBehavior.valueOf(defaultBehaviorString);
        }
        if (iqType == null) {
            String iqType2 = MamResultExtension.ELEMENT;
        }
        List<Jid> alwaysJids = null;
        List<Jid> neverJids = null;
        while (true) {
            int eventType = parser.next();
            String name = parser.getName();
            if (eventType == 2) {
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != -1414557169) {
                    if (hashCode == 104712844 && name.equals("never")) {
                        c = 1;
                    }
                } else if (name.equals("always")) {
                    c = 0;
                }
                if (c == 0) {
                    alwaysJids = iterateJids(parser);
                } else if (c == 1) {
                    neverJids = iterateJids(parser);
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return new MamPrefsIQ(alwaysJids, neverJids, defaultBehavior);
            }
        }
    }

    private static List<Jid> iterateJids(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Jid> jids = new ArrayList<>();
        int initialDepth = parser.getDepth();
        while (true) {
            int eventType = parser.next();
            String name = parser.getName();
            if (eventType == 2) {
                char c = 65535;
                if (name.hashCode() == 105221 && name.equals("jid")) {
                    c = 0;
                }
                if (c == 0) {
                    parser.next();
                    jids.add(JidCreate.from(parser.getText()));
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return jids;
            }
        }
    }
}
