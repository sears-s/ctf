package org.jivesoftware.smackx.last_interaction.element;

import java.util.Date;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.Objects;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class IdleElement implements ExtensionElement {
    public static final String ATTR_SINCE = "since";
    public static final String ELEMENT = "idle";
    public static final String NAMESPACE = "urn:xmpp:idle:1";
    private final Date since;

    public IdleElement() {
        this(new Date());
    }

    public IdleElement(Date since2) {
        this.since = (Date) Objects.requireNonNull(since2);
    }

    public Date getSince() {
        return this.since;
    }

    public static void addToPresence(Presence presence) {
        presence.addExtension(new IdleElement());
    }

    public static IdleElement fromPresence(Presence presence) {
        return (IdleElement) presence.getExtension(ELEMENT, NAMESPACE);
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        return new XmlStringBuilder((ExtensionElement) this).attribute(ATTR_SINCE, this.since).closeEmptyElement();
    }
}
