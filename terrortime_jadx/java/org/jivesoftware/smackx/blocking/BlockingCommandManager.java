package org.jivesoftware.smackx.blocking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.blocking.element.BlockContactsIQ;
import org.jivesoftware.smackx.blocking.element.BlockListIQ;
import org.jivesoftware.smackx.blocking.element.UnblockContactsIQ;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jxmpp.jid.Jid;

public final class BlockingCommandManager extends Manager {
    private static final Map<XMPPConnection, BlockingCommandManager> INSTANCES = new WeakHashMap();
    public static final String NAMESPACE = "urn:xmpp:blocking";
    /* access modifiers changed from: private */
    public final Set<AllJidsUnblockedListener> allJidsUnblockedListeners = new CopyOnWriteArraySet();
    /* access modifiers changed from: private */
    public volatile List<Jid> blockListCached;
    /* access modifiers changed from: private */
    public final Set<JidsBlockedListener> jidsBlockedListeners = new CopyOnWriteArraySet();
    /* access modifiers changed from: private */
    public final Set<JidsUnblockedListener> jidsUnblockedListeners = new CopyOnWriteArraySet();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                BlockingCommandManager.getInstanceFor(connection);
            }
        });
    }

    public static synchronized BlockingCommandManager getInstanceFor(XMPPConnection connection) {
        BlockingCommandManager blockingCommandManager;
        synchronized (BlockingCommandManager.class) {
            blockingCommandManager = (BlockingCommandManager) INSTANCES.get(connection);
            if (blockingCommandManager == null) {
                blockingCommandManager = new BlockingCommandManager(connection);
                INSTANCES.put(connection, blockingCommandManager);
            }
        }
        return blockingCommandManager;
    }

    private BlockingCommandManager(XMPPConnection connection) {
        super(connection);
        AnonymousClass2 r1 = new AbstractIqRequestHandler(BlockContactsIQ.ELEMENT, "urn:xmpp:blocking", Type.set, Mode.sync) {
            public IQ handleIQRequest(IQ iqRequest) {
                BlockContactsIQ blockContactIQ = (BlockContactsIQ) iqRequest;
                if (BlockingCommandManager.this.blockListCached == null) {
                    BlockingCommandManager.this.blockListCached = new ArrayList();
                }
                List<Jid> blockedJids = blockContactIQ.getJids();
                BlockingCommandManager.this.blockListCached.addAll(blockedJids);
                for (JidsBlockedListener listener : BlockingCommandManager.this.jidsBlockedListeners) {
                    listener.onJidsBlocked(blockedJids);
                }
                return IQ.createResultIQ(blockContactIQ);
            }
        };
        connection.registerIQRequestHandler(r1);
        AnonymousClass3 r7 = new AbstractIqRequestHandler(UnblockContactsIQ.ELEMENT, "urn:xmpp:blocking", Type.set, Mode.sync) {
            public IQ handleIQRequest(IQ iqRequest) {
                UnblockContactsIQ unblockContactIQ = (UnblockContactsIQ) iqRequest;
                if (BlockingCommandManager.this.blockListCached == null) {
                    BlockingCommandManager.this.blockListCached = new ArrayList();
                }
                List<Jid> unblockedJids = unblockContactIQ.getJids();
                if (unblockedJids == null) {
                    BlockingCommandManager.this.blockListCached.clear();
                    for (AllJidsUnblockedListener listener : BlockingCommandManager.this.allJidsUnblockedListeners) {
                        listener.onAllJidsUnblocked();
                    }
                } else {
                    BlockingCommandManager.this.blockListCached.removeAll(unblockedJids);
                    for (JidsUnblockedListener listener2 : BlockingCommandManager.this.jidsUnblockedListeners) {
                        listener2.onJidsUnblocked(unblockedJids);
                    }
                }
                return IQ.createResultIQ(unblockContactIQ);
            }
        };
        connection.registerIQRequestHandler(r7);
        connection.addConnectionListener(new AbstractConnectionListener() {
            public void authenticated(XMPPConnection connection, boolean resumed) {
                if (!resumed) {
                    BlockingCommandManager.this.blockListCached = null;
                }
            }
        });
    }

    public boolean isSupportedByServer() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).serverSupportsFeature("urn:xmpp:blocking");
    }

    public List<Jid> getBlockList() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (this.blockListCached == null) {
            this.blockListCached = ((BlockListIQ) connection().createStanzaCollectorAndSend(new BlockListIQ()).nextResultOrThrow()).getBlockedJidsCopy();
        }
        return Collections.unmodifiableList(this.blockListCached);
    }

    public void blockContacts(List<Jid> jids) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        connection().createStanzaCollectorAndSend(new BlockContactsIQ(jids)).nextResultOrThrow();
    }

    public void unblockContacts(List<Jid> jids) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        connection().createStanzaCollectorAndSend(new UnblockContactsIQ(jids)).nextResultOrThrow();
    }

    public void unblockAll() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        connection().createStanzaCollectorAndSend(new UnblockContactsIQ()).nextResultOrThrow();
    }

    public void addJidsBlockedListener(JidsBlockedListener jidsBlockedListener) {
        this.jidsBlockedListeners.add(jidsBlockedListener);
    }

    public void removeJidsBlockedListener(JidsBlockedListener jidsBlockedListener) {
        this.jidsBlockedListeners.remove(jidsBlockedListener);
    }

    public void addJidsUnblockedListener(JidsUnblockedListener jidsUnblockedListener) {
        this.jidsUnblockedListeners.add(jidsUnblockedListener);
    }

    public void removeJidsUnblockedListener(JidsUnblockedListener jidsUnblockedListener) {
        this.jidsUnblockedListeners.remove(jidsUnblockedListener);
    }

    public void addAllJidsUnblockedListener(AllJidsUnblockedListener allJidsUnblockedListener) {
        this.allJidsUnblockedListeners.add(allJidsUnblockedListener);
    }

    public void removeAllJidsUnblockedListener(AllJidsUnblockedListener allJidsUnblockedListener) {
        this.allJidsUnblockedListeners.remove(allJidsUnblockedListener);
    }
}
