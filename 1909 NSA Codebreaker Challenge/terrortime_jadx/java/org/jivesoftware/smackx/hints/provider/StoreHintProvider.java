package org.jivesoftware.smackx.hints.provider;

import org.jivesoftware.smackx.hints.element.StoreHint;

public class StoreHintProvider extends MessageProcessingHintProvider<StoreHint> {
    /* access modifiers changed from: protected */
    public StoreHint getHint() {
        return StoreHint.INSTANCE;
    }
}
