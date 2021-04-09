package org.jivesoftware.smackx.jingle;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.StanzaError.Condition;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.Jingle.Builder;
import org.jivesoftware.smackx.jingle.element.JingleAction;
import org.jivesoftware.smackx.jingle.element.JingleContent;
import org.jivesoftware.smackx.jingle.element.JingleContent.Creator;
import org.jivesoftware.smackx.jingle.element.JingleContent.Senders;
import org.jivesoftware.smackx.jingle.element.JingleContentDescription;
import org.jivesoftware.smackx.jingle.element.JingleContentTransport;
import org.jivesoftware.smackx.jingle.element.JingleError;
import org.jivesoftware.smackx.jingle.element.JingleReason;
import org.jivesoftware.smackx.jingle.element.JingleReason.Reason;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;

public class JingleUtil {
    private final XMPPConnection connection;

    public JingleUtil(XMPPConnection connection2) {
        this.connection = connection2;
    }

    public Jingle createSessionInitiate(FullJid recipient, String sessionId, Creator contentCreator, String contentName, Senders contentSenders, JingleContentDescription description, JingleContentTransport transport) {
        Builder jb = Jingle.getBuilder();
        jb.setAction(JingleAction.session_initiate).setSessionId(sessionId).setInitiator(this.connection.getUser());
        JingleContent.Builder cb = JingleContent.getBuilder();
        cb.setCreator(contentCreator).setName(contentName).setSenders(contentSenders).setDescription(description).setTransport(transport);
        Jingle jingle = jb.addJingleContent(cb.build()).build();
        jingle.setFrom((Jid) this.connection.getUser());
        jingle.setTo((Jid) recipient);
        return jingle;
    }

    public Jingle createSessionInitiateFileOffer(FullJid recipient, String sessionId, Creator contentCreator, String contentName, JingleContentDescription description, JingleContentTransport transport) {
        return createSessionInitiate(recipient, sessionId, contentCreator, contentName, Senders.initiator, description, transport);
    }

