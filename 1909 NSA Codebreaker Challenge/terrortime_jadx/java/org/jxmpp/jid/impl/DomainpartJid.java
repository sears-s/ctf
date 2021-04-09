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

public final class DomainpartJid extends AbstractJid implements DomainBareJid {
    private static final long serialVersionUID = 1;
    protected final Domainpart domain;

    DomainpartJid(String domain2) throws XmppStringprepException {
        this(Domainpart.from(domain2));
    }

    DomainpartJid(Domainpart domain2) {
        this.domain = (Domainpart) requireNonNull(domain2, "The Domainpart must not be null");
    }

    public Domainpart getDomain() {
        return this.domain;
    }

    public String toString() {
        if (this.cache != null) {
            return this.cache;
        }
        this.cache = this.domain.toString();
        return this.cache;
    }

    public String asUnescapedString() {
        return toString();
    }

    public DomainBareJid asDomainBareJid() {
        return this;
    }

    public boolean hasNoResource() {
        return true;
    }

    public EntityBareJid asEntityBareJidIfPossible() {
        return null;
    }

    public EntityFullJid asEntityFullJidIfPossible() {
        return null;
    }

    public DomainFullJid asDomainFullJidIfPossible() {
        return null;
    }

    public boolean isParentOf(EntityBareJid bareJid) {
        return this.domain.equals(bareJid.getDomain());
    }

    public boolean isParentOf(EntityFullJid fullJid) {
        return this.domain.equals(fullJid.getDomain());
    }

    public boolean isParentOf(DomainBareJid domainBareJid) {
        return this.domain.equals(domainBareJid.getDomain());
    }

    public boolean isParentOf(DomainFullJid domainFullJid) {
        return this.domain.equals(domainFullJid.getDomain());
    }

    public BareJid asBareJid() {
        return this;
    }

    public EntityJid asEntityJidIfPossible() {
        return null;
    }

    public FullJid asFullJidIfPossible() {
        return null;
    }

    public Resourcepart getResourceOrNull() {
        return null;
    }

    public Localpart getLocalpartOrNull() {
        return null;
    }
}
