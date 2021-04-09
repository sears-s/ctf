package org.jivesoftware.smackx.hashes.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.hashes.HashManager.ALGORITHM;
import org.jivesoftware.smackx.hashes.element.HashElement;
import org.xmlpull.v1.XmlPullParser;

public class HashElementProvider extends ExtensionElementProvider<HashElement> {
    public HashElement parse(XmlPullParser parser, int initialDepth) throws Exception {
        String algo = parser.getAttributeValue(null, HashElement.ATTR_ALGO);
        return new HashElement(ALGORITHM.valueOfName(algo), parser.nextText());
    }
}
