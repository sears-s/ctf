package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.parts.Resourcepart;

public final class Bind extends IQ {
    public static final String ELEMENT = "bind";
    public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-bind";
    private final EntityFullJid jid;
    private final Resourcepart resource;

    public static final class Feature implements ExtensionElement {
        public static final Feature INSTANCE = new Feature();

        private Feature() {
        }

        public String getElementName() {
            return Bind.ELEMENT;
        }

        public String getNamespace() {
            return Bind.NAMESPACE;
        }

        public String toXML(String enclosingNamespace) {
            return "<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/>";
        }
    }

    private Bind(Resourcepart resource2, EntityFullJid jid2) {
        super(ELEMENT, NAMESPACE);
        this.resource = resource2;
        this.jid = jid2;
    }

    public Resourcepart getResource() {
        return this.resource;
    }

    public EntityFullJid getJid() {
        return this.jid;
    }

    public static Bind newSet(Resourcepart resource2) {
        Bind bind = new Bind(resource2, null);
        bind.setType(Type.set);
        return bind;
    }

    public static Bind newResult(EntityFullJid jid2) {
        return new Bind(null, jid2);
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.optElement("resource", (CharSequence) this.resource);
        xml.optElement("jid", (CharSequence) this.jid);
        return xml;
    }
}
