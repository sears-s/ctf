package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.util.Objects;

public final class IQTypeFilter extends FlexibleStanzaTypeFilter<IQ> {
    public static final StanzaFilter ERROR = new IQTypeFilter(Type.error);
    public static final StanzaFilter GET = new IQTypeFilter(Type.get);
    public static final StanzaFilter GET_OR_SET = new OrFilter(GET, SET);
    public static final StanzaFilter RESULT = new IQTypeFilter(Type.result);
    public static final StanzaFilter SET = new IQTypeFilter(Type.set);
    private final Type type;

    private IQTypeFilter(Type type2) {
        super(IQ.class);
        this.type = (Type) Objects.requireNonNull(type2, "Type must not be null");
    }

    /* access modifiers changed from: protected */
    public boolean acceptSpecific(IQ iq) {
        return iq.getType() == this.type;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": type=");
        sb.append(this.type);
        return sb.toString();
    }
}
