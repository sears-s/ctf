package org.jivesoftware.smackx.jingle.transports.jingle_ibb.provider;

import org.jivesoftware.smackx.jingle.provider.JingleContentTransportProvider;
import org.jivesoftware.smackx.jingle.transports.jingle_ibb.element.JingleIBBTransport;
import org.xmlpull.v1.XmlPullParser;

public class JingleIBBTransportProvider extends JingleContentTransportProvider<JingleIBBTransport> {
    public JingleIBBTransport parse(XmlPullParser parser, int initialDepth) throws Exception {
        String blockSizeString = parser.getAttributeValue(null, JingleIBBTransport.ATTR_BLOCK_SIZE);
        String sid = parser.getAttributeValue(null, "sid");
        short blockSize = -1;
        if (blockSizeString != null) {
            blockSize = Short.valueOf(blockSizeString).shortValue();
        }
        return new JingleIBBTransport(blockSize, sid);
    }
}
