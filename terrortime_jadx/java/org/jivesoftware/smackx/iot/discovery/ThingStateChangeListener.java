package org.jivesoftware.smackx.iot.discovery;

import org.jxmpp.jid.BareJid;

public interface ThingStateChangeListener {
    void owned(BareJid bareJid);
}
