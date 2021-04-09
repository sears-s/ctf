package org.jivesoftware.smack;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;
import org.jivesoftware.smack.XMPPException.StreamErrorException;
import org.jivesoftware.smack.packet.StreamError.Condition;
import org.jivesoftware.smack.util.Async;

public final class ReconnectionManager {
    private static final Map<AbstractXMPPConnection, ReconnectionManager> INSTANCES = new WeakHashMap();
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(ReconnectionManager.class.getName());
    private static int defaultFixedDelay = 15;
    private static ReconnectionPolicy defaultReconnectionPolicy = ReconnectionPolicy.RANDOM_INCREASING_DELAY;
    private static boolean enabledPerDefault = false;
    private boolean automaticReconnectEnabled = false;
    private final ConnectionListener connectionListener = new AbstractConnectionListener() {
        public void connectionClosed() {
            ReconnectionManager.this.done = true;
        }

        public void authenticated(XMPPConnection connection, boolean resumed) {
            ReconnectionManager.this.done = false;
        }

        public void connectionClosedOnError(Exception e) {
            ReconnectionManager reconnectionManager = ReconnectionManager.this;
            reconnectionManager.done = false;
            if (reconnectionManager.isAutomaticReconnectEnabled()) {
                if (e instanceof StreamErrorException) {
                    if (Condition.conflict == ((StreamErrorException) e).getStreamError().getCondition()) {
                        return;
                    }
                }
                ReconnectionManager.this.reconnect();
            }
        }
    };
    boolean done = false;
    /* access modifiers changed from: private */
    public volatile int fixedDelay = defaultFixedDelay;
    /* access modifiers changed from: private */
    public final int randomBase = (new Random().nextInt(13) + 2);
    /* access modifiers changed from: private */
    public final Set<ReconnectionListener> reconnectionListeners = new CopyOnWriteArraySet();
    /* access modifiers changed from: private */
    public volatile ReconnectionPolicy reconnectionPolicy = defaultReconnectionPolicy;
    private final Runnable reconnectionRunnable;
    private Thread reconnectionThread;
    /* access modifiers changed from: private */
    public final WeakReference<AbstractXMPPConnection> weakRefConnection;

