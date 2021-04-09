package org.jivesoftware.smackx.iot.control.element;

import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public abstract class SetData implements NamedElement {
    private final String name;
    private final Type type;
    private final String value;

    public enum Type {
        BOOL,
        INT,
        LONG,
        DOUBLE;
        
        private final String toStringCache;

        public String toString() {
            return this.toStringCache;
        }
    }

    protected SetData(String name2, Type type2, String value2) {
        this.name = name2;
        this.type = type2;
        this.value = value2;
    }

    public final String getName() {
        return this.name;
    }

    public final String getValue() {
        return this.value;
    }

    public final Type getType() {
        return this.type;
    }

    public final String getElementName() {
        return getType().toString();
    }

    public final XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
        xml.attribute("name", this.name);
        xml.attribute("value", this.value);
        xml.closeEmptyElement();
        return xml;
    }
}
