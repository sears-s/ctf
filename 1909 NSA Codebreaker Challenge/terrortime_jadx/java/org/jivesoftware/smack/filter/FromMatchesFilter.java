package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.Jid;

public final class FromMatchesFilter extends AbstractFromToMatchesFilter {
    public static final FromMatchesFilter MATCH_NO_FROM_SET = create(null);

    public FromMatchesFilter(Jid address, boolean ignoreResourcepart) {
        super(address, ignoreResourcepart);
    }

    public static FromMatchesFilter create(Jid address) {
        return new FromMatchesFilter(address, address != null ? address.hasNoResource() : false);
    }

    public static FromMatchesFilter createBare(Jid address) {
        return new FromMatchesFilter(address, true);
    }

    public static FromMatchesFilter createFull(Jid address) {
        return new FromMatchesFilter(address, false);
    }

    /* access modifiers changed from: protected */
    public Jid getAddressToCompare(Stanza stanza) {
        return stanza.getFrom();
    }
}