    public IQ sendSessionInitiateFileOffer(FullJid recipient, String sessionId, Creator contentCreator, String contentName, JingleContentDescription description, JingleContentTransport transport) throws NotConnectedException, InterruptedException, XMPPErrorException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionInitiateFileOffer(recipient, sessionId, contentCreator, contentName, description, transport)).nextResultOrThrow();
    }

    public IQ sendSessionInitiate(FullJid recipient, String sessionId, Creator contentCreator, String contentName, Senders contentSenders, JingleContentDescription description, JingleContentTransport transport) throws NotConnectedException, InterruptedException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionInitiate(recipient, sessionId, contentCreator, contentName, contentSenders, description, transport)).nextResult();
    }

    public Jingle createSessionAccept(FullJid recipient, String sessionId, Creator contentCreator, String contentName, Senders contentSenders, JingleContentDescription description, JingleContentTransport transport) {
        Builder jb = Jingle.getBuilder();
        jb.setResponder(this.connection.getUser()).setAction(JingleAction.session_accept).setSessionId(sessionId);
        JingleContent.Builder cb = JingleContent.getBuilder();
        cb.setCreator(contentCreator).setName(contentName).setSenders(contentSenders).setDescription(description).setTransport(transport);
        Jingle jingle = jb.addJingleContent(cb.build()).build();
        jingle.setTo((Jid) recipient);
        jingle.setFrom((Jid) this.connection.getUser());
        return jingle;
    }

    public IQ sendSessionAccept(FullJid recipient, String sessionId, Creator contentCreator, String contentName, Senders contentSenders, JingleContentDescription description, JingleContentTransport transport) throws NotConnectedException, InterruptedException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionAccept(recipient, sessionId, contentCreator, contentName, contentSenders, description, transport)).nextResult();
    }

    public Jingle createSessionTerminate(FullJid recipient, String sessionId, JingleReason reason) {
        Builder jb = Jingle.getBuilder();
        jb.setAction(JingleAction.session_terminate).setSessionId(sessionId).setReason(reason);
        Jingle jingle = jb.build();
        jingle.setFrom((Jid) this.connection.getUser());
        jingle.setTo((Jid) recipient);
        return jingle;
    }

    public Jingle createSessionTerminate(FullJid recipient, String sessionId, Reason reason) {
        return createSessionTerminate(recipient, sessionId, new JingleReason(reason));
    }

    public Jingle createSessionTerminateDecline(FullJid recipient, String sessionId) {
        return createSessionTerminate(recipient, sessionId, Reason.decline);
    }

    public IQ sendSessionTerminateDecline(FullJid recipient, String sessionId) throws NotConnectedException, InterruptedException, XMPPErrorException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionTerminateDecline(recipient, sessionId)).nextResultOrThrow();
    }

    public Jingle createSessionTerminateSuccess(FullJid recipient, String sessionId) {
        return createSessionTerminate(recipient, sessionId, Reason.success);
    }

    public IQ sendSessionTerminateSuccess(FullJid recipient, String sessionId) throws InterruptedException, XMPPErrorException, NotConnectedException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionTerminateSuccess(recipient, sessionId)).nextResultOrThrow();
    }

    public Jingle createSessionTerminateBusy(FullJid recipient, String sessionId) {
        return createSessionTerminate(recipient, sessionId, Reason.busy);
    }

    public IQ sendSessionTerminateBusy(FullJid recipient, String sessionId) throws InterruptedException, XMPPErrorException, NotConnectedException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionTerminateBusy(recipient, sessionId)).nextResultOrThrow();
    }

    public Jingle createSessionTerminateAlternativeSession(FullJid recipient, String sessionId, String altSessionId) {
        return createSessionTerminate(recipient, sessionId, (JingleReason) JingleReason.AlternativeSession(altSessionId));
    }

    public IQ sendSessionTerminateAlternativeSession(FullJid recipient, String sessionId, String altSessionId) throws InterruptedException, XMPPErrorException, NotConnectedException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionTerminateAlternativeSession(recipient, sessionId, altSessionId)).nextResultOrThrow();
    }

    public Jingle createSessionTerminateCancel(FullJid recipient, String sessionId) {
        return createSessionTerminate(recipient, sessionId, Reason.cancel);
    }

    public IQ sendSessionTerminateCancel(FullJid recipient, String sessionId) throws InterruptedException, XMPPErrorException, NotConnectedException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionTerminateCancel(recipient, sessionId)).nextResultOrThrow();
    }

    public Jingle createSessionTerminateContentCancel(FullJid recipient, String sessionId, Creator contentCreator, String contentName) {
        Builder jb = Jingle.getBuilder();
        jb.setAction(JingleAction.session_terminate).setSessionId(sessionId);
        JingleContent.Builder cb = JingleContent.getBuilder();
        cb.setCreator(contentCreator).setName(contentName);
        Jingle jingle = jb.addJingleContent(cb.build()).build();
        jingle.setFrom((Jid) this.connection.getUser());
        jingle.setTo((Jid) recipient);
        return jingle;
    }

    public IQ sendSessionTerminateContentCancel(FullJid recipient, String sessionId, Creator contentCreator, String contentName) throws NotConnectedException, InterruptedException, XMPPErrorException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionTerminateContentCancel(recipient, sessionId, contentCreator, contentName)).nextResultOrThrow();
    }

    public Jingle createSessionTerminateUnsupportedTransports(FullJid recipient, String sessionId) {
        return createSessionTerminate(recipient, sessionId, Reason.unsupported_transports);
    }

    public IQ sendSessionTerminateUnsupportedTransports(FullJid recipient, String sessionId) throws InterruptedException, XMPPErrorException, NotConnectedException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionTerminateUnsupportedTransports(recipient, sessionId)).nextResultOrThrow();
    }

    public Jingle createSessionTerminateFailedTransport(FullJid recipient, String sessionId) {
        return createSessionTerminate(recipient, sessionId, Reason.failed_transport);
    }

    public IQ sendSessionTerminateFailedTransport(FullJid recipient, String sessionId) throws InterruptedException, XMPPErrorException, NotConnectedException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionTerminateFailedTransport(recipient, sessionId)).nextResultOrThrow();
    }

    public Jingle createSessionTerminateUnsupportedApplications(FullJid recipient, String sessionId) {
        return createSessionTerminate(recipient, sessionId, Reason.unsupported_applications);
    }

    public IQ sendSessionTerminateUnsupportedApplications(FullJid recipient, String sessionId) throws InterruptedException, XMPPErrorException, NotConnectedException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionTerminateUnsupportedApplications(recipient, sessionId)).nextResultOrThrow();
    }

    public Jingle createSessionTerminateFailedApplication(FullJid recipient, String sessionId) {
        return createSessionTerminate(recipient, sessionId, Reason.failed_application);
    }

    public IQ sendSessionTerminateFailedApplication(FullJid recipient, String sessionId) throws InterruptedException, XMPPErrorException, NotConnectedException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionTerminateFailedApplication(recipient, sessionId)).nextResultOrThrow();
    }

    public Jingle createSessionTerminateIncompatibleParameters(FullJid recipient, String sessionId) {
        return createSessionTerminate(recipient, sessionId, Reason.incompatible_parameters);
    }

    public IQ sendSessionTerminateIncompatibleParameters(FullJid recipient, String sessionId) throws InterruptedException, XMPPErrorException, NotConnectedException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionTerminateIncompatibleParameters(recipient, sessionId)).nextResultOrThrow();
    }

    public IQ sendContentRejectFileNotAvailable(FullJid recipient, String sessionId, JingleContentDescription description) {
        return null;
    }

    public Jingle createSessionPing(FullJid recipient, String sessionId) {
        Builder jb = Jingle.getBuilder();
        jb.setSessionId(sessionId).setAction(JingleAction.session_info);
        Jingle jingle = jb.build();
        jingle.setFrom((Jid) this.connection.getUser());
        jingle.setTo((Jid) recipient);
        return jingle;
    }

    public IQ sendSessionPing(FullJid recipient, String sessionId) throws NotConnectedException, InterruptedException, XMPPErrorException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createSessionPing(recipient, sessionId)).nextResultOrThrow();
    }

    public IQ createAck(Jingle jingle) {
        return IQ.createResultIQ(jingle);
    }

    public void sendAck(Jingle jingle) throws NotConnectedException, InterruptedException {
        this.connection.sendStanza(createAck(jingle));
    }

    public Jingle createTransportReplace(FullJid recipient, FullJid initiator, String sessionId, Creator contentCreator, String contentName, JingleContentTransport transport) {
        Builder jb = Jingle.getBuilder();
        jb.setInitiator(initiator).setSessionId(sessionId).setAction(JingleAction.transport_replace);
        JingleContent.Builder cb = JingleContent.getBuilder();
        cb.setName(contentName).setCreator(contentCreator).setTransport(transport);
        Jingle jingle = jb.addJingleContent(cb.build()).build();
        jingle.setTo((Jid) recipient);
        jingle.setFrom((Jid) this.connection.getUser());
        return jingle;
    }

    public IQ sendTransportReplace(FullJid recipient, FullJid initiator, String sessionId, Creator contentCreator, String contentName, JingleContentTransport transport) throws NotConnectedException, InterruptedException, XMPPErrorException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createTransportReplace(recipient, initiator, sessionId, contentCreator, contentName, transport)).nextResultOrThrow();
    }

    public Jingle createTransportAccept(FullJid recipient, FullJid initiator, String sessionId, Creator contentCreator, String contentName, JingleContentTransport transport) {
        Builder jb = Jingle.getBuilder();
        jb.setAction(JingleAction.transport_accept).setInitiator(initiator).setSessionId(sessionId);
        JingleContent.Builder cb = JingleContent.getBuilder();
        cb.setCreator(contentCreator).setName(contentName).setTransport(transport);
        Jingle jingle = jb.addJingleContent(cb.build()).build();
        jingle.setTo((Jid) recipient);
        jingle.setFrom((Jid) this.connection.getUser());
        return jingle;
    }

    public IQ sendTransportAccept(FullJid recipient, FullJid initiator, String sessionId, Creator contentCreator, String contentName, JingleContentTransport transport) throws NotConnectedException, InterruptedException, XMPPErrorException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createTransportAccept(recipient, initiator, sessionId, contentCreator, contentName, transport)).nextResultOrThrow();
    }

    public Jingle createTransportReject(FullJid recipient, FullJid initiator, String sessionId, Creator contentCreator, String contentName, JingleContentTransport transport) {
        Builder jb = Jingle.getBuilder();
        jb.setAction(JingleAction.transport_reject).setInitiator(initiator).setSessionId(sessionId);
        JingleContent.Builder cb = JingleContent.getBuilder();
        cb.setCreator(contentCreator).setName(contentName).setTransport(transport);
        Jingle jingle = jb.addJingleContent(cb.build()).build();
        jingle.setTo((Jid) recipient);
        jingle.setFrom((Jid) this.connection.getUser());
        return jingle;
    }

    public IQ sendTransportReject(FullJid recipient, FullJid initiator, String sessionId, Creator contentCreator, String contentName, JingleContentTransport transport) throws NotConnectedException, InterruptedException, XMPPErrorException, NoResponseException {
        return (IQ) this.connection.createStanzaCollectorAndSend(createTransportReject(recipient, initiator, sessionId, contentCreator, contentName, transport)).nextResultOrThrow();
    }

    public IQ createErrorUnknownSession(Jingle request) {
        StanzaError.Builder error = StanzaError.getBuilder();
        error.setCondition(Condition.item_not_found).addExtension(JingleError.UNKNOWN_SESSION);
        return IQ.createErrorResponse((IQ) request, error);
    }

    public void sendErrorUnknownSession(Jingle request) throws NotConnectedException, InterruptedException {
        this.connection.sendStanza(createErrorUnknownSession(request));
    }

    public IQ createErrorUnknownInitiator(Jingle request) {
        return IQ.createErrorResponse((IQ) request, Condition.service_unavailable);
    }

    public void sendErrorUnknownInitiator(Jingle request) throws NotConnectedException, InterruptedException {
        this.connection.sendStanza(createErrorUnknownInitiator(request));
    }

    public IQ createErrorUnsupportedInfo(Jingle request) {
        StanzaError.Builder error = StanzaError.getBuilder();
        error.setCondition(Condition.feature_not_implemented).addExtension(JingleError.UNSUPPORTED_INFO);
        return IQ.createErrorResponse((IQ) request, error);
    }

    public void sendErrorUnsupportedInfo(Jingle request) throws NotConnectedException, InterruptedException {
        this.connection.sendStanza(createErrorUnsupportedInfo(request));
    }

    public IQ createErrorTieBreak(Jingle request) {
        StanzaError.Builder error = StanzaError.getBuilder();
        error.setCondition(Condition.conflict).addExtension(JingleError.TIE_BREAK);
        return IQ.createErrorResponse((IQ) request, error);
    }

    public void sendErrorTieBreak(Jingle request) throws NotConnectedException, InterruptedException {
        this.connection.sendStanza(createErrorTieBreak(request));
    }

    public IQ createErrorOutOfOrder(Jingle request) {
        StanzaError.Builder error = StanzaError.getBuilder();
        error.setCondition(Condition.unexpected_request).addExtension(JingleError.OUT_OF_ORDER);
        return IQ.createErrorResponse((IQ) request, error);
    }

    public void sendErrorOutOfOrder(Jingle request) throws NotConnectedException, InterruptedException {
        this.connection.sendStanza(createErrorOutOfOrder(request));
    }

    public IQ createErrorMalformedRequest(Jingle request) {
        return IQ.createErrorResponse((IQ) request, Condition.bad_request);
    }

    public void sendErrorMalformedRequest(Jingle request) throws NotConnectedException, InterruptedException {
        this.connection.sendStanza(createErrorMalformedRequest(request));
    }
}
