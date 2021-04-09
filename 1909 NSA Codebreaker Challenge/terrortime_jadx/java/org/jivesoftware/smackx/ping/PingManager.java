package org.jivesoftware.smackx.ping;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.jivesoftware.smack.AbstractConnectionClosedListener;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackFuture;
import org.jivesoftware.smack.SmackFuture.InternalProcessStanzaSmackFuture;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smack.util.ExceptionCallback;
import org.jivesoftware.smack.util.SuccessCallback;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.ping.packet.Ping;
import org.jxmpp.jid.Jid;

public final class PingManager extends Manager {
    private static final Map<XMPPConnection, PingManager> INSTANCES = new WeakHashMap();
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(PingManager.class.getName());
    private static int defaultPingInterval = 1800;
    private ScheduledFuture<?> nextAutomaticPing;
    private final Set<PingFailedListener> pingFailedListeners = new CopyOnWriteArraySet();
    private int pingInterval = defaultPingInterval;
    private final Runnable pingServerRunnable = new Runnable() {
        public void run() {
            PingManager.LOGGER.fine("ServerPingTask run()");
            PingManager.this.pingServerIfNecessary();
        }
    };

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                PingManager.getInstanceFor(connection);
            }
        });
    }

    public static synchronized PingManager getInstanceFor(XMPPConnection connection) {
        PingManager pingManager;
        synchronized (PingManager.class) {
            pingManager = (PingManager) INSTANCES.get(connection);
            if (pingManager == null) {
                pingManager = new PingManager(connection);
                INSTANCES.put(connection, pingManager);
            }
        }
        return pingManager;
    }

    public static void setDefaultPingInterval(int interval) {
        defaultPingInterval = interval;
    }

    private PingManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature(Ping.NAMESPACE);
        AnonymousClass2 r2 = new AbstractIqRequestHandler(Ping.ELEMENT, Ping.NAMESPACE, Type.get, Mode.async) {
            public IQ handleIQRequest(IQ iqRequest) {
                return ((Ping) iqRequest).getPong();
            }
        };
        connection.registerIQRequestHandler(r2);
        connection.addConnectionListener(new AbstractConnectionClosedListener() {
            public void authenticated(XMPPConnection connection, boolean resumed) {
                PingManager.this.maybeSchedulePingServerTask();
            }

            public void connectionTerminated() {
                PingManager.this.maybeStopPingServerTask();
            }
        });
        maybeSchedulePingServerTask();
    }

    /* access modifiers changed from: private */
    public boolean isValidErrorPong(Jid destinationJid, XMPPErrorException xmppErrorException) {
        boolean z = true;
        if (destinationJid.equals((CharSequence) connection().getXMPPServiceDomain())) {
            return true;
        }
        StanzaError xmppError = xmppErrorException.getStanzaError();
        StanzaError.Type type = xmppError.getType();
        Condition condition = xmppError.getCondition();
        if (!(type == StanzaError.Type.CANCEL && condition == Condition.feature_not_implemented)) {
            z = false;
        }
        return z;
    }

    public SmackFuture<Boolean, Exception> pingAsync(Jid jid) {
        return pingAsync(jid, connection().getReplyTimeout());
    }

    public SmackFuture<Boolean, Exception> pingAsync(final Jid jid, long pongTimeout) {
        final InternalProcessStanzaSmackFuture<Boolean, Exception> future = new InternalProcessStanzaSmackFuture<Boolean, Exception>() {
            public void handleStanza(Stanza packet) {
                setResult(Boolean.valueOf(true));
            }

            public boolean isNonFatalException(Exception exception) {
                if (exception instanceof XMPPErrorException) {
                    if (PingManager.this.isValidErrorPong(jid, (XMPPErrorException) exception)) {
                        setResult(Boolean.valueOf(true));
                        return true;
                    }
                }
                return false;
            }
        };
        connection().sendIqRequestAsync(new Ping(jid), pongTimeout).onSuccess(new SuccessCallback<IQ>() {
            public void onSuccess(IQ result) {
                future.processStanza(result);
            }
        }).onError(new ExceptionCallback<Exception>() {
            public void processException(Exception exception) {
                future.processException(exception);
            }
        });
        return future;
    }

    public boolean ping(Jid jid, long pingTimeout) throws NotConnectedException, NoResponseException, InterruptedException {
        XMPPConnection connection = connection();
        if (connection.isAuthenticated()) {
            try {
                connection.createStanzaCollectorAndSend(new Ping(jid)).nextResultOrThrow(pingTimeout);
                return true;
            } catch (XMPPErrorException e) {
                return isValidErrorPong(jid, e);
            }
        } else {
            throw new NotConnectedException();
        }
    }

    public boolean ping(Jid jid) throws NotConnectedException, NoResponseException, InterruptedException {
        return ping(jid, connection().getReplyTimeout());
    }

    public boolean isPingSupported(Jid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(jid, Ping.NAMESPACE);
    }

    public boolean pingMyServer() throws NotConnectedException, InterruptedException {
        return pingMyServer(true);
    }

    public boolean pingMyServer(boolean notifyListeners) throws NotConnectedException, InterruptedException {
        return pingMyServer(notifyListeners, connection().getReplyTimeout());
    }

    public boolean pingMyServer(boolean notifyListeners, long pingTimeout) throws NotConnectedException, InterruptedException {
        boolean res;
        try {
            res = ping(connection().getXMPPServiceDomain(), pingTimeout);
        } catch (NoResponseException e) {
            res = false;
        }
        if (!res && notifyListeners) {
            for (PingFailedListener l : this.pingFailedListeners) {
                l.pingFailed();
            }
        }
        return res;
    }

    public void setPingInterval(int pingInterval2) {
        this.pingInterval = pingInterval2;
        maybeSchedulePingServerTask();
    }

    public int getPingInterval() {
        return this.pingInterval;
    }

    public void registerPingFailedListener(PingFailedListener listener) {
        this.pingFailedListeners.add(listener);
    }

    public void unregisterPingFailedListener(PingFailedListener listener) {
        this.pingFailedListeners.remove(listener);
    }

    /* access modifiers changed from: private */
    public void maybeSchedulePingServerTask() {
        maybeSchedulePingServerTask(0);
    }

    private synchronized void maybeSchedulePingServerTask(int delta) {
        maybeStopPingServerTask();
        if (this.pingInterval > 0) {
            int nextPingIn = this.pingInterval - delta;
            Logger logger = LOGGER;
            StringBuilder sb = new StringBuilder();
            sb.append("Scheduling ServerPingTask in ");
            sb.append(nextPingIn);
            sb.append(" seconds (pingInterval=");
            sb.append(this.pingInterval);
            sb.append(", delta=");
            sb.append(delta);
            sb.append(")");
            logger.fine(sb.toString());
            this.nextAutomaticPing = schedule(this.pingServerRunnable, (long) nextPingIn, TimeUnit.SECONDS);
        }
    }

    /* access modifiers changed from: private */
    public void maybeStopPingServerTask() {
        ScheduledFuture<?> scheduledFuture = this.nextAutomaticPing;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            this.nextAutomaticPing = null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0091, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void pingServerIfNecessary() {
        /*
            r14 = this;
            monitor-enter(r14)
            r0 = 1000(0x3e8, float:1.401E-42)
            r1 = 3
            org.jivesoftware.smack.XMPPConnection r2 = r14.connection()     // Catch:{ all -> 0x0092 }
            if (r2 != 0) goto L_0x000c
            monitor-exit(r14)
            return
        L_0x000c:
            int r3 = r14.pingInterval     // Catch:{ all -> 0x0092 }
            if (r3 > 0) goto L_0x0012
            monitor-exit(r14)
            return
        L_0x0012:
            long r3 = r2.getLastStanzaReceived()     // Catch:{ all -> 0x0092 }
            r5 = 0
            int r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            r6 = 1000(0x3e8, double:4.94E-321)
            if (r5 <= 0) goto L_0x002f
            long r8 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0092 }
            long r10 = r8 - r3
            long r10 = r10 / r6
            int r5 = (int) r10     // Catch:{ all -> 0x0092 }
            int r10 = r14.pingInterval     // Catch:{ all -> 0x0092 }
            if (r5 >= r10) goto L_0x002f
            r14.maybeSchedulePingServerTask(r5)     // Catch:{ all -> 0x0092 }
            monitor-exit(r14)
            return
        L_0x002f:
            boolean r5 = r2.isAuthenticated()     // Catch:{ all -> 0x0092 }
            if (r5 == 0) goto L_0x0089
            r5 = 0
            r8 = 0
        L_0x0037:
            r9 = 3
            if (r8 >= r9) goto L_0x006c
            if (r8 == 0) goto L_0x0043
            java.lang.Thread.sleep(r6)     // Catch:{ InterruptedException -> 0x0040 }
            goto L_0x0043
        L_0x0040:
            r6 = move-exception
            monitor-exit(r14)
            return
        L_0x0043:
            r9 = 0
            boolean r9 = r14.pingMyServer(r9)     // Catch:{ InterruptedException -> 0x004c, SmackException -> 0x004a }
            r5 = r9
            goto L_0x0066
        L_0x004a:
            r9 = move-exception
            goto L_0x004d
        L_0x004c:
            r9 = move-exception
        L_0x004d:
            java.util.logging.Logger r10 = LOGGER     // Catch:{ all -> 0x0092 }
            java.util.logging.Level r11 = java.util.logging.Level.WARNING     // Catch:{ all -> 0x0092 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x0092 }
            r12.<init>()     // Catch:{ all -> 0x0092 }
            java.lang.String r13 = "Exception while pinging server of "
            r12.append(r13)     // Catch:{ all -> 0x0092 }
            r12.append(r2)     // Catch:{ all -> 0x0092 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x0092 }
            r10.log(r11, r12, r9)     // Catch:{ all -> 0x0092 }
            r5 = 0
        L_0x0066:
            if (r5 == 0) goto L_0x0069
            goto L_0x006c
        L_0x0069:
            int r8 = r8 + 1
            goto L_0x0037
        L_0x006c:
            if (r5 != 0) goto L_0x0085
            java.util.Set<org.jivesoftware.smackx.ping.PingFailedListener> r6 = r14.pingFailedListeners     // Catch:{ all -> 0x0092 }
            java.util.Iterator r6 = r6.iterator()     // Catch:{ all -> 0x0092 }
        L_0x0074:
            boolean r7 = r6.hasNext()     // Catch:{ all -> 0x0092 }
            if (r7 == 0) goto L_0x0084
            java.lang.Object r7 = r6.next()     // Catch:{ all -> 0x0092 }
            org.jivesoftware.smackx.ping.PingFailedListener r7 = (org.jivesoftware.smackx.ping.PingFailedListener) r7     // Catch:{ all -> 0x0092 }
            r7.pingFailed()     // Catch:{ all -> 0x0092 }
            goto L_0x0074
        L_0x0084:
            goto L_0x0088
        L_0x0085:
            r14.maybeSchedulePingServerTask()     // Catch:{ all -> 0x0092 }
        L_0x0088:
            goto L_0x0090
        L_0x0089:
            java.util.logging.Logger r5 = LOGGER     // Catch:{ all -> 0x0092 }
            java.lang.String r6 = "XMPPConnection was not authenticated"
            r5.warning(r6)     // Catch:{ all -> 0x0092 }
        L_0x0090:
            monitor-exit(r14)
            return
        L_0x0092:
            r0 = move-exception
            monitor-exit(r14)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.ping.PingManager.pingServerIfNecessary():void");
    }
}
