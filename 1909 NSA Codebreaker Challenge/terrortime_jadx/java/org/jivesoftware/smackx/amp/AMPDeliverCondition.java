package org.jivesoftware.smackx.amp;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.amp.packet.AMPExtension.Condition;

public class AMPDeliverCondition implements Condition {
    public static final String NAME = "deliver";
    private final Value value;

    public enum Value {
        direct,
        forward,
        gateway,
        none,
        stored
    }

    public static boolean isSupported(XMPPConnection connection) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return AMPManager.isConditionSupported(connection, NAME);
    }

    public AMPDeliverCondition(Value value2) {
        if (value2 != null) {
            this.value = value2;
            return;
        }
        throw new NullPointerException("Can't create AMPDeliverCondition with null value");
    }

    public String getName() {
        return NAME;
    }

    public String getValue() {
        return this.value.toString();
    }
}
