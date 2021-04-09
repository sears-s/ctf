package org.jivesoftware.smackx.muclight.element;

import java.util.HashMap;
import java.util.Map.Entry;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smackx.muclight.MUCLightAffiliation;
import org.jivesoftware.smackx.muclight.element.MUCLightElements.UserWithAffiliationElement;
import org.jxmpp.jid.Jid;

public class MUCLightAffiliationsIQ extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "urn:xmpp:muclight:0#affiliations";
    private HashMap<Jid, MUCLightAffiliation> affiliations;
    private final String version;

    public MUCLightAffiliationsIQ(String version2, HashMap<Jid, MUCLightAffiliation> affiliations2) {
        super("query", "urn:xmpp:muclight:0#affiliations");
        this.version = version2;
        this.affiliations = affiliations2;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.optElement("version", this.version);
        for (Entry<Jid, MUCLightAffiliation> pair : this.affiliations.entrySet()) {
            xml.element(new UserWithAffiliationElement((Jid) pair.getKey(), (MUCLightAffiliation) pair.getValue()));
        }
        return xml;
    }

    public String getVersion() {
        return this.version;
    }

    public HashMap<Jid, MUCLightAffiliation> getAffiliations() {
        return this.affiliations;
    }
}
