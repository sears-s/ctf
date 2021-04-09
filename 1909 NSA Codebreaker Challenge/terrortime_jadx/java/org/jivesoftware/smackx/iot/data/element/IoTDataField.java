package org.jivesoftware.smackx.iot.data.element;

import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public abstract class IoTDataField implements NamedElement {
    private final String name;
    private final Type type;
    private String valueString;

    public static class BooleanField extends IoTDataField {
        private final boolean value;

        public /* bridge */ /* synthetic */ CharSequence toXML(String str) {
            return IoTDataField.super.toXML(str);
        }

        public BooleanField(String name, boolean value2) {
            super(Type.bool, name);
            this.value = value2;
        }

        /* access modifiers changed from: protected */
        public String getValueInternal() {
            return Boolean.toString(this.value);
        }

        public boolean getValue() {
            return this.value;
        }
    }

    public static class IntField extends IoTDataField {
        private final int value;

        public /* bridge */ /* synthetic */ CharSequence toXML(String str) {
            return IoTDataField.super.toXML(str);
        }

        public IntField(String name, int value2) {
            super(Type.integer, name);
            this.value = value2;
        }

        /* access modifiers changed from: protected */
        public String getValueInternal() {
            return Integer.toString(this.value);
        }

        public int getValue() {
            return this.value;
        }
    }

    enum Type {
        integer("int"),
        bool("boolean");
        
        /* access modifiers changed from: private */
        public final String stringRepresentation;

        private Type(String stringRepresentation2) {
            this.stringRepresentation = stringRepresentation2;
        }
    }

    /* access modifiers changed from: protected */
    public abstract String getValueInternal();

    protected IoTDataField(Type type2, String name2) {
        this.type = type2;
        this.name = name2;
    }

    public final String getName() {
        return this.name;
    }

    public final String getElementName() {
        return this.type.stringRepresentation;
    }

    public final XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
        String str = "value";
        xml.attribute("name", this.name).attribute(str, getValueString());
        xml.closeEmptyElement();
        return xml;
    }

    public final String getValueString() {
        if (this.valueString == null) {
            this.valueString = getValueInternal();
        }
        return this.valueString;
    }
}
