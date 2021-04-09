package org.jivesoftware.smackx.amp;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.amp.packet.AMPExtension.Condition;

public class AMPMatchResourceCondition implements Condition {
    public static final String NAME = "match-resource";
    private final Value value;

    public enum Value {
        any,
        exact,
        other
    }

    public static boolean isSupported(XMPPConnection connection) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return AMPManager.isConditionSupported(connection, NAME);
    }

    public AMPMatchResourceCondition(Value value2) {
        if (value2 != null) {
            this.value = value2;
            return;
        }
        throw new NullPointerException("Can't create AMPMatchResourceCondition with null value");
    }

    public String getName() {
        return NAME;
    }

    public String getValue() {
        return this.value.toString();
    }
}
