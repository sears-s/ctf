package org.jivesoftware.smackx.jingle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Future;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.JingleContent;
import org.jivesoftware.smackx.jingle.transports.JingleTransportSession;
import org.jxmpp.jid.FullJid;

public abstract class JingleSession implements JingleSessionHandler {
    protected final List<JingleContent> contents;
    protected HashSet<String> failedTransportMethods;
    protected final FullJid local;
    protected ArrayList<Future<?>> queued;
    protected final FullJid remote;
    protected final Role role;
    protected final String sid;
    protected JingleTransportSession<?> transportSession;

    public abstract XMPPConnection getConnection();

    public abstract void onTransportMethodFailed(String str);

    public JingleSession(FullJid initiator, FullJid responder, Role role2, String sid2) {
        this(initiator, responder, role2, sid2, null);
    }

    public JingleSession(FullJid initiator, FullJid responder, Role role2, String sid2, List<JingleContent> contents2) {
        this.failedTransportMethods = new HashSet<>();
        this.contents = new ArrayList();
        this.queued = new ArrayList<>();
        if (role2 == Role.initiator) {
            this.local = initiator;
            this.remote = responder;
        } else {
            this.local = responder;
            this.remote = initiator;
        }
        this.sid = sid2;
        this.role = role2;
        if (contents2 != null) {
            this.contents.addAll(contents2);
        }
    }

    public FullJid getInitiator() {
        return isInitiator() ? this.local : this.remote;
    }

    public boolean isInitiator() {
        return this.role == Role.initiator;
    }

    public FullJid getResponder() {
        return isResponder() ? this.local : this.remote;
    }

    public boolean isResponder() {
        return this.role == Role.responder;
    }

    public FullJid getRemote() {
        return this.remote;
    }

    public FullJid getLocal() {
        return this.local;
    }

    public String getSessionId() {
        return this.sid;
    }

    public FullJidAndSessionId getFullJidAndSessionId() {
        return new FullJidAndSessionId(this.remote, this.sid);
    }

    public List<JingleContent> getContents() {
        return this.contents;
    }

    public JingleTransportSession<?> getTransportSession() {
        return this.transportSession;
    }

    /* access modifiers changed from: protected */
    public void setTransportSession(JingleTransportSession<?> transportSession2) {
        this.transportSession = transportSession2;
    }

    public int hashCode() {
        return ((((getInitiator().hashCode() + 31) * 31) + getResponder().hashCode()) * 31) + getSessionId().hashCode();
    }

    public boolean equals(Object other) {
        boolean z = false;
        if (!(other instanceof JingleSession)) {
            return false;
        }
        JingleSession otherJingleSession = (JingleSession) other;
        if (getInitiator().equals((CharSequence) otherJingleSession.getInitiator()) && getResponder().equals((CharSequence) otherJingleSession.getResponder()) && this.sid.equals(otherJingleSession.sid)) {
            z = true;
        }
        return z;
    }

    public IQ handleJingleSessionRequest(Jingle jingle) {
        switch (jingle.getAction()) {
            case content_accept:
                return handleContentAccept(jingle);
            case content_add:
                return handleContentAdd(jingle);
            case content_modify:
                return handleContentModify(jingle);
            case content_reject:
                return handleContentReject(jingle);
            case content_remove:
                return handleContentRemove(jingle);
            case description_info:
                return handleDescriptionInfo(jingle);
            case session_info:
                return handleSessionInfo(jingle);
            case security_info:
                return handleSecurityInfo(jingle);
            case session_accept:
                return handleSessionAccept(jingle);
            case transport_accept:
                return handleTransportAccept(jingle);
            case transport_info:
                return this.transportSession.handleTransportInfo(jingle);
            case session_initiate:
                return handleSessionInitiate(jingle);
            case transport_reject:
                return handleTransportReject(jingle);
            case session_terminate:
                return handleSessionTerminate(jingle);
            case transport_replace:
                return handleTransportReplace(jingle);
            default:
                return IQ.createResultIQ(jingle);
        }
    }

    /* access modifiers changed from: protected */
    public IQ handleSessionInitiate(Jingle sessionInitiate) {
        return IQ.createResultIQ(sessionInitiate);
    }

    /* access modifiers changed from: protected */
    public IQ handleSessionTerminate(Jingle sessionTerminate) {
        return IQ.createResultIQ(sessionTerminate);
    }

    /* access modifiers changed from: protected */
    public IQ handleSessionInfo(Jingle sessionInfo) {
        return IQ.createResultIQ(sessionInfo);
    }

    /* access modifiers changed from: protected */
    public IQ handleSessionAccept(Jingle sessionAccept) {
        return IQ.createResultIQ(sessionAccept);
    }

    /* access modifiers changed from: protected */
    public IQ handleContentAdd(Jingle contentAdd) {
        return IQ.createResultIQ(contentAdd);
    }

    /* access modifiers changed from: protected */
    public IQ handleContentAccept(Jingle contentAccept) {
        return IQ.createResultIQ(contentAccept);
    }

    /* access modifiers changed from: protected */
    public IQ handleContentModify(Jingle contentModify) {
        return IQ.createResultIQ(contentModify);
    }

    /* access modifiers changed from: protected */
    public IQ handleContentReject(Jingle contentReject) {
        return IQ.createResultIQ(contentReject);
    }

    /* access modifiers changed from: protected */
    public IQ handleContentRemove(Jingle contentRemove) {
        return IQ.createResultIQ(contentRemove);
    }

    /* access modifiers changed from: protected */
    public IQ handleDescriptionInfo(Jingle descriptionInfo) {
        return IQ.createResultIQ(descriptionInfo);
    }

    /* access modifiers changed from: protected */
    public IQ handleSecurityInfo(Jingle securityInfo) {
        return IQ.createResultIQ(securityInfo);
    }

    /* access modifiers changed from: protected */
    public IQ handleTransportAccept(Jingle transportAccept) {
        return IQ.createResultIQ(transportAccept);
    }

    /* access modifiers changed from: protected */
    public IQ handleTransportReplace(Jingle transportReplace) {
        return IQ.createResultIQ(transportReplace);
    }

    /* access modifiers changed from: protected */
    public IQ handleTransportReject(Jingle transportReject) {
        return IQ.createResultIQ(transportReject);
    }
}
