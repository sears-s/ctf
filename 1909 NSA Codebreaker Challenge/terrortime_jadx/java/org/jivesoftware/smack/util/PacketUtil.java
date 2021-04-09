package org.jivesoftware.smack.util;

import java.util.Collection;
import org.jivesoftware.smack.packet.ExtensionElement;

public class PacketUtil {
    @Deprecated
    public static <PE extends ExtensionElement> PE packetExtensionfromCollection(Collection<ExtensionElement> collection, String element, String namespace) {
        return extensionElementFrom(collection, element, namespace);
    }

    @Deprecated
    public static <PE extends ExtensionElement> PE packetExtensionFromCollection(Collection<ExtensionElement> collection, String element, String namespace) {
        return extensionElementFrom(collection, element, namespace);
    }

    public static <PE extends ExtensionElement> PE extensionElementFrom(Collection<ExtensionElement> collection, String element, String namespace) {
        for (ExtensionElement packetExtension : collection) {
            if ((element == null || packetExtension.getElementName().equals(element)) && packetExtension.getNamespace().equals(namespace)) {
                return packetExtension;
            }
        }
        return null;
    }
}
