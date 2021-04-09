package org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements;

import java.util.logging.Logger;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHost;
import org.jivesoftware.smackx.jingle.element.JingleContentTransportCandidate;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

public final class JingleS5BTransportCandidate extends JingleContentTransportCandidate {
    public static final String ATTR_CID = "cid";
    public static final String ATTR_HOST = "host";
    public static final String ATTR_JID = "jid";
    public static final String ATTR_PORT = "port";
    public static final String ATTR_PRIORITY = "priority";
    public static final String ATTR_TYPE = "type";
    private static final Logger LOGGER = Logger.getLogger(JingleS5BTransportCandidate.class.getName());
    private final String cid;
    private final String host;
    private final Jid jid;
    private final int port;
    private final int priority;
    private final Type type;

    public static final class Builder {
        private String cid;
        private String host;
        private Jid jid;
        private int port;
        private int priority;
        private Type type;

        private Builder() {
            this.port = -1;
            this.priority = -1;
        }

        public Builder setCandidateId(String cid2) {
            this.cid = cid2;
            return this;
        }

        public Builder setHost(String host2) {
            this.host = host2;
            return this;
        }

        public Builder setJid(String jid2) throws XmppStringprepException {
            this.jid = JidCreate.from(jid2);
            return this;
        }

        public Builder setPort(int port2) {
            if (port2 >= 0) {
                this.port = port2;
                return this;
            }
            throw new IllegalArgumentException("Port MUST NOT be less than 0.");
        }

        public Builder setPriority(int priority2) {
            if (priority2 >= 0) {
                this.priority = priority2;
                return this;
            }
            throw new IllegalArgumentException("Priority MUST NOT be less than 0.");
        }

        public Builder setType(Type type2) {
            this.type = type2;
            return this;
        }

        public JingleS5BTransportCandidate build() {
            JingleS5BTransportCandidate jingleS5BTransportCandidate = new JingleS5BTransportCandidate(this.cid, this.host, this.jid, this.port, this.priority, this.type);
            return jingleS5BTransportCandidate;
        }
    }

    public enum Type {
        assisted(120),
        direct(126),
        proxy(10),
        tunnel(110);
        
        private final int weight;

        public int getWeight() {
            return this.weight;
        }

        private Type(int weight2) {
            this.weight = weight2;
        }

        public static Type fromString(String name) {
            Type[] values;
            for (Type t : values()) {
                if (t.toString().equals(name)) {
                    return t;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Illegal type: ");
            sb.append(name);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public JingleS5BTransportCandidate(String candidateId, String host2, Jid jid2, int port2, int priority2, Type type2) {
        Objects.requireNonNull(candidateId);
        Objects.requireNonNull(host2);
        Objects.requireNonNull(jid2);
        if (priority2 < 0) {
            throw new IllegalArgumentException("Priority MUST NOT be less than 0.");
        } else if (port2 >= 0) {
            this.cid = candidateId;
            this.host = host2;
            this.jid = jid2;
            this.port = port2;
            this.priority = priority2;
            this.type = type2;
        } else {
            throw new IllegalArgumentException("Port MUST NOT be less than 0.");
        }
    }

    public JingleS5BTransportCandidate(StreamHost streamHost, int priority2, Type type2) {
        this(StringUtils.randomString(24), streamHost.getAddress(), streamHost.getJID(), streamHost.getPort(), priority2, type2);
    }

    public String getCandidateId() {
        return this.cid;
    }

    public String getHost() {
        return this.host;
    }

    public Jid getJid() {
        return this.jid;
    }

    public int getPort() {
        return this.port;
    }

    public int getPriority() {
        return this.priority;
    }

    public Type getType() {
        return this.type;
    }

    public StreamHost getStreamHost() {
        return new StreamHost(this.jid, this.host, this.port);
    }

    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder();
        xml.halfOpenElement((NamedElement) this);
        xml.attribute("cid", this.cid);
        xml.attribute(ATTR_HOST, this.host);
        xml.attribute("jid", (CharSequence) this.jid);
        int i = this.port;
        if (i >= 0) {
            xml.attribute(ATTR_PORT, i);
        }
        xml.attribute(ATTR_PRIORITY, this.priority);
        xml.optAttribute("type", (Enum<?>) this.type);
        xml.closeEmptyElement();
        return xml;
    }

    public static Builder getBuilder() {
        return new Builder();
    }
}
