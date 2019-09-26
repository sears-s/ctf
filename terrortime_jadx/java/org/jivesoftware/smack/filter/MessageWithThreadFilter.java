package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

public final class MessageWithThreadFilter extends FlexibleStanzaTypeFilter<Message> {
    public static final StanzaFilter INSTANCE = new MessageWithThreadFilter();

    private MessageWithThreadFilter() {
        super(Message.class);
    }

    /* access modifiers changed from: protected */
    public boolean acceptSpecific(Message message) {
        return StringUtils.isNotEmpty((CharSequence) message.getThread());
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}
