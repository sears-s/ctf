package org.jivesoftware.smack;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.callback.CallbackHandler;
import org.jivesoftware.smack.debugger.SmackDebuggerFactory;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.sasl.core.SASLAnonymous;
import org.jivesoftware.smack.util.CollectionUtil;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.minidns.dnsname.DnsName;
import org.minidns.dnsname.InvalidDnsNameException;
import org.minidns.util.InetAddressUtil;

public abstract class ConnectionConfiguration {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Logger LOGGER = Logger.getLogger(ConnectionConfiguration.class.getName());
    protected final boolean allowNullOrEmptyUsername;
    private final EntityBareJid authzid;
    private final CallbackHandler callbackHandler;
    private final SSLContext customSSLContext;
    private final X509TrustManager customX509TrustManager;
    private final SmackDebuggerFactory debuggerFactory;
    private final DnssecMode dnssecMode;
    private final String[] enabledSSLCiphers;
    private final String[] enabledSSLProtocols;
    private final Set<String> enabledSaslMechanisms;
    protected final DnsName host;
    protected final InetAddress hostAddress;
    private final HostnameVerifier hostnameVerifier;
    private final String keystorePath;
    private final String keystoreType;
    private final String password;
    private final String pkcs11Library;
    protected final int port;
    protected final ProxyInfo proxy;
    private final Resourcepart resource;
    private final SecurityMode securityMode;
    private final boolean sendPresence;
    private final SocketFactory socketFactory;
    private final CharSequence username;
    protected final DomainBareJid xmppServiceDomain;
    protected final DnsName xmppServiceDomainDnsName;

    public static abstract class Builder<B extends Builder<B, C>, C extends ConnectionConfiguration> {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        /* access modifiers changed from: private */
        public boolean allowEmptyOrNullUsername = false;
        /* access modifiers changed from: private */
        public EntityBareJid authzid;
        /* access modifiers changed from: private */
        public CallbackHandler callbackHandler;
        /* access modifiers changed from: private */
        public SSLContext customSSLContext;
        /* access modifiers changed from: private */
        public X509TrustManager customX509TrustManager;
        /* access modifiers changed from: private */
        public SmackDebuggerFactory debuggerFactory;
        /* access modifiers changed from: private */
        public DnssecMode dnssecMode = DnssecMode.disabled;
        /* access modifiers changed from: private */
        public String[] enabledSSLCiphers;
        /* access modifiers changed from: private */
        public String[] enabledSSLProtocols;
        /* access modifiers changed from: private */
        public Set<String> enabledSaslMechanisms;
        /* access modifiers changed from: private */
        public DnsName host;
        /* access modifiers changed from: private */
        public InetAddress hostAddress;
        /* access modifiers changed from: private */
        public HostnameVerifier hostnameVerifier;
        /* access modifiers changed from: private */
        public String keystorePath = System.getProperty("javax.net.ssl.keyStore");
        /* access modifiers changed from: private */
        public String keystoreType = KeyStore.getDefaultType();
        /* access modifiers changed from: private */
        public String password;
        /* access modifiers changed from: private */
        public String pkcs11Library = "pkcs11.config";
        /* access modifiers changed from: private */
        public int port = 5222;
        /* access modifiers changed from: private */
        public ProxyInfo proxy;
        /* access modifiers changed from: private */
        public Resourcepart resource;
        private boolean saslMechanismsSealed;
        /* access modifiers changed from: private */
        public SecurityMode securityMode = SecurityMode.ifpossible;
        /* access modifiers changed from: private */
        public boolean sendPresence = true;
        /* access modifiers changed from: private */
        public SocketFactory socketFactory;
        /* access modifiers changed from: private */
        public CharSequence username;
        /* access modifiers changed from: private */
        public DomainBareJid xmppServiceDomain;

        public abstract C build();

        /* access modifiers changed from: protected */
        public abstract B getThis();

        static {
            Class<ConnectionConfiguration> cls = ConnectionConfiguration.class;
        }

        protected Builder() {
            if (SmackConfiguration.DEBUG) {
                enableDefaultDebugger();
            }
        }

        public B setUsernameAndPassword(CharSequence username2, String password2) {
            this.username = username2;
            this.password = password2;
            return getThis();
        }

