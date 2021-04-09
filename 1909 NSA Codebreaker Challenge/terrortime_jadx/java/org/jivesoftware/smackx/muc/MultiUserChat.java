package org.jivesoftware.smackx.muc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.AsyncButOrdered;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaCollector;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.MessageWithBodiesFilter;
import org.jivesoftware.smack.filter.MessageWithSubjectFilter;
import org.jivesoftware.smack.filter.MessageWithThreadFilter;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PresenceTypeFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.filter.ToMatchesFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jivesoftware.smackx.muc.MucEnterConfiguration.Builder;
import org.jivesoftware.smackx.muc.MultiUserChatException.MissingMucCreationAcknowledgeException;
import org.jivesoftware.smackx.muc.MultiUserChatException.MucAlreadyJoinedException;
import org.jivesoftware.smackx.muc.MultiUserChatException.MucNotJoinedException;
import org.jivesoftware.smackx.muc.MultiUserChatException.NotAMucServiceException;
import org.jivesoftware.smackx.muc.filter.MUCUserStatusCodeFilter;
import org.jivesoftware.smackx.muc.packet.Destroy;
import org.jivesoftware.smackx.muc.packet.MUCAdmin;
import org.jivesoftware.smackx.muc.packet.MUCItem;
import org.jivesoftware.smackx.muc.packet.MUCOwner;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.muc.packet.MUCUser.Decline;
import org.jivesoftware.smackx.muc.packet.MUCUser.Invite;
import org.jivesoftware.smackx.muc.packet.MUCUser.Status;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.util.cache.ExpirationCache;

public class MultiUserChat {
    private static final StanzaFilter DECLINE_FILTER = new AndFilter(MessageTypeFilter.NORMAL, new StanzaExtensionFilter("x", MUCUser.NAMESPACE));
    private static final ExpirationCache<DomainBareJid, Void> KNOWN_MUC_SERVICES = new ExpirationCache<>(100, 86400000);
    private static final Logger LOGGER = Logger.getLogger(MultiUserChat.class.getName());
    /* access modifiers changed from: private */
    public static final AsyncButOrdered<MultiUserChat> asyncButOrdered = new AsyncButOrdered<>();
    private final XMPPConnection connection;
    private final StanzaListener declinesListener;
    private final StanzaFilter fromRoomFilter;
    private final StanzaFilter fromRoomGroupchatFilter;
    private final Set<InvitationRejectionListener> invitationRejectionListeners = new CopyOnWriteArraySet();
    private boolean joined = false;
    private StanzaCollector messageCollector;
    private final StanzaListener messageListener;
    /* access modifiers changed from: private */
    public final Set<MessageListener> messageListeners = new CopyOnWriteArraySet();
    private final MultiUserChatManager multiUserChatManager;
    /* access modifiers changed from: private */
    public EntityFullJid myRoomJid;
    /* access modifiers changed from: private */
    public final Map<EntityFullJid, Presence> occupantsMap = new ConcurrentHashMap();
    /* access modifiers changed from: private */
    public final Set<ParticipantStatusListener> participantStatusListeners = new CopyOnWriteArraySet();
    private final StanzaListener presenceInterceptor;
    /* access modifiers changed from: private */
    public final Set<PresenceListener> presenceInterceptors = new CopyOnWriteArraySet();
    private final StanzaListener presenceListener;
    /* access modifiers changed from: private */
    public final Set<PresenceListener> presenceListeners = new CopyOnWriteArraySet();
    private final EntityBareJid room;
    /* access modifiers changed from: private */
    public String subject;
    private final StanzaListener subjectListener;
    /* access modifiers changed from: private */
    public final Set<SubjectUpdatedListener> subjectUpdatedListeners = new CopyOnWriteArraySet();
    private final Set<UserStatusListener> userStatusListeners = new CopyOnWriteArraySet();

    /* renamed from: org.jivesoftware.smackx.muc.MultiUserChat$7 reason: invalid class name */
    static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$Presence$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$packet$Presence$Type[Type.available.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$Presence$Type[Type.unavailable.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public class MucCreateConfigFormHandle {
        public MucCreateConfigFormHandle() {
        }

        public void makeInstant() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
            MultiUserChat.this.sendConfigurationForm(new Form(DataForm.Type.submit));
        }

        public MucConfigFormManager getConfigFormManager() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
            return MultiUserChat.this.getConfigFormManager();
        }
    }

