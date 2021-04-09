package org.jxmpp.jid.impl;

import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.DomainFullJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.parts.Domainpart;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

public final class DomainAndResourcepartJid extends AbstractJid implements DomainFullJid {
    private static final long serialVersionUID = 1;
    private final DomainBareJid domainBareJid;
    private final Resourcepart resource;

    DomainAndResourcepartJid(String domain, String resource2) throws XmppStringprepException {
        this((DomainBareJid) new DomainpartJid(domain), Resourcepart.from(resource2));
    }

    DomainAndResourcepartJid(DomainBareJid domainBareJid2, Resourcepart resource2) {
        this.domainBareJid = (DomainBareJid) requireNonNull(domainBareJid2, "The DomainBareJid must not be null");
        this.resource = (Resourcepart) requireNonNull(resource2, "The Resource must not be null");
    }

    public String toString() {
        if (this.cache != null) {
            return this.cache;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.domainBareJid.toString());
        sb.append('/');
        sb.append(this.resource);
        this.cache = sb.toString();
        return this.cache;
    }

    public DomainBareJid asDomainBareJid() {
        return this.domainBareJid;
    }

    public boolean hasNoResource() {
        return false;
    }

    public EntityBareJid asEntityBareJidIfPossible() {
        return null;
    }

    public EntityFullJid asEntityFullJidIfPossible() {
        return null;
    }

    public DomainFullJid asDomainFullJidIfPossible() {
        return this;
    }

    public Resourcepart getResourceOrNull() {
        return getResourcepart();
    }

    public boolean isParentOf(EntityBareJid bareJid) {
        return false;
    }

    public boolean isParentOf(EntityFullJid fullJid) {
        return false;
    }

    public boolean isParentOf(DomainBareJid domainBareJid2) {
        return false;
    }

    public boolean isParentOf(DomainFullJid domainFullJid) {
        return this.domainBareJid.equals((CharSequence) domainFullJid.getDomain()) && this.resource.equals(domainFullJid.getResourcepart());
    }

    public Resourcepart getResourcepart() {
        return this.resource;
    }

    public BareJid asBareJid() {
        return asDomainBareJid();
    }

    public Domainpart getDomain() {
        return this.domainBareJid.getDomain();
    }

    public String asUnescapedString() {
        return toString();
    }

    public EntityJid asEntityJidIfPossible() {
        return null;
    }

    public FullJid asFullJidIfPossible() {
        return this;
    }

    public Localpart getLocalpartOrNull() {
        return null;
    }
}
