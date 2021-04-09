package org.jivesoftware.smackx.sharedgroups;

import java.util.List;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;

public class SharedGroupManager {
    public static List<String> getSharedGroups(XMPPConnection connection) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        SharedGroupsInfo info = new SharedGroupsInfo();
        info.setType(Type.get);
        return ((SharedGroupsInfo) connection.createStanzaCollectorAndSend(info).nextResultOrThrow()).getGroups();
    }
}
