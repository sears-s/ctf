package org.jivesoftware.smackx.pubsub;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jxmpp.jid.BareJid;

public class Affiliation implements ExtensionElement {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final String ELEMENT = "affiliation";
    private final Type affiliation;
    private final BareJid jid;
    private final AffiliationNamespace namespace;
    private final String node;

    public enum AffiliationNamespace {
        basic(PubSubElementType.AFFILIATIONS),
        owner(PubSubElementType.AFFILIATIONS_OWNER);
        
        public final PubSubElementType type;

        private AffiliationNamespace(PubSubElementType type2) {
            this.type = type2;
        }

        public static AffiliationNamespace fromXmlns(String xmlns) {
            AffiliationNamespace[] values;
            for (AffiliationNamespace affiliationsNamespace : values()) {
                if (affiliationsNamespace.type.getNamespace().getXmlns().equals(xmlns)) {
                    return affiliationsNamespace;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid affiliations namespace: ");
            sb.append(xmlns);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public enum Type {
        member,
        none,
        outcast,
        owner,
        publisher
    }

    public Affiliation(String node2, Type affiliation2) {
        this(node2, affiliation2, affiliation2 == null ? AffiliationNamespace.basic : AffiliationNamespace.owner);
    }

    public Affiliation(String node2, Type affiliation2, AffiliationNamespace namespace2) {
        this.node = (String) StringUtils.requireNotNullOrEmpty(node2, "node must not be null or empty");
        this.affiliation = affiliation2;
        this.jid = null;
        this.namespace = (AffiliationNamespace) Objects.requireNonNull(namespace2);
    }

    public Affiliation(BareJid jid2, Type affiliation2) {
        this(jid2, affiliation2, AffiliationNamespace.owner);
    }

    public Affiliation(BareJid jid2, Type affiliation2, AffiliationNamespace namespace2) {
        this.jid = jid2;
        this.affiliation = affiliation2;
        this.node = null;
        this.namespace = namespace2;
    }

    @Deprecated
    public String getNodeId() {
        return getNode();
    }

    public String getNode() {
        return this.node;
    }

    @Deprecated
    public Type getType() {
        return getAffiliation();
    }

    public Type getAffiliation() {
        return this.affiliation;
    }

    public BareJid getJid() {
        return this.jid;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return getPubSubNamespace().getXmlns();
    }

    public PubSubNamespace getPubSubNamespace() {
        return this.namespace.type.getNamespace();
    }

    public boolean isAffiliationModification() {
        if (this.jid == null || this.affiliation == null) {
            return false;
        }
        return true;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.optAttribute(NodeElement.ELEMENT, this.node);
        xml.optAttribute("jid", (CharSequence) this.jid);
        xml.optAttribute(ELEMENT, (Enum<?>) this.affiliation);
        xml.closeEmptyElement();
        return xml;
    }
}
