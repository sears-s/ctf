package org.jivesoftware.smackx.hints.provider;

import org.jivesoftware.smackx.hints.element.NoCopyHint;

public class NoCopyHintProvider extends MessageProcessingHintProvider<NoCopyHint> {
    /* access modifiers changed from: protected */
    public NoCopyHint getHint() {
        return NoCopyHint.INSTANCE;
    }
}
