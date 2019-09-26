package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.Jid;

public abstract class AbstractFromToMatchesFilter implements StanzaFilter {
    private final Jid address;
    private final boolean ignoreResourcepart;

    /* access modifiers changed from: protected */
    public abstract Jid getAddressToCompare(Stanza stanza);

    protected AbstractFromToMatchesFilter(Jid address2, boolean ignoreResourcepart2) {
        if (address2 == null || !ignoreResourcepart2) {
            this.address = address2;
        } else {
            this.address = address2.asBareJid();
        }
        this.ignoreResourcepart = ignoreResourcepart2;
    }

    public final boolean accept(Stanza stanza) {
        Jid stanzaAddress = getAddressToCompare(stanza);
        if (stanzaAddress == null) {
            return this.address == null;
        }
        if (this.ignoreResourcepart) {
            stanzaAddress = stanzaAddress.asBareJid();
        }
        return stanzaAddress.equals((CharSequence) this.address);
    }

    public final String toString() {
        String matchMode = this.ignoreResourcepart ? "ignoreResourcepart" : "full";
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" (");
        sb.append(matchMode);
        sb.append("): ");
        sb.append(this.address);
        return sb.toString();
    }
}
