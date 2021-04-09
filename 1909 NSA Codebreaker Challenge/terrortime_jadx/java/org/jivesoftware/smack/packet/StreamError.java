package org.jivesoftware.smack.packet;

import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class StreamError extends AbstractError implements Nonza {
    public static final String ELEMENT = "stream:error";
    public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-streams";
    private final Condition condition;
    private final String conditionText;

    /* renamed from: org.jivesoftware.smack.packet.StreamError$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$StreamError$Condition = new int[Condition.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$packet$StreamError$Condition[Condition.see_other_host.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public enum Condition {
        bad_format,
        bad_namespace_prefix,
        conflict,
        connection_timeout,
        host_gone,
        host_unknown,
        improper_addressing,
        internal_server_error,
        invalid_from,
        invalid_namespace,
        invalid_xml,
        not_authorized,
        not_well_formed,
        policy_violation,
        remote_connection_failed,
        reset,
        resource_constraint,
        restricted_xml,
        see_other_host,
        system_shutdown,
        undefined_condition,
        unsupported_encoding,
        unsupported_feature,
        unsupported_stanza_type,
        unsupported_version;

        public String toString() {
            return name().replace('_', '-');
        }

        public static Condition fromString(String string) {
            if ("xml-not-well-formed".equals(string)) {
                string = "not-well-formed";
            }
            String string2 = string.replace('-', '_');
            try {
                return valueOf(string2);
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Could not transform string '");
                sb.append(string2);
                sb.append("' to XMPPErrorCondition");
                throw new IllegalStateException(sb.toString(), e);
            }
        }
    }

    public StreamError(Condition condition2, String conditionText2, Map<String, String> descriptiveTexts, List<ExtensionElement> extensions) {
        super(descriptiveTexts, extensions);
        if (StringUtils.isNullOrEmpty((CharSequence) conditionText2)) {
            conditionText2 = null;
        }
        if (conditionText2 == null || AnonymousClass1.$SwitchMap$org$jivesoftware$smack$packet$StreamError$Condition[condition2.ordinal()] == 1) {
            this.condition = condition2;
            this.conditionText = conditionText2;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("The given condition '");
        sb.append(condition2);
        sb.append("' can not contain a conditionText");
        throw new IllegalArgumentException(sb.toString());
    }

    public Condition getCondition() {
        return this.condition;
    }

    public String getConditionText() {
        return this.conditionText;
    }

    public String toString() {
        return toXML((String) null).toString();
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder();
        String str = ELEMENT;
        xml.openElement(str);
        xml.halfOpenElement(this.condition.toString()).xmlnsAttribute(NAMESPACE).closeEmptyElement();
        addDescriptiveTextsAndExtensions(xml);
        xml.closeElement(str);
        return xml;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String getElementName() {
        return ELEMENT;
    }
}
