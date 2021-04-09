package org.jivesoftware.smackx.nick.packet;

import java.io.IOException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class Nick implements ExtensionElement {
    public static final String ELEMENT_NAME = "nick";
    public static final String NAMESPACE = "http://jabber.org/protocol/nick";
    private String name = null;

    public static class Provider extends ExtensionElementProvider<Nick> {
        public Nick parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
            return new Nick(parser.nextText());
        }
    }

    public Nick(String name2) {
        this.name = name2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getElementName() {
        return ELEMENT_NAME;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String toXML(String enclosingNamespace) {
        StringBuilder buf = new StringBuilder();
        buf.append('<');
        String str = ELEMENT_NAME;
        buf.append(str);
        buf.append(" xmlns=\"");
        buf.append(NAMESPACE);
        buf.append("\">");
        buf.append(getName());
        buf.append("</");
        buf.append(str);
        buf.append('>');
        return buf.toString();
    }
}
