package org.jivesoftware.smackx.jingle.transports.jingle_s5b;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5Proxy;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHost;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.Jingle.Builder;
import org.jivesoftware.smackx.jingle.element.JingleAction;
import org.jivesoftware.smackx.jingle.element.JingleContent;
import org.jivesoftware.smackx.jingle.element.JingleContent.Creator;
import org.jivesoftware.smackx.jingle.element.JingleContent.Senders;
import org.jivesoftware.smackx.jingle.provider.JingleContentProviderManager;
import org.jivesoftware.smackx.jingle.transports.JingleTransportManager;
import org.jivesoftware.smackx.jingle.transports.JingleTransportSession;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransport;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.provider.JingleS5BTransportProvider;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;

public final class JingleS5BTransportManager extends JingleTransportManager<JingleS5BTransport> {
    private static final WeakHashMap<XMPPConnection, JingleS5BTransportManager> INSTANCES = new WeakHashMap<>();
    private static final Logger LOGGER = Logger.getLogger(JingleS5BTransportManager.class.getName());
    private static boolean useExternalCandidates = true;
    private static boolean useLocalCandidates = true;
    private List<StreamHost> availableStreamHosts = null;
    private List<StreamHost> localStreamHosts = null;

    private JingleS5BTransportManager(XMPPConnection connection) {
        super(connection);
        JingleContentProviderManager.addJingleContentTransportProvider(getNamespace(), new JingleS5BTransportProvider());
    }

    public static synchronized JingleS5BTransportManager getInstanceFor(XMPPConnection connection) {
        JingleS5BTransportManager manager;
        synchronized (JingleS5BTransportManager.class) {
            manager = (JingleS5BTransportManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new JingleS5BTransportManager(connection);
                INSTANCES.put(connection, manager);
            }
        }
        return manager;
    }

    public String getNamespace() {
        return JingleS5BTransport.NAMESPACE_V1;
    }

    public JingleTransportSession<JingleS5BTransport> transportSession(JingleSession jingleSession) {
        return new JingleS5BTransportSession(jingleSession);
    }

    private List<StreamHost> queryAvailableStreamHosts() throws XMPPErrorException, NotConnectedException, InterruptedException, NoResponseException {
        return determineStreamHostInfo(Socks5BytestreamManager.getBytestreamManager(getConnection()).determineProxies());
    }

    private List<StreamHost> queryLocalStreamHosts() {
        return Socks5BytestreamManager.getBytestreamManager(getConnection()).getLocalStreamHost();
    }

    public List<StreamHost> getAvailableStreamHosts() throws XMPPErrorException, NotConnectedException, InterruptedException, NoResponseException {
        if (this.availableStreamHosts == null) {
            this.availableStreamHosts = queryAvailableStreamHosts();
        }
        return this.availableStreamHosts;
    }

    public List<StreamHost> getLocalStreamHosts() {
        if (this.localStreamHosts == null) {
            this.localStreamHosts = queryLocalStreamHosts();
        }
        return this.localStreamHosts;
    }

    public List<StreamHost> determineStreamHostInfo(List<Jid> proxies) {
        XMPPConnection connection = getConnection();
        List<StreamHost> streamHosts = new ArrayList<>();
        Iterator<Jid> iterator = proxies.iterator();
        while (iterator.hasNext()) {
            Jid proxy = (Jid) iterator.next();
            Bytestream request = new Bytestream();
            request.setType(Type.get);
            request.setTo(proxy);
            try {
                streamHosts.addAll(((Bytestream) connection.createStanzaCollectorAndSend(request).nextResultOrThrow()).getStreamHosts());
            } catch (Exception e) {
                iterator.remove();
            }
        }
        return streamHosts;
    }

