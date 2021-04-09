package org.jivesoftware.smackx.bytestreams.socks5.provider;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.Activate;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.Mode;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHost;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHostUsed;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransport;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportCandidate;
import org.jxmpp.jid.Jid;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class BytestreamsProvider extends IQProvider<Bytestream> {
    public Bytestream parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
        boolean done = false;
        Bytestream toReturn = new Bytestream();
        String str = BuildConfig.FLAVOR;
        String id = parser.getAttributeValue(str, "sid");
        String mode = parser.getAttributeValue(str, JingleS5BTransport.ATTR_MODE);
        Jid JID = null;
        String host = null;
        String port = null;
        while (!done) {
            int eventType = parser.next();
            String elementName = parser.getName();
            if (eventType == 2) {
                if (elementName.equals(StreamHost.ELEMENTNAME)) {
                    JID = ParserUtils.getJidAttribute(parser);
                    host = parser.getAttributeValue(str, JingleS5BTransportCandidate.ATTR_HOST);
                    port = parser.getAttributeValue(str, JingleS5BTransportCandidate.ATTR_PORT);
                } else if (elementName.equals(StreamHostUsed.ELEMENTNAME)) {
                    toReturn.setUsedHost(ParserUtils.getJidAttribute(parser));
                } else if (elementName.equals(Activate.ELEMENTNAME)) {
                    toReturn.setToActivate(ParserUtils.getJidAttribute(parser));
                }
            } else if (eventType == 3) {
                if (elementName.equals("streamhost")) {
                    if (port == null) {
                        toReturn.addStreamHost(JID, host);
                    } else {
                        toReturn.addStreamHost(JID, host, Integer.parseInt(port));
                    }
                    JID = null;
                    host = null;
                    port = null;
                } else if (elementName.equals("query")) {
                    done = true;
                }
            }
        }
        if (mode == null) {
            toReturn.setMode(Mode.tcp);
        } else {
            toReturn.setMode(Mode.fromName(mode));
        }
        toReturn.setSessionID(id);
        return toReturn;
    }
}
