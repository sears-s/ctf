package org.jivesoftware.smackx.blocking;

import java.util.List;
import org.jxmpp.jid.Jid;

public interface JidsBlockedListener {
    void onJidsBlocked(List<Jid> list);
}
