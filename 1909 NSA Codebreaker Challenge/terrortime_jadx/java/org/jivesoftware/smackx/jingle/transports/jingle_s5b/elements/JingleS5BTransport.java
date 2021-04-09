package org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.Mode;
import org.jivesoftware.smackx.jingle.element.JingleContentTransport;
import org.jivesoftware.smackx.jingle.element.JingleContentTransportCandidate;
import org.jivesoftware.smackx.jingle.element.JingleContentTransportInfo;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo.CandidateActivated;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo.CandidateError;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo.CandidateUsed;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements.JingleS5BTransportInfo.ProxyError;

public class JingleS5BTransport extends JingleContentTransport {
    public static final String ATTR_DSTADDR = "dstaddr";
    public static final String ATTR_MODE = "mode";
    public static final String ATTR_SID = "sid";
    public static final String NAMESPACE_V1 = "urn:xmpp:jingle:transports:s5b:1";
    private final String dstAddr;
    private final Mode mode;
    private final String streamId;

    public static class Builder {
        private final ArrayList<JingleContentTransportCandidate> candidates = new ArrayList<>();
        private String dstAddr;
        private JingleContentTransportInfo info;
        private Mode mode;
        private String streamId;

        public Builder setStreamId(String sid) {
            this.streamId = sid;
            return this;
        }

        public Builder setDestinationAddress(String dstAddr2) {
            this.dstAddr = dstAddr2;
            return this;
        }

        public Builder setMode(Mode mode2) {
            this.mode = mode2;
            return this;
        }

        public Builder addTransportCandidate(JingleS5BTransportCandidate candidate) {
            if (this.info == null) {
                this.candidates.add(candidate);
                return this;
            }
            throw new IllegalStateException("Builder has already an info set. The transport can only have either an info or transport candidates, not both.");
        }

        public Builder setTransportInfo(JingleContentTransportInfo info2) {
            if (!this.candidates.isEmpty()) {
                throw new IllegalStateException("Builder has already at least one candidate set. The transport can only have either an info or transport candidates, not both.");
            } else if (this.info == null) {
                this.info = info2;
                return this;
            } else {
                throw new IllegalStateException("Builder has already an info set.");
            }
        }

        public Builder setCandidateUsed(String candidateId) {
            return setTransportInfo(new CandidateUsed(candidateId));
        }

        public Builder setCandidateActivated(String candidateId) {
            return setTransportInfo(new CandidateActivated(candidateId));
        }

        public Builder setCandidateError() {
            return setTransportInfo(CandidateError.INSTANCE);
        }

        public Builder setProxyError() {
            return setTransportInfo(ProxyError.INSTANCE);
        }

        public JingleS5BTransport build() {
            JingleS5BTransport jingleS5BTransport = new JingleS5BTransport(this.candidates, this.info, this.streamId, this.dstAddr, this.mode);
            return jingleS5BTransport;
        }
    }

    protected JingleS5BTransport(List<JingleContentTransportCandidate> candidates, JingleContentTransportInfo info, String streamId2, String dstAddr2, Mode mode2) {
        super(candidates, info);
        StringUtils.requireNotNullOrEmpty(streamId2, "sid MUST be neither null, nor empty.");
        this.streamId = streamId2;
        this.dstAddr = dstAddr2;
        this.mode = mode2;
    }

    public String getStreamId() {
        return this.streamId;
    }

    public String getDestinationAddress() {
        return this.dstAddr;
    }

    public Mode getMode() {
        Mode mode2 = this.mode;
        return mode2 == null ? Mode.tcp : mode2;
    }

    public String getNamespace() {
        return NAMESPACE_V1;
    }

    /* access modifiers changed from: protected */
    public void addExtraAttributes(XmlStringBuilder xml) {
        xml.optAttribute(ATTR_DSTADDR, this.dstAddr);
        xml.optAttribute(ATTR_MODE, (Enum<?>) this.mode);
        xml.attribute("sid", this.streamId);
    }

    public boolean hasCandidate(String candidateId) {
        return getCandidate(candidateId) != null;
    }

    public JingleS5BTransportCandidate getCandidate(String candidateId) {
        for (JingleContentTransportCandidate c : this.candidates) {
            JingleS5BTransportCandidate candidate = (JingleS5BTransportCandidate) c;
            if (candidate.getCandidateId().equals(candidateId)) {
                return candidate;
            }
        }
        return null;
    }

    public static Builder getBuilder() {
        return new Builder();
    }
}
