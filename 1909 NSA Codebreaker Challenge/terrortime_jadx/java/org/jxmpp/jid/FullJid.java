package org.jxmpp.jid;

import org.jxmpp.jid.parts.Resourcepart;

public interface FullJid extends Jid {
    Resourcepart getResourcepart();
}
