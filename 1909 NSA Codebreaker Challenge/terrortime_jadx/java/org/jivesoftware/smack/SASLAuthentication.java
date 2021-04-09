package org.jivesoftware.smack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.net.ssl.SSLSession;
import javax.security.auth.callback.CallbackHandler;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Mechanisms;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.core.ScramSha1PlusMechanism;
import org.jivesoftware.smack.sasl.packet.SaslStreamElements.SASLFailure;
import org.jivesoftware.smack.sasl.packet.SaslStreamElements.Success;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;

public final class SASLAuthentication {
    private static final Set<String> BLACKLISTED_MECHANISMS = new HashSet();
    private static final Logger LOGGER = Logger.getLogger(SASLAuthentication.class.getName());
    private static final List<SASLMechanism> REGISTERED_MECHANISMS = new ArrayList();
    private boolean authenticationSuccessful;
    private final ConnectionConfiguration configuration;
    private final AbstractXMPPConnection connection;
    private SASLMechanism currentMechanism = null;
    private Exception saslException;

    static {
        blacklistSASLMechanism(ScramSha1PlusMechanism.NAME);
    }

    public static void registerSASLMechanism(SASLMechanism mechanism) {
        synchronized (REGISTERED_MECHANISMS) {
            REGISTERED_MECHANISMS.add(mechanism);
            Collections.sort(REGISTERED_MECHANISMS);
        }
    }

    public static Map<String, String> getRegisterdSASLMechanisms() {
        Map<String, String> answer = new LinkedHashMap<>();
        synchronized (REGISTERED_MECHANISMS) {
            for (SASLMechanism mechanism : REGISTERED_MECHANISMS) {
                answer.put(mechanism.getClass().getName(), mechanism.toString());
            }
        }
        return answer;
    }

