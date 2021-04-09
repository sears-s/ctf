package org.jivesoftware.smackx.jingle.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.jingle.element.JingleContentTransport;
import org.xmlpull.v1.XmlPullParser;

public abstract class JingleContentTransportProvider<T extends JingleContentTransport> extends ExtensionElementProvider<T> {
    public abstract T parse(XmlPullParser xmlPullParser, int i) throws Exception;
}
