package org.jivesoftware.smackx.hints.provider;

import org.jivesoftware.smackx.hints.element.NoStoreHint;

public class NoStoreHintProvider extends MessageProcessingHintProvider<NoStoreHint> {
    /* access modifiers changed from: protected */
    public NoStoreHint getHint() {
        return NoStoreHint.INSTANCE;
    }
}
