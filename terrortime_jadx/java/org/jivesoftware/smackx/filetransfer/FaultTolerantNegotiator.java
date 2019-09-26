package org.jivesoftware.smackx.filetransfer;

import java.io.InputStream;
import java.io.OutputStream;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Open;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.si.packet.StreamInitiation;
import org.jxmpp.jid.Jid;

public class FaultTolerantNegotiator extends StreamNegotiator {
    private final StreamNegotiator primaryNegotiator;
    private final StreamNegotiator secondaryNegotiator;

    public FaultTolerantNegotiator(XMPPConnection connection, StreamNegotiator primary, StreamNegotiator secondary) {
        super(connection);
        this.primaryNegotiator = primary;
        this.secondaryNegotiator = secondary;
    }

    public void newStreamInitiation(Jid from, String streamID) {
        this.primaryNegotiator.newStreamInitiation(from, streamID);
        this.secondaryNegotiator.newStreamInitiation(from, streamID);
    }

    /* access modifiers changed from: 0000 */
    public InputStream negotiateIncomingStream(Stanza streamInitiation) {
        throw new UnsupportedOperationException("Negotiation only handled by create incoming stream method.");
    }

    public InputStream createIncomingStream(StreamInitiation initiation) throws SmackException, XMPPErrorException, InterruptedException {
        IQ initiationSet = initiateIncomingStream(connection(), initiation);
        return determineNegotiator(initiationSet).negotiateIncomingStream(initiationSet);
    }

    private StreamNegotiator determineNegotiator(Stanza streamInitiation) {
        if (streamInitiation instanceof Bytestream) {
            return this.primaryNegotiator;
        }
        if (streamInitiation instanceof Open) {
            return this.secondaryNegotiator;
        }
        throw new IllegalStateException("Unknown stream initiation type");
    }

    public OutputStream createOutgoingStream(String streamID, Jid initiator, Jid target) throws SmackException, XMPPException, InterruptedException {
        try {
            return this.primaryNegotiator.createOutgoingStream(streamID, initiator, target);
        } catch (Exception e) {
            return this.secondaryNegotiator.createOutgoingStream(streamID, initiator, target);
        }
    }

    public String[] getNamespaces() {
        String[] primary = this.primaryNegotiator.getNamespaces();
        String[] secondary = this.secondaryNegotiator.getNamespaces();
        String[] namespaces = new String[(primary.length + secondary.length)];
        System.arraycopy(primary, 0, namespaces, 0, primary.length);
        System.arraycopy(secondary, 0, namespaces, primary.length, secondary.length);
        return namespaces;
    }
}
