package org.jivesoftware.smackx.bytestreams.ibb;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Data;

class DataListener extends AbstractIqRequestHandler {
    private final InBandBytestreamManager manager;

    DataListener(InBandBytestreamManager manager2) {
        super("data", "http://jabber.org/protocol/ibb", Type.set, Mode.async);
        this.manager = manager2;
    }

    public IQ handleIQRequest(IQ iqRequest) {
        Data data = (Data) iqRequest;
        InBandBytestreamSession ibbSession = (InBandBytestreamSession) this.manager.getSessions().get(data.getDataPacketExtension().getSessionID());
        if (ibbSession == null) {
            try {
                this.manager.replyItemNotFoundPacket(data);
            } catch (InterruptedException | NotConnectedException | NotLoggedInException e) {
                return null;
            }
        } else {
            ibbSession.processIQPacket(data);
        }
        return null;
    }
}
