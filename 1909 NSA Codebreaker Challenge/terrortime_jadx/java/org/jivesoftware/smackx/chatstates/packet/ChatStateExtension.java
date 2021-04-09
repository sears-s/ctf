package org.jivesoftware.smackx.chatstates.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.chatstates.ChatState;

public class ChatStateExtension implements ExtensionElement {
    public static final String NAMESPACE = "http://jabber.org/protocol/chatstates";
    private final ChatState state;

    public ChatStateExtension(ChatState state2) {
        this.state = state2;
    }

    public String getElementName() {
        return this.state.name();
    }

    public String getNamespace() {
        return "http://jabber.org/protocol/chatstates";
    }

    public ChatState getChatState() {
        return this.state;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.closeEmptyElement();
        return xml;
    }
}
