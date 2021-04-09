package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.StanzaError.Builder;
import org.jivesoftware.smack.util.Objects;

public class ErrorIQ extends SimpleIQ {
    public static final String ELEMENT = "error";

    public ErrorIQ(Builder xmppErrorBuilder) {
        super("error", null);
        Objects.requireNonNull(xmppErrorBuilder, "xmppErrorBuilder must not be null");
        setType(Type.error);
        setError(xmppErrorBuilder);
    }
}
