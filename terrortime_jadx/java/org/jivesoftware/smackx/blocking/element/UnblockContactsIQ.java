package org.jivesoftware.smackx.blocking.element;

import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jxmpp.jid.Jid;

public class UnblockContactsIQ extends IQ {
    public static final String ELEMENT = "unblock";
    public static final String NAMESPACE = "urn:xmpp:blocking";
    private final List<Jid> jids;

    public UnblockContactsIQ(List<Jid> jids2) {
        super(ELEMENT, "urn:xmpp:blocking");
        setType(Type.set);
        if (jids2 != null) {
            this.jids = Collections.unmodifiableList(jids2);
        } else {
            this.jids = null;
        }
    }

    public UnblockContactsIQ() {
        this(null);
    }

    public List<Jid> getJids() {
        return this.jids;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        if (this.jids == null) {
            xml.setEmptyElement();
            return xml;
        }
        xml.rightAngleBracket();
        for (Jid jid : this.jids) {
            xml.halfOpenElement("item");
            xml.attribute("jid", (CharSequence) jid);
            xml.closeEmptyElement();
        }
        return xml;
    }
}
