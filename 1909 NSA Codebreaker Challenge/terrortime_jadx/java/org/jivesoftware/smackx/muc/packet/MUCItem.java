package org.jivesoftware.smackx.muc.packet;

import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.jingle.element.JingleReason;
import org.jivesoftware.smackx.muc.MUCAffiliation;
import org.jivesoftware.smackx.muc.MUCRole;
import org.jivesoftware.smackx.nick.packet.Nick;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;

public class MUCItem implements NamedElement {
    public static final String ELEMENT = "item";
    private final Jid actor;
    private final Resourcepart actorNick;
    private final MUCAffiliation affiliation;
    private final Jid jid;
    private final Resourcepart nick;
    private final String reason;
    private final MUCRole role;

    public MUCItem(MUCAffiliation affiliation2) {
        this(affiliation2, null, null, null, null, null, null);
    }

    public MUCItem(MUCRole role2) {
        this(null, role2, null, null, null, null, null);
    }

    public MUCItem(MUCRole role2, Resourcepart nick2) {
        this(null, role2, null, null, null, nick2, null);
    }

    public MUCItem(MUCAffiliation affiliation2, Jid jid2, String reason2) {
        this(affiliation2, null, null, reason2, jid2, null, null);
    }

    public MUCItem(MUCAffiliation affiliation2, Jid jid2) {
        this(affiliation2, null, null, null, jid2, null, null);
    }

    public MUCItem(MUCRole role2, Resourcepart nick2, String reason2) {
        this(null, role2, null, reason2, null, nick2, null);
    }

    public MUCItem(MUCAffiliation affiliation2, MUCRole role2, Jid actor2, String reason2, Jid jid2, Resourcepart nick2, Resourcepart actorNick2) {
        this.affiliation = affiliation2;
        this.role = role2;
        this.actor = actor2;
        this.reason = reason2;
        this.jid = jid2;
        this.nick = nick2;
        this.actorNick = actorNick2;
    }

    public Jid getActor() {
        return this.actor;
    }

    public Resourcepart getActorNick() {
        return this.actorNick;
    }

    public String getReason() {
        return this.reason;
    }

    public MUCAffiliation getAffiliation() {
        return this.affiliation;
    }

    public Jid getJid() {
        return this.jid;
    }

    public Resourcepart getNick() {
        return this.nick;
    }

    public MUCRole getRole() {
        return this.role;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((NamedElement) this);
        xml.optAttribute(Affiliation.ELEMENT, (Enum<?>) getAffiliation());
        String str = "jid";
        xml.optAttribute(str, (CharSequence) getJid());
        xml.optAttribute(Nick.ELEMENT_NAME, (CharSequence) getNick());
        xml.optAttribute("role", (Enum<?>) getRole());
        xml.rightAngleBracket();
        xml.optElement(JingleReason.ELEMENT, getReason());
        if (getActor() != null) {
            xml.halfOpenElement("actor").attribute(str, (CharSequence) getActor()).closeEmptyElement();
        }
        xml.closeElement("item");
        return xml;
    }

    public String getElementName() {
        return "item";
    }
}
