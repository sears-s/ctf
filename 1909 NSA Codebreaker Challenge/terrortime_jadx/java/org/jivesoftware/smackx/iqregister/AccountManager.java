package org.jivesoftware.smackx.iqregister;

import com.badguy.terrortime.BuildConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jivesoftware.smackx.iqregister.packet.Registration.Feature;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Localpart;

public final class AccountManager extends Manager {
    private static final Map<XMPPConnection, AccountManager> INSTANCES = new WeakHashMap();
    private static boolean allowSensitiveOperationOverInsecureConnectionDefault = false;
    private boolean accountCreationSupported = false;
    private boolean allowSensitiveOperationOverInsecureConnection = allowSensitiveOperationOverInsecureConnectionDefault;
    private Registration info = null;

    public static synchronized AccountManager getInstance(XMPPConnection connection) {
        AccountManager accountManager;
        synchronized (AccountManager.class) {
            accountManager = (AccountManager) INSTANCES.get(connection);
            if (accountManager == null) {
                accountManager = new AccountManager(connection);
                INSTANCES.put(connection, accountManager);
            }
        }
        return accountManager;
    }

    public static void sensitiveOperationOverInsecureConnectionDefault(boolean allow) {
        allowSensitiveOperationOverInsecureConnectionDefault = allow;
    }

    public void sensitiveOperationOverInsecureConnection(boolean allow) {
        this.allowSensitiveOperationOverInsecureConnection = allow;
    }

    private AccountManager(XMPPConnection connection) {
        super(connection);
    }

    /* access modifiers changed from: 0000 */
    public void setSupportsAccountCreation(boolean accountCreationSupported2) {
        this.accountCreationSupported = accountCreationSupported2;
    }

    public boolean supportsAccountCreation() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        boolean z = true;
        if (this.accountCreationSupported) {
            return true;
        }
        if (this.info == null) {
            getRegistrationInfo();
            if (this.info.getType() == Type.error) {
                z = false;
            }
            this.accountCreationSupported = z;
        }
        return this.accountCreationSupported;
    }

    public Set<String> getAccountAttributes() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (this.info == null) {
            getRegistrationInfo();
        }
        Map<String, String> attributes = this.info.getAttributes();
        if (attributes != null) {
            return Collections.unmodifiableSet(attributes.keySet());
        }
        return Collections.emptySet();
    }

    public String getAccountAttribute(String name) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (this.info == null) {
            getRegistrationInfo();
        }
        return (String) this.info.getAttributes().get(name);
    }

    public String getAccountInstructions() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (this.info == null) {
            getRegistrationInfo();
        }
        return this.info.getInstructions();
    }

    public void createAccount(Localpart username, String password) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Map<String, String> attributes = new HashMap<>();
        for (String attributeName : getAccountAttributes()) {
            attributes.put(attributeName, BuildConfig.FLAVOR);
        }
        createAccount(username, password, attributes);
    }

    public void createAccount(Localpart username, String password, Map<String, String> attributes) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (!connection().isSecureConnection() && !this.allowSensitiveOperationOverInsecureConnection) {
            throw new IllegalStateException("Creating account over insecure connection");
        } else if (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        } else if (!StringUtils.isNullOrEmpty((CharSequence) password)) {
            attributes.put("username", username.toString());
            attributes.put("password", password);
            Registration reg = new Registration(attributes);
            reg.setType(Type.set);
            reg.setTo((Jid) connection().getXMPPServiceDomain());
            createStanzaCollectorAndSend(reg).nextResultOrThrow();
        } else {
            throw new IllegalArgumentException("Password must not be null");
        }
    }

    public void changePassword(String newPassword) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (connection().isSecureConnection() || this.allowSensitiveOperationOverInsecureConnection) {
            Map<String, String> map = new HashMap<>();
            map.put("username", connection().getUser().getLocalpart().toString());
            map.put("password", newPassword);
            Registration reg = new Registration(map);
            reg.setType(Type.set);
            reg.setTo((Jid) connection().getXMPPServiceDomain());
            createStanzaCollectorAndSend(reg).nextResultOrThrow();
            return;
        }
        throw new IllegalStateException("Changing password over insecure connection.");
    }

    public void deleteAccount() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("remove", BuildConfig.FLAVOR);
        Registration reg = new Registration(attributes);
        reg.setType(Type.set);
        reg.setTo((Jid) connection().getXMPPServiceDomain());
        createStanzaCollectorAndSend(reg).nextResultOrThrow();
    }

    public boolean isSupported() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        XMPPConnection connection = connection();
        if (connection.getFeature("register", Feature.NAMESPACE) != null) {
            return true;
        }
        if (connection.isAuthenticated()) {
            return ServiceDiscoveryManager.getInstanceFor(connection).serverSupportsFeature(Registration.NAMESPACE);
        }
        return false;
    }

    private synchronized void getRegistrationInfo() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Registration reg = new Registration();
        reg.setTo((Jid) connection().getXMPPServiceDomain());
        this.info = (Registration) createStanzaCollectorAndSend(reg).nextResultOrThrow();
    }

    private StanzaCollector createStanzaCollectorAndSend(IQ req) throws NotConnectedException, InterruptedException {
        return connection().createStanzaCollectorAndSend(new StanzaIdFilter(req.getStanzaId()), req);
    }
}
