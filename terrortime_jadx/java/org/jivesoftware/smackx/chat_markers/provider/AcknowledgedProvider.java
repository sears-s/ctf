package org.jivesoftware.smackx.chat_markers.provider;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.chat_markers.element.ChatMarkersElements.AcknowledgedExtension;
import org.xmlpull.v1.XmlPullParser;

public class AcknowledgedProvider extends ExtensionElementProvider<AcknowledgedExtension> {
    public AcknowledgedExtension parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new AcknowledgedExtension(parser.getAttributeValue(BuildConfig.FLAVOR, "id"));
    }
}
