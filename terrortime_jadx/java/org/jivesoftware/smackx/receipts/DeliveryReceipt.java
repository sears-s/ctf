package org.jivesoftware.smackx.receipts;

import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class DeliveryReceipt implements ExtensionElement {
    public static final String ELEMENT = "received";
    public static final String NAMESPACE = "urn:xmpp:receipts";
    private final String id;

    public static class Provider extends EmbeddedExtensionProvider<DeliveryReceipt> {
        /* access modifiers changed from: protected */
        public DeliveryReceipt createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> list) {
            return new DeliveryReceipt((String) attributeMap.get("id"));
        }
    }

    public DeliveryReceipt(String id2) {
        this.id = id2;
    }

    public String getId() {
        return this.id;
    }

    public String getElementName() {
        return "received";
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.optAttribute("id", this.id);
        xml.closeEmptyElement();
        return xml;
    }

    @Deprecated
    public static DeliveryReceipt getFrom(Message p) {
        return from(p);
    }

    public static DeliveryReceipt from(Message message) {
        return (DeliveryReceipt) message.getExtension("received", NAMESPACE);
    }
}
