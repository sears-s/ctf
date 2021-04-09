package org.jivesoftware.smackx.eme.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.eme.element.ExplicitMessageEncryptionElement;
import org.xmlpull.v1.XmlPullParser;

public class ExplicitMessageEncryptionProvider extends ExtensionElementProvider<ExplicitMessageEncryptionElement> {
    public ExplicitMessageEncryptionElement parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new ExplicitMessageEncryptionElement(parser.getAttributeValue(null, "namespace"), parser.getAttributeValue(null, "name"));
    }
}
