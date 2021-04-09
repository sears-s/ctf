package org.jivesoftware.smackx.jingle.transports.jingle_s5b;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamSession;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5Client;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5ClientForInitiator;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5Proxy;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5Utils;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.Mode;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHost;
import org.jivesoftware.smackx.jingle.JingleManager;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.JingleContent;
import org.jivesoftware.smackx.jingle.element.JingleContentTransport;
import org.jivesoftware.smackx.jingle.element.JingleContentTransportCandidate;
import org.jivesoftware.smackx.jingle.transports.JingleTransportInitiationCallback;
import org.jivesoftware.smackx.jingle.transports.JingleTransportSession;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransport;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransport.Builder;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportCandidate;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportCandidate.Type;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo.CandidateUsed;
import org.jxmpp.jid.Jid;

public class JingleS5BTransportSession extends JingleTransportSession<JingleS5BTransport> {
    private static final UsedCandidate CANDIDATE_FAILURE = new UsedCandidate(null, null, null);
    private static final Logger LOGGER = Logger.getLogger(JingleS5BTransportSession.class.getName());
    private JingleTransportInitiationCallback callback;
    private UsedCandidate ourChoice;
    private UsedCandidate theirChoice;

    private static final class UsedCandidate {
        /* access modifiers changed from: private */
        public final JingleS5BTransportCandidate candidate;
        /* access modifiers changed from: private */
        public final Socket socket;
        /* access modifiers changed from: private */
        public final JingleS5BTransport transport;

        private UsedCandidate(JingleS5BTransport transport2, JingleS5BTransportCandidate candidate2, Socket socket2) {
            this.socket = socket2;
            this.transport = transport2;
            this.candidate = candidate2;
        }
    }

    public JingleS5BTransportSession(JingleSession jingleSession) {
        super(jingleSession);
    }

    public JingleS5BTransport createTransport() {
        if (this.ourProposal == null) {
            this.ourProposal = createTransport(JingleManager.randomId(), Mode.tcp);
        }
        return (JingleS5BTransport) this.ourProposal;
    }

    public void setTheirProposal(JingleContentTransport transport) {
        this.theirProposal = (JingleS5BTransport) transport;
    }

