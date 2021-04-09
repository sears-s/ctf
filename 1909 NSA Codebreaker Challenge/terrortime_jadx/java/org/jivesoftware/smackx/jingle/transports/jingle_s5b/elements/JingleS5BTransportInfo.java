package org.jivesoftware.smackx.jingle.transports.jingle_s5b.elements;

import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.jingle.element.JingleContentTransportInfo;

public abstract class JingleS5BTransportInfo extends JingleContentTransportInfo {

    public static final class CandidateActivated extends JingleS5BCandidateTransportInfo {
        public static final String ELEMENT = "candidate-activated";

        public CandidateActivated(String candidateId) {
            super(candidateId);
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static final class CandidateError extends JingleS5BTransportInfo {
        public static final String ELEMENT = "candidate-error";
        public static final CandidateError INSTANCE = new CandidateError();

        private CandidateError() {
        }

        public String getElementName() {
            return ELEMENT;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement((NamedElement) this);
            xml.closeEmptyElement();
            return xml;
        }

        public boolean equals(Object other) {
            return other == INSTANCE;
        }

        public int hashCode() {
            return toXML((String) null).toString().hashCode();
        }
    }

    public static final class CandidateUsed extends JingleS5BCandidateTransportInfo {
        public static final String ELEMENT = "candidate-used";

        public CandidateUsed(String candidateId) {
            super(candidateId);
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static abstract class JingleS5BCandidateTransportInfo extends JingleS5BTransportInfo {
        public static final String ATTR_CID = "cid";
        private final String candidateId;

        protected JingleS5BCandidateTransportInfo(String candidateId2) {
            this.candidateId = candidateId2;
        }

        public final String getCandidateId() {
            return this.candidateId;
        }

        public final XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement((NamedElement) this);
            xml.attribute("cid", getCandidateId());
            xml.closeEmptyElement();
            return xml;
        }

        public final boolean equals(Object other) {
            if (!(other instanceof JingleS5BCandidateTransportInfo)) {
                return false;
            }
            return toXML((String) null).equals(((JingleS5BCandidateTransportInfo) other).toXML((String) null));
        }

        public final int hashCode() {
            return getCandidateId().hashCode();
        }
    }

    public static final class ProxyError extends JingleS5BTransportInfo {
        public static final String ELEMENT = "proxy-error";
        public static final ProxyError INSTANCE = new ProxyError();

        private ProxyError() {
        }

        public String getElementName() {
            return ELEMENT;
        }

        public CharSequence toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement((NamedElement) this);
            xml.closeEmptyElement();
            return xml;
        }

        public boolean equals(Object other) {
            return other == INSTANCE;
        }

        public int hashCode() {
            return toXML(null).toString().hashCode();
        }
    }
}
