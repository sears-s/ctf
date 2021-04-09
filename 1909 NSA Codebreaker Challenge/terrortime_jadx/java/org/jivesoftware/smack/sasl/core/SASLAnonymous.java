package org.jivesoftware.smack.sasl.core;

import javax.security.auth.callback.CallbackHandler;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.sasl.SASLMechanism;

public class SASLAnonymous extends SASLMechanism {
    public static final String NAME = "ANONYMOUS";

    public String getName() {
        return NAME;
    }

    public int getPriority() {
        return PacketWriter.QUEUE_SIZE;
    }

    /* access modifiers changed from: protected */
    public void authenticateInternal(CallbackHandler cbh) throws SmackException {
    }

    /* access modifiers changed from: protected */
    public byte[] getAuthenticationText() throws SmackException {
        return null;
    }

    public SASLAnonymous newInstance() {
        return new SASLAnonymous();
    }

    public void checkIfSuccessfulOrThrow() throws SmackException {
    }
}
