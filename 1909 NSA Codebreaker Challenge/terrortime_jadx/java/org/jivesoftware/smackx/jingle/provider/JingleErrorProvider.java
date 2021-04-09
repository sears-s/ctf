package org.jivesoftware.smackx.jingle.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.jingle.element.JingleError;
import org.xmlpull.v1.XmlPullParser;

public class JingleErrorProvider extends ExtensionElementProvider<JingleError> {
    public JingleError parse(XmlPullParser parser, int initialDepth) throws Exception {
        return JingleError.fromString(parser.getName());
    }
}
