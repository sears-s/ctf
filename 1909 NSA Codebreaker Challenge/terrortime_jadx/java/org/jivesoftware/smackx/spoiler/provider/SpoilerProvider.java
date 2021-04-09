package org.jivesoftware.smackx.spoiler.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.spoiler.element.SpoilerElement;
import org.xmlpull.v1.XmlPullParser;

public class SpoilerProvider extends ExtensionElementProvider<SpoilerElement> {
    public static SpoilerProvider INSTANCE = new SpoilerProvider();

    public SpoilerElement parse(XmlPullParser parser, int initialDepth) throws Exception {
        String lang = ParserUtils.getXmlLang(parser);
        String hint = null;
        while (true) {
            int tag = parser.next();
            if (tag == 3) {
                return new SpoilerElement(lang, hint);
            }
            if (tag == 4) {
                hint = parser.getText();
            }
        }
    }
}
