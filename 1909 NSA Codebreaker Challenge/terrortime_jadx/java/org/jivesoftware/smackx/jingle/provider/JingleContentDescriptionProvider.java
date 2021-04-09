package org.jivesoftware.smackx.jingle.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.jingle.element.JingleContentDescription;
import org.xmlpull.v1.XmlPullParser;

public abstract class JingleContentDescriptionProvider<D extends JingleContentDescription> extends ExtensionElementProvider<D> {
    public abstract D parse(XmlPullParser xmlPullParser, int i) throws Exception;
}
