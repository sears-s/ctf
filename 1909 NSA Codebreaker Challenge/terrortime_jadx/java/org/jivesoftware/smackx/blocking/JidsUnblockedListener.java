package org.jivesoftware.smackx.blocking;

import java.util.List;
import org.jxmpp.jid.Jid;

public interface JidsUnblockedListener {
    void onJidsUnblocked(List<Jid> list);
}
