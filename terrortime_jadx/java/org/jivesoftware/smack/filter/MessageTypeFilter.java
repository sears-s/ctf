package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;

public final class MessageTypeFilter extends FlexibleStanzaTypeFilter<Message> {
    public static final StanzaFilter CHAT = new MessageTypeFilter(Type.chat);
    public static final StanzaFilter ERROR = new MessageTypeFilter(Type.error);
    public static final StanzaFilter GROUPCHAT = new MessageTypeFilter(Type.groupchat);
    public static final StanzaFilter HEADLINE = new MessageTypeFilter(Type.headline);
    public static final StanzaFilter NORMAL = new MessageTypeFilter(Type.normal);
    public static final StanzaFilter NORMAL_OR_CHAT = new OrFilter(NORMAL, CHAT);
    public static final StanzaFilter NORMAL_OR_CHAT_OR_HEADLINE = new OrFilter(NORMAL_OR_CHAT, HEADLINE);
    private final Type type;

    private MessageTypeFilter(Type type2) {
        super(Message.class);
        this.type = type2;
    }

    /* access modifiers changed from: protected */
    public boolean acceptSpecific(Message message) {
        return message.getType() == this.type;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": type=");
        sb.append(this.type);
        return sb.toString();
    }
}
