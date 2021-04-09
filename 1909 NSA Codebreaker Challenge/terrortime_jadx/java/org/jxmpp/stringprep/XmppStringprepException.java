package org.jxmpp.stringprep;

import java.io.IOException;

public class XmppStringprepException extends IOException {
    private static final long serialVersionUID = -8491853210107124624L;
    private final String causingString;

    public XmppStringprepException(String causingString2, Exception exception) {
        StringBuilder sb = new StringBuilder();
        sb.append("XmppStringprepException caused by '");
        sb.append(causingString2);
        sb.append("': ");
        sb.append(exception);
        super(sb.toString());
        initCause(exception);
        this.causingString = causingString2;
    }

    public XmppStringprepException(String causingString2, String message) {
        super(message);
        this.causingString = causingString2;
    }

    public String getCausingString() {
        return this.causingString;
    }
}
