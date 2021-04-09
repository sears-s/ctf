package org.jivesoftware.smackx.jingle.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.jingle.element.JingleReason.Reason;
import org.jxmpp.jid.FullJid;

public final class Jingle extends IQ {
    public static final String ACTION_ATTRIBUTE_NAME = "action";
    public static final String ELEMENT = "jingle";
    public static final String INITIATOR_ATTRIBUTE_NAME = "initiator";
    public static final String NAMESPACE = "urn:xmpp:jingle:1";
    public static final String RESPONDER_ATTRIBUTE_NAME = "responder";
    public static final String SESSION_ID_ATTRIBUTE_NAME = "sid";
    private final JingleAction action;
    private final List<JingleContent> contents;
    private final FullJid initiator;
    private final JingleReason reason;
    private final FullJid responder;
    private final String sessionId;

    public static final class Builder {
        private JingleAction action;
        private List<JingleContent> contents;
        private FullJid initiator;
        private JingleReason reason;
        private FullJid responder;
        private String sid;

        private Builder() {
        }

        public Builder setSessionId(String sessionId) {
            StringUtils.requireNotNullOrEmpty(sessionId, "Session ID must not be null or empty");
            this.sid = sessionId;
            return this;
        }

        public Builder setAction(JingleAction action2) {
            this.action = action2;
            return this;
        }

        public Builder setInitiator(FullJid initator) {
            this.initiator = initator;
            return this;
        }

        public Builder setResponder(FullJid responder2) {
            this.responder = responder2;
            return this;
        }

        public Builder addJingleContent(JingleContent content) {
            if (this.contents == null) {
                this.contents = new ArrayList(1);
            }
            this.contents.add(content);
            return this;
        }

        public Builder setReason(Reason reason2) {
            this.reason = new JingleReason(reason2);
            return this;
        }

        public Builder setReason(JingleReason reason2) {
            this.reason = reason2;
            return this;
        }

        public Jingle build() {
            Jingle jingle = new Jingle(this.sid, this.action, this.initiator, this.responder, this.reason, this.contents);
            return jingle;
        }
    }

    private Jingle(String sessionId2, JingleAction action2, FullJid initiator2, FullJid responder2, JingleReason reason2, List<JingleContent> contents2) {
        super(ELEMENT, NAMESPACE);
        this.sessionId = (String) StringUtils.requireNotNullOrEmpty(sessionId2, "Jingle session ID must not be null");
        this.action = (JingleAction) Objects.requireNonNull(action2, "Jingle action must not be null");
        this.initiator = initiator2;
        this.responder = responder2;
        this.reason = reason2;
        if (contents2 != null) {
            this.contents = Collections.unmodifiableList(contents2);
        } else {
            this.contents = Collections.emptyList();
        }
        setType(Type.set);
    }

    public FullJid getInitiator() {
        return this.initiator;
    }

    public FullJid getResponder() {
        return this.responder;
    }

    public String getSid() {
        return this.sessionId;
    }

    public JingleAction getAction() {
        return this.action;
    }

    public JingleReason getReason() {
        return this.reason;
    }

    public List<JingleContent> getContents() {
        return this.contents;
    }

    public JingleContent getSoleContentOrThrow() {
        if (this.contents.isEmpty()) {
            return null;
        }
        if (this.contents.size() <= 1) {
            return (JingleContent) this.contents.get(0);
        }
        throw new IllegalStateException();
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.optAttribute(INITIATOR_ATTRIBUTE_NAME, (CharSequence) getInitiator());
        xml.optAttribute(RESPONDER_ATTRIBUTE_NAME, (CharSequence) getResponder());
        xml.optAttribute("action", (Enum<?>) getAction());
        xml.optAttribute("sid", getSid());
        xml.rightAngleBracket();
        xml.optElement(this.reason);
        xml.append((Collection<? extends Element>) this.contents);
        return xml;
    }

    public static Builder getBuilder() {
        return new Builder();
    }
}
