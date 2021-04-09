package org.jivesoftware.smack.roster;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.roster.packet.RosterPacket.Item;
import org.jxmpp.jid.Jid;

public class RosterGroup extends Manager {
    private final Set<RosterEntry> entries = new LinkedHashSet();
    private final String name;

    RosterGroup(String name2, XMPPConnection connection) {
        super(connection);
        this.name = name2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) throws NotConnectedException, NoResponseException, XMPPErrorException, InterruptedException {
        synchronized (this.entries) {
            for (RosterEntry entry : this.entries) {
                RosterPacket packet = new RosterPacket();
                packet.setType(Type.set);
                Item item = RosterEntry.toRosterItem(entry);
                item.removeGroupName(this.name);
                item.addGroupName(name2);
                packet.addRosterItem(item);
                connection().createStanzaCollectorAndSend(packet).nextResultOrThrow();
            }
        }
    }

    public int getEntryCount() {
        int size;
        synchronized (this.entries) {
            size = this.entries.size();
        }
        return size;
    }

    public List<RosterEntry> getEntries() {
        ArrayList arrayList;
        synchronized (this.entries) {
            arrayList = new ArrayList(this.entries);
        }
        return arrayList;
    }

    public RosterEntry getEntry(Jid user) {
        if (user == null) {
            return null;
        }
        Jid user2 = user.asBareJid();
        synchronized (this.entries) {
            for (RosterEntry entry : this.entries) {
                if (entry.getJid().equals((CharSequence) user2)) {
                    return entry;
                }
            }
            return null;
        }
    }

    public boolean contains(RosterEntry entry) {
        boolean contains;
        synchronized (this.entries) {
            contains = this.entries.contains(entry);
        }
        return contains;
    }

    public boolean contains(Jid user) {
        return getEntry(user) != null;
    }

    public void addEntry(RosterEntry entry) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        synchronized (this.entries) {
            if (!this.entries.contains(entry)) {
                RosterPacket packet = new RosterPacket();
                packet.setType(Type.set);
                Item item = RosterEntry.toRosterItem(entry);
                item.addGroupName(getName());
                packet.addRosterItem(item);
                connection().createStanzaCollectorAndSend(packet).nextResultOrThrow();
            }
        }
    }

    public void removeEntry(RosterEntry entry) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        synchronized (this.entries) {
            if (this.entries.contains(entry)) {
                RosterPacket packet = new RosterPacket();
                packet.setType(Type.set);
                Item item = RosterEntry.toRosterItem(entry);
                item.removeGroupName(getName());
                packet.addRosterItem(item);
                connection().createStanzaCollectorAndSend(packet).nextResultOrThrow();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void addEntryLocal(RosterEntry entry) {
        synchronized (this.entries) {
            this.entries.remove(entry);
            this.entries.add(entry);
        }
    }

    /* access modifiers changed from: 0000 */
    public void removeEntryLocal(RosterEntry entry) {
        synchronized (this.entries) {
            if (this.entries.contains(entry)) {
                this.entries.remove(entry);
            }
        }
    }
}
