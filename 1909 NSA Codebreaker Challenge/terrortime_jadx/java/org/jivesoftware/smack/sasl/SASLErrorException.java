package org.jivesoftware.smack.sasl;

import java.util.HashMap;
import java.util.Map;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.packet.SaslStreamElements.SASLFailure;

public class SASLErrorException extends XMPPException {
    private static final long serialVersionUID = 6247573875760717257L;
    private final String mechanism;
    private final SASLFailure saslFailure;
    private final Map<String, String> texts;

    public SASLErrorException(String mechanism2, SASLFailure saslFailure2) {
        this(mechanism2, saslFailure2, new HashMap());
    }

    public SASLErrorException(String mechanism2, SASLFailure saslFailure2, Map<String, String> texts2) {
        StringBuilder sb = new StringBuilder();
        sb.append("SASLError using ");
        sb.append(mechanism2);
        sb.append(": ");
        sb.append(saslFailure2.getSASLErrorString());
        super(sb.toString());
        this.mechanism = mechanism2;
        this.saslFailure = saslFailure2;
        this.texts = texts2;
    }

    public SASLFailure getSASLFailure() {
        return this.saslFailure;
    }

    public String getMechanism() {
        return this.mechanism;
    }

    public Map<String, String> getTexts() {
        return this.texts;
    }
}
