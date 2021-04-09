package org.jivesoftware.smackx.muclight;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaCollector;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.muclight.element.MUCLightAffiliationsIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightChangeAffiliationsIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightConfigurationIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightCreateIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightDestroyIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightGetAffiliationsIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightGetConfigsIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightGetInfoIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightInfoIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightSetConfigsIQ;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;

public class MultiUserChatLight {
    public static final String AFFILIATIONS = "#affiliations";
    public static final String BLOCKING = "#blocking";
    public static final String CONFIGURATION = "#configuration";
    public static final String CREATE = "#create";
    public static final String DESTROY = "#destroy";
    public static final String INFO = "#info";
    public static final String NAMESPACE = "urn:xmpp:muclight:0";
    private final XMPPConnection connection;
    private final StanzaFilter fromRoomFilter;
    private final StanzaFilter fromRoomGroupChatFilter;
    private StanzaCollector messageCollector;
    private final StanzaListener messageListener;
    /* access modifiers changed from: private */
    public final Set<MessageListener> messageListeners = new CopyOnWriteArraySet();
    private final EntityJid room;

    MultiUserChatLight(XMPPConnection connection2, EntityJid room2) {
        this.connection = connection2;
        this.room = room2;
        this.fromRoomFilter = FromMatchesFilter.create(room2);
        this.fromRoomGroupChatFilter = new AndFilter(this.fromRoomFilter, MessageTypeFilter.GROUPCHAT);
        this.messageListener = new StanzaListener() {
            public void processStanza(Stanza packet) throws NotConnectedException {
                Message message = (Message) packet;
                for (MessageListener listener : MultiUserChatLight.this.messageListeners) {
                    listener.processMessage(message);
                }
            }
        };
        connection2.addSyncStanzaListener(this.messageListener, this.fromRoomGroupChatFilter);
    }

    public EntityJid getRoom() {
        return this.room;
    }

    public void sendMessage(String text) throws NotConnectedException, InterruptedException {
        Message message = createMessage();
        message.setBody(text);
        this.connection.sendStanza(message);
    }

    @Deprecated
    public Chat createPrivateChat(EntityJid occupant, ChatMessageListener listener) {
        return ChatManager.getInstanceFor(this.connection).createChat(occupant, listener);
    }

    public Message createMessage() {
        return new Message((Jid) this.room, Type.groupchat);
    }

    public void sendMessage(Message message) throws NotConnectedException, InterruptedException {
        message.setTo((Jid) this.room);
        message.setType(Type.groupchat);
        this.connection.sendStanza(message);
    }

    public Message pollMessage() {
        return (Message) this.messageCollector.pollResult();
    }

    public Message nextMessage() throws InterruptedException {
        return (Message) this.messageCollector.nextResult();
    }

    public Message nextMessage(long timeout) throws InterruptedException {
        return (Message) this.messageCollector.nextResult(timeout);
    }

    public boolean addMessageListener(MessageListener listener) {
        return this.messageListeners.add(listener);
    }

    public boolean removeMessageListener(MessageListener listener) {
        return this.messageListeners.remove(listener);
    }

    private void removeConnectionCallbacks() {
        this.connection.removeSyncStanzaListener(this.messageListener);
        StanzaCollector stanzaCollector = this.messageCollector;
        if (stanzaCollector != null) {
            stanzaCollector.cancel();
            this.messageCollector = null;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MUC Light: ");
        sb.append(this.room);
        sb.append("(");
        sb.append(this.connection.getUser());
        sb.append(")");
        return sb.toString();
    }

    public void create(String roomName, String subject, HashMap<String, String> hashMap, List<Jid> occupants) throws Exception {
        MUCLightCreateIQ createMUCLightIQ = new MUCLightCreateIQ(this.room, roomName, occupants);
        this.messageCollector = this.connection.createStanzaCollector(this.fromRoomGroupChatFilter);
        try {
            this.connection.createStanzaCollectorAndSend(createMUCLightIQ).nextResultOrThrow();
        } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e) {
            removeConnectionCallbacks();
            throw e;
        }
    }

    public void create(String roomName, List<Jid> occupants) throws Exception {
        create(roomName, null, null, occupants);
    }

    public void leave() throws NotConnectedException, InterruptedException, NoResponseException, XMPPErrorException {
        HashMap<Jid, MUCLightAffiliation> affiliations = new HashMap<>();
        affiliations.put(this.connection.getUser(), MUCLightAffiliation.none);
        if (((IQ) this.connection.createStanzaCollectorAndSend(new MUCLightChangeAffiliationsIQ(this.room, affiliations)).nextResultOrThrow()).getType().equals(IQ.Type.result)) {
            removeConnectionCallbacks();
        }
    }

    public MUCLightRoomInfo getFullInfo(String version) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCLightInfoIQ mucLightInfoResponseIQ = (MUCLightInfoIQ) ((IQ) this.connection.createStanzaCollectorAndSend(new MUCLightGetInfoIQ(this.room, version)).nextResultOrThrow());
        return new MUCLightRoomInfo(mucLightInfoResponseIQ.getVersion(), this.room, mucLightInfoResponseIQ.getConfiguration(), mucLightInfoResponseIQ.getOccupants());
    }

    public MUCLightRoomInfo getFullInfo() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getFullInfo(null);
    }

    public MUCLightRoomConfiguration getConfiguration(String version) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ((MUCLightConfigurationIQ) ((IQ) this.connection.createStanzaCollectorAndSend(new MUCLightGetConfigsIQ(this.room, version)).nextResultOrThrow())).getConfiguration();
    }

    public MUCLightRoomConfiguration getConfiguration() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getConfiguration(null);
    }

    public HashMap<Jid, MUCLightAffiliation> getAffiliations(String version) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ((MUCLightAffiliationsIQ) ((IQ) this.connection.createStanzaCollectorAndSend(new MUCLightGetAffiliationsIQ(this.room, version)).nextResultOrThrow())).getAffiliations();
    }

    public HashMap<Jid, MUCLightAffiliation> getAffiliations() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getAffiliations(null);
    }

    public void changeAffiliations(HashMap<Jid, MUCLightAffiliation> affiliations) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        this.connection.createStanzaCollectorAndSend(new MUCLightChangeAffiliationsIQ(this.room, affiliations)).nextResultOrThrow();
    }

    public void destroy() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (((IQ) this.connection.createStanzaCollectorAndSend(new MUCLightDestroyIQ(this.room)).nextResultOrThrow()).getType().equals(IQ.Type.result)) {
            removeConnectionCallbacks();
        }
    }

    public void changeSubject(String subject) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        this.connection.createStanzaCollectorAndSend(new MUCLightSetConfigsIQ(this.room, null, subject, null)).nextResultOrThrow();
    }

    public void changeRoomName(String roomName) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        this.connection.createStanzaCollectorAndSend(new MUCLightSetConfigsIQ(this.room, roomName, null)).nextResultOrThrow();
    }

    public void setRoomConfigs(HashMap<String, String> customConfigs) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        setRoomConfigs(null, customConfigs);
    }

    public void setRoomConfigs(String roomName, HashMap<String, String> customConfigs) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        this.connection.createStanzaCollectorAndSend(new MUCLightSetConfigsIQ(this.room, roomName, customConfigs)).nextResultOrThrow();
    }
}
