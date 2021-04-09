package org.jivesoftware.smackx.carbons.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.forward.packet.Forwarded;

public class CarbonExtension implements ExtensionElement {
    public static final String NAMESPACE = "urn:xmpp:carbons:2";
    private final Direction dir;
    private final Forwarded fwd;

    public enum Direction {
        received,
        sent
    }

    public static final class Private implements ExtensionElement {
        public static final String ELEMENT = "private";
        public static final Private INSTANCE = new Private();

        private Private() {
        }

        public String getElementName() {
            return ELEMENT;
        }

        public String getNamespace() {
            return "urn:xmpp:carbons:2";
        }

        public String toXML(String enclosingNamespace) {
            return "<private xmlns='urn:xmpp:carbons:2'/>";
        }

        public static void addTo(Message message) {
            message.addExtension(INSTANCE);
        }
    }

    public CarbonExtension(Direction dir2, Forwarded fwd2) {
        this.dir = dir2;
        this.fwd = fwd2;
    }

    public Direction getDirection() {
        return this.dir;
    }

    public Forwarded getForwarded() {
        return this.fwd;
    }

    public String getElementName() {
        return this.dir.name();
    }

    public String getNamespace() {
        return "urn:xmpp:carbons:2";
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.rightAngleBracket();
        xml.append(this.fwd.toXML((String) null));
        xml.closeElement((NamedElement) this);
        return xml;
    }

    @Deprecated
    public static CarbonExtension getFrom(Message msg) {
        return from(msg);
    }

    public static CarbonExtension from(Message msg) {
        String str = "urn:xmpp:carbons:2";
        CarbonExtension cc = (CarbonExtension) msg.getExtension(Direction.received.name(), str);
        if (cc == null) {
            return (CarbonExtension) msg.getExtension(Direction.sent.name(), str);
        }
        return cc;
    }
}
