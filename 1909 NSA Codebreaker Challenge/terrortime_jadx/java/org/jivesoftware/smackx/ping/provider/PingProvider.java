package org.jivesoftware.smackx.ping.provider;

import java.io.IOException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.ping.packet.Ping;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class PingProvider extends IQProvider<Ping> {
    public Ping parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
        return new Ping();
    }
}
