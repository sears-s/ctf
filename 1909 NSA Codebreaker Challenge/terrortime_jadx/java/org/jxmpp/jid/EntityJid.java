package org.jxmpp.jid;

import org.jxmpp.jid.parts.Localpart;

public interface EntityJid extends Jid {
    EntityBareJid asEntityBareJid();

    String asEntityBareJidString();

    Localpart getLocalpart();
}
