package org.jivesoftware.smackx.address.packet;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.jingle_filetransfer.element.JingleFileTransferChild;
import org.jivesoftware.smackx.reference.element.ReferenceElement;
import org.jxmpp.jid.Jid;

public class MultipleAddresses implements ExtensionElement {
    public static final String ELEMENT = "addresses";
    public static final String NAMESPACE = "http://jabber.org/protocol/address";
    private final List<Address> addresses = new ArrayList();

    public static final class Address implements NamedElement {
        public static final String ELEMENT = "address";
        private boolean delivered;
        private String description;
        private Jid jid;
        private String node;
        private final Type type;
        private String uri;

        private Address(Type type2) {
            this.type = type2;
        }

        public Type getType() {
            return this.type;
        }

        public Jid getJid() {
            return this.jid;
        }

        /* access modifiers changed from: private */
        public void setJid(Jid jid2) {
            this.jid = jid2;
        }

        public String getNode() {
            return this.node;
        }

        /* access modifiers changed from: private */
        public void setNode(String node2) {
            this.node = node2;
        }

        public String getDescription() {
            return this.description;
        }

        /* access modifiers changed from: private */
        public void setDescription(String description2) {
            this.description = description2;
        }

        public boolean isDelivered() {
            return this.delivered;
        }

        /* access modifiers changed from: private */
        public void setDelivered(boolean delivered2) {
            this.delivered = delivered2;
        }

        public String getUri() {
            return this.uri;
        }

        /* access modifiers changed from: private */
        public void setUri(String uri2) {
            this.uri = uri2;
        }

        public String getElementName() {
            return ELEMENT;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder buf = new XmlStringBuilder();
            buf.halfOpenElement((NamedElement) this).attribute("type", (Enum<?>) this.type);
            buf.optAttribute("jid", (CharSequence) this.jid);
            buf.optAttribute(NodeElement.ELEMENT, this.node);
            buf.optAttribute(JingleFileTransferChild.ELEM_DESC, this.description);
            String str = this.description;
            if (str != null && str.trim().length() > 0) {
                buf.append((CharSequence) " desc=\"");
                buf.append((CharSequence) this.description).append('\"');
            }
            buf.optBooleanAttribute("delivered", this.delivered);
            buf.optAttribute(ReferenceElement.ATTR_URI, this.uri);
            buf.closeEmptyElement();
            return buf;
        }
    }

    public enum Type {
        bcc,
        cc,
        noreply,
        replyroom,
        replyto,
        to,
        ofrom
    }

    public void addAddress(Type type, Jid jid, String node, String desc, boolean delivered, String uri) {
        Address address = new Address(type);
        address.setJid(jid);
        address.setNode(node);
        address.setDescription(desc);
        address.setDelivered(delivered);
        address.setUri(uri);
        this.addresses.add(address);
    }

    public void setNoReply() {
        this.addresses.add(new Address(Type.noreply));
    }

    public List<Address> getAddressesOfType(Type type) {
        List<Address> answer = new ArrayList<>(this.addresses.size());
        for (Address address : this.addresses) {
            if (address.getType().equals(type)) {
                answer.add(address);
            }
        }
        return answer;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder buf = new XmlStringBuilder((ExtensionElement) this);
        buf.rightAngleBracket();
        for (Address address : this.addresses) {
            buf.append(address.toXML((String) null));
        }
        buf.closeElement((NamedElement) this);
        return buf;
    }
}
