package org.jivesoftware.smack.tcp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.DnssecMode;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.AlreadyConnectedException;
import org.jivesoftware.smack.SmackException.AlreadyLoggedInException;
import org.jivesoftware.smack.SmackException.ConnectionException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.SmackException.SecurityRequiredByServerException;
import org.jivesoftware.smack.SmackFuture.SocketFuture;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.SynchronizationPoint;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.FailedNonzaException;
import org.jivesoftware.smack.XMPPException.StreamErrorException;
import org.jivesoftware.smack.compress.packet.Compress;
import org.jivesoftware.smack.compress.packet.Compress.Feature;
import org.jivesoftware.smack.compression.XMPPInputOutputStream;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.Nonza;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StartTls;
import org.jivesoftware.smack.packet.StreamOpen;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.sm.SMUtils;
import org.jivesoftware.smack.sm.StreamManagementException;
import org.jivesoftware.smack.sm.StreamManagementException.StreamManagementCounterError;
import org.jivesoftware.smack.sm.StreamManagementException.StreamManagementNotEnabledException;
import org.jivesoftware.smack.sm.StreamManagementException.UnacknowledgedQueueFullException;
import org.jivesoftware.smack.sm.packet.StreamManagement;
import org.jivesoftware.smack.sm.packet.StreamManagement.AckAnswer;
import org.jivesoftware.smack.sm.packet.StreamManagement.AckRequest;
import org.jivesoftware.smack.sm.packet.StreamManagement.Enable;
import org.jivesoftware.smack.sm.packet.StreamManagement.Resume;
import org.jivesoftware.smack.sm.packet.StreamManagement.StreamManagementFeature;
import org.jivesoftware.smack.sm.predicates.Predicate;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.Builder;
import org.jivesoftware.smack.util.ArrayBlockingQueueWithShutdown;
import org.jivesoftware.smack.util.Async;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smack.util.dns.HostAddress;
import org.jivesoftware.smack.util.dns.SmackDaneProvider;
import org.jivesoftware.smack.util.dns.SmackDaneVerifier;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;
import org.minidns.dnsname.DnsName;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XMPPTCPConnection extends AbstractXMPPConnection {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(XMPPTCPConnection.class.getName());
    private static final int QUEUE_SIZE = 500;
    private static BundleAndDeferCallback defaultBundleAndDeferCallback;
    private static boolean useSmDefault = true;
    private static boolean useSmResumptionDefault = true;
    /* access modifiers changed from: private */
    public BundleAndDeferCallback bundleAndDeferCallback;
    /* access modifiers changed from: private */
    public long clientHandledStanzasCount;
    /* access modifiers changed from: private */
    public final SynchronizationPoint<Exception> closingStreamReceived;
    /* access modifiers changed from: private */
    public final SynchronizationPoint<SmackException> compressSyncPoint;
    /* access modifiers changed from: private */
    public final XMPPTCPConnectionConfiguration config;
    private boolean disconnectedButResumeable;
    /* access modifiers changed from: private */
    public final SynchronizationPoint<Exception> initialOpenStreamSend;
    /* access modifiers changed from: private */
    public final SynchronizationPoint<XMPPException> maybeCompressFeaturesReceived;
    protected final PacketReader packetReader;
    protected final PacketWriter packetWriter;
    /* access modifiers changed from: private */
    public final Semaphore readerWriterSemaphore;
    private final Set<StanzaFilter> requestAckPredicates;
    private SSLSocket secureSocket;
    private long serverHandledStanzasCount;
    private int smClientMaxResumptionTime;
    /* access modifiers changed from: private */
    public final SynchronizationPoint<SmackException> smEnabledSyncPoint;
    /* access modifiers changed from: private */
    public final SynchronizationPoint<FailedNonzaException> smResumedSyncPoint;
    /* access modifiers changed from: private */
    public int smServerMaxResumptionTime;
    /* access modifiers changed from: private */
    public String smSessionId;
    /* access modifiers changed from: private */
    public boolean smWasEnabledAtLeastOnce;
    private Socket socket;
    /* access modifiers changed from: private */
    public final Collection<StanzaListener> stanzaAcknowledgedListeners;
    private final Collection<StanzaListener> stanzaDroppedListeners;
    /* access modifiers changed from: private */
    public final Map<String, StanzaListener> stanzaIdAcknowledgedListeners;
    /* access modifiers changed from: private */
    public BlockingQueue<Stanza> unacknowledgedStanzas;
    private boolean useSm;
    private boolean useSmResumption;

    protected class PacketReader {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        /* access modifiers changed from: private */
        public volatile boolean done;
        XmlPullParser parser;
        /* access modifiers changed from: private */
        public final String threadName;

        static {
            Class<XMPPTCPConnection> cls = XMPPTCPConnection.class;
        }

        protected PacketReader() {
            StringBuilder sb = new StringBuilder();
            sb.append("Smack Reader (");
            sb.append(XMPPTCPConnection.this.getConnectionCounter());
            sb.append(')');
            this.threadName = sb.toString();
        }

        /* access modifiers changed from: 0000 */
        public void init() {
            this.done = false;
            Async.go(new Runnable() {
                public void run() {
                    String str = " exit";
                    Logger access$900 = XMPPTCPConnection.LOGGER;
                    StringBuilder sb = new StringBuilder();
                    sb.append(PacketReader.this.threadName);
                    sb.append(" start");
                    access$900.finer(sb.toString());
                    try {
                        PacketReader.this.parsePackets();
                    } finally {
                        Logger access$9002 = XMPPTCPConnection.LOGGER;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(PacketReader.this.threadName);
                        sb2.append(str);
                        access$9002.finer(sb2.toString());
                        XMPPTCPConnection.this.readerWriterSemaphore.release();
                    }
                }
            }, this.threadName);
        }

        /* access modifiers changed from: 0000 */
        public void shutdown() {
            this.done = true;
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:130:0x0355 A[Catch:{ all -> 0x040a, Exception -> 0x0391, Exception -> 0x0442 }] */
        /* JADX WARNING: Removed duplicated region for block: B:170:0x037d A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void parsePackets() {
            /*
                r10 = this;
                java.lang.String r0 = ""
                org.jivesoftware.smack.tcp.XMPPTCPConnection r1 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r1 = r1.initialOpenStreamSend     // Catch:{ Exception -> 0x0442 }
                r1.checkIfSuccessOrWait()     // Catch:{ Exception -> 0x0442 }
                org.xmlpull.v1.XmlPullParser r1 = r10.parser     // Catch:{ Exception -> 0x0442 }
                int r1 = r1.getEventType()     // Catch:{ Exception -> 0x0442 }
            L_0x0011:
                boolean r2 = r10.done     // Catch:{ Exception -> 0x0442 }
                if (r2 != 0) goto L_0x0441
                r2 = 1
                if (r1 == r2) goto L_0x0439
                java.lang.String r3 = "stream"
                r4 = 3
                r5 = 2
                if (r1 == r5) goto L_0x00a4
                if (r1 == r4) goto L_0x0022
                goto L_0x0430
            L_0x0022:
                org.xmlpull.v1.XmlPullParser r2 = r10.parser     // Catch:{ Exception -> 0x0442 }
                java.lang.String r2 = r2.getName()     // Catch:{ Exception -> 0x0442 }
                boolean r3 = r3.equals(r2)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x0430
                org.xmlpull.v1.XmlPullParser r3 = r10.parser     // Catch:{ Exception -> 0x0442 }
                java.lang.String r3 = r3.getNamespace()     // Catch:{ Exception -> 0x0442 }
                java.lang.String r4 = "http://etherx.jabber.org/streams"
                boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x0442 }
                if (r3 != 0) goto L_0x0061
                java.util.logging.Logger r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.LOGGER     // Catch:{ Exception -> 0x0442 }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0442 }
                r4.<init>()     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r5 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                r4.append(r5)     // Catch:{ Exception -> 0x0442 }
                java.lang.String r5 = " </stream> but different namespace "
                r4.append(r5)     // Catch:{ Exception -> 0x0442 }
                org.xmlpull.v1.XmlPullParser r5 = r10.parser     // Catch:{ Exception -> 0x0442 }
                java.lang.String r5 = r5.getNamespace()     // Catch:{ Exception -> 0x0442 }
                r4.append(r5)     // Catch:{ Exception -> 0x0442 }
                java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0442 }
                r3.warning(r4)     // Catch:{ Exception -> 0x0442 }
                goto L_0x0430
            L_0x0061:
                org.jivesoftware.smack.tcp.XMPPTCPConnection r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection$PacketWriter r3 = r3.packetWriter     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.util.ArrayBlockingQueueWithShutdown r3 = r3.queue     // Catch:{ Exception -> 0x0442 }
                boolean r3 = r3.isShutdown()     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r4 = r4.closingStreamReceived     // Catch:{ Exception -> 0x0442 }
                r4.reportSuccess()     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x0079
                return
            L_0x0079:
                java.util.logging.Logger r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.LOGGER     // Catch:{ Exception -> 0x0442 }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0442 }
                r5.<init>()     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r6 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                r5.append(r6)     // Catch:{ Exception -> 0x0442 }
                java.lang.String r6 = " received closing </stream> element. Server wants to terminate the connection, calling disconnect()"
                r5.append(r6)     // Catch:{ Exception -> 0x0442 }
                java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0442 }
                r4.info(r5)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.AsyncButOrdered r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.ASYNC_BUT_ORDERED     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r5 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection$PacketReader$2 r6 = new org.jivesoftware.smack.tcp.XMPPTCPConnection$PacketReader$2     // Catch:{ Exception -> 0x0442 }
                r6.<init>()     // Catch:{ Exception -> 0x0442 }
                r4.performAsyncButOrdered(r5, r6)     // Catch:{ Exception -> 0x0442 }
                goto L_0x0430
            L_0x00a4:
                org.xmlpull.v1.XmlPullParser r6 = r10.parser     // Catch:{ Exception -> 0x0442 }
                java.lang.String r6 = r6.getName()     // Catch:{ Exception -> 0x0442 }
                int r7 = r6.hashCode()     // Catch:{ Exception -> 0x0442 }
                r8 = 0
                r9 = -1
                switch(r7) {
                    case -1867169789: goto L_0x0157;
                    case -1609594047: goto L_0x014c;
                    case -1281977283: goto L_0x0141;
                    case -1276666629: goto L_0x0137;
                    case -1086574198: goto L_0x012d;
                    case -891990144: goto L_0x0125;
                    case -369449087: goto L_0x011a;
                    case -309519186: goto L_0x0110;
                    case -290659267: goto L_0x0106;
                    case 97: goto L_0x00fa;
                    case 114: goto L_0x00ee;
                    case 3368: goto L_0x00e3;
                    case 96784904: goto L_0x00d8;
                    case 954925063: goto L_0x00cd;
                    case 1097547223: goto L_0x00c1;
                    case 1402633315: goto L_0x00b5;
                    default: goto L_0x00b3;
                }     // Catch:{ Exception -> 0x0442 }
            L_0x00b3:
                goto L_0x0162
            L_0x00b5:
                java.lang.String r3 = "challenge"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = 8
                goto L_0x0163
            L_0x00c1:
                java.lang.String r3 = "resumed"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = 13
                goto L_0x0163
            L_0x00cd:
                java.lang.String r3 = "message"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = r8
                goto L_0x0163
            L_0x00d8:
                java.lang.String r3 = "error"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = 4
                goto L_0x0163
            L_0x00e3:
                java.lang.String r3 = "iq"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = r2
                goto L_0x0163
            L_0x00ee:
                java.lang.String r3 = "r"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = 15
                goto L_0x0163
            L_0x00fa:
                java.lang.String r3 = "a"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = 14
                goto L_0x0163
            L_0x0106:
                java.lang.String r3 = "features"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = 5
                goto L_0x0163
            L_0x0110:
                java.lang.String r3 = "proceed"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = 6
                goto L_0x0163
            L_0x011a:
                java.lang.String r3 = "compressed"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = 10
                goto L_0x0163
            L_0x0125:
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = r4
                goto L_0x0163
            L_0x012d:
                java.lang.String r3 = "failure"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = 7
                goto L_0x0163
            L_0x0137:
                java.lang.String r3 = "presence"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = r5
                goto L_0x0163
            L_0x0141:
                java.lang.String r3 = "failed"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = 12
                goto L_0x0163
            L_0x014c:
                java.lang.String r3 = "enabled"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = 11
                goto L_0x0163
            L_0x0157:
                java.lang.String r3 = "success"
                boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x00b3
                r3 = 9
                goto L_0x0163
            L_0x0162:
                r3 = r9
            L_0x0163:
                r4 = 0
                switch(r3) {
                    case 0: goto L_0x03f2;
                    case 1: goto L_0x03f2;
                    case 2: goto L_0x03f2;
                    case 3: goto L_0x03cd;
                    case 4: goto L_0x03aa;
                    case 5: goto L_0x03a1;
                    case 6: goto L_0x0385;
                    case 7: goto L_0x031b;
                    case 8: goto L_0x030a;
                    case 9: goto L_0x02ef;
                    case 10: goto L_0x02da;
                    case 11: goto L_0x0279;
                    case 12: goto L_0x0226;
                    case 13: goto L_0x01a1;
                    case 14: goto L_0x0190;
                    case 15: goto L_0x016d;
                    default: goto L_0x0167;
                }     // Catch:{ Exception -> 0x0442 }
            L_0x0167:
                java.util.logging.Logger r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.LOGGER     // Catch:{ Exception -> 0x0442 }
                goto L_0x041b
            L_0x016d:
                org.xmlpull.v1.XmlPullParser r2 = r10.parser     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.sm.provider.ParseStreamManagement.ackRequest(r2)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r2 = r2.smEnabledSyncPoint     // Catch:{ Exception -> 0x0442 }
                boolean r2 = r2.wasSuccessful()     // Catch:{ Exception -> 0x0442 }
                if (r2 == 0) goto L_0x0185
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                r2.sendSmAcknowledgementInternal()     // Catch:{ Exception -> 0x0442 }
                goto L_0x042f
            L_0x0185:
                java.util.logging.Logger r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.LOGGER     // Catch:{ Exception -> 0x0442 }
                java.lang.String r3 = "SM Ack Request received while SM is not enabled"
                r2.warning(r3)     // Catch:{ Exception -> 0x0442 }
                goto L_0x042f
            L_0x0190:
                org.xmlpull.v1.XmlPullParser r2 = r10.parser     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.sm.packet.StreamManagement$AckAnswer r2 = org.jivesoftware.smack.sm.provider.ParseStreamManagement.ackAnswer(r2)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                long r4 = r2.getHandledCount()     // Catch:{ Exception -> 0x0442 }
                r3.processHandledCount(r4)     // Catch:{ Exception -> 0x0442 }
                goto L_0x042f
            L_0x01a1:
                org.xmlpull.v1.XmlPullParser r2 = r10.parser     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.sm.packet.StreamManagement$Resumed r2 = org.jivesoftware.smack.sm.provider.ParseStreamManagement.resumed(r2)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                java.lang.String r3 = r3.smSessionId     // Catch:{ Exception -> 0x0442 }
                java.lang.String r4 = r2.getPrevId()     // Catch:{ Exception -> 0x0442 }
                boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x0442 }
                if (r3 == 0) goto L_0x0216
                org.jivesoftware.smack.tcp.XMPPTCPConnection r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r3 = r3.smEnabledSyncPoint     // Catch:{ Exception -> 0x0442 }
                r3.reportSuccess()     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                long r4 = r2.getHandledCount()     // Catch:{ Exception -> 0x0442 }
                r3.processHandledCount(r4)     // Catch:{ Exception -> 0x0442 }
                java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                java.util.concurrent.BlockingQueue r4 = r4.unacknowledgedStanzas     // Catch:{ Exception -> 0x0442 }
                int r4 = r4.size()     // Catch:{ Exception -> 0x0442 }
                r3.<init>(r4)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                java.util.concurrent.BlockingQueue r4 = r4.unacknowledgedStanzas     // Catch:{ Exception -> 0x0442 }
                r4.drainTo(r3)     // Catch:{ Exception -> 0x0442 }
                java.util.Iterator r4 = r3.iterator()     // Catch:{ Exception -> 0x0442 }
            L_0x01e5:
                boolean r5 = r4.hasNext()     // Catch:{ Exception -> 0x0442 }
                if (r5 == 0) goto L_0x01f7
                java.lang.Object r5 = r4.next()     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.packet.Stanza r5 = (org.jivesoftware.smack.packet.Stanza) r5     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r7 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                r7.sendStanzaInternal(r5)     // Catch:{ Exception -> 0x0442 }
                goto L_0x01e5
            L_0x01f7:
                boolean r4 = r3.isEmpty()     // Catch:{ Exception -> 0x0442 }
                if (r4 != 0) goto L_0x0202
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                r4.requestSmAcknowledgementInternal()     // Catch:{ Exception -> 0x0442 }
            L_0x0202:
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r4 = r4.smResumedSyncPoint     // Catch:{ Exception -> 0x0442 }
                r4.reportSuccess()     // Catch:{ Exception -> 0x0442 }
                java.util.logging.Logger r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.LOGGER     // Catch:{ Exception -> 0x0442 }
                java.lang.String r5 = "Stream Management (XEP-198): Stream resumed"
                r4.fine(r5)     // Catch:{ Exception -> 0x0442 }
                goto L_0x042f
            L_0x0216:
                org.jivesoftware.smack.sm.StreamManagementException$StreamIdDoesNotMatchException r0 = new org.jivesoftware.smack.sm.StreamManagementException$StreamIdDoesNotMatchException     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                java.lang.String r3 = r3.smSessionId     // Catch:{ Exception -> 0x0442 }
                java.lang.String r4 = r2.getPrevId()     // Catch:{ Exception -> 0x0442 }
                r0.<init>(r3, r4)     // Catch:{ Exception -> 0x0442 }
                throw r0     // Catch:{ Exception -> 0x0442 }
            L_0x0226:
                org.xmlpull.v1.XmlPullParser r2 = r10.parser     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.sm.packet.StreamManagement$Failed r2 = org.jivesoftware.smack.sm.provider.ParseStreamManagement.failed(r2)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.XMPPException$FailedNonzaException r3 = new org.jivesoftware.smack.XMPPException$FailedNonzaException     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.packet.StanzaError$Condition r4 = r2.getStanzaErrorCondition()     // Catch:{ Exception -> 0x0442 }
                r3.<init>(r2, r4)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r4 = r4.smResumedSyncPoint     // Catch:{ Exception -> 0x0442 }
                boolean r4 = r4.requestSent()     // Catch:{ Exception -> 0x0442 }
                if (r4 == 0) goto L_0x024c
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r4 = r4.smResumedSyncPoint     // Catch:{ Exception -> 0x0442 }
                r4.reportFailure(r3)     // Catch:{ Exception -> 0x0442 }
                goto L_0x042f
            L_0x024c:
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r4 = r4.smEnabledSyncPoint     // Catch:{ Exception -> 0x0442 }
                boolean r4 = r4.requestSent()     // Catch:{ Exception -> 0x0442 }
                if (r4 == 0) goto L_0x0271
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r4 = r4.smEnabledSyncPoint     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SmackException r5 = new org.jivesoftware.smack.SmackException     // Catch:{ Exception -> 0x0442 }
                r5.<init>(r3)     // Catch:{ Exception -> 0x0442 }
                r4.reportFailure(r5)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r4 = r4.lastFeaturesReceived     // Catch:{ Exception -> 0x0442 }
                r4.reportSuccess()     // Catch:{ Exception -> 0x0442 }
                goto L_0x042f
            L_0x0271:
                java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ Exception -> 0x0442 }
                java.lang.String r4 = "Failed element received but SM was not previously enabled"
                r0.<init>(r4)     // Catch:{ Exception -> 0x0442 }
                throw r0     // Catch:{ Exception -> 0x0442 }
            L_0x0279:
                org.xmlpull.v1.XmlPullParser r3 = r10.parser     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.sm.packet.StreamManagement$Enabled r3 = org.jivesoftware.smack.sm.provider.ParseStreamManagement.enabled(r3)     // Catch:{ Exception -> 0x0442 }
                boolean r5 = r3.isResumeSet()     // Catch:{ Exception -> 0x0442 }
                if (r5 == 0) goto L_0x02b5
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                java.lang.String r5 = r3.getId()     // Catch:{ Exception -> 0x0442 }
                r4.smSessionId = r5     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                java.lang.String r4 = r4.smSessionId     // Catch:{ Exception -> 0x0442 }
                boolean r4 = org.jivesoftware.smack.util.StringUtils.isNullOrEmpty(r4)     // Catch:{ Exception -> 0x0442 }
                if (r4 != 0) goto L_0x02a4
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                int r5 = r3.getMaxResumptionTime()     // Catch:{ Exception -> 0x0442 }
                r4.smServerMaxResumptionTime = r5     // Catch:{ Exception -> 0x0442 }
                goto L_0x02ba
            L_0x02a4:
                org.jivesoftware.smack.SmackException r0 = new org.jivesoftware.smack.SmackException     // Catch:{ Exception -> 0x0442 }
                java.lang.String r2 = "Stream Management 'enabled' element with resume attribute but without session id received"
                r0.<init>(r2)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r2 = r2.smEnabledSyncPoint     // Catch:{ Exception -> 0x0442 }
                r2.reportFailure(r0)     // Catch:{ Exception -> 0x0442 }
                throw r0     // Catch:{ Exception -> 0x0442 }
            L_0x02b5:
                org.jivesoftware.smack.tcp.XMPPTCPConnection r5 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                r5.smSessionId = r4     // Catch:{ Exception -> 0x0442 }
            L_0x02ba:
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                r7 = 0
                r4.clientHandledStanzasCount = r7     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                r4.smWasEnabledAtLeastOnce = r2     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r2 = r2.smEnabledSyncPoint     // Catch:{ Exception -> 0x0442 }
                r2.reportSuccess()     // Catch:{ Exception -> 0x0442 }
                java.util.logging.Logger r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.LOGGER     // Catch:{ Exception -> 0x0442 }
                java.lang.String r4 = "Stream Management (XEP-198): successfully enabled"
                r2.fine(r4)     // Catch:{ Exception -> 0x0442 }
                goto L_0x042f
            L_0x02da:
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                r2.initReaderAndWriter()     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                r2.openStream()     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r2 = r2.compressSyncPoint     // Catch:{ Exception -> 0x0442 }
                r2.reportSuccess()     // Catch:{ Exception -> 0x0442 }
                goto L_0x042f
            L_0x02ef:
                org.jivesoftware.smack.sasl.packet.SaslStreamElements$Success r2 = new org.jivesoftware.smack.sasl.packet.SaslStreamElements$Success     // Catch:{ Exception -> 0x0442 }
                org.xmlpull.v1.XmlPullParser r3 = r10.parser     // Catch:{ Exception -> 0x0442 }
                java.lang.String r3 = r3.nextText()     // Catch:{ Exception -> 0x0442 }
                r2.<init>(r3)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                r3.openStream()     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SASLAuthentication r3 = r3.getSASLAuthentication()     // Catch:{ Exception -> 0x0442 }
                r3.authenticated(r2)     // Catch:{ Exception -> 0x0442 }
                goto L_0x042f
            L_0x030a:
                org.xmlpull.v1.XmlPullParser r2 = r10.parser     // Catch:{ Exception -> 0x0442 }
                java.lang.String r2 = r2.nextText()     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SASLAuthentication r3 = r3.getSASLAuthentication()     // Catch:{ Exception -> 0x0442 }
                r3.challengeReceived(r2)     // Catch:{ Exception -> 0x0442 }
                goto L_0x042f
            L_0x031b:
                org.xmlpull.v1.XmlPullParser r3 = r10.parser     // Catch:{ Exception -> 0x0442 }
                java.lang.String r3 = r3.getNamespace(r4)     // Catch:{ Exception -> 0x0442 }
                int r4 = r3.hashCode()     // Catch:{ Exception -> 0x0442 }
                r7 = -1570142914(0xffffffffa269853e, float:-3.1647926E-18)
                if (r4 == r7) goto L_0x0348
                r7 = 919182852(0x36c99e04, float:6.0086622E-6)
                if (r4 == r7) goto L_0x033f
                r7 = 2117926358(0x7e3cfdd6, float:6.2803214E37)
                if (r4 == r7) goto L_0x0335
            L_0x0334:
                goto L_0x0352
            L_0x0335:
                java.lang.String r4 = "http://jabber.org/protocol/compress"
                boolean r4 = r3.equals(r4)     // Catch:{ Exception -> 0x0442 }
                if (r4 == 0) goto L_0x0334
                r8 = r2
                goto L_0x0353
            L_0x033f:
                java.lang.String r4 = "urn:ietf:params:xml:ns:xmpp-tls"
                boolean r4 = r3.equals(r4)     // Catch:{ Exception -> 0x0442 }
                if (r4 == 0) goto L_0x0334
                goto L_0x0353
            L_0x0348:
                java.lang.String r4 = "urn:ietf:params:xml:ns:xmpp-sasl"
                boolean r4 = r3.equals(r4)     // Catch:{ Exception -> 0x0442 }
                if (r4 == 0) goto L_0x0334
                r8 = r5
                goto L_0x0353
            L_0x0352:
                r8 = r9
            L_0x0353:
                if (r8 == 0) goto L_0x037d
                if (r8 == r2) goto L_0x036a
                if (r8 == r5) goto L_0x035a
                goto L_0x037b
            L_0x035a:
                org.xmlpull.v1.XmlPullParser r2 = r10.parser     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.sasl.packet.SaslStreamElements$SASLFailure r2 = org.jivesoftware.smack.util.PacketParserUtils.parseSASLFailure(r2)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r4 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SASLAuthentication r4 = r4.getSASLAuthentication()     // Catch:{ Exception -> 0x0442 }
                r4.authenticationFailed(r2)     // Catch:{ Exception -> 0x0442 }
                goto L_0x037b
            L_0x036a:
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r2 = r2.compressSyncPoint     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SmackException r4 = new org.jivesoftware.smack.SmackException     // Catch:{ Exception -> 0x0442 }
                java.lang.String r5 = "Could not establish compression"
                r4.<init>(r5)     // Catch:{ Exception -> 0x0442 }
                r2.reportFailure(r4)     // Catch:{ Exception -> 0x0442 }
            L_0x037b:
                goto L_0x042f
            L_0x037d:
                org.jivesoftware.smack.SmackException r0 = new org.jivesoftware.smack.SmackException     // Catch:{ Exception -> 0x0442 }
                java.lang.String r2 = "TLS negotiation has failed"
                r0.<init>(r2)     // Catch:{ Exception -> 0x0442 }
                throw r0     // Catch:{ Exception -> 0x0442 }
            L_0x0385:
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0391 }
                r2.proceedTLSReceived()     // Catch:{ Exception -> 0x0391 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0391 }
                r2.openStream()     // Catch:{ Exception -> 0x0391 }
                goto L_0x042f
            L_0x0391:
                r0 = move-exception
                org.jivesoftware.smack.SmackException r2 = new org.jivesoftware.smack.SmackException     // Catch:{ Exception -> 0x0442 }
                r2.<init>(r0)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r3 = r3.tlsHandled     // Catch:{ Exception -> 0x0442 }
                r3.reportFailure(r2)     // Catch:{ Exception -> 0x0442 }
                throw r0     // Catch:{ Exception -> 0x0442 }
            L_0x03a1:
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.xmlpull.v1.XmlPullParser r3 = r10.parser     // Catch:{ Exception -> 0x0442 }
                r2.parseFeatures(r3)     // Catch:{ Exception -> 0x0442 }
                goto L_0x042f
            L_0x03aa:
                org.xmlpull.v1.XmlPullParser r0 = r10.parser     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.packet.StreamError r0 = org.jivesoftware.smack.util.PacketParserUtils.parseStreamError(r0)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r2 = r2.saslFeatureReceived     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.XMPPException$StreamErrorException r3 = new org.jivesoftware.smack.XMPPException$StreamErrorException     // Catch:{ Exception -> 0x0442 }
                r3.<init>(r0)     // Catch:{ Exception -> 0x0442 }
                r2.reportFailure(r3)     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.SynchronizationPoint r2 = r2.tlsHandled     // Catch:{ Exception -> 0x0442 }
                r2.reportSuccess()     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.XMPPException$StreamErrorException r2 = new org.jivesoftware.smack.XMPPException$StreamErrorException     // Catch:{ Exception -> 0x0442 }
                r2.<init>(r0)     // Catch:{ Exception -> 0x0442 }
                throw r2     // Catch:{ Exception -> 0x0442 }
            L_0x03cd:
                java.lang.String r2 = "jabber:client"
                org.xmlpull.v1.XmlPullParser r3 = r10.parser     // Catch:{ Exception -> 0x0442 }
                java.lang.String r3 = r3.getNamespace(r4)     // Catch:{ Exception -> 0x0442 }
                boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x0442 }
                if (r2 == 0) goto L_0x042f
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.xmlpull.v1.XmlPullParser r3 = r10.parser     // Catch:{ Exception -> 0x0442 }
                java.lang.String r4 = "id"
                java.lang.String r3 = r3.getAttributeValue(r0, r4)     // Catch:{ Exception -> 0x0442 }
                r2.streamId = r3     // Catch:{ Exception -> 0x0442 }
                org.xmlpull.v1.XmlPullParser r2 = r10.parser     // Catch:{ Exception -> 0x0442 }
                java.lang.String r3 = "from"
                java.lang.String r2 = r2.getAttributeValue(r0, r3)     // Catch:{ Exception -> 0x0442 }
                goto L_0x042f
            L_0x03f2:
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ all -> 0x040a }
                org.xmlpull.v1.XmlPullParser r3 = r10.parser     // Catch:{ all -> 0x040a }
                r2.parseAndProcessStanza(r3)     // Catch:{ all -> 0x040a }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                long r3 = r3.clientHandledStanzasCount     // Catch:{ Exception -> 0x0442 }
                long r3 = org.jivesoftware.smack.sm.SMUtils.incrementHeight(r3)     // Catch:{ Exception -> 0x0442 }
                r2.clientHandledStanzasCount = r3     // Catch:{ Exception -> 0x0442 }
                goto L_0x042f
            L_0x040a:
                r0 = move-exception
                org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                org.jivesoftware.smack.tcp.XMPPTCPConnection r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ Exception -> 0x0442 }
                long r3 = r3.clientHandledStanzasCount     // Catch:{ Exception -> 0x0442 }
                long r3 = org.jivesoftware.smack.sm.SMUtils.incrementHeight(r3)     // Catch:{ Exception -> 0x0442 }
                r2.clientHandledStanzasCount = r3     // Catch:{ Exception -> 0x0442 }
                throw r0     // Catch:{ Exception -> 0x0442 }
            L_0x041b:
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0442 }
                r3.<init>()     // Catch:{ Exception -> 0x0442 }
                java.lang.String r4 = "Unknown top level stream element: "
                r3.append(r4)     // Catch:{ Exception -> 0x0442 }
                r3.append(r6)     // Catch:{ Exception -> 0x0442 }
                java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0442 }
                r2.warning(r3)     // Catch:{ Exception -> 0x0442 }
            L_0x042f:
            L_0x0430:
                org.xmlpull.v1.XmlPullParser r2 = r10.parser     // Catch:{ Exception -> 0x0442 }
                int r2 = r2.next()     // Catch:{ Exception -> 0x0442 }
                r1 = r2
                goto L_0x0011
            L_0x0439:
                org.jivesoftware.smack.SmackException r0 = new org.jivesoftware.smack.SmackException     // Catch:{ Exception -> 0x0442 }
                java.lang.String r2 = "Parser got END_DOCUMENT event. This could happen e.g. if the server closed the connection without sending a closing stream element"
                r0.<init>(r2)     // Catch:{ Exception -> 0x0442 }
                throw r0     // Catch:{ Exception -> 0x0442 }
            L_0x0441:
                goto L_0x0463
            L_0x0442:
                r0 = move-exception
                org.jivesoftware.smack.tcp.XMPPTCPConnection r1 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this
                org.jivesoftware.smack.SynchronizationPoint r1 = r1.closingStreamReceived
                r1.reportFailure(r0)
                boolean r1 = r10.done
                if (r1 != 0) goto L_0x0463
                org.jivesoftware.smack.tcp.XMPPTCPConnection r1 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this
                org.jivesoftware.smack.tcp.XMPPTCPConnection$PacketWriter r1 = r1.packetWriter
                org.jivesoftware.smack.util.ArrayBlockingQueueWithShutdown r1 = r1.queue
                boolean r1 = r1.isShutdown()
                if (r1 != 0) goto L_0x0463
                org.jivesoftware.smack.tcp.XMPPTCPConnection r1 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this
                r1.notifyConnectionError(r0)
            L_0x0463:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.tcp.XMPPTCPConnection.PacketReader.parsePackets():void");
        }
    }

    protected class PacketWriter {
        public static final int QUEUE_SIZE = 500;
        private volatile boolean instantShutdown;
        /* access modifiers changed from: private */
        public final ArrayBlockingQueueWithShutdown<Element> queue = new ArrayBlockingQueueWithShutdown<>(500, true);
        private boolean shouldBundleAndDefer;
        protected SynchronizationPoint<NoResponseException> shutdownDone = new SynchronizationPoint<>(XMPPTCPConnection.this, "shutdown completed");
        protected volatile Long shutdownTimestamp = null;
        /* access modifiers changed from: private */
        public final String threadName;

        protected PacketWriter() {
            StringBuilder sb = new StringBuilder();
            sb.append("Smack Writer (");
            sb.append(XMPPTCPConnection.this.getConnectionCounter());
            sb.append(')');
            this.threadName = sb.toString();
        }

        /* access modifiers changed from: 0000 */
        public void init() {
            this.shutdownDone.init();
            this.shutdownTimestamp = null;
            if (XMPPTCPConnection.this.unacknowledgedStanzas != null) {
                drainWriterQueueToUnacknowledgedStanzas();
            }
            this.queue.start();
            Async.go(new Runnable() {
                public void run() {
                    String str = " exit";
                    Logger access$900 = XMPPTCPConnection.LOGGER;
                    StringBuilder sb = new StringBuilder();
                    sb.append(PacketWriter.this.threadName);
                    sb.append(" start");
                    access$900.finer(sb.toString());
                    try {
                        PacketWriter.this.writePackets();
                    } finally {
                        Logger access$9002 = XMPPTCPConnection.LOGGER;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(PacketWriter.this.threadName);
                        sb2.append(str);
                        access$9002.finer(sb2.toString());
                        XMPPTCPConnection.this.readerWriterSemaphore.release();
                    }
                }
            }, this.threadName);
        }

        /* access modifiers changed from: private */
        public boolean done() {
            return this.shutdownTimestamp != null;
        }

        /* access modifiers changed from: protected */
        public void throwNotConnectedExceptionIfDoneAndResumptionNotPossible() throws NotConnectedException {
            boolean done = done();
            if (done) {
                boolean smResumptionPossible = XMPPTCPConnection.this.isSmResumptionPossible();
                if (!smResumptionPossible) {
                    XMPPTCPConnection xMPPTCPConnection = XMPPTCPConnection.this;
                    StringBuilder sb = new StringBuilder();
                    sb.append("done=");
                    sb.append(done);
                    sb.append(" smResumptionPossible=");
                    sb.append(smResumptionPossible);
                    throw new NotConnectedException((XMPPConnection) xMPPTCPConnection, sb.toString());
                }
            }
        }

        /* access modifiers changed from: protected */
        public void sendStreamElement(Element element) throws NotConnectedException, InterruptedException {
            throwNotConnectedExceptionIfDoneAndResumptionNotPossible();
            try {
                this.queue.put(element);
            } catch (InterruptedException e) {
                throwNotConnectedExceptionIfDoneAndResumptionNotPossible();
                throw e;
            }
        }

        /* access modifiers changed from: 0000 */
        public void shutdown(boolean instant) {
            this.instantShutdown = instant;
            this.queue.shutdown();
            this.shutdownTimestamp = Long.valueOf(System.currentTimeMillis());
            if (this.shutdownDone.isNotInInitialState()) {
                try {
                    this.shutdownDone.checkIfSuccessOrWait();
                } catch (InterruptedException | NoResponseException e) {
                    XMPPTCPConnection.LOGGER.log(Level.WARNING, "shutdownDone was not marked as successful by the writer thread", e);
                }
            }
        }

        private Element nextStreamElement() {
            if (this.queue.isEmpty()) {
                this.shouldBundleAndDefer = true;
            }
            try {
                return (Element) this.queue.take();
            } catch (InterruptedException e) {
                if (this.queue.isShutdown()) {
                    return null;
                }
                XMPPTCPConnection.LOGGER.log(Level.WARNING, "Writer thread was interrupted. Don't do that. Use disconnect() instead.", e);
                return null;
            }
        }

        /* access modifiers changed from: private */
        public void writePackets() {
            Exception writerException = null;
            try {
                XMPPTCPConnection.this.openStream();
                XMPPTCPConnection.this.initialOpenStreamSend.reportSuccess();
                while (!done()) {
                    Element element = nextStreamElement();
                    if (element != null) {
                        BundleAndDeferCallback localBundleAndDeferCallback = XMPPTCPConnection.this.bundleAndDeferCallback;
                        if (localBundleAndDeferCallback != null && XMPPTCPConnection.this.isAuthenticated() && this.shouldBundleAndDefer) {
                            this.shouldBundleAndDefer = false;
                            AtomicBoolean bundlingAndDeferringStopped = new AtomicBoolean();
                            int bundleAndDeferMillis = localBundleAndDeferCallback.getBundleAndDeferMillis(new BundleAndDefer(bundlingAndDeferringStopped));
                            if (bundleAndDeferMillis > 0) {
                                long remainingWait = (long) bundleAndDeferMillis;
                                long waitStart = System.currentTimeMillis();
                                synchronized (bundlingAndDeferringStopped) {
                                    while (!bundlingAndDeferringStopped.get() && remainingWait > 0) {
                                        bundlingAndDeferringStopped.wait(remainingWait);
                                        remainingWait = ((long) bundleAndDeferMillis) - (System.currentTimeMillis() - waitStart);
                                    }
                                }
                            }
                        }
                        Stanza packet = null;
                        if (element instanceof Stanza) {
                            packet = (Stanza) element;
                        } else if (element instanceof Enable) {
                            XMPPTCPConnection.this.unacknowledgedStanzas = new ArrayBlockingQueue(500);
                        }
                        maybeAddToUnacknowledgedStanzas(packet);
                        CharSequence elementXml = element.toXML("jabber:client");
                        if (elementXml instanceof XmlStringBuilder) {
                            ((XmlStringBuilder) elementXml).write(XMPPTCPConnection.this.writer, "jabber:client");
                        } else {
                            XMPPTCPConnection.this.writer.write(elementXml.toString());
                        }
                        if (this.queue.isEmpty()) {
                            XMPPTCPConnection.this.writer.flush();
                        }
                        if (packet != null) {
                            XMPPTCPConnection.this.firePacketSendingListeners(packet);
                        }
                    }
                }
                if (!this.instantShutdown) {
                    while (!this.queue.isEmpty()) {
                        try {
                            Element packet2 = (Element) this.queue.remove();
                            if (packet2 instanceof Stanza) {
                                maybeAddToUnacknowledgedStanzas((Stanza) packet2);
                            }
                            XMPPTCPConnection.this.writer.write(packet2.toXML(null).toString());
                        } catch (Exception e) {
                            XMPPTCPConnection.LOGGER.log(Level.WARNING, "Exception flushing queue during shutdown, ignore and continue", e);
                        }
                    }
                    XMPPTCPConnection.this.writer.flush();
                    try {
                        XMPPTCPConnection.this.writer.write("</stream:stream>");
                        XMPPTCPConnection.this.writer.flush();
                    } catch (Exception e2) {
                        XMPPTCPConnection.LOGGER.log(Level.WARNING, "Exception writing closing stream element", e2);
                    }
                    this.queue.clear();
                } else if (this.instantShutdown && XMPPTCPConnection.this.isSmEnabled()) {
                    drainWriterQueueToUnacknowledgedStanzas();
                }
            } catch (Exception e3) {
                try {
                    if (done() || this.queue.isShutdown()) {
                        XMPPTCPConnection.LOGGER.log(Level.FINE, "Ignoring Exception in writePackets()", e3);
                    } else {
                        writerException = e3;
                    }
                } catch (Throwable th) {
                    XMPPTCPConnection.LOGGER.fine("Reporting shutdownDone success in writer thread");
                    this.shutdownDone.reportSuccess();
                    throw th;
                }
            }
            XMPPTCPConnection.LOGGER.fine("Reporting shutdownDone success in writer thread");
            this.shutdownDone.reportSuccess();
            if (writerException != null) {
                XMPPTCPConnection.this.notifyConnectionError(writerException);
            }
        }

        private void drainWriterQueueToUnacknowledgedStanzas() {
            List<Element> elements = new ArrayList<>(this.queue.size());
            this.queue.drainTo(elements);
            for (int i = 0; i < elements.size(); i++) {
                Element element = (Element) elements.get(i);
                if (XMPPTCPConnection.this.unacknowledgedStanzas.remainingCapacity() == 0) {
                    XMPPTCPConnection.LOGGER.log(Level.WARNING, "Some stanzas may be lost as not all could be drained to the unacknowledged stanzas queue", UnacknowledgedQueueFullException.newWith(i, elements, XMPPTCPConnection.this.unacknowledgedStanzas));
                    return;
                }
                if (element instanceof Stanza) {
                    XMPPTCPConnection.this.unacknowledgedStanzas.add((Stanza) element);
                }
            }
        }

        private void maybeAddToUnacknowledgedStanzas(Stanza stanza) throws IOException {
            if (XMPPTCPConnection.this.unacknowledgedStanzas != null && stanza != null) {
                if (((double) XMPPTCPConnection.this.unacknowledgedStanzas.size()) == 400.0d) {
                    XMPPTCPConnection.this.writer.write(AckRequest.INSTANCE.toXML(null).toString());
                    XMPPTCPConnection.this.writer.flush();
                }
                try {
                    XMPPTCPConnection.this.unacknowledgedStanzas.put(stanza);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    public XMPPTCPConnection(XMPPTCPConnectionConfiguration config2) {
        super(config2);
        this.disconnectedButResumeable = false;
        this.readerWriterSemaphore = new Semaphore(2);
        this.packetWriter = new PacketWriter();
        this.packetReader = new PacketReader();
        this.initialOpenStreamSend = new SynchronizationPoint<>(this, "initial open stream element send to server");
        this.maybeCompressFeaturesReceived = new SynchronizationPoint<>(this, "stream compression feature");
        this.compressSyncPoint = new SynchronizationPoint<>(this, "stream compression");
        this.closingStreamReceived = new SynchronizationPoint<>(this, "stream closing element received");
        this.bundleAndDeferCallback = defaultBundleAndDeferCallback;
        this.smResumedSyncPoint = new SynchronizationPoint<>(this, "stream resumed element");
        this.smEnabledSyncPoint = new SynchronizationPoint<>(this, "stream enabled element");
        this.smClientMaxResumptionTime = -1;
        this.smServerMaxResumptionTime = -1;
        this.useSm = useSmDefault;
        this.useSmResumption = useSmResumptionDefault;
        this.serverHandledStanzasCount = 0;
        this.clientHandledStanzasCount = 0;
        this.smWasEnabledAtLeastOnce = false;
        this.stanzaAcknowledgedListeners = new ConcurrentLinkedQueue();
        this.stanzaDroppedListeners = new ConcurrentLinkedQueue();
        this.stanzaIdAcknowledgedListeners = new ConcurrentHashMap();
        this.requestAckPredicates = new LinkedHashSet();
        this.config = config2;
        addConnectionListener(new AbstractConnectionListener() {
            public void connectionClosedOnError(Exception e) {
                if ((e instanceof StreamErrorException) || (e instanceof StreamManagementException)) {
                    XMPPTCPConnection.this.dropSmState();
                }
            }
        });
    }

    public XMPPTCPConnection(CharSequence jid, String password) throws XmppStringprepException {
        this(XmppStringUtils.parseLocalpart(jid.toString()), password, XmppStringUtils.parseDomain(jid.toString()));
    }

    public XMPPTCPConnection(CharSequence username, String password, String serviceName) throws XmppStringprepException {
        this(((Builder) ((Builder) XMPPTCPConnectionConfiguration.builder().setUsernameAndPassword(username, password)).setXmppDomain(JidCreate.domainBareFrom(serviceName))).build());
    }

    /* access modifiers changed from: protected */
    public void throwNotConnectedExceptionIfAppropriate() throws NotConnectedException {
        PacketWriter packetWriter2 = this.packetWriter;
        if (packetWriter2 != null) {
            packetWriter2.throwNotConnectedExceptionIfDoneAndResumptionNotPossible();
            return;
        }
        throw new NotConnectedException();
    }

    /* access modifiers changed from: protected */
    public void throwAlreadyConnectedExceptionIfAppropriate() throws AlreadyConnectedException {
        if (isConnected() && !this.disconnectedButResumeable) {
            throw new AlreadyConnectedException();
        }
    }

    /* access modifiers changed from: protected */
    public void throwAlreadyLoggedInExceptionIfAppropriate() throws AlreadyLoggedInException {
        if (isAuthenticated() && !this.disconnectedButResumeable) {
            throw new AlreadyLoggedInException();
        }
    }

    /* access modifiers changed from: protected */
    public void afterSuccessfulLogin(boolean resumed) throws NotConnectedException, InterruptedException {
        this.disconnectedButResumeable = false;
        super.afterSuccessfulLogin(resumed);
    }

    /* access modifiers changed from: protected */
    public synchronized void loginInternal(String username, String password, Resourcepart resource) throws XMPPException, SmackException, IOException, InterruptedException {
        this.saslAuthentication.authenticate(username, password, this.config.getAuthzid(), this.secureSocket != null ? this.secureSocket.getSession() : null);
        this.maybeCompressFeaturesReceived.checkIfSuccessOrWait();
        maybeEnableCompression();
        if (isSmResumptionPossible()) {
            this.smResumedSyncPoint.sendAndWaitForResponse(new Resume(this.clientHandledStanzasCount, this.smSessionId));
            if (this.smResumedSyncPoint.wasSuccessful()) {
                afterSuccessfulLogin(true);
                return;
            }
            LOGGER.fine("Stream resumption failed, continuing with normal stream establishment process");
        }
        List<Stanza> previouslyUnackedStanzas = new LinkedList<>();
        if (this.unacknowledgedStanzas != null) {
            this.unacknowledgedStanzas.drainTo(previouslyUnackedStanzas);
            dropSmState();
        }
        bindResourceAndEstablishSession(resource);
        if (isSmAvailable() && this.useSm) {
            this.serverHandledStanzasCount = 0;
            this.smEnabledSyncPoint.sendAndWaitForResponseOrThrow(new Enable(this.useSmResumption, this.smClientMaxResumptionTime));
            synchronized (this.requestAckPredicates) {
                if (this.requestAckPredicates.isEmpty()) {
                    this.requestAckPredicates.add(Predicate.forMessagesOrAfter5Stanzas());
                }
            }
        }
        if (!this.stanzaDroppedListeners.isEmpty()) {
            for (Stanza stanza : previouslyUnackedStanzas) {
                for (StanzaListener listener : this.stanzaDroppedListeners) {
                    try {
                        listener.processStanza(stanza);
                    } catch (InterruptedException | NotConnectedException | NotLoggedInException e) {
                        LOGGER.log(Level.FINER, "StanzaDroppedListener received exception", e);
                    }
                }
            }
        } else {
            for (Stanza stanza2 : previouslyUnackedStanzas) {
                sendStanzaInternal(stanza2);
            }
        }
        afterSuccessfulLogin(false);
    }

    public boolean isSecureConnection() {
        return this.secureSocket != null;
    }

    /* access modifiers changed from: protected */
    public void shutdown() {
        if (isSmEnabled()) {
            try {
                sendSmAcknowledgementInternal();
            } catch (InterruptedException | NotConnectedException e) {
                LOGGER.log(Level.FINE, "Can not send final SM ack as connection is not connected", e);
            }
        }
        shutdown(false);
    }

    public synchronized void instantShutdown() {
        shutdown(true);
    }

    private void shutdown(boolean instant) {
        LOGGER.finer("PacketWriter shutdown()");
        this.packetWriter.shutdown(instant);
        LOGGER.finer("PacketWriter has been shut down");
        if (!instant) {
            try {
                this.closingStreamReceived.checkIfSuccessOrWait();
            } catch (InterruptedException | NoResponseException e) {
                Logger logger = LOGGER;
                Level level = Level.INFO;
                StringBuilder sb = new StringBuilder();
                sb.append("Exception while waiting for closing stream element from the server ");
                sb.append(this);
                logger.log(level, sb.toString(), e);
            }
        }
        LOGGER.finer("PacketReader shutdown()");
        this.packetReader.shutdown();
        LOGGER.finer("PacketReader has been shut down");
        Socket socket2 = this.socket;
        if (socket2 != null && socket2.isConnected()) {
            try {
                socket2.close();
            } catch (Exception e2) {
                LOGGER.log(Level.WARNING, "shutdown", e2);
            }
        }
        setWasAuthenticated();
        this.readerWriterSemaphore.acquireUninterruptibly(2);
        this.readerWriterSemaphore.release(2);
        if (!this.disconnectedButResumeable) {
            if (!isSmResumptionPossible() || !instant) {
                this.disconnectedButResumeable = false;
                this.smSessionId = null;
            } else {
                this.disconnectedButResumeable = true;
            }
            this.authenticated = false;
            this.connected = false;
            this.secureSocket = null;
            this.reader = null;
            this.writer = null;
            initState();
        }
    }

    /* access modifiers changed from: protected */
    public void initState() {
        super.initState();
        this.maybeCompressFeaturesReceived.init();
        this.compressSyncPoint.init();
        this.smResumedSyncPoint.init();
        this.smEnabledSyncPoint.init();
        this.initialOpenStreamSend.init();
    }

    public void sendNonza(Nonza element) throws NotConnectedException, InterruptedException {
        this.packetWriter.sendStreamElement(element);
    }

    /* access modifiers changed from: protected */
    public void sendStanzaInternal(Stanza packet) throws NotConnectedException, InterruptedException {
        this.packetWriter.sendStreamElement(packet);
        if (isSmEnabled()) {
            for (StanzaFilter requestAckPredicate : this.requestAckPredicates) {
                if (requestAckPredicate.accept(packet)) {
                    requestSmAcknowledgementInternal();
                    return;
                }
            }
        }
    }

    private void connectUsingConfiguration() throws ConnectionException, IOException, InterruptedException {
        SocketFactory socketFactory;
        Iterator it;
        List<HostAddress> failedAddresses = populateHostAddresses();
        SocketFactory socketFactory2 = this.config.getSocketFactory();
        ProxyInfo proxyInfo = this.config.getProxyInfo();
        int timeout = this.config.getConnectTimeout();
        if (socketFactory2 == null) {
            socketFactory = SocketFactory.getDefault();
        } else {
            socketFactory = socketFactory2;
        }
        Iterator it2 = this.hostAddresses.iterator();
        while (it2.hasNext()) {
            HostAddress hostAddress = (HostAddress) it2.next();
            String host = hostAddress.getHost();
            int port = hostAddress.getPort();
            String str = "Established TCP connection to ";
            if (proxyInfo == null) {
                Iterator<InetAddress> inetAddresses = hostAddress.getInetAddresses().iterator();
                while (true) {
                    if (!inetAddresses.hasNext()) {
                        it = it2;
                        break;
                    }
                    SocketFuture socketFuture = new SocketFuture(socketFactory);
                    InetAddress inetAddress = (InetAddress) inetAddresses.next();
                    InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);
                    Logger logger = LOGGER;
                    StringBuilder sb = new StringBuilder();
                    it = it2;
                    sb.append("Trying to establish TCP connection to ");
                    sb.append(inetSocketAddress);
                    logger.finer(sb.toString());
                    socketFuture.connectAsync(inetSocketAddress, timeout);
                    try {
                        this.socket = (Socket) socketFuture.getOrThrow();
                        Logger logger2 = LOGGER;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(str);
                        sb2.append(inetSocketAddress);
                        logger2.finer(sb2.toString());
                        this.host = host;
                        this.port = port;
                        return;
                    } catch (IOException e) {
                        hostAddress.setException(inetAddress, e);
                        if (!inetAddresses.hasNext()) {
                            break;
                        }
                        it2 = it;
                    }
                }
                failedAddresses.add(hostAddress);
                it2 = it;
            } else {
                Iterator it3 = it2;
                this.socket = socketFactory.createSocket();
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Host of HostAddress ");
                sb3.append(hostAddress);
                sb3.append(" must not be null when using a Proxy");
                StringUtils.requireNotNullOrEmpty(host, sb3.toString());
                StringBuilder sb4 = new StringBuilder();
                sb4.append(host);
                sb4.append(" at port ");
                sb4.append(port);
                String hostAndPort = sb4.toString();
                Logger logger3 = LOGGER;
                StringBuilder sb5 = new StringBuilder();
                sb5.append("Trying to establish TCP connection via Proxy to ");
                sb5.append(hostAndPort);
                logger3.finer(sb5.toString());
                try {
                    proxyInfo.getProxySocketConnection().connect(this.socket, host, port, timeout);
                    Logger logger4 = LOGGER;
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append(str);
                    sb6.append(hostAndPort);
                    logger4.finer(sb6.toString());
                    this.host = host;
                    this.port = port;
                    return;
                } catch (IOException e2) {
                    hostAddress.setException(e2);
                    failedAddresses.add(hostAddress);
                    it2 = it3;
                }
            }
        }
        throw ConnectionException.from(failedAddresses);
    }

    private void initConnection() throws IOException, InterruptedException {
        this.compressionHandler = null;
        initReaderAndWriter();
        int availableReaderWriterSemaphorePermits = this.readerWriterSemaphore.availablePermits();
        if (availableReaderWriterSemaphorePermits < 2) {
            LOGGER.log(Level.FINE, "Not every reader/writer threads where terminated on connection re-initializtion of {0}. Available permits {1}", new Object[]{this, Integer.valueOf(availableReaderWriterSemaphorePermits)});
        }
        this.readerWriterSemaphore.acquire(2);
        this.packetWriter.init();
        this.packetReader.init();
    }

    /* access modifiers changed from: private */
    public void initReaderAndWriter() throws IOException {
        InputStream is = this.socket.getInputStream();
        OutputStream os = this.socket.getOutputStream();
        if (this.compressionHandler != null) {
            is = this.compressionHandler.getInputStream(is);
            os = this.compressionHandler.getOutputStream(os);
        }
        String str = StringUtils.UTF8;
        this.writer = new OutputStreamWriter(os, str);
        this.reader = new BufferedReader(new InputStreamReader(is, str));
        initDebugger();
    }

    /* access modifiers changed from: private */
    public void proceedTLSReceived() throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, NoSuchProviderException, UnrecoverableKeyException, KeyManagementException, SmackException {
        SmackDaneVerifier daneVerifier;
        String verifierHostname;
        if (this.config.getDnssecMode() == DnssecMode.needsDnssecAndDane) {
            SmackDaneProvider daneProvider = DNSUtil.getDaneProvider();
            if (daneProvider != null) {
                SmackDaneVerifier daneVerifier2 = daneProvider.newInstance();
                if (daneVerifier2 != null) {
                    daneVerifier = daneVerifier2;
                } else {
                    throw new IllegalStateException("DANE requested but DANE provider did not return a DANE verifier");
                }
            } else {
                throw new UnsupportedOperationException("DANE enabled but no SmackDaneProvider configured");
            }
        } else {
            daneVerifier = null;
        }
        SSLContext context = this.config.getCustomSSLContext();
        KeyStore ks = null;
        PasswordCallback pcb = null;
        if (context == null) {
            String keyStoreType = this.config.getKeystoreType();
            CallbackHandler callbackHandler = this.config.getCallbackHandler();
            String keystorePath = this.config.getKeystorePath();
            String str = "PKCS11";
            String str2 = "Exception";
            if (str.equals(keyStoreType)) {
                try {
                    Constructor<?> c = Class.forName("sun.security.pkcs11.SunPKCS11").getConstructor(new Class[]{InputStream.class});
                    StringBuilder sb = new StringBuilder();
                    sb.append("name = SmartCard\nlibrary = ");
                    sb.append(this.config.getPKCS11Library());
                    Provider p = (Provider) c.newInstance(new Object[]{new ByteArrayInputStream(sb.toString().getBytes(StringUtils.UTF8))});
                    Security.addProvider(p);
                    ks = KeyStore.getInstance(str, p);
                    pcb = new PasswordCallback("PKCS11 Password: ", false);
                    callbackHandler.handle(new Callback[]{pcb});
                    ks.load(null, pcb.getPassword());
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, str2, e);
                    ks = null;
                }
            } else {
                String str3 = "Apple";
                if (str3.equals(keyStoreType)) {
                    KeyStore ks2 = KeyStore.getInstance("KeychainStore", str3);
                    ks2.load(null, null);
                    ks = ks2;
                } else if (keyStoreType != null) {
                    ks = KeyStore.getInstance(keyStoreType);
                    if (callbackHandler == null || !StringUtils.isNotEmpty((CharSequence) keystorePath)) {
                        ks.load(null, null);
                    } else {
                        try {
                            pcb = new PasswordCallback("Keystore Password: ", false);
                            callbackHandler.handle(new Callback[]{pcb});
                            ks.load(new FileInputStream(keystorePath), pcb.getPassword());
                        } catch (Exception e2) {
                            LOGGER.log(Level.WARNING, str2, e2);
                            ks = null;
                        }
                    }
                }
            }
            KeyManager[] kms = null;
            if (ks != null) {
                String keyManagerFactoryAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
                KeyManagerFactory kmf = null;
                try {
                    kmf = KeyManagerFactory.getInstance(keyManagerFactoryAlgorithm);
                } catch (NoSuchAlgorithmException e3) {
                    NoSuchAlgorithmException e4 = e3;
                    Logger logger = LOGGER;
                    Level level = Level.FINE;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Could get the default KeyManagerFactory for the '");
                    sb2.append(keyManagerFactoryAlgorithm);
                    sb2.append("' algorithm");
                    logger.log(level, sb2.toString(), e4);
                }
                if (kmf != null) {
                    if (pcb == null) {
                        try {
                            kmf.init(ks, null);
                        } catch (NullPointerException npe) {
                            LOGGER.log(Level.WARNING, "NullPointerException", npe);
                        }
                    } else {
                        kmf.init(ks, pcb.getPassword());
                        pcb.clearPassword();
                    }
                    kms = kmf.getKeyManagers();
                }
            }
            context = SSLContext.getInstance(TLSUtils.TLS);
            SecureRandom secureRandom = new SecureRandom();
            X509TrustManager customTrustManager = this.config.getCustomX509TrustManager();
            if (daneVerifier != null) {
                daneVerifier.init(context, kms, customTrustManager, secureRandom);
            } else {
                TrustManager[] customTrustManagers = null;
                if (customTrustManager != null) {
                    customTrustManagers = new TrustManager[]{customTrustManager};
                }
                context.init(kms, customTrustManagers, secureRandom);
            }
        }
        Socket plain = this.socket;
        this.socket = context.getSocketFactory().createSocket(plain, this.config.getXMPPServiceDomain().toString(), plain.getPort(), true);
        SSLSocket sslSocket = (SSLSocket) this.socket;
        TLSUtils.setEnabledProtocolsAndCiphers(sslSocket, this.config.getEnabledSSLProtocols(), this.config.getEnabledSSLCiphers());
        initReaderAndWriter();
        sslSocket.startHandshake();
        if (daneVerifier != null) {
            daneVerifier.finish(sslSocket);
        }
        HostnameVerifier verifier = getConfiguration().getHostnameVerifier();
        if (verifier != null) {
            DnsName xmppServiceDomainDnsName = getConfiguration().getXmppServiceDomainAsDnsNameIfPossible();
            if (xmppServiceDomainDnsName != null) {
                verifierHostname = xmppServiceDomainDnsName.ace;
            } else {
                Logger logger2 = LOGGER;
                Level level2 = Level.WARNING;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("XMPP service domain name '");
                sb3.append(getXMPPServiceDomain());
                sb3.append("' can not be represented as DNS name. TLS X.509 certificate validiation may fail.");
                logger2.log(level2, sb3.toString());
                verifierHostname = getXMPPServiceDomain().toString();
            }
            if (verifier.verify(verifierHostname, sslSocket.getSession())) {
                this.secureSocket = sslSocket;
                return;
            }
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Hostname verification of certificate failed. Certificate does not authenticate ");
            sb4.append(getXMPPServiceDomain());
            throw new CertificateException(sb4.toString());
        }
        throw new IllegalStateException("No HostnameVerifier set. Use connectionConfiguration.setHostnameVerifier() to configure.");
    }

    private static XMPPInputOutputStream maybeGetCompressionHandler(Feature compression) {
        for (XMPPInputOutputStream handler : SmackConfiguration.getCompressionHandlers()) {
            if (compression.getMethods().contains(handler.getCompressionMethod())) {
                return handler;
            }
        }
        return null;
    }

    public boolean isUsingCompression() {
        return this.compressionHandler != null && this.compressSyncPoint.wasSuccessful();
    }

    private void maybeEnableCompression() throws SmackException, InterruptedException {
        if (this.config.isCompressionEnabled()) {
            Feature compression = (Feature) getFeature(Feature.ELEMENT, "http://jabber.org/protocol/compress");
            if (compression != null) {
                XMPPInputOutputStream maybeGetCompressionHandler = maybeGetCompressionHandler(compression);
                this.compressionHandler = maybeGetCompressionHandler;
                if (maybeGetCompressionHandler != null) {
                    this.compressSyncPoint.sendAndWaitForResponseOrThrow(new Compress(this.compressionHandler.getCompressionMethod()));
                } else {
                    LOGGER.warning("Could not enable compression because no matching handler/method pair was found");
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void connectInternal() throws SmackException, IOException, XMPPException, InterruptedException {
        this.closingStreamReceived.init();
        connectUsingConfiguration();
        initConnection();
        this.tlsHandled.checkIfSuccessOrWaitOrThrow();
        this.saslFeatureReceived.checkIfSuccessOrWaitOrThrow();
    }

    /* access modifiers changed from: private */
    public void notifyConnectionError(final Exception e) {
        ASYNC_BUT_ORDERED.performAsyncButOrdered(this, new Runnable() {
            static final /* synthetic */ boolean $assertionsDisabled = false;

            static {
                Class<XMPPTCPConnection> cls = XMPPTCPConnection.class;
            }

            /* JADX WARNING: CFG modification limit reached, blocks count: 118 */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r4 = this;
                    org.jivesoftware.smack.tcp.XMPPTCPConnection r0 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this
                    org.jivesoftware.smack.tcp.XMPPTCPConnection$PacketReader r0 = r0.packetReader
                    boolean r0 = r0.done
                    if (r0 != 0) goto L_0x006a
                    org.jivesoftware.smack.tcp.XMPPTCPConnection r0 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this
                    org.jivesoftware.smack.tcp.XMPPTCPConnection$PacketWriter r0 = r0.packetWriter
                    boolean r0 = r0.done()
                    if (r0 == 0) goto L_0x0015
                    goto L_0x006a
                L_0x0015:
                    org.jivesoftware.smack.SmackException$SmackWrappedException r0 = new org.jivesoftware.smack.SmackException$SmackWrappedException
                    java.lang.Exception r1 = r3
                    r0.<init>(r1)
                    org.jivesoftware.smack.tcp.XMPPTCPConnection r1 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this
                    org.jivesoftware.smack.SynchronizationPoint r1 = r1.tlsHandled
                    r1.reportGenericFailure(r0)
                    org.jivesoftware.smack.tcp.XMPPTCPConnection r1 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this
                    org.jivesoftware.smack.SynchronizationPoint r1 = r1.saslFeatureReceived
                    r1.reportGenericFailure(r0)
                    org.jivesoftware.smack.tcp.XMPPTCPConnection r1 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this
                    org.jivesoftware.smack.SynchronizationPoint r1 = r1.maybeCompressFeaturesReceived
                    r1.reportGenericFailure(r0)
                    org.jivesoftware.smack.tcp.XMPPTCPConnection r1 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this
                    org.jivesoftware.smack.SynchronizationPoint r1 = r1.lastFeaturesReceived
                    r1.reportGenericFailure(r0)
                    org.jivesoftware.smack.tcp.XMPPTCPConnection r1 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this
                    monitor-enter(r1)
                    org.jivesoftware.smack.tcp.XMPPTCPConnection r2 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this     // Catch:{ all -> 0x0068 }
                    r2.instantShutdown()     // Catch:{ all -> 0x0068 }
                    monitor-exit(r1)     // Catch:{ all -> 0x0068 }
                    org.jivesoftware.smack.tcp.XMPPTCPConnection$2$1 r1 = new org.jivesoftware.smack.tcp.XMPPTCPConnection$2$1
                    r1.<init>()
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder
                    r2.<init>()
                    org.jivesoftware.smack.tcp.XMPPTCPConnection r3 = org.jivesoftware.smack.tcp.XMPPTCPConnection.this
                    r2.append(r3)
                    java.lang.String r3 = " callConnectionClosedOnErrorListener()"
                    r2.append(r3)
                    java.lang.String r2 = r2.toString()
                    org.jivesoftware.smack.util.Async.go(r1, r2)
                    return
                L_0x0066:
                    monitor-exit(r1)     // Catch:{ all -> 0x0068 }
                    throw r2
                L_0x0068:
                    r2 = move-exception
                    goto L_0x0066
                L_0x006a:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.tcp.XMPPTCPConnection.AnonymousClass2.run():void");
            }
        });
    }

    /* access modifiers changed from: protected */
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    /* access modifiers changed from: protected */
    public void afterFeaturesReceived() throws NotConnectedException, InterruptedException, SecurityRequiredByServerException {
        StartTls startTlsFeature = (StartTls) getFeature(StartTls.ELEMENT, StartTls.NAMESPACE);
        if (startTlsFeature == null) {
            this.tlsHandled.reportSuccess();
        } else if (startTlsFeature.required() && this.config.getSecurityMode() == SecurityMode.disabled) {
            SecurityRequiredByServerException smackException = new SecurityRequiredByServerException();
            this.tlsHandled.reportFailure(smackException);
            throw smackException;
        } else if (this.config.getSecurityMode() != SecurityMode.disabled) {
            sendNonza(new StartTls());
        } else {
            this.tlsHandled.reportSuccess();
        }
        if (getSASLAuthentication().authenticationSuccessful()) {
            this.maybeCompressFeaturesReceived.reportSuccess();
        }
    }

    /* access modifiers changed from: 0000 */
    public void openStream() throws SmackException, InterruptedException {
        CharSequence to = getXMPPServiceDomain();
        CharSequence from = null;
        CharSequence localpart = this.config.getUsername();
        if (localpart != null) {
            from = XmppStringUtils.completeJidFrom(localpart, to);
        }
        sendNonza(new StreamOpen(to, from, getStreamId()));
        try {
            this.packetReader.parser = PacketParserUtils.newXmppParser(this.reader);
        } catch (XmlPullParserException e) {
            throw new SmackException((Throwable) e);
        }
    }

    public static void setUseStreamManagementDefault(boolean useSmDefault2) {
        useSmDefault = useSmDefault2;
    }

    @Deprecated
    public static void setUseStreamManagementResumptiodDefault(boolean useSmResumptionDefault2) {
        setUseStreamManagementResumptionDefault(useSmResumptionDefault2);
    }

    public static void setUseStreamManagementResumptionDefault(boolean useSmResumptionDefault2) {
        if (useSmResumptionDefault2) {
            setUseStreamManagementDefault(useSmResumptionDefault2);
        }
        useSmResumptionDefault = useSmResumptionDefault2;
    }

    public void setUseStreamManagement(boolean useSm2) {
        this.useSm = useSm2;
    }

    public void setUseStreamManagementResumption(boolean useSmResumption2) {
        if (useSmResumption2) {
            setUseStreamManagement(useSmResumption2);
        }
        this.useSmResumption = useSmResumption2;
    }

    public void setPreferredResumptionTime(int resumptionTime) {
        this.smClientMaxResumptionTime = resumptionTime;
    }

    public boolean addRequestAckPredicate(StanzaFilter predicate) {
        boolean add;
        synchronized (this.requestAckPredicates) {
            add = this.requestAckPredicates.add(predicate);
        }
        return add;
    }

    public boolean removeRequestAckPredicate(StanzaFilter predicate) {
        boolean remove;
        synchronized (this.requestAckPredicates) {
            remove = this.requestAckPredicates.remove(predicate);
        }
        return remove;
    }

    public void removeAllRequestAckPredicates() {
        synchronized (this.requestAckPredicates) {
            this.requestAckPredicates.clear();
        }
    }

    public void requestSmAcknowledgement() throws StreamManagementNotEnabledException, NotConnectedException, InterruptedException {
        if (isSmEnabled()) {
            requestSmAcknowledgementInternal();
            return;
        }
        throw new StreamManagementNotEnabledException();
    }

    /* access modifiers changed from: private */
    public void requestSmAcknowledgementInternal() throws NotConnectedException, InterruptedException {
        this.packetWriter.sendStreamElement(AckRequest.INSTANCE);
    }

    public void sendSmAcknowledgement() throws StreamManagementNotEnabledException, NotConnectedException, InterruptedException {
        if (isSmEnabled()) {
            sendSmAcknowledgementInternal();
            return;
        }
        throw new StreamManagementNotEnabledException();
    }

    /* access modifiers changed from: private */
    public void sendSmAcknowledgementInternal() throws NotConnectedException, InterruptedException {
        this.packetWriter.sendStreamElement(new AckAnswer(this.clientHandledStanzasCount));
    }

    public void addStanzaAcknowledgedListener(StanzaListener listener) {
        this.stanzaAcknowledgedListeners.add(listener);
    }

    public boolean removeStanzaAcknowledgedListener(StanzaListener listener) {
        return this.stanzaAcknowledgedListeners.remove(listener);
    }

    public void removeAllStanzaAcknowledgedListeners() {
        this.stanzaAcknowledgedListeners.clear();
    }

    public void addStanzaDroppedListener(StanzaListener listener) {
        this.stanzaDroppedListeners.add(listener);
    }

    public boolean removeStanzaDroppedListener(StanzaListener listener) {
        return this.stanzaDroppedListeners.remove(listener);
    }

    public StanzaListener addStanzaIdAcknowledgedListener(final String id, StanzaListener listener) throws StreamManagementNotEnabledException {
        if (this.smWasEnabledAtLeastOnce) {
            schedule(new Runnable() {
                public void run() {
                    XMPPTCPConnection.this.stanzaIdAcknowledgedListeners.remove(id);
                }
            }, (long) Math.min(getMaxSmResumptionTime(), 10800), TimeUnit.SECONDS);
            return (StanzaListener) this.stanzaIdAcknowledgedListeners.put(id, listener);
        }
        throw new StreamManagementNotEnabledException();
    }

    public StanzaListener removeStanzaIdAcknowledgedListener(String id) {
        return (StanzaListener) this.stanzaIdAcknowledgedListeners.remove(id);
    }

    public void removeAllStanzaIdAcknowledgedListeners() {
        this.stanzaIdAcknowledgedListeners.clear();
    }

    public boolean isSmAvailable() {
        return hasFeature(StreamManagementFeature.ELEMENT, StreamManagement.NAMESPACE);
    }

    public boolean isSmEnabled() {
        return this.smEnabledSyncPoint.wasSuccessful();
    }

    public boolean streamWasResumed() {
        return this.smResumedSyncPoint.wasSuccessful();
    }

    public boolean isDisconnectedButSmResumptionPossible() {
        return this.disconnectedButResumeable && isSmResumptionPossible();
    }

    public boolean isSmResumptionPossible() {
        if (this.smSessionId == null) {
            return false;
        }
        Long shutdownTimestamp = this.packetWriter.shutdownTimestamp;
        if (shutdownTimestamp == null) {
            return true;
        }
        if (System.currentTimeMillis() > shutdownTimestamp.longValue() + (((long) getMaxSmResumptionTime()) * 1000)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void dropSmState() {
        this.smSessionId = null;
        this.unacknowledgedStanzas = null;
    }

    public int getMaxSmResumptionTime() {
        int clientResumptionTime = this.smClientMaxResumptionTime;
        int serverResumptionTime = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        if (clientResumptionTime <= 0) {
            clientResumptionTime = Integer.MAX_VALUE;
        }
        int i = this.smServerMaxResumptionTime;
        if (i > 0) {
            serverResumptionTime = i;
        }
        return Math.min(clientResumptionTime, serverResumptionTime);
    }

    /* access modifiers changed from: private */
    public void processHandledCount(long handledCount) throws StreamManagementCounterError {
        int i;
        long j = handledCount;
        long ackedStanzasCount = SMUtils.calculateDelta(j, this.serverHandledStanzasCount);
        if (ackedStanzasCount <= 2147483647L) {
            i = (int) ackedStanzasCount;
        } else {
            i = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
        final ArrayList arrayList = new ArrayList(i);
        long i2 = 0;
        while (i2 < ackedStanzasCount) {
            Stanza ackedStanza = (Stanza) this.unacknowledgedStanzas.poll();
            if (ackedStanza != null) {
                arrayList.add(ackedStanza);
                i2++;
            } else {
                Stanza stanza = ackedStanza;
                StreamManagementCounterError streamManagementCounterError = new StreamManagementCounterError(handledCount, this.serverHandledStanzasCount, ackedStanzasCount, arrayList);
                throw streamManagementCounterError;
            }
        }
        boolean atLeastOneStanzaAcknowledgedListener = false;
        if (this.stanzaAcknowledgedListeners.isEmpty()) {
            Iterator it = arrayList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                String id = ((Stanza) it.next()).getStanzaId();
                if (id != null && this.stanzaIdAcknowledgedListeners.containsKey(id)) {
                    atLeastOneStanzaAcknowledgedListener = true;
                    break;
                }
            }
        } else {
            atLeastOneStanzaAcknowledgedListener = true;
        }
        if (atLeastOneStanzaAcknowledgedListener) {
            asyncGo(new Runnable() {
                public void run() {
                    String str;
                    for (Stanza ackedStanza : arrayList) {
                        Iterator it = XMPPTCPConnection.this.stanzaAcknowledgedListeners.iterator();
                        while (true) {
                            str = "Received exception";
                            if (!it.hasNext()) {
                                break;
                            }
                            try {
                                ((StanzaListener) it.next()).processStanza(ackedStanza);
                            } catch (InterruptedException | NotConnectedException | NotLoggedInException e) {
                                XMPPTCPConnection.LOGGER.log(Level.FINER, str, e);
                            }
                        }
                        String id = ackedStanza.getStanzaId();
                        if (!StringUtils.isNullOrEmpty((CharSequence) id)) {
                            StanzaListener listener = (StanzaListener) XMPPTCPConnection.this.stanzaIdAcknowledgedListeners.remove(id);
                            if (listener != null) {
                                try {
                                    listener.processStanza(ackedStanza);
                                } catch (InterruptedException | NotConnectedException | NotLoggedInException e2) {
                                    XMPPTCPConnection.LOGGER.log(Level.FINER, str, e2);
                                }
                            }
                        }
                    }
                }
            });
        }
        this.serverHandledStanzasCount = j;
    }

    public static void setDefaultBundleAndDeferCallback(BundleAndDeferCallback defaultBundleAndDeferCallback2) {
        defaultBundleAndDeferCallback = defaultBundleAndDeferCallback2;
    }

    public void setBundleandDeferCallback(BundleAndDeferCallback bundleAndDeferCallback2) {
        this.bundleAndDeferCallback = bundleAndDeferCallback2;
    }
}
