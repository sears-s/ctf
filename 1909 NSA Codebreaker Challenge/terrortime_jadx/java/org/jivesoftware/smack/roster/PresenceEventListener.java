package org.jivesoftware.smack.roster;

import org.jivesoftware.smack.packet.Presence;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;

public interface PresenceEventListener {
    void presenceAvailable(FullJid fullJid, Presence presence);

    void presenceError(Jid jid, Presence presence);

    void presenceSubscribed(BareJid bareJid, Presence presence);

    void presenceUnavailable(FullJid fullJid, Presence presence);

    void presenceUnsubscribed(BareJid bareJid, Presence presence);
}
