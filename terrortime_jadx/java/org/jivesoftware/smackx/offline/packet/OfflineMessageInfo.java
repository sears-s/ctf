package org.jivesoftware.smackx.offline.packet;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class OfflineMessageInfo implements ExtensionElement {
    private String node = null;

    public static class Provider extends ExtensionElementProvider<OfflineMessageInfo> {
        public OfflineMessageInfo parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
            OfflineMessageInfo info = new OfflineMessageInfo();
            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == 2) {
                    if (parser.getName().equals("item")) {
                        info.setNode(parser.getAttributeValue(BuildConfig.FLAVOR, NodeElement.ELEMENT));
                    }
                } else if (eventType == 3 && parser.getName().equals(OfflineMessageRequest.ELEMENT)) {
                    done = true;
                }
            }
            return info;
        }
    }

    public String getElementName() {
        return OfflineMessageRequest.ELEMENT;
    }

    public String getNamespace() {
        return OfflineMessageRequest.NAMESPACE;
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(String node2) {
        this.node = node2;
    }

    public String toXML(String enclosingNamespace) {
        StringBuilder buf = new StringBuilder();
        buf.append('<');
        buf.append(getElementName());
        buf.append(" xmlns=\"");
        buf.append(getNamespace());
        buf.append("\">");
        if (getNode() != null) {
            buf.append("<item node=\"");
            buf.append(getNode());
            buf.append("\"/>");
        }
        buf.append("</");
        buf.append(getElementName());
        buf.append('>');
        return buf.toString();
    }
}
