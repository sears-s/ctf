package org.jivesoftware.smackx.delay;

import java.util.Date;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.delay.packet.DelayInformation;

public class DelayInformationManager {
    public static final String LEGACY_DELAYED_DELIVERY_ELEMENT = "x";
    public static final String LEGACY_DELAYED_DELIVERY_NAMESPACE = "jabber:x:delay";

    public static DelayInformation getXep203DelayInformation(Stanza packet) {
        return DelayInformation.from(packet);
    }

    public static DelayInformation getLegacyDelayInformation(Stanza packet) {
        return (DelayInformation) packet.getExtension("x", LEGACY_DELAYED_DELIVERY_NAMESPACE);
    }

    public static DelayInformation getDelayInformation(Stanza packet) {
        DelayInformation delayInformation = getXep203DelayInformation(packet);
        if (delayInformation != null) {
            return delayInformation;
        }
        return getLegacyDelayInformation(packet);
    }

    public static Date getDelayTimestamp(Stanza packet) {
        DelayInformation delayInformation = getDelayInformation(packet);
        if (delayInformation == null) {
            return null;
        }
        return delayInformation.getStamp();
    }

    public static boolean isDelayedStanza(Stanza packet) {
        return getDelayInformation(packet) != null;
    }
}
