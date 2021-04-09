package org.jivesoftware.smack.sm.predicates;

import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;

public class AfterXStanzas implements StanzaFilter {
    final int count;
    int currentCount = 0;

    public AfterXStanzas(int count2) {
        this.count = count2;
    }

    public synchronized boolean accept(Stanza packet) {
        this.currentCount++;
        if (this.currentCount != this.count) {
            return false;
        }
        resetCounter();
        return true;
    }

    public synchronized void resetCounter() {
        this.currentCount = 0;
    }
}
