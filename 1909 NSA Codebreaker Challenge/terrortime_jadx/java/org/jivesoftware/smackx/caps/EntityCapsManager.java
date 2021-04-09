package org.jivesoftware.smackx.caps;

import com.badguy.terrortime.BuildConfig;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PresenceTypeFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.AbstractPresenceEventListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jivesoftware.smackx.caps.cache.EntityCapsPersistentCache;
import org.jivesoftware.smackx.caps.packet.CapsExtension;
import org.jivesoftware.smackx.disco.AbstractNodeInformationProvider;
import org.jivesoftware.smackx.disco.DiscoInfoLookupShortcutMechanism;
import org.jivesoftware.smackx.disco.EntityCapabilitiesChangedListener;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo.Feature;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.util.cache.LruCache;

public final class EntityCapsManager extends Manager {
    static final LruCache<String, DiscoverInfo> CAPS_CACHE = new LruCache<>(1000);
    private static String DEFAULT_ENTITY_NODE = "http://www.igniterealtime.org/projects/smack";
    private static final String DEFAULT_HASH = "SHA-1";
    public static final String ELEMENT = "c";
    static final LruCache<Jid, NodeVerHash> JID_TO_NODEVER_CACHE = new LruCache<>(10000);
    private static final Logger LOGGER = Logger.getLogger(EntityCapsManager.class.getName());
    public static final String NAMESPACE = "http://jabber.org/protocol/caps";
    private static final StanzaFilter PRESENCES_WITH_CAPS = new AndFilter(new StanzaTypeFilter(Presence.class), new StanzaExtensionFilter("c", "http://jabber.org/protocol/caps"));
    private static final Map<String, MessageDigest> SUPPORTED_HASHES = new HashMap();
    private static boolean autoEnableEntityCaps = true;
    private static final Map<XMPPConnection, EntityCapsManager> instances = new WeakHashMap();
    protected static EntityCapsPersistentCache persistentCache;
    private CapsVersionAndHash currentCapsVersion;
    /* access modifiers changed from: private */
    public boolean entityCapsEnabled;
    /* access modifiers changed from: private */
    public String entityNode = DEFAULT_ENTITY_NODE;
    private final Queue<CapsVersionAndHash> lastLocalCapsVersions = new ConcurrentLinkedQueue();
    /* access modifiers changed from: private */
    public volatile Presence presenceSend;
    /* access modifiers changed from: private */
    public final ServiceDiscoveryManager sdm;

    public static class NodeVerHash {
        private String hash;
        private String node;
        /* access modifiers changed from: private */
        public String nodeVer;
        private String ver;

        NodeVerHash(String node2, CapsVersionAndHash capsVersionAndHash) {
            this(node2, capsVersionAndHash.version, capsVersionAndHash.hash);
        }

        NodeVerHash(String node2, String ver2, String hash2) {
            this.node = node2;
            this.ver = ver2;
            this.hash = hash2;
            StringBuilder sb = new StringBuilder();
            sb.append(node2);
            sb.append("#");
            sb.append(ver2);
            this.nodeVer = sb.toString();
        }

        public String getNodeVer() {
            return this.nodeVer;
        }

        public String getNode() {
            return this.node;
        }

        public String getHash() {
            return this.hash;
        }

        public String getVer() {
            return this.ver;
        }
    }

