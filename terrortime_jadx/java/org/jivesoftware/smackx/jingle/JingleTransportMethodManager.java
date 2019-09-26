package org.jivesoftware.smackx.jingle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.JingleContent;
import org.jivesoftware.smackx.jingle.element.JingleContentTransport;
import org.jivesoftware.smackx.jingle.transports.JingleTransportManager;
import org.jivesoftware.smackx.jingle.transports.jingle_ibb.element.JingleIBBTransport;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransport;

public final class JingleTransportMethodManager extends Manager {
    private static final WeakHashMap<XMPPConnection, JingleTransportMethodManager> INSTANCES = new WeakHashMap<>();
    private static final String[] transportPreference = {JingleS5BTransport.NAMESPACE_V1, JingleIBBTransport.NAMESPACE_V1};
    private final HashMap<String, JingleTransportManager<?>> transportManagers = new HashMap<>();

    private JingleTransportMethodManager(XMPPConnection connection) {
        super(connection);
    }

    public static synchronized JingleTransportMethodManager getInstanceFor(XMPPConnection connection) {
        JingleTransportMethodManager manager;
        synchronized (JingleTransportMethodManager.class) {
            manager = (JingleTransportMethodManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new JingleTransportMethodManager(connection);
                INSTANCES.put(connection, manager);
            }
        }
        return manager;
    }

    public void registerTransportManager(JingleTransportManager<?> manager) {
        this.transportManagers.put(manager.getNamespace(), manager);
    }

    public static JingleTransportManager<?> getTransportManager(XMPPConnection connection, String namespace) {
        return getInstanceFor(connection).getTransportManager(namespace);
    }

    public JingleTransportManager<?> getTransportManager(String namespace) {
        return (JingleTransportManager) this.transportManagers.get(namespace);
    }

    public static JingleTransportManager<?> getTransportManager(XMPPConnection connection, Jingle request) {
        return getInstanceFor(connection).getTransportManager(request);
    }

    public JingleTransportManager<?> getTransportManager(Jingle request) {
        JingleContent content = (JingleContent) request.getContents().get(0);
        if (content == null) {
            return null;
        }
        JingleContentTransport transport = content.getTransport();
        if (transport == null) {
            return null;
        }
        return getTransportManager(transport.getNamespace());
    }

    public static JingleTransportManager<?> getBestAvailableTransportManager(XMPPConnection connection) {
        return getInstanceFor(connection).getBestAvailableTransportManager();
    }

    public JingleTransportManager<?> getBestAvailableTransportManager() {
        for (String ns : transportPreference) {
            JingleTransportManager<?> tm = getTransportManager(ns);
            if (tm != null) {
                return tm;
            }
        }
        Iterator<String> it = this.transportManagers.keySet().iterator();
        if (it.hasNext()) {
            return getTransportManager((String) it.next());
        }
        return null;
    }

    public JingleTransportManager<?> getBestAvailableTransportManager(Set<String> except) {
        for (String ns : transportPreference) {
            JingleTransportManager<?> tm = getTransportManager(ns);
            if (tm != null && !except.contains(tm.getNamespace())) {
                return tm;
            }
        }
        for (String ns2 : this.transportManagers.keySet()) {
            if (!except.contains(ns2)) {
                return getTransportManager(ns2);
            }
        }
        return null;
    }
}
