package org.jivesoftware.smackx.bytestreams.ibb.packet;

import com.badguy.terrortime.BuildConfig;
import java.util.Locale;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager.StanzaType;
import org.jivesoftware.smackx.jingle.transports.jingle_ibb.element.JingleIBBTransport;

public class Open extends IQ {
    public static final String ELEMENT = "open";
    public static final String NAMESPACE = "http://jabber.org/protocol/ibb";
    private final int blockSize;
    private final String sessionID;
    private final StanzaType stanza;

    public Open(String sessionID2, int blockSize2, StanzaType stanza2) {
        super("open", "http://jabber.org/protocol/ibb");
        if (sessionID2 == null || BuildConfig.FLAVOR.equals(sessionID2)) {
            throw new IllegalArgumentException("Session ID must not be null or empty");
        } else if (blockSize2 > 0) {
            this.sessionID = sessionID2;
            this.blockSize = blockSize2;
            this.stanza = stanza2;
            setType(Type.set);
        } else {
            throw new IllegalArgumentException("Block size must be greater than zero");
        }
    }

    public Open(String sessionID2, int blockSize2) {
        this(sessionID2, blockSize2, StanzaType.IQ);
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    public StanzaType getStanza() {
        return this.stanza;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute(JingleIBBTransport.ATTR_BLOCK_SIZE, Integer.toString(this.blockSize));
        xml.attribute("sid", this.sessionID);
        xml.attribute("stanza", this.stanza.toString().toLowerCase(Locale.US));
        xml.setEmptyElement();
        return xml;
    }
}
