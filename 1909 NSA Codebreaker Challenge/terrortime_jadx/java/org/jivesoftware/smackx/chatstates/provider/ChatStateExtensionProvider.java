package org.jivesoftware.smackx.chatstates.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.xmlpull.v1.XmlPullParser;

public class ChatStateExtensionProvider extends ExtensionElementProvider<ChatStateExtension> {
    public ChatStateExtension parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new ChatStateExtension(ChatState.valueOf(parser.getName()));
    }
}
