package org.jivesoftware.smackx.chat_markers.provider;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.chat_markers.element.ChatMarkersElements.DisplayedExtension;
import org.xmlpull.v1.XmlPullParser;

public class DisplayedProvider extends ExtensionElementProvider<DisplayedExtension> {
    public DisplayedExtension parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new DisplayedExtension(parser.getAttributeValue(BuildConfig.FLAVOR, "id"));
    }
}
