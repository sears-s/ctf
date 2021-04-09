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

public final class LocalAndDomainpartJid extends AbstractJid implements EntityBareJid {
    private static final long serialVersionUID = 1;
    private final DomainBareJid domainBareJid;
    private final Localpart localpart;
    private transient String unescapedCache;

    LocalAndDomainpartJid(String localpart2, String domain) throws XmppStringprepException {
        this.domainBareJid = new DomainpartJid(domain);
        this.localpart = Localpart.from(localpart2);
    }

    LocalAndDomainpartJid(Localpart localpart2, Domainpart domain) {
        this.localpart = (Localpart) requireNonNull(localpart2, "The Localpart must not be null");
        this.domainBareJid = new DomainpartJid(domain);
    }

    public Localpart getLocalpart() {
        return this.localpart;
    }

    public String toString() {
        if (this.cache != null) {
            return this.cache;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getLocalpart().toString());
        sb.append('@');
        sb.append(this.domainBareJid.toString());
        this.cache = sb.toString();
        return this.cache;
    }

    public String asUnescapedString() {
        String str = this.unescapedCache;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getLocalpart().asUnescapedString());
        sb.append('@');
        sb.append(this.domainBareJid.toString());
        this.unescapedCache = sb.toString();
        return this.unescapedCache;
    }

    public EntityBareJid asEntityBareJidIfPossible() {
        return this;
    }

    public EntityFullJid asEntityFullJidIfPossible() {
        return null;
    }

    public DomainFullJid asDomainFullJidIfPossible() {
        return null;
    }

    public boolean isParentOf(EntityBareJid bareJid) {
        return this.domainBareJid.equals((CharSequence) bareJid.getDomain()) && this.localpart.equals(bareJid.getLocalpart());
    }

    public boolean isParentOf(EntityFullJid fullJid) {
        return isParentOf(fullJid.asBareJid());
    }

    public boolean isParentOf(DomainBareJid domainBareJid2) {
        return false;
    }

    public boolean isParentOf(DomainFullJid domainFullJid) {
        return false;
    }

    public DomainBareJid asDomainBareJid() {
        return this.domainBareJid;
    }

    public Domainpart getDomain() {
        return this.domainBareJid.getDomain();
    }

    public BareJid asBareJid() {
        return this;
    }

    public boolean hasNoResource() {
        return true;
    }

    public EntityJid asEntityJidIfPossible() {
        return this;
    }

    public FullJid asFullJidIfPossible() {
        return null;
    }

    public EntityBareJid asEntityBareJid() {
        return this;
    }

    public Resourcepart getResourceOrNull() {
        return null;
    }

    public Localpart getLocalpartOrNull() {
        return getLocalpart();
    }

    public String asEntityBareJidString() {
        return toString();
    }
}
