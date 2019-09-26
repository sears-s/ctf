package org.jivesoftware.smackx.iot.control;

import java.util.Collection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.iot.control.element.SetData;
import org.jxmpp.jid.Jid;

public interface ThingControlRequest {
    void processRequest(Jid jid, Collection<SetData> collection) throws XMPPErrorException;
}
