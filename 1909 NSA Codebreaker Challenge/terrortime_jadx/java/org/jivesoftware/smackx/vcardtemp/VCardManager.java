package org.jivesoftware.smackx.vcardtemp;

import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.id.StanzaIdUtil;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;

public final class VCardManager extends Manager {
    public static final String ELEMENT = "vCard";
    private static final Map<XMPPConnection, VCardManager> INSTANCES = new WeakHashMap();
    public static final String NAMESPACE = "vcard-temp";

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                VCardManager.getInstanceFor(connection);
            }
        });
    }

    public static synchronized VCardManager getInstanceFor(XMPPConnection connection) {
        VCardManager vcardManager;
        synchronized (VCardManager.class) {
            vcardManager = (VCardManager) INSTANCES.get(connection);
            if (vcardManager == null) {
                vcardManager = new VCardManager(connection);
                INSTANCES.put(connection, vcardManager);
            }
        }
        return vcardManager;
    }

    @Deprecated
    public static boolean isSupported(Jid jid, XMPPConnection connection) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getInstanceFor(connection).isSupported(jid);
    }

    private VCardManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature("vcard-temp");
    }

    public void saveVCard(VCard vcard) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        vcard.setTo((Jid) null);
        vcard.setType(Type.set);
        vcard.setStanzaId(StanzaIdUtil.newStanzaId());
        connection().createStanzaCollectorAndSend(vcard).nextResultOrThrow();
    }

    public VCard loadVCard() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return loadVCard(null);
    }

    public VCard loadVCard(EntityBareJid bareJid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        VCard vcardRequest = new VCard();
        vcardRequest.setTo((Jid) bareJid);
        return (VCard) connection().createStanzaCollectorAndSend(vcardRequest).nextResultOrThrow();
    }

    public boolean isSupported(Jid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(jid, "vcard-temp");
    }
}
