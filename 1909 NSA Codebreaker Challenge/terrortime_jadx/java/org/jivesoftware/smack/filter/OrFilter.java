package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Stanza;

public class OrFilter extends AbstractListFilter implements StanzaFilter {
    public OrFilter() {
    }

    public OrFilter(StanzaFilter... filters) {
        super(filters);
    }

    public boolean accept(Stanza packet) {
        for (StanzaFilter filter : this.filters) {
            if (filter.accept(packet)) {
                return true;
            }
        }
        return false;
    }
}
