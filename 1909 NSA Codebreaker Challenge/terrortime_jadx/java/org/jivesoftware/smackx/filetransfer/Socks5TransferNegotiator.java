package org.jivesoftware.smackx.filetransfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamRequest;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.si.packet.StreamInitiation;
import org.jxmpp.jid.Jid;

public class Socks5TransferNegotiator extends StreamNegotiator {
    private final Socks5BytestreamManager manager;

    private static final class ByteStreamRequest extends Socks5BytestreamRequest {
        private ByteStreamRequest(Socks5BytestreamManager manager, Bytestream byteStreamRequest) {
            super(manager, byteStreamRequest);
        }
    }

    Socks5TransferNegotiator(XMPPConnection connection) {
        super(connection);
        this.manager = Socks5BytestreamManager.getBytestreamManager(connection);
    }

    public OutputStream createOutgoingStream(String streamID, Jid initiator, Jid target) throws SmackException, XMPPException {
        String str = "error establishing SOCKS5 Bytestream";
        try {
            return this.manager.establishSession(target, streamID).getOutputStream();
        } catch (IOException e) {
            throw new SmackException(str, e);
        } catch (InterruptedException e2) {
            throw new SmackException(str, e2);
        }
    }

    public InputStream createIncomingStream(StreamInitiation initiation) throws XMPPErrorException, InterruptedException, SmackException {
        this.manager.ignoreBytestreamRequestOnce(initiation.getSessionID());
        return negotiateIncomingStream(initiateIncomingStream(connection(), initiation));
    }

    public void newStreamInitiation(Jid from, String streamID) {
        this.manager.ignoreBytestreamRequestOnce(streamID);
    }

    public String[] getNamespaces() {
        return new String[]{Bytestream.NAMESPACE};
    }

    /* access modifiers changed from: 0000 */
    public InputStream negotiateIncomingStream(Stanza streamInitiation) throws InterruptedException, SmackException, XMPPErrorException {
        try {
            PushbackInputStream stream = new PushbackInputStream(new ByteStreamRequest(this.manager, (Bytestream) streamInitiation).accept().getInputStream());
            stream.unread(stream.read());
            return stream;
        } catch (IOException e) {
            throw new SmackException("Error establishing input stream", e);
        }
    }
}
