package org.jivesoftware.smack.packet;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.sid.element.StanzaIdElement;

public class StanzaError extends AbstractError implements ExtensionElement {
    static final Map<Condition, Type> CONDITION_TO_TYPE = new HashMap();
    public static final String ERROR = "error";
    public static final String ERROR_CONDITION_AND_TEXT_NAMESPACE = "urn:ietf:params:xml:ns:xmpp-stanzas";
    private static final Logger LOGGER = Logger.getLogger(StanzaError.class.getName());
    @Deprecated
    public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-stanzas";
    private final Condition condition;
    private final String conditionText;
    private final String errorGenerator;
    private final Stanza stanza;
    private final Type type;

    /* renamed from: org.jivesoftware.smack.packet.StanzaError$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$StanzaError$Condition = new int[Condition.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$packet$StanzaError$Condition[Condition.gone.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$StanzaError$Condition[Condition.redirect.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public static final class Builder extends org.jivesoftware.smack.packet.AbstractError.Builder<Builder> {
        private Condition condition;
        private String conditionText;
        private String errorGenerator;
        private Stanza stanza;
        private Type type;

        /* synthetic */ Builder(AnonymousClass1 x0) {
            this();
        }

        private Builder() {
        }

        public Builder setCondition(Condition condition2) {
            this.condition = condition2;
            return this;
        }

        public Builder setType(Type type2) {
            this.type = type2;
            return this;
        }

        public Builder setConditionText(String conditionText2) {
            this.conditionText = conditionText2;
            return this;
        }

        public Builder setErrorGenerator(String errorGenerator2) {
            this.errorGenerator = errorGenerator2;
            return this;
        }

        public Builder setStanza(Stanza stanza2) {
            this.stanza = stanza2;
            return this;
        }

        public Builder copyFrom(StanzaError xmppError) {
            setCondition(xmppError.getCondition());
            setType(xmppError.getType());
            setConditionText(xmppError.getConditionText());
            setErrorGenerator(xmppError.getErrorGenerator());
            setStanza(xmppError.getStanza());
            setDescriptiveTexts(xmppError.descriptiveTexts);
            setTextNamespace(xmppError.textNamespace);
            setExtensions(xmppError.extensions);
            return this;
        }

        public StanzaError build() {
            StanzaError stanzaError = new StanzaError(this.condition, this.conditionText, this.errorGenerator, this.type, this.descriptiveTexts, this.extensions, this.stanza);
            return stanzaError;
        }

