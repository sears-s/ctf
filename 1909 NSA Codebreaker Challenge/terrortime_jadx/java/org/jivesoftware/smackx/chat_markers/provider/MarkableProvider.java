package org.jivesoftware.smackx.chat_markers.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.chat_markers.element.ChatMarkersElements.MarkableExtension;
import org.xmlpull.v1.XmlPullParser;

public class MarkableProvider extends ExtensionElementProvider<MarkableExtension> {
    public MarkableExtension parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new MarkableExtension();
    }
}
