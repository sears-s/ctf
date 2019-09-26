package org.jivesoftware.smack.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.xmlpull.v1.XmlPullParser;

public abstract class EmbeddedExtensionProvider<PE extends ExtensionElement> extends ExtensionElementProvider<PE> {
    /* access modifiers changed from: protected */
    public abstract PE createReturnExtension(String str, String str2, Map<String, String> map, List<? extends ExtensionElement> list);

    public final PE parse(XmlPullParser parser, int initialDepth) throws Exception {
        String namespace = parser.getNamespace();
        String name = parser.getName();
        int attributeCount = parser.getAttributeCount();
        Map<String, String> attMap = new HashMap<>(attributeCount);
        for (int i = 0; i < attributeCount; i++) {
            attMap.put(parser.getAttributeName(i), parser.getAttributeValue(i));
        }
        List<ExtensionElement> extensions = new ArrayList<>();
        while (true) {
            int event = parser.next();
            if (event == 2) {
                PacketParserUtils.addExtensionElement((Collection<ExtensionElement>) extensions, parser);
            }
            if (event == 3 && parser.getDepth() == initialDepth) {
                return createReturnExtension(name, namespace, attMap, extensions);
            }
        }
    }
}
