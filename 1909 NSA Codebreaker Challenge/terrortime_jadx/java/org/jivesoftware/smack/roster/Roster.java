package org.jivesoftware.smack.roster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.AsyncButOrdered;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.FeatureNotSupportedException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PresenceTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.filter.ToMatchesFilter;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smack.roster.SubscribeListener.SubscribeAnswer;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.roster.packet.RosterPacket.Item;
import org.jivesoftware.smack.roster.packet.RosterPacket.ItemType;
import org.jivesoftware.smack.roster.packet.RosterVer;
import org.jivesoftware.smack.roster.packet.SubscriptionPreApproval;
import org.jivesoftware.smack.roster.rosterstore.RosterStore;
import org.jivesoftware.smack.util.ExceptionCallback;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.SuccessCallback;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.util.cache.LruCache;

public final class Roster extends Manager {
    public static final int INITIAL_DEFAULT_NON_ROSTER_PRESENCE_MAP_SIZE = 1024;
    private static final Map<XMPPConnection, Roster> INSTANCES = new WeakHashMap();
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(Roster.class.getName());
    private static final StanzaFilter OUTGOING_USER_UNAVAILABLE_PRESENCE = new AndFilter(PresenceTypeFilter.UNAVAILABLE, ToMatchesFilter.MATCH_NO_TO_SET);
    private static final StanzaFilter PRESENCE_PACKET_FILTER = StanzaTypeFilter.PRESENCE;
    private static int defaultNonRosterPresenceMapMaxSize = 1024;
    private static SubscriptionMode defaultSubscriptionMode = SubscriptionMode.reject_all;
    private static boolean rosterLoadedAtLoginDefault = true;
    /* access modifiers changed from: private */
    public final AsyncButOrdered<BareJid> asyncButOrdered = new AsyncButOrdered<>();
    /* access modifiers changed from: private */
    public final Map<BareJid, RosterEntry> entries = new ConcurrentHashMap();
    private final Map<String, RosterGroup> groups = new ConcurrentHashMap();
    private final LruCache<BareJid, Map<Resourcepart, Presence>> nonRosterPresenceMap = new LruCache<>(defaultNonRosterPresenceMapMaxSize);
    /* access modifiers changed from: private */
    public final Set<PresenceEventListener> presenceEventListeners = new CopyOnWriteArraySet();
    private final Map<BareJid, Map<Resourcepart, Presence>> presenceMap = new ConcurrentHashMap();
    private final PresencePacketListener presencePacketListener = new PresencePacketListener();
    private SubscriptionMode previousSubscriptionMode;
    private final Set<RosterListener> rosterListeners = new LinkedHashSet();
    private final Object rosterListenersAndEntriesLock = new Object();
    /* access modifiers changed from: private */
    public boolean rosterLoadedAtLogin = rosterLoadedAtLoginDefault;
    /* access modifiers changed from: private */
    public final Set<RosterLoadedListener> rosterLoadedListeners = new LinkedHashSet();
    /* access modifiers changed from: private */
    public RosterState rosterState = RosterState.uninitialized;
    /* access modifiers changed from: private */
    public RosterStore rosterStore;
    /* access modifiers changed from: private */
    public final Set<SubscribeListener> subscribeListeners = new CopyOnWriteArraySet();
    /* access modifiers changed from: private */
    public SubscriptionMode subscriptionMode = getDefaultSubscriptionMode();
    private final Set<RosterEntry> unfiledEntries = new CopyOnWriteArraySet();

