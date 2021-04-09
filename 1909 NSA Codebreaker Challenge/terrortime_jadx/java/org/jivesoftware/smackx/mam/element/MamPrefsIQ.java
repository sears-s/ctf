package org.jivesoftware.smackx.mam.element;

import java.util.List;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.mam.element.MamElements.AlwaysJidListElement;
import org.jivesoftware.smackx.mam.element.MamElements.NeverJidListElement;
import org.jxmpp.jid.Jid;

public class MamPrefsIQ extends IQ {
    public static final String ELEMENT = "prefs";
    public static final String NAMESPACE = "urn:xmpp:mam:1";
    private final List<Jid> alwaysJids;
    private final DefaultBehavior defaultBehavior;
    private final List<Jid> neverJids;

    public enum DefaultBehavior {
        always,
        never,
        roster
    }

    public MamPrefsIQ() {
        super(ELEMENT, "urn:xmpp:mam:1");
        this.alwaysJids = null;
        this.neverJids = null;
        this.defaultBehavior = null;
    }

    public MamPrefsIQ(List<Jid> alwaysJids2, List<Jid> neverJids2, DefaultBehavior defaultBehavior2) {
        super(ELEMENT, "urn:xmpp:mam:1");
        setType(Type.set);
        this.alwaysJids = alwaysJids2;
        this.neverJids = neverJids2;
        this.defaultBehavior = defaultBehavior2;
    }

    public List<Jid> getAlwaysJids() {
        return this.alwaysJids;
    }

    public List<Jid> getNeverJids() {
        return this.neverJids;
    }

    public DefaultBehavior getDefault() {
        return this.defaultBehavior;
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        if (getType().equals(Type.set) || getType().equals(Type.result)) {
            xml.attribute("default", (Enum<?>) this.defaultBehavior);
        }
        if (this.alwaysJids == null && this.neverJids == null) {
            xml.setEmptyElement();
            return xml;
        }
        xml.rightAngleBracket();
        List<Jid> list = this.alwaysJids;
        if (list != null) {
            xml.element(new AlwaysJidListElement(list));
        }
        List<Jid> list2 = this.neverJids;
        if (list2 != null) {
            xml.element(new NeverJidListElement(list2));
        }
        return xml;
    }
}
