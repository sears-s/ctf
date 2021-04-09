package org.jivesoftware.smackx.chat_markers.provider;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.chat_markers.element.ChatMarkersElements.ReceivedExtension;
import org.xmlpull.v1.XmlPullParser;

public class ReceivedProvider extends ExtensionElementProvider<ReceivedExtension> {
    public ReceivedExtension parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new ReceivedExtension(parser.getAttributeValue(BuildConfig.FLAVOR, "id"));
    }
}
