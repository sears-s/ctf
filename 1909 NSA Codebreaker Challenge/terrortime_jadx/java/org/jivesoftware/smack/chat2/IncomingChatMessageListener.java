package org.jivesoftware.smack.chat2;

import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;

public interface IncomingChatMessageListener {
    void newIncomingMessage(EntityBareJid entityBareJid, Message message, Chat chat);
}
