package org.jivesoftware.smackx.muc;

import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;

public interface ParticipantStatusListener {
    void adminGranted(EntityFullJid entityFullJid);

    void adminRevoked(EntityFullJid entityFullJid);

    void banned(EntityFullJid entityFullJid, Jid jid, String str);

    void joined(EntityFullJid entityFullJid);

    void kicked(EntityFullJid entityFullJid, Jid jid, String str);

    void left(EntityFullJid entityFullJid);

    void membershipGranted(EntityFullJid entityFullJid);

    void membershipRevoked(EntityFullJid entityFullJid);

    void moderatorGranted(EntityFullJid entityFullJid);

    void moderatorRevoked(EntityFullJid entityFullJid);

    void nicknameChanged(EntityFullJid entityFullJid, Resourcepart resourcepart);

    void ownershipGranted(EntityFullJid entityFullJid);

    void ownershipRevoked(EntityFullJid entityFullJid);

    void voiceGranted(EntityFullJid entityFullJid);

    void voiceRevoked(EntityFullJid entityFullJid);
}
