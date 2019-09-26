package org.jivesoftware.smack.sasl.packet;

import java.util.Map;
import org.jivesoftware.smack.packet.AbstractError;
import org.jivesoftware.smack.packet.Nonza;
import org.jivesoftware.smack.sasl.SASLError;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class SaslStreamElements {
    public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-sasl";

    public static class AuthMechanism implements Nonza {
        public static final String ELEMENT = "auth";
        private final String authenticationText;
        private final String mechanism;

        public AuthMechanism(String mechanism2, String authenticationText2) {
            this.mechanism = (String) Objects.requireNonNull(mechanism2, "SASL mechanism shouldn't be null.");
            this.authenticationText = (String) StringUtils.requireNotNullOrEmpty(authenticationText2, "SASL authenticationText must not be null or empty (RFC6120 6.4.2)");
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            String str = ELEMENT;
            xml.halfOpenElement(str).xmlnsAttribute("urn:ietf:params:xml:ns:xmpp-sasl").attribute("mechanism", this.mechanism).rightAngleBracket();
            xml.optAppend((CharSequence) this.authenticationText);
            xml.closeElement(str);
            return xml;
        }

        public String getMechanism() {
            return this.mechanism;
        }

        public String getAuthenticationText() {
            return this.authenticationText;
        }

        public String getNamespace() {
            return "urn:ietf:params:xml:ns:xmpp-sasl";
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class Challenge implements Nonza {
        public static final String ELEMENT = "challenge";
        private final String data;

        public Challenge(String data2) {
            this.data = StringUtils.returnIfNotEmptyTrimmed(data2);
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xmlStringBuilder = new XmlStringBuilder();
            String str = ELEMENT;
            XmlStringBuilder xml = xmlStringBuilder.halfOpenElement(str).xmlnsAttribute("urn:ietf:params:xml:ns:xmpp-sasl").rightAngleBracket();
            xml.optAppend((CharSequence) this.data);
            xml.closeElement(str);
            return xml;
        }

        public String getNamespace() {
            return "urn:ietf:params:xml:ns:xmpp-sasl";
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class Response implements Nonza {
        public static final String ELEMENT = "response";
        private final String authenticationText;

        public Response() {
            this.authenticationText = null;
        }

        public Response(String authenticationText2) {
            this.authenticationText = StringUtils.returnIfNotEmptyTrimmed(authenticationText2);
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            String str = ELEMENT;
            xml.halfOpenElement(str).xmlnsAttribute("urn:ietf:params:xml:ns:xmpp-sasl").rightAngleBracket();
            xml.optAppend((CharSequence) this.authenticationText);
            xml.closeElement(str);
            return xml;
        }

        public String getAuthenticationText() {
            return this.authenticationText;
        }

        public String getNamespace() {
            return "urn:ietf:params:xml:ns:xmpp-sasl";
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class SASLFailure extends AbstractError implements Nonza {
        public static final String ELEMENT = "failure";
        private final SASLError saslError;
        private final String saslErrorString;

        public SASLFailure(String saslError2) {
            this(saslError2, null);
        }

        public SASLFailure(String saslError2, Map<String, String> descriptiveTexts) {
            super(descriptiveTexts);
            SASLError error = SASLError.fromString(saslError2);
            if (error == null) {
                this.saslError = SASLError.not_authorized;
            } else {
                this.saslError = error;
            }
            this.saslErrorString = saslError2;
        }

        public SASLError getSASLError() {
            return this.saslError;
        }

        public String getSASLErrorString() {
            return this.saslErrorString;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            String str = ELEMENT;
            xml.halfOpenElement(str).xmlnsAttribute("urn:ietf:params:xml:ns:xmpp-sasl").rightAngleBracket();
            xml.emptyElement(this.saslErrorString);
            addDescriptiveTextsAndExtensions(xml);
            xml.closeElement(str);
            return xml;
        }

        public String toString() {
            return toXML((String) null).toString();
        }

        public String getNamespace() {
            return "urn:ietf:params:xml:ns:xmpp-sasl";
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class Success implements Nonza {
        public static final String ELEMENT = "success";
        private final String data;

        public Success(String data2) {
            this.data = StringUtils.returnIfNotEmptyTrimmed(data2);
        }

        public String getData() {
            return this.data;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder();
            String str = ELEMENT;
            xml.halfOpenElement(str).xmlnsAttribute("urn:ietf:params:xml:ns:xmpp-sasl").rightAngleBracket();
            xml.optAppend((CharSequence) this.data);
            xml.closeElement(str);
            return xml;
        }

        public String getNamespace() {
            return "urn:ietf:params:xml:ns:xmpp-sasl";
        }

        public String getElementName() {
            return ELEMENT;
        }
    }
}