    public static boolean isSaslMechanismRegistered(String saslMechanism) {
        synchronized (REGISTERED_MECHANISMS) {
            for (SASLMechanism mechanism : REGISTERED_MECHANISMS) {
                if (mechanism.getName().equals(saslMechanism)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean unregisterSASLMechanism(String clazz) {
        synchronized (REGISTERED_MECHANISMS) {
            Iterator<SASLMechanism> it = REGISTERED_MECHANISMS.iterator();
            while (it.hasNext()) {
                if (((SASLMechanism) it.next()).getClass().getName().equals(clazz)) {
                    it.remove();
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean blacklistSASLMechanism(String mechanism) {
        boolean add;
        synchronized (BLACKLISTED_MECHANISMS) {
            add = BLACKLISTED_MECHANISMS.add(mechanism);
        }
        return add;
    }

    public static boolean unBlacklistSASLMechanism(String mechanism) {
        boolean remove;
        synchronized (BLACKLISTED_MECHANISMS) {
            remove = BLACKLISTED_MECHANISMS.remove(mechanism);
        }
        return remove;
    }

    public static Set<String> getBlacklistedSASLMechanisms() {
        return Collections.unmodifiableSet(BLACKLISTED_MECHANISMS);
    }

    SASLAuthentication(AbstractXMPPConnection connection2, ConnectionConfiguration configuration2) {
        this.configuration = configuration2;
        this.connection = connection2;
        init();
    }

    public void authenticate(String username, String password, EntityBareJid authzid, SSLSession sslSession) throws XMPPErrorException, SASLErrorException, IOException, SmackException, InterruptedException {
        this.currentMechanism = selectMechanism(authzid);
        CallbackHandler callbackHandler = this.configuration.getCallbackHandler();
        String host = this.connection.getHost();
        DomainBareJid xmppServiceDomain = this.connection.getXMPPServiceDomain();
        synchronized (this) {
            if (callbackHandler != null) {
                this.currentMechanism.authenticate(host, xmppServiceDomain, callbackHandler, authzid, sslSession);
            } else {
                this.currentMechanism.authenticate(username, host, xmppServiceDomain, password, authzid, sslSession);
            }
            long deadline = System.currentTimeMillis() + this.connection.getReplyTimeout();
            while (true) {
                if (this.authenticationSuccessful || this.saslException != null) {
                    break;
                }
                long now = System.currentTimeMillis();
                if (now >= deadline) {
                    break;
                }
                wait(deadline - now);
            }
        }
        Exception exc = this.saslException;
        if (exc != null) {
            if (exc instanceof SmackException) {
                throw ((SmackException) exc);
            } else if (exc instanceof SASLErrorException) {
                throw ((SASLErrorException) exc);
            } else {
                throw new IllegalStateException("Unexpected exception type", exc);
            }
        } else if (!this.authenticationSuccessful) {
            throw NoResponseException.newWith((XMPPConnection) this.connection, "successful SASL authentication");
        }
    }

    public void challengeReceived(String challenge) throws SmackException, InterruptedException {
        challengeReceived(challenge, false);
    }

    public void challengeReceived(String challenge, boolean finalChallenge) throws SmackException, InterruptedException {
        try {
            this.currentMechanism.challengeReceived(challenge, finalChallenge);
        } catch (InterruptedException | SmackException e) {
            authenticationFailed(e);
            throw e;
        }
    }

    public void authenticated(Success success) throws SmackException, InterruptedException {
        if (success.getData() != null) {
            challengeReceived(success.getData(), true);
        }
        this.currentMechanism.checkIfSuccessfulOrThrow();
        this.authenticationSuccessful = true;
        synchronized (this) {
            notify();
        }
    }

    public void authenticationFailed(SASLFailure saslFailure) {
        authenticationFailed((Exception) new SASLErrorException(this.currentMechanism.getName(), saslFailure));
    }

    public void authenticationFailed(Exception exception) {
        this.saslException = exception;
        synchronized (this) {
            notify();
        }
    }

    public boolean authenticationSuccessful() {
        return this.authenticationSuccessful;
    }

    /* access modifiers changed from: 0000 */
    public void init() {
        this.authenticationSuccessful = false;
        this.saslException = null;
    }

    /* access modifiers changed from: 0000 */
    public String getNameOfLastUsedSaslMechansism() {
        SASLMechanism lastUsedMech = this.currentMechanism;
        if (lastUsedMech == null) {
            return null;
        }
        return lastUsedMech.getName();
    }

    private SASLMechanism selectMechanism(EntityBareJid authzid) throws SmackException {
        List<String> serverMechanisms = getServerMechanisms();
        if (serverMechanisms.isEmpty()) {
            LOGGER.warning("Server did not report any SASL mechanisms");
        }
        for (SASLMechanism mechanism : REGISTERED_MECHANISMS) {
            String mechanismName = mechanism.getName();
            synchronized (BLACKLISTED_MECHANISMS) {
                if (!BLACKLISTED_MECHANISMS.contains(mechanismName)) {
                    if (!this.configuration.isEnabledSaslMechanism(mechanismName)) {
                        continue;
                    } else if (authzid != null && !mechanism.authzidSupported()) {
                        Logger logger = LOGGER;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Skipping ");
                        sb.append(mechanism);
                        sb.append(" because authzid is required by not supported by this SASL mechanism");
                        logger.fine(sb.toString());
                    } else if (serverMechanisms.contains(mechanismName)) {
                        return mechanism.instanceForAuthentication(this.connection, this.configuration);
                    }
                }
            }
        }
        synchronized (BLACKLISTED_MECHANISMS) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("No supported and enabled SASL Mechanism provided by server. Server announced mechanisms: ");
            sb2.append(serverMechanisms);
            sb2.append(". Registered SASL mechanisms with Smack: ");
            sb2.append(REGISTERED_MECHANISMS);
            sb2.append(". Enabled SASL mechanisms for this connection: ");
            sb2.append(this.configuration.getEnabledSaslMechanisms());
            sb2.append(". Blacklisted SASL mechanisms: ");
            sb2.append(BLACKLISTED_MECHANISMS);
            sb2.append('.');
            throw new SmackException(sb2.toString());
        }
    }

    private List<String> getServerMechanisms() {
        Mechanisms mechanisms = (Mechanisms) this.connection.getFeature(Mechanisms.ELEMENT, "urn:ietf:params:xml:ns:xmpp-sasl");
        if (mechanisms == null) {
            return Collections.emptyList();
        }
        return mechanisms.getMechanisms();
    }
}
