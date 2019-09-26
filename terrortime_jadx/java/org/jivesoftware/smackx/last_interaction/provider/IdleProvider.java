package org.jivesoftware.smackx.last_interaction.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.last_interaction.element.IdleElement;
import org.jxmpp.util.XmppDateTime;
import org.xmlpull.v1.XmlPullParser;

public class IdleProvider extends ExtensionElementProvider<IdleElement> {
    public static final IdleProvider TEST_INSTANCE = new IdleProvider();

    public IdleElement parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new IdleElement(XmppDateTime.parseXEP0082Date(parser.getAttributeValue(null, IdleElement.ATTR_SINCE)));
    }
}
