package org.jivesoftware.smack.roster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.roster.packet.RosterPacket.Item;
import org.jivesoftware.smack.roster.packet.RosterPacket.ItemType;
import org.jxmpp.jid.BareJid;

public final class RosterEntry extends Manager {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private Item item;
    private final Roster roster;

    /* renamed from: org.jivesoftware.smack.roster.RosterEntry$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$roster$packet$RosterPacket$ItemType = new int[ItemType.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$roster$packet$RosterPacket$ItemType[ItemType.from.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$roster$packet$RosterPacket$ItemType[ItemType.both.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$roster$packet$RosterPacket$ItemType[ItemType.to.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    RosterEntry(Item item2, Roster roster2, XMPPConnection connection) {
        super(connection);
        this.item = item2;
        this.roster = roster2;
    }

    @Deprecated
    public String getUser() {
        return getJid().toString();
    }

    public BareJid getJid() {
        return this.item.getJid();
    }

    public String getName() {
        return this.item.getName();
    }

    public synchronized void setName(String name) throws NotConnectedException, NoResponseException, XMPPErrorException, InterruptedException {
        if (name != null) {
            if (name.equals(getName())) {
                return;
            }
        }
        RosterPacket packet = new RosterPacket();
        packet.setType(Type.set);
        packet.addRosterItem(toRosterItem(this, name));
        connection().createStanzaCollectorAndSend(packet).nextResultOrThrow();
        this.item.setName(name);
    }

    /* access modifiers changed from: 0000 */
    public void updateItem(Item item2) {
        this.item = item2;
    }

    public boolean isApproved() {
        return this.item.isApproved();
    }

    public List<RosterGroup> getGroups() {
        List<RosterGroup> results = new ArrayList<>();
        for (RosterGroup group : this.roster.getGroups()) {
            if (group.contains(this)) {
                results.add(group);
            }
        }
        return results;
    }

    public ItemType getType() {
        return this.item.getItemType();
    }

    public boolean isSubscriptionPending() {
        return this.item.isSubscriptionPending();
    }

    public boolean canSeeMyPresence() {
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$roster$packet$RosterPacket$ItemType[getType().ordinal()];
        if (i == 1 || i == 2) {
            return true;
        }
        return false;
    }

    public boolean canSeeHisPresence() {
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$roster$packet$RosterPacket$ItemType[getType().ordinal()];
        if (i == 2 || i == 3) {
            return true;
        }
        return false;
    }

    public void cancelSubscription() throws NotConnectedException, InterruptedException {
        connection().sendStanza(new Presence(this.item.getJid(), Presence.Type.unsubscribed));
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (getName() != null) {
            buf.append(getName());
            buf.append(": ");
        }
        buf.append(getJid());
        Collection<RosterGroup> groups = getGroups();
        if (!groups.isEmpty()) {
            buf.append(" [");
            Iterator<RosterGroup> iter = groups.iterator();
            buf.append(((RosterGroup) iter.next()).getName());
            while (iter.hasNext()) {
                buf.append(", ");
                buf.append(((RosterGroup) iter.next()).getName());
            }
            buf.append(']');
        }
        return buf.toString();
    }

    public int hashCode() {
        return getJid().hashCode();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !(object instanceof RosterEntry)) {
            return false;
        }
        return getJid().equals((CharSequence) ((RosterEntry) object).getJid());
    }

    public boolean equalsDeep(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            return ((RosterEntry) obj).item.equals(this.item);
        }
        return false;
    }

    static Item toRosterItem(RosterEntry entry) {
        return toRosterItem(entry, entry.getName(), false);
    }

    static Item toRosterItem(RosterEntry entry, String name) {
        return toRosterItem(entry, name, false);
    }

    static Item toRosterItem(RosterEntry entry, boolean includeAskAttribute) {
        return toRosterItem(entry, entry.getName(), includeAskAttribute);
    }

    private static Item toRosterItem(RosterEntry entry, String name, boolean includeAskAttribute) {
        Item item2 = new Item(entry.getJid(), name);
        item2.setItemType(entry.getType());
        if (includeAskAttribute) {
            item2.setSubscriptionPending(entry.isSubscriptionPending());
        }
        item2.setApproved(entry.isApproved());
        for (RosterGroup group : entry.getGroups()) {
            item2.addGroupName(group.getName());
        }
        return item2;
    }
}
