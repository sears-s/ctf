package org.jivesoftware.smackx.muc.packet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import okhttp3.internal.http.StatusLine;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.jingle.element.JingleReason;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.EntityJid;

public class MUCUser implements ExtensionElement {
    public static final String ELEMENT = "x";
    public static final String NAMESPACE = "http://jabber.org/protocol/muc#user";
    private Decline decline;
    private Destroy destroy;
    private Invite invite;
    private MUCItem item;
    private String password;
    private final Set<Status> statusCodes = new HashSet(4);

    public static class Decline implements NamedElement {
        public static final String ELEMENT = "decline";
        private final EntityBareJid from;
        private final String reason;
        private final EntityBareJid to;

        public Decline(String reason2, EntityBareJid to2) {
            this(reason2, null, to2);
        }

        public Decline(String reason2, EntityBareJid from2, EntityBareJid to2) {
            this.reason = reason2;
            this.from = from2;
            this.to = to2;
        }

        public EntityBareJid getFrom() {
            return this.from;
        }

        public String getReason() {
            return this.reason;
        }

        public EntityBareJid getTo() {
            return this.to;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.optAttribute(PrivacyItem.SUBSCRIPTION_TO, (CharSequence) getTo());
            xml.optAttribute(PrivacyItem.SUBSCRIPTION_FROM, (CharSequence) getFrom());
            xml.rightAngleBracket();
            xml.optElement(JingleReason.ELEMENT, getReason());
            xml.closeElement((NamedElement) this);
            return xml;
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static class Invite implements NamedElement {
        public static final String ELEMENT = "invite";
        private final EntityJid from;
        private final String reason;
        private final EntityBareJid to;

        public Invite(String reason2, EntityFullJid from2) {
            this(reason2, from2, null);
        }

        public Invite(String reason2, EntityBareJid to2) {
            this(reason2, null, to2);
        }

        public Invite(String reason2, EntityJid from2, EntityBareJid to2) {
            this.reason = reason2;
            this.from = from2;
            this.to = to2;
        }

        public EntityJid getFrom() {
            return this.from;
        }

        public String getReason() {
            return this.reason;
        }

        public EntityBareJid getTo() {
            return this.to;
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.optAttribute(PrivacyItem.SUBSCRIPTION_TO, (CharSequence) getTo());
            xml.optAttribute(PrivacyItem.SUBSCRIPTION_FROM, (CharSequence) getFrom());
            xml.rightAngleBracket();
            xml.optElement(JingleReason.ELEMENT, getReason());
            xml.closeElement((NamedElement) this);
            return xml;
        }

        public String getElementName() {
            return ELEMENT;
        }
    }

    public static final class Status implements NamedElement {
        public static final Status BANNED_301 = create(Integer.valueOf(301));
        public static final String ELEMENT = "status";
        public static final Status KICKED_307 = create(Integer.valueOf(StatusLine.HTTP_TEMP_REDIRECT));
        public static final Status NEW_NICKNAME_303 = create(Integer.valueOf(303));
        public static final Status PRESENCE_TO_SELF_110 = create(Integer.valueOf(110));
        public static final Status REMOVED_AFFIL_CHANGE_321 = create(Integer.valueOf(321));
        public static final Status ROOM_CREATED_201 = create(Integer.valueOf(201));
        private static final Map<Integer, Status> statusMap = new HashMap(8);
        private final Integer code;

        public static Status create(String string) {
            return create(Integer.valueOf(string));
        }

        public static Status create(Integer i) {
            Status status = (Status) statusMap.get(i);
            if (status != null) {
                return status;
            }
            Status status2 = new Status(i.intValue());
            statusMap.put(i, status2);
            return status2;
        }

        private Status(int code2) {
            this.code = Integer.valueOf(code2);
        }

        public int getCode() {
            return this.code.intValue();
        }

        public XmlStringBuilder toXML(String enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
            xml.attribute("code", getCode());
            xml.closeEmptyElement();
            return xml;
        }

        public String toString() {
            return this.code.toString();
        }

        public boolean equals(Object other) {
            if (other == null || !(other instanceof Status)) {
                return false;
            }
            return this.code.equals(Integer.valueOf(((Status) other).getCode()));
        }

        public int hashCode() {
            return this.code.intValue();
        }

        public String getElementName() {
            return "status";
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
        xml.optElement(getInvite());
        xml.optElement(getDecline());
        xml.optElement(getItem());
        xml.optElement("password", getPassword());
        xml.append((Collection<? extends Element>) this.statusCodes);
        xml.optElement(getDestroy());
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public Invite getInvite() {
        return this.invite;
    }

    public Decline getDecline() {
        return this.decline;
    }

    public MUCItem getItem() {
        return this.item;
    }

    public String getPassword() {
        return this.password;
    }

    public Set<Status> getStatus() {
        return this.statusCodes;
    }

    public boolean hasStatus() {
        return !this.statusCodes.isEmpty();
    }

    public Destroy getDestroy() {
        return this.destroy;
    }

    public void setInvite(Invite invite2) {
        this.invite = invite2;
    }

    public void setDecline(Decline decline2) {
        this.decline = decline2;
    }

    public void setItem(MUCItem item2) {
        this.item = item2;
    }

    public void setPassword(String string) {
        this.password = string;
    }

    public void addStatusCodes(Set<Status> statusCodes2) {
        this.statusCodes.addAll(statusCodes2);
    }

    public void addStatusCode(Status status) {
        this.statusCodes.add(status);
    }

    public void setDestroy(Destroy destroy2) {
        this.destroy = destroy2;
    }

    @Deprecated
    public static MUCUser getFrom(Stanza packet) {
        return from(packet);
    }

    public static MUCUser from(Stanza packet) {
        return (MUCUser) packet.getExtension("x", NAMESPACE);
    }
}
