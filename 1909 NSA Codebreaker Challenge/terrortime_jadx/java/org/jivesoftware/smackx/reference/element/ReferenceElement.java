package org.jivesoftware.smackx.reference.element;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.reference.ReferenceManager;
import org.jxmpp.jid.BareJid;

public class ReferenceElement implements ExtensionElement {
    public static final String ATTR_ANCHOR = "anchor";
    public static final String ATTR_BEGIN = "begin";
    public static final String ATTR_END = "end";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_URI = "uri";
    public static final String ELEMENT = "reference";
    private final String anchor;
    private final Integer begin;
    private final ExtensionElement child;
    private final Integer end;
    private final Type type;
    private final URI uri;

    public enum Type {
        mention,
        data
    }

    public ReferenceElement(Integer begin2, Integer end2, Type type2, String anchor2, URI uri2, ExtensionElement child2) {
        if (begin2 != null && begin2.intValue() < 0) {
            throw new IllegalArgumentException("Attribute 'begin' MUST NOT be smaller than 0.");
        } else if (end2 != null && end2.intValue() < 0) {
            throw new IllegalArgumentException("Attribute 'end' MUST NOT be smaller than 0.");
        } else if (begin2 == null || end2 == null || begin2.intValue() < end2.intValue()) {
            Objects.requireNonNull(type2);
            this.begin = begin2;
            this.end = end2;
            this.type = type2;
            this.anchor = anchor2;
            this.uri = uri2;
            this.child = child2;
        } else {
            throw new IllegalArgumentException("Attribute 'begin' MUST be smaller than attribute 'end'.");
        }
    }

    public ReferenceElement(Integer begin2, Integer end2, Type type2, String anchor2, URI uri2) {
        this(begin2, end2, type2, anchor2, uri2, null);
    }

    public Integer getBegin() {
        return this.begin;
    }

    public Integer getEnd() {
        return this.end;
    }

    public Type getType() {
        return this.type;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public URI getUri() {
        return this.uri;
    }

    public static void addMention(Stanza stanza, int begin2, int end2, BareJid jid) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("xmpp:");
            sb.append(jid.toString());
            ReferenceElement reference = new ReferenceElement(Integer.valueOf(begin2), Integer.valueOf(end2), Type.mention, null, new URI(sb.toString()));
            stanza.addExtension(reference);
        } catch (URISyntaxException e) {
            throw new AssertionError("Cannot create URI from bareJid.");
        }
    }

    public static List<ReferenceElement> getReferencesFromStanza(Stanza stanza) {
        List<ReferenceElement> references = new ArrayList<>();
        for (ExtensionElement e : stanza.getExtensions(ELEMENT, ReferenceManager.NAMESPACE)) {
            references.add((ReferenceElement) e);
        }
        return references;
    }

    public static boolean containsReferences(Stanza stanza) {
        return getReferencesFromStanza(stanza).size() > 0;
    }

    public String getNamespace() {
        return ReferenceManager.NAMESPACE;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xmlStringBuilder = new XmlStringBuilder((ExtensionElement) this);
        Integer num = this.begin;
        int i = -1;
        XmlStringBuilder optIntAttribute = xmlStringBuilder.optIntAttribute(ATTR_BEGIN, num != null ? num.intValue() : -1);
        Integer num2 = this.end;
        if (num2 != null) {
            i = num2.intValue();
        }
        XmlStringBuilder optAttribute = optIntAttribute.optIntAttribute("end", i).attribute("type", this.type.toString()).optAttribute(ATTR_ANCHOR, this.anchor);
        URI uri2 = this.uri;
        XmlStringBuilder xml = optAttribute.optAttribute(ATTR_URI, uri2 != null ? uri2.toString() : null);
        if (this.child == null) {
            return xml.closeEmptyElement();
        }
        return xml.rightAngleBracket().append(this.child.toXML(null)).closeElement((NamedElement) this);
    }
}
