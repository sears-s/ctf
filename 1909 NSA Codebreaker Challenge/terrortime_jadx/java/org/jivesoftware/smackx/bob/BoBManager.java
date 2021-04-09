package org.jivesoftware.smackx.bob;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.util.SHA1;
import org.jivesoftware.smackx.bob.element.BoBIQ;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jxmpp.jid.Jid;
import org.jxmpp.util.cache.LruCache;

public final class BoBManager extends Manager {
    private static final LruCache<BoBHash, BoBData> BOB_CACHE = new LruCache<>(128);
    private static final Map<XMPPConnection, BoBManager> INSTANCES = new WeakHashMap();
    public static final String NAMESPACE = "urn:xmpp:bob";
    /* access modifiers changed from: private */
    public final Map<BoBHash, BoBInfo> bobs = new ConcurrentHashMap();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                BoBManager.getInstanceFor(connection);
            }
        });
    }

    public static synchronized BoBManager getInstanceFor(XMPPConnection connection) {
        BoBManager bobManager;
        synchronized (BoBManager.class) {
            bobManager = (BoBManager) INSTANCES.get(connection);
            if (bobManager == null) {
                bobManager = new BoBManager(connection);
                INSTANCES.put(connection, bobManager);
            }
        }
        return bobManager;
    }

    private BoBManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature("urn:xmpp:bob");
        AnonymousClass2 r2 = new AbstractIqRequestHandler("data", "urn:xmpp:bob", Type.get, Mode.async) {
            public IQ handleIQRequest(IQ iqRequest) {
                BoBIQ bobIQRequest = (BoBIQ) iqRequest;
                BoBInfo bobInfo = (BoBInfo) BoBManager.this.bobs.get(bobIQRequest.getBoBHash());
                if (bobInfo == null) {
                    return null;
                }
                BoBIQ responseBoBIQ = new BoBIQ(bobIQRequest.getBoBHash(), bobInfo.getData());
                responseBoBIQ.setType(Type.result);
                responseBoBIQ.setTo(bobIQRequest.getFrom());
                return responseBoBIQ;
            }
        };
        connection.registerIQRequestHandler(r2);
    }

    public boolean isSupportedByServer() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).serverSupportsFeature("urn:xmpp:bob");
    }

    public BoBData requestBoB(Jid to, BoBHash bobHash) throws NotLoggedInException, NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        BoBData bobData = (BoBData) BOB_CACHE.lookup(bobHash);
        if (bobData != null) {
            return bobData;
        }
        BoBIQ requestBoBIQ = new BoBIQ(bobHash);
        requestBoBIQ.setType(Type.get);
        requestBoBIQ.setTo(to);
        BoBData bobData2 = ((BoBIQ) getAuthenticatedConnectionOrThrow().createStanzaCollectorAndSend(requestBoBIQ).nextResultOrThrow()).getBoBData();
        BOB_CACHE.put(bobHash, bobData2);
        return bobData2;
    }

    public BoBInfo addBoB(BoBData bobData) {
        BoBHash bobHash = new BoBHash(SHA1.hex(bobData.getContent()), "sha1");
        BoBInfo bobInfo = new BoBInfo(Collections.unmodifiableSet(Collections.singleton(bobHash)), bobData);
        this.bobs.put(bobHash, bobInfo);
        return bobInfo;
    }

    public BoBInfo removeBoB(BoBHash bobHash) {
        BoBInfo bobInfo = (BoBInfo) this.bobs.remove(bobHash);
        if (bobInfo == null) {
            return null;
        }
        for (BoBHash otherBobHash : bobInfo.getHashes()) {
            this.bobs.remove(otherBobHash);
        }
        return bobInfo;
    }
}
