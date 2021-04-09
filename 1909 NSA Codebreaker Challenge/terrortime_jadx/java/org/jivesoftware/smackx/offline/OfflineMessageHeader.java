package org.jivesoftware.smackx.offline;

import org.jivesoftware.smackx.disco.packet.DiscoverItems.Item;
import org.jxmpp.jid.Jid;

public class OfflineMessageHeader {
    private String jid;
    private String stamp;
    private Jid user;

    public OfflineMessageHeader(Item item) {
        this.user = item.getEntityID();
        this.jid = item.getName();
        this.stamp = item.getNode();
    }

    public Jid getUser() {
        return this.user;
    }

    public String getJid() {
        return this.jid;
    }

    public String getStamp() {
        return this.stamp;
    }
}
