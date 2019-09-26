package org.jivesoftware.smack.roster;

import org.jivesoftware.smack.packet.Presence;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;

public abstract class AbstractPresenceEventListener implements PresenceEventListener {
    public void presenceAvailable(FullJid address, Presence availablePresence) {
    }

    public void presenceUnavailable(FullJid address, Presence presence) {
    }

    public void presenceError(Jid address, Presence errorPresence) {
    }

    public void presenceSubscribed(BareJid address, Presence subscribedPresence) {
    }

    public void presenceUnsubscribed(BareJid address, Presence unsubscribedPresence) {
    }
}
