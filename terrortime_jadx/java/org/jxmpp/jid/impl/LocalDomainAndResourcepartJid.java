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

public final class LocalDomainAndResourcepartJid extends AbstractJid implements EntityFullJid {
    private static final long serialVersionUID = 1;
    private final EntityBareJid bareJid;
    private final Resourcepart resource;
    private String unescapedCache;

    LocalDomainAndResourcepartJid(String localpart, String domain, String resource2) throws XmppStringprepException {
        this(new LocalAndDomainpartJid(localpart, domain), Resourcepart.from(resource2));
    }

    LocalDomainAndResourcepartJid(EntityBareJid bareJid2, Resourcepart resource2) {
        this.bareJid = (EntityBareJid) requireNonNull(bareJid2, "The EntityBareJid must not be null");
        this.resource = (Resourcepart) requireNonNull(resource2, "The Resourcepart must not be null");
    }

    public String toString() {
        if (this.cache != null) {
            return this.cache;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.bareJid.toString());
        sb.append('/');
        sb.append(this.resource);
        this.cache = sb.toString();
        return this.cache;
    }

    public String asUnescapedString() {
        String str = this.unescapedCache;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.bareJid.asUnescapedString());
        sb.append('/');
        sb.append(this.resource);
        this.unescapedCache = sb.toString();
        return this.unescapedCache;
    }

    public EntityBareJid asEntityBareJid() {
        return this.bareJid;
    }

    public String asEntityBareJidString() {
        return asEntityBareJid().toString();
    }

    public boolean hasNoResource() {
        return false;
    }

    public EntityBareJid asEntityBareJidIfPossible() {
        return asEntityBareJid();
    }

    public EntityFullJid asEntityFullJidIfPossible() {
        return this;
    }

    public DomainFullJid asDomainFullJidIfPossible() {
        return null;
    }

    public Localpart getLocalpartOrNull() {
        return this.bareJid.getLocalpart();
    }

    public Resourcepart getResourceOrNull() {
        return getResourcepart();
    }

    public boolean isParentOf(EntityBareJid bareJid2) {
        return false;
    }

    public boolean isParentOf(EntityFullJid fullJid) {
        return equals((CharSequence) fullJid);
    }

    public boolean isParentOf(DomainBareJid domainBareJid) {
        return false;
    }

    public boolean isParentOf(DomainFullJid domainFullJid) {
        return false;
    }

    public DomainBareJid asDomainBareJid() {
        return this.bareJid.asDomainBareJid();
    }

    public Resourcepart getResourcepart() {
        return this.resource;
    }

    public BareJid asBareJid() {
        return asEntityBareJid();
    }

    public Domainpart getDomain() {
        return this.bareJid.getDomain();
    }

    public Localpart getLocalpart() {
        return this.bareJid.getLocalpart();
    }

    public EntityJid asEntityJidIfPossible() {
        return this;
    }

    public FullJid asFullJidIfPossible() {
        return this;
    }
}
