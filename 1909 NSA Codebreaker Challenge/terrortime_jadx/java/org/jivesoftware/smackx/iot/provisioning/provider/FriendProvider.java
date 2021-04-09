package org.jivesoftware.smackx.iot.provisioning.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.iot.provisioning.element.Friend;
import org.jxmpp.stringprep.XmppStringprepException;
import org.xmlpull.v1.XmlPullParser;

public class FriendProvider extends ExtensionElementProvider<Friend> {
    public Friend parse(XmlPullParser parser, int initialDepth) throws XmppStringprepException {
        return new Friend(ParserUtils.getBareJidAttribute(parser));
    }
}
