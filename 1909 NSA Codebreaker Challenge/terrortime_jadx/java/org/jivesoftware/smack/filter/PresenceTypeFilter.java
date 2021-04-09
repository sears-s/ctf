package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.util.Objects;

public final class PresenceTypeFilter extends FlexibleStanzaTypeFilter<Presence> {
    public static final PresenceTypeFilter AVAILABLE = new PresenceTypeFilter(Type.available);
    public static final PresenceTypeFilter ERROR = new PresenceTypeFilter(Type.error);
    public static final StanzaFilter OUTGOING_PRESENCE_BROADCAST = new AndFilter(AVAILABLE, EmptyToMatcher.INSTANCE);
    public static final PresenceTypeFilter PROBE = new PresenceTypeFilter(Type.probe);
    public static final PresenceTypeFilter SUBSCRIBE = new PresenceTypeFilter(Type.subscribe);
    public static final PresenceTypeFilter SUBSCRIBED = new PresenceTypeFilter(Type.subscribed);
    public static final PresenceTypeFilter UNAVAILABLE = new PresenceTypeFilter(Type.unavailable);
    public static final PresenceTypeFilter UNSUBSCRIBE = new PresenceTypeFilter(Type.unsubscribe);
    public static final PresenceTypeFilter UNSUBSCRIBED = new PresenceTypeFilter(Type.unsubscribed);
    private final Type type;

    private PresenceTypeFilter(Type type2) {
        super(Presence.class);
        this.type = (Type) Objects.requireNonNull(type2, "type must not be null");
    }

    /* access modifiers changed from: protected */
    public boolean acceptSpecific(Presence presence) {
        return presence.getType() == this.type;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": type=");
        sb.append(this.type);
        return sb.toString();
    }
}
