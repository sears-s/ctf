package org.jivesoftware.smackx.iqversion;

import java.util.Map;
import java.util.WeakHashMap;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.iqversion.packet.Version;
import org.jxmpp.jid.Jid;

public final class VersionManager extends Manager {
    private static final Map<XMPPConnection, VersionManager> INSTANCES = new WeakHashMap();
    private static boolean autoAppendSmackVersion = true;
    private static Version defaultVersion;
    /* access modifiers changed from: private */
    public Version ourVersion = defaultVersion;

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                VersionManager.getInstanceFor(connection);
            }
        });
    }

    public static void setDefaultVersion(String name, String version) {
        setDefaultVersion(name, version, null);
    }

    public static void setDefaultVersion(String name, String version, String os) {
        defaultVersion = generateVersionFrom(name, version, os);
    }

    private VersionManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature(Version.NAMESPACE);
        AnonymousClass2 r2 = new AbstractIqRequestHandler("query", Version.NAMESPACE, Type.get, Mode.async) {
            public IQ handleIQRequest(IQ iqRequest) {
                if (VersionManager.this.ourVersion == null) {
                    return IQ.createErrorResponse(iqRequest, Condition.not_acceptable);
                }
                return Version.createResultFor(iqRequest, VersionManager.this.ourVersion);
            }
        };
        connection.registerIQRequestHandler(r2);
    }

    public static synchronized VersionManager getInstanceFor(XMPPConnection connection) {
        VersionManager versionManager;
        synchronized (VersionManager.class) {
            versionManager = (VersionManager) INSTANCES.get(connection);
            if (versionManager == null) {
                versionManager = new VersionManager(connection);
                INSTANCES.put(connection, versionManager);
            }
        }
        return versionManager;
    }

    public static void setAutoAppendSmackVersion(boolean autoAppendSmackVersion2) {
        autoAppendSmackVersion = autoAppendSmackVersion2;
    }

    public void setVersion(String name, String version) {
        setVersion(name, version, null);
    }

    public void setVersion(String name, String version, String os) {
        this.ourVersion = generateVersionFrom(name, version, os);
    }

    public void unsetVersion() {
        this.ourVersion = null;
    }

    public boolean isSupported(Jid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(jid, Version.NAMESPACE);
    }

    public Version getVersion(Jid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (!isSupported(jid)) {
            return null;
        }
        return (Version) connection().createStanzaCollectorAndSend(new Version(jid)).nextResultOrThrow();
    }

    private static Version generateVersionFrom(String name, String version, String os) {
        if (autoAppendSmackVersion) {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(" (Smack ");
            sb.append(SmackConfiguration.getVersion());
            sb.append(')');
            name = sb.toString();
        }
        return new Version(name, version, os);
    }
}
