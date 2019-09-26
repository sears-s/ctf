package org.jivesoftware.smackx.muclight;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.IQReplyFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverItems.Item;
import org.jivesoftware.smackx.muclight.element.MUCLightBlockingIQ;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;

public final class MultiUserChatLightManager extends Manager {
    private static final Map<XMPPConnection, MultiUserChatLightManager> INSTANCES = new WeakHashMap();
    private final Map<EntityBareJid, WeakReference<MultiUserChatLight>> multiUserChatLights = new HashMap();

    public static synchronized MultiUserChatLightManager getInstanceFor(XMPPConnection connection) {
        MultiUserChatLightManager multiUserChatLightManager;
        synchronized (MultiUserChatLightManager.class) {
            multiUserChatLightManager = (MultiUserChatLightManager) INSTANCES.get(connection);
            if (multiUserChatLightManager == null) {
                multiUserChatLightManager = new MultiUserChatLightManager(connection);
                INSTANCES.put(connection, multiUserChatLightManager);
            }
        }
        return multiUserChatLightManager;
    }

    private MultiUserChatLightManager(XMPPConnection connection) {
        super(connection);
    }

    public synchronized MultiUserChatLight getMultiUserChatLight(EntityBareJid jid) {
        WeakReference<MultiUserChatLight> weakRefMultiUserChat = (WeakReference) this.multiUserChatLights.get(jid);
        if (weakRefMultiUserChat == null) {
            return createNewMucLightAndAddToMap(jid);
        }
        MultiUserChatLight multiUserChatLight = (MultiUserChatLight) weakRefMultiUserChat.get();
        if (multiUserChatLight != null) {
            return multiUserChatLight;
        }
        return createNewMucLightAndAddToMap(jid);
    }

    private MultiUserChatLight createNewMucLightAndAddToMap(EntityBareJid jid) {
        MultiUserChatLight multiUserChatLight = new MultiUserChatLight(connection(), jid);
        this.multiUserChatLights.put(jid, new WeakReference(multiUserChatLight));
        return multiUserChatLight;
    }

