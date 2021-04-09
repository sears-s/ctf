package org.jivesoftware.smackx.jiveproperties;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension;

public class JivePropertiesManager {
    private static boolean javaObjectEnabled = false;

    public static void setJavaObjectEnabled(boolean enabled) {
        javaObjectEnabled = enabled;
    }

    public static boolean isJavaObjectEnabled() {
        return javaObjectEnabled;
    }

    public static void addProperty(Stanza packet, String name, Object value) {
        JivePropertiesExtension jpe = (JivePropertiesExtension) packet.getExtension(JivePropertiesExtension.NAMESPACE);
        if (jpe == null) {
            jpe = new JivePropertiesExtension();
            packet.addExtension(jpe);
        }
        jpe.setProperty(name, value);
    }

    public static Object getProperty(Stanza packet, String name) {
        JivePropertiesExtension jpe = (JivePropertiesExtension) packet.getExtension(JivePropertiesExtension.NAMESPACE);
        if (jpe != null) {
            return jpe.getProperty(name);
        }
        return null;
    }

    public static Collection<String> getPropertiesNames(Stanza packet) {
        JivePropertiesExtension jpe = (JivePropertiesExtension) packet.getExtension(JivePropertiesExtension.NAMESPACE);
        if (jpe == null) {
            return Collections.emptyList();
        }
        return jpe.getPropertyNames();
    }

    public static Map<String, Object> getProperties(Stanza packet) {
        JivePropertiesExtension jpe = (JivePropertiesExtension) packet.getExtension(JivePropertiesExtension.NAMESPACE);
        if (jpe == null) {
            return Collections.emptyMap();
        }
        return jpe.getProperties();
    }
}
