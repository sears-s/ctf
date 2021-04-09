package org.jivesoftware.smackx.bytestreams.socks5;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.FeatureNotSupportedException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smackx.bytestreams.BytestreamListener;
import org.jivesoftware.smackx.bytestreams.BytestreamManager;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHost;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverItems.Item;
import org.jxmpp.jid.Jid;

public final class Socks5BytestreamManager extends Manager implements BytestreamManager {
    private static final String SESSION_ID_PREFIX = "js5_";
    private static final Map<XMPPConnection, Socks5BytestreamManager> managers = new WeakHashMap();
    private static final Random randomGenerator = new Random();
    private final List<BytestreamListener> allRequestListeners = Collections.synchronizedList(new LinkedList());
    private final List<String> ignoredBytestreamRequests = Collections.synchronizedList(new LinkedList());
    private final InitiationListener initiationListener = new InitiationListener(this);
    private Jid lastWorkingProxy;
    private final Set<Jid> proxyBlacklist = Collections.synchronizedSet(new HashSet());
    private int proxyConnectionTimeout = 10000;
    private boolean proxyPrioritizationEnabled = true;
    private int targetResponseTimeout = 10000;
    private final Map<Jid, BytestreamListener> userListeners = new ConcurrentHashMap();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                Socks5BytestreamManager.getBytestreamManager(connection);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001e, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager getBytestreamManager(org.jivesoftware.smack.XMPPConnection r3) {
        /*
            java.lang.Class<org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager> r0 = org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager.class
            monitor-enter(r0)
            if (r3 != 0) goto L_0x0008
            r1 = 0
            monitor-exit(r0)
            return r1
        L_0x0008:
            java.util.Map<org.jivesoftware.smack.XMPPConnection, org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager> r1 = managers     // Catch:{ all -> 0x001f }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ all -> 0x001f }
            org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager r1 = (org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager) r1     // Catch:{ all -> 0x001f }
            if (r1 != 0) goto L_0x001d
            org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager r2 = new org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager     // Catch:{ all -> 0x001f }
            r2.<init>(r3)     // Catch:{ all -> 0x001f }
            r1 = r2
            java.util.Map<org.jivesoftware.smack.XMPPConnection, org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager> r2 = managers     // Catch:{ all -> 0x001f }
            r2.put(r3, r1)     // Catch:{ all -> 0x001f }
        L_0x001d:
            monitor-exit(r0)
            return r1
        L_0x001f:
            r3 = move-exception
            monitor-exit(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager.getBytestreamManager(org.jivesoftware.smack.XMPPConnection):org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager");
    }

    private Socks5BytestreamManager(XMPPConnection connection) {
        super(connection);
        activate();
    }

    public void addIncomingBytestreamListener(BytestreamListener listener) {
        this.allRequestListeners.add(listener);
    }

    public void removeIncomingBytestreamListener(BytestreamListener listener) {
        this.allRequestListeners.remove(listener);
    }

    public void addIncomingBytestreamListener(BytestreamListener listener, Jid initiatorJID) {
        this.userListeners.put(initiatorJID, listener);
    }

    public void removeIncomingBytestreamListener(Jid initiatorJID) {
        this.userListeners.remove(initiatorJID);
    }

    public void ignoreBytestreamRequestOnce(String sessionID) {
        this.ignoredBytestreamRequests.add(sessionID);
    }

    public synchronized void disableService() {
        XMPPConnection connection = connection();
        connection.unregisterIQRequestHandler(this.initiationListener);
        this.initiationListener.shutdown();
        this.allRequestListeners.clear();
        this.userListeners.clear();
        this.lastWorkingProxy = null;
        this.proxyBlacklist.clear();
        this.ignoredBytestreamRequests.clear();
        managers.remove(connection);
        if (managers.size() == 0) {
            Socks5Proxy.getSocks5Proxy().stop();
        }
        ServiceDiscoveryManager serviceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(connection);
        if (serviceDiscoveryManager != null) {
            serviceDiscoveryManager.removeFeature(Bytestream.NAMESPACE);
        }
    }

    public int getTargetResponseTimeout() {
        if (this.targetResponseTimeout <= 0) {
            this.targetResponseTimeout = 10000;
        }
        return this.targetResponseTimeout;
    }

    public void setTargetResponseTimeout(int targetResponseTimeout2) {
        this.targetResponseTimeout = targetResponseTimeout2;
    }

    public int getProxyConnectionTimeout() {
        if (this.proxyConnectionTimeout <= 0) {
            this.proxyConnectionTimeout = 10000;
        }
        return this.proxyConnectionTimeout;
    }

    public void setProxyConnectionTimeout(int proxyConnectionTimeout2) {
        this.proxyConnectionTimeout = proxyConnectionTimeout2;
    }

    public boolean isProxyPrioritizationEnabled() {
        return this.proxyPrioritizationEnabled;
    }

    public void setProxyPrioritizationEnabled(boolean proxyPrioritizationEnabled2) {
        this.proxyPrioritizationEnabled = proxyPrioritizationEnabled2;
    }

    public Socks5BytestreamSession establishSession(Jid targetJID) throws XMPPException, IOException, InterruptedException, SmackException {
        return establishSession(targetJID, getNextSessionID());
    }

    public Socks5BytestreamSession establishSession(Jid targetJID, String sessionID) throws IOException, InterruptedException, SmackException, XMPPException {
        Throwable th;
        Jid jid = targetJID;
        String str = sessionID;
        XMPPConnection connection = connection();
        if (supportsSocks5(targetJID)) {
            List<Jid> proxies = new ArrayList<>();
            try {
                proxies.addAll(determineProxies());
                th = null;
            } catch (XMPPErrorException e) {
                th = e;
            }
            List<StreamHost> streamHosts = determineStreamHostInfos(proxies);
            if (!streamHosts.isEmpty()) {
                String digest = Socks5Utils.createDigest(str, connection.getUser(), jid);
                if (this.proxyPrioritizationEnabled && this.lastWorkingProxy != null) {
                    StreamHost selectedStreamHost = null;
                    Iterator it = streamHosts.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        StreamHost streamHost = (StreamHost) it.next();
                        if (streamHost.getJID().equals((CharSequence) this.lastWorkingProxy)) {
                            selectedStreamHost = streamHost;
                            break;
                        }
                    }
                    if (selectedStreamHost != null) {
                        streamHosts.remove(selectedStreamHost);
                        streamHosts.add(0, selectedStreamHost);
                    }
                }
                Socks5Proxy socks5Proxy = Socks5Proxy.getSocks5Proxy();
                try {
                    socks5Proxy.addTransfer(digest);
                    Bytestream initiation = createBytestreamInitiation(str, jid, streamHosts);
                    StreamHost usedStreamHost = initiation.getStreamHost(((Bytestream) connection.createStanzaCollectorAndSend(initiation).nextResultOrThrow((long) getTargetResponseTimeout())).getUsedHost().getJID());
                    if (usedStreamHost != null) {
                        Socks5ClientForInitiator socks5ClientForInitiator = new Socks5ClientForInitiator(usedStreamHost, digest, connection, sessionID, targetJID);
                        Socket socket = socks5ClientForInitiator.getSocket(getProxyConnectionTimeout());
                        this.lastWorkingProxy = usedStreamHost.getJID();
                        Socks5BytestreamSession socks5BytestreamSession = new Socks5BytestreamSession(socket, usedStreamHost.getJID().equals((CharSequence) connection.getUser()));
                        socks5Proxy.removeTransfer(digest);
                        return socks5BytestreamSession;
                    }
                    throw new SmackException("Remote user responded with unknown host");
                } catch (TimeoutException e2) {
                    throw new IOException("Timeout while connecting to SOCKS5 proxy");
                } catch (Throwable th2) {
                    socks5Proxy.removeTransfer(digest);
                    throw th2;
                }
            } else if (th != null) {
                throw th;
            } else {
                throw new SmackException("no SOCKS5 proxies available");
            }
        } else {
            throw new FeatureNotSupportedException("SOCKS5 Bytestream", jid);
        }
    }

    private boolean supportsSocks5(Jid targetJID) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(targetJID, Bytestream.NAMESPACE);
    }

    public List<Jid> determineProxies() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        XMPPConnection connection = connection();
        ServiceDiscoveryManager serviceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(connection);
        List<Jid> proxies = new ArrayList<>();
        for (Item item : serviceDiscoveryManager.discoverItems(connection.getXMPPServiceDomain()).getItems()) {
            if (!this.proxyBlacklist.contains(item.getEntityID())) {
                try {
                    if (serviceDiscoveryManager.discoverInfo(item.getEntityID()).hasIdentity("proxy", "bytestreams")) {
                        proxies.add(item.getEntityID());
                    } else {
                        this.proxyBlacklist.add(item.getEntityID());
                    }
                } catch (NoResponseException | XMPPErrorException e) {
                    this.proxyBlacklist.add(item.getEntityID());
                }
            }
        }
        return proxies;
    }

    private List<StreamHost> determineStreamHostInfos(List<Jid> proxies) {
        XMPPConnection connection = connection();
        List<StreamHost> streamHosts = new ArrayList<>();
        List<StreamHost> localProxies = getLocalStreamHost();
        if (localProxies != null) {
            streamHosts.addAll(localProxies);
        }
        for (Jid proxy : proxies) {
            try {
                streamHosts.addAll(((Bytestream) connection.createStanzaCollectorAndSend(createStreamHostRequest(proxy)).nextResultOrThrow()).getStreamHosts());
            } catch (Exception e) {
                this.proxyBlacklist.add(proxy);
            }
        }
        return streamHosts;
    }

    private static Bytestream createStreamHostRequest(Jid proxy) {
        Bytestream request = new Bytestream();
        request.setType(Type.get);
        request.setTo(proxy);
        return request;
    }

    public List<StreamHost> getLocalStreamHost() {
        XMPPConnection connection = connection();
        Socks5Proxy socks5Server = Socks5Proxy.getSocks5Proxy();
        if (!socks5Server.isRunning()) {
            return null;
        }
        List<String> addresses = socks5Server.getLocalAddresses();
        if (addresses.isEmpty()) {
            return null;
        }
        int port = socks5Server.getPort();
        List<StreamHost> streamHosts = new ArrayList<>();
        for (String address : addresses) {
            String[] loopbackAddresses = {"127.0.0.1", "0:0:0:0:0:0:0:1", "::1"};
            int length = loopbackAddresses.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    streamHosts.add(new StreamHost(connection.getUser(), address, port));
                    break;
                } else if (address.startsWith(loopbackAddresses[i])) {
                    break;
                } else {
                    i++;
                }
            }
        }
        return streamHosts;
    }

    private static Bytestream createBytestreamInitiation(String sessionID, Jid targetJID, List<StreamHost> streamHosts) {
        Bytestream initiation = new Bytestream(sessionID);
        for (StreamHost streamHost : streamHosts) {
            initiation.addStreamHost(streamHost);
        }
        initiation.setType(Type.set);
        initiation.setTo(targetJID);
        return initiation;
    }

    /* access modifiers changed from: protected */
    public void replyRejectPacket(IQ packet) throws NotConnectedException, InterruptedException {
        connection().sendStanza(IQ.createErrorResponse(packet, StanzaError.getBuilder(Condition.not_acceptable)));
    }

    private void activate() {
        connection().registerIQRequestHandler(this.initiationListener);
        enableService();
    }

    private void enableService() {
        ServiceDiscoveryManager.getInstanceFor(connection()).addFeature(Bytestream.NAMESPACE);
    }

    private static String getNextSessionID() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(SESSION_ID_PREFIX);
        buffer.append(Math.abs(randomGenerator.nextLong()));
        return buffer.toString();
    }

    /* access modifiers changed from: protected */
    public XMPPConnection getConnection() {
        return connection();
    }

    /* access modifiers changed from: protected */
    public BytestreamListener getUserListener(Jid initiator) {
        return (BytestreamListener) this.userListeners.get(initiator);
    }

    /* access modifiers changed from: protected */
    public List<BytestreamListener> getAllRequestListeners() {
        return this.allRequestListeners;
    }

    /* access modifiers changed from: protected */
    public List<String> getIgnoredBytestreamRequests() {
        return this.ignoredBytestreamRequests;
    }
}
