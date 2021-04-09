package org.jivesoftware.smackx.jingle.element;

import java.util.HashMap;
import java.util.Map;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class JingleReason implements NamedElement {
    public static final JingleReason Busy = new JingleReason(Reason.busy);
    public static final JingleReason Cancel = new JingleReason(Reason.cancel);
    public static final JingleReason ConnectivityError = new JingleReason(Reason.connectivity_error);
    public static final JingleReason Decline = new JingleReason(Reason.decline);
    public static final String ELEMENT = "reason";
    public static final JingleReason Expired = new JingleReason(Reason.expired);
    public static final JingleReason FailedApplication = new JingleReason(Reason.failed_application);
    public static final JingleReason FailedTransport = new JingleReason(Reason.failed_transport);
    public static final JingleReason GeneralError = new JingleReason(Reason.general_error);
    public static final JingleReason Gone = new JingleReason(Reason.gone);
    public static final JingleReason IncompatibleParameters = new JingleReason(Reason.incompatible_parameters);
    public static final JingleReason MediaError = new JingleReason(Reason.media_error);
    public static final JingleReason SecurityError = new JingleReason(Reason.security_error);
    public static final JingleReason Success = new JingleReason(Reason.success);
    public static final JingleReason Timeout = new JingleReason(Reason.timeout);
    public static final JingleReason UnsupportedApplications = new JingleReason(Reason.unsupported_applications);
    public static final JingleReason UnsupportedTransports = new JingleReason(Reason.unsupported_transports);
    protected final Reason reason;

    public static class AlternativeSession extends JingleReason {
        public static final String SID = "sid";
        private final String sessionId;

        public AlternativeSession(String sessionId2) {
            super(Reason.alternative_session);
            if (!StringUtils.isNullOrEmpty((CharSequence) sessionId2)) {
                this.sessionId = sessionId2;
                return;
            }
            throw new NullPointerException("SessionID must not be null or empty.");
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.rightAngleBracket();
            xml.openElement(this.reason.asString);
            String str = "sid";
            xml.openElement(str);
            xml.append((CharSequence) this.sessionId);
            xml.closeElement(str);
            xml.closeElement(this.reason.asString);
            xml.closeElement((NamedElement) this);
            return xml;
        }

        public String getAlternativeSessionId() {
            return this.sessionId;
        }
    }

    public enum Reason {
        alternative_session,
        busy,
        cancel,
        connectivity_error,
        decline,
        expired,
        failed_application,
        failed_transport,
        general_error,
        gone,
        incompatible_parameters,
        media_error,
        security_error,
        success,
        timeout,
        unsupported_applications,
        unsupported_transports;
        
        protected static final Map<String, Reason> LUT = null;
        protected final String asString;

        static {
            int i;
            Reason[] values;
            LUT = new HashMap(values().length);
            for (Reason reason : values()) {
                LUT.put(reason.toString(), reason);
            }
        }

        public String toString() {
            return this.asString;
        }

        public static Reason fromString(String string) {
            Reason reason = (Reason) LUT.get(string);
            if (reason != null) {
                return reason;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Unknown reason: ");
            sb.append(string);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public static AlternativeSession AlternativeSession(String sessionId) {
        return new AlternativeSession(sessionId);
    }

    public JingleReason(Reason reason2) {
        this.reason = reason2;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
        xml.rightAngleBracket();
        xml.emptyElement(this.reason.asString);
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public Reason asEnum() {
        return this.reason;
    }
}
