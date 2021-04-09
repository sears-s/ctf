package org.jivesoftware.smack.sm.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.sm.packet.StreamManagement.StreamManagementFeature;
import org.xmlpull.v1.XmlPullParser;

public class StreamManagementStreamFeatureProvider extends ExtensionElementProvider<StreamManagementFeature> {
    public StreamManagementFeature parse(XmlPullParser parser, int initialDepth) {
        return StreamManagementFeature.INSTANCE;
    }
}
