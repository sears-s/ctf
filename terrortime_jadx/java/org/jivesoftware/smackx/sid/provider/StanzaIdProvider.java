package org.jivesoftware.smackx.sid.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.sid.element.StanzaIdElement;
import org.xmlpull.v1.XmlPullParser;

public class StanzaIdProvider extends ExtensionElementProvider<StanzaIdElement> {
    public static final StanzaIdProvider INSTANCE = new StanzaIdProvider();
    @Deprecated
    public static final StanzaIdProvider TEST_INSTANCE = INSTANCE;

    public StanzaIdElement parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new StanzaIdElement(parser.getAttributeValue(null, "id"), parser.getAttributeValue(null, StanzaIdElement.ATTR_BY));
    }
}
