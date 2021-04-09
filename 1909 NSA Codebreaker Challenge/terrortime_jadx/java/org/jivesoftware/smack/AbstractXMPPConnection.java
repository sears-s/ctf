package org.jivesoftware.smack;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackConfiguration.UnknownIqRequestReplyMode;
import org.jivesoftware.smack.SmackException.AlreadyConnectedException;
import org.jivesoftware.smack.SmackException.AlreadyLoggedInException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.SmackException.ResourceBindingNotOfferedException;
import org.jivesoftware.smack.SmackException.SecurityRequiredByClientException;
import org.jivesoftware.smack.SmackException.SecurityRequiredException;
import org.jivesoftware.smack.SmackFuture.InternalSmackFuture;
import org.jivesoftware.smack.StanzaCollector.Configuration;
import org.jivesoftware.smack.XMPPConnection.FromMode;
import org.jivesoftware.smack.XMPPException.StreamErrorException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.compression.XMPPInputOutputStream;
import org.jivesoftware.smack.debugger.SmackDebugger;
import org.jivesoftware.smack.debugger.SmackDebuggerFactory;
import org.jivesoftware.smack.filter.IQReplyFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.iqrequest.IQRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.Bind;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Nonza;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Session;
import org.jivesoftware.smack.packet.Session.Feature;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smack.packet.StreamError;
import org.jivesoftware.smack.parsing.ParsingExceptionCallback;
import org.jivesoftware.smack.sasl.core.SASLAnonymous;
import org.jivesoftware.smack.util.Async;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.dns.HostAddress;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.util.XmppStringUtils;
import org.minidns.dnsname.DnsName;
import org.xmlpull.v1.XmlPullParser;

