package org.jivesoftware.smackx.bytestreams.ibb.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.util.Locale;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager.StanzaType;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Open;
import org.jivesoftware.smackx.jingle.transports.jingle_ibb.element.JingleIBBTransport;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class OpenIQProvider extends IQProvider<Open> {
    public Open parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
        StanzaType stanza;
        String str = BuildConfig.FLAVOR;
        String sessionID = parser.getAttributeValue(str, "sid");
        int blockSize = Integer.parseInt(parser.getAttributeValue(str, JingleIBBTransport.ATTR_BLOCK_SIZE));
        String stanzaValue = parser.getAttributeValue(str, "stanza");
        if (stanzaValue == null) {
            stanza = StanzaType.IQ;
        } else {
            stanza = StanzaType.valueOf(stanzaValue.toUpperCase(Locale.US));
        }
        parser.next();
        return new Open(sessionID, blockSize, stanza);
    }
}
