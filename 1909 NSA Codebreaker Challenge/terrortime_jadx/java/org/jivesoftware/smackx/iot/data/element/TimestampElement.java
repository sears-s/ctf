package org.jivesoftware.smackx.iot.data.element;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class TimestampElement implements NamedElement {
    public static final String ELEMENT = "timestamp";
    private final Date date;
    private final List<? extends IoTDataField> fields;

    public TimestampElement(Date date2, List<? extends IoTDataField> fields2) {
        this.date = date2;
        this.fields = Collections.unmodifiableList(fields2);
    }

    public List<? extends IoTDataField> getDataFields() {
        return this.fields;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
        xml.attribute("value", this.date);
        xml.rightAngleBracket();
        xml.append((Collection<? extends Element>) this.fields);
        xml.closeElement((NamedElement) this);
        return xml;
    }
}
