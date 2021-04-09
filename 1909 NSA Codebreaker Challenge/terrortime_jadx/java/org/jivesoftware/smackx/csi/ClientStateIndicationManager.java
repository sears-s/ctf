package org.jivesoftware.smackx.csi;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.csi.packet.ClientStateIndication;
import org.jivesoftware.smackx.csi.packet.ClientStateIndication.Active;
import org.jivesoftware.smackx.csi.packet.ClientStateIndication.Feature;
import org.jivesoftware.smackx.csi.packet.ClientStateIndication.Inactive;

public class ClientStateIndicationManager {
    public static void active(XMPPConnection connection) throws NotConnectedException, InterruptedException {
        throwIaeIfNotSupported(connection);
        connection.sendNonza(Active.INSTANCE);
    }

    public static void inactive(XMPPConnection connection) throws NotConnectedException, InterruptedException {
        throwIaeIfNotSupported(connection);
        connection.sendNonza(Inactive.INSTANCE);
    }

    public static boolean isSupported(XMPPConnection connection) {
        return connection.hasFeature(Feature.ELEMENT, ClientStateIndication.NAMESPACE);
    }

    private static void throwIaeIfNotSupported(XMPPConnection connection) {
        if (!isSupported(connection)) {
            throw new IllegalArgumentException("Client State Indication not supported by server");
        }
    }
}
