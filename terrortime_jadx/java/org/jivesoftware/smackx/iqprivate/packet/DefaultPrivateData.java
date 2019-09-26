package org.jivesoftware.smackx.iqprivate.packet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DefaultPrivateData implements PrivateData {
    private final String elementName;
    private Map<String, String> map;
    private final String namespace;

    public DefaultPrivateData(String elementName2, String namespace2) {
        this.elementName = elementName2;
        this.namespace = namespace2;
    }

    public String getElementName() {
        return this.elementName;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append('<');
        buf.append(this.elementName);
        buf.append(" xmlns=\"");
        buf.append(this.namespace);
        buf.append("\">");
        Iterator it = getNames().iterator();
        while (true) {
            String str = "</";
            if (it.hasNext()) {
                String name = (String) it.next();
                String value = getValue(name);
                buf.append('<');
                buf.append(name);
                buf.append('>');
                buf.append(value);
                buf.append(str);
                buf.append(name);
                buf.append('>');
            } else {
                buf.append(str);
                buf.append(this.elementName);
                buf.append('>');
                return buf.toString();
            }
        }
    }

    public synchronized Set<String> getNames() {
        if (this.map == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(this.map.keySet());
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
