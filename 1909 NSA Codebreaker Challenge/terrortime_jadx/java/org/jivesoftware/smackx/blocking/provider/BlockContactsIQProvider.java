package org.jivesoftware.smackx.blocking.provider;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.blocking.element.BlockContactsIQ;
import org.jxmpp.jid.Jid;
import org.xmlpull.v1.XmlPullParser;

public class BlockContactsIQProvider extends IQProvider<BlockContactsIQ> {
    public BlockContactsIQ parse(XmlPullParser parser, int initialDepth) throws Exception {
        List<Jid> jids = new ArrayList<>();
        while (true) {
            int eventType = parser.next();
            if (eventType != 2) {
                if (eventType == 3 && parser.getDepth() == initialDepth) {
                    return new BlockContactsIQ(jids);
                }
            } else if (parser.getName().equals("item")) {
                jids.add(ParserUtils.getJidAttribute(parser));
            }
        }
    }
}
