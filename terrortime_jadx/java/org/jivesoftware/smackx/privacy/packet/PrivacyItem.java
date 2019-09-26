package org.jivesoftware.smackx.privacy.packet;

import org.jivesoftware.smack.util.NumberUtil;

public class PrivacyItem {
    public static final String SUBSCRIPTION_BOTH = "both";
    public static final String SUBSCRIPTION_FROM = "from";
    public static final String SUBSCRIPTION_NONE = "none";
    public static final String SUBSCRIPTION_TO = "to";
    private final boolean allow;
    private boolean filterIQ;
    private boolean filterMessage;
    private boolean filterPresenceIn;
    private boolean filterPresenceOut;
    private final long order;
    private final Type type;
    private final String value;

    public enum Type {
        group,
        jid,
        subscription
    }

    public PrivacyItem(boolean allow2, long order2) {
        this((Type) null, (String) null, allow2, order2);
    }

    public PrivacyItem(Type type2, String value2, boolean allow2, long order2) {
        this.filterIQ = false;
        this.filterMessage = false;
        this.filterPresenceIn = false;
        this.filterPresenceOut = false;
        NumberUtil.checkIfInUInt32Range(order2);
        this.type = type2;
        this.value = value2;
        this.allow = allow2;
        this.order = order2;
    }

    public PrivacyItem(Type type2, CharSequence value2, boolean allow2, long order2) {
        this(type2, value2 != null ? value2.toString() : null, allow2, order2);
    }

    public boolean isAllow() {
        return this.allow;
    }

    public boolean isFilterIQ() {
        return this.filterIQ;
    }

    public void setFilterIQ(boolean filterIQ2) {
        this.filterIQ = filterIQ2;
    }

    public boolean isFilterMessage() {
        return this.filterMessage;
    }

    public void setFilterMessage(boolean filterMessage2) {
        this.filterMessage = filterMessage2;
    }

    public boolean isFilterPresenceIn() {
        return this.filterPresenceIn;
    }

    public void setFilterPresenceIn(boolean filterPresenceIn2) {
        this.filterPresenceIn = filterPresenceIn2;
    }

    public boolean isFilterPresenceOut() {
        return this.filterPresenceOut;
    }

    public void setFilterPresenceOut(boolean filterPresenceOut2) {
        this.filterPresenceOut = filterPresenceOut2;
    }

    public long getOrder() {
        return this.order;
    }

    public Type getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isFilterEverything() {
        return !isFilterIQ() && !isFilterMessage() && !isFilterPresenceIn() && !isFilterPresenceOut();
    }

    public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<item");
        if (isAllow()) {
            buf.append(" action=\"allow\"");
        } else {
            buf.append(" action=\"deny\"");
        }
        buf.append(" order=\"");
        buf.append(getOrder());
        buf.append('\"');
        if (getType() != null) {
            buf.append(" type=\"");
            buf.append(getType());
            buf.append('\"');
        }
        if (getValue() != null) {
            buf.append(" value=\"");
            buf.append(getValue());
            buf.append('\"');
        }
        if (isFilterEverything()) {
            buf.append("/>");
        } else {
            buf.append('>');
            if (isFilterIQ()) {
                buf.append("<iq/>");
            }
            if (isFilterMessage()) {
                buf.append("<message/>");
            }
            if (isFilterPresenceIn()) {
                buf.append("<presence-in/>");
            }
            if (isFilterPresenceOut()) {
                buf.append("<presence-out/>");
            }
            buf.append("</item>");
        }
        return buf.toString();
    }
}
