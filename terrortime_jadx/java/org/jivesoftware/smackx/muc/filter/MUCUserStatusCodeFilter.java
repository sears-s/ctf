package org.jivesoftware.smackx.muc.filter;

import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.muc.packet.MUCUser.Status;

public class MUCUserStatusCodeFilter implements StanzaFilter {
    public static final MUCUserStatusCodeFilter STATUS_110_PRESENCE_TO_SELF = new MUCUserStatusCodeFilter(Status.PRESENCE_TO_SELF_110);
    private final Status status;

    public MUCUserStatusCodeFilter(Status status2) {
        this.status = status2;
    }

    public MUCUserStatusCodeFilter(int statusCode) {
        this(Status.create(Integer.valueOf(statusCode)));
    }

    public boolean accept(Stanza stanza) {
        MUCUser mucUser = MUCUser.from(stanza);
        if (mucUser == null) {
            return false;
        }
        return mucUser.getStatus().contains(this.status);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": status=");
        sb.append(this.status);
        return sb.toString();
    }
}
