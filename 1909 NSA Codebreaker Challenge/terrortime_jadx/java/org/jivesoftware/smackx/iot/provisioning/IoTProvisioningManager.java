package org.jivesoftware.smackx.iot.provisioning;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.AbstractPresenceEventListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.SubscribeListener;
import org.jivesoftware.smack.roster.SubscribeListener.SubscribeAnswer;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.iot.IoTManager;
import org.jivesoftware.smackx.iot.discovery.IoTDiscoveryManager;
import org.jivesoftware.smackx.iot.provisioning.element.ClearCache;
import org.jivesoftware.smackx.iot.provisioning.element.ClearCacheResponse;
import org.jivesoftware.smackx.iot.provisioning.element.Friend;
import org.jivesoftware.smackx.iot.provisioning.element.IoTIsFriend;
import org.jivesoftware.smackx.iot.provisioning.element.IoTIsFriendResponse;
import org.jivesoftware.smackx.iot.provisioning.element.Unfriend;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.util.cache.LruCache;

public final class IoTProvisioningManager extends Manager {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final StanzaFilter FRIEND_MESSAGE;
    private static final Map<XMPPConnection, IoTProvisioningManager> INSTANCES = new WeakHashMap();
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(IoTProvisioningManager.class.getName());
    private static final StanzaFilter UNFRIEND_MESSAGE;
    /* access modifiers changed from: private */
    public final Set<BecameFriendListener> becameFriendListeners = new CopyOnWriteArraySet();
    private Jid configuredProvisioningServer;
    /* access modifiers changed from: private */
    public final LruCache<BareJid, Void> friendshipDeniedCache = new LruCache<>(16);
    /* access modifiers changed from: private */
    public final LruCache<BareJid, Void> friendshipRequestedCache = new LruCache<>(16);
    /* access modifiers changed from: private */
    public final LruCache<Jid, LruCache<BareJid, Void>> negativeFriendshipRequestCache = new LruCache<>(8);
    private final Roster roster;
    /* access modifiers changed from: private */
    public final Set<WasUnfriendedListener> wasUnfriendedListeners = new CopyOnWriteArraySet();

