package org.jivesoftware.smackx.chatstates;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.AsyncButOrdered;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromTypeFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jxmpp.jid.EntityBareJid;

public final class ChatStateManager extends Manager {
    private static final StanzaFilter INCOMING_CHAT_STATE_FILTER;
    private static final StanzaFilter INCOMING_MESSAGE_FILTER = new AndFilter(MessageTypeFilter.NORMAL_OR_CHAT, FromTypeFilter.ENTITY_FULL_JID);
    private static final Map<XMPPConnection, ChatStateManager> INSTANCES = new WeakHashMap();
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(ChatStateManager.class.getName());
    public static final String NAMESPACE = "http://jabber.org/protocol/chatstates";
    /* access modifiers changed from: private */
    public static final StanzaFilter filter;
    /* access modifiers changed from: private */
    public final AsyncButOrdered<Chat> asyncButOrdered = new AsyncButOrdered<>();
    /* access modifiers changed from: private */
    public final Set<ChatStateListener> chatStateListeners = new HashSet();
    private final Map<Chat, ChatState> chatStates = new WeakHashMap();

    static {
        String str = "http://jabber.org/protocol/chatstates";
        filter = new NotFilter(new StanzaExtensionFilter(str));
        INCOMING_CHAT_STATE_FILTER = new AndFilter(INCOMING_MESSAGE_FILTER, new StanzaExtensionFilter(str));
    }

    public static synchronized ChatStateManager getInstance(XMPPConnection connection) {
        ChatStateManager manager;
        synchronized (ChatStateManager.class) {
            manager = (ChatStateManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new ChatStateManager(connection);
                INSTANCES.put(connection, manager);
            }
        }
        return manager;
    }

    private ChatStateManager(XMPPConnection connection) {
        super(connection);
        ChatManager.getInstanceFor(connection).addOutgoingListener(new OutgoingChatMessageListener() {
            public void newOutgoingMessage(EntityBareJid to, Message message, Chat chat) {
                if (chat != null && ChatStateManager.filter.accept(message) && ChatStateManager.this.updateChatState(chat, ChatState.active)) {
                    message.addExtension(new ChatStateExtension(ChatState.active));
                }
            }
        });
        connection.addSyncStanzaListener(new StanzaListener() {
            public void processStanza(Stanza stanza) {
                List<ChatStateListener> arrayList;
                Message message = (Message) stanza;
                Chat chat = ChatManager.getInstanceFor(ChatStateManager.this.connection()).chatWith(message.getFrom().asEntityFullJidIfPossible().asEntityBareJid());
                String chatStateElementName = message.getExtension("http://jabber.org/protocol/chatstates").getElementName();
                try {
                    final ChatState finalState = ChatState.valueOf(chatStateElementName);
                    synchronized (ChatStateManager.this.chatStateListeners) {
                        arrayList = new ArrayList<>(ChatStateManager.this.chatStateListeners.size());
                        arrayList.addAll(ChatStateManager.this.chatStateListeners);
                    }
                    final List<ChatStateListener> finalListeners = arrayList;
                    AsyncButOrdered access$500 = ChatStateManager.this.asyncButOrdered;
                    final Chat chat2 = chat;
                    ArrayList arrayList2 = arrayList;
                    AnonymousClass1 r0 = r1;
                    final Message message2 = message;
                    AnonymousClass1 r1 = new Runnable() {
                        public void run() {
                            for (ChatStateListener listener : finalListeners) {
                                listener.stateChanged(chat2, finalState, message2);
                            }
                        }
                    };
                    access$500.performAsyncButOrdered(chat, r0);
                } catch (Exception e) {
                    Exception ex = e;
                    Logger access$300 = ChatStateManager.LOGGER;
                    Level level = Level.WARNING;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invalid chat state element name: ");
                    sb.append(chatStateElementName);
                    access$300.log(level, sb.toString(), ex);
                }
            }
        }, INCOMING_CHAT_STATE_FILTER);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature("http://jabber.org/protocol/chatstates");
    }

    public boolean addChatStateListener(ChatStateListener listener) {
        boolean add;
        synchronized (this.chatStateListeners) {
            add = this.chatStateListeners.add(listener);
        }
        return add;
    }

    public boolean removeChatStateListener(ChatStateListener listener) {
        boolean remove;
        synchronized (this.chatStateListeners) {
            remove = this.chatStateListeners.remove(listener);
        }
        return remove;
    }

    public void setCurrentState(ChatState newState, Chat chat) throws NotConnectedException, InterruptedException {
        if (chat == null || newState == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        } else if (updateChatState(chat, newState)) {
            Message message = new Message();
            message.addExtension(new ChatStateExtension(newState));
            chat.send(message);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return connection().equals(((ChatStateManager) o).connection());
    }

    public int hashCode() {
        return connection().hashCode();
    }

    /* access modifiers changed from: private */
    public synchronized boolean updateChatState(Chat chat, ChatState newState) {
        if (((ChatState) this.chatStates.get(chat)) == newState) {
            return false;
        }
        this.chatStates.put(chat, newState);
        return true;
    }
}