        @Deprecated
        public B setServiceName(DomainBareJid serviceName) {
            return setXmppDomain(serviceName);
        }

        public B setXmppDomain(DomainBareJid xmppDomain) {
            this.xmppServiceDomain = xmppDomain;
            return getThis();
        }

        public B setXmppDomain(String xmppServiceDomain2) throws XmppStringprepException {
            this.xmppServiceDomain = JidCreate.domainBareFrom(xmppServiceDomain2);
            return getThis();
        }

        public B setResource(Resourcepart resource2) {
            this.resource = resource2;
            return getThis();
        }

        public B setResource(CharSequence resource2) throws XmppStringprepException {
            Objects.requireNonNull(resource2, "resource must not be null");
            return setResource(Resourcepart.from(resource2.toString()));
        }

        public B setHostAddress(InetAddress address) {
            this.hostAddress = address;
            return getThis();
        }

        public B setHost(String host2) {
            return setHost(DnsName.from(host2));
        }

        public B setHost(DnsName host2) {
            this.host = host2;
            return getThis();
        }

        public B setHostAddressByNameOrIp(CharSequence fqdnOrIp) {
            String fqdnOrIpString = fqdnOrIp.toString();
            if (InetAddressUtil.isIpAddress(fqdnOrIp)) {
                try {
                    setHostAddress(InetAddress.getByName(fqdnOrIpString));
                } catch (UnknownHostException e) {
                    throw new AssertionError(e);
                }
            } else {
                setHost(fqdnOrIpString);
            }
            return getThis();
        }

        public B setPort(int port2) {
            if (port2 < 0 || port2 > 65535) {
                StringBuilder sb = new StringBuilder();
                sb.append("Port must be a 16-bit unsigned integer (i.e. between 0-65535. Port was: ");
                sb.append(port2);
                throw new IllegalArgumentException(sb.toString());
            }
            this.port = port2;
            return getThis();
        }

        public B setCallbackHandler(CallbackHandler callbackHandler2) {
            this.callbackHandler = callbackHandler2;
            return getThis();
        }

        public B setDnssecMode(DnssecMode dnssecMode2) {
            this.dnssecMode = (DnssecMode) Objects.requireNonNull(dnssecMode2, "DNSSEC mode must not be null");
            return getThis();
        }

        public B setCustomX509TrustManager(X509TrustManager x509TrustManager) {
            this.customX509TrustManager = x509TrustManager;
            return getThis();
        }

        public B setSecurityMode(SecurityMode securityMode2) {
            this.securityMode = securityMode2;
            return getThis();
        }

        public B setKeystorePath(String keystorePath2) {
            this.keystorePath = keystorePath2;
            return getThis();
        }

        public B setKeystoreType(String keystoreType2) {
            this.keystoreType = keystoreType2;
            return getThis();
        }

        public B setPKCS11Library(String pkcs11Library2) {
            this.pkcs11Library = pkcs11Library2;
            return getThis();
        }

        public B setCustomSSLContext(SSLContext context) {
            this.customSSLContext = (SSLContext) Objects.requireNonNull(context, "The SSLContext must not be null");
            return getThis();
        }

        public B setEnabledSSLProtocols(String[] enabledSSLProtocols2) {
            this.enabledSSLProtocols = enabledSSLProtocols2;
            return getThis();
        }

        public B setEnabledSSLCiphers(String[] enabledSSLCiphers2) {
            this.enabledSSLCiphers = enabledSSLCiphers2;
            return getThis();
        }

        public B setHostnameVerifier(HostnameVerifier verifier) {
            this.hostnameVerifier = verifier;
            return getThis();
        }

        public B setSendPresence(boolean sendPresence2) {
            this.sendPresence = sendPresence2;
            return getThis();
        }

        public B enableDefaultDebugger() {
            this.debuggerFactory = SmackConfiguration.getDefaultSmackDebuggerFactory();
            return getThis();
        }

        public B setDebuggerFactory(SmackDebuggerFactory debuggerFactory2) {
            this.debuggerFactory = debuggerFactory2;
            return getThis();
        }

        public B setSocketFactory(SocketFactory socketFactory2) {
            this.socketFactory = socketFactory2;
            return getThis();
        }

