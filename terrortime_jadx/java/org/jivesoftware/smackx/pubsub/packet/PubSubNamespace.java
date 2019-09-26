package org.jivesoftware.smackx.pubsub.packet;

public enum PubSubNamespace {
    basic(null),
    error("errors"),
    event(r4),
    owner(r5);
    
    private final String fragment;
    private final String fullNamespace;

    private PubSubNamespace(String fragment2) {
        this.fragment = fragment2;
        if (fragment2 != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("http://jabber.org/protocol/pubsub#");
            sb.append(fragment2);
            this.fullNamespace = sb.toString();
            return;
        }
        this.fullNamespace = "http://jabber.org/protocol/pubsub";
    }

    public String getXmlns() {
        return this.fullNamespace;
    }

    public String getFragment() {
        return this.fragment;
    }

    public static PubSubNamespace valueOfFromXmlns(String ns) {
        int index = ns.lastIndexOf(35);
        String str = " is not a valid PubSub namespace";
        if (index != -1) {
            if (index <= ns.length()) {
                return valueOf(ns.substring(index + 1));
            }
            StringBuilder sb = new StringBuilder();
            sb.append(ns);
            sb.append(str);
            throw new IllegalArgumentException(sb.toString());
        } else if ("http://jabber.org/protocol/pubsub".equals(ns)) {
            return basic;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(ns);
            sb2.append(str);
            throw new IllegalArgumentException(sb2.toString());
        }
    }
}
