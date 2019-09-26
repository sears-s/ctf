package org.jivesoftware.smackx.hints.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.hints.element.MessageProcessingHint;
import org.xmlpull.v1.XmlPullParser;

public abstract class MessageProcessingHintProvider<H extends MessageProcessingHint> extends ExtensionElementProvider<H> {
    /* access modifiers changed from: protected */
    public abstract H getHint();

    public H parse(XmlPullParser parser, int initialDepth) throws Exception {
        return getHint();
    }
}
