package org.jivesoftware.smackx.push_notifications.element;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jxmpp.jid.Jid;

public class PushNotificationsElements {
    public static final String NAMESPACE = "urn:xmpp:push:0";

    public static class RemoteDisablingExtension implements ExtensionElement {
        public static final String ELEMENT = "pubsub";
        public static final String NAMESPACE = "http://jabber.org/protocol/pubsub";
        private final String node;
        private final Jid userJid;

        public RemoteDisablingExtension(String node2, Jid userJid2) {
            this.node = node2;
            this.userJid = userJid2;
        }

        public String getElementName() {
            return "pubsub";
        }

        public String getNamespace() {
            return "http://jabber.org/protocol/pubsub";
        }

        public String getNode() {
            return this.node;
        }

        public Jid getUserJid() {
            return this.userJid;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
            xml.attribute(NodeElement.ELEMENT, this.node);
            xml.rightAngleBracket();
            String str = Affiliation.ELEMENT;
            xml.halfOpenElement(str);
            xml.attribute("jid", (CharSequence) this.userJid);
            xml.attribute(str, PrivacyItem.SUBSCRIPTION_NONE);
            xml.closeEmptyElement();
            xml.closeElement((NamedElement) this);
            return xml;
        }

        public static RemoteDisablingExtension from(Message message) {
            return (RemoteDisablingExtension) message.getExtension("pubsub", "http://jabber.org/protocol/pubsub");
        }
    }
}
