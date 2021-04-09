package org.jivesoftware.smack.filter;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;

public class IQReplyFilter implements StanzaFilter {
    private static final Logger LOGGER = Logger.getLogger(IQReplyFilter.class.getName());
    private final OrFilter fromFilter;
    private final StanzaFilter iqAndIdFilter;
    private final EntityFullJid local;
    private final String packetId;
    private final DomainBareJid server;
    private final Jid to;

    public IQReplyFilter(IQ iqPacket, XMPPConnection conn) {
        if (iqPacket.isRequestIQ()) {
            this.to = iqPacket.getTo();
            this.local = conn.getUser();
            if (this.local != null) {
                this.server = conn.getXMPPServiceDomain();
                this.packetId = iqPacket.getStanzaId();
                this.iqAndIdFilter = new AndFilter(new OrFilter(IQTypeFilter.ERROR, IQTypeFilter.RESULT), new StanzaIdFilter((Stanza) iqPacket));
                this.fromFilter = new OrFilter();
                this.fromFilter.addFilter(FromMatchesFilter.createFull(this.to));
                Jid jid = this.to;
                if (jid == null) {
                    this.fromFilter.addFilter(FromMatchesFilter.createBare(this.local));
                    this.fromFilter.addFilter(FromMatchesFilter.createFull(this.server));
                } else if (jid.equals((CharSequence) this.local.asBareJid())) {
                    this.fromFilter.addFilter(FromMatchesFilter.createFull(null));
                }
            } else {
                throw new IllegalArgumentException("Must have a local (user) JID set. Either you didn't configure one or you where not connected at least once");
            }
        } else {
            throw new IllegalArgumentException("IQ must be a request IQ, i.e. of type 'get' or 'set'.");
        }
    }

    public boolean accept(Stanza packet) {
        if (!this.iqAndIdFilter.accept(packet)) {
            return false;
        }
        if (this.fromFilter.accept(packet)) {
            return true;
        }
        LOGGER.log(Level.WARNING, String.format("Rejected potentially spoofed reply to IQ-packet. Filter settings: packetId=%s, to=%s, local=%s, server=%s. Received packet with from=%s", new Object[]{this.packetId, this.to, this.local, this.server, packet.getFrom()}), packet);
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(": iqAndIdFilter (");
        sb.append(this.iqAndIdFilter.toString());
        sb.append("), ");
        sb.append(": fromFilter (");
        sb.append(this.fromFilter.toString());
        sb.append(')');
        return sb.toString();
    }
}
