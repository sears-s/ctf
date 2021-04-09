package org.jivesoftware.smackx.blocking.element;

import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jxmpp.jid.Jid;

public class BlockContactsIQ extends IQ {
    public static final String ELEMENT = "block";
    public static final String NAMESPACE = "urn:xmpp:blocking";
    private final List<Jid> jids;

    public BlockContactsIQ(List<Jid> jids2) {
        super(ELEMENT, "urn:xmpp:blocking");
        setType(Type.set);
        this.jids = Collections.unmodifiableList(jids2);
    }

    public List<Jid> getJids() {
        return this.jids;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        List<Jid> list = this.jids;
        if (list != null) {
            for (Jid jid : list) {
                xml.halfOpenElement("item");
                xml.attribute("jid", (CharSequence) jid);
                xml.closeEmptyElement();
            }
        }
        return xml;
    }
}
