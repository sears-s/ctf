package org.jivesoftware.smack.roster;

import java.util.Collection;
import org.jivesoftware.smack.packet.Presence;
import org.jxmpp.jid.Jid;

public abstract class AbstractRosterListener implements RosterListener {
    public void entriesAdded(Collection<Jid> collection) {
    }

    public void entriesUpdated(Collection<Jid> collection) {
    }

    public void entriesDeleted(Collection<Jid> collection) {
    }

    public void presenceChanged(Presence presence) {
    }
}
