package org.jivesoftware.smackx.gcm.packet;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.json.packet.AbstractJsonPacketExtension;

public class GcmPacketExtension extends AbstractJsonPacketExtension {
    public static final String ELEMENT = "gcm";
    public static final String NAMESPACE = "google:mobile:data";

    public GcmPacketExtension(String json) {
        super(json);
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public static GcmPacketExtension from(Stanza packet) {
        return (GcmPacketExtension) packet.getExtension(ELEMENT, NAMESPACE);
    }
}
