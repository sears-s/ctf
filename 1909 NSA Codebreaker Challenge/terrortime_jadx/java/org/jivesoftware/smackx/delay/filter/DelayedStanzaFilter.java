package org.jivesoftware.smackx.delay.filter;

import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.delay.DelayInformationManager;

public final class DelayedStanzaFilter implements StanzaFilter {
    public static final StanzaFilter INSTANCE = new DelayedStanzaFilter();
    public static final StanzaFilter NOT_DELAYED_STANZA = new NotFilter(INSTANCE);

    private DelayedStanzaFilter() {
    }

    public boolean accept(Stanza packet) {
        return DelayInformationManager.isDelayedStanza(packet);
    }
}