    public JingleS5BTransport createTransport(String sid, Mode mode) {
        Builder jb = JingleS5BTransport.getBuilder().setStreamId(sid).setMode(mode).setDestinationAddress(Socks5Utils.createDigest(sid, this.jingleSession.getLocal(), this.jingleSession.getRemote()));
        if (JingleS5BTransportManager.isUseLocalCandidates()) {
            for (StreamHost host : transportManager().getLocalStreamHosts()) {
                jb.addTransportCandidate(new JingleS5BTransportCandidate(host, 100, Type.direct));
            }
        }
        List<StreamHost> emptyList = Collections.emptyList();
        if (JingleS5BTransportManager.isUseExternalCandidates()) {
            try {
                emptyList = transportManager().getAvailableStreamHosts();
            } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e) {
                LOGGER.log(Level.WARNING, "Could not determine available StreamHosts.", e);
            }
        }
        for (StreamHost host2 : emptyList) {
            jb.addTransportCandidate(new JingleS5BTransportCandidate(host2, 0, Type.proxy));
        }
        return jb.build();
    }

    public void setTheirTransport(JingleContentTransport transport) {
        this.theirProposal = (JingleS5BTransport) transport;
    }

    public void initiateOutgoingSession(JingleTransportInitiationCallback callback2) {
        this.callback = callback2;
        initiateSession();
    }

    public void initiateIncomingSession(JingleTransportInitiationCallback callback2) {
        this.callback = callback2;
        initiateSession();
    }

    private void initiateSession() {
        Socks5Proxy.getSocks5Proxy().addTransfer(createTransport().getDestinationAddress());
        JingleContent content = (JingleContent) this.jingleSession.getContents().get(0);
        UsedCandidate usedCandidate = chooseFromProposedCandidates((JingleS5BTransport) this.theirProposal);
        if (usedCandidate == null) {
            this.ourChoice = CANDIDATE_FAILURE;
            try {
                this.jingleSession.getConnection().sendStanza(transportManager().createCandidateError(this.jingleSession.getRemote(), this.jingleSession.getInitiator(), this.jingleSession.getSessionId(), content.getSenders(), content.getCreator(), content.getName(), ((JingleS5BTransport) this.theirProposal).getStreamId()));
            } catch (InterruptedException | NotConnectedException e) {
                LOGGER.log(Level.WARNING, "Could not send candidate-error.", e);
            }
        } else {
            this.ourChoice = usedCandidate;
            try {
                this.jingleSession.getConnection().createStanzaCollectorAndSend(transportManager().createCandidateUsed(this.jingleSession.getRemote(), this.jingleSession.getInitiator(), this.jingleSession.getSessionId(), content.getSenders(), content.getCreator(), content.getName(), ((JingleS5BTransport) this.theirProposal).getStreamId(), this.ourChoice.candidate.getCandidateId())).nextResultOrThrow();
            } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e2) {
                LOGGER.log(Level.WARNING, "Could not send candidate-used.", e2);
            }
        }
        connectIfReady();
    }

    private UsedCandidate chooseFromProposedCandidates(JingleS5BTransport proposal) {
        for (JingleContentTransportCandidate c : proposal.getCandidates()) {
            JingleS5BTransportCandidate candidate = (JingleS5BTransportCandidate) c;
            try {
                return connectToTheirCandidate(candidate);
            } catch (IOException | InterruptedException | TimeoutException | SmackException | XMPPException e) {
                Logger logger = LOGGER;
                Level level = Level.WARNING;
                StringBuilder sb = new StringBuilder();
                sb.append("Could not connect to ");
                sb.append(candidate.getHost());
                logger.log(level, sb.toString(), e);
            }
        }
        LOGGER.log(Level.WARNING, "Failed to connect to any candidate.");
        return null;
    }

    private UsedCandidate connectToTheirCandidate(JingleS5BTransportCandidate candidate) throws InterruptedException, TimeoutException, SmackException, XMPPException, IOException {
        StreamHost streamHost = candidate.getStreamHost();
        String address = streamHost.getAddress();
        Socket socket = new Socks5Client(streamHost, ((JingleS5BTransport) this.theirProposal).getDestinationAddress()).getSocket(10000);
        Logger logger = LOGGER;
        Level level = Level.INFO;
        StringBuilder sb = new StringBuilder();
        sb.append("Connected to their StreamHost ");
        sb.append(address);
        sb.append(" using dstAddr ");
        sb.append(((JingleS5BTransport) this.theirProposal).getDestinationAddress());
        logger.log(level, sb.toString());
        return new UsedCandidate((JingleS5BTransport) this.theirProposal, candidate, socket);
    }

    private UsedCandidate connectToOurCandidate(JingleS5BTransportCandidate candidate) throws InterruptedException, TimeoutException, SmackException, XMPPException, IOException {
        StreamHost streamHost = candidate.getStreamHost();
        String address = streamHost.getAddress();
        Socks5ClientForInitiator socks5Client = new Socks5ClientForInitiator(streamHost, ((JingleS5BTransport) this.ourProposal).getDestinationAddress(), this.jingleSession.getConnection(), ((JingleS5BTransport) this.ourProposal).getStreamId(), this.jingleSession.getRemote());
        Socket socket = socks5Client.getSocket(10000);
        Logger logger = LOGGER;
        Level level = Level.INFO;
        StringBuilder sb = new StringBuilder();
        sb.append("Connected to our StreamHost ");
        sb.append(address);
        sb.append(" using dstAddr ");
        sb.append(((JingleS5BTransport) this.ourProposal).getDestinationAddress());
        logger.log(level, sb.toString());
        return new UsedCandidate((JingleS5BTransport) this.ourProposal, candidate, socket);
    }

    public String getNamespace() {
        return JingleS5BTransport.NAMESPACE_V1;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0034, code lost:
        if (r2.equals(org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo.CandidateUsed.ELEMENT) != false) goto L_0x004c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smack.packet.IQ handleTransportInfo(org.jivesoftware.smackx.jingle.element.Jingle r8) {
        /*
            r7 = this;
            java.util.List r0 = r8.getContents()
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            org.jivesoftware.smackx.jingle.element.JingleContent r0 = (org.jivesoftware.smackx.jingle.element.JingleContent) r0
            org.jivesoftware.smackx.jingle.element.JingleContentTransport r0 = r0.getTransport()
            org.jivesoftware.smackx.jingle.element.JingleContentTransportInfo r0 = r0.getInfo()
            org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo r0 = (org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo) r0
            java.lang.String r2 = r0.getElementName()
            int r3 = r2.hashCode()
            r4 = 3
            r5 = 2
            r6 = 1
            switch(r3) {
                case -1033040578: goto L_0x0041;
                case 995927529: goto L_0x0037;
                case 1352626631: goto L_0x002e;
                case 2000321031: goto L_0x0024;
                default: goto L_0x0023;
            }
        L_0x0023:
            goto L_0x004b
        L_0x0024:
            java.lang.String r1 = "candidate-activated"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x0023
            r1 = r6
            goto L_0x004c
        L_0x002e:
            java.lang.String r3 = "candidate-used"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x0023
            goto L_0x004c
        L_0x0037:
            java.lang.String r1 = "proxy-error"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x0023
            r1 = r4
            goto L_0x004c
        L_0x0041:
            java.lang.String r1 = "candidate-error"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x0023
            r1 = r5
            goto L_0x004c
        L_0x004b:
            r1 = -1
        L_0x004c:
            if (r1 == 0) goto L_0x0068
            if (r1 == r6) goto L_0x0063
            if (r1 == r5) goto L_0x005e
            if (r1 == r4) goto L_0x0059
            org.jivesoftware.smack.packet.IQ r1 = org.jivesoftware.smack.packet.IQ.createResultIQ(r8)
            return r1
        L_0x0059:
            org.jivesoftware.smack.packet.IQ r1 = r7.handleProxyError(r8)
            return r1
        L_0x005e:
            org.jivesoftware.smack.packet.IQ r1 = r7.handleCandidateError(r8)
            return r1
        L_0x0063:
            org.jivesoftware.smack.packet.IQ r1 = r7.handleCandidateActivate(r8)
            return r1
        L_0x0068:
            org.jivesoftware.smack.packet.IQ r1 = r7.handleCandidateUsed(r8)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smackx.jingle.transports.jingle_s5b.JingleS5BTransportSession.handleTransportInfo(org.jivesoftware.smackx.jingle.element.Jingle):org.jivesoftware.smack.packet.IQ");
    }

    public IQ handleCandidateUsed(Jingle jingle) {
        this.theirChoice = new UsedCandidate((JingleS5BTransport) this.ourProposal, ((JingleS5BTransport) this.ourProposal).getCandidate(((CandidateUsed) ((JingleS5BTransportInfo) ((JingleContent) jingle.getContents().get(0)).getTransport().getInfo())).getCandidateId()), null);
        this.theirChoice.candidate;
        connectIfReady();
        return IQ.createResultIQ(jingle);
    }

    public IQ handleCandidateActivate(Jingle jingle) {
        LOGGER.log(Level.INFO, "handleCandidateActivate");
        this.callback.onSessionInitiated(new Socks5BytestreamSession(this.ourChoice.socket, this.ourChoice.candidate.getJid().asBareJid().equals((CharSequence) this.jingleSession.getRemote().asBareJid())));
        return IQ.createResultIQ(jingle);
    }

    public IQ handleCandidateError(Jingle jingle) {
        this.theirChoice = CANDIDATE_FAILURE;
        connectIfReady();
        return IQ.createResultIQ(jingle);
    }

    public IQ handleProxyError(Jingle jingle) {
        return IQ.createResultIQ(jingle);
    }

    private void connectIfReady() {
        UsedCandidate nominated;
        boolean z = false;
        JingleContent content = (JingleContent) this.jingleSession.getContents().get(0);
        UsedCandidate usedCandidate = this.ourChoice;
        if (usedCandidate != null) {
            UsedCandidate usedCandidate2 = this.theirChoice;
            if (usedCandidate2 != null) {
                UsedCandidate usedCandidate3 = CANDIDATE_FAILURE;
                if (usedCandidate == usedCandidate3 && usedCandidate2 == usedCandidate3) {
                    LOGGER.log(Level.INFO, "Failure.");
                    this.jingleSession.onTransportMethodFailed(getNamespace());
                    return;
                }
                LOGGER.log(Level.INFO, "Ready.");
                UsedCandidate usedCandidate4 = this.ourChoice;
                UsedCandidate usedCandidate5 = CANDIDATE_FAILURE;
                if (usedCandidate4 == usedCandidate5 || this.theirChoice == usedCandidate5) {
                    if (this.ourChoice != CANDIDATE_FAILURE) {
                        nominated = this.ourChoice;
                    } else {
                        nominated = this.theirChoice;
                    }
                } else if (usedCandidate4.candidate.getPriority() > this.theirChoice.candidate.getPriority()) {
                    nominated = this.ourChoice;
                } else if (this.ourChoice.candidate.getPriority() < this.theirChoice.candidate.getPriority()) {
                    nominated = this.theirChoice;
                } else {
                    nominated = this.jingleSession.isInitiator() ? this.ourChoice : this.theirChoice;
                }
                if (nominated == this.theirChoice) {
                    LOGGER.log(Level.INFO, "Their choice, so our proposed candidate is used.");
                    boolean isProxy = nominated.candidate.getType() == Type.proxy;
                    try {
                        UsedCandidate nominated2 = connectToOurCandidate(nominated.candidate);
                        if (isProxy) {
                            LOGGER.log(Level.INFO, "Is external proxy. Activate it.");
                            Bytestream activate = new Bytestream(((JingleS5BTransport) this.ourProposal).getStreamId());
                            activate.setMode(null);
                            activate.setType(IQ.Type.set);
                            activate.setTo(nominated2.candidate.getJid());
                            activate.setToActivate(this.jingleSession.getRemote());
                            activate.setFrom((Jid) this.jingleSession.getLocal());
                            try {
                                this.jingleSession.getConnection().createStanzaCollectorAndSend(activate).nextResultOrThrow();
                                LOGGER.log(Level.INFO, "Send candidate-activate.");
                                try {
                                    this.jingleSession.getConnection().createStanzaCollectorAndSend(transportManager().createCandidateActivated(this.jingleSession.getRemote(), this.jingleSession.getInitiator(), this.jingleSession.getSessionId(), content.getSenders(), content.getCreator(), content.getName(), nominated2.transport.getStreamId(), nominated2.candidate.getCandidateId())).nextResultOrThrow();
                                } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e) {
                                    LOGGER.log(Level.WARNING, "Could not send candidate-activated", e);
                                    return;
                                }
                            } catch (InterruptedException | NoResponseException | NotConnectedException | XMPPErrorException e2) {
                                LOGGER.log(Level.WARNING, "Could not activate proxy.", e2);
                                return;
                            }
                        }
                        LOGGER.log(Level.INFO, "Start transmission.");
                        Socket access$200 = nominated2.socket;
                        if (!isProxy) {
                            z = true;
                        }
                        this.callback.onSessionInitiated(new Socks5BytestreamSession(access$200, z));
                    } catch (IOException | InterruptedException | TimeoutException | SmackException | XMPPException e3) {
                        LOGGER.log(Level.INFO, "Could not connect to our candidate.", e3);
                        return;
                    }
                } else {
                    LOGGER.log(Level.INFO, "Our choice, so their candidate was used.");
                    if (nominated.candidate.getType() == Type.proxy) {
                        z = true;
                    }
                    if (!z) {
                        LOGGER.log(Level.INFO, "Direct connection.");
                        this.callback.onSessionInitiated(new Socks5BytestreamSession(nominated.socket, true));
                    } else {
                        LOGGER.log(Level.INFO, "Our choice was their external proxy. wait for candidate-activate.");
                    }
                }
                return;
            }
        }
        LOGGER.log(Level.INFO, "Not ready.");
    }

    public JingleS5BTransportManager transportManager() {
        return JingleS5BTransportManager.getInstanceFor(this.jingleSession.getConnection());
    }
}
