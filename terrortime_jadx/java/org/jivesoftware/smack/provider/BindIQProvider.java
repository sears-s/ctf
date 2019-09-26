package org.jivesoftware.smack.provider;

import java.io.IOException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Bind;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class BindIQProvider extends IQProvider<Bind> {
    public Bind parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        Bind bind = null;
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                char c = 65535;
                int hashCode = name.hashCode();
                if (hashCode != -341064690) {
                    if (hashCode == 105221 && name.equals("jid")) {
                        c = 1;
                    }
                } else if (name.equals("resource")) {
                    c = 0;
                }
                if (c == 0) {
                    bind = Bind.newSet(Resourcepart.from(parser.nextText()));
                } else if (c == 1) {
                    bind = Bind.newResult(JidCreate.entityFullFrom(parser.nextText()));
                }
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                return bind;
            }
        }
    }
}
