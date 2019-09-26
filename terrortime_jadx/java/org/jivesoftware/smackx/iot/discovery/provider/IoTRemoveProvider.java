package org.jivesoftware.smackx.iot.discovery.provider;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.iot.discovery.element.IoTRemove;
import org.jivesoftware.smackx.iot.parser.NodeInfoParser;
import org.jxmpp.jid.Jid;
import org.xmlpull.v1.XmlPullParser;

public class IoTRemoveProvider extends IQProvider<IoTRemove> {
    public IoTRemove parse(XmlPullParser parser, int initialDepth) throws Exception {
        Jid jid = ParserUtils.getJidAttribute(parser);
        if (!jid.hasResource()) {
            return new IoTRemove(jid.asBareJid(), NodeInfoParser.parse(parser));
        }
        throw new SmackException("JID must be without resourcepart");
    }
}
