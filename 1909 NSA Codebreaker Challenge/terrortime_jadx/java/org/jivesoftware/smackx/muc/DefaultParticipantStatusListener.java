package org.jivesoftware.smackx.muc;

import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;

public class DefaultParticipantStatusListener implements ParticipantStatusListener {
    public void joined(EntityFullJid participant) {
    }

    public void left(EntityFullJid participant) {
    }

    public void kicked(EntityFullJid participant, Jid actor, String reason) {
    }

    public void voiceGranted(EntityFullJid participant) {
    }

    public void voiceRevoked(EntityFullJid participant) {
    }

    public void banned(EntityFullJid participant, Jid actor, String reason) {
    }

    public void membershipGranted(EntityFullJid participant) {
    }

    public void membershipRevoked(EntityFullJid participant) {
    }

    public void moderatorGranted(EntityFullJid participant) {
    }

    public void moderatorRevoked(EntityFullJid participant) {
    }

    public void ownershipGranted(EntityFullJid participant) {
    }

    public void ownershipRevoked(EntityFullJid participant) {
    }

    public void adminGranted(EntityFullJid participant) {
    }

    public void adminRevoked(EntityFullJid participant) {
    }

    public void nicknameChanged(EntityFullJid participant, Resourcepart newNickname) {
    }
}
