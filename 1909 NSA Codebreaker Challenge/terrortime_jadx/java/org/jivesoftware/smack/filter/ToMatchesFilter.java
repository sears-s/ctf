package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.Jid;

public final class ToMatchesFilter extends AbstractFromToMatchesFilter {
    public static final ToMatchesFilter MATCH_NO_TO_SET = create(null);

    public ToMatchesFilter(Jid address, boolean ignoreResourcepart) {
        super(address, ignoreResourcepart);
    }

    public static ToMatchesFilter create(Jid address) {
        return new ToMatchesFilter(address, address != null ? address.hasNoResource() : false);
    }

    public static ToMatchesFilter createBare(Jid address) {
        return new ToMatchesFilter(address, true);
    }

    public static ToMatchesFilter createFull(Jid address) {
        return new ToMatchesFilter(address, false);
    }

    /* access modifiers changed from: protected */
    public Jid getAddressToCompare(Stanza stanza) {
        return stanza.getTo();
    }
}
