package org.jivesoftware.smackx.jingle.transports;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.JingleContent;
import org.jivesoftware.smackx.jingle.element.JingleContentTransport;

public abstract class JingleTransportSession<T extends JingleContentTransport> {
    protected final JingleSession jingleSession;
    protected T ourProposal;
    protected T theirProposal;

    public abstract T createTransport();

    public abstract String getNamespace();

    public abstract IQ handleTransportInfo(Jingle jingle);

    public abstract void initiateIncomingSession(JingleTransportInitiationCallback jingleTransportInitiationCallback);

    public abstract void initiateOutgoingSession(JingleTransportInitiationCallback jingleTransportInitiationCallback);

    public abstract void setTheirProposal(JingleContentTransport jingleContentTransport);

    public abstract JingleTransportManager<T> transportManager();

    public JingleTransportSession(JingleSession session) {
        this.jingleSession = session;
    }

    public void processJingle(Jingle jingle) {
        if (jingle.getContents().size() != 0) {
            JingleContentTransport t = ((JingleContent) jingle.getContents().get(0)).getTransport();
            if (t != null && t.getNamespace().equals(getNamespace())) {
                setTheirProposal(t);
            }
        }
    }
}
