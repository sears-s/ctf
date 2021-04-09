package org.jivesoftware.smack.provider;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.util.ParserUtils;
import org.xmlpull.v1.XmlPullParser;

public abstract class Provider<E extends Element> {
    public abstract E parse(XmlPullParser xmlPullParser, int i) throws Exception;

    public final E parse(XmlPullParser parser) throws Exception {
        ParserUtils.assertAtStartTag(parser);
        int initialDepth = parser.getDepth();
        E e = parse(parser, initialDepth);
        ParserUtils.forwardToEndTagOfDepth(parser, initialDepth);
        return e;
    }
}
