package org.jivesoftware.smackx.muclight.element;

import java.util.HashMap;
import java.util.Map.Entry;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.muclight.MUCLightAffiliation;
import org.jivesoftware.smackx.muclight.element.MUCLightElements.UserWithAffiliationElement;
import org.jxmpp.jid.Jid;

public class MUCLightChangeAffiliationsIQ extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "urn:xmpp:muclight:0#affiliations";
    private HashMap<Jid, MUCLightAffiliation> affiliations;

    public MUCLightChangeAffiliationsIQ(Jid room, HashMap<Jid, MUCLightAffiliation> affiliations2) {
        super("query", "urn:xmpp:muclight:0#affiliations");
        setType(Type.set);
        setTo(room);
        this.affiliations = affiliations2;
    }

    public HashMap<Jid, MUCLightAffiliation> getAffiliations() {
        return this.affiliations;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        HashMap<Jid, MUCLightAffiliation> hashMap = this.affiliations;
        if (hashMap != null) {
            for (Entry<Jid, MUCLightAffiliation> pair : hashMap.entrySet()) {
                xml.element(new UserWithAffiliationElement((Jid) pair.getKey(), (MUCLightAffiliation) pair.getValue()));
            }
        }
        return xml;
    }
}
