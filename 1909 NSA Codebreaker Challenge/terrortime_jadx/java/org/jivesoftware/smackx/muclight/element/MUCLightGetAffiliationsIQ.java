package org.jivesoftware.smackx.muclight.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jxmpp.jid.Jid;

public class MUCLightGetAffiliationsIQ extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "urn:xmpp:muclight:0#affiliations";
    private String version;

    public MUCLightGetAffiliationsIQ(Jid roomJid, String version2) {
        super("query", "urn:xmpp:muclight:0#affiliations");
        this.version = version2;
        setType(Type.get);
        setTo(roomJid);
    }

    public MUCLightGetAffiliationsIQ(Jid roomJid) {
        this(roomJid, null);
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.optElement("version", this.version);
        return xml;
    }
}
