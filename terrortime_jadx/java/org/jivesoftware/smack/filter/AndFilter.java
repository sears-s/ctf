package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Stanza;

public class AndFilter extends AbstractListFilter implements StanzaFilter {
    public AndFilter() {
    }

    public AndFilter(StanzaFilter... filters) {
        super(filters);
    }

    public boolean accept(Stanza packet) {
        for (StanzaFilter filter : this.filters) {
            if (!filter.accept(packet)) {
                return false;
            }
        }
        return true;
    }
}
