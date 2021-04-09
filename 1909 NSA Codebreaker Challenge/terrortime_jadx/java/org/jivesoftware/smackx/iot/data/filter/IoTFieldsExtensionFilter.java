package org.jivesoftware.smackx.iot.data.filter;

import org.jivesoftware.smack.filter.FlexibleStanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.iot.data.element.IoTFieldsExtension;

public class IoTFieldsExtensionFilter extends FlexibleStanzaTypeFilter<Message> {
    private final boolean onlyDone;
    private final int seqNr;

    public IoTFieldsExtensionFilter(int seqNr2, boolean onlyDone2) {
        this.seqNr = seqNr2;
        this.onlyDone = onlyDone2;
    }

    /* access modifiers changed from: protected */
    public boolean acceptSpecific(Message message) {
        IoTFieldsExtension iotFieldsExtension = IoTFieldsExtension.from(message);
        if (iotFieldsExtension == null || iotFieldsExtension.getSequenceNr() != this.seqNr) {
            return false;
        }
        if (!this.onlyDone || iotFieldsExtension.isDone()) {
            return true;
        }
        return false;
    }
}
