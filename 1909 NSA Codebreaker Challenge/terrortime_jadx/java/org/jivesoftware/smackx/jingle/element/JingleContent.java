package org.jivesoftware.smackx.jingle.element;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;

public final class JingleContent implements NamedElement {
    public static final String CREATOR_ATTRIBUTE_NAME = "creator";
    public static final String DISPOSITION_ATTRIBUTE_NAME = "disposition";
    public static final String ELEMENT = "content";
    public static final String NAME_ATTRIBUTE_NAME = "name";
    public static final String SENDERS_ATTRIBUTE_NAME = "senders";
    private final Creator creator;
    private final JingleContentDescription description;
    private final String disposition;
    private final String name;
    private final Senders senders;
    private final JingleContentTransport transport;

    public static final class Builder {
        private Creator creator;
        private JingleContentDescription description;
        private String disposition;
        private String name;
        private Senders senders;
        private JingleContentTransport transport;

        private Builder() {
        }

        public Builder setCreator(Creator creator2) {
            this.creator = creator2;
            return this;
        }

        public Builder setDisposition(String disposition2) {
            this.disposition = disposition2;
            return this;
        }

        public Builder setName(String name2) {
            this.name = name2;
            return this;
        }

        public Builder setSenders(Senders senders2) {
            this.senders = senders2;
            return this;
        }

        public Builder setDescription(JingleContentDescription description2) {
            if (this.description == null) {
                this.description = description2;
                return this;
            }
            throw new IllegalStateException("Jingle content description already set");
        }

        public Builder setTransport(JingleContentTransport transport2) {
            this.transport = transport2;
            return this;
        }

        public JingleContent build() {
            JingleContent jingleContent = new JingleContent(this.creator, this.disposition, this.name, this.senders, this.description, this.transport);
            return jingleContent;
        }
    }

    public enum Creator {
        initiator,
        responder
    }

    public enum Senders {
        both,
        initiator,
        none,
        responder
    }

    private JingleContent(Creator creator2, String disposition2, String name2, Senders senders2, JingleContentDescription description2, JingleContentTransport transport2) {
        this.creator = (Creator) Objects.requireNonNull(creator2, "Jingle content creator must not be null");
        this.disposition = disposition2;
        this.name = (String) StringUtils.requireNotNullOrEmpty(name2, "Jingle content name must not be null or empty");
        this.senders = senders2;
        this.description = description2;
        this.transport = transport2;
    }

    public Creator getCreator() {
        return this.creator;
    }

    public String getDisposition() {
        return this.disposition;
    }

    public String getName() {
        return this.name;
    }

    public Senders getSenders() {
        return this.senders;
    }

    public JingleContentDescription getDescription() {
        return this.description;
    }

    @Deprecated
    public JingleContentTransport getJingleTransport() {
        return getTransport();
    }

    public JingleContentTransport getTransport() {
        return this.transport;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
        xml.attribute("creator", (Enum<?>) this.creator);
        xml.optAttribute(DISPOSITION_ATTRIBUTE_NAME, this.disposition);
        xml.attribute("name", this.name);
        xml.optAttribute(SENDERS_ATTRIBUTE_NAME, (Enum<?>) this.senders);
        xml.rightAngleBracket();
        xml.optAppend((Element) this.description);
        xml.optElement(this.transport);
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public static Builder getBuilder() {
        return new Builder();
    }
}
