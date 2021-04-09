package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.Jid;

public final class ToTypeFilter extends AbstractJidTypeFilter {
    public static final ToTypeFilter DOMAIN_BARE_JID = new ToTypeFilter(JidType.domainBare);
    public static final ToTypeFilter DOMAIN_FULL_JID = new ToTypeFilter(JidType.domainFull);
    public static final ToTypeFilter ENTITY_BARE_JID = new ToTypeFilter(JidType.entityBare);
    public static final ToTypeFilter ENTITY_FULL_JID = new ToTypeFilter(JidType.entityFull);
    public static final StanzaFilter ENTITY_FULL_OR_BARE_JID = new OrFilter(ENTITY_FULL_JID, ENTITY_BARE_JID);
    public static final ToTypeFilter TO_ANY_JID = new ToTypeFilter(JidType.any);

    private ToTypeFilter(JidType jidType) {
        super(jidType);
    }

    /* access modifiers changed from: protected */
    public Jid getJidToInspect(Stanza stanza) {
        return stanza.getTo();
    }
}
