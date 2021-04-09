package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.Jid;

public abstract class AbstractJidTypeFilter implements StanzaFilter {
    private final JidType jidType;

    /* renamed from: org.jivesoftware.smack.filter.AbstractJidTypeFilter$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$filter$AbstractJidTypeFilter$JidType = new int[JidType.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$filter$AbstractJidTypeFilter$JidType[JidType.entityFull.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$filter$AbstractJidTypeFilter$JidType[JidType.entityBare.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$filter$AbstractJidTypeFilter$JidType[JidType.domainFull.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$filter$AbstractJidTypeFilter$JidType[JidType.domainBare.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$filter$AbstractJidTypeFilter$JidType[JidType.any.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    protected enum JidType {
        entityFull,
        entityBare,
        domainFull,
        domainBare,
        any
    }

    /* access modifiers changed from: protected */
    public abstract Jid getJidToInspect(Stanza stanza);

    protected AbstractJidTypeFilter(JidType jidType2) {
        this.jidType = jidType2;
    }

    public final boolean accept(Stanza stanza) {
        Jid jid = getJidToInspect(stanza);
        if (jid == null) {
            return false;
        }
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$filter$AbstractJidTypeFilter$JidType[this.jidType.ordinal()];
        if (i == 1) {
            return jid.isEntityFullJid();
        }
        if (i == 2) {
            return jid.isEntityBareJid();
        }
        if (i == 3) {
            return jid.isDomainFullJid();
        }
        if (i == 4) {
            return jid.isDomainBareJid();
        }
        if (i == 5) {
            return true;
        }
        throw new AssertionError();
    }
}