    MultiUserChat(XMPPConnection connection2, EntityBareJid room2, MultiUserChatManager multiUserChatManager2) {
        this.connection = connection2;
        this.room = room2;
        this.multiUserChatManager = multiUserChatManager2;
        this.fromRoomFilter = FromMatchesFilter.create(room2);
        this.fromRoomGroupchatFilter = new AndFilter(this.fromRoomFilter, MessageTypeFilter.GROUPCHAT);
        this.messageListener = new StanzaListener() {
            public void processStanza(Stanza packet) throws NotConnectedException {
                final Message message = (Message) packet;
                MultiUserChat.asyncButOrdered.performAsyncButOrdered(MultiUserChat.this, new Runnable() {
                    public void run() {
                        for (MessageListener listener : MultiUserChat.this.messageListeners) {
                            listener.processMessage(message);
                        }
                    }
                });
            }
        };
        this.subjectListener = new StanzaListener() {
            public void processStanza(Stanza packet) {
                final Message msg = (Message) packet;
                final EntityFullJid from = msg.getFrom().asEntityFullJidIfPossible();
                MultiUserChat.this.subject = msg.getSubject();
                MultiUserChat.asyncButOrdered.performAsyncButOrdered(MultiUserChat.this, new Runnable() {
                    public void run() {
                        for (SubjectUpdatedListener listener : MultiUserChat.this.subjectUpdatedListeners) {
                            listener.subjectUpdated(msg.getSubject(), from);
                        }
                    }
                });
            }
        };
        this.presenceListener = new StanzaListener() {
            public void processStanza(Stanza packet) {
                Presence presence = (Presence) packet;
                EntityFullJid from = presence.getFrom().asEntityFullJidIfPossible();
                if (from != null) {
                    EntityFullJid myRoomJID = MultiUserChat.this.myRoomJid;
                    boolean isUserStatusModification = presence.getFrom().equals((CharSequence) myRoomJID);
                    AsyncButOrdered access$100 = MultiUserChat.asyncButOrdered;
                    MultiUserChat multiUserChat = MultiUserChat.this;
                    final Presence presence2 = presence;
                    final EntityFullJid entityFullJid = from;
                    final Stanza stanza = packet;
                    final boolean z = isUserStatusModification;
                    final EntityFullJid entityFullJid2 = myRoomJID;
                    AnonymousClass1 r0 = new Runnable() {
                        public void run() {
                            int i = AnonymousClass7.$SwitchMap$org$jivesoftware$smack$packet$Presence$Type[presence2.getType().ordinal()];
                            if (i == 1) {
                                Presence oldPresence = (Presence) MultiUserChat.this.occupantsMap.put(entityFullJid, presence2);
                                if (oldPresence != null) {
                                    MUCUser mucExtension = MUCUser.from(oldPresence);
                                    MUCAffiliation oldAffiliation = mucExtension.getItem().getAffiliation();
                                    MUCRole oldRole = mucExtension.getItem().getRole();
                                    MUCUser mucExtension2 = MUCUser.from(stanza);
                                    MUCAffiliation newAffiliation = mucExtension2.getItem().getAffiliation();
                                    MultiUserChat.this.checkRoleModifications(oldRole, mucExtension2.getItem().getRole(), z, entityFullJid);
                                    MultiUserChat.this.checkAffiliationModifications(oldAffiliation, newAffiliation, z, entityFullJid);
                                } else if (!z) {
                                    for (ParticipantStatusListener listener : MultiUserChat.this.participantStatusListeners) {
                                        listener.joined(entityFullJid);
                                    }
                                }
                            } else if (i == 2) {
                                MultiUserChat.this.occupantsMap.remove(entityFullJid);
                                MUCUser mucUser = MUCUser.from(stanza);
                                if (mucUser != null && mucUser.hasStatus()) {
                                    MultiUserChat.this.checkPresenceCode(mucUser.getStatus(), presence2.getFrom().equals((CharSequence) entityFullJid2), mucUser, entityFullJid);
                                } else if (!z) {
                                    for (ParticipantStatusListener listener2 : MultiUserChat.this.participantStatusListeners) {
                                        listener2.left(entityFullJid);
                                    }
                                }
                            }
                            for (PresenceListener listener3 : MultiUserChat.this.presenceListeners) {
                                listener3.processPresence(presence2);
                            }
                        }
                    };
                    access$100.performAsyncButOrdered(multiUserChat, r0);
                }
            }
        };
        this.declinesListener = new StanzaListener() {
            public void processStanza(Stanza packet) {
                Message message = (Message) packet;
                Decline rejection = MUCUser.from(packet).getDecline();
                if (rejection != null) {
                    MultiUserChat.this.fireInvitationRejectionListeners(message, rejection);
                }
            }
        };
        this.presenceInterceptor = new StanzaListener() {
            public void processStanza(Stanza packet) {
                Presence presence = (Presence) packet;
                for (PresenceListener interceptor : MultiUserChat.this.presenceInterceptors) {
                    interceptor.processPresence(presence);
                }
            }
        };
    }

    public EntityBareJid getRoom() {
        return this.room;
    }

