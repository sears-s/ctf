package org.jivesoftware.smack.packet;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jivesoftware.smack.util.MultiMap;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jxmpp.util.XmppStringUtils;

public final class StandardExtensionElement implements ExtensionElement {
    private final Map<String, String> attributes;
    private final MultiMap<String, StandardExtensionElement> elements;
    private final String name;
    private final String namespace;
    private final String text;
    private XmlStringBuilder xmlCache;

    public static final class Builder {
        private Map<String, String> attributes;
        private MultiMap<String, StandardExtensionElement> elements;
        private final String name;
        private final String namespace;
        private String text;

        private Builder(String name2, String namespace2) {
            this.name = name2;
            this.namespace = namespace2;
        }

        public Builder addAttribute(String name2, String value) {
            StringUtils.requireNotNullOrEmpty(name2, "Attribute name must be set");
            Objects.requireNonNull(value, "Attribute value must be not null");
            if (this.attributes == null) {
                this.attributes = new LinkedHashMap();
            }
            this.attributes.put(name2, value);
            return this;
        }

        public Builder addAttributes(Map<String, String> attributes2) {
            if (this.attributes == null) {
                this.attributes = new LinkedHashMap(attributes2.size());
            }
            this.attributes.putAll(attributes2);
            return this;
        }

        public Builder setText(String text2) {
            this.text = (String) Objects.requireNonNull(text2, "Text must be not null");
            return this;
        }

        public Builder addElement(StandardExtensionElement element) {
            Objects.requireNonNull(element, "Element must not be null");
            if (this.elements == null) {
                this.elements = new MultiMap<>();
            }
            this.elements.put(XmppStringUtils.generateKey(element.getElementName(), element.getNamespace()), element);
            return this;
        }

        public Builder addElement(String name2, String textValue) {
            return addElement(StandardExtensionElement.builder(name2, this.namespace).setText(textValue).build());
        }

        public StandardExtensionElement build() {
            StandardExtensionElement standardExtensionElement = new StandardExtensionElement(this.name, this.namespace, this.attributes, this.text, this.elements);
            return standardExtensionElement;
        }
    }

    public StandardExtensionElement(String name2, String namespace2) {
        this(name2, namespace2, null, null, null);
    }

    private StandardExtensionElement(String name2, String namespace2, Map<String, String> attributes2, String text2, MultiMap<String, StandardExtensionElement> elements2) {
        this.name = (String) StringUtils.requireNotNullOrEmpty(name2, "Name must not be null or empty");
        this.namespace = (String) StringUtils.requireNotNullOrEmpty(namespace2, "Namespace must not be null or empty");
        if (attributes2 == null) {
            this.attributes = Collections.emptyMap();
        } else {
            this.attributes = attributes2;
        }
        this.text = text2;
        this.elements = elements2;
    }

    public String getElementName() {
        return this.name;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getAttributeValue(String attribute) {
        return (String) this.attributes.get(attribute);
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    public StandardExtensionElement getFirstElement(String element, String namespace2) {
        if (this.elements == null) {
            return null;
        }
        return (StandardExtensionElement) this.elements.getFirst(XmppStringUtils.generateKey(element, namespace2));
    }

    public StandardExtensionElement getFirstElement(String element) {
        return getFirstElement(element, this.namespace);
    }

    public List<StandardExtensionElement> getElements(String element, String namespace2) {
        if (this.elements == null) {
            return null;
        }
        return this.elements.getAll(XmppStringUtils.generateKey(element, namespace2));
    }

    public List<StandardExtensionElement> getElements(String element) {
        return getElements(element, this.namespace);
    }

    public List<StandardExtensionElement> getElements() {
        MultiMap<String, StandardExtensionElement> multiMap = this.elements;
        if (multiMap == null) {
            return Collections.emptyList();
        }
        return multiMap.values();
    }

    public String getText() {
        return this.text;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xmlStringBuilder = this.xmlCache;
        if (xmlStringBuilder != null) {
            return xmlStringBuilder;
        }
        XmlStringBuilder xml = new XmlStringBuilder(this, enclosingNamespace);
        for (Entry<String, String> entry : this.attributes.entrySet()) {
            xml.attribute((String) entry.getKey(), (String) entry.getValue());
        }
        xml.rightAngleBracket();
        xml.optEscape(this.text);
        MultiMap<String, StandardExtensionElement> multiMap = this.elements;
        if (multiMap != null) {
            for (Entry<String, StandardExtensionElement> entry2 : multiMap.entrySet()) {
                xml.append(((StandardExtensionElement) entry2.getValue()).toXML(getNamespace()));
            }
        }
        xml.closeElement((NamedElement) this);
        this.xmlCache = xml;
        return xml;
    }

    public static Builder builder(String name2, String namespace2) {
        return new Builder(name2, namespace2);
    }
}
