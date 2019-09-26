package org.jivesoftware.smack.filter.jidtype;

import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.Objects;
import org.jxmpp.jid.Jid;

public abstract class AbstractJidTypeFilter implements StanzaFilter {
    private final JidType jidType;

    public enum JidType {
        BareJid,
        DomainBareJid,
        DomainFullJid,
        DomainJid,
        EntityBareJid,
        EntityFullJid,
        EntityJid,
        FullJid;

        public boolean isTypeOf(Jid jid) {
            if (jid == null) {
                return false;
            }
            switch (this) {
                case BareJid:
                    return jid.hasNoResource();
                case DomainBareJid:
                    return jid.isDomainBareJid();
                case DomainFullJid:
                    return jid.isDomainFullJid();
                case EntityBareJid:
                    return jid.isEntityBareJid();
                case EntityFullJid:
                    return jid.isEntityFullJid();
                case EntityJid:
                    return jid.isEntityJid();
                case FullJid:
                    return jid.hasResource();
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /* access modifiers changed from: protected */
    public abstract Jid getJidToMatchFrom(Stanza stanza);

    protected AbstractJidTypeFilter(JidType jidType2) {
        this.jidType = (JidType) Objects.requireNonNull(jidType2, "jidType must not be null");
    }

    public boolean accept(Stanza stanza) {
        Jid toMatch = getJidToMatchFrom(stanza);
        if (toMatch == null) {
            return false;
        }
        return this.jidType.isTypeOf(toMatch);
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": ");
        sb.append(this.jidType);
        return sb.toString();
    }
}
