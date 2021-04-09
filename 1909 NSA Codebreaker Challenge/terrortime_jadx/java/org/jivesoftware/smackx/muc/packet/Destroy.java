package org.jivesoftware.smackx.muc.packet;

import java.io.Serializable;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.jingle.element.JingleReason;
import org.jxmpp.jid.EntityBareJid;

public class Destroy implements NamedElement, Serializable {
    public static final String ELEMENT = "destroy";
    private static final long serialVersionUID = 1;
    private final EntityBareJid jid;
    private final String reason;

    public Destroy(Destroy other) {
        this(other.jid, other.reason);
    }

    public Destroy(EntityBareJid alternativeJid, String reason2) {
        this.jid = alternativeJid;
        this.reason = reason2;
    }

    public EntityBareJid getJid() {
        return this.jid;
    }

    public String getReason() {
        return this.reason;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
        xml.optAttribute("jid", (CharSequence) getJid());
        xml.rightAngleBracket();
        xml.optElement(JingleReason.ELEMENT, getReason());
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public Destroy clone() {
        return new Destroy(this);
    }
}
