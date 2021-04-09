package org.jivesoftware.smackx.amp;

import java.util.Date;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.amp.packet.AMPExtension.Condition;
import org.jxmpp.util.XmppDateTime;

public class AMPExpireAtCondition implements Condition {
    public static final String NAME = "expire-at";
    private final String value;

    public static boolean isSupported(XMPPConnection connection) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return AMPManager.isConditionSupported(connection, NAME);
    }

    public AMPExpireAtCondition(Date utcDateTime) {
        if (utcDateTime != null) {
            this.value = XmppDateTime.formatXEP0082Date(utcDateTime);
            return;
        }
        throw new NullPointerException("Can't create AMPExpireAtCondition with null value");
    }

    public AMPExpireAtCondition(String utcDateTime) {
        if (utcDateTime != null) {
            this.value = utcDateTime;
            return;
        }
        throw new NullPointerException("Can't create AMPExpireAtCondition with null value");
    }

    public String getName() {
        return NAME;
    }

    public String getValue() {
        return this.value;
    }
}
