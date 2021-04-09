package org.jivesoftware.smackx.jingle.transports.jingle_ibb;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.bytestreams.BytestreamListener;
import org.jivesoftware.smackx.bytestreams.BytestreamRequest;
import org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.JingleContentTransport;
import org.jivesoftware.smackx.jingle.transports.JingleTransportInitiationCallback;
import org.jivesoftware.smackx.jingle.transports.JingleTransportManager;
import org.jivesoftware.smackx.jingle.transports.JingleTransportSession;
import org.jivesoftware.smackx.jingle.transports.jingle_ibb.element.JingleIBBTransport;
import org.jxmpp.jid.Jid;

public class JingleIBBTransportSession extends JingleTransportSession<JingleIBBTransport> {
    private static final Logger LOGGER = Logger.getLogger(JingleIBBTransportSession.class.getName());
    private final JingleIBBTransportManager transportManager;

    public JingleIBBTransportSession(JingleSession session) {
        super(session);
        this.transportManager = JingleIBBTransportManager.getInstanceFor(session.getConnection());
    }

    public JingleIBBTransport createTransport() {
        if (this.theirProposal == null) {
            return new JingleIBBTransport();
        }
        return new JingleIBBTransport(((JingleIBBTransport) this.theirProposal).getBlockSize(), ((JingleIBBTransport) this.theirProposal).getSessionId());
    }

    public void setTheirProposal(JingleContentTransport transport) {
        this.theirProposal = (JingleIBBTransport) transport;
    }

    public void initiateOutgoingSession(JingleTransportInitiationCallback callback) {
        LOGGER.log(Level.INFO, "Initiate Jingle InBandBytestream session.");
        try {
            callback.onSessionInitiated(InBandBytestreamManager.getByteStreamManager(this.jingleSession.getConnection()).establishSession((Jid) this.jingleSession.getRemote(), ((JingleIBBTransport) this.theirProposal).getSessionId()));
        } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e) {
            callback.onException(e);
        }
    }

    public void initiateIncomingSession(final JingleTransportInitiationCallback callback) {
        LOGGER.log(Level.INFO, "Await Jingle InBandBytestream session.");
        InBandBytestreamManager.getByteStreamManager(this.jingleSession.getConnection()).addIncomingBytestreamListener(new BytestreamListener() {
            public void incomingBytestreamRequest(BytestreamRequest request) {
                if (request.getFrom().asFullJidIfPossible().equals((CharSequence) JingleIBBTransportSession.this.jingleSession.getRemote()) && request.getSessionID().equals(((JingleIBBTransport) JingleIBBTransportSession.this.theirProposal).getSessionId())) {
                    try {
                        callback.onSessionInitiated(request.accept());
                    } catch (InterruptedException | SmackException | XMPPErrorException e) {
                        callback.onException(e);
                    }
                }
            }
        });
    }

    public String getNamespace() {
        return this.transportManager.getNamespace();
    }

    public IQ handleTransportInfo(Jingle transportInfo) {
        return IQ.createResultIQ(transportInfo);
    }

    public JingleTransportManager<JingleIBBTransport> transportManager() {
        return JingleIBBTransportManager.getInstanceFor(this.jingleSession.getConnection());
    }
}
