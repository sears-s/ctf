package org.jivesoftware.smackx.privacy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
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
import org.jivesoftware.smack.filter.IQResultReplyFilter;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.privacy.filter.SetActiveListFilter;
import org.jivesoftware.smackx.privacy.filter.SetDefaultListFilter;
import org.jivesoftware.smackx.privacy.packet.Privacy;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;

public final class PrivacyListManager extends Manager {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Map<XMPPConnection, PrivacyListManager> INSTANCES = new WeakHashMap();
    public static final String NAMESPACE = "jabber:iq:privacy";
    public static final StanzaFilter PRIVACY_FILTER = new StanzaTypeFilter(Privacy.class);
    private static final StanzaFilter PRIVACY_RESULT = new AndFilter(IQTypeFilter.RESULT, PRIVACY_FILTER);
    /* access modifiers changed from: private */
    public volatile String cachedActiveListName;
    /* access modifiers changed from: private */
    public volatile String cachedDefaultListName;
    /* access modifiers changed from: private */
    public final Set<PrivacyListListener> listeners = new CopyOnWriteArraySet();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                PrivacyListManager.getInstanceFor(connection);
            }
        });
    }

    private PrivacyListManager(XMPPConnection connection) {
        super(connection);
        AnonymousClass2 r1 = new AbstractIqRequestHandler("query", "jabber:iq:privacy", Type.set, Mode.sync) {
            public IQ handleIQRequest(IQ iqRequest) {
                Privacy privacy = (Privacy) iqRequest;
                for (PrivacyListListener listener : PrivacyListManager.this.listeners) {
                    for (Entry<String, List<PrivacyItem>> entry : privacy.getItemLists().entrySet()) {
                        String listName = (String) entry.getKey();
                        List<PrivacyItem> items = (List) entry.getValue();
                        if (items.isEmpty()) {
                            listener.updatedPrivacyList(listName);
                        } else {
                            listener.setPrivacyList(listName, items);
                        }
                    }
                }
                return IQ.createResultIQ(privacy);
            }
        };
        connection.registerIQRequestHandler(r1);
        connection.addStanzaSendingListener(new StanzaListener() {
            public void processStanza(Stanza packet) throws NotConnectedException {
                XMPPConnection connection = PrivacyListManager.this.connection();
                Privacy privacy = (Privacy) packet;
                StanzaFilter iqResultReplyFilter = new IQResultReplyFilter(privacy, connection);
                final String activeListName = privacy.getActiveName();
                final boolean declinceActiveList = privacy.isDeclineActiveList();
                connection.addOneTimeSyncCallback(new StanzaListener() {
                    public void processStanza(Stanza packet) throws NotConnectedException {
                        if (declinceActiveList) {
                            PrivacyListManager.this.cachedActiveListName = null;
                        } else {
                            PrivacyListManager.this.cachedActiveListName = activeListName;
                        }
                    }
                }, iqResultReplyFilter);
            }
        }, SetActiveListFilter.INSTANCE);
        connection.addStanzaSendingListener(new StanzaListener() {
            public void processStanza(Stanza packet) throws NotConnectedException {
                XMPPConnection connection = PrivacyListManager.this.connection();
                Privacy privacy = (Privacy) packet;
                StanzaFilter iqResultReplyFilter = new IQResultReplyFilter(privacy, connection);
                final String defaultListName = privacy.getDefaultName();
                final boolean declinceDefaultList = privacy.isDeclineDefaultList();
                connection.addOneTimeSyncCallback(new StanzaListener() {
                    public void processStanza(Stanza packet) throws NotConnectedException {
                        if (declinceDefaultList) {
                            PrivacyListManager.this.cachedDefaultListName = null;
                        } else {
                            PrivacyListManager.this.cachedDefaultListName = defaultListName;
                        }
                    }
                }, iqResultReplyFilter);
            }
        }, SetDefaultListFilter.INSTANCE);
        connection.addSyncStanzaListener(new StanzaListener() {
            public void processStanza(Stanza packet) throws NotConnectedException {
                Privacy privacy = (Privacy) packet;
                String activeList = privacy.getActiveName();
                if (activeList != null) {
                    PrivacyListManager.this.cachedActiveListName = activeList;
                }
                String defaultList = privacy.getDefaultName();
                if (defaultList != null) {
                    PrivacyListManager.this.cachedDefaultListName = defaultList;
                }
            }
        }, PRIVACY_RESULT);
        connection.addConnectionListener(new AbstractConnectionListener() {
            public void authenticated(XMPPConnection connection, boolean resumed) {
                if (!resumed) {
                    PrivacyListManager privacyListManager = PrivacyListManager.this;
                    privacyListManager.cachedActiveListName = privacyListManager.cachedDefaultListName = null;
                }
            }
        });
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature("jabber:iq:privacy");
    }

    public static synchronized PrivacyListManager getInstanceFor(XMPPConnection connection) {
        PrivacyListManager plm;
        synchronized (PrivacyListManager.class) {
            plm = (PrivacyListManager) INSTANCES.get(connection);
            if (plm == null) {
                plm = new PrivacyListManager(connection);
                INSTANCES.put(connection, plm);
            }
        }
        return plm;
    }

    private Privacy getRequest(Privacy requestPrivacy) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        requestPrivacy.setType(Type.get);
        return (Privacy) connection().createStanzaCollectorAndSend(requestPrivacy).nextResultOrThrow();
    }

    private Stanza setRequest(Privacy requestPrivacy) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        requestPrivacy.setType(Type.set);
        return connection().createStanzaCollectorAndSend(requestPrivacy).nextResultOrThrow();
    }

    private Privacy getPrivacyWithListNames() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getRequest(new Privacy());
    }

    public PrivacyList getActiveList() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Privacy privacyAnswer = getPrivacyWithListNames();
        String listName = privacyAnswer.getActiveName();
        if (StringUtils.isNullOrEmpty((CharSequence) listName)) {
            return null;
        }
        return new PrivacyList(true, listName != null && listName.equals(privacyAnswer.getDefaultName()), listName, getPrivacyListItems(listName));
    }

    public String getActiveListName() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (this.cachedActiveListName != null) {
            return this.cachedActiveListName;
        }
        return getPrivacyWithListNames().getActiveName();
    }

    public PrivacyList getDefaultList() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Privacy privacyAnswer = getPrivacyWithListNames();
        String listName = privacyAnswer.getDefaultName();
        if (StringUtils.isNullOrEmpty((CharSequence) listName)) {
            return null;
        }
        return new PrivacyList(listName.equals(privacyAnswer.getActiveName()), true, listName, getPrivacyListItems(listName));
    }

    public String getDefaultListName() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (this.cachedDefaultListName != null) {
            return this.cachedDefaultListName;
        }
        return getPrivacyWithListNames().getDefaultName();
    }

    public String getEffectiveListName() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        String activeListName = getActiveListName();
        if (activeListName != null) {
            return activeListName;
        }
        return getDefaultListName();
    }

    private List<PrivacyItem> getPrivacyListItems(String listName) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Privacy request = new Privacy();
        request.setPrivacyList(listName, new ArrayList());
        return getRequest(request).getPrivacyList(listName);
    }

    public PrivacyList getPrivacyList(String listName) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        String listName2 = (String) StringUtils.requireNotNullOrEmpty(listName, "List name must not be null");
        return new PrivacyList(false, false, listName2, getPrivacyListItems(listName2));
    }

    public List<PrivacyList> getPrivacyLists() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Privacy privacyAnswer = getPrivacyWithListNames();
        Set<String> names = privacyAnswer.getPrivacyListNames();
        List<PrivacyList> lists = new ArrayList<>(names.size());
        for (String listName : names) {
            lists.add(new PrivacyList(listName.equals(privacyAnswer.getActiveName()), listName.equals(privacyAnswer.getDefaultName()), listName, getPrivacyListItems(listName)));
        }
        return lists;
    }

    public void setActiveListName(String listName) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Privacy request = new Privacy();
        request.setActiveName(listName);
        setRequest(request);
    }

    public void declineActiveList() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Privacy request = new Privacy();
        request.setDeclineActiveList(true);
        setRequest(request);
    }

    public void setDefaultListName(String listName) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Privacy request = new Privacy();
        request.setDefaultName(listName);
        setRequest(request);
    }

    public void declineDefaultList() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Privacy request = new Privacy();
        request.setDeclineDefaultList(true);
        setRequest(request);
    }

    public void createPrivacyList(String listName, List<PrivacyItem> privacyItems) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        updatePrivacyList(listName, privacyItems);
    }

    public void updatePrivacyList(String listName, List<PrivacyItem> privacyItems) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Privacy request = new Privacy();
        request.setPrivacyList(listName, privacyItems);
        setRequest(request);
    }

    public void deletePrivacyList(String listName) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Privacy request = new Privacy();
        request.setPrivacyList(listName, new ArrayList());
        setRequest(request);
    }

    public boolean addListener(PrivacyListListener listener) {
        return this.listeners.add(listener);
    }

    public boolean removeListener(PrivacyListListener listener) {
        return this.listeners.remove(listener);
    }

    public boolean isSupported() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).serverSupportsFeature("jabber:iq:privacy");
    }
}