    public void authenticated(XMPPConnection connection, boolean resumed) {
        if (!resumed) {
            try {
                Socks5Proxy socks5Proxy = Socks5Proxy.getSocks5Proxy();
                if (!socks5Proxy.isRunning()) {
                    socks5Proxy.start();
                }
                this.localStreamHosts = queryLocalStreamHosts();
                this.availableStreamHosts = queryAvailableStreamHosts();
            } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e) {
                Logger logger = LOGGER;
                Level level = Level.WARNING;
                StringBuilder sb = new StringBuilder();
                sb.append("Could not query available StreamHosts: ");
                sb.append(e);
                logger.log(level, sb.toString(), e);
            }
        }
    }

    public Jingle createCandidateUsed(FullJid recipient, FullJid initiator, String sessionId, Senders contentSenders, Creator contentCreator, String contentName, String streamId, String candidateId) {
        Builder jb = Jingle.getBuilder();
        jb.setSessionId(sessionId).setInitiator(initiator).setAction(JingleAction.transport_info);
        JingleContent.Builder cb = JingleContent.getBuilder();
        cb.setName(contentName).setCreator(contentCreator).setSenders(contentSenders);
        JingleS5BTransport.Builder tb = JingleS5BTransport.getBuilder();
        tb.setCandidateUsed(candidateId).setStreamId(streamId);
        Jingle jingle = jb.addJingleContent(cb.setTransport(tb.build()).build()).build();
        jingle.setFrom((Jid) getConnection().getUser().asFullJidOrThrow());
        jingle.setTo((Jid) recipient);
        return jingle;
    }

    public Jingle createCandidateError(FullJid remote, FullJid initiator, String sessionId, Senders senders, Creator creator, String name, String streamId) {
        Builder jb = Jingle.getBuilder();
        jb.setSessionId(sessionId).setInitiator(initiator).setAction(JingleAction.transport_info);
        JingleContent.Builder cb = JingleContent.getBuilder();
        cb.setName(name).setCreator(creator).setSenders(senders);
        JingleS5BTransport.Builder tb = JingleS5BTransport.getBuilder();
        tb.setCandidateError().setStreamId(streamId);
        Jingle jingle = jb.addJingleContent(cb.setTransport(tb.build()).build()).build();
        jingle.setFrom((Jid) getConnection().getUser().asFullJidOrThrow());
        jingle.setTo((Jid) remote);
        return jingle;
    }

    public Jingle createProxyError(FullJid remote, FullJid initiator, String sessionId, Senders senders, Creator creator, String name, String streamId) {
        Builder jb = Jingle.getBuilder();
        jb.setSessionId(sessionId).setAction(JingleAction.transport_info).setInitiator(initiator);
        JingleContent.Builder cb = JingleContent.getBuilder();
        cb.setSenders(senders).setCreator(creator).setName(name);
        JingleS5BTransport.Builder tb = JingleS5BTransport.getBuilder();
        tb.setStreamId(sessionId).setProxyError().setStreamId(streamId);
        Jingle jingle = jb.addJingleContent(cb.setTransport(tb.build()).build()).build();
        jingle.setTo((Jid) remote);
        jingle.setFrom((Jid) getConnection().getUser().asFullJidOrThrow());
        return jingle;
    }

    public Jingle createCandidateActivated(FullJid remote, FullJid initiator, String sessionId, Senders senders, Creator creator, String name, String streamId, String candidateId) {
        Builder jb = Jingle.getBuilder();
        jb.setInitiator(initiator).setSessionId(sessionId).setAction(JingleAction.transport_info);
        JingleContent.Builder cb = JingleContent.getBuilder();
        cb.setName(name).setCreator(creator).setSenders(senders);
        JingleS5BTransport.Builder tb = JingleS5BTransport.getBuilder();
        tb.setStreamId(streamId).setCandidateActivated(candidateId);
        Jingle jingle = jb.addJingleContent(cb.setTransport(tb.build()).build()).build();
        jingle.setFrom((Jid) getConnection().getUser().asFullJidOrThrow());
        jingle.setTo((Jid) remote);
        return jingle;
    }

    public static void setUseLocalCandidates(boolean localCandidates) {
        useLocalCandidates = localCandidates;
    }

    public static void setUseExternalCandidates(boolean externalCandidates) {
        useExternalCandidates = externalCandidates;
    }

    public static boolean isUseLocalCandidates() {
        return useLocalCandidates;
    }

    public static boolean isUseExternalCandidates() {
        return useExternalCandidates;
    }
}
