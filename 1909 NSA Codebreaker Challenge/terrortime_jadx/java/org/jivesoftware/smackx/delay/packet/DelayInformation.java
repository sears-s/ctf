package org.jivesoftware.smackx.delay.packet;

import java.util.Date;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jxmpp.util.XmppDateTime;

public class DelayInformation implements ExtensionElement {
    public static final String ELEMENT = "delay";
    public static final String NAMESPACE = "urn:xmpp:delay";
    private final String from;
    private final String reason;
    private final Date stamp;

    public DelayInformation(Date stamp2, String from2, String reason2) {
        this.stamp = stamp2;
        this.from = from2;
        this.reason = reason2;
    }

    public DelayInformation(Date stamp2) {
        this(stamp2, null, null);
    }

    public String getFrom() {
        return this.from;
    }

    public Date getStamp() {
        return this.stamp;
    }

    public String getReason() {
        return this.reason;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.attribute("stamp", XmppDateTime.formatXEP0082Date(this.stamp));
        xml.optAttribute(PrivacyItem.SUBSCRIPTION_FROM, this.from);
        xml.rightAngleBracket();
        xml.optAppend((CharSequence) this.reason);
        xml.closeElement((NamedElement) this);
        return xml;
    }

    @Deprecated
    public static DelayInformation getFrom(Stanza packet) {
        return from(packet);
    }

    public static DelayInformation from(Stanza packet) {
        return (DelayInformation) packet.getExtension(ELEMENT, NAMESPACE);
    }
}
