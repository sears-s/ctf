package org.jivesoftware.smackx.bytestreams.ibb;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Close;

class CloseListener extends AbstractIqRequestHandler {
    private final InBandBytestreamManager manager;

    protected CloseListener(InBandBytestreamManager manager2) {
        super(Close.ELEMENT, "http://jabber.org/protocol/ibb", Type.set, Mode.async);
        this.manager = manager2;
    }

    public IQ handleIQRequest(IQ iqRequest) {
        Close closeRequest = (Close) iqRequest;
        InBandBytestreamSession ibbSession = (InBandBytestreamSession) this.manager.getSessions().get(closeRequest.getSessionID());
        if (ibbSession == null) {
            try {
                this.manager.replyItemNotFoundPacket(closeRequest);
            } catch (InterruptedException | NotConnectedException e) {
                return null;
            }
        } else {
            try {
                ibbSession.closeByPeer(closeRequest);
                this.manager.getSessions().remove(closeRequest.getSessionID());
            } catch (InterruptedException | NotConnectedException e2) {
                return null;
            }
        }
        return null;
    }
}
