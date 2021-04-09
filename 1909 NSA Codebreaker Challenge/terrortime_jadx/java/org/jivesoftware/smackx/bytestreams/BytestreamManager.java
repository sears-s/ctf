package org.jivesoftware.smackx.bytestreams;

import java.io.IOException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jxmpp.jid.Jid;

public interface BytestreamManager {
    void addIncomingBytestreamListener(BytestreamListener bytestreamListener);

    void addIncomingBytestreamListener(BytestreamListener bytestreamListener, Jid jid);

    BytestreamSession establishSession(Jid jid) throws XMPPException, IOException, InterruptedException, SmackException;

    BytestreamSession establishSession(Jid jid, String str) throws XMPPException, IOException, InterruptedException, SmackException;

    void removeIncomingBytestreamListener(BytestreamListener bytestreamListener);

    void removeIncomingBytestreamListener(Jid jid);
}
