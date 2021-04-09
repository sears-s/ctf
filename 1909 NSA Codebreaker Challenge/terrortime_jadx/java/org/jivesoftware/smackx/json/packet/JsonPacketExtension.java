package org.jivesoftware.smackx.json.packet;

import org.jivesoftware.smack.packet.Stanza;

public class JsonPacketExtension extends AbstractJsonPacketExtension {
    public static final String ELEMENT = "json";
    public static final String NAMESPACE = "urn:xmpp:json:0";

    public JsonPacketExtension(String json) {
        super(json);
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public static JsonPacketExtension from(Stanza packet) {
        return (JsonPacketExtension) packet.getExtension(ELEMENT, NAMESPACE);
    }
}
