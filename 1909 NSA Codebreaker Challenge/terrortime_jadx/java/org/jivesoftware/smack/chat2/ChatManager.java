package org.jivesoftware.smack.chat2;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jivesoftware.smack.AsyncButOrdered;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromTypeFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.MessageWithBodiesFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.ToTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.AbstractRosterListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;

public final class ChatManager extends Manager {
    private static final StanzaFilter INCOMING_MESSAGE_FILTER = new AndFilter(MESSAGE_FILTER, FromTypeFilter.ENTITY_FULL_JID);
    private static final Map<XMPPConnection, ChatManager> INSTANCES = new WeakHashMap();
    private static final StanzaFilter MESSAGE_FILTER = new AndFilter(MessageTypeFilter.NORMAL_OR_CHAT, new OrFilter(MessageWithBodiesFilter.INSTANCE, new StanzaExtensionFilter(XHTMLExtension.ELEMENT, XHTMLExtension.NAMESPACE)));
    private static final StanzaFilter OUTGOING_MESSAGE_FILTER = new AndFilter(MESSAGE_FILTER, ToTypeFilter.ENTITY_FULL_OR_BARE_JID);
    /* access modifiers changed from: private */
    public final AsyncButOrdered<Chat> asyncButOrdered = new AsyncButOrdered<>();
    /* access modifiers changed from: private */
    public final Map<EntityBareJid, Chat> chats = new ConcurrentHashMap();
    /* access modifiers changed from: private */
    public final Set<IncomingChatMessageListener> incomingListeners = new CopyOnWriteArraySet();
    /* access modifiers changed from: private */
    public final Set<OutgoingChatMessageListener> outgoingListeners = new CopyOnWriteArraySet();
    private boolean xhtmlIm;

    public static synchronized ChatManager getInstanceFor(XMPPConnection connection) {
        ChatManager chatManager;
        synchronized (ChatManager.class) {
            chatManager = (ChatManager) INSTANCES.get(connection);
            if (chatManager == null) {
                chatManager = new ChatManager(connection);
                INSTANCES.put(connection, chatManager);
            }
        }
        return chatManager;
    }

    private ChatManager(XMPPConnection connection) {
        super(connection);
        connection.addSyncStanzaListener(new StanzaListener() {
            public void processStanza(Stanza stanza) {
                final Message message = (Message) stanza;
                if (ChatManager.this.shouldAcceptMessage(message)) {
                    EntityFullJid fullFrom = message.getFrom().asEntityFullJidOrThrow();
                    final EntityBareJid bareFrom = fullFrom.asEntityBareJid();
                    final Chat chat = ChatManager.this.chatWith(bareFrom);
                    chat.lockedResource = fullFrom;
                    ChatManager.this.asyncButOrdered.performAsyncButOrdered(chat, new Runnable() {
                        public void run() {
                            for (IncomingChatMessageListener listener : ChatManager.this.incomingListeners) {
                                listener.newIncomingMessage(bareFrom, message, chat);
                            }
                        }
                    });
                }
            }
        }, INCOMING_MESSAGE_FILTER);
        connection.addStanzaInterceptor(new StanzaListener() {
            public void processStanza(Stanza stanza) throws NotConnectedException, InterruptedException {
                Message message = (Message) stanza;
                if (ChatManager.this.shouldAcceptMessage(message)) {
                    EntityBareJid to = message.getTo().asEntityBareJidOrThrow();
                    Chat chat = ChatManager.this.chatWith(to);
                    for (OutgoingChatMessageListener listener : ChatManager.this.outgoingListeners) {
                        listener.newOutgoingMessage(to, message, chat);
                    }
                }
            }
        }, OUTGOING_MESSAGE_FILTER);
        Roster.getInstanceFor(connection).addRosterListener(new AbstractRosterListener() {
            public void presenceChanged(Presence presence) {
                Jid from = presence.getFrom();
                EntityBareJid bareFrom = from.asEntityBareJidIfPossible();
                if (bareFrom != null) {
                    Chat chat = (Chat) ChatManager.this.chats.get(bareFrom);
                    if (chat != null && chat.lockedResource != null) {
                        if (chat.lockedResource.equals((CharSequence) from.asEntityFullJidIfPossible())) {
                            if (chat.lastPresenceOfLockedResource == null) {
                                chat.lastPresenceOfLockedResource = presence;
                                return;
                            }
                            if (!(chat.lastPresenceOfLockedResource.getMode() == presence.getMode() && chat.lastPresenceOfLockedResource.getType() == presence.getType())) {
                                chat.unlockResource();
                            }
                        }
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public boolean shouldAcceptMessage(Message message) {
        if (!message.getBodies().isEmpty()) {
            return true;
        }
        if (this.xhtmlIm && XHTMLExtension.from(message) != null) {
            return true;
        }
        return false;
    }

    public boolean addIncomingListener(IncomingChatMessageListener listener) {
        return this.incomingListeners.add(listener);
    }

    public boolean removeIncomingListener(IncomingChatMessageListener listener) {
        return this.incomingListeners.remove(listener);
    }

    public boolean addOutgoingListener(OutgoingChatMessageListener listener) {
        return this.outgoingListeners.add(listener);
    }

    public boolean removeOutgoingListener(OutgoingChatMessageListener listener) {
        return this.outgoingListeners.remove(listener);
    }

    public Chat chatWith(EntityBareJid jid) {
        Chat chat = (Chat) this.chats.get(jid);
        if (chat == null) {
            synchronized (this.chats) {
                Chat chat2 = (Chat) this.chats.get(jid);
                if (chat2 != null) {
                    return chat2;
                }
                chat = new Chat(connection(), jid);
                this.chats.put(jid, chat);
            }
        }
        return chat;
    }

    public void setXhmtlImEnabled(boolean xhtmlIm2) {
        this.xhtmlIm = xhtmlIm2;
    }
}
