package org.jivesoftware.smackx.blocking.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jxmpp.jid.Jid;

public class BlockListIQ extends IQ {
    public static final String ELEMENT = "blocklist";
    public static final String NAMESPACE = "urn:xmpp:blocking";
    private final List<Jid> jids;

    public BlockListIQ(List<Jid> jids2) {
        super(ELEMENT, "urn:xmpp:blocking");
        if (jids2 == null) {
            this.jids = Collections.emptyList();
        } else {
            this.jids = Collections.unmodifiableList(jids2);
        }
    }

    public BlockListIQ() {
        this(null);
    }

    public List<Jid> getBlockedJids() {
        return this.jids;
    }

    public List<Jid> getBlockedJidsCopy() {
        return new ArrayList(getBlockedJids());
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        if (this.jids.isEmpty()) {
            xml.setEmptyElement();
        } else {
            xml.rightAngleBracket();
            for (Jid jid : this.jids) {
                xml.halfOpenElement("item");
                xml.attribute("jid", (CharSequence) jid);
                xml.closeEmptyElement();
            }
        }
        return xml;
    }
}
