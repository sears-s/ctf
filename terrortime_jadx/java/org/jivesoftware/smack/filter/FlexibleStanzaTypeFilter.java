package org.jivesoftware.smack.filter;

import java.lang.reflect.ParameterizedType;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.Objects;

public abstract class FlexibleStanzaTypeFilter<S extends Stanza> implements StanzaFilter {
    protected final Class<S> stanzaType;

    /* access modifiers changed from: protected */
    public abstract boolean acceptSpecific(S s);

    public FlexibleStanzaTypeFilter(Class<S> packetType) {
        this.stanzaType = (Class) Objects.requireNonNull(packetType, "Type must not be null");
    }

    public FlexibleStanzaTypeFilter() {
        this.stanzaType = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public final boolean accept(Stanza packet) {
        if (this.stanzaType.isInstance(packet)) {
            return acceptSpecific(packet);
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": ");
        sb.append(this.stanzaType.toString());
        return sb.toString();
    }
}
