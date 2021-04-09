package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;

@Deprecated
public class PacketTypeFilter implements StanzaFilter {
    public static final PacketTypeFilter MESSAGE = new PacketTypeFilter(Message.class);
    public static final PacketTypeFilter PRESENCE = new PacketTypeFilter(Presence.class);
    private final Class<? extends Stanza> packetType;

    public PacketTypeFilter(Class<? extends Stanza> packetType2) {
        this.packetType = packetType2;
    }

    public boolean accept(Stanza packet) {
        return this.packetType.isInstance(packet);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": ");
        sb.append(this.packetType.getName());
        return sb.toString();
    }
}
