package org.jivesoftware.smackx.hints.provider;

import org.jivesoftware.smackx.hints.element.NoPermanentStoreHint;

public class NoPermanentStoreHintProvider extends MessageProcessingHintProvider<NoPermanentStoreHint> {
    /* access modifiers changed from: protected */
    public NoPermanentStoreHint getHint() {
        return NoPermanentStoreHint.INSTANCE;
    }
}
