package org.jivesoftware.smackx.iot.data.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;

public class IoTDataReadOutAccepted extends IQ {
    public static final String ELEMENT = "accepted";
    public static final String NAMESPACE = "urn:xmpp:iot:sensordata";
    private final boolean queued;
    private final int seqNr;

    public IoTDataReadOutAccepted(int seqNr2, boolean queued2) {
        super(ELEMENT, "urn:xmpp:iot:sensordata");
        this.seqNr = seqNr2;
        this.queued = queued2;
        setType(Type.result);
    }

    public IoTDataReadOutAccepted(IoTDataRequest dataRequest) {
        this(dataRequest.getSequenceNr(), false);
        setStanzaId(dataRequest.getStanzaId());
        setTo(dataRequest.getFrom());
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("seqnr", this.seqNr);
        xml.optBooleanAttribute("queued", this.queued);
        xml.setEmptyElement();
        return xml;
    }
}
