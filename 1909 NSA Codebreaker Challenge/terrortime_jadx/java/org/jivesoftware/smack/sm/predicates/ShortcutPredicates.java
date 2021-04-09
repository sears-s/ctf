package org.jivesoftware.smack.sm.predicates;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;

public class ShortcutPredicates implements StanzaFilter {
    private final Set<StanzaFilter> predicates = new LinkedHashSet();

    public ShortcutPredicates() {
    }

    public ShortcutPredicates(Collection<? extends StanzaFilter> predicates2) {
        this.predicates.addAll(predicates2);
    }

    public boolean addPredicate(StanzaFilter predicate) {
        return this.predicates.add(predicate);
    }

    public boolean removePredicate(StanzaFilter prediacte) {
        return this.predicates.remove(prediacte);
    }

    public boolean accept(Stanza packet) {
        for (StanzaFilter predicate : this.predicates) {
            if (predicate.accept(packet)) {
                return true;
            }
        }
        return false;
    }
}