    /* renamed from: org.jivesoftware.smack.ReconnectionManager$4 reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$ReconnectionManager$ReconnectionPolicy = new int[ReconnectionPolicy.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$ReconnectionManager$ReconnectionPolicy[ReconnectionPolicy.FIXED_DELAY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$ReconnectionManager$ReconnectionPolicy[ReconnectionPolicy.RANDOM_INCREASING_DELAY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public enum ReconnectionPolicy {
        RANDOM_INCREASING_DELAY,
        FIXED_DELAY
    }

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                if (connection instanceof AbstractXMPPConnection) {
                    ReconnectionManager.getInstanceFor((AbstractXMPPConnection) connection);
                }
            }
        });
    }

    public static synchronized ReconnectionManager getInstanceFor(AbstractXMPPConnection connection) {
        ReconnectionManager reconnectionManager;
        synchronized (ReconnectionManager.class) {
            reconnectionManager = (ReconnectionManager) INSTANCES.get(connection);
            if (reconnectionManager == null) {
                reconnectionManager = new ReconnectionManager(connection);
                INSTANCES.put(connection, reconnectionManager);
            }
        }
        return reconnectionManager;
    }

    public static void setEnabledPerDefault(boolean enabled) {
        enabledPerDefault = enabled;
    }

    public static boolean getEnabledPerDefault() {
        return enabledPerDefault;
    }

    public static void setDefaultFixedDelay(int fixedDelay2) {
        defaultFixedDelay = fixedDelay2;
        setDefaultReconnectionPolicy(ReconnectionPolicy.FIXED_DELAY);
    }

    public static void setDefaultReconnectionPolicy(ReconnectionPolicy reconnectionPolicy2) {
        defaultReconnectionPolicy = reconnectionPolicy2;
    }

    public boolean addReconnectionListener(ReconnectionListener listener) {
        return this.reconnectionListeners.add(listener);
    }

    public boolean removeReconnectionListener(ReconnectionListener listener) {
        return this.reconnectionListeners.remove(listener);
    }

    public void setFixedDelay(int fixedDelay2) {
        this.fixedDelay = fixedDelay2;
        setReconnectionPolicy(ReconnectionPolicy.FIXED_DELAY);
    }

    public void setReconnectionPolicy(ReconnectionPolicy reconnectionPolicy2) {
        this.reconnectionPolicy = reconnectionPolicy2;
    }

    private ReconnectionManager(AbstractXMPPConnection connection) {
        this.weakRefConnection = new WeakReference<>(connection);
        this.reconnectionRunnable = new Runnable() {
            private int attempts = 0;

            private int timeDelay() {
                this.attempts++;
                int i = AnonymousClass4.$SwitchMap$org$jivesoftware$smack$ReconnectionManager$ReconnectionPolicy[ReconnectionManager.this.reconnectionPolicy.ordinal()];
                if (i == 1) {
                    return ReconnectionManager.this.fixedDelay;
                }
                if (i == 2) {
                    int i2 = this.attempts;
                    if (i2 > 13) {
                        return ReconnectionManager.this.randomBase * 6 * 5;
                    }
                    if (i2 > 7) {
                        return ReconnectionManager.this.randomBase * 6;
                    }
                    return ReconnectionManager.this.randomBase;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Unknown reconnection policy ");
                sb.append(ReconnectionManager.this.reconnectionPolicy);
                throw new AssertionError(sb.toString());
            }

            /* JADX WARNING: Code restructure failed: missing block: B:21:0x0058, code lost:
                r4 = org.jivesoftware.smack.ReconnectionManager.access$500(r8.this$0).iterator();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:23:0x0066, code lost:
                if (r4.hasNext() == false) goto L_0x0072;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:24:0x0068, code lost:
                ((org.jivesoftware.smack.ReconnectionListener) r4.next()).reconnectingIn(0);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:26:0x0078, code lost:
                if (org.jivesoftware.smack.ReconnectionManager.access$400(r8.this$0, r0) != false) goto L_0x007b;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:27:0x007a, code lost:
                return;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
                r0.connect();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:30:0x007f, code lost:
                r1 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:31:0x0081, code lost:
                r3 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:32:0x0087, code lost:
                r1 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:33:0x0089, code lost:
                r4 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
                org.jivesoftware.smack.ReconnectionManager.access$600().log(java.util.logging.Level.FINER, "Connection was already connected on reconnection attempt", r4);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:37:0x0099, code lost:
                org.jivesoftware.smack.ReconnectionManager.access$600().log(java.util.logging.Level.FINE, r3, r1);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:38:0x00a2, code lost:
                return;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:39:0x00a3, code lost:
                r4 = org.jivesoftware.smack.ReconnectionManager.access$500(r8.this$0).iterator();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:41:0x00b1, code lost:
                if (r4.hasNext() != false) goto L_0x00b3;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:42:0x00b3, code lost:
                ((org.jivesoftware.smack.ReconnectionListener) r4.next()).reconnectionFailed(r3);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:43:0x00bf, code lost:
                org.jivesoftware.smack.ReconnectionManager.access$600().log(java.util.logging.Level.FINER, "Reconnection not required, was already logged in", r1);
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r8 = this;
                    org.jivesoftware.smack.ReconnectionManager r0 = org.jivesoftware.smack.ReconnectionManager.this
                    java.lang.ref.WeakReference r0 = r0.weakRefConnection
                    java.lang.Object r0 = r0.get()
                    org.jivesoftware.smack.AbstractXMPPConnection r0 = (org.jivesoftware.smack.AbstractXMPPConnection) r0
                    if (r0 != 0) goto L_0x000f
                    return
                L_0x000f:
                    r1 = 0
                    r8.attempts = r1
                L_0x0012:
                    org.jivesoftware.smack.ReconnectionManager r2 = org.jivesoftware.smack.ReconnectionManager.this
                    boolean r2 = r2.isReconnectionPossible(r0)
                    if (r2 == 0) goto L_0x00cc
                    int r2 = r8.timeDelay()
                L_0x001e:
                    java.lang.String r3 = "Reconnection Thread was interrupted, aborting reconnection mechanism"
                    if (r2 <= 0) goto L_0x0058
                    org.jivesoftware.smack.ReconnectionManager r4 = org.jivesoftware.smack.ReconnectionManager.this
                    boolean r4 = r4.isReconnectionPossible(r0)
                    if (r4 != 0) goto L_0x002b
                    return
                L_0x002b:
                    r4 = 1000(0x3e8, double:4.94E-321)
                    java.lang.Thread.sleep(r4)     // Catch:{ InterruptedException -> 0x004d }
                    int r2 = r2 + -1
                    org.jivesoftware.smack.ReconnectionManager r4 = org.jivesoftware.smack.ReconnectionManager.this     // Catch:{ InterruptedException -> 0x004d }
                    java.util.Set r4 = r4.reconnectionListeners     // Catch:{ InterruptedException -> 0x004d }
                    java.util.Iterator r4 = r4.iterator()     // Catch:{ InterruptedException -> 0x004d }
                L_0x003c:
                    boolean r5 = r4.hasNext()     // Catch:{ InterruptedException -> 0x004d }
                    if (r5 == 0) goto L_0x004c
                    java.lang.Object r5 = r4.next()     // Catch:{ InterruptedException -> 0x004d }
                    org.jivesoftware.smack.ReconnectionListener r5 = (org.jivesoftware.smack.ReconnectionListener) r5     // Catch:{ InterruptedException -> 0x004d }
                    r5.reconnectingIn(r2)     // Catch:{ InterruptedException -> 0x004d }
                    goto L_0x003c
                L_0x004c:
                    goto L_0x001e
                L_0x004d:
                    r1 = move-exception
                    java.util.logging.Logger r4 = org.jivesoftware.smack.ReconnectionManager.LOGGER
                    java.util.logging.Level r5 = java.util.logging.Level.FINE
                    r4.log(r5, r3, r1)
                    return
                L_0x0058:
                    org.jivesoftware.smack.ReconnectionManager r4 = org.jivesoftware.smack.ReconnectionManager.this
                    java.util.Set r4 = r4.reconnectionListeners
                    java.util.Iterator r4 = r4.iterator()
                L_0x0062:
                    boolean r5 = r4.hasNext()
                    if (r5 == 0) goto L_0x0072
                    java.lang.Object r5 = r4.next()
                    org.jivesoftware.smack.ReconnectionListener r5 = (org.jivesoftware.smack.ReconnectionListener) r5
                    r5.reconnectingIn(r1)
                    goto L_0x0062
                L_0x0072:
                    org.jivesoftware.smack.ReconnectionManager r4 = org.jivesoftware.smack.ReconnectionManager.this
                    boolean r4 = r4.isReconnectionPossible(r0)
                    if (r4 != 0) goto L_0x007b
                    return
                L_0x007b:
                    r0.connect()     // Catch:{ AlreadyConnectedException -> 0x0089 }
                    goto L_0x0095
                L_0x007f:
                    r1 = move-exception
                    goto L_0x0099
                L_0x0081:
                    r3 = move-exception
                    goto L_0x00a3
                L_0x0083:
                    r3 = move-exception
                    goto L_0x00a3
                L_0x0085:
                    r3 = move-exception
                    goto L_0x00a3
                L_0x0087:
                    r1 = move-exception
                    goto L_0x00bf
                L_0x0089:
                    r4 = move-exception
                    java.util.logging.Logger r5 = org.jivesoftware.smack.ReconnectionManager.LOGGER     // Catch:{ AlreadyLoggedInException -> 0x0087, SmackException -> 0x0085, IOException -> 0x0083, XMPPException -> 0x0081, InterruptedException -> 0x007f }
                    java.util.logging.Level r6 = java.util.logging.Level.FINER     // Catch:{ AlreadyLoggedInException -> 0x0087, SmackException -> 0x0085, IOException -> 0x0083, XMPPException -> 0x0081, InterruptedException -> 0x007f }
                    java.lang.String r7 = "Connection was already connected on reconnection attempt"
                    r5.log(r6, r7, r4)     // Catch:{ AlreadyLoggedInException -> 0x0087, SmackException -> 0x0085, IOException -> 0x0083, XMPPException -> 0x0081, InterruptedException -> 0x007f }
                L_0x0095:
                    r0.login()     // Catch:{ AlreadyLoggedInException -> 0x0087, SmackException -> 0x0085, IOException -> 0x0083, XMPPException -> 0x0081, InterruptedException -> 0x007f }
                    goto L_0x00ca
                L_0x0099:
                    java.util.logging.Logger r4 = org.jivesoftware.smack.ReconnectionManager.LOGGER
                    java.util.logging.Level r5 = java.util.logging.Level.FINE
                    r4.log(r5, r3, r1)
                    return
                L_0x00a3:
                    org.jivesoftware.smack.ReconnectionManager r4 = org.jivesoftware.smack.ReconnectionManager.this
                    java.util.Set r4 = r4.reconnectionListeners
                    java.util.Iterator r4 = r4.iterator()
                L_0x00ad:
                    boolean r5 = r4.hasNext()
                    if (r5 == 0) goto L_0x00bd
                    java.lang.Object r5 = r4.next()
                    org.jivesoftware.smack.ReconnectionListener r5 = (org.jivesoftware.smack.ReconnectionListener) r5
                    r5.reconnectionFailed(r3)
                    goto L_0x00ad
                L_0x00bd:
                    goto L_0x0012
                L_0x00bf:
                    java.util.logging.Logger r3 = org.jivesoftware.smack.ReconnectionManager.LOGGER
                    java.util.logging.Level r4 = java.util.logging.Level.FINER
                    java.lang.String r5 = "Reconnection not required, was already logged in"
                    r3.log(r4, r5, r1)
                L_0x00ca:
                    return
                L_0x00cc:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.ReconnectionManager.AnonymousClass2.run():void");
            }
        };
        if (getEnabledPerDefault()) {
            enableAutomaticReconnection();
        }
    }

    public synchronized void enableAutomaticReconnection() {
        if (!this.automaticReconnectEnabled) {
            XMPPConnection connection = (XMPPConnection) this.weakRefConnection.get();
            if (connection != null) {
                connection.addConnectionListener(this.connectionListener);
                this.automaticReconnectEnabled = true;
                return;
            }
            throw new IllegalStateException("Connection instance no longer available");
        }
    }

    public synchronized void disableAutomaticReconnection() {
        if (this.automaticReconnectEnabled) {
            XMPPConnection connection = (XMPPConnection) this.weakRefConnection.get();
            if (connection != null) {
                connection.removeConnectionListener(this.connectionListener);
                this.automaticReconnectEnabled = false;
                return;
            }
            throw new IllegalStateException("Connection instance no longer available");
        }
    }

    public synchronized boolean isAutomaticReconnectEnabled() {
        return this.automaticReconnectEnabled;
    }

    /* access modifiers changed from: private */
    public boolean isReconnectionPossible(XMPPConnection connection) {
        return !this.done && !connection.isConnected() && isAutomaticReconnectEnabled();
    }

    /* access modifiers changed from: private */
    public synchronized void reconnect() {
        XMPPConnection connection = (XMPPConnection) this.weakRefConnection.get();
        if (connection == null) {
            LOGGER.fine("Connection is null, will not reconnect");
        } else if (this.reconnectionThread == null || !this.reconnectionThread.isAlive()) {
            Runnable runnable = this.reconnectionRunnable;
            StringBuilder sb = new StringBuilder();
            sb.append("Smack Reconnection Manager (");
            sb.append(connection.getConnectionCounter());
            sb.append(')');
            this.reconnectionThread = Async.go(runnable, sb.toString());
        }
    }

    public synchronized void abortPossiblyRunningReconnection() {
        if (this.reconnectionThread != null) {
            this.reconnectionThread.interrupt();
            this.reconnectionThread = null;
        }
    }
}
