package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

public class ThreadFilter extends FlexibleStanzaTypeFilter<Message> implements StanzaFilter {
    private final String thread;

    public ThreadFilter(String thread2) {
        StringUtils.requireNotNullOrEmpty(thread2, "Thread must not be null or empty.");
        this.thread = thread2;
    }

    /* access modifiers changed from: protected */
    public boolean acceptSpecific(Message message) {
        return this.thread.equals(message.getThread());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": thread=");
        sb.append(this.thread);
        return sb.toString();
    }
}
