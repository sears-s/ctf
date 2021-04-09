package org.jivesoftware.smack.extensions;

import org.jivesoftware.smack.initializer.UrlInitializer;

public class ExtensionsInitializer extends UrlInitializer {
    /* access modifiers changed from: protected */
    public String getProvidersUri() {
        return "classpath:org.jivesoftware.smack.extensions/extensions.providers";
    }

    /* access modifiers changed from: protected */
    public String getConfigUri() {
        return "classpath:org.jivesoftware.smack.extensions/extensions.xml";
    }
}
