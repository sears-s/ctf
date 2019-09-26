package org.jivesoftware.smackx.iot.provisioning;

import org.jivesoftware.smack.packet.Presence;
import org.jxmpp.jid.BareJid;

public interface WasUnfriendedListener {
    void wasUnfriendedListener(BareJid bareJid, Presence presence);
}
