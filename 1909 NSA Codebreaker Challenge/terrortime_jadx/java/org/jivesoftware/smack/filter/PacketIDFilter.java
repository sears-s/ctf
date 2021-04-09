package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.StringUtils;

@Deprecated
public class PacketIDFilter implements StanzaFilter {
    private final String packetID;

    @Deprecated
    public PacketIDFilter(Stanza packet) {
        this(packet.getStanzaId());
    }

    @Deprecated
    public PacketIDFilter(String packetID2) {
        StringUtils.requireNotNullOrEmpty(packetID2, "Packet ID must not be null or empty.");
        this.packetID = packetID2;
    }

    public boolean accept(Stanza packet) {
        return this.packetID.equals(packet.getStanzaId());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": id=");
        sb.append(this.packetID);
        return sb.toString();
    }
}
