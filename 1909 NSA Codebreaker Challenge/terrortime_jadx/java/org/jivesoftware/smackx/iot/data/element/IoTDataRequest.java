package org.jivesoftware.smackx.iot.data.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;

public class IoTDataRequest extends IQ {
    public static final String ELEMENT = "req";
    public static final String NAMESPACE = "urn:xmpp:iot:sensordata";
    private final boolean momentary;
    private final int seqNr;

    public IoTDataRequest(int seqNr2, boolean momentary2) {
        super("req", "urn:xmpp:iot:sensordata");
        this.seqNr = seqNr2;
        this.momentary = momentary2;
    }

    public int getSequenceNr() {
        return this.seqNr;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("seqnr", this.seqNr);
        xml.optBooleanAttribute("momentary", this.momentary);
        xml.setEmptyElement();
        return xml;
    }

    public boolean isMomentary() {
        return this.momentary;
    }
}
