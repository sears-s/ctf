package org.jivesoftware.smackx.receipts;

import java.io.IOException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.id.StanzaIdUtil;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DeliveryReceiptRequest implements ExtensionElement {
    public static final String ELEMENT = "request";

    public static class Provider extends ExtensionElementProvider<DeliveryReceiptRequest> {
        public DeliveryReceiptRequest parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
            return new DeliveryReceiptRequest();
        }
    }

    public String getElementName() {
        return "request";
    }

    public String getNamespace() {
        return DeliveryReceipt.NAMESPACE;
    }

    public String toXML(String enclosingNamespace) {
        return "<request xmlns='urn:xmpp:receipts'/>";
    }

    @Deprecated
    public static DeliveryReceiptRequest getFrom(Stanza p) {
        return from(p);
    }

    public static DeliveryReceiptRequest from(Stanza packet) {
        return (DeliveryReceiptRequest) packet.getExtension("request", DeliveryReceipt.NAMESPACE);
    }

    public static String addTo(Message message) {
        if (message.getStanzaId() == null) {
            message.setStanzaId(StanzaIdUtil.newStanzaId());
        }
        message.addExtension(new DeliveryReceiptRequest());
        return message.getStanzaId();
    }
}
