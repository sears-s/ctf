package org.jivesoftware.smackx.muc;

import java.util.logging.Logger;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.packet.MUCItem;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;

public class Occupant {
    private static final Logger LOGGER = Logger.getLogger(Occupant.class.getName());
    private final MUCAffiliation affiliation;
    private final Jid jid;
    private final Resourcepart nick;
    private final MUCRole role;

    Occupant(MUCItem item) {
        this.jid = item.getJid();
        this.affiliation = item.getAffiliation();
        this.role = item.getRole();
        this.nick = item.getNick();
    }

    Occupant(Presence presence) {
        MUCItem item = ((MUCUser) presence.getExtension("x", MUCUser.NAMESPACE)).getItem();
        this.jid = item.getJid();
        this.affiliation = item.getAffiliation();
        this.role = item.getRole();
        EntityFullJid from = presence.getFrom().asEntityFullJidIfPossible();
        if (from == null) {
            Logger logger = LOGGER;
            StringBuilder sb = new StringBuilder();
            sb.append("Occupant presence without resource: ");
            sb.append(presence.getFrom());
            logger.warning(sb.toString());
            this.nick = null;
            return;
        }
        this.nick = from.getResourcepart();
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

    public boolean equals(Object obj) {
        if (!(obj instanceof Occupant)) {
            return false;
        }
        return this.jid.equals((CharSequence) ((Occupant) obj).jid);
    }

    public int hashCode() {
        int result = ((this.affiliation.hashCode() * 17) + this.role.hashCode()) * 17;
        Jid jid2 = this.jid;
        int i = 0;
        int result2 = (result + (jid2 != null ? jid2.hashCode() : 0)) * 17;
        Resourcepart resourcepart = this.nick;
        if (resourcepart != null) {
            i = resourcepart.hashCode();
        }
        return result2 + i;
    }
}
