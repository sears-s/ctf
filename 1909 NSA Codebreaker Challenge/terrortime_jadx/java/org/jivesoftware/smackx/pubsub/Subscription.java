package org.jivesoftware.smackx.pubsub;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jxmpp.jid.Jid;

public class Subscription extends NodeExtension {
    protected boolean configRequired;
    protected String id;
    protected Jid jid;
    protected State state;

    public enum State {
        subscribed,
        unconfigured,
        pending,
        none
    }

    public Subscription(Jid subscriptionJid) {
        this(subscriptionJid, null, null, null);
    }

    public Subscription(Jid subscriptionJid, String nodeId) {
        this(subscriptionJid, nodeId, null, null);
    }

    public Subscription(Jid subscriptionJid, State state2) {
        this(subscriptionJid, null, null, state2);
    }

    public Subscription(Jid jid2, String nodeId, String subscriptionId, State state2) {
        super(PubSubElementType.SUBSCRIPTION, nodeId);
        this.configRequired = false;
        this.jid = jid2;
        this.id = subscriptionId;
        this.state = state2;
    }

    public Subscription(Jid jid2, String nodeId, String subscriptionId, State state2, boolean configRequired2) {
        super(PubSubElementType.SUBSCRIPTION, nodeId);
        this.configRequired = false;
        this.jid = jid2;
        this.id = subscriptionId;
        this.state = state2;
        this.configRequired = configRequired2;
    }

    public Jid getJid() {
        return this.jid;
    }

    public String getId() {
        return this.id;
    }

    public State getState() {
        return this.state;
    }

    public boolean isConfigRequired() {
        return this.configRequired;
    }

    public XmlStringBuilder toXML(String enclosingNamespace) {
        XmlStringBuilder builder = new XmlStringBuilder((ExtensionElement) this);
        builder.attribute("jid", (CharSequence) this.jid);
        builder.optAttribute(NodeElement.ELEMENT, getNode());
        builder.optAttribute("subid", this.id);
        builder.optAttribute("subscription", this.state.toString());
        builder.closeEmptyElement();
        return builder;
    }

    private static void appendAttribute(StringBuilder builder, String att, String value) {
        builder.append(' ');
        builder.append(att);
        builder.append("='");
        builder.append(value);
        builder.append('\'');
    }
}
