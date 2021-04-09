package org.jivesoftware.smack.packet;

import java.util.Locale;
import org.jivesoftware.smack.packet.id.StanzaIdUtil;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.TypedCloneable;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportCandidate;
import org.jxmpp.jid.Jid;

public final class Presence extends Stanza implements TypedCloneable<Presence> {
    public static final String ELEMENT = "presence";
    private Mode mode;
    private int priority;
    private String status;
    private Type type;

    public enum Mode {
        chat,
        available,
        away,
        xa,
        dnd;

        public static Mode fromString(String string) {
            return valueOf(string.toLowerCase(Locale.US));
        }
    }

    public enum Type {
        available,
        unavailable,
        subscribe,
        subscribed,
        unsubscribe,
        unsubscribed,
        error,
        probe;

        public static Type fromString(String string) {
            return valueOf(string.toLowerCase(Locale.US));
        }
    }

    public Presence(Type type2) {
        this.type = Type.available;
        this.status = null;
        this.priority = Integer.MIN_VALUE;
        this.mode = null;
        setType(type2);
    }

    public Presence(Jid to, Type type2) {
        this(type2);
        setTo(to);
    }

    public Presence(Type type2, String status2, int priority2, Mode mode2) {
        this.type = Type.available;
        this.status = null;
        this.priority = Integer.MIN_VALUE;
        this.mode = null;
        setType(type2);
        setStatus(status2);
        setPriority(priority2);
        setMode(mode2);
    }

    public Presence(Presence other) {
        super((Stanza) other);
        this.type = Type.available;
        this.status = null;
        this.priority = Integer.MIN_VALUE;
        this.mode = null;
        this.type = other.type;
        this.status = other.status;
        this.priority = other.priority;
        this.mode = other.mode;
    }

    public boolean isAvailable() {
        return this.type == Type.available;
    }

    public boolean isAway() {
        return this.type == Type.available && (this.mode == Mode.away || this.mode == Mode.xa || this.mode == Mode.dnd);
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type2) {
        this.type = (Type) Objects.requireNonNull(type2, "Type cannot be null");
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status2) {
        this.status = status2;
    }

    public int getPriority() {
        int i = this.priority;
        if (i == Integer.MIN_VALUE) {
            return 0;
        }
        return i;
    }

    public void setPriority(int priority2) {
        if (priority2 < -128 || priority2 > 127) {
            StringBuilder sb = new StringBuilder();
            sb.append("Priority value ");
            sb.append(priority2);
            sb.append(" is not valid. Valid range is -128 through 127.");
            throw new IllegalArgumentException(sb.toString());
        }
        this.priority = priority2;
    }

    public Mode getMode() {
        Mode mode2 = this.mode;
        if (mode2 == null) {
            return Mode.available;
        }
        return mode2;
    }

    public void setMode(Mode mode2) {
        this.mode = mode2;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Presence Stanza [");
        logCommonAttributes(sb);
        sb.append("type=");
        sb.append(this.type);
        sb.append(',');
        if (this.mode != null) {
            sb.append("mode=");
            sb.append(this.mode);
            sb.append(',');
        }
        if (!StringUtils.isNullOrEmpty((CharSequence) this.status)) {
            sb.append("status=");
            sb.append(this.status);
            sb.append(',');
        }
        if (this.priority != Integer.MIN_VALUE) {
            sb.append("prio=");
            sb.append(this.priority);
            sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder buf = new XmlStringBuilder(enclosingNamespace);
        String str = ELEMENT;
        buf.halfOpenElement(str);
        addCommonAttributes(buf, enclosingNamespace);
        if (this.type != Type.available) {
            buf.attribute("type", (Enum<?>) this.type);
        }
        buf.rightAngleBracket();
        buf.optElement("status", this.status);
        int i = this.priority;
        if (i != Integer.MIN_VALUE) {
            buf.element(JingleS5BTransportCandidate.ATTR_PRIORITY, Integer.toString(i));
        }
        Mode mode2 = this.mode;
        if (!(mode2 == null || mode2 == Mode.available)) {
            buf.element("show", (Enum<?>) this.mode);
        }
        buf.append(getExtensions(), enclosingNamespace);
        appendErrorIfExists(buf, enclosingNamespace);
        buf.closeElement(str);
        return buf;
    }

    public Presence clone() {
        return new Presence(this);
    }

    public Presence cloneWithNewId() {
        Presence clone = clone();
        clone.setStanzaId(StanzaIdUtil.newStanzaId());
        return clone;
    }
}
