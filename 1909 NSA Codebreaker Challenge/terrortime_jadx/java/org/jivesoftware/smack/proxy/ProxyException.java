package org.jivesoftware.smack.proxy;

import java.io.IOException;
import org.jivesoftware.smack.proxy.ProxyInfo.ProxyType;

public class ProxyException extends IOException {
    private static final long serialVersionUID = 1;

    public ProxyException(ProxyType type, String ex) {
        StringBuilder sb = new StringBuilder();
        sb.append("Proxy Exception ");
        sb.append(type.toString());
        sb.append(" : ");
        sb.append(ex);
        super(sb.toString());
    }

    public ProxyException(ProxyType type) {
        StringBuilder sb = new StringBuilder();
        sb.append("Proxy Exception ");
        sb.append(type.toString());
        sb.append(" : Unknown Error");
        super(sb.toString());
    }
}
