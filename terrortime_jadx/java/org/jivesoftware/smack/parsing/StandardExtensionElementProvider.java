package org.jivesoftware.smack.parsing;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.packet.StandardExtensionElement.Builder;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class StandardExtensionElementProvider extends ExtensionElementProvider<StandardExtensionElement> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static StandardExtensionElementProvider INSTANCE = new StandardExtensionElementProvider();

    public StandardExtensionElement parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException {
        String attributeKey;
        Builder builder = StandardExtensionElement.builder(parser.getName(), parser.getNamespace());
        int namespaceCount = parser.getNamespaceCount(initialDepth);
        int attributeCount = parser.getAttributeCount();
        Map<String, String> attributes = new LinkedHashMap<>(namespaceCount + attributeCount);
        for (int i = 0; i < namespaceCount; i++) {
            String nsprefix = parser.getNamespacePrefix(i);
            if (nsprefix != null) {
                String nsuri = parser.getNamespaceUri(i);
                StringBuilder sb = new StringBuilder();
                sb.append("xmlns:");
                sb.append(nsprefix);
                attributes.put(sb.toString(), nsuri);
            }
        }
        for (int i2 = 0; i2 < attributeCount; i2++) {
            String attributePrefix = parser.getAttributePrefix(i2);
            String attributeName = parser.getAttributeName(i2);
            String attributeValue = parser.getAttributeValue(i2);
            if (StringUtils.isNullOrEmpty((CharSequence) attributePrefix)) {
                attributeKey = attributeName;
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(attributePrefix);
                sb2.append(':');
                sb2.append(attributeName);
                attributeKey = sb2.toString();
            }
            attributes.put(attributeKey, attributeValue);
        }
        builder.addAttributes(attributes);
        while (true) {
            int event = parser.next();
            if (event == 2) {
                builder.addElement(parse(parser, parser.getDepth()));
            } else if (event != 3) {
                if (event == 4) {
                    builder.setText(parser.getText());
                }
            } else if (initialDepth == parser.getDepth()) {
                ParserUtils.assertAtEndTag(parser);
                return builder.build();
            }
        }
    }
}
