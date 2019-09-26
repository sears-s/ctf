package org.jivesoftware.smack.sasl.provided;

import javax.security.auth.callback.CallbackHandler;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.StringUtils;
import org.jxmpp.util.XmppStringUtils;

public class SASLExternalMechanism extends SASLMechanism {
    public static final String NAME = "EXTERNAL";

    /* access modifiers changed from: protected */
    public void authenticateInternal(CallbackHandler cbh) throws SmackException {
    }

    /* access modifiers changed from: protected */
    public byte[] getAuthenticationText() throws SmackException {
        if (this.authorizationId != null) {
            return toBytes(this.authorizationId.toString());
        }
        if (StringUtils.isNullOrEmpty((CharSequence) this.authenticationId)) {
            return null;
        }
        return toBytes(XmppStringUtils.completeJidFrom((CharSequence) this.authenticationId, (CharSequence) this.serviceName));
    }

    public String getName() {
        return "EXTERNAL";
    }

    public int getPriority() {
        return 510;
    }

    /* access modifiers changed from: protected */
    public SASLMechanism newInstance() {
        return new SASLExternalMechanism();
    }

    public void checkIfSuccessfulOrThrow() throws SmackException {
    }

    public boolean authzidSupported() {
        return true;
    }
}
