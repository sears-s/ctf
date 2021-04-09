package org.jivesoftware.smackx.iqregister.packet;

import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;

public class Registration extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "jabber:iq:register";
    private final Map<String, String> attributes;
    private final String instructions;

    public static final class Feature implements ExtensionElement {
        public static final String ELEMENT = "register";
        public static final Feature INSTANCE = new Feature();
        public static final String NAMESPACE = "http://jabber.org/features/iq-register";

        private Feature() {
        }

        public String getElementName() {
            return "register";
        }

        public CharSequence toXML(String enclosingNamespace) {
            return "<register xmlns='http://jabber.org/features/iq-register'/>";
        }

        public String getNamespace() {
            return NAMESPACE;
        }
    }

    public Registration() {
        this(null);
    }

    public Registration(Map<String, String> attributes2) {
        this(null, attributes2);
    }

    public Registration(String instructions2, Map<String, String> attributes2) {
        super("query", NAMESPACE);
        this.instructions = instructions2;
        this.attributes = attributes2;
    }

    public String getInstructions() {
        return this.instructions;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.optElement("instructions", this.instructions);
        Map<String, String> map = this.attributes;
        if (map != null && map.size() > 0) {
            for (String name : this.attributes.keySet()) {
                xml.element(name, (String) this.attributes.get(name));
            }
        }
        return xml;
    }
}