    public boolean isFeatureSupported(DomainBareJid mucLightService) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).discoverInfo(mucLightService).containsFeature(MultiUserChatLight.NAMESPACE);
    }

    public List<Jid> getOccupiedRooms(DomainBareJid mucLightService) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        List<Item> items = ServiceDiscoveryManager.getInstanceFor(connection()).discoverItems(mucLightService).getItems();
        List<Jid> answer = new ArrayList<>(items.size());
        for (Item item : items) {
            answer.add(item.getEntityID());
        }
        return answer;
    }

    public List<DomainBareJid> getLocalServices() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).findServices(MultiUserChatLight.NAMESPACE, false, false);
    }

    public List<Jid> getUsersAndRoomsBlocked(DomainBareJid mucLightService) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCLightBlockingIQ muclIghtBlockingIQResult = getBlockingList(mucLightService);
        List<Jid> jids = new ArrayList<>();
        if (muclIghtBlockingIQResult.getRooms() != null) {
            jids.addAll(muclIghtBlockingIQResult.getRooms().keySet());
        }
        if (muclIghtBlockingIQResult.getUsers() != null) {
            jids.addAll(muclIghtBlockingIQResult.getUsers().keySet());
        }
        return jids;
    }

    public List<Jid> getRoomsBlocked(DomainBareJid mucLightService) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCLightBlockingIQ mucLightBlockingIQResult = getBlockingList(mucLightService);
        List<Jid> jids = new ArrayList<>();
        if (mucLightBlockingIQResult.getRooms() != null) {
            jids.addAll(mucLightBlockingIQResult.getRooms().keySet());
        }
        return jids;
    }

    public List<Jid> getUsersBlocked(DomainBareJid mucLightService) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCLightBlockingIQ mucLightBlockingIQResult = getBlockingList(mucLightService);
        List<Jid> jids = new ArrayList<>();
        if (mucLightBlockingIQResult.getUsers() != null) {
            jids.addAll(mucLightBlockingIQResult.getUsers().keySet());
        }
        return jids;
    }

    private MUCLightBlockingIQ getBlockingList(DomainBareJid mucLightService) throws NoResponseException, XMPPErrorException, InterruptedException, NotConnectedException {
        MUCLightBlockingIQ mucLightBlockingIQ = new MUCLightBlockingIQ(null, null);
        mucLightBlockingIQ.setType(Type.get);
        mucLightBlockingIQ.setTo((Jid) mucLightService);
        return (MUCLightBlockingIQ) ((IQ) connection().createStanzaCollectorAndSend(new IQReplyFilter(mucLightBlockingIQ, connection()), mucLightBlockingIQ).nextResultOrThrow());
    }

    public void blockRoom(DomainBareJid mucLightService, Jid roomJid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        HashMap<Jid, Boolean> rooms = new HashMap<>();
        rooms.put(roomJid, Boolean.valueOf(false));
        sendBlockRooms(mucLightService, rooms);
    }

    public void blockRooms(DomainBareJid mucLightService, List<Jid> roomsJids) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        HashMap<Jid, Boolean> rooms = new HashMap<>();
        for (Jid jid : roomsJids) {
            rooms.put(jid, Boolean.valueOf(false));
        }
        sendBlockRooms(mucLightService, rooms);
    }

    private void sendBlockRooms(DomainBareJid mucLightService, HashMap<Jid, Boolean> rooms) throws NoResponseException, XMPPErrorException, InterruptedException, NotConnectedException {
        MUCLightBlockingIQ mucLightBlockingIQ = new MUCLightBlockingIQ(rooms, null);
        mucLightBlockingIQ.setType(Type.set);
        mucLightBlockingIQ.setTo((Jid) mucLightService);
        connection().createStanzaCollectorAndSend(mucLightBlockingIQ).nextResultOrThrow();
    }

    public void blockUser(DomainBareJid mucLightService, Jid userJid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        HashMap<Jid, Boolean> users = new HashMap<>();
        users.put(userJid, Boolean.valueOf(false));
        sendBlockUsers(mucLightService, users);
    }

    public void blockUsers(DomainBareJid mucLightService, List<Jid> usersJids) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        HashMap<Jid, Boolean> users = new HashMap<>();
        for (Jid jid : usersJids) {
            users.put(jid, Boolean.valueOf(false));
        }
        sendBlockUsers(mucLightService, users);
    }

    private void sendBlockUsers(DomainBareJid mucLightService, HashMap<Jid, Boolean> users) throws NoResponseException, XMPPErrorException, InterruptedException, NotConnectedException {
        MUCLightBlockingIQ mucLightBlockingIQ = new MUCLightBlockingIQ(null, users);
        mucLightBlockingIQ.setType(Type.set);
        mucLightBlockingIQ.setTo((Jid) mucLightService);
        connection().createStanzaCollectorAndSend(mucLightBlockingIQ).nextResultOrThrow();
    }

    public void unblockRoom(DomainBareJid mucLightService, Jid roomJid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        HashMap<Jid, Boolean> rooms = new HashMap<>();
        rooms.put(roomJid, Boolean.valueOf(true));
        sendUnblockRooms(mucLightService, rooms);
    }

    public void unblockRooms(DomainBareJid mucLightService, List<Jid> roomsJids) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        HashMap<Jid, Boolean> rooms = new HashMap<>();
        for (Jid jid : roomsJids) {
            rooms.put(jid, Boolean.valueOf(true));
        }
        sendUnblockRooms(mucLightService, rooms);
    }

    private void sendUnblockRooms(DomainBareJid mucLightService, HashMap<Jid, Boolean> rooms) throws NoResponseException, XMPPErrorException, InterruptedException, NotConnectedException {
        MUCLightBlockingIQ mucLightBlockingIQ = new MUCLightBlockingIQ(rooms, null);
        mucLightBlockingIQ.setType(Type.set);
        mucLightBlockingIQ.setTo((Jid) mucLightService);
        connection().createStanzaCollectorAndSend(mucLightBlockingIQ).nextResultOrThrow();
    }

    public void unblockUser(DomainBareJid mucLightService, Jid userJid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        HashMap<Jid, Boolean> users = new HashMap<>();
        users.put(userJid, Boolean.valueOf(true));
        sendUnblockUsers(mucLightService, users);
    }

    public void unblockUsers(DomainBareJid mucLightService, List<Jid> usersJids) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        HashMap<Jid, Boolean> users = new HashMap<>();
        for (Jid jid : usersJids) {
            users.put(jid, Boolean.valueOf(true));
        }
        sendUnblockUsers(mucLightService, users);
    }

    private void sendUnblockUsers(DomainBareJid mucLightService, HashMap<Jid, Boolean> users) throws NoResponseException, XMPPErrorException, InterruptedException, NotConnectedException {
        MUCLightBlockingIQ mucLightBlockingIQ = new MUCLightBlockingIQ(null, users);
        mucLightBlockingIQ.setType(Type.set);
        mucLightBlockingIQ.setTo((Jid) mucLightService);
        connection().createStanzaCollectorAndSend(mucLightBlockingIQ).nextResultOrThrow();
    }
}
