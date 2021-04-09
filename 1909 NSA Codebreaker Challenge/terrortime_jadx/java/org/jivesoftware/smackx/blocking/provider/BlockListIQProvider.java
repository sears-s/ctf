package org.jivesoftware.smackx.blocking.provider;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.blocking.element.BlockListIQ;
import org.jxmpp.jid.Jid;
import org.xmlpull.v1.XmlPullParser;

public class BlockListIQProvider extends IQProvider<BlockListIQ> {
    public BlockListIQ parse(XmlPullParser parser, int initialDepth) throws Exception {
        List<Jid> jids = null;
        while (true) {
            int eventType = parser.next();
            if (eventType != 2) {
                if (eventType == 3 && parser.getDepth() == initialDepth) {
                    BlockListIQ blockListIQ = new BlockListIQ(jids);
                    blockListIQ.setType(Type.result);
                    return blockListIQ;
                }
            } else if (parser.getName().equals("item")) {
                if (jids == null) {
                    jids = new ArrayList<>();
                }
                jids.add(ParserUtils.getJidAttribute(parser));
            }
        }
    }
}
