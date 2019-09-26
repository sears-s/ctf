package org.jivesoftware.smack.roster;

import org.jivesoftware.smack.packet.Presence;
import org.jxmpp.jid.Jid;

public interface SubscribeListener {

    public enum SubscribeAnswer {
        Approve,
        ApproveAndAlsoRequestIfRequired,
        Deny
    }

    SubscribeAnswer processSubscribe(Jid jid, Presence presence);
}
