package org.jivesoftware.smackx.iot.data.element;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iot.element.NodeInfo;

public class IoTFieldsExtension implements ExtensionElement {
    public static final String ELEMENT = "fields";
    public static final String NAMESPACE = "urn:xmpp:iot:sensordata";
    private final boolean done;
    private final List<NodeElement> nodes;
    private final int seqNr;

    public IoTFieldsExtension(int seqNr2, boolean done2, NodeElement node) {
        this(seqNr2, done2, Collections.singletonList(node));
    }

    public IoTFieldsExtension(int seqNr2, boolean done2, List<NodeElement> nodes2) {
        this.seqNr = seqNr2;
        this.done = done2;
        this.nodes = Collections.unmodifiableList(nodes2);
    }

    public int getSequenceNr() {
        return this.seqNr;
    }

    public boolean isDone() {
        return this.done;
    }

    public List<NodeElement> getNodes() {
        return this.nodes;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return "urn:xmpp:iot:sensordata";
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.attribute("seqnr", Integer.toString(this.seqNr));
        xml.attribute("done", this.done);
        xml.rightAngleBracket();
        xml.append((Collection<? extends Element>) this.nodes);
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public static IoTFieldsExtension buildFor(int seqNr2, boolean done2, NodeInfo nodeInfo, List<? extends IoTDataField> data) {
        return new IoTFieldsExtension(seqNr2, done2, new NodeElement(nodeInfo, new TimestampElement(new Date(), data)));
    }

    public static IoTFieldsExtension from(Message message) {
        return (IoTFieldsExtension) message.getExtension(ELEMENT, "urn:xmpp:iot:sensordata");
    }
}