    static {
        String str = "urn:xmpp:iot:provisioning";
        FRIEND_MESSAGE = new AndFilter(StanzaTypeFilter.MESSAGE, new StanzaExtensionFilter(Friend.ELEMENT, str));
        UNFRIEND_MESSAGE = new AndFilter(StanzaTypeFilter.MESSAGE, new StanzaExtensionFilter(Unfriend.ELEMENT, str));
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                if (IoTManager.isAutoEnableActive()) {
                    IoTProvisioningManager.getInstanceFor(connection);
                }
            }
        });
    }

    public static synchronized IoTProvisioningManager getInstanceFor(XMPPConnection connection) {
        IoTProvisioningManager manager;
        synchronized (IoTProvisioningManager.class) {
            manager = (IoTProvisioningManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new IoTProvisioningManager(connection);
                INSTANCES.put(connection, manager);
            }
        }
        return manager;
    }

    private IoTProvisioningManager(XMPPConnection connection) {
        super(connection);
        connection.addAsyncStanzaListener(new StanzaListener() {
            public void processStanza(Stanza stanza) throws NotConnectedException, InterruptedException {
                if (IoTProvisioningManager.this.isFromProvisioningService(stanza, true)) {
                    BareJid unfriendJid = Unfriend.from((Message) stanza).getJid();
                    XMPPConnection connection = IoTProvisioningManager.this.connection();
                    if (!Roster.getInstanceFor(connection).isSubscribedToMyPresence(unfriendJid)) {
                        Logger access$200 = IoTProvisioningManager.LOGGER;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Ignoring <unfriend/> request '");
                        sb.append(stanza);
                        sb.append("' because ");
                        sb.append(unfriendJid);
                        sb.append(" is already not subscribed to our presence.");
                        access$200.warning(sb.toString());
                        return;
                    }
                    Presence unsubscribed = new Presence(Type.unsubscribed);
                    unsubscribed.setTo((Jid) unfriendJid);
                    connection.sendStanza(unsubscribed);
                }
            }
        }, UNFRIEND_MESSAGE);
        connection.addAsyncStanzaListener(new StanzaListener() {
            public void processStanza(Stanza stanza) throws NotConnectedException, InterruptedException {
                Message friendMessage = (Message) stanza;
                BareJid friendJid = Friend.from(friendMessage).getFriend();
                if (IoTProvisioningManager.this.isFromProvisioningService(friendMessage, false)) {
                    XMPPConnection connection = IoTProvisioningManager.this.connection();
                    connection.sendStanza(new Message((Jid) friendJid, (ExtensionElement) new Friend(connection.getUser().asBareJid())));
                } else {
                    BareJid bareFrom = friendMessage.getFrom().asBareJid();
                    String str = "Ignoring friendship recommendation ";
                    if (!IoTProvisioningManager.this.friendshipDeniedCache.containsKey(bareFrom)) {
                        Logger access$200 = IoTProvisioningManager.LOGGER;
                        Level level = Level.WARNING;
                        StringBuilder sb = new StringBuilder();
                        sb.append(str);
                        sb.append(friendMessage);
                        sb.append(" because friendship to this JID was not previously denied.");
                        access$200.log(level, sb.toString());
                    } else if (!bareFrom.equals((CharSequence) friendJid)) {
                        Logger access$2002 = IoTProvisioningManager.LOGGER;
                        Level level2 = Level.WARNING;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(str);
                        sb2.append(friendMessage);
                        sb2.append(" because it does not recommend itself, but ");
                        sb2.append(friendJid);
                        sb2.append('.');
                        access$2002.log(level2, sb2.toString());
                    } else {
                        IoTProvisioningManager.this.sendFriendshipRequest(friendJid);
                    }
                }
            }
        }, FRIEND_MESSAGE);
        AnonymousClass4 r2 = new AbstractIqRequestHandler(ClearCache.ELEMENT, "urn:xmpp:iot:provisioning", IQ.Type.set, Mode.async) {
            public IQ handleIQRequest(IQ iqRequest) {
                if (!IoTProvisioningManager.this.isFromProvisioningService(iqRequest, true)) {
                    return null;
                }
                ClearCache clearCache = (ClearCache) iqRequest;
                LruCache<BareJid, Void> cache = (LruCache) IoTProvisioningManager.this.negativeFriendshipRequestCache.lookup(iqRequest.getFrom());
                if (cache != null) {
                    cache.clear();
                }
                return new ClearCacheResponse(clearCache);
            }
        };
        connection.registerIQRequestHandler(r2);
        this.roster = Roster.getInstanceFor(connection);
        this.roster.addSubscribeListener(new SubscribeListener() {
            public SubscribeAnswer processSubscribe(Jid from, Presence subscribeRequest) {
                String str = "Could not determine if ";
                try {
                    if (IoTDiscoveryManager.getInstanceFor(IoTProvisioningManager.this.connection()).isRegistry(from.asBareJid())) {
                        return SubscribeAnswer.Approve;
                    }
                } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e) {
                    Logger access$200 = IoTProvisioningManager.LOGGER;
                    Level level = Level.WARNING;
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(from);
                    sb.append(" is a registry");
                    access$200.log(level, sb.toString(), e);
                }
                Jid provisioningServer = null;
                try {
                    provisioningServer = IoTProvisioningManager.this.getConfiguredProvisioningServer();
                } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e2) {
                    Logger access$2002 = IoTProvisioningManager.LOGGER;
                    Level level2 = Level.WARNING;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Could not determine provisioning server. Ignoring friend request from ");
                    sb2.append(from);
                    access$2002.log(level2, sb2.toString(), e2);
                }
                if (provisioningServer == null) {
                    return null;
                }
                try {
                    if (IoTProvisioningManager.this.isFriend(provisioningServer, from.asBareJid())) {
                        return SubscribeAnswer.Approve;
                    }
                    return SubscribeAnswer.Deny;
                } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e3) {
                    Logger access$2003 = IoTProvisioningManager.LOGGER;
                    Level level3 = Level.WARNING;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(str);
                    sb3.append(from);
                    sb3.append(" is a friend.");
                    access$2003.log(level3, sb3.toString(), e3);
                    return null;
                }
            }
        });
        this.roster.addPresenceEventListener(new AbstractPresenceEventListener() {
            public void presenceSubscribed(BareJid address, Presence subscribedPresence) {
                IoTProvisioningManager.this.friendshipRequestedCache.remove(address);
                for (BecameFriendListener becameFriendListener : IoTProvisioningManager.this.becameFriendListeners) {
                    becameFriendListener.becameFriend(address, subscribedPresence);
                }
            }

            public void presenceUnsubscribed(BareJid address, Presence unsubscribedPresence) {
                if (IoTProvisioningManager.this.friendshipRequestedCache.containsKey(address)) {
                    IoTProvisioningManager.this.friendshipDeniedCache.put(address, null);
                }
                for (WasUnfriendedListener wasUnfriendedListener : IoTProvisioningManager.this.wasUnfriendedListeners) {
                    wasUnfriendedListener.wasUnfriendedListener(address, unsubscribedPresence);
                }
            }
        });
    }

    public void setConfiguredProvisioningServer(Jid provisioningServer) {
        this.configuredProvisioningServer = provisioningServer;
    }

    public Jid getConfiguredProvisioningServer() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (this.configuredProvisioningServer == null) {
            this.configuredProvisioningServer = findProvisioningServerComponent();
        }
        return this.configuredProvisioningServer;
    }

    public DomainBareJid findProvisioningServerComponent() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        List<DiscoverInfo> discoverInfos = ServiceDiscoveryManager.getInstanceFor(connection()).findServicesDiscoverInfo("urn:xmpp:iot:provisioning", true, true);
        if (discoverInfos.isEmpty()) {
            return null;
        }
        return ((DiscoverInfo) discoverInfos.get(0)).getFrom().asDomainBareJid();
    }

    public boolean isFriend(Jid provisioningServer, BareJid friendInQuestion) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        LruCache lruCache = (LruCache) this.negativeFriendshipRequestCache.lookup(provisioningServer);
        if (lruCache != null && lruCache.containsKey(friendInQuestion)) {
            return false;
        }
        IoTIsFriend iotIsFriend = new IoTIsFriend(friendInQuestion);
        iotIsFriend.setTo(provisioningServer);
        boolean isFriend = ((IoTIsFriendResponse) connection().createStanzaCollectorAndSend(iotIsFriend).nextResultOrThrow()).getIsFriendResult();
        if (!isFriend) {
            if (lruCache == null) {
                lruCache = new LruCache(1024);
                this.negativeFriendshipRequestCache.put(provisioningServer, lruCache);
            }
            lruCache.put(friendInQuestion, null);
        }
        return isFriend;
    }

    public boolean iAmFriendOf(BareJid otherJid) {
        return this.roster.iAmSubscribedTo(otherJid);
    }

    public void sendFriendshipRequest(BareJid bareJid) throws NotConnectedException, InterruptedException {
        Presence presence = new Presence(Type.subscribe);
        presence.setTo((Jid) bareJid);
        this.friendshipRequestedCache.put(bareJid, null);
        connection().sendStanza(presence);
    }

    public void sendFriendshipRequestIfRequired(BareJid jid) throws NotConnectedException, InterruptedException {
        if (!iAmFriendOf(jid)) {
            sendFriendshipRequest(jid);
        }
    }

    public boolean isMyFriend(Jid friendInQuestion) {
        return this.roster.isSubscribedToMyPresence(friendInQuestion);
    }

    public void unfriend(Jid friend) throws NotConnectedException, InterruptedException {
        if (isMyFriend(friend)) {
            Presence presence = new Presence(Type.unsubscribed);
            presence.setTo(friend);
            connection().sendStanza(presence);
        }
    }

    public boolean addBecameFriendListener(BecameFriendListener becameFriendListener) {
        return this.becameFriendListeners.add(becameFriendListener);
    }

    public boolean removeBecameFriendListener(BecameFriendListener becameFriendListener) {
        return this.becameFriendListeners.remove(becameFriendListener);
    }

    public boolean addWasUnfriendedListener(WasUnfriendedListener wasUnfriendedListener) {
        return this.wasUnfriendedListeners.add(wasUnfriendedListener);
    }

    public boolean removeWasUnfriendedListener(WasUnfriendedListener wasUnfriendedListener) {
        return this.wasUnfriendedListeners.remove(wasUnfriendedListener);
    }

    /* access modifiers changed from: private */
    public boolean isFromProvisioningService(Stanza stanza, boolean log) {
        try {
            Jid provisioningServer = getConfiguredProvisioningServer();
            if (provisioningServer == null) {
                if (log) {
                    Logger logger = LOGGER;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Ignoring request '");
                    sb.append(stanza);
                    sb.append("' because no provisioning server configured.");
                    logger.warning(sb.toString());
                }
                return false;
            } else if (provisioningServer.equals((CharSequence) stanza.getFrom())) {
                return true;
            } else {
                if (log) {
                    Logger logger2 = LOGGER;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Ignoring  request '");
                    sb2.append(stanza);
                    sb2.append("' because not from provisioning server '");
                    sb2.append(provisioningServer);
                    sb2.append("'.");
                    logger2.warning(sb2.toString());
                }
                return false;
            }
        } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e) {
            LOGGER.log(Level.WARNING, "Could determine provisioning server", e);
            return false;
        }
    }
}
