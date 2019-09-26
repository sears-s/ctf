package org.jivesoftware.smack;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.packet.Stanza;

public interface StanzaListener {
    void processStanza(Stanza stanza) throws NotConnectedException, InterruptedException, NotLoggedInException;
}
