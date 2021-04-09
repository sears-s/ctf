package org.jivesoftware.smack.tcp;

import org.jivesoftware.smack.initializer.UrlInitializer;

public class TCPInitializer extends UrlInitializer {
    /* access modifiers changed from: protected */
    public String getProvidersUri() {
        return "classpath:org.jivesoftware.smack.tcp/smacktcp.providers";
    }
}
