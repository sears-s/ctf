package org.jivesoftware.smackx.blocking.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.blocking.element.BlockedErrorExtension;
import org.xmlpull.v1.XmlPullParser;

public class BlockedErrorExtensionProvider extends ExtensionElementProvider<BlockedErrorExtension> {
    public BlockedErrorExtension parse(XmlPullParser parser, int initialDepth) throws Exception {
        return new BlockedErrorExtension();
    }
}
