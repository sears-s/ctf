package org.jivesoftware.smack.sasl;

import com.badguy.terrortime.BuildConfig;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import javax.net.ssl.SSLSession;
import javax.security.auth.callback.CallbackHandler;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.sasl.packet.SaslStreamElements.AuthMechanism;
import org.jivesoftware.smack.sasl.packet.SaslStreamElements.Response;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;

public abstract class SASLMechanism implements Comparable<SASLMechanism> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final String CRAMMD5 = "CRAM-MD5";
    public static final String DIGESTMD5 = "DIGEST-MD5";
    public static final String EXTERNAL = "EXTERNAL";
    public static final String GSSAPI = "GSSAPI";
    public static final String PLAIN = "PLAIN";
    protected String authenticationId;
    protected EntityBareJid authorizationId;
    protected XMPPConnection connection;
    protected ConnectionConfiguration connectionConfiguration;
    protected String host;
    protected String password;
    protected DomainBareJid serviceName;
    protected SSLSession sslSession;

    /* access modifiers changed from: protected */
    public abstract void authenticateInternal(CallbackHandler callbackHandler) throws SmackException;

    public abstract void checkIfSuccessfulOrThrow() throws SmackException;

    /* access modifiers changed from: protected */
    public abstract byte[] getAuthenticationText() throws SmackException;

    public abstract String getName();

    public abstract int getPriority();

    /* access modifiers changed from: protected */
    public abstract SASLMechanism newInstance();

    public final void authenticate(String username, String host2, DomainBareJid serviceName2, String password2, EntityBareJid authzid, SSLSession sslSession2) throws SmackException, NotConnectedException, InterruptedException {
        this.authenticationId = username;
        this.host = host2;
        this.serviceName = serviceName2;
        this.password = password2;
        this.authorizationId = authzid;
        this.sslSession = sslSession2;
        authenticateInternal();
        authenticate();
    }

    /* access modifiers changed from: protected */
    public void authenticateInternal() throws SmackException {
    }

    public void authenticate(String host2, DomainBareJid serviceName2, CallbackHandler cbh, EntityBareJid authzid, SSLSession sslSession2) throws SmackException, NotConnectedException, InterruptedException {
        this.host = host2;
        this.serviceName = serviceName2;
        this.authorizationId = authzid;
        this.sslSession = sslSession2;
        authenticateInternal(cbh);
        authenticate();
    }

    private void authenticate() throws SmackException, NotConnectedException, InterruptedException {
        String authenticationText;
        byte[] authenticationBytes = getAuthenticationText();
        if (authenticationBytes == null || authenticationBytes.length <= 0) {
            authenticationText = "=";
        } else {
            authenticationText = Base64.encodeToString(authenticationBytes);
        }
        this.connection.sendNonza(new AuthMechanism(getName(), authenticationText));
    }

    public final void challengeReceived(String challengeString, boolean finalChallenge) throws SmackException, InterruptedException {
        Response responseStanza;
        byte[] response = evaluateChallenge(Base64.decode((challengeString == null || !challengeString.equals("=")) ? challengeString : BuildConfig.FLAVOR));
        if (!finalChallenge) {
            if (response == null) {
                responseStanza = new Response();
            } else {
                responseStanza = new Response(Base64.encodeToString(response));
            }
            this.connection.sendNonza(responseStanza);
        }
    }

    /* access modifiers changed from: protected */
    public byte[] evaluateChallenge(byte[] challenge) throws SmackException {
        return null;
    }

    public final int compareTo(SASLMechanism other) {
        return Integer.valueOf(getPriority()).compareTo(Integer.valueOf(other.getPriority()));
    }

    public SASLMechanism instanceForAuthentication(XMPPConnection connection2, ConnectionConfiguration connectionConfiguration2) {
        SASLMechanism saslMechansim = newInstance();
        saslMechansim.connection = connection2;
        saslMechansim.connectionConfiguration = connectionConfiguration2;
        return saslMechansim;
    }

    public boolean authzidSupported() {
        return false;
    }

    protected static byte[] toBytes(String string) {
        return StringUtils.toUtf8Bytes(string);
    }

    protected static String saslPrep(String string) {
        return Normalizer.normalize(string, Form.NFKC);
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SASL Mech: ");
        sb.append(getName());
        sb.append(", Prio: ");
        sb.append(getPriority());
        return sb.toString();
    }
}
