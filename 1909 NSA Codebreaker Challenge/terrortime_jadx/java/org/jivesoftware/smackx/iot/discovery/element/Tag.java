package org.jivesoftware.smackx.iot.discovery.element;

import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class Tag implements NamedElement {
    private final String name;
    private final Type type;
    private final String value;

    public enum Type {
        str,
        num
    }

    public Tag(String name2, Type type2, String value2) {
        this.name = (String) StringUtils.requireNotNullOrEmpty(name2, "name must not be null or empty");
        this.type = (Type) Objects.requireNonNull(type2);
        this.value = (String) StringUtils.requireNotNullOrEmpty(value2, "value must not be null or empty");
        if (this.name.length() > 32) {
            throw new IllegalArgumentException("Meta Tag names must not be longer then 32 characters (XEP-0347 § 5.2");
        } else if (this.type == Type.str && this.value.length() > 128) {
            throw new IllegalArgumentException("Meta Tag string values must not be longer then 128 characters (XEP-0347 § 5.2");
        }
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
        xml.attribute("name", this.name);
        xml.attribute("value", this.value);
        xml.closeEmptyElement();
        return xml;
    }

    public String getElementName() {
        return getType().toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name);
        sb.append('(');
        sb.append(this.type);
        sb.append("):");
        sb.append(this.value);
        return sb.toString();
    }
}
