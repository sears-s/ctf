package org.jivesoftware.smackx.pubsub.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jxmpp.jid.Jid;

public class PubSub extends IQ {
    public static final String ELEMENT = "pubsub";
    public static final String NAMESPACE = "http://jabber.org/protocol/pubsub";

    public PubSub() {
        super("pubsub", "http://jabber.org/protocol/pubsub");
    }

    public PubSub(PubSubNamespace ns) {
        super("pubsub", ns.getXmlns());
    }

    public PubSub(Jid to, Type type, PubSubNamespace ns) {
        super("pubsub", (ns == null ? PubSubNamespace.basic : ns).getXmlns());
        setTo(to);
        setType(type);
    }

    public <PE extends ExtensionElement> PE getExtension(PubSubElementType elem) {
        return getExtension(elem.getElementName(), elem.getNamespace().getXmlns());
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        return xml;
    }

    public static PubSub createPubsubPacket(Jid to, Type type, NodeExtension extension) {
        PubSub pubSub = new PubSub(to, type, extension.getPubSubNamespace());
        pubSub.addExtension(extension);
        return pubSub;
    }
}
