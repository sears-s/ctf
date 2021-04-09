package org.jivesoftware.smackx.disco;

import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jxmpp.jid.Jid;

public abstract class DiscoInfoLookupShortcutMechanism implements Comparable<DiscoInfoLookupShortcutMechanism> {
    private final String name;
    private final int priority;

    public abstract DiscoverInfo getDiscoverInfoByUser(ServiceDiscoveryManager serviceDiscoveryManager, Jid jid);

    protected DiscoInfoLookupShortcutMechanism(String name2, int priority2) {
        this.name = name2;
        this.priority = priority2;
    }

    public final String getName() {
        return this.name;
    }

    public final int getPriority() {
        return this.priority;
    }

    public final int compareTo(DiscoInfoLookupShortcutMechanism other) {
        return Integer.valueOf(getPriority()).compareTo(Integer.valueOf(other.getPriority()));
    }
}
