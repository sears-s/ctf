package org.jivesoftware.smack.sm.predicates;

import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;

public final class OnceForThisStanza implements StanzaFilter {
    private final XMPPTCPConnection connection;
    private final String id;

    public static void setup(XMPPTCPConnection connection2, Stanza packet) {
        connection2.addRequestAckPredicate(new OnceForThisStanza(connection2, packet));
    }

    private OnceForThisStanza(XMPPTCPConnection connection2, Stanza packet) {
        this.connection = connection2;
        this.id = packet.getStanzaId();
        if (StringUtils.isNullOrEmpty((CharSequence) this.id)) {
            throw new IllegalArgumentException("Stanza ID must be set");
        }
    }

    public boolean accept(Stanza packet) {
        String otherId = packet.getStanzaId();
        if (StringUtils.isNullOrEmpty((CharSequence) otherId) || !this.id.equals(otherId)) {
            return false;
        }
        this.connection.removeRequestAckPredicate(this);
        return true;
    }
}
