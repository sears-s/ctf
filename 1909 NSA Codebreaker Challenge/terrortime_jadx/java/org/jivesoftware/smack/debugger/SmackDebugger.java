package org.jivesoftware.smack.debugger;

import java.io.Reader;
import java.io.Writer;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.TopLevelStreamElement;
import org.jxmpp.jid.EntityFullJid;

public abstract class SmackDebugger {
    protected final XMPPConnection connection;

    public abstract Reader newConnectionReader(Reader reader);

    public abstract Writer newConnectionWriter(Writer writer);

    public abstract void onIncomingStreamElement(TopLevelStreamElement topLevelStreamElement);

    public abstract void onOutgoingStreamElement(TopLevelStreamElement topLevelStreamElement);

    public abstract void userHasLogged(EntityFullJid entityFullJid);

    protected SmackDebugger(XMPPConnection connection2) {
        this.connection = connection2;
    }
}