        public B setProxyInfo(ProxyInfo proxyInfo) {
            this.proxy = proxyInfo;
            return getThis();
        }

        public B allowEmptyOrNullUsernames() {
            this.allowEmptyOrNullUsername = true;
            return getThis();
        }

        public B performSaslAnonymousAuthentication() {
            String str = SASLAnonymous.NAME;
            if (SASLAuthentication.isSaslMechanismRegistered(str)) {
                throwIfEnabledSaslMechanismsSet();
                allowEmptyOrNullUsernames();
                addEnabledSaslMechanism(str);
                this.saslMechanismsSealed = true;
                return getThis();
            }
            throw new IllegalArgumentException("SASL ANONYMOUS is not registered");
        }

        public B performSaslExternalAuthentication(SSLContext sslContext) {
            String str = "EXTERNAL";
            if (SASLAuthentication.isSaslMechanismRegistered(str)) {
                setCustomSSLContext(sslContext);
                throwIfEnabledSaslMechanismsSet();
                allowEmptyOrNullUsernames();
                setSecurityMode(SecurityMode.required);
                addEnabledSaslMechanism(str);
                this.saslMechanismsSealed = true;
                return getThis();
            }
            throw new IllegalArgumentException("SASL EXTERNAL is not registered");
        }

        private void throwIfEnabledSaslMechanismsSet() {
            if (this.enabledSaslMechanisms != null) {
                throw new IllegalStateException("Enabled SASL mechanisms found");
            }
        }

        public B addEnabledSaslMechanism(String saslMechanism) {
            return addEnabledSaslMechanism((Collection<String>) Arrays.asList(new String[]{(String) StringUtils.requireNotNullOrEmpty(saslMechanism, "saslMechanism must not be null or empty")}));
        }

        public B addEnabledSaslMechanism(Collection<String> saslMechanisms) {
            if (!this.saslMechanismsSealed) {
                CollectionUtil.requireNotEmpty(saslMechanisms, "saslMechanisms");
                Set<String> blacklistedMechanisms = SASLAuthentication.getBlacklistedSASLMechanisms();
                for (String mechanism : saslMechanisms) {
                    if (!SASLAuthentication.isSaslMechanismRegistered(mechanism)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("SASL ");
                        sb.append(mechanism);
                        sb.append(" is not available. Consider registering it with Smack");
                        throw new IllegalArgumentException(sb.toString());
                    } else if (blacklistedMechanisms.contains(mechanism)) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("SALS ");
                        sb2.append(mechanism);
                        sb2.append(" is blacklisted.");
                        throw new IllegalArgumentException(sb2.toString());
                    }
                }
                if (this.enabledSaslMechanisms == null) {
                    this.enabledSaslMechanisms = new HashSet(saslMechanisms.size());
                }
                this.enabledSaslMechanisms.addAll(saslMechanisms);
                return getThis();
            }
            throw new IllegalStateException("The enabled SASL mechanisms are sealed, you can not add new ones");
        }

