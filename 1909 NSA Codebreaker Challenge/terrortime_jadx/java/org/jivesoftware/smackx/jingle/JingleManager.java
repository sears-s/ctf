package org.jivesoftware.smackx.jingle;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.JingleAction;
import org.jivesoftware.smackx.jingle.element.JingleContent;
import org.jivesoftware.smackx.jingle.transports.jingle_ibb.JingleIBBTransportManager;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.JingleS5BTransportManager;
import org.jxmpp.jid.FullJid;

public final class JingleManager extends Manager {
    private static final Map<XMPPConnection, JingleManager> INSTANCES = new WeakHashMap();
    /* access modifiers changed from: private */
    public static final Logger LOGGER = Logger.getLogger(JingleManager.class.getName());
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    /* access modifiers changed from: private */
    public final Map<String, JingleHandler> descriptionHandlers = new ConcurrentHashMap();
    /* access modifiers changed from: private */
    public final Map<FullJidAndSessionId, JingleSessionHandler> jingleSessionHandlers = new ConcurrentHashMap();
    /* access modifiers changed from: private */
    public final JingleUtil jutil;

    public static ExecutorService getThreadPool() {
        return threadPool;
    }

    public static synchronized JingleManager getInstanceFor(XMPPConnection connection) {
        JingleManager jingleManager;
        synchronized (JingleManager.class) {
            jingleManager = (JingleManager) INSTANCES.get(connection);
            if (jingleManager == null) {
                jingleManager = new JingleManager(connection);
                INSTANCES.put(connection, jingleManager);
            }
        }
        return jingleManager;
    }

    private JingleManager(XMPPConnection connection) {
        super(connection);
        this.jutil = new JingleUtil(connection);
        AnonymousClass1 r1 = new AbstractIqRequestHandler(Jingle.ELEMENT, Jingle.NAMESPACE, Type.set, Mode.async) {
            public IQ handleIQRequest(IQ iqRequest) {
                Jingle jingle = (Jingle) iqRequest;
                FullJid fullFrom = jingle.getFrom().asFullJidOrThrow();
                String sid = jingle.getSid();
                JingleSessionHandler sessionHandler = (JingleSessionHandler) JingleManager.this.jingleSessionHandlers.get(new FullJidAndSessionId(fullFrom, sid));
                if (sessionHandler != null) {
                    return sessionHandler.handleJingleSessionRequest(jingle);
                }
                if (jingle.getAction() == JingleAction.session_initiate) {
                    JingleHandler jingleDescriptionHandler = (JingleHandler) JingleManager.this.descriptionHandlers.get(((JingleContent) jingle.getContents().get(0)).getDescription().getNamespace());
                    if (jingleDescriptionHandler != null) {
                        return jingleDescriptionHandler.handleJingleRequest(jingle);
                    }
                    JingleManager.LOGGER.log(Level.WARNING, "Unsupported Jingle application.");
                    return JingleManager.this.jutil.createSessionTerminateUnsupportedApplications(fullFrom, sid);
                }
                JingleManager.LOGGER.log(Level.WARNING, "Unknown session.");
                return JingleManager.this.jutil.createErrorUnknownSession(jingle);
            }
        };
        connection.registerIQRequestHandler(r1);
        JingleTransportMethodManager transportMethodManager = JingleTransportMethodManager.getInstanceFor(connection);
        transportMethodManager.registerTransportManager(JingleIBBTransportManager.getInstanceFor(connection));
        transportMethodManager.registerTransportManager(JingleS5BTransportManager.getInstanceFor(connection));
    }

    public JingleHandler registerDescriptionHandler(String namespace, JingleHandler handler) {
        return (JingleHandler) this.descriptionHandlers.put(namespace, handler);
    }

    public JingleSessionHandler registerJingleSessionHandler(FullJid otherJid, String sessionId, JingleSessionHandler sessionHandler) {
        return (JingleSessionHandler) this.jingleSessionHandlers.put(new FullJidAndSessionId(otherJid, sessionId), sessionHandler);
    }

    public JingleSessionHandler unregisterJingleSessionHandler(FullJid otherJid, String sessionId, JingleSessionHandler sessionHandler) {
        return (JingleSessionHandler) this.jingleSessionHandlers.remove(new FullJidAndSessionId(otherJid, sessionId));
    }

    public static String randomId() {
        return StringUtils.randomString(24);
    }
}
