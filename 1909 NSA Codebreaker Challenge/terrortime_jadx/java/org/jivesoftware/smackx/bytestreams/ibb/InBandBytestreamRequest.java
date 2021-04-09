package org.jivesoftware.smackx.bytestreams.ibb;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.bytestreams.BytestreamRequest;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Open;
import org.jxmpp.jid.Jid;

public class InBandBytestreamRequest implements BytestreamRequest {
    private final Open byteStreamRequest;
    private final InBandBytestreamManager manager;

    protected InBandBytestreamRequest(InBandBytestreamManager manager2, Open byteStreamRequest2) {
        this.manager = manager2;
        this.byteStreamRequest = byteStreamRequest2;
    }

    public Jid getFrom() {
        return this.byteStreamRequest.getFrom();
    }

    public String getSessionID() {
        return this.byteStreamRequest.getSessionID();
    }

    public InBandBytestreamSession accept() throws NotConnectedException, InterruptedException {
        XMPPConnection connection = this.manager.getConnection();
        Open open = this.byteStreamRequest;
        InBandBytestreamSession ibbSession = new InBandBytestreamSession(connection, open, open.getFrom());
        this.manager.getSessions().put(this.byteStreamRequest.getSessionID(), ibbSession);
        connection.sendStanza(IQ.createResultIQ(this.byteStreamRequest));
        return ibbSession;
    }

    public void reject() throws NotConnectedException, InterruptedException {
        this.manager.replyRejectPacket(this.byteStreamRequest);
    }
}