        public B setAuthzid(EntityBareJid authzid2) {
            this.authzid = authzid2;
            return getThis();
        }
    }

    public enum DnssecMode {
        disabled,
        needsDnssec,
        needsDnssecAndDane
    }

    public enum SecurityMode {
        required,
        ifpossible,
        disabled
    }

    static {
        SmackConfiguration.getVersion();
    }

    protected ConnectionConfiguration(Builder<?, ?> builder) {
        DnsName xmppServiceDomainDnsName2;
        this.authzid = builder.authzid;
        this.username = builder.username;
        this.password = builder.password;
        this.callbackHandler = builder.callbackHandler;
        this.resource = builder.resource;
        this.xmppServiceDomain = builder.xmppServiceDomain;
        DomainBareJid domainBareJid = this.xmppServiceDomain;
        if (domainBareJid != null) {
            try {
                xmppServiceDomainDnsName2 = DnsName.from((CharSequence) domainBareJid);
            } catch (InvalidDnsNameException e) {
                Logger logger = LOGGER;
                Level level = Level.INFO;
                StringBuilder sb = new StringBuilder();
                sb.append("Could not transform XMPP service domain '");
                sb.append(this.xmppServiceDomain);
                sb.append("' to a DNS name. TLS X.509 certificate validiation may not be possible.");
                logger.log(level, sb.toString(), e);
                xmppServiceDomainDnsName2 = null;
            }
            this.xmppServiceDomainDnsName = xmppServiceDomainDnsName2;
            this.hostAddress = builder.hostAddress;
            this.host = builder.host;
            this.port = builder.port;
            this.proxy = builder.proxy;
            this.socketFactory = builder.socketFactory;
            this.dnssecMode = builder.dnssecMode;
            this.customX509TrustManager = builder.customX509TrustManager;
            this.securityMode = builder.securityMode;
            this.keystoreType = builder.keystoreType;
            this.keystorePath = builder.keystorePath;
            this.pkcs11Library = builder.pkcs11Library;
            this.customSSLContext = builder.customSSLContext;
            this.enabledSSLProtocols = builder.enabledSSLProtocols;
            this.enabledSSLCiphers = builder.enabledSSLCiphers;
            this.hostnameVerifier = builder.hostnameVerifier;
            this.sendPresence = builder.sendPresence;
            this.debuggerFactory = builder.debuggerFactory;
            this.allowNullOrEmptyUsername = builder.allowEmptyOrNullUsername;
            this.enabledSaslMechanisms = builder.enabledSaslMechanisms;
            if (this.dnssecMode != DnssecMode.disabled && this.customSSLContext != null) {
                throw new IllegalStateException("You can not use a custom SSL context with DNSSEC enabled");
            }
            return;
        }
        throw new IllegalArgumentException("Must define the XMPP domain");
    }

    /* access modifiers changed from: 0000 */
    public DnsName getHost() {
        return this.host;
    }

    /* access modifiers changed from: 0000 */
    public InetAddress getHostAddress() {
        return this.hostAddress;
    }

    @Deprecated
    public DomainBareJid getServiceName() {
        return this.xmppServiceDomain;
    }

    public DomainBareJid getXMPPServiceDomain() {
        return this.xmppServiceDomain;
    }

    public DnsName getXmppServiceDomainAsDnsNameIfPossible() {
        return this.xmppServiceDomainDnsName;
    }

    public SecurityMode getSecurityMode() {
        return this.securityMode;
    }

    public DnssecMode getDnssecMode() {
        return this.dnssecMode;
    }

    public X509TrustManager getCustomX509TrustManager() {
        return this.customX509TrustManager;
    }

    public String getKeystorePath() {
        return this.keystorePath;
    }

    public String getKeystoreType() {
        return this.keystoreType;
    }

    public String getPKCS11Library() {
        return this.pkcs11Library;
    }

    public SSLContext getCustomSSLContext() {
        return this.customSSLContext;
    }

    public String[] getEnabledSSLProtocols() {
        return this.enabledSSLProtocols;
    }

    public String[] getEnabledSSLCiphers() {
        return this.enabledSSLCiphers;
    }

    public HostnameVerifier getHostnameVerifier() {
        HostnameVerifier hostnameVerifier2 = this.hostnameVerifier;
        if (hostnameVerifier2 != null) {
            return hostnameVerifier2;
        }
        return SmackConfiguration.getDefaultHostnameVerifier();
    }

    public SmackDebuggerFactory getDebuggerFactory() {
        return this.debuggerFactory;
    }

    public CallbackHandler getCallbackHandler() {
        return this.callbackHandler;
    }

    public SocketFactory getSocketFactory() {
        return this.socketFactory;
    }

    public ProxyInfo getProxyInfo() {
        return this.proxy;
    }

    public CharSequence getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public Resourcepart getResource() {
        return this.resource;
    }

    public EntityBareJid getAuthzid() {
        return this.authzid;
    }

    public boolean isSendPresence() {
        return this.sendPresence;
    }

    public boolean isCompressionEnabled() {
        return false;
    }

    public boolean isEnabledSaslMechanism(String saslMechanism) {
        Set<String> set = this.enabledSaslMechanisms;
        if (set == null) {
            return !SASLAuthentication.getBlacklistedSASLMechanisms().contains(saslMechanism);
        }
        return set.contains(saslMechanism);
    }

    public Set<String> getEnabledSaslMechanisms() {
        Set<String> set = this.enabledSaslMechanisms;
        if (set == null) {
            return null;
        }
        return Collections.unmodifiableSet(set);
    }
}