public abstract class AbstractXMPPConnection implements XMPPConnection {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    /* access modifiers changed from: protected */
    public static final AsyncButOrdered<AbstractXMPPConnection> ASYNC_BUT_ORDERED = new AsyncButOrdered<>();
    private static final ExecutorService CACHED_EXECUTOR_SERVICE = Executors.newCachedThreadPool(new ThreadFactory() {
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("Smack Cached Executor");
            thread.setDaemon(true);
            thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                public void uncaughtException(Thread t, Throwable e) {
                    Logger access$000 = AbstractXMPPConnection.LOGGER;
                    Level level = Level.WARNING;
                    StringBuilder sb = new StringBuilder();
                    sb.append(t);
                    sb.append(" encountered uncaught exception");
                    access$000.log(level, sb.toString(), e);
                }
            });
            return thread;
        }
    });
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(AbstractXMPPConnection.class.getName());
    protected static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("Smack Scheduled Executor Service");
            thread.setDaemon(true);
            return thread;
        }
    });
    private static final AtomicInteger connectionCounter = new AtomicInteger(0);
    private final Map<StanzaListener, ListenerWrapper> asyncRecvListeners = new LinkedHashMap();
    protected boolean authenticated = false;
    protected long authenticatedConnectionInitiallyEstablishedTimestamp;
    private final Collection<StanzaCollector> collectors = new ConcurrentLinkedQueue();
    protected XMPPInputOutputStream compressionHandler;
    protected final ConnectionConfiguration config;
    protected boolean connected = false;
    protected final int connectionCounterValue = connectionCounter.getAndIncrement();
    protected final Set<ConnectionListener> connectionListeners = new CopyOnWriteArraySet();
    protected final Lock connectionLock = new ReentrantLock();
    /* access modifiers changed from: private */
    public int currentAsyncRunnables;
    protected final SmackDebugger debugger;
    /* access modifiers changed from: private */
    public final Queue<Runnable> deferredAsyncRunnables = new LinkedList();
    /* access modifiers changed from: private */
    public int deferredAsyncRunnablesCount;
    private int deferredAsyncRunnablesCountPrevious;
    private FromMode fromMode = FromMode.OMITTED;
    private final Map<String, IQRequestHandler> getIqRequestHandler = new HashMap();
    protected String host;
    protected List<HostAddress> hostAddresses;
    private final Map<StanzaListener, InterceptorWrapper> interceptors = new HashMap();
    /* access modifiers changed from: protected */
    public final SynchronizationPoint<SmackException> lastFeaturesReceived = new SynchronizationPoint<>(this, "last stream features received from server");
    private long lastStanzaReceived;
    private final Executor limitedExcutor = new Executor() {
        public void execute(Runnable runnable) {
            AbstractXMPPConnection.this.asyncGoLimited(runnable);
        }
    };
    private int maxAsyncRunnables = SmackConfiguration.getDefaultConcurrencyLevelLimit();
    private ParsingExceptionCallback parsingExceptionCallback = SmackConfiguration.getDefaultParsingExceptionCallback();
    protected int port;
    protected Reader reader;
    private long replyTimeout = ((long) SmackConfiguration.getDefaultReplyTimeout());
    protected final SASLAuthentication saslAuthentication;
    /* access modifiers changed from: protected */
    public final SynchronizationPoint<XMPPException> saslFeatureReceived = new SynchronizationPoint<>(this, "SASL mechanisms stream feature from server");
    private final Map<StanzaListener, ListenerWrapper> sendListeners = new HashMap();
    private final Map<String, IQRequestHandler> setIqRequestHandler = new HashMap();
    protected final Map<String, ExtensionElement> streamFeatures = new HashMap();
    /* access modifiers changed from: protected */
    public String streamId;
    private final Map<StanzaListener, ListenerWrapper> syncRecvListeners = new LinkedHashMap();
    /* access modifiers changed from: protected */
    public final SynchronizationPoint<SmackException> tlsHandled = new SynchronizationPoint<>(this, "establishing TLS");
    private UnknownIqRequestReplyMode unknownIqRequestReplyMode = SmackConfiguration.getUnknownIqRequestReplyMode();
    private String usedPassword;
    private Resourcepart usedResource;
    private String usedUsername;
    protected EntityFullJid user;
    protected boolean wasAuthenticated = false;
    /* access modifiers changed from: protected */
    public Writer writer;
    private DomainBareJid xmppServiceDomain;

    /* renamed from: org.jivesoftware.smack.AbstractXMPPConnection$15 reason: invalid class name */
    static /* synthetic */ class AnonymousClass15 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$SmackConfiguration$UnknownIqRequestReplyMode = new int[UnknownIqRequestReplyMode.values().length];
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$XMPPConnection$FromMode = new int[FromMode.values().length];
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$iqrequest$IQRequestHandler$Mode = new int[Mode.values().length];
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$IQ$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$iqrequest$IQRequestHandler$Mode[Mode.sync.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$iqrequest$IQRequestHandler$Mode[Mode.async.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$SmackConfiguration$UnknownIqRequestReplyMode[UnknownIqRequestReplyMode.doNotReply.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$SmackConfiguration$UnknownIqRequestReplyMode[UnknownIqRequestReplyMode.replyFeatureNotImplemented.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$SmackConfiguration$UnknownIqRequestReplyMode[UnknownIqRequestReplyMode.replyServiceUnavailable.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$IQ$Type[Type.set.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$IQ$Type[Type.get.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$XMPPConnection$FromMode[FromMode.OMITTED.ordinal()] = 1;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$XMPPConnection$FromMode[FromMode.USER.ordinal()] = 2;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$XMPPConnection$FromMode[FromMode.UNCHANGED.ordinal()] = 3;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    protected static class InterceptorWrapper {
        private final StanzaFilter packetFilter;
        private final StanzaListener packetInterceptor;

        public InterceptorWrapper(StanzaListener packetInterceptor2, StanzaFilter packetFilter2) {
            this.packetInterceptor = packetInterceptor2;
            this.packetFilter = packetFilter2;
        }

        public boolean filterMatches(Stanza packet) {
            StanzaFilter stanzaFilter = this.packetFilter;
            return stanzaFilter == null || stanzaFilter.accept(packet);
        }

        public StanzaListener getInterceptor() {
            return this.packetInterceptor;
        }
    }

    protected static class ListenerWrapper {
        private final StanzaFilter packetFilter;
        private final StanzaListener packetListener;

        public ListenerWrapper(StanzaListener packetListener2, StanzaFilter packetFilter2) {
            this.packetListener = packetListener2;
            this.packetFilter = packetFilter2;
        }

        public boolean filterMatches(Stanza packet) {
            StanzaFilter stanzaFilter = this.packetFilter;
            return stanzaFilter == null || stanzaFilter.accept(packet);
        }

        public StanzaListener getListener() {
            return this.packetListener;
        }
    }

    /* access modifiers changed from: protected */
    public abstract void connectInternal() throws SmackException, IOException, XMPPException, InterruptedException;

    public abstract void instantShutdown();

    public abstract boolean isSecureConnection();

    public abstract boolean isUsingCompression();

    /* access modifiers changed from: protected */
    public abstract void loginInternal(String str, String str2, Resourcepart resourcepart) throws XMPPException, SmackException, IOException, InterruptedException;

    public abstract void sendNonza(Nonza nonza) throws NotConnectedException, InterruptedException;

    /* access modifiers changed from: protected */
    public abstract void sendStanzaInternal(Stanza stanza) throws NotConnectedException, InterruptedException;

    /* access modifiers changed from: protected */
    public abstract void shutdown();

    static {
        SmackConfiguration.getVersion();
    }

    protected AbstractXMPPConnection(ConnectionConfiguration configuration) {
        this.saslAuthentication = new SASLAuthentication(this, configuration);
        this.config = configuration;
        SmackDebuggerFactory debuggerFactory = configuration.getDebuggerFactory();
        if (debuggerFactory != null) {
            this.debugger = debuggerFactory.create(this);
        } else {
            this.debugger = null;
        }
        for (ConnectionCreationListener listener : XMPPConnectionRegistry.getConnectionCreationListeners()) {
            listener.connectionCreated(this);
        }
    }

    public ConnectionConfiguration getConfiguration() {
        return this.config;
    }

    public DomainBareJid getXMPPServiceDomain() {
        DomainBareJid domainBareJid = this.xmppServiceDomain;
        if (domainBareJid != null) {
            return domainBareJid;
        }
        return this.config.getXMPPServiceDomain();
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    /* access modifiers changed from: protected */
    public void initState() {
        this.saslFeatureReceived.init();
        this.lastFeaturesReceived.init();
        this.tlsHandled.init();
    }

    public synchronized AbstractXMPPConnection connect() throws SmackException, IOException, XMPPException, InterruptedException {
        throwAlreadyConnectedExceptionIfAppropriate();
        initState();
        this.saslAuthentication.init();
        this.streamId = null;
        try {
            connectInternal();
            if (!isSecureConnection()) {
                if (getConfiguration().getSecurityMode() == SecurityMode.required) {
                    throw new SecurityRequiredByClientException();
                }
            }
            this.connected = true;
            callConnectionConnectedListener();
        } catch (IOException | InterruptedException | SmackException | XMPPException e) {
            instantShutdown();
            throw e;
        }
        return this;
    }

    public synchronized void login() throws XMPPException, SmackException, IOException, InterruptedException {
        login(this.usedUsername != null ? this.usedUsername : this.config.getUsername(), this.usedPassword != null ? this.usedPassword : this.config.getPassword(), this.usedResource != null ? this.usedResource : this.config.getResource());
    }

    public synchronized void login(CharSequence username, String password) throws XMPPException, SmackException, IOException, InterruptedException {
        login(username, password, this.config.getResource());
    }

    public synchronized void login(CharSequence username, String password, Resourcepart resource) throws XMPPException, SmackException, IOException, InterruptedException {
        if (!this.config.allowNullOrEmptyUsername) {
            StringUtils.requireNotNullOrEmpty(username, "Username must not be null or empty");
        }
        throwNotConnectedExceptionIfAppropriate("Did you call connect() before login()?");
        throwAlreadyLoggedInExceptionIfAppropriate();
        this.usedUsername = username != null ? username.toString() : null;
        this.usedPassword = password;
        this.usedResource = resource;
        loginInternal(this.usedUsername, this.usedPassword, this.usedResource);
    }

    public final boolean isConnected() {
        return this.connected;
    }

    public final boolean isAuthenticated() {
        return this.authenticated;
    }

    public final EntityFullJid getUser() {
        return this.user;
    }

    public String getStreamId() {
        if (!isConnected()) {
            return null;
        }
        return this.streamId;
    }

    /* access modifiers changed from: protected */
    public void bindResourceAndEstablishSession(Resourcepart resource) throws XMPPErrorException, SmackException, InterruptedException {
        LOGGER.finer("Waiting for last features to be received before continuing with resource binding");
        this.lastFeaturesReceived.checkIfSuccessOrWaitOrThrow();
        if (hasFeature(Bind.ELEMENT, Bind.NAMESPACE)) {
            Bind bindResource = Bind.newSet(resource);
            this.user = ((Bind) createStanzaCollectorAndSend(new StanzaIdFilter((Stanza) bindResource), bindResource).nextResultOrThrow()).getJid();
            this.xmppServiceDomain = this.user.asDomainBareJid();
            Feature sessionFeature = (Feature) getFeature(Session.ELEMENT, Session.NAMESPACE);
            if (sessionFeature != null && !sessionFeature.isOptional()) {
                Session session = new Session();
                createStanzaCollectorAndSend(new StanzaIdFilter((Stanza) session), session).nextResultOrThrow();
                return;
            }
            return;
        }
        throw new ResourceBindingNotOfferedException();
    }

    /* access modifiers changed from: protected */
    public void afterSuccessfulLogin(boolean resumed) throws NotConnectedException, InterruptedException {
        if (!resumed) {
            this.authenticatedConnectionInitiallyEstablishedTimestamp = System.currentTimeMillis();
        }
        this.authenticated = true;
        SmackDebugger smackDebugger = this.debugger;
        if (smackDebugger != null) {
            smackDebugger.userHasLogged(this.user);
        }
        callConnectionAuthenticatedListener(resumed);
        if (this.config.isSendPresence() && !resumed) {
            sendStanza(new Presence(Presence.Type.available));
        }
    }

    public final boolean isAnonymous() {
        if (isAuthenticated()) {
            if (SASLAnonymous.NAME.equals(getUsedSaslMechansism())) {
                return true;
            }
        }
        return false;
    }

    public final String getUsedSaslMechansism() {
        return this.saslAuthentication.getNameOfLastUsedSaslMechansism();
    }

    /* access modifiers changed from: protected */
    public List<HostAddress> populateHostAddresses() {
        List<HostAddress> failedAddresses = new LinkedList<>();
        if (this.config.hostAddress != null) {
            this.hostAddresses = new ArrayList(1);
            this.hostAddresses.add(new HostAddress(this.config.port, this.config.hostAddress));
        } else if (this.config.host != null) {
            this.hostAddresses = new ArrayList(1);
            HostAddress hostAddress = DNSUtil.getDNSResolver().lookupHostAddress(this.config.host, this.config.port, failedAddresses, this.config.getDnssecMode());
            if (hostAddress != null) {
                this.hostAddresses.add(hostAddress);
            }
        } else {
            this.hostAddresses = DNSUtil.resolveXMPPServiceDomain(DnsName.from((CharSequence) this.config.getXMPPServiceDomain()), failedAddresses, this.config.getDnssecMode());
        }
        return failedAddresses;
    }

    /* access modifiers changed from: protected */
    public Lock getConnectionLock() {
        return this.connectionLock;
    }

    /* access modifiers changed from: protected */
    public void throwNotConnectedExceptionIfAppropriate() throws NotConnectedException {
        throwNotConnectedExceptionIfAppropriate(null);
    }

    /* access modifiers changed from: protected */
    public void throwNotConnectedExceptionIfAppropriate(String optionalHint) throws NotConnectedException {
        if (!isConnected()) {
            throw new NotConnectedException(optionalHint);
        }
    }

    /* access modifiers changed from: protected */
    public void throwAlreadyConnectedExceptionIfAppropriate() throws AlreadyConnectedException {
        if (isConnected()) {
            throw new AlreadyConnectedException();
        }
    }

    /* access modifiers changed from: protected */
    public void throwAlreadyLoggedInExceptionIfAppropriate() throws AlreadyLoggedInException {
        if (isAuthenticated()) {
            throw new AlreadyLoggedInException();
        }
    }

    public void sendStanza(Stanza stanza) throws NotConnectedException, InterruptedException {
        Objects.requireNonNull(stanza, "Stanza must not be null");
        throwNotConnectedExceptionIfAppropriate();
        int i = AnonymousClass15.$SwitchMap$org$jivesoftware$smack$XMPPConnection$FromMode[this.fromMode.ordinal()];
        if (i == 1) {
            stanza.setFrom((Jid) null);
        } else if (i == 2) {
            stanza.setFrom((Jid) getUser());
        }
        firePacketInterceptors(stanza);
        sendStanzaInternal(stanza);
    }

    /* access modifiers changed from: protected */
    public SASLAuthentication getSASLAuthentication() {
        return this.saslAuthentication;
    }

    public void disconnect() {
        Presence unavailablePresence = null;
        if (isAuthenticated()) {
            unavailablePresence = new Presence(Presence.Type.unavailable);
        }
        try {
            disconnect(unavailablePresence);
        } catch (NotConnectedException e) {
            LOGGER.log(Level.FINEST, "Connection is already disconnected", e);
        }
    }

    public synchronized void disconnect(Presence unavailablePresence) throws NotConnectedException {
        if (unavailablePresence != null) {
            try {
                sendStanza(unavailablePresence);
            } catch (InterruptedException e) {
                LOGGER.log(Level.FINE, "Was interrupted while sending unavailable presence. Continuing to disconnect the connection", e);
            }
        }
        shutdown();
        callConnectionClosedListener();
    }

    public void addConnectionListener(ConnectionListener connectionListener) {
        if (connectionListener != null) {
            this.connectionListeners.add(connectionListener);
        }
    }

    public void removeConnectionListener(ConnectionListener connectionListener) {
        this.connectionListeners.remove(connectionListener);
    }

    public <I extends IQ> I sendIqRequestAndWaitForResponse(IQ request) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return (IQ) createStanzaCollectorAndSend(request).nextResultOrThrow();
    }

    public StanzaCollector createStanzaCollectorAndSend(IQ packet) throws NotConnectedException, InterruptedException {
        return createStanzaCollectorAndSend(new IQReplyFilter(packet, this), packet);
    }

    public StanzaCollector createStanzaCollectorAndSend(StanzaFilter packetFilter, Stanza packet) throws NotConnectedException, InterruptedException {
        StanzaCollector packetCollector = createStanzaCollector(StanzaCollector.newConfiguration().setStanzaFilter(packetFilter).setRequest(packet));
        try {
            sendStanza(packet);
            return packetCollector;
        } catch (InterruptedException | RuntimeException | NotConnectedException e) {
            packetCollector.cancel();
            throw e;
        }
    }

    public StanzaCollector createStanzaCollector(StanzaFilter packetFilter) {
        return createStanzaCollector(StanzaCollector.newConfiguration().setStanzaFilter(packetFilter));
    }

    public StanzaCollector createStanzaCollector(Configuration configuration) {
        StanzaCollector collector = new StanzaCollector(this, configuration);
        this.collectors.add(collector);
        return collector;
    }

    public void removeStanzaCollector(StanzaCollector collector) {
        this.collectors.remove(collector);
    }

    public void addSyncStanzaListener(StanzaListener packetListener, StanzaFilter packetFilter) {
        if (packetListener != null) {
            ListenerWrapper wrapper = new ListenerWrapper(packetListener, packetFilter);
            synchronized (this.syncRecvListeners) {
                this.syncRecvListeners.put(packetListener, wrapper);
            }
            return;
        }
        throw new NullPointerException("Packet listener is null.");
    }

    public boolean removeSyncStanzaListener(StanzaListener packetListener) {
        boolean z;
        synchronized (this.syncRecvListeners) {
            z = this.syncRecvListeners.remove(packetListener) != null;
        }
        return z;
    }

    public void addAsyncStanzaListener(StanzaListener packetListener, StanzaFilter packetFilter) {
        if (packetListener != null) {
            ListenerWrapper wrapper = new ListenerWrapper(packetListener, packetFilter);
            synchronized (this.asyncRecvListeners) {
                this.asyncRecvListeners.put(packetListener, wrapper);
            }
            return;
        }
        throw new NullPointerException("Packet listener is null.");
    }

    public boolean removeAsyncStanzaListener(StanzaListener packetListener) {
        boolean z;
        synchronized (this.asyncRecvListeners) {
            z = this.asyncRecvListeners.remove(packetListener) != null;
        }
        return z;
    }

    @Deprecated
    public void addPacketSendingListener(StanzaListener packetListener, StanzaFilter packetFilter) {
        addStanzaSendingListener(packetListener, packetFilter);
    }

    public void addStanzaSendingListener(StanzaListener packetListener, StanzaFilter packetFilter) {
        if (packetListener != null) {
            ListenerWrapper wrapper = new ListenerWrapper(packetListener, packetFilter);
            synchronized (this.sendListeners) {
                this.sendListeners.put(packetListener, wrapper);
            }
            return;
        }
        throw new NullPointerException("Packet listener is null.");
    }

    @Deprecated
    public void removePacketSendingListener(StanzaListener packetListener) {
        removeStanzaSendingListener(packetListener);
    }

    public void removeStanzaSendingListener(StanzaListener packetListener) {
        synchronized (this.sendListeners) {
            this.sendListeners.remove(packetListener);
        }
    }

    /* access modifiers changed from: protected */
    public void firePacketSendingListeners(final Stanza packet) {
        SmackDebugger debugger2 = this.debugger;
        if (debugger2 != null) {
            debugger2.onOutgoingStreamElement(packet);
        }
        final List<StanzaListener> listenersToNotify = new LinkedList<>();
        synchronized (this.sendListeners) {
            for (ListenerWrapper listenerWrapper : this.sendListeners.values()) {
                if (listenerWrapper.filterMatches(packet)) {
                    listenersToNotify.add(listenerWrapper.getListener());
                }
            }
        }
        if (!listenersToNotify.isEmpty()) {
            asyncGo(new Runnable() {
                public void run() {
                    for (StanzaListener listener : listenersToNotify) {
                        try {
                            listener.processStanza(packet);
                        } catch (Exception e) {
                            AbstractXMPPConnection.LOGGER.log(Level.WARNING, "Sending listener threw exception", e);
                        }
                    }
                }
            });
        }
    }

    @Deprecated
    public void addPacketInterceptor(StanzaListener packetInterceptor, StanzaFilter packetFilter) {
        addStanzaInterceptor(packetInterceptor, packetFilter);
    }

    public void addStanzaInterceptor(StanzaListener packetInterceptor, StanzaFilter packetFilter) {
        if (packetInterceptor != null) {
            InterceptorWrapper interceptorWrapper = new InterceptorWrapper(packetInterceptor, packetFilter);
            synchronized (this.interceptors) {
                this.interceptors.put(packetInterceptor, interceptorWrapper);
            }
            return;
        }
        throw new NullPointerException("Packet interceptor is null.");
    }

    @Deprecated
    public void removePacketInterceptor(StanzaListener packetInterceptor) {
        removeStanzaInterceptor(packetInterceptor);
    }

    public void removeStanzaInterceptor(StanzaListener packetInterceptor) {
        synchronized (this.interceptors) {
            this.interceptors.remove(packetInterceptor);
        }
    }

    private void firePacketInterceptors(Stanza packet) {
        List<StanzaListener> interceptorsToInvoke = new LinkedList<>();
        synchronized (this.interceptors) {
            for (InterceptorWrapper interceptorWrapper : this.interceptors.values()) {
                if (interceptorWrapper.filterMatches(packet)) {
                    interceptorsToInvoke.add(interceptorWrapper.getInterceptor());
                }
            }
        }
        for (StanzaListener interceptor : interceptorsToInvoke) {
            try {
                interceptor.processStanza(packet);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Packet interceptor threw exception", e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void initDebugger() {
        Reader reader2 = this.reader;
        if (reader2 == null || this.writer == null) {
            throw new NullPointerException("Reader or writer isn't initialized.");
        }
        SmackDebugger smackDebugger = this.debugger;
        if (smackDebugger != null) {
            this.reader = smackDebugger.newConnectionReader(reader2);
            this.writer = this.debugger.newConnectionWriter(this.writer);
        }
    }

    public long getReplyTimeout() {
        return this.replyTimeout;
    }

    public void setReplyTimeout(long timeout) {
        this.replyTimeout = timeout;
    }

    public void setUnknownIqRequestReplyMode(UnknownIqRequestReplyMode unknownIqRequestReplyMode2) {
        this.unknownIqRequestReplyMode = (UnknownIqRequestReplyMode) Objects.requireNonNull(unknownIqRequestReplyMode2, "Mode must not be null");
    }

    /* access modifiers changed from: protected */
    public void parseAndProcessStanza(XmlPullParser parser) throws Exception {
        ParserUtils.assertAtStartTag(parser);
        int parserDepth = parser.getDepth();
        Stanza stanza = null;
        try {
            stanza = PacketParserUtils.parseStanza(parser);
        } catch (Exception e) {
            UnparseableStanza message = new UnparseableStanza(PacketParserUtils.parseContentDepth(parser, parserDepth), e);
            ParsingExceptionCallback callback = getParsingExceptionCallback();
            if (callback != null) {
                callback.handleUnparsableStanza(message);
            }
        }
        ParserUtils.assertAtEndTag(parser);
        if (stanza != null) {
            processStanza(stanza);
        }
    }

    /* access modifiers changed from: protected */
    public void processStanza(Stanza stanza) throws InterruptedException {
        SmackDebugger debugger2 = this.debugger;
        if (debugger2 != null) {
            debugger2.onIncomingStreamElement(stanza);
        }
        this.lastStanzaReceived = System.currentTimeMillis();
        invokeStanzaCollectorsAndNotifyRecvListeners(stanza);
    }

    /* access modifiers changed from: protected */
    public void invokeStanzaCollectorsAndNotifyRecvListeners(final Stanza packet) {
        IQRequestHandler iqRequestHandler;
        Condition replyCondition;
        if (packet instanceof IQ) {
            final IQ iq = (IQ) packet;
            if (iq.isRequestIQ()) {
                final IQ iqRequest = iq;
                String key = XmppStringUtils.generateKey(iq.getChildElementName(), iq.getChildElementNamespace());
                int i = AnonymousClass15.$SwitchMap$org$jivesoftware$smack$packet$IQ$Type[iq.getType().ordinal()];
                if (i == 1) {
                    synchronized (this.setIqRequestHandler) {
                        iqRequestHandler = (IQRequestHandler) this.setIqRequestHandler.get(key);
                    }
                } else if (i == 2) {
                    synchronized (this.getIqRequestHandler) {
                        iqRequestHandler = (IQRequestHandler) this.getIqRequestHandler.get(key);
                    }
                } else {
                    throw new IllegalStateException("Should only encounter IQ type 'get' or 'set'");
                }
                if (iqRequestHandler == null) {
                    int i2 = AnonymousClass15.$SwitchMap$org$jivesoftware$smack$SmackConfiguration$UnknownIqRequestReplyMode[this.unknownIqRequestReplyMode.ordinal()];
                    if (i2 != 1) {
                        if (i2 == 2) {
                            replyCondition = Condition.feature_not_implemented;
                        } else if (i2 == 3) {
                            replyCondition = Condition.service_unavailable;
                        } else {
                            throw new AssertionError();
                        }
                        try {
                            sendStanza(IQ.createErrorResponse(iq, StanzaError.getBuilder(replyCondition)));
                        } catch (InterruptedException | NotConnectedException e) {
                            LOGGER.log(Level.WARNING, "Exception while sending error IQ to unkown IQ request", e);
                        }
                    } else {
                        return;
                    }
                } else {
                    Executor executorService = null;
                    int i3 = AnonymousClass15.$SwitchMap$org$jivesoftware$smack$iqrequest$IQRequestHandler$Mode[iqRequestHandler.getMode().ordinal()];
                    if (i3 == 1) {
                        executorService = ASYNC_BUT_ORDERED.asExecutorFor(this);
                    } else if (i3 == 2) {
                        executorService = this.limitedExcutor;
                    }
                    final IQRequestHandler finalIqRequestHandler = iqRequestHandler;
                    executorService.execute(new Runnable() {
                        static final /* synthetic */ boolean $assertionsDisabled = false;

                        static {
                            Class<AbstractXMPPConnection> cls = AbstractXMPPConnection.class;
                        }

                        public void run() {
                            IQ response = finalIqRequestHandler.handleIQRequest(iq);
                            if (response != null) {
                                response.setTo(iqRequest.getFrom());
                                response.setStanzaId(iqRequest.getStanzaId());
                                try {
                                    AbstractXMPPConnection.this.sendStanza(response);
                                } catch (InterruptedException | NotConnectedException e) {
                                    AbstractXMPPConnection.LOGGER.log(Level.WARNING, "Exception while sending response to IQ request", e);
                                }
                            }
                        }
                    });
                }
                return;
            }
        }
        final Collection<StanzaListener> listenersToNotify = new LinkedList<>();
        synchronized (this.asyncRecvListeners) {
            for (ListenerWrapper listenerWrapper : this.asyncRecvListeners.values()) {
                if (listenerWrapper.filterMatches(packet)) {
                    listenersToNotify.add(listenerWrapper.getListener());
                }
            }
        }
        for (final StanzaListener listener : listenersToNotify) {
            asyncGoLimited(new Runnable() {
                public void run() {
                    try {
                        listener.processStanza(packet);
                    } catch (Exception e) {
                        AbstractXMPPConnection.LOGGER.log(Level.SEVERE, "Exception in async packet listener", e);
                    }
                }
            });
        }
        for (StanzaCollector collector : this.collectors) {
            collector.processStanza(packet);
        }
        listenersToNotify.clear();
        synchronized (this.syncRecvListeners) {
            for (ListenerWrapper listenerWrapper2 : this.syncRecvListeners.values()) {
                if (listenerWrapper2.filterMatches(packet)) {
                    listenersToNotify.add(listenerWrapper2.getListener());
                }
            }
        }
        ASYNC_BUT_ORDERED.performAsyncButOrdered(this, new Runnable() {
            public void run() {
                for (StanzaListener listener : listenersToNotify) {
                    try {
                        listener.processStanza(packet);
                    } catch (NotConnectedException e) {
                        AbstractXMPPConnection.LOGGER.log(Level.WARNING, "Got not connected exception, aborting", e);
                        return;
                    } catch (Exception e2) {
                        AbstractXMPPConnection.LOGGER.log(Level.SEVERE, "Exception in packet listener", e2);
                    }
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void setWasAuthenticated() {
        if (!this.wasAuthenticated) {
            this.wasAuthenticated = this.authenticated;
        }
    }

    /* access modifiers changed from: protected */
    public void callConnectionConnectedListener() {
        for (ConnectionListener listener : this.connectionListeners) {
            listener.connected(this);
        }
    }

    /* access modifiers changed from: protected */
    public void callConnectionAuthenticatedListener(boolean resumed) {
        for (ConnectionListener listener : this.connectionListeners) {
            try {
                listener.authenticated(this, resumed);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Exception in authenticated listener", e);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void callConnectionClosedListener() {
        for (ConnectionListener listener : this.connectionListeners) {
            try {
                listener.connectionClosed();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error in listener while closing connection", e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void callConnectionClosedOnErrorListener(Exception e) {
        boolean logWarning = true;
        if ((e instanceof StreamErrorException) && ((StreamErrorException) e).getStreamError().getCondition() == StreamError.Condition.not_authorized && this.wasAuthenticated) {
            logWarning = false;
            LOGGER.log(Level.FINE, "Connection closed with not-authorized stream error after it was already authenticated. The account was likely deleted/unregistered on the server");
        }
        if (logWarning) {
            Logger logger = LOGGER;
            Level level = Level.WARNING;
            StringBuilder sb = new StringBuilder();
            sb.append("Connection ");
            sb.append(this);
            sb.append(" closed with error");
            logger.log(level, sb.toString(), e);
        }
        for (ConnectionListener listener : this.connectionListeners) {
            try {
                listener.connectionClosedOnError(e);
            } catch (Exception e2) {
                LOGGER.log(Level.SEVERE, "Error in listener while closing connection", e2);
            }
        }
    }

    public int getConnectionCounter() {
        return this.connectionCounterValue;
    }

    public void setFromMode(FromMode fromMode2) {
        this.fromMode = fromMode2;
    }

    public FromMode getFromMode() {
        return this.fromMode;
    }

    /* access modifiers changed from: protected */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void parseFeatures(org.xmlpull.v1.XmlPullParser r17) throws java.lang.Exception {
        /*
            r16 = this;
            r0 = r16
            java.util.Map<java.lang.String, org.jivesoftware.smack.packet.ExtensionElement> r1 = r0.streamFeatures
            r1.clear()
            int r1 = r17.getDepth()
        L_0x000b:
            int r2 = r17.next()
            java.lang.String r3 = "compression"
            java.lang.String r4 = "starttls"
            java.lang.String r5 = "bind"
            java.lang.String r6 = "mechanisms"
            r7 = 3
            r8 = 2
            if (r2 != r8) goto L_0x00ac
            int r9 = r17.getDepth()
            int r10 = r1 + 1
            if (r9 != r10) goto L_0x00ac
            r9 = 0
            java.lang.String r10 = r17.getName()
            java.lang.String r11 = r17.getNamespace()
            r12 = -1
            int r13 = r10.hashCode()
            r14 = 4
            r15 = 1
            switch(r13) {
                case -676919238: goto L_0x0059;
                case 3023933: goto L_0x0051;
                case 1316817241: goto L_0x0049;
                case 1431984486: goto L_0x0041;
                case 1984987798: goto L_0x0037;
                default: goto L_0x0036;
            }
        L_0x0036:
            goto L_0x0061
        L_0x0037:
            java.lang.String r3 = "session"
            boolean r3 = r10.equals(r3)
            if (r3 == 0) goto L_0x0036
            r3 = r7
            goto L_0x0062
        L_0x0041:
            boolean r3 = r10.equals(r3)
            if (r3 == 0) goto L_0x0036
            r3 = r14
            goto L_0x0062
        L_0x0049:
            boolean r3 = r10.equals(r4)
            if (r3 == 0) goto L_0x0036
            r3 = 0
            goto L_0x0062
        L_0x0051:
            boolean r3 = r10.equals(r5)
            if (r3 == 0) goto L_0x0036
            r3 = r8
            goto L_0x0062
        L_0x0059:
            boolean r3 = r10.equals(r6)
            if (r3 == 0) goto L_0x0036
            r3 = r15
            goto L_0x0062
        L_0x0061:
            r3 = r12
        L_0x0062:
            if (r3 == 0) goto L_0x009f
            if (r3 == r15) goto L_0x0092
            if (r3 == r8) goto L_0x008d
            if (r3 == r7) goto L_0x0086
            if (r3 == r14) goto L_0x007f
            org.jivesoftware.smack.provider.ExtensionElementProvider r3 = org.jivesoftware.smack.provider.ProviderManager.getStreamFeatureProvider(r10, r11)
            if (r3 == 0) goto L_0x007c
            r8 = r17
            org.jivesoftware.smack.packet.Element r4 = r3.parse(r8)
            r9 = r4
            org.jivesoftware.smack.packet.ExtensionElement r9 = (org.jivesoftware.smack.packet.ExtensionElement) r9
            goto L_0x00a6
        L_0x007c:
            r8 = r17
            goto L_0x00a6
        L_0x007f:
            r8 = r17
            org.jivesoftware.smack.compress.packet.Compress$Feature r9 = org.jivesoftware.smack.util.PacketParserUtils.parseCompressionFeature(r17)
            goto L_0x00a6
        L_0x0086:
            r8 = r17
            org.jivesoftware.smack.packet.Session$Feature r9 = org.jivesoftware.smack.util.PacketParserUtils.parseSessionFeature(r17)
            goto L_0x00a6
        L_0x008d:
            r8 = r17
            org.jivesoftware.smack.packet.Bind$Feature r9 = org.jivesoftware.smack.packet.Bind.Feature.INSTANCE
            goto L_0x00a6
        L_0x0092:
            r8 = r17
            org.jivesoftware.smack.packet.Mechanisms r3 = new org.jivesoftware.smack.packet.Mechanisms
            java.util.Collection r4 = org.jivesoftware.smack.util.PacketParserUtils.parseMechanisms(r17)
            r3.<init>(r4)
            r9 = r3
            goto L_0x00a6
        L_0x009f:
            r8 = r17
            org.jivesoftware.smack.packet.StartTls r9 = org.jivesoftware.smack.util.PacketParserUtils.parseStartTlsFeature(r17)
        L_0x00a6:
            if (r9 == 0) goto L_0x00ab
            r0.addStreamFeature(r9)
        L_0x00ab:
            goto L_0x00fc
        L_0x00ac:
            r8 = r17
            if (r2 != r7) goto L_0x00fc
            int r7 = r17.getDepth()
            if (r7 != r1) goto L_0x00fc
            java.lang.String r2 = "urn:ietf:params:xml:ns:xmpp-sasl"
            boolean r2 = r0.hasFeature(r6, r2)
            if (r2 == 0) goto L_0x00db
            java.lang.String r2 = "urn:ietf:params:xml:ns:xmpp-tls"
            boolean r2 = r0.hasFeature(r4, r2)
            if (r2 == 0) goto L_0x00d1
            org.jivesoftware.smack.ConnectionConfiguration r2 = r0.config
            org.jivesoftware.smack.ConnectionConfiguration$SecurityMode r2 = r2.getSecurityMode()
            org.jivesoftware.smack.ConnectionConfiguration$SecurityMode r4 = org.jivesoftware.smack.ConnectionConfiguration.SecurityMode.disabled
            if (r2 != r4) goto L_0x00db
        L_0x00d1:
            org.jivesoftware.smack.SynchronizationPoint<org.jivesoftware.smack.SmackException> r2 = r0.tlsHandled
            r2.reportSuccess()
            org.jivesoftware.smack.SynchronizationPoint<org.jivesoftware.smack.XMPPException> r2 = r0.saslFeatureReceived
            r2.reportSuccess()
        L_0x00db:
            java.lang.String r2 = "urn:ietf:params:xml:ns:xmpp-bind"
            boolean r2 = r0.hasFeature(r5, r2)
            if (r2 == 0) goto L_0x00f8
            java.lang.String r2 = "http://jabber.org/protocol/compress"
            boolean r2 = r0.hasFeature(r3, r2)
            if (r2 == 0) goto L_0x00f3
            org.jivesoftware.smack.ConnectionConfiguration r2 = r0.config
            boolean r2 = r2.isCompressionEnabled()
            if (r2 != 0) goto L_0x00f8
        L_0x00f3:
            org.jivesoftware.smack.SynchronizationPoint<org.jivesoftware.smack.SmackException> r2 = r0.lastFeaturesReceived
            r2.reportSuccess()
        L_0x00f8:
            r16.afterFeaturesReceived()
            return
        L_0x00fc:
            goto L_0x000b
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.AbstractXMPPConnection.parseFeatures(org.xmlpull.v1.XmlPullParser):void");
    }

    /* access modifiers changed from: protected */
    public void afterFeaturesReceived() throws SecurityRequiredException, NotConnectedException, InterruptedException {
    }

    public <F extends ExtensionElement> F getFeature(String element, String namespace) {
        return (ExtensionElement) this.streamFeatures.get(XmppStringUtils.generateKey(element, namespace));
    }

    public boolean hasFeature(String element, String namespace) {
        return getFeature(element, namespace) != null;
    }

    /* access modifiers changed from: protected */
    public void addStreamFeature(ExtensionElement feature) {
        this.streamFeatures.put(XmppStringUtils.generateKey(feature.getElementName(), feature.getNamespace()), feature);
    }

    public void sendStanzaWithResponseCallback(Stanza stanza, StanzaFilter replyFilter, StanzaListener callback) throws NotConnectedException, InterruptedException {
        sendStanzaWithResponseCallback(stanza, replyFilter, callback, null);
    }

    public void sendStanzaWithResponseCallback(Stanza stanza, StanzaFilter replyFilter, StanzaListener callback, ExceptionCallback exceptionCallback) throws NotConnectedException, InterruptedException {
        sendStanzaWithResponseCallback(stanza, replyFilter, callback, exceptionCallback, getReplyTimeout());
    }

    public SmackFuture<IQ, Exception> sendIqRequestAsync(IQ request) {
        return sendIqRequestAsync(request, getReplyTimeout());
    }

    public SmackFuture<IQ, Exception> sendIqRequestAsync(IQ request, long timeout) {
        return sendAsync(request, new IQReplyFilter(request, this), timeout);
    }

    public <S extends Stanza> SmackFuture<S, Exception> sendAsync(S stanza, StanzaFilter replyFilter) {
        return sendAsync(stanza, replyFilter, getReplyTimeout());
    }

    public <S extends Stanza> SmackFuture<S, Exception> sendAsync(S stanza, final StanzaFilter replyFilter, long timeout) {
        Objects.requireNonNull(stanza, "stanza must not be null");
        Objects.requireNonNull(replyFilter, "replyFilter must not be null");
        final InternalSmackFuture<S, Exception> future = new InternalSmackFuture<>();
        final StanzaListener stanzaListener = new StanzaListener() {
            public void processStanza(Stanza stanza) throws NotConnectedException, InterruptedException {
                if (AbstractXMPPConnection.this.removeAsyncStanzaListener(this)) {
                    try {
                        XMPPErrorException.ifHasErrorThenThrow(stanza);
                        future.setResult(stanza);
                    } catch (XMPPErrorException exception) {
                        future.setException(exception);
                    }
                }
            }
        };
        schedule(new Runnable() {
            public void run() {
                Exception exception;
                if (AbstractXMPPConnection.this.removeAsyncStanzaListener(stanzaListener)) {
                    if (!AbstractXMPPConnection.this.isConnected()) {
                        exception = new NotConnectedException((XMPPConnection) AbstractXMPPConnection.this, replyFilter);
                    } else {
                        exception = NoResponseException.newWith((XMPPConnection) AbstractXMPPConnection.this, replyFilter);
                    }
                    future.setException(exception);
                }
            }
        }, timeout, TimeUnit.MILLISECONDS);
        addAsyncStanzaListener(stanzaListener, replyFilter);
        try {
            sendStanza(stanza);
        } catch (InterruptedException | NotConnectedException exception) {
            future.setException(exception);
        }
        return future;
    }

    public void sendStanzaWithResponseCallback(Stanza stanza, final StanzaFilter replyFilter, final StanzaListener callback, final ExceptionCallback exceptionCallback, long timeout) throws NotConnectedException, InterruptedException {
        Objects.requireNonNull(stanza, "stanza must not be null");
        Objects.requireNonNull(replyFilter, "replyFilter must not be null");
        Objects.requireNonNull(callback, "callback must not be null");
        final StanzaListener packetListener = new StanzaListener() {
            public void processStanza(Stanza packet) throws NotConnectedException, InterruptedException, NotLoggedInException {
                if (AbstractXMPPConnection.this.removeAsyncStanzaListener(this)) {
                    try {
                        XMPPErrorException.ifHasErrorThenThrow(packet);
                        callback.processStanza(packet);
                    } catch (XMPPErrorException e) {
                        ExceptionCallback exceptionCallback = exceptionCallback;
                        if (exceptionCallback != null) {
                            exceptionCallback.processException(e);
                        }
                    }
                }
            }
        };
        schedule(new Runnable() {
            public void run() {
                Exception exception;
                if (AbstractXMPPConnection.this.removeAsyncStanzaListener(packetListener) && exceptionCallback != null) {
                    if (!AbstractXMPPConnection.this.isConnected()) {
                        exception = new NotConnectedException((XMPPConnection) AbstractXMPPConnection.this, replyFilter);
                    } else {
                        exception = NoResponseException.newWith((XMPPConnection) AbstractXMPPConnection.this, replyFilter);
                    }
                    final Exception exceptionToProcess = exception;
                    Async.go(new Runnable() {
                        public void run() {
                            exceptionCallback.processException(exceptionToProcess);
                        }
                    });
                }
            }
        }, timeout, TimeUnit.MILLISECONDS);
        addAsyncStanzaListener(packetListener, replyFilter);
        sendStanza(stanza);
    }

    public void sendIqWithResponseCallback(IQ iqRequest, StanzaListener callback) throws NotConnectedException, InterruptedException {
        sendIqWithResponseCallback(iqRequest, callback, null);
    }

    public void sendIqWithResponseCallback(IQ iqRequest, StanzaListener callback, ExceptionCallback exceptionCallback) throws NotConnectedException, InterruptedException {
        sendIqWithResponseCallback(iqRequest, callback, exceptionCallback, getReplyTimeout());
    }

    public void sendIqWithResponseCallback(IQ iqRequest, StanzaListener callback, ExceptionCallback exceptionCallback, long timeout) throws NotConnectedException, InterruptedException {
        sendStanzaWithResponseCallback(iqRequest, new IQReplyFilter(iqRequest, this), callback, exceptionCallback, timeout);
    }

    public void addOneTimeSyncCallback(final StanzaListener callback, StanzaFilter packetFilter) {
        final StanzaListener packetListener = new StanzaListener() {
            public void processStanza(Stanza packet) throws NotConnectedException, InterruptedException, NotLoggedInException {
                try {
                    callback.processStanza(packet);
                } finally {
                    AbstractXMPPConnection.this.removeSyncStanzaListener(this);
                }
            }
        };
        addSyncStanzaListener(packetListener, packetFilter);
        schedule(new Runnable() {
            public void run() {
                AbstractXMPPConnection.this.removeSyncStanzaListener(packetListener);
            }
        }, getReplyTimeout(), TimeUnit.MILLISECONDS);
    }

    public IQRequestHandler registerIQRequestHandler(IQRequestHandler iqRequestHandler) {
        IQRequestHandler iQRequestHandler;
        IQRequestHandler iQRequestHandler2;
        String key = XmppStringUtils.generateKey(iqRequestHandler.getElement(), iqRequestHandler.getNamespace());
        int i = AnonymousClass15.$SwitchMap$org$jivesoftware$smack$packet$IQ$Type[iqRequestHandler.getType().ordinal()];
        if (i == 1) {
            synchronized (this.setIqRequestHandler) {
                iQRequestHandler = (IQRequestHandler) this.setIqRequestHandler.put(key, iqRequestHandler);
            }
            return iQRequestHandler;
        } else if (i == 2) {
            synchronized (this.getIqRequestHandler) {
                iQRequestHandler2 = (IQRequestHandler) this.getIqRequestHandler.put(key, iqRequestHandler);
            }
            return iQRequestHandler2;
        } else {
            throw new IllegalArgumentException("Only IQ type of 'get' and 'set' allowed");
        }
    }

    public final IQRequestHandler unregisterIQRequestHandler(IQRequestHandler iqRequestHandler) {
        return unregisterIQRequestHandler(iqRequestHandler.getElement(), iqRequestHandler.getNamespace(), iqRequestHandler.getType());
    }

    public IQRequestHandler unregisterIQRequestHandler(String element, String namespace, Type type) {
        IQRequestHandler iQRequestHandler;
        IQRequestHandler iQRequestHandler2;
        String key = XmppStringUtils.generateKey(element, namespace);
        int i = AnonymousClass15.$SwitchMap$org$jivesoftware$smack$packet$IQ$Type[type.ordinal()];
        if (i == 1) {
            synchronized (this.setIqRequestHandler) {
                iQRequestHandler = (IQRequestHandler) this.setIqRequestHandler.remove(key);
            }
            return iQRequestHandler;
        } else if (i == 2) {
            synchronized (this.getIqRequestHandler) {
                iQRequestHandler2 = (IQRequestHandler) this.getIqRequestHandler.remove(key);
            }
            return iQRequestHandler2;
        } else {
            throw new IllegalArgumentException("Only IQ type of 'get' and 'set' allowed");
        }
    }

    public long getLastStanzaReceived() {
        return this.lastStanzaReceived;
    }

    public final long getAuthenticatedConnectionInitiallyEstablishedTimestamp() {
        return this.authenticatedConnectionInitiallyEstablishedTimestamp;
    }

    public void setParsingExceptionCallback(ParsingExceptionCallback callback) {
        this.parsingExceptionCallback = callback;
    }

    public ParsingExceptionCallback getParsingExceptionCallback() {
        return this.parsingExceptionCallback;
    }

    public final String toString() {
        EntityFullJid localEndpoint = getUser();
        String localEndpointString = localEndpoint == null ? "not-authenticated" : localEndpoint.toString();
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append('[');
        sb.append(localEndpointString);
        sb.append("] (");
        sb.append(getConnectionCounter());
        sb.append(')');
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public void asyncGoLimited(final Runnable runnable) {
        Runnable wrappedRunnable = new Runnable() {
            public void run() {
                runnable.run();
                synchronized (AbstractXMPPConnection.this.deferredAsyncRunnables) {
                    Runnable defferredRunnable = (Runnable) AbstractXMPPConnection.this.deferredAsyncRunnables.poll();
                    if (defferredRunnable == null) {
                        
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x001c: INVOKE  (wrap: org.jivesoftware.smack.AbstractXMPPConnection
                              0x001a: IGET  (r2v1 org.jivesoftware.smack.AbstractXMPPConnection) = (r3v0 'this' org.jivesoftware.smack.AbstractXMPPConnection$14 A[THIS]) org.jivesoftware.smack.AbstractXMPPConnection.14.this$0 org.jivesoftware.smack.AbstractXMPPConnection) org.jivesoftware.smack.AbstractXMPPConnection.access$210(org.jivesoftware.smack.AbstractXMPPConnection):int type: STATIC in method: org.jivesoftware.smack.AbstractXMPPConnection.14.run():void, dex: classes.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:245)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:213)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:138)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeSynchronizedRegion(RegionGen.java:248)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:70)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:210)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:203)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:316)
                            	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:262)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:225)
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:661)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:595)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:353)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:239)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:213)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:210)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:203)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:316)
                            	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:262)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:225)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:76)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:32)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:20)
                            	at jadx.core.ProcessClass.process(ProcessClass.java:36)
                            	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
                            	at jadx.api.JavaClass.decompile(JavaClass.java:62)
                            	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
                            Caused by: org.objenesis.ObjenesisException: java.lang.ClassNotFoundException: sun.reflect.ReflectionFactory
                            	at org.objenesis.instantiator.sun.SunReflectionFactoryHelper.getReflectionFactoryClass(SunReflectionFactoryHelper.java:57)
                            	at org.objenesis.instantiator.sun.SunReflectionFactoryHelper.newConstructorForSerialization(SunReflectionFactoryHelper.java:37)
                            	at org.objenesis.instantiator.sun.SunReflectionFactoryInstantiator.<init>(SunReflectionFactoryInstantiator.java:41)
                            	at org.objenesis.strategy.StdInstantiatorStrategy.newInstantiatorOf(StdInstantiatorStrategy.java:68)
                            	at org.objenesis.ObjenesisBase.getInstantiatorOf(ObjenesisBase.java:94)
                            	at org.objenesis.ObjenesisBase.newInstance(ObjenesisBase.java:73)
                            	at com.rits.cloning.ObjenesisInstantiationStrategy.newInstance(ObjenesisInstantiationStrategy.java:17)
                            	at com.rits.cloning.Cloner.newInstance(Cloner.java:300)
                            	at com.rits.cloning.Cloner.cloneObject(Cloner.java:461)
                            	at com.rits.cloning.Cloner.cloneInternal(Cloner.java:456)
                            	at com.rits.cloning.Cloner.deepClone(Cloner.java:326)
                            	at jadx.core.dex.nodes.InsnNode.copy(InsnNode.java:352)
                            	at jadx.core.dex.nodes.InsnNode.copyCommonParams(InsnNode.java:333)
                            	at jadx.core.dex.instructions.IndexInsnNode.copy(IndexInsnNode.java:24)
                            	at jadx.core.dex.instructions.IndexInsnNode.copy(IndexInsnNode.java:9)
                            	at jadx.core.codegen.InsnGen.inlineMethod(InsnGen.java:880)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:669)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:357)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:239)
                            	... 45 more
                            Caused by: java.lang.ClassNotFoundException: sun.reflect.ReflectionFactory
                            	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(Unknown Source)
                            	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(Unknown Source)
                            	at java.base/java.lang.ClassLoader.loadClass(Unknown Source)
                            	at java.base/java.lang.Class.forName0(Native Method)
                            	at java.base/java.lang.Class.forName(Unknown Source)
                            	at org.objenesis.instantiator.sun.SunReflectionFactoryHelper.getReflectionFactoryClass(SunReflectionFactoryHelper.java:54)
                            	... 63 more
                            */
                        /*
                            this = this;
                            java.lang.Runnable r0 = r9
                            r0.run()
                            org.jivesoftware.smack.AbstractXMPPConnection r0 = org.jivesoftware.smack.AbstractXMPPConnection.this
                            java.util.Queue r0 = r0.deferredAsyncRunnables
                            monitor-enter(r0)
                            org.jivesoftware.smack.AbstractXMPPConnection r1 = org.jivesoftware.smack.AbstractXMPPConnection.this     // Catch:{ all -> 0x002a }
                            java.util.Queue r1 = r1.deferredAsyncRunnables     // Catch:{ all -> 0x002a }
                            java.lang.Object r1 = r1.poll()     // Catch:{ all -> 0x002a }
                            java.lang.Runnable r1 = (java.lang.Runnable) r1     // Catch:{ all -> 0x002a }
                            if (r1 != 0) goto L_0x0020     // Catch:{ all -> 0x002a }
                            org.jivesoftware.smack.AbstractXMPPConnection r2 = org.jivesoftware.smack.AbstractXMPPConnection.this     // Catch:{ all -> 0x002a }
                            
                            // error: 0x001c: INVOKE  (r2 I:org.jivesoftware.smack.AbstractXMPPConnection) org.jivesoftware.smack.AbstractXMPPConnection.access$210(org.jivesoftware.smack.AbstractXMPPConnection):int type: STATIC
                            goto L_0x0028     // Catch:{ all -> 0x002a }
                        L_0x0020:
                            org.jivesoftware.smack.AbstractXMPPConnection r2 = org.jivesoftware.smack.AbstractXMPPConnection.this     // Catch:{ all -> 0x002a }
                            
                            // error: 0x0022: INVOKE  (r2 I:org.jivesoftware.smack.AbstractXMPPConnection) org.jivesoftware.smack.AbstractXMPPConnection.access$310(org.jivesoftware.smack.AbstractXMPPConnection):int type: STATIC
                            org.jivesoftware.smack.AbstractXMPPConnection.asyncGo(r1)     // Catch:{ all -> 0x002a }
                        L_0x0028:
                            monitor-exit(r0)     // Catch:{ all -> 0x002a }
                            return     // Catch:{ all -> 0x002a }
                        L_0x002a:
                            r1 = move-exception     // Catch:{ all -> 0x002a }
                            monitor-exit(r0)     // Catch:{ all -> 0x002a }
                            throw r1
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.AbstractXMPPConnection.AnonymousClass14.run():void");
                    }
                };
                synchronized (this.deferredAsyncRunnables) {
                    if (this.currentAsyncRunnables < this.maxAsyncRunnables) {
                        this.currentAsyncRunnables++;
                        asyncGo(wrappedRunnable);
                    } else {
                        this.deferredAsyncRunnablesCount++;
                        this.deferredAsyncRunnables.add(wrappedRunnable);
                    }
                    int deferredAsyncRunnablesCount2 = this.deferredAsyncRunnablesCount;
                    if (deferredAsyncRunnablesCount2 >= 100 && this.deferredAsyncRunnablesCountPrevious < 100) {
                        LOGGER.log(Level.WARNING, "High watermark of 100 simultaneous executing runnables reached");
                    } else if (deferredAsyncRunnablesCount2 >= 20 && this.deferredAsyncRunnablesCountPrevious < 20) {
                        LOGGER.log(Level.INFO, "20 simultaneous executing runnables reached");
                    }
                    this.deferredAsyncRunnablesCountPrevious = deferredAsyncRunnablesCount2;
                }
            }

            public void setMaxAsyncOperations(int maxAsyncOperations) {
                if (maxAsyncOperations >= 1) {
                    synchronized (this.deferredAsyncRunnables) {
                        this.maxAsyncRunnables = maxAsyncOperations;
                    }
                    return;
                }
                throw new IllegalArgumentException("Max async operations must be greater than 0");
            }

            protected static void asyncGo(Runnable runnable) {
                CACHED_EXECUTOR_SERVICE.execute(runnable);
            }

            protected static ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
                return SCHEDULED_EXECUTOR_SERVICE.schedule(runnable, delay, unit);
            }
        }
