package org.jivesoftware.smackx.bytestreams.socks5;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.concurrent.TimeoutException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHost;
import org.jxmpp.jid.Jid;

public class Socks5ClientForInitiator extends Socks5Client {
    private WeakReference<XMPPConnection> connection;
    private String sessionID;
    private final Jid target;

    public Socks5ClientForInitiator(StreamHost streamHost, String digest, XMPPConnection connection2, String sessionID2, Jid target2) {
        super(streamHost, digest);
        this.connection = new WeakReference<>(connection2);
        this.sessionID = sessionID2;
        this.target = target2;
    }

    public Socket getSocket(int timeout) throws IOException, InterruptedException, TimeoutException, XMPPException, SmackException {
        Socket socket;
        if (this.streamHost.getJID().equals((CharSequence) ((XMPPConnection) this.connection.get()).getUser())) {
            socket = Socks5Proxy.getSocks5Proxy().getSocket(this.digest);
            if (socket == null) {
                throw new SmackException("target is not connected to SOCKS5 proxy");
            }
        } else {
            socket = super.getSocket(timeout);
            try {
                activate();
            } catch (XMPPException e1) {
                socket.close();
                throw e1;
            } catch (NoResponseException e2) {
                socket.close();
                throw e2;
            }
        }
        return socket;
    }

    private void activate() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        ((XMPPConnection) this.connection.get()).createStanzaCollectorAndSend(createStreamHostActivation()).nextResultOrThrow();
    }

    private Bytestream createStreamHostActivation() {
        Bytestream activate = new Bytestream(this.sessionID);
        activate.setMode(null);
        activate.setType(Type.set);
        activate.setTo(this.streamHost.getJID());
        activate.setToActivate(this.target);
        return activate;
    }
}
