package org.jxmpp.jid.impl;

import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.DomainFullJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;

public abstract class AbstractJid implements Jid {
    private static final long serialVersionUID = 1;
    protected String cache;
    private transient String internalizedCache;

    public abstract Localpart getLocalpartOrNull();

    public abstract Resourcepart getResourceOrNull();

    public abstract boolean hasNoResource();

    public final boolean isEntityJid() {
        return isEntityBareJid() || isEntityFullJid();
    }

    public final boolean isEntityBareJid() {
        return this instanceof EntityBareJid;
    }

    public final boolean isEntityFullJid() {
        return this instanceof EntityFullJid;
    }

    public final boolean isDomainBareJid() {
        return this instanceof DomainBareJid;
    }

    public final boolean isDomainFullJid() {
        return this instanceof DomainFullJid;
    }

    public final boolean hasResource() {
        return this instanceof FullJid;
    }

    public final boolean hasLocalpart() {
        return this instanceof EntityJid;
    }

    public final <T extends Jid> T downcast(Class<T> jidClass) {
        return (Jid) jidClass.cast(this);
    }

    public int length() {
        return toString().length();
    }

    public char charAt(int index) {
        return toString().charAt(index);
    }

    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    public final EntityBareJid asEntityBareJidOrThrow() {
        EntityBareJid entityBareJid = asEntityBareJidIfPossible();
        if (entityBareJid == null) {
            throwIse("can not be converted to EntityBareJid");
        }
        return entityBareJid;
    }

    public EntityFullJid asEntityFullJidOrThrow() {
        EntityFullJid entityFullJid = asEntityFullJidIfPossible();
        if (entityFullJid == null) {
            throwIse("can not be converted to EntityFullJid");
        }
        return entityFullJid;
    }

    public EntityJid asEntityJidOrThrow() {
        EntityJid entityJid = asEntityJidIfPossible();
        if (entityJid == null) {
            throwIse("can not be converted to EntityJid");
        }
        return entityJid;
    }

    public EntityFullJid asFullJidOrThrow() {
        EntityFullJid entityFullJid = asEntityFullJidIfPossible();
        if (entityFullJid == null) {
            throwIse("can not be converted to EntityBareJid");
        }
        return entityFullJid;
    }

    public DomainFullJid asDomainFullJidOrThrow() {
        DomainFullJid domainFullJid = asDomainFullJidIfPossible();
        if (domainFullJid == null) {
            throwIse("can not be converted to DomainFullJid");
        }
        return domainFullJid;
    }

    public final Resourcepart getResourceOrEmpty() {
        Resourcepart resourcepart = getResourceOrNull();
        if (resourcepart == null) {
            return Resourcepart.EMPTY;
        }
        return resourcepart;
    }

    public final Resourcepart getResourceOrThrow() {
        Resourcepart resourcepart = getResourceOrNull();
        if (resourcepart == null) {
            throwIse("has no resourcepart");
        }
        return resourcepart;
    }

    public final Localpart getLocalpartOrThrow() {
        Localpart localpart = getLocalpartOrNull();
        if (localpart == null) {
            throwIse("has no localpart");
        }
        return localpart;
    }

    public final boolean isParentOf(Jid jid) {
        EntityFullJid fullJid = jid.asEntityFullJidIfPossible();
        if (fullJid != null) {
            return isParentOf(fullJid);
        }
        EntityBareJid bareJid = jid.asEntityBareJidIfPossible();
        if (bareJid != null) {
            return isParentOf(bareJid);
        }
        DomainFullJid domainFullJid = jid.asDomainFullJidIfPossible();
        if (domainFullJid != null) {
            return isParentOf(domainFullJid);
        }
        return isParentOf(jid.asDomainBareJid());
    }

    public final int hashCode() {
        return toString().hashCode();
    }

    public final boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (other instanceof CharSequence) {
            return equals((CharSequence) other);
        }
        return false;
    }

    public final boolean equals(CharSequence charSequence) {
        if (charSequence == null) {
            return false;
        }
        return equals(charSequence.toString());
    }

    public final boolean equals(String string) {
        return toString().equals(string);
    }

    public final int compareTo(Jid other) {
        return toString().compareTo(other.toString());
    }

    public final String intern() {
        if (this.internalizedCache == null) {
            String intern = toString().intern();
            this.internalizedCache = intern;
            this.cache = intern;
        }
        return this.internalizedCache;
    }

    private void throwIse(String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("The JID '");
        sb.append(this);
        sb.append("' ");
        sb.append(message);
        throw new IllegalStateException(sb.toString());
    }

    static <O> O requireNonNull(O object, String message) {
        if (object != null) {
            return object;
        }
        throw new IllegalArgumentException(message);
    }
}
