package org.jivesoftware.smack.roster.rosterstore;

import java.util.Collection;
import java.util.List;
import org.jivesoftware.smack.roster.packet.RosterPacket.Item;
import org.jxmpp.jid.Jid;

public interface RosterStore {
    boolean addEntry(Item item, String str);

    List<Item> getEntries();

    Item getEntry(Jid jid);

    String getRosterVersion();

    boolean removeEntry(Jid jid, String str);

    boolean resetEntries(Collection<Item> collection, String str);

    void resetStore();
}
