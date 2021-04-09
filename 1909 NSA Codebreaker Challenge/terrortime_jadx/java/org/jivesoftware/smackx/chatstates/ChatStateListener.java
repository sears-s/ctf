package org.jivesoftware.smackx.chatstates;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;

public interface ChatStateListener {
    void stateChanged(Chat chat, ChatState chatState, Message message);
}
