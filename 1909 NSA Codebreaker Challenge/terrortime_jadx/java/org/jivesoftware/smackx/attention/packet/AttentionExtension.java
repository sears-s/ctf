package org.jivesoftware.smackx.attention.packet;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;

public class AttentionExtension implements ExtensionElement {
    public static final String ELEMENT_NAME = "attention";
    public static final String NAMESPACE = "urn:xmpp:attention:0";

    public static class Provider extends ExtensionElementProvider<AttentionExtension> {
        public AttentionExtension parse(XmlPullParser parser, int initialDepth) {
            return new AttentionExtension();
        }
    }

    public String getElementName() {
        return ELEMENT_NAME;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String toXML(String enclosingNamespace) {
        StringBuilder sb = new StringBuilder();
        sb.append('<');
        sb.append(getElementName());
        sb.append(" xmlns=\"");
        sb.append(getNamespace());
        sb.append("\"/>");
        return sb.toString();
    }
}
