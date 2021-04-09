package org.jivesoftware.smackx.disco;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.disco.packet.DiscoverItems.Item;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.util.cache.Cache;
import org.jxmpp.util.cache.ExpirationCache;

public final class ServiceDiscoveryManager extends Manager {
    private static final String DEFAULT_IDENTITY_CATEGORY = "client";
    private static final String DEFAULT_IDENTITY_NAME = "Smack";
    private static final String DEFAULT_IDENTITY_TYPE = "pc";
    private static Identity defaultIdentity = new Identity(DEFAULT_IDENTITY_CATEGORY, DEFAULT_IDENTITY_NAME, DEFAULT_IDENTITY_TYPE);
    private static final List<DiscoInfoLookupShortcutMechanism> discoInfoLookupShortcutMechanisms = new ArrayList(2);
    private static final Map<XMPPConnection, ServiceDiscoveryManager> instances = new WeakHashMap();
    private final Set<EntityCapabilitiesChangedListener> entityCapabilitiesChangedListeners = new CopyOnWriteArraySet();
    private DataForm extendedInfo = null;
    private final Set<String> features = new HashSet();
    private final Set<Identity> identities = new HashSet();
    private Identity identity = defaultIdentity;
    private final Map<String, NodeInformationProvider> nodeInformationProviders = new ConcurrentHashMap();
    private final Cache<String, List<DiscoverInfo>> services = new ExpirationCache(25, 86400000);

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                ServiceDiscoveryManager.getInstanceFor(connection);
            }
        });
    }

    public static void setDefaultIdentity(Identity identity2) {
        defaultIdentity = identity2;
    }

    private ServiceDiscoveryManager(XMPPConnection connection) {
        super(connection);
        addFeature(DiscoverInfo.NAMESPACE);
        addFeature(DiscoverItems.NAMESPACE);
        AnonymousClass2 r1 = new AbstractIqRequestHandler("query", DiscoverItems.NAMESPACE, Type.get, Mode.async) {
            public IQ handleIQRequest(IQ iqRequest) {
                DiscoverItems discoverItems = (DiscoverItems) iqRequest;
                DiscoverItems response = new DiscoverItems();
                response.setType(Type.result);
                response.setTo(discoverItems.getFrom());
                response.setStanzaId(discoverItems.getStanzaId());
                response.setNode(discoverItems.getNode());
                NodeInformationProvider nodeInformationProvider = ServiceDiscoveryManager.this.getNodeInformationProvider(discoverItems.getNode());
                if (nodeInformationProvider != null) {
                    response.addItems(nodeInformationProvider.getNodeItems());
                    response.addExtensions(nodeInformationProvider.getNodePacketExtensions());
                } else if (discoverItems.getNode() != null) {
                    response.setType(Type.error);
                    response.setError(StanzaError.getBuilder(Condition.item_not_found));
                }
                return response;
            }
        };
        connection.registerIQRequestHandler(r1);
        AnonymousClass3 r7 = new AbstractIqRequestHandler("query", DiscoverInfo.NAMESPACE, Type.get, Mode.async) {
            public IQ handleIQRequest(IQ iqRequest) {
                DiscoverInfo discoverInfo = (DiscoverInfo) iqRequest;
                DiscoverInfo response = new DiscoverInfo();
                response.setType(Type.result);
                response.setTo(discoverInfo.getFrom());
                response.setStanzaId(discoverInfo.getStanzaId());
                response.setNode(discoverInfo.getNode());
                if (discoverInfo.getNode() == null) {
                    ServiceDiscoveryManager.this.addDiscoverInfoTo(response);
                } else {
                    NodeInformationProvider nodeInformationProvider = ServiceDiscoveryManager.this.getNodeInformationProvider(discoverInfo.getNode());
                    if (nodeInformationProvider != null) {
                        response.addFeatures(nodeInformationProvider.getNodeFeatures());
                        response.addIdentities(nodeInformationProvider.getNodeIdentities());
                        response.addExtensions(nodeInformationProvider.getNodePacketExtensions());
                    } else {
                        response.setType(Type.error);
                        response.setError(StanzaError.getBuilder(Condition.item_not_found));
                    }
                }
                return response;
            }
        };
        connection.registerIQRequestHandler(r7);
    }

    public String getIdentityName() {
        return this.identity.getName();
    }

    public synchronized void setIdentity(Identity identity2) {
        this.identity = (Identity) Objects.requireNonNull(identity2, "Identity can not be null");
        renewEntityCapsVersion();
    }

    public Identity getIdentity() {
        return this.identity;
    }

    public String getIdentityType() {
        return this.identity.getType();
    }

    public synchronized void addIdentity(Identity identity2) {
        this.identities.add(identity2);
        renewEntityCapsVersion();
    }

    public synchronized boolean removeIdentity(Identity identity2) {
        if (identity2.equals(this.identity)) {
            return false;
        }
        this.identities.remove(identity2);
        renewEntityCapsVersion();
        return true;
    }

    public Set<Identity> getIdentities() {
        Set<Identity> res = new HashSet<>(this.identities);
        res.add(this.identity);
        return Collections.unmodifiableSet(res);
    }

    public static synchronized ServiceDiscoveryManager getInstanceFor(XMPPConnection connection) {
        ServiceDiscoveryManager sdm;
        synchronized (ServiceDiscoveryManager.class) {
            sdm = (ServiceDiscoveryManager) instances.get(connection);
            if (sdm == null) {
                sdm = new ServiceDiscoveryManager(connection);
                instances.put(connection, sdm);
            }
        }
        return sdm;
    }

    public synchronized void addDiscoverInfoTo(DiscoverInfo response) {
        response.addIdentities(getIdentities());
        for (String feature : getFeatures()) {
            response.addFeature(feature);
        }
        response.addExtension(this.extendedInfo);
    }

    /* access modifiers changed from: private */
    public NodeInformationProvider getNodeInformationProvider(String node) {
        if (node == null) {
            return null;
        }
        return (NodeInformationProvider) this.nodeInformationProviders.get(node);
    }

    public void setNodeInformationProvider(String node, NodeInformationProvider listener) {
        this.nodeInformationProviders.put(node, listener);
    }

    public void removeNodeInformationProvider(String node) {
        this.nodeInformationProviders.remove(node);
    }

    public synchronized List<String> getFeatures() {
        return new ArrayList(this.features);
    }

    public synchronized void addFeature(String feature) {
        this.features.add(feature);
        renewEntityCapsVersion();
    }

    public synchronized void removeFeature(String feature) {
        this.features.remove(feature);
        renewEntityCapsVersion();
    }

    public synchronized boolean includesFeature(String feature) {
        return this.features.contains(feature);
    }

    public synchronized void setExtendedInfo(DataForm info) {
        this.extendedInfo = info;
        renewEntityCapsVersion();
    }

    public DataForm getExtendedInfo() {
        return this.extendedInfo;
    }

    public List<ExtensionElement> getExtendedInfoAsList() {
        if (this.extendedInfo == null) {
            return null;
        }
        List<ExtensionElement> res = new ArrayList<>(1);
        res.add(this.extendedInfo);
        return res;
    }

    public synchronized void removeExtendedInfo() {
        this.extendedInfo = null;
        renewEntityCapsVersion();
    }

    public DiscoverInfo discoverInfo(Jid entityID) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (entityID == null) {
            return discoverInfo(null, null);
        }
        synchronized (discoInfoLookupShortcutMechanisms) {
            for (DiscoInfoLookupShortcutMechanism discoInfoLookupShortcutMechanism : discoInfoLookupShortcutMechanisms) {
                DiscoverInfo info = discoInfoLookupShortcutMechanism.getDiscoverInfoByUser(this, entityID);
                if (info != null) {
                    return info;
                }
            }
            return discoverInfo(entityID, null);
        }
    }

    public DiscoverInfo discoverInfo(Jid entityID, String node) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        DiscoverInfo disco = new DiscoverInfo();
        disco.setType(Type.get);
        disco.setTo(entityID);
        disco.setNode(node);
        return (DiscoverInfo) connection().createStanzaCollectorAndSend(disco).nextResultOrThrow();
    }

    public DiscoverItems discoverItems(Jid entityID) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return discoverItems(entityID, null);
    }

    public DiscoverItems discoverItems(Jid entityID, String node) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        DiscoverItems disco = new DiscoverItems();
        disco.setType(Type.get);
        disco.setTo(entityID);
        disco.setNode(node);
        return (DiscoverItems) connection().createStanzaCollectorAndSend(disco).nextResultOrThrow();
    }

    @Deprecated
    public boolean canPublishItems(Jid entityID) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return canPublishItems(discoverInfo(entityID));
    }

    @Deprecated
    public static boolean canPublishItems(DiscoverInfo info) {
        return info.containsFeature("http://jabber.org/protocol/disco#publish");
    }

    @Deprecated
    public void publishItems(Jid entityID, DiscoverItems discoverItems) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        publishItems(entityID, null, discoverItems);
    }

    @Deprecated
    public void publishItems(Jid entityID, String node, DiscoverItems discoverItems) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        discoverItems.setType(Type.set);
        discoverItems.setTo(entityID);
        discoverItems.setNode(node);
        connection().createStanzaCollectorAndSend(discoverItems).nextResultOrThrow();
    }

    public boolean serverSupportsFeature(CharSequence feature) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return serverSupportsFeatures(feature);
    }

    public boolean serverSupportsFeatures(CharSequence... features2) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return serverSupportsFeatures((Collection<? extends CharSequence>) Arrays.asList(features2));
    }

    public boolean serverSupportsFeatures(Collection<? extends CharSequence> features2) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return supportsFeatures((Jid) connection().getXMPPServiceDomain(), features2);
    }

    public boolean accountSupportsFeatures(CharSequence... features2) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return accountSupportsFeatures((Collection<? extends CharSequence>) Arrays.asList(features2));
    }

    public boolean accountSupportsFeatures(Collection<? extends CharSequence> features2) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return supportsFeatures((Jid) connection().getUser().asEntityBareJid(), features2);
    }

    public boolean supportsFeature(Jid jid, CharSequence feature) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return supportsFeatures(jid, feature);
    }

    public boolean supportsFeatures(Jid jid, CharSequence... features2) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return supportsFeatures(jid, (Collection<? extends CharSequence>) Arrays.asList(features2));
    }

    public boolean supportsFeatures(Jid jid, Collection<? extends CharSequence> features2) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        DiscoverInfo result = discoverInfo(jid);
        for (CharSequence feature : features2) {
            if (!result.containsFeature(feature)) {
                return false;
            }
        }
        return true;
    }

    public List<DiscoverInfo> findServicesDiscoverInfo(String feature, boolean stopOnFirst, boolean useCache) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return findServicesDiscoverInfo(feature, stopOnFirst, useCache, null);
    }

    public List<DiscoverInfo> findServicesDiscoverInfo(String feature, boolean stopOnFirst, boolean useCache, Map<? super Jid, Exception> encounteredExceptions) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return findServicesDiscoverInfo(connection().getXMPPServiceDomain(), feature, stopOnFirst, useCache, encounteredExceptions);
    }

    public List<DiscoverInfo> findServicesDiscoverInfo(DomainBareJid serviceName, String feature, boolean stopOnFirst, boolean useCache, Map<? super Jid, Exception> encounteredExceptions) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (useCache) {
            List<DiscoverInfo> serviceDiscoInfo = (List) this.services.lookup(feature);
            if (serviceDiscoInfo != null) {
                return serviceDiscoInfo;
            }
        }
        List<DiscoverInfo> serviceDiscoInfo2 = new LinkedList<>();
        try {
            DiscoverInfo info = discoverInfo(serviceName);
            if (info.containsFeature(feature)) {
                serviceDiscoInfo2.add(info);
                if (stopOnFirst) {
                    if (useCache) {
                        this.services.put(feature, serviceDiscoInfo2);
                    }
                    return serviceDiscoInfo2;
                }
            }
            try {
                for (Item item : discoverItems(serviceName).getItems()) {
                    Jid address = item.getEntityID();
                    try {
                        DiscoverInfo info2 = discoverInfo(address);
                        if (info2.containsFeature(feature)) {
                            serviceDiscoInfo2.add(info2);
                            if (stopOnFirst) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    } catch (NoResponseException | XMPPErrorException e) {
                        if (encounteredExceptions != null) {
                            encounteredExceptions.put(address, e);
                        }
                    }
                }
                if (useCache) {
                    this.services.put(feature, serviceDiscoInfo2);
                }
                return serviceDiscoInfo2;
            } catch (XMPPErrorException e2) {
                if (encounteredExceptions != null) {
                    encounteredExceptions.put(serviceName, e2);
                }
                return serviceDiscoInfo2;
            }
        } catch (XMPPErrorException e3) {
            if (encounteredExceptions != null) {
                encounteredExceptions.put(serviceName, e3);
            }
            return serviceDiscoInfo2;
        }
    }

    public List<DomainBareJid> findServices(String feature, boolean stopOnFirst, boolean useCache) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        List<DiscoverInfo> services2 = findServicesDiscoverInfo(feature, stopOnFirst, useCache);
        List<DomainBareJid> res = new ArrayList<>(services2.size());
        for (DiscoverInfo info : services2) {
            res.add(info.getFrom().asDomainBareJid());
        }
        return res;
    }

    public DomainBareJid findService(String feature, boolean useCache, String category, String type) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        boolean noCategory = StringUtils.isNullOrEmpty((CharSequence) category);
        boolean noType = StringUtils.isNullOrEmpty((CharSequence) type);
        if (noType == noCategory) {
            List<DiscoverInfo> services2 = findServicesDiscoverInfo(feature, false, useCache);
            if (services2.isEmpty()) {
                return null;
            }
            if (!noCategory && !noType) {
                for (DiscoverInfo info : services2) {
                    if (info.hasIdentity(category, type)) {
                        return info.getFrom().asDomainBareJid();
                    }
                }
            }
            return ((DiscoverInfo) services2.get(0)).getFrom().asDomainBareJid();
        }
        throw new IllegalArgumentException("Must specify either both, category and type, or none");
    }

    public DomainBareJid findService(String feature, boolean useCache) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return findService(feature, useCache, null, null);
    }

    public boolean addEntityCapabilitiesChangedListener(EntityCapabilitiesChangedListener entityCapabilitiesChangedListener) {
        return this.entityCapabilitiesChangedListeners.add(entityCapabilitiesChangedListener);
    }

    private void renewEntityCapsVersion() {
        for (EntityCapabilitiesChangedListener entityCapabilitiesChangedListener : this.entityCapabilitiesChangedListeners) {
            entityCapabilitiesChangedListener.onEntityCapailitiesChanged();
        }
    }

    public static void addDiscoInfoLookupShortcutMechanism(DiscoInfoLookupShortcutMechanism discoInfoLookupShortcutMechanism) {
        synchronized (discoInfoLookupShortcutMechanisms) {
            discoInfoLookupShortcutMechanisms.add(discoInfoLookupShortcutMechanism);
            Collections.sort(discoInfoLookupShortcutMechanisms);
        }
    }

    public static void removeDiscoInfoLookupShortcutMechanism(DiscoInfoLookupShortcutMechanism discoInfoLookupShortcutMechanism) {
        synchronized (discoInfoLookupShortcutMechanisms) {
            discoInfoLookupShortcutMechanisms.remove(discoInfoLookupShortcutMechanism);
        }
    }
}
