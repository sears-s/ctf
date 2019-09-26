package org.jivesoftware.smackx.ping.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.SimpleIQ;
import org.jxmpp.jid.Jid;

public class Ping extends SimpleIQ {
    public static final String ELEMENT = "ping";
    public static final String NAMESPACE = "urn:xmpp:ping";

    public Ping() {
        super(ELEMENT, NAMESPACE);
    }

    public Ping(Jid to) {
        this();
        setTo(to);
        setType(Type.get);
    }

    public IQ getPong() {
        return createResultIQ(this);
    }
}
