package org.jivesoftware.smackx.iot.control.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;

public class IoTSetResponse extends IQ {
    public static final String ELEMENT = "setResponse";
    public static final String NAMESPACE = "urn:xmpp:iot:control";

    public IoTSetResponse() {
        super(ELEMENT, "urn:xmpp:iot:control");
    }

    public IoTSetResponse(IoTSetRequest iotSetRequest) {
        this();
        initializeAsResultFor(iotSetRequest);
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.setEmptyElement();
        return xml;
    }
}
