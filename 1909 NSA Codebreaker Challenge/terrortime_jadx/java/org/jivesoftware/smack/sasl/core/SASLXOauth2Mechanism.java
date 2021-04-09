package org.jivesoftware.smack.sasl.core;

import javax.security.auth.callback.CallbackHandler;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.sasl.SASLMechanism;

public class SASLXOauth2Mechanism extends SASLMechanism {
    public static final String NAME = "X-OAUTH2";

    /* access modifiers changed from: protected */
    public void authenticateInternal(CallbackHandler cbh) throws SmackException {
        throw new UnsupportedOperationException("CallbackHandler not (yet) supported");
    }

    /* access modifiers changed from: protected */
    public byte[] getAuthenticationText() throws SmackException {
        StringBuilder sb = new StringBuilder();
        sb.append(0);
        sb.append(this.authenticationId);
        sb.append(0);
        sb.append(this.password);
        return toBytes(sb.toString());
    }

    public String getName() {
        return NAME;
    }

    public int getPriority() {
        return 410;
    }

    public SASLXOauth2Mechanism newInstance() {
        return new SASLXOauth2Mechanism();
    }

    public void checkIfSuccessfulOrThrow() throws SmackException {
    }
}
