package org.jivesoftware.smackx.muc;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
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
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.Async;
import org.jivesoftware.smack.util.CleaningWeakReferenceMap;
import org.jivesoftware.smackx.disco.AbstractNodeInformationProvider;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverItems.Item;
import org.jivesoftware.smackx.muc.MultiUserChatException.NotAMucServiceException;
import org.jivesoftware.smackx.muc.packet.MUCInitialPresence;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.muc.packet.MUCUser.Decline;
import org.jivesoftware.smackx.muc.packet.MUCUser.Invite;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;

public final class MultiUserChatManager extends Manager {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String DISCO_NODE = "http://jabber.org/protocol/muc#rooms";
    private static final Map<XMPPConnection, MultiUserChatManager> INSTANCES = new WeakHashMap();
    private static final StanzaFilter INVITATION_FILTER = new AndFilter(StanzaTypeFilter.MESSAGE, new StanzaExtensionFilter((ExtensionElement) new MUCUser()), new NotFilter(MessageTypeFilter.ERROR));
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(MultiUserChatManager.class.getName());
    /* access modifiers changed from: private */
    public AutoJoinFailedCallback autoJoinFailedCallback;
    /* access modifiers changed from: private */
    public boolean autoJoinOnReconnect;
    /* access modifiers changed from: private */
    public final Set<InvitationListener> invitationsListeners = new CopyOnWriteArraySet();
    private final Set<EntityBareJid> joinedRooms = new CopyOnWriteArraySet();
    private final Map<EntityBareJid, WeakReference<MultiUserChat>> multiUserChats = new CleaningWeakReferenceMap();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                ServiceDiscoveryManager.getInstanceFor(connection).addFeature(MUCInitialPresence.NAMESPACE);
                final WeakReference<XMPPConnection> weakRefConnection = new WeakReference<>(connection);
                ServiceDiscoveryManager.getInstanceFor(connection).setNodeInformationProvider(MultiUserChatManager.DISCO_NODE, new AbstractNodeInformationProvider() {
                    public List<Item> getNodeItems() {
                        XMPPConnection connection = (XMPPConnection) weakRefConnection.get();
                        if (connection == null) {
                            return Collections.emptyList();
                        }
                        Set<EntityBareJid> joinedRooms = MultiUserChatManager.getInstanceFor(connection).getJoinedRooms();
                        List<Item> answer = new ArrayList<>();
                        for (EntityBareJid room : joinedRooms) {
                            answer.add(new Item(room));
                        }
                        return answer;
                    }
                });
            }
        });
    }

    public static synchronized MultiUserChatManager getInstanceFor(XMPPConnection connection) {
        MultiUserChatManager multiUserChatManager;
        synchronized (MultiUserChatManager.class) {
            multiUserChatManager = (MultiUserChatManager) INSTANCES.get(connection);
            if (multiUserChatManager == null) {
                multiUserChatManager = new MultiUserChatManager(connection);
                INSTANCES.put(connection, multiUserChatManager);
            }
        }
        return multiUserChatManager;
    }

    private MultiUserChatManager(XMPPConnection connection) {
        super(connection);
        connection.addAsyncStanzaListener(new StanzaListener() {
            public void processStanza(Stanza packet) {
                Message message = (Message) packet;
                MUCUser mucUser = MUCUser.from(message);
                if (mucUser.getInvite() != null) {
                    EntityBareJid mucJid = message.getFrom().asEntityBareJidIfPossible();
                    if (mucJid == null) {
                        Logger access$000 = MultiUserChatManager.LOGGER;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Invite to non bare JID: '");
                        sb.append(message.toXML((String) null));
                        sb.append("'");
                        access$000.warning(sb.toString());
                        return;
                    }
                    MultiUserChat muc = MultiUserChatManager.this.getMultiUserChat(mucJid);
                    XMPPConnection connection = MultiUserChatManager.this.connection();
                    Invite invite = mucUser.getInvite();
                    EntityJid from = invite.getFrom();
                    String reason = invite.getReason();
                    String password = mucUser.getPassword();
                    for (InvitationListener listener : MultiUserChatManager.this.invitationsListeners) {
                        listener.invitationReceived(connection, muc, from, reason, password, message, invite);
                    }
                }
            }
        }, INVITATION_FILTER);
        connection.addConnectionListener(new AbstractConnectionListener() {
            public void authenticated(XMPPConnection connection, boolean resumed) {
                if (!resumed && MultiUserChatManager.this.autoJoinOnReconnect) {
                    final Set<EntityBareJid> mucs = MultiUserChatManager.this.getJoinedRooms();
                    if (!mucs.isEmpty()) {
                        Async.go(new Runnable() {
                            public void run() {
                                String str = "Could not leave room";
                                AutoJoinFailedCallback failedCallback = MultiUserChatManager.this.autoJoinFailedCallback;
                                for (EntityBareJid mucJid : mucs) {
                                    MultiUserChat muc = MultiUserChatManager.this.getMultiUserChat(mucJid);
                                    if (muc.isJoined()) {
                                        Resourcepart nickname = muc.getNickname();
                                        if (nickname != null) {
                                            try {
                                                muc.leave();
                                                try {
                                                    muc.join(nickname);
                                                } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException | NotAMucServiceException e) {
                                                    if (failedCallback != null) {
                                                        failedCallback.autoJoinFailed(muc, e);
                                                    } else {
                                                        MultiUserChatManager.LOGGER.log(Level.WARNING, str, e);
                                                    }
                                                    return;
                                                }
                                            } catch (InterruptedException | NotConnectedException e2) {
                                                if (failedCallback != null) {
                                                    failedCallback.autoJoinFailed(muc, e2);
                                                } else {
                                                    MultiUserChatManager.LOGGER.log(Level.WARNING, str, e2);
                                                }
                                                return;
                                            }
                                        } else {
                                            return;
                                        }
                                    } else {
                                        return;
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public synchronized MultiUserChat getMultiUserChat(EntityBareJid jid) {
        WeakReference<MultiUserChat> weakRefMultiUserChat = (WeakReference) this.multiUserChats.get(jid);
        if (weakRefMultiUserChat == null) {
            return createNewMucAndAddToMap(jid);
        }
        MultiUserChat multiUserChat = (MultiUserChat) weakRefMultiUserChat.get();
        if (multiUserChat != null) {
            return multiUserChat;
        }
        return createNewMucAndAddToMap(jid);
    }

    private MultiUserChat createNewMucAndAddToMap(EntityBareJid jid) {
        MultiUserChat multiUserChat = new MultiUserChat(connection(), jid, this);
        this.multiUserChats.put(jid, new WeakReference(multiUserChat));
        return multiUserChat;
    }

    public boolean isServiceEnabled(Jid user) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(user, MUCInitialPresence.NAMESPACE);
    }

    public Set<EntityBareJid> getJoinedRooms() {
        return Collections.unmodifiableSet(this.joinedRooms);
    }

    public List<EntityBareJid> getJoinedRooms(EntityJid user) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        List<Item> items = ServiceDiscoveryManager.getInstanceFor(connection()).discoverItems(user, DISCO_NODE).getItems();
        List<EntityBareJid> answer = new ArrayList<>(items.size());
        for (Item item : items) {
            EntityBareJid muc = item.getEntityID().asEntityBareJidIfPossible();
            if (muc == null) {
                Logger logger = LOGGER;
                StringBuilder sb = new StringBuilder();
                sb.append("Not a bare JID: ");
                sb.append(item.getEntityID());
                logger.warning(sb.toString());
            } else {
                answer.add(muc);
            }
        }
        return answer;
    }

    public RoomInfo getRoomInfo(EntityBareJid room) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return new RoomInfo(ServiceDiscoveryManager.getInstanceFor(connection()).discoverInfo(room));
    }

    public List<DomainBareJid> getMucServiceDomains() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).findServices(MUCInitialPresence.NAMESPACE, false, false);
    }

    @Deprecated
    public List<DomainBareJid> getXMPPServiceDomains() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getMucServiceDomains();
    }

    public boolean providesMucService(DomainBareJid domainBareJid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(domainBareJid, MUCInitialPresence.NAMESPACE);
    }

    @Deprecated
    public List<HostedRoom> getHostedRooms(DomainBareJid serviceName) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotAMucServiceException {
        return new ArrayList(getRoomsHostedBy(serviceName).values());
    }

    public Map<EntityBareJid, HostedRoom> getRoomsHostedBy(DomainBareJid serviceName) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotAMucServiceException {
        if (providesMucService(serviceName)) {
            List<Item> items = ServiceDiscoveryManager.getInstanceFor(connection()).discoverItems(serviceName).getItems();
            Map<EntityBareJid, HostedRoom> answer = new HashMap<>(items.size());
            for (Item item : items) {
                HostedRoom hostedRoom = new HostedRoom(item);
                HostedRoom hostedRoom2 = (HostedRoom) answer.put(hostedRoom.getJid(), hostedRoom);
            }
            return answer;
        }
        throw new NotAMucServiceException(serviceName);
    }

    public void decline(EntityBareJid room, EntityBareJid inviter, String reason) throws NotConnectedException, InterruptedException {
        Message message = new Message((Jid) room);
        MUCUser mucUser = new MUCUser();
        mucUser.setDecline(new Decline(reason, inviter));
        message.addExtension(mucUser);
        connection().sendStanza(message);
    }

    public void addInvitationListener(InvitationListener listener) {
        this.invitationsListeners.add(listener);
    }

    public void removeInvitationListener(InvitationListener listener) {
        this.invitationsListeners.remove(listener);
    }

    public void setAutoJoinOnReconnect(boolean autoJoin) {
        this.autoJoinOnReconnect = autoJoin;
    }

    public void setAutoJoinFailedCallback(AutoJoinFailedCallback failedCallback) {
        this.autoJoinFailedCallback = failedCallback;
        if (failedCallback != null) {
            setAutoJoinOnReconnect(true);
        }
    }

    /* access modifiers changed from: 0000 */
    public void addJoinedRoom(EntityBareJid room) {
        this.joinedRooms.add(room);
    }

    /* access modifiers changed from: 0000 */
    public void removeJoinedRoom(EntityBareJid room) {
        this.joinedRooms.remove(room);
    }
}
