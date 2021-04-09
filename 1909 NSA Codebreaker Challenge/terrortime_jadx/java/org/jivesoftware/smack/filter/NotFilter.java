package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.Objects;

public class NotFilter implements StanzaFilter {
    private final StanzaFilter filter;

    public NotFilter(StanzaFilter filter2) {
        this.filter = (StanzaFilter) Objects.requireNonNull(filter2, "Parameter must not be null.");
    }

    public boolean accept(Stanza packet) {
        return !this.filter.accept(packet);
    }
}
