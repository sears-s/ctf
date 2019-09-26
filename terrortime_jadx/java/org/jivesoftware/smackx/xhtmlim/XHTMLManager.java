package org.jivesoftware.smackx.xhtmlim;

import java.util.List;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;
import org.jxmpp.jid.Jid;

public class XHTMLManager {
    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                XHTMLManager.setServiceEnabled(connection, true);
            }
        });
    }

    public static List<CharSequence> getBodies(Message message) {
        XHTMLExtension xhtmlExtension = XHTMLExtension.from(message);
        if (xhtmlExtension != null) {
            return xhtmlExtension.getBodies();
        }
        return null;
    }

    public static void addBody(Message message, XHTMLText xhtmlText) {
        XHTMLExtension xhtmlExtension = XHTMLExtension.from(message);
        if (xhtmlExtension == null) {
            xhtmlExtension = new XHTMLExtension();
            message.addExtension(xhtmlExtension);
        }
        xhtmlExtension.addBody(xhtmlText.toXML());
    }

    public static boolean isXHTMLMessage(Message message) {
        return message.getExtension(XHTMLExtension.ELEMENT, XHTMLExtension.NAMESPACE) != null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0021, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized void setServiceEnabled(org.jivesoftware.smack.XMPPConnection r3, boolean r4) {
        /*
            java.lang.Class<org.jivesoftware.smackx.xhtmlim.XHTMLManager> r0 = org.jivesoftware.smackx.xhtmlim.XHTMLManager.class
            monitor-enter(r0)
            boolean r1 = isServiceEnabled(r3)     // Catch:{ all -> 0x0022 }
            if (r1 != r4) goto L_0x000b
            monitor-exit(r0)
            return
        L_0x000b:
            if (r4 == 0) goto L_0x0017
            org.jivesoftware.smackx.disco.ServiceDiscoveryManager r1 = org.jivesoftware.smackx.disco.ServiceDiscoveryManager.getInstanceFor(r3)     // Catch:{ all -> 0x0022 }
            java.lang.String r2 = "http://jabber.org/protocol/xhtml-im"
            r1.addFeature(r2)     // Catch:{ all -> 0x0022 }
            goto L_0x0020
        L_0x0017:
            org.jivesoftware.smackx.disco.ServiceDiscoveryManager r1 = org.jivesoftware.smackx.disco.ServiceDiscoveryManager.getInstanceFor(r3)     // Catch:{ all -> 0x0022 }
            java.lang.String r2 = "http://jabber.org/protocol/xhtml-im"
            r1.removeFeature(r2)     // Catch:{ all -> 0x0022 }
        L_0x0020:
            monitor-exit(r0)
            return
        L_0x0022:
            r3 = move-exception
            monitor-exit(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.xhtmlim.XHTMLManager.setServiceEnabled(org.jivesoftware.smack.XMPPConnection, boolean):void");
    }

    public static boolean isServiceEnabled(XMPPConnection connection) {
        return ServiceDiscoveryManager.getInstanceFor(connection).includesFeature(XHTMLExtension.NAMESPACE);
    }

    public static boolean isServiceEnabled(XMPPConnection connection, Jid userID) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection).supportsFeature(userID, XHTMLExtension.NAMESPACE);
    }
}
