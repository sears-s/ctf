package org.jivesoftware.smack.sasl.core;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import javax.net.ssl.SSLPeerUnverifiedException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.util.TLSUtils;

public abstract class ScramPlusMechanism extends ScramMechanism {
    protected ScramPlusMechanism(ScramHmac scramHmac) {
        super(scramHmac);
    }

    public String getName() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getName());
        sb.append("-PLUS");
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public String getChannelBindingName() {
        return "p=tls-server-end-point";
    }

    /* access modifiers changed from: protected */
    public byte[] getChannelBindingData() throws SmackException {
        try {
            return TLSUtils.getChannelBindingTlsServerEndPoint(this.sslSession);
        } catch (NoSuchAlgorithmException | CertificateEncodingException | SSLPeerUnverifiedException e) {
            throw new SmackException((Throwable) e);
        }
    }
}