    private Presence enter(MucEnterConfiguration conf) throws NotConnectedException, NoResponseException, XMPPErrorException, InterruptedException, NotAMucServiceException {
        DomainBareJid mucService = this.room.asDomainBareJid();
        if (!KNOWN_MUC_SERVICES.containsKey(mucService)) {
            if (this.multiUserChatManager.providesMucService(mucService)) {
                KNOWN_MUC_SERVICES.put(mucService, null);
            } else {
                throw new NotAMucServiceException(this);
            }
        }
        Presence joinPresence = conf.getJoinPresence(this);
        this.connection.addSyncStanzaListener(this.messageListener, this.fromRoomGroupchatFilter);
        StanzaFilter presenceFromRoomFilter = new AndFilter(this.fromRoomFilter, StanzaTypeFilter.PRESENCE);
        this.connection.addSyncStanzaListener(this.presenceListener, presenceFromRoomFilter);
        this.connection.addSyncStanzaListener(this.subjectListener, new AndFilter(this.fromRoomFilter, MessageWithSubjectFilter.INSTANCE, new NotFilter(MessageTypeFilter.ERROR), new NotFilter(MessageWithBodiesFilter.INSTANCE), new NotFilter(MessageWithThreadFilter.INSTANCE)));
        this.connection.addSyncStanzaListener(this.declinesListener, new AndFilter(this.fromRoomFilter, DECLINE_FILTER));
        this.connection.addStanzaInterceptor(this.presenceInterceptor, new AndFilter(ToMatchesFilter.create(this.room), StanzaTypeFilter.PRESENCE));
        this.messageCollector = this.connection.createStanzaCollector(this.fromRoomGroupchatFilter);
        StanzaFilter responseFilter = new AndFilter(StanzaTypeFilter.PRESENCE, new OrFilter(new AndFilter(FromMatchesFilter.createBare(getRoom()), MUCUserStatusCodeFilter.STATUS_110_PRESENCE_TO_SELF), new AndFilter(FromMatchesFilter.createFull(joinPresence.getTo()), new StanzaIdFilter((Stanza) joinPresence), PresenceTypeFilter.ERROR)));
        StanzaCollector presenceStanzaCollector = null;
        try {
            StanzaCollector selfPresenceCollector = this.connection.createStanzaCollectorAndSend(responseFilter, joinPresence);
            presenceStanzaCollector = this.connection.createStanzaCollector(StanzaCollector.newConfiguration().setCollectorToReset(selfPresenceCollector).setStanzaFilter(presenceFromRoomFilter));
            Presence presence = (Presence) selfPresenceCollector.nextResultOrThrow(conf.getTimeout());
            if (presenceStanzaCollector != null) {
                presenceStanzaCollector.cancel();
            }
            setNickname(presence.getFrom().getResourceOrThrow());
            this.joined = true;
            this.multiUserChatManager.addJoinedRoom(this.room);
            return presence;
        } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e) {
            removeConnectionCallbacks();
            throw e;
        } catch (Throwable th) {
            if (presenceStanzaCollector != null) {
                presenceStanzaCollector.cancel();
            }
            throw th;
        }
    }

    private void setNickname(Resourcepart nickname) {
        this.myRoomJid = JidCreate.entityFullFrom(this.room, nickname);
    }

    public Builder getEnterConfigurationBuilder(Resourcepart nickname) {
        return new Builder(nickname, this.connection.getReplyTimeout());
    }

    public synchronized MucCreateConfigFormHandle create(Resourcepart nickname) throws NoResponseException, XMPPErrorException, InterruptedException, MucAlreadyJoinedException, NotConnectedException, MissingMucCreationAcknowledgeException, NotAMucServiceException {
        MucCreateConfigFormHandle mucCreateConfigFormHandle;
        if (!this.joined) {
            mucCreateConfigFormHandle = createOrJoin(nickname);
            if (mucCreateConfigFormHandle == null) {
                leave();
                throw new MissingMucCreationAcknowledgeException();
            }
        } else {
            throw new MucAlreadyJoinedException();
        }
        return mucCreateConfigFormHandle;
    }

    public synchronized MucCreateConfigFormHandle createOrJoin(Resourcepart nickname) throws NoResponseException, XMPPErrorException, InterruptedException, MucAlreadyJoinedException, NotConnectedException, NotAMucServiceException {
        return createOrJoin(getEnterConfigurationBuilder(nickname).build());
    }

    @Deprecated
    public MucCreateConfigFormHandle createOrJoin(Resourcepart nickname, String password, DiscussionHistory history, long timeout) throws NoResponseException, XMPPErrorException, InterruptedException, MucAlreadyJoinedException, NotConnectedException, NotAMucServiceException {
        return createOrJoin(getEnterConfigurationBuilder(nickname).withPassword(password).timeoutAfter(timeout).build());
    }

    public synchronized MucCreateConfigFormHandle createOrJoin(MucEnterConfiguration mucEnterConfiguration) throws NoResponseException, XMPPErrorException, InterruptedException, MucAlreadyJoinedException, NotConnectedException, NotAMucServiceException {
        if (!this.joined) {
            MUCUser mucUser = MUCUser.from(enter(mucEnterConfiguration));
            if (mucUser == null || !mucUser.getStatus().contains(Status.ROOM_CREATED_201)) {
                return null;
            }
            return new MucCreateConfigFormHandle();
        }
        throw new MucAlreadyJoinedException();
    }

    public MucCreateConfigFormHandle createOrJoinIfNecessary(Resourcepart nickname, String password) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotAMucServiceException {
        if (isJoined()) {
            return null;
        }
        try {
            return createOrJoin(getEnterConfigurationBuilder(nickname).withPassword(password).build());
        } catch (MucAlreadyJoinedException e) {
            return null;
        }
    }

    public void join(Resourcepart nickname) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotAMucServiceException {
        join(getEnterConfigurationBuilder(nickname).build());
    }

    public void join(Resourcepart nickname, String password) throws XMPPErrorException, InterruptedException, NoResponseException, NotConnectedException, NotAMucServiceException {
        join(getEnterConfigurationBuilder(nickname).withPassword(password).build());
    }

    @Deprecated
    public void join(Resourcepart nickname, String password, DiscussionHistory history, long timeout) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException, NotAMucServiceException {
        join(getEnterConfigurationBuilder(nickname).withPassword(password).timeoutAfter(timeout).build());
    }

    public synchronized void join(MucEnterConfiguration mucEnterConfiguration) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException, NotAMucServiceException {
        if (this.joined) {
            try {
                leaveSync();
            } catch (NoResponseException | XMPPErrorException | MucNotJoinedException e) {
                LOGGER.log(Level.WARNING, "Could not leave MUC prior joining, assuming we are not joined", e);
            }
        }
        enter(mucEnterConfiguration);
    }

    public boolean isJoined() {
        return this.joined;
    }

    public synchronized void leave() throws NotConnectedException, InterruptedException {
        userHasLeft();
        EntityFullJid myRoomJid2 = this.myRoomJid;
        if (myRoomJid2 != null) {
            Presence leavePresence = new Presence(Type.unavailable);
            leavePresence.setTo((Jid) myRoomJid2);
            this.connection.sendStanza(leavePresence);
        }
    }

    public synchronized Presence leaveSync() throws NotConnectedException, InterruptedException, NoResponseException, XMPPErrorException, MucNotJoinedException {
        EntityFullJid myRoomJid2;
        Presence leavePresence;
        userHasLeft();
        myRoomJid2 = this.myRoomJid;
        if (myRoomJid2 != null) {
            leavePresence = new Presence(Type.unavailable);
            leavePresence.setTo((Jid) myRoomJid2);
        } else {
            throw new MucNotJoinedException(this);
        }
        return (Presence) this.connection.createStanzaCollectorAndSend(new AndFilter(StanzaTypeFilter.PRESENCE, new StanzaIdFilter((Stanza) leavePresence), new OrFilter(new AndFilter(FromMatchesFilter.createFull(myRoomJid2), PresenceTypeFilter.UNAVAILABLE, MUCUserStatusCodeFilter.STATUS_110_PRESENCE_TO_SELF), new AndFilter(this.fromRoomFilter, PresenceTypeFilter.ERROR))), leavePresence).nextResultOrThrow();
    }

    public MucConfigFormManager getConfigFormManager() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return new MucConfigFormManager(this);
    }

    public Form getConfigurationForm() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCOwner iq = new MUCOwner();
        iq.setTo((Jid) this.room);
        iq.setType(IQ.Type.get);
        return Form.getFormFrom((IQ) this.connection.createStanzaCollectorAndSend(iq).nextResultOrThrow());
    }

    public void sendConfigurationForm(Form form) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCOwner iq = new MUCOwner();
        iq.setTo((Jid) this.room);
        iq.setType(IQ.Type.set);
        iq.addExtension(form.getDataFormToSend());
        this.connection.createStanzaCollectorAndSend(iq).nextResultOrThrow();
    }

    public Form getRegistrationForm() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Registration reg = new Registration();
        reg.setType(IQ.Type.get);
        reg.setTo((Jid) this.room);
        return Form.getFormFrom((IQ) this.connection.createStanzaCollectorAndSend(reg).nextResultOrThrow());
    }

    public void sendRegistrationForm(Form form) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Registration reg = new Registration();
        reg.setType(IQ.Type.set);
        reg.setTo((Jid) this.room);
        reg.addExtension(form.getDataFormToSend());
        this.connection.createStanzaCollectorAndSend(reg).nextResultOrThrow();
    }

    public void destroy(String reason, EntityBareJid alternateJID) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCOwner iq = new MUCOwner();
        iq.setTo((Jid) this.room);
        iq.setType(IQ.Type.set);
        iq.setDestroy(new Destroy(alternateJID, reason));
        try {
            this.connection.createStanzaCollectorAndSend(iq).nextResultOrThrow();
            userHasLeft();
        } catch (XMPPErrorException e) {
            throw e;
        } catch (InterruptedException | NoResponseException | NotConnectedException e2) {
            userHasLeft();
            throw e2;
        }
    }

    public void invite(EntityBareJid user, String reason) throws NotConnectedException, InterruptedException {
        invite(new Message(), user, reason);
    }

    public void invite(Message message, EntityBareJid user, String reason) throws NotConnectedException, InterruptedException {
        message.setTo((Jid) this.room);
        MUCUser mucUser = new MUCUser();
        mucUser.setInvite(new Invite(reason, user));
        message.addExtension(mucUser);
        this.connection.sendStanza(message);
    }

    public boolean addInvitationRejectionListener(InvitationRejectionListener listener) {
        return this.invitationRejectionListeners.add(listener);
    }

    public boolean removeInvitationRejectionListener(InvitationRejectionListener listener) {
        return this.invitationRejectionListeners.remove(listener);
    }

    /* access modifiers changed from: private */
    public void fireInvitationRejectionListeners(Message message, Decline rejection) {
        InvitationRejectionListener[] listeners;
        EntityBareJid invitee = rejection.getFrom();
        String reason = rejection.getReason();
        synchronized (this.invitationRejectionListeners) {
            listeners = new InvitationRejectionListener[this.invitationRejectionListeners.size()];
            this.invitationRejectionListeners.toArray(listeners);
        }
        for (InvitationRejectionListener listener : listeners) {
            listener.invitationDeclined(invitee, reason, message, rejection);
        }
    }

    public boolean addSubjectUpdatedListener(SubjectUpdatedListener listener) {
        return this.subjectUpdatedListeners.add(listener);
    }

    public boolean removeSubjectUpdatedListener(SubjectUpdatedListener listener) {
        return this.subjectUpdatedListeners.remove(listener);
    }

    public void addPresenceInterceptor(PresenceListener presenceInterceptor2) {
        this.presenceInterceptors.add(presenceInterceptor2);
    }

    public void removePresenceInterceptor(PresenceListener presenceInterceptor2) {
        this.presenceInterceptors.remove(presenceInterceptor2);
    }

    public String getSubject() {
        return this.subject;
    }

    public String getReservedNickname() throws SmackException, InterruptedException {
        try {
            Iterator it = ServiceDiscoveryManager.getInstanceFor(this.connection).discoverInfo(this.room, "x-roomuser-item").getIdentities().iterator();
            if (it.hasNext()) {
                return ((Identity) it.next()).getName();
            }
        } catch (XMPPException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving room nickname", e);
        }
        return null;
    }

    public Resourcepart getNickname() {
        EntityFullJid myRoomJid2 = this.myRoomJid;
        if (myRoomJid2 == null) {
            return null;
        }
        return myRoomJid2.getResourcepart();
    }

    public synchronized void changeNickname(Resourcepart nickname) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, MucNotJoinedException {
        Objects.requireNonNull(nickname, "Nickname must not be null or blank.");
        if (this.joined) {
            EntityFullJid jid = JidCreate.fullFrom(this.room, nickname);
            Presence joinPresence = new Presence(Type.available);
            joinPresence.setTo((Jid) jid);
            this.connection.createStanzaCollectorAndSend(new AndFilter(FromMatchesFilter.createFull(jid), new StanzaTypeFilter(Presence.class)), joinPresence).nextResultOrThrow();
            setNickname(nickname);
        } else {
            throw new MucNotJoinedException(this);
        }
    }

    public void changeAvailabilityStatus(String status, Mode mode) throws NotConnectedException, InterruptedException, MucNotJoinedException {
        EntityFullJid myRoomJid2 = this.myRoomJid;
        if (myRoomJid2 == null) {
            throw new MucNotJoinedException(this);
        } else if (this.joined) {
            Presence joinPresence = new Presence(Type.available);
            joinPresence.setStatus(status);
            joinPresence.setMode(mode);
            joinPresence.setTo((Jid) myRoomJid2);
            this.connection.sendStanza(joinPresence);
        } else {
            throw new MucNotJoinedException(this);
        }
    }

    public void kickParticipant(Resourcepart nickname, String reason) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeRole(nickname, MUCRole.none, reason);
    }

    public void requestVoice() throws NotConnectedException, InterruptedException {
        DataForm form = new DataForm(DataForm.Type.submit);
        FormField formTypeField = new FormField(FormField.FORM_TYPE);
        formTypeField.addValue((CharSequence) "http://jabber.org/protocol/muc#request");
        form.addField(formTypeField);
        FormField requestVoiceField = new FormField("muc#role");
        requestVoiceField.setType(FormField.Type.text_single);
        requestVoiceField.setLabel("Requested role");
        requestVoiceField.addValue((CharSequence) "participant");
        form.addField(requestVoiceField);
        Message message = new Message((Jid) this.room);
        message.addExtension(form);
        this.connection.sendStanza(message);
    }

    public void grantVoice(Collection<Resourcepart> nicknames) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeRole(nicknames, MUCRole.participant);
    }

    public void grantVoice(Resourcepart nickname) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeRole(nickname, MUCRole.participant, null);
    }

    public void revokeVoice(Collection<Resourcepart> nicknames) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeRole(nicknames, MUCRole.visitor);
    }

    public void revokeVoice(Resourcepart nickname) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeRole(nickname, MUCRole.visitor, null);
    }

    public void banUsers(Collection<? extends Jid> jids) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jids, MUCAffiliation.outcast);
    }

    public void banUser(Jid jid, String reason) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jid, MUCAffiliation.outcast, reason);
    }

    public void grantMembership(Collection<? extends Jid> jids) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jids, MUCAffiliation.member);
    }

    public void grantMembership(Jid jid) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jid, MUCAffiliation.member, null);
    }

    public void revokeMembership(Collection<? extends Jid> jids) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jids, MUCAffiliation.none);
    }

    public void revokeMembership(Jid jid) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jid, MUCAffiliation.none, null);
    }

    public void grantModerator(Collection<Resourcepart> nicknames) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeRole(nicknames, MUCRole.moderator);
    }

    public void grantModerator(Resourcepart nickname) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeRole(nickname, MUCRole.moderator, null);
    }

    public void revokeModerator(Collection<Resourcepart> nicknames) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeRole(nicknames, MUCRole.participant);
    }

    public void revokeModerator(Resourcepart nickname) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeRole(nickname, MUCRole.participant, null);
    }

    public void grantOwnership(Collection<? extends Jid> jids) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jids, MUCAffiliation.owner);
    }

    public void grantOwnership(Jid jid) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jid, MUCAffiliation.owner, null);
    }

    public void revokeOwnership(Collection<? extends Jid> jids) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jids, MUCAffiliation.admin);
    }

    public void revokeOwnership(Jid jid) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jid, MUCAffiliation.admin, null);
    }

    public void grantAdmin(Collection<? extends Jid> jids) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jids, MUCAffiliation.admin);
    }

    public void grantAdmin(Jid jid) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jid, MUCAffiliation.admin);
    }

    public void revokeAdmin(Collection<? extends Jid> jids) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jids, MUCAffiliation.admin);
    }

    public void revokeAdmin(EntityJid jid) throws XMPPErrorException, NoResponseException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin((Jid) jid, MUCAffiliation.member);
    }

    private void changeAffiliationByAdmin(Jid jid, MUCAffiliation affiliation) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        changeAffiliationByAdmin(jid, affiliation, null);
    }

    private void changeAffiliationByAdmin(Jid jid, MUCAffiliation affiliation, String reason) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCAdmin iq = new MUCAdmin();
        iq.setTo((Jid) this.room);
        iq.setType(IQ.Type.set);
        iq.addItem(new MUCItem(affiliation, jid, reason));
        this.connection.createStanzaCollectorAndSend(iq).nextResultOrThrow();
    }

    private void changeAffiliationByAdmin(Collection<? extends Jid> jids, MUCAffiliation affiliation) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCAdmin iq = new MUCAdmin();
        iq.setTo((Jid) this.room);
        iq.setType(IQ.Type.set);
        for (Jid jid : jids) {
            iq.addItem(new MUCItem(affiliation, jid));
        }
        this.connection.createStanzaCollectorAndSend(iq).nextResultOrThrow();
    }

    private void changeRole(Resourcepart nickname, MUCRole role, String reason) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCAdmin iq = new MUCAdmin();
        iq.setTo((Jid) this.room);
        iq.setType(IQ.Type.set);
        iq.addItem(new MUCItem(role, nickname, reason));
        this.connection.createStanzaCollectorAndSend(iq).nextResultOrThrow();
    }

    private void changeRole(Collection<Resourcepart> nicknames, MUCRole role) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCAdmin iq = new MUCAdmin();
        iq.setTo((Jid) this.room);
        iq.setType(IQ.Type.set);
        for (Resourcepart nickname : nicknames) {
            iq.addItem(new MUCItem(role, nickname));
        }
        this.connection.createStanzaCollectorAndSend(iq).nextResultOrThrow();
    }

    public int getOccupantsCount() {
        return this.occupantsMap.size();
    }

    public List<EntityFullJid> getOccupants() {
        return new ArrayList(this.occupantsMap.keySet());
    }

    public Presence getOccupantPresence(EntityFullJid user) {
        return (Presence) this.occupantsMap.get(user);
    }

    public Occupant getOccupant(EntityFullJid user) {
        Presence presence = getOccupantPresence(user);
        if (presence != null) {
            return new Occupant(presence);
        }
        return null;
    }

    public boolean addParticipantListener(PresenceListener listener) {
        return this.presenceListeners.add(listener);
    }

    public boolean removeParticipantListener(PresenceListener listener) {
        return this.presenceListeners.remove(listener);
    }

    public List<Affiliate> getOwners() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getAffiliatesByAdmin(MUCAffiliation.owner);
    }

    public List<Affiliate> getAdmins() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getAffiliatesByAdmin(MUCAffiliation.admin);
    }

    public List<Affiliate> getMembers() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getAffiliatesByAdmin(MUCAffiliation.member);
    }

    public List<Affiliate> getOutcasts() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getAffiliatesByAdmin(MUCAffiliation.outcast);
    }

    private List<Affiliate> getAffiliatesByAdmin(MUCAffiliation affiliation) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCAdmin iq = new MUCAdmin();
        iq.setTo((Jid) this.room);
        iq.setType(IQ.Type.get);
        iq.addItem(new MUCItem(affiliation));
        MUCAdmin answer = (MUCAdmin) this.connection.createStanzaCollectorAndSend(iq).nextResultOrThrow();
        List<Affiliate> affiliates = new ArrayList<>();
        for (MUCItem mucadminItem : answer.getItems()) {
            affiliates.add(new Affiliate(mucadminItem));
        }
        return affiliates;
    }

    public List<Occupant> getModerators() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getOccupants(MUCRole.moderator);
    }

    public List<Occupant> getParticipants() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getOccupants(MUCRole.participant);
    }

    private List<Occupant> getOccupants(MUCRole role) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCAdmin iq = new MUCAdmin();
        iq.setTo((Jid) this.room);
        iq.setType(IQ.Type.get);
        iq.addItem(new MUCItem(role));
        MUCAdmin answer = (MUCAdmin) this.connection.createStanzaCollectorAndSend(iq).nextResultOrThrow();
        List<Occupant> participants = new ArrayList<>();
        for (MUCItem mucadminItem : answer.getItems()) {
            participants.add(new Occupant(mucadminItem));
        }
        return participants;
    }

    public void sendMessage(String text) throws NotConnectedException, InterruptedException {
        Message message = createMessage();
        message.setBody(text);
        this.connection.sendStanza(message);
    }

    public Chat createPrivateChat(EntityFullJid occupant, ChatMessageListener listener) {
        return ChatManager.getInstanceFor(this.connection).createChat(occupant, listener);
    }

    public Message createMessage() {
        return new Message((Jid) this.room, Message.Type.groupchat);
    }

    public void sendMessage(Message message) throws NotConnectedException, InterruptedException {
        message.setTo((Jid) this.room);
        message.setType(Message.Type.groupchat);
        this.connection.sendStanza(message);
    }

    public Message pollMessage() throws MucNotJoinedException {
        StanzaCollector stanzaCollector = this.messageCollector;
        if (stanzaCollector != null) {
            return (Message) stanzaCollector.pollResult();
        }
        throw new MucNotJoinedException(this);
    }

    public Message nextMessage() throws MucNotJoinedException, InterruptedException {
        StanzaCollector stanzaCollector = this.messageCollector;
        if (stanzaCollector != null) {
            return (Message) stanzaCollector.nextResult();
        }
        throw new MucNotJoinedException(this);
    }

    public Message nextMessage(long timeout) throws MucNotJoinedException, InterruptedException {
        StanzaCollector stanzaCollector = this.messageCollector;
        if (stanzaCollector != null) {
            return (Message) stanzaCollector.nextResult(timeout);
        }
        throw new MucNotJoinedException(this);
    }

    public boolean addMessageListener(MessageListener listener) {
        return this.messageListeners.add(listener);
    }

    public boolean removeMessageListener(MessageListener listener) {
        return this.messageListeners.remove(listener);
    }

    public void changeSubject(final String subject2) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Message message = createMessage();
        message.setSubject(subject2);
        this.connection.createStanzaCollectorAndSend(new AndFilter(this.fromRoomGroupchatFilter, new StanzaFilter() {
            public boolean accept(Stanza packet) {
                return subject2.equals(((Message) packet).getSubject());
            }
        }), message).nextResultOrThrow();
    }

    private void removeConnectionCallbacks() {
        this.connection.removeSyncStanzaListener(this.messageListener);
        this.connection.removeSyncStanzaListener(this.presenceListener);
        this.connection.removeSyncStanzaListener(this.subjectListener);
        this.connection.removeSyncStanzaListener(this.declinesListener);
        this.connection.removeStanzaInterceptor(this.presenceInterceptor);
        StanzaCollector stanzaCollector = this.messageCollector;
        if (stanzaCollector != null) {
            stanzaCollector.cancel();
            this.messageCollector = null;
        }
    }

    private synchronized void userHasLeft() {
        this.occupantsMap.clear();
        this.joined = false;
        this.multiUserChatManager.removeJoinedRoom(this.room);
        removeConnectionCallbacks();
    }

    public boolean addUserStatusListener(UserStatusListener listener) {
        return this.userStatusListeners.add(listener);
    }

    public boolean removeUserStatusListener(UserStatusListener listener) {
        return this.userStatusListeners.remove(listener);
    }

    public boolean addParticipantStatusListener(ParticipantStatusListener listener) {
        return this.participantStatusListeners.add(listener);
    }

    public boolean removeParticipantStatusListener(ParticipantStatusListener listener) {
        return this.participantStatusListeners.remove(listener);
    }

    /* access modifiers changed from: private */
    public void checkRoleModifications(MUCRole oldRole, MUCRole newRole, boolean isUserModification, EntityFullJid from) {
        if ((MUCRole.visitor.equals(oldRole) || MUCRole.none.equals(oldRole)) && MUCRole.participant.equals(newRole)) {
            if (isUserModification) {
                for (UserStatusListener listener : this.userStatusListeners) {
                    listener.voiceGranted();
                }
            } else {
                for (ParticipantStatusListener listener2 : this.participantStatusListeners) {
                    listener2.voiceGranted(from);
                }
            }
        } else if (MUCRole.participant.equals(oldRole) && (MUCRole.visitor.equals(newRole) || MUCRole.none.equals(newRole))) {
            if (isUserModification) {
                for (UserStatusListener listener3 : this.userStatusListeners) {
                    listener3.voiceRevoked();
                }
            } else {
                for (ParticipantStatusListener listener4 : this.participantStatusListeners) {
                    listener4.voiceRevoked(from);
                }
            }
        }
        if (!MUCRole.moderator.equals(oldRole) && MUCRole.moderator.equals(newRole)) {
            if (MUCRole.visitor.equals(oldRole) || MUCRole.none.equals(oldRole)) {
                if (isUserModification) {
                    for (UserStatusListener listener5 : this.userStatusListeners) {
                        listener5.voiceGranted();
                    }
                } else {
                    for (ParticipantStatusListener listener6 : this.participantStatusListeners) {
                        listener6.voiceGranted(from);
                    }
                }
            }
            if (isUserModification) {
                for (UserStatusListener listener7 : this.userStatusListeners) {
                    listener7.moderatorGranted();
                }
                return;
            }
            for (ParticipantStatusListener listener8 : this.participantStatusListeners) {
                listener8.moderatorGranted(from);
            }
        } else if (MUCRole.moderator.equals(oldRole) && !MUCRole.moderator.equals(newRole)) {
            if (MUCRole.visitor.equals(newRole) || MUCRole.none.equals(newRole)) {
                if (isUserModification) {
                    for (UserStatusListener listener9 : this.userStatusListeners) {
                        listener9.voiceRevoked();
                    }
                } else {
                    for (ParticipantStatusListener listener10 : this.participantStatusListeners) {
                        listener10.voiceRevoked(from);
                    }
                }
            }
            if (isUserModification) {
                for (UserStatusListener listener11 : this.userStatusListeners) {
                    listener11.moderatorRevoked();
                }
                return;
            }
            for (ParticipantStatusListener listener12 : this.participantStatusListeners) {
                listener12.moderatorRevoked(from);
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkAffiliationModifications(MUCAffiliation oldAffiliation, MUCAffiliation newAffiliation, boolean isUserModification, EntityFullJid from) {
        if (!MUCAffiliation.owner.equals(oldAffiliation) || MUCAffiliation.owner.equals(newAffiliation)) {
            if (!MUCAffiliation.admin.equals(oldAffiliation) || MUCAffiliation.admin.equals(newAffiliation)) {
                if (MUCAffiliation.member.equals(oldAffiliation) && !MUCAffiliation.member.equals(newAffiliation)) {
                    if (isUserModification) {
                        for (UserStatusListener listener : this.userStatusListeners) {
                            listener.membershipRevoked();
                        }
                    } else {
                        for (ParticipantStatusListener listener2 : this.participantStatusListeners) {
                            listener2.membershipRevoked(from);
                        }
                    }
                }
            } else if (isUserModification) {
                for (UserStatusListener listener3 : this.userStatusListeners) {
                    listener3.adminRevoked();
                }
            } else {
                for (ParticipantStatusListener listener4 : this.participantStatusListeners) {
                    listener4.adminRevoked(from);
                }
            }
        } else if (isUserModification) {
            for (UserStatusListener listener5 : this.userStatusListeners) {
                listener5.ownershipRevoked();
            }
        } else {
            for (ParticipantStatusListener listener6 : this.participantStatusListeners) {
                listener6.ownershipRevoked(from);
            }
        }
        if (MUCAffiliation.owner.equals(oldAffiliation) || !MUCAffiliation.owner.equals(newAffiliation)) {
            if (MUCAffiliation.admin.equals(oldAffiliation) || !MUCAffiliation.admin.equals(newAffiliation)) {
                if (!MUCAffiliation.member.equals(oldAffiliation) && MUCAffiliation.member.equals(newAffiliation)) {
                    if (isUserModification) {
                        for (UserStatusListener listener7 : this.userStatusListeners) {
                            listener7.membershipGranted();
                        }
                        return;
                    }
                    for (ParticipantStatusListener listener8 : this.participantStatusListeners) {
                        listener8.membershipGranted(from);
                    }
                }
            } else if (isUserModification) {
                for (UserStatusListener listener9 : this.userStatusListeners) {
                    listener9.adminGranted();
                }
            } else {
                for (ParticipantStatusListener listener10 : this.participantStatusListeners) {
                    listener10.adminGranted(from);
                }
            }
        } else if (isUserModification) {
            for (UserStatusListener listener11 : this.userStatusListeners) {
                listener11.ownershipGranted();
            }
        } else {
            for (ParticipantStatusListener listener12 : this.participantStatusListeners) {
                listener12.ownershipGranted(from);
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkPresenceCode(Set<Status> statusCodes, boolean isUserModification, MUCUser mucUser, EntityFullJid from) {
        if (statusCodes.contains(Status.KICKED_307)) {
            if (isUserModification) {
                userHasLeft();
                for (UserStatusListener listener : this.userStatusListeners) {
                    listener.kicked(mucUser.getItem().getActor(), mucUser.getItem().getReason());
                }
            } else {
                for (ParticipantStatusListener listener2 : this.participantStatusListeners) {
                    listener2.kicked(from, mucUser.getItem().getActor(), mucUser.getItem().getReason());
                }
            }
        }
        if (statusCodes.contains(Status.BANNED_301)) {
            if (isUserModification) {
                this.joined = false;
                for (UserStatusListener listener3 : this.userStatusListeners) {
                    listener3.banned(mucUser.getItem().getActor(), mucUser.getItem().getReason());
                }
                this.occupantsMap.clear();
                this.myRoomJid = null;
                userHasLeft();
            } else {
                for (ParticipantStatusListener listener4 : this.participantStatusListeners) {
                    listener4.banned(from, mucUser.getItem().getActor(), mucUser.getItem().getReason());
                }
            }
        }
        if (statusCodes.contains(Status.REMOVED_AFFIL_CHANGE_321) && isUserModification) {
            this.joined = false;
            for (UserStatusListener listener5 : this.userStatusListeners) {
                listener5.membershipRevoked();
            }
            this.occupantsMap.clear();
            this.myRoomJid = null;
            userHasLeft();
        }
        if (statusCodes.contains(Status.NEW_NICKNAME_303)) {
            for (ParticipantStatusListener listener6 : this.participantStatusListeners) {
                listener6.nicknameChanged(from, mucUser.getItem().getNick());
            }
        }
        if (mucUser.getDestroy() != null) {
            MultiUserChat alternateMUC = this.multiUserChatManager.getMultiUserChat(mucUser.getDestroy().getJid());
            for (UserStatusListener listener7 : this.userStatusListeners) {
                listener7.roomDestroyed(alternateMUC, mucUser.getDestroy().getReason());
            }
            this.occupantsMap.clear();
            this.myRoomJid = null;
            userHasLeft();
        }
    }

    public XMPPConnection getXmppConnection() {
        return this.connection;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MUC: ");
        sb.append(this.room);
        sb.append("(");
        sb.append(this.connection.getUser());
        sb.append(")");
        return sb.toString();
    }
}
