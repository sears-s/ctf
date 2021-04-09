package org.jivesoftware.smack.filter.jidtype;

import org.jivesoftware.smack.filter.jidtype.AbstractJidTypeFilter.JidType;
import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.Jid;

public class FromJidTypeFilter extends AbstractJidTypeFilter {
    public FromJidTypeFilter(JidType jidType) {
        super(jidType);
    }

    /* access modifiers changed from: protected */
    public Jid getJidToMatchFrom(Stanza stanza) {
        return stanza.getFrom();
    }
}
