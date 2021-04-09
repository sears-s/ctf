package org.jivesoftware.smackx.bytestreams.ibb.packet;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smack.util.stringencoder.Base64;

public class DataPacketExtension implements ExtensionElement {
    public static final String ELEMENT = "data";
    public static final String NAMESPACE = "http://jabber.org/protocol/ibb";
    private final String data;
    private byte[] decodedData;
    private final long seq;
    private final String sessionID;

    public DataPacketExtension(String sessionID2, long seq2, String data2) {
        if (sessionID2 == null || BuildConfig.FLAVOR.equals(sessionID2)) {
            throw new IllegalArgumentException("Session ID must not be null or empty");
        } else if (seq2 < 0 || seq2 > 65535) {
            throw new IllegalArgumentException("Sequence must not be between 0 and 65535");
        } else if (data2 != null) {
            this.sessionID = sessionID2;
            this.seq = seq2;
            this.data = data2;
        } else {
            throw new IllegalArgumentException("Data must not be null");
        }
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public long getSeq() {
        return this.seq;
    }

    public String getData() {
        return this.data;
    }

    public byte[] getDecodedData() {
        byte[] bArr = this.decodedData;
        if (bArr != null) {
            return bArr;
        }
        if (this.data.matches(".*={1,2}+.+")) {
            return null;
        }
        this.decodedData = Base64.decode(this.data);
        return this.decodedData;
    }

    public String getElementName() {
        return "data";
    }

    public String getNamespace() {
        return "http://jabber.org/protocol/ibb";
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = getIQChildElementBuilder(new IQChildElementXmlStringBuilder((ExtensionElement) this));
        xml.closeElement((NamedElement) this);
        return xml;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("seq", Long.toString(this.seq));
        xml.attribute("sid", this.sessionID);
        xml.rightAngleBracket();
        xml.append((CharSequence) this.data);
        return xml;
    }
}
