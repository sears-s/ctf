package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.Jid;

public final class FromTypeFilter extends AbstractJidTypeFilter {
    public static final FromTypeFilter DOMAIN_BARE_JID = new FromTypeFilter(JidType.domainBare);
    public static final FromTypeFilter DOMAIN_FULL_JID = new FromTypeFilter(JidType.domainFull);
    public static final FromTypeFilter ENTITY_BARE_JID = new FromTypeFilter(JidType.entityBare);
    public static final FromTypeFilter ENTITY_FULL_JID = new FromTypeFilter(JidType.entityFull);
    public static final FromTypeFilter FROM_ANY_JID = new FromTypeFilter(JidType.any);

    private FromTypeFilter(JidType jidType) {
        super(jidType);
    }

    /* access modifiers changed from: protected */
    public Jid getJidToInspect(Stanza stanza) {
        return stanza.getFrom();
    }
}
