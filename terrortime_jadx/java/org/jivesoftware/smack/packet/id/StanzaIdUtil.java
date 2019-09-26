package org.jivesoftware.smack.packet.id;

import java.util.concurrent.atomic.AtomicLong;
import org.jivesoftware.smack.util.StringUtils;

public class StanzaIdUtil {
    private static final AtomicLong ID = new AtomicLong();
    private static final String PREFIX;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.randomString(5));
        sb.append("-");
        PREFIX = sb.toString();
    }

    public static String newStanzaId() {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX);
        sb.append(Long.toString(ID.incrementAndGet()));
        return sb.toString();
    }
}
