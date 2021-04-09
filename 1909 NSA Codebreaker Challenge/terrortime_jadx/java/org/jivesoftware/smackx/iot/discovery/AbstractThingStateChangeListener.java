package org.jivesoftware.smackx.iot.discovery;

import org.jxmpp.jid.BareJid;

public abstract class AbstractThingStateChangeListener implements ThingStateChangeListener {
    public void owned(BareJid owner) {
    }
}