    /* renamed from: org.jivesoftware.smack.roster.Roster$6 reason: invalid class name */
    static /* synthetic */ class AnonymousClass6 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$Presence$Type = new int[Type.values().length];
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$roster$Roster$SubscriptionMode = new int[SubscriptionMode.values().length];
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$roster$SubscribeListener$SubscribeAnswer = new int[SubscribeAnswer.values().length];
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$roster$packet$RosterPacket$ItemType = new int[ItemType.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$packet$Presence$Type[Type.available.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$Presence$Type[Type.unavailable.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$Presence$Type[Type.error.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$Presence$Type[Type.subscribed.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$Presence$Type[Type.unsubscribed.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$roster$packet$RosterPacket$ItemType[ItemType.none.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$roster$packet$RosterPacket$ItemType[ItemType.from.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$roster$packet$RosterPacket$ItemType[ItemType.to.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$roster$packet$RosterPacket$ItemType[ItemType.both.ordinal()] = 4;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$roster$SubscribeListener$SubscribeAnswer[SubscribeAnswer.ApproveAndAlsoRequestIfRequired.ordinal()] = 1;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$roster$SubscribeListener$SubscribeAnswer[SubscribeAnswer.Approve.ordinal()] = 2;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$roster$SubscribeListener$SubscribeAnswer[SubscribeAnswer.Deny.ordinal()] = 3;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$roster$Roster$SubscriptionMode[SubscriptionMode.manual.ordinal()] = 1;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$roster$Roster$SubscriptionMode[SubscriptionMode.accept_all.ordinal()] = 2;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$roster$Roster$SubscriptionMode[SubscriptionMode.reject_all.ordinal()] = 3;
            } catch (NoSuchFieldError e15) {
            }
        }
    }

    private class PresencePacketListener implements StanzaListener {
        private PresencePacketListener() {
        }

        public void processStanza(Stanza packet) throws NotConnectedException, InterruptedException {
            final BareJid key;
            if (Roster.this.rosterState == RosterState.loading) {
                try {
                    Roster.this.waitUntilLoaded();
                } catch (InterruptedException e) {
                    Roster.LOGGER.log(Level.INFO, "Presence listener was interrupted", e);
                }
            }
            if (!Roster.this.isLoaded() && Roster.this.rosterLoadedAtLogin) {
                Logger access$500 = Roster.LOGGER;
                StringBuilder sb = new StringBuilder();
                sb.append("Roster not loaded while processing ");
                sb.append(packet);
                access$500.warning(sb.toString());
            }
            final Presence presence = (Presence) packet;
            final Jid from = presence.getFrom();
            if (from != null) {
                key = from.asBareJid();
            } else {
                XMPPConnection connection = Roster.this.connection();
                if (connection == null) {
                    Logger access$5002 = Roster.LOGGER;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Connection was null while trying to handle exotic presence stanza: ");
                    sb2.append(presence);
                    access$5002.finest(sb2.toString());
                    return;
                }
                EntityFullJid myJid = connection.getUser();
                if (myJid == null) {
                    Logger access$5003 = Roster.LOGGER;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Connection had no local address in Roster's presence listener. Possibly we received a presence without from before being authenticated. Presence: ");
                    sb3.append(presence);
                    access$5003.info(sb3.toString());
                    return;
                }
                Logger access$5004 = Roster.LOGGER;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Exotic presence stanza without from received: ");
                sb4.append(presence);
                access$5004.info(sb4.toString());
                key = myJid.asBareJid();
            }
            Roster.this.asyncButOrdered.performAsyncButOrdered(key, new Runnable() {
                static final /* synthetic */ boolean $assertionsDisabled = false;

                static {
                    Class<Roster> cls = Roster.class;
                }

                public void run() {
                    Resourcepart fromResource = Resourcepart.EMPTY;
                    BareJid bareFrom = null;
                    FullJid fullFrom = null;
                    Jid jid = from;
                    if (jid != null) {
                        fromResource = jid.getResourceOrNull();
                        if (fromResource == null) {
                            fromResource = Resourcepart.EMPTY;
                            bareFrom = from.asBareJid();
                        } else {
                            fullFrom = from.asFullJidIfPossible();
                        }
                    }
                    int i = AnonymousClass6.$SwitchMap$org$jivesoftware$smack$packet$Presence$Type[presence.getType().ordinal()];
                    if (i == 1) {
                        Map<Resourcepart, Presence> userPresences = Roster.this.getOrCreatePresencesInternal(key);
                        userPresences.remove(Resourcepart.EMPTY);
                        userPresences.put(fromResource, presence);
                        if (Roster.this.contains(key)) {
                            Roster.this.fireRosterPresenceEvent(presence);
                        }
                        for (PresenceEventListener presenceEventListener : Roster.this.presenceEventListeners) {
                            presenceEventListener.presenceAvailable(fullFrom, presence);
                        }
                    } else if (i == 2) {
                        Map<Resourcepart, Presence> userPresences2 = Roster.this.getOrCreatePresencesInternal(key);
                        if (from.hasNoResource()) {
                            userPresences2.put(Resourcepart.EMPTY, presence);
                        } else {
                            userPresences2.put(fromResource, presence);
                        }
                        if (Roster.this.contains(key)) {
                            Roster.this.fireRosterPresenceEvent(presence);
                        }
                        if (fullFrom != null) {
                            for (PresenceEventListener presenceEventListener2 : Roster.this.presenceEventListeners) {
                                presenceEventListener2.presenceUnavailable(fullFrom, presence);
                            }
                            return;
                        }
                        Logger access$500 = Roster.LOGGER;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Unavailable presence from bare JID: ");
                        sb.append(presence);
                        access$500.fine(sb.toString());
                    } else if (i == 3) {
                        Jid jid2 = from;
                        if (jid2 != null && jid2.isEntityBareJid()) {
                            Map<Resourcepart, Presence> userPresences3 = Roster.this.getOrCreatePresencesInternal(key);
                            userPresences3.clear();
                            userPresences3.put(Resourcepart.EMPTY, presence);
                            if (Roster.this.contains(key)) {
                                Roster.this.fireRosterPresenceEvent(presence);
                            }
                            for (PresenceEventListener presenceEventListener3 : Roster.this.presenceEventListeners) {
                                presenceEventListener3.presenceError(from, presence);
                            }
                        }
                    } else if (i == 4) {
                        for (PresenceEventListener presenceEventListener4 : Roster.this.presenceEventListeners) {
                            presenceEventListener4.presenceSubscribed(bareFrom, presence);
                        }
                    } else if (i == 5) {
                        for (PresenceEventListener presenceEventListener5 : Roster.this.presenceEventListeners) {
                            presenceEventListener5.presenceUnsubscribed(bareFrom, presence);
                        }
                    }
                }
            });
        }
    }

    private final class RosterPushListener extends AbstractIqRequestHandler {
        private RosterPushListener() {
            super("query", RosterPacket.NAMESPACE, IQ.Type.set, Mode.sync);
        }

        public IQ handleIQRequest(IQ iqRequest) {
            Collection<Jid> deletedEntries;
            IQ iq = iqRequest;
            XMPPConnection connection = Roster.this.connection();
            RosterPacket rosterPacket = (RosterPacket) iq;
            EntityFullJid ourFullJid = connection.getUser();
            if (ourFullJid == null) {
                Logger access$500 = Roster.LOGGER;
                StringBuilder sb = new StringBuilder();
                sb.append("Ignoring roster push ");
                sb.append(iq);
                sb.append(" while ");
                sb.append(connection);
                sb.append(" has no bound resource. This may be a server bug.");
                access$500.warning(sb.toString());
                return null;
            }
            EntityBareJid ourBareJid = ourFullJid.asEntityBareJid();
            Jid from = rosterPacket.getFrom();
            if (from != null) {
                if (from.equals((CharSequence) ourFullJid)) {
                    Logger access$5002 = Roster.LOGGER;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Received roster push from full JID. This behavior is since RFC 6121 not longer standard compliant. Please ask your server vendor to fix this and comply to RFC 6121 § 2.1.6. IQ roster push stanza: ");
                    sb2.append(iq);
                    access$5002.warning(sb2.toString());
                } else if (!from.equals((CharSequence) ourBareJid)) {
                    Logger access$5003 = Roster.LOGGER;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Ignoring roster push with a non matching 'from' ourJid='");
                    sb3.append(ourBareJid);
                    sb3.append("' from='");
                    sb3.append(from);
                    sb3.append("'");
                    access$5003.warning(sb3.toString());
                    return IQ.createErrorResponse(iq, Condition.service_unavailable);
                }
            }
            Collection<Item> items = rosterPacket.getRosterItems();
            if (items.size() != 1) {
                Logger access$5004 = Roster.LOGGER;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Ignoring roster push with not exactly one entry. size=");
                sb4.append(items.size());
                access$5004.warning(sb4.toString());
                return IQ.createErrorResponse(iq, Condition.bad_request);
            }
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Collection<Jid> arrayList3 = new ArrayList<>();
            Collection<Jid> unchangedEntries = new ArrayList<>();
            Item item = (Item) items.iterator().next();
            RosterEntry entry = new RosterEntry(item, Roster.this, connection);
            String version = rosterPacket.getVersion();
            if (item.getItemType().equals(ItemType.remove)) {
                Roster.this.deleteEntry(arrayList3, entry);
                if (Roster.this.rosterStore != null) {
                    Roster.this.rosterStore.removeEntry(entry.getJid(), version);
                    XMPPConnection xMPPConnection = connection;
                    String str = version;
                    RosterEntry rosterEntry = entry;
                    Item item2 = item;
                    deletedEntries = arrayList3;
                } else {
                    XMPPConnection xMPPConnection2 = connection;
                    String str2 = version;
                    RosterEntry rosterEntry2 = entry;
                    Item item3 = item;
                    deletedEntries = arrayList3;
                }
            } else if (Roster.hasValidSubscriptionType(item)) {
                String version2 = version;
                Item item4 = item;
                XMPPConnection xMPPConnection3 = connection;
                deletedEntries = arrayList3;
                Roster.this.addUpdateEntry(arrayList, arrayList2, unchangedEntries, item, entry);
                if (Roster.this.rosterStore != null) {
                    Roster.this.rosterStore.addEntry(item4, version2);
                }
            } else {
                String str3 = version;
                RosterEntry rosterEntry3 = entry;
                Item item5 = item;
                deletedEntries = arrayList3;
            }
            Roster.this.removeEmptyGroups();
            Roster.this.fireRosterChangedEvent(arrayList, arrayList2, deletedEntries);
            return IQ.createResultIQ(rosterPacket);
        }
    }

    private class RosterResultListener implements SuccessCallback<IQ> {
        private RosterResultListener() {
        }

        public void onSuccess(IQ packet) {
            IQ iq = packet;
            XMPPConnection connection = Roster.this.connection();
            Roster.LOGGER.log(Level.FINE, "RosterResultListener received {}", iq);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Collection<Jid> deletedEntries = new ArrayList<>();
            ArrayList arrayList3 = new ArrayList();
            if (iq instanceof RosterPacket) {
                RosterPacket rosterPacket = (RosterPacket) iq;
                ArrayList arrayList4 = new ArrayList();
                for (Item item : rosterPacket.getRosterItems()) {
                    if (Roster.hasValidSubscriptionType(item)) {
                        arrayList4.add(item);
                    }
                }
                Iterator it = arrayList4.iterator();
                while (it.hasNext()) {
                    Item item2 = (Item) it.next();
                    Item item3 = item2;
                    Roster.this.addUpdateEntry(arrayList, arrayList2, arrayList3, item2, new RosterEntry(item2, Roster.this, connection));
                }
                Set<Jid> toDelete = new HashSet<>();
                for (RosterEntry entry : Roster.this.entries.values()) {
                    toDelete.add(entry.getJid());
                }
                toDelete.removeAll(arrayList);
                toDelete.removeAll(arrayList2);
                toDelete.removeAll(arrayList3);
                for (Jid user : toDelete) {
                    Roster roster = Roster.this;
                    roster.deleteEntry(deletedEntries, (RosterEntry) roster.entries.get(user));
                }
                if (Roster.this.rosterStore != null) {
                    Roster.this.rosterStore.resetEntries(arrayList4, rosterPacket.getVersion());
                }
                Roster.this.removeEmptyGroups();
            } else {
                List<Item> storedItems = Roster.this.rosterStore.getEntries();
                if (storedItems == null) {
                    Roster.this.rosterStore.resetStore();
                    try {
                        Roster.this.reload();
                    } catch (InterruptedException | NotConnectedException | NotLoggedInException e) {
                        Roster.LOGGER.log(Level.FINE, "Exception while trying to load the roster after the roster store was corrupted", e);
                    }
                    return;
                }
                for (Item item4 : storedItems) {
                    Roster.this.addUpdateEntry(arrayList, arrayList2, arrayList3, item4, new RosterEntry(item4, Roster.this, connection));
                }
            }
            Roster.this.rosterState = RosterState.loaded;
            synchronized (Roster.this) {
                Roster.this.notifyAll();
            }
            Roster.this.fireRosterChangedEvent(arrayList, arrayList2, deletedEntries);
            try {
                synchronized (Roster.this.rosterLoadedListeners) {
                    for (RosterLoadedListener rosterLoadedListener : Roster.this.rosterLoadedListeners) {
                        rosterLoadedListener.onRosterLoaded(Roster.this);
                    }
                }
            } catch (Exception e2) {
                Roster.LOGGER.log(Level.WARNING, "RosterLoadedListener threw exception", e2);
            }
        }
    }

    private enum RosterState {
        uninitialized,
        loading,
        loaded
    }

    public enum SubscriptionMode {
        accept_all,
        reject_all,
        manual
    }

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                Roster.getInstanceFor(connection);
            }
        });
    }

    public static synchronized Roster getInstanceFor(XMPPConnection connection) {
        Roster roster;
        synchronized (Roster.class) {
            roster = (Roster) INSTANCES.get(connection);
            if (roster == null) {
                roster = new Roster(connection);
                INSTANCES.put(connection, roster);
            }
        }
        return roster;
    }

    public static SubscriptionMode getDefaultSubscriptionMode() {
        return defaultSubscriptionMode;
    }

    public static void setDefaultSubscriptionMode(SubscriptionMode subscriptionMode2) {
        defaultSubscriptionMode = subscriptionMode2;
    }

    private Roster(final XMPPConnection connection) {
        super(connection);
        connection.registerIQRequestHandler(new RosterPushListener());
        connection.addSyncStanzaListener(this.presencePacketListener, PRESENCE_PACKET_FILTER);
        connection.addAsyncStanzaListener(new StanzaListener() {
            public void processStanza(Stanza stanza) throws NotConnectedException, InterruptedException, NotLoggedInException {
                Presence response;
                Presence presence = (Presence) stanza;
                Jid from = presence.getFrom();
                SubscribeAnswer subscribeAnswer = null;
                int i = AnonymousClass6.$SwitchMap$org$jivesoftware$smack$roster$Roster$SubscriptionMode[Roster.this.subscriptionMode.ordinal()];
                if (i == 1) {
                    for (SubscribeListener subscribeListener : Roster.this.subscribeListeners) {
                        subscribeAnswer = subscribeListener.processSubscribe(from, presence);
                        if (subscribeAnswer != null) {
                            break;
                        }
                    }
                    if (subscribeAnswer == null) {
                        return;
                    }
                } else if (i == 2) {
                    subscribeAnswer = SubscribeAnswer.Approve;
                } else if (i == 3) {
                    subscribeAnswer = SubscribeAnswer.Deny;
                }
                if (subscribeAnswer != null) {
                    int i2 = AnonymousClass6.$SwitchMap$org$jivesoftware$smack$roster$SubscribeListener$SubscribeAnswer[subscribeAnswer.ordinal()];
                    if (i2 == 1) {
                        RosterUtil.askForSubscriptionIfRequired(Roster.this, from.asBareJid());
                    } else if (i2 != 2) {
                        if (i2 == 3) {
                            response = new Presence(Type.unsubscribed);
                            response.setTo(presence.getFrom());
                            connection.sendStanza(response);
                        }
                        throw new AssertionError();
                    }
                    response = new Presence(Type.subscribed);
                    response.setTo(presence.getFrom());
                    connection.sendStanza(response);
                }
            }
        }, PresenceTypeFilter.SUBSCRIBE);
        connection.addConnectionListener(new AbstractConnectionListener() {
            public void authenticated(XMPPConnection connection, boolean resumed) {
                if (Roster.this.isRosterLoadedAtLogin() && !resumed) {
                    Roster.this.setOfflinePresencesAndResetLoaded();
                    try {
                        Roster.this.reload();
                    } catch (InterruptedException | SmackException e) {
                        Roster.LOGGER.log(Level.SEVERE, "Could not reload Roster", e);
                    }
                }
            }

            public void connectionClosed() {
                Roster.this.setOfflinePresencesAndResetLoaded();
            }
        });
        connection.addStanzaSendingListener(new StanzaListener() {
            public void processStanza(Stanza stanzav) throws NotConnectedException, InterruptedException {
                Roster.this.setOfflinePresences();
            }
        }, OUTGOING_USER_UNAVAILABLE_PRESENCE);
        if (connection.isAuthenticated()) {
            try {
                reloadAndWait();
            } catch (InterruptedException | SmackException e) {
                LOGGER.log(Level.SEVERE, "Could not reload Roster", e);
            }
        }
    }

    private Map<Resourcepart, Presence> getPresencesInternal(BareJid entity) {
        Map<Resourcepart, Presence> entityPresences = (Map) this.presenceMap.get(entity);
        if (entityPresences == null) {
            return (Map) this.nonRosterPresenceMap.lookup(entity);
        }
        return entityPresences;
    }

    /* access modifiers changed from: private */
    public synchronized Map<Resourcepart, Presence> getOrCreatePresencesInternal(BareJid entity) {
        Map<Resourcepart, Presence> entityPresences;
        entityPresences = getPresencesInternal(entity);
        if (entityPresences == null) {
            if (contains(entity)) {
                entityPresences = new ConcurrentHashMap<>();
                this.presenceMap.put(entity, entityPresences);
            } else {
                Map<Resourcepart, Presence> lruCache = new LruCache<>(32);
                this.nonRosterPresenceMap.put(entity, lruCache);
                entityPresences = lruCache;
            }
        }
        return entityPresences;
    }

    public SubscriptionMode getSubscriptionMode() {
        return this.subscriptionMode;
    }

    public void setSubscriptionMode(SubscriptionMode subscriptionMode2) {
        this.subscriptionMode = subscriptionMode2;
    }

    public void reload() throws NotLoggedInException, NotConnectedException, InterruptedException {
        XMPPConnection connection = getAuthenticatedConnectionOrThrow();
        RosterPacket packet = new RosterPacket();
        if (this.rosterStore != null && isRosterVersioningSupported()) {
            packet.setVersion(this.rosterStore.getRosterVersion());
        }
        this.rosterState = RosterState.loading;
        connection.sendIqRequestAsync(packet).onSuccess(new RosterResultListener()).onError(new ExceptionCallback<Exception>() {
            public void processException(Exception exception) {
                Level logLevel;
                Roster.this.rosterState = RosterState.uninitialized;
                if (exception instanceof NotConnectedException) {
                    logLevel = Level.FINE;
                } else {
                    logLevel = Level.SEVERE;
                }
                Roster.LOGGER.log(logLevel, "Exception reloading roster", exception);
                for (RosterLoadedListener listener : Roster.this.rosterLoadedListeners) {
                    listener.onRosterLoadingFailed(exception);
                }
            }
        });
    }

    public void reloadAndWait() throws NotLoggedInException, NotConnectedException, InterruptedException {
        reload();
        waitUntilLoaded();
    }

    public boolean setRosterStore(RosterStore rosterStore2) {
        this.rosterStore = rosterStore2;
        try {
            reload();
            return true;
        } catch (InterruptedException | NotConnectedException | NotLoggedInException e) {
            LOGGER.log(Level.FINER, "Could not reload roster", e);
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public boolean waitUntilLoaded() throws InterruptedException {
        long waitTime = connection().getReplyTimeout();
        long start = System.currentTimeMillis();
        while (!isLoaded() && waitTime > 0) {
            synchronized (this) {
                if (!isLoaded()) {
                    wait(waitTime);
                }
            }
            long now = System.currentTimeMillis();
            waitTime -= now - start;
            start = now;
        }
        return isLoaded();
    }

    public boolean isLoaded() {
        return this.rosterState == RosterState.loaded;
    }

    public boolean addRosterListener(RosterListener rosterListener) {
        boolean add;
        synchronized (this.rosterListenersAndEntriesLock) {
            add = this.rosterListeners.add(rosterListener);
        }
        return add;
    }

    public boolean removeRosterListener(RosterListener rosterListener) {
        boolean remove;
        synchronized (this.rosterListenersAndEntriesLock) {
            remove = this.rosterListeners.remove(rosterListener);
        }
        return remove;
    }

    public boolean addRosterLoadedListener(RosterLoadedListener rosterLoadedListener) {
        boolean add;
        synchronized (rosterLoadedListener) {
            add = this.rosterLoadedListeners.add(rosterLoadedListener);
        }
        return add;
    }

    public boolean removeRosterLoadedListener(RosterLoadedListener rosterLoadedListener) {
        boolean remove;
        synchronized (rosterLoadedListener) {
            remove = this.rosterLoadedListeners.remove(rosterLoadedListener);
        }
        return remove;
    }

    public boolean addPresenceEventListener(PresenceEventListener presenceEventListener) {
        return this.presenceEventListeners.add(presenceEventListener);
    }

    public boolean removePresenceEventListener(PresenceEventListener presenceEventListener) {
        return this.presenceEventListeners.remove(presenceEventListener);
    }

    public RosterGroup createGroup(String name) {
        XMPPConnection connection = connection();
        if (this.groups.containsKey(name)) {
            return (RosterGroup) this.groups.get(name);
        }
        RosterGroup group = new RosterGroup(name, connection);
        this.groups.put(name, group);
        return group;
    }

    public void createEntry(BareJid user, String name, String[] groups2) throws NotLoggedInException, NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        XMPPConnection connection = getAuthenticatedConnectionOrThrow();
        RosterPacket rosterPacket = new RosterPacket();
        rosterPacket.setType(IQ.Type.set);
        Item item = new Item(user, name);
        if (groups2 != null) {
            for (String group : groups2) {
                if (group != null && group.trim().length() > 0) {
                    item.addGroupName(group);
                }
            }
        }
        rosterPacket.addRosterItem(item);
        connection.createStanzaCollectorAndSend(rosterPacket).nextResultOrThrow();
        sendSubscriptionRequest(user);
    }

    public void preApproveAndCreateEntry(BareJid user, String name, String[] groups2) throws NotLoggedInException, NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, FeatureNotSupportedException {
        preApprove(user);
        createEntry(user, name, groups2);
    }

    public void preApprove(BareJid user) throws NotLoggedInException, NotConnectedException, InterruptedException, FeatureNotSupportedException {
        XMPPConnection connection = connection();
        if (isSubscriptionPreApprovalSupported()) {
            Presence presencePacket = new Presence(Type.subscribed);
            presencePacket.setTo((Jid) user);
            connection.sendStanza(presencePacket);
            return;
        }
        throw new FeatureNotSupportedException("Pre-approving");
    }

    public boolean isSubscriptionPreApprovalSupported() throws NotLoggedInException {
        return getAuthenticatedConnectionOrThrow().hasFeature(SubscriptionPreApproval.ELEMENT, SubscriptionPreApproval.NAMESPACE);
    }

    public void sendSubscriptionRequest(BareJid jid) throws NotLoggedInException, NotConnectedException, InterruptedException {
        XMPPConnection connection = getAuthenticatedConnectionOrThrow();
        Presence presencePacket = new Presence(Type.subscribe);
        presencePacket.setTo((Jid) jid);
        connection.sendStanza(presencePacket);
    }

    public boolean addSubscribeListener(SubscribeListener subscribeListener) {
        Objects.requireNonNull(subscribeListener, "SubscribeListener argument must not be null");
        if (this.subscriptionMode != SubscriptionMode.manual) {
            this.previousSubscriptionMode = this.subscriptionMode;
            this.subscriptionMode = SubscriptionMode.manual;
        }
        return this.subscribeListeners.add(subscribeListener);
    }

    public boolean removeSubscribeListener(SubscribeListener subscribeListener) {
        boolean removed = this.subscribeListeners.remove(subscribeListener);
        if (removed && this.subscribeListeners.isEmpty()) {
            setSubscriptionMode(this.previousSubscriptionMode);
        }
        return removed;
    }

    public void removeEntry(RosterEntry entry) throws NotLoggedInException, NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        XMPPConnection connection = getAuthenticatedConnectionOrThrow();
        if (this.entries.containsKey(entry.getJid())) {
            RosterPacket packet = new RosterPacket();
            packet.setType(IQ.Type.set);
            Item item = RosterEntry.toRosterItem(entry);
            item.setItemType(ItemType.remove);
            packet.addRosterItem(item);
            connection.createStanzaCollectorAndSend(packet).nextResultOrThrow();
        }
    }

    public int getEntryCount() {
        return getEntries().size();
    }

    public void getEntriesAndAddListener(RosterListener rosterListener, RosterEntries rosterEntries) {
        Objects.requireNonNull(rosterListener, "listener must not be null");
        Objects.requireNonNull(rosterEntries, "rosterEntries must not be null");
        synchronized (this.rosterListenersAndEntriesLock) {
            rosterEntries.rosterEntries(this.entries.values());
            addRosterListener(rosterListener);
        }
    }

    public Set<RosterEntry> getEntries() {
        Set<RosterEntry> allEntries;
        synchronized (this.rosterListenersAndEntriesLock) {
            allEntries = new HashSet<>(this.entries.size());
            for (RosterEntry entry : this.entries.values()) {
                allEntries.add(entry);
            }
        }
        return allEntries;
    }

    public int getUnfiledEntryCount() {
        return this.unfiledEntries.size();
    }

    public Set<RosterEntry> getUnfiledEntries() {
        return Collections.unmodifiableSet(this.unfiledEntries);
    }

    public RosterEntry getEntry(BareJid jid) {
        if (jid == null) {
            return null;
        }
        return (RosterEntry) this.entries.get(jid);
    }

    public boolean contains(BareJid jid) {
        return getEntry(jid) != null;
    }

    public RosterGroup getGroup(String name) {
        return (RosterGroup) this.groups.get(name);
    }

    public int getGroupCount() {
        return this.groups.size();
    }

    public Collection<RosterGroup> getGroups() {
        return Collections.unmodifiableCollection(this.groups.values());
    }

    public Presence getPresence(BareJid jid) {
        Map<Resourcepart, Presence> userPresences = getPresencesInternal(jid);
        if (userPresences == null) {
            Presence presence = new Presence(Type.unavailable);
            presence.setFrom((Jid) jid);
            return presence;
        }
        Presence presence2 = null;
        Presence unavailable = null;
        for (Presence p : userPresences.values()) {
            if (!p.isAvailable()) {
                unavailable = p;
            } else if (presence2 == null || p.getPriority() > presence2.getPriority()) {
                presence2 = p;
            } else if (p.getPriority() == presence2.getPriority()) {
                Presence.Mode pMode = p.getMode();
                if (pMode == null) {
                    pMode = Presence.Mode.available;
                }
                Presence.Mode presenceMode = presence2.getMode();
                if (presenceMode == null) {
                    presenceMode = Presence.Mode.available;
                }
                if (pMode.compareTo(presenceMode) < 0) {
                    presence2 = p;
                }
            }
        }
        if (presence2 != null) {
            return presence2.clone();
        }
        if (unavailable != null) {
            return unavailable.clone();
        }
        Presence presence3 = new Presence(Type.unavailable);
        presence3.setFrom((Jid) jid);
        return presence3;
    }

    public Presence getPresenceResource(FullJid userWithResource) {
        BareJid key = userWithResource.asBareJid();
        Resourcepart resource = userWithResource.getResourcepart();
        Map<Resourcepart, Presence> userPresences = getPresencesInternal(key);
        if (userPresences == null) {
            Presence presence = new Presence(Type.unavailable);
            presence.setFrom((Jid) userWithResource);
            return presence;
        }
        Presence presence2 = (Presence) userPresences.get(resource);
        if (presence2 != null) {
            return presence2.clone();
        }
        Presence presence3 = new Presence(Type.unavailable);
        presence3.setFrom((Jid) userWithResource);
        return presence3;
    }

    public List<Presence> getAllPresences(BareJid bareJid) {
        Map<Resourcepart, Presence> userPresences = getPresencesInternal(bareJid);
        if (userPresences == null) {
            Presence unavailable = new Presence(Type.unavailable);
            unavailable.setFrom((Jid) bareJid);
            return new ArrayList<>(Arrays.asList(new Presence[]{unavailable}));
        }
        List<Presence> res = new ArrayList<>(userPresences.values().size());
        for (Presence presence : userPresences.values()) {
            res.add(presence.clone());
        }
        return res;
    }

    public List<Presence> getAvailablePresences(BareJid bareJid) {
        List<Presence> allPresences = getAllPresences(bareJid);
        List<Presence> res = new ArrayList<>(allPresences.size());
        for (Presence presence : allPresences) {
            if (presence.isAvailable()) {
                res.add(presence);
            }
        }
        return res;
    }

    public List<Presence> getPresences(BareJid jid) {
        Map<Resourcepart, Presence> userPresences = getPresencesInternal(jid);
        if (userPresences == null) {
            Presence presence = new Presence(Type.unavailable);
            presence.setFrom((Jid) jid);
            return Arrays.asList(new Presence[]{presence});
        }
        List<Presence> arrayList = new ArrayList<>();
        Presence unavailable = null;
        for (Presence presence2 : userPresences.values()) {
            if (presence2.isAvailable()) {
                arrayList.add(presence2.clone());
            } else {
                unavailable = presence2;
            }
        }
        if (!arrayList.isEmpty()) {
            return arrayList;
        }
        if (unavailable != null) {
            return Arrays.asList(new Presence[]{unavailable.clone()});
        }
        Presence presence3 = new Presence(Type.unavailable);
        presence3.setFrom((Jid) jid);
        return Arrays.asList(new Presence[]{presence3});
    }

    public boolean isSubscribedToMyPresence(Jid jid) {
        if (jid == null) {
            return false;
        }
        BareJid bareJid = jid.asBareJid();
        if (connection().getXMPPServiceDomain().equals((CharSequence) bareJid)) {
            return true;
        }
        RosterEntry entry = getEntry(bareJid);
        if (entry == null) {
            return false;
        }
        return entry.canSeeMyPresence();
    }

    public boolean iAmSubscribedTo(Jid jid) {
        if (jid == null) {
            return false;
        }
        RosterEntry entry = getEntry(jid.asBareJid());
        if (entry == null) {
            return false;
        }
        return entry.canSeeHisPresence();
    }

    public static void setRosterLoadedAtLoginDefault(boolean rosterLoadedAtLoginDefault2) {
        rosterLoadedAtLoginDefault = rosterLoadedAtLoginDefault2;
    }

    public void setRosterLoadedAtLogin(boolean rosterLoadedAtLogin2) {
        this.rosterLoadedAtLogin = rosterLoadedAtLogin2;
    }

    public boolean isRosterLoadedAtLogin() {
        return this.rosterLoadedAtLogin;
    }

    /* access modifiers changed from: 0000 */
    public RosterStore getRosterStore() {
        return this.rosterStore;
    }

    /* access modifiers changed from: private */
    public void setOfflinePresences() {
        for (Jid user : this.presenceMap.keySet()) {
            Map<Resourcepart, Presence> resources = (Map) this.presenceMap.get(user);
            if (resources != null) {
                for (Resourcepart resource : resources.keySet()) {
                    Presence packetUnavailable = new Presence(Type.unavailable);
                    EntityBareJid bareUserJid = user.asEntityBareJidIfPossible();
                    if (bareUserJid == null) {
                        Logger logger = LOGGER;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Can not transform user JID to bare JID: '");
                        sb.append(user);
                        sb.append("'");
                        logger.warning(sb.toString());
                    } else {
                        packetUnavailable.setFrom((Jid) JidCreate.fullFrom(bareUserJid, resource));
                        try {
                            this.presencePacketListener.processStanza(packetUnavailable);
                        } catch (NotConnectedException e) {
                            throw new IllegalStateException("presencePacketListener should never throw a NotConnectedException when processStanza is called with a presence of type unavailable", e);
                        } catch (InterruptedException e2) {
                            return;
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void setOfflinePresencesAndResetLoaded() {
        setOfflinePresences();
        this.rosterState = RosterState.uninitialized;
    }

    /* access modifiers changed from: private */
    public void fireRosterChangedEvent(Collection<Jid> addedEntries, Collection<Jid> updatedEntries, Collection<Jid> deletedEntries) {
        synchronized (this.rosterListenersAndEntriesLock) {
            for (RosterListener listener : this.rosterListeners) {
                if (!addedEntries.isEmpty()) {
                    listener.entriesAdded(addedEntries);
                }
                if (!updatedEntries.isEmpty()) {
                    listener.entriesUpdated(updatedEntries);
                }
                if (!deletedEntries.isEmpty()) {
                    listener.entriesDeleted(deletedEntries);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void fireRosterPresenceEvent(Presence presence) {
        synchronized (this.rosterListenersAndEntriesLock) {
            for (RosterListener listener : this.rosterListeners) {
                listener.presenceChanged(presence);
            }
        }
    }

    /* access modifiers changed from: private */
    public void addUpdateEntry(Collection<Jid> addedEntries, Collection<Jid> updatedEntries, Collection<Jid> unchangedEntries, Item item, RosterEntry entry) {
        RosterEntry oldEntry;
        synchronized (this.rosterListenersAndEntriesLock) {
            oldEntry = (RosterEntry) this.entries.put(item.getJid(), entry);
        }
        if (oldEntry == null) {
            BareJid jid = item.getJid();
            addedEntries.add(jid);
            move(jid, this.nonRosterPresenceMap, this.presenceMap);
        } else {
            Item oldItem = RosterEntry.toRosterItem(oldEntry);
            if (!oldEntry.equalsDeep(entry) || !item.getGroupNames().equals(oldItem.getGroupNames())) {
                updatedEntries.add(item.getJid());
                oldEntry.updateItem(item);
            } else {
                unchangedEntries.add(item.getJid());
            }
        }
        if (item.getGroupNames().isEmpty()) {
            this.unfiledEntries.add(entry);
        } else {
            this.unfiledEntries.remove(entry);
        }
        List<String> newGroupNames = new ArrayList<>();
        for (String groupName : item.getGroupNames()) {
            newGroupNames.add(groupName);
            RosterGroup group = getGroup(groupName);
            if (group == null) {
                group = createGroup(groupName);
                this.groups.put(groupName, group);
            }
            group.addEntryLocal(entry);
        }
        List<String> oldGroupNames = new ArrayList<>();
        for (RosterGroup group2 : getGroups()) {
            oldGroupNames.add(group2.getName());
        }
        oldGroupNames.removeAll(newGroupNames);
        for (String groupName2 : oldGroupNames) {
            RosterGroup group3 = getGroup(groupName2);
            group3.removeEntryLocal(entry);
            if (group3.getEntryCount() == 0) {
                this.groups.remove(groupName2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void deleteEntry(Collection<Jid> deletedEntries, RosterEntry entry) {
        BareJid user = entry.getJid();
        this.entries.remove(user);
        this.unfiledEntries.remove(entry);
        move(user, this.presenceMap, this.nonRosterPresenceMap);
        deletedEntries.add(user);
        for (Entry<String, RosterGroup> e : this.groups.entrySet()) {
            RosterGroup group = (RosterGroup) e.getValue();
            group.removeEntryLocal(entry);
            if (group.getEntryCount() == 0) {
                this.groups.remove(e.getKey());
            }
        }
    }

    /* access modifiers changed from: private */
    public void removeEmptyGroups() {
        for (RosterGroup group : getGroups()) {
            if (group.getEntryCount() == 0) {
                this.groups.remove(group.getName());
            }
        }
    }

    private static void move(BareJid entity, Map<BareJid, Map<Resourcepart, Presence>> from, Map<BareJid, Map<Resourcepart, Presence>> to) {
        Map<Resourcepart, Presence> presences = (Map) from.remove(entity);
        if (presences != null && !presences.isEmpty()) {
            to.put(entity, presences);
        }
    }

    /* access modifiers changed from: private */
    public static boolean hasValidSubscriptionType(Item item) {
        int i = AnonymousClass6.$SwitchMap$org$jivesoftware$smack$roster$packet$RosterPacket$ItemType[item.getItemType().ordinal()];
        if (i == 1 || i == 2 || i == 3 || i == 4) {
            return true;
        }
        return false;
    }

    public boolean isRosterVersioningSupported() {
        return connection().hasFeature(RosterVer.ELEMENT, RosterVer.NAMESPACE);
    }

    public static void setDefaultNonRosterPresenceMapMaxSize(int maximumSize) {
        defaultNonRosterPresenceMapMaxSize = maximumSize;
    }

    public void setNonRosterPresenceMapMaxSize(int maximumSize) {
        this.nonRosterPresenceMap.setMaxCacheSize(maximumSize);
    }
}
