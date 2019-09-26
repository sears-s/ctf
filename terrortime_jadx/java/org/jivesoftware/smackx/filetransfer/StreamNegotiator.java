package org.jivesoftware.smackx.filetransfer;

import java.io.InputStream;
import java.io.OutputStream;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.EventManger;
import org.jivesoftware.smack.util.EventManger.Callback;
import org.jivesoftware.smackx.si.packet.StreamInitiation;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.Jid;

public abstract class StreamNegotiator extends Manager {
    protected static final EventManger<String, IQ, NotConnectedException> initationSetEvents = new EventManger<>();

    public abstract InputStream createIncomingStream(StreamInitiation streamInitiation) throws XMPPErrorException, InterruptedException, SmackException;

    public abstract OutputStream createOutgoingStream(String str, Jid jid, Jid jid2) throws SmackException, XMPPException, InterruptedException;

    public abstract String[] getNamespaces();

    /* access modifiers changed from: 0000 */
    public abstract InputStream negotiateIncomingStream(Stanza stanza) throws XMPPErrorException, InterruptedException, SmackException;

    /* access modifiers changed from: protected */
    public abstract void newStreamInitiation(Jid jid, String str);

    protected StreamNegotiator(XMPPConnection connection) {
        super(connection);
    }

    protected static StreamInitiation createInitiationAccept(StreamInitiation streamInitiationOffer, String[] namespaces) {
        StreamInitiation response = new StreamInitiation();
        response.setTo(streamInitiationOffer.getFrom());
        response.setFrom(streamInitiationOffer.getTo());
        response.setType(Type.result);
        response.setStanzaId(streamInitiationOffer.getStanzaId());
        DataForm form = new DataForm(DataForm.Type.submit);
        FormField field = new FormField("stream-method");
        for (String namespace : namespaces) {
            field.addValue((CharSequence) namespace);
        }
        form.addField(field);
        response.setFeatureNegotiationForm(form);
        return response;
    }

    /* access modifiers changed from: protected */
    public final IQ initiateIncomingStream(final XMPPConnection connection, StreamInitiation initiation) throws NoResponseException, XMPPErrorException, NotConnectedException {
        final StreamInitiation response = createInitiationAccept(initiation, getNamespaces());
        newStreamInitiation(initiation.getFrom(), initiation.getSessionID());
        StringBuilder sb = new StringBuilder();
        sb.append(initiation.getFrom().toString());
        sb.append(9);
        sb.append(initiation.getSessionID());
        try {
            IQ streamMethodInitiation = (IQ) initationSetEvents.performActionAndWaitForEvent(sb.toString(), connection.getReplyTimeout(), new Callback<NotConnectedException>() {
                public void action() throws NotConnectedException {
                    try {
                        connection.sendStanza(response);
                    } catch (InterruptedException e) {
                    }
                }
            });
            if (streamMethodInitiation != null) {
                XMPPErrorException.ifHasErrorThenThrow(streamMethodInitiation);
                return streamMethodInitiation;
            }
            throw NoResponseException.newWith(connection, "stream initiation");
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void signal(String eventKey, IQ eventValue) {
        initationSetEvents.signalEvent(eventKey, eventValue);
    }
}
