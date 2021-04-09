package org.jivesoftware.smackx.mam.element;

import java.util.List;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jxmpp.jid.Jid;

public class MamElements {
    public static final String NAMESPACE = "urn:xmpp:mam:1";

    public static class AlwaysJidListElement implements Element {
        private final List<Jid> alwaysJids;

        AlwaysJidListElement(List<Jid> alwaysJids2) {
            this.alwaysJids = alwaysJids2;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            String str = "always";
            xml.openElement(str);
            for (Jid jid : this.alwaysJids) {
                xml.element("jid", (CharSequence) jid);
            }
            xml.closeElement(str);
            return xml;
        }
    }

    public static class MamResultExtension implements ExtensionElement {
        public static final String ELEMENT = "result";
        private final Forwarded forwarded;
        private final String id;
        private String queryId;

        public MamResultExtension(String queryId2, String id2, Forwarded forwarded2) {
            if (StringUtils.isEmpty(id2)) {
                throw new IllegalArgumentException("id must not be null or empty");
            } else if (forwarded2 != null) {
                this.id = id2;
                this.forwarded = forwarded2;
                this.queryId = queryId2;
            } else {
                throw new IllegalArgumentException("forwarded must no be null");
            }
        }

        public String getId() {
            return this.id;
        }

        public Forwarded getForwarded() {
            return this.forwarded;
        }

        public final String getQueryId() {
            return this.queryId;
        }

        public String getElementName() {
            return ELEMENT;
        }

        public final String getNamespace() {
            return "urn:xmpp:mam:1";
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement((NamedElement) this);
            xml.xmlnsAttribute("urn:xmpp:mam:1");
            xml.optAttribute("queryid", getQueryId());
            xml.optAttribute("id", getId());
            xml.rightAngleBracket();
            xml.element(getForwarded());
            xml.closeElement((NamedElement) this);
            return xml;
        }

        public static MamResultExtension from(Message message) {
            return (MamResultExtension) message.getExtension(ELEMENT, "urn:xmpp:mam:1");
        }
    }

    public static class NeverJidListElement implements Element {
        private List<Jid> neverJids;

        public NeverJidListElement(List<Jid> neverJids2) {
            this.neverJids = neverJids2;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            String str = "never";
            xml.openElement(str);
            for (Jid jid : this.neverJids) {
                xml.element("jid", (CharSequence) jid);
            }
            xml.closeElement(str);
            return xml;
        }
    }
}
