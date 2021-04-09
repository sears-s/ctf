package org.jivesoftware.smack.chat;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaCollector;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FlexibleStanzaTypeFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.ThreadFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;

@Deprecated
public final class ChatManager extends Manager {
    private static final Map<XMPPConnection, ChatManager> INSTANCES = new WeakHashMap();
    private static final Logger LOGGER = Logger.getLogger(ChatManager.class.getName());
    private static boolean defaultIsNormalInclude = true;
    private static MatchMode defaultMatchMode = MatchMode.BARE_JID;
    private final Map<EntityBareJid, Chat> baseJidChats = new ConcurrentHashMap();
    private final Set<ChatManagerListener> chatManagerListeners = new CopyOnWriteArraySet();
    private final Map<MessageListener, StanzaFilter> interceptors = new WeakHashMap();
    private final Map<Jid, Chat> jidChats = new ConcurrentHashMap();
    private MatchMode matchMode = defaultMatchMode;
    /* access modifiers changed from: private */
    public boolean normalIncluded = defaultIsNormalInclude;
    private final StanzaFilter packetFilter = new OrFilter(MessageTypeFilter.CHAT, new FlexibleStanzaTypeFilter<Message>() {
        /* access modifiers changed from: protected */
        public boolean acceptSpecific(Message message) {
            return ChatManager.this.normalIncluded && message.getType() == Type.normal;
        }
    });
    private final Map<String, Chat> threadChats = new ConcurrentHashMap();

    public enum MatchMode {
        NONE,
        SUPPLIED_JID,
        BARE_JID
    }

    public static synchronized ChatManager getInstanceFor(XMPPConnection connection) {
        ChatManager manager;
        synchronized (ChatManager.class) {
            manager = (ChatManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new ChatManager(connection);
            }
        }
        return manager;
    }

    private ChatManager(XMPPConnection connection) {
        super(connection);
        connection.addSyncStanzaListener(new StanzaListener() {
            public void processStanza(Stanza packet) {
                Chat chat;
                Message message = (Message) packet;
                if (message.getThread() == null) {
                    chat = ChatManager.this.getUserChat(message.getFrom());
                } else {
                    chat = ChatManager.this.getThreadChat(message.getThread());
                }
                if (chat == null) {
                    chat = ChatManager.this.createChat(message);
                }
                if (chat != null) {
                    ChatManager.deliverMessage(chat, message);
                }
            }
        }, this.packetFilter);
        INSTANCES.put(connection, this);
    }

    public boolean isNormalIncluded() {
        return this.normalIncluded;
    }

    public void setNormalIncluded(boolean normalIncluded2) {
        this.normalIncluded = normalIncluded2;
    }

    public MatchMode getMatchMode() {
        return this.matchMode;
    }

    public void setMatchMode(MatchMode matchMode2) {
        this.matchMode = matchMode2;
    }

    public Chat createChat(EntityJid userJID) {
        return createChat(userJID, null);
    }

    public Chat createChat(EntityJid userJID, ChatMessageListener listener) {
        return createChat(userJID, (String) null, listener);
    }

    public Chat createChat(EntityJid userJID, String thread, ChatMessageListener listener) {
        if (thread == null) {
            thread = nextID();
        }
        if (((Chat) this.threadChats.get(thread)) == null) {
            Chat chat = createChat(userJID, thread, true);
            chat.addMessageListener(listener);
            return chat;
        }
        throw new IllegalArgumentException("ThreadID is already used");
    }

    private Chat createChat(EntityJid userJID, String threadID, boolean createdLocally) {
        Chat chat = new Chat(this, userJID, threadID);
        this.threadChats.put(threadID, chat);
        this.jidChats.put(userJID, chat);
        this.baseJidChats.put(userJID.asEntityBareJid(), chat);
        for (ChatManagerListener listener : this.chatManagerListeners) {
            listener.chatCreated(chat, createdLocally);
        }
        return chat;
    }

    /* access modifiers changed from: 0000 */
    public void closeChat(Chat chat) {
        this.threadChats.remove(chat.getThreadID());
        EntityJid userJID = chat.getParticipant();
        this.jidChats.remove(userJID);
        this.baseJidChats.remove(userJID.asEntityBareJid());
    }

    /* access modifiers changed from: private */
    public Chat createChat(Message message) {
        Jid from = message.getFrom();
        if (from == null) {
            return null;
        }
        EntityJid userJID = from.asEntityJidIfPossible();
        if (userJID == null) {
            Logger logger = LOGGER;
            StringBuilder sb = new StringBuilder();
            sb.append("Message from JID without localpart: '");
            sb.append(message.toXML((String) null));
            sb.append("'");
            logger.warning(sb.toString());
            return null;
        }
        String threadID = message.getThread();
        if (threadID == null) {
            threadID = nextID();
        }
        return createChat(userJID, threadID, false);
    }

    /* access modifiers changed from: private */
    public Chat getUserChat(Jid userJID) {
        if (this.matchMode == MatchMode.NONE || userJID == null) {
            return null;
        }
        Chat match = (Chat) this.jidChats.get(userJID);
        if (match == null && this.matchMode == MatchMode.BARE_JID) {
            EntityBareJid entityBareJid = userJID.asEntityBareJidIfPossible();
            if (entityBareJid != null) {
                match = (Chat) this.baseJidChats.get(entityBareJid);
            }
        }
        return match;
    }

    public Chat getThreadChat(String thread) {
        return (Chat) this.threadChats.get(thread);
    }

    public void addChatListener(ChatManagerListener listener) {
        this.chatManagerListeners.add(listener);
    }

    public void removeChatListener(ChatManagerListener listener) {
        this.chatManagerListeners.remove(listener);
    }

    public Set<ChatManagerListener> getChatListeners() {
        return Collections.unmodifiableSet(this.chatManagerListeners);
    }

    /* access modifiers changed from: private */
    public static void deliverMessage(Chat chat, Message message) {
        chat.deliver(message);
    }

    /* access modifiers changed from: 0000 */
    public void sendMessage(Chat chat, Message message) throws NotConnectedException, InterruptedException {
        for (Entry<MessageListener, StanzaFilter> interceptor : this.interceptors.entrySet()) {
            StanzaFilter filter = (StanzaFilter) interceptor.getValue();
            if (filter != null && filter.accept(message)) {
                ((MessageListener) interceptor.getKey()).processMessage(message);
            }
        }
        connection().sendStanza(message);
    }

    /* access modifiers changed from: 0000 */
    public StanzaCollector createStanzaCollector(Chat chat) {
        return connection().createStanzaCollector((StanzaFilter) new AndFilter(new ThreadFilter(chat.getThreadID()), FromMatchesFilter.create(chat.getParticipant())));
    }

    public void addOutgoingMessageInterceptor(MessageListener messageInterceptor) {
        addOutgoingMessageInterceptor(messageInterceptor, null);
    }

    public void addOutgoingMessageInterceptor(MessageListener messageInterceptor, StanzaFilter filter) {
        if (messageInterceptor != null) {
            this.interceptors.put(messageInterceptor, filter);
        }
    }

    private static String nextID() {
        return UUID.randomUUID().toString();
    }

    public static void setDefaultMatchMode(MatchMode mode) {
        defaultMatchMode = mode;
    }

    public static void setDefaultIsNormalIncluded(boolean allowNormal) {
        defaultIsNormalInclude = allowNormal;
    }
}
