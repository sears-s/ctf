package org.jivesoftware.smackx.iqprivate.provider;

import java.io.IOException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.iqprivate.packet.PrivateData;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public interface PrivateDataProvider {
    PrivateData parsePrivateData(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, SmackException;
}
