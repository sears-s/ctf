package org.jivesoftware.smackx.muc;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.packet.MUCUser.Invite;
import org.jxmpp.jid.EntityJid;

public interface InvitationListener {
    void invitationReceived(XMPPConnection xMPPConnection, MultiUserChat multiUserChat, EntityJid entityJid, String str, String str2, Message message, Invite invite);
}
