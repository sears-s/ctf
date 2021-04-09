package org.jivesoftware.smackx.bytestreams.ibb;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jivesoftware.smack.AbstractConnectionClosedListener;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smackx.bytestreams.BytestreamListener;
import org.jivesoftware.smackx.bytestreams.BytestreamManager;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Open;
import org.jxmpp.jid.Jid;

public final class InBandBytestreamManager extends Manager implements BytestreamManager {
    public static final int MAXIMUM_BLOCK_SIZE = 65535;
    private static final String SESSION_ID_PREFIX = "jibb_";
    private static final Map<XMPPConnection, InBandBytestreamManager> managers = new WeakHashMap();
    private static final Random randomGenerator = new Random();
    private final List<BytestreamListener> allRequestListeners = Collections.synchronizedList(new LinkedList());
    private final CloseListener closeListener;
    private final DataListener dataListener;
    private int defaultBlockSize = 4096;
    private final List<String> ignoredBytestreamRequests = Collections.synchronizedList(new LinkedList());
    private final InitiationListener initiationListener = new InitiationListener(this);
    private int maximumBlockSize = 65535;
    private final Map<String, InBandBytestreamSession> sessions = new ConcurrentHashMap();
    private StanzaType stanza = StanzaType.IQ;
    private final Map<Jid, BytestreamListener> userListeners = new ConcurrentHashMap();

    public enum StanzaType {
        IQ,
        MESSAGE
    }

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(final XMPPConnection connection) {
                InBandBytestreamManager.getByteStreamManager(connection);
                connection.addConnectionListener(new AbstractConnectionClosedListener() {
                    public void connectionTerminated() {
                        InBandBytestreamManager.getByteStreamManager(connection).disableService();
                    }

                    public void connected(XMPPConnection connection) {
                        InBandBytestreamManager.getByteStreamManager(connection);
                    }
                });
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001e, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager getByteStreamManager(org.jivesoftware.smack.XMPPConnection r3) {
        /*
            java.lang.Class<org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager> r0 = org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager.class
            monitor-enter(r0)
            if (r3 != 0) goto L_0x0008
            r1 = 0
            monitor-exit(r0)
            return r1
        L_0x0008:
            java.util.Map<org.jivesoftware.smack.XMPPConnection, org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager> r1 = managers     // Catch:{ all -> 0x001f }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ all -> 0x001f }
            org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager r1 = (org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager) r1     // Catch:{ all -> 0x001f }
            if (r1 != 0) goto L_0x001d
            org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager r2 = new org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager     // Catch:{ all -> 0x001f }
            r2.<init>(r3)     // Catch:{ all -> 0x001f }
            r1 = r2
            java.util.Map<org.jivesoftware.smack.XMPPConnection, org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager> r2 = managers     // Catch:{ all -> 0x001f }
            r2.put(r3, r1)     // Catch:{ all -> 0x001f }
        L_0x001d:
            monitor-exit(r0)
            return r1
        L_0x001f:
            r3 = move-exception
            monitor-exit(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager.getByteStreamManager(org.jivesoftware.smack.XMPPConnection):org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager");
    }

    private InBandBytestreamManager(XMPPConnection connection) {
        super(connection);
        connection.registerIQRequestHandler(this.initiationListener);
        this.dataListener = new DataListener(this);
        connection.registerIQRequestHandler(this.dataListener);
        this.closeListener = new CloseListener(this);
        connection.registerIQRequestHandler(this.closeListener);
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

    public int getDefaultBlockSize() {
        return this.defaultBlockSize;
    }

    public void setDefaultBlockSize(int defaultBlockSize2) {
        if (defaultBlockSize2 <= 0 || defaultBlockSize2 > 65535) {
            throw new IllegalArgumentException("Default block size must be between 1 and 65535");
        }
        this.defaultBlockSize = defaultBlockSize2;
    }

    public int getMaximumBlockSize() {
        return this.maximumBlockSize;
    }

    public void setMaximumBlockSize(int maximumBlockSize2) {
        if (maximumBlockSize2 <= 0 || maximumBlockSize2 > 65535) {
            throw new IllegalArgumentException("Maximum block size must be between 1 and 65535");
        }
        this.maximumBlockSize = maximumBlockSize2;
    }

    public StanzaType getStanza() {
        return this.stanza;
    }

    public void setStanza(StanzaType stanza2) {
        this.stanza = stanza2;
    }

    public InBandBytestreamSession establishSession(Jid targetJID) throws XMPPException, SmackException, InterruptedException {
        return establishSession(targetJID, getNextSessionID());
    }

    public InBandBytestreamSession establishSession(Jid targetJID, String sessionID) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Open byteStreamRequest = new Open(sessionID, this.defaultBlockSize, this.stanza);
        byteStreamRequest.setTo(targetJID);
        XMPPConnection connection = connection();
        connection.createStanzaCollectorAndSend(byteStreamRequest).nextResultOrThrow();
        InBandBytestreamSession inBandBytestreamSession = new InBandBytestreamSession(connection, byteStreamRequest, targetJID);
        this.sessions.put(sessionID, inBandBytestreamSession);
        return inBandBytestreamSession;
    }

    /* access modifiers changed from: protected */
    public void replyRejectPacket(IQ request) throws NotConnectedException, InterruptedException {
        connection().sendStanza(IQ.createErrorResponse(request, Condition.not_acceptable));
    }

    /* access modifiers changed from: protected */
    public void replyResourceConstraintPacket(IQ request) throws NotConnectedException, InterruptedException {
        connection().sendStanza(IQ.createErrorResponse(request, Condition.resource_constraint));
    }

    /* access modifiers changed from: protected */
    public void replyItemNotFoundPacket(IQ request) throws NotConnectedException, InterruptedException {
        connection().sendStanza(IQ.createErrorResponse(request, Condition.item_not_found));
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
    public Map<String, InBandBytestreamSession> getSessions() {
        return this.sessions;
    }

    /* access modifiers changed from: protected */
    public List<String> getIgnoredBytestreamRequests() {
        return this.ignoredBytestreamRequests;
    }

    /* access modifiers changed from: private */
    public void disableService() {
        XMPPConnection connection = connection();
        managers.remove(connection);
        connection.unregisterIQRequestHandler(this.initiationListener);
        connection.unregisterIQRequestHandler(this.dataListener);
        connection.unregisterIQRequestHandler(this.closeListener);
        this.initiationListener.shutdown();
        this.userListeners.clear();
        this.allRequestListeners.clear();
        this.sessions.clear();
        this.ignoredBytestreamRequests.clear();
    }
}