    static {
        String str = "SHA-1";
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                EntityCapsManager.getInstanceFor(connection);
            }
        });
        try {
            SUPPORTED_HASHES.put(str, MessageDigest.getInstance(str));
        } catch (NoSuchAlgorithmException e) {
        }
        ServiceDiscoveryManager.addDiscoInfoLookupShortcutMechanism(new DiscoInfoLookupShortcutMechanism("XEP-0115: Entity Capabilities", 100) {
            public DiscoverInfo getDiscoverInfoByUser(ServiceDiscoveryManager serviceDiscoveryManager, Jid jid) {
                DiscoverInfo info = EntityCapsManager.getDiscoverInfoByUser(jid);
                if (info != null) {
                    return info;
                }
                NodeVerHash nodeVerHash = EntityCapsManager.getNodeVerHashByJid(jid);
                if (nodeVerHash == null) {
                    return null;
                }
                try {
                    DiscoverInfo info2 = serviceDiscoveryManager.discoverInfo(jid, nodeVerHash.getNodeVer());
                    if (EntityCapsManager.verifyDiscoverInfoVersion(nodeVerHash.getVer(), nodeVerHash.getHash(), info2)) {
                        EntityCapsManager.addDiscoverInfoByNode(nodeVerHash.getNodeVer(), info2);
                    }
                    return info2;
                } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e) {
                    return null;
                }
            }
        });
    }

    public static void setDefaultEntityNode(String entityNode2) {
        DEFAULT_ENTITY_NODE = entityNode2;
    }

    static void addDiscoverInfoByNode(String nodeVer, DiscoverInfo info) {
        CAPS_CACHE.put(nodeVer, info);
        EntityCapsPersistentCache entityCapsPersistentCache = persistentCache;
        if (entityCapsPersistentCache != null) {
            entityCapsPersistentCache.addDiscoverInfoByNodePersistent(nodeVer, info);
        }
    }

    public static String getNodeVersionByJid(Jid jid) {
        NodeVerHash nvh = (NodeVerHash) JID_TO_NODEVER_CACHE.lookup(jid);
        if (nvh != null) {
            return nvh.nodeVer;
        }
        return null;
    }

    public static NodeVerHash getNodeVerHashByJid(Jid jid) {
        return (NodeVerHash) JID_TO_NODEVER_CACHE.lookup(jid);
    }

    public static DiscoverInfo getDiscoverInfoByUser(Jid user) {
        NodeVerHash nvh = (NodeVerHash) JID_TO_NODEVER_CACHE.lookup(user);
        if (nvh == null) {
            return null;
        }
        return getDiscoveryInfoByNodeVer(nvh.nodeVer);
    }

    public static DiscoverInfo getDiscoveryInfoByNodeVer(String nodeVer) {
        DiscoverInfo info = (DiscoverInfo) CAPS_CACHE.lookup(nodeVer);
        if (info == null) {
            EntityCapsPersistentCache entityCapsPersistentCache = persistentCache;
            if (entityCapsPersistentCache != null) {
                info = entityCapsPersistentCache.lookup(nodeVer);
                if (info != null) {
                    CAPS_CACHE.put(nodeVer, info);
                }
            }
        }
        if (info != null) {
            return new DiscoverInfo(info);
        }
        return info;
    }

    public static void setPersistentCache(EntityCapsPersistentCache cache) {
        persistentCache = cache;
    }

    public static void setMaxsCacheSizes(int maxJidToNodeVerSize, int maxCapsCacheSize) {
        JID_TO_NODEVER_CACHE.setMaxCacheSize(maxJidToNodeVerSize);
        CAPS_CACHE.setMaxCacheSize(maxCapsCacheSize);
    }

    public static void clearMemoryCache() {
        JID_TO_NODEVER_CACHE.clear();
        CAPS_CACHE.clear();
    }

    /* access modifiers changed from: private */
    public static void addCapsExtensionInfo(Jid from, CapsExtension capsExtension) {
        String capsExtensionHash = capsExtension.getHash();
        if (SUPPORTED_HASHES.containsKey(capsExtensionHash.toUpperCase(Locale.US))) {
            String hash = capsExtensionHash.toLowerCase(Locale.US);
            JID_TO_NODEVER_CACHE.put(from, new NodeVerHash(capsExtension.getNode(), capsExtension.getVer(), hash));
        }
    }

    private EntityCapsManager(XMPPConnection connection) {
        super(connection);
        this.sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        instances.put(connection, this);
        connection.addConnectionListener(new AbstractConnectionListener() {
            public void connected(XMPPConnection connection) {
                processCapsStreamFeatureIfAvailable(connection);
            }

            public void authenticated(XMPPConnection connection, boolean resumed) {
                processCapsStreamFeatureIfAvailable(connection);
                if (!resumed) {
                    EntityCapsManager.this.presenceSend = null;
                }
            }

            private void processCapsStreamFeatureIfAvailable(XMPPConnection connection) {
                CapsExtension capsExtension = (CapsExtension) connection.getFeature("c", "http://jabber.org/protocol/caps");
                if (capsExtension != null) {
                    EntityCapsManager.addCapsExtensionInfo(connection.getXMPPServiceDomain(), capsExtension);
                }
            }
        });
        updateLocalEntityCaps();
        if (autoEnableEntityCaps) {
            enableEntityCaps();
        }
        connection.addAsyncStanzaListener(new StanzaListener() {
            public void processStanza(Stanza packet) {
                if (EntityCapsManager.this.entityCapsEnabled()) {
                    EntityCapsManager.addCapsExtensionInfo(packet.getFrom(), CapsExtension.from(packet));
                }
            }
        }, PRESENCES_WITH_CAPS);
        Roster.getInstanceFor(connection).addPresenceEventListener(new AbstractPresenceEventListener() {
            public void presenceUnavailable(FullJid from, Presence presence) {
                EntityCapsManager.JID_TO_NODEVER_CACHE.remove(from);
            }
        });
        connection.addStanzaSendingListener(new StanzaListener() {
            public void processStanza(Stanza packet) {
                EntityCapsManager.this.presenceSend = (Presence) packet;
            }
        }, PresenceTypeFilter.OUTGOING_PRESENCE_BROADCAST);
        connection.addStanzaInterceptor(new StanzaListener() {
            public void processStanza(Stanza packet) {
                if (!EntityCapsManager.this.entityCapsEnabled) {
                    packet.removeExtension("c", "http://jabber.org/protocol/caps");
                    return;
                }
                CapsVersionAndHash capsVersionAndHash = EntityCapsManager.this.getCapsVersionAndHash();
                packet.overrideExtension(new CapsExtension(EntityCapsManager.this.entityNode, capsVersionAndHash.version, capsVersionAndHash.hash));
            }
        }, PresenceTypeFilter.AVAILABLE);
        this.sdm.addEntityCapabilitiesChangedListener(new EntityCapabilitiesChangedListener() {
            public void onEntityCapailitiesChanged() {
                if (EntityCapsManager.this.entityCapsEnabled()) {
                    EntityCapsManager.this.updateLocalEntityCaps();
                }
            }
        });
    }

    public static synchronized EntityCapsManager getInstanceFor(XMPPConnection connection) {
        EntityCapsManager entityCapsManager;
        synchronized (EntityCapsManager.class) {
            if (SUPPORTED_HASHES.size() > 0) {
                entityCapsManager = (EntityCapsManager) instances.get(connection);
                if (entityCapsManager == null) {
                    entityCapsManager = new EntityCapsManager(connection);
                }
            } else {
                throw new IllegalStateException("No supported hashes for EntityCapsManager");
            }
        }
        return entityCapsManager;
    }

    public synchronized void enableEntityCaps() {
        this.sdm.addFeature("http://jabber.org/protocol/caps");
        updateLocalEntityCaps();
        this.entityCapsEnabled = true;
    }

    public synchronized void disableEntityCaps() {
        this.entityCapsEnabled = false;
        this.sdm.removeFeature("http://jabber.org/protocol/caps");
    }

    public boolean entityCapsEnabled() {
        return this.entityCapsEnabled;
    }

    public void setEntityNode(String entityNode2) {
        this.entityNode = entityNode2;
        updateLocalEntityCaps();
    }

    public static void removeUserCapsNode(Jid user) {
        JID_TO_NODEVER_CACHE.remove(user);
    }

    public CapsVersionAndHash getCapsVersionAndHash() {
        return this.currentCapsVersion;
    }

    public String getLocalNodeVer() {
        CapsVersionAndHash capsVersionAndHash = getCapsVersionAndHash();
        if (capsVersionAndHash == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.entityNode);
        sb.append('#');
        sb.append(capsVersionAndHash.version);
        return sb.toString();
    }

    public boolean areEntityCapsSupported(Jid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return this.sdm.supportsFeature(jid, "http://jabber.org/protocol/caps");
    }

    public boolean areEntityCapsSupportedByServer() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return areEntityCapsSupported(connection().getXMPPServiceDomain());
    }

    /* access modifiers changed from: private */
    public void updateLocalEntityCaps() {
        XMPPConnection connection = connection();
        DiscoverInfo discoverInfo = new DiscoverInfo();
        discoverInfo.setType(Type.result);
        this.sdm.addDiscoverInfoTo(discoverInfo);
        this.currentCapsVersion = generateVerificationString(discoverInfo);
        String localNodeVer = getLocalNodeVer();
        discoverInfo.setNode(localNodeVer);
        addDiscoverInfoByNode(localNodeVer, discoverInfo);
        if (this.lastLocalCapsVersions.size() > 10) {
            CapsVersionAndHash oldCapsVersion = (CapsVersionAndHash) this.lastLocalCapsVersions.poll();
            ServiceDiscoveryManager serviceDiscoveryManager = this.sdm;
            StringBuilder sb = new StringBuilder();
            sb.append(this.entityNode);
            sb.append('#');
            sb.append(oldCapsVersion.version);
            serviceDiscoveryManager.removeNodeInformationProvider(sb.toString());
        }
        this.lastLocalCapsVersions.add(this.currentCapsVersion);
        if (connection != null) {
            JID_TO_NODEVER_CACHE.put(connection.getUser(), new NodeVerHash(this.entityNode, this.currentCapsVersion));
        }
        final List<Identity> identities = new LinkedList<>(ServiceDiscoveryManager.getInstanceFor(connection).getIdentities());
        this.sdm.setNodeInformationProvider(localNodeVer, new AbstractNodeInformationProvider() {
            List<String> features = EntityCapsManager.this.sdm.getFeatures();
            List<ExtensionElement> packetExtensions = EntityCapsManager.this.sdm.getExtendedInfoAsList();

            public List<String> getNodeFeatures() {
                return this.features;
            }

            public List<Identity> getNodeIdentities() {
                return identities;
            }

            public List<ExtensionElement> getNodePacketExtensions() {
                return this.packetExtensions;
            }
        });
        if (connection != null && connection.isAuthenticated() && this.presenceSend != null) {
            try {
                connection.sendStanza(this.presenceSend.cloneWithNewId());
            } catch (InterruptedException | NotConnectedException e) {
                LOGGER.log(Level.WARNING, "Could could not update presence with caps info", e);
            }
        }
    }

    public static boolean verifyDiscoverInfoVersion(String ver, String hash, DiscoverInfo info) {
        if (!info.containsDuplicateIdentities() && !info.containsDuplicateFeatures() && !verifyPacketExtensions(info) && ver.equals(generateVerificationString(info, hash).version)) {
            return true;
        }
        return false;
    }

    protected static boolean verifyPacketExtensions(DiscoverInfo info) {
        List<FormField> foundFormTypes = new LinkedList<>();
        for (ExtensionElement pe : info.getExtensions()) {
            if (pe.getNamespace().equals("jabber:x:data")) {
                for (FormField f : ((DataForm) pe).getFields()) {
                    if (f.getVariable().equals(FormField.FORM_TYPE)) {
                        for (FormField fft : foundFormTypes) {
                            if (f.equals(fft)) {
                                return true;
                            }
                        }
                        foundFormTypes.add(f);
                    }
                }
                continue;
            }
        }
        return false;
    }

    protected static CapsVersionAndHash generateVerificationString(DiscoverInfo discoverInfo) {
        return generateVerificationString(discoverInfo, null);
    }

    protected static CapsVersionAndHash generateVerificationString(DiscoverInfo discoverInfo, String hash) {
        byte[] digest;
        if (hash == null) {
            hash = "SHA-1";
        }
        MessageDigest md = (MessageDigest) SUPPORTED_HASHES.get(hash.toUpperCase(Locale.US));
        if (md == null) {
            return null;
        }
        String hash2 = hash.toLowerCase(Locale.US);
        DataForm extendedInfo = DataForm.from(discoverInfo);
        StringBuilder sb = new StringBuilder();
        SortedSet<Identity> sortedIdentities = new TreeSet<>();
        sortedIdentities.addAll(discoverInfo.getIdentities());
        for (Identity identity : sortedIdentities) {
            sb.append(identity.getCategory());
            sb.append('/');
            sb.append(identity.getType());
            sb.append('/');
            sb.append(identity.getLanguage() == null ? BuildConfig.FLAVOR : identity.getLanguage());
            sb.append('/');
            sb.append(identity.getName() == null ? BuildConfig.FLAVOR : identity.getName());
            sb.append('<');
        }
        SortedSet<String> features = new TreeSet<>();
        for (Feature f : discoverInfo.getFeatures()) {
            features.add(f.getVar());
        }
        for (String f2 : features) {
            sb.append(f2);
            sb.append('<');
        }
        if (extendedInfo != null && extendedInfo.hasHiddenFormTypeField()) {
            synchronized (extendedInfo) {
                SortedSet<FormField> fs = new TreeSet<>(new Comparator<FormField>() {
                    public int compare(FormField f1, FormField f2) {
                        return f1.getVariable().compareTo(f2.getVariable());
                    }
                });
                FormField ft = null;
                for (FormField f3 : extendedInfo.getFields()) {
                    if (!f3.getVariable().equals(FormField.FORM_TYPE)) {
                        fs.add(f3);
                    } else {
                        ft = f3;
                    }
                }
                if (ft != null) {
                    formFieldValuesToCaps(ft.getValues(), sb);
                }
                for (FormField f4 : fs) {
                    sb.append(f4.getVariable());
                    sb.append('<');
                    formFieldValuesToCaps(f4.getValues(), sb);
                }
            }
        }
        try {
            byte[] bytes = sb.toString().getBytes(StringUtils.UTF8);
            synchronized (md) {
                digest = md.digest(bytes);
            }
            return new CapsVersionAndHash(Base64.encodeToString(digest), hash2);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    private static void formFieldValuesToCaps(List<CharSequence> i, StringBuilder sb) {
        SortedSet<CharSequence> fvs = new TreeSet<>();
        fvs.addAll(i);
        for (CharSequence fv : fvs) {
            sb.append(fv);
            sb.append('<');
        }
    }
}
