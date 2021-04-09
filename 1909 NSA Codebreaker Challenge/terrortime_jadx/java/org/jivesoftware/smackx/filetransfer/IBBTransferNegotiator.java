package org.jivesoftware.smackx.filetransfer;

import java.io.InputStream;
import java.io.OutputStream;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager;
import org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamRequest;
import org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamSession;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Open;
import org.jivesoftware.smackx.si.packet.StreamInitiation;
import org.jxmpp.jid.Jid;

public class IBBTransferNegotiator extends StreamNegotiator {
    private final InBandBytestreamManager manager;

    private static final class ByteStreamRequest extends InBandBytestreamRequest {
        private ByteStreamRequest(InBandBytestreamManager manager, Open byteStreamRequest) {
            super(manager, byteStreamRequest);
        }
    }

    protected IBBTransferNegotiator(XMPPConnection connection) {
        super(connection);
        this.manager = InBandBytestreamManager.getByteStreamManager(connection);
    }

    public OutputStream createOutgoingStream(String streamID, Jid initiator, Jid target) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        InBandBytestreamSession session = this.manager.establishSession(target, streamID);
        session.setCloseBothStreamsEnabled(true);
        return session.getOutputStream();
    }

    public InputStream createIncomingStream(StreamInitiation initiation) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        this.manager.ignoreBytestreamRequestOnce(initiation.getSessionID());
        return negotiateIncomingStream(initiateIncomingStream(connection(), initiation));
    }

    public void newStreamInitiation(Jid from, String streamID) {
        this.manager.ignoreBytestreamRequestOnce(streamID);
    }

    public String[] getNamespaces() {
        return new String[]{"http://jabber.org/protocol/ibb"};
    }

    /* access modifiers changed from: 0000 */
    public InputStream negotiateIncomingStream(Stanza streamInitiation) throws NotConnectedException, InterruptedException {
        InBandBytestreamSession session = new ByteStreamRequest(this.manager, (Open) streamInitiation).accept();
        session.setCloseBothStreamsEnabled(true);
        return session.getInputStream();
    }
}
