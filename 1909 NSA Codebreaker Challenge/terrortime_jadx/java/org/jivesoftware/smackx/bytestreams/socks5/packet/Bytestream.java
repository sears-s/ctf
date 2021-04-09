package org.jivesoftware.smackx.bytestreams.socks5.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransport;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportCandidate;
import org.jxmpp.jid.Jid;

public class Bytestream extends IQ {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "http://jabber.org/protocol/bytestreams";
    private Mode mode;
    private String sessionID;
    private final List<StreamHost> streamHosts;
    private Activate toActivate;
    private StreamHostUsed usedHost;

    /* renamed from: org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$IQ$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$packet$IQ$Type[Type.set.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$IQ$Type[Type.result.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$IQ$Type[Type.get.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public static class Activate implements NamedElement {
        public static String ELEMENTNAME = "activate";
        private final Jid target;

        public Activate(Jid target2) {
            this.target = target2;
        }

        public Jid getTarget() {
            return this.target;
        }

        public String getElementName() {
            return ELEMENTNAME;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.rightAngleBracket();
            xml.escape((CharSequence) getTarget());
            xml.closeElement((NamedElement) this);
            return xml;
        }
    }

    public enum Mode {
        tcp,
        udp;

        public static Mode fromName(String name) {
            try {
                return valueOf(name);
            } catch (Exception e) {
                return tcp;
            }
        }
    }

    public static class StreamHost implements NamedElement {
        public static String ELEMENTNAME = "streamhost";
        private final Jid JID;
        private final String addy;
        private final int port;

        public StreamHost(Jid jid, String address) {
            this(jid, address, 0);
        }

        public StreamHost(Jid JID2, String address, int port2) {
            this.JID = (Jid) Objects.requireNonNull(JID2, "StreamHost JID must not be null");
            this.addy = (String) StringUtils.requireNotNullOrEmpty(address, "StreamHost address must not be null");
            this.port = port2;
        }

        public Jid getJID() {
            return this.JID;
        }

        public String getAddress() {
            return this.addy;
        }

        public int getPort() {
            return this.port;
        }

        public String getElementName() {
            return ELEMENTNAME;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.attribute("jid", (CharSequence) getJID());
            xml.attribute(JingleS5BTransportCandidate.ATTR_HOST, getAddress());
            if (getPort() != 0) {
                xml.attribute(JingleS5BTransportCandidate.ATTR_PORT, Integer.toString(getPort()));
            } else {
                xml.attribute("zeroconf", "_jabber.bytestreams");
            }
            xml.closeEmptyElement();
            return xml;
        }
    }

    public static class StreamHostUsed implements NamedElement {
        public static String ELEMENTNAME = "streamhost-used";
        private final Jid JID;

        public StreamHostUsed(Jid JID2) {
            this.JID = JID2;
        }

        public Jid getJID() {
            return this.JID;
        }

        public String getElementName() {
            return ELEMENTNAME;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.attribute("jid", (CharSequence) getJID());
            xml.closeEmptyElement();
            return xml;
        }
    }

    public Bytestream() {
        super("query", NAMESPACE);
        this.mode = Mode.tcp;
        this.streamHosts = new ArrayList();
    }

    public Bytestream(String SID) {
        this();
        setSessionID(SID);
    }

    public void setSessionID(String sessionID2) {
        this.sessionID = sessionID2;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public void setMode(Mode mode2) {
        this.mode = mode2;
    }

    public Mode getMode() {
        return this.mode;
    }

    public StreamHost addStreamHost(Jid JID, String address) {
        return addStreamHost(JID, address, 0);
    }

    public StreamHost addStreamHost(Jid JID, String address, int port) {
        StreamHost host = new StreamHost(JID, address, port);
        addStreamHost(host);
        return host;
    }

    public void addStreamHost(StreamHost host) {
        this.streamHosts.add(host);
    }

    public List<StreamHost> getStreamHosts() {
        return Collections.unmodifiableList(this.streamHosts);
    }

    public StreamHost getStreamHost(Jid JID) {
        if (JID == null) {
            return null;
        }
        for (StreamHost host : this.streamHosts) {
            if (host.getJID().equals((CharSequence) JID)) {
                return host;
            }
        }
        return null;
    }

    public int countStreamHosts() {
        return this.streamHosts.size();
    }

    public void setUsedHost(Jid JID) {
        this.usedHost = new StreamHostUsed(JID);
    }

    public StreamHostUsed getUsedHost() {
        return this.usedHost;
    }

    public Activate getToActivate() {
        return this.toActivate;
    }

    public void setToActivate(Jid targetID) {
        this.toActivate = new Activate(targetID);
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$packet$IQ$Type[getType().ordinal()];
        if (i == 1) {
            xml.optAttribute("sid", getSessionID());
            xml.optAttribute(JingleS5BTransport.ATTR_MODE, (Enum<?>) getMode());
            xml.rightAngleBracket();
            if (getToActivate() == null) {
                for (StreamHost streamHost : getStreamHosts()) {
                    xml.append(streamHost.toXML((String) null));
                }
            } else {
                xml.append(getToActivate().toXML((String) null));
            }
        } else if (i == 2) {
            xml.rightAngleBracket();
            xml.optAppend((Element) getUsedHost());
            for (StreamHost host : this.streamHosts) {
                xml.append(host.toXML((String) null));
            }
        } else if (i == 3) {
            xml.setEmptyElement();
        } else {
            throw new IllegalStateException();
        }
        return xml;
    }
}