        /* access modifiers changed from: protected */
        public Builder getThis() {
            return this;
        }
    }

    public enum Condition {
        bad_request,
        conflict,
        feature_not_implemented,
        forbidden,
        gone,
        internal_server_error,
        item_not_found,
        jid_malformed,
        not_acceptable,
        not_allowed,
        not_authorized,
        policy_violation,
        recipient_unavailable,
        redirect,
        registration_required,
        remote_server_not_found,
        remote_server_timeout,
        resource_constraint,
        service_unavailable,
        subscription_required,
        undefined_condition,
        unexpected_request;

        public String toString() {
            return name().replace('_', '-');
        }

        public static Condition fromString(String string) {
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

    public enum Type {
        WAIT,
        CANCEL,
        MODIFY,
        AUTH,
        CONTINUE;

        public String toString() {
            return name().toLowerCase(Locale.US);
        }

        public static Type fromString(String string) {
            return valueOf(string.toUpperCase(Locale.US));
        }
    }

    static {
        CONDITION_TO_TYPE.put(Condition.bad_request, Type.MODIFY);
        CONDITION_TO_TYPE.put(Condition.conflict, Type.CANCEL);
        CONDITION_TO_TYPE.put(Condition.feature_not_implemented, Type.CANCEL);
        CONDITION_TO_TYPE.put(Condition.forbidden, Type.AUTH);
        CONDITION_TO_TYPE.put(Condition.gone, Type.CANCEL);
        CONDITION_TO_TYPE.put(Condition.internal_server_error, Type.CANCEL);
        CONDITION_TO_TYPE.put(Condition.item_not_found, Type.CANCEL);
        CONDITION_TO_TYPE.put(Condition.jid_malformed, Type.MODIFY);
        CONDITION_TO_TYPE.put(Condition.not_acceptable, Type.MODIFY);
        CONDITION_TO_TYPE.put(Condition.not_allowed, Type.CANCEL);
        CONDITION_TO_TYPE.put(Condition.not_authorized, Type.AUTH);
        CONDITION_TO_TYPE.put(Condition.policy_violation, Type.MODIFY);
        CONDITION_TO_TYPE.put(Condition.recipient_unavailable, Type.WAIT);
        CONDITION_TO_TYPE.put(Condition.redirect, Type.MODIFY);
        CONDITION_TO_TYPE.put(Condition.registration_required, Type.AUTH);
        CONDITION_TO_TYPE.put(Condition.remote_server_not_found, Type.CANCEL);
        CONDITION_TO_TYPE.put(Condition.remote_server_timeout, Type.WAIT);
        CONDITION_TO_TYPE.put(Condition.resource_constraint, Type.WAIT);
        CONDITION_TO_TYPE.put(Condition.service_unavailable, Type.CANCEL);
        CONDITION_TO_TYPE.put(Condition.subscription_required, Type.AUTH);
        CONDITION_TO_TYPE.put(Condition.undefined_condition, Type.MODIFY);
        CONDITION_TO_TYPE.put(Condition.unexpected_request, Type.WAIT);
    }

    public StanzaError(Condition condition2, String conditionText2, String errorGenerator2, Type type2, Map<String, String> descriptiveTexts, List<ExtensionElement> extensions, Stanza stanza2) {
        super(descriptiveTexts, "urn:ietf:params:xml:ns:xmpp-stanzas", extensions);
        this.condition = (Condition) Objects.requireNonNull(condition2, "condition must not be null");
        this.stanza = stanza2;
        if (StringUtils.isNullOrEmpty((CharSequence) conditionText2)) {
            conditionText2 = null;
        }
        if (conditionText2 != null) {
            int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$packet$StanzaError$Condition[condition2.ordinal()];
            if (!(i == 1 || i == 2)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Condition text can only be set with condtion types 'gone' and 'redirect', not ");
                sb.append(condition2);
                throw new IllegalArgumentException(sb.toString());
            }
        }
        this.conditionText = conditionText2;
        this.errorGenerator = errorGenerator2;
        if (type2 == null) {
            Type determinedType = (Type) CONDITION_TO_TYPE.get(condition2);
            if (determinedType == null) {
                Logger logger = LOGGER;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Could not determine type for condition: ");
                sb2.append(condition2);
                logger.warning(sb2.toString());
                determinedType = Type.CANCEL;
            }
            this.type = determinedType;
            return;
        }
        this.type = type2;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public Type getType() {
        return this.type;
    }

    public String getErrorGenerator() {
        return this.errorGenerator;
    }

    public String getConditionText() {
        return this.conditionText;
    }

    public Stanza getStanza() {
        return this.stanza;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("XMPPError: ");
        sb.append(this.condition.toString());
        sb.append(" - ");
        sb.append(this.type.toString());
        String descriptiveText = getDescriptiveText();
        if (descriptiveText != null) {
            sb.append(" [");
            sb.append(descriptiveText);
            sb.append(']');
        }
        if (this.errorGenerator != null) {
            sb.append(". Generated by ");
            sb.append(this.errorGenerator);
        }
        return sb.toString();
    }

    public String getElementName() {
        return "error";
    }

    public String getNamespace() {
        return "jabber:client";
    }

    public XmlStringBuilder toXML() {
        return toXML((String) null);
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder(this, enclosingNamespace);
        xml.attribute("type", this.type.toString());
        xml.optAttribute(StanzaIdElement.ATTR_BY, this.errorGenerator);
        xml.rightAngleBracket();
        xml.halfOpenElement(this.condition.toString());
        xml.xmlnsAttribute("urn:ietf:params:xml:ns:xmpp-stanzas");
        if (this.conditionText != null) {
            xml.rightAngleBracket();
            xml.escape(this.conditionText);
            xml.closeElement(this.condition.toString());
        } else {
            xml.closeEmptyElement();
        }
        addDescriptiveTextsAndExtensions(xml);
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public static Builder from(Condition condition2, String descriptiveText) {
        Builder builder = getBuilder().setCondition(condition2);
        if (descriptiveText != null) {
            Map<String, String> descriptiveTexts = new HashMap<>();
            descriptiveTexts.put("en", descriptiveText);
            builder.setDescriptiveTexts(descriptiveTexts);
        }
        return builder;
    }

    public static Builder getBuilder() {
        return new Builder(null);
    }

    public static Builder getBuilder(Condition condition2) {
        return getBuilder().setCondition(condition2);
    }

    public static Builder getBuilder(StanzaError xmppError) {
        return getBuilder().copyFrom(xmppError);
    }
}
