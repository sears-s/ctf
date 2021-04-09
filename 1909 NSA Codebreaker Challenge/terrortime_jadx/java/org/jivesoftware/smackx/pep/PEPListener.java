package org.jivesoftware.smackx.pep;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jxmpp.jid.EntityBareJid;

public interface PEPListener {
    void eventReceived(EntityBareJid entityBareJid, EventElement eventElement, Message message);
}
