package org.jivesoftware.smack.chat2;

import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Presence;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;

public final class Chat extends Manager {
    private final EntityBareJid jid;
    Presence lastPresenceOfLockedResource;
    volatile EntityFullJid lockedResource;

    /* renamed from: org.jivesoftware.smack.chat2.Chat$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$Message$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$packet$Message$Type[Type.normal.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$Message$Type[Type.chat.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    Chat(XMPPConnection connection, EntityBareJid jid2) {
        super(connection);
        this.jid = jid2;
    }

    public void send(CharSequence message) throws NotConnectedException, InterruptedException {
        Message stanza = new Message();
        stanza.setBody(message);
        stanza.setType(Type.chat);
        send(stanza);
    }

    public void send(Message message) throws NotConnectedException, InterruptedException {
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$packet$Message$Type[message.getType().ordinal()];
        if (i == 1 || i == 2) {
            Jid to = this.lockedResource;
            if (to == null) {
                to = this.jid;
            }
            message.setTo(to);
            connection().sendStanza(message);
            return;
        }
        throw new IllegalArgumentException("Message must be of type 'normal' or 'chat'");
    }

    public EntityBareJid getXmppAddressOfChatPartner() {
        return this.jid;
    }

    /* access modifiers changed from: 0000 */
    public void unlockResource() {
        this.lockedResource = null;
        this.lastPresenceOfLockedResource = null;
    }
}
