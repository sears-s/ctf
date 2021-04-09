package org.jivesoftware.smackx.amp;

import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.amp.packet.AMPExtension;
import org.jivesoftware.smackx.amp.packet.AMPExtension.Action;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;

public class AMPManager {
    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                AMPManager.setServiceEnabled(connection, true);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0021, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized void setServiceEnabled(org.jivesoftware.smack.XMPPConnection r3, boolean r4) {
        /*
            java.lang.Class<org.jivesoftware.smackx.amp.AMPManager> r0 = org.jivesoftware.smackx.amp.AMPManager.class
            monitor-enter(r0)
            boolean r1 = isServiceEnabled(r3)     // Catch:{ all -> 0x0022 }
            if (r1 != r4) goto L_0x000b
            monitor-exit(r0)
            return
        L_0x000b:
            if (r4 == 0) goto L_0x0017
            org.jivesoftware.smackx.disco.ServiceDiscoveryManager r1 = org.jivesoftware.smackx.disco.ServiceDiscoveryManager.getInstanceFor(r3)     // Catch:{ all -> 0x0022 }
            java.lang.String r2 = "http://jabber.org/protocol/amp"
            r1.addFeature(r2)     // Catch:{ all -> 0x0022 }
            goto L_0x0020
        L_0x0017:
            org.jivesoftware.smackx.disco.ServiceDiscoveryManager r1 = org.jivesoftware.smackx.disco.ServiceDiscoveryManager.getInstanceFor(r3)     // Catch:{ all -> 0x0022 }
            java.lang.String r2 = "http://jabber.org/protocol/amp"
            r1.removeFeature(r2)     // Catch:{ all -> 0x0022 }
        L_0x0020:
            monitor-exit(r0)
            return
        L_0x0022:
            r3 = move-exception
            monitor-exit(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.amp.AMPManager.setServiceEnabled(org.jivesoftware.smack.XMPPConnection, boolean):void");
    }

    public static boolean isServiceEnabled(XMPPConnection connection) {
        connection.getXMPPServiceDomain();
        return ServiceDiscoveryManager.getInstanceFor(connection).includesFeature(AMPExtension.NAMESPACE);
    }

    public static boolean isActionSupported(XMPPConnection connection, Action action) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append("http://jabber.org/protocol/amp?action=");
        sb.append(action.toString());
        return isFeatureSupportedByServer(connection, sb.toString());
    }

    public static boolean isConditionSupported(XMPPConnection connection, String conditionName) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append("http://jabber.org/protocol/amp?condition=");
        sb.append(conditionName);
        return isFeatureSupportedByServer(connection, sb.toString());
    }

    private static boolean isFeatureSupportedByServer(XMPPConnection connection, String featureName) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection).serverSupportsFeature(featureName);
    }
}
