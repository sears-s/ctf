package org.jivesoftware.smackx.muc;

import org.jivesoftware.smackx.muc.packet.MUCItem;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;

public class Affiliate {
    private final MUCAffiliation affiliation;
    private final Jid jid;
    private final Resourcepart nick;
    private final MUCRole role;

    Affiliate(MUCItem item) {
        this.jid = item.getJid();
        this.affiliation = item.getAffiliation();
        this.role = item.getRole();
        this.nick = item.getNick();
    }

    public Jid getJid() {
        return this.jid;
    }

    public MUCAffiliation getAffiliation() {
        return this.affiliation;
    }

    public MUCRole getRole() {
        return this.role;
    }

    public Resourcepart getNick() {
        return this.nick;
    }
}
