package org.jivesoftware.smack.chat;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaCollector;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.util.StringUtils;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;

@Deprecated
public class Chat {
    private final ChatManager chatManager;
    private final Set<ChatMessageListener> listeners = new CopyOnWriteArraySet();
    private final EntityJid participant;
    private final String threadID;

    Chat(ChatManager chatManager2, EntityJid participant2, String threadID2) {
        if (!StringUtils.isEmpty(threadID2)) {
            this.chatManager = chatManager2;
            this.participant = participant2;
            this.threadID = threadID2;
            return;
        }
        throw new IllegalArgumentException("Thread ID must not be null");
    }

    public String getThreadID() {
        return this.threadID;
    }

    public EntityJid getParticipant() {
        return this.participant;
    }

    public void sendMessage(String text) throws NotConnectedException, InterruptedException {
        Message message = new Message();
        message.setBody(text);
        sendMessage(message);
    }

    public void sendMessage(Message message) throws NotConnectedException, InterruptedException {
        message.setTo((Jid) this.participant);
        message.setType(Type.chat);
        message.setThread(this.threadID);
        this.chatManager.sendMessage(this, message);
    }

    public void addMessageListener(ChatMessageListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }

    public void removeMessageListener(ChatMessageListener listener) {
        this.listeners.remove(listener);
    }

    public void close() {
        this.chatManager.closeChat(this);
        this.listeners.clear();
    }

    public Set<ChatMessageListener> getListeners() {
        return Collections.unmodifiableSet(this.listeners);
    }

    public StanzaCollector createCollector() {
        return this.chatManager.createStanzaCollector(this);
    }

    /* access modifiers changed from: 0000 */
    public void deliver(Message message) {
        message.setThread(this.threadID);
        for (ChatMessageListener listener : this.listeners) {
            listener.processMessage(this, message);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Chat [(participant=");
        sb.append(this.participant);
        sb.append("), (thread=");
        sb.append(this.threadID);
        sb.append(")]");
        return sb.toString();
    }

    public int hashCode() {
        return (((1 * 31) + this.threadID.hashCode()) * 31) + this.participant.hashCode();
    }

    public boolean equals(Object obj) {
        return (obj instanceof Chat) && this.threadID.equals(((Chat) obj).getThreadID()) && this.participant.equals((CharSequence) ((Chat) obj).getParticipant());
    }
}
