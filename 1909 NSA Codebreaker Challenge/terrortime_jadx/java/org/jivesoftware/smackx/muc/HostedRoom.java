package org.jivesoftware.smackx.muc;

import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smackx.disco.packet.DiscoverItems.Item;
import org.jxmpp.jid.EntityBareJid;

public class HostedRoom {
    private final EntityBareJid jid;
    private final String name;

    public HostedRoom(Item item) {
        this.jid = (EntityBareJid) Objects.requireNonNull(item.getEntityID().asEntityBareJidIfPossible(), "The discovered item must be an entity bare JID");
        this.name = item.getName();
    }

    public EntityBareJid getJid() {
        return this.jid;
    }

    public String getName() {
        return this.name;
    }
}
