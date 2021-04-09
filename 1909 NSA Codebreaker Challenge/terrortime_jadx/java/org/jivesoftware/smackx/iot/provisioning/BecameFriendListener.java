package org.jivesoftware.smackx.iot.provisioning;

import org.jivesoftware.smack.packet.Presence;
import org.jxmpp.jid.BareJid;

public interface BecameFriendListener {
    void becameFriend(BareJid bareJid, Presence presence);
}
