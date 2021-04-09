package org.jivesoftware.smackx.iot.data.element;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iot.element.NodeInfo;

public class NodeElement implements NamedElement {
    public static final String ELEMENT = "node";
    private final NodeInfo nodeInfo;
    private final List<TimestampElement> timestampElements;

    public NodeElement(NodeInfo nodeInfo2, TimestampElement timestampElement) {
        this(nodeInfo2, Collections.singletonList(timestampElement));
    }

    public NodeElement(NodeInfo nodeInfo2, List<TimestampElement> timestampElements2) {
        this.nodeInfo = nodeInfo2;
        this.timestampElements = Collections.unmodifiableList(timestampElements2);
    }

    public List<TimestampElement> getTimestampElements() {
        return this.timestampElements;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
        this.nodeInfo.appendTo(xml);
        xml.rightAngleBracket();
        xml.append((Collection<? extends Element>) this.timestampElements);
        xml.closeElement((NamedElement) this);
        return xml;
    }
}
