package org.jivesoftware.smack.packet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jivesoftware.smack.util.XmlStringBuilder;

@Deprecated
public class DefaultExtensionElement implements ExtensionElement {
    private String elementName;
    private Map<String, String> map;
    private String namespace;

    public DefaultExtensionElement(String elementName2, String namespace2) {
        this.elementName = elementName2;
        this.namespace = namespace2;
    }

    public String getElementName() {
        return this.elementName;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder buf = new XmlStringBuilder();
        buf.halfOpenElement(this.elementName).xmlnsAttribute(this.namespace).rightAngleBracket();
        for (String name : getNames()) {
            buf.element(name, getValue(name));
        }
        buf.closeElement(this.elementName);
        return buf;
    }

    public synchronized Collection<String> getNames() {
        if (this.map == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashMap(this.map).keySet());
    }

    public synchronized String getValue(String name) {
        if (this.map == null) {
            return null;
        }
        return (String) this.map.get(name);
    }

    public synchronized void setValue(String name, String value) {
        if (this.map == null) {
            this.map = new HashMap();
        }
        this.map.put(name, value);
    }
}
