package org.jivesoftware.smackx.iot.discovery;

import org.jivesoftware.smackx.iot.IoTException;
import org.jivesoftware.smackx.iot.discovery.element.IoTClaimed;

public class IoTClaimedException extends IoTException {
    private static final long serialVersionUID = 1;
    private final IoTClaimed iotClaimed;

    public IoTClaimedException(IoTClaimed iotClaimed2) {
        this.iotClaimed = iotClaimed2;
    }

    public IoTClaimed getIoTClaimed() {
        return this.iotClaimed;
    }
}
