package org.jivesoftware.smackx.jiveproperties.packet;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smack.util.stringencoder.Base64;

public class JivePropertiesExtension implements ExtensionElement {
    public static final String ELEMENT = "properties";
    private static final Logger LOGGER = Logger.getLogger(JivePropertiesExtension.class.getName());
    public static final String NAMESPACE = "http://www.jivesoftware.com/xmlns/xmpp/properties";
    private final Map<String, Object> properties;

    public JivePropertiesExtension() {
        this.properties = new HashMap();
    }

    public JivePropertiesExtension(Map<String, Object> properties2) {
        this.properties = properties2;
    }

    public synchronized Object getProperty(String name) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(name);
    }

    public synchronized void setProperty(String name, Object value) {
        if (value instanceof Serializable) {
            this.properties.put(name, value);
        } else {
            throw new IllegalArgumentException("Value must be serializable");
        }
    }

    public synchronized void deleteProperty(String name) {
        if (this.properties != null) {
            this.properties.remove(name);
        }
    }

    public synchronized Collection<String> getPropertyNames() {
        if (this.properties == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet(this.properties.keySet()));
    }

    public synchronized Map<String, Object> getProperties() {
        if (this.properties == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new HashMap(this.properties));
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public CharSequence toXML(String enclosingNamespace) {
        String valueStr;
        String type;
        String str = "java-object";
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.rightAngleBracket();
        for (String name : getPropertyNames()) {
            Object value = getProperty(name);
            String str2 = "property";
            xml.openElement(str2);
            xml.element("name", name);
            String str3 = "value";
            xml.halfOpenElement(str3);
            if (value instanceof Integer) {
                type = "integer";
                valueStr = Integer.toString(((Integer) value).intValue());
            } else if (value instanceof Long) {
                type = "long";
                valueStr = Long.toString(((Long) value).longValue());
            } else if (value instanceof Float) {
                type = "float";
                valueStr = Float.toString(((Float) value).floatValue());
            } else if (value instanceof Double) {
                type = "double";
                valueStr = Double.toString(((Double) value).doubleValue());
            } else if (value instanceof Boolean) {
                type = "boolean";
                valueStr = Boolean.toString(((Boolean) value).booleanValue());
            } else if (value instanceof String) {
                type = "string";
                valueStr = (String) value;
            } else {
                ByteArrayOutputStream byteStream = null;
                ObjectOutputStream out = null;
                try {
                    ByteArrayOutputStream byteStream2 = new ByteArrayOutputStream();
                    ObjectOutputStream out2 = new ObjectOutputStream(byteStream2);
                    out2.writeObject(value);
                    String type2 = str;
                    String valueStr2 = Base64.encodeToString(byteStream2.toByteArray());
                    try {
                        out2.close();
                    } catch (Exception e) {
                    }
                    try {
                        byteStream2.close();
                    } catch (Exception e2) {
                    }
                    type = type2;
                    valueStr = valueStr2;
                } catch (Exception e3) {
                    LOGGER.log(Level.SEVERE, "Error encoding java object", e3);
                    String type3 = str;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Serializing error: ");
                    sb.append(e3.getMessage());
                    String valueStr3 = sb.toString();
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Exception e4) {
                        }
                    }
                    if (byteStream != null) {
                        try {
                            byteStream.close();
                        } catch (Exception e5) {
                        }
                    }
                    valueStr = valueStr3;
                    type = type3;
                } catch (Throwable th) {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Exception e6) {
                        }
                    }
                    if (byteStream != null) {
                        try {
                            byteStream.close();
                        } catch (Exception e7) {
                        }
                    }
                    throw th;
                }
            }
            xml.attribute("type", type);
            xml.rightAngleBracket();
            xml.escape(valueStr);
            xml.closeElement(str3);
            xml.closeElement(str2);
        }
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public static JivePropertiesExtension from(Message message) {
        return (JivePropertiesExtension) message.getExtension(ELEMENT, NAMESPACE);
    }
}
