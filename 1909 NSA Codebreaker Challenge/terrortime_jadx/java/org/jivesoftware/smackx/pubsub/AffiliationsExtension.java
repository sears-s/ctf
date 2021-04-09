package org.jivesoftware.smackx.pubsub;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.pubsub.Affiliation.AffiliationNamespace;

public class AffiliationsExtension extends NodeExtension {
    protected List<Affiliation> items;
    private final String node;

    public AffiliationsExtension() {
        this(null);
    }

    public AffiliationsExtension(List<Affiliation> subList) {
        this(subList, (String) null);
    }

    public AffiliationsExtension(AffiliationNamespace affiliationsNamespace, List<Affiliation> subList) {
        this(affiliationsNamespace, subList, null);
    }

    public AffiliationsExtension(List<Affiliation> subList, String node2) {
        this(AffiliationNamespace.basic, subList, node2);
    }

    public AffiliationsExtension(AffiliationNamespace affiliationsNamespace, List<Affiliation> subList, String node2) {
        super(affiliationsNamespace.type);
        this.items = Collections.emptyList();
        this.items = subList;
        this.node = node2;
    }

    public List<Affiliation> getAffiliations() {
        return this.items;
    }

    public CharSequence toXML(String enclosingNamespace) {
        List<Affiliation> list = this.items;
        if (list == null || list.size() == 0) {
            return super.toXML(enclosingNamespace);
        }
        XmlStringBuilder xml = new XmlStringBuilder();
        xml.halfOpenElement(getElementName());
        xml.optAttribute(NodeElement.ELEMENT, this.node);
        xml.rightAngleBracket();
        xml.append((Collection<? extends Element>) this.items);
        xml.closeElement((NamedElement) this);
        return xml;
    }
}
