package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.StringUtils;

public class StanzaIdFilter implements StanzaFilter {
    private final String stanzaId;

    public StanzaIdFilter(Stanza stanza) {
        this(stanza.getStanzaId());
    }

    public StanzaIdFilter(String stanzaID) {
        this.stanzaId = (String) StringUtils.requireNotNullOrEmpty(stanzaID, "Stanza ID must not be null or empty.");
    }

    public boolean accept(Stanza stanza) {
        return this.stanzaId.equals(stanza.getStanzaId());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": id=");
        sb.append(this.stanzaId);
        return sb.toString();
    }
}
