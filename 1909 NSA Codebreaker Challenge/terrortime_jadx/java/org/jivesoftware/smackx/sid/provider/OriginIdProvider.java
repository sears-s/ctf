package org.jivesoftware.smackx.sid.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.sid.element.OriginIdElement;
import org.xmlpull.v1.XmlPullParser;

public class OriginIdProvider extends ExtensionElementProvider<OriginIdElement> {
    public static final OriginIdProvider INSTANCE = new OriginIdProvider();
    @Deprecated
    public static final OriginIdProvider TEST_INSTANCE = INSTANCE;

    public OriginIdElement parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new OriginIdElement(parser.getAttributeValue(null, "id"));
    }
}
