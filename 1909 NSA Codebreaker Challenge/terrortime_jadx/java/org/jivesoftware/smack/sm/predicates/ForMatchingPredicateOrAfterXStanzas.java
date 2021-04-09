package org.jivesoftware.smack.sm.predicates;

import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;

public class ForMatchingPredicateOrAfterXStanzas implements StanzaFilter {
    private final AfterXStanzas afterXStanzas;
    private final StanzaFilter predicate;

    public ForMatchingPredicateOrAfterXStanzas(StanzaFilter predicate2, int count) {
        this.predicate = predicate2;
        this.afterXStanzas = new AfterXStanzas(count);
    }

    public boolean accept(Stanza packet) {
        if (!this.predicate.accept(packet)) {
            return this.afterXStanzas.accept(packet);
        }
        this.afterXStanzas.resetCounter();
        return true;
    }
}
