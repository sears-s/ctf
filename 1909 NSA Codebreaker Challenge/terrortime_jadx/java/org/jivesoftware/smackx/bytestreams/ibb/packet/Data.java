package org.jivesoftware.smackx.bytestreams.ibb.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;

public class Data extends IQ {
    private final DataPacketExtension dataPacketExtension;

    public Data(DataPacketExtension data) {
        super("data", "http://jabber.org/protocol/ibb");
        if (data != null) {
            this.dataPacketExtension = data;
            setType(Type.set);
            return;
        }
        throw new IllegalArgumentException("Data must not be null");
    }

    public DataPacketExtension getDataPacketExtension() {
        return this.dataPacketExtension;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return this.dataPacketExtension.getIQChildElementBuilder(xml);
    }
}
