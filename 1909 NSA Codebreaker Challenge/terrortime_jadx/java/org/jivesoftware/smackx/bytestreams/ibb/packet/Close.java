package org.jivesoftware.smackx.bytestreams.ibb.packet;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;

public class Close extends IQ {
    public static final String ELEMENT = "close";
    public static final String NAMESPACE = "http://jabber.org/protocol/ibb";
    private final String sessionID;

    public Close(String sessionID2) {
        super(ELEMENT, "http://jabber.org/protocol/ibb");
        if (sessionID2 == null || BuildConfig.FLAVOR.equals(sessionID2)) {
            throw new IllegalArgumentException("Session ID must not be null or empty");
        }
        this.sessionID = sessionID2;
        setType(Type.set);
    }

    public String getSessionID() {
        return this.sessionID;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("sid", this.sessionID);
        xml.setEmptyElement();
        return xml;
    }
}
