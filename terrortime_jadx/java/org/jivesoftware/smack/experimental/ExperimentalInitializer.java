package org.jivesoftware.smack.experimental;

import org.jivesoftware.smack.initializer.UrlInitializer;

public class ExperimentalInitializer extends UrlInitializer {
    /* access modifiers changed from: protected */
    public String getProvidersUri() {
        return "classpath:org.jivesoftware.smack.experimental/experimental.providers";
    }

    /* access modifiers changed from: protected */
    public String getConfigUri() {
        return "classpath:org.jivesoftware.smack.experimental/experimental.xml";
    }
}
