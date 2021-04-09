package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.Jid;

@Deprecated
public class ToFilter implements StanzaFilter {
    private final Jid to;

    public ToFilter(Jid to2) {
        this.to = to2;
    }

    public boolean accept(Stanza packet) {
        Jid packetTo = packet.getTo();
        if (packetTo == null) {
            return false;
        }
        return packetTo.equals((CharSequence) this.to);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": to=");
        sb.append(this.to);
        return sb.toString();
    }
}
