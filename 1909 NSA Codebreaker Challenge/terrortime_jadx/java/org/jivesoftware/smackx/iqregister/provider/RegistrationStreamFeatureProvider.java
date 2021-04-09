package org.jivesoftware.smackx.iqregister.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.iqregister.packet.Registration.Feature;
import org.xmlpull.v1.XmlPullParser;

public class RegistrationStreamFeatureProvider extends ExtensionElementProvider<Feature> {
    public Feature parse(XmlPullParser parser, int initialDepth) {
        return Feature.INSTANCE;
    }
}
