package org.jivesoftware.smackx.jingle_filetransfer;

import java.util.WeakHashMap;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.XMPPConnection;

public final class JingleFileTransferManager extends Manager {
    private static final WeakHashMap<XMPPConnection, JingleFileTransferManager> INSTANCES = new WeakHashMap<>();

    private JingleFileTransferManager(XMPPConnection connection) {
        super(connection);
    }

    public static synchronized JingleFileTransferManager getInstanceFor(XMPPConnection connection) {
        JingleFileTransferManager manager;
        synchronized (JingleFileTransferManager.class) {
            manager = (JingleFileTransferManager) INSTANCES.get(connection);
            if (manager == null) {
                manager = new JingleFileTransferManager(connection);
                INSTANCES.put(connection, manager);
            }
        }
        return manager;
    }
}
