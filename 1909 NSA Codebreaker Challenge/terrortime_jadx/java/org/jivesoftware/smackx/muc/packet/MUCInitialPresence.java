package org.jivesoftware.smackx.muc.packet;

import java.util.Date;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.last_interaction.element.IdleElement;
import org.jxmpp.util.XmppDateTime;

public class MUCInitialPresence implements ExtensionElement {
    public static final String ELEMENT = "x";
    public static final String NAMESPACE = "http://jabber.org/protocol/muc";
    private History history;
    private String password;

    public static class History implements NamedElement {
        public static final String ELEMENT = "history";
        private int maxChars;
        private int maxStanzas;
        private int seconds;
        private Date since;

        @Deprecated
        public History() {
            this.maxChars = -1;
            this.maxStanzas = -1;
            this.seconds = -1;
        }

        public History(int maxChars2, int maxStanzas2, int seconds2, Date since2) {
            if (maxChars2 >= 0 || maxStanzas2 >= 0 || seconds2 >= 0 || since2 != null) {
                this.maxChars = maxChars2;
                this.maxStanzas = maxStanzas2;
                this.seconds = seconds2;
                this.since = since2;
                return;
            }
            throw new IllegalArgumentException();
        }

        public int getMaxChars() {
            return this.maxChars;
        }

        public int getMaxStanzas() {
            return this.maxStanzas;
        }

        public int getSeconds() {
            return this.seconds;
        }

        public Date getSince() {
            return this.since;
        }

        @Deprecated
        public void setMaxChars(int maxChars2) {
            this.maxChars = maxChars2;
        }

        @Deprecated
        public void setMaxStanzas(int maxStanzas2) {
            this.maxStanzas = maxStanzas2;
        }

        @Deprecated
        public void setSeconds(int seconds2) {
            this.seconds = seconds2;
        }

        @Deprecated
        public void setSince(Date since2) {
            this.since = since2;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.optIntAttribute("maxchars", getMaxChars());
            xml.optIntAttribute("maxstanzas", getMaxStanzas());
            xml.optIntAttribute("seconds", getSeconds());
            if (getSince() != null) {
                xml.attribute(IdleElement.ATTR_SINCE, XmppDateTime.formatXEP0082Date(getSince()));
            }
            xml.closeEmptyElement();
            return xml;
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    @Deprecated
    public MUCInitialPresence() {
    }

    public MUCInitialPresence(String password2, int maxChars, int maxStanzas, int seconds, Date since) {
        this.password = password2;
        if (maxChars > -1 || maxStanzas > -1 || seconds > -1 || since != null) {
            this.history = new History(maxChars, maxStanzas, seconds, since);
        } else {
            this.history = null;
        }
    }

    public String getElementName() {
        return "x";
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.rightAngleBracket();
        xml.optElement("password", getPassword());
        xml.optElement(getHistory());
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public History getHistory() {
        return this.history;
    }

    public String getPassword() {
        return this.password;
    }

    @Deprecated
    public void setHistory(History history2) {
        this.history = history2;
    }

    @Deprecated
    public void setPassword(String password2) {
        this.password = password2;
    }

    @Deprecated
    public static MUCInitialPresence getFrom(Stanza packet) {
        return from(packet);
    }

    public static MUCInitialPresence from(Stanza packet) {
        return (MUCInitialPresence) packet.getExtension("x", NAMESPACE);
    }
}
