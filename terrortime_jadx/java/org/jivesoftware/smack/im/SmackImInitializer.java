package org.jivesoftware.smack.im;

import org.jivesoftware.smack.initializer.UrlInitializer;

public class SmackImInitializer extends UrlInitializer {
    /* access modifiers changed from: protected */
    public String getProvidersUri() {
        return "classpath:org.jivesoftware.smack.im/smackim.providers";
    }

    /* access modifiers changed from: protected */
    public String getConfigUri() {
        return "classpath:org.jivesoftware.smack.im/smackim.xml";
    }
}
