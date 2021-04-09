package org.jivesoftware.smack.sasl.provided;

import com.badguy.terrortime.BuildConfig;
import javax.security.auth.callback.CallbackHandler;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.ByteUtils;

public class SASLPlainMechanism extends SASLMechanism {
    public static final String NAME = "PLAIN";

    /* access modifiers changed from: protected */
    public void authenticateInternal(CallbackHandler cbh) throws SmackException {
        throw new UnsupportedOperationException("CallbackHandler not (yet) supported");
    }

    /* access modifiers changed from: protected */
    public byte[] getAuthenticationText() throws SmackException {
        String authzid;
        if (this.authorizationId == null) {
            authzid = BuildConfig.FLAVOR;
        } else {
            authzid = this.authorizationId.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(authzid);
        sb.append(0);
        sb.append(this.authenticationId);
        byte[] authcid = toBytes(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(0);
        sb2.append(this.password);
        return ByteUtils.concat(authcid, toBytes(sb2.toString()));
    }

    public String getName() {
        return "PLAIN";
    }

    public int getPriority() {
        return 410;
    }

    public SASLPlainMechanism newInstance() {
        return new SASLPlainMechanism();
    }

    public void checkIfSuccessfulOrThrow() throws SmackException {
    }

    public boolean authzidSupported() {
        return true;
    }
}
