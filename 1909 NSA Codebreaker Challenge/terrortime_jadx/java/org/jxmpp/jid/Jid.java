package org.jxmpp.jid;

import java.io.Serializable;
import org.jxmpp.jid.parts.Domainpart;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;

public interface Jid extends Comparable<Jid>, CharSequence, Serializable {
    BareJid asBareJid();

    DomainBareJid asDomainBareJid();

    DomainFullJid asDomainFullJidIfPossible();

    DomainFullJid asDomainFullJidOrThrow();

    EntityBareJid asEntityBareJidIfPossible();

    EntityBareJid asEntityBareJidOrThrow();

    EntityFullJid asEntityFullJidIfPossible();

    EntityFullJid asEntityFullJidOrThrow();

    EntityJid asEntityJidIfPossible();

    EntityJid asEntityJidOrThrow();

    FullJid asFullJidIfPossible();

    EntityFullJid asFullJidOrThrow();

    String asUnescapedString();

    <T extends Jid> T downcast(Class<T> cls) throws ClassCastException;

    boolean equals(CharSequence charSequence);

    boolean equals(String str);

    Domainpart getDomain();

    Localpart getLocalpartOrNull();

    Localpart getLocalpartOrThrow();

    Resourcepart getResourceOrEmpty();

    Resourcepart getResourceOrNull();

    Resourcepart getResourceOrThrow();

    boolean hasLocalpart();

    boolean hasNoResource();

    boolean hasResource();

    String intern();

    boolean isDomainBareJid();

    boolean isDomainFullJid();

    boolean isEntityBareJid();

    boolean isEntityFullJid();

    boolean isEntityJid();

    boolean isParentOf(DomainBareJid domainBareJid);

    boolean isParentOf(DomainFullJid domainFullJid);

    boolean isParentOf(EntityBareJid entityBareJid);

    boolean isParentOf(EntityFullJid entityFullJid);

    boolean isParentOf(Jid jid);

    String toString();
}
