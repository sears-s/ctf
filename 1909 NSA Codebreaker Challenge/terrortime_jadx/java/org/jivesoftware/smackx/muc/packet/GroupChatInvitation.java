package org.jivesoftware.smackx.muc.packet;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class GroupChatInvitation implements ExtensionElement {
    public static final String ELEMENT = "x";
    public static final String NAMESPACE = "jabber:x:conference";
    private final String roomAddress;

    public static class Provider extends ExtensionElementProvider<GroupChatInvitation> {
        public GroupChatInvitation parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
            String roomAddress = parser.getAttributeValue(BuildConfig.FLAVOR, "jid");
            parser.next();
            return new GroupChatInvitation(roomAddress);
        }
    }

    public GroupChatInvitation(String roomAddress2) {
        this.roomAddress = roomAddress2;
    }

    public String getRoomAddress() {
        return this.roomAddress;
    }

    public String getElementName() {
        return "x";
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.attribute("jid", getRoomAddress());
        xml.closeEmptyElement();
        return xml;
    }

    @Deprecated
    public static GroupChatInvitation getFrom(Stanza packet) {
        return from(packet);
    }

    public static GroupChatInvitation from(Stanza packet) {
        return (GroupChatInvitation) packet.getExtension("x", NAMESPACE);
    }
}
